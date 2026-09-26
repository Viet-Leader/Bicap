<template>
  <div class="container-fluid py-2">
    <!-- Form Search -->
    <div class="card mb-4">
      <div class="card-body p-3">
        <form @submit.prevent="loadData" class="row gx-3 gy-2 align-items-center">
          <div class="col-md-4">
            <div class="input-group input-group-outline w-100">
              <input type="text" class="form-control" v-model="keyword" placeholder="Tìm tên SP hoặc tên Farm..." />
            </div>
          </div>
          <div class="col-md-3">
            <div class="input-group input-group-outline w-100">
              <select class="form-control" v-model="status">
                <option value="">-- Tất cả trạng thái --</option>
                <option value="ACTIVE">Đang bán (Active)</option>
                <option value="APPROVED">Đã duyệt (Approved)</option>
                <option value="PENDING">Chờ duyệt (Pending)</option>
                <option value="BANNED">Đã khóa (Banned)</option>
                <option value="OUT_OF_STOCK">Hết hàng (Out of Stock)</option>
              </select>
            </div>
          </div>
          <div class="col-md-2">
            <button type="submit" class="btn bg-gradient-dark w-100 mb-0">Lọc</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Products Table -->
    <div class="row">
      <div class="col-12">
        <div class="card my-4">
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 px-3">
              <h6 class="text-white text-capitalize ps-3 mb-0">Danh sách sản phẩm</h6>
            </div>
          </div>
          <div class="card-body px-0 pb-2">
            <div class="table-responsive p-0">
              <table class="table align-items-center mb-0">
                <thead>
                  <tr>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Sản phẩm</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Nguồn gốc (Farm)</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Giá bán</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Trạng thái</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="!products || products.length === 0">
                    <td colspan="5" class="text-center py-4 text-secondary">Không tìm thấy sản phẩm nào.</td>
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
                      <p class="text-xs text-secondary mb-0">Owner: {{ p.ownerName || 'Unknown' }}</p>
                    </td>
                    <td class="align-middle text-center">
                      <span class="text-secondary text-xs font-weight-bold">
                        {{ (p.price || 0).toLocaleString('vi-VN') }} VND
                      </span>
                    </td>
                    <td class="align-middle text-center text-sm">
                      <span v-if="p.status === 'ACTIVE'" class="badge badge-sm bg-gradient-success">Đang bán</span>
                      <span v-else-if="p.status === 'APPROVED'" class="badge badge-sm bg-gradient-info">Đã duyệt</span>
                      <span v-else-if="p.status === 'PENDING'" class="badge badge-sm bg-gradient-warning">Chờ duyệt</span>
                      <span v-else-if="p.status === 'BANNED'" class="badge badge-sm bg-gradient-danger">Đã khóa</span>
                      <span v-else-if="p.status === 'OUT_OF_STOCK'" class="badge badge-sm bg-gradient-secondary">Hết hàng</span>
                      <span v-else class="badge badge-sm bg-gradient-light text-dark">{{ p.status }}</span>
                    </td>
                    <td class="align-middle text-center">
                      <button v-if="p.status !== 'BANNED'" class="btn btn-sm btn-outline-danger mb-0 me-1" @click="openBanModal(p)">
                        <i class="material-symbols-rounded text-sm">block</i> Khóa
                      </button>
                      <button v-else class="btn btn-sm btn-outline-success mb-0 me-1" @click="unbanProduct(p.productId)">
                        <i class="material-symbols-rounded text-sm">check_circle</i> Mở lại
                      </button>
                      <button class="btn btn-link text-info px-1 mb-0" @click="showDetails(p)">
                        <i class="material-symbols-rounded text-sm">visibility</i>
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
    <div v-if="isBanModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title text-danger">Khóa sản phẩm: <span>{{ banProductName }}</span></h5>
            <button type="button" class="btn-close" @click="isBanModalOpen = false"></button>
          </div>
          <div class="modal-body">
            <div class="input-group input-group-outline mb-3 flex-column">
              <label class="form-label">Lý do khóa (Bắt buộc)</label>
              <input type="text" class="form-control w-100" v-model="banReason" required>
            </div>
            <p class="text-xs text-muted">Farmer sẽ nhận được thông báo này và sản phẩm sẽ bị ẩn khỏi sàn.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="isBanModalOpen = false">Hủy</button>
            <button type="button" class="btn bg-gradient-danger" @click="confirmBan">Xác nhận Khóa</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedProduct" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết sản phẩm</h5>
            <button type="button" class="btn-close" @click="selectedProduct = null"></button>
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
                <p class="text-sm text-secondary mb-1">Mã lô (Batch): <span class="badge bg-light text-dark">{{ selectedProduct.batchCode || 'N/A' }}</span></p>
                <p class="text-sm text-secondary mb-3">Giá: <b class="text-success" style="font-size: 1.2rem;">{{ (selectedProduct.price || 0).toLocaleString('vi-VN') }} VND</b></p>
                
                <h6 class="text-uppercase text-xs font-weight-bolder">Mô tả:</h6>
                <p class="text-sm text-dark bg-light p-2 border-radius-md">{{ selectedProduct.description || 'Không có mô tả.' }}</p>
                
                <div v-if="selectedProduct.status === 'BANNED' && selectedProduct.banReason" class="alert alert-danger text-white mt-3">
                  <strong>Lý do bị khóa:</strong> <span>{{ selectedProduct.banReason }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import axiosClient from '@/api/axiosClient'

const products = ref([])
const keyword = ref('')
const status = ref('')
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)
const totalElements = ref(0)

const isBanModalOpen = ref(false)
const banProductId = ref('')
const banProductName = ref('')
const banReason = ref('')
const selectedProduct = ref(null)

const loadData = async () => {
  try {
    const { data } = await axiosClient.get('/products', {
      params: {
        page: page.value,
        size: size.value,
        keyword: keyword.value || undefined,
        status: status.value || undefined
      }
    })
    products.value = data.content || data || []
    totalPages.value = data.totalPages || 1
    totalElements.value = data.totalElements || products.value.length
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

const openBanModal = (p) => {
  banProductId.value = p.productId
  banProductName.value = p.productName
  banReason.value = ''
  isBanModalOpen.value = true
}

const confirmBan = async () => {
  if (!banReason.value.trim()) {
    alert("Vui lòng nhập lý do khóa!")
    return
  }
  try {
    await axiosClient.patch(`/products/${banProductId.value}/status`, { 
        status: 'BANNED',
        reason: banReason.value
    })
    alert("Đã khóa sản phẩm!")
    isBanModalOpen.value = false
    loadData()
  } catch (e) {
    console.error(e)
    alert("Lỗi khi khóa sản phẩm.")
  }
}

const unbanProduct = async (id) => {
  if (!confirm("Bạn có chắc chắn muốn mở lại sản phẩm này?")) return
  try {
    await axiosClient.patch(`/products/${id}/status`, { status: 'ACTIVE' })
    alert("Đã mở khóa sản phẩm!")
    loadData()
  } catch (e) {
    console.error(e)
    alert("Lỗi khi mở khóa.")
  }
}

const showDetails = (p) => {
  selectedProduct.value = p
}

onMounted(() => {
  loadData()
})
</script>
