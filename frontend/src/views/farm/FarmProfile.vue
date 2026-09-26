<template>
  <div class="container-fluid py-4">
    <div class="card card-body">
      <div class="row gx-4 mb-4 align-items-center">
        <div class="col-auto">
          <div class="avatar avatar-xl position-relative">
            <img :src="avatarUrl" alt="Ảnh đại diện trang trại" class="w-100 border-radius-lg shadow-sm">
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

      <form v-else class="row" @submit.prevent="saveProfile">
        <div class="col-12 col-xl-8">
          <div class="card card-plain h-100">
            <div class="card-header px-0"><h6 class="mb-0">Thông tin trang trại</h6></div>
            <div class="card-body px-0">
              <div class="mb-3">
                <label class="form-label" for="farm-name">Tên trang trại</label>
                <input id="farm-name" v-model.trim="form.farmName" class="form-control" type="text" required maxlength="150">
              </div>
              <div class="mb-3">
                <label class="form-label" for="farm-address">Địa chỉ</label>
                <input id="farm-address" v-model.trim="form.address" class="form-control" type="text" required maxlength="255">
              </div>
              <div class="mb-3">
                <label class="form-label" for="farm-description">Mô tả</label>
                <textarea id="farm-description" v-model.trim="form.description" class="form-control" rows="4" maxlength="1000"></textarea>
              </div>
              <button class="btn bg-gradient-dark mb-0" type="submit" :disabled="saving">
                {{ saving ? 'Đang cập nhật...' : 'Cập nhật thông tin' }}
              </button>
            </div>
          </div>
        </div>

        <div class="col-12 col-xl-4">
          <div class="card card-plain h-100">
            <div class="card-header px-0"><h6 class="mb-0">Ảnh trang trại</h6></div>
            <div class="card-body px-0">
              <label class="form-label" for="farm-avatar">Chọn ảnh</label>
              <input id="farm-avatar" class="form-control" type="file" accept="image/*" @change="selectAvatar">
              <p class="text-xs text-secondary mt-2 mb-0">Ảnh sẽ được xem trước ngay sau khi chọn.</p>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import axiosClient from '@/api/axiosClient';

const defaultAvatar = '/admin-assets/img/farm-organic-1.png';
const loading = ref(true);
const saving = ref(false);
const error = ref('');
const success = ref('');
const avatarUrl = ref(localStorage.getItem('bicap_farm_avatar') || defaultAvatar);
const form = reactive({ farmName: '', address: '', description: '' });

const loadProfile = async () => {
  loading.value = true;
  error.value = '';
  try {
    const { data } = await axiosClient.get('/farms/me');
    Object.assign(form, {
      farmName: data?.farmName || '',
      address: data?.address || '',
      description: data?.description || '',
    });
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tải thông tin trang trại.';
  } finally {
    loading.value = false;
  }
};

const selectAvatar = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    error.value = 'Vui lòng chọn một tệp hình ảnh.';
    return;
  }
  if (file.size > 5 * 1024 * 1024) {
    error.value = 'Ảnh không được lớn hơn 5 MB.';
    return;
  }
  error.value = '';
  const reader = new FileReader();
  reader.onload = () => {
    avatarUrl.value = String(reader.result);
    localStorage.setItem('bicap_farm_avatar', avatarUrl.value);
    success.value = 'Đã cập nhật ảnh xem trước.';
  };
  reader.readAsDataURL(file);
};

const saveProfile = async () => {
  saving.value = true;
  error.value = '';
  success.value = '';
  try {
    const { data } = await axiosClient.put('/farms/me', {
      farmName: form.farmName,
      address: form.address,
      description: form.description,
    });
    Object.assign(form, {
      farmName: data?.farmName || form.farmName,
      address: data?.address || form.address,
      description: data?.description || form.description,
    });
    success.value = 'Đã cập nhật thông tin trang trại.';
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể cập nhật thông tin trang trại.';
  } finally {
    saving.value = false;
  }
};

onMounted(loadProfile);
</script>
