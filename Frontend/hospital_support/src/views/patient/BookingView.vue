<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// User state
const isLoggedIn = ref(false)
const currentUser = ref(null)

// Form state
const form = ref({
  fullName: '',
  gender: 'Nam',
  phone: '',
  email: '',
  address: '',
  department: '',
  doctor: '',
  date: '',
  timeSlot: '',
  symptoms: '',
  bookingType: 'Khám mới',
  paymentMethod: 'Tại quầy',
  paymentStatus: 'Chưa thanh toán'
})

// AI Suggestion State
const aiSuggestedDept = ref('')
const aiConfidence = ref(0)

// UI state
const showConfirmModal = ref(false)
const showPaymentQRModal = ref(false) // New QR mockup modal
const activeBookingResult = ref(null) // Holds successfully created booking
const bookingHistory = ref([])

const departmentOptions = [
  'Tim mạch', 'Nội tổng quát', 'Ngoại khoa',
  'Sản phụ khoa', 'Nhi khoa', 'Mắt'
]

const doctorsMap = {
  'Tim mạch': ['PGS.TS Nguyễn Văn An', 'Bác sĩ bất kỳ'],
  'Nội tổng quát': ['ThS.BS Trần Văn Bình', 'Bác sĩ bất kỳ'],
  'Ngoại khoa': ['GS.TS Lê Hoàng Minh', 'Bác sĩ bất kỳ'],
  'Sản phụ khoa': ['TS.BS Trần Thị Mai', 'Bác sĩ bất kỳ'],
  'Nhi khoa': ['ThS.BS Phạm Thùy Linh', 'Bác sĩ bất kỳ'],
  'Mắt': ['TS.BS Lê Thị Lan', 'Bác sĩ bất kỳ']
}

const timeSlots = [
  '08:00 - 08:30', '08:30 - 09:00', '09:00 - 09:30', '09:30 - 10:00',
  '10:00 - 10:30', '10:30 - 11:00', '11:00 - 11:30',
  '13:30 - 14:00', '14:00 - 14:30', '14:30 - 15:00', '15:00 - 15:30',
  '15:30 - 16:00', '16:00 - 16:30'
]

onMounted(() => {
  // Load user from localStorage
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
    
    // Autofill
    form.value.fullName = currentUser.value.fullName || ''
    form.value.gender = currentUser.value.gender || 'Nam'
    form.value.phone = currentUser.value.phone || ''
    form.value.email = currentUser.value.email || ''
    form.value.address = currentUser.value.address || ''
  }
  
  // Load bookings list
  loadBookings()
})

function loadBookings() {
  const data = localStorage.getItem('patientBookings')
  if (data) {
    bookingHistory.value = JSON.parse(data)
  } else {
    bookingHistory.value = []
  }
}

// AI logic triggered by symptoms input
watch(() => form.value.symptoms, (newVal) => {
  if (!newVal || newVal.trim().length < 5) {
    aiSuggestedDept.value = ''
    aiConfidence.value = 0
    return
  }
  
  const val = newVal.toLowerCase()
  
  // 1. Tim mạch
  if (val.includes('ngực') || val.includes('tim') || val.includes('huyết áp') || val.includes('mạch') || val.includes('hồi hộp') || val.includes('xơ vữa')) {
    aiSuggestedDept.value = 'Tim mạch'
    aiConfidence.value = 92
    form.value.department = 'Tim mạch'
  }
  // 2. Mắt
  else if (val.includes('mắt') || val.includes('nhìn') || val.includes('mờ') || val.includes('cận') || val.includes('viễn') || val.includes('giác mạc') || val.includes('cườm')) {
    aiSuggestedDept.value = 'Mắt'
    aiConfidence.value = 95
    form.value.department = 'Mắt'
  }
  // 3. Nhi khoa
  else if (val.includes('trẻ') || val.includes('bé') || val.includes('con tôi') || val.includes('cháu nhà') || val.includes('sơ sinh') || val.includes('nhi')) {
    aiSuggestedDept.value = 'Nhi khoa'
    aiConfidence.value = 88
    form.value.department = 'Nhi khoa'
  }
  // 4. Sản phụ khoa
  else if (val.includes('thai') || val.includes('bầu') || val.includes('sinh') || val.includes('đẻ') || val.includes('phụ khoa') || val.includes('tử cung') || val.includes('buồng trứng')) {
    aiSuggestedDept.value = 'Sản phụ khoa'
    aiConfidence.value = 90
    form.value.department = 'Sản phụ khoa'
  }
  // 5. Ngoại khoa
  else if (val.includes('gãy') || val.includes('chấn thương') || val.includes('mổ') || val.includes('phẫu thuật') || val.includes('ruột thừa') || val.includes('khớp') || val.includes('xương')) {
    aiSuggestedDept.value = 'Ngoại khoa'
    aiConfidence.value = 85
    form.value.department = 'Ngoại khoa'
  }
  // 6. Nội tổng quát
  else if (val.includes('dạ dày') || val.includes('bụng') || val.includes('tiêu hóa') || val.includes('tiểu đường') || val.includes('gan') || val.includes('mật') || val.includes('ho') || val.includes('sốt')) {
    aiSuggestedDept.value = 'Nội tổng quát'
    aiConfidence.value = 80
    form.value.department = 'Nội tổng quát'
  }
  else {
    aiSuggestedDept.value = ''
    aiConfidence.value = 0
  }
})

