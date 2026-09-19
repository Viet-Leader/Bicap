<template>
  <button 
    class="app-button" 
    :class="[variant, size, { disabled, block }]" 
    :disabled="disabled"
    @click="$emit('click')"
  >
    <slot name="icon-left" />
    <span class="app-button__text"><slot /></span>
    <slot name="icon-right" />
  </button>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'primary' }, // primary, secondary, outline, danger
  size: { type: String, default: 'md' }, // sm, md, lg
  disabled: { type: Boolean, default: false },
  block: { type: Boolean, default: false }
});
defineEmits(['click']);
</script>

<style scoped lang="scss">
.app-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: $space-2;
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 600;
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all 0.2s;
  
  &.block {
    width: 100%;
  }

  &.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  // Sizes
  &.sm { padding: 6px 12px; font-size: 14px; }
  &.md { padding: 10px 20px; font-size: 16px; }
  &.lg { padding: 14px 28px; font-size: 18px; }

  // Variants
  &.primary {
    background: $gradient-hero;
    color: $bg-deep;
    box-shadow: $shadow-glow;
    
    &:hover:not(.disabled) {
      filter: brightness(1.1);
      transform: translateY(-1px);
    }
  }

  &.secondary {
    background: rgba(255, 255, 255, 0.1);
    color: $text-primary;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    &:hover:not(.disabled) {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  &.outline {
    background: transparent;
    color: $primary-cyan;
    border: 1px solid $primary-cyan;
    
    &:hover:not(.disabled) {
      background: rgba(0, 217, 255, 0.1);
    }
  }

  &.danger {
    background: rgba($accent-red, 0.2);
    color: $accent-red;
    border: 1px solid rgba($accent-red, 0.5);
    
    &:hover:not(.disabled) {
      background: rgba($accent-red, 0.3);
    }
  }
}
</style>
