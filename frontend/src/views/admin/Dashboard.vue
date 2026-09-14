<template>
  <div>
    <div class="mb-8">
      <h2 class="text-2xl font-bold mb-2 text-white">Platform Overview</h2>
      <p class="text-gray-400">Real-time statistics from the BICAP network.</p>
    </div>
    
    <OverviewCards />
    
    <!-- We can add recent transactions or other widgets here later -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">
      <div class="glass-panel p-6 rounded-xl">
        <h3 class="font-bold mb-4 text-white">System Health</h3>
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
    </div>
  </div>
</template>

<script setup>
import OverviewCards from '../../features/dashboard/components/OverviewCards.vue'
import Badge from '../../components/ui/Badge.vue'
</script>
