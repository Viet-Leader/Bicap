# BLOCKCHAIN FRONTEND INTEGRATION SPECIFICATION
## BICAP – Hệ thống truy xuất nguồn gốc nông sản

> **Loại tài liệu:** Frontend Refactor Prompt  
> **Trạng thái backend:** ĐÃ HOÀN THIỆN – toàn bộ API blockchain đã sẵn sàng  
> **Phạm vi công việc:** CHỈ sửa frontend, KHÔNG chỉnh backend, KHÔNG đổi schema DB  
> **Mục tiêu:** Tích hợp Blockchain vào đúng luồng nghiệp vụ của từng Actor, thay thế mock data bằng dữ liệu thật, hoàn thiện logic xác minh (verify) và hiển thị trạng thái giao dịch.

---

## 1. BỐI CẢNH & NGUYÊN TẮC THIẾT KẾ

### 1.1 Bối cảnh

Đồ án BICAP đã có giao diện cho 4 Actor: **Admin – Farm – Retailer – Guest**.  
Backend blockchain (ghi nhận, lưu TxHash, API verify) đã hoàn thiện.  
Tuy nhiên frontend hiện tại **chưa phản ánh đúng dữ liệu blockchain thật**:

- Nhiều badge "Đã xác thực" đang hard-code.
- Một số bảng vẫn dùng **Mock Data**.
- Có sự nhầm lẫn giữa hai chức năng "duyệt sản phẩm" và "theo dõi giao dịch blockchain".
- TxHash trong timeline đang bị gán `null`.

### 1.2 Nguyên tắc thiết kế (BẮT BUỘC tuân thủ)

1. **KHÔNG tạo thêm nhóm Page Blockchain riêng** cho từng Actor.  
   Blockchain phải được **tích hợp trực tiếp vào Page nghiệp vụ** hiện có.
2. **KHÔNG xây lại** các chức năng đã có sẵn (nút "Lưu & Ghi Blockchain", hiển thị TxHash, tạo QR trong `SeasonManagement.vue`).
3. **KHÔNG dùng Mock Data** ở bất kỳ màn hình nào liên quan blockchain — phải gọi API thật.
4. **Trạng thái xác minh phải phản ánh kết quả thật** từ backend, tuyệt đối không hard-code.
5. Mọi hiển thị trạng thái blockchain phải dùng chung một bộ trạng thái chuẩn:  
   `SUCCESS | PENDING | FAILED | NOT_RECORDED | VERIFIED | MISMATCH | CONNECTION_ERROR`.
6. Mọi TxHash/Data Hash hiển thị phải có **copy-to-clipboard** và **rút gọn dạng `0xabc…xyz`** (hover xem full).

### 1.3 Luồng nghiệp vụ chuẩn (kim chỉ nam)

```
Farm tạo dữ liệu  ──►  Ghi nhận Blockchain  ──►  Admin giám sát giao dịch
                                                └►  Retailer kiểm tra nguồn gốc
                                                └►  Guest quét QR & xác minh
```

---

## 2. BỐI CẢNH KỸ THUẬT – CÁC FILE HIỆN CÓ

> AI PHẢI đọc kỹ mục này trước khi sửa để tránh phá vỡ chức năng hiện tại.

### 2.1 Các file ĐÃ CÓ và cần lưu ý

| File | Trạng thái | Ghi chú |
|---|---|---|
| `Transactions.vue` | ⚠️ **ĐANG SAI MỤC ĐÍCH** | Nội dung hiện tại là **danh sách sản phẩm chờ duyệt** → đây KHÔNG phải trang giao dịch blockchain. Cần tách vai trò rõ ràng. |
| `features/blockchain/TransactionTable.vue` | ⚠️ **ĐANG DÙNG MOCK DATA** | Phải thay bằng API thật. Đây mới là component hiển thị giao dịch blockchain. |
| `SeasonManagement.vue` | ✅ **ĐÃ ĐẦY ĐỦ** | Đã có nút "Lưu & Ghi Blockchain", hiển thị TxHash, tạo QR. **KHÔNG sửa phần này**, chỉ cần bổ sung verify + trạng thái chính xác. |
| `TraceSearch.vue` | ✅ Đã có | Trang tìm kiếm của Guest. |
| `TraceabilityView.vue` | ⚠️ **CẦN HOÀN THIỆN** | Có 4 lỗi cụ thể – xem mục 3.4. |