// Submit button -> triggers confirmation modal
function triggerSubmit() {
  if (!isLoggedIn.value) {
    alert('Vui lòng đăng nhập tài khoản trước khi đặt lịch khám!')
    router.push('/dang-nhap')
    return
  }
  const f = form.value
  if (!f.fullName || !f.phone || !f.department || !f.date || !f.timeSlot) {
    alert('Vui lòng nhập đầy đủ các trường thông tin bắt buộc (*)!')
    return
  }
  showConfirmModal.value = true
}

// Temp storage for booking during payment
const tempBookingData = ref(null)

// Confirm booking -> triggers QR payment if online, or completes immediately if counter payment
function confirmBooking() {
  showConfirmModal.value = false
  
  const randomCode = 'BV-' + Math.floor(100000 + Math.random() * 900000)
  const randomSTT = Math.floor(1 + Math.random() * 30)
  
  const newBooking = {
    id: Date.now(),
    code: randomCode,
    stt: randomSTT,
    fullName: form.value.fullName,
    gender: form.value.gender,
    phone: form.value.phone,
    email: form.value.email || 'Không có',
    address: form.value.address || 'Không có',
    department: form.value.department,
    doctor: form.value.doctor || 'Bác sĩ bất kỳ',
    date: form.value.date,
    timeSlot: form.value.timeSlot,
    symptoms: form.value.symptoms || 'Khám tổng quát định kỳ',
    bookingType: form.value.bookingType,
    paymentMethod: form.value.paymentMethod,
    paymentStatus: form.value.paymentMethod === 'Online' ? 'Chưa thanh toán' : 'Chưa thanh toán',
    status: 'Đặt lịch'
  }
  
  if (form.value.paymentMethod === 'Online') {
    tempBookingData.value = newBooking
    showPaymentQRModal.value = true
  } else {
    // Save to history list directly
    bookingHistory.value.unshift(newBooking)
    localStorage.setItem('patientBookings', JSON.stringify(bookingHistory.value))
    activeBookingResult.value = newBooking
    alert('Đặt lịch thành công! Vui lòng thanh toán viện phí tại quầy tiếp đón khi đến khám.')
  }
}

function completeOnlinePayment() {
  if (!tempBookingData.value) return
  
  const completedBooking = {
    ...tempBookingData.value,
    paymentStatus: 'Đã thanh toán'
  }
  
  bookingHistory.value.unshift(completedBooking)
  localStorage.setItem('patientBookings', JSON.stringify(bookingHistory.value))
  activeBookingResult.value = completedBooking
  
  showPaymentQRModal.value = false
  tempBookingData.value = null
  alert('Xác nhận thanh toán Online thành công! Số thứ tự của bạn đã được kích hoạt.')
}

