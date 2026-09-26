<template>
  <div class="login-page d-flex align-items-center justify-content-center py-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-10 col-xl-9">
                <div class="card login-card shadow-lg border-0 overflow-hidden">
                    <div class="row g-0">
                        <div class="col-lg-5 d-none d-lg-flex flex-column justify-content-center p-5 login-image-bg text-white">
                            <div class="position-relative z-2">
                                <h3 class="fw-bold mb-4">Gia nhập mạng lưới BiCap</h3>
                                <div class="d-flex align-items-start mb-4">
                                    <div class="bg-white text-success rounded-circle p-2 me-3 shadow-sm">
                                        <i class="material-symbols-rounded">verified</i>
                                    </div>
                                    <div>
                                        <h6 class="mb-0 font-weight-bold">Minh bạch 100%</h6>
                                        <small class="opacity-75" style="font-size: 0.75rem;">Mọi thông tin được mã hóa trên Blockchain</small>
                                    </div>
                                </div>
                                <div class="d-flex align-items-start mb-4">
                                    <div class="bg-white text-success rounded-circle p-2 me-3 shadow-sm">
                                        <i class="material-symbols-rounded">storefront</i>
                                    </div>
                                    <div>
                                        <h6 class="mb-0 font-weight-bold">Kết nối trực tiếp</h6>
                                        <small class="opacity-75" style="font-size: 0.75rem;">Không qua trung gian thương lái</small>
                                    </div>
                                </div>
                                <div class="d-flex align-items-start">
                                    <div class="bg-white text-success rounded-circle p-2 me-3 shadow-sm">
                                        <i class="material-symbols-rounded">local_shipping</i>
                                    </div>
                                    <div>
                                        <h6 class="mb-0 font-weight-bold">Logistics thông minh</h6>
                                        <small class="opacity-75" style="font-size: 0.75rem;">Vận chuyển tối ưu hóa</small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-7 bg-white">
                            <div class="card-body p-4 p-md-5">
                                <div class="text-center mb-4">
                                    <h4 class="fw-bold text-dark">Tạo tài khoản</h4>
                                    <p class="text-muted small">Điền thông tin để bắt đầu hành trình của bạn</p>
                                </div>

                                <div v-if="error" class="alert alert-danger d-flex align-items-center p-2 mb-3" role="alert">
                                    <i class="material-symbols-rounded me-2">error</i>
                                    <small>{{ error }}</small>
                                </div>

                                <form @submit.prevent="handleRegister">
                                    <div class="row g-3 mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label small text-muted fw-bold">HỌ TÊN</label>
                                            <div class="input-group">
                                                <span class="input-group-text text-muted"><i class="material-symbols-rounded">person</i></span>
                                                <input type="text" v-model="form.fullname" class="form-control" placeholder="Nguyễn Văn A" required>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small text-muted fw-bold">SỐ ĐIỆN THOẠI</label>
                                            <div class="input-group">
                                                <span class="input-group-text text-muted"><i class="material-symbols-rounded">smartphone</i></span>
                                                <input type="tel" v-model="form.phone" class="form-control" placeholder="09xxxxxxx" required>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label small text-muted fw-bold">EMAIL</label>
                                        <div class="input-group">
                                            <span class="input-group-text text-muted"><i class="material-symbols-rounded">mail</i></span>
                                            <input type="email" v-model="form.email" class="form-control" placeholder="name@example.com" required>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label small text-muted fw-bold">VAI TRÒ</label>
                                        <select class="form-select" v-model="form.role">
                                            <option value="FARM">Nông trại (Farm)</option>
                                            <option value="RETAILER">Nhà bán lẻ (Retailer)</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label small text-muted fw-bold">MẬT KHẨU</label>
                                        <div class="input-group">
                                            <span class="input-group-text text-muted"><i class="material-symbols-rounded">lock</i></span>
                                            <input type="password" v-model="form.password" @input="checkStrength" class="form-control" placeholder="Tối thiểu 8 ký tự" required minlength="8">
                                        </div>
                                        <div class="password-strength mt-2">
                                            <div class="bar" :style="{ width: strengthWidth, background: strengthColor }"></div>
                                        </div>
                                        <div class="d-flex justify-content-between mt-1">
                                            <small class="text-muted" style="font-size: 0.7rem;" :style="{ color: strengthColor }">{{ strengthText }}</small>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label small text-muted fw-bold">XÁC NHẬN MẬT KHẨU</label>
                                        <div class="input-group">
                                            <span class="input-group-text text-muted"><i class="material-symbols-rounded">lock_reset</i></span>
                                            <input type="password" v-model="form.confirmPassword" class="form-control" placeholder="Nhập lại mật khẩu" required>
                                        </div>
                                    </div>

                                    <div class="mb-4">
                                        <div class="form-check">
                                            <input type="checkbox" v-model="form.terms" class="form-check-input" id="terms" required>
                                            <label class="form-check-label small text-muted" for="terms">
                                                Tôi đồng ý với <a href="#" class="text-success text-decoration-none">Điều khoản sử dụng</a> & <a href="#" class="text-success text-decoration-none">Chính sách bảo mật</a>
                                            </label>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn btn-bicap w-100 py-2 fs-6 shadow-sm" :disabled="isSubmitting">
                                        {{ isSubmitting ? 'Đang đăng ký...' : 'Đăng ký tài khoản' }}
                                    </button>
                                </form>

                                <div class="text-center mt-4">
                                    <p class="mb-0 text-muted small">Đã có tài khoản? <router-link to="/login" class="text-success fw-bold text-decoration-none">Đăng nhập ngay</router-link></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="text-center mt-4">
                    <router-link to="/" class="text-muted text-decoration-none opacity-75 hover-opacity-100">
                        <i class="material-symbols-rounded align-middle fs-5">arrow_back</i> Quay về trang chủ
                    </router-link>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = ref({
  fullname: '',
  phone: '',
  email: '',
  role: 'FARM',
  password: '',
  confirmPassword: '',
  terms: false
})
const error = ref('')
const isSubmitting = ref(false)

