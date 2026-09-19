import { useAuthStore } from '@/stores/auth';
import api from '@/services/api';
import endpoints from '@/services/endpoints';
import { ref } from 'vue';

export function useAuth() {
  const store = useAuthStore();
  const loading = ref(false);
  const error = ref(null);

  const login = async (credentials) => {
    loading.value = true;
    error.value = null;
    try {
      // Mock login since backend might not be ready
      setTimeout(() => {
        store.login(credentials);
      }, 500);
      return true;
    } catch (err) {
      error.value = err.message || 'Login failed';
      return false;
    } finally {
      loading.value = false;
    }
  };

  const logout = () => {
    store.logout();
  };

  return { login, logout, loading, error };
}
