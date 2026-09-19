import { createRouter, createWebHistory } from 'vue-router';
import { roleGuard } from './guards';
import publicRoutes from './routes/public.routes';
import adminRoutes from './routes/admin.routes';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...publicRoutes,
    ...adminRoutes
  ]
});

router.beforeEach(roleGuard);

export default router;
