<template>
  <div class="container-fluid py-4">

    <!-- ===================================================== -->
    <!-- CARD 1: QUẢN LÝ MÙA VỤ                                -->
    <!-- ===================================================== -->
    <div class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Quản lý Mùa vụ</h5>
        <button class="btn btn-success mb-0" type="button" @click="showForm = !showForm">
          {{ showForm ? 'Đóng' : '+ Tạo mùa vụ' }}
        </button>
      </div>
      <div class="card-body">
        <div v-if="seasonError" class="alert alert-danger alert-dismissible fade show" role="alert">
          {{ seasonError }}
          <button type="button" class="btn-close" @click="seasonError = ''"></button>
        </div>

        <!-- Form tạo mùa vụ -->
        <form v-if="showForm" class="row g-3 mb-4 p-3 border rounded bg-light" @submit.prevent="createSeason">
          <div class="col-md-4">
            <label class="form-label" for="season-name">Tên mùa vụ <span class="text-danger">*</span></label>
            <input id="season-name" v-model.trim="seasonForm.seasonName" class="form-control" placeholder="VD: Lúa Đông Xuân 2026" required maxlength="150" />
          </div>
          <div class="col-md-3">
            <label class="form-label" for="planting-date">Ngày gieo trồng <span class="text-danger">*</span></label>
            <input id="planting-date" v-model="seasonForm.plantingDate" class="form-control" type="date" required />
          </div>
          <div class="col-md-3">
            <label class="form-label" for="harvest-date">Ngày thu hoạch dự kiến <span class="text-danger">*</span></label>
            <input id="harvest-date" v-model="seasonForm.expectedHarvestDate" class="form-control" type="date" required />
          </div>
          <div class="col-md-2 d-flex align-items-end">
            <button class="btn btn-primary mb-0 w-100" type="submit" :disabled="savingSeason">
              <span v-if="savingSeason" class="spinner-border spinner-border-sm me-1" role="status"></span>
              {{ savingSeason ? 'Đang lưu...' : 'Lưu mùa vụ' }}
            </button>
          </div>
        </form>

        <!-- Bảng danh sách mùa vụ -->
        <div v-if="loadingSeasons" class="text-center py-4">
          <div class="spinner-border text-success" role="status"></div>
          <p class="mt-2 text-muted">Đang tải danh sách mùa vụ...</p>
        </div>
        <div v-else-if="seasons.length === 0" class="alert alert-info mb-0">
          Chưa có mùa vụ nào. Hãy tạo mùa vụ đầu tiên!
        </div>
        <div v-else class="table-responsive">
          <table class="table align-items-center mb-0">
            <thead>
              <tr>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Tên mùa vụ</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Ngày gieo</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Ngày thu hoạch</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Trạng thái</th>
                <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="season in seasons"
                :key="season.seasonId"
                :class="{ 'table-active': selectedSeason?.seasonId === season.seasonId }"
                style="cursor:pointer"
                @click="selectSeason(season)"
              >
                <td>
                  <div class="d-flex align-items-center">
                    <span class="material-symbols-rounded text-success me-2" style="font-size:18px">agriculture</span>
                    <span class="text-sm font-weight-bold">{{ season.seasonName }}</span>
                  </div>
                </td>
                <td><p class="text-xs text-secondary mb-0">{{ formatDate(season.plantingDate) }}</p></td>
                <td>
                  <p class="text-xs text-secondary mb-0">
                    {{ formatDate(season.actualHarvestDate || season.expectedHarvestDate) }}
                  </p>
                  <small v-if="season.actualHarvestDate" class="text-success">Ngày thực tế</small>
                </td>
                <td>
                  <span class="badge badge-sm" :class="statusClass(season.status)">{{ statusLabel(season.status) }}</span>
                </td>
                <td class="text-center text-nowrap" @click.stop>
                  <button
                    v-if="season.status === 'PLANNING'"
                    class="btn btn-sm btn-info mb-0 me-1"
                    :disabled="actionLoading[season.seasonId]"
                    @click="updateSeason(season.seasonId, 'start')"
                  >
                    <span v-if="actionLoading[season.seasonId]" class="spinner-border spinner-border-sm me-1"></span>
                    Bắt đầu
                  </button>
                  <button
                    v-if="season.status === 'GROWING'"
                    class="btn btn-sm btn-success mb-0 me-1"
                    :disabled="actionLoading[season.seasonId]"
                    @click="updateSeason(season.seasonId, 'harvest')"
                  >
                    <span v-if="actionLoading[season.seasonId]" class="spinner-border spinner-border-sm me-1"></span>
                    Thu hoạch
                  </button>
                  <template v-if="season.status === 'HARVESTING'">
                    <input
                      v-model="finishDates[season.seasonId]"
                      class="form-control form-control-sm d-inline-block me-1"
                      style="width:145px"
                      type="date"
                      :max="today"
                      :disabled="actionLoading[season.seasonId]"
                      title="Ngày thu hoạch thực tế"
                    />
                    <button
                      class="btn btn-sm btn-success mb-0 me-1"
                      :disabled="actionLoading[season.seasonId] || !finishDates[season.seasonId]"
                      @click="finishSeason(season)"
                    >
                      <span v-if="actionLoading[season.seasonId]" class="spinner-border spinner-border-sm me-1"></span>
                      Hoàn thành mùa vụ
                    </button>
                  </template>
                  <button
                    v-if="season.status === 'FINISHED'"
                    class="btn btn-sm btn-outline-success mb-0 me-1"
                    :disabled="qrLoading[season.seasonId]"
                    @click="openSeasonQr(season)"
                  >
                    <span v-if="qrLoading[season.seasonId]" class="spinner-border spinner-border-sm me-1"></span>
                    <span v-else class="material-symbols-rounded me-1" style="font-size:16px;vertical-align:middle">qr_code_2</span>
                    Tạo mã QR
                  </button>
                  <button
                    v-if="!['FINISHED', 'CANCELLED'].includes(season.status)"
                    class="btn btn-sm btn-outline-danger mb-0"
                    :disabled="actionLoading[season.seasonId]"
                    @click="confirmCancel(season)"
                  >
                    Hủy
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- ===================================================== -->
    <!-- CARD 2: SỰ KIỆN CANH TÁC (SeasonActivity)             -->
    <!-- ===================================================== -->
    <div v-if="selectedSeason" class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div>
          <h5 class="mb-0">
            Sự kiện canh tác
            <span class="badge badge-sm ms-2" :class="statusClass(selectedSeason.status)">
              {{ selectedSeason.seasonName }}
            </span>
          </h5>
          <p class="text-xs text-secondary mb-0 mt-1">Mỗi sự kiện sẽ được ghi lên Blockchain để đảm bảo truy xuất nguồn gốc.</p>
        </div>
        <button
          v-if="!['FINISHED','CANCELLED'].includes(selectedSeason.status)"
          class="btn btn-primary btn-sm mb-0"
          @click="showEventForm = !showEventForm"
        >
          {{ showEventForm ? 'Đóng' : '+ Thêm sự kiện' }}
        </button>
      </div>
      <div class="card-body">

        <!-- Thông báo lỗi sự kiện -->
        <div v-if="eventError" class="alert alert-danger alert-dismissible fade show" role="alert">
          {{ eventError }}
          <button type="button" class="btn-close" @click="eventError = ''"></button>
        </div>

        <!-- Thông báo thành công -->
        <div v-if="eventSuccess" class="alert alert-success alert-dismissible fade show" role="alert">
          <span class="material-symbols-rounded me-1" style="font-size:18px;vertical-align:middle">verified</span>
          {{ eventSuccess }}
          <button type="button" class="btn-close" @click="eventSuccess = ''"></button>
        </div>

        <!-- Form tạo sự kiện -->
        <form
          v-if="showEventForm"
          class="row g-3 mb-4 p-3 border border-primary rounded"
          style="background:#f0f4ff"
          @submit.prevent="createEvent"
        >
          <div class="col-12">
            <h6 class="text-primary mb-0">
              <span class="material-symbols-rounded me-1" style="font-size:18px;vertical-align:middle">add_circle</span>
              Tạo sự kiện mới
            </h6>
          </div>
          <div class="col-md-4">
            <label class="form-label" for="activity-type">Loại sự kiện <span class="text-danger">*</span></label>
            <select id="activity-type" v-model="eventForm.activityTypeId" class="form-select" required>
              <option value="" disabled>-- Chọn loại --</option>
              <option v-for="t in activityTypes" :key="t.activityTypeId" :value="t.activityTypeId">
                {{ t.activityName }}
              </option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label" for="activity-time">Thời gian <span class="text-danger">*</span></label>
            <input id="activity-time" v-model="eventForm.activityTime" class="form-control" type="datetime-local" required />
          </div>
          <div class="col-md-4 d-flex align-items-end">
            <button class="btn btn-primary mb-0 w-100" type="submit" :disabled="savingEvent">
              <span v-if="savingEvent" class="spinner-border spinner-border-sm me-1" role="status"></span>
              <span v-if="savingEvent">Đang ghi Blockchain ⏳</span>
              <span v-else>
                <span class="material-symbols-rounded me-1" style="font-size:16px;vertical-align:middle">link</span>
                Lưu & Ghi Blockchain
              </span>
            </button>
          </div>
          <div class="col-12">
            <label class="form-label" for="activity-desc">Mô tả <span class="text-danger">*</span></label>
            <textarea id="activity-desc" v-model.trim="eventForm.description" class="form-control" rows="2"
              placeholder="VD: Bón phân NPK 200kg/ha, trời nắng, đất ẩm vừa..." required maxlength="2000"></textarea>
          </div>
          <div class="col-12">
            <div class="alert alert-info py-2 mb-0">
              <span class="material-symbols-rounded me-1" style="font-size:16px;vertical-align:middle">info</span>
              Sau khi nhấn lưu, hệ thống sẽ tự động ghi dữ liệu lên blockchain (~5–30 giây). Vui lòng không đóng trang.
            </div>
          </div>
        </form>

        <!-- Danh sách sự kiện -->
        <div v-if="loadingEvents" class="text-center py-4">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="mt-2 text-muted">Đang tải sự kiện...</p>
        </div>
        <div v-else-if="events.length === 0" class="alert alert-light border mb-0">
          <span class="material-symbols-rounded me-1" style="font-size:18px;vertical-align:middle">event_note</span>
          Chưa có sự kiện nào trong mùa vụ này.
        </div>
        <div v-else class="table-responsive">
          <table class="table align-items-center mb-0">
            <thead>
              <tr>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">#</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Loại sự kiện</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Thời gian</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Mô tả</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Blockchain TxHash</th>
                <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Xóa</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(evt, idx) in events" :key="evt.activityId">
                <td><p class="text-xs text-secondary mb-0">{{ idx + 1 }}</p></td>
                <td>
                  <span class="badge badge-sm bg-gradient-info">{{ evt.activityTypeName }}</span>
                </td>
                <td><p class="text-xs text-secondary mb-0">{{ formatDateTime(evt.activityTime) }}</p></td>
                <td>
                  <p class="text-xs mb-0" style="max-width:200px;white-space:normal">{{ evt.description }}</p>
                </td>
                <td>
                  <div v-if="evt.blockchainTxHash" class="d-flex align-items-center">
                    <span class="material-symbols-rounded text-success me-1" style="font-size:16px">verified</span>
                    <span
                      class="text-xs font-monospace text-success"
                      style="max-width:120px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap"
                      :title="evt.blockchainTxHash"
                    >
                      {{ evt.blockchainTxHash }}
                    </span>
                  </div>
                  <span v-else class="badge badge-sm bg-gradient-secondary">Chưa ghi</span>
                </td>
                <td class="text-center">
                  <button
                    class="btn btn-sm btn-outline-danger mb-0"
                    :disabled="deletingEvent[evt.activityId]"
                    @click="deleteEvent(evt.activityId)"
                  >
                    <span v-if="deletingEvent[evt.activityId]" class="spinner-border spinner-border-sm"></span>
                    <span v-else class="material-symbols-rounded" style="font-size:16px">delete</span>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Gợi ý khi chưa chọn mùa vụ -->
    <div v-else-if="!loadingSeasons && seasons.length > 0" class="alert alert-light border text-center mt-2">
      <span class="material-symbols-rounded me-1" style="font-size:20px;vertical-align:middle">touch_app</span>
      Nhấn vào một mùa vụ để xem và quản lý sự kiện canh tác.
    </div>

    <div v-if="qrModal.open" class="qr-modal-backdrop" @click.self="closeSeasonQr">
      <div class="qr-modal" role="dialog" aria-modal="true" aria-labelledby="qr-title">
        <div class="d-flex justify-content-between align-items-center mb-3 no-print">
          <div>
            <h5 id="qr-title" class="mb-1">Mã QR truy xuất blockchain</h5>
            <p class="text-muted small mb-0">{{ qrModal.seasonName }}</p>
          </div>
          <button class="btn btn-sm btn-light" type="button" aria-label="Đóng" @click="closeSeasonQr">×</button>
        </div>
        <div v-if="qrModal.loading" class="text-center py-4 no-print">
          <span class="spinner-border text-success"></span>
          <p class="small text-muted mt-2 mb-0">Đang tạo mã QR...</p>
        </div>
        <div v-else-if="qrModal.error" class="alert alert-warning no-print">{{ qrModal.error }}</div>
        <template v-else>
          <div class="qr-list">
            <article v-for="batch in qrModal.batches" :key="batch.batchId" class="qr-item">
              <img :src="batch.qrDataUrl" :alt="`Mã QR lô ${batch.batchCode}`" width="220" height="220" />
              <div>
                <h6 class="mb-1">{{ batch.batchCode }}</h6>
                <p class="small text-muted mb-2">Quét để xem hành trình và xác thực blockchain</p>
                <code class="small">{{ batch.traceUrl }}</code>
              </div>
            </article>
          </div>
          <p v-if="qrModal.batches.length === 0" class="text-muted text-center py-3">Mùa vụ chưa có lô sản phẩm để tạo mã QR.</p>
          <div class="d-flex justify-content-end gap-2 mt-3 no-print">
            <button class="btn btn-outline-secondary" type="button" @click="closeSeasonQr">Đóng</button>
            <button v-if="qrModal.batches.length" class="btn btn-success" type="button" @click="printSeasonQr">
              <span class="material-symbols-rounded me-1" style="font-size:17px;vertical-align:middle">print</span>
              In mã QR
            </button>
          </div>
        </template>
      </div>
    </div>

  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import axiosClient from '@/api/axiosClient';
