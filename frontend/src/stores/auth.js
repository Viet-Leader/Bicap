import { defineStore } from 'pinia';
import axiosClient from '@/api/axiosClient';

const ROLE_ALIASES = {
  ADMIN: 'ADMIN',
  ROLE_ADMIN: 'ADMIN',
  FARM: 'FARM',
  ROLE_FARM: 'FARM',
  RETAILER: 'RETAILER',
  ROLE_RETAILER: 'RETAILER',
};

function normalizeRole(role) {
  return ROLE_ALIASES[String(role || '').toUpperCase()] || null;
}

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
    async login(username, password) {
      const response = await axiosClient.post('/auth/login', { username, password });
      const data = response.data;
      const role = normalizeRole(data.role);

      if (!data.accessToken || !role) {
        throw new Error('Tài khoản không có quyền hợp lệ.');
      }

      this.token = data.accessToken;
      this.role = role;
      this.user = { username: data.username || username };
      localStorage.setItem('bicap_token', this.token);
      localStorage.setItem('bicap_user', JSON.stringify(this.user));
      localStorage.setItem('bicap_role', this.role);
      return data;
    },
    restore() {
      const token = localStorage.getItem('bicap_token');
      const role = normalizeRole(localStorage.getItem('bicap_role'));
      const user = localStorage.getItem('bicap_user');
      if (token && role) {
        this.token = token;
        this.role = role;
        try {
          this.user = user ? JSON.parse(user) : null;
        } catch {
          this.user = null;
        }
      }
    },
    logout() {
      this.$reset();
      localStorage.removeItem('bicap_token');
      localStorage.removeItem('bicap_user');
      localStorage.removeItem('bicap_role');
    }
  },
});
