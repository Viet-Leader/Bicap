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

      <h2 class="text-2xl font-bold text-white mb-6 text-center">Node Authentication</h2>

      <p v-if="errors.form" class="mb-4 rounded border border-error/40 bg-error/10 px-3 py-2 text-sm text-red-300">
        {{ errors.form }}
      </p>

      <form @submit.prevent="handleLogin" class="space-y-5">
        <Input
          v-model="username"
          label="Network ID / Username"
          placeholder="Enter your assigned ID"
          :error="errors.username"
        />
        
        <Input
          v-model="password"
          label="Passphrase"
          type="password"
          placeholder="••••••••••••"
          :error="errors.password"
        />

        <div class="flex items-center justify-between mt-2">
          <ToggleSwitch v-model="remember" label="Stay connected" />
          <a href="#" class="text-sm font-mono text-primary hover:underline">Forgot key?</a>
        </div>

        <Button type="submit" variant="primary" class="w-full mt-4" size="lg" :disabled="isSubmitting">
          {{ isSubmitting ? 'Authenticating...' : 'Authenticate' }}
        </Button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'
import axiosClient from '../../../api/axiosClient'
import Input from '../../../components/ui/Input.vue'
import Button from '../../../components/ui/Button.vue'
import ToggleSwitch from '../../../components/ui/ToggleSwitch.vue'

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
    const { data } = await axiosClient.post('/auth/login', {
      username: username.value,
      password: password.value,
    })
    auth.setSession(data)
    const redirectPath = router.currentRoute.value.query.redirect
    if (typeof redirectPath === 'string') {
      router.push(redirectPath)
    } else if (auth.isAdmin) {
      router.push('/admin')
    } else if (auth.role === 'FARM') {
      router.push('/farm')
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
