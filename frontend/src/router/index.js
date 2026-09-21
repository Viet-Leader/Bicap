import { createRouter, createWebHistory } from 'vue-router';
import { roleGuard } from './guards';
import publicRoutes from './routes/public.routes';
import adminRoutes from './routes/admin.routes';
import farmRoutes from './routes/farm.routes';
import retailerRoutes from './routes/retailer.routes';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...publicRoutes,
    ...adminRoutes,
    ...farmRoutes,
    ...retailerRoutes
  ]
});

router.beforeEach(roleGuard);

export default router;