### 2.2 Yêu cầu phân biệt rõ hai chức năng

- **"Duyệt sản phẩm"** (product approval) → giữ nguyên ở page hiện tại nếu cần.  
- **"Theo dõi giao dịch Blockchain"** → chuyển sang dùng `TransactionTable.vue` với API thật.

Đề xuất: **đổi tên** `Transactions.vue` thành `ProductApprovals.vue` (nếu vẫn giữ) và route `/admin/transactions` phải trỏ tới trang blockchain thật.

---

## 3. YÊU CẦU CHI TIẾT THEO TỪNG ACTOR

### 3.1 👤 ADMIN – Quản trị toàn hệ thống

#### 3.1.1 Page `Dashboard`

**Bổ sung:** Khối thống kê Blockchain gồm 3 chỉ số:

- Số giao dịch **thành công** (SUCCESS)
- Số giao dịch **thất bại** (FAILED)
- Số giao dịch **đang xử lý** (PENDING)

**Yêu cầu UI:**
- Dùng card/dashboard widget, có icon + màu phân biệt (xanh / đỏ / vàng).
- Click vào từng card → điều hướng tới `/admin/transactions?status=SUCCESS|FAILED|PENDING`.
- Có nút "Refresh" và hiển thị "Cập nhật lúc HH:mm:ss".

**Data source:** API thống kê blockchain (backend đã có).

---

#### 3.1.2 Page `Transactions` (Trang theo dõi giao dịch Blockchain)

**Đây là trang quan trọng nhất của Admin.** Phải xây dựng lại dựa trên `TransactionTable.vue` (bỏ mock data, gọi API thật).

**Cột bắt buộc trong bảng:**

| Cột | Mô tả |
|---|---|
| Transaction Hash | Rút gọn `0xabc…xyz`, có nút copy |
| Data Hash | Rút gọn, có nút copy |
| Loại dữ liệu | `ProductBatch` \| `SeasonActivity` (badge màu) |
| Đối tượng | Mã lô / mã hoạt động (link tới chi tiết) |
| Trạng thái | `SUCCESS / PENDING / FAILED` (badge) |
| Thời gian | `dd/MM/yyyy HH:mm:ss` |
| Hành động | Xem chi tiết, Verify |

**Chức năng bắt buộc:**

1. **Tìm kiếm theo Transaction Hash** – ô search, debounce 300ms, hỗ trợ paste full hash.
2. **Lọc theo loại đối tượng:** `ProductBatch` hoặc `SeasonActivity` (dropdown/tab).
3. **Lọc theo trạng thái:** `SUCCESS / PENDING / FAILED` (multi-select).
4. **Xem chi tiết giao dịch:** mở Drawer/Modal hiển thị:
   - Full TxHash, DataHash, BlockNumber, Timestamp
   - Payload gốc (JSON viewer có collapse)
   - Trạng thái xác minh
5. **Kiểm tra tính toàn vẹn dữ liệu (Verify):** nút "Verify" gọi API verify → hiển thị kết quả:
   - ✅ `VERIFIED` – "Dữ liệu khớp với blockchain"
   - ⚠️ `MISMATCH` – "Dữ liệu đã bị thay đổi"
   - ❌ `CONNECTION_ERROR` – "Không thể kết nối blockchain"
6. **Phân trang server-side** (page, pageSize).
7. **Empty state** rõ ràng: "Không có giao dịch nào phù hợp bộ lọc".

**Yêu cầu kỹ thuật:**
- Không load toàn bộ; dùng phân trang + filter qua query params.
- Sync filter với URL (`?status=...&type=...`) để share link được.
- Loading skeleton khi fetch.

---

#### 3.1.3 Page `Products`

**Bổ sung:** Hiển thị **trạng thái ghi nhận Blockchain** của từng ProductBatch.

- Cột "Blockchain" với badge: `SUCCESS / PENDING / FAILED / NOT_RECORDED`.
- Icon mở nhanh Transaction Detail (nếu có TxHash).
- Hover hiển thị TxHash rút gọn.

---

#### 3.1.4 Page `Farm Detail`

**Bổ sung:** Khối "Lịch sử ghi nhận Blockchain" của Farm:

