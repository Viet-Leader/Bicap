<template>
  <div class="g-sidenav-show bg-gray-100 min-vh-100">
    <aside class="sidenav navbar navbar-vertical navbar-expand-xs border-radius-lg fixed-start ms-2 bg-white my-2" id="sidenav-main">
      <div class="sidenav-header">
        <i class="fas fa-times p-3 cursor-pointer text-dark opacity-5 position-absolute end-0 top-0 d-none d-xl-none" aria-hidden="true" id="iconSidenav"></i>
        <router-link class="navbar-brand px-4 py-3 m-0" to="/admin">
          <span class="material-symbols-rounded text-success me-2 align-middle">eco</span>
          <span class="ms-1 text-sm text-dark font-weight-bold">BiCap Admin</span>
        </router-link>
      </div>
      <hr class="horizontal dark mt-0 mb-2">
      <div class="navbar-collapse w-auto d-block" id="sidenav-collapse-main" style="height: auto !important; overflow-y: auto;">
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/dashboard">
              <i class="material-symbols-rounded opacity-5">admin_panel_settings</i>
              <span class="nav-link-text ms-1">Xét duyệt quyền</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/accounts">
              <i class="material-symbols-rounded opacity-5">group</i>
              <span class="nav-link-text ms-1">Quản lý User</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/farms">
              <i class="material-symbols-rounded opacity-5">agriculture</i>
              <span class="nav-link-text ms-1">Giám sát Farm</span>
            </router-link>
          </li>
          <li class="nav-item">
              <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/crops">
                  <i class="material-symbols-rounded opacity-5">category</i>
                  <span class="nav-link-text ms-1">Quản lý Danh mục</span>
              </router-link>
          </li>
          <li class="nav-item">
              <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/products">
                  <i class="material-symbols-rounded opacity-5">inventory_2</i>
                  <span class="nav-link-text ms-1">Giám sát Sản phẩm</span>
              </router-link>
          </li>
          <li class="nav-item">
              <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/orders">
                  <i class="material-symbols-rounded opacity-5">shopping_cart</i>
                  <span class="nav-link-text ms-1">Quản lý Đơn hàng</span>
              </router-link>
          </li>
            <li class="nav-item">
              <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/transactions">
                <i class="material-symbols-rounded opacity-5">account_balance</i>
                <span class="nav-link-text ms-1">Giao dịch</span>
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link text-dark" active-class="active bg-gradient-dark text-white" to="/admin/notifications">
                <i class="material-symbols-rounded opacity-5">notifications</i>
                <span class="nav-link-text ms-1">Thông báo</span>
              </router-link>
            </li>
          <li class="nav-item mt-3">
            <h6 class="ps-4 ms-2 text-uppercase text-xs text-dark font-weight-bolder opacity-5">Hệ thống</h6>
          </li>
          <li class="nav-item">
            <a class="nav-link text-dark" href="#" @click.prevent="handleLogout">
              <i class="material-symbols-rounded opacity-5">logout</i>
              <span class="nav-link-text ms-1">Đăng xuất</span>
            </a>
          </li>
        </ul>
      </div>
    </aside>

    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg">
      <nav class="navbar navbar-main navbar-expand-lg px-0 mx-3 shadow-none border-radius-xl" id="navbarBlur" data-scroll="true">
        <div class="container-fluid py-1 px-3">
          <nav aria-label="breadcrumb">
            <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
              <li class="breadcrumb-item text-sm"><a class="opacity-5 text-dark" href="javascript:;">Admin</a></li>
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
    '/admin/dashboard': 'Xét duyệt hồ sơ đăng ký',
    '/admin/accounts': 'Quản lý Người dùng',
    '/admin/farms': 'Giám sát Trang trại',
    '/admin/crops': 'Quản lý Danh mục',
    '/admin/products': 'Giám sát Sản phẩm',
    '/admin/orders': 'Quản lý Đơn hàng',
    '/admin/transactions': 'Xét duyệt giao dịch',
    '/admin/notifications': 'Thông báo hệ thống',
  };
  return map[route.path] || 'Bảng điều khiển';
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
</style>



