const fs = require('fs');
const loginHtml = fs.readFileSync('login_main.html', 'utf8');
const vueComponent = `<template>
${loginHtml
  .replace(/<form(.*?)>/, '<form @submit.prevent="handleLogin"$1>')
  .replace(/<input(.*?)id="email"(.*?)value=".*?"(.*?)>/, '<input$1id="email"$2v-model="username"$3>')
  .replace(/<input(.*?)id="password"(.*?)>/, '<input$1id="password"$2v-model="password">')
  .replace(/<input(.*?)type="checkbox"(.*?)>/, '<input$1type="checkbox"$2v-model="remember">')
  .replace(/<button(.*?)type="button"(.*?)Đăng nhập/, '<button$1type="submit"$2{{ isSubmitting ? "Đang xử lý..." : "Đăng nhập" }}')
}
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
`;
fs.writeFileSync('src/features/auth/components/LoginForm.vue', vueComponent);
