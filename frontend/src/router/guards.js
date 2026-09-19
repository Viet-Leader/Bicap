import { useAuthStore } from '@/stores/auth';

export function roleGuard(to, from, next) {
  const auth = useAuthStore();
  const requiredRole = to.meta.role;
  
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return next({ name: 'login', query: { redirect: to.fullPath } });
  }
  
  if (requiredRole && auth.role !== requiredRole) {
    return next({ name: 'home' }); // Redirect to home if forbidden
  }
  
  next();
}

export function guestGuard(to, from, next) {
  const auth = useAuthStore();
  if (auth.isAuthenticated) {
    return next({ name: `${auth.role.toLowerCase()}-dashboard` });
  }
  next();
}
