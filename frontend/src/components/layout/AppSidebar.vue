<template>
  <aside class="app-sidebar">
    <nav class="nav-menu">
      <router-link 
        v-for="item in menuItems" 
        :key="item.path" 
        :to="item.path" 
        class="nav-item"
        active-class="active"
      >
        <component :is="item.icon" class="icon" v-if="item.icon" />
        <span>{{ item.label }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
defineProps({
  menuItems: { type: Array, required: true }
});
</script>

<style scoped lang="scss">
.app-sidebar {
  width: 240px;
  height: calc(100vh - 64px);
  background: rgba(15, 22, 41, 0.95);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  padding: $space-4 0;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 64px;
}
.nav-menu {
  display: flex;
  flex-direction: column;
  gap: $space-2;
  padding: 0 $space-3;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: $space-3;
  padding: 12px 16px;
  color: $text-secondary;
  border-radius: $radius-md;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.05);
    color: $text-primary;
  }
  
  &.active {
    background: rgba($primary-green, 0.1);
    color: $primary-green;
    border-left: 3px solid $primary-green;
  }
}
.icon {
  width: 20px;
  height: 20px;
}
</style>
