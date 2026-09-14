<template>
  <div class="flex flex-col min-h-screen font-sans text-gray-100">
    <!-- Navbar -->
    <header class="fixed top-0 inset-x-0 z-50 bg-secondary/40 backdrop-blur-xl border-b border-white/10 shadow-lg">
      <div class="container mx-auto px-4 lg:px-8 h-16 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary/20 rounded border border-primary/50 flex items-center justify-center font-bold text-xl text-primary shadow-[0_0_10px_rgba(11,218,81,0.3)]">B</div>
          <span class="text-xl font-bold tracking-widest uppercase text-white drop-shadow-md">BICAP</span>
        </div>
        
        <nav class="hidden md:flex items-center gap-8 font-medium text-sm tracking-wider">
          <a href="#solutions" class="text-gray-300 hover:text-primary transition-colors">SOLUTIONS</a>
          <a href="#network" class="text-gray-300 hover:text-primary transition-colors">NETWORK</a>
          <a href="#about" class="text-gray-300 hover:text-primary transition-colors">ABOUT US</a>
        </nav>
        
        <div class="flex items-center gap-4">
          <router-link to="/login">
            <Button variant="ghost" size="sm">Log In</Button>
          </router-link>
          <Button variant="primary" size="sm">Get Started</Button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-grow">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-black/40 backdrop-blur-md text-white py-12 border-t border-white/10 mt-20 relative z-10">
      <div class="container mx-auto px-4 lg:px-8 grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <div class="flex items-center gap-3 mb-4">
            <div class="w-8 h-8 bg-primary/20 rounded border border-primary/50 flex items-center justify-center font-bold text-xl text-primary shadow-[0_0_10px_rgba(11,218,81,0.3)]">B</div>
            <span class="text-xl font-bold tracking-widest uppercase">BICAP</span>
          </div>
          <p class="text-gray-400 text-sm">Securing the future of agriculture through transparent, immutable blockchain ledgers.</p>
        </div>
        <div>
          <h4 class="font-bold mb-4">Platform</h4>
          <ul class="space-y-2 text-sm text-gray-400">
            <li><a href="#" class="hover:text-primary">Traceability</a></li>
            <li><a href="#" class="hover:text-primary">Smart Contracts</a></li>
            <li><a href="#" class="hover:text-primary">Analytics</a></li>
          </ul>
        </div>
        <div>
          <h4 class="font-bold mb-4">Company</h4>
          <ul class="space-y-2 text-sm text-gray-400">
            <li><a href="#" class="hover:text-primary">About</a></li>
            <li><a href="#" class="hover:text-primary">Careers</a></li>
            <li><a href="#" class="hover:text-primary">Contact</a></li>
          </ul>
        </div>
        <div>
          <h4 class="font-bold mb-4">Legal</h4>
          <ul class="space-y-2 text-sm text-gray-400">
            <li><a href="#" class="hover:text-primary">Privacy Policy</a></li>
            <li><a href="#" class="hover:text-primary">Terms of Service</a></li>
          </ul>
        </div>
      </div>
      <div class="container mx-auto px-4 lg:px-8 mt-12 pt-8 border-t border-gray-800 text-sm text-gray-500 text-center">
        &copy; 2026 BICAP Blockchain Supply Chain. All rights reserved.
      </div>
    </footer>
  </div>
</template>

<script setup>
import Button from '../components/ui/Button.vue'
</script>
