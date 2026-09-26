
<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import axiosClient from '@/api/axiosClient'

const keyword = ref('')
const selectedCropId = ref(null)
const sortOption = ref('')

const products = ref([])
const crops = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

// Load danh sách loại cây trồng từ DB để làm bộ lọc thật
const loadCrops = async () => {
  try {
    const { data } = await axiosClient.get('/crops')
    crops.value = data || []
  } catch {
    crops.value = []
  }
}

// Load sản phẩm thật từ DB
const loadProducts = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const params = { size: 24 }
    if (keyword.value?.trim()) params.keyword = keyword.value.trim()
    if (selectedCropId.value) params.cropId = selectedCropId.value

    const { data } = await axiosClient.get('/public/products', { params })
    let list = data.content || []

    // Sort client-side
    if (sortOption.value === 'price-asc') {
      list = [...list].sort((a, b) => (a.unitPrice ?? Infinity) - (b.unitPrice ?? Infinity))
    } else if (sortOption.value === 'price-desc') {
      list = [...list].sort((a, b) => (b.unitPrice ?? -1) - (a.unitPrice ?? -1))
    }

    products.value = list
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Không thể tải danh sách sản phẩm'
  } finally {
    isLoading.value = false
  }
}

const selectCrop = (cropId) => {
  selectedCropId.value = cropId
  loadProducts()
}

const getProductEmoji = (cropName) => {
  if (!cropName) return '🥬'
  const lower = cropName.toLowerCase()
  if (lower.includes('xoài') || lower.includes('xoai')) return '🥭'
  if (lower.includes('chuối') || lower.includes('chuoi')) return '🍌'
  if (lower.includes('cam')) return '🍊'
  if (lower.includes('dưa') || lower.includes('dua')) return '🍉'
  if (lower.includes('cà chua') || lower.includes('ca chua')) return '🍅'
  if (lower.includes('ớt') || lower.includes('ot')) return '🌶️'
  if (lower.includes('cà rốt') || lower.includes('ca rot')) return '🥕'
  if (lower.includes('bông cải') || lower.includes('bong cai')) return '🥦'
  if (lower.includes('cải') || lower.includes('xà lách') || lower.includes('cai')) return '🥬'
  if (lower.includes('lúa') || lower.includes('gạo') || lower.includes('lua')) return '🌾'
  return '🥬'
}

onMounted(() => {
  loadCrops()
  loadProducts()
})
</script>

