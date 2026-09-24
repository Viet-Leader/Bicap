<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();
const isSidebarCollapsed = ref(false);

const pageTitle = computed(() => {
  const map = {
    '/retailer': 'Marketplace',
    '/retailer/products': 'Sản phẩm',
    '/retailer/cart': 'Giỏ hàng',
    '/retailer/orders': 'Đơn hàng của tôi'
  };
  return map[route.path] || 'Trang Retailer';
});

const handleLogout = () => {
  auth.logout();
  router.push('/login');
};

const cssLinks = [
  'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css',
  '/retailer-assets/css/theme.css'
];

onMounted(() => {
  cssLinks.forEach(href => {
    if (!document.querySelector(`link[href="${href}"]`)) {
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = href;
      link.dataset.retailerCss = 'true';
      document.head.appendChild(link);
    }
  });
});

onUnmounted(() => {
  document.querySelectorAll('link[data-retailer-css="true"]').forEach(el => el.remove());
});
</script>

<template>
  <div class="app">
    <!-- SIDEBAR -->
    <aside class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }" id="sidebar">
      <div class="sidebar-top">
        <span class="logo" v-if="!isSidebarCollapsed">BICAP</span>
        <span class="logo" v-else>BC</span>
        <button class="toggle-btn" @click="isSidebarCollapsed = !isSidebarCollapsed" title="Thu / mở menu">
          <i class="fas fa-bars"></i>
        </button>
      </div>

      <nav class="menu">
        <router-link to="/retailer" class="menu-item" data-tooltip="Marketplace" :class="{ active: route.path === '/retailer' }">
          <i class="fas fa-store"></i>
          <span>Marketplace</span>
        </router-link>

        <router-link to="/retailer/orders" class="menu-item" data-tooltip="Đơn hàng" :class="{ active: route.path === '/retailer/orders' }">
          <i class="fas fa-box"></i>
          <span>Đơn hàng</span>
        </router-link>

        <router-link to="/retailer/cart" class="menu-item" data-tooltip="Giỏ hàng" :class="{ active: route.path === '/retailer/cart' }">
          <i class="fas fa-shopping-cart"></i>
          <span>Giỏ hàng</span>
        </router-link>

        <button type="button" class="menu-item logout-menu-item" data-tooltip="Đăng xuất" @click="handleLogout">
          <i class="fas fa-right-from-bracket"></i>
          <span>Đăng xuất</span>
        </button>
      </nav>

      <div class="user group hover:cursor-pointer" id="userMenu">
        <div class="user-info">
          <div class="avatar">
            {{ auth.user?.username?.charAt(0).toUpperCase() || 'U' }}
          </div>
          <div class="user-text" v-if="!isSidebarCollapsed">
            <strong>{{ auth.user?.username }}</strong>
            <small>Retailer</small>
          </div>
        </div>
        <div class="user-dropdown d-none group-hover:block" style="display: none;">
          <button type="button" @click="handleLogout">Đăng xuất</button>
        </div>
      </div>
    </aside>

    <!-- MAIN -->
    <div class="main">
      <header class="header">
        <div class="header-left">
          <h2>{{ pageTitle }}</h2>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
/* Optional specific overrides if needed, but styling is driven by theme.css */
</style>
