<script setup>
import { onMounted, ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const cart = useCartStore()
const router = useRouter()
const notice = ref('')
const showCheckoutModal = ref(false)
const checkoutStep = ref(1)
const paymentMethod = ref('bank')
const shippingAddress = ref('')
const isProcessing = ref(false)

const BANK_INFO = {
  bankName: 'MB Bank',
  bankShortName: 'MBB',
  accountNumber: '0912920836',
  accountName: 'TRAN QUOC VIET',
  bin: '970436'
}

const imageUrl = (path) => path || ''
const transferContent = computed(() => `BICAP${Date.now()}`)
const qrUrl = computed(() => {
  const amount = cart.totalAmount || 0
  return `https://img.vietqr.io/image/${BANK_INFO.bin}-${BANK_INFO.accountNumber}-compact2.png?amount=${amount}&addInfo=${encodeURIComponent(transferContent.value)}&accountName=${encodeURIComponent(BANK_INFO.accountName)}`
})

const openCheckout = () => {
  if (cart.isEmpty) { notice.value = 'Gio hang trong!'; return }
  notice.value = ''
  checkoutStep.value = 1
  shippingAddress.value = ''
  paymentMethod.value = 'bank'
  showCheckoutModal.value = true
}
const closeModal = () => { if (isProcessing.value) return; showCheckoutModal.value = false }
const goToStep2 = () => { checkoutStep.value = 2 }
const goToStep3 = () => {
  if (!shippingAddress.value.trim()) { alert('Vui long nhap dia chi giao hang'); return }
  checkoutStep.value = 3
}
const confirmPayment = async () => {
  isProcessing.value = true
  try {
    await cart.checkout()
    showCheckoutModal.value = false
    await router.push('/retailer/orders')
  } catch (error) {
    notice.value = error.response?.data?.message || 'Khong the dat hang. Vui long thu lai.'
    showCheckoutModal.value = false
  } finally {
    isProcessing.value = false
  }
}
const copyToClipboard = (text, label) => {
  navigator.clipboard.writeText(text).then(() => alert(`Da sao chep ${label}!`))
}

onMounted(() => cart.load())
</script>

<template>
  <div class="content-inner cart-page">

    <div v-if="notice" class="notice-error">{{ notice }}</div>

    <div v-if="cart.isLoading" class="empty-state">
      <div class="spinner"></div>
      <p>Dang tai gio hang...</p>
    </div>

    <div v-else-if="cart.isEmpty" class="empty-state">
      <div class="empty-icon">🛒</div>
      <h3>Gio hang trong</h3>
      <p>Ban chua co san pham nao trong gio hang</p>
      <RouterLink to="/retailer" class="btn-shop">Tiep tuc mua sam</RouterLink>
    </div>

    <div v-else>
      <div class="cart-header-bar">
        <h2 class="cart-title">🛒 Gio hang cua ban</h2>
        <span class="item-count">{{ cart.items.length }} san pham</span>
      </div>

      <div class="cart-list">
        <div v-for="item in cart.items" :key="item.cartItemId" class="cart-item">
          <div class="cart-image">
            <img v-if="item.thumbnail" :src="imageUrl(item.thumbnail)" :alt="item.productName">
            <div v-else class="cart-image-empty">🥬</div>
          </div>
          <div class="cart-info">
            <h4 class="product-name">{{ item.productName }}</h4>
            <p class="batch-info">{{ item.batchCode }} &middot; Hang {{ item.grade }}</p>
            <p class="unit-price">{{ (item.unitPrice || 0).toLocaleString('vi-VN') }} VND / {{ item.unit || 'kg' }}</p>
          </div>
          <div class="cart-qty">
            <button class="qty-btn" @click="cart.updateQuantity(item.cartItemId, item.quantity - 1)" :disabled="item.quantity <= 1">−</button>
            <span class="qty-value">{{ item.quantity }}</span>
            <button class="qty-btn" @click="cart.updateQuantity(item.cartItemId, item.quantity + 1)">+</button>
          </div>
          <div class="cart-subtotal">
            <span class="subtotal-value">{{ (item.subTotal || 0).toLocaleString('vi-VN') }}</span>
            <span class="subtotal-unit">VND</span>
          </div>
          <button class="remove-btn" @click="cart.removeItem(item.cartItemId)" title="Xoa san pham">✕</button>
        </div>
      </div>

      <div class="cart-footer">
        <div class="cart-total-row">
          <span class="total-label">Tong thanh toan:</span>
          <span class="total-amount">{{ cart.totalAmount.toLocaleString('vi-VN') }} <small>VND</small></span>
        </div>
        <button type="button" class="btn-checkout" @click="openCheckout">
          💳 Tien hanh thanh toan
        </button>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showCheckoutModal" class="co-overlay" @click.self="closeModal">
        <div class="co-modal">

          <div class="step-bar">
            <div class="step" :class="{ active: checkoutStep >= 1, done: checkoutStep > 1 }">
              <div class="step-dot">{{ checkoutStep > 1 ? '✓' : '1' }}</div>
              <span>Xac nhan</span>
            </div>
            <div class="step-line" :class="{ active: checkoutStep > 1 }"></div>
            <div class="step" :class="{ active: checkoutStep >= 2, done: checkoutStep > 2 }">
              <div class="step-dot">{{ checkoutStep > 2 ? '✓' : '2' }}</div>
              <span>Thong tin</span>
            </div>
            <div class="step-line" :class="{ active: checkoutStep > 2 }"></div>
            <div class="step" :class="{ active: checkoutStep >= 3 }">
              <div class="step-dot">3</div>
              <span>Thanh toan</span>
            </div>
          </div>

          <!-- STEP 1 -->
          <div v-if="checkoutStep === 1" class="co-body">
            <h3 class="co-title">📋 Xac nhan don hang</h3>
            <div class="review-list">
              <div v-for="item in cart.items" :key="item.cartItemId" class="review-item">
                <div class="ri-image">
                  <img v-if="item.thumbnail" :src="item.thumbnail" :alt="item.productName">
                  <span v-else class="ri-emoji">🥬</span>
                </div>
                <div class="ri-info">
                  <p class="ri-name">{{ item.productName }}</p>
                  <p class="ri-meta">{{ item.batchCode }} · Hang {{ item.grade }}</p>
                  <p class="ri-price">{{ (item.unitPrice || 0).toLocaleString('vi-VN') }} VND × {{ item.quantity }} {{ item.unit || 'kg' }}</p>
                </div>
                <div class="ri-total">{{ (item.subTotal || 0).toLocaleString('vi-VN') }}<br><small>VND</small></div>
              </div>
            </div>
            <div class="co-divider"></div>
            <div class="co-total-row">
              <span>Tong cong</span>
              <strong class="co-total-val">{{ cart.totalAmount.toLocaleString('vi-VN') }} VND</strong>
            </div>
            <div class="co-actions">
              <button class="btn-back" @click="showCheckoutModal = false">← Quay lai</button>
              <button class="btn-next" @click="goToStep2">Tiep theo →</button>
            </div>
          </div>

          <!-- STEP 2 -->
          <div v-else-if="checkoutStep === 2" class="co-body">
            <h3 class="co-title">📍 Thong tin giao hang</h3>
            <div class="form-group">
              <label class="form-label">Dia chi giao hang <span class="required">*</span></label>
              <textarea v-model="shippingAddress" class="form-textarea" placeholder="So nha, duong, phuong/xa, quan/huyen, tinh/thanh pho..." rows="4" id="shippingAddressInput"></textarea>
            </div>
            <div class="form-group">
              <label class="form-label">Phuong thuc thanh toan</label>
              <div class="payment-options">
                <label class="pay-option" :class="{ selected: paymentMethod === 'bank' }">
                  <input type="radio" v-model="paymentMethod" value="bank" hidden>
                  <span class="pay-icon">🏦</span>
                  <span class="pay-name">Chuyen khoan ngan hang (QR)</span>
                  <span v-if="paymentMethod === 'bank'" class="pay-check">✓</span>
                </label>
                <label class="pay-option" :class="{ selected: paymentMethod === 'cod' }">
                  <input type="radio" v-model="paymentMethod" value="cod" hidden>
                  <span class="pay-icon">💵</span>
                  <span class="pay-name">Tien mat khi nhan hang</span>
                  <span v-if="paymentMethod === 'cod'" class="pay-check">✓</span>
                </label>
              </div>
            </div>
            <div class="co-divider"></div>
            <div class="co-total-row">
              <span>Tong thanh toan</span>
              <strong class="co-total-val">{{ cart.totalAmount.toLocaleString('vi-VN') }} VND</strong>
            </div>
            <div class="co-actions">
              <button class="btn-back" @click="checkoutStep = 1">← Quay lai</button>
              <button class="btn-next" @click="goToStep3">{{ paymentMethod === 'bank' ? 'Xem QR thanh toan →' : 'Xac nhan dat hang →' }}</button>
            </div>
          </div>

          <!-- STEP 3 -->
          <div v-else-if="checkoutStep === 3" class="co-body">
            <h3 class="co-title">💳 Thanh toan</h3>

            <div v-if="paymentMethod === 'cod'" class="cod-confirm">
              <div class="cod-icon">💵</div>
              <h4>Thanh toan khi nhan hang</h4>
              <p>Ban se thanh toan <strong>{{ cart.totalAmount.toLocaleString('vi-VN') }} VND</strong> khi nhan hang.</p>
            </div>

            <div v-else class="qr-section">
              <div class="bank-card">
                <div class="bank-header">
                  <div class="bank-logo">🏦</div>
                  <div>
                    <div class="bank-name">{{ BANK_INFO.bankName }}</div>
                    <div class="bank-short">{{ BANK_INFO.bankShortName }}</div>
                  </div>
                  <div class="vietqr-badge">VietQR</div>
                </div>
                <div class="bank-details">
                  <div class="bank-row">
                    <span class="bd-label">Ten tai khoan</span>
                    <span class="bd-val">{{ BANK_INFO.accountName }}</span>
                  </div>
                  <div class="bank-row">
                    <span class="bd-label">So tai khoan</span>
                    <span class="bd-val acc-num">
                      {{ BANK_INFO.accountNumber }}
                      <button class="copy-btn" @click="copyToClipboard(BANK_INFO.accountNumber, 'so tai khoan')" title="Sao chep">📋</button>
                    </span>
                  </div>
                  <div class="bank-row">
                    <span class="bd-label">So tien</span>
                    <span class="bd-val amount-val">
                      {{ cart.totalAmount.toLocaleString('vi-VN') }} VND
                      <button class="copy-btn" @click="copyToClipboard(String(cart.totalAmount), 'so tien')" title="Sao chep">📋</button>
                    </span>
                  </div>
                  <div class="bank-row">
                    <span class="bd-label">Noi dung CK</span>
                    <span class="bd-val transfer-content">
                      {{ transferContent }}
                      <button class="copy-btn" @click="copyToClipboard(transferContent, 'noi dung')" title="Sao chep">📋</button>
                    </span>
                  </div>
                </div>
                <div class="qr-container">
                  <img :src="qrUrl" alt="QR Code VietQR" class="qr-img" onerror="this.src='https://api.qrserver.com/v1/create-qr-code/?data=BICAP-THANH-TOAN&size=200x200&color=2f855a'">
                  <p class="qr-hint">📱 Quet bang ung dung ngan hang bat ky</p>
                  <p class="qr-note">Ho tro: Vietcombank, BIDV, Techcombank, MB Bank, VPBank...</p>
                </div>
              </div>
              <div class="payment-note">
                ⚠️ Vui long chuyen khoan dung so tien va noi dung. Don hang se duoc xac nhan sau khi nhan duoc tien.
              </div>
            </div>

            <div class="co-actions">
              <button class="btn-back" @click="checkoutStep = 2" :disabled="isProcessing">← Quay lai</button>
              <button class="btn-confirm" @click="confirmPayment" :disabled="isProcessing">
                <span v-if="isProcessing">⏳ Dang xu ly...</span>
                <span v-else>✅ Xac nhan da thanh toan</span>
              </button>
            </div>
          </div>

          <button class="co-close" @click="closeModal" :disabled="isProcessing">✕</button>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<style scoped>
.cart-page { padding: 0; }
.notice-error {
  background: #fff0f0; border-left: 4px solid #e53e3e;
  color: #c53030; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; font-size: 0.9rem;
}
.empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 60px 20px; text-align: center; color: #718096;
}
.empty-icon { font-size: 4rem; margin-bottom: 16px; }
.empty-state h3 { font-size: 1.25rem; color: #2d3748; margin-bottom: 8px; }
.spinner {
  width: 36px; height: 36px; border: 3px solid #e2e8f0;
  border-top-color: var(--primary, #2f855a); border-radius: 50%;
  animation: spin 0.8s linear infinite; margin-bottom: 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.btn-shop {
  display: inline-block; margin-top: 16px; padding: 10px 24px;
  background: var(--primary, #2f855a); color: #fff; border-radius: 8px;
  text-decoration: none; font-weight: 600; transition: opacity 0.2s;
}
.btn-shop:hover { opacity: 0.85; }
.cart-header-bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.cart-title { font-size: 1.25rem; font-weight: 700; color: #1a202c; }
.item-count { background: #e6fffa; color: #2c7a7b; font-size: 0.8rem; font-weight: 600; padding: 4px 12px; border-radius: 20px; }
.cart-list { display: flex; flex-direction: column; gap: 12px; margin-bottom: 16px; }
.cart-item {
  display: flex; align-items: center; gap: 14px; background: #fff;
  border-radius: 12px; padding: 14px; box-shadow: 0 1px 4px rgba(0,0,0,0.07); transition: box-shadow 0.2s;
}
.cart-item:hover { box-shadow: 0 3px 12px rgba(0,0,0,0.1); }
.cart-image {
  width: 72px; height: 72px; flex-shrink: 0; border-radius: 10px; overflow: hidden;
  background: #f0fff4; display: flex; align-items: center; justify-content: center;
}
.cart-image img { width: 100%; height: 100%; object-fit: cover; }
.cart-image-empty { font-size: 2rem; }
.cart-info { flex: 1; min-width: 0; }
.product-name { font-weight: 700; color: #1a202c; font-size: 0.95rem; margin: 0 0 3px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.batch-info { font-size: 0.75rem; color: #718096; margin: 0 0 3px; }
.unit-price { font-size: 0.8rem; color: #48bb78; font-weight: 600; margin: 0; }
.cart-qty { display: flex; align-items: center; gap: 8px; background: #f7fafc; border-radius: 8px; padding: 4px 8px; }
.qty-btn {
  width: 28px; height: 28px; border: 1px solid #e2e8f0; border-radius: 6px;
  background: #fff; color: #2d3748; font-size: 1rem; cursor: pointer;
  display: flex; align-items: center; justify-content: center; transition: all 0.15s;
}
.qty-btn:hover:not(:disabled) { background: #2f855a; color: #fff; border-color: #2f855a; }
.qty-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.qty-value { font-weight: 700; min-width: 24px; text-align: center; font-size: 0.95rem; }
.cart-subtotal { text-align: right; min-width: 90px; }
.subtotal-value { font-weight: 700; font-size: 1rem; color: #2f855a; display: block; }
.subtotal-unit { font-size: 0.7rem; color: #a0aec0; }
.remove-btn {
  width: 32px; height: 32px; border: none; border-radius: 8px;
  background: #fff5f5; color: #e53e3e; cursor: pointer; font-size: 0.85rem;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.15s; flex-shrink: 0;
}
.remove-btn:hover { background: #e53e3e; color: #fff; }
.cart-footer {
  background: #fff; border-radius: 12px; padding: 16px 20px;
  box-shadow: 0 -2px 12px rgba(0,0,0,0.07);
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
}
.cart-total-row { display: flex; flex-direction: column; }
.total-label { font-size: 0.8rem; color: #718096; }
.total-amount { font-size: 1.4rem; font-weight: 800; color: #2f855a; }
.total-amount small { font-size: 0.7rem; font-weight: 400; }
.btn-checkout {
  padding: 12px 28px; background: linear-gradient(135deg, #38a169, #2f855a); color: #fff;
  border: none; border-radius: 10px; font-weight: 700; font-size: 0.95rem;
  cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 12px rgba(47,133,90,0.35); white-space: nowrap;
}
.btn-checkout:hover { transform: translateY(-1px); box-shadow: 0 6px 18px rgba(47,133,90,0.45); }
.co-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.55);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999; padding: 16px; animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.co-modal {
  background: #fff; border-radius: 20px; width: 100%; max-width: 520px;
  max-height: 90vh; overflow-y: auto; position: relative;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: slideUp 0.25s ease;
}
@keyframes slideUp { from { transform: translateY(30px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
.co-close {
  position: absolute; top: 14px; right: 14px; width: 32px; height: 32px;
  border: none; border-radius: 50%; background: #f7fafc; color: #718096;
  cursor: pointer; font-size: 0.85rem; display: flex; align-items: center; justify-content: center;
  transition: all 0.15s; z-index: 1;
}
.co-close:hover { background: #e53e3e; color: #fff; }
.step-bar { display: flex; align-items: center; justify-content: center; padding: 20px 24px 0; }
.step { display: flex; flex-direction: column; align-items: center; gap: 4px; min-width: 70px; }
.step-dot {
  width: 32px; height: 32px; border-radius: 50%;
  border: 2px solid #e2e8f0; background: #f7fafc;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8rem; font-weight: 700; color: #a0aec0; transition: all 0.3s;
}
.step.active .step-dot { background: var(--primary, #2f855a); border-color: var(--primary, #2f855a); color: #fff; box-shadow: 0 2px 8px rgba(47,133,90,0.4); }
.step.done .step-dot { background: #48bb78; border-color: #48bb78; color: #fff; }
.step > span { font-size: 0.72rem; color: #a0aec0; font-weight: 500; }
.step.active > span, .step.done > span { color: #2f855a; font-weight: 700; }
.step-line { flex: 1; height: 2px; background: #e2e8f0; transition: background 0.3s; margin-bottom: 18px; }
.step-line.active { background: #48bb78; }
.co-body { padding: 20px 24px 24px; }
.co-title { font-size: 1.1rem; font-weight: 700; color: #1a202c; margin: 0 0 18px; }
.review-list { display: flex; flex-direction: column; gap: 10px; }
.review-item { display: flex; gap: 12px; align-items: flex-start; padding: 12px; background: #f7fafc; border-radius: 10px; }
.ri-image { width: 52px; height: 52px; border-radius: 8px; overflow: hidden; background: #e6fffa; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.ri-image img { width: 100%; height: 100%; object-fit: cover; }
.ri-emoji { font-size: 1.5rem; }
.ri-info { flex: 1; min-width: 0; }
.ri-name { font-weight: 700; font-size: 0.9rem; color: #1a202c; margin: 0 0 2px; }
.ri-meta { font-size: 0.75rem; color: #a0aec0; margin: 0 0 3px; }
.ri-price { font-size: 0.8rem; color: #718096; margin: 0; }
.ri-total { text-align: right; font-weight: 800; font-size: 0.95rem; color: #2f855a; white-space: nowrap; }
.ri-total small { display: block; font-size: 0.65rem; color: #a0aec0; font-weight: 400; }
.co-divider { height: 1px; background: #e2e8f0; margin: 16px 0; }
.co-total-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.co-total-row > span { color: #4a5568; font-weight: 600; }
.co-total-val { font-size: 1.2rem; font-weight: 800; color: #2f855a; }
.form-group { margin-bottom: 18px; }
.form-label { display: block; font-weight: 600; color: #4a5568; font-size: 0.875rem; margin-bottom: 8px; }
.required { color: #e53e3e; }
.form-textarea { width: 100%; padding: 10px 14px; border: 1.5px solid #e2e8f0; border-radius: 10px; font-size: 0.9rem; resize: vertical; font-family: inherit; transition: border-color 0.2s; box-sizing: border-box; }
.form-textarea:focus { outline: none; border-color: #48bb78; }
.payment-options { display: flex; flex-direction: column; gap: 10px; }
.pay-option { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border: 1.5px solid #e2e8f0; border-radius: 10px; cursor: pointer; transition: all 0.2s; background: #f7fafc; }
.pay-option.selected { border-color: #48bb78; background: #f0fff4; }
.pay-icon { font-size: 1.3rem; }
.pay-name { flex: 1; font-weight: 600; font-size: 0.875rem; color: #2d3748; }
.pay-check { color: #48bb78; font-weight: 800; font-size: 1.1rem; }
.co-actions { display: flex; gap: 10px; margin-top: 20px; }
.btn-back { padding: 11px 20px; border: 1.5px solid #e2e8f0; border-radius: 10px; background: #fff; color: #4a5568; font-weight: 600; cursor: pointer; font-size: 0.875rem; transition: all 0.15s; }
.btn-back:hover:not(:disabled) { border-color: #a0aec0; background: #f7fafc; }
.btn-next { flex: 1; padding: 11px 20px; background: linear-gradient(135deg, #38a169, #2f855a); color: #fff; border: none; border-radius: 10px; font-weight: 700; cursor: pointer; font-size: 0.875rem; transition: all 0.2s; }
.btn-next:hover { opacity: 0.9; transform: translateY(-1px); }
.btn-confirm { flex: 1; padding: 12px 20px; background: linear-gradient(135deg, #3182ce, #2b6cb0); color: #fff; border: none; border-radius: 10px; font-weight: 700; cursor: pointer; font-size: 0.875rem; transition: all 0.2s; box-shadow: 0 4px 12px rgba(49,130,206,0.3); }
.btn-confirm:hover:not(:disabled) { opacity: 0.9; transform: translateY(-1px); }
.btn-confirm:disabled, .btn-back:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }
.cod-confirm { text-align: center; padding: 30px 20px; background: #f0fff4; border-radius: 12px; margin-bottom: 8px; }
.cod-icon { font-size: 3rem; margin-bottom: 12px; }
.cod-confirm h4 { font-size: 1.1rem; font-weight: 700; margin-bottom: 8px; color: #1a202c; }
.cod-confirm p { color: #4a5568; font-size: 0.9rem; }
.bank-card { border: 1.5px solid #e2e8f0; border-radius: 16px; overflow: hidden; background: linear-gradient(160deg, #f0fff4 0%, #fff 60%); margin-bottom: 12px; }
.bank-header { display: flex; align-items: center; gap: 12px; padding: 14px 18px; background: linear-gradient(135deg, #1a5276, #1f618d); color: #fff; }
.bank-logo { font-size: 1.8rem; }
.bank-name { font-weight: 700; font-size: 1rem; }
.bank-short { font-size: 0.75rem; opacity: 0.8; }
.vietqr-badge { margin-left: auto; background: #ff6b35; color: #fff; font-size: 0.7rem; font-weight: 800; padding: 3px 8px; border-radius: 6px; }
.bank-details { padding: 14px 18px; display: flex; flex-direction: column; gap: 10px; }
.bank-row { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
.bd-label { font-size: 0.78rem; color: #718096; flex-shrink: 0; }
.bd-val { font-weight: 600; font-size: 0.85rem; color: #1a202c; display: flex; align-items: center; gap: 4px; text-align: right; }
.acc-num { font-family: monospace; font-size: 1rem; color: #2f855a; }
.amount-val { color: #e53e3e; font-weight: 800; font-size: 1rem; }
.transfer-content { font-family: monospace; font-size: 0.8rem; color: #553c9a; }
.copy-btn { border: none; background: none; cursor: pointer; font-size: 0.9rem; padding: 2px; transition: transform 0.15s; line-height: 1; }
.copy-btn:hover { transform: scale(1.2); }
.qr-container { text-align: center; padding: 16px 18px; border-top: 1px dashed #c6f6d5; background: #f0fff4; }
.qr-img { width: 180px; height: 180px; object-fit: contain; border-radius: 12px; border: 2px solid #c6f6d5; }
.qr-hint { font-size: 0.82rem; color: #2f855a; font-weight: 600; margin: 10px 0 2px; }
.qr-note { font-size: 0.72rem; color: #718096; margin: 0; }
.payment-note { background: #fffbeb; border: 1px solid #f6e05e; border-radius: 10px; padding: 10px 14px; font-size: 0.8rem; color: #744210; line-height: 1.5; }
</style>
