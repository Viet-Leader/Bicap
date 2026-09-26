<template>
  <div class="error-page">
    <div class="error-box">
        <h1>Error</h1>
        <p>{{ message || 'Đã xảy ra lỗi. Vui lòng thử lại.' }}</p>
        <router-link to="/">Quay về trang chủ</router-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const message = computed(() => route.query.message || '')
</script>

<style scoped>
.error-page { 
    font-family: Arial, sans-serif; 
    background: #f8d7da; 
    color: #721c24; 
    display: flex; 
    justify-content: center; 
    align-items: center; 
    height: 100vh; 
}
.error-box { 
    background: white; 
    padding: 30px; 
    border-radius: 8px; 
    box-shadow: 0 4px 10px rgba(0,0,0,0.1); 
    text-align: center; 
}
h1 { color: #c9302c; }
a { color: #4e73df; text-decoration: none; margin-top: 20px; display: inline-block; }
</style>
