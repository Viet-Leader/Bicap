<template>
  <section><h2 class="text-2xl font-bold">Tổng quan hệ thống</h2><p class="mt-1 text-gray-400">Số liệu được tổng hợp từ các API hiện có.</p>
    <p v-if="error" class="mt-5 text-error">{{ error }}</p>
    <div class="grid grid-cols-1 gap-4 mt-6 sm:grid-cols-2 lg:grid-cols-4">
      <div v-for="card in cards" :key="card.label" class="rounded-xl bg-white/5 border border-white/10 p-5"><p class="text-sm text-gray-400">{{ card.label }}</p><p class="text-3xl font-bold mt-2">{{ loading ? '…' : card.value }}</p></div>
    </div>
  </section>
</template>
<script setup>
import { computed, onMounted, ref } from 'vue'
import { adminApi } from '../../api/adminService'
const loading = ref(true); const error = ref(''); const data = ref({ accounts: [], crops: [], products: [], orders: [], notifications: [] })
const cards = computed(() => [{ label: 'Tài khoản', value: data.value.accounts.length }, { label: 'Cây trồng', value: data.value.crops.length }, { label: 'Sản phẩm', value: data.value.products.length }, { label: 'Đơn chờ hoàn tất', value: data.value.orders.filter((item) => item.status === 'CONFIRMED').length }])
onMounted(async () => { try { const [accounts, crops, products, orders, notifications] = await Promise.all([adminApi.accounts(), adminApi.crops(), adminApi.products({ size: 1 }), adminApi.orders(), adminApi.notifications()]); data.value = { accounts: accounts.data, crops: crops.data, products: Array(products.data.totalElements || 0), orders: orders.data, notifications: notifications.data } } catch (e) { error.value = e.response?.data?.message || 'Không thể tải dữ liệu tổng quan.' } finally { loading.value = false } })
</script>
