<template>
  <div class="course-detail-container">
    <div class="course-header" v-if="course">
      <div class="course-cover">
        <img :src="course.coverImage || '/api/placeholder/course'" alt="课程封面">
        <div class="cover-overlay">
          <button @click="startLearning" class="start-btn">开始学习</button>
        </div>
      </div>
      <div class="course-info">
        <h1>{{ course.title }}</h1>
        <p class="description">{{ course.description }}</p>
        <div class="course-meta">
          <span class="language">{{ getLanguageName(course.languageId) }}</span>
          <span class="level">{{ getLevelText(course.level) }}</span>
          <span class="chapters">{{ course.chapterCount }} 章节</span>
          <span class="hours">{{ course.totalHours }} 学时</span>
        </div>
        <div class="progress-section">
          <div class="progress-header">
            <span>学习进度</span>
            <span>{{ currentProgress }}%</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: currentProgress + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="chapters-section">
      <h2>课程章节</h2>
      <div class="chapters-list">
        <div
          v-for="chapter in chapters"
          :key="chapter.id"
          class="chapter-item"
        >
          <div class="chapter-header" @click="toggleChapter(chapter.id)">
            <div class="chapter-info">
              <span class="chapter-number">{{ chapter.order }}</span>
              <div>
                <h3>{{ chapter.title }}</h3>
                <p>{{ chapter.lessonCount }} 节课</p>
              </div>
            </div>
            <el-icon :class="{ rotated: expandedChapters.includes(chapter.id) }">ArrowDown</el-icon>
          </div>
          <div v-if="expandedChapters.includes(chapter.id)" class="lessons-list">
            <div
              v-for="lesson in getLessons(chapter.id)"
              :key="lesson.id"
              class="lesson-item"
              :class="{ completed: getLessonProgress(lesson.id) === 100 }"
              @click="goToLesson(lesson.id)"
            >
              <div class="lesson-info">
                <span class="lesson-icon">
                  <el-icon v-if="getLessonProgress(lesson.id) === 100">Check</el-icon>
                  <span v-else>{{ lesson.order }}</span>
                </span>
                <div>
                  <h4>{{ lesson.title }}</h4>
                  <p>{{ getLessonTypeText(lesson.type) }}</p>
                </div>
              </div>
              <span class="lesson-progress">{{ getLessonProgress(lesson.id) }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="recommendations-section">
      <h2>推荐课程</h2>
      <div class="recommend-grid">
        <div
          v-for="rec in recommendations"
          :key="rec.id"
          class="recommend-card"
          @click="$router.push(`/course/${rec.id}`)"
        >
          <img :src="rec.coverImage || '/api/placeholder/course'" alt="课程封面">
          <div class="rec-info">
            <h4>{{ rec.title }}</h4>
            <p>{{ getLevelText(rec.level) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { courseApi, chapterApi, lessonApi, learningApi } from '../api';
import { languageApi } from '../api';
const route = useRoute();
const router = useRouter();
const course = ref(null);
const chapters = ref([]);
const lessons = ref([]);
const recommendations = ref([]);
const languages = ref([]);
const expandedChapters = ref([]);
const courseProgress = ref(0);
const lessonProgressMap = ref({});
const currentProgress = computed(() => courseProgress.value);
const getLanguageName = (languageId) => {
 const lang = languages.value.find(l => l.id === languageId);
 return lang ? lang.name : '未知语言';
};
const getLevelText = (level) => {
 const levels = { 'BEGINNER': '入门', 'INTERMEDIATE': '中级', 'ADVANCED': '高级' };
 return levels[level] || level;
};
const getLessonTypeText = (type) => {
 const types = { 'VOCABULARY': '单词学习', 'GRAMMAR': '语法练习', 'SPEAKING': '口语跟读', 'LISTENING': '听力训练' };
 return types[type] || type;
};
const toggleChapter = (chapterId) => {
 const index = expandedChapters.value.indexOf(chapterId);
 if (index > -1) {
 expandedChapters.value.splice(index, 1);
 }
 else {
 expandedChapters.value.push(chapterId);
 }
};
const getLessons = (chapterId) => {
 return lessons.value.filter(l => l.chapterId === chapterId);
};
const getLessonProgress = (lessonId) => {
 return lessonProgressMap.value[lessonId] || 0;
};
const goToLesson = (lessonId) => {
 router.push(`/learning/${lessonId}`);
};
const startLearning = () => {
 if (chapters.value.length > 0) {
 const firstChapterLessons = lessons.value.filter(l => l.chapterId === chapters.value[0].id);
 if (firstChapterLessons.length > 0) {
 goToLesson(firstChapterLessons[0].id);
 }
 }
};
const loadCourse = async () => {
 try {
 course.value = await courseApi.getById(route.params.id);
 }
 catch (error) {
 console.error('加载课程失败', error);
 }
};
const loadChapters = async () => {
 try {
 chapters.value = await chapterApi.getByCourse(route.params.id);
 if (chapters.value.length > 0) {
 expandedChapters.value = [chapters.value[0].id];
 }
 }
 catch (error) {
 console.error('加载章节失败', error);
 }
};
const loadLessons = async () => {
 try {
 for (const chapter of chapters.value) {
 const chapterLessons = await lessonApi.getByChapter(chapter.id);
 lessons.value = [...lessons.value, ...chapterLessons];
 }
 }
 catch (error) {
 console.error('加载课时失败', error);
 }
};
const loadProgress = async () => {
 try {
 const progress = await learningApi.getCourseProgressById(route.params.id);
 courseProgress.value = progress.progress || 0;
 const lessonProgressList = await learningApi.getLessonProgress(route.params.id);
 lessonProgressList.forEach(p => {
 lessonProgressMap.value[p.lessonId] = p.progress;
 });
 }
 catch (error) {
 console.error('加载进度失败', error);
 }
};
const loadRecommendations = async () => {
 try {
 recommendations.value = await learningApi.getRecommendations();
 }
 catch (error) {
 console.error('加载推荐课程失败', error);
 }
};
const loadLanguages = async () => {
 try {
 languages.value = await languageApi.getAll();
 }
 catch (error) {
 console.error('加载语言失败', error);
 }
};
onMounted(async () => {
 await loadLanguages();
 await loadCourse();
 await loadChapters();
 await loadLessons();
 await loadProgress();
 await loadRecommendations();
});
</script>

<style scoped>
.course-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.course-header {
  margin-bottom: 40px;
}

.course-cover {
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  margin-bottom: 20px;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
  padding: 40px 20px 20px;
}

.start-btn {
  background: #409EFF;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 20px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.start-btn:hover {
  background: #66B1FF;
}

.course-info h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 12px;
}

.description {
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
}

.course-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.course-meta span {
  padding: 6px 14px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 13px;
}

.progress-section {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.progress-header span:last-child {
  color: #409EFF;
  font-weight: bold;
}

.progress-bar {
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 4px;
  transition: width 0.5s;
}

.chapters-section, .recommendations-section {
  margin-bottom: 40px;
}

.chapters-section h2, .recommendations-section h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
}

.chapter-item {
  background: white;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.chapter-header:hover {
  background: #f8f9fa;
}

.chapter-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.chapter-number {
  width: 36px;
  height: 36px;
  background: #409EFF;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.chapter-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.chapter-info p {
  font-size: 13px;
  color: #999;
}

.rotated {
  transform: rotate(180deg);
}

.lessons-list {
  border-top: 1px solid #eee;
}

.lesson-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px 14px 72px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
}

.lesson-item:hover {
  background: #f8f9fa;
}

.lesson-item:last-child {
  border-bottom: none;
}

.lesson-item.completed {
  background: #f0f9ff;
}

.lesson-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lesson-icon {
  width: 28px;
  height: 28px;
  background: #eee;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #666;
}

.lesson-item.completed .lesson-icon {
  background: #67C23A;
  color: white;
}

.lesson-info h4 {
  font-size: 15px;
  color: #333;
  margin-bottom: 3px;
}

.lesson-info p {
  font-size: 12px;
  color: #999;
}

.lesson-progress {
  font-size: 13px;
  color: #666;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.recommend-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: transform 0.2s;
}

.recommend-card:hover {
  transform: translateY(-2px);
}

.recommend-card img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.rec-info {
  padding: 12px;
}

.rec-info h4 {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.rec-info p {
  font-size: 12px;
  color: #999;
}
</style>