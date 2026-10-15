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
/* ===== LAYOUT ===== */
.app {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: #f0f4f8;
  font-family: 'Inter', 'Segoe UI', sans-serif;
}

/* ===== SIDEBAR ===== */
.sidebar {
  width: 240px;
  background: linear-gradient(160deg, #1a5276 0%, #1f618d 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
  overflow: hidden;
  box-shadow: 2px 0 12px rgba(0,0,0,0.15);
}
.sidebar.collapsed {
  width: 68px;
}
.sidebar-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo {
  color: #fff;
  font-weight: 800;
  font-size: 1.1rem;
  letter-spacing: 2px;
  white-space: nowrap;
  overflow: hidden;
}
.toggle-btn {
  background: rgba(255,255,255,0.15);
  border: none;
  color: #fff;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  flex-shrink: 0;
}
.toggle-btn:hover { background: rgba(255,255,255,0.25); }

/* ===== MENU ===== */
.menu {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow-y: auto;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 10px;
  color: rgba(255,255,255,0.75);
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
}
.menu-item i { font-size: 1rem; flex-shrink: 0; min-width: 20px; text-align: center; }
.menu-item span { overflow: hidden; text-overflow: ellipsis; }
.menu-item:hover, .menu-item.active {
  background: rgba(255,255,255,0.15);
  color: #fff;
}
.menu-item.active {
  background: rgba(255,255,255,0.2);
  box-shadow: inset 3px 0 0 #48bb78;
}
.logout-menu-item { color: rgba(255,150,150,0.85); }
.logout-menu-item:hover { background: rgba(229,62,62,0.2); color: #fc8181; }

/* ===== USER ===== */
.user {
  padding: 12px 16px;
  border-top: 1px solid rgba(255,255,255,0.1);
  position: relative;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.9rem;
  flex-shrink: 0;
}
.user-text { overflow: hidden; }
.user-text strong { display: block; color: #fff; font-size: 0.85rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.user-text small { color: rgba(255,255,255,0.5); font-size: 0.72rem; }

/* ===== MAIN ===== */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
}
.header-left h2 {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}
.content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
.content-inner {
  max-width: 1200px;
  margin: 0 auto;
}

/* ===== SIDEBAR TOOLTIP (collapsed) ===== */
.sidebar.collapsed .menu-item {
  position: relative;
  justify-content: center;
  padding: 10px;
}
.sidebar.collapsed .menu-item span { display: none; }
.sidebar.collapsed .menu-item[data-tooltip]:hover::after {
  content: attr(data-tooltip);
  position: absolute;
  left: calc(100% + 8px);
  top: 50%;
  transform: translateY(-50%);
  background: #1a202c;
  color: #fff;
  padding: 5px 10px;
  border-radius: 6px;
  font-size: 0.78rem;
  white-space: nowrap;
  z-index: 100;
  pointer-events: none;
}
.sidebar.collapsed .logo { display: none; }
</style>
