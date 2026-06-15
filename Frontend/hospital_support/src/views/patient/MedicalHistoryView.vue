<script setup>
import { ref, onMounted } from 'vue'

const activeSubTab = ref('bookings') // 'bookings' | 'examinations'
const isLoggedIn = ref(false)
const currentUser = ref(null)

const bookingHistory = ref([])
const selectedPrescription = ref(null) // Details for popup

onMounted(() => {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
  }
  loadBookings()
})

function loadBookings() {
  const data = localStorage.getItem('patientBookings')
  if (data) {
    bookingHistory.value = JSON.parse(data)
  }
}

function cancelBooking(id) {
  const confirmCancel = confirm('Bác có chắc chắn muốn hủy lịch hẹn khám này không?')
  if (!confirmCancel) return
  
  bookingHistory.value = bookingHistory.value.filter(b => b.id !== id)
  localStorage.setItem('patientBookings', JSON.stringify(bookingHistory.value))
  alert('Đã hủy lịch hẹn khám thành công.')
}

/* ==================== CLINICAL EXAMINATION HISTORY MOCK DATA ==================== */
const examinations = [
  {
    id: 'EXAM-829472',
    date: '10/05/2026',
    department: 'Tim mạch',
    doctor: 'PGS.TS Nguyễn Văn An',
    disease: 'Tăng huyết áp vô căn (I10)',
    diagnosis: 'Huyết áp đo tại phòng khám 150/90 mmHg. Nhịp tim đều 78ck/phút. Tim T1 T2 rõ, không âm bệnh lý.',
    prescription: {
      id: 'DT-992018',
      date: '10/05/2026',
      notes: 'Uống đúng giờ hằng ngày, hạn chế ăn mặn (dưới 5g muối/ngày), kiêng bia rượu, không làm việc nặng quá sức.',
      medicines: [
        { name: 'Amlodipine 5mg', quantity: 30, unit: 'Viên', dosage: 'Uống 1 viên vào lúc 08:00 sáng, sau khi ăn.' },
        { name: 'Concor 5mg (Bisoprolol)', quantity: 30, unit: 'Viên', dosage: 'Uống 1 viên vào lúc 08:00 sáng, sau khi ăn.' }
      ]
    }
  },
  {
    id: 'EXAM-619284',
    date: '22/04/2026',
    department: 'Mắt',
    doctor: 'TS.BS Lê Thị Lan',
    disease: 'Đục thể thủy tinh tuổi già (H25)',
    diagnosis: 'Mắt phải thị lực 4/10, đục nhân nhẹ. Mắt trái thị lực 2/10, đục chín. Đáy mắt bình thường.',
    prescription: {
      id: 'DT-552019',
      date: '22/04/2026',
      notes: 'Nhỏ thuốc đều đặn hằng ngày, tránh để bụi bẩn bay vào mắt. Tái khám sau 1 tháng để chuẩn bị lịch phẫu thuật Phaco mắt trái.',
      medicines: [
        { name: 'Kary Uni 0.005% (Nhỏ mắt)', quantity: 2, unit: 'Lọ', dosage: 'Nhỏ mắt trái 4 lần/ngày (sáng, trưa, chiều, tối), mỗi lần 1 giọt.' },
        { name: 'Ocuvite PreserVision (Bổ mắt)', quantity: 60, unit: 'Viên', dosage: 'Uống ngày 2 viên chia 2 lần sáng và tối, sau ăn.' }
      ]
    }
  },
  {
    id: 'EXAM-442819',
    date: '15/03/2026',
    department: 'Nội tổng quát',
    doctor: 'ThS.BS Trần Văn Bình',
    disease: 'Viêm dạ dày - tá tràng mãn tính (K29)',
    diagnosis: 'Đau âm ỉ vùng thượng vị, ợ hơi nhiều. Nội soi phát hiện sung huyết niêm mạc hang vị.',
    prescription: {
      id: 'DT-112030',
      date: '15/03/2026',
      notes: 'Ăn chín uống sôi, không ăn đồ chua, cay, nóng. Chia nhỏ bữa ăn, không ăn quá no trước khi đi ngủ.',
      medicines: [
        { name: 'Esomeprazole 40mg', quantity: 28, unit: 'Viên', dosage: 'Uống 1 viên vào buổi sáng trước khi ăn 30 phút.' },
        { name: 'Phosphalugel (Thuốc chữ P)', quantity: 30, unit: 'Gói', dosage: 'Uống 1 gói khi đau hoặc sau ăn 2 tiếng.' }
      ]
    }
  }
]

// Mock Prescription File Download
function downloadPrescription(exam) {
  const content = `==================================================
              ĐƠN THUỐC BỆNH VIỆN ĐA KHOA
==================================================
Mã đơn thuốc: ${exam.prescription.id}
Ngày kê đơn: ${exam.date}
Bác sĩ kê đơn: ${exam.doctor}
Khoa khám: ${exam.department}
--------------------------------------------------
Chẩn đoán bệnh: ${exam.disease}
--------------------------------------------------
DANH SÁCH THUỐC KÊ ĐƠN:
${exam.prescription.medicines.map((med, idx) => `${idx + 1}. ${med.name} - Số lượng: ${med.quantity} ${med.unit}\n   Hướng dẫn: ${med.dosage}`).join('\n\n')}

Ghi chú từ bác sĩ:
${exam.prescription.notes}
--------------------------------------------------
Cảm ơn bác đã tin tưởng khám bệnh tại Bệnh viện Đa khoa!
==================================================`

  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `Don_Thuoc_${exam.prescription.id}.txt`
  link.click()
  URL.revokeObjectURL(url)
  alert('Đã tải đơn thuốc về máy của bác thành công!')
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Lịch Sử Khám Bệnh</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Tra cứu lại lịch hẹn khám trực tuyến và xem kết quả chuẩn đoán bệnh, đơn thuốc chi tiết trước đây của bác.
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
        <p class="text-base text-gray-500 mt-2">Bác cần đăng nhập tài khoản để tra cứu lịch sử và tải đơn thuốc y khoa.</p>
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
          Bác không có lịch hẹn khám nào đang chờ khám.
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
                @click="selectedPrescription = exam"
                class="px-5 py-3 bg-primary-100 hover:bg-primary-200 text-primary-800 font-bold text-base rounded-xl border border-primary-200 shadow-sm flex items-center gap-2"
              >
                <i class="bi bi-capsule-prescription text-lg"></i> Xem Đơn Thuốc
              </button>
              <button
                @click="downloadPrescription(exam)"
                class="px-5 py-3 border-2 border-primary-700 text-primary-700 font-bold text-base hover:bg-primary-50 rounded-xl flex items-center gap-2"
              >
                <i class="bi bi-download"></i> Tải Đơn Thuốc (File chữ)
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
