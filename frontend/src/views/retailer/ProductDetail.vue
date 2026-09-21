<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useCartStore } from '../../stores/cart'
import axiosClient from '../../api/axiosClient'

const route = useRoute()
const cart = useCartStore()
const product = ref(null)
const quantity = ref(1)
const notice = ref('')
const isLoading = ref(true)

const loadProduct = async () => {
  try {
    const { data } = await axiosClient.get(`/public/products/${route.params.id}`)
    product.value = data
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to load product.'
  } finally {
    isLoading.value = false
  }
}

const addBatch = async (batch) => {
  try {
    await cart.addItem(batch.batchId, quantity.value, product.value.farmId)
    notice.value = `${batch.batchCode} added to cart.`
  } catch (error) {
    notice.value = error.response?.data?.message || error.message || 'Unable to add batch.'
  }
}

onMounted(loadProduct)
</script>

<template>
  <section class="mx-auto max-w-5xl"><RouterLink to="/retailer/products" class="text-sm font-semibold text-[#6f8e42]">Back to marketplace</RouterLink><p v-if="isLoading" class="mt-10 text-[#668078]">Loading product...</p><div v-else-if="product" class="mt-6"><p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">{{ product.cropName }}</p><h1 class="mt-3 text-4xl font-bold">{{ product.productName }}</h1><p class="mt-3 max-w-2xl text-[#668078]">{{ product.description || 'Verified produce from the BICAP network.' }}</p><div class="mt-10 grid gap-6 lg:grid-cols-[1fr_1.2fr]"><div class="flex aspect-square items-center justify-center bg-[#eaf3d3] text-8xl">{{ product.cropName?.slice(0, 1) || 'P' }}</div><div class="border border-[#dce4d8] bg-white p-6"><div class="flex items-center justify-between"><h2 class="text-xl font-bold">Available batches</h2><span class="text-sm text-[#668078]">{{ product.farmName }}</span></div><label class="mt-6 block text-sm font-semibold">Quantity<input v-model.number="quantity" min="0.01" step="0.01" type="number" class="mt-2 w-full border border-[#dce4d8] px-3 py-2" /></label><div v-for="batch in product.batches" :key="batch.batchId" class="mt-5 flex items-center justify-between gap-4 border-t border-[#e4ebe0] pt-4"><div><p class="font-semibold">{{ batch.batchCode }}</p><p class="text-xs text-[#668078]">Grade {{ batch.grade }} · {{ batch.remainingQuantity }} {{ product.unit }}</p></div><button class="bg-[#173f35] px-4 py-2 text-sm font-bold text-white" @click="addBatch(batch)">Add</button></div><p v-if="notice" class="mt-5 text-sm font-semibold text-[#6f8e42]">{{ notice }}</p></div></div></div><p v-else class="mt-10 text-red-700">{{ notice }}</p></section>
</template>
