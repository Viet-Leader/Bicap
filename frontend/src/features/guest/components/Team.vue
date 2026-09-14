<template>
  <section id="about" class="py-20 relative border-t border-white/10">
    <div class="container mx-auto px-4 lg:px-8 relative z-10">
      <div class="text-center max-w-3xl mx-auto mb-16">
        <h2 class="text-3xl font-bold mb-4 text-white">Our Network Leaders</h2>
        <p class="text-gray-400">Driving the future of decentralized agriculture.</p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div v-for="member in team" :key="member.name" class="group relative glass-panel rounded-xl overflow-hidden transition-all duration-300 hover:border-primary/50 hover:shadow-[0_0_20px_rgba(11,218,81,0.2)]">
          <div class="aspect-square bg-white/5 flex items-center justify-center border-b border-white/10">
            <span class="text-5xl group-hover:scale-110 transition-transform">👨‍🌾</span>
          </div>
          <div class="p-5 bg-transparent transition-colors group-hover:bg-primary/10">
            <h3 class="font-bold text-lg text-white">{{ member.name }}</h3>
            <p class="text-sm font-mono mt-1 text-primary">{{ member.role }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const team = [
  { name: 'Dr. Sarah Chen', role: 'Head of Traceability' },
  { name: 'Michael Osei', role: 'Lead Blockchain Eng' },
  { name: 'Elena Rostova', role: 'Agritech Director' },
  { name: 'David Kim', role: 'Operations' }
]
</script>
