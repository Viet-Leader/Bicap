<template>
  <div class="g-sidenav-show bg-gray-100 min-vh-100">
    <aside class="sidenav navbar navbar-vertical navbar-expand-xs border-radius-lg fixed-start ms-2 bg-white my-2" id="sidenav-main">
      <div class="sidenav-header">
        <i class="fas fa-times p-3 cursor-pointer text-dark opacity-5 position-absolute end-0 top-0 d-none d-xl-none" aria-hidden="true" id="iconSidenav"></i>
        <router-link class="navbar-brand px-4 py-3 m-0" to="/farm">
          <div class="d-flex align-items-center">
            <i class="material-symbols-rounded text-success me-2" style="font-size: 28px;">agriculture</i>
            <span class="ms-1 text-sm text-dark font-weight-bolder">BICAP Farm</span>
          </div>
        </router-link>
      </div>
      <hr class="horizontal dark mt-0 mb-2">
      <div class="navbar-collapse w-auto d-block" id="sidenav-collapse-main" style="height: auto !important; overflow-y: auto;">
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link text-dark" exact-active-class="active bg-gradient-dark text-white" to="/farm">
              <i class="material-symbols-rounded opacity-5">dashboard</i>
              <span class="nav-link-text ms-1">Dashboard</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/farm/products">
              <i class="material-symbols-rounded opacity-5">storefront</i>
              <span class="nav-link-text ms-1">Product Management</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/farm/seasons">
              <i class="material-symbols-rounded opacity-5">grass</i>
              <span class="nav-link-text ms-1">Season Monitor</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/farm/farm-info">
              <i class="material-symbols-rounded opacity-5">agriculture</i>
              <span class="nav-link-text ms-1">Farm Information</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/farm/notifications">
              <i class="material-symbols-rounded opacity-5">notifications</i>
              <span class="nav-link-text ms-1">Notifications</span>
            </router-link>
          </li>

          <li class="nav-item mt-3">
            <h6 class="ps-4 ms-2 text-uppercase text-xs text-dark font-weight-bolder opacity-5">Account</h6>
          </li>

          <li class="nav-item">
            <a class="nav-link text-dark" href="#" @click.prevent="handleLogout">
              <i class="material-symbols-rounded opacity-5">logout</i>
              <span class="nav-link-text ms-1">Logout</span>
            </a>
          </li>
        </ul>
      </div>
      <div class="sidenav-footer position-absolute w-100 bottom-0">
        <div class="mx-3 mb-3">
          <div class="card bg-gradient-success shadow-sm" style="border-radius: 15px; background: linear-gradient(135deg, #43A047 0%, #2E7D32 100%);">
            <div class="card-body p-3 text-center text-white">
              <i class="material-symbols-rounded mb-2" style="font-size: 32px; opacity: 1; color: white;">agriculture</i>
              <p class="text-sm mb-0 font-weight-bold" style="color: white;">BICAP Farm</p>
              <p class="text-xs mb-0 opacity-8" style="color: white;">Management System</p>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg">
      <!-- Navbar -->
      <nav class="navbar navbar-main navbar-expand-lg px-0 mx-3 shadow-none border-radius-xl" id="navbarBlur" data-scroll="true">
        <div class="container-fluid py-1 px-3">
          <nav aria-label="breadcrumb">
            <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
              <li class="breadcrumb-item text-sm"><a class="opacity-5 text-dark" href="javascript:;">Farm</a></li>
              <li class="breadcrumb-item text-sm text-dark active" aria-current="page">{{ currentRouteName }}</li>
            </ol>
            <h6 class="font-weight-bolder mb-0">{{ currentRouteName }}</h6>
          </nav>
        </div>
      </nav>

      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

const currentRouteName = computed(() => {
  const map = {
    '/farm': 'Dashboard',
    '/farm/profile': 'Profile',
    '/farm/seasons': 'Season Monitor',
    '/farm/orders': 'Orders',
    '/farm/products': 'Product Management',
    '/farm/farm-info': 'Farm Information',
    '/farm/notifications': 'Notifications'
  };
  return map[route.path] || 'Farm Management';
});

const handleLogout = () => {
  auth.logout();
  router.push('/login');
};

const cssLinks = [
  '/admin-assets/css/nucleo-icons.css',
  '/admin-assets/css/nucleo-svg.css',
  '/admin-assets/css/material-dashboard.css?v=3.2.0'
];

onMounted(() => {
  cssLinks.forEach(href => {
    if (!document.querySelector(`link[href="${href}"]`)) {
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = href;
      link.dataset.adminCss = 'true';
      document.head.appendChild(link);
    }
  });
});

onUnmounted(() => {
  document.querySelectorAll('link[data-admin-css="true"]').forEach(el => el.remove());
});
</script>

<style scoped>
.nav-link.active .opacity-5 {
  opacity: 1 !important;
}
</style>
