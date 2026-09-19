<template>
  <div class="layout-container">
    <AppHeader>
      <template #actions>
        <router-link to="/retailer/cart" class="text-cyan">🛒 Giỏ hàng</router-link>
      </template>
    </AppHeader>
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
  { label: 'Chợ nông sản', path: '/retailer' },
  { label: 'Sản phẩm', path: '/retailer/products' },
  { label: 'Đơn hàng của tôi', path: '/retailer/orders' }
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
