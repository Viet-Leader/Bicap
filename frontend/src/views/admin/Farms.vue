<template>
  <section>
    <h2 class="text-2xl font-bold">Danh sách trang trại</h2>
    <p class="text-gray-400 mt-1">Admin có thể xem, khóa hoặc mở khóa trang trại.</p>
    <p v-if="error" class="mt-5 text-error">{{ error }}</p>
    <p v-else-if="loading" class="mt-5 text-gray-400">Đang tải…</p>
    <p v-else-if="farms.length === 0" class="mt-5 text-gray-400">Chưa có trang trại.</p>
    <Table v-else class="mt-5" :columns="columns" :data="farms">
      <template #farmName="{ row }">{{ row.farmName }}</template>
      <template #status="{ row }"><span :class="row.status === 'ACTIVE' ? 'text-primary' : 'text-error'">{{ row.status === 'ACTIVE' ? 'Đang hoạt động' : 'Đã khóa' }}</span></template>
      <template #actions="{ row }"><router-link class="text-primary hover:underline mr-3" :to="{ name: 'admin-farm-detail', params: { id: row.farmId } }">Xem</router-link><button class="text-error hover:underline" @click="toggle(row)">{{ row.status === 'ACTIVE' ? 'Khóa' : 'Mở khóa' }}</button></template>
    </Table>
  </section>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import Table from '../../components/ui/Table.vue'
import { adminApi } from '../../api/adminService'
const farms = ref([]); const loading = ref(true); const error = ref('')
const columns = [{ key: 'farmId', label: 'Mã' }, { key: 'farmName', label: 'Tên trang trại' }, { key: 'businessLicense', label: 'Giấy phép' }, { key: 'address', label: 'Địa chỉ' }, { key: 'status', label: 'Trạng thái' }, { key: 'actions', label: 'Thao tác' }]
const load = async () => { loading.value = true; try { farms.value = (await adminApi.farms()).data } catch (e) { error.value = e.response?.data?.message || 'Không thể tải danh sách trang trại.' } finally { loading.value = false } }
const toggle = async (farm) => { const status = farm.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'; const action = status === 'INACTIVE' ? 'khóa' : 'mở khóa'; if (!window.confirm(`Xác nhận ${action} trang trại “${farm.farmName}”? Tài khoản Farm liên kết cũng sẽ ${action}.`)) return; try { const { data } = await adminApi.setFarmStatus(farm.farmId, status); Object.assign(farm, data) } catch (e) { error.value = e.response?.data?.message || `Không thể ${action} trang trại.` } }
onMounted(load)
</script>
