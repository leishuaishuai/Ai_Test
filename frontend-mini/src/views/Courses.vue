<template>
  <div class="courses-container">
    <div class="header">
      <h1>课程中心</h1>
      <p>选择你想学习的语言和课程</p>
    </div>

    <div class="language-tabs">
      <button
        v-for="lang in languages"
        :key="lang.code"
        :class="['tab', { active: selectedLanguage === lang.code }]"
        @click="selectLanguage(lang.code)"
      >
        {{ lang.name }}
      </button>
    </div>

    <div class="course-grid">
      <div
        v-for="course in courses"
        :key="course.id"
        class="course-card"
        @click="$router.push(`/course/${course.id}`)"
      >
        <div class="course-image">
          <img :src="course.coverImage || '/api/placeholder/course'" alt="课程封面">
        </div>
        <div class="course-info">
          <h3>{{ course.title }}</h3>
          <p class="level">难度：{{ getLevelText(course.level) }}</p>
          <p class="description">{{ course.description }}</p>
          <div class="course-stats">
            <span>章节：{{ course.chapterCount }}</span>
            <span>学时：{{ course.totalHours }}h</span>
          </div>
          <div class="progress-bar" v-if="getProgress(course.id)">
            <div class="progress-fill" :style="{ width: getProgress(course.id) + '%' }"></div>
          </div>
          <p class="progress-text" v-if="getProgress(course.id)">已学习 {{ getProgress(course.id) }}%</p>
        </div>
      </div>
    </div>

    <div v-if="courses.length === 0" class="empty-state">
      <el-icon size="48">Document</el-icon>
      <p>暂无课程</p>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { languageApi, courseApi, learningApi } from '../api';
const languages = ref([]);
const courses = ref([]);
const selectedLanguage = ref('en');
const courseProgress = ref({});
const getLevelText = (level) => {
 const levels = { 'BEGINNER': '入门', 'INTERMEDIATE': '中级', 'ADVANCED': '高级' };
 return levels[level] || level;
};
const selectLanguage = (code) => {
 selectedLanguage.value = code;
 loadCourses();
};
const getProgress = (courseId) => {
 return courseProgress.value[courseId] || 0;
};
const loadLanguages = async () => {
 try {
 languages.value = await languageApi.getAll();
 }
 catch (error) {
 console.error('加载语言失败', error);
 }
};
const loadCourses = async () => {
 try {
 courses.value = await courseApi.getAll({ languageCode: selectedLanguage.value });
 }
 catch (error) {
 console.error('加载课程失败', error);
 }
};
const loadProgress = async () => {
 try {
 const progressList = await learningApi.getCourseProgress();
 progressList.forEach(p => {
 courseProgress.value[p.courseId] = p.progress;
 });
 }
 catch (error) {
 console.error('加载进度失败', error);
 }
};
onMounted(() => {
 loadLanguages();
 loadCourses();
 loadProgress();
});
</script>

<style scoped>
.courses-container {
  padding: 20px;
  max-width: 1200px;
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

.language-tabs {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.tab {
  padding: 10px 24px;
  border-radius: 20px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.tab.active {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

.tab:hover {
  border-color: #409EFF;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.course-image {
  height: 180px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 20px;
}

.course-info h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.level {
  display: inline-block;
  padding: 4px 12px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
}

.description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.progress-bar {
  height: 6px;
  background: #eee;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 3px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 12px;
  color: #409EFF;
  margin-top: 6px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-state p {
  margin-top: 12px;
}
</style>