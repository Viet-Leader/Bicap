<script setup>
const stats = [
  { label: 'Active farms', value: '24', note: '+3 this season' },
  { label: 'Harvest volume', value: '18.4 t', note: '82% of target' },
  { label: 'In transit', value: '07', note: 'All on schedule' },
]
</script>

<template>
  <section class="mx-auto max-w-6xl animate-[rise_.5s_ease-out]">
    <header class="mb-10 flex flex-col justify-between gap-5 sm:flex-row sm:items-end"><div><p class="mb-3 text-xs font-bold uppercase tracking-[.22em] text-[#6f8e42]">Thursday, 27 August 2026</p><h1 class="text-4xl font-bold tracking-tight sm:text-5xl">Good morning, team.</h1><p class="mt-3 max-w-lg text-[#668078]">Here is the pulse of your agricultural network today.</p></div><button class="rounded-lg bg-[#d4e85b] px-5 py-3 text-sm font-bold text-[#173f35] shadow-sm transition hover:bg-[#c4da42]">+ Log harvest</button></header>
    <div class="grid gap-4 md:grid-cols-3"> <article v-for="stat in stats" :key="stat.label" class="border border-[#dce4d8] bg-white p-6"><p class="text-sm text-[#668078]">{{ stat.label }}</p><p class="mt-4 font-display text-4xl font-bold">{{ stat.value }}</p><p class="mt-2 text-xs font-semibold text-[#6f8e42]">{{ stat.note }}</p></article></div>
    <div class="mt-8 grid gap-6 lg:grid-cols-[1.3fr_1fr]"><article class="border border-[#dce4d8] bg-white p-6"><div class="mb-8 flex items-center justify-between"><h2 class="text-xl font-bold">Harvest overview</h2><span class="text-xs text-[#668078]">This year</span></div><div class="flex h-48 items-end gap-3 border-b border-[#e4ebe0] pb-0"> <div v-for="(height, index) in [42, 61, 52, 78, 68, 92, 74, 100, 84, 94, 81, 108]" :key="index" class="group flex flex-1 flex-col justify-end gap-2"><div class="h-[var(--bar)] bg-[#83a86d] transition group-hover:bg-[#d4e85b]" :style="{ '--bar': `${height}px` }"></div><span class="text-center text-[10px] text-[#91a49b]">{{ index + 1 }}</span></div></div></article><article class="border border-[#dce4d8] bg-[#173f35] p-6 text-white"><p class="text-xs font-bold uppercase tracking-[.18em] text-[#a8c7b5]">Next milestone</p><h2 class="mt-10 text-2xl font-bold">North valley collection</h2><p class="mt-2 text-sm text-[#c5d9ce]">12 farms ready for pickup</p><div class="mt-10 h-2 rounded-full bg-white/15"><div class="h-2 w-3/4 rounded-full bg-[#d4e85b]"></div></div><p class="mt-3 text-xs text-[#a8c7b5]">75% prepared · Tomorrow, 06:30</p></article></div>
  </section>
</template>
