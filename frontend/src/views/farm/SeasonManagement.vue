<script setup>
import { onMounted, reactive, ref } from 'vue'
import axiosClient from '../../api/axiosClient'

const seasons = ref([])
const isLoading = ref(true)
const notice = ref('')
const form = reactive({ seasonName: '', plantingDate: '', expectedHarvestDate: '' })

const loadSeasons = async () => {
  try {
    const { data } = await axiosClient.get('/farming-seasons', { params: { size: 50 } })
    seasons.value = data.content || []
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to load seasons.'
  } finally {
    isLoading.value = false
  }
}

const createSeason = async () => {
  try {
    await axiosClient.post('/farming-seasons', form)
    Object.assign(form, { seasonName: '', plantingDate: '', expectedHarvestDate: '' })
    notice.value = 'Season created.'
    await loadSeasons()
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to create season.'
  }
}

const changeStatus = async (season) => {
  const action = season.status === 'PLANNING' ? 'start' : season.status === 'PLANTING' ? 'harvest' : null
  if (!action) return
  try {
    await axiosClient.patch(`/farming-seasons/${season.seasonId}/${action}`)
    await loadSeasons()
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to update season.'
  }
}

onMounted(loadSeasons)
</script>

<template>
  <section class="mx-auto max-w-6xl"><p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Production planning</p><h1 class="mt-3 text-4xl font-bold">Farming seasons</h1><p class="mt-3 text-[#668078]">Plan, start and track each growing cycle.</p><div class="mt-8 grid gap-6 lg:grid-cols-[320px_1fr]"><form class="h-fit border border-[#dce4d8] bg-white p-5" @submit.prevent="createSeason"><h2 class="text-lg font-bold">New season</h2><label class="mt-5 block text-sm font-semibold">Name<input v-model="form.seasonName" class="mt-2 w-full border border-[#dce4d8] px-3 py-2" required /></label><label class="mt-4 block text-sm font-semibold">Planting date<input v-model="form.plantingDate" type="date" class="mt-2 w-full border border-[#dce4d8] px-3 py-2" required /></label><label class="mt-4 block text-sm font-semibold">Expected harvest<input v-model="form.expectedHarvestDate" type="date" class="mt-2 w-full border border-[#dce4d8] px-3 py-2" required /></label><button class="mt-5 w-full bg-[#173f35] px-4 py-3 font-bold text-white">Create season</button><p class="mt-3 text-sm text-[#6f8e42]">{{ notice }}</p></form><div><p v-if="isLoading" class="text-[#668078]">Loading seasons...</p><div v-else class="space-y-4"><article v-for="season in seasons" :key="season.seasonId" class="border border-[#dce4d8] bg-white p-5"><div class="flex flex-wrap items-start justify-between gap-4"><div><h2 class="text-xl font-bold">{{ season.seasonName }}</h2><p class="mt-2 text-sm text-[#668078]">{{ season.plantingDate }} to {{ season.expectedHarvestDate }}</p></div><span class="bg-[#eaf3d3] px-3 py-2 text-xs font-bold text-[#58752d]">{{ season.status }}</span></div><button v-if="season.status === 'PLANNING' || season.status === 'PLANTING'" class="mt-5 border border-[#173f35] px-3 py-2 text-sm font-semibold text-[#173f35]" @click="changeStatus(season)">{{ season.status === 'PLANNING' ? 'Start planting' : 'Start harvest' }}</button></article><p v-if="!seasons.length" class="border border-dashed border-[#cbd8c5] p-8 text-center text-[#668078]">No seasons yet.</p></div></div></div></section>
</template>
