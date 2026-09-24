
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
const selectedProduct = ref(null)

const imageUrl = (path) => path || ''

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

const openProductDetail = (product) => {
  selectedProduct.value = product
}

const closeProductDetail = () => {
  selectedProduct.value = null
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
            <img v-if="p.thumbnail" :src="imageUrl(p.thumbnail)" :alt="p.productName">
            <div v-else class="product-image-empty" aria-label="Chưa có ảnh sản phẩm">Chưa có ảnh</div>
          </div>

          <!-- INFO -->
          <div class="product-info">
            <h3>{{ p.productName }}</h3>
            <p class="farm">{{ p.farmName }}</p>
            <p class="price">{{ p.unitPrice?.toLocaleString('vi-VN') }} VND / {{ p.unit || 'đơn vị' }}</p>
            <p class="farm">Còn lại: {{ p.remainingQuantity }} {{ p.unit || '' }}</p>
          </div>

          <!-- ACTION -->
          <div class="product-actions">
            <button class="btn-outline" @click="openProductDetail(p)">
               Xem chi tiết
            </button>
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

    <!-- PRODUCT DETAIL MODAL -->
    <div v-if="selectedProduct" class="qr-overlay" id="productDetailOverlay" @click="closeProductDetail">
      <div class="product-detail-modal" @click.stop>
        <button class="product-detail-close" @click="closeProductDetail">&times;</button>
        <div class="product-detail-content">
          <div class="product-detail-image-wrap">
            <img v-if="selectedProduct.thumbnail" id="productDetailImg" :src="imageUrl(selectedProduct.thumbnail)" :alt="selectedProduct.productName">
            <div v-else class="product-image-empty" aria-label="Chưa có ảnh sản phẩm">Chưa có ảnh</div>
          </div>
          <div class="product-detail-info">
            <h3 id="productDetailName">{{ selectedProduct.productName }}</h3>
            <p class="product-detail-meta"><span id="productDetailFarm">Farm: {{ selectedProduct.farmName }}</span></p>
            <p class="product-detail-price" id="productDetailPrice">{{ selectedProduct.unitPrice?.toLocaleString('vi-VN') }} VND / {{ selectedProduct.unit || 'đơn vị' }}</p>
            <p class="product-detail-desc" id="productDetailDesc">{{ selectedProduct.description || 'Sản phẩm an toàn từ mạng lưới BICAP.' }}</p>
            <button class="btn-primary add-to-cart-detail" id="productDetailAddCart" @click="addToCart(selectedProduct); closeProductDetail()">
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
</style>
