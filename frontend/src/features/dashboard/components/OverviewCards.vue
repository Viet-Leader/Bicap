<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
    <div v-for="stat in stats" :key="stat.label" class="glass-panel rounded-xl p-6 relative overflow-hidden group hover:border-primary/50 transition-colors">
      <!-- Decorative background accent -->
      <div class="absolute -right-4 -top-4 w-20 h-20 bg-primary/10 rounded-full blur-xl group-hover:bg-primary/20 transition-colors"></div>
      
      <p class="font-mono text-sm uppercase text-gray-400 mb-2 relative z-10">{{ stat.label }}</p>
      <div class="flex items-baseline gap-2 relative z-10">
        <h3 class="text-3xl font-bold text-white">{{ stat.value }}</h3>
        <span :class="stat.trend > 0 ? 'text-primary' : 'text-error'" class="font-bold text-sm">
          {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
// Mock Data
const stats = [
  { label: 'Total Accounts', value: '1,248', trend: 12 },
  { label: 'Active Farms', value: '456', trend: 5 },
  { label: 'Pending Approvals', value: '24', trend: -2 },
  { label: 'Tx Volume (24h)', value: '8.4k', trend: 18 }
]
</script>
