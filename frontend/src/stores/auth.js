import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('bicap_token'),
    user: JSON.parse(localStorage.getItem('bicap_user') || 'null'),
  }),
  getters: { isAuthenticated: (state) => Boolean(state.token) },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('bicap_token', token)
    },
    setUser(user) {
      this.user = user
      localStorage.setItem('bicap_user', JSON.stringify(user))
    },
    signOut() {
      this.token = null
      this.user = null
      localStorage.removeItem('bicap_token')
      localStorage.removeItem('bicap_user')
    },
  },
})
