<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import GlassCard from '@/components/common/GlassCard.vue'
import AppBadge from '@/components/common/AppBadge.vue'
import AppButton from '@/components/common/AppButton.vue'
// import axiosClient from '../api/axiosClient'

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
      batchCode: batchId.value,
      farmName: 'Trang trại Xoài Cát Hòa Lộc (Tiền Giang)',
      quantity: 500,
      unit: 'kg',
      blockchainStatus: 'SUCCESS',
      txHash: '0x1c44a82f9b21f4d1e3a89b21f4d1e3a89b21f4d1',
      activities: [
        { activityId: 1, activityTime: '2026-01-15T08:00:00Z', activityType: 'Gieo trồng', description: 'Gieo 500 cây giống Xoài Cát loại A' },
        { activityId: 2, activityTime: '2026-02-20T09:30:00Z', activityType: 'Bón phân', description: 'Sử dụng phân bò ủ vi sinh hữu cơ' },
        { activityId: 3, activityTime: '2026-04-15T14:15:00Z', activityType: 'Tưới nước', description: 'Hệ thống tưới nhỏ giọt tự động' },
        { activityId: 4, activityTime: '2026-05-10T10:00:00Z', activityType: 'Thu hoạch', description: 'Sản lượng 500kg, đạt chuẩn VietGAP' }
      ]
    }
  } catch (error) {
    errorMessage.value = error.message || 'Không thể tải dữ liệu hành trình sản phẩm.'
  } finally {
    isLoading.value = false
  }
}

const copyHash = async () => {
  if (!traceability.value?.txHash) return
  await navigator.clipboard.writeText(traceability.value.txHash)
  copied.value = true
  window.setTimeout(() => { copied.value = false }, 1800)
}

onMounted(loadTraceability)
</script>

<template>
  <div class="traceability-page">
    <div class="background-effects">
      <div class="glow-orb top-orb"></div>
      <div class="glow-orb bottom-orb"></div>
    </div>

    <section class="container">
      <header class="page-header">
        <span class="subtitle">Hành trình sản phẩm</span>
        <h1 class="title">Truy xuất Nguồn gốc</h1>
        <p class="description">Theo dõi minh bạch từ nông trại đến bàn ăn thông qua công nghệ Blockchain.</p>
      </header>

      <GlassCard v-if="isLoading" class="loading-state" glow>
        <div class="spinner"></div>
        <p>Đang tải dữ liệu từ Blockchain...</p>
      </GlassCard>

      <GlassCard v-else-if="errorMessage" class="error-state">
        <div class="error-icon">⚠️</div>
        <p>{{ errorMessage }}</p>
        <AppButton @click="loadTraceability" variant="outline" class="retry-btn">Thử lại</AppButton>
      </GlassCard>

      <div v-else class="content-wrapper">
        <GlassCard class="product-info-card" glow>
          <div class="product-header">
            <div class="details">
              <span class="label">Lô hàng đang xem</span>
              <h2 class="product-name">{{ traceability.productName }} <span class="batch-code">#{{ traceability.batchCode }}</span></h2>
              <p class="farm-info">{{ traceability.farmName }} • {{ traceability.quantity }} {{ traceability.unit }}</p>
            </div>
            <div class="status-badge">
              <AppBadge :variant="isVerified ? 'success' : 'warning'" :pulse="!isVerified">
                {{ isVerified ? 'VERIFIED ON CHAIN' : 'BLOCKCHAIN PENDING' }}
              </AppBadge>
            </div>
          </div>
        </GlassCard>

        <h3 class="section-title mt-8">Nhật ký Hoạt động</h3>
        <div class="timeline">
          <div v-for="(event, index) in traceability.activities" :key="event.activityId" class="timeline-item">
            <div class="timeline-node" :class="{ 'pulse-neon': index === traceability.activities.length - 1 }"></div>
            <GlassCard class="timeline-content">
              <span class="time">{{ new Date(event.activityTime).toLocaleString('vi-VN') }}</span>
              <h4 class="activity-type">{{ event.activityType }}</h4>
              <p class="activity-desc">{{ event.description }}</p>
            </GlassCard>
          </div>
        </div>

        <GlassCard v-if="traceability.txHash" class="blockchain-proof mt-8">
          <h3 class="section-title">Bằng chứng Blockchain</h3>
          <div class="hash-container">
            <code class="tx-hash">{{ traceability.txHash }}</code>
            <AppButton @click="copyHash" variant="primary" size="sm">
              {{ copied ? 'Đã chép!' : 'Copy Hash' }}
            </AppButton>
          </div>
        </GlassCard>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/_variables.scss' as *;

