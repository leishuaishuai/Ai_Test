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
  login: data => api.post('/auth/login', data)
}

export const adminUserApi = {
  getAll: params => api.get('/admin/users', { params }),
  getById: id => api.get(`/admin/users/${id}`),
  updateStatus: (id, status) => api.put(`/admin/users/${id}/status`, { status }),
  delete: id => api.delete(`/admin/users/${id}`)
}

export const adminCourseApi = {
  getAll: params => api.get('/admin/courses', { params }),
  getById: id => api.get(`/admin/courses/${id}`),
  create: data => api.post('/admin/courses', data),
  update: (id, data) => api.put(`/admin/courses/${id}`, data),
  updateStatus: (id, status) => api.put(`/admin/courses/${id}/status`, { status }),
  delete: id => api.delete(`/admin/courses/${id}`),
  getChapters: courseId => api.get(`/admin/courses/${courseId}/chapters`),
  addChapter: (courseId, data) => api.post(`/admin/courses/${courseId}/chapters`, data),
  updateChapter: (courseId, chapterId, data) => api.put(`/admin/courses/${courseId}/chapters/${chapterId}`, data),
  deleteChapter: (courseId, chapterId) => api.delete(`/admin/courses/${courseId}/chapters/${chapterId}`),
  getLessons: (courseId, chapterId) => api.get(`/admin/courses/${courseId}/chapters/${chapterId}/lessons`),
  addLesson: (courseId, chapterId, data) => api.post(`/admin/courses/${courseId}/chapters/${chapterId}/lessons`, data),
  updateLesson: (courseId, chapterId, lessonId, data) => api.put(`/admin/courses/${courseId}/chapters/${chapterId}/lessons/${lessonId}`, data),
  deleteLesson: (courseId, chapterId, lessonId) => api.delete(`/admin/courses/${courseId}/chapters/${chapterId}/lessons/${lessonId}`)
}

export const achievementApi = {
  getAll: () => api.get('/achievements'),
  create: data => api.post('/achievements', data),
  update: (id, data) => api.put(`/achievements/${id}`, data),
  delete: id => api.delete(`/achievements/${id}`)
}

export const postApi = {
  getAll: params => api.get('/posts', { params }),
  delete: id => api.delete(`/posts/${id}`)
}

export const languageApi = {
  getAll: () => api.get('/languages')
}

export default api