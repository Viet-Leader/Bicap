export default [
  {
    path: '/',
    component: () => import('@/components/layout/GuestLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/Home.vue') },
      { path: 'market', name: 'market', component: () => import('@/views/MarketView.vue') },
      { path: 'login', name: 'login', component: () => import('@/views/auth/Login.vue') },
      { path: 'register', name: 'register', component: () => import('@/features/auth/RegisterView.vue') },
      { path: 'trace', name: 'trace-search', component: () => import('@/views/TraceSearch.vue') },
      { path: 'trace/:batchCode', name: 'traceability', component: () => import('@/views/TraceabilityView.vue') }
    ]
  }
];
