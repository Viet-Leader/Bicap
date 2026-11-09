<template>
  <button
    class="btn btn-verify"
    type="button"
    :disabled="isVerifying || disabled"
    :title="isVerifying ? 'Đang xác minh...' : 'Xác minh tính toàn vẹn dữ liệu trên blockchain'"
    @click="$emit('verify')"
  >
    <span v-if="isVerifying" class="spinner-border spinner-border-sm me-1" role="status"></span>
    <span v-else class="verify-icon me-1">🔍</span>
    {{ isVerifying ? 'Đang xác minh...' : label }}
  </button>
</template>

<script setup>
defineProps({
  isVerifying: { type: Boolean, default: false },
  disabled:    { type: Boolean, default: false },
  label:       { type: String, default: 'Xác minh' },
})

defineEmits(['verify'])
</script>

<style scoped>
.btn-verify {
  font-size: 0.75rem;
  padding: 3px 10px;
  background: transparent;
  border: 1px solid #6b7280;
  border-radius: 6px;
  color: #374151;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  transition: all 0.15s;
}
.btn-verify:hover:not(:disabled) {
  background: #f0fdf4;
  border-color: #16a34a;
  color: #15803d;
}
.btn-verify:disabled { opacity: 0.6; cursor: not-allowed; }
</style>

