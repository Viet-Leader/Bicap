<template>
  <div class="login-page d-flex align-items-center justify-content-center py-5">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-lg-10 col-xl-9">
          <div class="card login-card shadow-lg border-0 overflow-hidden">
            <div class="row g-0">
              <div class="col-lg-5 d-none d-lg-flex flex-column justify-content-center align-items-center text-white p-5 login-image-bg">
                <div class="text-center position-relative z-2">
                  <div class="bg-white rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3 shadow-sm" style="width: 80px; height: 80px;">
                    <span style="font-size: 3rem;">🌱</span>
                  </div>
                  <h2 class="fw-bold mb-2">BiCap Farm</h2>
                  <p class="opacity-75">Nền tảng nông nghiệp sạch<br/>ứng dụng Blockchain</p>
                </div>
              </div>

              <div class="col-lg-7 bg-white">
                <div class="card-body p-4 p-md-5">
                  <div class="text-center mb-4">
                    <h4 class="fw-bold text-dark">Đăng nhập</h4>
                    <p class="text-muted small">Chào mừng bạn quay trở lại!</p>
                  </div>

                  <div v-if="error" class="alert alert-danger d-flex align-items-center p-2 mb-4" role="alert">
                    <i class="material-symbols-rounded me-2">error</i>
                    <small>{{ error }}</small>
                  </div>
                  
                  <div v-if="success" class="alert alert-success d-flex align-items-center p-2 mb-4" role="alert">
                    <i class="material-symbols-rounded me-2">check_circle</i>
                    <small>{{ success }}</small>
                  </div>

                  <form @submit.prevent="handleLogin">
                    <div class="mb-3">
                      <label class="form-label small text-muted fw-bold">TÊN ĐĂNG NHẬP HOẶC EMAIL</label>
                      <div class="input-group">
                        <span class="input-group-text text-muted"><i class="material-symbols-rounded">mail</i></span>
                          <input type="text" v-model="email" class="form-control" placeholder="admin hoặc email của bạn" required>
                      </div>
                    </div>

                    <div class="mb-3">
                      <label class="form-label small text-muted fw-bold">MẬT KHẨU</label>
                      <div class="input-group">
                        <span class="input-group-text text-muted"><i class="material-symbols-rounded">lock</i></span>
                        <input type="password" v-model="password" class="form-control" placeholder="••••••••" required>
                      </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                      <div class="form-check">
                        <input type="checkbox" class="form-check-input" id="remember" v-model="remember">
                        <label class="form-check-label small text-muted" for="remember">Ghi nhớ tôi</label>
                      </div>
                      <a href="#" class="text-success small fw-bold text-decoration-none">Quên mật khẩu?</a>
                    </div>

                    <button type="submit" class="btn btn-bicap w-100 py-2 fs-6 shadow-sm" :disabled="loading">
                      {{ loading ? 'Đang xử lý...' : 'Đăng nhập' }}
                    </button>
                  </form>

                  <div class="position-relative text-center my-4">
                    <hr class="text-muted opacity-25">
                    <span class="position-absolute top-50 start-50 translate-middle px-3 bg-white text-muted small">hoặc</span>
                  </div>

                  <div class="text-center">
                    <p class="mb-0 text-muted small">Chưa có tài khoản? <router-link to="/register" class="text-success fw-bold text-decoration-none">Đăng ký ngay</router-link></p>
                  </div>

                  <div class="mt-4 p-3 bg-light rounded text-center border border-dashed">
                    <small class="text-muted d-block">
                      💡 <strong>Demo Account:</strong>
                    </small>
                      <code class="text-dark">admin</code> / <code class="text-dark">mật khẩu đã cấu hình trong DB</code>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const remember = ref(false)
const error = ref('')
const success = ref('')
const loading = ref(false)

const handleLogin = async () => {
    error.value = ''
    success.value = ''
    loading.value = true
    try {
        await authStore.login(email.value.trim(), password.value)
        success.value = 'Đăng nhập thành công!'
        
        // Điều hướng dựa trên role (Tất cả dùng chung trang này)
        const redirectPath = route.query.redirect || '/'
        if (redirectPath !== '/') {
            router.push(redirectPath)
        } else if (authStore.isAdmin) {
            router.push('/admin/dashboard')
        } else if (authStore.isFarm) {
            router.push('/farm')
        } else if (authStore.isRetailer) {
            router.push('/retailer')
        } else {
            router.push('/')
        }
    } catch (e) {
        error.value = e.response?.data?.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại email hoặc mật khẩu.'
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-page {
    /* Đảm bảo khung login có chiều cao vừa vặn */
    min-height: calc(100vh - 150px);
}
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
.border-dashed {
    border-style: dashed !important;
}
.btn-bicap {
    background-color: #4CAF50;
    color: white;
    border: none;
}
.btn-bicap:hover {
    background-color: #388E3C;
    color: white;
}
</style>
