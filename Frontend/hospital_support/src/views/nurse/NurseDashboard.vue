<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const activeMenu = ref('queue') // queue, checkin, guide, stats, profile

/* ========== PROFILE STATE ========== */
const profileData = ref({
  name: 'Nguyễn Thị Mai',
  role: 'Điều dưỡng hành chính',
  employeeId: 'NV-2024-018',
  department: 'Quầy tiếp đón — Phân luồng',
  phone: '0901 234 567',
  email: 'mai.nguyen@hospital.vn',
  address: '121/4a Trung Mỹ Tây, Quận 12, TP.HCM',
  dob: '1995-03-15',
  gender: 'Nữ',
  startDate: '2020-06-01',
  qualification: 'Cử nhân Điều dưỡng — Đại học Y Dược TP.HCM',
  avatar: ''
})
const isEditingProfile = ref(false)

function loadProfile() {
  const saved = localStorage.getItem('nurseProfile')
  if (saved) profileData.value = { ...profileData.value, ...JSON.parse(saved) }
  const user = localStorage.getItem('currentUser')
  if (user) {
    const u = JSON.parse(user)
    if (u.name) profileData.value.name = u.name
    if (u.email) profileData.value.email = u.email
    if (u.phone) profileData.value.phone = u.phone
  }
}
function saveProfile() {
  localStorage.setItem('nurseProfile', JSON.stringify(profileData.value))
  isEditingProfile.value = false
  alert('✅ Đã lưu thông tin cá nhân thành công!')
}
const bookings = ref([])
const lastCalledLane = ref(localStorage.getItem('lastCalledLane') || 'lane2')
let pollTimer = null

/* ========== COMPUTED QUEUES ========== */
const patientInRoom = computed(() => bookings.value.find(b => b.status === 'Đang khám' || b.status === 'Đang đọc KQ'))
const lane1Queue = computed(() => bookings.value.filter(b => b.status === 'Làn 1').sort((a, b) => a.stt - b.stt))
const lane2Queue = computed(() => bookings.value.filter(b => b.status === 'Làn 2').sort((a, b) => a.stt - b.stt))
const waitingPatients = computed(() => bookings.value.filter(b => b.status === 'Chờ khám').sort((a, b) => a.stt - b.stt))
const atLabPatients = computed(() => bookings.value.filter(b => b.status === 'Đi xét nghiệm'))
const donePatients = computed(() => bookings.value.filter(b => b.status === 'Đã khám'))

const stats = computed(() => ({
  total: bookings.value.filter(b => b.status !== 'Đã hủy').length,
  lane1: lane1Queue.value.length,
  lane2: lane2Queue.value.length,
  inRoom: patientInRoom.value ? 1 : 0,
  done: donePatients.value.length,
  waiting: waitingPatients.value.length,
  atLab: atLabPatients.value.length
}))

/* ========== CHECKIN TAB STATE ========== */
const activeFilter = ref('Tất cả')
const searchPhone = ref('')
const filteredCheckinList = computed(() => {
  return bookings.value.filter(item => {
    if (activeFilter.value === 'Đã thanh toán' && item.paymentStatus !== 'Đã thanh toán') return false
    if (activeFilter.value === 'Chưa thanh toán' && item.paymentStatus !== 'Chưa thanh toán') return false
    if (searchPhone.value.trim() && !item.phone.includes(searchPhone.value.trim())) return false
    return item.status !== 'Đã hủy'
  })
})

/* ========== Q&A GUIDE ========== */
const qaList = [
  { q: 'Bảo hiểm y tế (BHYT) đồng chi trả như thế nào?', a: 'Khám đúng tuyến hỗ trợ 80% - 100%, trái tuyến hỗ trợ 40% chi phí điều trị nội trú.' },
  { q: 'Làm thế nào để lấy số khám ưu tiên?', a: 'Bệnh nhân đặt lịch trực tuyến và thanh toán online trước sẽ được cấp STT ưu tiên.' },
  { q: 'Phòng xét nghiệm ở đâu?', a: 'Tầng trệt, phòng P.104 (X-Quang & Siêu âm) và P.105 (Xét nghiệm máu).' },
  { q: 'Bệnh nhân đi xét nghiệm xong quay lại có cần lấy số mới không?', a: 'Không. Bệnh nhân giữ nguyên STT cũ, quay lại gặp điều dưỡng trước phòng khám để được xếp vào Làn 2 (ưu tiên đọc kết quả nhanh).' }
]

/* ========== LIFECYCLE ========== */
onMounted(() => {
  loadBookings()
  loadProfile()
  pollTimer = setInterval(() => { loadBookings() }, 2000)
})
onUnmounted(() => { if (pollTimer) clearInterval(pollTimer) })

/* ========== DATA LOADING ========== */
function loadBookings() {
  const data = localStorage.getItem('patientBookings')
  if (data && JSON.parse(data).length >= 4) {
    bookings.value = JSON.parse(data)
    checkLabResults()
  } else {
    seedMockData()
  }
}

