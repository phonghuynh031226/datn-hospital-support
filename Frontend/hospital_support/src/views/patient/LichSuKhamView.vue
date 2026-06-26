<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const activeSubTab = ref('bookings') // 'bookings' | 'examinations'
const isLoggedIn = ref(false)
const currentUser = ref(null)

const bookingHistory = ref([])
const examinations = ref([])
const selectedPrescription = ref(null) // Details for popup

onMounted(async () => {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
    await loadBookings()
    await loadExaminations()
  }
})

async function loadBookings() {
  try {
    const res = await axios.get(`http://localhost:8080/api/lich-hen/patient/${currentUser.value.id}`)
    bookingHistory.value = res.data.map(b => ({
      id: b.id,
      doctor: b.doctorName,
      date: b.ngayHen ? new Date(b.ngayHen).toLocaleDateString('vi-VN') : '',
      time: b.khungGio || 'Cả ngày',
      department: 'Phòng khám bệnh',
      symptoms: b.trieuChung || 'Không có triệu chứng',
      status: b.trangThai
    }))
  } catch (err) {
    console.error('Không tải được danh sách lịch hẹn', err)
  }
}

async function cancelBooking(id) {
  const confirmCancel = confirm('Bạn có chắc chắn muốn hủy lịch hẹn khám này không?')
  if (!confirmCancel) return
  
  try {
    await axios.put(`http://localhost:8080/api/lich-hen/${id}/cancel`)
    alert('Đã hủy lịch hẹn khám thành công.')
    await loadBookings()
  } catch (err) {
    alert(err.response?.data?.message || 'Hủy lịch hẹn khám thất bại!')
  }
}

async function loadExaminations() {
  try {
    const res = await axios.get(`http://localhost:8080/api/benh-an/patient/${currentUser.value.id}`)
    examinations.value = res.data.map(item => ({
      id: item.id,
      date: item.ngayTao ? new Date(item.ngayTao).toLocaleDateString('vi-VN') : '',
      department: 'Phòng khám chuyên khoa',
      doctor: item.bacSi ? item.bacSi.hoTen : 'Bác sĩ điều trị',
      disease: item.chanDoan || 'Chưa chẩn đoán xong',
      diagnosis: item.trieuChung || 'Không có triệu chứng ghi nhận',
      prescription: null // Loaded on click
    }))
  } catch (err) {
    console.error('Không tải được bệnh án', err)
  }
}

async function showPrescription(exam) {
  try {
    const res = await axios.get(`http://localhost:8080/api/don-thuoc/benh-an/${exam.id}`)
    const dt = res.data
    exam.prescription = {
      id: 'DT-' + dt.id,
      realId: dt.id,
      date: dt.ngayTao ? new Date(dt.ngayTao).toLocaleDateString('vi-VN') : '',
      notes: dt.trangThai,
      medicines: dt.details.map(m => ({
        name: m.tenThuoc,
        quantity: m.soLuongThucPhat || m.soLuong,
        unit: m.donViTinh,
        dosage: m.huongDanSuDung || 'Uống theo chỉ dẫn bác sĩ'
      }))
    }
    selectedPrescription.value = exam
  } catch (err) {
    alert(err.response?.data?.message || 'Bác sĩ chưa kê đơn thuốc cho bệnh án này hoặc đơn thuốc chưa được lập!')
  }
}

async function downloadPrescriptionPdf(exam) {
  try {
    const res = await axios.get(`http://localhost:8080/api/don-thuoc/benh-an/${exam.id}`)
    const dt = res.data
    window.open(`http://localhost:8080/api/don-thuoc/${dt.id}/pdf`, '_blank')
  } catch (err) {
    alert('Không thể tải file PDF đơn thuốc cho bệnh án này!')
  }
}