<template>
  <div class="container py-4 mt-lg-5">
    <!-- Hero Section -->
    <div class="card border-0 shadow-sm mb-4" style="background: linear-gradient(135deg, #E8F5E9, #C8E6C9);">
        <div class="card-body p-4">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2 class="font-weight-bold mb-2">🏪 Sàn Giao Dịch Nông Sản</h2>
                    <p class="text-muted mb-0">Kết nối trực tiếp nông trại với người tiêu dùng - 100% truy xuất nguồn gốc</p>
                </div>
                <div class="col-md-4 text-end d-none d-md-block">
                    <span style="font-size: 4rem;">🥬🍅🥕</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Search & Filter -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <form @submit.prevent="loadProducts">
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="d-flex h-100">
                            <span class="d-flex align-items-center px-3 bg-light border border-end-0 rounded-start text-muted">🔍</span>
                            <input type="text" v-model="keyword" class="form-control bg-light border-start-0 ps-0"
                                   placeholder="Tìm kiếm sản phẩm, nông trại..."
                                   style="border-radius: 0 0.5rem 0.5rem 0;">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select v-model="sortOption" class="form-select bg-light" @change="loadProducts">
                            <option value="">Sắp xếp theo</option>
                            <option value="price-asc">Giá: Thấp → Cao</option>
                            <option value="price-desc">Giá: Cao → Thấp</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button type="submit" class="btn btn-bicap w-100 h-100">🔍 Tìm kiếm</button>
                    </div>
                </div>

                <!-- Category Chips từ DB -->
                <div class="d-flex flex-wrap gap-2 mt-3">
                    <button type="button" @click="selectCrop(null)"
                            class="btn btn-sm rounded-pill"
                            :class="!selectedCropId ? 'btn-success' : 'btn-outline-success'">
                        📋 Tất cả
                    </button>

                    <button v-for="crop in crops" :key="crop.cropId" type="button"
                            @click="selectCrop(crop.cropId)"
                            class="btn btn-sm rounded-pill"
                            :class="selectedCropId === crop.cropId ? 'btn-success' : 'btn-outline-success'">
                        {{ getProductEmoji(crop.cropName) }} {{ crop.cropName }}
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Stats Bar -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <p class="text-muted mb-0">
            Tìm thấy <strong class="text-success">{{ products.length }}</strong> sản phẩm
            <span v-if="keyword"> cho "<strong>{{ keyword }}</strong>"</span>
        </p>
    </div>

    <!-- State Messages -->
    <div v-if="isLoading" class="text-center py-5 text-muted">
        <div class="spinner-border text-success" role="status"></div>
        <p class="mt-2">Đang tải sản phẩm...</p>
    </div>

    <div v-else-if="errorMessage" class="alert alert-danger text-center">
        {{ errorMessage }} <button class="btn btn-link p-0 ms-2" @click="loadProducts">Thử lại</button>
    </div>

    <!-- Products Grid -->
    <div v-else class="row g-4">
        <div v-if="products.length === 0" class="col-12">
            <div class="card border-0 shadow-sm text-center py-5">
                <span style="font-size: 4rem;">📦</span>
                <h5 class="mt-3 font-weight-bold">Không tìm thấy sản phẩm</h5>
                <p class="text-muted">Hãy thử tìm kiếm với từ khóa khác hoặc bỏ bộ lọc</p>
                <button @click="keyword=''; selectedCropId=null; loadProducts()" class="btn btn-bicap mt-2">Xem tất cả sản phẩm</button>
            </div>
        </div>

        <div v-for="product in products" :key="product.productId" class="col-lg-3 col-md-4 col-sm-6">
            <div class="card border-0 shadow-sm h-100 product-card transition-all hover-translate-up">
                <!-- Product Image -->
                <div class="position-relative">
                    <div v-if="product.thumbnail"
                         style="height: 180px; border-radius: 0.5rem 0.5rem 0 0; overflow: hidden;">
                        <img :src="product.thumbnail" :alt="product.productName"
                             style="width: 100%; height: 100%; object-fit: cover;">
                    </div>
                    <div v-else
                         style="height: 180px; background: linear-gradient(135deg, #f8f9fa, #e9ecef); display: flex; align-items: center; justify-content: center; border-radius: 0.5rem 0.5rem 0 0;">
                        <span style="font-size: 4rem;">{{ getProductEmoji(product.cropName) }}</span>
                    </div>
                    <!-- Badge tồn kho -->
                    <span v-if="product.batchId" class="position-absolute badge bg-success shadow-sm" style="top: 10px; right: 10px;">
                        ✅ Còn hàng
                    </span>
                    <span v-else class="position-absolute badge bg-secondary shadow-sm" style="top: 10px; right: 10px;">
                        ⏸ Hết hàng
                    </span>
                </div>

                <!-- Product Info -->
                <div class="card-body d-flex flex-column">
                    <h6 class="font-weight-bold mb-1 text-truncate" :title="product.productName">{{ product.productName }}</h6>
                    <p class="text-muted small mb-1 text-truncate">🌿 {{ product.cropName }}</p>
                    <p class="text-muted small mb-2 text-truncate">🏠 {{ product.farmName }}</p>

                    <!-- Tồn kho & Hạng -->
                    <div class="mb-2 d-flex flex-wrap gap-1">
                        <span class="badge bg-light text-dark border">
                            📦 Còn: {{ product.remainingQuantity ?? 0 }} {{ product.unit }}
                        </span>
                        <span v-if="product.grade" class="badge bg-warning text-dark">
                            Hạng {{ product.grade }}
                        </span>
                    </div>

                    <!-- Price & Action -->
                    <div class="mt-auto pt-3">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <div>
                                <span v-if="product.unitPrice" class="text-success font-weight-bold fs-5">
                                    {{ product.unitPrice.toLocaleString('vi-VN') }}đ
                                </span>
                                <span v-else class="text-muted fst-italic small">Chưa có giá</span>
                                <span class="text-muted small">/{{ product.unit }}</span>
                            </div>
                        </div>
                        <div class="d-flex gap-2">
                            <RouterLink v-if="product.batchCode"
                                        :to="`/trace/${product.batchCode}`"
                                        class="btn btn-sm btn-outline-success flex-grow-1 rounded-pill">
                                📱 Truy xuất
                            </RouterLink>
                            <span v-else class="btn btn-sm btn-outline-secondary flex-grow-1 rounded-pill disabled">
                                📱 Truy xuất
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
    transition: all 0.3s ease;
}
.hover-translate-up:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}
.cursor-pointer {
    cursor: pointer;
}
</style>
