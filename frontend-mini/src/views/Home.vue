
<template>
  <div class="home-page">
    <div class="header">
      <div class="user-info">
        <el-avatar :size="48" :src="user?.avatar" class="avatar">
          {{ user?.nickname?.charAt(0) }}
        </el-avatar>
        <div class="greeting">
          <h2>你好，{{ user?.nickname }}</h2>
          <p>今天也要加油学习哦！</p>
        </div>
      </div>
      <div class="points-badge">
        <span class="points-icon">⭐</span>
        <span class="points-count">{{ points }}</span>
      </div>
    </div>
    
    <div class="sign-section" @click="handleSign">
      <div class="sign-content">
        <span class="sign-icon">{{ signStatus.isSigned ? '✅' : '📅' }}</span>
        <div class="sign-text">
          <p>{{ signStatus.isSigned ? '今日已签到' : '每日签到' }}</p>
          <p class="sign-days">连续 {{ signStatus.continuousDays }} 天</p>
        </div>
      </div>
      <el-button v-if="!signStatus.isSigned" type="primary" class="sign-btn">
        签到
      </el-button>
    </div>
    
    <div class="section">
      <div class="section-header">
        <h3>推荐课程</h3>
        <a href="/courses" class="see-all">查看全部</a>
      </div>
      <div class="course-list">
        <div 
          v-for="course in recommendations" 
          :key="course.id" 
          class="course-card"
          @click="goToCourse(course.id)"
        >
          <div class="course-cover">
            <span class="cover-icon">📚</span>
          </div>
          <div class="course-info">
            <h4>{{ course.title }}</h4>
            <div class="course-meta">
              <span class="level">{{ getLevelText(course.level) }}</span>
              <span class="price">{{ course.isFree ? '免费' : `¥${course.price}` }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="section">
      <div class="section-header">
        <h3>继续学习</h3>
      </div>
      <div v-if="currentCourse" class="continue-card" @click="continueLearning">
        <div class="continue-cover">
          <span class="cover-icon">🎯</span>
        </div>
        <div class="continue-info">
          <h4>{{ currentCourse.title }}</h4>
          <p>进度: {{ currentCourse.progressPercent }}%</p>
        </div>
        <div class="continue-btn">
          <span>继续</span>
          <span class="arrow">→</span>
        </div>
      </div>
      <div v-else class="empty-state">
        <span class="empty-icon">📖</span>
        <p>还没有开始学习，快去选择课程吧！</p>
        <el-button type="primary" @click="$router.push('/courses')">去学习</el-button>
      </div>
    </div>
    
    <div class="section">
      <div class="section-header">
        <h3>快速入口</h3>
      </div>
      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/words')">
          <span class="action-icon">📝</span>
          <span class="action-text">单词记忆</span>
        </div>
        <div class="action-item" @click="$router.push('/review')">
          <span class="action-icon">🔄</span>
          <span class="action-text">复习单词</span>
        </div>
        <div class="action-item" @click="$router.push('/statistics')">
          <span class="action-icon">📊</span>
          <span class="action-text">学习统计</span>
        </div>
        <div class="action-item" @click="$router.push('/achievements')">
          <span class="action-icon">🏆</span>
          <span class="action-text">成就中心</span>
        </div>
      </div>
    </div>
    
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { learningApi, signApi, pointsApi, courseApi } from '../api'
import BottomNav from '../components/BottomNav.vue'

const store = useStore()
const user = ref(null)
const points = ref(0)
const signStatus = ref({
  isSigned: false,
  continuousDays: 0,
  totalDays: 0
})
const recommendations = ref([])
const currentCourse = ref(null)

const getLevelText = (level) => {
  const levels = ['', '入门', '初级', '中级', '高级']
  return levels[level] || '未知'
}

const handleSign = async () => {
  if (signStatus.value.isSigned) return
  
  try {
    const res = await signApi.sign()
    if (res.code === 200 && res.data.success) {
      signStatus.value.isSigned = true
      signStatus.value.continuousDays = res.data.continuousDays
      ElMessage.success(`签到成功！获得 ${res.data.points} 积分`)
      loadPoints()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

const goToCourse = (id) => {
  store.dispatch('setLanguage', '')
  window.location.href = `/course/${id}`
}

const continueLearning = () => {
  if (currentCourse.value) {
    goToCourse(currentCourse.value.courseId)
  }
}

const loadData = async () => {
  try {
    const [signRes, pointsRes, recRes, progressRes] = await Promise.all([
      signApi.getStatus(),
      pointsApi.getTotal(),
      learningApi.getRecommendations(),
      learningApi.getCourseProgress()
    ])
    
    if (signRes.code === 200) {
      signStatus.value = signRes.data
    }
    
    if (pointsRes.code === 200) {
      points.value = pointsRes.data
    }
    
    if (recRes.code === 200) {
      recommendations.value = recRes.data.slice(0, 4)
    }
    
    if (progressRes.code === 200 && progressRes.data.length > 0) {
      const latest = progressRes.data[0]
      const courseRes = await courseApi.getById(latest.courseId)
      if (courseRes.code === 200) {
        currentCourse.value = { ...courseRes.data, ...latest }
      }
    }
  } catch (error) {
    console.error('加载数据失败', error)
  }
}

const loadPoints = async () => {
  try {
    const res = await pointsApi.getTotal()
    if (res.code === 200) {
      points.value = res.data
    }
  } catch (error) {
    console.error('加载积分失败', error)
  }
}

onMounted(() => {
  user.value = store.state.user
  loadData()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding-bottom: 80px;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  background: rgba(255, 255, 255, 0.3);
}

.greeting {
  color: #fff;
}

.greeting h2 {
  font-size: 20px;
  margin-bottom: 4px;
}

.greeting p {
  font-size: 14px;
  opacity: 0.9;
}

.points-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.points-icon {
  font-size: 16px;
}

.points-count {
  color: #fff;
  font-weight: bold;
  font-size: 16px;
}

.sign-section {
  background: #fff;
  margin: -20px 15px 15px;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.sign-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sign-icon {
  font-size: 32px;
}

.sign-text p {
  margin: 0;
}

.sign-text p:first-child {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.sign-days {
  font-size: 14px;
  color: #999;
}

.sign-btn {
  border-radius: 20px;
}

.section {
  padding: 0 15px;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.see-all {
  font-size: 14px;
  color: #667eea;
  text-decoration: none;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.course-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.course-cover {
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-icon {
  font-size: 36px;
}

.course-info {
  padding: 12px;
}

.course-info h4 {
  font-size: 14px;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.level {
  color: #667eea;
  background: #f0f0ff;
  padding: 2px 8px;
  border-radius: 4px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.continue-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: #fff;
}

.continue-cover {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.continue-info h4 {
  margin: 0 0 5px;
  font-size: 16px;
}

.continue-info p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.continue-btn {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
}

.empty-state {
  background: #fff;
  border-radius: 16px;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 15px;
}

.empty-state p {
  color: #999;
  margin-bottom: 20px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.action-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px 10px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.action-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 8px;
}

.action-text {
  font-size: 13px;
  color: #333;
}
</style>
