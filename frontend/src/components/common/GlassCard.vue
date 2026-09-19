<template>
  <div class="glass-card" :class="[variant, { glow: glow }]">
    <div v-if="glow" class="glass-card__glow"></div>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'default' }, // default, elevated, outline
  glow: { type: Boolean, default: false }
});
</script>

<style scoped lang="scss">
.glass-card {
  position: relative;
  @include glass-card;
  padding: $space-5;
  transition: all 0.3s ease;
  
  &.elevated {
    background: lighten($bg-glass, 5%);
    box-shadow: $shadow-md;
  }
  
  &.outline {
    background: transparent;
    border-color: rgba($primary-cyan, 0.3);
  }

  &:hover {
    @include glass-card-hover;
  }
  
  &__glow {
    position: absolute;
    inset: -1px;
    background: $gradient-hero;
    border-radius: inherit;
    opacity: 0.15;
    z-index: -1;
    filter: blur(20px);
  }
}
</style>
