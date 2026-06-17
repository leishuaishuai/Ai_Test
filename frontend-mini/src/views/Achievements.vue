<template>
  <div class="achievements-container">
    <div class="header">
      <h1>成就中心</h1>
      <p>解锁成就，见证你的学习成长</p>
    </div>

    <div class="stats-bar">
      <span>已获得成就：</span>
      <span class="achievement-count">{{ unlockedCount }} / {{ achievements.length }}</span>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <div class="achievements-grid">
      <div
        v-for="achievement in achievements"
        :key="achievement.id"
        :class="['achievement-card', { unlocked: isUnlocked(achievement.id) }]"
      >
        <div class="achievement-icon">
          <el-icon :size="36">{{ isUnlocked(achievement.id) ? 'Medal' : 'Lock' }}</el-icon>
        </div>
        <div class="achievement-info">
          <h3>{{ achievement.title }}</h3>
          <p class="description">{{ achievement.description }}</p>
          <div class="achievement-meta">
            <span class="points">+{{ achievement.points }} 积分</span>
            <span class="category">{{ getCategoryText(achievement.category) }}</span>
          </div>
        </div>
        <div v-if="isUnlocked(achievement.id)" class="unlocked-badge">
          <el-icon>Check</el-icon>
        </div>
      </div>
    </div>

    <div class="leaderboard-section">
      <h2>排行榜</h2>
      <div class="leaderboard-list">
        <div
          v-for="(user, index) in leaderboard"
          :key="user.id"
          :class="['leaderboard-item', { 'top-three': index < 3 }]"
        >
          <span class="rank">{{ index + 1 }}</span>
          <div class="user-avatar">
            <el-icon size="32">User</el-icon>
          </div>
          <div class="user-info">
            <h4>{{ user.username }}</h4>
            <p>总积分：{{ user.totalPoints }}</p>
          </div>
          <div :class="['medal', `medal-${index + 1}`]" v-if="index < 3">
            <el-icon>{{ getMedalIcon(index) }}</el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, computed, onMounted } from 'vue';
import { achievementApi } from '../api';
const achievements = ref([]);
const userAchievements = ref([]);
const leaderboard = ref([
 { id: 1, username: '学霸小明', totalPoints: 2580 },
 { id: 2, username: '学习达人', totalPoints: 2340 },
 { id: 3, username: '语言爱好者', totalPoints: 2100 },
 { id: 4, username: '持之以恒', totalPoints: 1890 },
 { id: 5, username: '初学者小王', totalPoints: 1560 }
]);
const unlockedCount = computed(() => userAchievements.value.length);
const progressPercent = computed(() => {
 if (achievements.value.length === 0)
 return 0;
 return (unlockedCount.value / achievements.value.length) * 100;
});
const isUnlocked = (achievementId) => {
 return userAchievements.value.some(a => a.achievementId === achievementId);
};
const getCategoryText = (category) => {
 const categories = {
 'STUDY': '学习',
 'COURSE': '课程',
 'WORD': '单词',
 'COMMUNITY': '社区',
 'SIGN': '签到'
 };
 return categories[category] || category;
};
const getMedalIcon = (index) => {
 const icons = ['Trophy', 'Medal', 'Medal'];
 return icons[index] || 'Medal';
};
const loadAchievements = async () => {
 try {
 achievements.value = await achievementApi.getAll();
 }
 catch (error) {
 console.error('加载成就列表失败', error);
 }
};
const loadUserAchievements = async () => {
 try {
 userAchievements.value = await achievementApi.getUserAchievements();
 }
 catch (error) {
 console.error('加载用户成就失败', error);
 }
};
onMounted(async () => {
 await loadAchievements();
 await loadUserAchievements();
});
</script>

<style scoped>
.achievements-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.header p {
  color: #666;
}

.stats-bar {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stats-bar span:first-child {
  font-size: 16px;
  color: #666;
}

.achievement-count {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.stats-bar .progress-bar {
  flex: 1;
  height: 8px;
  background: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.stats-bar .progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 4px;
}

.achievements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.achievement-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  opacity: 0.6;
  filter: grayscale(100%);
  transition: all 0.3s;
}

.achievement-card.unlocked {
  opacity: 1;
  filter: grayscale(0%);
  border: 2px solid #E6A23C;
}

.achievement-icon {
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.achievement-card.unlocked .achievement-icon {
  background: #fdf6ec;
  color: #E6A23C;
}

.achievement-info {
  flex: 1;
}

.achievement-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.description {
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
}

.achievement-meta {
  display: flex;
  gap: 12px;
}

.points {
  font-size: 12px;
  color: #E6A23C;
  background: #fdf6ec;
  padding: 4px 10px;
  border-radius: 4px;
}

.category {
  font-size: 12px;
  color: #999;
}

.unlocked-badge {
  width: 32px;
  height: 32px;
  background: #67C23A;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.leaderboard-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.leaderboard-section h2 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.leaderboard-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.leaderboard-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.leaderboard-item.top-three {
  background: linear-gradient(90deg, #fffbe6 0%, #fff 100%);
}

.rank {
  width: 32px;
  height: 32px;
  background: #eee;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #666;
}

.leaderboard-item:nth-child(1) .rank {
  background: #F5A623;
  color: white;
}

.leaderboard-item:nth-child(2) .rank {
  background: #909399;
  color: white;
}

.leaderboard-item:nth-child(3) .rank {
  background: #CD7F32;
  color: white;
}

.user-avatar {
  width: 48px;
  height: 48px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-info h4 {
  font-size: 15px;
  color: #333;
  margin-bottom: 4px;
}

.user-info p {
  font-size: 13px;
  color: #999;
}

.medal {
  margin-left: auto;
  font-size: 24px;
}

.medal-1 {
  color: #F5A623;
}

.medal-2 {
  color: #909399;
}

.medal-3 {
  color: #CD7F32;
}
</style>