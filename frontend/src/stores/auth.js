import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    role: null, // 'ADMIN' | 'FARM' | 'RETAILER'
    farmId: null,
    retailerId: null,
    accountId: null,
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
    isFarm: (state) => state.role === 'FARM',
    isRetailer: (state) => state.role === 'RETAILER',
  },
  
  actions: {
    login(credentials) {
      // Mock login implementation
      this.token = 'mock-token';
      this.role = credentials.role || 'FARM';
      this.user = { name: 'Test User' };
    },
    logout() {
      this.$reset();
      localStorage.removeItem('token');
    }
  },
});
