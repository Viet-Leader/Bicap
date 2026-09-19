export default [
  {
    path: '/',
    component: () => import('@/components/layout/GuestLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/features/guest/HomeView.vue') },
      { path: 'login', name: 'login', component: () => import('@/features/auth/LoginView.vue') },
      { path: 'trace/:batchCode', name: 'traceability', component: () => import('@/features/traceability/TraceabilityView.vue') }
    ]
  }
];
