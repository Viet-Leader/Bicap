<template>
  <div class="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
    <!-- Abstract tech background elements -->
    <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-5 pointer-events-none"></div>
    <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-primary/10 rounded-full blur-[100px] pointer-events-none"></div>
    <div class="absolute bottom-1/4 right-1/4 w-96 h-96 bg-accent/10 rounded-full blur-[100px] pointer-events-none"></div>

    <div class="w-full max-w-md relative z-10">
      <router-link to="/" class="inline-flex items-center gap-2 text-sm font-mono text-gray-400 hover:text-white mb-6 transition-colors">
        <span>&larr;</span> Back to Public Network
      </router-link>
      
      <LoginForm />
    </div>
  </div>
</template>

<script setup>
import LoginForm from '../../features/auth/components/LoginForm.vue'
</script>
