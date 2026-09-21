export default [
  {
    path: '/admin',
    component: () => import('@/components/layout/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', name: 'admin-dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'accounts', name: 'admin-accounts', component: () => import('@/views/admin/Accounts.vue') },
      { path: 'farms', name: 'admin-farms', component: () => import('@/views/admin/Farms.vue') },
      { path: 'crops', name: 'admin-crops', component: () => import('@/views/admin/Crops.vue') },
      { path: 'products', name: 'admin-products', component: () => import('@/views/admin/Products.vue') },
      { path: 'transactions', name: 'admin-transactions', component: () => import('@/views/admin/Transactions.vue') },
      { path: 'orders', name: 'admin-orders', component: () => import('@/views/admin/Orders.vue') },
      { path: 'notifications', name: 'admin-notifications', component: () => import('@/views/admin/Notifications.vue') }
    ]
  }
];
