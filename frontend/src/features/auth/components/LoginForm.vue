<template>
<div class="flex flex-col w-full h-full items-center justify-center p-md bg-tertiary">
<div class="w-full max-w-md bg-surface-container-lowest border border-secondary p-lg relative overflow-hidden">
<div class="absolute -top-16 -right-16 w-32 h-32 bg-primary-container opacity-10 blur-2xl rounded-full"></div>
<div class="absolute -bottom-16 -left-16 w-32 h-32 bg-primary-container opacity-10 blur-2xl rounded-full"></div>
<div class="mb-xl relative z-10">
<h1 class="font-headline-lg text-headline-lg text-on-surface mb-xs">BICAP</h1>
<p class="font-body-md text-body-md text-on-surface-variant">Chuỗi cung ứng nông sản sạch qua blockchain.</p>
</div>
<div class="mb-lg border-2 border-error bg-surface-container-lowest p-sm flex items-start gap-sm relative z-10">
<span class="material-symbols-outlined text-error" style="font-variation-settings: 'FILL' 1;">error</span>
<p class="font-body-md text-body-md text-error font-bold">Email không đúng định dạng</p>
</div>
<form @submit.prevent="handleLogin" class="flex flex-col gap-md relative z-10">
<div class="flex flex-col gap-base">
<label class="font-label-sm text-label-sm text-on-surface-variant uppercase" for="email">Email</label>
<input class="w-full bg-surface-container-lowest border-2 border-error p-sm font-body-md text-body-md text-on-surface focus:outline-none transition-all" id="email" placeholder="nhap.email@vidu.com" type="email" v-model="username"/>
</div>
<div class="flex flex-col gap-base">
<label class="font-label-sm text-label-sm text-on-surface-variant uppercase" for="password">Mật khẩu</label>
<div class="relative">
<input class="w-full bg-surface-container-lowest border border-secondary p-sm font-body-md text-body-md text-on-surface focus:outline-none focus:border-2 focus:border-secondary focus:ring-4 focus:ring-primary-container/30 transition-all" id="password" placeholder="••••••••" type="password"/v-model="password">
<button class="absolute right-sm top-1/2 -translate-y-1/2 text-on-surface-variant hover:text-primary transition-colors" type="button">
<span class="material-symbols-outlined">visibility_off</span>
</button>
</div>
</div>
<div class="flex justify-between items-center mt-sm">
<label class="flex items-center gap-xs cursor-pointer group">
<input class="appearance-none w-4 h-4 border border-secondary checked:bg-primary-container checked:border-secondary flex items-center justify-center transition-colors after:content-[''] after:w-2 after:h-2 after:bg-secondary after:hidden checked:after:block" type="checkbox"/v-model="remember">
<span class="font-body-md text-body-md text-on-surface-variant group-hover:text-on-surface transition-colors">Ghi nhớ đăng nhập</span>
</label>
<a class="font-body-md text-body-md text-primary hover:text-on-primary-container transition-colors underline decoration-1 underline-offset-4" href="#">Quên mật khẩu?</a>
</div>
<button class="mt-lg w-full bg-primary-container text-on-secondary-fixed border-2 border-secondary py-sm px-md font-button text-button hover:bg-surface-container-lowest transition-colors flex items-center justify-center gap-xs group" type="button">
        Đăng nhập
        <span class="material-symbols-outlined group-hover:translate-x-1 transition-transform">arrow_forward</span>
</button>
</form>
<div class="mt-xl pt-md border-t border-secondary text-center relative z-10">
<p class="font-body-md text-body-md text-on-surface-variant">
        Chưa có tài khoản? <a class="text-primary font-bold hover:underline decoration-2 underline-offset-4" href="#">Đăng ký ngay</a>
</p>
</div>
</div>
<div class="mt-lg flex items-center gap-sm text-on-surface-variant opacity-70">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">verified</span>
<span class="font-label-sm text-label-sm uppercase">Hệ thống minh bạch 100%</span>
</div>
</div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const remember = ref(false)
const errors = ref({})
const isSubmitting = ref(false)

const handleLogin = async () => {
  errors.value = {}
  if (!username.value) errors.value.username = 'Network ID is required'
  if (!password.value) errors.value.password = 'Passphrase is required'

  if (Object.keys(errors.value).length > 0) return

  isSubmitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 800));
    
    let role = 'FARM';
    if (username.value === 'admin' || username.value.includes('admin')) role = 'ADMIN';
    else if (username.value.includes('retailer')) role = 'RETAILER';
    
    auth.login({ username: username.value, role });

    const redirectPath = router.currentRoute.value.query.redirect;
    if (typeof redirectPath === 'string') {
      router.push(redirectPath)
    } else if (auth.isAdmin) {
      router.push('/admin')
    } else if (auth.role === 'FARM') {
      router.push('/farm')
    } else if (auth.role === 'RETAILER') {
      router.push('/retailer')
    } else {
      router.push('/')
    }
  } catch (error) {
    errors.value.form = error.response?.data?.message || 'Unable to authenticate with the network'
  } finally {
    isSubmitting.value = false
  }
}
</script>
