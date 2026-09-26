<template>
  <div class="login-view container flex items-center justify-center">
    <GlassCard variant="elevated" style="width: 400px; max-width: 100%;" glow>
      <h2 class="text-center text-cyan mb-4">Đăng nhập</h2>
      <AppInput v-model="email" label="Email" placeholder="admin@bicap.vn" class="mb-4" />
      <AppInput v-model="password" label="Mật khẩu" type="password" placeholder="********" class="mb-4" />
      <AppButton block @click="handleLogin">Đăng nhập</AppButton>
    </GlassCard>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import GlassCard from '@/components/common/GlassCard.vue';
import AppInput from '@/components/common/AppInput.vue';
import AppButton from '@/components/common/AppButton.vue';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const auth = useAuthStore();
const email = ref('');
const password = ref('');

const handleLogin = async () => {
  if (!email.value.trim() || !password.value.trim()) return;

  try {
    await auth.login(email.value, password.value);
    router.push(auth.isAdmin ? '/admin' : auth.isFarm ? '/farm' : '/retailer');
  } catch {
    // The active login view displays the API error; this legacy view only prevents navigation.
  }
};
</script>

<style scoped>
.login-view {
  min-height: calc(100vh - 64px);
}
</style>
