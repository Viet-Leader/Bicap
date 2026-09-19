<template>
  <div class="layout-container">
    <AppHeader />
    <div class="layout-body">
      <AppSidebar :menuItems="menuItems" />
      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { LayoutDashboard, Users, Map, Wheat, Package, Activity } from 'lucide-vue-next'; // fallback to standard icons or vue next, wait, the package is @lucide/vue
import AppHeader from './AppHeader.vue';
import AppSidebar from './AppSidebar.vue';

// Use basic string labels if icons fail, or use dynamic components
const menuItems = ref([
  { label: 'Dashboard', path: '/admin', icon: 'LayoutDashboard' },
  { label: 'Tài khoản', path: '/admin/accounts', icon: 'Users' },
  { label: 'Trang trại', path: '/admin/farms', icon: 'Map' },
  { label: 'Cây trồng', path: '/admin/crops', icon: 'Wheat' },
  { label: 'Sản phẩm', path: '/admin/products', icon: 'Package' },
  { label: 'Giao dịch', path: '/admin/transactions', icon: 'Activity' }
]);
</script>

<style scoped lang="scss">
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.layout-body {
  display: flex;
  flex: 1;
}
.layout-content {
  flex: 1;
  padding: $space-5;
  overflow-y: auto;
}
</style>
