<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import axiosClient from '@/api/axiosClient'
import BlockchainStatusBadge from '@/components/blockchain/BlockchainStatusBadge.vue'
import HashDisplay from '@/components/blockchain/HashDisplay.vue'

const route = useRoute()
const traceability = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

// Trạng thái xác minh blockchain (5 trạng thái chuẩn)
const verificationStatus = ref('') // VERIFIED | MISMATCH | NOT_RECORDED | CONNECTION_ERROR | PENDING
const isVerifying = ref(false)
const hasVerified = ref(false)

const batchId = computed(() => route.params.batchId)

// Dùng verificationStatus để điều khiển toàn bộ UI
const isVerified = computed(() => verificationStatus.value === 'VERIFIED')

const formatVnDate = (value) =>
  value ? new Date(value).toLocaleDateString('vi-VN') : 'Chưa cập nhật'

const formatVnDateTime = (value) =>
  value ? new Date(value).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  }) : ''

// Map trạng thái activity blockchain sang chuẩn 5 trạng thái
const mapActivityStatus = (status) => {
  if (!status) return 'NOT_RECORDED'
  const map = {
    SUCCESS: 'VERIFIED',
    VERIFIED: 'VERIFIED',
    PENDING: 'PENDING',
    FAILED: 'FAILED',
    NOT_RECORDED: 'NOT_RECORDED',
  }
  return map[status] || 'NOT_RECORDED'
}

const loadTraceability = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const { data } = await axiosClient.get(`/public/traceability/batch/${encodeURIComponent(batchId.value)}`)
    traceability.value = {
      ...data,
      batchId: data.batchId || data.batchCode,
      batchCode: data.batchCode,
      category: data.cropName || 'Nông sản',
      icon: '🌾',
      harvestDate: formatVnDate(data.actualHarvestDate || data.expectedHarvestDate),
      farmLocation: data.farmAddress || data.farmLocation || '',
      farmArea: data.farmArea ? `${data.farmArea} ha` : '',
      farmRating: data.farmRating || '',
      certifications: data.certifications || [],
      blockchainStatus: data.blockchainStatus || 'NOT_RECORDED',
      txHash: data.blockchainTxHash || data.txHash || '',
      quality: data.quality || { freshness: 0, safety: 0, organic: 0 },
      timeline: (data.activities || []).map((activity) => ({
        stage: activity.activityTypeName || activity.activityType,
        title: activity.activityTypeName || activity.activityType,
        description: activity.description,
        date: formatVnDate(activity.activityTime),
        location: data.farmName,
        txHash: activity.blockchainTxHash || activity.txHash || '',
        blockchainStatus: mapActivityStatus(activity.blockchainStatus || (activity.blockchainTxHash ? 'SUCCESS' : '')),
      })),
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Không thể tải dữ liệu hành trình sản phẩm.'
  } finally {
    isLoading.value = false
  }
}

// Auto-verify on mount sau khi load data
const verifyBatch = async () => {
  if (!batchId.value) return
  isVerifying.value = true
  try {
    const { data } = await axiosClient.get(`/public/traceability/batch/${encodeURIComponent(batchId.value)}/verify`)
    // Backend trả về { status: 'VERIFIED' | 'MISMATCH' | 'NOT_RECORDED' | 'CONNECTION_ERROR' }
    verificationStatus.value = data?.status || data?.verificationStatus || 'NOT_RECORDED'
    hasVerified.value = true
  } catch (e) {
    if (e.response?.status === 404) {
      verificationStatus.value = 'NOT_RECORDED'
    } else {
      verificationStatus.value = 'CONNECTION_ERROR'
    }
    hasVerified.value = true
  } finally {
    isVerifying.value = false
  }
}

onMounted(async () => {
  await loadTraceability()
  // Tự động verify sau khi load data
  await verifyBatch()
})
</script>

