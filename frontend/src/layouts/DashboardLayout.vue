<script setup>
import { RouterLink, RouterView } from 'vue-router'

const navigation = [
  { label: 'Farm dashboard', to: '/farm', icon: '01' },
  { label: 'Traceability', to: '/traceability', icon: '02' },
  { label: 'Driver logistics', to: '/logistics', icon: '03' },
]
</script>

<template>
  <div class="min-h-screen bg-[#f4f6f1] text-[#20352f] lg:flex">
    <aside class="flex w-full flex-col justify-between bg-[#173f35] px-6 py-7 text-white lg:min-h-screen lg:w-72 lg:px-8">
      <div>
        <div class="mb-14 flex items-center gap-3"><span class="grid h-10 w-10 place-items-center rounded-full bg-[#d4e85b] text-sm font-bold text-[#173f35]">BI</span><span class="font-display text-xl font-bold tracking-tight">BICAP</span></div>
        <p class="mb-4 text-xs font-semibold uppercase tracking-[.2em] text-[#a8c7b5]">Operations</p>
        <nav class="space-y-2">
          <RouterLink v-for="item in navigation" :key="item.to" :to="item.to" class="flex items-center gap-4 rounded-lg px-3 py-3 text-sm text-[#c5d9ce] transition hover:bg-white/10 hover:text-white" active-class="bg-[#d4e85b] !text-[#173f35] font-semibold">
            <span class="text-xs opacity-60">{{ item.icon }}</span>{{ item.label }}
          </RouterLink>
        </nav>
      </div>
      <div class="mt-10 hidden border-t border-white/15 pt-5 text-xs text-[#a8c7b5] lg:block">Season 2026<br><span class="text-white">Connected to live operations</span></div>
    </aside>
    <main class="min-w-0 flex-1 px-5 py-6 sm:px-8 lg:px-12 lg:py-10"><RouterView /></main>
  </div>
</template>
