import { createRouter, createWebHistory } from 'vue-router'

// Layouts
import GuestLayout from '../layouts/GuestLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

// Views
import Home from '../views/Home.vue'
import Login from '../views/auth/Login.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'
import AdminAccounts from '../views/admin/Accounts.vue'
import AdminFarms from '../views/admin/Farms.vue'
import AdminTransactions from '../views/admin/Transactions.vue'
import AdminCrops from '../views/admin/Crops.vue'
import AdminCropForm from '../views/admin/CropForm.vue'
import AdminProducts from '../views/admin/Products.vue'
import AdminOrders from '../views/admin/Orders.vue'
import AdminNotifications from '../views/admin/Notifications.vue'
import AdminNotificationDetail from '../views/admin/NotificationDetail.vue'
import AdminOrderDetail from '../views/admin/OrderDetail.vue'
import AdminAccountForm from '../views/admin/AccountForm.vue'
import AdminAccountDetail from '../views/admin/AccountDetail.vue'
import AdminFarmDetail from '../views/admin/FarmDetail.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: GuestLayout,
      children: [
        { path: '', component: Home, name: 'home' }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: Login
    },
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        { path: '', redirect: { name: 'admin-dashboard' } },
        { path: 'dashboard', component: AdminDashboard, name: 'admin-dashboard' },
        { path: 'accounts', component: AdminAccounts, name: 'admin-accounts' },
        { path: 'accounts/create', component: AdminAccountForm, name: 'admin-account-create' },
        { path: 'accounts/:id', component: AdminAccountDetail, name: 'admin-account-detail' },
        { path: 'accounts/:id/edit', component: AdminAccountForm, name: 'admin-account-edit' },
        { path: 'farms', component: AdminFarms, name: 'admin-farms' },
        { path: 'farms/:id', component: AdminFarmDetail, name: 'admin-farm-detail' },
        { path: 'crops', component: AdminCrops, name: 'admin-crops' },
        { path: 'crops/create', component: AdminCropForm, name: 'admin-crop-create' },
        { path: 'crops/:id/edit', component: AdminCropForm, name: 'admin-crop-edit' },
        { path: 'products', component: AdminProducts, name: 'admin-products' },
        { path: 'orders', component: AdminOrders, name: 'admin-orders' },
        { path: 'orders/:id', component: AdminOrderDetail, name: 'admin-order-detail' },
        { path: 'orders/pending-completion', component: AdminOrders, props: { pendingOnly: true }, name: 'admin-orders-pending' },
        { path: 'notifications', component: AdminNotifications, name: 'admin-notifications' },
        { path: 'notifications/:id', component: AdminNotificationDetail, name: 'admin-notification-detail' },
        { path: 'transactions', component: AdminTransactions, name: 'admin-transactions' }
      ]
    }
  ]
})

router.beforeEach((to) => {
  if (!to.path.startsWith('/admin') || to.name === 'admin-login') return true
  const user = JSON.parse(localStorage.getItem('bicap_user') || 'null')
  if (localStorage.getItem('bicap_token') && user?.role === 'ADMIN') return true
  return { name: 'admin-login' }
})

export default router
