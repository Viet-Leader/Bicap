export default [
  {
    path: '/admin',
    component: () => import('@/components/layout/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', name: 'admin-dashboard', component: () => import('@/features/dashboard/AdminDashboard.vue') },
      { path: 'accounts', name: 'admin-accounts', component: () => import('@/features/accounts/AccountList.vue') }
    ]
  }
];
