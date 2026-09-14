<template>
  <div class="flex h-screen bg-transparent font-sans text-gray-100 overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-64 glass-panel flex flex-col flex-shrink-0 z-20 border-r border-white/10 rounded-r-2xl m-4 my-4">
      <div class="h-16 flex items-center px-6 border-b border-white/10">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary/20 rounded border border-primary/50 flex items-center justify-center font-bold text-xl text-primary shadow-[0_0_10px_rgba(11,218,81,0.3)]">B</div>
          <span class="text-xl font-bold tracking-widest uppercase">Admin</span>
        </div>
      </div>
      
      <div class="p-4 flex-grow overflow-y-auto">
        <nav class="space-y-2">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="flex items-center gap-3 px-4 py-3 rounded-lg font-medium transition-all"
            :class="[
              $route.path === item.path
                ? 'bg-primary/20 text-primary border border-primary/50 shadow-[0_0_15px_rgba(11,218,81,0.2)]'
                : 'text-gray-300 hover:bg-white/10 border border-transparent'
            ]"
          >
            <!-- Using simple spans for icons -->
            <span class="w-5 h-5 flex items-center justify-center font-mono" :class="[$route.path === item.path ? 'text-primary' : '']">{{ item.icon }}</span>
            {{ item.name }}
          </router-link>
        </nav>
      </div>
      
      <div class="p-4 border-t border-white/10">
        <router-link to="/" class="flex items-center gap-3 px-4 py-3 text-gray-300 hover:bg-white/10 rounded-lg transition-colors font-medium border border-transparent hover:border-white/10">
          <span class="w-5 h-5 font-mono">&larr;</span>
          Exit Dashboard
        </router-link>
      </div>
    </aside>

    <!-- Main Content Area -->
    <div class="flex-grow flex flex-col overflow-hidden relative">
      <!-- Top Header -->
      <header class="h-16 glass-panel m-4 mb-0 rounded-2xl flex items-center justify-between px-8 flex-shrink-0 z-10 border border-white/10">
        <h1 class="text-xl font-bold text-white">{{ currentRouteName }}</h1>
        <div class="flex items-center gap-4">
          <span class="font-mono text-sm bg-primary/20 text-primary px-3 py-1 border border-primary/50 rounded-full shadow-[0_0_10px_rgba(11,218,81,0.2)]">NODE: ACTIVE</span>
          <div class="w-10 h-10 bg-accent/20 rounded-full border-2 border-accent/50 text-accent flex items-center justify-center font-bold text-sm shadow-[0_0_10px_rgba(56,189,248,0.2)] cursor-pointer hover:bg-accent/30 transition-colors">
            AD
          </div>
        </div>
      </header>

      <!-- Scrollable Main Content -->
      <main class="flex-grow p-4 md:p-8 overflow-y-auto">
        <div class="max-w-7xl mx-auto">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const navItems = [
  { name: 'Dashboard', path: '/admin', icon: '■' },
  { name: 'Accounts', path: '/admin/accounts', icon: '웃' },
  { name: 'Farm Approvals', path: '/admin/farms', icon: '⌂' },
  { name: 'Blockchain Explorer', path: '/admin/transactions', icon: '▤' }
]

const currentRouteName = computed(() => {
  const item = navItems.find(i => i.path === route.path)
  return item ? item.name : 'Admin'
})
</script>
