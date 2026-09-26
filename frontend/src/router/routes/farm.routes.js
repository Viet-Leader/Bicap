export default [
  {
    path: '/farm',
    component: () => import('@/components/layout/FarmLayout.vue'),
    meta: { requiresAuth: true, role: 'FARM' },
    children: [
      { path: '', name: 'farm-dashboard', component: () => import('@/views/FarmDashboardView.vue') },
      { path: 'products', name: 'farm-products', component: () => import('@/views/farm/FarmProducts.vue') },
      { path: 'seasons', name: 'farm-seasons', component: () => import('@/views/farm/SeasonManagement.vue') },
      { path: 'farm-info', name: 'farm-info', component: () => import('@/views/farm/FarmInfo.vue') },
      { path: 'notifications', name: 'farm-notifications', component: () => import('@/views/farm/FarmNotifications.vue') },
      { path: 'profile', redirect: { name: 'farm-info' } }
    ]
  }
];