// Cancel registration in history
function cancelBooking(id) {
  const confirmCancel = confirm('Bạn có chắc chắn muốn hủy lịch hẹn khám này không?')
  if (!confirmCancel) return
  
  bookingHistory.value = bookingHistory.value.filter(b => b.id !== id)
  localStorage.setItem('patientBookings', JSON.stringify(bookingHistory.value))
  
  if (activeBookingResult.value && activeBookingResult.value.id === id) {
    activeBookingResult.value = null
  }
  
  alert('Đã hủy lịch hẹn khám thành công.')
}

function resetForm() {
  activeBookingResult.value = null
  form.value.symptoms = ''
  form.value.department = ''
  form.value.doctor = ''
  form.value.date = ''
  form.value.timeSlot = ''
  aiSuggestedDept.value = ''
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Đăng Ký Đặt Lịch Khám</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Điền các thông tin triệu chứng để hệ thống tự động gợi ý khoa và xếp chỗ khám ưu tiên cho bạn.
        </p>
      </div>
    </div>

    <div class="max-w-6xl mx-auto px-4 mt-8">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        <!-- ==================== BOOKING FORM ==================== -->
        <div class="lg:col-span-7 bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm animate-slide-in-left space-y-6">
          <div class="flex justify-between items-center border-b border-gray-100 pb-3">
            <h2 class="text-2xl font-bold text-gray-800"><i class="bi bi-calendar-plus text-primary-700 mr-2"></i>Phiếu Đăng Ký</h2>
            <span v-if="isLoggedIn" class="text-xs bg-emerald-100 text-emerald-700 font-bold px-3 py-1 rounded-full">
              Đã tự điền hồ sơ
            </span>
          </div>

          <div v-if="!isLoggedIn" class="bg-amber-50 border border-amber-200 text-amber-800 p-4 rounded-2xl flex items-center gap-3 text-base">
            <i class="bi bi-exclamation-triangle-fill text-amber-600 text-xl"></i>
            <div>
              Bạn chưa đăng nhập. Vui lòng <RouterLink to="/dang-nhap" class="font-bold underline text-primary-700">Đăng nhập</RouterLink> để có thể tiến hành đặt lịch khám.
            </div>
          </div>

          <form @submit.prevent="triggerSubmit" class="space-y-5">
            <!-- Full Name -->
            <div>
              <label for="booking-name" class="block text-lg font-semibold text-gray-700 mb-2">
                Họ và tên người bệnh <span class="text-red-500">*</span>
              </label>
              <input
                id="booking-name"
                v-model="form.fullName"
                type="text"
                required
                placeholder="Ví dụ: Nguyễn Văn Hùng"
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
              />
            </div>

            <!-- Gender & Phone -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <span class="block text-lg font-semibold text-gray-700 mb-2">Giới tính <span class="text-red-500">*</span></span>
                <div class="flex gap-3">
                  <label class="flex-1 flex items-center justify-center gap-2 p-3 border rounded-2xl cursor-pointer text-base font-semibold transition-all"
                         :class="form.gender === 'Nam' ? 'bg-primary-50 border-primary-500 text-primary-700 font-bold' : 'border-gray-200 text-gray-600'">
                    <input type="radio" value="Nam" v-model="form.gender" class="sr-only" />
                    Nam
                  </label>
                  <label class="flex-1 flex items-center justify-center gap-2 p-3 border rounded-2xl cursor-pointer text-base font-semibold transition-all"
                         :class="form.gender === 'Nữ' ? 'bg-pink-50 border-pink-500 text-pink-700 font-bold' : 'border-gray-200 text-gray-600'">
                    <input type="radio" value="Nữ" v-model="form.gender" class="sr-only" />
                    Nữ
                  </label>
                </div>
              </div>

              <div>
                <label for="booking-phone" class="block text-lg font-semibold text-gray-700 mb-2">
                  Số điện thoại <span class="text-red-500">*</span>
                </label>
                <input
                  id="booking-phone"
                  v-model="form.phone"
                  type="tel"
                  required
                  placeholder="VD: 0901234567"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
                />
              </div>
            </div>

            <!-- Address -->
            <div>
              <label for="booking-address" class="block text-lg font-semibold text-gray-700 mb-2">Địa chỉ cư trú</label>
              <input
                id="booking-address"
                v-model="form.address"
                type="text"
                placeholder="Nhập địa chỉ nhà của bạn"
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
              />
            </div>

            <!-- Booking Type & Payment Method Selection -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <span class="block text-lg font-semibold text-gray-700 mb-2">Loại hình khám bệnh</span>
                <div class="flex gap-2.5">
                  <button
                    v-for="type in (isLoggedIn ? ['Khám mới', 'Tái khám'] : ['Khám mới'])"
                    :key="type"
                    type="button"
                    @click="form.bookingType = type"
                    class="flex-1 py-3 border rounded-2xl text-base font-semibold transition-all"
                    :class="form.bookingType === type 
                      ? 'bg-emerald-50 border-emerald-500 text-emerald-700 font-bold shadow-sm' 
                      : 'border-gray-200 text-gray-500 hover:text-gray-700'"
                  >
                    {{ type }}
                  </button>
                </div>
              </div>

              <div>
                <span class="block text-lg font-semibold text-gray-700 mb-2">Hình thức thanh toán</span>
                <div class="flex gap-2.5">
                  <button
                    v-for="method in ['Tại quầy', 'Online']"
                    :key="method"
                    type="button"
                    @click="form.paymentMethod = method"
                    class="flex-1 py-3 border rounded-2xl text-base font-semibold transition-all flex items-center justify-center gap-1.5"
                    :class="form.paymentMethod === method 
                      ? 'bg-primary-50 border-primary-500 text-primary-700 font-bold shadow-sm' 
                      : 'border-gray-200 text-gray-500 hover:text-gray-700'"
                  >
                    <i :class="method === 'Online' ? 'bi bi-qr-code-scan' : 'bi bi-cash'"></i>
                    {{ method }}
                  </button>
                </div>
              </div>
            </div>

            <!-- Symptoms Input (AI auto department triggers here) -->
            <div>
              <label for="booking-symptoms" class="block text-lg font-semibold text-gray-700 mb-2">
                Triệu chứng sức khỏe / Lý do đi khám <span class="text-gray-400 text-sm">(Để AI tự chọn khoa khám hộ)</span>
              </label>
              <textarea
                id="booking-symptoms"
                v-model="form.symptoms"
                rows="3"
                placeholder="Ví dụ: Bạn cảm thấy bị đau thắt ngực, mệt mỏi, khó thở khi leo cầu thang..."
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 resize-none bg-gray-50/50"
              ></textarea>

              <!-- AI SUGGESTION ALERT BOX -->
              <div v-if="aiSuggestedDept" class="mt-3 bg-gradient-to-r from-primary-50 to-blue-100 border border-primary-200 rounded-2xl p-4 flex items-center justify-between animate-fade-in text-base text-primary-900">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-white flex items-center justify-center text-primary-700 text-xl shadow-sm">
                    🤖
                  </div>
                  <div>
                    <span class="font-bold text-sm bg-primary-700 text-white px-2 py-0.5 rounded-full mr-2">Trí tuệ AI gợi ý</span>
                    Bạn nên khám khoa: <strong class="text-lg underline">{{ aiSuggestedDept }}</strong>
                  </div>
                </div>
                <span class="text-xs text-gray-400 font-bold">Độ chính xác: {{ aiConfidence }}%</span>
              </div>
            </div>

            <!-- Department & Doctor Select -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="booking-dept" class="block text-lg font-semibold text-gray-700 mb-2">
                  Khoa khám chuyên khoa <span class="text-red-500">*</span>
                </label>
                <select
                  id="booking-dept"
                  v-model="form.department"
                  required
                  class="w-full px-4 py-3 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 appearance-none bg-no-repeat bg-right"
                  style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-position: right 16px center;"
                >
                  <option value="" disabled>-- Chọn khoa khám --</option>
                  <option v-for="d in departmentOptions" :key="d" :value="d">{{ d }}</option>
                </select>
              </div>

              <div>
                <label for="booking-doc" class="block text-lg font-semibold text-gray-700 mb-2">
                  Bác sĩ khám (Tùy chọn)
                </label>
                <select
                  id="booking-doc"
                  v-model="form.doctor"
                  class="w-full px-4 py-3 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 appearance-none bg-no-repeat bg-right"
                  style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-position: right 16px center;"
                >
                  <option value="">-- Bác sĩ bất kỳ --</option>
                  <option v-for="doc in (doctorsMap[form.department] || [])" :key="doc" :value="doc">{{ doc }}</option>
                </select>
              </div>
            </div>

            <!-- Date & Time slot -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="booking-date" class="block text-lg font-semibold text-gray-700 mb-2">
                  Chọn ngày đi khám <span class="text-red-500">*</span>
                </label>
                <input
                  id="booking-date"
                  v-model="form.date"
                  type="date"
                  required
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all text-gray-700 bg-gray-50/50"
                />
              </div>

              <div>
                <label for="booking-time" class="block text-lg font-semibold text-gray-700 mb-2">
                  Khung giờ mong muốn <span class="text-red-500">*</span>
                </label>
                <select
                  id="booking-time"
                  v-model="form.timeSlot"
                  required
                  class="w-full px-4 py-3 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 appearance-none bg-no-repeat bg-right"
                  style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-position: right 16px center;"
                >
                  <option value="" disabled>-- Chọn khung giờ --</option>
                  <option v-for="t in timeSlots" :key="t" :value="t">{{ t }}</option>
                </select>
              </div>
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg mt-4"
            >
              <i class="bi bi-calendar-check mr-2"></i>Tiến Hành Đăng Ký
            </button>
          </form>
        </div>

        <!-- ==================== BOOKING RESULTS / SIDEBAR ==================== -->
        <div class="lg:col-span-5 space-y-6">
          
          <!-- Successful booking receipt -->
          <div v-if="activeBookingResult" class="bg-gradient-to-br from-primary-600 to-primary-800 text-white p-6 md:p-8 rounded-3xl shadow-xl animate-fade-in space-y-6 relative overflow-hidden border border-primary-700">
            <!-- Decorative circle -->
            <div class="absolute -right-10 -bottom-10 w-40 h-40 bg-white/10 rounded-full"></div>
            
            <div class="text-center border-b border-white/20 pb-4">
              <span class="text-sm bg-white/20 px-3 py-1 rounded-full font-bold">PHIẾU ĐẶT LỊCH THÀNH CÔNG</span>
              <h3 class="text-3xl font-extrabold mt-3 tracking-widest">{{ activeBookingResult.code }}</h3>
              <p class="text-blue-100 text-sm mt-1">Trình mã này khi tới quầy tiếp đón</p>
            </div>

            <div class="space-y-4 text-base">
              <div class="flex justify-between border-b border-white/10 pb-2">
                <span class="text-blue-100">Bệnh nhân:</span>
                <span class="font-bold text-lg">{{ activeBookingResult.fullName }}</span>
              </div>
              <div class="flex justify-between border-b border-white/10 pb-2">
                <span class="text-blue-100">Bác sĩ:</span>
                <span class="font-bold">{{ activeBookingResult.doctor }}</span>
              </div>
              <div class="flex justify-between border-b border-white/10 pb-2">
                <span class="text-blue-100">Khung giờ:</span>
                <span class="font-bold text-yellow-300">{{ activeBookingResult.timeSlot }}</span>
              </div>
              <div class="flex justify-between border-b border-white/10 pb-2">
                <span class="text-blue-100">Ngày khám:</span>
                <span class="font-bold">{{ activeBookingResult.date }}</span>
              </div>
              <div class="flex justify-between border-b border-white/10 pb-2">
                <span class="text-blue-100">Loại khám:</span>
                <span class="font-bold text-yellow-200">{{ activeBookingResult.bookingType }}</span>
              </div>
              <div class="flex justify-between border-b border-white/10 pb-2">
                <span class="text-blue-100">Thanh toán:</span>
                <span class="font-bold">
                  {{ activeBookingResult.paymentMethod }} 
                  <span class="text-xs px-2 py-0.5 rounded-full ml-1 font-bold" 
                        :class="activeBookingResult.paymentStatus === 'Đã thanh toán' ? 'bg-emerald-500/30 text-emerald-300' : 'bg-amber-500/30 text-amber-300'">
                    ({{ activeBookingResult.paymentStatus }})
                  </span>
                </span>
              </div>

              <!-- Queue STT -->
              <div class="p-4 bg-white text-primary-900 rounded-2xl flex items-center justify-between shadow-sm">
                <div>
                  <span class="text-xs text-gray-400 font-bold uppercase">Số thứ tự khám</span>
                  <h4 class="text-4xl font-extrabold text-primary-800 mt-1">{{ activeBookingResult.stt }}</h4>
                </div>
                <div class="text-right text-xs text-gray-400">
                  <p>Hãy tới sảnh phòng khám</p>
                  <p class="font-semibold text-primary-700 mt-0.5">Khoa {{ activeBookingResult.department }}</p>
                </div>
              </div>
            </div>

            <div class="flex gap-3">
              <button @click="resetForm" class="flex-1 py-3 bg-white text-primary-800 font-bold rounded-xl hover:bg-blue-50 text-base shadow transition-colors">
                Đặt lịch mới
              </button>
              <button @click="cancelBooking(activeBookingResult.id)" class="py-3 px-4 bg-red-600 text-white font-bold rounded-xl hover:bg-red-700 text-base transition-colors flex items-center gap-1.5">
                <i class="bi bi-trash"></i> Hủy lịch
              </button>
            </div>
          </div>

          <!-- BOOKING HISTORY (Cancellation support) -->
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
            <h3 class="text-xl font-bold text-gray-800 border-b border-gray-50 pb-2"><i class="bi bi-clock-history mr-2 text-primary-700"></i>Lịch Sử Đăng Ký Gần Đây</h3>
            
            <div v-if="bookingHistory.length > 0" class="divide-y divide-gray-100 max-h-80 overflow-y-auto pr-1">
              <div v-for="b in bookingHistory" :key="b.id" class="py-4 first:pt-0 last:pb-0 flex items-center justify-between text-base">
                <div>
                  <div class="flex items-center gap-2">
                    <span class="font-bold text-gray-800">{{ b.department }}</span>
                    <span class="text-xs bg-primary-100 text-primary-800 px-2 py-0.5 rounded font-bold">{{ b.code }}</span>
                  </div>
                  <p class="text-sm text-gray-500 mt-0.5">Giờ khám: <strong class="text-gray-700">{{ b.timeSlot }}</strong> - Ngày: {{ b.date }}</p>
                  <p class="text-xs text-gray-400">
                    STT: <span class="font-bold text-primary-700 mr-2">{{ b.stt }}</span> 
                    Loại: <span class="text-gray-600 mr-2">{{ b.bookingType }}</span>
                    Thanh toán: <span class="font-semibold" :class="b.paymentStatus === 'Đã thanh toán' ? 'text-emerald-600' : 'text-amber-600'">{{ b.paymentStatus }} ({{ b.paymentMethod }})</span>
                  </p>
                </div>

                <button
                  @click="cancelBooking(b.id)"
                  class="p-2 text-red-500 hover:bg-red-50 rounded-lg text-lg transition-colors"
                  title="Hủy lịch hẹn"
                >
                  <i class="bi bi-trash"></i>
                </button>
              </div>
            </div>
            <div v-else class="text-center py-6 text-gray-400 text-base">
              Bạn chưa có lịch hẹn khám nào được đăng ký gần đây.
            </div>
          </div>

        </div>

      </div>
    </div>

    <!-- ==================== REGISTRATION CONFIRMATION OVERLAY MODAL ==================== -->
    <div v-if="showConfirmModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="text-center">
          <div class="w-16 h-16 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center mx-auto mb-3 text-3xl">
            📋
          </div>
          <h3 class="text-2xl font-extrabold text-gray-800">Xác Nhận Đăng Ký Khám</h3>
          <p class="text-base text-gray-400 mt-1">Bạn vui lòng soát lại thông tin trước khi xác nhận:</p>
        </div>

        <div class="bg-gray-50 p-5 rounded-2xl border border-gray-100 text-base space-y-3">
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Họ và tên:</span> <strong class="text-gray-800">{{ form.fullName }}</strong></p>
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Số điện thoại:</span> <strong class="text-gray-800">{{ form.phone }}</strong></p>
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Khoa khám:</span> <strong class="text-primary-800 font-bold">{{ form.department }}</strong></p>
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Khung giờ:</span> <strong class="text-gray-800">{{ form.timeSlot }}</strong></p>
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Ngày khám:</span> <strong class="text-gray-800">{{ form.date }}</strong></p>
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Loại khám:</span> <strong class="text-emerald-700">{{ form.bookingType }}</strong></p>
          <p class="flex justify-between border-b pb-1.5"><span class="text-gray-500">Thanh toán:</span> <strong class="text-blue-700">{{ form.paymentMethod }}</strong></p>
          <p class="text-sm text-gray-500 mt-2"><span class="font-bold">Lý do:</span> {{ form.symptoms || 'Khám sức khỏe tổng quát' }}</p>
        </div>

        <div class="flex gap-3">
          <button
            @click="showConfirmModal = false"
            class="flex-1 py-3.5 border border-gray-200 rounded-xl font-bold text-gray-600 hover:bg-gray-50 text-base"
          >
            Hủy / Sửa lại
          </button>
          <button
            @click="confirmBooking"
            class="flex-1 py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl text-base shadow"
          >
            Xác nhận đặt lịch
          </button>
        </div>
      </div>
    </div>

    <!-- ==================== ONLINE PAYMENT QR MODAL ==================== -->
    <div v-if="showPaymentQRModal" class="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6 border border-gray-100 text-center">
        <div>
          <span class="inline-block px-3 py-1 bg-blue-100 text-blue-800 text-xs font-bold rounded-full uppercase tracking-wider mb-2">Cổng thanh toán điện tử</span>
          <h3 class="text-2xl font-extrabold text-gray-800">Quét Mã QR Đóng Viện Phí</h3>
          <p class="text-sm text-gray-500 mt-1">Sử dụng ứng dụng Ngân hàng hoặc Ví điện tử để quét mã thanh toán</p>
        </div>

        <!-- QR Code simulation container -->
        <div class="bg-gray-50 p-6 rounded-3xl border border-gray-200/60 inline-block mx-auto relative group">
          <div class="absolute inset-0 bg-emerald-500/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-3xl flex items-center justify-center pointer-events-none">
            <span class="bg-emerald-600 text-white font-bold text-xs px-2.5 py-1 rounded-full shadow-md">Simulating QR</span>
          </div>
          <!-- Real mockup QR using open API or clean image placeholder -->
          <img 
            src="https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=HOSPITAL_SUPPORT_PAYMENT_DEMO" 
            alt="Mã QR Viện Phí" 
            class="w-44 h-44 mx-auto rounded-xl shadow-sm border border-white"
          />
          <div class="mt-3 text-center">
            <p class="text-xs text-gray-400 font-bold uppercase tracking-wider">Số tiền cần đóng</p>
            <p class="text-2xl font-black text-primary-700 mt-0.5">150.000đ</p>
          </div>
        </div>

        <div class="bg-primary-50 p-4 rounded-2xl text-left text-sm text-primary-850 space-y-1">
          <p><strong>Nội dung:</strong> Đóng tiền khám bệnh - {{ tempBookingData?.fullName }}</p>
          <p><strong>Khoa khám:</strong> Khoa {{ tempBookingData?.department }}</p>
          <p><strong>Tài khoản hưởng:</strong> Bệnh Viện Đa Khoa Trung Ương</p>
        </div>

        <div class="flex gap-3">
          <button
            @click="showPaymentQRModal = false; tempBookingData = null"
            class="flex-1 py-3.5 border border-gray-200 rounded-xl font-bold text-gray-500 hover:bg-gray-50 text-sm"
          >
            Hủy thanh toán
          </button>
          <button
            @click="completeOnlinePayment"
            class="flex-1 py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl text-sm shadow flex items-center justify-center gap-1.5"
          >
            <i class="bi bi-shield-check"></i>
            Xác nhận Đã Chuyển Khoản
          </button>
        </div>
      </div>
    </div>

  </div>
</template>