import QRCode from 'qrcode';

// ================================================================
// STATE — Mùa vụ
// ================================================================
const seasons = ref([]);
const loadingSeasons = ref(true);
const savingSeason = ref(false);
const showForm = ref(false);
const seasonError = ref('');
const actionLoading = reactive({}); // { [seasonId]: boolean }
const finishDates = reactive({});
const today = new Date().toISOString().slice(0, 10);
const qrLoading = reactive({});
const qrModal = reactive({
  open: false,
  loading: false,
  error: '',
  seasonName: '',
  batches: [],
});

const seasonForm = reactive({
  seasonName: '',
  plantingDate: '',
  expectedHarvestDate: '',
});

// ================================================================
// STATE — Sự kiện canh tác
// ================================================================
const selectedSeason = ref(null);
const events = ref([]);
const loadingEvents = ref(false);
const savingEvent = ref(false);
const showEventForm = ref(false);
const eventError = ref('');
const eventSuccess = ref('');
const deletingEvent = reactive({}); // { [activityId]: boolean }
const activityTypes = ref([]);

const eventForm = reactive({
  activityTypeId: '',
  activityTime: '',
  description: '',
});

// ================================================================
// API — Mùa vụ
// ================================================================

const loadSeasons = async () => {
  loadingSeasons.value = true;
  seasonError.value = '';
  try {
    const { data } = await axiosClient.get('/farming-seasons');
    seasons.value = data.content || data || [];
  } catch (err) {
    seasonError.value = err.response?.data?.message || 'Không thể tải danh sách mùa vụ.';
  } finally {
    loadingSeasons.value = false;
  }
};

