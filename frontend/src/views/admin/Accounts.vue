<template>
  <div class="container-fluid py-2">
      <!-- Form Tìm kiếm -->
      <div class="card mb-4">
        <div class="card-body p-3">
          <form @submit.prevent="loadData" class="row gx-3 gy-2 align-items-center">
            <div class="col-md-7">
              <div class="input-group input-group-outline w-100">
                <input type="text" class="form-control" v-model="keyword" placeholder="Nhập username, email hoặc họ tên..." />
              </div>
            </div>
            <div class="col-md-3">
              <div class="input-group input-group-outline w-100">
                <select class="form-control" v-model="role">
                  <option value="">-- Tất cả Role --</option>
                  <option value="ROLE_ADMIN">Quản trị viên (Admin)</option>
                  <option value="ROLE_RETAILER">Nhà Bán Lẻ (Retailer)</option>
                  <option value="ROLE_FARM_OWNER">Chủ Trang Trại</option>
                  <option value="ROLE_SHIPPER">Vận chuyển (Shipper)</option>
                  <option value="ROLE_GUEST">Khách (Guest)</option>
                </select>
              </div>
            </div>
            <div class="col-md-2">
              <button type="submit" class="btn bg-gradient-dark w-100 mb-0">Lọc</button>
            </div>
          </form>
        </div>
      </div>

      <div class="row">
        <div class="col-12">
          <div class="card my-4">
            <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
              <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 d-flex justify-content-between align-items-center px-3">
                <h6 class="text-white text-capitalize ps-3 mb-0">Danh sách Users</h6>
              </div>
            </div>
            <div class="card-body px-0 pb-2">
              <div class="table-responsive p-0">
                <table class="table align-items-center mb-0">
                  <thead>
                    <tr>
                      <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">User Info</th>
                      <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Roles</th>
                      <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Trạng thái</th>
                      <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                      <tr v-if="!users || users.length === 0">
                        <td colspan="4" class="text-center py-4">Không tìm thấy user nào.</td>
                      </tr>
                      <tr v-for="user in users" :key="user.accountId">
                          <td>
                            <div class="d-flex px-3 py-1">
                              <div>
                                <img src="/admin-assets/img/avatar-default.jpg" class="avatar avatar-sm me-3 border-radius-lg" alt="user1" onerror="this.onerror=null;this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2240%22 height=%2240%22%3E%3Crect fill=%22%23eee%22 width=%2240%22 height=%2240%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 fill=%22%23999%22 font-size=%2210%22%3EUser%3C/text%3E%3C/svg%3E'">
                              </div>
                              <div class="d-flex flex-column justify-content-center">
                                <h6 class="mb-0 text-sm">{{ user.username }}</h6>
                                <p class="text-xs text-secondary mb-0">{{ user.email }}</p>
                              </div>
                            </div>
                          </td>
                          <td>
                            <span class="badge badge-sm bg-gradient-info">{{ user.role }}</span>
                          </td>
                          <td class="align-middle text-center text-sm">
                            <span v-if="user.status === 'ACTIVE'" class="badge badge-sm bg-gradient-success">Hoạt động</span>
                            <span v-else-if="user.status === 'PENDING'" class="badge badge-sm bg-gradient-warning text-dark">Chờ duyệt</span>
                            <span v-else class="badge badge-sm bg-gradient-danger">Đã khóa</span>
                          </td>
                          <td class="align-middle text-center">
                              <button v-if="user.status === 'ACTIVE'" class="btn btn-sm btn-outline-danger mb-0" @click="changeStatus(user.accountId, 'BLOCKED')">
                                <i class="material-symbols-rounded text-sm">lock</i> Khóa
                              </button>
                              <button v-else class="btn btn-sm btn-outline-success mb-0" @click="changeStatus(user.accountId, 'ACTIVE')">
                                <i class="material-symbols-rounded text-sm">lock_open</i> Mở
                              </button>
                          </td>
                        </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="card-footer px-3 border-0 d-flex align-items-center justify-content-between">
                <div class="text-secondary text-sm">
                    Hiển thị <b>{{ users.length }}</b> trên tổng số <b>{{ totalElements }}</b> người dùng
                </div>

                <nav aria-label="Page navigation">
                  <ul class="pagination d-flex justify-content-end mb-0">
                    <li class="page-item" :class="{ disabled: page <= 0 }">
                      <a class="page-link" href="#" @click.prevent="goToPage(page - 1)" aria-label="Previous">
                        <span class="material-symbols-rounded text-sm">chevron_left</span>
                      </a>
                    </li>

                    <li class="page-item active">
                        <span class="page-link bg-gradient-dark text-white shadow-dark border-0">
                          {{ page + 1 }} / {{ totalPages > 0 ? totalPages : 1 }}
                        </span>
                    </li>

                    <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
                      <a class="page-link" href="#" @click.prevent="goToPage(page + 1)" aria-label="Next">
                        <span class="material-symbols-rounded text-sm">chevron_right</span>
                      </a>
                    </li>
                  </ul>
                </nav>
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { adminApi } from '@/api/adminService'

const users = ref([])
const keyword = ref('')
const role = ref('')
const page = ref(0)
const size = ref(10)
const totalElements = ref(0)
const totalPages = ref(1)

const loadData = async () => {
  try {
    const { data } = await adminApi.accounts()
    // For now we do local filtering since API might not support it
    let filtered = data || []
    
    if (keyword.value) {
        const val = keyword.value.toLowerCase()
        filtered = filtered.filter(u => u.username?.toLowerCase().includes(val) || u.email?.toLowerCase().includes(val))
    }
    
    if (role.value) {
        filtered = filtered.filter(u => u.role === role.value || u.roleName === role.value)
    }
    
    totalElements.value = filtered.length
    totalPages.value = Math.ceil(filtered.length / size.value)
    
    const start = page.value * size.value
    users.value = filtered.slice(start, start + size.value)
  } catch (e) {
    console.error(e)
  }
}

const goToPage = (p) => {
    if (p >= 0 && p < totalPages.value) {
        page.value = p
        loadData()
    }
}

const changeStatus = async (accountId, newStatus) => {
    const action = newStatus === 'BLOCKED' ? 'KHÓA' : 'MỞ KHÓA'
    if (!confirm(`Bạn có chắc chắn muốn ${action} tài khoản này?`)) return

    try {
        await adminApi.updateAccountStatus(accountId, newStatus)
        alert(`Đã ${action} thành công!`)
        loadData()
    } catch (e) {
        console.error(e)
        alert('Lỗi kết nối server')
    }
}

onMounted(() => {
    loadData()
})
</script>
