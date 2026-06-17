<template>
  <div class="review-container">
    <div class="header">
      <h1>单词复习</h1>
      <p>根据记忆曲线复习需要巩固的单词</p>
    </div>

    <div class="stats-card">
      <div class="stat-item">
        <span class="stat-value">{{ reviewWords.length }}</span>
        <span class="stat-label">待复习单词</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ correctCount }}</span>
        <span class="stat-label">本次正确</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ wrongCount }}</span>
        <span class="stat-label">本次错误</span>
      </div>
    </div>

    <div class="review-area" v-if="reviewWords.length > 0 && currentWord">
      <div class="word-card">
        <div class="word-display">
          <span class="word-text">{{ currentWord.word }}</span>
          <span class="pronunciation">{{ currentWord.pronunciation }}</span>
        </div>
        <button class="show-answer-btn" @click="showAnswer" v-if="!showingAnswer">
          显示答案
        </button>
        <div class="answer-section" v-if="showingAnswer">
          <p class="meaning">{{ currentWord.meaning }}</p>
          <p class="example" v-if="currentWord.example">{{ currentWord.example }}</p>
        </div>
      </div>

      <div class="action-buttons" v-if="showingAnswer">
        <button class="know-btn" @click="markAsKnow">
          <el-icon>Check</el-icon>
          认识
        </button>
        <button class="forget-btn" @click="markAsForget">
          <el-icon>Close</el-icon>
          忘记
        </button>
      </div>

      <div class="progress-info">
        <span>进度：{{ currentIndex + 1 }} / {{ reviewWords.length }}</span>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
        </div>
      </div>
    </div>

    <div v-if="reviewWords.length === 0" class="empty-state">
      <el-icon size="48">CheckCircle</el-icon>
      <p>暂无需要复习的单词</p>
      <button @click="loadReviewWords" class="refresh-btn">
        <el-icon>Refresh</el-icon>
        刷新
      </button>
    </div>

    <div v-if="isFinished" class="finish-modal">
      <div class="finish-content">
        <el-icon size="64" class="success-icon">CheckCircle</el-icon>
        <h2>复习完成！</h2>
        <div class="result-stats">
          <div class="result-item">
            <span class="result-value">{{ correctCount }}</span>
            <span class="result-label">正确</span>
          </div>
          <div class="result-item">
            <span class="result-value">{{ wrongCount }}</span>
            <span class="result-label">错误</span>
          </div>
          <div class="result-item">
            <span class="result-value">{{ accuracy }}%</span>
            <span class="result-label">正确率</span>
          </div>
        </div>
        <button @click="restartReview" class="restart-btn">
          <el-icon>Refresh</el-icon>
          再次复习
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, computed, onMounted } from 'vue';
import { wordApi, learningApi } from '../api';
const reviewWords = ref([]);
const currentIndex = ref(0);
const showingAnswer = ref(false);
const correctCount = ref(0);
const wrongCount = ref(0);
const isFinished = ref(false);
const currentWord = computed(() => reviewWords.value[currentIndex.value]);
const progressPercent = computed(() => {
 if (reviewWords.value.length === 0)
 return 0;
 return ((currentIndex.value + 1) / reviewWords.value.length) * 100;
});
const accuracy = computed(() => {
 const total = correctCount.value + wrongCount.value;
 if (total === 0)
 return 0;
 return Math.round((correctCount.value / total) * 100);
});
const loadReviewWords = async () => {
 try {
 reviewWords.value = await wordApi.getForReview();
 currentIndex.value = 0;
 showingAnswer.value = false;
 correctCount.value = 0;
 wrongCount.value = 0;
 isFinished.value = false;
 }
 catch (error) {
 console.error('加载复习单词失败', error);
 }
};
const showAnswer = () => {
 showingAnswer.value = true;
};
const markAsKnow = async () => {
 if (!currentWord.value)
 return;
 try {
 await learningApi.updateWordProgress(currentWord.value.id, { progress: 100 });
 correctCount.value++;
 nextWord();
 }
 catch (error) {
 console.error('更新进度失败', error);
 }
};
const markAsForget = async () => {
 if (!currentWord.value)
 return;
 try {
 await learningApi.updateWordProgress(currentWord.value.id, { progress: 0 });
 wrongCount.value++;
 nextWord();
 }
 catch (error) {
 console.error('更新进度失败', error);
 }
};
const nextWord = () => {
 showingAnswer.value = false;
 if (currentIndex.value < reviewWords.value.length - 1) {
 currentIndex.value++;
 }
 else {
 isFinished.value = true;
 }
};
const restartReview = () => {
 loadReviewWords();
};
onMounted(() => {
 loadReviewWords();
});
</script>

<style scoped>
.review-container {
  padding: 20px;
  max-width: 600px;
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

.stats-card {
  display: flex;
  justify-content: space-around;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.review-area {
  background: white;
  border-radius: 12px;
  padding: 40px 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.word-card {
  text-align: center;
  margin-bottom: 30px;
}

.word-display {
  margin-bottom: 30px;
}

.word-text {
  display: block;
  font-size: 48px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.pronunciation {
  font-size: 20px;
  color: #666;
}

.show-answer-btn {
  background: #409EFF;
  color: white;
  border: none;
  padding: 14px 48px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.show-answer-btn:hover {
  background: #66B1FF;
}

.answer-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
}

.meaning {
  font-size: 24px;
  color: #333;
  margin-bottom: 12px;
}

.example {
  font-size: 16px;
  color: #666;
  font-style: italic;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 30px;
}

.action-buttons button {
  flex: 1;
  max-width: 180px;
  padding: 16px;
  border-radius: 12px;
  border: none;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: transform 0.2s;
}

.know-btn {
  background: #67C23A;
  color: white;
}

.know-btn:hover {
  transform: scale(1.05);
}

.forget-btn {
  background: #F56C6C;
  color: white;
}

.forget-btn:hover {
  transform: scale(1.05);
}

.progress-info {
  text-align: center;
}

.progress-info span {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.progress-info .progress-bar {
  height: 8px;
  background: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 4px;
  transition: width 0.3s;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-state p {
  margin-top: 12px;
  margin-bottom: 20px;
}

.refresh-btn {
  background: #409EFF;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.finish-modal {
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

.finish-content {
  background: white;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  width: 90%;
  max-width: 400px;
}

.success-icon {
  color: #67C23A;
  margin-bottom: 20px;
}

.finish-content h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
}

.result-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30px;
}

.result-item {
  text-align: center;
}

.result-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.result-label {
  font-size: 14px;
  color: #666;
}

.restart-btn {
  background: #409EFF;
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
</style>