const createSeason = async () => {
  if (seasonForm.expectedHarvestDate && seasonForm.plantingDate
      && seasonForm.expectedHarvestDate < seasonForm.plantingDate) {
    seasonError.value = 'Ngày thu hoạch phải sau ngày gieo trồng.';
    return;
  }
  savingSeason.value = true;
  seasonError.value = '';
  try {
    await axiosClient.post('/farming-seasons', seasonForm);
    Object.assign(seasonForm, { seasonName: '', plantingDate: '', expectedHarvestDate: '' });
    showForm.value = false;
    await loadSeasons();
  } catch (err) {
    seasonError.value = err.response?.data?.message || 'Không thể tạo mùa vụ.';
  } finally {
    savingSeason.value = false;
  }
};

const updateSeason = async (id, action) => {
  actionLoading[id] = true;
  seasonError.value = '';
  try {
    await loadSeasons();
    const currentSeason = seasons.value.find(season => season.seasonId === id);
    const requiredStatus = {
      start: 'PLANNING',
      harvest: 'GROWING',
      cancel: null,
    }[action];

    if (!currentSeason) {
      throw new Error('Không tìm thấy mùa vụ.');
    }

    if (requiredStatus && currentSeason.status !== requiredStatus) {
      seasonError.value = `Mùa vụ hiện đang ở trạng thái “${statusLabel(currentSeason.status)}”, không thể thực hiện thao tác này.`;
      return;
    }

    await axiosClient.patch(`/farming-seasons/${id}/${action}`);
    await loadSeasons();
    // Sync lại selectedSeason nếu đang chọn
    if (selectedSeason.value?.seasonId === id) {
      selectedSeason.value = seasons.value.find(s => s.seasonId === id) || null;
    }
  } catch (err) {
    seasonError.value = err.response?.data?.message || 'Không thể cập nhật mùa vụ.';
  } finally {
    actionLoading[id] = false;
  }
};