- Danh sách các giao dịch liên quan Farm (theo thời gian giảm dần).
- Cột: TxHash, loại (ProductBatch/SeasonActivity), trạng thái, thời gian.
- Nút "Xem chi tiết" → Transaction Detail.

---

### 3.2 🌾 FARM – Quản lý nông trại

> **Đây là Actor trực tiếp tạo dữ liệu lên Blockchain → UI phải thể hiện blockchain rõ nhất.**

#### 3.2.1 Page `Farm Dashboard`

**Bổ sung:**

- Card **"ProductBatch đã ghi Blockchain"** (số lượng + % trên tổng).
- Card **"SeasonActivity đã ghi Blockchain"** (số lượng + % trên tổng).
- Card **"Giao dịch đang chờ" (PENDING)**.
- Card **"Giao dịch thất bại" (FAILED)** – có nút "Xem và xử lý".
- Nếu có FAILED → banner cảnh báo đỏ phía trên dashboard.

#### 3.2.2 Page `Product Management`

**Bổ sung:**

- Cột "Trạng thái Blockchain" cho mỗi ProductBatch.
- Cột "Transaction Hash" (rút gọn + copy).
- **Nút "Verify"** cho từng dòng → gọi API verify, hiển thị kết quả inline (icon ✅ / ⚠️ / ❌ + tooltip).
- Nếu `NOT_RECORDED` → nút **"Ghi Blockchain"** (nếu chưa ghi).
- Nếu `FAILED` → nút **"Thử ghi lại"**.

#### 3.2.3 Page `Season Monitor`

**Bổ sung:** (lưu ý `SeasonManagement.vue` đã có nút "Lưu & Ghi Blockchain", TxHash, QR – KHÔNG xây lại)

- Hiển thị **trạng thái ghi nhận Blockchain** của từng SeasonActivity (badge chuẩn).
- Hiển thị TxHash (nếu có).
- **Nút "Verify"** → gọi API verify, cập nhật badge thời gian thực.
- Nếu `PENDING` → tự động poll mỗi 5s để cập nhật khi có kết quả.
- Nếu `FAILED` → hiển thị lý do lỗi + nút "Thử lại".

#### 3.2.4 Page `Farm Information`

Không cần bổ sung blockchain. Giữ nguyên.

---

### 3.3 🛒 RETAILER – Người mua nông sản

> Retailer **không quản lý giao dịch**. Vai trò chính: **kiểm tra nguồn gốc sản phẩm đã mua**.

#### 3.3.1 Page `Marketplace / Product Search`

**Bổ sung:**

- **Badge "Truy xuất Blockchain"** (icon khiên xanh) trên sản phẩm có hỗ trợ.
- Tooltip: "Sản phẩm có thể xác minh nguồn gốc trên blockchain".
- Filter: "Chỉ hiện sản phẩm có truy xuất".

#### 3.3.2 Page `Cart`

Không cần bổ sung.

#### 3.3.3 Page `Order History`

**Bổ sung:**

- Mỗi ProductBatch trong đơn hàng có nút **"Truy xuất nguồn gốc"**.
- Click → mở Traceability View (reuse component của Guest, truyền `batchCode`).

**Phân quyền (quan trọng):**
- Retailer **chỉ được truy xuất các lô thuộc đơn hàng của mình**.
- Nếu cố truy cập lô khác → hiển thị: "Bạn không có quyền xem lô hàng này".
- Backend đã enforce – frontend hiển thị thông báo đúng.

**Nội dung Traceability View khi Retailer xem:**

- Thông tin ProductBatch (tên, ngày thu hoạch, …)
- Farm sản xuất (tên, địa chỉ, chứng nhận)
- Mùa vụ (Season)
- Các hoạt động canh tác (SeasonActivity) theo timeline
- Trạng thái xác minh Blockchain (`VERIFIED / MISMATCH / NOT_RECORDED / CONNECTION_ERROR`)
- Transaction Hash (nếu có)

---

### 3.4 👥 GUEST – Khách truy cập công khai

> Đây là Actor rất quan trọng – chứng minh **tính minh bạch** của BICAP với người tiêu dùng.

#### 3.4.1 Page `Home`

**Bổ sung:** Section giới thiệu cơ chế truy xuất nguồn gốc bằng Blockchain:

