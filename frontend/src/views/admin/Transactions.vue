<template>
  <div class="container-fluid py-4">

    <!-- Filter bar -->
    <div class="card mb-4">
      <div class="card-body p-3">
        <div class="row gx-2 gy-2 align-items-end">
          <div class="col-md-4">
            <label class="form-label text-xs mb-1">Tìm theo Transaction Hash</label>
            <input
              v-model="filter.txHash"
              type="text"
              class="form-control form-control-sm"
              placeholder="Nhập hoặc dán TxHash..."
              @input="debouncedLoad"
            />
          </div>
          <div class="col-md-3">
            <label class="form-label text-xs mb-1">Loại dữ liệu</label>
            <select v-model="filter.entityType" class="form-select form-select-sm" @change="loadData">
              <option value="">-- Tất cả --</option>
              <option value="ProductBatch">ProductBatch</option>
              <option value="SeasonActivity">SeasonActivity</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label text-xs mb-1">Trạng thái</label>
            <select v-model="filter.status" class="form-select form-select-sm" @change="loadData">
              <option value="">-- Tất cả --</option>
              <option value="SUCCESS">SUCCESS</option>
              <option value="PENDING">PENDING</option>
              <option value="FAILED">FAILED</option>
            </select>
          </div>
          <div class="col-md-2 d-flex gap-1">
            <button class="btn btn-sm bg-gradient-dark mb-0 w-100" @click="loadData">Lọc</button>
            <button class="btn btn-sm btn-outline-secondary mb-0" title="Làm mới" @click="resetFilter">↺</button>
          </div>
        </div>
        <small class="text-muted mt-1 d-block">Cập nhật lúc {{ lastUpdated }}</small>
      </div>
    </div>

    <!-- Table -->
    <div class="card">
      <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
        <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 d-flex justify-content-between align-items-center px-3">
          <h6 class="text-white mb-0">⛓️ Giao dịch Blockchain</h6>
          <button class="btn btn-sm btn-outline-white mb-0" @click="loadData">
            <i class="material-symbols-rounded text-sm">refresh</i> Làm mới
          </button>
        </div>
      </div>
      <div class="card-body px-0 pb-2">
        <!-- Loading skeleton -->
        <div v-if="isLoading" class="p-4">
          <div v-for="i in 5" :key="i" class="skeleton-row mb-2"></div>
        </div>

        <!-- Error -->
        <div v-else-if="errorMessage" class="alert alert-danger mx-3 mt-3">
          <strong>Lỗi:</strong> {{ errorMessage }}
          <button class="btn btn-sm btn-outline-danger ms-2" @click="loadData">Thử lại</button>
        </div>

        <!-- Empty state -->
        <div v-else-if="transactions.length === 0" class="text-center py-5 text-muted">
          <i class="material-symbols-rounded" style="font-size:3rem">search_off</i>
          <p class="mt-2">Không có giao dịch nào phù hợp bộ lọc.</p>
        </div>

        <!-- Data table -->
        <div v-else class="table-responsive p-0">
          <table class="table align-items-center mb-0">
            <thead>
              <tr>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Transaction Hash</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Data Hash</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Loại dữ liệu</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Đối tượng</th>
                <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Trạng thái</th>
                <th class="text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Thời gian</th>
                <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="tx in transactions" :key="tx.id || tx.txHash">
                <td class="ps-3">
                  <HashDisplay :hash="tx.blockchainTxHash || tx.txHash" :prefix-len="8" :suffix-len="6" />
                </td>
                <td>
                  <HashDisplay :hash="tx.dataHash" :prefix-len="8" :suffix-len="6" />
                </td>
                <td>
                  <span
                    class="badge badge-sm"
                    :class="tx.entityType === 'ProductBatch' ? 'bg-gradient-info' : 'bg-gradient-success'"
                  >
                    {{ tx.entityType || tx.recordType || '—' }}
                  </span>
                </td>
                <td>
                  <code class="text-xs">{{ tx.entityId || tx.objectCode || '—' }}</code>
                </td>
                <td class="text-center">
                  <BlockchainStatusBadge :status="tx.blockchainStatus || tx.status" size="sm" />
                </td>
                <td>
                  <span class="text-xs text-secondary">{{ formatVnDateTime(tx.createdAt || tx.timestamp) }}</span>
                </td>
                <td class="text-center text-nowrap">
                  <button
                    class="btn btn-link text-info px-1 mb-0"
                    title="Xem chi tiết"
                    @click="openDetail(tx)"
                  >
                    <i class="material-symbols-rounded text-sm">visibility</i>
                  </button>
                  <VerifyButton
                    v-if="tx.blockchainTxHash || tx.txHash"
                    :is-verifying="verifyingTx[tx.id]"
                    label=""
                    @verify="verifyTx(tx)"
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="px-3 py-3" v-if="totalPages > 1">
          <nav>
            <ul class="pagination pagination-sm justify-content-end mb-0">
              <li class="page-item" :class="{ disabled: page === 0 }">
                <a class="page-link" href="#" @click.prevent="goToPage(page - 1)">‹</a>
              </li>
              <li v-for="i in Math.min(totalPages, 10)" :key="i" class="page-item" :class="{ active: i - 1 === page }">
                <a class="page-link" href="#" @click.prevent="goToPage(i - 1)">{{ i }}</a>
              </li>
              <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
                <a class="page-link" href="#" @click.prevent="goToPage(page + 1)">›</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>

    <!-- Transaction Detail Drawer -->
    <TxDetailDrawer
      :open="drawer.open"
      :tx="drawer.tx"
      :verify-endpoint="drawer.verifyEndpoint"
      @close="drawer.open = false"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axiosClient from '@/api/axiosClient'