const finishSeason = async (season) => {
  const actualHarvestDate = finishDates[season.seasonId];
  if (!actualHarvestDate) {
    seasonError.value = 'Vui lòng chọn ngày thu hoạch thực tế.';
    return;
  }

  actionLoading[season.seasonId] = true;
  seasonError.value = '';
  try {
    await axiosClient.patch(`/farming-seasons/${season.seasonId}/finish`, {
      actualHarvestDate,
    });
    delete finishDates[season.seasonId];
    await loadSeasons();
    if (selectedSeason.value?.seasonId === season.seasonId) {
      selectedSeason.value = seasons.value.find(item => item.seasonId === season.seasonId) || null;
    }
  } catch (err) {
    seasonError.value = err.response?.data?.message || 'Không thể hoàn thành mùa vụ.';
    await loadSeasons();
    if (selectedSeason.value?.seasonId === season.seasonId) {
      selectedSeason.value = seasons.value.find(item => item.seasonId === season.seasonId) || null;
    }
  } finally {
    actionLoading[season.seasonId] = false;
  }
};

const openSeasonQr = async (season) => {
  qrModal.open = true;
  qrModal.loading = true;
  qrModal.error = '';
  qrModal.seasonName = season.seasonName;
  qrModal.batches = [];
  qrLoading[season.seasonId] = true;
  try {
    const { data } = await axiosClient.get(`/farming-seasons/${season.seasonId}/batches`);
    const batches = data || [];
    qrModal.batches = await Promise.all(batches.map(async (batch) => {
      const traceUrl = `${window.location.origin}/trace/${batch.batchId}`;
      return {
        ...batch,
        traceUrl,
        qrDataUrl: await QRCode.toDataURL(traceUrl, { width: 220, margin: 2 }),
      };
    }));
  } catch (err) {
    qrModal.error = err.response?.data?.message || 'Không thể tạo mã QR cho mùa vụ.';
  } finally {
    qrModal.loading = false;
    qrLoading[season.seasonId] = false;
  }
};

