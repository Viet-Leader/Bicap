<template>
  <div class="home-view container flex flex-col items-center justify-center">
    <h1 class="text-green text-3xl font-bold mt-4">Welcome to BICAP</h1>
    <p class="text-muted mt-4">Blockchain Integration in Clean Agricultural Production</p>
    <AppButton class="mt-4" @click="$router.push('/login')">Đăng nhập</AppButton>
  </div>
</template>

<script setup>
import AppButton from '@/components/common/AppButton.vue';
</script>

<style scoped>
.home-view {
  min-height: calc(100vh - 64px);
}
</style>