const strengthWidth = ref('0%')
const strengthColor = ref('#e0e0e0')
const strengthText = ref('Độ mạnh mật khẩu')

const checkStrength = () => {
  const pwd = form.value.password
  let strength = 0
  if (pwd.length >= 8) strength++
  if (pwd.match(/[a-z]/)) strength++
  if (pwd.match(/[A-Z]/)) strength++
  if (pwd.match(/[0-9]/)) strength++
  if (pwd.match(/[^a-zA-Z0-9]/)) strength++
  
  const colors = ['#dc3545', '#ffc107', '#ffc107', '#4CAF50', '#2E7D32']
  const texts = ['Rất yếu', 'Yếu', 'Trung bình', 'Mạnh', 'Rất mạnh']
  const widths = ['20%', '40%', '60%', '80%', '100%']
  
  if (strength === 0) {
      strengthWidth.value = '0%'
      strengthColor.value = '#e0e0e0'
      strengthText.value = 'Độ mạnh mật khẩu'
  } else {
      strengthWidth.value = widths[strength - 1]
      strengthColor.value = colors[strength - 1]
      strengthText.value = texts[strength - 1]
  }
}

const handleRegister = async () => {
  error.value = ''
  if (form.value.password !== form.value.confirmPassword) {
      error.value = 'Mật khẩu không khớp'
      return
  }
  isSubmitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 800))
    auth.login({ username: form.value.email, role: form.value.role })
    if (form.value.role === 'FARM') router.push('/farm')
    else router.push('/retailer')
  } catch (err) {
    error.value = 'Lỗi đăng ký'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.login-image-bg {
    background: linear-gradient(135deg, rgba(46, 125, 50, 0.8), rgba(27, 94, 32, 0.9)), url('https://images.unsplash.com/photo-1595855709915-d7b594b91763?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80');
    background-size: cover;
    background-position: center;
}
.input-group-text {
    background-color: #f8f9fa;
    border-right: none;
}
.form-control {
    border-left: none;
}
.form-control:focus {
    box-shadow: none;
    border-color: #ced4da;
}
.input-group:focus-within {
    box-shadow: 0 0 0 0.25rem rgba(76, 175, 80, 0.25);
    border-radius: 0.375rem;
}
.input-group:focus-within .input-group-text, 
.input-group:focus-within .form-control {
    border-color: #4CAF50;
}
.password-strength .bar {
    height: 4px;
    border-radius: 2px;
    transition: all 0.3s ease;
}
</style>
