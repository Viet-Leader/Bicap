<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4"
  >
    <div class="glass-panel w-full max-w-lg overflow-hidden flex flex-col rounded-xl border border-white/20">
      <div class="flex justify-between items-center p-4 border-b border-white/10 bg-white/5">
        <h3 class="text-lg font-bold text-white">{{ title }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-error text-xl font-bold leading-none transition-colors">
          &times;
        </button>
      </div>
      <div class="p-6 overflow-y-auto text-gray-200">
        <slot></slot>
      </div>
      <div v-if="$slots.footer" class="p-4 border-t border-white/10 bg-white/5 flex justify-end gap-2">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: 'Modal Title'
  }
})

defineEmits(['close'])
</script>