.traceability-page {
  min-height: calc(100vh - 64px);
  padding: $space-6 0;
  position: relative;
  overflow: hidden;
}

.background-effects {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  
  .glow-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(120px);
    opacity: 0.15;
    
    &.top-orb {
      top: -10%;
      right: -5%;
      width: 500px;
      height: 500px;
      background: $primary-green;
    }
    
    &.bottom-orb {
      bottom: -10%;
      left: -5%;
      width: 600px;
      height: 600px;
      background: $primary-cyan;
    }
  }
}

.container {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
  padding: 0 $space-4;
}

.page-header {
  text-align: center;
  margin-bottom: $space-8;
  
  .subtitle {
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.2em;
    color: $primary-green;
  }
  
  .title {
    font-family: 'Space Grotesk', sans-serif;
    font-size: 2.5rem;
    font-weight: 700;
    margin: $space-2 0;
    background: $gradient-hero;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  
  .description {
    color: $text-secondary;
  }
}

.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $space-8;
  text-align: center;
  
  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid rgba($primary-green, 0.2);
    border-top-color: $primary-green;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: $space-4;
  }
  
  .error-icon {
    font-size: 3rem;
    margin-bottom: $space-4;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.mt-8 { margin-top: $space-8; }

.product-info-card {
  .product-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: $space-4;
    
    @media (max-width: 640px) {
      flex-direction: column;
    }
    
    .label {
      font-size: 11px;
      text-transform: uppercase;
      letter-spacing: 0.1em;
      color: $text-secondary;
    }
    
    .product-name {
      font-family: 'Space Grotesk', sans-serif;
      font-size: 1.5rem;
      font-weight: 700;
      margin: $space-1 0;
      
      .batch-code {
        color: $primary-cyan;
        font-family: 'JetBrains Mono', monospace;
        font-size: 1.2rem;
      }
    }
    
    .farm-info {
      color: $text-secondary;
      font-size: 0.9rem;
    }
  }
}

.section-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: $space-4;
  color: $text-primary;
}

.timeline {
  position: relative;
  padding-left: 20px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    bottom: 0;
    left: 4px;
    width: 2px;
    background: rgba($primary-green, 0.2);
  }
  
  .timeline-item {
    position: relative;
    margin-bottom: $space-6;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .timeline-node {
      position: absolute;
      left: -20px;
      top: 24px;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background: $primary-green;
      box-shadow: 0 0 10px rgba($primary-green, 0.5);
    }
    
    .timeline-content {
      padding: $space-4;
      
      .time {
        font-family: 'JetBrains Mono', monospace;
        font-size: 0.8rem;
        color: $primary-cyan;
      }
      
      .activity-type {
        font-size: 1.1rem;
        font-weight: 600;
        margin: $space-1 0;
        color: $text-primary;
      }
      
      .activity-desc {
        color: $text-secondary;
        font-size: 0.95rem;
        margin: 0;
      }
    }
  }
}

.blockchain-proof {
  .hash-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-4;
    background: rgba(0, 0, 0, 0.3);
    padding: $space-3;
    border-radius: $radius-sm;
    margin-top: $space-3;
    
    .tx-hash {
      font-family: 'JetBrains Mono', monospace;
      color: $primary-green;
      font-size: 0.9rem;
      word-break: break-all;
    }
    
    @media (max-width: 480px) {
      flex-direction: column;
      align-items: flex-start;
      
      button { width: 100%; }
    }
  }
}
</style>
