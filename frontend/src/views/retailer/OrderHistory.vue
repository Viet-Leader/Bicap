<script setup>
import { onMounted, ref } from 'vue'
import axiosClient from '../../api/axiosClient'

const orders = ref([])
const isLoading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const { data } = await axiosClient.get('/orders/retailer')
    orders.value = data
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Unable to load orders.'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <section class="mx-auto max-w-5xl"><p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Retailer workspace</p><h1 class="mt-3 text-4xl font-bold">Order history</h1><p class="mt-3 text-[#668078]">Track every order and its farm origin.</p><p v-if="isLoading" class="mt-10 text-[#668078]">Loading orders...</p><p v-else-if="errorMessage" class="mt-10 text-red-700">{{ errorMessage }}</p><div v-else class="mt-10 space-y-4"><article v-for="order in orders" :key="order.orderId" class="border border-[#dce4d8] bg-white p-6"><div class="flex flex-wrap items-start justify-between gap-4"><div><p class="text-xs uppercase tracking-widest text-[#91a49b]">Order #{{ order.orderId }}</p><h2 class="mt-2 text-xl font-bold">{{ order.farmName }}</h2><p class="mt-1 text-sm text-[#668078]">{{ new Date(order.createdAt).toLocaleString() }}</p></div><span class="bg-[#eaf3d3] px-3 py-2 text-xs font-bold text-[#58752d]">{{ order.status }}</span></div><div class="mt-5 flex justify-between border-t border-[#e4ebe0] pt-4 text-sm"><span>{{ order.orderDetails?.length || 0 }} items</span><strong>{{ order.totalAmount }}</strong></div></article><p v-if="!orders.length" class="border border-dashed border-[#cbd8c5] p-8 text-center text-[#668078]">No orders yet.</p></div></section>
</template>
