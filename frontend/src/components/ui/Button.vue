<template>
  <button
    :class="[
      'inline-flex items-center justify-center rounded transition-colors font-semibold',
      sizes[size],
      variants[variant]
    ]"
    :disabled="disabled"
    @click="$emit('click', $event)"
  >
    <slot></slot>
  </button>
</template>

<script setup>
const props = defineProps({
  variant: {
    type: String,
    default: 'primary' // primary, secondary, outline, danger
  },
  size: {
    type: String,
    default: 'md' // sm, md, lg
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click'])

const variants = {
  primary: 'bg-primary text-secondary border border-primary hover:bg-primary/90 shadow-[0_0_15px_rgba(11,218,81,0.3)] hover:shadow-[0_0_25px_rgba(11,218,81,0.5)] disabled:opacity-50 disabled:cursor-not-allowed',
  outline: 'bg-white/5 backdrop-blur-md text-white border border-white/20 hover:bg-white/10 disabled:opacity-50',
  danger: 'bg-error/80 backdrop-blur-md text-white border border-error hover:bg-error shadow-[0_0_15px_rgba(239,68,68,0.3)]',
  ghost: 'bg-transparent text-gray-300 hover:bg-white/10 hover:text-white'
}

const sizes = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-base',
  lg: 'px-6 py-3 text-lg'
}
</script>