<template>
  <div class="traceability-page">
    <div class="container py-4">
      <!-- Loading -->
      <div v-if="isLoading" class="text-center py-5 my-5">
        <div class="spinner-border text-success" role="status"></div>
        <p class="mt-3 text-muted">Đang tải dữ liệu từ Blockchain...</p>
      </div>

      <!-- Error -->
      <div v-else-if="errorMessage" class="alert alert-danger text-center my-5 py-4" role="alert">
        <i class="material-symbols-rounded fs-1 mb-2">error</i>
        <h5 class="alert-heading">Lỗi truy xuất!</h5>
        <p>{{ errorMessage }}</p>
        <button @click="loadTraceability" class="btn btn-outline-danger mt-3">Thử lại</button>
      </div>

      <template v-else>

        <!-- === BANNER TRẠNG THÁI BLOCKCHAIN === -->
        <div v-if="hasVerified || isVerifying" class="mb-4">
          <!-- MISMATCH -->
          <div v-if="verificationStatus === 'MISMATCH'" class="alert alert-danger d-flex align-items-center gap-2">
            <span style="font-size:1.5rem">⚠️</span>
            <div>
              <strong>Dữ liệu đã bị thay đổi so với blockchain!</strong>
              Vui lòng liên hệ nhà sản xuất để xác nhận tính toàn vẹn của lô hàng này.
            </div>
          </div>
          <!-- NOT_RECORDED -->
          <div v-else-if="verificationStatus === 'NOT_RECORDED'" class="alert alert-secondary d-flex align-items-center gap-2">
            <span style="font-size:1.5rem">⚪</span>
            <div>
              <strong>Lô hàng này chưa được ghi nhận lên blockchain.</strong>
              Dữ liệu truy xuất chưa được xác minh độc lập trên mạng blockchain.
            </div>
          </div>
          <!-- CONNECTION_ERROR -->
          <div v-else-if="verificationStatus === 'CONNECTION_ERROR'" class="alert alert-warning d-flex align-items-center gap-2">
            <span style="font-size:1.5rem">🔌</span>
            <div class="flex-grow-1">
              <strong>Không thể kết nối blockchain.</strong>
              Hệ thống đang gặp sự cố kết nối tạm thời.
            </div>
            <button class="btn btn-warning btn-sm ms-2" @click="verifyBatch">Thử lại</button>
          </div>
          <!-- VERIFIED -->
          <div v-else-if="verificationStatus === 'VERIFIED'" class="alert alert-success d-flex align-items-center gap-2">
            <span style="font-size:1.5rem">✅</span>
            <strong>Dữ liệu đã được xác thực trên blockchain. Thông tin hoàn toàn tin cậy.</strong>
          </div>
        </div>

        <!-- Product Header -->
        <div class="card border-0 shadow-sm mb-4">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-md-2 text-center mb-3 mb-md-0">
                <div style="width: 100px; height: 100px; border-radius: 1rem; background: linear-gradient(135deg, #E8F5E9, #C8E6C9); display: flex; align-items: center; justify-content: center; margin: 0 auto;">
                  <span style="font-size: 3rem;">{{ traceability.icon || '🥬' }}</span>
                </div>
              </div>
              <div class="col-md-7">
                <div class="d-flex align-items-center flex-wrap gap-2 mb-2">
                  <!-- Badge trạng thái xác minh thật (không hard-code) -->
                  <span v-if="isVerifying" class="badge" style="background:#fef3c7;color:#92400e;border:1px solid #fcd34d;">
                    <span class="spinner-border spinner-border-sm me-1" style="width:10px;height:10px"></span>
                    Đang xác minh...
                  </span>
                  <BlockchainStatusBadge v-else-if="hasVerified" :status="verificationStatus" size="md" />
                  <span class="badge bg-info-subtle text-info">{{ traceability.category }}</span>
                </div>
                <h3 class="font-weight-bold mb-1">{{ traceability.productName }}</h3>
                <p class="text-muted mb-2">📍 {{ traceability.farmName }} | 📅 Thu hoạch: {{ traceability.harvestDate }}</p>
                <div class="d-flex flex-wrap gap-2">
                  <span v-for="cert in traceability.certifications" :key="cert" class="cert-badge">✓ {{ cert }}</span>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div style="width: 120px; height: 120px; background: white; border: 2px solid #E8F5E9; border-radius: 1rem; display: flex; align-items: center; justify-content: center; margin: 0 auto;">
                  <div style="font-size: 5rem; line-height: 1;">📱</div>
                </div>
                <small class="text-muted d-block mt-2">Mã: {{ traceability.batchCode || traceability.batchId }}</small>
              </div>
            </div>
          </div>
        </div>

        <div class="row">
          <!-- Timeline -->
          <div class="col-lg-8">
            <div class="card border-0 shadow-sm mb-4">
              <div class="card-header bg-white border-0 d-flex justify-content-between align-items-center">
                <h5 class="mb-0">📋 Hành Trình Sản Phẩm</h5>
                <span class="text-muted small">{{ traceability.timeline.length }} sự kiện</span>
              </div>
              <div class="card-body">
                <div v-if="traceability.timeline.length === 0" class="alert alert-light text-center">
                  Chưa có sự kiện canh tác nào được ghi nhận.
                </div>
                <div class="timeline">
                  <div
                    v-for="(event, index) in traceability.timeline"
                    :key="index"
                    class="timeline-item"
                    :class="{
                      'timeline-verified': event.blockchainStatus === 'VERIFIED',
                      'timeline-pending': event.blockchainStatus === 'PENDING',
                      'timeline-failed': event.blockchainStatus === 'FAILED',
                    }"
                  >
                    <div class="d-flex justify-content-between align-items-start mb-2">
                      <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                          <span class="badge bg-success-subtle text-success">{{ event.stage }}</span>
                          <!-- Badge trạng thái blockchain của từng event -->
                          <BlockchainStatusBadge :status="event.blockchainStatus" size="sm" />
                        </div>
                        <h6 class="font-weight-bold mb-1">{{ event.title }}</h6>
                        <p class="text-muted small mb-1">{{ event.description }}</p>
                        <small class="text-muted">📅 {{ event.date }} | 📍 {{ event.location }}</small>
                      </div>
                    </div>
                    <!-- TxHash của từng event (lấy từ API thật, không null) -->
                    <div v-if="event.txHash" class="mt-2 p-2 bg-light rounded d-flex justify-content-between align-items-center">
                      <div>
                        <small class="text-muted d-block">🔗 Transaction Hash:</small>
                        <HashDisplay :hash="event.txHash" :prefix-len="8" :suffix-len="6" />
                      </div>
                    </div>
                    <div v-else class="mt-1">
                      <small class="text-muted">Chưa có transaction hash</small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Sidebar Info -->
          <div class="col-lg-4">
            <!-- Blockchain Verification Card -->
            <div class="card border-0 shadow-sm mb-4">
              <div class="card-header bg-white border-0 d-flex justify-content-between align-items-center">
                <h6 class="mb-0">⛓️ Xác minh Blockchain</h6>
                <!-- Nút Xác minh lại (manual re-verify) -->
                <button
                  class="btn btn-sm btn-outline-success"
                  :disabled="isVerifying"
                  @click="verifyBatch"
                >
                  <span v-if="isVerifying" class="spinner-border spinner-border-sm me-1" style="width:12px;height:12px"></span>
                  <span v-else>🔄</span>
                  {{ isVerifying ? 'Đang xác minh...' : 'Xác minh lại' }}
                </button>
              </div>
              <div class="card-body">
                <!-- Trạng thái xác minh -->
                <div class="mb-3">
                  <div v-if="isVerifying" class="d-flex align-items-center gap-2 text-muted">
                    <span class="spinner-border spinner-border-sm"></span>
                    Đang xác minh tính toàn vẹn dữ liệu...
                  </div>
                  <BlockchainStatusBadge v-else-if="hasVerified" :status="verificationStatus" size="lg" />
                  <span v-else class="text-muted small">Nhấn "Xác minh lại" để kiểm tra</span>
                </div>

                <!-- TxHash của batch -->
                <div v-if="traceability.txHash" class="bg-light rounded p-2 mb-2">
                  <small class="text-muted d-block mb-1">Transaction Hash của lô:</small>
                  <HashDisplay :hash="traceability.txHash" :prefix-len="10" :suffix-len="8" />
                  <small v-if="traceability.blockchainCreatedAt" class="d-block text-muted mt-1">
                    Ghi nhận: {{ formatVnDateTime(traceability.blockchainCreatedAt) }}
                  </small>
                </div>
                <p v-else class="small text-muted mb-0">Chưa có transaction hash để đối chiếu trên mạng blockchain.</p>
              </div>
            </div>

            <!-- Farm Info -->
            <div class="card border-0 shadow-sm mb-4">
              <div class="card-header bg-white border-0">
                <h6 class="mb-0">🌾 Thông tin Nông trại</h6>
              </div>
              <div class="card-body">
                <div class="d-flex align-items-center mb-3">
                  <div style="width: 50px; height: 50px; border-radius: 50%; background: linear-gradient(135deg, #4CAF50, #2E7D32); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; margin-right: 12px;">F</div>
                  <div>
                    <h6 class="mb-0 font-weight-bold">{{ traceability.farmName }}</h6>
                    <small class="text-muted">📍 {{ traceability.farmLocation || 'Đang cập nhật' }}</small>
                  </div>
                </div>
                <div v-if="traceability.farmArea" class="mb-2"><span class="text-muted">Diện tích:</span> {{ traceability.farmArea }}</div>
                <div v-if="traceability.certifications.length" class="mb-2"><span class="text-muted">Tiêu chuẩn:</span> {{ traceability.certifications.join(', ') }}</div>
                <div v-if="traceability.farmRating"><span class="text-muted">Đánh giá:</span> ⭐ {{ traceability.farmRating }}/5</div>
              </div>
            </div>

            <!-- Quality Info (chỉ hiện nếu có dữ liệu thật) -->
            <div v-if="traceability.quality && (traceability.quality.freshness || traceability.quality.safety || traceability.quality.organic)" class="card border-0 shadow-sm">
              <div class="card-header bg-white border-0">
                <h6 class="mb-0">📊 Chỉ số chất lượng</h6>
              </div>
              <div class="card-body">
                <div class="mb-3">
                  <div class="d-flex justify-content-between mb-1">
                    <small>Độ tươi</small>
                    <small class="text-success font-weight-bold">{{ traceability.quality.freshness }}%</small>
                  </div>
                  <div class="progress" style="height: 6px;">
                    <div class="progress-bar bg-success" :style="{ width: traceability.quality.freshness + '%' }"></div>
                  </div>
                </div>
                <div class="mb-3">
                  <div class="d-flex justify-content-between mb-1">
                    <small>An toàn</small>
                    <small class="text-success font-weight-bold">{{ traceability.quality.safety }}%</small>
                  </div>
                  <div class="progress" style="height: 6px;">
                    <div class="progress-bar bg-success" :style="{ width: traceability.quality.safety + '%' }"></div>
                  </div>
                </div>
                <div>
                  <div class="d-flex justify-content-between mb-1">
                    <small>Organic</small>
                    <small class="text-success font-weight-bold">{{ traceability.quality.organic }}%</small>
                  </div>
                  <div class="progress" style="height: 6px;">
                    <div class="progress-bar bg-success" :style="{ width: traceability.quality.organic + '%' }"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </template>
    </div>
  </div>
</template>

<style scoped>
.progress { background-color: #e9ecef; border-radius: 4px; overflow: hidden; }
.progress-bar { transition: width 0.6s ease; }

.cert-badge {
  background: #E8F5E9;
  color: #2E7D32;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
}

.timeline { }
.timeline-item {
  position: relative;
  padding-left: 30px;
  padding-bottom: 24px;
  border-left: 2px solid #d1d5db;
  margin-left: 10px;
}
.timeline-item::before {
  content: '';
  position: absolute;
  left: -8px;
  top: 4px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #d1d5db;
}
/* Màu timeline theo trạng thái xác minh blockchain của từng event */
.timeline-verified  { border-left-color: #4CAF50; }
.timeline-verified::before  { background: #4CAF50; }
.timeline-pending   { border-left-color: #f59e0b; }
.timeline-pending::before   { background: #f59e0b; }
.timeline-failed    { border-left-color: #ef4444; }
.timeline-failed::before    { background: #ef4444; }
</style>
