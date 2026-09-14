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
      path: '/admin',
      component: AdminLayout,
      children: [
        { path: '', component: AdminDashboard, name: 'admin-dashboard' },
        { path: 'accounts', component: AdminAccounts, name: 'admin-accounts' },
        { path: 'farms', component: AdminFarms, name: 'admin-farms' },
        { path: 'transactions', component: AdminTransactions, name: 'admin-transactions' }
      ]
    }
  ]
})

export default router
