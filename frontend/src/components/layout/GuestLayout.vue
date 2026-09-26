<template>
  <div class="guest-layout-wrapper">
    <nav class="navbar navbar-expand-lg navbar-light bg-white fixed-top shadow-sm transition-all">
        <div class="container">
            <router-link class="navbar-brand d-flex align-items-center" to="/">
                <span class="material-symbols-rounded text-success me-2 fs-1">eco</span>
                <div class="d-flex flex-column lh-1">
                    <span class="fw-bold text-dark fs-5">BiCap</span>
                    <small class="text-success fw-bold" style="font-size: 0.7rem; letter-spacing: 1px;">SYSTEM</small>
                </div>
            </router-link>
            <button class="navbar-toggler border-0" type="button" :aria-expanded="isMenuOpen" aria-controls="navbarNav" aria-label="Mở menu điều hướng" @click="isMenuOpen = !isMenuOpen">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div id="navbarNav" class="guest-nav-menu navbar-collapse" :class="{ 'is-open': isMenuOpen }">
                <ul class="navbar-nav mx-auto">
                    <li class="nav-item"><router-link class="nav-link" to="/" @click="isMenuOpen = false">Trang chủ</router-link></li>
                    <li class="nav-item"><router-link class="nav-link" to="/market" @click="isMenuOpen = false">Sàn giao dịch</router-link></li>
                    <li class="nav-item"><router-link class="nav-link" to="/trace" @click="isMenuOpen = false">Truy xuất</router-link></li>
                    <li class="nav-item"><a class="nav-link" href="/#about" @click="isMenuOpen = false">Về chúng tôi</a></li>
                    <li class="nav-item"><a class="nav-link" href="#contact" @click="isMenuOpen = false">Liên hệ</a></li>
                </ul>
                <div class="d-flex align-items-center gap-3 mt-3 mt-lg-0">
                    <template v-if="authStore.isAuthenticated">
                        <div class="dropdown">
                            <a href="#" class="d-flex align-items-center text-dark text-decoration-none dropdown-toggle" data-bs-toggle="dropdown">
                                <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center me-2 shadow-sm" style="width: 38px; height: 38px; font-weight: bold;">
                                    {{ authStore.user?.name?.charAt(0)?.toUpperCase() || 'U' }}
                                </div>
                                <span class="fw-semibold">{{ authStore.user?.name || 'User' }}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end border-0 shadow mt-2 rounded-3 overflow-hidden">
                                <li><router-link class="dropdown-item py-2" to="/profile"><i class="material-symbols-rounded align-middle me-2 text-primary">person</i> Hồ sơ cá nhân</router-link></li>
                                <li v-if="authStore.isAdmin"><router-link class="dropdown-item py-2" to="/admin/dashboard"><i class="material-symbols-rounded align-middle me-2 text-warning">admin_panel_settings</i> Quản trị</router-link></li>
                                <li v-else-if="authStore.isFarm"><router-link class="dropdown-item py-2" to="/farm"><i class="material-symbols-rounded align-middle me-2 text-success">agriculture</i> Quản lý Nông trại</router-link></li>
                                <li v-else-if="authStore.isRetailer"><router-link class="dropdown-item py-2" to="/retailer"><i class="material-symbols-rounded align-middle me-2 text-info">storefront</i> Quản lý Cửa hàng</router-link></li>
                                <li><hr class="dropdown-divider my-0"></li>
                                <li><a class="dropdown-item py-2 text-danger" href="#" @click.prevent="logout"><i class="material-symbols-rounded align-middle me-2">logout</i> Đăng xuất</a></li>
                            </ul>
                        </div>
                    </template>
                    <template v-else>
                        <router-link to="/login" class="text-dark fw-bold text-decoration-none me-2">Đăng nhập</router-link>
                        <router-link to="/register" class="btn btn-bicap shadow-sm">Đăng ký ngay</router-link>
                    </template>
                </div>
            </div>
        </div>
    </nav>

    <main class="w-full">
      <router-view />
    </main>

    <footer class="bg-dark text-white pt-5 pb-3" id="contact">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="d-flex align-items-center mb-3">
                        <span class="material-symbols-rounded text-success fs-2 me-2">eco</span>
                        <h4 class="fw-bold mb-0">BiCap Farm</h4>
                    </div>
                    <p class="text-white-50 small">Nền tảng nông nghiệp sạch ứng dụng Blockchain để truy xuất nguồn gốc minh bạch, kết nối nông dân với người tiêu dùng.</p>
                </div>
                <div class="col-lg-2 col-6">
                    <h6 class="fw-bold text-uppercase mb-3">Liên kết</h6>
                    <ul class="list-unstyled">
                        <li class="mb-2"><router-link to="/" class="text-white-50 text-decoration-none hover-white">Trang chủ</router-link></li>
                        <li class="mb-2"><router-link to="/market" class="text-white-50 text-decoration-none hover-white">Sàn giao dịch</router-link></li>
                        <li class="mb-2"><router-link to="/trace" class="text-white-50 text-decoration-none hover-white">Truy xuất</router-link></li>
                    </ul>
                </div>
                <div class="col-lg-2 col-6">
                    <h6 class="fw-bold text-uppercase mb-3">Đối tác</h6>
                    <ul class="list-unstyled">
                        <li class="mb-2"><router-link to="/register" class="text-white-50 text-decoration-none hover-white">Đăng ký bán hàng</router-link></li>
                        <li class="mb-2"><router-link to="/register" class="text-white-50 text-decoration-none hover-white">Đăng ký vận chuyển</router-link></li>
                    </ul>
                </div>
                <div class="col-lg-4">
                    <h6 class="fw-bold text-uppercase mb-3">Liên hệ</h6>
                    <ul class="list-unstyled text-white-50 small">
                        <li class="mb-2 d-flex align-items-center"><i class="material-symbols-rounded me-2 fs-6">location_on</i> 123 Nguyễn Văn Linh, Q.7, TP.HCM</li>
                        <li class="mb-2 d-flex align-items-center"><i class="material-symbols-rounded me-2 fs-6">call</i> 1900 1234 56</li>
                        <li class="mb-2 d-flex align-items-center"><i class="material-symbols-rounded me-2 fs-6">mail</i> support@bicap.vn</li>
                    </ul>
                </div>
            </div>
            <hr class="border-secondary my-4">
            <div class="text-center text-white-50 small">
                &copy; 2026 BiCap System. All rights reserved.
            </div>
        </div>
    </footer>

    <div class="position-fixed bottom-0 end-0 p-4 z-3">
        <router-link to="/trace" class="btn btn-success rounded-circle shadow-lg d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;" title="Quét QR">
            <span class="material-symbols-rounded fs-2">qr_code_scanner</span>
        </router-link>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

const authStore = useAuthStore();
const router = useRouter();
const isMenuOpen = ref(false);

const logout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style>
.guest-nav-menu {
    display: flex !important;
}

@media (max-width: 991.98px) {
    .guest-nav-menu {
        display: none !important;
        padding: 12px 0 16px;
    }

    .guest-nav-menu.is-open {
        display: block !important;
    }

    .guest-nav-menu .navbar-nav {
        margin: 0 !important;
    }

    .guest-nav-menu .nav-link {
        padding: 10px 0;
    }
}

.hover-white:hover { color: white !important; }
</style>
