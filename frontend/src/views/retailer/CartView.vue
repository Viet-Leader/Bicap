<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useCartStore } from '../../stores/cart'

const cart = useCartStore()
const router = useRouter()
const notice = ref('')

const checkout = async () => {
  try {
    const order = await cart.checkout()
    await router.push(`/retailer/orders?created=${order.orderId}`)
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to checkout.'
  }
}

onMounted(() => cart.load())
</script>

<template>
  <section class="mx-auto max-w-5xl"><RouterLink to="/retailer/products" class="text-sm font-semibold text-[#6f8e42]">Back to marketplace</RouterLink><h1 class="mt-4 text-4xl font-bold">Your cart</h1><p class="mt-3 text-[#668078]">Orders are grouped by one farm to preserve traceability.</p><p v-if="notice" class="mt-4 text-red-700">{{ notice }}</p><p v-if="cart.isLoading" class="mt-10 text-[#668078]">Loading cart...</p><div v-else-if="cart.isEmpty" class="mt-10 border border-dashed border-[#cbd8c5] bg-white p-10 text-center text-[#668078]">Your cart is empty.</div><div v-else class="mt-10 grid gap-6 lg:grid-cols-[1fr_320px]"><div class="border border-[#dce4d8] bg-white"><div v-for="item in cart.items" :key="item.cartItemId" class="flex flex-wrap items-center justify-between gap-4 border-b border-[#e4ebe0] p-5 last:border-0"><div><h2 class="font-bold">{{ item.productName }}</h2><p class="mt-1 text-xs text-[#668078]">{{ item.batchCode }} · Grade {{ item.grade }}</p></div><div class="text-right"><p class="font-semibold">{{ item.subTotal }}</p><button class="mt-1 text-xs text-red-700 underline" @click="cart.removeItem(item.cartItemId)">Remove</button></div></div></div><aside class="h-fit bg-[#173f35] p-6 text-white"><p class="text-xs uppercase tracking-widest text-[#a8c7b5]">Order total</p><p class="mt-3 text-3xl font-bold">{{ cart.totalAmount }}</p><button class="mt-8 w-full bg-[#d4e85b] px-4 py-3 font-bold text-[#173f35]" @click="checkout">Checkout</button></aside></div></section>
</template>