- 3–4 bước minh họa: Farm ghi dữ liệu → Blockchain lưu trữ → Người dùng quét QR → Xác minh.
- CTA "Thử truy xuất ngay" → trỏ tới `/trace`.

#### 3.4.2 Page `Market`

**Bổ sung:** Badge "Có truy xuất Blockchain" trên sản phẩm hỗ trợ.

#### 3.4.3 Page `Trace Search`

Giữ nguyên chức năng hiện tại (`TraceSearch.vue`):

- Nhập Batch Code
- Quét QR
- Submit → điều hướng `TraceabilityView`.

#### 3.4.4 Page `Traceability View` ⚠️ **TRỌNG TÂM – CẦN HOÀN THIỆN**

File: `TraceabilityView.vue` — **phải sửa 4 điểm sau:**

**Lỗi 1 – Badge "Đã xác thực" đang hard-code**

- ❌ Hiện tại: luôn hiển thị "Đã xác thực" bất kể kết quả thật.
- ✅ Yêu cầu: bind vào `verificationStatus` trả về từ API.
- Badge phải hỗ trợ đủ các trạng thái:

| Trạng thái | Label hiển thị | Màu | Icon |
|---|---|---|---|
| `VERIFIED` | "Đã xác thực trên Blockchain" | Xanh lá | ✅ |
| `MISMATCH` | "Dữ liệu không khớp blockchain" | Đỏ | ⚠️ |
| `NOT_RECORDED` | "Chưa ghi nhận Blockchain" | Xám | ⚪ |
| `CONNECTION_ERROR` | "Không thể kết nối blockchain" | Cam | 🔌 |
| `PENDING` | "Đang chờ xác nhận" | Vàng | ⏳ |

**Lỗi 2 – biến `isVerified` khai báo nhưng không dùng**

- ✅ Dùng `isVerified` (hoặc `verificationStatus`) để điều khiển **toàn bộ giao diện**:
  - Badge ở header.
  - Màu sắc timeline.
  - Nội dung tooltip.
  - Trạng thái nút "Verify".

**Lỗi 3 – `event.txHash` đang gán `null`**

- ✅ Phải lấy `txHash` thật từ API và hiển thị trong **từng event của Timeline**.
- Mỗi event trên timeline có:
  - TxHash (rút gọn, copy được)
  - Badge trạng thái riêng của event đó (SUCCESS / PENDING / FAILED)
  - Nút "Xem trên blockchain" (nếu có explorer URL) – optional

**Lỗi 4 – Chưa hiển thị rõ các trạng thái lỗi**

- ✅ Phải hiển thị **rõ ràng** 4 trạng thái:
  1. **Chưa ghi Blockchain** (`NOT_RECORDED`) – hướng dẫn: "Lô hàng này chưa được ghi nhận lên blockchain".
  2. **Xác minh thành công** (`VERIFIED`) – hiển thị dấu xanh + TxHash.
  3. **Không khớp** (`MISMATCH`) – banner đỏ: "Dữ liệu đã bị thay đổi so với blockchain. Vui lòng liên hệ nhà sản xuất."
  4. **Lỗi kết nối** (`CONNECTION_ERROR`) – banner vàng + nút "Thử lại".

**Yêu cầu bổ sung:**

- Nút **"Xác minh lại"** (manual re-verify) → gọi API verify.
- Tự động verify khi mount page (không cần user bấm).
- Loading state rõ ràng trong khi verify.
- Truyền `batchCode` qua route param `:batchCode` – không hard-code.

---

## 4. THỨ TỰ TRIỂN KHAI (BẮT BUỘC)

Thực hiện tuần tự theo đúng thứ tự sau – **không nhảy bước**:

| # | Actor | Page | Mục tiêu |
|---|---|---|---|
| **1** | Guest | `TraceabilityView.vue` | Hoàn thiện 4 lỗi đã nêu, hiển thị TxHash + trạng thái xác minh thật. |
| **2** | Farm | `Product Management` + `Season Monitor` | Bổ sung trạng thái giao dịch + nút Verify cho ProductBatch & SeasonActivity. |
| **3** | Admin | `Transactions` | Kết nối API thật, thay thế Mock Data trong `TransactionTable.vue`, phân biệt với trang duyệt sản phẩm. |
| **4** | Retailer | `Order History` | Tích hợp nút "Truy xuất nguồn gốc" cho từng lô đã mua. |
| **5** | Admin + Farm | `Dashboard` | Bổ sung thống kê Blockchain cho cả 2 Actor. |

