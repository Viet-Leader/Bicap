
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import jsQR from 'jsqr'

const router = useRouter()
const batchId = ref('')
const fileInput = ref(null)
const scanError = ref('')
const scanningImage = ref(false)

const searchBatch = () => {
  if (!batchId.value.trim()) return
  router.push(`/trace/${batchId.value.trim()}`)
}

const openImagePicker = () => {
    scanError.value = ''
    fileInput.value?.click()
}

const extractTraceId = (value) => {
    const decoded = value.trim()
    try {
        const url = new URL(decoded)
        const traceMatch = url.pathname.match(/\/trace\/([^/]+)/i)
        if (traceMatch) return decodeURIComponent(traceMatch[1])
    } catch {
        // QR may contain a plain batch id instead of a URL.
    }
    return decoded
}

const scanImage = async (event) => {
    const file = event.target.files?.[0]
    event.target.value = ''
    if (!file) return

    scanError.value = ''
    scanningImage.value = true
    try {
        const bitmap = await createImageBitmap(file)
        const canvas = document.createElement('canvas')
        canvas.width = bitmap.width
        canvas.height = bitmap.height
        const context = canvas.getContext('2d', { willReadFrequently: true })
        context.drawImage(bitmap, 0, 0)
        const image = context.getImageData(0, 0, canvas.width, canvas.height)
        const result = jsQR(image.data, image.width, image.height)
        bitmap.close()

        if (!result?.data) {
            throw new Error('Không tìm thấy mã QR trong ảnh. Hãy chọn ảnh rõ hơn.')
        }

        const traceId = extractTraceId(result.data)
        if (!traceId) throw new Error('Mã QR không chứa mã truy xuất hợp lệ.')
        batchId.value = traceId
        router.push(`/trace/${encodeURIComponent(traceId)}`)
    } catch (error) {
        scanError.value = error.message || 'Không thể đọc ảnh QR.'
    } finally {
        scanningImage.value = false
    }
}
</script>

<template>
  <div class="container py-5 mt-lg-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <!-- Header Section -->
            <div class="text-center mb-4">
                <div class="hero-icon mx-auto mb-3" style="font-size: 2rem;">
                    <span>🔍</span>
                </div>
                <h2 class="font-weight-bold">Truy Xuất Nguồn Gốc</h2>
                <p class="text-muted">Nhập mã lô hàng hoặc quét mã QR để xem hành trình sản phẩm</p>
            </div>

            <!-- Search Card -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body p-4">
                    <form @submit.prevent="searchBatch">
                        <div class="mb-3">
                            <label class="form-label font-weight-bold">Mã Lô Hàng / Batch ID</label>
                            <input type="text" v-model="batchId" class="form-control form-control-lg bg-light border-0" placeholder="VD: BATCH-001" required>
                        </div>
                        <button type="submit" class="btn btn-bicap btn-lg w-100">
                            🔍 Tra cứu thông tin
                        </button>
                    </form>

                    <div class="divider">
                        <span class="divider-text">HOẶC</span>
                    </div>

                    <div class="text-center">
                        <input
                            ref="fileInput"
                            class="d-none"
                            type="file"
                            accept="image/png,image/jpeg,image/webp,image/gif"
                            @change="scanImage"
                        >
                        <button id="start-scan-btn" type="button" class="btn btn-outline-success" :disabled="scanningImage" @click="openImagePicker">
                            <span v-if="scanningImage" class="spinner-border spinner-border-sm me-1"></span>
                            {{ scanningImage ? 'Đang đọc ảnh...' : '📷 Quét mã QR' }}
                        </button>
                        <p v-if="scanError" class="text-danger small mt-2 mb-0">{{ scanError }}</p>
                    </div>
                </div>
            </div>

            <!-- Suggestion Card -->
            <div class="card border-0 bg-gray-100">
                <div class="card-header bg-transparent border-0 pt-3 pb-0">
                    <h6 class="font-weight-bold text-muted mb-0">Mã mẫu để thử nghiệm:</h6>
                </div>
                <div class="card-body">
                    <div class="row g-2">
                        <div class="col-6 col-md-4">
                            <router-link to="/trace/BATCH-001" class="btn btn-outline-success btn-sm w-100">BATCH-001</router-link>
                        </div>
                        <div class="col-6 col-md-4">
                            <router-link to="/trace/LOT-2024-001" class="btn btn-outline-success btn-sm w-100">LOT-2024-001</router-link>
                        </div>
                        <div class="col-6 col-md-4">
                            <router-link to="/trace/FARM-VN-001" class="btn btn-outline-success btn-sm w-100">FARM-VN-001</router-link>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Info Cards -->
            <div class="row mt-4">
                <div class="col-md-4 mb-3">
                    <div class="text-center p-3">
                        <div class="hero-icon mx-auto mb-2" style="width: 50px; height: 50px; font-size: 1.5rem;">
                            <span>✅</span>
                        </div>
                        <h6 class="font-weight-bold mb-1">Xác Thực</h6>
                        <small class="text-muted">Dữ liệu được xác thực trên blockchain</small>
                    </div>
                </div>
                <div class="col-md-4 mb-3">
                    <div class="text-center p-3">
                        <div class="hero-icon mx-auto mb-2" style="width: 50px; height: 50px; font-size: 1.5rem;">
                            <span>🔒</span>
                        </div>
                        <h6 class="font-weight-bold mb-1">Bảo Mật</h6>
                        <small class="text-muted">Không thể chỉnh sửa hoặc giả mạo</small>
                    </div>
                </div>
                <div class="col-md-4 mb-3">
                    <div class="text-center p-3">
                        <div class="hero-icon mx-auto mb-2" style="width: 50px; height: 50px; font-size: 1.5rem;">
                            <span>📍</span>
                        </div>
                        <h6 class="font-weight-bold mb-1">Minh Bạch</h6>
                        <small class="text-muted">Theo dõi từ nông trại đến bàn ăn</small>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.hero-icon {
    width: 60px; height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
    display: flex; align-items: center; justify-content: center;
}
.qr-scanner {
    background: #f8f9fa;
    border-radius: 1rem;
    position: relative;
    overflow: hidden;
}
.qr-overlay {
    position: absolute;
    top: 20px; left: 20px; right: 20px; bottom: 20px;
    pointer-events: none;
}
.qr-corner {
    position: absolute;
    width: 30px; height: 30px;
    border-color: #4CAF50;
    border-style: solid;
    border-width: 0;
}
.qr-corner.top-left { top: 0; left: 0; border-top-width: 4px; border-left-width: 4px; border-top-left-radius: 8px; }
.qr-corner.top-right { top: 0; right: 0; border-top-width: 4px; border-right-width: 4px; border-top-right-radius: 8px; }
.qr-corner.bottom-left { bottom: 0; left: 0; border-bottom-width: 4px; border-left-width: 4px; border-bottom-left-radius: 8px; }
.qr-corner.bottom-right { bottom: 0; right: 0; border-bottom-width: 4px; border-right-width: 4px; border-bottom-right-radius: 8px; }
.qr-placeholder { position: absolute; top: 0; left: 0; right: 0; bottom: 0; }
.divider { display: flex; align-items: center; text-align: center; margin: 1.5rem 0; }
.divider::before, .divider::after { content: ''; flex: 1; border-bottom: 1px solid #dee2e6; }
.divider-text { padding: 0 1rem; color: #6c757d; font-size: 0.875rem; }
</style>
