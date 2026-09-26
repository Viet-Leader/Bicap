<template>
  <div class="container-fluid py-2">
    <!-- Bộ lọc theo trạng thái -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card">
          <div class="card-body p-3">
            <form @submit.prevent="loadData" class="row align-items-center">
              <div class="col-md-4">
                <label class="form-label mb-0">Lọc theo trạng thái:</label>
              </div>
              <div class="col-md-5">
                <select class="form-select form-select-sm px-3 py-2 border rounded" v-model="status">
                  <option value="">-- Tất cả --</option>
                  <option value="CREATED">Mới tạo</option>
                  <option value="CONFIRMED">Đã xác nhận</option>
                  <option value="COMPLETED">Hoàn thành</option>
                  <option value="REJECTED">Đã từ chối</option>
                </select>
              </div>
              <div class="col-md-3">
                <button type="submit" class="btn btn-sm bg-gradient-primary mb-0 w-100">Lọc</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Danh sách đơn hàng -->
    <div class="row">
      <div class="col-12">
        <div class="card my-4">
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 px-3">
              <h6 class="text-white text-capitalize ps-3 mb-0">Danh sách đơn hàng</h6>
            </div>
          </div>
          <div class="card-body px-0 pb-2">
            <div class="table-responsive p-0">
              <table class="table align-items-center mb-0">
                <thead>
                  <tr>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">ID Đơn hàng</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Tổng tiền</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Trạng thái</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Ngày tạo</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Chi tiết</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="!orders || orders.length === 0">
                    <td colspan="5" class="text-center py-4 text-secondary">Không có đơn hàng nào.</td>
                  </tr>
                  <tr v-for="order in orders" :key="order.orderId">
                    <td>
                      <div class="d-flex px-3 py-1">
                        <div class="d-flex flex-column justify-content-center">
                          <h6 class="mb-0 text-sm">#{{ order.orderId }}</h6>
                        </div>
                      </div>
                    </td>
                    <td>
                      <p class="text-sm font-weight-bold mb-0">
                        {{ (order.totalAmount || 0).toLocaleString('vi-VN') }} đ
                      </p>
                    </td>
                    <td class="align-middle text-center text-sm">
                      <span v-if="order.status === 'CREATED'" class="badge badge-sm bg-gradient-info">Mới tạo</span>
                      <span v-else-if="order.status === 'CONFIRMED'" class="badge badge-sm bg-gradient-warning">Đã xác nhận</span>
                      <span v-else-if="order.status === 'COMPLETED'" class="badge badge-sm bg-gradient-success">Hoàn thành</span>
                      <span v-else-if="order.status === 'REJECTED'" class="badge badge-sm bg-gradient-danger">Từ chối</span>
                      <span v-else class="badge badge-sm bg-gradient-secondary">{{ order.status }}</span>
                    </td>
                    <td class="align-middle text-center">
                      <span class="text-secondary text-xs font-weight-bold">
                        {{ order.createdAt ? new Date(order.createdAt).toLocaleString('vi-VN') : 'N/A' }}
                      </span>
                    </td>
                    <td class="align-middle text-center">
                      <button class="btn btn-link text-info px-3 mb-0" @click="viewOrderDetail(order.orderId)">
                        <i class="material-symbols-rounded text-sm me-2">visibility</i>Xem
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

    <!-- Modal Chi tiết đơn hàng -->
    <div v-if="isModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết đơn hàng #<span>{{ selectedOrderId }}</span></h5>
            <button type="button" class="btn-close text-dark" @click="closeModal">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <div v-if="isLoadingDetail" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </div>
            <div v-else-if="orderDetail">
                <h6 class="mb-3">Sản phẩm trong đơn hàng:</h6>
                <div class="table-responsive">
                    <table class="table table-sm">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Đơn giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(item, idx) in orderDetail.items" :key="idx">
                                <td>{{ item.productName || item.batchCode }}</td>
                                <td>{{ (item.price || 0).toLocaleString('vi-VN') }}đ</td>
                                <td>{{ item.quantity }}</td>
                                <td>{{ ((item.price || 0) * (item.quantity || 1)).toLocaleString('vi-VN') }}đ</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="mt-3 text-end">
                    <h5>Tổng cộng: <span class="text-success">{{ (orderDetail.totalAmount || 0).toLocaleString('vi-VN') }}đ</span></h5>
                </div>
            </div>
            <div v-else class="text-center text-danger">Không thể tải chi tiết đơn hàng.</div>
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
import { computed, onMounted, ref } from 'vue'
import axiosClient from '@/api/axiosClient'

const orders = ref([])
const status = ref('')
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)

const isModalOpen = ref(false)
const selectedOrderId = ref('')
const orderDetail = ref(null)
const isLoadingDetail = ref(false)

const loadData = async () => {
  try {
    const { data } = await axiosClient.get('/orders', {
      params: {
        page: page.value,
        size: size.value,
        status: status.value || undefined
      }
    })
    orders.value = data.content || data || []
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

const viewOrderDetail = async (orderId) => {
    selectedOrderId.value = orderId
    isModalOpen.value = true
    isLoadingDetail.value = true
    orderDetail.value = null
    try {
        const { data } = await axiosClient.get(`/orders/${orderId}`)
        orderDetail.value = data
    } catch (error) {
        console.error(error)
    } finally {
        isLoadingDetail.value = false
    }
}

const closeModal = () => {
    isModalOpen.value = false
    orderDetail.value = null
}

onMounted(() => {
  loadData()
})
</script>
