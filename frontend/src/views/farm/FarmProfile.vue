<script setup>
import { onMounted, reactive, ref } from 'vue'
import axiosClient from '../../api/axiosClient'

const form = reactive({ farmName: '', businessLicense: '', address: '', description: '' })
const isLoading = ref(true)
const isSaving = ref(false)
const notice = ref('')
const errorMessage = ref('')

const loadFarm = async () => {
  try {
    const { data } = await axiosClient.get('/farms/me')
    Object.assign(form, data)
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Unable to load farm profile.'
  } finally {
    isLoading.value = false
  }
}

const saveFarm = async () => {
  isSaving.value = true
  notice.value = ''
  try {
    const { data } = await axiosClient.put('/farms/me', form)
    Object.assign(form, data)
    notice.value = 'Farm profile saved.'
  } catch (error) {
    notice.value = error.response?.data?.message || 'Unable to save farm profile.'
  } finally {
    isSaving.value = false
  }
}

onMounted(loadFarm)
</script>

<template>
  <section class="mx-auto max-w-4xl"><p class="text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Farm identity</p><h1 class="mt-3 text-4xl font-bold">Farm profile</h1><p class="mt-3 text-[#668078]">Keep the farm information shown across the supply chain accurate.</p><p v-if="isLoading" class="mt-10 text-[#668078]">Loading profile...</p><p v-else-if="errorMessage" class="mt-10 text-red-700">{{ errorMessage }}</p><form v-else class="mt-10 border border-[#dce4d8] bg-white p-6 sm:p-8" @submit.prevent="saveFarm"><div class="grid gap-5 sm:grid-cols-2"><label class="text-sm font-semibold">Farm name<input v-model="form.farmName" class="mt-2 w-full border border-[#dce4d8] px-3 py-3" required /></label><label class="text-sm font-semibold">Business license<input v-model="form.businessLicense" class="mt-2 w-full border border-[#dce4d8] px-3 py-3" required /></label><label class="text-sm font-semibold sm:col-span-2">Address<input v-model="form.address" class="mt-2 w-full border border-[#dce4d8] px-3 py-3" required /></label><label class="text-sm font-semibold sm:col-span-2">Description<textarea v-model="form.description" rows="5" class="mt-2 w-full border border-[#dce4d8] px-3 py-3"></textarea></label></div><div class="mt-6 flex items-center justify-between"><span class="text-sm text-[#6f8e42]">{{ notice }}</span><button class="bg-[#173f35] px-5 py-3 font-bold text-white" :disabled="isSaving">{{ isSaving ? 'Saving...' : 'Save profile' }}</button></div></form></section>
</template>
