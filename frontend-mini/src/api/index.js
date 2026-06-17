
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(response => {
  return response.data
}, error => {
  if (error.response?.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login'
  }
  return Promise.reject(error)
})

export const authApi = {
  login: data => api.post('/auth/login', data),
  register: data => api.post('/auth/register', data),
  getCurrentUser: () => api.get('/auth/me'),
  updateUser: data => api.put('/auth/me', data),
  logout: () => api.post('/auth/logout')
}

export const languageApi = {
  getAll: () => api.get('/languages'),
  getByCode: code => api.get(`/languages/${code}`)
}

export const courseApi = {
  getAll: params => api.get('/courses', { params }),
  getById: id => api.get(`/courses/${id}`)
}

export const chapterApi = {
  getByCourse: courseId => api.get(`/chapters/course/${courseId}`),
  getById: id => api.get(`/chapters/${id}`)
}

export const lessonApi = {
  getByChapter: chapterId => api.get(`/lessons/chapter/${chapterId}`),
  getById: id => api.get(`/lessons/${id}`)
}

export const wordApi = {
  getByLanguage: languageId => api.get('/words', { params: { languageId } }),
  getForReview: () => api.get('/words/review'),
  getById: id => api.get(`/words/${id}`)
}

export const learningApi = {
  getStatistics: () => api.get('/learning/statistics'),
  getCourseProgress: () => api.get('/learning/course-progress'),
  getCourseProgressById: courseId => api.get(`/learning/course-progress/${courseId}`),
  updateCourseProgress: courseId => api.post(`/learning/course-progress/${courseId}`),
  getLessonProgress: lessonId => api.get(`/learning/lesson-progress/${lessonId}`),
  updateLessonProgress: (lessonId, data) => api.post(`/learning/lesson-progress/${lessonId}`, data),
  getWordProgress: wordId => api.get(`/learning/word-progress/${wordId}`),
  updateWordProgress: (wordId, data) => api.post(`/learning/word-progress/${wordId}`, data),
  getRecommendations: () => api.get('/learning/recommendations')
}

export const achievementApi = {
  getAll: () => api.get('/achievements'),
  getUserAchievements: () => api.get('/achievements/mine')
}

export const pointsApi = {
  getTotal: () => api.get('/points/total'),
  getHistory: params => api.get('/points/history', { params })
}

export const postApi = {
  getAll: params => api.get('/posts', { params }),
  getById: id => api.get(`/posts/${id}`),
  create: data => api.post('/posts', data),
  update: (id, data) => api.put(`/posts/${id}`, data),
  delete: id => api.delete(`/posts/${id}`),
  like: id => api.post(`/posts/${id}/like`),
  unlike: id => api.post(`/posts/${id}/unlike`)
}

export const commentApi = {
  getByPost: (postId, params) => api.get(`/comments/post/${postId}`, { params }),
  create: data => api.post('/comments', data),
  update: (id, data) => api.put(`/comments/${id}`, data),
  delete: id => api.delete(`/comments/${id}`)
}

export const signApi = {
  sign: () => api.post('/sign'),
  getStatus: () => api.get('/sign/status')
}

export default api
