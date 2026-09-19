<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import axiosClient from '../api/axiosClient'

const route = useRoute()
const traceability = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')
const copied = ref(false)

const batchId = computed(() => route.params.batchCode)
const isVerified = computed(() => traceability.value?.blockchainStatus === 'SUCCESS')

const loadTraceability = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const { data } = await axiosClient.get(`/public/traceability/batch/${batchId.value}`)
    traceability.value = data
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Unable to load this product journey.'
  } finally {
    isLoading.value = false
  }
}

const copyHash = async () => {
  if (!traceability.value?.txHash) return
  await navigator.clipboard.writeText(traceability.value.txHash)
  copied.value = true
  window.setTimeout(() => { copied.value = false }, 1800)
}

onMounted(loadTraceability)
</script>

<template>
  <section class="mx-auto max-w-4xl">
    <p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Product journey</p>
    <h1 class="mt-3 text-4xl font-bold">Traceability</h1>
    <p class="mt-3 text-[#668078]">Follow every handoff from soil to shipment.</p>

    <div v-if="isLoading" class="mt-10 border border-[#dce4d8] bg-white p-8 text-[#668078]">Loading product journey...</div>
    <div v-else-if="errorMessage" class="mt-10 border border-red-200 bg-white p-8 text-red-700">
      <p>{{ errorMessage }}</p>
      <button class="mt-4 font-semibold underline" @click="loadTraceability">Retry</button>
    </div>
    <div v-else class="mt-10 border border-[#dce4d8] bg-white p-6 sm:p-8">
      <div class="flex flex-col justify-between gap-4 border-b border-[#e4ebe0] pb-6 sm:flex-row sm:items-center">
        <div>
          <p class="text-xs uppercase tracking-widest text-[#91a49b]">Selected batch</p>
          <h2 class="mt-2 text-2xl font-bold">{{ traceability.productName }} · {{ traceability.batchCode }}</h2>
          <p class="mt-2 text-sm text-[#668078]">{{ traceability.farmName }} · {{ traceability.quantity }} {{ traceability.unit }}</p>
        </div>
        <span class="w-fit px-3 py-2 text-xs font-bold" :class="isVerified ? 'bg-[#eaf3d3] text-[#58752d]' : 'bg-amber-100 text-amber-700'">
          {{ isVerified ? 'VERIFIED ON CHAIN' : 'BLOCKCHAIN PENDING' }}
        </span>
      </div>

      <div class="mt-8 space-y-8">
        <article v-for="event in traceability.activities" :key="event.activityId" class="relative border-l-2 border-[#d4e85b] pl-7">
          <span class="absolute -left-[7px] top-0 h-3 w-3 rounded-full bg-[#6f8e42] ring-4 ring-white"></span>
          <p class="text-xs font-semibold text-[#6f8e42]">{{ new Date(event.activityTime).toLocaleString() }}</p>
          <h3 class="mt-1 text-lg font-bold">{{ event.activityType }}</h3>
          <p class="mt-1 text-sm text-[#668078]">{{ event.description }}</p>
        </article>
      </div>

      <div v-if="traceability.txHash" class="mt-8 border-t border-[#e4ebe0] pt-5">
        <p class="text-xs uppercase tracking-widest text-[#91a49b]">Blockchain proof</p>
        <div class="mt-2 flex flex-wrap items-center gap-3">
          <code class="break-all text-xs text-[#58752d]">{{ traceability.txHash }}</code>
          <button class="text-xs font-semibold text-[#6f8e42] underline" @click="copyHash">{{ copied ? 'Copied' : 'Copy hash' }}</button>
        </div>
      </div>
    </div>
  </section>
</template>
