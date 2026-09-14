<template>
  <section id="solutions" class="py-20 relative border-t border-white/10">
    <div class="container mx-auto px-4 lg:px-8 relative z-10">
      <div class="text-center max-w-3xl mx-auto mb-16">
        <h2 class="text-3xl font-bold mb-4 text-white">Core Pillars</h2>
        <p class="text-gray-400">Built on the principles of transparency, efficiency, and ecological integrity.</p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div v-for="value in values" :key="value.title" class="glass-panel p-8 rounded-xl relative overflow-hidden group">
          <div class="absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
          <div class="w-14 h-14 bg-white/5 border border-white/20 rounded-lg flex items-center justify-center text-xl mb-6 font-mono text-primary shadow-[0_0_15px_rgba(11,218,81,0.2)]">
            {{ value.icon }}
          </div>
          <h3 class="text-xl font-bold mb-3 text-white">{{ value.title }}</h3>
          <p class="text-gray-400 relative z-10">{{ value.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const values = [
  {
    icon: '01',
    title: 'Transparency',
    description: 'Every transaction is recorded on an immutable ledger, ensuring absolute clarity from farm to table.'
  },
  {
    icon: '02',
    title: 'Efficiency',
    description: 'Smart contracts automate compliance and payments, removing intermediaries and friction.'
  },
  {
    icon: '03',
    title: 'Ecological Integrity',
    description: 'Verifiable proof of sustainable farming practices, incentivizing green agriculture.'
  }
]
</script>
