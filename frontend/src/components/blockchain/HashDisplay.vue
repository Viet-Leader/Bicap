<template>
  <span v-if="hash" class="hash-display" :title="hash">
    <code class="hash-short">{{ shortHash }}</code>
    <button
      class="btn-copy"
      type="button"
      :title="copied ? 'Đã sao chép!' : 'Sao chép hash'"
      @click.stop="copyHash"
    >
      <span v-if="copied">✓</span>
      <span v-else>⧉</span>
    </button>
    <span v-if="copied" class="copy-toast">Đã chép!</span>
  </span>
  <span v-else class="text-muted" style="font-size:0.8rem">—</span>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  hash: { type: String, default: '' },
  prefixLen: { type: Number, default: 6 },
  suffixLen: { type: Number, default: 4 },
})

const copied = ref(false)

const shortHash = computed(() => {
  if (!props.hash) return ''
  if (props.hash.length <= props.prefixLen + props.suffixLen + 3) return props.hash
  const prefix = props.hash.slice(0, props.prefixLen)
  const suffix = props.hash.slice(-props.suffixLen)
  return `${prefix}…${suffix}`
})

const copyHash = async () => {
  try {
    await navigator.clipboard.writeText(props.hash)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  } catch {
    // fallback
  }
}
</script>

<style scoped>
.hash-display {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.78rem;
  position: relative;
}
.hash-short {
  font-family: monospace;
  background: #f1f5f9;
  padding: 1px 6px;
  border-radius: 4px;
  color: #0f4c35;
  cursor: help;
}
.btn-copy {
  background: none;
  border: none;
  cursor: pointer;
  color: #6b7280;
  padding: 0 2px;
  font-size: 0.8rem;
  line-height: 1;
}
.btn-copy:hover { color: #111827; }
.copy-toast {
  font-size: 0.65rem;
  color: #065f46;
  font-weight: 600;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0 } to { opacity: 1 } }
</style>

