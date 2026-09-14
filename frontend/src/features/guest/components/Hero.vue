<template>
  <section class="py-20 lg:py-32 relative mt-16">
    <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 pointer-events-none"></div>
    <div class="container mx-auto px-4 lg:px-8 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center relative z-10">
      <div>
        <Badge variant="verified" class="mb-6">Blockchain Powered</Badge>
        <h1 class="text-4xl lg:text-6xl font-bold leading-tight mb-6 text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
          The Immutable Ledger for Global Agriculture
        </h1>
        <p class="text-lg text-gray-300 mb-8 max-w-xl">
          BICAP provides end-to-end traceability, smart contracts, and data-driven trust for every stakeholder in the agricultural supply chain.
        </p>
        <div class="flex items-center gap-4">
          <Button size="lg">Join the Network</Button>
          <Button variant="outline" size="lg">Read Whitepaper</Button>
        </div>
      </div>
      <div class="relative">
        <div class="aspect-[4/3] glass-panel rounded-2xl flex items-center justify-center overflow-hidden border border-white/20 p-2 relative">
          <div class="absolute inset-0 bg-gradient-to-br from-primary/10 to-accent/10 blur-xl"></div>
          <div class="grid grid-cols-3 gap-3 w-full h-full p-4 relative z-10">
            <div v-for="i in 9" :key="i" class="glass-panel border-white/10 rounded-lg flex items-center justify-center font-mono text-xs text-primary shadow-[0_0_15px_rgba(11,218,81,0.2)] animate-pulse" :style="{ animationDelay: `${i * 100}ms` }">
              Block {{ i + 1000 }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import Button from '../../../components/ui/Button.vue'
import Badge from '../../../components/ui/Badge.vue'
</script>
