<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const traceability = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')
const copied = ref(false)

const batchId = computed(() => route.params.batchCode)
const isVerified = computed(() => traceability.value?.blockchainStatus === 'SUCCESS')

const loadTraceability = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    // Mocking an API call
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    if (batchId.value === 'INVALID') {
      throw new Error('Mã lô hàng không tồn tại trên hệ thống.')
    }
    
    traceability.value = {
      productName: 'Xoài Cát Hòa Lộc',
      batchId: batchId.value,
      category: 'Trái cây',
      icon: '🥭',
      harvestDate: '10/05/2026',
      farmName: 'Trang trại Xoài Cát Hòa Lộc',
      farmLocation: 'Tiền Giang',
      farmArea: '5 ha',
      farmRating: 4.8,
      certifications: ['VietGAP', 'GlobalGAP'],
      blockchainStatus: 'SUCCESS',
      contractAddress: '0xabc123...def456',
      quality: {
          freshness: 98,
          safety: 100,
          organic: 95
      },
      timeline: [
        { stage: 'Gieo trồng', title: 'Bắt đầu vụ mùa', description: 'Gieo 500 cây giống loại A', date: '15/01/2026', location: 'Khu A', txHash: '0x1c44a82f9b21f4d1e3a89b21f4d1e3a89b21f4d1' },
        { stage: 'Chăm sóc', title: 'Bón phân hữu cơ', description: 'Sử dụng phân bò ủ vi sinh', date: '20/02/2026', location: 'Khu A', txHash: '0x2d55b930ac32g5e2f4b90c32g5e2f4b90c32g5e2' },
        { stage: 'Thu hoạch', title: 'Thu hoạch Xoài Cát', description: 'Đạt sản lượng 500kg', date: '10/05/2026', location: 'Khu A', txHash: '0x3e66c041bd43h6f3g5c01d43h6f3g5c01d43h6f3' }
      ]
    }
  } catch (error) {
    errorMessage.value = error.message || 'Không thể tải dữ liệu hành trình sản phẩm.'
  } finally {
    isLoading.value = false
  }
}

const copyHash = async (hash) => {
  if (!hash) return
  await navigator.clipboard.writeText(hash)
  copied.value = true
  window.setTimeout(() => { copied.value = false }, 1800)
}

onMounted(loadTraceability)
</script>

<template>
  <div class="traceability-page">
    <div class="container py-4">
        <div v-if="isLoading" class="text-center py-5 my-5">
            <div class="spinner-border text-success" role="status"></div>
            <p class="mt-3 text-muted">Đang tải dữ liệu từ Blockchain...</p>
        </div>

        <div v-else-if="errorMessage" class="alert alert-danger text-center my-5 py-4" role="alert">
            <i class="material-symbols-rounded fs-1 mb-2">error</i>
            <h5 class="alert-heading">Lỗi truy xuất!</h5>
            <p>{{ errorMessage }}</p>
            <button @click="loadTraceability" class="btn btn-outline-danger mt-3">Thử lại</button>
        </div>

        <template v-else>
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
                            <div class="d-flex align-items-center gap-2 mb-2">
                                <span class="badge bg-success">✓ Đã xác thực</span>
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
                            <small class="text-muted d-block mt-2">Mã: {{ traceability.batchId }}</small>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <!-- Timeline -->
                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm mb-4">
                        <div class="card-header bg-white border-0">
                            <h5 class="mb-0">📋 Hành Trình Sản Phẩm</h5>
                        </div>
                        <div class="card-body">
                            <div class="timeline">
                                <div v-for="(event, index) in traceability.timeline" :key="index" class="timeline-item verified">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <span class="badge bg-success-subtle text-success mb-1">{{ event.stage }}</span>
                                            <h6 class="font-weight-bold mb-1">{{ event.title }}</h6>
                                            <p class="text-muted small mb-1">{{ event.description }}</p>
                                            <small class="text-muted">📅 {{ event.date }} | 📍 {{ event.location }}</small>
                                        </div>
                                        <span class="badge bg-success">✓</span>
                                    </div>
                                    <div v-if="event.txHash" class="mt-2 p-2 bg-light rounded d-flex justify-content-between align-items-center">
                                        <div>
                                            <small class="text-muted d-block">🔗 Transaction Hash:</small>
                                            <code class="hash-text">{{ event.txHash }}</code>
                                        </div>
                                        <button @click="copyHash(event.txHash)" class="btn btn-sm btn-outline-success">
                                            <i class="material-symbols-rounded" style="font-size: 1rem;">content_copy</i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Sidebar Info -->
                <div class="col-lg-4">
                    <!-- Blockchain Verification -->
                    <div class="blockchain-verified mb-4">
                        <div class="d-flex align-items-center mb-3">
                            <span style="font-size: 2rem; margin-right: 12px;">⛓️</span>
                            <div>
                                <h6 class="mb-0 font-weight-bold">Blockchain Verified</h6>
                                <small class="opacity-75">Dữ liệu đã được ghi trên blockchain</small>
                            </div>
                        </div>
                        <div class="bg-white bg-opacity-10 rounded p-2">
                            <small class="d-block text-white-50">Contract Address:</small>
                            <code class="text-white small" style="word-break: break-all;">{{ traceability.contractAddress }}</code>
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
                                    <small class="text-muted">📍 {{ traceability.farmLocation }}</small>
                                </div>
                            </div>
                            <div class="mb-2"><span class="text-muted">Diện tích:</span> {{ traceability.farmArea }}</div>
                            <div class="mb-2"><span class="text-muted">Tiêu chuẩn:</span> {{ traceability.certifications.join(', ') }}</div>
                            <div><span class="text-muted">Đánh giá:</span> ⭐ {{ traceability.farmRating }}/5</div>
                        </div>
                    </div>

                    <!-- Quality Info -->
                    <div class="card border-0 shadow-sm">
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
.blockchain-verified {
    background: linear-gradient(135deg, #1B5E20, #2E7D32);
    color: white;
    padding: 1.5rem;
    border-radius: 1rem;
}
.cert-badge {
    background: #E8F5E9;
    color: #2E7D32;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.8rem;
}
.timeline-item {
    position: relative;
    padding-left: 30px;
    padding-bottom: 20px;
    border-left: 2px solid #4CAF50;
    margin-left: 10px;
}
.timeline-item::before {
    content: '';
    position: absolute;
    left: -8px;
    top: 0;
    width: 14px;
    height: 14px;
    border-radius: 50%;
    background: #4CAF50;
}
.hash-text {
    font-size: 0.75rem;
    word-break: break-all;
}
</style>
