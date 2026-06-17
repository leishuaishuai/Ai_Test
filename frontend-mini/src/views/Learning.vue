<template>
  <div class="learning-container">
    <div class="lesson-header" v-if="lesson">
      <div class="back-btn" @click="$router.back()">
        <el-icon>ArrowLeft</el-icon>
        返回
      </div>
      <div class="lesson-info">
        <h1>{{ lesson.title }}</h1>
        <p>{{ getLessonTypeText(lesson.type) }} - {{ getCourseTitle() }}</p>
      </div>
    </div>

    <div class="lesson-content" v-if="lesson">
      <div class="vocabulary-section" v-if="lesson.type === 'VOCABULARY'">
        <h2>单词学习</h2>
        <div class="words-list">
          <div
            v-for="(word, index) in currentWords"
            :key="word.id"
            class="word-item"
            :class="{ active: currentIndex === index }"
          >
            <div class="word-content">
              <div class="word-main">
                <span class="word-text">{{ word.word }}</span>
                <span class="pronunciation">{{ word.pronunciation }}</span>
                <button class="audio-btn" @click="playAudio(word)">
                  <el-icon>Volume</el-icon>
                </button>
              </div>
              <p class="meaning">{{ word.meaning }}</p>
              <p class="example" v-if="word.example">{{ word.example }}</p>
            </div>
            <div class="word-actions">
              <button @click="markWord(word, true)">
                <el-icon>Check</el-icon>
              </button>
              <button @click="markWord(word, false)">
                <el-icon>Close</el-icon>
              </button>
            </div>
          </div>
        </div>
        <div class="navigation">
          <button @click="prevWord" :disabled="currentIndex === 0">
            <el-icon>ArrowLeft</el-icon>
            上一个
          </button>
          <span>{{ currentIndex + 1 }} / {{ currentWords.length }}</span>
          <button @click="nextWord" :disabled="currentIndex === currentWords.length - 1">
            下一个
            <el-icon>ArrowRight</el-icon>
          </button>
        </div>
      </div>

      <div class="grammar-section" v-if="lesson.type === 'GRAMMAR'">
        <h2>语法练习</h2>
        <div class="grammar-content" v-html="lesson.content"></div>
        <div class="quiz-section" v-if="grammarQuestions.length > 0">
          <div
            v-for="(question, index) in grammarQuestions"
            :key="index"
            class="question-item"
            :class="{ active: currentQuestionIndex === index }"
          >
            <h3>问题 {{ index + 1 }}</h3>
            <p>{{ question.question }}</p>
            <div class="options">
              <button
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
                :class="['option-btn', { selected: question.selected === optIndex, correct: question.answered && question.correctAnswer === optIndex, wrong: question.answered && question.selected === optIndex && question.selected !== question.correctAnswer }]"
                @click="selectAnswer(index, optIndex)"
              >
                {{ String.fromCharCode(65 + optIndex) }}. {{ option }}
              </button>
            </div>
            <div v-if="question.answered" class="feedback">
              <p v-if="question.selected === question.correctAnswer" class="correct">
                <el-icon>Check</el-icon> 回答正确！
              </p>
              <p v-else class="wrong">
                <el-icon>Close</el-icon> 回答错误，正确答案是 {{ String.fromCharCode(65 + question.correctAnswer) }}
              </p>
            </div>
          </div>
        </div>
        <div class="quiz-navigation">
          <button @click="prevQuestion" :disabled="currentQuestionIndex === 0">
            <el-icon>ArrowLeft</el-icon>
          </button>
          <span>{{ currentQuestionIndex + 1 }} / {{ grammarQuestions.length }}</span>
          <button @click="nextQuestion" :disabled="currentQuestionIndex === grammarQuestions.length - 1">
            <el-icon>ArrowRight</el-icon>
          </button>
        </div>
      </div>

      <div class="speaking-section" v-if="lesson.type === 'SPEAKING'">
        <h2>口语跟读</h2>
        <div class="speaking-content">
          <div class="target-text">
            <p>{{ speakingText.text }}</p>
            <p class="translation">{{ speakingText.translation }}</p>
          </div>
          <div class="recording-area">
            <button
              :class="['record-btn', { recording: isRecording }]"
              @click="toggleRecording"
            >
              <el-icon>{{ isRecording ? 'Stop' : 'Microphone' }}</el-icon>
              <span>{{ isRecording ? '停止录音' : '开始录音' }}</span>
            </button>
          </div>
          <div class="result-area" v-if="recordingResult">
            <p>你的发音：{{ recordingResult.text }}</p>
            <div class="score-display">
              <span class="score-label">发音评分</span>
              <span class="score-value">{{ recordingResult.score }}分</span>
            </div>
            <button @click="playRecording">
              <el-icon>Play</el-icon>
              播放录音
            </button>
          </div>
        </div>
      </div>

      <div class="listening-section" v-if="lesson.type === 'LISTENING'">
        <h2>听力训练</h2>
        <div class="listening-content">
          <div class="audio-player">
            <button class="play-btn" @click="playListening">
              <el-icon>{{ isPlaying ? 'Pause' : 'Play' }}</el-icon>
            </button>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
            </div>
            <span class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
          </div>
          <div class="transcript">
            <button @click="toggleTranscript">
              {{ showTranscript ? '隐藏原文' : '显示原文' }}
            </button>
            <p v-if="showTranscript">{{ listeningContent.transcript }}</p>
          </div>
          <div class="listening-questions" v-if="listeningQuestions.length > 0">
            <div
              v-for="(question, index) in listeningQuestions"
              :key="index"
              class="question-item"
            >
              <h3>问题 {{ index + 1 }}</h3>
              <p>{{ question.question }}</p>
              <div class="options">
                <button
                  v-for="(option, optIndex) in question.options"
                  :key="optIndex"
                  :class="['option-btn', { selected: question.selected === optIndex }]"
                  @click="selectListeningAnswer(index, optIndex)"
                >
                  {{ String.fromCharCode(65 + optIndex) }}. {{ option }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="finish-section">
      <button @click="finishLesson" class="finish-btn">
        <el-icon>CheckCircle</el-icon>
        完成课程
      </button>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { lessonApi, wordApi, learningApi, courseApi } from '../api';
const route = useRoute();
const lesson = ref(null);
const course = ref(null);
const currentWords = ref([]);
const currentIndex = ref(0);
const grammarQuestions = ref([]);
const currentQuestionIndex = ref(0);
const speakingText = ref({ text: 'Hello, how are you?', translation: '你好，你好吗？' });
const isRecording = ref(false);
const recordingResult = ref(null);
const isPlaying = ref(false);
const progressPercent = ref(0);
const currentTime = ref(0);
const duration = ref(180);
const showTranscript = ref(false);
const listeningContent = ref({ transcript: 'This is a listening exercise. Please listen carefully.' });
const listeningQuestions = ref([
 { question: 'What is this exercise about?', options: ['Reading', 'Writing', 'Listening', 'Speaking'], selected: null, correctAnswer: 2 },
 { question: 'How should you listen?', options: ['Carelessly', 'Carefully', 'Quickly', 'Slowly'], selected: null, correctAnswer: 1 }
]);
const getLessonTypeText = (type) => {
 const types = { 'VOCABULARY': '单词学习', 'GRAMMAR': '语法练习', 'SPEAKING': '口语跟读', 'LISTENING': '听力训练' };
 return types[type] || type;
};
const getCourseTitle = () => {
 return course.value?.title || '';
};
const loadLesson = async () => {
 try {
 lesson.value = await lessonApi.getById(route.params.lessonId);
 }
 catch (error) {
 console.error('加载课程失败', error);
 }
};
const loadCourse = async () => {
 if (!lesson.value)
 return;
 try {
 course.value = await courseApi.getById(lesson.value.courseId);
 }
 catch (error) {
 console.error('加载课程信息失败', error);
 }
};
const loadWords = async () => {
 if (!lesson.value?.languageId)
 return;
 try {
 currentWords.value = await wordApi.getByLanguage(lesson.value.languageId);
 }
 catch (error) {
 console.error('加载单词失败', error);
 }
};
const initGrammarQuestions = () => {
 grammarQuestions.value = [
 { question: '选择正确的动词形式：She ___ to school every day.', options: ['go', 'goes', 'going', 'went'], selected: null, correctAnswer: 1, answered: false },
 { question: '选择正确的冠词：I saw ___ interesting movie yesterday.', options: ['a', 'an', 'the', '/'], selected: null, correctAnswer: 1, answered: false },
 { question: '选择正确的代词：This book belongs to ___.', options: ['I', 'me', 'my', 'mine'], selected: null, correctAnswer: 1, answered: false }
 ];
};
const prevWord = () => {
 if (currentIndex.value > 0) {
 currentIndex.value--;
 }
};
const nextWord = () => {
 if (currentIndex.value < currentWords.value.length - 1) {
 currentIndex.value++;
 }
};
const playAudio = (word) => {
};
const markWord = async (word, learned) => {
 try {
 await learningApi.updateWordProgress(word.id, { progress: learned ? 100 : 50 });
 }
 catch (error) {
 console.error('更新单词进度失败', error);
 }
};
const selectAnswer = (questionIndex, optionIndex) => {
 const question = grammarQuestions.value[questionIndex];
 if (question.answered)
 return;
 question.selected = optionIndex;
 question.answered = true;
};
const prevQuestion = () => {
 if (currentQuestionIndex.value > 0) {
 currentQuestionIndex.value--;
 }
};
const nextQuestion = () => {
 if (currentQuestionIndex.value < grammarQuestions.value.length - 1) {
 currentQuestionIndex.value++;
 }
};
const toggleRecording = () => {
 isRecording.value = !isRecording.value;
 if (!isRecording.value) {
 recordingResult.value = { text: speakingText.value.text, score: 85 };
 }
};
const playRecording = () => {
};
const playListening = () => {
 isPlaying.value = !isPlaying.value;
};
const formatTime = (seconds) => {
 const mins = Math.floor(seconds / 60);
 const secs = seconds % 60;
 return `${mins}:${secs.toString().padStart(2, '0')}`;
};
const toggleTranscript = () => {
 showTranscript.value = !showTranscript.value;
};
const selectListeningAnswer = (questionIndex, optionIndex) => {
 listeningQuestions.value[questionIndex].selected = optionIndex;
};
const finishLesson = async () => {
 try {
 await learningApi.updateLessonProgress(route.params.lessonId, { progress: 100 });
 alert('课程完成！');
 }
 catch (error) {
 console.error('完成课程失败', error);
 }
};
onMounted(async () => {
 await loadLesson();
 await loadCourse();
 await loadWords();
 if (lesson.value?.type === 'GRAMMAR') {
 initGrammarQuestions();
 }
});
</script>

<style scoped>
.learning-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.lesson-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
}

