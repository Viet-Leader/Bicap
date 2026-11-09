<template>
  <div class="container py-4">
    <div class="d-flex align-items-center justify-content-between mb-4">
      <div>
        <h4 class="mb-1">📦 Lịch sử đơn hàng</h4>
        <p class="text-muted small mb-0">Theo dõi đơn hàng và truy xuất nguồn gốc sản phẩm.</p>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="isLoading" class="text-center py-5">
      <span class="spinner-border text-success"></span>
      <p class="mt-3 text-muted">Đang tải đơn hàng...</p>
    </div>

    <!-- Error -->
    <div v-else-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
      <button class="btn btn-sm btn-outline-danger ms-2" @click="loadOrders">Thử lại</button>
    </div>

    <!-- Empty -->
    <div v-else-if="!orders || orders.length === 0" class="alert alert-light text-center py-5">
      <span style="font-size:3rem">🛒</span>
      <p class="mt-3 mb-0 text-muted">Bạn chưa có đơn hàng nào.</p>
    </div>

    <!-- Order list -->
    <div v-else class="orders-list">
      <div
        v-for="order in orders"
        :key="order.orderId"
        class="card mb-4 order-card"
      >
        <!-- Order header -->
        <div class="card-header d-flex justify-content-between align-items-center">
          <div>
            <span class="text-muted small">Đơn hàng #{{ order.orderId }}</span>
            <h6 class="mb-0 mt-1">{{ order.farmName || 'Nông trại' }}</h6>
            <small class="text-muted">{{ formatVnDateTime(order.createdAt) }}</small>
          </div>
          <div class="text-end">
            <span class="badge" :class="orderStatusClass(order.status)">{{ orderStatusLabel(order.status) }}</span>
            <div class="mt-1 fw-bold text-success">{{ formatPrice(order.totalAmount) }} ₫</div>
          </div>
        </div>

        <!-- Order items -->
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-sm mb-0 align-middle">
              <thead class="table-light">
                <tr>
                  <th class="ps-3">Sản phẩm</th>
                  <th>Mã lô</th>
                  <th class="text-center">Số lượng</th>
                  <th class="text-end pe-3">Thành tiền</th>
                  <th class="text-center">Truy xuất</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="!order.orderDetails || order.orderDetails.length === 0">
                  <td colspan="5" class="text-center text-muted py-3 ps-3">Không có chi tiết đơn hàng.</td>
                </tr>
                <tr v-for="item in order.orderDetails" :key="item.orderDetailId || item.batchId">
                  <td class="ps-3">
                    <span class="fw-bold">{{ item.productName || '—' }}</span>
                    <small class="d-block text-muted">{{ item.cropName || '' }}</small>
                  </td>
                  <td><code class="text-xs">{{ item.batchCode || '—' }}</code></td>
                  <td class="text-center">{{ item.quantity }} {{ item.unit || '' }}</td>
                  <td class="text-end pe-3">{{ formatPrice(item.totalPrice || item.price) }} ₫</td>
                  <td class="text-center">
                    <!-- Nút Truy xuất nguồn gốc (chỉ hiển thị nếu có batchId) -->
                    <router-link
                      v-if="item.batchId"
                      :to="`/trace/${item.batchId}`"
                      class="btn btn-sm btn-outline-success mb-0"
                      title="Xem truy xuất nguồn gốc blockchain"
                      target="_blank"
                    >
                      🔍 Truy xuất
                    </router-link>
                    <span v-else class="text-muted small">—</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Order footer -->
        <div class="card-footer d-flex justify-content-between align-items-center bg-transparent py-2">
          <small class="text-muted">{{ (order.orderDetails || []).length }} sản phẩm</small>
          <router-link
            v-if="hasTraceableItems(order)"
            :to="`/retailer/orders/${order.orderId}/trace`"
            class="btn btn-sm btn-success mb-0"
          >
            ⛓️ Truy xuất tất cả lô trong đơn
          </router-link>
        </div>
      </div>
    </div>

    <!-- Thông báo phân quyền nếu bị từ chối -->
    <div v-if="accessDenied" class="alert alert-warning mt-4">
      <strong>⚠️ Không có quyền truy cập:</strong> Bạn không có quyền xem lô hàng này.
      Chỉ được truy xuất các lô thuộc đơn hàng của bạn.
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axiosClient from '../../api/axiosClient'

const orders = ref([])
const isLoading = ref(true)
const errorMessage = ref('')
const accessDenied = ref(false)

const loadOrders = async () => {
  isLoading.value = true
  errorMessage.value = ''
  accessDenied.value = false
  try {
    const { data } = await axiosClient.get('/orders/retailer')
    orders.value = Array.isArray(data) ? data : (data.content || [])
  } catch (error) {
    if (error.response?.status === 403) {
      accessDenied.value = true
      errorMessage.value = 'Bạn không có quyền xem lô hàng này.'
    } else {
      errorMessage.value = error.response?.data?.message || 'Không thể tải danh sách đơn hàng.'
    }
  } finally {
    isLoading.value = false
  }
}

const hasTraceableItems = (order) =>
  (order.orderDetails || []).some(item => item.batchId)

const formatVnDateTime = (val) => {
  if (!val) return ''
  return new Date(val).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}

const formatPrice = (val) => {
  if (!val && val !== 0) return '—'
  return Number(val).toLocaleString('vi-VN')
}

const ORDER_STATUS_MAP = {
  PENDING:    { label: 'Đang chờ xử lý', cls: 'bg-warning text-dark' },
  CONFIRMED:  { label: 'Đã xác nhận',    cls: 'bg-info text-white'   },
  SHIPPING:   { label: 'Đang giao',       cls: 'bg-primary text-white'},
  DELIVERED:  { label: 'Đã giao',         cls: 'bg-success text-white'},
  CANCELLED:  { label: 'Đã huỷ',          cls: 'bg-danger text-white' },
}

const orderStatusLabel = (s) => ORDER_STATUS_MAP[s]?.label || s
const orderStatusClass = (s) => ORDER_STATUS_MAP[s]?.cls  || 'bg-secondary text-white'

onMounted(loadOrders)
</script>

<style scoped>
.order-card { border-radius: 12px; overflow: hidden; }
.order-card .card-header { background: #f9fafb; }
</style>
