
<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import axiosClient from '@/api/axiosClient'
import { useCartStore } from '@/stores/cart'

const cart = useCartStore()
const keyword = ref('')
const products = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const imageUrl = (path) => path || ''

const markImageAsUnavailable = (product) => {
  product.imageUnavailable = true
}

const loadProducts = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const { data } = await axiosClient.get('/public/products', {
      params: {
        keyword: keyword.value || undefined,
        size: 24
      }
    })
    products.value = data.content || []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Không thể tải danh sách sản phẩm'
  } finally {
    isLoading.value = false
  }
}

const addToCart = async (product) => {
  try {
    if (!product.batchId) {
      throw new Error('Sản phẩm hiện chưa có lô hàng khả dụng')
    }
    await cart.addItem(product.batchId, 1, product.farmId)
    alert('Đã thêm vào giỏ hàng')
  } catch (error) {
    alert(error.response?.data?.message || 'Không thể thêm vào giỏ')
  }
}

onMounted(loadProducts)
</script>

<template>
  <div class="content-inner">
    <div v-if="isLoading">Đang tải...</div>
    <div v-else-if="errorMessage">{{ errorMessage }}</div>
    <div v-else-if="!products || products.length === 0">
      <p class="empty-text">Không có sản phẩm nào</p>
    </div>
    <div v-else>
      <div class="grid" id="productGrid">
        <div v-for="p in products" :key="p.productId" class="product-card" :data-name="p.productName?.toLowerCase()">
          <!-- IMAGE -->
          <div class="product-image">
            <img
              v-if="p.thumbnail && !p.imageUnavailable"
              :src="imageUrl(p.thumbnail)"
              :alt="p.productName"
              loading="lazy"
              @error="markImageAsUnavailable(p)"
            >
            <div v-else class="product-image-empty" aria-label="Chưa có ảnh sản phẩm">Chưa có ảnh</div>
          </div>

          <!-- INFO -->
          <div class="product-info">
            <h3>{{ p.productName }}</h3>
            <p class="product-type">Loại sản phẩm: {{ p.cropName }}</p>
            <p class="farm">{{ p.farmName }}</p>
            <p class="price">{{ p.unitPrice?.toLocaleString('vi-VN') }} VND / {{ p.unit || 'đơn vị' }}</p>
            <p class="farm">Còn lại: {{ p.remainingQuantity }} {{ p.unit || '' }}</p>
          </div>

          <!-- ACTION -->
          <div class="product-actions">
            <RouterLink
              v-if="p.batchId"
              :to="`/trace/${p.batchId}`"
              class="btn-outline"
            >
              Truy xuất
            </RouterLink>
            <button
                class="btn-primary add-to-cart-btn"
                @click="addToCart(p)"
            >
              Thêm vào giỏ
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.product-image-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 160px;
  color: #718078;
  background: #f3f6f4;
  font-size: 0.85rem;
}

.product-image {
  min-height: 160px;
  aspect-ratio: 4 / 3;
}

.product-image img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center;
  background: #f3f6f4;
}
</style>