function seedMockData() {
  const today = new Date().toISOString().split('T')[0]
  const mock = [
    { id: 2001, code: 'BV-30001', stt: 1, fullName: 'Nguyễn Minh Tuấn', gender: 'Nam', phone: '0901234567', email: 'tuan@email.com', address: 'Quận 1, TP.HCM', department: 'Tim mạch', doctor: 'PGS.TS Nguyễn Văn An', date: today, timeSlot: '08:00 - 08:30', symptoms: 'Đau tức ngực trái khi gắng sức, khó thở leo cầu thang', bookingType: 'Khám mới', paymentMethod: 'Online', paymentStatus: 'Đã thanh toán', status: 'Làn 1', room: 'Phòng 101' },
    { id: 2002, code: 'BV-30002', stt: 2, fullName: 'Phạm Thị Hoa', gender: 'Nữ', phone: '0912345678', email: 'hoa@email.com', address: 'Bình Thạnh, TP.HCM', department: 'Nội tổng quát', doctor: 'ThS.BS Trần Văn Bình', date: today, timeSlot: '08:30 - 09:00', symptoms: 'Đau đầu dữ dội 3 ngày, buồn nôn, chóng mặt liên tục', bookingType: 'Khám mới', paymentMethod: 'Tại quầy', paymentStatus: 'Đã thanh toán', status: 'Làn 1', room: 'Phòng 201' },
    { id: 2003, code: 'BV-30003', stt: 3, fullName: 'Võ Văn Đức', gender: 'Nam', phone: '0923456789', email: 'duc@email.com', address: 'Quận 12, TP.HCM', department: 'Tim mạch', doctor: 'PGS.TS Nguyễn Văn An', date: today, timeSlot: '09:00 - 09:30', symptoms: 'Tái khám tim mạch — đã có kết quả xét nghiệm máu và điện tim', bookingType: 'Tái khám', paymentMethod: 'Online', paymentStatus: 'Đã thanh toán', status: 'Làn 2', room: 'Phòng 101', testResults: { bloodTest: 'Cholesterol tổng: 6.2 mmol/l (Cao). LDL: 4.1 mmol/l (Cao). HDL: 1.0 mmol/l (Thấp). Triglyceride: 2.8 mmol/l (Cao nhẹ).', xray: 'Điện tâm đồ: Nhịp xoang đều 78 lần/phút. Trục QRS bình thường. Không ST chênh.' } },
    { id: 2004, code: 'BV-30004', stt: 4, fullName: 'Trần Văn Hoàng', gender: 'Nam', phone: '0934567890', email: 'hoang@email.com', address: 'Hóc Môn, TP.HCM', department: 'Tim mạch', doctor: 'PGS.TS Nguyễn Văn An', date: today, timeSlot: '09:30 - 10:00', symptoms: 'Nhức mỏi toàn thân, sốt nhẹ 37.5°C kéo dài 5 ngày', bookingType: 'Khám mới', paymentMethod: 'Tại quầy', paymentStatus: 'Đã thanh toán', status: 'Chờ khám', room: 'Phòng 101' },
    { id: 2005, code: 'BV-30005', stt: 5, fullName: 'Lê Thị Thu', gender: 'Nữ', phone: '0945678901', email: 'thu@email.com', address: 'Thủ Đức, TP.HCM', department: 'Mắt', doctor: 'TS.BS Lê Thị Lan', date: today, timeSlot: '10:00 - 10:30', symptoms: 'Mắt phải nhìn mờ dần, mỏi mắt nhiều khi đọc sách', bookingType: 'Khám mới', paymentMethod: 'Online', paymentStatus: 'Đã thanh toán', status: 'Chờ khám', room: 'Phòng 204' },
    { id: 2006, code: 'BV-30006', stt: 6, fullName: 'Trần Thị Lan', gender: 'Nữ', phone: '0956789012', email: 'lan@email.com', address: 'Gò Vấp, TP.HCM', department: 'Nội tổng quát', doctor: 'ThS.BS Trần Văn Bình', date: today, timeSlot: '08:00 - 08:30', symptoms: 'Đau bụng âm ỉ vùng thượng vị, ợ chua, chướng bụng sau ăn', bookingType: 'Khám mới', paymentMethod: 'Tại quầy', paymentStatus: 'Đã thanh toán', status: 'Đi xét nghiệm', room: 'Phòng 201', testOrdered: ['bloodTest', 'ultrasound'], testSentAt: Date.now() - 3000 }
  ]
  bookings.value = mock
  localStorage.setItem('patientBookings', JSON.stringify(mock))
}

/* ========== AUTO-CHECK LAB RESULTS ========== */
function checkLabResults() {
  let changed = false
  const list = [...bookings.value]
  list.forEach((b, idx) => {
    if (b.status === 'Đi xét nghiệm' && b.testSentAt && (Date.now() - b.testSentAt > 15000)) {
      list[idx].status = 'Làn 2'
      list[idx].testResults = generateMockResults(b.testOrdered || ['bloodTest'])
      changed = true
    }
  })
  if (changed) {
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
  }
}

