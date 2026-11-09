
<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import axiosClient from '@/api/axiosClient'
import { useCartStore } from '@/stores/cart'

const cart = useCartStore()
const keyword = ref('')
const products = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const onlyBlockchain = ref(false) // filter chỉ hiện sản phẩm có truy xuất

const imageUrl = (path) => path || ''

const markImageAsUnavailable = (product) => {
  product.imageUnavailable = true
}

const filteredProducts = computed(() => {
  if (!onlyBlockchain.value) return products.value
  return products.value.filter(p => p.batchId && p.hasBlockchain !== false)
})

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
    <!-- Filter: chỉ hiện sản phẩm có truy xuất Blockchain -->
    <div class="filter-bar mb-3 d-flex align-items-center gap-3 flex-wrap">
      <label class="blockchain-filter d-flex align-items-center gap-2 cursor-pointer">
        <input type="checkbox" v-model="onlyBlockchain" class="form-check-input mt-0" />
        <span class="blockchain-shield">🛡️</span>
        <span class="filter-label">Chỉ hiện sản phẩm có truy xuất Blockchain</span>
      </label>
    </div>

    <div v-if="isLoading">Đang tải...</div>
    <div v-else-if="errorMessage">{{ errorMessage }}</div>
    <div v-else-if="!filteredProducts || filteredProducts.length === 0">
      <p class="empty-text">Không có sản phẩm nào</p>
    </div>
    <div v-else>
      <div class="grid" id="productGrid">
        <div v-for="p in filteredProducts" :key="p.productId" class="product-card" :data-name="p.productName?.toLowerCase()">
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

            <!-- Badge Blockchain (nếu sản phẩm có batchId => có truy xuất) -->
            <span
              v-if="p.batchId"
              class="blockchain-badge"
              title="Sản phẩm có thể xác minh nguồn gốc trên blockchain"
            >
              🛡️ Truy xuất Blockchain
            </span>
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
              title="Xem truy xuất nguồn gốc blockchain"
            >
              🔍 Truy xuất
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
.filter-bar { padding: 8px 0; }
.blockchain-filter { font-size: 0.85rem; cursor: pointer; user-select: none; }
.filter-label { color: #2d6a2d; font-weight: 500; }

.blockchain-badge {
  position: absolute;
  top: 8px; left: 8px;
  background: rgba(21, 128, 61, 0.9);
  color: #fff;
  font-size: 0.65rem;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 20px;
  white-space: nowrap;
  pointer-events: none;
}

.product-image { position: relative; }

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