.lesson-info h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 4px;
}

.lesson-info p {
  font-size: 14px;
  color: #666;
}

.lesson-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.vocabulary-section h2, .grammar-section h2, .speaking-section h2, .listening-section h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 24px;
}

.word-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.word-item.active {
  border-color: #409EFF;
  background: #f0f7ff;
}

.word-main {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.word-text {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.pronunciation {
  font-size: 16px;
  color: #666;
}

.audio-btn {
  background: #409EFF;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
}

.meaning {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.example {
  font-size: 14px;
  color: #999;
  font-style: italic;
}

.word-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.word-actions button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.word-actions button:first-child {
  background: #67C23A;
  color: white;
}

.word-actions button:last-child {
  background: #F56C6C;
  color: white;
}

.navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}

.navigation button {
  padding: 12px 24px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.navigation button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.navigation span {
  font-size: 16px;
  color: #666;
}

.grammar-content {
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
}

.question-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.question-item h3 {
  font-size: 16px;
  color: #999;
  margin-bottom: 12px;
}

.question-item p {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-btn {
  padding: 14px 20px;
  border: 2px solid #ddd;
  border-radius: 8px;
  background: white;
  text-align: left;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.option-btn:hover {
  border-color: #409EFF;
}

.option-btn.selected {
  border-color: #409EFF;
  background: #f0f7ff;
}

.option-btn.correct {
  border-color: #67C23A;
  background: #f0fdf4;
}

.option-btn.wrong {
  border-color: #F56C6C;
  background: #fef2f2;
}

.feedback {
  margin-top: 16px;
}

.feedback .correct {
  color: #67C23A;
  font-weight: bold;
}

.feedback .wrong {
  color: #F56C6C;
}

.quiz-navigation {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  padding: 20px 0;
}

.quiz-navigation button {
  width: 48px;
  height: 48px;
  border: 1px solid #ddd;
  border-radius: 50%;
  background: white;
  cursor: pointer;
}

.quiz-navigation button:disabled {
  opacity: 0.5;
}

.speaking-content, .listening-content {
  text-align: center;
}

.target-text {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
}

.target-text p {
  font-size: 24px;
  color: #333;
  margin-bottom: 12px;
}

.target-text .translation {
  font-size: 18px;
  color: #666;
}

.recording-area {
  margin-bottom: 30px;
}

.record-btn {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: none;
  background: #409EFF;
  color: white;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.record-btn.recording {
  background: #F56C6C;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.result-area {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
}

.result-area p {
  font-size: 18px;
  color: #333;
  margin-bottom: 16px;
}

.score-display {
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 16px;
}

.score-label {
  font-size: 16px;
  color: #666;
}

.score-value {
  font-size: 36px;
  color: #409EFF;
  font-weight: bold;
}

.result-area button {
  padding: 10px 24px;
  background: #409EFF;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
}

.audio-player {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
}

.play-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #409EFF;
  color: white;
  border: none;
  cursor: pointer;
}

.audio-player .progress-bar {
  flex: 1;
  height: 8px;
  background: #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.audio-player .progress-fill {
  height: 100%;
  background: #409EFF;
}

.time-display {
  font-size: 14px;
  color: #666;
  min-width: 100px;
}

.transcript {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
}

.transcript button {
  background: none;
  border: none;
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 16px;
}

.transcript p {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
}

.listening-questions {
  text-align: left;
}

.finish-section {
  text-align: center;
}

.finish-btn {
  background: #67C23A;
  color: white;
  border: none;
  padding: 16px 48px;
  border-radius: 25px;
  font-size: 18px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background 0.3s;
}

.finish-btn:hover {
  background: #85CE61;
}
</style>