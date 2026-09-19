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
import AppHeader from './AppHeader.vue';
import AppSidebar from './AppSidebar.vue';

const menuItems = ref([
  { label: 'Dashboard', path: '/farm' },
  { label: 'Hồ sơ', path: '/farm/profile' },
  { label: 'Mùa vụ', path: '/farm/seasons' },
  { label: 'Hoạt động', path: '/farm/activities' },
  { label: 'Lô hàng', path: '/farm/batches' },
  { label: 'Đơn hàng', path: '/farm/orders' }
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
