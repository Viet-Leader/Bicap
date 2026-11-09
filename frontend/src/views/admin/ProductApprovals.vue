<template>
  <div class="container-fluid py-2">
    <div class="card my-3">
      <div class="card-body p-3">
        <form @submit.prevent="loadData" class="row align-items-center">
          <div class="col-md-6">
            <div class="input-group input-group-outline w-100">
              <input type="text" class="form-control" v-model="keyword" placeholder="Tìm tên SP cần duyệt..." />
            </div>
          </div>
          <div class="col-md-2">
            <button type="submit" class="btn bg-gradient-dark w-100 mb-0">Tìm kiếm</button>
          </div>
        </form>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="card my-4">
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <div class="bg-gradient-warning shadow-warning border-radius-lg pt-4 pb-3 px-3">
              <h6 class="text-white text-capitalize ps-3 mb-0">Danh sách chờ duyệt (Pending)</h6>
            </div>
          </div>
          <div class="card-body px-0 pb-2">
            <div class="table-responsive p-0">
              <table class="table align-items-center mb-0">
                <thead>
                  <tr>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Sản phẩm</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Nguồn gốc (Farm)</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Giá đề xuất</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Trạng thái</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Xét duyệt</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="!products || products.length === 0">
                    <td colspan="5" class="text-center py-4 text-secondary">Không có sản phẩm nào đang chờ duyệt.</td>
                  </tr>
                  <tr v-for="p in products" :key="p.productId">
                    <td>
                      <div class="d-flex px-3 py-1">
                        <div>
                          <img :src="p.imageUrl || '/admin-assets/img/product-placeholder.jpg'" class="avatar avatar-sm me-3 border-radius-lg" alt="img" style="object-fit: cover;">
                        </div>
                        <div class="d-flex flex-column justify-content-center">
                          <h6 class="mb-0 text-sm">{{ p.productName }}</h6>
                          <p class="text-xs text-secondary mb-0">{{ p.categoryName || 'Chưa phân loại' }}</p>
                        </div>
                      </div>
                    </td>
                    <td>
                      <p class="text-xs font-weight-bold mb-0">{{ p.farmName || 'Unknown Farm' }}</p>
                      <p class="text-xs text-secondary mb-0">Chủ trại: {{ p.ownerName || 'Unknown' }}</p>
                    </td>
                    <td class="align-middle text-center">
                      <span class="text-secondary text-xs font-weight-bold">
                        {{ (p.price || 0).toLocaleString('vi-VN') }} VND
                      </span>
                    </td>
                    <td class="align-middle text-center text-sm">
                      <span class="badge badge-sm bg-gradient-warning">Đang chờ duyệt</span>
                    </td>
                    <td class="align-middle text-center">
                      <button class="btn btn-link text-info px-1 mb-0" @click="showDetails(p)" title="Xem chi tiết">
                        <i class="material-symbols-rounded text-sm">visibility</i>
                      </button>

                      <button class="btn btn-sm btn-outline-success mb-0 mx-1" @click="approveProduct(p)" title="Duyệt lên sàn">
                        <i class="material-symbols-rounded text-sm">check_circle</i> Duyệt
                      </button>

                      <button class="btn btn-sm btn-outline-danger mb-0" @click="openRejectModal(p)" title="Từ chối">
                        <i class="material-symbols-rounded text-sm">cancel</i> Từ chối
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="px-3 py-3" v-if="totalPages > 1">
              <nav aria-label="Page navigation">
                <ul class="pagination pagination-sm justify-content-end mb-0">
                  <li v-for="i in totalPages" :key="i" class="page-item" :class="{ active: i - 1 === page }">
                    <a class="page-link" href="#" @click.prevent="goToPage(i - 1)">{{ i }}</a>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <div v-if="isRejectModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title text-danger">Từ chối sản phẩm: <span>{{ rejectProductName }}</span></h5>
            <button type="button" class="btn-close text-dark" @click="isRejectModalOpen = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="input-group input-group-outline mb-3 flex-column">
              <label class="form-label">Lý do từ chối (Bắt buộc)</label>
              <input type="text" class="form-control w-100" v-model="rejectReason" required>
            </div>
            <p class="text-xs text-muted">Sản phẩm sẽ bị trả về trạng thái REJECTED.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="isRejectModalOpen = false">Hủy</button>
            <button type="button" class="btn bg-gradient-danger" @click="confirmReject">Xác nhận Từ chối</button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="selectedProduct" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết sản phẩm chờ duyệt</h5>
            <button type="button" class="btn-close text-dark" @click="selectedProduct = null">&times;</button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div class="col-md-4">
                <img :src="selectedProduct.imageUrl || '/admin-assets/img/product-placeholder.jpg'" class="img-fluid border-radius-lg shadow-sm" style="width: 100%; object-fit: cover;">
              </div>
              <div class="col-md-8">
                <h4>{{ selectedProduct.productName }}</h4>
                <p class="text-sm text-secondary mb-1">Trang trại: <b class="text-dark">{{ selectedProduct.farmName }}</b></p>
                <p class="text-sm text-secondary mb-1">Owner: <span>{{ selectedProduct.ownerName }}</span></p>
                <p class="text-sm text-secondary mb-3">Giá đề xuất: <b class="text-success" style="font-size: 1.2rem;">{{ (selectedProduct.price || 0).toLocaleString('vi-VN') }} VND</b></p>
                
                <h6 class="text-uppercase text-xs font-weight-bolder">Mô tả:</h6>
                <p class="text-sm text-dark bg-light p-2 border-radius-md">{{ selectedProduct.description || 'Không có mô tả.' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axiosClient from '@/api/axiosClient'

const products = ref([])
const keyword = ref('')
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)

const isRejectModalOpen = ref(false)
const rejectProductId = ref('')
const rejectProductName = ref('')
const rejectReason = ref('')
const selectedProduct = ref(null)

const loadData = async () => {
  try {
    const { data } = await axiosClient.get('/products', {
      params: {
        page: page.value,
        size: size.value,
        keyword: keyword.value || undefined,
        status: 'PENDING'
      }
    })
    products.value = data.content || data || []
    totalPages.value = data.totalPages || 1
  } catch (e) {
    console.error(e)
  }
}

const goToPage = (p) => {
  if (p >= 0 && p < totalPages.value) {
    page.value = p
    loadData()
  }
}

const approveProduct = async (p) => {
  if (!confirm(`Bạn có chắc chắn muốn DUYỆT sản phẩm "${p.productName}" lên sàn?`)) return
  try {
    await axiosClient.patch(`/products/${p.productId}/status`)
    alert("Đã duyệt sản phẩm thành công!")
    loadData()
  } catch (e) {
    console.error(e)
    alert("Lỗi khi duyệt sản phẩm.")
  }
}

const openRejectModal = (p) => {
  rejectProductId.value = p.productId
  rejectProductName.value = p.productName
  rejectReason.value = ''
  isRejectModalOpen.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    alert("Vui lòng nhập lý do từ chối!")
    return
  }
  try {
    await axiosClient.patch(`/products/${rejectProductId.value}/status`)
    alert("Đã từ chối sản phẩm!")
    isRejectModalOpen.value = false
    loadData()
  } catch (e) {
    console.error(e)
    alert("Lỗi khi từ chối sản phẩm.")
  }
}

const showDetails = (p) => {
  selectedProduct.value = p
}

onMounted(() => {
  loadData()
})
</script>
