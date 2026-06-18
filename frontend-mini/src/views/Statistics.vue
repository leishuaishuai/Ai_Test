<template>
  <div class="statistics-container">
    <div class="header">
      <h1>学习统计</h1>
      <p>查看你的学习数据和进步</p>
    </div>

    <div class="summary-cards" v-if="statistics">
      <div class="summary-card">
        <div class="card-icon">
          <el-icon>Clock</el-icon>
        </div>
        <div class="card-content">
          <span class="card-value">{{ statistics.totalHours }}h</span>
          <span class="card-label">累计学习时长</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon">
          <el-icon>BookOpen</el-icon>
        </div>
        <div class="card-content">
          <span class="card-value">{{ statistics.completedLessons }}</span>
          <span class="card-label">已完成课时</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon">
          <el-icon>Word</el-icon>
        </div>
        <div class="card-content">
          <span class="card-value">{{ statistics.learnedWords }}</span>
          <span class="card-label">已学单词</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon">
          <el-icon>Calendar</el-icon>
        </div>
        <div class="card-content">
          <span class="card-value">{{ statistics.studyDays }}</span>
          <span class="card-label">学习天数</span>
        </div>
      </div>
    </div>

    <div class="chart-section">
      <h2>学习趋势</h2>
      <div class="chart-container">
        <div class="chart-bars">
          <div
            v-for="(day, index) in weekData"
            :key="index"
            class="bar-item"
          >
            <div class="bar-wrapper">
              <div
                class="bar"
                :style="{ height: (day.hours / maxHours * 100) + '%' }"
              ></div>
            </div>
            <span class="bar-label">{{ day.label }}</span>
            <span class="bar-value">{{ day.hours }}h</span>
          </div>
        </div>
      </div>
    </div>

    <div class="course-progress-section">
      <h2>课程进度</h2>
      <div class="progress-list">
        <div
          v-for="course in courseProgress"
          :key="course.courseId"
          class="progress-item"
        >
          <div class="course-info">
            <h3>{{ course.courseTitle }}</h3>
            <span class="course-language">{{ course.languageName }}</span>
          </div>
          <div class="progress-detail">
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: course.progress + '%' }"
              ></div>
            </div>
            <span class="progress-text">{{ course.progress }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="word-stats-section">
      <h2>单词掌握情况</h2>
      <div class="word-stats-grid">
        <div class="word-stat-item">
          <div class="stat-circle">
            <span class="stat-percent">{{ masteredPercent }}%</span>
          </div>
          <span class="stat-label">已掌握</span>
          <span class="stat-count">{{ wordStats.mastered }} 个</span>
        </div>
        <div class="word-stat-item">
          <div class="stat-circle learning">
            <span class="stat-percent">{{ learningPercent }}%</span>
          </div>
          <span class="stat-label">学习中</span>
          <span class="stat-count">{{ wordStats.learning }} 个</span>
        </div>
        <div class="word-stat-item">
          <div class="stat-circle new">
            <span class="stat-percent">{{ newPercent }}%</span>
          </div>
          <span class="stat-label">待学习</span>
          <span class="stat-count">{{ wordStats.new }} 个</span>
        </div>
      </div>
    </div>

    <div class="activity-section">
      <h2>最近学习</h2>
      <div class="activity-list">
        <div
          v-for="activity in recentActivity"
          :key="activity.id"
          class="activity-item"
        >
          <div class="activity-icon">
            <el-icon>{{ getActivityIcon(activity.type) }}</el-icon>
          </div>
          <div class="activity-content">
            <h4>{{ activity.title }}</h4>
            <p>{{ activity.description }}</p>
            <span class="activity-time">{{ activity.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, computed, onMounted } from 'vue';
import { learningApi } from '../api';
const statistics = ref(null);
const courseProgress = ref([]);
const weekData = ref([]);
const wordStats = ref({
 mastered: 0,
 learning: 0,
 new: 0
});
const recentActivity = ref([]);
const maxHours = computed(() => {
 return Math.max(...weekData.value.map(d => d.hours), 1);
});
const totalWords = computed(() => {
 return wordStats.value.mastered + wordStats.value.learning + wordStats.value.new;
});
const masteredPercent = computed(() => {
 if (totalWords.value === 0)
  return 0;
 return Math.round((wordStats.value.mastered / totalWords.value) * 100);
});
const learningPercent = computed(() => {
 if (totalWords.value === 0)
  return 0;
 return Math.round((wordStats.value.learning / totalWords.value) * 100);
});
const newPercent = computed(() => {
 if (totalWords.value === 0)
  return 0;
 return Math.round((wordStats.value.new / totalWords.value) * 100);
});
const getActivityIcon = (type) => {
 const icons = {
 'lesson': 'VideoPlay',
 'word': 'BookOpen',
 'review': 'Refresh',
 'course': 'Document'
 };
 return icons[type] || 'Circle';
};
const loadStatistics = async () => {
 try {
  const response = await learningApi.getStatistics();
  statistics.value = response.data || response;
 }
 catch (error) {
  console.error('加载统计信息失败', error);
 }
};
const loadCourseProgress = async () => {
 try {
  const response = await learningApi.getCourseProgress();
  const progressList = response.data || response;
  courseProgress.value = progressList.map(p => ({
    courseId: p.courseId,
    courseTitle: p.course?.title || '未知课程',
    languageName: p.course?.language?.name || '未知语言',
    progress: p.progressPercent || 0
  }));
 }
 catch (error) {
  console.error('加载课程进度失败', error);
 }
};
const loadWeeklyStatistics = async () => {
 try {
  const response = await learningApi.getWeeklyStatistics();
  const weeklyStats = response.data || response;
  if (weeklyStats.dailyData) {
    weekData.value = weeklyStats.dailyData;
  }
 }
 catch (error) {
  console.error('加载每周统计失败', error);
 }
};
const loadWordStats = async () => {
 try {
  const response = await learningApi.getWordStats();
  wordStats.value = response.data || response;
 }
 catch (error) {
  console.error('加载单词统计失败', error);
 }
};
const loadReviewRecommendations = async () => {
 try {
  const response = await learningApi.getReviewRecommendations();
  const recommendations = response.data || response;
  recentActivity.value = recommendations.slice(0, 4).map((r, index) => ({
    id: index,
    type: 'word',
    title: '复习单词',
    description: `${r.word} - ${r.meaning}`,
    time: r.nextReviewTime ? '即将复习' : '待复习'
  }));
 }
 catch (error) {
  console.error('加载复习推荐失败', error);
  recentActivity.value = [];
 }
};
onMounted(async () => {
 await loadStatistics();
 await loadCourseProgress();
 await loadWeeklyStatistics();
 await loadWordStats();
 await loadReviewRecommendations();
});
</script>

<style scoped>
.statistics-container {
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

.summary-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 30px;
}

.summary-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-icon {
  width: 48px;
  height: 48px;
  background: #f0f7ff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409EFF;
}

.card-content {
  display: flex;
  flex-direction: column;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.card-label {
  font-size: 13px;
  color: #999;
}

.chart-section, .course-progress-section, .word-stats-section, .activity-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.chart-section h2, .course-progress-section h2, .word-stats-section h2, .activity-section h2 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.chart-container {
  height: 200px;
}

.chart-bars {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 12%;
}

.bar-wrapper {
  width: 100%;
  height: 150px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: flex-end;
}

.bar {
  width: 100%;
  background: linear-gradient(to top, #409EFF, #66B1FF);
  border-radius: 4px 4px 0 0;
  transition: height 0.5s;
}

.bar-label {
  font-size: 12px;
  color: #666;
}

.bar-value {
  font-size: 12px;
  color: #409EFF;
  font-weight: bold;
}

.progress-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.progress-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-info h3 {
  font-size: 15px;
  color: #333;
  margin-bottom: 4px;
}

.course-language {
  font-size: 12px;
  color: #999;
}

.progress-detail {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 200px;
}

.progress-detail .progress-bar {
  flex: 1;
  height: 8px;
  background: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.progress-detail .progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 4px;
}

.progress-text {
  font-size: 14px;
  color: #409EFF;
  font-weight: bold;
  min-width: 40px;
  text-align: right;
}

.word-stats-grid {
  display: flex;
  justify-content: space-around;
}

.word-stat-item {
  text-align: center;
}

.stat-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #67C23A;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  color: white;
}

.stat-circle.learning {
  background: #E6A23C;
}

.stat-circle.new {
  background: #909399;
}

.stat-percent {
  font-size: 18px;
  font-weight: bold;
}

.word-stat-item .stat-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.word-stat-item .stat-count {
  font-size: 12px;
  color: #999;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.activity-icon {
  width: 40px;
  height: 40px;
  background: #409EFF;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.activity-content h4 {
  font-size: 15px;
  color: #333;
  margin-bottom: 4px;
}

.activity-content p {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}
</style>