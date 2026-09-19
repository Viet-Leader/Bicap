<template>
  <div class="w-full max-w-md p-8 glass-panel rounded-2xl relative overflow-hidden">
    <!-- Decorative background glow -->
    <div class="absolute -top-10 -right-10 w-40 h-40 bg-primary/20 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute -bottom-10 -left-10 w-40 h-40 bg-accent/20 rounded-full blur-3xl pointer-events-none"></div>

    <div class="relative z-10">
      <div class="flex items-center gap-3 mb-8 justify-center">
        <div class="w-10 h-10 bg-primary/20 rounded border border-primary/50 flex items-center justify-center font-bold text-2xl text-primary shadow-[0_0_15px_rgba(11,218,81,0.3)]">B</div>
        <span class="text-2xl font-bold tracking-widest uppercase text-white">BICAP</span>
      </div>

      <h2 class="text-2xl font-bold text-white mb-6 text-center">Đăng nhập quản trị</h2>

      <form @submit.prevent="handleLogin" class="space-y-5">
        <Input
          v-model="username"
          label="Tên đăng nhập"
          placeholder="Nhập tên đăng nhập"
          :error="errors.username"
        />
        
        <Input
          v-model="password"
          label="Mật khẩu"
          type="password"
          placeholder="••••••••••••"
          :error="errors.password"
        />

        <div class="flex items-center justify-between mt-2">
          <ToggleSwitch v-model="remember" label="Duy trì đăng nhập" />
          <a href="#" class="text-sm font-mono text-primary hover:underline">Quên mật khẩu?</a>
        </div>

        <p v-if="errors.form" class="text-sm text-error">{{ errors.form }}</p>
        <Button type="submit" variant="primary" class="w-full mt-4" size="lg" :disabled="submitting">{{ submitting ? 'Đang đăng nhập...' : 'Đăng nhập' }}</Button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'
import { adminApi } from '../../../api/adminService'
import Input from '../../../components/ui/Input.vue'
import Button from '../../../components/ui/Button.vue'
import ToggleSwitch from '../../../components/ui/ToggleSwitch.vue'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const remember = ref(false)
const errors = ref({})
const submitting = ref(false)

const handleLogin = async () => {
  errors.value = {}
  if (!username.value) errors.value.username = 'Vui lòng nhập tên đăng nhập.'
  if (!password.value) errors.value.password = 'Vui lòng nhập mật khẩu.'
  
  if (Object.keys(errors.value).length) return
  submitting.value = true
  try {
    const { data } = await adminApi.login({ username: username.value, password: password.value })
    if (data.role !== 'ADMIN') {
      errors.value.form = 'Tài khoản này không có quyền quản trị.'
      return
    }
    auth.setToken(data.accessToken)
    auth.setUser({ username: data.username, role: data.role })
    router.push({ name: 'admin-dashboard' })
  } catch (error) {
    errors.value.form = error.response?.data?.message || 'Đăng nhập không thành công.'
  } finally {
    submitting.value = false
  }
}
</script>
