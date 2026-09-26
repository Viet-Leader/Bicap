<template>
  <div class="container-fluid py-4">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Quản lý mùa vụ</h5>
        <button class="btn btn-success mb-0" type="button" @click="showForm = !showForm">
          {{ showForm ? 'Đóng' : 'Tạo mùa vụ' }}
        </button>
      </div>
      <div class="card-body">
        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <form v-if="showForm" class="row g-3 mb-4" @submit.prevent="createSeason">
          <div class="col-md-4"><label class="form-label" for="season-name">Tên mùa vụ</label><input id="season-name" v-model="form.seasonName" class="form-control" required></div>
          <div class="col-md-3"><label class="form-label" for="planting-date">Ngày gieo trồng</label><input id="planting-date" v-model="form.plantingDate" class="form-control" type="date" required></div>
          <div class="col-md-3"><label class="form-label" for="harvest-date">Ngày thu hoạch dự kiến</label><input id="harvest-date" v-model="form.expectedHarvestDate" class="form-control" type="date" required></div>
          <div class="col-md-2 d-flex align-items-end"><button class="btn btn-primary mb-0 w-100" type="submit" :disabled="saving">{{ saving ? 'Đang lưu...' : 'Lưu' }}</button></div>
        </form>
        <div v-if="loading" class="text-center py-4">Đang tải mùa vụ...</div>
        <div v-else-if="seasons.length === 0" class="alert alert-info mb-0">Chưa có mùa vụ nào.</div>
        <div v-else class="table-responsive">
          <table class="table align-items-center mb-0">
            <thead><tr><th>Tên mùa vụ</th><th>Ngày gieo</th><th>Ngày thu hoạch</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
            <tbody>
              <tr v-for="season in seasons" :key="season.seasonId">
                <td>{{ season.seasonName }}</td><td>{{ formatDate(season.plantingDate) }}</td><td>{{ formatDate(season.expectedHarvestDate) }}</td>
                <td><span class="badge" :class="statusClass(season.status)">{{ season.status }}</span></td>
                <td class="text-nowrap">
                  <button v-if="season.status === 'PLANNED'" class="btn btn-sm btn-info mb-0 me-1" @click="updateSeason(season.seasonId, 'start')">Bắt đầu</button>
                  <button v-if="season.status === 'IN_PROGRESS'" class="btn btn-sm btn-success mb-0 me-1" @click="updateSeason(season.seasonId, 'harvest')">Thu hoạch</button>
                  <button v-if="!['COMPLETED', 'CANCELLED'].includes(season.status)" class="btn btn-sm btn-outline-danger mb-0" @click="updateSeason(season.seasonId, 'cancel')">Hủy</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import axiosClient from '@/api/axiosClient';

const seasons = ref([]);
const loading = ref(true);
const saving = ref(false);
const showForm = ref(false);
const error = ref('');
const form = reactive({ seasonName: '', plantingDate: '', expectedHarvestDate: '' });

const loadSeasons = async () => {
  loading.value = true;
  error.value = '';
  try {
    const { data } = await axiosClient.get('/farming-seasons');
    seasons.value = data.content || data || [];
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tải danh sách mùa vụ.';
  } finally { loading.value = false; }
};

const createSeason = async () => {
  saving.value = true;
  error.value = '';
  try {
    await axiosClient.post('/farming-seasons', form);
    Object.assign(form, { seasonName: '', plantingDate: '', expectedHarvestDate: '' });
    showForm.value = false;
    await loadSeasons();
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tạo mùa vụ.';
  } finally { saving.value = false; }
};

const updateSeason = async (id, action) => {
  error.value = '';
  try {
    await axiosClient.patch(`/farming-seasons/${id}/${action}`);
    await loadSeasons();
  } catch (err) { error.value = err.response?.data?.message || 'Không thể cập nhật mùa vụ.'; }
};

const formatDate = (value) => value ? new Date(value).toLocaleDateString('vi-VN') : 'Chưa cập nhật';
const statusClass = (status) => ({ PLANNED: 'bg-gradient-secondary', IN_PROGRESS: 'bg-gradient-info', COMPLETED: 'bg-gradient-success', CANCELLED: 'bg-gradient-danger' }[status] || 'bg-gradient-secondary');

onMounted(loadSeasons);
</script>
