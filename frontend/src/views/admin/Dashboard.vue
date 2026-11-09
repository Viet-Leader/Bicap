<template>
  <div class="container-fluid px-2 px-md-4">
    <div class="row mb-4">
      <div class="col-xl-3 col-sm-6 mb-xl-0 mb-4">
        <div class="card">
          <div class="card-header p-2 ps-3">
            <div class="d-flex justify-content-between">
              <div>
                <p class="text-sm mb-0 text-capitalize">Chờ phê duyệt</p>
                <h4 class="mb-0">{{ pendingCount }}</h4>
              </div>
              <div class="icon icon-md icon-shape bg-gradient-warning shadow-dark shadow text-center border-radius-lg">
                <i class="material-symbols-rounded opacity-10">pending_actions</i>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-sm-6 mb-xl-0 mb-4">
        <div class="card">
          <div class="card-header p-2 ps-3">
            <div class="d-flex justify-content-between">
              <div>
                <p class="text-sm mb-0 text-capitalize">Tổng User</p>
                <h4 class="mb-0">{{ totalUsers }}</h4>
              </div>
              <div class="icon icon-md icon-shape bg-gradient-primary shadow-primary shadow text-center border-radius-lg">
                <i class="material-symbols-rounded opacity-10">group</i>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-sm-6 mb-xl-0 mb-4">
        <div class="card">
          <div class="card-header p-2 ps-3">
            <div class="d-flex justify-content-between">
              <div>
                <p class="text-sm mb-0 text-capitalize">Tổng Farm</p>
                <h4 class="mb-0">{{ totalFarms }}</h4>
              </div>
              <div class="icon icon-md icon-shape bg-gradient-success shadow-success shadow text-center border-radius-lg">
                <i class="material-symbols-rounded opacity-10">agriculture</i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== BLOCKCHAIN STATS ===== -->
    <div class="card mb-4">
      <div class="card-header pb-0 d-flex justify-content-between align-items-center">
        <h6 class="mb-0">⛓️ Thống kê Giao dịch Blockchain</h6>
        <div class="d-flex align-items-center gap-2">
          <small class="text-muted" v-if="blockchainLastUpdated">Cập nhật lúc {{ blockchainLastUpdated }}</small>
          <button class="btn btn-sm btn-outline-dark mb-0" @click="loadBlockchainStats" :disabled="blockchainLoading">
            <span v-if="blockchainLoading" class="spinner-border spinner-border-sm me-1" style="width:12px;height:12px"></span>
            <i v-else class="material-symbols-rounded text-sm">refresh</i>
          </button>
        </div>
      </div>
      <div class="card-body pt-3">
        <div class="row">
          <!-- SUCCESS -->
          <div class="col-md-4 mb-3 mb-md-0">
            <div
              class="blockchain-stat-card success-card"
              role="button"
              title="Xem giao dịch thành công"
              @click="goToTransactions('SUCCESS')"
            >
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <p class="stat-label">Thành công</p>
                  <h3 class="stat-value">{{ blockchainLoading ? '...' : blockchainStats.successCount }}</h3>
                </div>
                <span class="stat-icon">✅</span>
              </div>
              <small class="stat-hint">Click để xem chi tiết →</small>
            </div>
          </div>
          <!-- FAILED -->
          <div class="col-md-4 mb-3 mb-md-0">
            <div
              class="blockchain-stat-card failed-card"
              role="button"
              title="Xem giao dịch thất bại"
              @click="goToTransactions('FAILED')"
            >
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <p class="stat-label">Thất bại</p>
                  <h3 class="stat-value">{{ blockchainLoading ? '...' : blockchainStats.failedCount }}</h3>
                </div>
                <span class="stat-icon">❌</span>
              </div>
              <small class="stat-hint">Click để xem chi tiết →</small>
            </div>
          </div>
          <!-- PENDING -->
          <div class="col-md-4">
            <div
              class="blockchain-stat-card pending-card"
              role="button"
              title="Xem giao dịch đang xử lý"
              @click="goToTransactions('PENDING')"
            >
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <p class="stat-label">Đang xử lý</p>
                  <h3 class="stat-value">{{ blockchainLoading ? '...' : blockchainStats.pendingCount }}</h3>
                </div>
                <span class="stat-icon">⏳</span>
              </div>
              <small class="stat-hint">Click để xem chi tiết →</small>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- ===== END BLOCKCHAIN STATS ===== -->

    <div class="row">
      <div class="col-12">
        <div class="card my-4">
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 d-flex justify-content-between align-items-center px-3">
              <h6 class="text-white text-capitalize ps-3 mb-0">Danh sách yêu cầu nâng cấp Role</h6>
              <button class="btn btn-sm btn-outline-white mb-0" @click="loadData">
                  <i class="material-symbols-rounded text-sm">refresh</i> Làm mới
              </button>
            </div>
          </div>
          <div class="card-body px-0 pb-2">
            <div class="table-responsive p-0">
              <table class="table align-items-center mb-0">
                <thead>
                  <tr>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Người dùng</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Role mong muốn</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Giấy phép</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Ngày nộp</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="requests.length === 0">
                    <td colspan="5" class="text-center py-4 text-secondary">Không có yêu cầu nào đang chờ xử lý.</td>
                  </tr>
                  <tr v-for="req in requests" :key="req.requestId">
                    <td>
                      <div class="d-flex px-3 py-1">
                        <div class="d-flex flex-column justify-content-center">
                          <h6 class="mb-0 text-sm">{{ req.userName }}</h6>
                          <p class="text-xs text-secondary mb-0">{{ req.email }}</p>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span v-if="req.requestedRoleName === 'FARM_OWNER'" class="badge badge-sm bg-gradient-success">Chủ Trang Trại</span>
                      <span v-else-if="req.requestedRoleName === 'SHIPPER'" class="badge badge-sm bg-gradient-info">Vận Chuyển</span>
                      <span v-else class="badge badge-sm bg-gradient-secondary">{{ req.requestedRoleName }}</span>
                    </td>
                    <td class="align-middle text-center text-sm">
                      <div class="d-flex justify-content-center gap-2">
                        <img v-if="req.images && req.images.length > 0" :src="req.images[0]" class="license-thumb border" alt="license" @click="showImageModal(req.images[0])">
                        <span v-if="req.images && req.images.length > 1" class="badge bg-secondary text-xxs align-self-end">+{{ req.images.length - 1 }}</span>
                        <span v-else-if="!req.images || req.images.length === 0" class="text-xs text-secondary">Không có ảnh</span>
                      </div>
                    </td>
                    <td class="align-middle text-center">
                      <span class="text-secondary text-xs font-weight-bold">{{ req.createdAt }}</span>
                    </td>
                    <td class="align-middle text-center">
                      <button class="btn btn-sm bg-gradient-success mb-0 me-1" @click="approveRequest(req.requestId)">
                          <i class="material-symbols-rounded text-sm">check</i> Duyệt
                      </button>
                      <button class="btn btn-sm btn-outline-danger mb-0" @click="openRejectModal(req.requestId)">
                          <i class="material-symbols-rounded text-sm">close</i> Từ chối
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Image Modal -->
    <div v-if="isImageModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-body p-0 text-center bg-dark border-radius-lg position-relative">
              <button type="button" class="btn btn-link text-white position-absolute top-0 end-0 m-2" @click="isImageModalOpen = false">
                  <i class="material-symbols-rounded text-2xl">close</i>
              </button>
              <img :src="previewImage" class="img-fluid border-radius-lg" style="max-height: 80vh;">
          </div>
        </div>
      </div>
    </div>

    <!-- Reject Modal -->
    <div v-if="isRejectModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Từ chối yêu cầu</h5>
            <button type="button" class="btn-close text-dark" @click="isRejectModalOpen = false">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <div class="input-group input-group-outline my-3 flex-column">
                  <label class="form-label">Lý do từ chối</label>
                  <input type="text" class="form-control w-100" v-model="rejectReason">
              </div>
              <p class="text-xs text-muted">User sẽ nhận được thông báo về lý do này.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="isRejectModalOpen = false">Hủy</button>
            <button type="button" class="btn bg-gradient-danger" @click="confirmReject">Xác nhận Từ chối</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import axiosClient from '@/api/axiosClient'

