<template>
  <!-- Backdrop -->
  <div v-if="open" class="tx-drawer-backdrop" @click.self="$emit('close')">
    <div class="tx-drawer">
      <!-- Header -->
      <div class="tx-drawer-header">
        <h5 class="mb-0">⛓️ Chi tiết Giao dịch Blockchain</h5>
        <button class="btn-close-drawer" type="button" @click="$emit('close')">×</button>
      </div>

      <!-- Body -->
      <div class="tx-drawer-body" v-if="tx">
        <!-- Status banner -->
        <div class="mb-4">
          <BlockchainStatusBadge :status="tx.blockchainStatus || tx.status" size="lg" />
        </div>

        <!-- Hashes -->
        <div class="info-block mb-3">
          <div class="info-row">
            <span class="info-label">Transaction Hash</span>
            <HashDisplay :hash="tx.blockchainTxHash || tx.txHash" :prefix-len="10" :suffix-len="8" />
          </div>
          <div class="info-row" v-if="tx.dataHash">
            <span class="info-label">Data Hash</span>
            <HashDisplay :hash="tx.dataHash" :prefix-len="10" :suffix-len="8" />
          </div>
          <div class="info-row" v-if="tx.blockNumber">
            <span class="info-label">Block Number</span>
            <code>{{ tx.blockNumber }}</code>
          </div>
          <div class="info-row" v-if="tx.entityType || tx.recordType">
            <span class="info-label">Loại dữ liệu</span>
            <span class="badge" :class="tx.entityType === 'ProductBatch' ? 'bg-info text-white' : 'bg-success text-white'">
              {{ tx.entityType || tx.recordType }}
            </span>
          </div>
          <div class="info-row" v-if="tx.createdAt || tx.blockchainCreatedAt">
            <span class="info-label">Thời gian ghi</span>
            <span>{{ formatVnDateTime(tx.createdAt || tx.blockchainCreatedAt) }}</span>
          </div>
        </div>

        <!-- Verify section -->
        <div class="verify-section mb-3">
          <div class="d-flex align-items-center gap-2 mb-2">
            <span class="fw-bold" style="font-size:0.85rem">Kết quả xác minh:</span>
            <BlockchainStatusBadge v-if="verifyResult" :status="verifyResult" />
            <span v-else class="text-muted" style="font-size:0.8rem">Chưa xác minh</span>
          </div>
          <div v-if="verifyResult === 'VERIFIED'" class="alert alert-success py-2 mb-2">✅ Dữ liệu khớp với blockchain</div>
          <div v-else-if="verifyResult === 'MISMATCH'" class="alert alert-danger py-2 mb-2">⚠️ Dữ liệu đã bị thay đổi so với blockchain</div>
          <div v-else-if="verifyResult === 'CONNECTION_ERROR'" class="alert alert-warning py-2 mb-2">🔌 Không thể kết nối blockchain</div>
          <VerifyButton :is-verifying="isVerifying" label="Xác minh tính toàn vẹn" @verify="runVerify" />
        </div>

        <!-- Payload JSON -->
        <div v-if="tx.payload || tx.rawData" class="payload-section">
          <div class="d-flex align-items-center justify-content-between mb-1">
            <span class="fw-bold" style="font-size:0.85rem">Payload gốc:</span>
            <button class="btn btn-sm btn-outline-secondary py-0" @click="showPayload = !showPayload">
              {{ showPayload ? 'Thu gọn' : 'Mở rộng' }}
            </button>
          </div>
          <pre v-if="showPayload" class="payload-json">{{ formatPayload(tx.payload || tx.rawData) }}</pre>
        </div>
      </div>

      <div v-else class="tx-drawer-body text-center text-muted py-5">
        Không có dữ liệu giao dịch
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axiosClient from '@/api/axiosClient'
import BlockchainStatusBadge from './BlockchainStatusBadge.vue'
import HashDisplay from './HashDisplay.vue'
import VerifyButton from './VerifyButton.vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  tx:   { type: Object, default: null },
  // verifyEndpoint: e.g. /blockchain/verify/product-batch/123
  verifyEndpoint: { type: String, default: '' },
})

const emit = defineEmits(['close'])

const verifyResult = ref('')
const isVerifying = ref(false)
const showPayload = ref(false)

const runVerify = async () => {
  if (!props.verifyEndpoint) return
  isVerifying.value = true
  verifyResult.value = ''
  try {
    const { data } = await axiosClient.get(props.verifyEndpoint)
    verifyResult.value = data?.status || data?.verificationStatus || 'VERIFIED'
  } catch (e) {
    verifyResult.value = 'CONNECTION_ERROR'
  } finally {
    isVerifying.value = false
  }
}

const formatPayload = (p) => {
  try { return JSON.stringify(typeof p === 'string' ? JSON.parse(p) : p, null, 2) }
  catch { return String(p) }
}

const formatVnDateTime = (val) => {
  if (!val) return ''
  return new Date(val).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  })
}
</script>

<style scoped>
.tx-drawer-backdrop {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.45);
  z-index: 1050;
  display: flex; justify-content: flex-end;
}
.tx-drawer {
  width: 480px; max-width: 95vw;
  background: #fff;
  height: 100%;
  overflow-y: auto;
  box-shadow: -4px 0 24px rgba(0,0,0,0.15);
  display: flex; flex-direction: column;
}
.tx-drawer-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.2rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  position: sticky; top: 0; background: #fff; z-index: 1;
}
.btn-close-drawer {
  background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #6b7280;
}
.tx-drawer-body { padding: 1.5rem; flex: 1; }
.info-block { background: #f9fafb; border-radius: 8px; padding: 1rem; }
.info-row { display: flex; align-items: center; gap: 8px; padding: 4px 0; font-size: 0.82rem; }
.info-label { color: #6b7280; min-width: 130px; font-weight: 500; }
.verify-section { background: #f0fdf4; border-radius: 8px; padding: 1rem; }
.payload-section { background: #1e293b; border-radius: 8px; padding: 1rem; }
.payload-json {
  color: #e2e8f0; font-size: 0.72rem; white-space: pre-wrap;
  word-break: break-all; max-height: 320px; overflow-y: auto; margin: 0;
}
</style>

