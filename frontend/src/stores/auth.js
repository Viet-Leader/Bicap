import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({ token: localStorage.getItem('bicap_token') }),
  getters: { isAuthenticated: (state) => Boolean(state.token) },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('bicap_token', token)
    },
    signOut() {
      this.token = null
      localStorage.removeItem('bicap_token')
    },
  },
})
