<template>
  <div class="container-fluid py-4">
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <div class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div>
          <h5 class="mb-1">Tạo lô sản phẩm</h5>
          <p class="text-muted small mb-0">Gắn lô vào mùa vụ để có thể tạo mã QR truy xuất.</p>
        </div>
        <span class="material-symbols-rounded text-success" style="font-size:28px">qr_code_2</span>
      </div>
      <div class="card-body">
        <form class="row g-3" @submit.prevent="createBatch">
          <div class="col-md-6">
            <label class="form-label" for="batch-crop">Chọn loại sản phẩm <span class="text-danger">*</span></label>
            <select id="batch-crop" v-model="batchForm.cropId" class="form-select" required>
              <option value="" disabled>-- Chọn loại sản phẩm --</option>
              <option v-for="crop in crops" :key="crop.cropId" :value="crop.cropId">
                {{ crop.cropName }}
              </option>
            </select>
          </div>
          <div class="col-md-6">
            <label class="form-label" for="batch-product-name">Tên sản phẩm <span class="text-danger">*</span></label>
            <input
              id="batch-product-name"
              v-model.trim="batchForm.productName"
              class="form-control"
              type="text"
              maxlength="150"
              placeholder="Nhập tên sản phẩm"
              required
            />
          </div>
          <div class="col-md-6">
            <label class="form-label" for="batch-season">Mùa vụ <span class="text-danger">*</span></label>
            <select id="batch-season" v-model="batchForm.seasonId" class="form-select" required>
              <option value="" disabled>-- Chọn mùa vụ --</option>
              <option v-for="season in seasons" :key="season.seasonId" :value="season.seasonId">
                {{ season.seasonName }} ({{ season.status }})
              </option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label" for="batch-grade">Hạng sản phẩm <span class="text-danger">*</span></label>
            <select id="batch-grade" v-model="batchForm.grade" class="form-select" required>
              <option value="A">Hạng A</option>
              <option value="B">Hạng B</option>
              <option value="C">Hạng C</option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label" for="batch-quantity">Số lượng <span class="text-danger">*</span></label>
            <input id="batch-quantity" v-model.number="batchForm.quantity" class="form-control" type="number" min="0.01" step="0.01" required />
          </div>
          <div class="col-md-4">
            <label class="form-label" for="batch-price">Đơn giá <span class="text-danger">*</span></label>
            <input id="batch-price" v-model.number="batchForm.unitPrice" class="form-control" type="number" min="1" step="1" required />
          </div>
          <div class="col-12">
            <label class="form-label" for="batch-images">Tải hình ảnh sản phẩm</label>
            <input
              id="batch-images"
              class="form-control"
              type="file"
              accept="image/*"
              multiple
              @change="handleImageSelection"
            />
            <small class="text-muted">Có thể chọn một hoặc nhiều hình ảnh.</small>
          </div>
          <div class="col-12">
            <button class="btn btn-success mb-0" type="submit" :disabled="savingBatch">
              <span v-if="savingBatch" class="spinner-border spinner-border-sm me-1"></span>
              {{ savingBatch ? 'Đang tạo lô...' : 'Tạo lô sản phẩm' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div class="card">
      <div class="card-header"><h5 class="mb-0">Sản phẩm của tôi</h5></div>
      <div class="table-responsive">
        <table class="table align-items-center mb-0">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Mã batch gần nhất</th>
              <th>Hạng</th>
              <th>Đơn giá</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.productId">
              <td>{{ product.productName }}</td>
              <td>{{ product.batchCode || 'Chưa có lô' }}</td>
              <td>{{ product.grade || '-' }}</td>
              <td>{{ product.unitPrice ? Number(product.unitPrice).toLocaleString('vi-VN') : '-' }}</td>
            </tr>
            <tr v-if="!loading && products.length === 0"><td colspan="4" class="text-center text-muted py-4">Chưa có sản phẩm.</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import axiosClient from '@/api/axiosClient';

const products = ref([]);
const crops = ref([]);
const seasons = ref([]);
const loading = ref(true);
const savingBatch = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const selectedImages = ref([]);
const batchForm = reactive({ cropId: '', productName: '', seasonId: '', grade: 'A', quantity: null, unitPrice: null });

const handleImageSelection = (event) => {
  selectedImages.value = Array.from(event.target.files || []);
};

const loadData = async () => {
  loading.value = true;
  try {
    const [productsResponse, cropsResponse, seasonsResponse] = await Promise.all([
      axiosClient.get('/products?size=100'),
      axiosClient.get('/crops'),
      axiosClient.get('/farming-seasons?size=100'),
    ]);
    products.value = productsResponse.data.content || productsResponse.data || [];
    crops.value = cropsResponse.data || [];
    seasons.value = seasonsResponse.data.content || seasonsResponse.data || [];
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Không thể tải sản phẩm và mùa vụ.';
  } finally {
    loading.value = false;
  }
};

const createBatch = async () => {
  errorMessage.value = '';
  successMessage.value = '';
  savingBatch.value = true;
  try {
    const productName = batchForm.productName.trim();
    const selectedCrop = crops.value.find(crop => crop.cropId === Number(batchForm.cropId));
    let product = products.value.find(item =>
      item.productName?.trim().toLowerCase() === productName.toLowerCase()
      && item.cropName === selectedCrop?.cropName
    );

    if (!product) {
      const { data: createdProduct } = await axiosClient.post('/products', {
        cropId: Number(batchForm.cropId),
        productName,
        unit: 'kg',
      });
      product = createdProduct;
    }

    const { data } = await axiosClient.post(`/products/${product.productId}/batches`, {
      seasonId: Number(batchForm.seasonId),
      grade: batchForm.grade,
      quantity: Number(batchForm.quantity),
      unitPrice: Number(batchForm.unitPrice),
    });

    if (selectedImages.value.length > 0) {
      const formData = new FormData();
      selectedImages.value.forEach(image => formData.append('files', image));
      await axiosClient.post(`/product-images/batch/${data.batchId}`, formData);
    }

    successMessage.value = `Đã tạo lô ${data.batchCode}. Vào Quản lý mùa vụ, chọn mùa vụ đã hoàn thành và bấm Tạo mã QR.`;
    Object.assign(batchForm, { cropId: '', productName: '', seasonId: '', grade: 'A', quantity: null, unitPrice: null });
    selectedImages.value = [];
    document.getElementById('batch-images').value = '';
    await loadData();
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Không thể tạo lô sản phẩm.';
  } finally {
    savingBatch.value = false;
  }
};

onMounted(loadData);
</script>