const router = useRouter()

const pendingCount = ref(0)
const totalUsers = ref(0)
const totalFarms = ref(0)

// Blockchain stats
const blockchainStats = ref({ successCount: 0, failedCount: 0, pendingCount: 0 })
const blockchainLoading = ref(false)
const blockchainLastUpdated = ref('')

const requests = ref([])
const isImageModalOpen = ref(false)
const previewImage = ref('')
const isRejectModalOpen = ref(false)
const rejectRequestId = ref('')
const rejectReason = ref('')

const loadData = async () => {
  try {
    // Load role upgrade requests
    const { data } = await axiosClient.get('/accounts/role-requests?status=PENDING')
    requests.value = data.content || data || []
    pendingCount.value = requests.value.length
  } catch {
    // ignore
  }
  try {
    const { data } = await axiosClient.get('/accounts?size=1')
    totalUsers.value = data.totalElements || 0
  } catch { /* ignore */ }
  try {
    const { data } = await axiosClient.get('/farms?size=1')
    totalFarms.value = data.totalElements || 0
  } catch { /* ignore */ }

  loadBlockchainStats()
}

const loadBlockchainStats = async () => {
  blockchainLoading.value = true
  try {
    const { data } = await axiosClient.get('/blockchain/stats')
    blockchainStats.value = {
      successCount: data.successCount || data.SUCCESS || 0,
      failedCount:  data.failedCount  || data.FAILED  || 0,
      pendingCount: data.pendingCount || data.PENDING  || 0,
    }
    blockchainLastUpdated.value = new Date().toLocaleTimeString('vi-VN')
  } catch {
    // keep zeros if API not available
  } finally {
    blockchainLoading.value = false
  }
}

