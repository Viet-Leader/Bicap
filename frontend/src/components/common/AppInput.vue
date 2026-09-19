<template>
  <div class="app-input-wrapper" :class="{ error: !!error }">
    <label v-if="label" class="app-input-label">{{ label }}</label>
    <div class="app-input-container">
      <slot name="prefix" />
      <input
        class="app-input"
        :type="type"
        :placeholder="placeholder"
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        v-bind="$attrs"
      />
      <slot name="suffix" />
    </div>
    <span v-if="error" class="app-input-error">{{ error }}</span>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  error: { type: String, default: '' }
});
defineEmits(['update:modelValue']);
</script>

<style scoped lang="scss">
.app-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: $space-1;
  margin-bottom: $space-3;

  .app-input-label {
    font-size: 14px;
    font-weight: 500;
    color: $text-secondary;
  }

  .app-input-container {
    display: flex;
    align-items: center;
    background: rgba(15, 22, 41, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: $radius-md;
    padding: 0 $space-3;
    transition: all 0.2s;
    
    &:focus-within {
      border-color: $primary-cyan;
      box-shadow: 0 0 0 2px rgba($primary-cyan, 0.2);
    }
  }

  .app-input {
    flex: 1;
    background: transparent;
    border: none;
    color: $text-primary;
    padding: 10px 0;
    font-family: inherit;
    font-size: 14px;
    outline: none;

    &::placeholder {
      color: rgba(255, 255, 255, 0.3);
    }
  }

  .app-input-error {
    font-size: 12px;
    color: $accent-red;
    margin-top: 2px;
  }
  
  &.error {
    .app-input-container {
      border-color: $accent-red;
    }
  }
}
</style>
