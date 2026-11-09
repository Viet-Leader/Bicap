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
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Sản phẩm của tôi</h5>
        <span class="text-muted small">{{ products.length }} sản phẩm</span>
      </div>
      <div class="table-responsive">
        <table class="table align-items-center mb-0">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Mã batch</th>
              <th>Hạng</th>
              <th>Đơn giá</th>
              <th class="text-center">Blockchain</th>
              <th class="text-center">Transaction Hash</th>
              <th class="text-center">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading"><td colspan="7" class="text-center py-4"><span class="spinner-border spinner-border-sm me-2"></span>Đang tải...</td></tr>
            <tr v-else-if="products.length === 0"><td colspan="7" class="text-center text-muted py-4">Chưa có sản phẩm.</td></tr>
            <tr v-for="product in products" :key="product.productId">
              <td>
                <span class="text-sm font-weight-bold">{{ product.productName }}</span>
                <p class="text-xs text-secondary mb-0">{{ product.cropName || '' }}</p>
              </td>
              <td><code class="text-xs">{{ product.batchCode || '—' }}</code></td>
              <td><span class="badge badge-sm bg-gradient-secondary">{{ product.grade || '—' }}</span></td>
              <td class="text-xs">{{ product.unitPrice ? Number(product.unitPrice).toLocaleString('vi-VN') + ' ₫' : '—' }}</td>
              <td class="text-center">
                <!-- Kết quả verify inline nếu đã verify -->
                <BlockchainStatusBadge
                  v-if="verifyResults[product.batchId]"
                  :status="verifyResults[product.batchId]"
                  size="sm"
                />
                <!-- Trạng thái gốc từ server nếu chưa verify -->
                <BlockchainStatusBadge
                  v-else
                  :status="product.blockchainStatus || (product.blockchainTxHash ? 'SUCCESS' : 'NOT_RECORDED')"
                  size="sm"
                />
              </td>
              <td class="text-center">
                <HashDisplay :hash="product.blockchainTxHash || product.txHash" :prefix-len="6" :suffix-len="4" />
              </td>
              <td class="text-center text-nowrap">
                <!-- Nút Verify -->
                <VerifyButton
                  v-if="product.batchId && (product.blockchainTxHash || product.txHash)"
                  :is-verifying="verifyingBatch[product.batchId]"
                  label="Verify"
                  @verify="verifyBatch(product)"
                />
                <!-- Nút Ghi Blockchain nếu NOT_RECORDED -->
                <button
                  v-else-if="product.batchId && !product.blockchainTxHash"
                  class="btn btn-sm btn-outline-primary mb-0"
                  :disabled="product.recordingBlockchain"
                  @click="recordBlockchain(product)"
                >
                  <span v-if="product.recordingBlockchain" class="spinner-border spinner-border-sm me-1"></span>
                  {{ product.recordingBlockchain ? 'Đang ghi...' : '⛓️ Ghi Blockchain' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import axiosClient from '@/api/axiosClient';
import BlockchainStatusBadge from '@/components/blockchain/BlockchainStatusBadge.vue';
import HashDisplay from '@/components/blockchain/HashDisplay.vue';
import VerifyButton from '@/components/blockchain/VerifyButton.vue';

const products = ref([]);
const crops = ref([]);
const seasons = ref([]);
const loading = ref(true);
const savingBatch = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const selectedImages = ref([]);
const batchForm = reactive({ cropId: '', productName: '', seasonId: '', grade: 'A', quantity: null, unitPrice: null });

// Trạng thái verify cho từng batch
const verifyingBatch = reactive({}); // { [batchId]: boolean }
const verifyResults  = reactive({}); // { [batchId]: 'VERIFIED' | 'MISMATCH' | ... }

const verifyBatch = async (batch) => {
  if (!batch.batchId) return;
  verifyingBatch[batch.batchId] = true;
  verifyResults[batch.batchId] = '';
  errorMessage.value = '';
  try {
    const { data } = await axiosClient.get(`/product-batches/${batch.batchId}/verify`);
    verifyResults[batch.batchId] = data?.status || data?.verificationStatus || 'VERIFIED';
  } catch (e) {
    verifyResults[batch.batchId] = e.response?.status === 404 ? 'NOT_RECORDED' : 'CONNECTION_ERROR';
    errorMessage.value = e.response?.data?.message || 'Không thể xác minh dữ liệu trên blockchain.';
  } finally {
    verifyingBatch[batch.batchId] = false;
  }
};

const recordBlockchain = async (batch) => {
  if (!confirm(`Ghi lô "${batch.productName}" lên blockchain?`)) return;
  errorMessage.value = '';
  successMessage.value = '';
  batch.recordingBlockchain = true;
  try {
    const { data } = await axiosClient.post(`/product-batches/${batch.batchId}/blockchain`);
    if (!data?.txHash) {
      throw new Error(data?.errorMessage || 'Blockchain chưa trả về transaction hash.');
    }
    successMessage.value = `Đã ghi blockchain thành công. Transaction hash: ${data.txHash}`;
    await loadData();
  } catch (e) {
    errorMessage.value = e.response?.data?.message || e.message || 'Không thể ghi blockchain.';
  } finally {
    batch.recordingBlockchain = false;
  }
};

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