const closeSeasonQr = () => {
  qrModal.open = false;
};

const printSeasonQr = () => {
  window.print();
};

const confirmCancel = (season) => {
  if (!confirm(`Bạn có chắc muốn HỦY mùa vụ "${season.seasonName}"? Hành động này không thể hoàn tác.`)) return;
  updateSeason(season.seasonId, 'cancel');
};

// ================================================================
// API — Sự kiện
// ================================================================

const selectSeason = async (season) => {
  selectedSeason.value = season;
  showEventForm.value = false;
  eventError.value = '';
  eventSuccess.value = '';
  await loadEvents(season.seasonId);
};

const loadEvents = async (seasonId) => {
  loadingEvents.value = true;
  events.value = [];
  try {
    const { data } = await axiosClient.get(`/season-activities/season/${seasonId}`);
    events.value = data.content || data || [];
  } catch (err) {
    eventError.value = err.response?.data?.message || 'Không thể tải danh sách sự kiện.';
  } finally {
    loadingEvents.value = false;
  }
};

const loadActivityTypes = async () => {
  try {
    const { data } = await axiosClient.get('/activity-types');
    activityTypes.value = data || [];
  } catch {
    // Không hiển thị lỗi — nếu fail thì dropdown trống
  }
};

const createEvent = async () => {
  if (!eventForm.activityTypeId) {
    eventError.value = 'Vui lòng chọn loại sự kiện.';
    return;
  }
  savingEvent.value = true;
  eventError.value = '';
  eventSuccess.value = '';
  try {
    const payload = {
      activityTypeId: Number(eventForm.activityTypeId),
      activityTime: eventForm.activityTime, // ISO datetime-local
      description: eventForm.description,
    };
    const { data } = await axiosClient.post(
      `/season-activities/season/${selectedSeason.value.seasonId}`,
      payload
    );
    // Reset form
    Object.assign(eventForm, { activityTypeId: '', activityTime: '', description: '' });
    showEventForm.value = false;

    const txHash = data.blockchainTxHash;
    if (txHash) {
      eventSuccess.value = `✅ Sự kiện đã được ghi lên Blockchain thành công! TxHash: ${txHash}`;
    } else {
      eventSuccess.value = '✅ Sự kiện đã được lưu.';
    }

    await loadEvents(selectedSeason.value.seasonId);
  } catch (err) {
    const msg = err.response?.data?.message || err.message || '';
    if (msg.toLowerCase().includes('blockchain')) {
      eventError.value = '⚠️ Lỗi Blockchain: ' + msg + '. Sự kiện chưa được ghi. Vui lòng thử lại.';
    } else {
      eventError.value = msg || 'Không thể tạo sự kiện.';
    }
  } finally {
    savingEvent.value = false;
  }
};

