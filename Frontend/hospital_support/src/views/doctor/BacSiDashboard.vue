<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const activeMenu = ref('checkup') // checkup, history, schedule, performance, profile

/* ========== PROFILE STATE ========== */
const profileData = ref({
  name: 'PGS.TS Nguyễn Văn An',
  role: 'Bác sĩ chuyên khoa',
  employeeId: 'BS-2024-001',
  department: 'Khoa Tim mạch',
  phone: '0909 888 777',
  email: 'an.nguyen@hospital.vn',
  address: '121/4a Trung Mỹ Tây, Quận 12, TP.HCM',
  dob: '1975-08-20',
  gender: 'Nam',
  startDate: '2005-01-15',
  qualification: 'Phó Giáo sư, Tiến sĩ Y khoa — Đại học Y Hà Nội',
  bio: 'Nguyên Phó Giám đốc Bệnh viện Tim TP.HCM. Chuyên sâu về can thiệp tim mạch, điều trị suy tim và tăng huyết áp. Hơn 20 năm kinh nghiệm lâm sàng.',
  avatar: ''
})
const isEditingProfile = ref(false)

function loadProfile() {
  const saved = localStorage.getItem('doctorProfile')
  if (saved) profileData.value = { ...profileData.value, ...JSON.parse(saved) }
  const user = localStorage.getItem('currentUser')
  if (user) {
    const u = JSON.parse(user)
    if (u.name) profileData.value.name = u.name
    if (u.email) profileData.value.email = u.email
    if (u.phone) profileData.value.phone = u.phone
  }
  const staffData = localStorage.getItem('hospitalStaff')
  if (staffData) {
    const staffList = JSON.parse(staffData)
    const found = staffList.find(s => s.name.includes(profileData.value.name) || profileData.value.name.includes(s.name))
    if (found) {
      if (found.bio) profileData.value.bio = found.bio
      if (found.specialty) profileData.value.department = 'Khoa ' + found.specialty
    }
  }
}
function saveProfile() {
  localStorage.setItem('doctorProfile', JSON.stringify(profileData.value))
  isEditingProfile.value = false
  alert('✅ Đã lưu thông tin cá nhân thành công!')
}
const bookings = ref([])
let pollTimer = null

/* ========== PATIENT QUEUE (READ-ONLY for Doctor) ========== */
const lane1Queue = computed(() => bookings.value.filter(b => b.status === 'Làn 1').sort((a, b) => a.stt - b.stt))
const lane2Queue = computed(() => bookings.value.filter(b => b.status === 'Làn 2').sort((a, b) => a.stt - b.stt))
const currentPatient = computed(() => bookings.value.find(b => b.status === 'Đang khám' || b.status === 'Đang đọc KQ'))
const doneToday = computed(() => bookings.value.filter(b => b.status === 'Đã khám').length)

/* ========== SUBCLINICAL TESTS ========== */
const subclinicals = ref({ bloodTest: false, xray: false, ultrasound: false, ctscan: false })
const subclinicalResults = {
  bloodTest: 'Hồng cầu: 4.5 T/l (BT). Bạch cầu: 7.2 G/l (BT). Glucose: 5.6 mmol/l (BT). Creatinin: 88 µmol/l (BT). Cholesterol tổng: 5.8 mmol/l (Bình thường cao).',
  xray: 'X-Quang tim phổi thẳng: Không tổn thương nhu mô phổi. Bóng tim không to. Các xoang sườn hoành rõ.',
  ultrasound: 'Siêu âm ổ bụng: Gan, mật, tụy, lách bình thường. Dạ dày tăng co bóp nhẹ. Không dịch ổ bụng.',
  ctscan: 'CT-Scanner sọ não: Không ổ xuất huyết hay u não. Hệ thống não thất bình thường. Mô não đồng đều.'
}

/* ========== DIAGNOSE & PRESCRIPTION ========== */
const diagnose = ref('')
const selectedMedicine = ref('')
const medicineQty = ref(10)
const medicineUse = ref('')
const prescriptionList = ref([])
const clinicalNotes = ref('')

const medicineDirectory = [
  { name: 'Panadol Extra 500mg', unit: 'Viên', use: 'Uống sáng 1 viên, chiều 1 viên sau ăn' },
  { name: 'Concor 2.5mg (Bisoprolol)', unit: 'Viên', use: 'Uống sáng 1 viên trước ăn 30 phút' },
  { name: 'Nexium mups 20mg', unit: 'Viên', use: 'Uống sáng 1 viên trước ăn 30 phút' },
  { name: 'Phosphalugel (Thuốc chữ P)', unit: 'Gói', use: 'Uống 1 gói khi đau hoặc sau ăn 2 tiếng' },
  { name: 'Pharmaton Essential', unit: 'Viên', use: 'Uống trưa 1 viên sau ăn' },
  { name: 'Amoxicillin 500mg', unit: 'Viên', use: 'Uống sáng 1 viên, tối 1 viên sau ăn' },
  { name: 'Atorvastatin 20mg', unit: 'Viên', use: 'Uống tối 1 viên sau ăn' },
  { name: 'Losartan 50mg', unit: 'Viên', use: 'Uống sáng 1 viên' },
  { name: 'Omeprazol 20mg', unit: 'Viên', use: 'Uống sáng 1 viên trước ăn 30 phút' }
]

/* ========== DOCTOR INFO ========== */
const doctorSchedule = ref('Thứ 2 - Thứ 6 Ca sáng (08:00 - 12:00)')
const doctorRoom = ref('Phòng 101 (Tim mạch)')
const currentDoctorName = ref('PGS.TS Nguyễn Văn An')

/* ========== HISTORY ========== */
const historySearchQuery = ref('')
const mockMedicalHistory = ref([
  { date: '15/06/2026', patient: 'Võ Văn Đức', doctor: 'PGS.TS Nguyễn Văn An', diagnose: 'Tăng huyết áp vô căn độ 1, rối loạn lipid máu', treatment: 'Concor 2.5mg sáng, Atorvastatin 20mg tối', lane: 'Làn 2' },
  { date: '14/06/2026', patient: 'Nguyễn Thị Hồng', doctor: 'PGS.TS Nguyễn Văn An', diagnose: 'Rối loạn dạ dày tá tràng dạng trào ngược (GERD)', treatment: 'Nexium mups 20mg sáng, Phosphalugel khi đau', lane: 'Làn 1' },
  { date: '13/06/2026', patient: 'Trần Minh Đạt', doctor: 'PGS.TS Nguyễn Văn An', diagnose: 'Viêm phế quản cấp, suy nhược cơ thể', treatment: 'Amoxicillin 500mg sáng tối, Pharmaton trưa', lane: 'Làn 1' }
])