function generateMockResults(tests) {
  const results = {}
  if (tests.includes('bloodTest')) results.bloodTest = 'Hồng cầu: 4.5 T/l (BT). Bạch cầu: 7.2 G/l (BT). Glucose: 5.6 mmol/l (BT). Creatinin: 88 µmol/l (BT).'
  if (tests.includes('xray')) results.xray = 'X-Quang tim phổi thẳng: Không tổn thương nhu mô phổi. Bóng tim không to. Vòm hoành đều.'
  if (tests.includes('ultrasound')) results.ultrasound = 'Siêu âm ổ bụng: Gan, mật, tụy, lách bình thường. Dạ dày tăng co bóp nhẹ. Không dịch ổ bụng.'
  if (tests.includes('ctscan')) results.ctscan = 'CT-Scanner sọ não: Không ổ xuất huyết hay u não. Hệ thống não thất bình thường.'
  return results
}

/* ========== MANUAL LAB RESULT CONFIRM ========== */
function confirmLabResults(id) {
  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === id)
  if (idx !== -1) {
    list[idx].status = 'Làn 2'
    list[idx].testResults = generateMockResults(list[idx].testOrdered || ['bloodTest'])
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
    alert(`Kết quả xét nghiệm của ${list[idx].fullName} đã sẵn sàng! Đã chuyển sang Làn 2.`)
  }
}

/* ========== CLASSIFY PATIENT TO LANE 1 ========== */
function classifyToLane1(id) {
  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === id)
  if (idx !== -1) {
    list[idx].status = 'Làn 1'
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
  }
}

/* ========== CALL NEXT PATIENT (ALTERNATING 1-1) ========== */
function callNextPatient() {
  if (patientInRoom.value) {
    alert(`⚠️ Bệnh nhân ${patientInRoom.value.fullName} vẫn đang trong phòng bác sĩ! Chờ bác sĩ hoàn thành.`)
    return
  }
  const l1 = lane1Queue.value
  const l2 = lane2Queue.value
  if (l1.length === 0 && l2.length === 0) {
    alert('Không còn bệnh nhân nào trong hàng đợi 2 làn!')
    return
  }

  let target = null
  if (lastCalledLane.value === 'lane1') {
    target = l2.length > 0 ? l2[0] : l1[0]
  } else {
    target = l1.length > 0 ? l1[0] : l2[0]
  }

  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === target.id)
  if (idx !== -1) {
    const isLane2 = target.status === 'Làn 2'
    list[idx].status = isLane2 ? 'Đang đọc KQ' : 'Đang khám'
    lastCalledLane.value = isLane2 ? 'lane2' : 'lane1'
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
    localStorage.setItem('lastCalledLane', lastCalledLane.value)
    const laneLabel = isLane2 ? 'Làn 2 — Đọc kết quả' : 'Làn 1 — Khám mới'
    alert(`🔔 Mời bệnh nhân ${target.fullName} (STT: ${target.stt}) vào phòng bác sĩ!\n[${laneLabel}]`)
  }
}

/* ========== CHECKIN TAB FUNCTIONS ========== */
function collectPayment(id) {
  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === id)
  if (idx !== -1) {
    list[idx].paymentStatus = 'Đã thanh toán'
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
    alert(`✅ Đã thu viện phí cho: ${list[idx].fullName}.`)
  }
}

function confirmArrival(id) {
  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === id)
  if (idx !== -1) {
    if (list[idx].paymentStatus !== 'Đã thanh toán') {
      alert('Vui lòng thu phí trước khi xác nhận!')
      return
    }
    list[idx].status = 'Chờ khám'
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
    alert(`🎫 In phiếu thành công! BN ${list[idx].fullName} - STT: ${list[idx].stt} chờ phân loại.`)
  }
}

function cancelAppointment(id) {
  if (confirm('Bạn có chắc chắn muốn hủy lịch khám này?')) {
    const list = [...bookings.value]
    const idx = list.findIndex(b => b.id === id)
    if (idx !== -1) {
      list[idx].status = 'Đã hủy'
      bookings.value = list
      localStorage.setItem('patientBookings', JSON.stringify(list))
    }
  }
}

function getLaneLabel(lane) {
  return lane === 'lane1' ? 'Làn 1 (Khám mới)' : 'Làn 2 (Có kết quả)'
}
</script>

