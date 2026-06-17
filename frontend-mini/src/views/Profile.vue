<template>
  <div class="profile-container">
    <div class="profile-header" v-if="user">
      <div class="avatar-section">
        <div class="avatar">
          <el-icon size="48">User</el-icon>
        </div>
        <div class="user-info">
          <h2>{{ user.username }}</h2>
          <p>{{ user.email }}</p>
        </div>
      </div>
      <div class="stats-row">
        <div class="stat-box">
          <span class="stat-num">{{ totalPoints }}</span>
          <span class="stat-text">总积分</span>
        </div>
        <div class="stat-box">
          <span class="stat-num">{{ completedCourses }}</span>
          <span class="stat-text">已完成课程</span>
        </div>
        <div class="stat-box">
          <span class="stat-num">{{ completedAchievements }}</span>
          <span class="stat-text">已获成就</span>
        </div>
        <div class="stat-box">
          <span class="stat-num">{{ signDays }}</span>
          <span class="stat-text">连续签到</span>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <div class="menu-title">学习中心</div>
      <div class="menu-items">
        <div class="menu-item" @click="$router.push('/statistics')">
          <el-icon>TrendCharts</el-icon>
          <span>学习统计</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/words')">
          <el-icon>BookOpen</el-icon>
          <span>单词本</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/review')">
          <el-icon>Refresh</el-icon>
          <span>单词复习</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/courses')">
          <el-icon>Document</el-icon>
          <span>我的课程</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <div class="menu-title">成就激励</div>
      <div class="menu-items">
        <div class="menu-item" @click="$router.push('/achievements')">
          <el-icon>Medal</el-icon>
          <span>成就中心</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item">
          <el-icon>Coins</el-icon>
          <span>积分记录</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item">
          <el-icon>Calendar</el-icon>
          <span>签到记录</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <div class="menu-title">社区交流</div>
      <div class="menu-items">
        <div class="menu-item" @click="$router.push('/community')">
          <el-icon>Message</el-icon>
          <span>学习社区</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/create-post')">
          <el-icon>Edit</el-icon>
          <span>发布帖子</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <div class="menu-title">设置</div>
      <div class="menu-items">
        <div class="menu-item">
          <el-icon>Settings</el-icon>
          <span>账号设置</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item">
          <el-icon>Help</el-icon>
          <span>帮助与反馈</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
        <div class="menu-item" @click="logout">
          <el-icon>SwitchButton</el-icon>
          <span>退出登录</span>
          <el-icon class="arrow">ArrowRight</el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { authApi, pointsApi, learningApi, achievementApi, signApi } from '../api';
const store = useStore();
const router = useRouter();
const user = ref(null);
const totalPoints = ref(0);
const completedCourses = ref(0);
const completedAchievements = ref(0);
const signDays = ref(0);
const loadUser = async () => {
 try {
 user.value = await authApi.getCurrentUser();
 store.commit('SET_USER', user.value);
 }
 catch (error) {
 console.error('加载用户信息失败', error);
 }
};
const loadPoints = async () => {
 try {
 totalPoints.value = await pointsApi.getTotal();
 }
 catch (error) {
 console.error('加载积分失败', error);
 }
};
const loadStats = async () => {
 try {
 const stats = await learningApi.getStatistics();
 completedCourses.value = stats.completedCourses || 0;
 }
 catch (error) {
 console.error('加载统计信息失败', error);
 }
};
const loadAchievements = async () => {
 try {
 const achievements = await achievementApi.getUserAchievements();
 completedAchievements.value = achievements.length;
 }
 catch (error) {
 console.error('加载成就失败', error);
 }
};
const loadSignStatus = async () => {
 try {
 const status = await signApi.getStatus();
 signDays.value = status.continuousDays || 0;
 }
 catch (error) {
 console.error('加载签到状态失败', error);
 }
};
const logout = async () => {
 try {
 await authApi.logout();
 store.dispatch('logout');
 router.push('/login');
 }
 catch (error) {
 console.error('退出登录失败', error);
 }
};
onMounted(async () => {
 await loadUser();
 await loadPoints();
 await loadStats();
 await loadAchievements();
 await loadSignStatus();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.profile-header {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 20px;
  color: white;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.avatar {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info h2 {
  font-size: 24px;
  margin-bottom: 4px;
}

.user-info p {
  opacity: 0.9;
  font-size: 14px;
}

.stats-row {
  display: flex;
  justify-content: space-around;
}

.stat-box {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: bold;
}

.stat-text {
  font-size: 12px;
  opacity: 0.9;
}

.menu-section {
  background: white;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.menu-title {
  padding: 16px 20px;
  font-size: 14px;
  color: #999;
  border-bottom: 1px solid #f5f5f5;
}

.menu-items {
  padding: 8px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item span {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.arrow {
  color: #ccc;
}
</style>