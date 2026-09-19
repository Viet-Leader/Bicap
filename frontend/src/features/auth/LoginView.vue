<template>
  <div class="login-view container flex items-center justify-center">
    <GlassCard variant="elevated" style="width: 400px; max-width: 100%;" glow>
      <h2 class="text-center text-cyan mb-4">Đăng nhập</h2>
      <AppInput label="Email" placeholder="admin@bicap.vn" class="mb-4" />
      <AppInput label="Mật khẩu" type="password" placeholder="********" class="mb-4" />
      <AppButton block @click="handleLogin">Đăng nhập</AppButton>
    </GlassCard>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import GlassCard from '@/components/common/GlassCard.vue';
import AppInput from '@/components/common/AppInput.vue';
import AppButton from '@/components/common/AppButton.vue';

const router = useRouter();

const handleLogin = () => {
  // Mock login and redirect
  router.push('/admin');
};
</script>

<style scoped>
.login-view {
  min-height: calc(100vh - 64px);
}
</style>