const filteredHistory = computed(() => {
  if (!historySearchQuery.value.trim()) return mockMedicalHistory.value
  const q = historySearchQuery.value.toLowerCase()
  return mockMedicalHistory.value.filter(h =>
    h.patient.toLowerCase().includes(q) || h.diagnose.toLowerCase().includes(q)
  )
})

const currentPatientHistory = computed(() => {
  if (!currentPatient.value) return []
  const name = currentPatient.value.fullName.toLowerCase().trim()
  return mockMedicalHistory.value.filter(h => h.patient.toLowerCase().trim() === name)
})

function loadMedicalHistory() {
  const saved = localStorage.getItem('hospitalMedicalHistory')
  if (saved) {
    mockMedicalHistory.value = JSON.parse(saved)
  } else {
    localStorage.setItem('hospitalMedicalHistory', JSON.stringify(mockMedicalHistory.value))
  }
}

/* ========== LIFECYCLE ========== */
onMounted(() => {
  loadPatients()
  loadDoctorSchedule()
  loadProfile()
  loadMedicalHistory()
  pollTimer = setInterval(() => { loadPatients() }, 2000)
})
onUnmounted(() => { if (pollTimer) clearInterval(pollTimer) })

function loadDoctorSchedule() {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    const user = JSON.parse(userJson)
    currentDoctorName.value = user.name || currentDoctorName.value
    const staffData = localStorage.getItem('hospitalStaff')
    if (staffData) {
      const staffList = JSON.parse(staffData)
      const found = staffList.find(s => s.name.includes(user.name) || user.name.includes(s.name))
      if (found) {
        doctorSchedule.value = found.schedule || doctorSchedule.value
        doctorRoom.value = found.room || doctorRoom.value
      }
    }
  }
}

function loadPatients() {
  const data = localStorage.getItem('patientBookings')
  if (data) bookings.value = JSON.parse(data)
}

/* ========== SEND FOR TESTS ========== */
function sendForTests() {
  if (!currentPatient.value) return
  const anySelected = subclinicals.value.bloodTest || subclinicals.value.xray || subclinicals.value.ultrasound || subclinicals.value.ctscan
  if (!anySelected) {
    alert('Vui lòng chọn ít nhất một chỉ định cận lâm sàng!')
    return
  }

  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === currentPatient.value.id)
  if (idx !== -1) {
    const ordered = []
    if (subclinicals.value.bloodTest) ordered.push('bloodTest')
    if (subclinicals.value.xray) ordered.push('xray')
    if (subclinicals.value.ultrasound) ordered.push('ultrasound')
    if (subclinicals.value.ctscan) ordered.push('ctscan')

    list[idx].status = 'Đi xét nghiệm'
    list[idx].testOrdered = ordered
    list[idx].testSentAt = Date.now()
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
    resetExamState()
    alert(`📋 Đã gửi bệnh nhân ${list[idx].fullName} đi xét nghiệm: ${ordered.map(t => getTestName(t)).join(', ')}.\nBN sẽ quay lại Làn 2 khi có kết quả.`)
  }
}

function getTestName(key) {
  const map = { bloodTest: 'Xét nghiệm máu', xray: 'X-Quang', ultrasound: 'Siêu âm', ctscan: 'CT-Scanner' }
  return map[key] || key
}

/* ========== FINISH DIRECT (no tests needed) ========== */
function finishDirect() {
  if (!diagnose.value.trim()) {
    alert('Vui lòng nhập chẩn đoán bệnh!')
    return
  }
  completeExamination()
}

/* ========== FINISH WITH RESULTS (Lane 2) ========== */
function finishWithResults() {
  if (!diagnose.value.trim()) {
    alert('Vui lòng nhập chẩn đoán bệnh!')
    return
  }
  completeExamination()
}

const editingPrescriptionCode = ref('')

const pendingEditPatients = computed(() => {
  return bookings.value.filter(b => b.status === 'Yêu cầu sửa đơn')
})

function loadPrescriptionForEdit(p) {
  if (currentPatient.value) {
    if (!confirm(`Bác sĩ đang khám cho bệnh nhân ${currentPatient.value.fullName}. Bạn có muốn tạm dừng để sửa đơn cho bệnh nhân ${p.fullName} không?`)) {
      return
    }
  }

  // Load prescription details
  const presData = localStorage.getItem('patientPrescriptions')
  if (presData) {
    const list = JSON.parse(presData)
    const foundPres = list.find(pr => pr.code === p.prescriptionCode)
    if (foundPres) {
      editingPrescriptionCode.value = foundPres.code
      diagnose.value = foundPres.diagnose || ''
      clinicalNotes.value = foundPres.clinicalNotes || ''
      prescriptionList.value = [...foundPres.medicines]
    }
  }

  // Set patient's booking status to 'Đang khám'
  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === p.id)
  if (idx !== -1) {
    list.forEach(b => {
      if (b.status === 'Đang khám') b.status = 'Làn 1'
      if (b.status === 'Đang đọc KQ') b.status = 'Làn 2'
    })
    list[idx].status = 'Đang khám'
    bookings.value = list
    localStorage.setItem('patientBookings', JSON.stringify(list))
    alert(`📂 Đã tải bệnh án của bệnh nhân ${p.fullName} (Đơn thuốc: ${editingPrescriptionCode.value}) vào bảng làm việc. Vui lòng điều chỉnh đơn thuốc và lưu lại.`)
  }
}

