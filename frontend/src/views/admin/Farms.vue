<template>
  <div class="container-fluid py-2">
    <div class="row">
      <div class="col-12">
        <div class="card my-4">
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 px-3">
              <h6 class="text-white text-capitalize mb-0">Danh sách Nông trại đăng ký</h6>
            </div>
          </div>
          <div class="card-body px-0 pb-2">
            <div class="table-responsive p-0">
              <table class="table align-items-center mb-0">
                <thead>
                  <tr>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Tên Nông trại</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Chủ sở hữu</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Địa chỉ</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="!farms || farms.length === 0">
                    <td colspan="4" class="text-center py-4">Chưa có nông trại nào.</td>
                  </tr>
                  <tr v-for="farm in farms" :key="farm.farmId">
                    <td>
                      <div class="d-flex px-3 py-1">
                        <div class="icon icon-md icon-shape bg-gradient-success shadow-success text-center border-radius-lg me-3">
                          <i class="material-symbols-rounded opacity-10">compost</i>
                        </div>
                        <div class="d-flex flex-column justify-content-center">
                          <h6 class="mb-0 text-sm">{{ farm.farmName }}</h6>
                          <p class="text-xs text-secondary mb-0">ID: {{ farm.farmId }}</p>
                        </div>
                      </div>
                    </td>
                    <td>
                      <p class="text-xs font-weight-bold mb-0">{{ farm.ownerName || 'Chưa cập nhật' }}</p>
                    </td>
                    <td class="text-xs text-secondary mb-0">
                      <span class="d-inline-block text-truncate" style="max-width: 200px;">
                        {{ farm.location || farm.address || 'N/A' }}
                      </span>
                    </td>
                    <td class="align-middle text-center">
                      <button class="btn btn-sm btn-info mb-0" @click="viewLogs(farm.farmId)">
                        <i class="material-symbols-rounded text-sm">history</i> Nhật ký
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

    <!-- Modal Nhật ký -->
    <div v-if="isModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Nhật ký hoạt động (Audit Log)</h5>
            <button type="button" class="btn-close text-dark" @click="closeModal">&times;</button>
          </div>
          <div class="modal-body">
            <div v-if="isLoadingLogs" class="text-center py-4">
              <div class="spinner-border text-success" role="status"></div>
              <p>Đang tải dữ liệu...</p>
            </div>
            <div v-else-if="logs.length === 0" class="text-center text-muted">
              <p>Chưa có hoạt động nào được ghi nhận.</p>
            </div>
            <div v-else class="timeline">
              <div v-for="(log, index) in logs" :key="index" class="timeline-block">
                <div class="timeline-icon">
                  <span class="badge rounded-circle p-2" :class="getLogIconClass(log.type)">
                    <i class="material-symbols-rounded text-white text-sm">{{ getLogIcon(log.type) }}</i>
                  </span>
                  <div class="v-line"></div>
                </div>
                <div class="timeline-content shadow-sm border p-3 mb-3 border-radius-md bg-light">
                  <div class="d-flex justify-content-between">
                    <h6 class="text-dark text-sm font-weight-bold mb-1">{{ log.type }} - {{ log.code }}</h6>
                    <span class="text-xxs text-secondary"><i class="fa fa-clock-o"></i> {{ new Date(log.timestamp).toLocaleString('vi-VN') }}</span>
                  </div>
                  <p class="text-sm mt-1 mb-0 text-secondary">{{ log.description }}</p>
                  <span class="badge bg-white text-dark text-xxs mt-2 border">{{ log.status }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Đóng</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axiosClient from '@/api/axiosClient'

const farms = ref([])
const logs = ref([])
const isModalOpen = ref(false)
const isLoadingLogs = ref(false)

const loadFarms = async () => {
  try {
    const { data } = await axiosClient.get('/farms')
    farms.value = data.content || data || []
  } catch (error) {
    console.error(error)
  }
}

const viewLogs = async (farmId) => {
  isModalOpen.value = true
  isLoadingLogs.value = true
  logs.value = []
  try {
    const { data } = await axiosClient.get(`/farms/${farmId}`)
    logs.value = data?.logs || []
  } catch (error) {
    console.error(error)
  } finally {
    isLoadingLogs.value = false
  }
}

const closeModal = () => {
  isModalOpen.value = false
  logs.value = []
}

const getLogIcon = (type) => {
  if (type === 'SẢN XUẤT') return 'eco'
  if (type === 'XUẤT KHẨU') return 'local_shipping'
  return 'inventory_2'
}

const getLogIconClass = (type) => {
  if (type === 'SẢN XUẤT') return 'bg-gradient-success'
  if (type === 'XUẤT KHẨU') return 'bg-gradient-warning'
  return 'bg-gradient-info'
}

onMounted(() => {
  loadFarms()
})
</script>

<style scoped>
.timeline-block {
    position: relative;
    display: flex;
    gap: 1rem;
}
.timeline-icon {
    display: flex;
    flex-direction: column;
    align-items: center;
}
.v-line {
    width: 2px;
    background-color: #e9ecef;
    flex-grow: 1;
    margin-top: 0.5rem;
}
.timeline-block:last-child .v-line {
    display: none;
}
.timeline-content {
    flex-grow: 1;
}
</style>