</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Lịch Sử Khám Bệnh</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Tra cứu lại lịch hẹn khám trực tuyến và xem kết quả chuẩn đoán bệnh, đơn thuốc chi tiết trước đây của bạn.
        </p>
      </div>
    </div>

    <!-- Check Login State -->
    <div v-if="!isLoggedIn" class="max-w-md mx-auto px-4 mt-16 text-center animate-fade-in space-y-6">
      <div class="bg-white p-8 rounded-3xl border border-gray-100 shadow-sm">
        <div class="w-16 h-16 bg-primary-50 text-primary-700 rounded-full flex items-center justify-center mx-auto mb-4 text-3xl">
          🔒
        </div>
        <h3 class="text-2xl font-bold text-gray-800">Yêu Cầu Đăng Nhập</h3>
        <p class="text-base text-gray-500 mt-2">Bạn cần đăng nhập tài khoản để tra cứu lịch sử và tải đơn thuốc y khoa.</p>
        <RouterLink to="/dang-nhap" class="w-full btn-primary !py-3.5 !text-lg !rounded-2xl shadow mt-6">
          Đến Trang Đăng Nhập
        </RouterLink>
      </div>
    </div>

    <div v-else class="max-w-5xl mx-auto px-4 mt-8">
      
      <!-- ==================== SUB-TABS ==================== -->
      <div class="flex border-b border-gray-200 bg-white rounded-t-3xl shadow-sm overflow-hidden">
        <button
          @click="activeSubTab = 'bookings'"
          class="flex-1 py-4.5 text-xl font-bold text-center border-b-4 transition-all duration-200 flex items-center justify-center gap-2"
          :class="activeSubTab === 'bookings'
            ? 'border-primary-700 text-primary-700 bg-primary-50/20'
            : 'border-transparent text-gray-400 hover:text-gray-600'"
        >
          <i class="bi bi-calendar-check text-2xl"></i>
          Lịch Đăng Ký Khám
        </button>
        <button
          @click="activeSubTab = 'examinations'"
          class="flex-1 py-4.5 text-xl font-bold text-center border-b-4 transition-all duration-200 flex items-center justify-center gap-2"
          :class="activeSubTab === 'examinations'
            ? 'border-primary-700 text-primary-700 bg-primary-50/20'
            : 'border-transparent text-gray-400 hover:text-gray-600'"
        >
          <i class="bi bi-file-earmark-medical text-2xl"></i>
          Lịch Sử Khám Lâm Sàng
        </button>
      </div>

      <!-- ==================== SUB-TAB: BOOKINGS (REGISTERED ONLINE) ==================== -->
      <div v-if="activeSubTab === 'bookings'" class="bg-white p-6 md:p-8 rounded-b-3xl border border-t-0 border-gray-100 shadow-sm space-y-6 animate-fade-in">
        <h2 class="text-2xl font-bold text-gray-800 border-b border-gray-100 pb-3">Lịch hẹn khám đang chờ</h2>
        
        <div v-if="bookingHistory.length > 0" class="space-y-4">
          <div
            v-for="b in bookingHistory"
            :key="b.id"
            class="p-6 border border-gray-100 bg-gray-50/50 rounded-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4 hover:shadow-md transition-shadow"
          >
            <div>
              <div class="flex items-center gap-2 flex-wrap">
                <span class="font-bold text-xl text-primary-800">{{ b.department }}</span>
                <span class="text-xs bg-primary-700 text-white font-bold px-2.5 py-0.5 rounded-full">{{ b.code }}</span>
                <span class="text-xs bg-emerald-100 text-emerald-800 font-bold px-2 py-0.5 rounded-full">Số TT: {{ b.stt }}</span>
              </div>
              <p class="text-base text-gray-600 mt-2">
                <i class="bi bi-person mr-1 text-primary-600"></i> Bác sĩ: <strong>{{ b.doctor }}</strong>
              </p>
              <p class="text-base text-gray-500 mt-1">
                <i class="bi bi-clock mr-1 text-primary-600"></i> Giờ khám: <strong class="text-primary-700">{{ b.timeSlot }}</strong> - Ngày khám: {{ b.date }}
              </p>
              <p class="text-sm text-gray-400 mt-1 italic">Triệu chứng: {{ b.symptoms }}</p>
            </div>

            <button
              @click="cancelBooking(b.id)"
              class="w-full md:w-auto px-5 py-3 bg-red-50 text-red-600 border border-red-200 font-bold text-base rounded-xl hover:bg-red-600 hover:text-white transition-all flex items-center justify-center gap-1.5"
            >
              <i class="bi bi-calendar-x"></i> Hủy lịch hẹn
            </button>
          </div>
        </div>
        
        <div v-else class="text-center py-12 text-gray-400 text-lg">
          Bạn không có lịch hẹn khám nào đang chờ khám.
        </div>
      </div>

      <!-- ==================== SUB-TAB: CLINICAL EXAMINATIONS ==================== -->
      <div v-if="activeSubTab === 'examinations'" class="bg-white p-6 md:p-8 rounded-b-3xl border border-t-0 border-gray-100 shadow-sm space-y-6 animate-fade-in">
        <h2 class="text-2xl font-bold text-gray-800 border-b border-gray-100 pb-3">Kết quả khám bệnh trước đây</h2>
        
        <div class="space-y-6">
          <div
            v-for="exam in examinations"
            :key="exam.id"
            class="p-6 border border-gray-200 rounded-3xl space-y-4 hover:shadow-md transition-shadow bg-white"
          >
            <!-- Header of visit -->
            <div class="flex justify-between items-start border-b border-gray-100 pb-3 flex-wrap gap-2">
              <div>
                <span class="text-xs bg-gray-100 text-gray-500 font-bold px-2 py-0.5 rounded">{{ exam.id }}</span>
                <h3 class="text-xl font-bold text-primary-800 mt-1">{{ exam.department }} — {{ exam.doctor }}</h3>
              </div>
              <span class="text-base font-bold text-gray-400"><i class="bi bi-calendar3"></i> Ngày khám: {{ exam.date }}</span>
            </div>

            <!-- Diagnosis details -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-base">
              <div class="md:col-span-1 p-4 bg-red-50/50 rounded-2xl border border-red-100">
                <h4 class="font-bold text-red-800 mb-1"><i class="bi bi-bookmark-heart"></i> Tên bệnh chẩn đoán:</h4>
                <p class="font-semibold text-gray-800 leading-snug">{{ exam.disease }}</p>
              </div>

              <div class="md:col-span-2 p-4 bg-gray-50 rounded-2xl border border-gray-100">
                <h4 class="font-bold text-gray-700 mb-1"><i class="bi bi-clipboard2-pulse"></i> Kết luận chẩn đoán lâm sàng:</h4>
                <p class="text-gray-600 leading-relaxed text-sm">{{ exam.diagnosis }}</p>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="flex flex-wrap gap-3 pt-2">
              <button
                @click="showPrescription(exam)"
                class="px-5 py-3 bg-primary-100 hover:bg-primary-200 text-primary-800 font-bold text-base rounded-xl border border-primary-200 shadow-sm flex items-center gap-2"
              >
                <i class="bi bi-capsule-prescription text-lg"></i> Xem Đơn Thuốc
              </button>
              <button
                @click="downloadPrescriptionPdf(exam)"
                class="px-5 py-3 border-2 border-primary-700 text-primary-700 font-bold text-base hover:bg-primary-50 rounded-xl flex items-center gap-2"
              >
                <i class="bi bi-download"></i> Tải Đơn Thuốc (PDF)
              </button>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- ==================== PRESCRIPTION DETAIL POPUP MODAL ==================== -->
    <div v-if="selectedPrescription" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-xl w-full max-h-[85vh] overflow-y-auto p-6 md:p-8 animate-fade-in-up space-y-6">
        
        <div class="flex justify-between items-start border-b border-gray-100 pb-4">
          <div>
            <span class="text-xs bg-primary-100 text-primary-800 font-bold px-2 py-0.5 rounded-full">ĐƠN THUỐC ĐIỆN TỬ</span>
            <h2 class="text-2xl font-bold text-gray-800 mt-1">Đơn Thuốc: {{ selectedPrescription.prescription.id }}</h2>
            <p class="text-xs text-gray-400 mt-0.5">Kê đơn ngày: {{ selectedPrescription.date }} - Bác sĩ: {{ selectedPrescription.doctor }}</p>
          </div>
          <button @click="selectedPrescription = null" class="w-10 h-10 rounded-full hover:bg-gray-100 flex items-center justify-center text-2xl text-gray-400 hover:text-gray-600 transition-colors">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="space-y-5">
          <!-- Diagnostics check -->
          <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100 text-base">
            <span class="text-xs text-gray-400 font-bold">BỆNH LÝ CHẨN ĐOÁN</span>
            <p class="font-bold text-gray-800 mt-0.5">{{ selectedPrescription.disease }}</p>
          </div>

          <!-- Medicine list -->
          <div class="space-y-4">
            <h4 class="text-lg font-bold text-gray-800 border-b border-gray-100 pb-1.5"><i class="bi bi-list-check text-primary-600 mr-1.5"></i> Danh sách thuốc:</h4>
            
            <div
              v-for="(med, idx) in selectedPrescription.prescription.medicines"
              :key="idx"
              class="p-4 border border-gray-100 rounded-2xl flex gap-3 text-base items-start bg-primary-50/20"
            >
              <span class="w-7 h-7 rounded-full bg-primary-700 text-white font-bold text-xs flex items-center justify-center flex-shrink-0 mt-0.5">
                {{ idx + 1 }}
              </span>
              <div class="flex-1">
                <div class="flex justify-between font-bold text-gray-800">
                  <span>{{ med.name }}</span>
                  <span class="text-primary-700">SL: {{ med.quantity }} {{ med.unit }}</span>
                </div>
                <p class="text-sm text-gray-600 mt-1.5 font-semibold bg-white p-2.5 rounded-xl border border-primary-100/50">
                  💊 Cách uống: {{ med.dosage }}
                </p>
              </div>
            </div>
          </div>

          <!-- Doctor notes -->
          <div class="p-4 bg-yellow-50 rounded-2xl border border-yellow-100 text-base">
            <h5 class="font-bold text-yellow-800 mb-1"><i class="bi bi-exclamation-triangle"></i> Ghi chú dặn dò của bác sĩ:</h5>
            <p class="text-gray-700 leading-relaxed text-sm">{{ selectedPrescription.prescription.notes }}</p>
          </div>
        </div>

        <!-- Footer -->
        <div class="mt-8 pt-4 border-t border-gray-100 flex justify-end gap-3">
          <button @click="selectedPrescription = null" class="px-6 py-3 border border-gray-200 rounded-xl font-bold text-gray-600 hover:bg-gray-50 text-base">Đóng</button>
          <button
            @click="downloadPrescription(selectedPrescription)"
            class="btn-primary !py-3 !px-6 !text-base !rounded-xl"
          >
            <i class="bi bi-download"></i> Tải Đơn Thuốc (.txt)
          </button>
        </div>

      </div>
    </div>

  </div>
</template>
