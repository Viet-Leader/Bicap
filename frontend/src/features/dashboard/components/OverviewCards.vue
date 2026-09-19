<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
    <div v-for="stat in stats" :key="stat.label" class="glass-panel rounded-xl p-5 relative overflow-hidden group hover:border-primary/50 transition-colors">
      <!-- Decorative background accent -->
      <div class="absolute -right-4 -top-4 w-20 h-20 bg-primary/10 rounded-full blur-xl group-hover:bg-primary/20 transition-colors"></div>
      
      <p class="font-mono text-xs uppercase tracking-wider text-gray-400 mb-3 relative z-10">{{ stat.label }}</p>
      <div class="flex items-baseline gap-2 relative z-10">
        <h3 class="text-3xl font-bold text-white">{{ stat.value }}</h3>
        <span :class="stat.trend >= 0 ? 'text-primary' : 'text-error'" class="font-bold text-xs">
          {{ stat.trend >= 0 ? '+' : '' }}{{ stat.trend }}%
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
// Mock Data
const stats = [
  { label: 'Users', value: '245', trend: 12 },
  { label: 'Farms', value: '32', trend: 5 },
  { label: 'Batches', value: '1,240', trend: 18 },
  { label: 'Pending approvals', value: '18', trend: -2 }
]
</script>
