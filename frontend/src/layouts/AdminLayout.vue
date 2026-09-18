<template>
  <div class="flex min-h-screen bg-transparent text-gray-100">
    <aside class="w-64 glass-panel m-4 rounded-2xl flex flex-col shrink-0">
      <div class="p-6 border-b border-white/10"><span class="text-xl font-bold tracking-widest">BICAP QUẢN TRỊ</span></div>
      <nav class="p-3 space-y-1 flex-1">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to" class="block rounded-lg px-4 py-3 text-sm font-medium" :class="isActive(item) ? 'bg-primary/20 text-primary' : 'text-gray-300 hover:bg-white/10'">{{ item.label }}</router-link>
      </nav>
      <div class="p-4 border-t border-white/10"><button class="w-full text-left rounded-lg px-4 py-3 text-gray-300 hover:bg-white/10" @click="logout">Đăng xuất</button></div>
    </aside>
    <div class="min-w-0 flex-1 p-4 pl-0">
      <header class="glass-panel rounded-2xl h-16 px-6 flex justify-between items-center mb-4"><h1 class="font-bold">{{ title }}</h1><div class="text-sm text-gray-300">{{ auth.user?.username || 'Quản trị viên' }}</div></header>
      <main class="glass-panel rounded-2xl min-h-[calc(100vh-6rem)] p-6"><router-view /></main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const navItems = [
  { label: 'Tổng quan', to: '/admin/dashboard' }, { label: 'Tài khoản', to: '/admin/accounts' },
  { label: 'Trang trại', to: '/admin/farms' }, { label: 'Cây trồng', to: '/admin/crops' },
  { label: 'Sản phẩm', to: '/admin/products' }, { label: 'Tất cả đơn hàng', to: '/admin/orders' },
  { label: 'Chờ hoàn tất', to: '/admin/orders/pending-completion' }, { label: 'Thông báo', to: '/admin/notifications' },
]
const isActive = (item) => route.path === item.to
const title = computed(() => navItems.find((item) => isActive(item))?.label || 'Quản trị')
const logout = () => { auth.signOut(); router.push({ name: 'admin-login' }) }
</script>
