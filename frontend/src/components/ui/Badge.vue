<template>
  <span
    :class="[
      'inline-flex items-center px-2 py-1 text-xs font-mono uppercase rounded',
      variants[variant]
    ]"
  >
    <slot></slot>
  </span>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'default' // verified, pending, error, default
  }
})

const variants = {
  verified: 'bg-primary/20 text-primary border border-primary/50 shadow-[0_0_10px_rgba(11,218,81,0.2)]',
  pending: 'bg-accent/20 text-accent border border-accent/50 shadow-[0_0_10px_rgba(56,189,248,0.2)]',
  error: 'bg-error/20 text-error border border-error/50 shadow-[0_0_10px_rgba(239,68,68,0.2)]',
  default: 'bg-white/10 text-gray-300 border border-white/20 backdrop-blur-md'
}
</script>
