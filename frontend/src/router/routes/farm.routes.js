export default [
  {
    path: '/farm',
    component: () => import('@/components/layout/FarmLayout.vue'),
    meta: { requiresAuth: true, role: 'FARM' },
    children: [
      { path: '', name: 'farm-dashboard', component: () => import('@/views/FarmDashboardView.vue') },
      { path: 'profile', name: 'farm-profile', component: () => import('@/views/farm/FarmProfile.vue') },
      { path: 'seasons', name: 'farm-seasons', component: () => import('@/views/farm/SeasonManagement.vue') },
      { path: 'orders', name: 'farm-orders', component: () => import('@/views/farm/FarmOrders.vue') }
    ]
  }
];
