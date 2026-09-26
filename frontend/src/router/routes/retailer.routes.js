export default [
  {
    path: '/retailer',
    component: () => import('@/components/layout/RetailerLayout.vue'),
    meta: { requiresAuth: true, role: 'RETAILER' },
    children: [
      { path: '', name: 'retailer-dashboard', component: () => import('@/views/retailer/ProductSearch.vue') },
      { path: 'products', name: 'retailer-products', component: () => import('@/views/retailer/ProductSearch.vue') },
      { path: 'cart', name: 'retailer-cart', component: () => import('@/views/retailer/CartView.vue') },
      { path: 'orders', name: 'retailer-orders', component: () => import('@/views/retailer/OrderHistory.vue') }
    ]
  }
];