const deleteEvent = async (activityId) => {
  if (!confirm('Bạn có chắc muốn xóa sự kiện này?')) return;
  deletingEvent[activityId] = true;
  try {
    await axiosClient.delete(`/season-activities/${activityId}`);
    await loadEvents(selectedSeason.value.seasonId);
  } catch (err) {
    eventError.value = err.response?.data?.message || 'Không thể xóa sự kiện.';
  } finally {
    deletingEvent[activityId] = false;
  }
};

// ================================================================
// Helpers
// ================================================================

const formatDate = (value) =>
  value ? new Date(value).toLocaleDateString('vi-VN') : 'Chưa cập nhật';

const formatDateTime = (value) =>
  value ? new Date(value).toLocaleString('vi-VN') : 'Chưa cập nhật';

const STATUS_LABELS = {
  PLANNING: 'Lên kế hoạch',
  GROWING: 'Đang canh tác',
  HARVESTING: 'Đang thu hoạch',
  FINISHED: 'Đã kết thúc',
  CANCELLED: 'Đã hủy',
};

const STATUS_CLASSES = {
  PLANNING: 'bg-gradient-secondary',
  GROWING: 'bg-gradient-info',
  HARVESTING: 'bg-gradient-warning',
  FINISHED: 'bg-gradient-success',
  CANCELLED: 'bg-gradient-danger',
};

const statusLabel = (status) => STATUS_LABELS[status] || status;
const statusClass = (status) => STATUS_CLASSES[status] || 'bg-gradient-secondary';

// ================================================================
// Lifecycle
// ================================================================
onMounted(async () => {
  await Promise.all([loadSeasons(), loadActivityTypes()]);
});
</script>
