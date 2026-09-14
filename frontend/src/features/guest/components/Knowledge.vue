<template>
  <section id="network" class="py-20 relative bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-secondary via-[#020617] to-[#020617]">
    <div class="container mx-auto px-4 lg:px-8 relative z-10">
      <div class="flex flex-col md:flex-row justify-between items-end mb-12">
        <div class="max-w-2xl">
          <h2 class="text-3xl font-bold mb-4 text-white">Knowledge Base</h2>
          <p class="text-gray-400">Latest research and protocol updates from the BICAP ecosystem.</p>
        </div>
        <Button variant="outline" class="mt-4 md:mt-0">View All Updates</Button>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <article v-for="post in posts" :key="post.title" class="glass-panel-hover glass-panel rounded-xl p-8 cursor-pointer group">
          <div class="flex items-center gap-4 mb-6">
            <Badge variant="default">{{ post.category }}</Badge>
            <span class="text-sm font-mono text-gray-500">{{ post.date }}</span>
          </div>
          <h3 class="text-2xl font-bold mb-4 text-white group-hover:text-primary transition-colors">{{ post.title }}</h3>
          <p class="text-gray-400 mb-8">{{ post.excerpt }}</p>
          <span class="font-bold text-accent group-hover:text-primary transition-colors inline-flex items-center gap-2">
            Read more <span class="group-hover:translate-x-1 transition-transform">&rarr;</span>
          </span>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import Button from '../../../components/ui/Button.vue'
import Badge from '../../../components/ui/Badge.vue'

const posts = [
  {
    category: 'Protocol',
    date: 'OCT 24, 2026',
    title: 'Implementing Zero-Knowledge Proofs for Yield Data',
    excerpt: 'How we are protecting farmer privacy while ensuring network-wide data integrity.'
  },
  {
    category: 'Network',
    date: 'OCT 18, 2026',
    title: 'Q3 Ecosystem Growth Report',
    excerpt: 'Over 500 new farms onboarded. Tracing $20M in agricultural assets.'
  }
]
</script>
