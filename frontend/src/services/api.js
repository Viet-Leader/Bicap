import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 15000,
});

api.interceptors.request.use((config) => {
  const token = useAuthStore().token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res.data,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore().logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
