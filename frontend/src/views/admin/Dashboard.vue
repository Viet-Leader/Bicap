<template>
  <div>
    <div class="mb-8">
      <p class="font-mono text-xs uppercase tracking-[.2em] text-primary">Operations center</p>
      <h2 class="mt-2 text-3xl font-bold text-white">Platform Overview</h2>
      <p class="mt-2 text-gray-400">Real-time statistics from the BICAP network.</p>
    </div>
    
    <OverviewCards />
    
    <div class="grid grid-cols-1 xl:grid-cols-[1.4fr_1fr] gap-6 mt-8">
      <div class="glass-panel p-6 rounded-xl">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="font-bold text-white">Network growth</h3>
            <p class="mt-1 text-xs text-gray-500">Activity over the last 30 days</p>
          </div>
          <span class="font-mono text-xs text-primary">+18.4%</span>
        </div>
        <div class="flex h-44 items-end gap-2 border-b border-white/10 px-2">
          <div v-for="(height, index) in chartBars" :key="index" class="group flex flex-1 flex-col justify-end gap-2">
            <div class="rounded-t bg-primary/50 transition group-hover:bg-primary" :style="{ height: `${height}px` }"></div>
            <span v-if="index % 3 === 0" class="text-center text-[10px] text-gray-600">{{ index + 1 }}</span>
          </div>
        </div>
      </div>

      <div class="glass-panel p-6 rounded-xl">
        <div class="flex items-center justify-between mb-5">
          <h3 class="font-bold text-white">New farm approvals</h3>
          <router-link to="/admin/farms" class="text-xs font-semibold text-primary hover:underline">View all</router-link>
        </div>
        <div class="space-y-4">
          <div v-for="farm in pendingFarms" :key="farm.name" class="flex items-center justify-between gap-3 border-b border-white/10 pb-3 last:border-0 last:pb-0">
            <div><p class="text-sm font-semibold text-white">{{ farm.name }}</p><p class="text-xs text-gray-500">{{ farm.location }}</p></div>
            <Badge variant="pending">{{ farm.age }}</Badge>
          </div>
        </div>
      </div>

      <div class="glass-panel p-6 rounded-xl">
        <h3 class="font-bold mb-4 text-white">System health</h3>
        <div class="space-y-4">
          <div class="flex justify-between items-center">
            <span class="text-gray-300">Blockchain Nodes</span>
            <Badge variant="verified">Operational</Badge>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-300">Smart Contract APIs</span>
            <Badge variant="verified">Operational</Badge>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-300">Storage (IPFS)</span>
            <Badge variant="verified">Operational</Badge>
          </div>
        </div>
      </div>

      <div class="glass-panel p-6 rounded-xl">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-bold text-white">Recent transactions</h3>
          <router-link to="/admin/transactions" class="text-xs font-semibold text-primary hover:underline">Explorer</router-link>
        </div>
        <div class="space-y-3">
          <div v-for="transaction in transactions" :key="transaction.hash" class="flex items-center justify-between gap-3">
            <div><p class="font-mono text-xs text-primary">{{ transaction.hash }}</p><p class="text-xs text-gray-500">{{ transaction.type }}</p></div>
            <span class="text-xs text-gray-500">{{ transaction.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import OverviewCards from '../../features/dashboard/components/OverviewCards.vue'
import Badge from '../../components/ui/Badge.vue'

const chartBars = [38, 52, 45, 68, 58, 76, 64, 92, 78, 104, 88, 118]
const pendingFarms = [
  { name: 'Green Valley Co.', location: 'Da Lat, VN', age: '2m' },
  { name: 'Mekong Organics', location: 'Can Tho, VN', age: '18m' },
  { name: 'Sunrise Ag', location: 'Dak Lak, VN', age: '1h' },
]
const transactions = [
  { hash: '0x9f8b...3a21', type: 'Farm registration', time: '2m' },
  { hash: '0x7a2c...1f99', type: 'Product harvest', time: '5m' },
  { hash: '0x3b11...8e44', type: 'Quality check', time: '12m' },
]
</script>