const goToTransactions = (status) => {
  router.push({ name: 'admin-transactions', query: { status } })
}

const showImageModal = (src) => {
  previewImage.value = src
  isImageModalOpen.value = true
}

const approveRequest = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn duyệt yêu cầu này?')) return
  try {
    await axiosClient.patch(`/accounts/role-requests/${id}/approve`)
    await loadData()
    alert('Đã duyệt thành công!')
  } catch {
    alert('Lỗi khi duyệt yêu cầu.')
  }
}

const openRejectModal = (id) => {
  rejectRequestId.value = id
  rejectReason.value = ''
  isRejectModalOpen.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    alert('Vui lòng nhập lý do từ chối!')
    return
  }
  try {
    await axiosClient.patch(`/accounts/role-requests/${rejectRequestId.value}/reject`, {
      reason: rejectReason.value
    })
    isRejectModalOpen.value = false
    await loadData()
    alert('Đã từ chối yêu cầu.')
  } catch {
    alert('Lỗi khi từ chối yêu cầu.')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.license-thumb {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  cursor: zoom-in;
  transition: transform 0.2s;
}
.license-thumb:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}
.input-group-outline .form-control {
  border: 1px solid #d2d6da;
  padding: 0.5rem;
}

/* Blockchain stat cards */
.blockchain-stat-card {
  border-radius: 12px;
  padding: 1rem 1.2rem;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}
.blockchain-stat-card:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,0.12); }
.success-card { background: linear-gradient(135deg, #d1fae5, #a7f3d0); border: 1px solid #6ee7b7; }
.failed-card  { background: linear-gradient(135deg, #fee2e2, #fecaca); border: 1px solid #fca5a5; }
.pending-card { background: linear-gradient(135deg, #fef3c7, #fde68a); border: 1px solid #fcd34d; }
.stat-label { font-size: 0.8rem; font-weight: 600; margin-bottom: 2px; color: #374151; }
.stat-value { font-size: 2rem; font-weight: 700; margin: 0; color: #111827; }
.stat-icon  { font-size: 2.5rem; }
.stat-hint  { font-size: 0.72rem; color: #6b7280; }
</style>
