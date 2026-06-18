<template>
  <div class="words-container">
    <div class="header">
      <h1>单词学习</h1>
      <p>选择语言开始学习单词</p>
    </div>

    <div class="language-selector">
      <select v-model="selectedLanguage" @change="loadWords">
        <option value="">选择语言</option>
        <option v-for="lang in languages" :key="lang.id" :value="lang.id">
          {{ lang.name }}
        </option>
      </select>
      <span class="word-count">共 {{ words.length }} 个单词</span>
    </div>

    <div class="words-grid" v-if="words.length > 0">
      <div
        v-for="word in words"
        :key="word.id"
        class="word-card"
        :class="{ learned: getWordProgress(word.id) === 100 }"
        @click="showWordDetail(word)"
      >
        <div class="word-header">
          <h3>{{ word.word }}</h3>
          <span class="pronunciation">{{ word.phonetic }}</span>
        </div>
        <p class="meaning">{{ word.meaning }}</p>
        <p class="example" v-if="word.example">{{ word.example }}</p>
        <div class="word-progress">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: getWordProgress(word.id) + '%' }"></div>
          </div>
          <span>{{ getWordProgress(word.id) }}%</span>
        </div>
      </div>
    </div>

    <div v-if="words.length === 0 && selectedLanguage" class="empty-state">
      <el-icon size="48">Document</el-icon>
      <p>暂无单词数据</p>
    </div>

    <div v-if="selectedLanguage" class="study-action">
      <button @click="startStudy" class="study-btn">
        开始学习
        <el-icon>Play</el-icon>
      </button>
    </div>

    <div v-if="currentWord" class="word-detail-modal" @click.self="closeWordDetail">
      <div class="modal-content">
        <button class="close-btn" @click="closeWordDetail">
          <el-icon>Close</el-icon>
        </button>
        <div class="word-display">
          <h2>{{ currentWord.word }}</h2>
          <p class="pronunciation">{{ currentWord.phonetic }}</p>
          <button class="audio-btn" @click="playAudio">
            <el-icon>Volume</el-icon>
          </button>
        </div>
        <div class="word-info">
          <div class="section">
            <h4>中文释义</h4>
            <p>{{ currentWord.meaning }}</p>
          </div>
          <div class="section" v-if="currentWord.example">
            <h4>例句</h4>
            <p>{{ currentWord.example }}</p>
          </div>
          <div class="section" v-if="currentWord.exampleTranslation">
            <h4>例句翻译</h4>
            <p>{{ currentWord.exampleTranslation }}</p>
          </div>
          <div class="section" v-if="currentWord.context">
            <h4>语境说明</h4>
            <p>{{ currentWord.context }}</p>
          </div>
        </div>
        <div class="action-buttons">
          <button @click="markAsLearned" class="learn-btn">
            <el-icon>Check</el-icon>
            标记已学
          </button>
          <button @click="markAsNeedReview" class="review-btn">
            <el-icon>Refresh</el-icon>
            需要复习
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { languageApi, wordApi, learningApi } from '../api';
const router = useRouter();
const languages = ref([]);
const words = ref([]);
const selectedLanguage = ref('');
const wordProgressMap = ref({});
const currentWord = ref(null);
const getWordProgress = (wordId) => {
  return wordProgressMap.value[wordId] || 0;
};
const loadLanguages = async () => {
 try {
  const response = await languageApi.getAll();
  languages.value = response.data || response;
 }
 catch (error) {
  console.error('加载语言失败', error);
 }
};
const loadWords = async () => {
 if (!selectedLanguage.value)
  return;
 try {
  const response = await wordApi.getByLanguage(selectedLanguage.value);
  words.value = response.data || response;
  // 加载该语言下所有单词的进度
  await loadProgressForLanguage(selectedLanguage.value);
 }
 catch (error) {
  console.error('加载单词失败', error);
 }
};
const loadProgressForLanguage = async (languageId) => {
 try {
  const response = await learningApi.getWordProgressList(languageId);
  const progressList = response.data || response;
  progressList.forEach(p => {
    wordProgressMap.value[p.wordId] = p.progress || 0;
  });
 }
 catch (error) {
  console.error('加载进度失败', error);
 }
};
const showWordDetail = (word) => {
 currentWord.value = word;
};
const closeWordDetail = () => {
 currentWord.value = null;
};
const playAudio = () => {
};
const markAsLearned = async () => {
 if (!currentWord.value)
  return;
 try {
  await learningApi.updateWordProgress(currentWord.value.id, { progress: 100 });
  wordProgressMap.value[currentWord.value.id] = 100;
  closeWordDetail();
 }
 catch (error) {
  console.error('更新进度失败', error);
 }
};
const markAsNeedReview = async () => {
 if (!currentWord.value)
  return;
 try {
  await learningApi.updateWordProgress(currentWord.value.id, { progress: 50 });
  wordProgressMap.value[currentWord.value.id] = 50;
  closeWordDetail();
 }
 catch (error) {
  console.error('更新进度失败', error);
 }
};
const startStudy = () => {
 if (words.value.length > 0) {
  router.push(`/review`);
 }
};
watch(selectedLanguage, () => {
  if (selectedLanguage.value) {
    loadWords();
  }
});
onMounted(() => {
 loadLanguages();
});
</script>

<style scoped>
.words-container {
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

.language-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}

.language-selector select {
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  min-width: 150px;
}

.word-count {
  color: #666;
  font-size: 14px;
}

.words-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.word-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
  border-left: 4px solid #409EFF;
}

.word-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.word-card.learned {
  border-left-color: #67C23A;
  background: #f0fdf4;
}

.word-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.word-header h3 {
  font-size: 20px;
  color: #333;
}

.pronunciation {
  color: #999;
  font-size: 14px;
}

.meaning {
  color: #666;
  font-size: 15px;
  margin-bottom: 8px;
}

.example {
  color: #999;
  font-size: 13px;
  font-style: italic;
  margin-bottom: 12px;
}

.word-progress {
  display: flex;
  align-items: center;
  gap: 10px;
}

.word-progress .progress-bar {
  flex: 1;
  height: 6px;
  background: #eee;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 3px;
}

.word-progress span {
  font-size: 12px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.study-action {
  text-align: center;
  margin-top: 30px;
}

.study-btn {
  background: #409EFF;
  color: white;
  border: none;
  padding: 14px 40px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.study-btn:hover {
  background: #66B1FF;
}

.word-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  padding: 30px;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.word-display {
  text-align: center;
  margin-bottom: 30px;
}

.word-display h2 {
  font-size: 36px;
  color: #333;
  margin-bottom: 8px;
}

.word-display .pronunciation {
  font-size: 18px;
  color: #666;
}

.audio-btn {
  margin-top: 16px;
  background: #409EFF;
  color: white;
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.3s;
}

.audio-btn:hover {
  background: #66B1FF;
}

.word-info {
  margin-bottom: 30px;
}

.section {
  margin-bottom: 20px;
}

.section h4 {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.section p {
  font-size: 16px;
  color: #333;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.action-buttons button {
  flex: 1;
  padding: 14px;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.learn-btn {
  background: #67C23A;
  color: white;
  border: none;
}

.learn-btn:hover {
  background: #85CE61;
}

.review-btn {
  background: #E6A23C;
  color: white;
  border: none;
}

.review-btn:hover {
  background: #F0C78A;
}
</style>