function completeExamination() {
  const patient = currentPatient.value
  if (!patient) return

  // 1. Read central warehouseStock and validate availability
  const stockData = localStorage.getItem('warehouseStock')
  if (!stockData) {
    alert('Hệ thống kho chưa khởi tạo! Vui lòng liên hệ thủ kho.')
    return
  }

  let warehouseStock = JSON.parse(stockData)
  
  // Validate each medicine in the prescription list
  for (const med of prescriptionList.value) {
    const stockItem = warehouseStock.find(s => s.name.toLowerCase() === med.name.toLowerCase())
    const available = stockItem ? (stockItem.qty - (stockItem.heldQty || 0)) : 0
    if (available < med.qty) {
      alert(`⚠️ Không đủ tồn kho khả dụng cho thuốc: "${med.name}"!\nSố lượng khả dụng trong kho: ${available} ${stockItem ? stockItem.unit : 'đơn vị'}.\nYêu cầu: ${med.qty}. Bác sĩ vui lòng điều chỉnh đơn thuốc!`)
      return
    }
  }

  // 2. Lock (Hold) the quantities in warehouseStock
  prescriptionList.value.forEach(med => {
    const stockItem = warehouseStock.find(s => s.name.toLowerCase() === med.name.toLowerCase())
    if (stockItem) {
      stockItem.heldQty = (stockItem.heldQty || 0) + med.qty
    }
  })
  localStorage.setItem('warehouseStock', JSON.stringify(warehouseStock))

  // 3. Mark booking as completed
  const list = [...bookings.value]
  const idx = list.findIndex(b => b.id === patient.id)
  
  // Generate or reuse prescription code
  let prescriptionCode = editingPrescriptionCode.value
  if (!prescriptionCode) {
    prescriptionCode = 'DT-' + Math.floor(10000 + Math.random() * 90000)
  }

  if (idx !== -1) {
    list[idx].status = 'Đã khám'
    list[idx].prescriptionCode = prescriptionCode
    localStorage.setItem('patientBookings', JSON.stringify(list))
    bookings.value = list
  }

  // 4. Save/Update prescription
  let currentPrescriptions = []
  const presData = localStorage.getItem('patientPrescriptions')
  if (presData) currentPrescriptions = JSON.parse(presData)

  if (editingPrescriptionCode.value) {
    // Update existing prescription in place
    const pIdx = currentPrescriptions.findIndex(p => p.code === editingPrescriptionCode.value)
    if (pIdx !== -1) {
      currentPrescriptions[pIdx].diagnose = diagnose.value
      currentPrescriptions[pIdx].clinicalNotes = clinicalNotes.value
      currentPrescriptions[pIdx].medicines = [...prescriptionList.value]
      currentPrescriptions[pIdx].status = 'Chờ gọi tên' // change status back
    }
  } else {
    // Create new prescription
    const newPrescription = {
      id: Date.now(),
      code: prescriptionCode,
      date: new Date().toLocaleDateString('vi-VN'),
      patient: patient.fullName,
      patientAge: patient.patientAge || 35,
      patientGender: patient.gender || 'Chưa rõ',
      doctor: currentDoctorName.value,
      diagnose: diagnose.value,
      clinicalNotes: clinicalNotes.value,
      status: 'Chờ gọi tên',
      medicines: [...prescriptionList.value],
      extraMedicines: [],
      replacementRequests: []
    }
    currentPrescriptions.unshift(newPrescription)
  }
  localStorage.setItem('patientPrescriptions', JSON.stringify(currentPrescriptions))

  // Add to history
  mockMedicalHistory.value.unshift({
    date: new Date().toLocaleDateString('vi-VN'),
    patient: patient.fullName,
    doctor: currentDoctorName.value,
    diagnose: diagnose.value,
    treatment: prescriptionList.value.map(m => m.name).join(', ') || 'Không kê thuốc',
    lane: patient.status === 'Đang đọc KQ' ? 'Làn 2' : 'Làn 1'
  })
  localStorage.setItem('hospitalMedicalHistory', JSON.stringify(mockMedicalHistory.value))

  alert(`✅ Hoàn thành khám bệnh!\nBệnh nhân: ${patient.fullName}\nĐơn thuốc: ${prescriptionCode}\nĐã khóa tạm thời số lượng và gửi quầy Dược sĩ.`)
  resetExamState()
}

function resetExamState() {
  diagnose.value = ''
  clinicalNotes.value = ''
  prescriptionList.value = []
  subclinicals.value = { bloodTest: false, xray: false, ultrasound: false, ctscan: false }
  selectedMedicine.value = ''
  medicineQty.value = 10
  medicineUse.value = ''
  editingPrescriptionCode.value = ''
}

