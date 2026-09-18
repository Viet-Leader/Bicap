<template><section><div class="flex justify-between gap-4 mb-6"><div><h2 class="text-2xl font-bold">Tài khoản</h2><p class="text-gray-400">Quản lý tài khoản Farm theo API hệ thống.</p></div><router-link class="px-4 py-2 rounded bg-primary text-secondary font-semibold" :to="{ name: 'admin-account-create' }">Tạo tài khoản Farm</router-link></div>
  <input v-model="query" class="mb-4 w-full max-w-sm rounded bg-white/10 border border-white/20 p-2" placeholder="Tìm username, họ tên, email" />
  <p v-if="error" class="text-error">{{ error }}</p><p v-else-if="loading" class="text-gray-400">Đang tải…</p><p v-else-if="filtered.length === 0" class="text-gray-400">Không có tài khoản phù hợp.</p>
  <Table v-else :columns="columns" :data="filtered"><template #role="{ row }"><span>{{ row.role }}</span></template><template #status="{ row }"><span :class="row.status === 'ACTIVE' ? 'text-primary' : 'text-error'">{{ row.status }}</span></template><template #actions="{ row }"><router-link class="text-primary hover:underline" :to="{ name: 'admin-account-detail', params: { id: row.accountId } }">Xem</router-link></template></Table>
</section></template>
<script setup>
import { computed, onMounted, ref } from 'vue'; import Table from '../../components/ui/Table.vue'; import { adminApi } from '../../api/adminService'
const accounts = ref([]); const loading = ref(true); const error = ref(''); const query = ref(''); const columns = [{ key:'username', label:'Tên đăng nhập' }, { key:'fullName', label:'Họ tên' }, { key:'email', label:'Email' }, { key:'role', label:'Vai trò' }, { key:'status', label:'Trạng thái' }, { key:'actions', label:'' }]
const filtered = computed(() => { const value = query.value.toLowerCase(); return accounts.value.filter((a) => [a.username, a.fullName, a.email, a.role, a.status].some((v) => String(v || '').toLowerCase().includes(value))) })
onMounted(async () => { try { accounts.value = (await adminApi.accounts()).data } catch (e) { error.value = e.response?.data?.message || 'Không thể tải danh sách tài khoản.' } finally { loading.value = false } })
</script>
