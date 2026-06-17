
import { createStore } from 'vuex'

export default createStore({
  state: {
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    currentLanguage: 'en',
    learningProgress: {}
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    CLEAR_AUTH(state) {
      state.token = null
      state.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    SET_LANGUAGE(state, language) {
      state.currentLanguage = language
    },
    UPDATE_PROGRESS(state, progress) {
      state.learningProgress = { ...state.learningProgress, ...progress }
    }
  },
  actions: {
    login({ commit }, { token, user }) {
      commit('SET_TOKEN', token)
      commit('SET_USER', user)
    },
    logout({ commit }) {
      commit('CLEAR_AUTH')
    },
    setLanguage({ commit }, language) {
      commit('SET_LANGUAGE', language)
    },
    updateProgress({ commit }, progress) {
      commit('UPDATE_PROGRESS', progress)
    }
  },
  getters: {
    isLoggedIn: state => !!state.token,
    userId: state => state.user?.id,
    username: state => state.user?.username,
    role: state => state.user?.role,
    token: state => state.token
  }
})
