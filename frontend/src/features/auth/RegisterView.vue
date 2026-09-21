<template>
  <div class="min-h-screen flex items-center justify-center p-4 relative overflow-hidden bg-primary">
    <!-- Abstract tech background elements -->
    <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-5 pointer-events-none"></div>
    <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-primary-green/10 rounded-full blur-[100px] pointer-events-none"></div>
    <div class="absolute bottom-1/4 right-1/4 w-96 h-96 bg-primary-cyan/10 rounded-full blur-[100px] pointer-events-none"></div>

    <div class="w-full max-w-md relative z-10">
      <router-link to="/" class="inline-flex items-center gap-2 text-sm font-mono text-gray-400 hover:text-white mb-6 transition-colors">
        <span>&larr;</span> Trở về Trang chủ
      </router-link>
      
      <GlassCard glow class="p-8">
        <div class="flex items-center gap-3 mb-8 justify-center">
          <div class="w-10 h-10 bg-primary-green/20 rounded border border-primary-green/50 flex items-center justify-center font-bold text-2xl text-primary-green shadow-[0_0_15px_rgba(0,255,136,0.3)]">B</div>
          <span class="text-2xl font-bold tracking-widest uppercase text-white">BICAP</span>
        </div>

        <h2 class="text-2xl font-bold text-white mb-6 text-center">Đăng ký tham gia chuỗi</h2>

        <form @submit.prevent="handleRegister" class="space-y-4">
          <AppInput
            v-model="form.name"
            label="Tên tổ chức/cá nhân"
            placeholder="Ví dụ: Trang trại Xoài Cát Hòa Lộc"
            required
          />
          <AppInput
            v-model="form.username"
            label="Tên đăng nhập"
            placeholder="Username"
            required
          />
          <AppInput
            v-model="form.password"
            label="Mật khẩu"
            type="password"
            placeholder="••••••••••••"
            required
          />
          
          <div class="pt-2">
            <label class="block text-sm font-mono text-gray-400 mb-2">Vai trò tham gia</label>
            <div class="flex gap-4">
              <label class="flex items-center gap-2 text-white cursor-pointer">
                <input type="radio" v-model="form.role" value="FARM" class="accent-primary-green" /> Trang trại
              </label>
              <label class="flex items-center gap-2 text-white cursor-pointer">
                <input type="radio" v-model="form.role" value="RETAILER" class="accent-primary-cyan" /> Nhà bán lẻ
              </label>
            </div>
          </div>

          <AppButton type="submit" variant="primary" class="w-full mt-6" :disabled="isSubmitting" block>
            {{ isSubmitting ? 'Đang đăng ký...' : 'Đăng ký ngay' }}
          </AppButton>
        </form>
        
        <div class="mt-6 text-center text-sm text-gray-400">
          Đã có tài khoản? 
          <router-link to="/login" class="text-primary-green hover:underline">Đăng nhập</router-link>
        </div>
      </GlassCard>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import GlassCard from '@/components/common/GlassCard.vue'
import AppInput from '@/components/common/AppInput.vue'
import AppButton from '@/components/common/AppButton.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = ref({
  name: '',
  username: '',
  password: '',
  role: 'FARM'
})
const isSubmitting = ref(false)

const handleRegister = async () => {
  isSubmitting.value = true
  try {
    // Mock API delay
    await new Promise(resolve => setTimeout(resolve, 800))
    // Auto login after register
    auth.login({ username: form.value.username, role: form.value.role })
    
    if (form.value.role === 'FARM') router.push('/farm')
    else router.push('/retailer')
  } catch (error) {
    console.error(error)
  } finally {
    isSubmitting.value = false
  }
}
</script>