import BlockchainStatusBadge from '@/components/blockchain/BlockchainStatusBadge.vue'
import HashDisplay from '@/components/blockchain/HashDisplay.vue'
import VerifyButton from '@/components/blockchain/VerifyButton.vue'
import TxDetailDrawer from '@/components/blockchain/TxDetailDrawer.vue'

const route = useRoute()
const router = useRouter()

const transactions = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const page = ref(0)
const size = ref(15)
const totalPages = ref(1)
const lastUpdated = ref('')
const verifyingTx = reactive({})

const filter = reactive({
  txHash: route.query.txHash || '',
  entityType: route.query.type || '',
  status: route.query.status || '',
})

const drawer = reactive({ open: false, tx: null, verifyEndpoint: '' })

// Debounce search
let debounceTimer = null
const debouncedLoad = () => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => loadData(), 300)
}

const syncUrl = () => {
  router.replace({
    query: {
      ...(filter.txHash     ? { txHash: filter.txHash }         : {}),
      ...(filter.entityType ? { type: filter.entityType }        : {}),
      ...(filter.status     ? { status: filter.status }          : {}),
      ...(page.value > 0    ? { page: String(page.value) }       : {}),
    }
  })
}

const loadData = async () => {
  isLoading.value = true
  errorMessage.value = ''
  syncUrl()
  try {
    const { data } = await axiosClient.get('/blockchain/transactions', {
      params: {
        page: page.value,
        size: size.value,
        txHash:     filter.txHash     || undefined,
        entityType: filter.entityType || undefined,
        status:     filter.status     || undefined,
      }
    })
    transactions.value = data.content || data || []
    totalPages.value   = data.totalPages || 1
    lastUpdated.value  = new Date().toLocaleTimeString('vi-VN')
  } catch (e) {
    errorMessage.value = e.response?.data?.message || 'Không thể tải danh sách giao dịch blockchain.'
    transactions.value = []
  } finally {
    isLoading.value = false
  }
}

const goToPage = (p) => {
  if (p >= 0 && p < totalPages.value) {
    page.value = p
    loadData()
  }
}

const resetFilter = () => {
  Object.assign(filter, { txHash: '', entityType: '', status: '' })
  page.value = 0
  loadData()
}

const openDetail = (tx) => {
  drawer.tx = tx
  drawer.verifyEndpoint = tx.entityType === 'ProductBatch'
    ? `/blockchain/verify/product-batch/${tx.entityId}`
    : `/blockchain/verify/season-activity/${tx.entityId}`
  drawer.open = true
}

const verifyTx = async (tx) => {
  const id = tx.id || tx.txHash
  verifyingTx[id] = true
  try {
    const endpoint = tx.entityType === 'ProductBatch'
      ? `/blockchain/verify/product-batch/${tx.entityId}`
      : `/blockchain/verify/season-activity/${tx.entityId}`
    await axiosClient.get(endpoint)
    openDetail(tx) // show detail with result
  } catch (e) {
    openDetail(tx)
  } finally {
    verifyingTx[id] = false
  }
}

const formatVnDateTime = (val) => {
  if (!val) return '—'
  return new Date(val).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  })
}

onMounted(() => {
  if (route.query.page) page.value = parseInt(route.query.page) || 0
  loadData()
})
</script>

<style scoped>
.skeleton-row {
  height: 36px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.2s infinite;
  border-radius: 6px;
}
@keyframes shimmer { 0% { background-position: 200% 0 } 100% { background-position: -200% 0 } }
</style>

