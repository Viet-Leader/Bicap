import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '../layouts/DashboardLayout.vue'
import TraceabilityView from '../views/TraceabilityView.vue'
import DriverLogisticsView from '../views/DriverLogisticsView.vue'
import FarmDashboardView from '../views/FarmDashboardView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: DashboardLayout,
      children: [
        { path: '', redirect: '/farm' },
        { path: 'farm', component: FarmDashboardView },
        { path: 'traceability', component: TraceabilityView },
        { path: 'logistics', component: DriverLogisticsView },
      ],
    },
  ],
})

export default router
