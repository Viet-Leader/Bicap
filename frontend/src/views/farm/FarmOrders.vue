<script setup>
import { onMounted, ref } from 'vue'
import axiosClient from '../../api/axiosClient'

const orders = ref([])
const notice = ref('')

const loadOrders = async () => {
  try {
    const { data } = await axiosClient.get('/orders/farm')
    orders.value = data
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to load farm orders.'
  }
}

const confirmOrder = async (order) => {
  try {
    await axiosClient.patch(`/orders/${order.orderId}/confirm`)
    await loadOrders()
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to confirm order.'
  }
}

onMounted(loadOrders)
</script>

<template>
  <section class="mx-auto max-w-6xl"><p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Fulfillment</p><h1 class="mt-3 text-4xl font-bold">Farm orders</h1><p class="mt-3 text-[#668078]">Confirm incoming orders and keep the handoff visible.</p><p v-if="notice" class="mt-5 text-red-700">{{ notice }}</p><div class="mt-10 space-y-4"><article v-for="order in orders" :key="order.orderId" class="border border-[#dce4d8] bg-white p-6"><div class="flex flex-wrap justify-between gap-4"><div><p class="text-xs uppercase tracking-widest text-[#91a49b]">Order #{{ order.orderId }}</p><h2 class="mt-2 text-xl font-bold">{{ order.retailerName }}</h2><p class="mt-1 text-sm text-[#668078]">{{ order.createdAt }}</p></div><span class="bg-[#eaf3d3] px-3 py-2 text-xs font-bold text-[#58752d]">{{ order.status }}</span></div><div class="mt-5 flex justify-between border-t border-[#e4ebe0] pt-4"><span>{{ order.orderDetails?.length || 0 }} batches</span><button v-if="order.status === 'PENDING'" class="bg-[#173f35] px-4 py-2 text-sm font-bold text-white" @click="confirmOrder(order)">Confirm order</button></div></article><p v-if="!orders.length" class="border border-dashed border-[#cbd8c5] p-8 text-center text-[#668078]">No orders yet.</p></div></section>
</template>
