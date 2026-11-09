<template>
  <span class="badge blockchain-status-badge" :class="badgeClass" :title="tooltipText">
    <span class="badge-icon">{{ icon }}</span>
    <span class="badge-label">{{ label }}</span>
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: String,
    default: 'NOT_RECORDED',
    validator: (v) => ['SUCCESS', 'PENDING', 'FAILED', 'NOT_RECORDED', 'VERIFIED', 'MISMATCH', 'CONNECTION_ERROR'].includes(v)
  },
  size: {
    type: String,
    default: 'md' // sm | md | lg
  }
})

const STATUS_MAP = {
  VERIFIED:         { label: 'Đã xác thực trên Blockchain', icon: '✅', cls: 'status-verified' },
  SUCCESS:          { label: 'Đã ghi nhận',                 icon: '✅', cls: 'status-verified' },
  MISMATCH:         { label: 'Dữ liệu không khớp blockchain', icon: '⚠️', cls: 'status-mismatch' },
  FAILED:           { label: 'Ghi nhận thất bại',           icon: '❌', cls: 'status-failed' },
  NOT_RECORDED:     { label: 'Chưa ghi nhận Blockchain',    icon: '⚪', cls: 'status-not-recorded' },
  PENDING:          { label: 'Đang chờ xác nhận',           icon: '⏳', cls: 'status-pending' },
  CONNECTION_ERROR: { label: 'Không thể kết nối blockchain', icon: '🔌', cls: 'status-connection-error' },
}

const resolved = computed(() => STATUS_MAP[props.status] || STATUS_MAP['NOT_RECORDED'])
const label       = computed(() => resolved.value.label)
const icon        = computed(() => resolved.value.icon)
const badgeClass  = computed(() => [resolved.value.cls, `badge-${props.size}`])
const tooltipText = computed(() => label.value)
</script>

<style scoped>
.blockchain-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 20px;
  padding: 3px 10px;
  white-space: nowrap;
}
.badge-sm { font-size: 0.65rem; padding: 2px 7px; }
.badge-lg { font-size: 0.85rem; padding: 5px 14px; }

.status-verified        { background: #d1fae5; color: #065f46; border: 1px solid #6ee7b7; }
.status-mismatch        { background: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; }
.status-failed          { background: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; }
.status-not-recorded    { background: #f3f4f6; color: #374151; border: 1px solid #d1d5db; }
.status-pending         { background: #fef3c7; color: #92400e; border: 1px solid #fcd34d; }
.status-connection-error{ background: #ffedd5; color: #9a3412; border: 1px solid #fdba74; }
</style>