---

## 5. TIÊU CHÍ NGHIỆM THU (ACCEPTANCE CRITERIA)

### 5.1 Guest – Traceability View

- [ ] Badge "Đã xác thực" thay đổi theo kết quả thật từ API.
- [ ] `isVerified` / `verificationStatus` điều khiển toàn bộ UI.
- [ ] TxHash hiển thị trong từng event timeline (không còn `null`).
- [ ] Hiển thị đúng 4 trạng thái: NOT_RECORDED, VERIFIED, MISMATCH, CONNECTION_ERROR.
- [ ] Có nút "Xác minh lại"; auto-verify on mount.

### 5.2 Farm

- [ ] `Product Management` có cột trạng thái blockchain + nút Verify hoạt động.
- [ ] `Season Monitor` có badge trạng thái + nút Verify; poll khi PENDING.
- [ ] `SeasonManagement.vue` **không bị sửa** phần ghi blockchain hiện có.

### 5.3 Admin

- [ ] `Transactions` gọi API thật, không còn mock data.
- [ ] Có đủ 4 chức năng: search TxHash, filter theo loại, filter theo trạng thái, xem chi tiết + verify.
- [ ] `Transactions.vue` (cũ – duyệt sản phẩm) được tách/đổi tên rõ ràng.
- [ ] Filter sync với URL.

### 5.4 Retailer

- [ ] `Order History` có nút "Truy xuất nguồn gốc" cho từng lô.
- [ ] Chỉ xem được lô thuộc đơn của mình; lô khác → báo lỗi quyền.
- [ ] `Marketplace` có badge "Có truy xuất Blockchain".

### 5.5 Dashboard

- [ ] Admin Dashboard có 3 card SUCCESS / FAILED / PENDING, click điều hướng.
- [ ] Farm Dashboard có card ProductBatch & SeasonActivity đã ghi blockchain + cảnh báo FAILED.

---

## 6. RÀNG BUỘC KỸ THUẬT

1. **Không đổi tên route** đang có (trừ khi bắt buộc – phải ghi rõ trong PR).
2. **Không xoá** component đang hoạt động – chỉ mở rộng/sửa.
3. **Không hard-code** bất kỳ trạng thái blockchain nào.
4. **Không gọi API trong vòng lặp render** – dùng effect/watch hợp lý.
5. Tất cả text hiển thị cho user **bằng tiếng Việt**, có dấu đầy đủ.
6. Tất cả thời gian hiển thị theo format `dd/MM/yyyy HH:mm:ss` (giờ VN).
7. Tất cả TxHash/DataHash dùng component `HashDisplay` dùng chung:
   - Rút gọn `0xabc…xyz`
   - Copy-to-clipboard có toast xác nhận
   - Hover hiện full hash
8. Trạng thái blockchain dùng component `BlockchainStatusBadge` dùng chung (5 trạng thái đã liệt kê ở mục 3.4.4).
9. **Loading skeleton** cho mọi bảng/danh sách khi fetch.
10. **Error boundary** ở cấp page – không để trắng màn hình khi API lỗi.

---

## 7. GHI CHÚ QUAN TRỌNG CHO AI

- ⚠️ Đọc kỹ **mục 2** trước khi sửa để tránh phá vỡ các chức năng đã có (đặc biệt `SeasonManagement.vue`).
- ⚠️ `Transactions.vue` và `TransactionTable.vue` là **hai thứ khác nhau** – không gộp lẫn.
- ⚠️ `TraceabilityView.vue` là file **quan trọng nhất** – ưu tiên sửa trước.
- ✅ Ưu tiên tạo **component dùng chung** (`BlockchainStatusBadge`, `HashDisplay`, `VerifyButton`, `TxDetailDrawer`) để tái sử dụng across 4 Actor.
- ✅ Nếu có mâu thuẫn giữa "code hiện tại" và "yêu cầu trong spec này" → **theo spec**, trừ khi phá vỡ chức năng đã được đánh dấu ✅.
- 📌 Mỗi khi sửa xong 1 Actor → **chạy lại checklist mục 5** trước khi sang Actor tiếp theo.

---

**HẾT TÀI LIỆU**