/* ========== MEDICINE HELPERS ========== */
function addMedicine() {
  if (!selectedMedicine.value) return
  const med = medicineDirectory.find(m => m.name === selectedMedicine.value)
  if (med) {
    prescriptionList.value.push({ name: med.name, qty: medicineQty.value, unit: med.unit, use: medicineUse.value || med.use })
    selectedMedicine.value = ''
    medicineQty.value = 10
    medicineUse.value = ''
  }
}
function removeMedicine(index) { prescriptionList.value.splice(index, 1) }
function onMedicineChange() {
  const med = medicineDirectory.find(m => m.name === selectedMedicine.value)
  if (med) medicineUse.value = med.use
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
            <h4 class="text-sm font-bold text-gray-800">{{ currentDoctorName }}</h4>
            <span class="text-xs text-sky-600 font-semibold">{{ doctorRoom }}</span>
          </div>
        </div>
        <nav class="space-y-1">
          <button @click="activeMenu = 'checkup'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'checkup' ? 'bg-sky-50 text-sky-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-heart-pulse text-lg"></i> Tiếp nhận khám bệnh
          </button>
          <button @click="activeMenu = 'history'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'history' ? 'bg-sky-50 text-sky-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-journal-medical text-lg"></i> Lịch sử bệnh án
          </button>
          <button @click="activeMenu = 'schedule'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'schedule' ? 'bg-sky-50 text-sky-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-calendar3 text-lg"></i> Lịch trực bác sĩ
          </button>
          <button @click="activeMenu = 'performance'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'performance' ? 'bg-sky-50 text-sky-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-graph-up-arrow text-lg"></i> Hiệu suất & Thống kê
          </button>
          <button @click="activeMenu = 'profile'" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all" :class="activeMenu === 'profile' ? 'bg-sky-50 text-sky-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <i class="bi bi-person-circle text-lg"></i> Thông tin cá nhân
          </button>
        </nav>
      </div>
      <div class="p-2 border-t text-xs text-gray-400 text-center">Doctor Workspace v3.0</div>
    </aside>

    <!-- ===== MAIN ===== -->
    <main class="flex-1 overflow-y-auto">

      <!-- ==================== MENU 1: TIẾP NHẬN KHÁM BỆNH ==================== -->
      <div v-if="activeMenu === 'checkup'" class="flex h-full animate-fade-in">

        <!-- MINI QUEUE SIDEBAR -->
        <div class="w-72 bg-white border-r border-gray-100 flex-shrink-0 flex flex-col overflow-hidden">
          <!-- Queue Header -->
          <div class="p-4 border-b border-gray-100 bg-gray-50/80">
            <h3 class="text-xs font-black text-gray-600 uppercase tracking-wider">Hàng đợi bệnh nhân</h3>
            <p class="text-[10px] text-gray-400 mt-0.5">Do Điều dưỡng quản lý gọi vào</p>
          </div>

          <div class="flex-1 overflow-y-auto p-3 space-y-4">
            <!-- Lane 1 Mini -->
            <div>
              <div class="flex items-center gap-1.5 mb-2">
                <span class="w-5 h-5 rounded-md bg-blue-600 text-white flex items-center justify-center text-[10px] font-black">1</span>
                <span class="text-[11px] font-bold text-blue-700">Khám mới ({{ lane1Queue.length }})</span>
              </div>
              <div class="space-y-1.5">
                <div v-for="p in lane1Queue" :key="p.id" class="p-2.5 bg-blue-50 rounded-xl border border-blue-100 text-[11px]">
                  <div class="font-bold text-gray-800">{{ p.fullName }}</div>
                  <div class="text-blue-500 text-[10px]">STT {{ p.stt }} • {{ p.department }}</div>
                </div>
                <div v-if="lane1Queue.length === 0" class="text-center py-3 text-[10px] text-gray-300">Trống</div>
              </div>
            </div>

            <!-- Lane 2 Mini -->
            <div>
              <div class="flex items-center gap-1.5 mb-2">
                <span class="w-5 h-5 rounded-md bg-emerald-600 text-white flex items-center justify-center text-[10px] font-black">2</span>
                <span class="text-[11px] font-bold text-emerald-700">Có kết quả ({{ lane2Queue.length }})</span>
              </div>
              <div class="space-y-1.5">
                <div v-for="p in lane2Queue" :key="p.id" class="p-2.5 bg-emerald-50 rounded-xl border border-emerald-100 text-[11px]">
                  <div class="font-bold text-gray-800">{{ p.fullName }}</div>
                  <div class="text-emerald-500 text-[10px]">STT {{ p.stt }} • Có KQ XN</div>
                </div>
                <div v-if="lane2Queue.length === 0" class="text-center py-3 text-[10px] text-gray-300">Trống</div>
              </div>
            </div>
          </div>

          <!-- Done count -->
          <div class="p-3 border-t border-gray-100 bg-gray-50/50">
            <div class="text-center">
              <span class="text-[10px] text-gray-400 font-bold uppercase">Đã khám hôm nay</span>
              <span class="text-xl font-black text-gray-600 block">{{ doneToday }}</span>
            </div>
          </div>
        </div>

        <!-- EXAMINATION WORKSPACE -->
        <div class="flex-1 p-6 md:p-8 overflow-y-auto">

          <!-- PENDING EDIT REQUESTS BANNER -->
          <div v-if="pendingEditPatients.length > 0" class="bg-rose-50 border border-rose-200 rounded-3xl p-5 mb-6 animate-pulse">
            <h3 class="text-sm font-bold text-rose-800 mb-3 flex items-center gap-2">
              <i class="bi bi-exclamation-triangle-fill text-rose-600"></i>
              Có {{ pendingEditPatients.length }} đơn thuốc bị Dược sĩ trả về yêu cầu sửa lại (do thiếu thuốc thực tế):
            </h3>
            <div class="space-y-2">
              <div v-for="p in pendingEditPatients" :key="p.id" class="p-3.5 bg-white rounded-2xl border border-rose-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                <div>
                  <p class="font-bold text-gray-850">{{ p.fullName }} <span class="font-mono text-blue-600 font-bold bg-blue-50 px-2 py-0.5 rounded ml-1">({{ p.prescriptionCode }})</span></p>
                  <p class="text-rose-600 font-semibold mt-1"><i class="bi bi-exclamation-circle mr-1"></i>Lý do trả: {{ p.needEditReason || 'Thiếu thuốc thực tế tại quầy' }}</p>
                  <p class="text-gray-400 text-[10px] mt-0.5">Triệu chứng ban đầu: {{ p.symptoms }}</p>
                </div>
                <button @click="loadPrescriptionForEdit(p)" class="py-1.5 px-3.5 bg-rose-600 hover:bg-rose-700 text-white font-bold rounded-xl transition-colors flex items-center gap-1 flex-shrink-0">
                  <i class="bi bi-pencil-square"></i> Tiến hành sửa đơn
                </button>
              </div>
            </div>
          </div>

          <!-- NO PATIENT -->
          <div v-if="!currentPatient" class="flex flex-col items-center justify-center h-full text-center">
            <div class="w-28 h-28 bg-gray-100 rounded-full flex items-center justify-center mb-6">
              <i class="bi bi-person-workspace text-5xl text-gray-300"></i>
            </div>
            <h2 class="text-2xl font-black text-gray-300">Chưa có bệnh nhân</h2>
            <p class="text-sm text-gray-400 mt-2 max-w-md">Điều dưỡng sẽ gọi bệnh nhân tiếp theo vào phòng khám.<br/>Hàng đợi hiện tại: <strong class="text-blue-600">{{ lane1Queue.length }} Làn 1</strong> • <strong class="text-emerald-600">{{ lane2Queue.length }} Làn 2</strong></p>
          </div>

          <!-- HAS PATIENT -->
          <div v-else class="space-y-6">

            <!-- Patient Info Banner -->
            <div class="rounded-3xl p-6 shadow-lg" :class="currentPatient.status === 'Đang đọc KQ' ? 'bg-gradient-to-r from-emerald-600 to-teal-700 text-white' : 'bg-gradient-to-r from-blue-600 to-indigo-700 text-white'">
              <div class="flex items-start justify-between">
                <div>
                  <span class="text-xs font-bold uppercase opacity-80 tracking-wider flex items-center gap-1.5">
                    <span class="w-5 h-5 rounded-md bg-white/20 flex items-center justify-center text-[10px] font-black">{{ currentPatient.status === 'Đang đọc KQ' ? '2' : '1' }}</span>
                    {{ currentPatient.status === 'Đang đọc KQ' ? 'Làn 2 — Đọc kết quả & Kê đơn' : 'Làn 1 — Khám lâm sàng mới' }}
                  </span>
                  <h2 class="text-2xl font-black mt-2">{{ currentPatient.fullName }}</h2>
                  <div class="flex flex-wrap gap-2 mt-2 text-xs">
                    <span class="bg-white/20 px-2.5 py-1 rounded-lg font-bold">STT: {{ currentPatient.stt }}</span>
                    <span class="bg-white/20 px-2.5 py-1 rounded-lg font-bold">{{ currentPatient.gender }}</span>
                    <span class="bg-white/20 px-2.5 py-1 rounded-lg font-bold">{{ currentPatient.department }}</span>
                    <span class="bg-white/20 px-2.5 py-1 rounded-lg font-bold">{{ currentPatient.code }}</span>
                  </div>
                </div>
                <div class="text-right flex-shrink-0">
                  <div class="text-xs opacity-70">Giờ khám</div>
                  <div class="text-lg font-black">{{ currentPatient.timeSlot }}</div>
                </div>
              </div>
              <div class="mt-4 p-3 bg-white/10 rounded-xl">
                <span class="text-[10px] uppercase font-bold opacity-60 block mb-1">Lý do khám / Triệu chứng</span>
                <p class="text-sm font-semibold leading-relaxed">{{ currentPatient.symptoms }}</p>
              </div>
            </div>

            <!-- Workspaces and Patient History Layout -->
            <div class="grid grid-cols-1 xl:grid-cols-3 gap-6 items-start">
              
              <!-- Left Column: Current Exam Workspace (Takes 2/3 width) -->
              <div class="xl:col-span-2 space-y-6">
                <!-- ===== LANE 1 WORKSPACE: KHÁM MỚI ===== -->
                <div v-if="currentPatient.status === 'Đang khám'" class="space-y-6">

                  <!-- Clinical Notes -->
                  <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
                    <h3 class="text-base font-bold text-gray-800 mb-3 flex items-center gap-2"><i class="bi bi-clipboard2-pulse text-blue-600"></i>Ghi chú lâm sàng</h3>
                    <textarea v-model="clinicalNotes" rows="3" placeholder="Ghi chú khám lâm sàng: huyết áp, nhịp tim, nghe phổi, khám bụng..." class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-200 text-sm resize-none"></textarea>
                  </div>

                  <!-- Subclinical Orders -->
                  <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
                    <h3 class="text-base font-bold text-gray-800 mb-4 flex items-center gap-2"><i class="bi bi-droplet-half text-purple-600"></i>Chỉ định cận lâm sàng</h3>
                    <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                      <label class="flex items-center gap-3 p-4 rounded-2xl border-2 cursor-pointer transition-all" :class="subclinicals.bloodTest ? 'border-purple-400 bg-purple-50' : 'border-gray-100 hover:border-gray-200'">
                        <input type="checkbox" v-model="subclinicals.bloodTest" class="w-4 h-4 accent-purple-600" />
                        <div><span class="text-sm font-bold text-gray-800">🩸 Xét nghiệm máu</span><p class="text-[10px] text-gray-400">Công thức máu, sinh hóa</p></div>
                      </label>
                      <label class="flex items-center gap-3 p-4 rounded-2xl border-2 cursor-pointer transition-all" :class="subclinicals.xray ? 'border-purple-400 bg-purple-50' : 'border-gray-100 hover:border-gray-200'">
                        <input type="checkbox" v-model="subclinicals.xray" class="w-4 h-4 accent-purple-600" />
                        <div><span class="text-sm font-bold text-gray-800">📷 X-Quang</span><p class="text-[10px] text-gray-400">Tim phổi, xương khớp</p></div>
                      </label>
                      <label class="flex items-center gap-3 p-4 rounded-2xl border-2 cursor-pointer transition-all" :class="subclinicals.ultrasound ? 'border-purple-400 bg-purple-50' : 'border-gray-100 hover:border-gray-200'">
                        <input type="checkbox" v-model="subclinicals.ultrasound" class="w-4 h-4 accent-purple-600" />
                        <div><span class="text-sm font-bold text-gray-800">🔊 Siêu âm</span><p class="text-[10px] text-gray-400">Ổ bụng, tuyến giáp</p></div>
                      </label>
                      <label class="flex items-center gap-3 p-4 rounded-2xl border-2 cursor-pointer transition-all" :class="subclinicals.ctscan ? 'border-purple-400 bg-purple-50' : 'border-gray-100 hover:border-gray-200'">
                        <input type="checkbox" v-model="subclinicals.ctscan" class="w-4 h-4 accent-purple-600" />
                        <div><span class="text-sm font-bold text-gray-800">🧠 CT-Scanner</span><p class="text-[10px] text-gray-400">Sọ não, ổ bụng</p></div>
                      </label>
                    </div>
                    <button @click="sendForTests" class="mt-4 py-3 px-6 bg-purple-700 hover:bg-purple-800 text-white font-bold text-sm rounded-xl shadow transition-all flex items-center gap-2">
                      <i class="bi bi-send"></i> Gửi đi xét nghiệm → BN ra khỏi phòng
                    </button>
                    <p class="text-[10px] text-gray-400 mt-2 italic">BN sẽ đi xét nghiệm và tự động quay lại Làn 2 khi có kết quả.</p>
                  </div>

                  <!-- OR: Diagnose directly (no tests) -->
                  <div class="bg-white p-6 rounded-3xl border-2 border-dashed border-sky-200 shadow-sm">
                    <h3 class="text-base font-bold text-gray-800 mb-1 flex items-center gap-2"><i class="bi bi-lightning text-amber-500"></i>Hoặc: Chẩn đoán trực tiếp (không cần XN)</h3>
                    <p class="text-xs text-gray-400 mb-4">Nếu triệu chứng rõ ràng, bác sĩ có thể chẩn đoán và kê đơn ngay mà không cần gửi đi xét nghiệm.</p>

                    <div class="space-y-4">
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-1.5">Chẩn đoán bệnh *</label>
                        <textarea v-model="diagnose" rows="2" placeholder="VD: Tăng huyết áp vô căn độ 1, suy nhược cơ thể..." class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-sky-200 text-sm resize-none"></textarea>
                      </div>
                      <!-- Prescription -->
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-1.5">Kê đơn thuốc</label>
                        <div class="flex gap-2 flex-wrap">
                          <select v-model="selectedMedicine" @change="onMedicineChange" class="flex-1 min-w-[200px] px-3 py-2 rounded-xl border border-gray-200 text-sm focus:outline-none">
                            <option value="">-- Chọn thuốc --</option>
                            <option v-for="m in medicineDirectory" :key="m.name" :value="m.name">{{ m.name }}</option>
                          </select>
                          <input v-model.number="medicineQty" type="number" min="1" class="w-20 px-3 py-2 rounded-xl border border-gray-200 text-sm text-center focus:outline-none" />
                          <input v-model="medicineUse" type="text" placeholder="Cách dùng..." class="flex-1 min-w-[200px] px-3 py-2 rounded-xl border border-gray-200 text-sm focus:outline-none" />
                          <button @click="addMedicine" class="py-2 px-4 bg-sky-600 hover:bg-sky-700 text-white text-sm font-bold rounded-xl">+ Thêm</button>
                        </div>
                      </div>
                      <div v-if="prescriptionList.length" class="bg-gray-50 p-4 rounded-xl border space-y-2">
                        <div v-for="(m, i) in prescriptionList" :key="i" class="flex items-center justify-between text-sm">
                          <span class="font-semibold text-gray-700">{{ i + 1 }}. {{ m.name }} — {{ m.qty }} {{ m.unit }}</span>
                          <div class="flex items-center gap-2">
                            <span class="text-xs text-gray-400 italic">{{ m.use }}</span>
                            <button @click="removeMedicine(i)" class="text-rose-500 hover:text-rose-700"><i class="bi bi-x-circle"></i></button>
                          </div>
                        </div>
                      </div>
                      <button @click="finishDirect" class="py-3 px-6 bg-sky-700 hover:bg-sky-800 text-white font-bold text-sm rounded-xl shadow transition-all flex items-center gap-2">
                        <i class="bi bi-check-circle"></i> Hoàn thành khám & Kê đơn
                      </button>
                    </div>
                  </div>
                </div>

                <!-- ===== LANE 2 WORKSPACE: ĐỌC KẾT QUẢ & KÊ ĐƠN ===== -->
                <div v-else-if="currentPatient.status === 'Đang đọc KQ'" class="space-y-6">

                  <!-- Test Results -->
                  <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
                    <h3 class="text-base font-bold text-gray-800 mb-4 flex items-center gap-2"><i class="bi bi-file-earmark-medical text-emerald-600"></i>Kết quả cận lâm sàng</h3>
                    <div class="space-y-3" v-if="currentPatient.testResults">
                      <div v-if="currentPatient.testResults.bloodTest" class="p-4 bg-red-50 rounded-2xl border border-red-100">
                        <h4 class="text-sm font-bold text-red-800 mb-1">🩸 Xét nghiệm máu</h4>
                        <p class="text-sm text-gray-700 leading-relaxed">{{ currentPatient.testResults.bloodTest }}</p>
                      </div>
                      <div v-if="currentPatient.testResults.xray" class="p-4 bg-blue-50 rounded-2xl border border-blue-100">
                        <h4 class="text-sm font-bold text-blue-800 mb-1">📷 X-Quang / Điện tim</h4>
                        <p class="text-sm text-gray-700 leading-relaxed">{{ currentPatient.testResults.xray }}</p>
                      </div>
                      <div v-if="currentPatient.testResults.ultrasound" class="p-4 bg-amber-50 rounded-2xl border border-amber-100">
                        <h4 class="text-sm font-bold text-amber-800 mb-1">🔊 Siêu âm</h4>
                        <p class="text-sm text-gray-700 leading-relaxed">{{ currentPatient.testResults.ultrasound }}</p>
                      </div>
                      <div v-if="currentPatient.testResults.ctscan" class="p-4 bg-purple-50 rounded-2xl border border-purple-100">
                        <h4 class="text-sm font-bold text-purple-800 mb-1">🧠 CT-Scanner</h4>
                        <p class="text-sm text-gray-700 leading-relaxed">{{ currentPatient.testResults.ctscan }}</p>
                      </div>
                    </div>
                    <div v-else class="p-4 bg-gray-50 rounded-xl text-center text-sm text-gray-400">Không có kết quả cận lâm sàng đính kèm.</div>
                  </div>

                  <!-- Diagnose & Prescribe -->
                  <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
                    <h3 class="text-base font-bold text-gray-800 flex items-center gap-2"><i class="bi bi-prescription2 text-emerald-600"></i>Chẩn đoán & Kê đơn thuốc</h3>
                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-1.5">Chẩn đoán bệnh *</label>
                      <textarea v-model="diagnose" rows="3" placeholder="VD: Rối loạn lipid máu, tăng huyết áp. Cần theo dõi định kỳ..." class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-200 text-sm resize-none"></textarea>
                    </div>
                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-1.5">Ghi chú thêm</label>
                      <textarea v-model="clinicalNotes" rows="2" placeholder="Lời dặn bệnh nhân, chế độ ăn uống, tái khám..." class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-200 text-sm resize-none"></textarea>
                    </div>
                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-1.5">Kê đơn thuốc</label>
                      <div class="flex gap-2 flex-wrap">
                        <select v-model="selectedMedicine" @change="onMedicineChange" class="flex-1 min-w-[200px] px-3 py-2 rounded-xl border border-gray-200 text-sm focus:outline-none">
                          <option value="">-- Chọn thuốc --</option>
                          <option v-for="m in medicineDirectory" :key="m.name" :value="m.name">{{ m.name }}</option>
                        </select>
                        <input v-model.number="medicineQty" type="number" min="1" class="w-20 px-3 py-2 rounded-xl border border-gray-200 text-sm text-center focus:outline-none" />
                        <input v-model="medicineUse" type="text" placeholder="Cách dùng..." class="flex-1 min-w-[200px] px-3 py-2 rounded-xl border border-gray-200 text-sm focus:outline-none" />
                        <button @click="addMedicine" class="py-2 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-bold rounded-xl">+ Thêm</button>
                      </div>
                    </div>
                    <div v-if="prescriptionList.length" class="bg-gray-50 p-4 rounded-xl border space-y-2">
                      <div v-for="(m, i) in prescriptionList" :key="i" class="flex items-center justify-between text-sm">
                        <span class="font-semibold text-gray-700">{{ i + 1 }}. {{ m.name }} — {{ m.qty }} {{ m.unit }}</span>
                        <div class="flex items-center gap-2">
                          <span class="text-xs text-gray-400 italic">{{ m.use }}</span>
                          <button @click="removeMedicine(i)" class="text-rose-500 hover:text-rose-700"><i class="bi bi-x-circle"></i></button>
                        </div>
                      </div>
                    </div>
                    <button @click="finishWithResults" class="py-3 px-6 bg-emerald-700 hover:bg-emerald-800 text-white font-bold text-sm rounded-xl shadow transition-all flex items-center gap-2">
                      <i class="bi bi-check-circle"></i> Hoàn thành khám & Kê đơn
                    </button>
                  </div>
                </div>
              </div>

              <!-- Right Column: Medical History of Current Patient (Takes 1/3 width) -->
              <div class="space-y-6">
                <div class="bg-white p-6 rounded-3xl border border-gray-150 shadow-sm flex flex-col">
                  <div class="flex items-center justify-between pb-4 border-b border-gray-100 mb-4">
                    <h3 class="text-sm font-bold text-gray-800 flex items-center gap-2">
                      <i class="bi bi-clock-history text-sky-650 text-base"></i>
                      Lịch sử khám bệnh nhân
                    </h3>
                    <span class="text-[10px] font-bold px-2 py-0.5 bg-sky-50 text-sky-700 rounded-lg">
                      {{ currentPatientHistory.length }} lần khám
                    </span>
                  </div>

                  <!-- History Timeline -->
                  <div class="overflow-y-auto max-h-[500px] space-y-4 pr-1">
                    <div v-for="(h, idx) in currentPatientHistory" :key="idx" class="relative pl-6 pb-2 border-l border-gray-150 last:pb-0">
                      <!-- Timeline dot -->
                      <span class="absolute left-[-5px] top-1.5 w-2.5 h-2.5 rounded-full bg-sky-500 border border-white"></span>
                      
                      <div class="text-[11px] text-gray-400 font-mono flex items-center justify-between mb-1">
                        <span>{{ h.date }}</span>
                        <span class="font-bold px-1.5 py-0.5 rounded text-[9px]" :class="h.lane === 'Làn 2' ? 'bg-emerald-50 text-emerald-700' : 'bg-blue-50 text-blue-700'">
                          {{ h.lane || 'Làn 1' }}
                        </span>
                      </div>
                      
                      <h4 class="text-xs font-bold text-gray-800">Chẩn đoán: {{ h.diagnose }}</h4>
                      <p class="text-[11px] text-gray-500 mt-1">
                        <strong class="text-gray-655">Bác sĩ:</strong> {{ h.doctor }}
                      </p>
                      <p class="text-[11px] text-gray-600 mt-0.5 italic">
                        <strong class="text-gray-655">Điều trị:</strong> {{ h.treatment }}
                      </p>
                    </div>

                    <div v-if="currentPatientHistory.length === 0" class="text-center py-8 text-gray-400">
                      <i class="bi bi-file-earmark-medical text-3xl text-gray-300 block mb-2"></i>
                      <p class="text-xs">Chưa có lịch sử khám trước đó trên hệ thống</p>
                    </div>
                  </div>
                </div>
              </div>

            </div>

          </div>
        </div>
      </div>

      <!-- ==================== MENU 2: LỊCH SỬ BỆNH ÁN ==================== -->
      <div v-else-if="activeMenu === 'history'" class="p-6 md:p-8 space-y-6 animate-fade-in">
        <div class="flex items-end justify-between">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Lịch sử bệnh án</h2>
            <p class="text-sm text-gray-400">Tra cứu hồ sơ khám bệnh và đơn thuốc đã kê.</p>
          </div>
          <div class="relative w-80">
            <input v-model="historySearchQuery" type="text" placeholder="Tìm theo tên BN hoặc chẩn đoán..." class="w-full pl-10 pr-3 py-2.5 text-sm rounded-xl border border-gray-200 focus:outline-none" />
            <i class="bi bi-search absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
        </div>
        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <table class="w-full text-left border-collapse text-sm">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                <th class="py-3 px-5">Ngày</th>
                <th class="py-3 px-5">Bệnh nhân</th>
                <th class="py-3 px-5">Bác sĩ</th>
                <th class="py-3 px-5">Luồng</th>
                <th class="py-3 px-5">Chẩn đoán</th>
                <th class="py-3 px-5">Điều trị</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 text-gray-700">
              <tr v-for="h in filteredHistory" :key="h.date + h.patient" class="hover:bg-gray-50/20">
                <td class="py-3.5 px-5 font-mono text-xs text-gray-500">{{ h.date }}</td>
                <td class="py-3.5 px-5 font-bold">{{ h.patient }}</td>
                <td class="py-3.5 px-5 text-sky-700 font-semibold text-xs">{{ h.doctor }}</td>
                <td class="py-3.5 px-5">
                  <span class="text-[10px] font-bold px-2 py-0.5 rounded-full" :class="h.lane === 'Làn 2' ? 'bg-emerald-100 text-emerald-700' : 'bg-blue-100 text-blue-700'">{{ h.lane }}</span>
                </td>
                <td class="py-3.5 px-5 text-gray-600 text-xs">{{ h.diagnose }}</td>
                <td class="py-3.5 px-5 text-gray-500 text-xs italic">{{ h.treatment }}</td>
              </tr>
              <tr v-if="filteredHistory.length === 0">
                <td colspan="6" class="py-8 text-center text-gray-300 text-sm">Không tìm thấy hồ sơ</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- ==================== MENU 3: LỊCH TRỰC ==================== -->
      <div v-else-if="activeMenu === 'schedule'" class="p-6 md:p-8 space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Lịch trực bác sĩ</h2>
          <p class="text-sm text-gray-400">Thông tin ca trực và phòng khám được phân công.</p>
        </div>
        <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 text-sm text-gray-700">
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h4 class="font-bold text-sky-800 mb-2">Lịch trực Tuần này</h4>
              <p class="text-xs text-gray-600 leading-relaxed font-semibold">{{ doctorSchedule }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h4 class="font-bold text-sky-800 mb-2">Phòng khám được phân công</h4>
              <p class="text-xs text-gray-600 leading-relaxed font-semibold"><i class="bi bi-geo-alt text-sky-600 mr-1"></i>{{ doctorRoom }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h4 class="font-bold text-sky-800 mb-2">Quy tắc phân luồng</h4>
              <p class="text-xs text-gray-600 leading-relaxed">ĐD gọi BN so le: 1 Làn 1 → 1 Làn 2 → luân phiên liên tục.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 4: HIỆU SUẤT ==================== -->
      <div v-else-if="activeMenu === 'performance'" class="p-6 md:p-8 space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Hiệu suất & Thống kê</h2>
          <p class="text-sm text-gray-400">Tổng quan số ca khám theo luồng.</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm text-center">
            <span class="text-3xl">🆕</span>
            <h4 class="text-3xl font-black text-blue-700 mt-2">{{ lane1Queue.length }}</h4>
            <span class="text-xs text-gray-400 font-bold uppercase">Chờ khám mới (Làn 1)</span>
          </div>
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm text-center">
            <span class="text-3xl">🔄</span>
            <h4 class="text-3xl font-black text-emerald-600 mt-2">{{ lane2Queue.length }}</h4>
            <span class="text-xs text-gray-400 font-bold uppercase">Chờ đọc KQ (Làn 2)</span>
          </div>
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm text-center">
            <span class="text-3xl">✅</span>
            <h4 class="text-3xl font-black text-gray-700 mt-2">{{ doneToday }}</h4>
            <span class="text-xs text-gray-400 font-bold uppercase">Đã khám hôm nay</span>
          </div>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
          <h3 class="text-lg font-bold text-gray-800 mb-4">Tỷ lệ phân luồng</h3>
          <div class="space-y-4">
            <div class="space-y-1">
              <div class="flex justify-between text-sm"><span class="font-bold text-blue-700">Làn 1 — Khám mới</span><span class="text-gray-400">{{ lane1Queue.length }}</span></div>
              <div class="w-full bg-gray-100 h-3 rounded-full overflow-hidden"><div class="bg-blue-500 h-full rounded-full transition-all duration-700" :style="{ width: (lane1Queue.length + lane2Queue.length) ? `${(lane1Queue.length / (lane1Queue.length + lane2Queue.length)) * 100}%` : '0%' }"></div></div>
            </div>
            <div class="space-y-1">
              <div class="flex justify-between text-sm"><span class="font-bold text-emerald-600">Làn 2 — Có kết quả</span><span class="text-gray-400">{{ lane2Queue.length }}</span></div>
              <div class="w-full bg-gray-100 h-3 rounded-full overflow-hidden"><div class="bg-emerald-500 h-full rounded-full transition-all duration-700" :style="{ width: (lane1Queue.length + lane2Queue.length) ? `${(lane2Queue.length / (lane1Queue.length + lane2Queue.length)) * 100}%` : '0%' }"></div></div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 5: THÔNG TIN CÁ NHÂN ==================== -->
      <div v-else-if="activeMenu === 'profile'" class="p-6 md:p-8 space-y-6 animate-fade-in">
        <div class="flex items-end justify-between">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Thông tin cá nhân</h2>
            <p class="text-sm text-gray-400">Hồ sơ bác sĩ và thông tin nghề nghiệp của bạn.</p>
          </div>
          <button v-if="!isEditingProfile" @click="isEditingProfile = true" class="py-2.5 px-5 bg-sky-700 hover:bg-sky-800 text-white text-sm font-bold rounded-xl shadow transition-all flex items-center gap-2">
            <i class="bi bi-pencil-square"></i> Chỉnh sửa
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Avatar & Summary Card -->
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm text-center">
            <div class="w-24 h-24 rounded-full bg-gradient-to-br from-sky-400 to-blue-600 mx-auto flex items-center justify-center text-white text-3xl font-black shadow-lg">
              {{ profileData.name.replace(/^(GS\.TS|PGS\.TS|TS\.BS|ThS\.BS|BS)\s*/, '').charAt(0) }}
            </div>
            <h3 class="text-lg font-black text-gray-800 mt-4">{{ profileData.name }}</h3>
            <span class="text-xs bg-sky-100 text-sky-700 font-bold px-3 py-1 rounded-full">{{ profileData.role }}</span>
            <div class="mt-4 space-y-2 text-sm text-gray-500">
              <p><i class="bi bi-hash text-sky-400 mr-1.5"></i>{{ profileData.employeeId }}</p>
              <p><i class="bi bi-building text-sky-400 mr-1.5"></i>{{ profileData.department }}</p>
              <p><i class="bi bi-mortarboard text-sky-400 mr-1.5"></i>{{ profileData.qualification }}</p>
            </div>
            <div class="mt-4 p-3 bg-sky-50 rounded-xl border border-sky-100 text-left">
              <span class="text-[10px] uppercase font-bold text-sky-500 block mb-1">Tiểu sử / Bio</span>
              <p v-if="!isEditingProfile" class="text-xs text-gray-600 leading-relaxed">{{ profileData.bio }}</p>
              <textarea v-else v-model="profileData.bio" rows="3" class="w-full text-xs px-3 py-2 rounded-lg border border-sky-200 focus:outline-none resize-none"></textarea>
            </div>
          </div>

          <!-- Detail Fields -->
          <div class="lg:col-span-2 bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
            <h3 class="text-base font-bold text-gray-800 mb-5 flex items-center gap-2"><i class="bi bi-person-vcard text-sky-600"></i>Chi tiết hồ sơ</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Họ và tên</label>
                <input v-if="isEditingProfile" v-model="profileData.name" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
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
                <input v-if="isEditingProfile" v-model="profileData.dob" type="date" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ new Date(profileData.dob).toLocaleDateString('vi-VN') }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Số điện thoại</label>
                <input v-if="isEditingProfile" v-model="profileData.phone" type="tel" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.phone }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Email</label>
                <input v-if="isEditingProfile" v-model="profileData.email" type="email" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.email }}</p>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Ngày vào làm</label>
                <input v-if="isEditingProfile" v-model="profileData.startDate" type="date" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ new Date(profileData.startDate).toLocaleDateString('vi-VN') }}</p>
              </div>
              <div class="md:col-span-2">
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Địa chỉ</label>
                <input v-if="isEditingProfile" v-model="profileData.address" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.address }}</p>
              </div>
              <div class="md:col-span-2">
                <label class="block text-xs font-bold text-gray-400 uppercase mb-1">Bằng cấp / Học hàm học vị</label>
                <input v-if="isEditingProfile" v-model="profileData.qualification" class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-sky-200" />
                <p v-else class="text-sm font-semibold text-gray-800 py-2.5">{{ profileData.qualification }}</p>
              </div>
            </div>
            <div v-if="isEditingProfile" class="flex gap-3 mt-6 pt-4 border-t border-gray-100">
              <button @click="isEditingProfile = false" class="py-2.5 px-5 border border-gray-200 rounded-xl text-sm font-bold text-gray-600 hover:bg-gray-50">Hủy</button>
              <button @click="saveProfile" class="py-2.5 px-5 bg-sky-700 hover:bg-sky-800 text-white text-sm font-bold rounded-xl shadow">Lưu thay đổi</button>
            </div>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>
