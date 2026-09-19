# 🎨 BICAP Frontend Design Document

> **Phiên bản:** 1.0
> **Ngày cập nhật:** 2026
> **Đối tượng:** Frontend Developers, UI/UX Designers, QA Engineers
> **Nguồn tham chiếu:** BICAP System Design Document (Chương 2-6) & Frontend Test Plan

---

## 📑 Mục lục

1. [Tổng quan Dự án](#1-tổng-quan-dự-án)
2. [Triết lý Thiết kế](#2-triết-lý-thiết-kế-design-philosophy)
3. [Design System](#3-design-system-ngôn-ngữ-thiết-kế)
4. [Kiến trúc Thư mục](#4-kiến-trúc-thư-mục-folder-structure)
5. [Layout theo Role](#5-layout-chi-tiết-theo-role)
6. [Các Component Đặc thù](#6-các-component-đặc-thù)
7. [Router & Phân quyền](#7-router--phân-quyền)
8. [State Management](#8-state-management-pinia)
9. [API Service Layer](#9-api-service-layer)
10. [Roadmap Triển khai](#10-checklist-triển-khai-implementation-roadmap)
11. [UX Best Practices](#11-điểm-nhấn-ux-quan-trọng)
12. [Cấu hình & Môi trường](#12-cấu-hình--môi-trường)
13. [Mapping Use Cases](#13-mapping-use-cases--frontend-modules)
14. [Testing Strategy](#14-testing-strategy)

---

## 1. Tổng quan Dự án

### 1.1. Mục tiêu

Xây dựng giao diện người dùng hiện đại, trực quan cho hệ thống **BICAP (Blockchain Integration in Clean Agricultural Production)** — nền tảng quản lý chuỗi cung ứng nông sản sạch ứng dụng công nghệ Blockchain.

### 1.2. Tech Stack

| Layer | Công nghệ | Lý do |
|---|---|---|
| **Framework** | Vue 3 (Composition API) | Hiệu suất cao, reactivity tốt, ecosystem mạnh |
| **Build Tool** | Vite | HMR cực nhanh, bundle size nhỏ |
| **State** | Pinia | Official store, TypeScript friendly |
| **Router** | Vue Router 4 | Route guards linh hoạt cho phân quyền |
| **Styling** | SCSS + CSS Variables | Hỗ trợ theming, dễ maintain |
| **HTTP Client** | Axios | Interceptor mạnh mẽ |
| **Testing** | Vitest + Vue Test Utils + Cypress | Unit → Integration → E2E |
| **QR Scanner** | `html5-qrcode` hoặc `vue-qrcode-reader` | Quét mã QR Consumer |
| **Charts** | Chart.js hoặc ApexCharts | Dashboard analytics |
| **Icons** | Lucide Vue hoặc Heroicons | Icon set đồng bộ |

### 1.3. Actors & Phạm vi Giao diện

| Actor | Vai trò | Số màn hình | Đặc điểm UI |
|---|---|---|---|
| **Admin** | Quản trị hệ thống | ~12 | Dashboard nhiều dữ liệu, bảng lớn |
| **Farm** | Quản lý sản xuất | ~10 | Form nhập liệu nhiều, mobile-friendly |
| **Retailer** | Mua hàng | ~7 | Marketplace, product card |
| **Consumer** | Truy xuất nguồn gốc | 1 | Landing page ấn tượng, không đăng nhập |
| **Guest** | Khách | 2 | Trang chủ, đăng nhập |

---

## 2. Triết lý Thiết kế (Design Philosophy)

Hệ thống BICAP là sự giao thoa giữa **Nông nghiệp sạch** (truyền thống, tự nhiên) và **Blockchain** (công nghệ, minh bạch).

### 🌿 Phong cách chủ đạo: **"Agri-Tech Glassmorphism"**

> *Cyberpunk Nông nghiệp* — Kết hợp nền tối sâu thẳm (Blockchain void) với các khối kính mờ phát sáng (Glassmorphism), điểm xuyết màu xanh neon lá (Neon Green) thể hiện sự sống và minh bạch.

### Nguyên tắc Thiết kế

1. **Minh bạch (Transparency)** — Mọi dữ liệu Blockchain hiển thị dạng hash có thể copy/verify.
2. **Tin cậy (Trust)** — Màu xanh neon = verified, màu vàng = pending, đỏ = rejected.
3. **Đơn giản cho Nông dân** — Form to, dễ chạm, hỗ trợ mobile ngoài đồng.
4. **Ấn tượng cho Consumer** — Trang truy xuất phải đẹp như một câu chuyện.

---

## 3. Design System (Ngôn ngữ Thiết kế)

### 3.1. Bảng màu (Color Palette)

```scss
// === BACKGROUND LAYERS ===
$bg-deep:        #0A0E1A;                    // Nền sâu (Blockchain void)
$bg-primary:     #0F1629;                    // Nền chính
$bg-elevated:    #1A2332;                    // Card nâng cao
$bg-glass:       rgba(26, 35, 50, 0.55);     // Glassmorphism base

// === BRAND COLORS ===
$primary-green:  #00FF88;   // Neon Green - Sự sống, thành công, Blockchain verified
$primary-cyan:   #00D9FF;   // Cyan - Công nghệ, thông tin
$accent-amber:   #FFB800;   // Vàng - Cảnh báo, đang xử lý
$accent-red:     #FF3860;   // Đỏ - Lỗi, từ chối

// === GRADIENTS ===
$gradient-hero:  linear-gradient(135deg, #00FF88 0%, #00D9FF 100%);
$gradient-glow:  radial-gradient(circle at 50% 0%, rgba(0,255,136,0.15), transparent 70%);

// === TEXT ===
$text-primary:   #FFFFFF;
$text-secondary: #A0AEC0;
$text-muted:     #4A5568;

// === GLASS EFFECT ===
$glass-border:   1px solid rgba(0, 255, 136, 0.15);
$glass-blur:     backdrop-filter: blur(20px) saturate(180%);
$glass-shadow:   0 8px 32px rgba(0, 255, 136, 0.08);
```

### 3.2. Typography

| Vai trò | Font | Weight | Size |
|---|---|---|---|
| **Heading** | `Space Grotesk` | 700 | 32-48px |
| **Sub-heading** | `Space Grotesk` | 600 | 20-24px |
| **Body** | `Inter` | 400 | 14-16px |
| **Code/Hash** | `JetBrains Mono` | 500 | 12-14px |
| **Label/Tag** | `Inter` | 600 | 11-12px (uppercase, tracking rộng) |

### 3.3. Spacing & Radius

```scss
// Spacing scale (base 4px)
$space-1: 4px;   $space-2: 8px;   $space-3: 12px;
$space-4: 16px;  $space-5: 24px;  $space-6: 32px;
$space-7: 48px;  $space-8: 64px;

// Border radius
$radius-sm: 8px;
$radius-md: 12px;
$radius-lg: 16px;   // Dùng cho GlassCard
$radius-xl: 24px;
$radius-full: 9999px;

// Shadow
$shadow-sm: 0 2px 8px rgba(0,0,0,0.1);
$shadow-md: 0 4px 16px rgba(0,0,0,0.2);
$shadow-glow: 0 8px 32px rgba(0, 255, 136, 0.15);
```

### 3.4. Status Colors (cho Badges & Enums)

| Enum | Giá trị | Màu | Badge Style |
|---|---|---|---|
| **AccountStatus** | ACTIVE | Xanh neon | `bg-green/10` + `text-green` |
| | INACTIVE | Xám | `bg-gray/10` + `text-gray` |
| **FarmStatus** | ACTIVE | Xanh neon | Verified |
| | INACTIVE | Xám | |
| | PENDING | Vàng | Pulse animation |
| **SeasonStatus** | PLANNING | Cyan | |
| | PLANTING | Xanh dương | |
| | HARVESTING | Vàng | |
| | FINISHED | Xanh neon | |
| **BatchStatus** | AVAILABLE | Xanh neon | |
| | SOLD_OUT | Đỏ nhạt | |
| **OrderStatus** | PENDING | Vàng | Pulse |
| | CONFIRMED | Cyan | |
| | COMPLETED | Xanh neon | ✅ |
| | CANCELLED | Đỏ | ❌ |
| **BlockchainStatus** | PENDING | Vàng | |
| | SUCCESS | Xanh neon | ⛓️ |
| | FAILED | Đỏ | |

---

## 4. Kiến trúc Thư mục (Folder Structure)

```
frontend/
├── public/
│   └── favicon-leaf.svg
├── src/
│   ├── assets/
│   │   ├── images/
│   │   │   ├── hero-bg.webp           # Ảnh nền nông trại mờ
│   │   │   └── texture-noise.png      # Noise overlay
│   │   └── styles/
│   │       ├── _variables.scss        # Design tokens
│   │       ├── _glass.scss            # Mixin Glassmorphism
│   │       ├── _animations.scss       # Keyframes
│   │       └── main.scss
│   │
│   ├── components/
│   │   ├── common/                    # Atomic components
│   │   │   ├── AppButton.vue
│   │   │   ├── AppInput.vue
│   │   │   ├── AppModal.vue
│   │   │   ├── AppTable.vue
│   │   │   ├── AppBadge.vue           # Status badges
│   │   │   ├── AppToast.vue
│   │   │   ├── AppPagination.vue
│   │   │   ├── AppSkeleton.vue
│   │   │   ├── GlassCard.vue          # ⭐ Wrapper glass
│   │   │   ├── CopyButton.vue         # Copy hash
│   │   │   └── QRScanner.vue          # ⭐ Quét QR
│   │   │
│   │   ├── layout/
│   │   │   ├── AdminLayout.vue
│   │   │   ├── FarmLayout.vue
│   │   │   ├── RetailerLayout.vue
│   │   │   ├── GuestLayout.vue
│   │   │   ├── AppSidebar.vue
│   │   │   ├── AppHeader.vue
│   │   │   └── NotificationBell.vue
│   │   │
│   │   └── charts/
│   │       ├── StatCard.vue
│   │       ├── LineChart.vue
│   │       └── BlockchainTimeline.vue  # ⭐ Timeline truy xuất
│   │
│   ├── features/                       # Feature-based modules
│   │   ├── auth/
│   │   │   ├── views/LoginView.vue
│   │   │   ├── components/LoginForm.vue
│   │   │   └── stores/authStore.js
│   │   │
│   │   ├── dashboard/
│   │   │   ├── views/AdminDashboard.vue
│   │   │   └── components/OverviewCards.vue
│   │   │
│   │   ├── accounts/                    # UC03
│   │   │   ├── views/AccountList.vue
│   │   │   ├── components/AccountTable.vue
│   │   │   ├── components/AccountFormModal.vue
│   │   │   └── api/accountApi.js
│   │   │
│   │   ├── farms/                       # UC04, UC05
│   │   │   ├── views/FarmApprovalList.vue
│   │   │   ├── views/FarmProfile.vue
│   │   │   └── components/FarmApprovalTable.vue
│   │   │
│   │   ├── crops/                       # UC06
│   │   │   ├── views/CropManagement.vue
│   │   │   └── components/CropFormModal.vue
│   │   │
│   │   ├── products/                    # UC07, UC12, UC13
│   │   │   ├── views/ProductManagement.vue    # Farm
│   │   │   ├── views/ProductSearch.vue        # Retailer
│   │   │   ├── views/ProductBatchDetail.vue   # Retailer
│   │   │   └── components/ProductCard.vue
│   │   │
│   │   ├── seasons/                     # UC08
│   │   │   ├── views/SeasonManagement.vue
│   │   │   └── components/SeasonTimeline.vue
│   │   │
│   │   ├── activities/                  # UC09
│   │   │   ├── views/ActivityLog.vue
│   │   │   └── components/ActivityTimeline.vue
│   │   │
│   │   ├── batches/                     # UC10, UC11
│   │   │   ├── views/BatchManagement.vue
│   │   │   ├── views/BatchImageGallery.vue
│   │   │   └── components/BatchQRCard.vue
│   │   │
│   │   ├── cart/                        # Cart
│   │   │   ├── views/CartView.vue
│   │   │   └── stores/cartStore.js
│   │   │
│   │   ├── orders/                      # UC14-UC18
│   │   │   ├── views/OrderHistory.vue
│   │   │   ├── views/OrderDetail.vue
│   │   │   ├── components/OrderStatusStepper.vue
│   │   │   └── stores/orderStore.js
│   │   │
│   │   ├── blockchain/                  # UC19
│   │   │   ├── views/BlockchainExplorer.vue
│   │   │   ├── components/TransactionTable.vue
│   │   │   └── components/TxHashCard.vue
│   │   │
│   │   ├── notifications/               # UC20
│   │   │   ├── views/NotificationCenter.vue
│   │   │   └── stores/notificationStore.js
│   │   │
│   │   └── traceability/                # UC21 ⭐ KEY FEATURE
│   │       ├── views/TraceabilityView.vue
│   │       ├── components/TraceabilityHero.vue
│   │       ├── components/BlockchainProofCard.vue
│   │       └── components/JourneyTimeline.vue
│   │
│   ├── router/
│   │   ├── index.js
│   │   ├── guards.js                    # Route protection theo role
│   │   └── routes/
│   │       ├── admin.routes.js
│   │       ├── farm.routes.js
│   │       ├── retailer.routes.js
│   │       └── public.routes.js
│   │
│   ├── stores/                          # Pinia
│   │   ├── auth.js
│   │   ├── ui.js                        # Theme, sidebar collapse
│   │   └── notification.js
│   │
│   ├── services/
│   │   ├── api.js                       # Axios instance + interceptors
│   │   ├── endpoints.js
│   │   └── socket.js                    # (Optional) Real-time notifications
│   │
│   ├── composables/
│   │   ├── useAuth.js
│   │   ├── usePermission.js             # Check role
│   │   ├── usePagination.js
│   │   ├── useQRScanner.js
│   │   └── useToast.js
│   │
│   ├── utils/
│   │   ├── formatters.js                # Date, currency, hash
│   │   ├── validators.js
│   │   └── constants.js                 # Enums (OrderStatus, BatchStatus...)
│   │
│   ├── App.vue
│   └── main.js
│
├── tests/
│   ├── unit/
│   ├── integration/
│   └── e2e/
│
├── .env.development
├── .env.production
├── vite.config.js
├── vitest.config.js
└── package.json
```

---

## 5. Layout chi tiết theo Role

### 5.1. 🌾 Farm Dashboard — "Người Nông Dân Số"

```
┌──────────────────────────────────────────────────────────────┐
│  [🏠 Logo BICAP]  Trang trại Xoài Cát  [🔔 3] [Avatar ▼]    │  ← Glass Header
├───────────────┬──────────────────────────────────────────────┤
│               │                                              │
│  📊 Tổng quan │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐        │
│  🌱 Mùa vụ    │  │ 5    │ │ 12   │ │ 8    │ │ 3    │        │  ← Stat Cards
│  📝 Nhật ký   │  │Vụ mùa│ │Sản phẩm│ │Lô │ │Đơn │        │
│  📦 Lô hàng   │  └──────┘ └──────┘ └──────┘ └──────┘        │
│  🛒 Đơn hàng  │                                              │
│  🔔 Thông báo │  ┌──────────────────────────────────────┐   │
│               │  │   🌱 Vụ mùa đang canh tác            │   │
│               │  │   ┌─────────────────────────────┐    │   │
│               │  │   │ Vụ Xuân 2026                │    │   │
│               │  │   │ ▓▓▓▓▓▓▓░░░░  65%           │    │   │  ← Progress bar neon
│               │  │   │ 📅 01/01 → 30/05            │    │   │
│               │  │   │ [ Xem chi tiết ]            │    │   │
│               │  │   └─────────────────────────────┘    │   │
│               │  └──────────────────────────────────────┘   │
│               │                                              │
│  [⚙️ Cài đặt] │  ┌──────────────────────────────────────┐   │
│               │  │   ⛓️ Giao dịch Blockchain gần đây     │   │
│               │  │   0x1c44...a82f  ✅ Success           │   │
│               │  │   0x9b21...f4d1  ✅ Success           │   │
│               │  └──────────────────────────────────────┘   │
└───────────────┴──────────────────────────────────────────────┘
```

**Đặc trưng:**
- Sidebar thu gọn được (Collapse toggle)
- Stat Cards có glow khi hover
- Progress bar màu neon xanh thể hiện tiến độ vụ mùa
- Mobile: Sidebar → Hamburger menu, form nhập to dễ chạm

---

### 5.2. 🏪 Retailer Marketplace — "Chợ Nông Sản Số"

```
┌──────────────────────────────────────────────────────────────┐
│  [🔍 Tìm kiếm sản phẩm...]  [🛒 3] [🔔] [Avatar ▼]          │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  Bộ lọc:  [Cây trồng ▼] [Trang trại ▼] [Giá: 0-100k]        │
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐            │
│  │ [Ảnh]      │  │ [Ảnh]      │  │ [Ảnh]      │            │
│  │            │  │            │  │            │            │
│  │ Xoài Cát   │  │ Chuối Tây  │  │ Xoài Keo   │            │  ← Product Cards
│  │ Hòa Lộc    │  │            │  │            │            │     Glassmorphism
│  │            │  │            │  │            │            │
│  │ 35.000đ/kg │  │ 18.000đ/kg │  │ 28.000đ/kg │            │
│  │ ✅ Available│  │ ✅ Available│  │ ⚠️ Còn 50kg │            │
│  │ 🏠 Farm A  │  │ 🏠 Farm B  │  │ 🏠 Farm A  │            │
│  │ [Xem] [🛒] │  │ [Xem] [🛒] │  │ [Xem] [🛒] │            │
│  └────────────┘  └────────────┘  └────────────┘            │
└──────────────────────────────────────────────────────────────┘
```

**Đặc trưng:**
- Product Card có ảnh zoom nhẹ khi hover
- Badge trạng thái động (Available/Còn hàng/Hết hàng)
- Nút thêm giỏ hàng có hiệu ứng pulse
- Filter bar sticky khi scroll

---

### 5.3. 👤 Consumer Traceability Page — "Câu Chuyện Sản Phẩm" ⭐

Đây là **trang quan trọng nhất** — Consumer không đăng nhập, chỉ quét QR. Cần gây ấn tượng mạnh.

```
┌──────────────────────────────────────────────────────────────┐
│                    🌿 BICAP TRACEABILITY                     │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │                         │                    │
│              │      [Ảnh Xoài Cát]     │                    │  ← Hero
│              │                         │                    │
│              └─────────────────────────┘                    │
│                                                              │
│           🥭 Xoài Cát Hòa Lộc — Loại A                       │
│           📍 Farm Xanh, Tiền Giang                           │
│                                                              │
│           ⛓️ VERIFIED ON BLOCKCHAIN  ✅                      │  ← Glow badge
│                                                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   HÀNH TRÌNH SẢN PHẨM                                        │
│                                                              │
│   ●─── 15/01/2026 ─── Gieo trồng                            │
│   │   📝 Gieo 500 cây giống Xoài Cát                         │
│   │   ⛓️ 0x1c44...a82f                                       │
│   │                                                          │
│   ●─── 20/02/2026 ─── Bón phân hữu cơ                       │
│   │   📝 Sử dụng phân bò ủ vi sinh                            │
│   │   ⛓️ 0x3d91...b23c                                       │
│   │                                                          │
│   ●─── 15/04/2026 ─── Tưới nước tự động                     │
│   │   📝 Hệ thống tưới nhỏ giọt                              │
│   │                                                          │
│   ●─── 10/05/2026 ─── Thu hoạch                             │
│       📝 Sản lượng: 500kg — Grade A                          │
│       ⛓️ 0x7f22...e91a                                       │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│  📸 Hình ảnh lô hàng  |  📊 Chứng nhận chất lượng           │
│  🏠 Thông tin trang trại  |  ⛓️ Xem tất cả giao dịch        │
└──────────────────────────────────────────────────────────────┘
```

**Đặc trưng:**
- Timeline dọc với các node phát sáng
- Mỗi node có thể expand để xem chi tiết
- Màu xanh neon cho các mốc đã verify trên Blockchain
- Hoạt động tốt trên mobile (Consumer quét QR bằng điện thoại)

---

### 5.4. 🛡️ Admin Dashboard — "Trung Tâm Điều Hành"

```
┌──────────────────────────────────────────────────────────────┐
│  [🏠 BICAP Admin]  [🔍 Global Search]  [🔔 12] [Avatar ▼]    │
├───────────────┬──────────────────────────────────────────────┤
│               │                                              │
│  📊 Dashboard │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐│
│  👥 Accounts  │  │ 245    │ │ 32     │ │ 1,240  │ │ 18     ││
│  🏠 Farms     │  │Users   │ │Farms   │ │Batches ││Pending ││
│  🌱 Crops     │  └────────┘ └────────┘ └────────┘ └────────┘│
│  📦 Products  │                                              │
│  🛒 Orders    │  ┌─────────────────────────────────────┐    │
│  ⛓️ Blockchain│  │ 📈 Biểu đồ tăng trưởng              │    │
│  🔔 Notifs    │  │                                     │    │
│               │  │  [Line Chart - 30 ngày]             │    │
│               │  └─────────────────────────────────────┘    │
│               │                                              │
│               │  ┌──────────────┐ ┌──────────────┐         │
│               │  │ 🏠 Farm mới  │ │ ⛓️ Tx gần đây│         │
│               │  │ [3 pending]  │ │ [Live feed]  │         │
│               │  └──────────────┘ └──────────────┘         │
└───────────────┴──────────────────────────────────────────────┘
```

---

## 6. Các Component Đặc thù

### 6.1. `GlassCard.vue` — Nền tảng của mọi UI

```vue
<template>
  <div class="glass-card" :class="variant">
    <div v-if="glow" class="glass-card__glow"></div>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'default' }, // default | elevated | outline
  glow: { type: Boolean, default: false }
});
</script>

<style scoped lang="scss">
.glass-card {
  position: relative;
  background: rgba(26, 35, 50, 0.55);
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(0, 255, 136, 0.15);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 255, 136, 0.08);
  transition: all 0.3s ease;
  
  &:hover {
    border-color: rgba(0, 255, 136, 0.35);
    box-shadow: 0 12px 48px rgba(0, 255, 136, 0.15);
    transform: translateY(-2px);
  }
  
  &__glow {
    position: absolute;
    inset: -1px;
    background: linear-gradient(135deg, #00FF88, #00D9FF);
    border-radius: inherit;
    opacity: 0.15;
    z-index: -1;
    filter: blur(20px);
  }
}
</style>
```

### 6.2. `TxHashCard.vue` — Hiển thị giao dịch Blockchain

```
┌────────────────────────────────────────┐
│  ⛓️ TRANSACTION                        │
│                                        │
│  0x1c44a82f9b21...f4d1e3a8   [📋 Copy]│  ← JetBrains Mono
│                                        │
│  ✅ SUCCESS   •   15/01/2026 10:23    │
│                                        │
│  Action: CREATE_PRODUCT_BATCH          │
│  Entity: BATCH001                      │
│                                        │
│  [🔗 Xem trên Explorer]                │
└────────────────────────────────────────┘
```

**Props:**
```javascript
{
  txHash: String,      // "0x1c44a82f9b21...f4d1e3a8"
  status: String,      // 'SUCCESS' | 'PENDING' | 'FAILED'
  action: String,      // 'CREATE_PRODUCT_BATCH'
  entityType: String,  // 'PRODUCT_BATCH'
  entityId: String,    // 'BATCH001'
  createdAt: Date
}
```

### 6.3. `OrderStatusStepper.vue` — Trạng thái đơn hàng

```
  ✅──────────●──────────○──────────○
  PENDING   CONFIRMED   SHIPPING   COMPLETED
    │           │
 08:00      10:30
 Retailer   Farm
```

**Logic:** Đọc `order.status` từ enum:
- `PENDING` (màu vàng, pulse animation)
- `CONFIRMED` (xanh dương)
- `COMPLETED` (xanh neon)
- `CANCELLED` (đỏ, gạch chéo)

### 6.4. `QRScanner.vue` — Quét mã QR

**Features:**
- Full-screen modal với scanner overlay
- Camera permission handling
- Auto redirect khi tìm thấy batch code
- Fallback: nhập batch code thủ công

---

## 7. Router & Phân quyền

### 7.1. Bảng Routes theo Role

| Route | Component | Roles | Ghi chú |
|---|---|---|---|
| `/` | `GuestLanding` | Public | Hero, giới thiệu |
| `/login` | `LoginView` | Public | |
| `/trace/:batchCode` | `TraceabilityView` | Public ⭐ | Consumer quét QR |
| `/admin` | `AdminDashboard` | Admin | |
| `/admin/accounts` | `AccountList` | Admin | UC03 |
| `/admin/farms` | `FarmApprovalList` | Admin | UC05 |
| `/admin/crops` | `CropManagement` | Admin | UC06 |
| `/admin/products` | `ProductManagement` | Admin | UC07 |
| `/admin/transactions` | `BlockchainExplorer` | Admin | UC19 |
| `/farm` | `FarmDashboard` | Farm | |
| `/farm/profile` | `FarmProfile` | Farm | UC04 |
| `/farm/seasons` | `SeasonManagement` | Farm | UC08 |
| `/farm/activities` | `ActivityLog` | Farm | UC09 |
| `/farm/batches` | `BatchManagement` | Farm | UC10 |
| `/farm/orders` | `FarmOrderList` | Farm | UC15, UC17 |
| `/retailer` | `RetailerDashboard` | Retailer | |
| `/retailer/products` | `ProductSearch` | Retailer | UC12 |
| `/retailer/products/:id` | `ProductBatchDetail` | Retailer | UC13 |
| `/retailer/cart` | `CartView` | Retailer | Cart |
| `/retailer/orders` | `OrderHistory` | Retailer | UC16 |

### 7.2. Router Guard (mẫu)

```javascript
// router/guards.js
import { useAuthStore } from '@/stores/auth';

export function roleGuard(to, from, next) {
  const auth = useAuthStore();
  const requiredRole = to.meta.role;
  
  if (!auth.isAuthenticated) {
    return next({ name: 'Login', query: { redirect: to.fullPath } });
  }
  
  if (requiredRole && auth.role !== requiredRole) {
    return next({ name: 'Forbidden' });
  }
  
  next();
}

export function guestGuard(to, from, next) {
  const auth = useAuthStore();
  if (auth.isAuthenticated) {
    return next({ name: `${auth.role.toLowerCase()}-dashboard` });
  }
  next();
}
```

### 7.3. Meta Fields cho Route

```javascript
{
  path: '/admin/accounts',
  name: 'admin-accounts',
  component: () => import('@/src/views/admin/Accounts.vue'),
  meta: {
    role: 'ADMIN',
    title: 'Quản lý tài khoản',
    breadcrumb: ['Admin', 'Accounts'],
    requiresAuth: true
  }
}
```

---

## 8. State Management (Pinia)

### 8.1. `auth.js` Store

```javascript
// stores/auth.js
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    role: null,           // 'ADMIN' | 'FARM' | 'RETAILER'
    farmId: null,         // ⭐ Quan trọng cho Farm
    retailerId: null,     // ⭐ Quan trọng cho Retailer
    accountId: null,
  }),
  
  getters: {
    isAuthenticated: (s) => !!s.token,
    isAdmin: (s) => s.role === 'ADMIN',
    isFarm: (s) => s.role === 'FARM',
    isRetailer: (s) => s.role === 'RETAILER',
  },
  
  actions: {
    async login(credentials) {
      // Gọi API, lưu token + user info
    },
    logout() {
      this.$reset();
      localStorage.removeItem('token');
      // Redirect to login
    },
    restoreSession() {
      // Restore từ localStorage khi F5
    }
  },
});
```

### 8.2. `cart.js` Store (Retailer)

```javascript
export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],           // [{ batchId, productName, quantity, unitPrice, farmId }]
    farmId: null,        // Ràng buộc BR02: chỉ 1 Farm
  }),
  
  getters: {
    totalItems: (s) => s.items.length,
    totalAmount: (s) => s.items.reduce((sum, i) => sum + i.quantity * i.unitPrice, 0),
    isEmpty: (s) => s.items.length === 0,
  },
  
  actions: {
    addItem(batch) {
      // Kiểm tra cùng Farm (BR03)
      // Nếu khác Farm → throw error
    },
    updateQuantity(batchId, qty) {},
    removeItem(batchId) {},
    clear() { this.items = []; this.farmId = null; }
  },
});
```

---

## 9. API Service Layer

### 9.1. Axios Instance

```javascript
// services/api.js
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
});

// Request interceptor — đính JWT
api.interceptors.request.use((config) => {
  const token = useAuthStore().token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Response interceptor — bắt 401
api.interceptors.response.use(
  (res) => res.data,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore().logout();
      router.push('/login');
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 9.2. Endpoints Mapping

| Module | Endpoint | Method |
|---|---|---|
| **Auth** | `/auth/login` | POST |
| | `/auth/register` | POST |
| | `/auth/logout` | POST |
| **Accounts** | `/admin/accounts` | GET, POST |
| | `/admin/accounts/:id` | GET, PUT, DELETE |
| | `/admin/accounts/:id/lock` | PATCH |
| **Farms** | `/admin/farms/pending` | GET |
| | `/admin/farms/:id/approve` | PATCH |
| | `/admin/farms/:id/reject` | PATCH |
| | `/farm/profile` | GET, PUT |
| **Crops** | `/admin/crops` | GET, POST, PUT |
| **Products** | `/farm/products` | GET, POST, PUT |
| | `/retailer/products/search` | GET |
| **Seasons** | `/farm/seasons` | GET, POST, PUT |
| **Activities** | `/farm/seasons/:id/activities` | GET, POST |
| **Batches** | `/farm/batches` | GET, POST |
| | `/farm/batches/:id/images` | POST, DELETE |
| **Cart** | `/retailer/cart` | GET, POST, PUT, DELETE |
| **Orders** | `/retailer/orders` | POST, GET |
| | `/farm/orders/:id/confirm` | PATCH |
| | `/admin/orders/:id/complete` | PATCH |
| **Blockchain** | `/blockchain/transactions` | GET |
| **Notifications** | `/notifications` | GET |
| | `/notifications/:id/read` | PATCH |
| **Traceability** | `/trace/:batchCode` | GET |

---

## 10. Checklist Triển khai (Implementation Roadmap)

### Phase 1 — Foundation (Tuần 1-2)
- [ ] Setup Vite + Vue 3 + Pinia + Vue Router
- [ ] Cấu hình SCSS variables + Glass mixin
- [ ] Xây dựng `GlassCard`, `AppButton`, `AppInput`, `AppBadge`
- [ ] Xây dựng 4 Layout (Guest, Admin, Farm, Retailer)
- [ ] Implement `authStore` + Login flow
- [ ] Router guards theo role
- [ ] Axios instance + interceptors

### Phase 2 — Core Modules (Tuần 3-4)
- [ ] Module Authentication (UC01, UC02)
- [ ] Module Account Management (UC03)
- [ ] Module Farm Approval (UC05)
- [ ] Module Crop Management (UC06)

### Phase 3 — Production Modules (Tuần 5-6)
- [ ] Farm Profile (UC04)
- [ ] Product Management (UC07)
- [ ] Farming Season (UC08) + Timeline
- [ ] Season Activity (UC09)

### Phase 4 — Commerce & Blockchain (Tuần 7-8)
- [ ] Product Batch (UC10, UC11) + QR generator
- [ ] Product Search (UC12, UC13)
- [ ] Cart + Order flow (UC14-UC18)
- [ ] Blockchain Explorer (UC19)
- [ ] Notification Center (UC20)

### Phase 5 — Traceability & Polish (Tuần 9)
- [ ] Traceability Page (UC21) ⭐
- [ ] QR Scanner component
- [ ] Responsive check (mobile/tablet)
- [ ] Animation polish, skeleton loading
- [ ] Write tests (Vitest, Cypress)

---

## 11. Điểm nhấn UX quan trọng

| Yếu tố | Thiết kế |
|---|---|
| **Feedback ngay lập tức** | Toast notification khi approve/reject farm (UC05), khi tạo order thành công |
| **Loading skeleton** | Mọi bảng/list dùng `AppSkeleton` thay vì spinner đơn điệu |
| **Empty state** | "Chưa có mùa vụ nào. Tạo mùa vụ đầu tiên của bạn?" với CTA rõ ràng |
| **Copy hash** | Nút copy cạnh mọi `tx_hash` với toast "Copied to clipboard" |
| **QR quét mượt** | Modal full-screen với scanner overlay, tự động redirect khi tìm thấy batch |
| **Mobile Farm Mode** | Farm thường dùng điện thoại ngoài đồng → Form nhập phải **to, dễ chạm**, có nút chụp ảnh trực tiếp |
| **Confirm Dialog** | Mọi hành động nguy hiểm (xóa, hủy đơn, từ chối) đều có modal xác nhận |
| **Error Handling** | Hiển thị lỗi rõ ràng, có nút "Retry" cho network errors |
| **Breadcrumb** | Luôn có breadcrumb ở các trang con để dễ điều hướng |
| **Optimistic UI** | Với action đơn giản (đánh dấu đã đọc, toggle), update UI ngay và rollback nếu API lỗi |

---

## 12. Cấu hình & Môi trường

### 12.1. `vite.config.js`

```javascript
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/styles/_variables.scss" as *;`
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
});
```

### 12.2. Environment Variables

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_NAME=BICAP
VITE_CHAIN_EXPLORER=https://explorer.example.com
VITE_QR_SCANNER_TIMEOUT=30000

# .env.production
VITE_API_BASE_URL=https://api.bicap.vn/api/v1
VITE_APP_NAME=BICAP
VITE_CHAIN_EXPLORER=https://explorer.bicap.vn
```

### 12.3. Package Dependencies

```json
{
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.0",
    "axios": "^1.6.0",
    "vue-qrcode-reader": "^5.0.0",
    "chart.js": "^4.4.0",
    "vue-chartjs": "^5.3.0",
    "lucide-vue-next": "^0.400.0"
  },
  "devDependencies": {
    "vite": "^5.2.0",
    "@vitejs/plugin-vue": "^5.0.0",
    "sass": "^1.75.0",
    "vitest": "^1.6.0",
    "@vue/test-utils": "^2.4.0",
    "cypress": "^13.10.0",
    "msw": "^2.3.0",
    "eslint": "^9.0.0",
    "prettier": "^3.2.0"
  }
}
```

---

## 13. Mapping Use Cases → Frontend Modules

| UC | Tên Use Case | Module Frontend | Route | Actor |
|---|---|---|---|---|
| UC01 | Đăng ký tài khoản | `features/auth` | `/register` | Retailer |
| UC02 | Đăng nhập | `features/auth` | `/login` | All |
| UC03 | Quản lý tài khoản | `features/accounts` | `/admin/accounts` | Admin |
| UC04 | Quản lý trang trại | `features/farms` | `/farm/profile` | Farm |
| UC05 | Duyệt Farm đăng ký | `features/farms` | `/admin/farms` | Admin |
| UC06 | Quản lý cây trồng | `features/crops` | `/admin/crops` | Admin |
| UC07 | Quản lý sản phẩm | `features/products` | `/farm/products` | Farm |
| UC08 | Quản lý mùa vụ | `features/seasons` | `/farm/seasons` | Farm |
| UC09 | Quản lý hoạt động mùa vụ | `features/activities` | `/farm/activities` | Farm |
| UC10 | Quản lý lô sản phẩm | `features/batches` | `/farm/batches` | Farm |
| UC11 | Quản lý hình ảnh lô | `features/batches` | `/farm/batches/:id/images` | Farm |
| UC12 | Tìm kiếm sản phẩm | `features/products` | `/retailer/products` | Retailer |
| UC13 | Xem chi tiết lô sản phẩm | `features/products` | `/retailer/products/:id` | Retailer |
| UC14 | Tạo đơn hàng | `features/orders` + `cart` | `/retailer/cart` | Retailer |
| UC15 | Xác nhận đơn hàng | `features/orders` | `/farm/orders` | Farm |
| UC16 | Hủy đơn hàng | `features/orders` | `/retailer/orders` | Retailer |
| UC17 | Hoàn tất đơn hàng | `features/orders` | `/admin/orders` | Admin |
| UC18 | Xem lịch sử đơn hàng | `features/orders` | `/farm/orders`, `/retailer/orders` | Farm, Retailer |
| UC19 | Tra cứu Blockchain | `features/blockchain` | `/admin/transactions` | Admin, Consumer |
| UC20 | Quản lý thông báo | `features/notifications` | `/notifications` | All |
| UC21 | Truy xuất nguồn gốc ⭐ | `features/traceability` | `/trace/:batchCode` | Consumer |

---

## 14. Testing Strategy

### 14.1. Test Pyramid

```
        ▲
       ╱ ╲         E2E (Cypress/Playwright) — 10%
      ╱───╲        Integration (MSW + Vue Test Utils) — 30%
     ╱─────╲       Unit (Vitest) — 60%
    ╱───────╲
   ╱─────────╲
```

### 14.2. Test Cases Trọng tâm

Dựa trên **Frontend Test Plan**, các module cần test ưu tiên:

| Module | Test Case IDs | Coverage |
|---|---|---|
| **Authentication** | AUTH-01 → AUTH-04 | Login flow, validation, "Stay connected" |
| **Guest Landing** | GUEST-01 → GUEST-03 | Sticky navbar, điều hướng, animation |
| **Admin Dashboard** | DASH-01 → DASH-03 | Layout, route protection, real-time update |
| **Accounts** | ACC-01, ACC-02 | Table rendering, role-based UI |
| **Farm Approvals** | FARM-01 → FARM-03 | Dynamic action buttons, approve/reject |
| **Blockchain Explorer** | BLK-01 → BLK-03 | Copy hash, search, empty state |

### 14.3. QA Standards

- **Performance:** Lighthouse > 90, LCP < 2.5s
- **Responsive:** Mobile (375px), Tablet (768px), Desktop (1440px)
- **Cross-browser:** Chrome, Safari, Edge, Firefox
- **Security:** JWT ở Header (không URL), XSS prevention với `{{ }}`
- **Accessibility:** WCAG 2.1 AA, keyboard navigation, ARIA labels

---

## 📌 Phụ lục: Business Rules ảnh hưởng đến UI

| BR | Nội dung | Ảnh hưởng Frontend |
|---|---|---|
| BR01 | Mỗi Account 1 Role | Route guard theo role, menu ẩn/hiện |
| BR04 | Account chỉ là Farm HOẶC Retailer | Redirect đúng dashboard theo role |
| BR07 | Không trùng Product trong cùng Farm+Crop | Validate form, show error inline |
| BR09 | Không trùng Season trong cùng Farm | Validate form |
| BR12 | Product & Season phải cùng Farm | Disable dropdown nếu khác Farm |
| BR16 | Order chỉ thuộc 1 Farm | Cart chỉ cho phép 1 Farm, warning khi thêm khác |
| BR18 | Batch chỉ xuất hiện 1 lần trong Order | Deduplicate trong cart |
| BR19 | Chỉ hủy Order ở PENDING | Ẩn nút Cancel nếu status ≠ PENDING |
| BR22 | Farm chỉ thao tác data của mình | Backend check, frontend ẩn nút không có quyền |

---

## 🎯 Kết luận

Thiết kế này đáp ứng đủ:

- ✅ **22 Use Cases** trong tài liệu (UC01-UC21 + Cart)
- ✅ **4 vai trò** với layout riêng biệt
- ✅ **Phong cách Glassmorphism** theo Frontend Test Plan
- ✅ **Feature-based structure** dễ scale, dễ maintain
- ✅ **Trang Traceability** làm điểm nhấn truyền thông
- ✅ **Tuân thủ Business Rules** (BR01-BR22) ở tầng UI/UX
- ✅ **Testability** với Vitest + Cypress + MSW

---

**Tài liệu được xây dựng dựa trên:**
- BICAP System Design Document (Chương 2-6)
- Frontend Test Plan — BICAP System
- Design System best practices 2026

**Liên hệ:** Frontend Team — BICAP Project
```

Tôi đã tạo file markdown hoàn chỉnh với đầy đủ 14 sections, bao gồm:

✅ **Design System đầy đủ** (colors, typography, spacing, status)
✅ **Folder structure** chi tiết theo feature-based
✅ **ASCII wireframes** cho 4 layouts chính
✅ **Code mẫu** cho `GlassCard`, `authStore`, Axios
✅ **Bảng route** đầy đủ với 20+ routes
✅ **API endpoints mapping**
✅ **Roadmap 5 phases** (9 tuần)
✅ **Mapping 22 Use Cases** → Frontend modules
✅ **Testing strategy** theo Test Pyramid
✅ **Business Rules** ảnh hưởng UI

Bạn có thể lưu file này thành `docs/frontend-design.md` trong repo dự án. Cần tôi bổ sung thêm phần nào không? Ví dụ:
- 📐 **Sequence Diagram Frontend** (login, tạo order)
- 🎨 **Figma-style specs** (spacing, shadows chi tiết)
- 🧪 **Chi tiết test cases** cho từng module