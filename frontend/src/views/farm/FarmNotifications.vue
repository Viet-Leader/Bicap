<template>
  <div class="container-fluid py-4">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div><h5 class="mb-0">Trung tâm thông báo</h5><p class="text-sm mb-0 text-muted">Thông báo của tài khoản Farm</p></div>
        <button class="btn btn-outline-primary btn-sm mb-0" type="button" :disabled="!unreadCount" @click="markAllRead">Đánh dấu đã đọc</button>
      </div>
      <div class="card-body">
        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <div v-if="loading" class="text-center py-4">Đang tải thông báo...</div>
        <div v-else-if="notifications.length === 0" class="text-center text-muted py-5">Chưa có thông báo nào.</div>
        <article v-for="item in notifications" v-else :key="item.notificationId" class="border-bottom py-3" :class="{ 'bg-light': !item.isRead }">
          <div class="d-flex justify-content-between gap-3">
            <div><h6 class="mb-1">{{ item.title || 'Thông báo hệ thống' }}</h6><p class="mb-1 text-sm">{{ item.message || item.content }}</p><small class="text-muted">{{ formatDate(item.createdAt) }}</small></div>
            <button v-if="!item.isRead" class="btn btn-sm btn-outline-success mb-0 text-nowrap" @click="markRead(item)">Đã đọc</button>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import axiosClient from '@/api/axiosClient';

const notifications = ref([]);
const loading = ref(true);
const error = ref('');
const unreadCount = computed(() => notifications.value.filter((item) => !item.isRead).length);

const loadNotifications = async () => {
  loading.value = true;
  try {
    const { data } = await axiosClient.get('/notifications');
    notifications.value = data || [];
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tải thông báo.';
  } finally { loading.value = false; }
};

const markRead = async (item) => {
  try {
    await axiosClient.patch(`/notifications/${item.notificationId}/read`);
    item.isRead = true;
  } catch (err) { error.value = err.response?.data?.message || 'Không thể cập nhật thông báo.'; }
};

const markAllRead = async () => {
  try {
    await axiosClient.patch('/notifications/read-all');
    notifications.value.forEach((item) => { item.isRead = true; });
  } catch (err) { error.value = err.response?.data?.message || 'Không thể cập nhật thông báo.'; }
};

const formatDate = (value) => value ? new Date(value).toLocaleString('vi-VN') : '';
onMounted(loadNotifications);
</script>
