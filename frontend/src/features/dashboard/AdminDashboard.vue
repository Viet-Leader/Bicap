<template>
  <div class="admin-dashboard">
    <h1 class="text-xl font-bold mb-4">Admin Dashboard</h1>
    <div class="flex gap-4">
      <GlassCard glow>
        <h3 class="text-muted">Tổng số tài khoản</h3>
        <p class="text-cyan text-3xl font-bold">245</p>
      </GlassCard>
      <GlassCard>
        <h3 class="text-muted">Trang trại</h3>
        <p class="text-green text-3xl font-bold">32</p>
      </GlassCard>
    </div>
  </div>
</template>

<script setup>
import GlassCard from '@/components/common/GlassCard.vue';
</script>
