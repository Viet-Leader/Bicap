<template>
  <div class="container-fluid py-4">
    <div class="card card-body">
      <div class="row gx-4 mb-4 align-items-center">
        <div class="col-auto">
          <div class="avatar avatar-xl position-relative">
            <img :src="imagePreview || defaultImage" alt="Ảnh trang trại" class="w-100 border-radius-lg shadow-sm">
          </div>
        </div>
        <div class="col-auto">
          <h5 class="mb-1">{{ form.farmName || 'Trang trại' }}</h5>
          <p class="mb-0 text-sm text-secondary">{{ form.address || 'Chưa cập nhật địa chỉ' }}</p>
        </div>
      </div>

      <div v-if="success" class="alert alert-success">{{ success }}</div>
      <div v-if="error" class="alert alert-danger">{{ error }}</div>
      <div v-if="loading" class="text-center py-4">Đang tải thông tin trang trại...</div>

      <form v-else class="row g-3" @submit.prevent="saveFarm">
        <div class="col-md-6">
          <label class="form-label" for="farm-name">Tên trang trại</label>
          <input id="farm-name" v-model.trim="form.farmName" class="form-control" required maxlength="150">
        </div>
        <div class="col-md-6">
          <label class="form-label" for="farm-email">Email</label>
          <input id="farm-email" v-model.trim="form.email" class="form-control" type="email" maxlength="100">
        </div>
        <div class="col-md-6">
          <label class="form-label" for="farm-area">Diện tích (Ha)</label>
          <input id="farm-area" v-model.number="form.areaSize" class="form-control" type="number" min="0" step="0.01">
        </div>
        <div class="col-12">
          <label class="form-label" for="farm-address">Địa chỉ</label>
          <input id="farm-address" v-model.trim="form.address" class="form-control" required maxlength="255">
        </div>
        <div class="col-12">
          <label class="form-label" for="farm-description">Mô tả</label>
          <textarea id="farm-description" v-model.trim="form.description" class="form-control" rows="4" maxlength="1000"></textarea>
        </div>
        <div class="col-12">
          <label class="form-label" for="farm-image">Ảnh trang trại</label>
          <input id="farm-image" class="form-control" type="file" accept="image/*" @change="selectImage">
          <small class="text-secondary">Chọn ảnh tối đa 1 MB để lưu cùng thông tin trang trại.</small>
        </div>
        <div class="col-12">
          <button class="btn btn-success mb-0" type="submit" :disabled="saving">
            {{ saving ? 'Đang lưu...' : 'Lưu thông tin trang trại' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import axiosClient from '@/api/axiosClient';

const defaultImage = '/admin-assets/img/farm-organic-1.png';
const loading = ref(true);
const saving = ref(false);
const error = ref('');
const success = ref('');
const imagePreview = ref('');
const form = reactive({
  farmName: '',
  address: '',
  description: '',
  email: '',
  areaSize: null,
  imageUrl: '',
});

const loadFarm = async () => {
  loading.value = true;
  error.value = '';
  try {
    const { data } = await axiosClient.get('/farms/me');
    Object.assign(form, {
      farmName: data?.farmName || '',
      address: data?.address || '',
      description: data?.description || '',
      email: data?.email || '',
      areaSize: data?.areaSize ?? null,
      imageUrl: data?.imageUrl || '',
    });
    imagePreview.value = form.imageUrl;
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tải thông tin trang trại.';
  } finally {
    loading.value = false;
  }
};

const selectImage = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    error.value = 'Vui lòng chọn một tệp hình ảnh.';
    return;
  }
  if (file.size > 1024 * 1024) {
    error.value = 'Ảnh không được lớn hơn 1 MB.';
    return;
  }
  const reader = new FileReader();
  reader.onload = () => {
    form.imageUrl = String(reader.result);
    imagePreview.value = form.imageUrl;
    error.value = '';
    success.value = 'Đã chọn ảnh. Nhấn Lưu để cập nhật vào database.';
  };
  reader.readAsDataURL(file);
};

const saveFarm = async () => {
  saving.value = true;
  error.value = '';
  success.value = '';
  try {
    const { data } = await axiosClient.put('/farms/me', form);
    Object.assign(form, {
      farmName: data?.farmName || form.farmName,
      address: data?.address || form.address,
      description: data?.description || form.description,
      email: data?.email || form.email,
      areaSize: data?.areaSize ?? form.areaSize,
      imageUrl: data?.imageUrl || form.imageUrl,
    });
    imagePreview.value = form.imageUrl;
    success.value = 'Đã lưu thông tin trang trại vào database.';
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể lưu thông tin trang trại.';
  } finally {
    saving.value = false;
  }
};

onMounted(loadFarm);
</script>
