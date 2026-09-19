<template>
  <span class="app-badge" :class="[variant, size, { pulse: pulse }]">
    <slot />
  </span>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'success' }, // success, warning, danger, info, default
  size: { type: String, default: 'md' }, // sm, md
  pulse: { type: Boolean, default: false }
});
</script>

<style scoped lang="scss">
.app-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-radius: $radius-sm;
  
  // Sizes
  &.sm { padding: 2px 6px; font-size: 10px; }
  &.md { padding: 4px 8px; font-size: 12px; }

  // Variants
  &.success {
    background: rgba($primary-green, 0.1);
    color: $primary-green;
    border: 1px solid rgba($primary-green, 0.2);
  }
  &.warning {
    background: rgba($accent-amber, 0.1);
    color: $accent-amber;
    border: 1px solid rgba($accent-amber, 0.2);
    
    &.pulse {
      animation: pulse-amber 2s infinite;
    }
  }
  &.danger {
    background: rgba($accent-red, 0.1);
    color: $accent-red;
    border: 1px solid rgba($accent-red, 0.2);
  }
  &.info {
    background: rgba($primary-cyan, 0.1);
    color: $primary-cyan;
    border: 1px solid rgba($primary-cyan, 0.2);
  }
  &.default {
    background: rgba(255, 255, 255, 0.1);
    color: $text-secondary;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
}

@keyframes pulse-amber {
  0% { box-shadow: 0 0 0 0 rgba($accent-amber, 0.4); }
  70% { box-shadow: 0 0 0 6px rgba($accent-amber, 0); }
  100% { box-shadow: 0 0 0 0 rgba($accent-amber, 0); }
}
</style>