<template>
  <div class="min-h-[calc(100vh-112px)] bg-gray-50 flex">
    <!-- ===== SIDEBAR ===== -->
    <aside class="w-64 bg-white border-r border-gray-150 flex flex-col justify-between p-4 flex-shrink-0">
      <div class="space-y-6">
        <div class="flex items-center gap-3 p-2 pb-4 border-b border-gray-100">
          <img src="https://i.ibb.co/4gXZ9qs4/z7939748517288-6fea6e7a88629fd0c0ef6ce9326fd25f.png" alt="Logo" class="w-10 h-10 object-contain rounded-lg bg-gray-50 p-0.5 shadow-sm" />
          <div>
            <h4 class="text-sm font-bold text-gray-800">Điều Dưỡng Phân Luồng</h4>
            <span class="text-xs text-indigo-600 font-semibold">ĐD. Nguyễn Thị Mai</span>
          </div>
        </div>
        <nav class="space-y-1">
          <button @click="activeMenu = 'queue'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'queue' ? 'bg-indigo-50 text-indigo-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-signpost-split text-lg"></i> Phân luồng & Gọi khám
          </button>
          <button @click="activeMenu = 'checkin'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'checkin' ? 'bg-indigo-50 text-indigo-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-person-check text-lg"></i> Tiếp nhận tại quầy
          </button>
          <button @click="activeMenu = 'guide'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'guide' ? 'bg-indigo-50 text-indigo-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-question-circle text-lg"></i> Hướng dẫn BN
          </button>
          <button @click="activeMenu = 'stats'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'stats' ? 'bg-indigo-50 text-indigo-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-graph-up-arrow text-lg"></i> Thống kê hoạt động
          </button>
          <button @click="activeMenu = 'profile'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'profile' ? 'bg-indigo-50 text-indigo-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-person-circle text-lg"></i> Thông tin cá nhân
          </button>
        </nav>
      </div>
      <div class="p-2 border-t text-xs text-gray-400 text-center">Hospital Nurse v3.0</div>
    </aside>

    <!-- ===== MAIN CONTENT ===== -->
    <main class="flex-1 p-6 md:p-8 overflow-y-auto">

      <!-- Stats bar -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div><span class="text-[10px] text-gray-400 font-bold uppercase">Làn 1 — Khám mới</span><h4 class="text-2xl font-black text-blue-700 mt-0.5">{{ stats.lane1 }}</h4></div>
          <span class="text-2xl bg-blue-50 p-2.5 rounded-xl">🆕</span>
        </div>
        <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div><span class="text-[10px] text-gray-400 font-bold uppercase">Làn 2 — Có kết quả</span><h4 class="text-2xl font-black text-emerald-600 mt-0.5">{{ stats.lane2 }}</h4></div>
          <span class="text-2xl bg-emerald-50 p-2.5 rounded-xl">🔄</span>
        </div>
        <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div><span class="text-[10px] text-gray-400 font-bold uppercase">Đang trong phòng BS</span><h4 class="text-2xl font-black text-purple-700 mt-0.5">{{ stats.inRoom }}</h4></div>
          <span class="text-2xl bg-purple-50 p-2.5 rounded-xl">🩺</span>
        </div>
        <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div><span class="text-[10px] text-gray-400 font-bold uppercase">Hoàn thành hôm nay</span><h4 class="text-2xl font-black text-gray-600 mt-0.5">{{ stats.done }}</h4></div>
          <span class="text-2xl bg-gray-100 p-2.5 rounded-xl">✅</span>
        </div>
      </div>

      <!-- ==================== MENU 1: PHÂN LUỒNG & GỌI KHÁM ==================== -->
      <div v-if="activeMenu === 'queue'" class="space-y-6 animate-fade-in">

        <!-- CURRENT PATIENT IN ROOM -->
        <div class="bg-gradient-to-r from-purple-600 to-indigo-700 rounded-3xl p-6 text-white shadow-lg">
          <div class="flex items-center justify-between">
            <div>
              <span class="text-xs font-bold uppercase opacity-80 tracking-wider">🩺 Đang trong phòng Bác sĩ</span>
              <div v-if="patientInRoom" class="mt-2">
                <h3 class="text-2xl font-black">{{ patientInRoom.fullName }} <span class="text-base font-medium opacity-80">(STT: {{ patientInRoom.stt }})</span></h3>
                <p class="text-sm opacity-90 mt-1">
                  <span class="bg-white/20 px-2 py-0.5 rounded-full text-xs font-bold mr-2">{{ patientInRoom.status === 'Đang đọc KQ' ? '🔄 Đọc kết quả XN' : '🆕 Khám lần đầu' }}</span>
                  {{ patientInRoom.department }} — {{ patientInRoom.doctor }}
                </p>
                <p class="text-xs opacity-70 mt-1 italic">"{{ patientInRoom.symptoms }}"</p>
              </div>
              <div v-else class="mt-2">
                <h3 class="text-xl font-bold opacity-60">Phòng bác sĩ đang trống — Sẵn sàng gọi bệnh nhân</h3>
              </div>
            </div>
            <button
              @click="callNextPatient"
              :disabled="!!patientInRoom"
              class="flex-shrink-0 py-4 px-6 rounded-2xl font-black text-sm transition-all flex items-center gap-2"
              :class="patientInRoom
                ? 'bg-white/10 text-white/40 cursor-not-allowed'
                : 'bg-white text-purple-700 hover:bg-purple-50 shadow-lg hover:scale-105 active:scale-95'"
            >
              <i class="bi bi-megaphone-fill text-lg"></i>
              GỌI BỆNH NHÂN TIẾP
              <span v-if="!patientInRoom" class="text-[10px] bg-purple-100 text-purple-700 px-2 py-0.5 rounded-full ml-1">
                → {{ getLaneLabel(lastCalledLane === 'lane1' ? 'lane2' : 'lane1') }}
              </span>
            </button>
          </div>
        </div>

        <!-- TWO LANES -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- LANE 1: KHÁM MỚI -->
          <div class="bg-white rounded-3xl border-2 border-blue-200 shadow-sm overflow-hidden">
            <div class="bg-blue-50 px-6 py-4 border-b border-blue-100 flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span class="w-8 h-8 rounded-xl bg-blue-600 text-white flex items-center justify-center font-black text-sm">1</span>
                <div>
                  <h3 class="text-base font-black text-blue-800">Làn 1 — Khám Mới</h3>
                  <p class="text-[10px] text-blue-500 font-semibold">Bệnh nhân chưa có kết quả, cần khám lâm sàng đầy đủ</p>
                </div>
              </div>
              <span class="text-2xl font-black text-blue-700">{{ lane1Queue.length }}</span>
            </div>
            <div class="p-4 space-y-3 max-h-80 overflow-y-auto">
              <div v-for="(p, i) in lane1Queue" :key="p.id" class="p-4 bg-blue-50/50 rounded-2xl border border-blue-100 flex items-center justify-between gap-3 hover:border-blue-300 transition-all">
                <div class="flex items-center gap-3">
                  <span class="w-10 h-10 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center font-black text-lg flex-shrink-0">{{ i + 1 }}</span>
                  <div>
                    <h4 class="text-sm font-bold text-gray-800">{{ p.fullName }}</h4>
                    <p class="text-[11px] text-gray-500">{{ p.department }} • STT: {{ p.stt }}</p>
                    <p class="text-[10px] text-gray-400 italic mt-0.5 line-clamp-1">"{{ p.symptoms }}"</p>
                  </div>
                </div>
                <span class="text-xs bg-blue-100 text-blue-700 font-bold px-2.5 py-1 rounded-lg flex-shrink-0">Chờ gọi</span>
              </div>
              <div v-if="lane1Queue.length === 0" class="text-center py-8 text-gray-300 text-sm">
                <i class="bi bi-inbox text-3xl block mb-2"></i>
                Không có bệnh nhân khám mới
              </div>
            </div>
          </div>

          <!-- LANE 2: CÓ KẾT QUẢ -->
          <div class="bg-white rounded-3xl border-2 border-emerald-200 shadow-sm overflow-hidden">
            <div class="bg-emerald-50 px-6 py-4 border-b border-emerald-100 flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span class="w-8 h-8 rounded-xl bg-emerald-600 text-white flex items-center justify-center font-black text-sm">2</span>
                <div>
                  <h3 class="text-base font-black text-emerald-800">Làn 2 — Có Kết Quả</h3>
                  <p class="text-[10px] text-emerald-500 font-semibold">Quay lại từ phòng XN, chỉ cần BS đọc KQ + kê đơn (nhanh)</p>
                </div>
              </div>
              <span class="text-2xl font-black text-emerald-700">{{ lane2Queue.length }}</span>
            </div>
            <div class="p-4 space-y-3 max-h-80 overflow-y-auto">
              <div v-for="(p, i) in lane2Queue" :key="p.id" class="p-4 bg-emerald-50/50 rounded-2xl border border-emerald-100 flex items-center justify-between gap-3 hover:border-emerald-300 transition-all">
                <div class="flex items-center gap-3">
                  <span class="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-black text-lg flex-shrink-0">{{ i + 1 }}</span>
                  <div>
                    <h4 class="text-sm font-bold text-gray-800">{{ p.fullName }}</h4>
                    <p class="text-[11px] text-gray-500">{{ p.department }} • STT: {{ p.stt }}</p>
                    <p class="text-[10px] text-emerald-600 font-semibold mt-0.5"><i class="bi bi-file-earmark-medical mr-1"></i>Đã có kết quả XN</p>
                  </div>
                </div>
                <span class="text-xs bg-emerald-100 text-emerald-700 font-bold px-2.5 py-1 rounded-lg flex-shrink-0">Ưu tiên</span>
              </div>
              <div v-if="lane2Queue.length === 0" class="text-center py-8 text-gray-300 text-sm">
                <i class="bi bi-inbox text-3xl block mb-2"></i>
                Chưa có BN quay lại từ phòng XN
              </div>
            </div>
          </div>
        </div>

        <!-- WAITING + AT LAB SECTIONS -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- WAITING FOR CLASSIFICATION -->
          <div class="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
            <div class="bg-gray-50 px-6 py-3 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-sm font-bold text-gray-700"><i class="bi bi-hourglass-split text-amber-500 mr-1.5"></i>Chờ phân loại (từ quầy tiếp đón)</h3>
              <span class="text-sm font-black text-amber-600">{{ waitingPatients.length }}</span>
            </div>
            <div class="p-4 space-y-2">
              <div v-for="p in waitingPatients" :key="p.id" class="p-3 bg-amber-50/50 rounded-xl border border-amber-100 flex items-center justify-between gap-3">
                <div>
                  <h4 class="text-xs font-bold text-gray-800">{{ p.fullName }} <span class="text-gray-400 font-normal">— STT {{ p.stt }}</span></h4>
                  <p class="text-[10px] text-gray-400">{{ p.department }} • {{ p.symptoms.substring(0, 50) }}...</p>
                </div>
                <button @click="classifyToLane1(p.id)" class="py-1.5 px-3 bg-blue-600 hover:bg-blue-700 text-white text-[11px] font-bold rounded-lg transition-all flex-shrink-0 flex items-center gap-1">
                  <i class="bi bi-arrow-right-circle"></i> Vào Làn 1
                </button>
              </div>
              <div v-if="waitingPatients.length === 0" class="text-center py-4 text-gray-300 text-xs">Tất cả BN đã được phân loại</div>
            </div>
          </div>

          <!-- AT LAB -->
          <div class="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
            <div class="bg-gray-50 px-6 py-3 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-sm font-bold text-gray-700"><i class="bi bi-droplet-half text-purple-500 mr-1.5"></i>Đang đi xét nghiệm</h3>
              <span class="text-sm font-black text-purple-600">{{ atLabPatients.length }}</span>
            </div>
            <div class="p-4 space-y-2">
              <div v-for="p in atLabPatients" :key="p.id" class="p-3 bg-purple-50/30 rounded-xl border border-purple-100 flex items-center justify-between gap-3">
                <div>
                  <h4 class="text-xs font-bold text-gray-800">{{ p.fullName }} <span class="text-gray-400 font-normal">— STT {{ p.stt }}</span></h4>
                  <p class="text-[10px] text-gray-400">{{ p.department }} • Đang chờ kết quả...</p>
                </div>
                <button @click="confirmLabResults(p.id)" class="py-1.5 px-3 bg-emerald-600 hover:bg-emerald-700 text-white text-[11px] font-bold rounded-lg transition-all flex-shrink-0 flex items-center gap-1">
                  <i class="bi bi-check-circle"></i> Có KQ → Làn 2
                </button>
              </div>
              <div v-if="atLabPatients.length === 0" class="text-center py-4 text-gray-300 text-xs">Không có BN đang xét nghiệm</div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 2: TIẾP NHẬN TẠI QUẦY ==================== -->
      <div v-else-if="activeMenu === 'checkin'" class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden animate-fade-in">
        <div class="p-6 border-b border-gray-100 flex flex-col md:flex-row items-center justify-between gap-4 bg-gray-50/50">
          <div class="flex flex-wrap gap-2">
            <button v-for="filter in ['Tất cả', 'Đã thanh toán', 'Chưa thanh toán']" :key="filter" @click="activeFilter = filter" class="px-4 py-2 text-xs font-bold rounded-xl transition-all" :class="activeFilter === filter ? 'bg-indigo-700 text-white shadow' : 'bg-white border text-gray-500 hover:text-gray-700'">{{ filter }}</button>
          </div>
          <div class="relative w-full md:w-80">
            <input v-model="searchPhone" type="text" placeholder="Tìm bằng số điện thoại..." class="w-full pl-10 pr-3 py-2 text-xs rounded-xl border border-gray-250 focus:outline-none" />
            <i class="bi bi-search absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
        </div>
        <div class="divide-y divide-gray-100">
          <div v-for="b in filteredCheckinList" :key="b.id" class="p-5 flex flex-col lg:flex-row items-start lg:items-center justify-between gap-4 hover:bg-gray-50/30 transition-all">
            <div class="space-y-1.5">
              <div class="flex items-center gap-2 flex-wrap">
                <h3 class="text-sm font-bold text-gray-800">{{ b.fullName }}</h3>
                <span class="text-[10px] bg-gray-100 text-gray-500 font-bold px-2 py-0.5 rounded-full">{{ b.gender }}</span>
                <span class="text-[10px] bg-indigo-50 text-indigo-700 font-bold px-2 py-0.5 rounded">{{ b.code }}</span>
                <span class="text-[10px] font-bold px-2 py-0.5 rounded" :class="{
                  'bg-blue-50 text-blue-700': b.status === 'Làn 1',
                  'bg-emerald-50 text-emerald-700': b.status === 'Làn 2',
                  'bg-purple-50 text-purple-700': b.status === 'Đang khám' || b.status === 'Đang đọc KQ',
                  'bg-amber-50 text-amber-700': b.status === 'Chờ khám',
                  'bg-gray-50 text-gray-500': b.status === 'Đã khám'
                }">{{ b.status }}</span>
              </div>
              <p class="text-[11px] text-gray-500">SĐT: <strong>{{ b.phone }}</strong> — {{ b.department }} — {{ b.doctor }}</p>
              <div class="flex gap-2 text-[11px]">
                <span class="font-bold" :class="b.paymentStatus === 'Đã thanh toán' ? 'text-emerald-600' : 'text-amber-600'">{{ b.paymentStatus }}</span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <div class="bg-indigo-50 border border-indigo-100 px-3 py-1.5 rounded-xl text-center">
                <span class="text-[9px] text-indigo-400 font-bold uppercase block leading-none">STT</span>
                <span class="text-xl font-black text-indigo-800 leading-none mt-1 block">{{ b.stt }}</span>
              </div>
              <button v-if="b.paymentStatus !== 'Đã thanh toán'" @click="collectPayment(b.id)" class="py-2 px-3 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow transition-all">Thu phí</button>
              <button v-if="b.status !== 'Đã hủy' && b.status !== 'Đã khám'" @click="cancelAppointment(b.id)" class="p-2 bg-rose-50 hover:bg-rose-100 text-rose-600 rounded-xl transition-colors" title="Hủy lịch"><i class="bi bi-trash"></i></button>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 3: HƯỚNG DẪN BN ==================== -->
      <div v-else-if="activeMenu === 'guide'" class="grid grid-cols-1 md:grid-cols-2 gap-8 animate-fade-in">
        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-4">
          <h3 class="text-lg font-bold text-indigo-800 border-b pb-2"><i class="bi bi-signpost-split-fill mr-1.5"></i>Quy trình phân luồng 2 làn đường</h3>
          <ul class="space-y-3.5 text-sm text-gray-700">
            <li class="flex items-start gap-2.5"><span class="w-5 h-5 rounded-full bg-blue-100 text-blue-700 font-bold text-xs flex items-center justify-center flex-shrink-0">1</span><span><strong>Làn 1 — Khám mới:</strong> BN chưa có kết quả gì, cần bác sĩ hỏi bệnh, nghe tim phổi, viết chỉ định (tốn thời gian nhất).</span></li>
            <li class="flex items-start gap-2.5"><span class="w-5 h-5 rounded-full bg-emerald-100 text-emerald-700 font-bold text-xs flex items-center justify-center flex-shrink-0">2</span><span><strong>Làn 2 — Có kết quả:</strong> BN đã xét nghiệm xong, chỉ cần BS đọc KQ và kê đơn (rất nhanh, 1-2 phút).</span></li>
            <li class="flex items-start gap-2.5"><span class="w-5 h-5 rounded-full bg-purple-100 text-purple-700 font-bold text-xs flex items-center justify-center flex-shrink-0">3</span><span><strong>Quy tắc so le:</strong> Gọi 1 người Làn 1 → 1 người Làn 2 → luân phiên. Nếu 1 làn hết → gọi từ làn còn lại.</span></li>
            <li class="flex items-start gap-2.5"><span class="w-5 h-5 rounded-full bg-amber-100 text-amber-700 font-bold text-xs flex items-center justify-center flex-shrink-0">4</span><span><strong>BN quay lại:</strong> Giữ nguyên STT cũ, được ưu tiên xếp vào Làn 2 để đọc kết quả nhanh.</span></li>
          </ul>
        </div>
        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-4">
          <h3 class="text-lg font-bold text-indigo-800 border-b pb-2"><i class="bi bi-chat-dots-fill mr-1.5"></i>Giải đáp thắc mắc thường gặp</h3>
          <div class="space-y-3">
            <div v-for="(item, idx) in qaList" :key="idx" class="p-3.5 bg-gray-50 rounded-2xl border text-sm space-y-1">
              <p class="font-bold text-gray-800">Q: {{ item.q }}</p>
              <p class="text-gray-600 leading-relaxed">A: {{ item.a }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 4: THỐNG KÊ ==================== -->
      <div v-else-if="activeMenu === 'stats'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Báo cáo thống kê lượt khám hôm nay</h2>
          <p class="text-sm text-gray-400">Biểu đồ tổng quan phân luồng và lưu lượng tiếp nhận.</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
            <h3 class="text-lg font-bold text-gray-850 border-b pb-2">Tổng kết phân luồng</h3>
            <div class="space-y-3 text-sm text-gray-700 pt-1">
              <div class="flex justify-between border-b pb-2"><span>Tổng bệnh nhân hôm nay:</span><span class="font-bold text-gray-800">{{ stats.total }} ca</span></div>
              <div class="flex justify-between border-b pb-2"><span>Đang chờ phân loại:</span><span class="font-bold text-amber-600">{{ stats.waiting }} ca</span></div>
              <div class="flex justify-between border-b pb-2"><span>Làn 1 (Khám mới):</span><span class="font-bold text-blue-700">{{ stats.lane1 }} ca</span></div>
              <div class="flex justify-between border-b pb-2"><span>Làn 2 (Có kết quả):</span><span class="font-bold text-emerald-600">{{ stats.lane2 }} ca</span></div>
              <div class="flex justify-between border-b pb-2"><span>Đang xét nghiệm:</span><span class="font-bold text-purple-600">{{ stats.atLab }} ca</span></div>
              <div class="flex justify-between pb-2"><span>Đã khám xong:</span><span class="font-bold text-gray-800">{{ stats.done }} ca</span></div>
            </div>
          </div>
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
            <h3 class="text-lg font-bold text-gray-850 mb-4">Hiệu suất phân luồng</h3>
            <div class="space-y-4">
              <div class="space-y-1">
                <div class="flex justify-between text-sm"><span class="font-bold text-blue-700">Làn 1 — Khám mới</span><span class="text-gray-400">{{ stats.lane1 }} / {{ stats.total }}</span></div>
                <div class="w-full bg-gray-100 h-3 rounded-full overflow-hidden"><div class="bg-blue-500 h-full rounded-full transition-all duration-700" :style="{ width: stats.total ? `${(stats.lane1 / stats.total) * 100}%` : '0%' }"></div></div>
              </div>
              <div class="space-y-1">
                <div class="flex justify-between text-sm"><span class="font-bold text-emerald-600">Làn 2 — Có kết quả</span><span class="text-gray-400">{{ stats.lane2 }} / {{ stats.total }}</span></div>
                <div class="w-full bg-gray-100 h-3 rounded-full overflow-hidden"><div class="bg-emerald-500 h-full rounded-full transition-all duration-700" :style="{ width: stats.total ? `${(stats.lane2 / stats.total) * 100}%` : '0%' }"></div></div>
              </div>
              <div class="space-y-1">
                <div class="flex justify-between text-sm"><span class="font-bold text-gray-600">Hoàn thành</span><span class="text-gray-400">{{ stats.done }} / {{ stats.total }}</span></div>
                <div class="w-full bg-gray-100 h-3 rounded-full overflow-hidden"><div class="bg-gray-500 h-full rounded-full transition-all duration-700" :style="{ width: stats.total ? `${(stats.done / stats.total) * 100}%` : '0%' }"></div></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 5: THÔNG TIN CÁ NHÂN ==================== -->
      <div v-else-if="activeMenu === 'profile'" class="space-y-6 animate-fade-in">
        <div class="flex items-end justify-between">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Thông tin cá nhân</h2>
            <p class="text-sm text-gray-400">Hồ sơ nhân sự và thông tin liên hệ của bạn.</p>
          </div>
          <button v-if="!isEditingProfile" @click="isEditingProfile = true" class="py-2.5 px-5 bg-indigo-700 hover:bg-indigo-800 text-white text-sm font-bold rounded-xl shadow transition-all flex items-center gap-2">
            <i class="bi bi-pencil-square"></i> Chỉnh sửa
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Avatar & Summary Card -->
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm text-center">
            <div class="w-24 h-24 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 mx-auto flex items-center justify-center text-white text-3xl font-black shadow-lg">
              {{ profileData.name.charAt(0) }}
            </div>
            <h3 class="text-lg font-black text-gray-800 mt-4">{{ profileData.name }}</h3>
            <span class="text-xs bg-indigo-100 text-indigo-700 font-bold px-3 py-1 rounded-full">{{ profileData.role }}</span>
            <div class="mt-4 space-y-2 text-sm text-gray-500">
              <p><i class="bi bi-hash text-indigo-400 mr-1.5"></i>{{ profileData.employeeId }}</p>
              <p><i class="bi bi-building text-indigo-400 mr-1.5"></i>{{ profileData.department }}</p>
              <p><i class="bi bi-mortarboard text-indigo-400 mr-1.5"></i>{{ profileData.qualification }}</p>
            </div>
          </div>

          <!-- Detail Fields -->
          <div class="lg:col-span-2 bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
            <h3 class="text-base font-bold text-gray-800 mb-5 flex items-center gap-2"><i class="bi bi-person-vcard text-indigo-600"></i>Chi tiết hồ sơ</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Họ và tên</label>
                <input v-if="isEditingProfile" v-model="profileData.name" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.name }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Giới tính</label>
                <select v-if="isEditingProfile" v-model="profileData.gender" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none">
                  <option>Nam</option><option>Nữ</option>
                </select>
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.gender }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Ngày sinh</label>
                <input v-if="isEditingProfile" v-model="profileData.dob" type="date" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ new Date(profileData.dob).toLocaleDateString('vi-VN') }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Số điện thoại</label>
                <input v-if="isEditingProfile" v-model="profileData.phone" type="tel" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.phone }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Email</label>
                <input v-if="isEditingProfile" v-model="profileData.email" type="email" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.email }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Ngày vào làm</label>
                <input v-if="isEditingProfile" v-model="profileData.startDate" type="date" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ new Date(profileData.startDate).toLocaleDateString('vi-VN') }}</p>
              </div>
              <div class="md:col-span-2">
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Địa chỉ</label>
                <input v-if="isEditingProfile" v-model="profileData.address" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.address }}</p>
              </div>
              <div class="md:col-span-2">
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Bằng cấp / Chứng chỉ</label>
                <input v-if="isEditingProfile" v-model="profileData.qualification" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.qualification }}</p>
              </div>
            </div>
            <div v-if="isEditingProfile" class="flex gap-3 mt-6 pt-4 border-t border-gray-100">
              <button @click="isEditingProfile = false" class="py-2.5 px-5 border border-gray-200 rounded-xl text-sm font-bold text-gray-600 hover:bg-gray-50">Hủy</button>
              <button @click="saveProfile" class="py-2.5 px-5 bg-indigo-700 hover:bg-indigo-800 text-white text-sm font-bold rounded-xl shadow">Lưu thay đổi</button>
            </div>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>
