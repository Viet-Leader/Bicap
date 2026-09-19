<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import axiosClient from '../../api/axiosClient'
import { useCartStore } from '../../stores/cart'

const cart = useCartStore()
const keyword = ref('')
const products = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const notice = ref('')

const loadProducts = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const { data } = await axiosClient.get('/public/products', { params: { keyword: keyword.value || undefined, size: 24 } })
    products.value = data.content || []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Unable to load products'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadProducts)
</script>

<template>
  <section class="mx-auto max-w-6xl">
    <header class="flex flex-col justify-between gap-5 sm:flex-row sm:items-end">
      <div><p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Retail marketplace</p><h1 class="mt-3 text-4xl font-bold">Find clean produce.</h1><p class="mt-3 text-[#668078]">Browse verified products from connected farms.</p></div>
      <RouterLink to="/retailer/cart" class="rounded-lg bg-[#173f35] px-5 py-3 text-sm font-bold text-white">Cart ({{ cart.items.length }})</RouterLink>
    </header>
    <form class="mt-8 flex gap-3" @submit.prevent="loadProducts"><input v-model="keyword" class="min-w-0 flex-1 border border-[#dce4d8] bg-white px-4 py-3 outline-none focus:border-[#6f8e42]" placeholder="Search product or crop" /><button class="bg-[#d4e85b] px-5 py-3 font-bold text-[#173f35]">Search</button></form>
    <p v-if="notice" class="mt-4 text-sm font-semibold text-[#6f8e42]">{{ notice }}</p>
    <p v-if="errorMessage" class="mt-8 text-red-700">{{ errorMessage }} <button class="underline" @click="loadProducts">Retry</button></p>
    <p v-else-if="isLoading" class="mt-10 text-[#668078]">Loading products...</p>
    <div v-else class="mt-10 grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
      <article v-for="product in products" :key="product.productId" class="border border-[#dce4d8] bg-white p-5">
        <div class="flex aspect-[4/3] items-center justify-center bg-[#eaf3d3] text-5xl">{{ product.cropName?.slice(0, 1) || 'P' }}</div>
        <p class="mt-5 text-xs font-bold uppercase tracking-widest text-[#91a49b]">{{ product.cropName }}</p>
        <h2 class="mt-2 text-xl font-bold">{{ product.productName }}</h2>
        <p class="mt-2 text-sm text-[#668078]">{{ product.farmName }} · {{ product.unit }}</p>
        <div class="mt-5"><RouterLink :to="`/retailer/products/${product.productId}`" class="block bg-[#173f35] px-3 py-2 text-center text-sm font-semibold text-white">View batches</RouterLink></div>
      </article>
    </div>
    <p v-if="!isLoading && !products.length && !errorMessage" class="mt-10 border border-dashed border-[#cbd8c5] p-8 text-center text-[#668078]">No products found.</p>
  </section>
</template>
