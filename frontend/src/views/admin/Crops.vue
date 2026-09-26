<template>
  <div class="container-fluid py-2">
    <div class="row mb-4">
      <div class="col-12 text-end">
        <button class="btn bg-gradient-primary mb-0" @click="openCategoryModal()">
          <i class="material-symbols-rounded text-sm">add</i> Thêm danh mục mới
        </button>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="card my-4">
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <div class="bg-gradient-dark shadow-dark border-radius-lg pt-4 pb-3 px-3">
              <h6 class="text-white text-capitalize ps-3 mb-0">Danh sách các loại nông sản</h6>
            </div>
          </div>
          <div class="card-body px-0 pb-2">
            <div class="table-responsive p-0">
              <table class="table align-items-center mb-0">
                <thead>
                  <tr>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Tên danh mục</th>
                    <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Mô tả</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Trạng thái</th>
                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="!categories || categories.length === 0">
                    <td colspan="4" class="text-center py-4 text-secondary">Chưa có danh mục nào. Hãy tạo mới!</td>
                  </tr>
                  <tr v-for="cat in categories" :key="cat.categoryId || cat.id">
                    <td>
                      <div class="d-flex px-3 py-1">
                        <div>
                          <img :src="cat.iconUrl || '/admin-assets/img/default-category.png'" class="avatar avatar-sm me-3 border-radius-lg" alt="icon">
                        </div>
                        <div class="d-flex flex-column justify-content-center">
                          <h6 class="mb-0 text-sm">{{ cat.name || cat.cropName }}</h6>
                        </div>
                      </div>
                    </td>
                    <td>
                      <p class="text-xs font-weight-bold mb-0 text-wrap" style="max-width: 300px;">{{ cat.description || 'Chưa có mô tả' }}</p>
                    </td>
                    <td class="align-middle text-center text-sm">
                      <span v-if="cat.isActive !== false" class="badge badge-sm bg-gradient-success">Hiển thị</span>
                      <span v-else class="badge badge-sm bg-gradient-secondary">Đang ẩn</span>
                    </td>
                    <td class="align-middle text-center">
                      <button class="btn btn-link text-dark px-3 mb-0" @click="editCategory(cat)">
                        <i class="material-symbols-rounded text-sm me-2">edit</i>Sửa
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Thêm/Sửa -->
    <div v-if="isModalOpen" class="modal fade show" style="display: block; background: rgba(0,0,0,0.5)" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalTitle">{{ isEditMode ? 'Cập nhật danh mục' : 'Thêm danh mục mới' }}</h5>
            <button type="button" class="btn-close text-dark" @click="closeModal">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveCategory" class="flex-column">
              <div class="input-group input-group-outline mb-3">
                <label class="form-label" v-if="!formData.name">Tên danh mục</label>
                <input type="text" class="form-control" v-model="formData.name" required placeholder="Tên danh mục">
              </div>
              <div class="input-group input-group-outline mb-3">
                <label class="form-label" v-if="!formData.iconUrl">URL Icon (Link ảnh)</label>
                <input type="text" class="form-control" v-model="formData.iconUrl" placeholder="URL Icon (Link ảnh)">
              </div>
              <div class="input-group input-group-outline mb-3">
                <textarea class="form-control" v-model="formData.description" rows="3" placeholder="Mô tả ngắn..."></textarea>
              </div>
              <div class="form-check form-switch ps-0 d-flex align-items-center">
                <input class="form-check-input ms-0" type="checkbox" v-model="formData.isActive" id="catActive">
                <label class="form-check-label text-body ms-3 mb-0" for="catActive">Hiển thị ngay</label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Hủy</button>
            <button type="button" class="btn bg-gradient-primary" @click="saveCategory">Lưu lại</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import axiosClient from '@/api/axiosClient'

const categories = ref([])
const isModalOpen = ref(false)
const isEditMode = ref(false)

const formData = reactive({
  id: '',
  name: '',
  iconUrl: '',
  description: '',
  isActive: true
})

const loadData = async () => {
  try {
    const { data } = await axiosClient.get('/crops')
    categories.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const openCategoryModal = () => {
  isEditMode.value = false
  formData.id = ''
  formData.name = ''
  formData.iconUrl = ''
  formData.description = ''
  formData.isActive = true
  isModalOpen.value = true
}

const editCategory = (cat) => {
  isEditMode.value = true
  formData.id = cat.cropId || cat.id
  formData.name = cat.cropName || cat.name
  formData.iconUrl = cat.iconUrl || ''
  formData.description = cat.description || ''
  formData.isActive = cat.isActive !== false
  isModalOpen.value = true
}

const saveCategory = async () => {
  try {
    if (isEditMode.value) {
      await axiosClient.put(`/crops/${formData.id}`, {
        cropName: formData.name,
        description: formData.description
      })
      alert('Đã cập nhật danh mục!')
    } else {
      await axiosClient.post('/crops', {
        cropName: formData.name,
        description: formData.description
      })
      alert('Đã thêm danh mục mới!')
    }
    isModalOpen.value = false
    loadData()
  } catch (error) {
    console.error(error)
    alert(error.response?.data?.message || 'Có lỗi xảy ra!')
  }
}

const closeModal = () => {
  isModalOpen.value = false
}

onMounted(() => {
  loadData()
})
</script>
