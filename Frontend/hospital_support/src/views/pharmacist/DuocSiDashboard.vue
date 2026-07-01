<script setup>
import { ref, onMounted, computed, watch } from 'vue'

const activeMenu = ref('prescriptions') // prescriptions, queue, schedule, profile, reports
const prescriptions = ref([])
const activePrescription = ref(null)
const stock = ref([])

// Step-by-step dispensing flow state for active prescription
const currentDispenseStep = ref('detail') // 'detail', 'verify', 'payment', 'complete'
const verifiedMedicines = ref([]) // copy of medicines with verifiedQty

// Search & filter states
const searchPrescriptionQuery = ref('')
const selectedDateFilter = ref('')

// Replacement modal states
const showReplaceModal = ref(false)
const replaceTargetMed = ref(null) // the medicine that needs replacement
const selectedReplaceWith = ref('') // name of replacement medicine from stock
const replaceQty = ref(1)
const replaceNote = ref('')

// Add extra medicine states
const showAddExtraForm = ref(false)
const selectedExtraMed = ref('')
const extraQty = ref(1)

// Payment details
const paymentMethod = ref('cash') // 'cash', 'online'
const cashAmountGiven = ref(0)
const onlinePaymentStatus = ref('waiting') // 'waiting', 'processing', 'success'
const showVietQRModal = ref(false)

// Queue states
const currentCallingPatient = ref(null)
const counterNumber = ref(2)
const queueList = ref([]) // patients waiting for prescription collection

// Staff Card / Profile info
const currentUser = ref({
  fullName: 'Dược sĩ Trần Thanh Thủy',
  gender: 'Nữ',
  phone: '0908887777',
  email: 'pharmacy.thuy@hospital.com',
  role: 'pharmacist',
  staffId: 'NV-DS-041',
  specialty: 'Dược sĩ Đại học - Chuyên khoa Dược lâm sàng',
  department: 'Khoa Dược - Quầy cấp phát số 2',
  joinDate: '12/04/2021',
  avatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=200'
})
const isEditingProfile = ref(false)
const editForm = ref({ fullName: '', phone: '', email: '', specialty: '', department: '' })

// Modal print previews
const showReceiptModal = ref(false)
const showPocketLabelsModal = ref(false)
const receiptPrescription = ref(null)

// Stats
const summaryStats = ref({
  totalDispensed: 0,
  pendingDispense: 0,
  todayRevenue: 0, // from extra sales
  replacedRequests: 0,
  alertStockCount: 0
})

// Alternative medicines mapping for suggestions
const alternativeSuggestions = {
  'Panadol Extra 500mg': ['Pharmaton Essential', 'Amoxicillin 500mg'],
  'Nexium mups 20mg': ['Phosphalugel (Thuốc chữ P)'],
  'Concor 2.5mg (Bisoprolol fumarate)': ['Pharmaton Essential'],
  'Amoxicillin 500mg': ['Nexium mups 20mg'],
  'Phosphalugel (Thuốc chữ P)': ['Nexium mups 20mg']
}

// Weekly schedule mock data
const weeklySchedule = ref([
  { day: 'Thứ Hai', date: 'Hôm nay', shift: 'Ca Sáng', time: '07:30 - 11:30', location: 'Quầy cấp phát số 2', role: 'Phát thuốc Bảo hiểm y tế', status: 'Đang hoạt động' },
  { day: 'Thứ Ba', date: 'Ngày mai', shift: 'Ca Chiều', time: '13:00 - 17:00', location: 'Quầy cấp phát số 2', role: 'Phát thuốc Bảo hiểm y tế', status: 'Sắp diễn ra' },
  { day: 'Thứ Tư', date: 'Kia', shift: 'Ca Sáng', time: '07:30 - 11:30', location: 'Kho trung tâm', role: 'Kiểm kê và nhập hàng', status: 'Sắp diễn ra' },
  { day: 'Thứ Năm', date: 'Tuần này', shift: 'Nghỉ', time: '-', location: '-', role: '-', status: 'Nghỉ trực' },
  { day: 'Thứ Sáu', date: 'Tuần này', shift: 'Ca Tối', time: '17:00 - 21:00', location: 'Quầy tự nguyện số 4', role: 'Bán thuốc kê đơn dịch vụ', status: 'Sắp diễn ra' },
  { day: 'Thứ Bảy', date: 'Tuần này', shift: 'Ca Sáng', time: '07:30 - 11:30', location: 'Quầy cấp phát số 2', role: 'Phát thuốc Bảo hiểm y tế', status: 'Sắp diễn ra' },
  { day: 'Chủ Nhật', date: 'Tuần này', shift: 'Nghỉ', time: '-', location: '-', role: '-', status: 'Nghỉ trực' }
])

onMounted(() => {
  loadUser()
  loadStock()
  loadPrescriptions()
  syncQueueList()
})

function loadUser() {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    const data = JSON.parse(userJson)
    currentUser.value = {
      ...currentUser.value,
      ...data
    }
  }
}

function loadStock() {
  const mockStock = [
    { name: 'Panadol Extra 500mg', qty: 120, heldQty: 0, unit: 'Viên', expiryDate: '2027-06-30', minStock: 20, price: 2000 },
    { name: 'Concor 2.5mg (Bisoprolol fumarate)', qty: 85, heldQty: 0, unit: 'Viên', expiryDate: '2026-09-15', minStock: 25, price: 8000 },
    { name: 'Nexium mups 20mg', qty: 15, heldQty: 0, unit: 'Viên', expiryDate: '2027-11-20', minStock: 30, price: 15000 },
    { name: 'Phosphalugel (Thuốc chữ P)', qty: 60, heldQty: 0, unit: 'Gói', expiryDate: '2026-08-01', minStock: 15, price: 6000 },
    { name: 'Pharmaton Essential', qty: 45, heldQty: 0, unit: 'Viên', expiryDate: '2027-04-10', minStock: 20, price: 5000 },
    { name: 'Amoxicillin 500mg', qty: 9, heldQty: 0, unit: 'Viên', expiryDate: '2027-09-30', minStock: 25, price: 3000 },
    { name: 'Siro ho Prospan (Chai)', qty: 30, heldQty: 0, unit: 'Chai', expiryDate: '2027-10-15', minStock: 5, price: 75000 }
  ]
  const data = localStorage.getItem('warehouseStock')
  if (data) {
    const parsed = JSON.parse(data)
    stock.value = parsed.map(item => {
      const match = mockStock.find(m => m.name.toLowerCase() === item.name.toLowerCase())
      return {
        heldQty: 0,
        price: 5000,
        ...match,
        ...item
      }
    })
    if (!stock.value.some(s => s.name.toLowerCase().includes('siro ho'))) {
      const siro = mockStock.find(m => m.name.includes('Siro ho'))
      if (siro) {
        stock.value.push(siro)
        localStorage.setItem('warehouseStock', JSON.stringify(stock.value))
      }
    }
  } else {
    stock.value = mockStock
    localStorage.setItem('warehouseStock', JSON.stringify(mockStock))
  }
}

function loadPrescriptions() {
  const data = localStorage.getItem('patientPrescriptions')
  let list = []
  if (data) {
    list = JSON.parse(data)
  } else {
    list = [
      {
        id: 201,
        code: 'DT-88271',
        date: new Date().toLocaleDateString('vi-VN'),
        doctor: 'PGS.TS Nguyễn Văn An',
        diagnose: 'Suy nhược cơ thể, Rối loạn nhịp tim nhẹ',
        status: 'Chờ gọi tên',
        patientName: 'Lê Hoàng Long',
        patientAge: 45,
        patientGender: 'Nam',
        medicines: [
          { name: 'Panadol Extra 500mg', qty: 10, unit: 'Viên', use: 'Uống sáng 1 viên, chiều 1 viên sau ăn' },
          { name: 'Concor 2.5mg (Bisoprolol fumarate)', qty: 30, unit: 'Viên', use: 'Uống sáng 1 viên trước ăn' }
        ],
        extraMedicines: [],
        replacementRequests: []
      },
      {
        id: 202,
        code: 'DT-44281',
        date: new Date().toLocaleDateString('vi-VN'),
        doctor: 'TS.BS Trần Thị Mai',
        diagnose: 'Đau dạ dày cấp tính, viêm loét hang vị',
        status: 'Chờ gọi tên',
        patientName: 'Nguyễn Thị Hoa',
        patientAge: 32,
        patientGender: 'Nữ',
        medicines: [
          { name: 'Nexium mups 20mg', qty: 25, unit: 'Viên', use: 'Uống sáng 1 viên trước ăn 30 phút' },
          { name: 'Phosphalugel (Thuốc chữ P)', qty: 15, unit: 'Gói', use: 'Uống 1 gói khi đau hoặc sau ăn 2 tiếng' }
        ],
        extraMedicines: [],
        replacementRequests: []
      },
      {
        id: 203,
        code: 'DT-11029',
        date: new Date().toLocaleDateString('vi-VN'),
        doctor: 'BSCKII. Lê Văn Dũng',
        diagnose: 'Viêm họng cấp tính, sốt siêu vi',
        status: 'Chờ gọi tên',
        patientName: 'Trần Văn Tú',
        patientAge: 28,
        patientGender: 'Nam',
        medicines: [
          { name: 'Amoxicillin 500mg', qty: 15, unit: 'Viên', use: 'Uống sáng 1 viên, tối 1 viên sau ăn' }
        ],
        extraMedicines: [],
        replacementRequests: []
      }
    ]
    localStorage.setItem('patientPrescriptions', JSON.stringify(list))
  }

  prescriptions.value = list.map(p => ({
    extraMedicines: [],
    replacementRequests: [],
    patientAge: p.patientAge || 35,
    patientGender: p.patientGender || 'Chưa rõ',
    patientName: p.patientName || 'Bệnh nhân ẩn danh',
    ...p
  }))
  calculateStats()
}

function calculateStats() {
  const list = prescriptions.value
  summaryStats.value.totalDispensed = list.filter(p => p.status === 'Đã nhận thuốc').length
  summaryStats.value.pendingDispense = list.filter(p => p.status === 'Chờ gọi tên' || p.status === 'Chờ lấy thuốc' || p.status === 'Đang soạn thuốc').length
  summaryStats.value.alertStockCount = stock.value.filter(item => item.qty <= item.minStock).length

  let revenue = 0
  let replacementCount = 0
  list.forEach(p => {
    if (p.status === 'Đã nhận thuốc') {
      if (p.extraMedicines) {
        p.extraMedicines.forEach(em => {
          revenue += em.qty * em.price
        })
      }
    }
    if (p.replacementRequests) {
      replacementCount += p.replacementRequests.length
    }
  })
  summaryStats.value.todayRevenue = revenue
  summaryStats.value.replacedRequests = replacementCount
}

function selectPrescription(p) {
  activePrescription.value = p
  currentDispenseStep.value = 'detail' // reset to first step on change
  showAddExtraForm.value = false
  selectedExtraMed.value = ''
  extraQty.value = 1
}

// Step 1: Start Dispensing process - initializes verified medicines copy
function startDispenseFlow() {
  if (!activePrescription.value) return
  
  // Clone medicines list so we can modify and track verified/input quantities
  verifiedMedicines.value = activePrescription.value.medicines.map(m => ({
    ...m,
    verifiedQty: m.qty // default verified quantity to the prescribed quantity
  }))

  currentDispenseStep.value = 'verify'
}

// Check stock status for a drug line
function getStockItem(medName) {
  return stock.value.find(s => s.name.toLowerCase() === medName.toLowerCase())
}

function checkStockSufficiency(medName, neededQty) {
  const item = getStockItem(medName)
  if (!item) return { ok: false, available: 0, text: 'Hết hàng (Không có trong kho)' }
  if (item.qty >= neededQty) {
    return { ok: true, available: item.qty, text: `Đủ hàng (Tồn: ${item.qty} ${item.unit})` }
  } else {
    return { ok: false, available: item.qty, text: `Thiếu hàng (Chỉ còn: ${item.qty} ${item.unit})` }
  }
}

// Alternative medicines available in stock
const availableAlternatives = computed(() => {
  if (!replaceTargetMed.value) return []
  const original = replaceTargetMed.value.name
  const suggestedNames = alternativeSuggestions[original] || []
  return stock.value.filter(s => s.qty > 0 && s.name !== original)
})

// Filtered prescriptions list
const filteredPrescriptions = computed(() => {
  let list = prescriptions.value

  if (searchPrescriptionQuery.value.trim() !== '') {
    const q = searchPrescriptionQuery.value.toLowerCase()
    list = list.filter(p => 
      p.code.toLowerCase().includes(q) || 
      p.patientName.toLowerCase().includes(q) || 
      p.doctor.toLowerCase().includes(q)
    )
  }

  if (selectedDateFilter.value) {
    list = list.filter(p => p.date === selectedDateFilter.value)
  }

  return list
})

// Add Extra Medicine logic
const availableExtraMedicines = computed(() => {
  return stock.value.filter(s => s.qty > 0)
})

function addExtraMedicine() {
  if (!selectedExtraMed.value || extraQty.value <= 0) return
  
  const targetStock = stock.value.find(s => s.name === selectedExtraMed.value)
  if (!targetStock) return

  if (targetStock.qty < extraQty.value) {
    alert(`Không đủ tồn kho! Kho chỉ còn ${targetStock.qty} ${targetStock.unit}.`)
    return
  }

  const existingExtraIdx = activePrescription.value.extraMedicines.findIndex(em => em.name === selectedExtraMed.value)
  if (existingExtraIdx !== -1) {
    const totalExtraQty = activePrescription.value.extraMedicines[existingExtraIdx].qty + extraQty.value
    if (targetStock.qty < totalExtraQty) {
      alert(`Tổng số lượng mua thêm (${totalExtraQty}) vượt quá lượng tồn kho (${targetStock.qty}).`)
      return
    }
    activePrescription.value.extraMedicines[existingExtraIdx].qty = totalExtraQty
  } else {
    activePrescription.value.extraMedicines.push({
      name: targetStock.name,
      qty: extraQty.value,
      unit: targetStock.unit,
      price: targetStock.price,
      use: 'Dùng theo nhu cầu bệnh nhân'
    })
  }

  savePrescriptionsToStorage()
  calculateStats()

  selectedExtraMed.value = ''
  extraQty.value = 1
  showAddExtraForm.value = false
  alert('Đã thêm thuốc mua kèm thành công!')
}

function removeExtraMedicine(index) {
  activePrescription.value.extraMedicines.splice(index, 1)
  savePrescriptionsToStorage()
  calculateStats()
}

// Drug replacement request flows
function openReplaceModal(med) {
  replaceTargetMed.value = med
  replaceQty.value = med.qty
  replaceNote.value = `Hết tồn kho ${med.name} tại quầy dược sĩ. Đề xuất đổi thuốc.`
  
  const suggestions = alternativeSuggestions[med.name] || []
  const availableSuggest = stock.value.find(s => suggestions.includes(s.name) && s.qty >= med.qty)
  selectedReplaceWith.value = availableSuggest ? availableSuggest.name : ''
  
  showReplaceModal.value = true
}

function submitReplacementRequest() {
  if (!selectedReplaceWith.value) {
    alert('Vui lòng chọn thuốc thay thế tương đương!')
    return
  }

  const request = {
    id: Date.now(),
    originalMed: replaceTargetMed.value.name,
    replacementMed: selectedReplaceWith.value,
    qty: replaceQty.value,
    note: replaceNote.value,
    status: 'Chờ duyệt' // Chờ duyệt, Đã duyệt, Từ chối
  }

  if (!activePrescription.value.replacementRequests) {
    activePrescription.value.replacementRequests = []
  }

  activePrescription.value.replacementRequests.push(request)
  savePrescriptionsToStorage()
  calculateStats()
  
  showReplaceModal.value = false
  alert('Đã gửi yêu cầu đổi thuốc lên Bác sĩ kê đơn! Vui lòng chờ phản hồi.')
}

// Simulation of doctor approval to make the flow fully functional
function simulateDoctorApproval(reqId) {
  const reqIdx = activePrescription.value.replacementRequests.findIndex(r => r.id === reqId)
  if (reqIdx === -1) return

  const req = activePrescription.value.replacementRequests[reqIdx]
  req.status = 'Đã duyệt'

  // Update prescription medicines list
  const medIdx = activePrescription.value.medicines.findIndex(m => m.name === req.originalMed)
  if (medIdx !== -1) {
    const replaceStock = stock.value.find(s => s.name === req.replacementMed)
    activePrescription.value.medicines[medIdx] = {
      name: req.replacementMed,
      qty: req.qty,
      unit: replaceStock ? replaceStock.unit : 'Viên',
      use: `[ĐỔI THEO BÁC SĨ] ` + activePrescription.value.medicines[medIdx].use
    }
  }

  // Update verifiedMedicines copy as well if we are in verification step
  if (currentDispenseStep.value === 'verify') {
    const verIdx = verifiedMedicines.value.findIndex(m => m.name === req.originalMed)
    if (verIdx !== -1) {
      const replaceStock = stock.value.find(s => s.name === req.replacementMed)
      verifiedMedicines.value[verIdx] = {
        name: req.replacementMed,
        qty: req.qty,
        verifiedQty: req.qty,
        unit: replaceStock ? replaceStock.unit : 'Viên',
        use: `[ĐỔI THEO BÁC SĨ] ` + verifiedMedicines.value[verIdx].use
      }
    }
  }

  savePrescriptionsToStorage()
  calculateStats()
  alert('📢 MÔ PHỎNG BÁC SĨ: Bác sĩ kê đơn đã PHÊ DUYỆT yêu cầu đổi thuốc thành công!')
}

// Verification checks before moving to checkout payment step
function proceedToPaymentStep() {
  // Check if any verified item exceeds stock or is empty
  for (let m of verifiedMedicines.value) {
    if (m.verifiedQty <= 0) {
      alert(`Số lượng thực tế lấy của thuốc ${m.name} phải lớn hơn 0!`)
      return
    }
    const check = checkStockSufficiency(m.name, m.verifiedQty)
    if (!check.ok) {
      alert(`Không thể tiếp tục! Thuốc ${m.name} đang thiếu hàng tồn kho. Vui lòng chọn tác vụ Đổi thuốc thay thế.`);
      return
    }
  }

  // Sync back verified quantities to activePrescription medicines list
  activePrescription.value.medicines = verifiedMedicines.value.map(m => ({
    name: m.name,
    qty: m.verifiedQty,
    unit: m.unit,
    use: m.use
  }))
  savePrescriptionsToStorage()

  // Reset payment calculator values
  cashAmountGiven.value = activePrescriptionCosts.value.total
  onlinePaymentStatus.value = 'waiting'
  
  currentDispenseStep.value = 'payment'
}

// Simulated QR code scanning animation
function triggerOnlinePaymentScan() {
  onlinePaymentStatus.value = 'processing'
  setTimeout(() => {
    onlinePaymentStatus.value = 'success'
    alert('💳 CỔNG THANH TOÁN: Giao dịch quét mã QR trực tuyến THÀNH CÔNG!')
  }, 2200)
}

// Confirm checkout, deduct quantities and move to Complete step
function confirmDispenseAndCheckout() {
  if (paymentMethod.value === 'online' && onlinePaymentStatus.value !== 'success') {
    alert('Vui lòng đợi quét mã QR và xác nhận thanh toán trực tuyến thành công!')
    return
  }

  if (paymentMethod.value === 'cash' && changeAmountDue.value < 0) {
    alert('Số tiền khách đưa không đủ để thanh toán đơn thuốc này!')
    return
  }

  const list = [...prescriptions.value]
  const idx = list.findIndex(p => p.id === activePrescription.value.id)
  
  if (idx !== -1) {
    list[idx].status = 'Đã nhận thuốc'
    
    // Deduct stock quantities from central warehouse
    deductStock(list[idx].medicines, true) // Prescribed medicines were held
    if (list[idx].extraMedicines) {
      deductStock(list[idx].extraMedicines, false) // Extra medicines were not held
    }

    prescriptions.value = list
    savePrescriptionsToStorage()
    
    receiptPrescription.value = list[idx]
    activePrescription.value = list[idx]
    
    calculateStats()
    syncQueueList()
    
    if (currentCallingPatient.value && currentCallingPatient.value.code === activePrescription.value.code) {
      currentCallingPatient.value = null
    }

    currentDispenseStep.value = 'complete'
  }
}

function deductStock(medicines, isHeld = false) {
  let updatedStock = [...stock.value]
  medicines.forEach(med => {
    const targetIdx = updatedStock.findIndex(s => s.name.toLowerCase() === med.name.toLowerCase())
    if (targetIdx !== -1) {
      updatedStock[targetIdx].qty = Math.max(0, updatedStock[targetIdx].qty - med.qty)
      if (isHeld) {
        updatedStock[targetIdx].heldQty = Math.max(0, (updatedStock[targetIdx].heldQty || 0) - med.qty)
      }
    }
  })
  stock.value = updatedStock
  localStorage.setItem('warehouseStock', JSON.stringify(updatedStock))
}

function requestPrescriptionEdit(reason) {
  if (!activePrescription.value) return
  
  const p = activePrescription.value
  
  // 1. Release held stock quantities for all prescribed medicines
  const stockData = localStorage.getItem('warehouseStock')
  if (stockData) {
    let warehouseStock = JSON.parse(stockData)
    p.medicines.forEach(med => {
      const stockItem = warehouseStock.find(s => s.name.toLowerCase() === med.name.toLowerCase())
      if (stockItem) {
        stockItem.heldQty = Math.max(0, (stockItem.heldQty || 0) - med.qty)
      }
    })
    localStorage.setItem('warehouseStock', JSON.stringify(warehouseStock))
    // Sync local stock value
    stock.value = warehouseStock.map(item => {
      const match = stock.value.find(s => s.name.toLowerCase() === item.name.toLowerCase())
      return { ...match, ...item }
    })
  }

  // 2. Set patient booking status to 'Yêu cầu sửa đơn'
  const bookingsData = localStorage.getItem('patientBookings')
  if (bookingsData) {
    let bookingsList = JSON.parse(bookingsData)
    // Find booking matching patientName or prescriptionCode
    const bIdx = bookingsList.findIndex(b => b.prescriptionCode === p.code || b.fullName === p.patientName)
    if (bIdx !== -1) {
      bookingsList[bIdx].status = 'Yêu cầu sửa đơn'
      bookingsList[bIdx].needEditReason = reason || 'Thiếu thuốc thực tế tại quầy'
      localStorage.setItem('patientBookings', JSON.stringify(bookingsList))
    }
  }

  // 3. Set prescription status to 'Yêu cầu sửa đơn'
  const list = [...prescriptions.value]
  const idx = list.findIndex(pr => pr.code === p.code)
  if (idx !== -1) {
    list[idx].status = 'Yêu cầu sửa đơn'
    list[idx].needEditReason = reason || 'Thiếu thuốc thực tế tại quầy'
    prescriptions.value = list
    savePrescriptionsToStorage()
  }

  alert(`📢 Đã gửi yêu cầu sửa đơn thuốc cho Bác sĩ!\nLý do: ${reason}\nHàng giữ tạm thời đã được giải phóng.`)
  resetDispenseFlow()
}

function savePrescriptionsToStorage() {
  localStorage.setItem('patientPrescriptions', JSON.stringify(prescriptions.value))
}

// Queue system sync
function syncQueueList() {
  queueList.value = prescriptions.value
    .filter(p => p.status === 'Chờ gọi tên' || p.status === 'Đang soạn thuốc')
    .map((p, idx) => ({
      code: p.code,
      name: p.patientName,
      status: p.status,
      stt: 100 + idx
    }))
}

// Call out loud patient name using Web Speech API (Vietnamese TTS)
function callPatientName() {
  if (!activePrescription.value) return
  
  const p = activePrescription.value
  currentCallingPatient.value = {
    code: p.code,
    name: p.patientName,
    counter: counterNumber.value
  }

  if (p.status === 'Chờ gọi tên') {
    p.status = 'Đang soạn thuốc'
    savePrescriptionsToStorage()
    syncQueueList()
  }

  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel() 
    const speechText = `Xin mời bệnh nhân ${p.patientName}, sở hữu mã số đơn thuốc ${p.code.replace('DT-', 'Đê Tê ')}, đến quầy số ${counterNumber.value} để nhận thuốc.`
    const utterance = new SpeechSynthesisUtterance(speechText)
    utterance.lang = 'vi-VN'
    utterance.rate = 0.85 
    utterance.volume = 1.0

    const voices = window.speechSynthesis.getVoices()
    const viVoice = voices.find(v => v.lang === 'vi-VN' || v.name.toLowerCase().includes('vietnamese') || v.name.toLowerCase().includes('an'))
    if (viVoice) {
      utterance.voice = viVoice
    }
    window.speechSynthesis.speak(utterance)
  }
}

// Reset workspace dispensing steps to initial
function resetDispenseFlow() {
  activePrescription.value = null
  currentDispenseStep.value = 'detail'
}

// Edit Profile function
function startEditProfile() {
  editForm.value = {
    fullName: currentUser.value.fullName,
    phone: currentUser.value.phone,
    email: currentUser.value.email,
    specialty: currentUser.value.specialty,
    department: currentUser.value.department
  }
  isEditingProfile.value = true
}

function saveProfile() {
  currentUser.value = {
    ...currentUser.value,
    fullName: editForm.value.fullName,
    phone: editForm.value.phone,
    email: editForm.value.email,
    specialty: editForm.value.specialty,
    department: editForm.value.department
  }
  localStorage.setItem('currentUser', JSON.stringify(currentUser.value))
  isEditingProfile.value = false
  alert('Thông tin cá nhân đã được cập nhật thành công!')
}

// Calculations for billing details
const activePrescriptionCosts = computed(() => {
  if (!activePrescription.value) return { base: 0, extra: 0, total: 0 }
  
  let baseCost = 0
  // use either verifiedMedicines or raw prescription medicines depending on step
  const targetMeds = currentDispenseStep.value === 'verify' ? verifiedMedicines.value : activePrescription.value.medicines
  targetMeds.forEach(m => {
    // prescribed medicines under insurance co-pay (mocked at 1500đ average or custom price)
    const price = getStockPrice(m.name)
    // base patient co-pay is 20% of medicine value
    baseCost += Math.round(m.qty * price * 0.20)
  })

  let extraCost = 0
  if (activePrescription.value.extraMedicines) {
    activePrescription.value.extraMedicines.forEach(em => {
      extraCost += em.qty * em.price
    })
  }

  return {
    base: baseCost,
    extra: extraCost,
    total: baseCost + extraCost
  }
})

// Change calculator
const changeAmountDue = computed(() => {
  return cashAmountGiven.value - activePrescriptionCosts.value.total
})

function getStockPrice(medName) {
  const item = stock.value.find(s => s.name.toLowerCase() === medName.toLowerCase())
  return item ? item.price : 5000
}

watch(prescriptions, () => {
  syncQueueList()
}, { deep: true })
</script>

<template>
  <div class="min-h-[calc(100vh-112px)] bg-blue-50/50 text-slate-800 flex flex-col md:flex-row font-sans">
    
    <!-- Sidebar Navigation -->
    <aside class="w-full md:w-64 bg-white border-b md:border-b-0 md:border-r border-blue-100 flex flex-col justify-between p-4 flex-shrink-0 shadow-sm">
      <div class="space-y-6">
        <!-- Logo Header -->
        <div class="flex items-center gap-3 p-2 pb-4 border-b border-blue-50">
          <div class="w-10 h-10 rounded-xl bg-blue-100/60 border border-blue-200/50 flex items-center justify-center shadow-sm text-blue-600">
            <span class="text-2xl">💊</span>
          </div>
          <div>
            <h4 class="text-sm font-bold tracking-wide text-blue-700">NHÀ THUỐC BỆNH VIỆN</h4>
            <span class="text-[10px] text-slate-500 block font-medium truncate max-w-[140px]">{{ currentUser.fullName }}</span>
          </div>
        </div>

        <!-- Navigation Menu -->
        <nav class="space-y-1.5">
          <button 
            @click="activeMenu = 'prescriptions'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all duration-200"
            :class="activeMenu === 'prescriptions' ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-600 shadow-sm' : 'text-slate-500 hover:text-blue-600 hover:bg-blue-50/40'"
          >
            <i class="bi bi-prescription2 text-lg"></i>
            Xử lý đơn thuốc
          </button>
          
          <button 
            @click="activeMenu = 'queue'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all duration-200"
            :class="activeMenu === 'queue' ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-600 shadow-sm' : 'text-slate-500 hover:text-blue-600 hover:bg-blue-50/40'"
          >
            <i class="bi bi-display text-lg"></i>
            Thứ tự lấy thuốc
          </button>

          <button 
            @click="activeMenu = 'schedule'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all duration-200"
            :class="activeMenu === 'schedule' ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-600 shadow-sm' : 'text-slate-500 hover:text-blue-600 hover:bg-blue-50/40'"
          >
            <i class="bi bi-calendar-event text-lg"></i>
            Lịch trực tuần
          </button>

          <button 
            @click="activeMenu = 'profile'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all duration-200"
            :class="activeMenu === 'profile' ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-600 shadow-sm' : 'text-slate-500 hover:text-blue-600 hover:bg-blue-50/40'"
          >
            <i class="bi bi-person-badge text-lg"></i>
            Thẻ nhân viên
          </button>

          <button 
            @click="activeMenu = 'reports'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all duration-200"
            :class="activeMenu === 'reports' ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-600 shadow-sm' : 'text-slate-500 hover:text-blue-600 hover:bg-blue-50/40'"
          >
            <i class="bi bi-pie-chart text-lg"></i>
            Báo cáo quầy thuốc
          </button>
        </nav>
      </div>

      <div class="p-2 border-t border-blue-50 text-[10px] text-slate-400 text-center font-medium mt-6 md:mt-0">
        Hospital Smart Pharmacy v2.5
      </div>
    </aside>

    <!-- Main Content Area -->
    <main class="flex-1 p-4 md:p-8 overflow-y-auto bg-blue-50/40">
      
      <!-- Counters/Widgets top bar -->
      <div v-if="activeMenu !== 'queue'" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8 animate-fade-in">
        <div class="bg-white p-4.5 rounded-2xl border border-blue-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Đơn Chờ Phát</span>
            <h4 class="text-xl font-black text-amber-600 mt-1">{{ summaryStats.pendingDispense }} toa</h4>
          </div>
          <span class="text-2xl bg-amber-50 p-2.5 rounded-xl border border-amber-100 text-amber-600">📥</span>
        </div>
        <div class="bg-white p-4.5 rounded-2xl border border-blue-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Đã Cấp Hôm Nay</span>
            <h4 class="text-xl font-black text-blue-600 mt-1">{{ summaryStats.totalDispensed }} toa</h4>
          </div>
          <span class="text-2xl bg-blue-50 p-2.5 rounded-xl border border-blue-100 text-blue-600">📤</span>
        </div>
        <div class="bg-white p-4.5 rounded-2xl border border-blue-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Bán Thuốc Mua Kèm</span>
            <h4 class="text-xl font-black text-emerald-600 mt-1">{{ summaryStats.todayRevenue.toLocaleString('vi-VN') }}đ</h4>
          </div>
          <span class="text-2xl bg-emerald-50 p-2.5 rounded-xl border border-emerald-100 text-emerald-650">💰</span>
        </div>
        <div class="bg-white p-4.5 rounded-2xl border border-blue-100 shadow-sm flex items-center justify-between"
             :class="summaryStats.alertStockCount > 0 ? 'border-rose-250 bg-rose-50/30' : ''">
          <div>
            <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Cảnh báo tồn quầy</span>
            <h4 class="text-xl font-black mt-1" :class="summaryStats.alertStockCount > 0 ? 'text-rose-600 font-black' : 'text-slate-650'">
              {{ summaryStats.alertStockCount }} loại thuốc
            </h4>
          </div>
          <span class="text-2xl p-2.5 rounded-xl border"
                :class="summaryStats.alertStockCount > 0 ? 'bg-rose-50 border-rose-105 text-rose-550' : 'bg-slate-50 border-slate-200 text-slate-500'">
            ⚠️
          </span>
        </div>
      </div>

      <!-- ==================== TAB 1: PRESCRIPTIONS & WORKSPACE ==================== -->
      <div v-if="activeMenu === 'prescriptions'" class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start animate-fade-in">
        
        <!-- Prescriptions list sidebar (Left Col) -->
        <div class="lg:col-span-4 bg-white p-4 rounded-3xl border border-blue-100 shadow-sm space-y-4">
          <div class="flex justify-between items-center border-b border-blue-50 pb-2">
            <h2 class="text-sm font-bold text-slate-800">Đơn Thuốc Bác Sĩ Gửi Về</h2>
            <span class="text-[10px] bg-blue-50 text-blue-600 px-2 py-0.5 rounded font-mono">Hôm nay</span>
          </div>

          <!-- Search controls -->
          <div class="space-y-2">
            <div class="relative">
              <input 
                v-model="searchPrescriptionQuery"
                type="text" 
                placeholder="Tìm mã đơn, tên bệnh nhân..." 
                class="w-full text-xs bg-slate-55 border border-blue-100/80 rounded-xl px-9 py-2 text-slate-800 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/30 placeholder:text-slate-450"
              />
              <i class="bi bi-search absolute left-3 top-2.5 text-slate-400 text-xs"></i>
            </div>
          </div>

          <!-- Prescription Item list -->
          <div class="space-y-2 max-h-[500px] overflow-y-auto pr-1">
            <div 
              v-for="p in filteredPrescriptions" 
              :key="p.id"
              @click="selectPrescription(p)"
              class="p-3.5 rounded-2xl border transition-all cursor-pointer group"
              :class="activePrescription?.id === p.id 
                ? 'bg-blue-50/80 border-blue-400/60 shadow-sm' 
                : 'border-slate-100 bg-slate-50/50 hover:bg-white hover:border-blue-200 hover:shadow-sm'"
            >
              <div class="flex justify-between items-center mb-1.5">
                <span class="font-bold text-xs font-mono text-blue-600 group-hover:text-blue-700">{{ p.code }}</span>
                <span class="text-[9px] font-extrabold px-2 py-0.5 rounded uppercase tracking-wider"
                      :class="p.status === 'Đã nhận thuốc' 
                        ? 'bg-emerald-100 text-emerald-800 border border-emerald-200' 
                        : p.status === 'Đang soạn thuốc'
                          ? 'bg-blue-100 text-blue-800 border border-blue-200'
                          : 'bg-amber-100 text-amber-800 border border-amber-200'">
                  {{ p.status }}
                </span>
              </div>
              <div class="flex justify-between text-[11px] text-slate-600">
                <span class="font-semibold text-slate-800">{{ p.patientName }} ({{ p.patientAge }}t - {{ p.patientGender }})</span>
                <span class="font-mono text-[9px] text-slate-450">{{ p.date }}</span>
              </div>
              <p class="text-[10px] text-slate-500 mt-1 italic truncate">Chẩn đoán: {{ p.diagnose }}</p>
            </div>

            <div v-if="filteredPrescriptions.length === 0" class="text-center py-12 text-slate-400 text-xs">
              <span class="text-2xl block mb-2">🔍</span>
              Không tìm thấy đơn thuốc phù hợp.
            </div>
          </div>
        </div>

        <!-- Dispensing Workspace (Right Col) -->
        <div class="lg:col-span-8 bg-white p-6 rounded-3xl border border-blue-100 shadow-sm min-h-[500px] flex flex-col justify-between">
          <div v-if="activePrescription" class="space-y-6 flex-1 flex flex-col justify-between animate-fade-in">
            
            <!-- STEP-BY-STEP PROGRESS BAR -->
            <div class="flex items-center justify-between border-b border-blue-50 pb-4">
              <div class="flex items-center gap-3">
                <span class="font-bold text-xs text-blue-800">Quy trình cấp phát:</span>
                <div class="flex items-center gap-2">
                  <span class="px-2.5 py-1 text-[10px] font-bold rounded-lg"
                        :class="currentDispenseStep === 'detail' ? 'bg-blue-600 text-white shadow-sm' : 'bg-blue-50 text-blue-700'">
                    1. Chi tiết đơn
                  </span>
                  <span class="text-slate-400 text-[10px]">/</span>
                  <span class="px-2.5 py-1 text-[10px] font-bold rounded-lg"
                        :class="currentDispenseStep === 'verify' ? 'bg-blue-600 text-white shadow-sm' : 'bg-blue-50 text-blue-700'">
                    2. Soạn & Nhập số lượng
                  </span>
                  <span class="text-slate-400 text-[10px]">/</span>
                  <span class="px-2.5 py-1 text-[10px] font-bold rounded-lg"
                        :class="currentDispenseStep === 'payment' ? 'bg-blue-600 text-white shadow-sm' : 'bg-blue-50 text-blue-700'">
                    3. Thanh toán
                  </span>
                  <span class="text-slate-400 text-[10px]">/</span>
                  <span class="px-2.5 py-1 text-[10px] font-bold rounded-lg"
                        :class="currentDispenseStep === 'complete' ? 'bg-blue-600 text-white shadow-sm' : 'bg-blue-50 text-blue-700'">
                    4. In nhãn & hóa đơn
                  </span>
                </div>
              </div>
              <button 
                @click="resetDispenseFlow" 
                class="text-xs font-bold text-slate-500 hover:text-slate-700 px-3 py-1 bg-slate-100 rounded-lg"
              >
                Đóng
              </button>
            </div>

            <!-- ==================== DISPENSING STEP: 1. DETAILS ==================== -->
            <div v-if="currentDispenseStep === 'detail'" class="space-y-6 flex-1 flex flex-col justify-between">
              <div class="space-y-6">
                <!-- Patient & Prescription details -->
                <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center border-b border-blue-50 pb-3 gap-2">
                  <div>
                    <h3 class="text-base font-extrabold text-slate-800 flex items-center gap-2">
                      Đơn thuốc: <span class="font-mono text-blue-600 font-black text-lg">{{ activePrescription.code }}</span>
                    </h3>
                    <p class="text-xs text-slate-500 mt-0.5">
                      Bệnh nhân: <strong class="text-slate-800">{{ activePrescription.patientName }}</strong> ({{ activePrescription.patientAge }} tuổi) | Bác sĩ: <span class="italic text-slate-700 font-semibold">{{ activePrescription.doctor }}</span>
                    </p>
                  </div>
                  <span class="text-[10px] font-bold px-3 py-1 rounded-full uppercase bg-amber-100 text-amber-800 border border-amber-200">
                    {{ activePrescription.status }}
                  </span>
                </div>

                <div class="bg-blue-50/60 p-3.5 rounded-2xl border border-blue-100/50 text-xs">
                  <span class="text-[9px] text-blue-600/85 font-bold uppercase block tracking-wider mb-0.5">Chẩn đoán từ bác sĩ:</span>
                  <p class="font-semibold text-slate-800 italic">"{{ activePrescription.diagnose }}"</p>
                </div>

                <!-- Prescription Medicines summary -->
                <div class="space-y-3">
                  <h4 class="text-xs font-bold text-blue-700 uppercase tracking-wider">Danh mục thuốc BHYT chỉ định:</h4>
                  <div class="border border-blue-100 rounded-2xl overflow-hidden shadow-sm">
                    <table class="w-full text-left text-xs border-collapse">
                      <thead class="bg-blue-50 border-b border-blue-100 font-bold text-slate-650">
                        <tr>
                          <th class="py-2.5 px-4">Tên thuốc</th>
                          <th class="py-2.5 px-4 text-center">Số lượng kê đơn</th>
                          <th class="py-2.5 px-4">Đơn vị</th>
                          <th class="py-2.5 px-4">Hướng dẫn sử dụng chi tiết</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-blue-50 text-slate-700 bg-white">
                        <tr v-for="m in activePrescription.medicines" :key="m.name">
                          <td class="py-3 px-4 font-bold text-slate-800">{{ m.name }}</td>
                          <td class="py-3 px-4 text-center font-mono font-bold text-blue-600">{{ m.qty }}</td>
                          <td class="py-3 px-4 text-slate-550">{{ m.unit }}</td>
                          <td class="py-3 px-4 text-slate-500 font-medium italic">{{ m.use }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <!-- Dispensing detail bottom bar -->
              <div class="border-t border-blue-50 pt-5 flex justify-end gap-3">
                <button 
                  @click="callPatientName"
                  class="py-3 px-5 bg-blue-50 hover:bg-blue-100 text-blue-750 font-bold rounded-2xl text-xs transition-all border border-blue-200 shadow-sm flex items-center gap-2"
                >
                  <i class="bi bi-megaphone"></i> Gọi tên bệnh nhân nhận thuốc
                </button>
                <button 
                  v-if="activePrescription.status !== 'Đã nhận thuốc'"
                  @click="startDispenseFlow"
                  class="py-3 px-6 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-2xl text-xs shadow-md transition-all flex items-center gap-2"
                >
                  <i class="bi bi-play-fill text-sm"></i> Bắt đầu soạn & lấy thuốc
                </button>
                <button 
                  v-else
                  @click="receiptPrescription = activePrescription; showReceiptModal = true"
                  class="py-3 px-5 bg-slate-100 hover:bg-slate-200 border border-slate-250 text-slate-700 font-bold rounded-2xl text-xs transition-all flex items-center gap-2"
                >
                  <i class="bi bi-printer"></i> In hóa đơn biên lai
                </button>
              </div>
            </div>

            <!-- ==================== DISPENSING STEP: 2. VERIFY QUANTITIES & REPLACE ==================== -->
            <div v-else-if="currentDispenseStep === 'verify'" class="space-y-6 flex-1 flex flex-col justify-between">
              <div class="space-y-6">
                <div class="flex justify-between items-center border-b border-blue-50 pb-2">
                  <h4 class="text-xs font-bold text-blue-700 uppercase tracking-wider">Bước 2: Soạn thuốc & Nhập số lượng thực tế cấp phát:</h4>
                  <span class="text-[10px] text-slate-450 font-medium">* Bác có thể sửa số lượng nếu cấp lẻ hoặc hết thuốc</span>
                </div>

                <!-- Medicines verification table -->
                <div class="border border-blue-100 rounded-2xl overflow-hidden shadow-sm">
                  <table class="w-full text-left text-xs border-collapse">
                    <thead class="bg-blue-50 border-b border-blue-100 font-bold text-slate-650">
                      <tr>
                        <th class="py-2.5 px-4">Tên thuốc chỉ định</th>
                        <th class="py-2.5 px-3 text-center">Đơn kê</th>
                        <th class="py-2.5 px-3 text-center">Thực cấp</th>
                        <th class="py-2.5 px-4">Tồn kho quầy</th>
                        <th class="py-2.5 px-4 text-center">Tác vụ</th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-blue-50 text-slate-700 bg-white">
                      <tr v-for="m in verifiedMedicines" :key="m.name">
                        <td class="py-3 px-4">
                          <p class="font-bold text-slate-800">{{ m.name }}</p>
                          <p class="text-[10px] text-slate-500 mt-0.5 leading-normal font-mono italic">{{ m.use }}</p>
                        </td>
                        <td class="py-3 px-3 text-center font-mono font-semibold text-slate-600">
                          {{ m.qty }} <span class="text-[10px] text-slate-400 font-normal">{{ m.unit }}</span>
                        </td>
                        <td class="py-3 px-3 text-center">
                          <!-- INPUT QUANTITY TO COLLECT -->
                          <div class="flex items-center justify-center gap-1">
                            <input 
                              type="number" 
                              min="0" 
                              v-model.number="m.verifiedQty"
                              class="w-16 bg-blue-50/50 border border-blue-100 rounded-lg p-1.5 text-center text-blue-750 font-bold text-xs font-mono focus:outline-none focus:border-blue-400"
                            />
                            <span class="text-[10px] text-slate-500">{{ m.unit }}</span>
                          </div>
                        </td>
                        <td class="py-3 px-4">
                          <div class="flex items-center gap-2">
                            <span class="inline-block w-2 h-2 rounded-full" 
                                  :class="checkStockSufficiency(m.name, m.verifiedQty).ok ? 'bg-emerald-500' : 'bg-rose-500'">
                            </span>
                            <span class="font-bold text-[11px]" 
                                  :class="checkStockSufficiency(m.name, m.verifiedQty).ok ? 'text-emerald-600' : 'text-rose-600'">
                              {{ checkStockSufficiency(m.name, m.verifiedQty).text }}
                            </span>
                          </div>
                        </td>
                        <td class="py-3 px-4 text-center">
                          <button 
                            v-if="!checkStockSufficiency(m.name, m.verifiedQty).ok"
                            @click="openReplaceModal(m)"
                            class="py-1 px-2.5 bg-rose-50 hover:bg-rose-100 border border-rose-200 text-rose-600 font-bold rounded-lg text-[10px] transition-all"
                          >
                            Đổi thuốc thay thế
                          </button>
                          <span v-else class="text-[10px] text-emerald-600 font-bold">✓ Đủ thuốc</span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <!-- Active drug replacement requests -->
                <div v-if="activePrescription.replacementRequests && activePrescription.replacementRequests.length > 0" class="space-y-2">
                  <h5 class="text-xs font-bold text-amber-600 flex items-center gap-1.5">
                    <i class="bi bi-clock-history"></i> Trạng thái đổi thuốc với bác sĩ kê đơn:
                  </h5>
                  <div class="space-y-2">
                    <div 
                      v-for="req in activePrescription.replacementRequests" 
                      :key="req.id"
                      class="bg-amber-50/50 border border-amber-100 p-3 rounded-2xl text-xs flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3"
                    >
                      <div>
                        <div class="flex items-center gap-2 mb-1">
                          <span class="font-bold text-slate-800">Đổi: {{ req.originalMed }}</span>
                          <span class="text-slate-400 text-[10px]">⟶</span>
                          <span class="font-bold text-blue-600">{{ req.replacementMed }} (SL: {{ req.qty }})</span>
                        </div>
                        <p class="text-[10px] text-slate-500">Lý do: <span class="italic">"{{ req.note }}"</span></p>
                      </div>
                      <div class="flex items-center gap-2">
                        <span class="text-[10px] font-bold px-2 py-0.5 rounded"
                              :class="req.status === 'Đã duyệt' ? 'bg-emerald-100 text-emerald-800 border border-emerald-200' : 'bg-amber-100 text-amber-800 border border-amber-200'">
                          {{ req.status }}
                        </span>
                        <button 
                          v-if="req.status === 'Chờ duyệt'"
                          @click="simulateDoctorApproval(req.id)"
                          class="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg text-[10px] shadow transition-colors flex items-center gap-1"
                        >
                          ⚡ Mô phỏng Bác sĩ duyệt
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Patient requests additional medicine (Mua thêm) -->
                <div class="space-y-3 pt-2">
                  <div class="flex justify-between items-center border-t border-blue-50 pt-4">
                    <h4 class="text-xs font-bold text-emerald-600 uppercase tracking-wider flex items-center gap-1.5">
                      <i class="bi bi-cart-plus-fill"></i> Bệnh nhân có yêu cầu mua thêm thuốc ngoài đơn:
                    </h4>
                    <button 
                      @click="showAddExtraForm = !showAddExtraForm"
                      class="py-1 px-3 border border-emerald-250 hover:border-emerald-500 bg-emerald-50 text-emerald-600 font-bold rounded-xl text-[10px] transition-colors"
                    >
                      {{ showAddExtraForm ? 'Hủy bỏ' : '+ Thêm thuốc mua kèm' }}
                    </button>
                  </div>

                  <!-- Add extra medicine form -->
                  <div v-if="showAddExtraForm" class="bg-emerald-50/30 border border-emerald-100 p-4 rounded-2xl space-y-3.5 animate-fade-in text-xs">
                    <div class="grid grid-cols-1 md:grid-cols-12 gap-3.5 items-end">
                      <div class="md:col-span-6">
                        <label for="extra-med-select-step" class="block text-[10px] text-slate-500 font-bold uppercase mb-1">Chọn dược phẩm từ kho</label>
                        <select id="extra-med-select-step" v-model="selectedExtraMed" class="w-full bg-white border border-blue-100 rounded-xl p-2.5 text-slate-750">
                          <option value="" disabled>-- Chọn thuốc còn hàng --</option>
                          <option v-for="item in availableExtraMedicines" :key="item.name" :value="item.name">
                            {{ item.name }} (Còn: {{ item.qty }} {{ item.unit }} - {{ item.price.toLocaleString('vi-VN') }}đ)
                          </option>
                        </select>
                      </div>
                      <div class="md:col-span-3">
                        <label for="extra-qty-step" class="block text-[10px] text-slate-500 font-bold uppercase mb-1 text-center">Số lượng mua</label>
                        <input id="extra-qty-step" type="number" min="1" v-model="extraQty" class="w-full bg-white border border-blue-100 rounded-xl p-2 text-center text-slate-750" />
                      </div>
                      <div class="md:col-span-3">
                        <button 
                          @click="addExtraMedicine"
                          type="button" 
                          class="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl shadow transition-colors"
                        >
                          Thêm vào danh sách
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- Extra Medicines Table -->
                  <div v-if="activePrescription.extraMedicines && activePrescription.extraMedicines.length > 0" class="border border-blue-100 rounded-2xl overflow-hidden shadow-sm">
                    <table class="w-full text-left text-xs border-collapse">
                      <thead class="bg-blue-50 border-b border-blue-100 font-bold text-slate-650">
                        <tr>
                          <th class="py-2.5 px-4">Tên thuốc mua kèm thêm</th>
                          <th class="py-2.5 px-3 text-center">Số lượng</th>
                          <th class="py-2.5 px-4 text-right">Đơn giá</th>
                          <th class="py-2.5 px-4 text-right">Thành tiền</th>
                          <th class="py-2.5 px-4 text-center">Xóa</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-blue-50 text-slate-600 bg-white">
                        <tr v-for="(em, idx) in activePrescription.extraMedicines" :key="em.name">
                          <td class="py-3 px-4 font-bold text-slate-800">{{ em.name }}</td>
                          <td class="py-3 px-3 text-center font-mono font-bold text-emerald-600">
                            {{ em.qty }} <span class="text-[10px] text-slate-500 font-normal">{{ em.unit }}</span>
                          </td>
                          <td class="py-3 px-4 text-right font-mono font-medium">{{ em.price.toLocaleString('vi-VN') }}đ</td>
                          <td class="py-3 px-4 text-right font-mono font-bold text-emerald-600">{{ (em.qty * em.price).toLocaleString('vi-VN') }}đ</td>
                          <td class="py-3 px-4 text-center">
                            <button @click="removeExtraMedicine(idx)" class="text-rose-500 hover:bg-rose-50 p-1.5 rounded-lg transition-colors">
                              <i class="bi bi-trash"></i>
                            </button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <!-- Navigation verification controls -->
              <div class="border-t border-blue-50 pt-5 flex justify-between items-center">
                <div class="flex gap-2">
                  <button 
                    @click="currentDispenseStep = 'detail'"
                    class="py-2.5 px-5 border border-slate-200 hover:bg-slate-50 text-slate-600 font-bold rounded-xl text-xs"
                  >
                     Quay lại bước 1
                  </button>
                  <button 
                    @click="() => {
                      const reason = prompt('Nhập lý do thiếu thuốc / yêu cầu sửa đơn:', 'Thiếu thuốc Panadol Extra 500mg thực tế tại quầy');
                      if (reason) requestPrescriptionEdit(reason);
                    }"
                    class="py-2.5 px-5 bg-rose-50 hover:bg-rose-100 border border-rose-200 text-rose-600 font-bold rounded-xl text-xs flex items-center gap-1.5"
                  >
                    <i class="bi bi-exclamation-triangle-fill text-rose-600"></i> Báo thiếu thuốc & Yêu cầu đổi đơn
                  </button>
                </div>
                <button 
                  @click="proceedToPaymentStep"
                  class="py-3 px-6 bg-blue-600 hover:bg-blue-755 text-white font-bold rounded-2xl text-xs shadow-md flex items-center gap-1.5"
                >
                  Xác nhận & Chuyển sang thanh toán <i class="bi bi-arrow-right-short text-base"></i>
                </button>
              </div>
            </div>

            <!-- ==================== DISPENSING STEP: 3. PAYMENT CHECKOUT ==================== -->
            <div v-else-if="currentDispenseStep === 'payment'" class="space-y-6 flex-1 flex flex-col justify-between">
              <div class="space-y-6">
                <div class="flex justify-between items-center border-b border-blue-50 pb-2">
                  <h4 class="text-xs font-bold text-blue-700 uppercase tracking-wider">Bước 3: Thực hiện thu phí & thanh toán quầy thuốc</h4>
                  <span class="text-[10px] text-slate-450 font-medium">BHYT đã khấu trừ 80% giá trị toa gốc</span>
                </div>

                <!-- Cost summary card -->
                <div class="bg-blue-50/50 border border-blue-100 rounded-3xl p-5 grid grid-cols-1 md:grid-cols-3 gap-4.5 text-xs text-slate-700">
                  <div class="space-y-1">
                    <span class="text-[9px] text-slate-500 uppercase block font-bold tracking-wider">Tiền toa gốc (Co-pay 20%):</span>
                    <p class="font-mono text-sm font-bold text-slate-800">{{ activePrescriptionCosts.base.toLocaleString('vi-VN') }}đ</p>
                  </div>
                  <div class="space-y-1">
                    <span class="text-[9px] text-slate-500 uppercase block font-bold tracking-wider">Tiền mua kèm ngoài toa:</span>
                    <p class="font-mono text-sm font-bold text-emerald-600">+{{ activePrescriptionCosts.extra.toLocaleString('vi-VN') }}đ</p>
                  </div>
                  <div class="space-y-1 bg-white p-2.5 rounded-xl border border-blue-100">
                    <span class="text-[9px] text-blue-600 uppercase block font-black tracking-wider">TỔNG TIỀN PHẢI THU:</span>
                    <p class="font-mono text-base font-black text-blue-600">{{ activePrescriptionCosts.total.toLocaleString('vi-VN') }}đ</p>
                  </div>
                </div>

                <!-- Select payment method -->
                <div class="space-y-3">
                  <span class="block text-xs font-bold text-slate-800 uppercase tracking-wider">Phương thức thanh toán:</span>
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Cash Option -->
                    <label class="p-4 border rounded-2xl cursor-pointer flex flex-col justify-between transition-all"
                           :class="paymentMethod === 'cash' ? 'bg-blue-50 border-blue-500 text-blue-800 shadow-sm font-bold' : 'border-slate-200 text-slate-600 bg-white hover:bg-slate-50/50'">
                      <div class="flex items-center justify-between">
                        <span class="flex items-center gap-2 text-xs">
                          <input type="radio" value="cash" v-model="paymentMethod" class="text-blue-600" />
                          💵 Tiền mặt tại quầy
                        </span>
                        <span class="text-[10px] text-slate-450 font-normal">Thu ngân trực tiếp</span>
                      </div>
                      <div v-if="paymentMethod === 'cash'" class="mt-4 pt-3 border-t border-blue-100 text-xs text-slate-700 space-y-3 animate-fade-in">
                        <div>
                          <label class="block text-[9px] text-slate-500 uppercase font-bold mb-1">Số tiền mặt khách đưa:</label>
                          <input 
                            type="number" 
                            v-model.number="cashAmountGiven" 
                            class="w-full bg-white border border-blue-200 rounded-lg p-2 font-mono font-bold text-blue-750 focus:outline-none"
                          />
                        </div>
                        <div class="flex justify-between items-center text-xs pt-1">
                          <span class="font-semibold text-slate-600">Tiền thừa trả lại:</span>
                          <span class="font-mono font-black text-xs" :class="changeAmountDue >= 0 ? 'text-teal-650' : 'text-rose-600'">
                            {{ changeAmountDue >= 0 ? changeAmountDue.toLocaleString('vi-VN') + 'đ' : 'Không đủ (Thiếu ' + Math.abs(changeAmountDue).toLocaleString('vi-VN') + 'đ)' }}
                          </span>
                        </div>
                      </div>
                    </label>

                    <!-- Online/QR Code Option -->
                    <label class="p-4 border rounded-2xl cursor-pointer flex flex-col justify-between transition-all"
                           :class="paymentMethod === 'online' ? 'bg-blue-50 border-blue-500 text-blue-800 shadow-sm font-bold' : 'border-slate-200 text-slate-600 bg-white hover:bg-slate-50/50'">
                      <div class="flex items-center justify-between">
                        <span class="flex items-center gap-2 text-xs">
                          <input type="radio" value="online" v-model="paymentMethod" class="text-blue-600" />
                          📱 Thanh toán Online quét mã QR
                        </span>
                        <span class="text-[10px] text-slate-450 font-normal">Chuyển khoản / Ví MoMo</span>
                      </div>
                      
                      <div v-if="paymentMethod === 'online'" class="mt-4 pt-3 border-t border-blue-100 text-xs text-slate-700 space-y-3 animate-fade-in flex flex-col items-center">
                        <span class="text-[10px] text-slate-500 font-bold block mb-1">Hệ thống VietQR Bệnh viện</span>
                        
                        <!-- Visual VietQR mockup box -->
                        <div class="border border-blue-100 bg-white p-3 rounded-xl flex flex-col items-center gap-2.5 shadow-sm w-44">
                          <div class="relative w-28 h-28 bg-slate-100 border border-slate-200 rounded flex items-center justify-center overflow-hidden">
                            <!-- Simulated QR grid drawing using CSS gradient/icon -->
                            <img src="https://i.ibb.co/tPpV2c2Y/qr-mock.png" alt="Mock QR Code" class="w-full h-full object-contain" />
                            
                            <div v-if="onlinePaymentStatus === 'processing'" class="absolute inset-0 bg-white/80 flex flex-col items-center justify-center gap-1.5">
                              <span class="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></span>
                              <span class="text-[8px] text-blue-600 font-black animate-pulse">Checking...</span>
                            </div>
                            <div v-else-if="onlinePaymentStatus === 'success'" class="absolute inset-0 bg-emerald-500/90 text-white flex flex-col items-center justify-center font-bold">
                              <i class="bi bi-check-circle-fill text-2xl animate-bounce"></i>
                              <span class="text-[9px] uppercase tracking-wider font-black mt-1">Paid!</span>
                            </div>
                          </div>
                          
                          <!-- Dynamic online status label -->
                          <div class="text-[10px] text-center font-medium font-mono">
                            <span v-if="onlinePaymentStatus === 'waiting'" class="text-amber-600 animate-pulse">⏳ Chờ quét mã...</span>
                            <span v-else-if="onlinePaymentStatus === 'processing'" class="text-blue-600 font-bold">🔍 Đang kiểm tra...</span>
                            <span v-else class="text-emerald-600 font-black">✓ Thành công!</span>
                          </div>
                        </div>

                        <button 
                          v-if="onlinePaymentStatus !== 'success'"
                          type="button"
                          @click="triggerOnlinePaymentScan"
                          class="mt-2 py-1.5 px-4 bg-blue-600 hover:bg-blue-755 text-white font-bold rounded-lg text-[9px] shadow-sm flex items-center gap-1"
                        >
                          ⚡ Mô phỏng quét quét mã & thanh toán
                        </button>
                      </div>
                    </label>
                  </div>
                </div>
              </div>

              <!-- Payment Navigation controls -->
              <div class="border-t border-blue-50 pt-5 flex justify-between items-center">
                <button 
                  @click="currentDispenseStep = 'verify'"
                  class="py-2.5 px-5 border border-slate-200 hover:bg-slate-50 text-slate-600 font-bold rounded-xl text-xs"
                >
                  Quay lại bước 2 (Sửa SL)
                </button>
                <button 
                  @click="confirmDispenseAndCheckout"
                  class="py-3 px-6 bg-blue-600 hover:bg-blue-755 text-white font-bold rounded-2xl text-xs shadow-md flex items-center gap-2"
                >
                  <i class="bi bi-check-circle-fill"></i> Xác nhận thanh toán & Bàn giao
                </button>
              </div>
            </div>

            <!-- ==================== DISPENSING STEP: 4. Dispense Complete & Printing ==================== -->
            <div v-else-if="currentDispenseStep === 'complete'" class="space-y-6 flex-1 flex flex-col justify-between animate-fade-in">
              <div class="flex-1 flex flex-col items-center justify-center text-center space-y-6 py-8">
                <!-- Big Success check icon -->
                <div class="w-20 h-20 rounded-full bg-emerald-50 border-2 border-emerald-100 flex items-center justify-center shadow-lg shadow-emerald-500/10">
                  <span class="text-4xl animate-bounce">🎉</span>
                </div>
                <div class="space-y-1.5 max-w-sm">
                  <h3 class="text-lg font-black text-emerald-650">Cấp phát & Thu phí thành công!</h3>
                  <p class="text-xs text-slate-500 leading-normal">
                    Đơn thuốc <strong class="text-slate-800">{{ receiptPrescription?.code }}</strong> đã chuyển sang trạng thái <strong>Đã nhận thuốc</strong>. Số lượng tồn kho tương ứng đã được khấu trừ trong hệ thống.
                  </p>
                </div>

                <!-- Print Options Layout -->
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 w-full max-w-md pt-2">
                  <!-- Print pocket labels -->
                  <button 
                    @click="showPocketLabelsModal = true"
                    class="p-4 bg-blue-550/5 hover:bg-blue-50 border border-blue-200 rounded-2xl flex flex-col items-center justify-center text-center space-y-2 group transition-all"
                  >
                    <span class="text-2xl group-hover:scale-110 transition-transform">🏷️</span>
                    <span class="font-bold text-xs text-blue-700 block">In nhãn dán bao bì thuốc</span>
                    <span class="text-[9px] text-slate-450 font-normal leading-normal">In nhãn liều dùng cụ thể dán lên túi đựng thuốc</span>
                  </button>

                  <!-- Print invoice checkout -->
                  <button 
                    @click="showReceiptModal = true"
                    class="p-4 bg-teal-500/5 hover:bg-teal-50/50 border border-teal-200 rounded-2xl flex flex-col items-center justify-center text-center space-y-2 group transition-all"
                  >
                    <span class="text-2xl group-hover:scale-110 transition-transform">🧾</span>
                    <span class="font-bold text-xs text-teal-700 block">In hóa đơn thanh toán</span>
                    <span class="text-[9px] text-slate-450 font-normal leading-normal">Hóa đơn giá trị thuốc, tiền BHYT & tiền bán thêm</span>
                  </button>
                </div>
              </div>

              <!-- Close flow control -->
              <div class="border-t border-blue-50 pt-5 flex justify-end">
                <button 
                  @click="resetDispenseFlow"
                  class="py-3 px-8 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-2xl text-xs shadow-md transition-colors"
                >
                  Hoàn tất & Về danh sách quầy
                </button>
              </div>
            </div>

          </div>

          <!-- Empty workspace message -->
          <div v-else class="flex-1 flex flex-col items-center justify-center text-center text-slate-400 p-12">
            <span class="text-5xl mb-4 filter drop-shadow-[0_0_15px_rgba(59,130,246,0.1)] animate-pulse">💊</span>
            <h3 class="text-base font-bold text-slate-700">Chưa chọn đơn thuốc cần phát</h3>
            <p class="text-xs mt-1.5 max-w-xs text-slate-400 leading-normal">
              Bác sĩ Thủy vui lòng chọn một đơn thuốc cần phát ở danh sách bên trái để bắt đầu quy trình kiểm tra số lượng, thanh toán quầy và in hóa đơn thuốc.
            </p>
          </div>
        </div>

      </div>

      <!-- ==================== TAB 2: PATIENT QUEUE BOARD VIEW ==================== -->
      <div v-else-if="activeMenu === 'queue'" class="space-y-6 animate-fade-in">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-xl font-extrabold text-slate-800">Màn Hình Thứ Tự Nhận Thuốc Ngoại Trú</h2>
            <p class="text-xs text-slate-500">Màn hình giám sát và hiển thị tiến trình gọi tên phát thuốc tự động tại Nhà thuốc.</p>
          </div>
          <div class="flex items-center gap-3 bg-white p-2 border border-blue-100 rounded-2xl text-xs shadow-sm">
            <label for="counter-no-q" class="text-slate-500 font-bold">Số quầy trực:</label>
            <input id="counter-no-q" type="number" min="1" max="10" v-model="counterNumber" class="w-12 text-center bg-blue-50 border border-blue-100 rounded-xl text-blue-600 font-bold p-1" />
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-stretch">
          <div class="lg:col-span-8 bg-white p-6 md:p-8 rounded-3xl border border-blue-100 flex flex-col justify-between space-y-6 shadow-md relative overflow-hidden">
            <div class="absolute top-0 right-0 w-80 h-80 bg-blue-500/5 rounded-full blur-3xl -mr-20 -mt-20"></div>

            <div class="space-y-6 z-10 relative">
              <span class="text-[9px] font-black tracking-widest bg-blue-50 text-blue-600 border border-blue-100 px-3 py-1 rounded-full uppercase">
                📢 HỆ THỐNG PHÁT LOA TỰ ĐỘNG (TTS ON)
              </span>

              <div class="space-y-4">
                <h3 class="text-slate-600 text-xs font-bold uppercase tracking-wider">Số Thứ Tự Đang Được Gọi:</h3>
                
                <div v-if="currentCallingPatient" class="bg-blue-50/60 border border-blue-200 rounded-3xl p-6 md:p-8 text-center space-y-4 shadow-sm">
                  <div class="animate-bounce inline-block bg-blue-600 text-white font-black text-xs px-4 py-1.5 rounded-full shadow-lg">
                    XIN MỜI BỆNH NHÂN
                  </div>
                  <h1 class="text-3xl md:text-5xl font-black text-blue-700 font-sans tracking-wide">
                    {{ currentCallingPatient.name }}
                  </h1>
                  <div class="flex justify-center items-center gap-8 mt-2">
                    <div>
                      <span class="text-[10px] text-slate-500 uppercase block font-bold">Mã Đơn</span>
                      <span class="font-mono text-lg font-bold text-slate-800">{{ currentCallingPatient.code }}</span>
                    </div>
                    <div class="w-px h-8 bg-slate-200"></div>
                    <div>
                      <span class="text-[10px] text-slate-500 uppercase block font-bold">Vị Trí Nhận</span>
                      <span class="text-lg font-black text-amber-600 uppercase">Quầy số {{ currentCallingPatient.counter }}</span>
                    </div>
                  </div>
                </div>

                <div v-else class="bg-slate-50 border border-blue-50 rounded-3xl p-16 text-center text-slate-450">
                  <span class="text-4xl block mb-3 opacity-60">📢</span>
                  <p class="font-bold text-slate-700">Không có bệnh nhân nào đang được phát loa gọi tên.</p>
                  <p class="text-[11px] text-slate-450 mt-1">Dược sĩ hãy nhấn "Phát loa gọi tên" trong tab Xử lý đơn thuốc để gọi bệnh nhân tiếp theo.</p>
                </div>
              </div>
            </div>

            <div class="bg-blue-50/80 border border-blue-100/50 p-4 rounded-2xl text-xs text-slate-600 flex items-center gap-2.5 z-10 relative">
              <span class="text-lg">📢</span>
              <span><strong>Mẹo nhỏ:</strong> Hệ thống tự động chuyển đổi mã đơn thuốc và gọi tên bệnh nhân bằng tiếng Việt chuẩn thông qua công nghệ Voice Synthesis trên trình duyệt.</span>
            </div>
          </div>

          <div class="lg:col-span-4 bg-white p-5 rounded-3xl border border-blue-100 shadow-sm space-y-4">
            <h3 class="text-xs font-bold text-slate-800 flex items-center justify-between border-b border-blue-50 pb-2">
              <span>Hàng đợi tiếp nhận lấy thuốc</span>
              <span class="text-[10px] bg-blue-50 text-blue-600 px-2 py-0.5 rounded font-mono">{{ queueList.length }} ca</span>
            </h3>

            <div class="space-y-2.5 max-h-[400px] overflow-y-auto pr-1">
              <div 
                v-for="item in queueList" 
                :key="item.code"
                class="p-3.5 bg-slate-50 border rounded-2xl flex items-center justify-between transition-all"
                :class="item.status === 'Đang soạn thuốc' ? 'border-blue-400/55 bg-blue-50/60' : 'border-slate-100'"
              >
                <div>
                  <div class="flex items-center gap-2">
                    <span class="font-mono text-xs font-bold" :class="item.status === 'Đang soạn thuốc' ? 'text-blue-600' : 'text-slate-650'">
                      {{ item.code }}
                    </span>
                    <span class="text-[8px] font-bold px-1.5 py-0.5 rounded"
                          :class="item.status === 'Đang soạn thuốc' ? 'bg-blue-100 text-blue-800' : 'bg-amber-100 text-amber-800'">
                      {{ item.status === 'Đang soạn thuốc' ? 'Đang soạn' : 'Chờ gọi' }}
                    </span>
                  </div>
                  <h4 class="font-semibold text-slate-800 mt-1 text-xs">{{ item.name }}</h4>
                </div>
                <div class="text-right">
                  <span class="text-[10px] text-slate-450 block font-bold">STT nhận</span>
                  <span class="font-bold text-slate-700 font-mono text-sm">#{{ item.stt }}</span>
                </div>
              </div>

              <div v-if="queueList.length === 0" class="text-center py-16 text-slate-400 text-xs">
                Không có bệnh nhân chờ nhận thuốc.
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== TAB 3: WORK SHIFT SCHEDULE ==================== -->
      <div v-else-if="activeMenu === 'schedule'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-xl font-extrabold text-slate-800">Lịch Trực Tuần Dược Sĩ</h2>
          <p class="text-xs text-slate-500">Xem và theo dõi lịch trực quầy cấp phát thuốc cùng quầy thuốc tự nguyện.</p>
        </div>

        <div class="bg-white rounded-3xl border border-blue-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-xs">
              <thead>
                <tr class="bg-blue-50 border-b border-blue-100 text-slate-600 font-bold uppercase tracking-wider">
                  <th class="py-4 px-6">Ngày trực</th>
                  <th class="py-4 px-4">Ca trực</th>
                  <th class="py-4 px-4">Khung giờ</th>
                  <th class="py-4 px-6">Quầy / Địa điểm</th>
                  <th class="py-4 px-6">Nghiệp vụ chi tiết</th>
                  <th class="py-4 px-6 text-center">Trạng thái</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-blue-50 text-slate-700 bg-white">
                <tr 
                  v-for="s in weeklySchedule" 
                  :key="s.day"
                  class="hover:bg-slate-55 transition-colors"
                  :class="s.status === 'Đang hoạt động' ? 'bg-blue-50/40' : ''"
                >
                  <td class="py-4.5 px-6">
                    <span class="font-bold text-slate-800 text-xs block">{{ s.day }}</span>
                    <span class="text-[10px] text-slate-450 block font-medium mt-0.5">{{ s.date }}</span>
                  </td>
                  <td class="py-4.5 px-4 font-bold" :class="s.shift === 'Nghỉ' ? 'text-slate-400' : 'text-slate-800'">{{ s.shift }}</td>
                  <td class="py-4.5 px-4 font-mono font-semibold text-slate-600">{{ s.time }}</td>
                  <td class="py-4.5 px-6 font-semibold text-slate-800">
                    <span v-if="s.location !== '-'">📍 {{ s.location }}</span>
                    <span v-else class="text-slate-400">-</span>
                  </td>
                  <td class="py-4.5 px-6 text-slate-600 font-medium">
                    {{ s.role }}
                  </td>
                  <td class="py-4.5 px-6 text-center">
                    <span class="text-[10px] font-bold px-2.5 py-1 rounded-full border uppercase"
                          :class="s.status === 'Đang hoạt động' 
                            ? 'bg-blue-100 text-blue-800 border-blue-200' 
                            : s.status === 'Sắp diễn ra'
                              ? 'bg-blue-50 text-blue-600 border-blue-105'
                              : 'bg-gray-100 text-gray-500 border-gray-250'">
                      {{ s.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== TAB 4: EMPLOYEE CARD & PROFILE ==================== -->
      <div v-else-if="activeMenu === 'profile'" class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start animate-fade-in">
        
        <div class="lg:col-span-5 flex flex-col items-center">
          <div class="w-full max-w-[320px] bg-gradient-to-br from-blue-500 via-sky-600 to-blue-700 border border-blue-400 rounded-3xl p-6 shadow-2xl relative overflow-hidden flex flex-col justify-between items-center text-center space-y-6 aspect-[2/3] group transition-all duration-350 hover:shadow-blue-500/15 text-white">
            <div class="absolute top-8 left-6 w-10 h-8 rounded bg-gradient-to-br from-amber-300 to-amber-600 border border-amber-400 flex flex-col justify-between p-1 opacity-90">
              <div class="flex justify-between"><span class="w-2 h-0.5 bg-slate-900/20"></span><span class="w-2 h-0.5 bg-slate-900/20"></span></div>
              <div class="w-full h-px bg-slate-900/20"></div>
              <div class="flex justify-between"><span class="w-2 h-0.5 bg-slate-900/20"></span><span class="w-2 h-0.5 bg-slate-900/20"></span></div>
            </div>

            <div class="w-full flex justify-end">
              <div>
                <h5 class="text-[9px] font-black text-blue-100 tracking-widest text-right">METRO HOSPITAL</h5>
                <span class="text-[8px] text-blue-200/90 block font-bold text-right uppercase mt-0.5">Thẻ nhân viên nội bộ</span>
              </div>
            </div>

            <div class="relative">
              <div class="w-28 h-28 rounded-full border-2 border-white/40 overflow-hidden shadow-lg p-1 bg-white">
                <img :src="currentUser.avatar" alt="Avatar" class="w-full h-full object-cover rounded-full" />
              </div>
              <span class="absolute bottom-1 right-2 w-4 h-4 bg-emerald-500 border-2 border-blue-600 rounded-full shadow" title="Đang trực tuyến"></span>
            </div>

            <div class="space-y-1">
              <h3 class="text-lg font-black tracking-wide text-white">{{ currentUser.fullName }}</h3>
              <p class="text-[10px] text-blue-100 font-bold uppercase tracking-wider">{{ currentUser.specialty }}</p>
              <p class="text-[9px] text-blue-200/80 font-mono">{{ currentUser.department }}</p>
            </div>

            <div class="w-full space-y-1.5 flex flex-col items-center">
              <div class="w-full h-8 bg-white rounded-lg flex items-center justify-around px-4 overflow-hidden shadow-inner">
                <div class="w-0.5 h-6 bg-slate-900"></div>
                <div class="w-1 h-6 bg-slate-900"></div>
                <div class="w-px h-6 bg-slate-900"></div>
                <div class="w-1.5 h-6 bg-slate-900"></div>
                <div class="w-0.5 h-6 bg-slate-900"></div>
                <div class="w-px h-6 bg-slate-900"></div>
                <div class="w-2 h-6 bg-slate-900"></div>
                <div class="w-px h-6 bg-slate-900"></div>
                <div class="w-0.5 h-6 bg-slate-900"></div>
                <div class="w-1 h-6 bg-slate-900"></div>
                <div class="w-1.5 h-6 bg-slate-900"></div>
                <div class="w-px h-6 bg-slate-900"></div>
                <div class="w-0.5 h-6 bg-slate-900"></div>
              </div>
              <span class="font-mono text-[9px] text-blue-100/90 tracking-widest uppercase font-bold">{{ currentUser.staffId }}</span>
            </div>

            <div class="w-full border-t border-white/20 pt-3 flex justify-between items-center text-[9px] text-blue-100/80">
              <span>Ngày cấp: {{ currentUser.joinDate }}</span>
              <span class="text-emerald-350 font-bold flex items-center gap-1">
                <span class="w-1.5 h-1.5 bg-emerald-450 rounded-full animate-ping"></span> RFID ACTIVE
              </span>
            </div>
          </div>
          <p class="text-[10px] text-slate-550 text-center mt-3 max-w-[280px]">
            * Thẻ nhân viên được tích hợp chip cảm ứng RFID dùng để mở khóa kho và đăng nhập thiết bị chuyên dụng tại quầy thuốc.
          </p>
        </div>

        <div class="lg:col-span-7 bg-white p-6 rounded-3xl border border-blue-100 shadow-sm space-y-6">
          <div class="flex justify-between items-center border-b border-blue-50 pb-3">
            <h3 class="text-base font-bold text-slate-800">Thông Tin Hồ Sơ Cá Nhân</h3>
            <button 
              @click="isEditingProfile ? saveProfile() : startEditProfile()"
              class="py-1.5 px-4 font-bold rounded-xl text-xs transition-colors flex items-center gap-1.5 bg-blue-50 text-blue-600 border border-blue-200 hover:bg-blue-100"
            >
              <i :class="isEditingProfile ? 'bi bi-check-lg' : 'bi bi-pencil-square'"></i>
              {{ isEditingProfile ? 'Lưu cập nhật' : 'Chỉnh sửa hồ sơ' }}
            </button>
          </div>

          <div v-if="isEditingProfile" class="space-y-4 text-xs">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-[10px] text-slate-505 font-bold uppercase mb-1.5">Họ và tên nhân viên</label>
                <input type="text" v-model="editForm.fullName" class="w-full bg-white border border-blue-100 rounded-xl px-3 py-2.5 text-slate-850" />
              </div>
              <div> 
                <label class="block text-[10px] text-slate-505 font-bold uppercase mb-1.5">Số điện thoại</label>
                <input type="tel" v-model="editForm.phone" class="w-full bg-white border border-blue-100 rounded-xl px-3 py-2.5 text-slate-850" />
              </div>
            </div>
            <div>
              <label class="block text-[10px] text-slate-505 font-bold uppercase mb-1.5">Địa chỉ Email</label>
              <input type="email" v-model="editForm.email" class="w-full bg-white border border-blue-100 rounded-xl px-3 py-2.5 text-slate-850" />
            </div>
            <div>
              <label class="block text-[10px] text-slate-505 font-bold uppercase mb-1.5">Chức danh / Học hàm học vị</label>
              <input type="text" v-model="editForm.specialty" class="w-full bg-white border border-blue-100 rounded-xl px-3 py-2.5 text-slate-850" />
            </div>
            <div>
              <label class="block text-[10px] text-slate-505 font-bold uppercase mb-1.5">Bộ phận / Nơi công tác</label>
              <input type="text" v-model="editForm.department" class="w-full bg-white border border-blue-100 rounded-xl px-3 py-2.5 text-slate-850" />
            </div>
            <div class="flex justify-end gap-2 pt-2">
              <button type="button" @click="isEditingProfile = false" class="py-2 px-4 border border-slate-200 hover:bg-slate-50 rounded-xl text-slate-500 font-bold text-xs">
                Hủy bỏ
              </button>
            </div>
          </div>

          <div v-else class="space-y-4 text-xs">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4.5">
              <div class="bg-slate-50/60 border border-blue-50 p-3.5 rounded-2xl">
                <span class="text-[9px] text-slate-450 font-bold uppercase tracking-wider block mb-1">Mã nhân viên:</span>
                <span class="font-mono text-sm font-bold text-slate-800">{{ currentUser.staffId }}</span>
              </div>
              <div class="bg-slate-50/60 border border-blue-50 p-3.5 rounded-2xl">
                <span class="text-[9px] text-slate-450 font-bold uppercase tracking-wider block mb-1">Họ & Tên:</span>
                <span class="text-sm font-bold text-slate-850">{{ currentUser.fullName }}</span>
              </div>
              <div class="bg-slate-50/60 border border-blue-50 p-3.5 rounded-2xl">
                <span class="text-[9px] text-slate-450 font-bold uppercase tracking-wider block mb-1">Số điện thoại di động:</span>
                <span class="font-mono text-sm font-bold text-slate-800">{{ currentUser.phone }}</span>
              </div>
              <div class="bg-slate-50/60 border border-blue-50 p-3.5 rounded-2xl">
                <span class="text-[9px] text-slate-450 font-bold uppercase tracking-wider block mb-1">Địa chỉ hòm thư điện tử:</span>
                <span class="text-sm font-bold text-slate-850">{{ currentUser.email }}</span>
              </div>
              <div class="bg-slate-50/60 border border-blue-50 p-3.5 rounded-2xl">
                <span class="text-[9px] text-slate-450 font-bold uppercase tracking-wider block mb-1">Bằng cấp & Chức danh:</span>
                <span class="text-sm font-bold text-slate-850">{{ currentUser.specialty }}</span>
              </div>
              <div class="bg-slate-50/60 border border-blue-50 p-3.5 rounded-2xl">
                <span class="text-[9px] text-slate-450 font-bold uppercase tracking-wider block mb-1">Vị trí đảm nhận:</span>
                <span class="text-sm font-bold text-slate-850">{{ currentUser.department }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== TAB 5: REPORTS & CHARTS ==================== -->
      <div v-else-if="activeMenu === 'reports'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-xl font-extrabold text-slate-800">Báo Cáo Hiệu Suất Quầy Dược</h2>
          <p class="text-xs text-slate-550">Báo cáo số lượng thuốc phân phối, doanh thu ngoài dịch vụ kê đơn và yêu cầu đổi thuốc.</p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="bg-white p-6 rounded-3xl border border-blue-100 shadow-sm space-y-4">
            <h3 class="text-sm font-bold text-slate-800 border-b border-blue-50 pb-2">Ước tính giá trị thuốc bàn giao</h3>
            <div class="space-y-3.5 pt-2 text-xs text-slate-700">
              <div class="flex justify-between border-b border-blue-50 pb-2.5">
                <span>Số toa thuốc đã cấp hoàn thành:</span>
                <span class="font-bold text-slate-900 font-mono">{{ summaryStats.totalDispensed }} đơn</span>
              </div>
              <div class="flex justify-between border-b border-blue-50 pb-2.5">
                <span>Số yêu cầu đổi thuốc được duyệt:</span>
                <span class="font-bold text-slate-900 font-mono">{{ summaryStats.replacedRequests }} yêu cầu</span>
              </div>
              <div class="flex justify-between border-b border-blue-50 pb-2.5">
                <span>Doanh thu ngoài từ bán kèm thuốc:</span>
                <span class="font-bold text-emerald-600 font-mono">{{ summaryStats.todayRevenue.toLocaleString('vi-VN') }}đ</span>
              </div>
              <div class="flex justify-between items-center pt-2">
                <span class="font-bold text-blue-700 uppercase tracking-wider text-xs">Tổng giá trị thuốc đã soạn và phát:</span>
                <span class="font-black text-base text-blue-600 font-mono">
                  ~{{ ((summaryStats.totalDispensed * 85000) + summaryStats.todayRevenue).toLocaleString('vi-VN') }}đ
                </span>
              </div>
            </div>
          </div>

          <div class="bg-white p-6 rounded-3xl border border-blue-100 shadow-sm space-y-4">
            <h3 class="text-sm font-bold text-slate-800 border-b border-blue-50 pb-2">Top 4 thuốc cấp phát chạy nhất ngày</h3>
            
            <div class="space-y-4.5 pt-2 text-xs">
              <div class="space-y-1.5">
                <div class="flex justify-between font-medium">
                  <span class="text-slate-750">Panadol Extra 500mg</span>
                  <span class="text-blue-600 font-bold font-mono">68 đơn vị (72%)</span>
                </div>
                <div class="w-full h-3 bg-blue-50 border border-blue-100/50 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-blue-600 to-blue-400 rounded-full transition-all duration-500" style="width: 72%"></div>
                </div>
              </div>

              <div class="space-y-1.5">
                <div class="flex justify-between font-medium">
                  <span class="text-slate-755">Concor 2.5mg (Bisoprolol fumarate)</span>
                  <span class="text-blue-600 font-bold font-mono">42 đơn vị (45%)</span>
                </div>
                <div class="w-full h-3 bg-blue-50 border border-blue-100/50 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-blue-600 to-blue-400 rounded-full transition-all duration-500" style="width: 45%"></div>
                </div>
              </div>

              <div class="space-y-1.5">
                <div class="flex justify-between font-medium">
                  <span class="text-slate-755">Nexium mups 20mg</span>
                  <span class="text-blue-600 font-bold font-mono">31 đơn vị (33%)</span>
                </div>
                <div class="w-full h-3 bg-blue-50 border border-blue-100/50 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-blue-600 to-blue-400 rounded-full transition-all duration-500" style="width: 33%"></div>
                </div>
              </div>

              <div class="space-y-1.5">
                <div class="flex justify-between font-medium">
                  <span class="text-slate-755">Phosphalugel (Thuốc chữ P)</span>
                  <span class="text-blue-600 font-bold font-mono">20 đơn vị (21%)</span>
                </div>
                <div class="w-full h-3 bg-blue-50 border border-blue-100/50 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-blue-600 to-blue-400 rounded-full transition-all duration-500" style="width: 21%"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </main>

    <!-- ==================== DIALOG MODAL 1: DRUG REPLACEMENT REQUEST ==================== -->
    <div v-if="showReplaceModal" class="fixed inset-0 bg-black/65 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in text-xs">
      <div class="bg-white border border-blue-100 rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6 text-slate-800">
        <div class="text-center">
          <h3 class="text-lg font-black text-slate-800 flex items-center justify-center gap-2">
            <span class="text-xl">⚠️</span> Yêu Cầu Thay Thế Thuốc
          </h3>
          <p class="text-slate-550 mt-1">Đề xuất thuốc thay thế tương đương do hết hàng tồn kho tại quầy.</p>
        </div>

        <div class="bg-rose-50/60 p-4 border border-rose-100 rounded-2xl space-y-1.5">
          <span class="text-[9px] text-rose-500 font-bold block uppercase tracking-wider">Mặt hàng đang thiếu trong toa:</span>
          <p class="font-bold text-slate-800" v-if="replaceTargetMed">
            {{ replaceTargetMed.name }} (Lượng cần phát: {{ replaceTargetMed.qty }} {{ replaceTargetMed.unit }})
          </p>
        </div>

        <form @submit.prevent="submitReplacementRequest" class="space-y-4">
          <div>
            <label for="replace-select" class="block text-[10px] text-slate-500 font-bold uppercase mb-1.5">Chọn thuốc thay thế trong kho</label>
            <select id="replace-select" v-model="selectedReplaceWith" required class="w-full bg-white border border-blue-100 rounded-xl p-2.5 text-slate-800">
              <option value="" disabled>-- Chọn biệt dược thay thế còn hàng --</option>
              <option v-for="item in availableAlternatives" :key="item.name" :value="item.name">
                {{ item.name }} (Còn: {{ item.qty }} {{ item.unit }})
              </option>
            </select>
          </div>

          <div>
            <label for="replace-qty" class="block text-[10px] text-slate-550 font-bold uppercase mb-1.5">Số lượng thay đổi tương đương</label>
            <input id="replace-qty" type="number" min="1" v-model="replaceQty" required class="w-full bg-white border border-blue-100 rounded-xl p-2 text-slate-750 font-bold font-mono" />
          </div>

          <div>
            <label for="replace-note" class="block text-[10px] text-slate-550 font-bold uppercase mb-1.5">Ghi chú gửi bác sĩ</label>
            <textarea id="replace-note" v-model="replaceNote" rows="2" class="w-full bg-white border border-blue-100 rounded-xl p-2.5 text-slate-700 focus:outline-none focus:border-blue-500" placeholder="Lý do đổi..."></textarea>
          </div>

          <div class="flex gap-3.5 pt-3">
            <button type="button" @click="showReplaceModal = false" class="flex-1 py-3 border border-slate-200 rounded-xl font-bold text-slate-500 hover:bg-slate-50">
              Hủy
            </button>
            <button type="submit" class="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-lg">
              Gửi yêu cầu đổi thuốc
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ==================== DIALOG MODAL 2: RECEIPT PRINT PREVIEW ==================== -->
    <div v-if="showReceiptModal && receiptPrescription" class="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in text-xs">
      <div class="bg-white text-slate-900 rounded-3xl shadow-2xl max-w-lg w-full p-6 md:p-8 animate-fade-in-up space-y-6 flex flex-col justify-between max-h-[90vh]">
        
        <div class="overflow-y-auto pr-1 space-y-6 flex-1">
          <div class="text-center space-y-1.5 border-b border-slate-200 pb-4">
            <h4 class="text-xs font-black tracking-widest text-slate-400 uppercase">NHÀ THUỐC BỆNH VIỆN METRO HOSPITAL</h4>
            <h2 class="text-lg font-black text-slate-900 uppercase">HÓA ĐƠN THANH TOÁN THUỐC</h2>
            <p class="text-[10px] text-slate-500">Mã đơn: <strong class="font-mono text-slate-800 text-xs">{{ receiptPrescription.code }}</strong> | Ngày cấp: {{ receiptPrescription.date }}</p>
          </div>

          <div class="space-y-1.5 text-slate-700">
            <p>Bệnh nhân: <strong class="text-slate-900">{{ receiptPrescription.patientName }}</strong> ({{ receiptPrescription.patientAge }} tuổi - {{ receiptPrescription.patientGender }})</p>
            <p>Bác sĩ chỉ định kê toa: <span>{{ receiptPrescription.doctor }}</span></p>
            <p>Dược sĩ cấp phát: <span>{{ currentUser.fullName }}</span></p>
            <p>Hình thức thanh toán: <strong class="text-blue-700 uppercase">{{ paymentMethod === 'cash' ? 'Tiền mặt tại quầy' : 'Quét mã QR Online' }}</strong></p>
          </div>

          <div class="space-y-4">
            <div class="space-y-1.5">
              <span class="text-[9px] text-slate-400 font-bold uppercase tracking-wider block">1. Danh mục thuốc theo đơn (Khách tự chi trả 20% co-pay BHYT):</span>
              <table class="w-full text-left text-xs border-collapse">
                <thead>
                  <tr class="border-b border-slate-200 text-slate-500 font-bold">
                    <th class="py-2">Tên thuốc</th>
                    <th class="py-2 text-center">SL</th>
                    <th class="py-2 text-right">Giá gốc</th>
                    <th class="py-2 text-right">Người bệnh trả (20%)</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 text-slate-700">
                  <tr v-for="m in receiptPrescription.medicines" :key="m.name">
                    <td class="py-2 font-bold">{{ m.name }}</td>
                    <td class="py-2 text-center font-mono font-bold">{{ m.qty }}</td>
                    <td class="py-2 text-right font-mono">{{ getStockPrice(m.name).toLocaleString('vi-VN') }}đ</td>
                    <td class="py-2 text-right font-mono font-bold">{{ Math.round(m.qty * getStockPrice(m.name) * 0.20).toLocaleString('vi-VN') }}đ</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="space-y-1.5" v-if="receiptPrescription.extraMedicines && receiptPrescription.extraMedicines.length > 0">
              <span class="text-[9px] text-slate-400 font-bold uppercase tracking-wider block">2. Danh mục thuốc mua thêm tự chi trả:</span>
              <table class="w-full text-left text-xs border-collapse">
                <thead>
                  <tr class="border-b border-slate-200 text-slate-500 font-bold">
                    <th class="py-2">Tên thuốc mua kèm</th>
                    <th class="py-2 text-center">SL</th>
                    <th class="py-2 text-right">Đơn giá</th>
                    <th class="py-2 text-right">Thành tiền</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 text-slate-700">
                  <tr v-for="em in receiptPrescription.extraMedicines" :key="em.name">
                    <td class="py-2 font-bold">{{ em.name }}</td>
                    <td class="py-2 text-center font-mono font-bold">{{ em.qty }}</td>
                    <td class="py-2 text-right font-mono">{{ em.price.toLocaleString('vi-VN') }}đ</td>
                    <td class="py-2 text-right font-mono font-bold">{{ (em.qty * em.price).toLocaleString('vi-VN') }}đ</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="border-t border-slate-200 pt-4 space-y-1.5 text-right">
            <p class="text-[11px] text-slate-650">Tiền toa gốc co-pay: <strong class="text-slate-800">{{ activePrescriptionCosts.base.toLocaleString('vi-VN') }}đ</strong></p>
            <p class="text-[11px] text-slate-650" v-if="activePrescriptionCosts.extra > 0">Tiền mua kèm ngoài toa: <strong class="text-slate-800">{{ activePrescriptionCosts.extra.toLocaleString('vi-VN') }}đ</strong></p>
            <h3 class="text-sm font-black text-slate-900 uppercase">TỔNG TIỀN ĐÃ THU: {{ activePrescriptionCosts.total.toLocaleString('vi-VN') }} VNĐ</h3>
            <p class="text-[10px] text-slate-500 italic" v-if="paymentMethod === 'cash'">Khách đưa: {{ cashAmountGiven.toLocaleString('vi-VN') }}đ | Tiền thừa thối lại: {{ changeAmountDue.toLocaleString('vi-VN') }}đ</p>
          </div>

          <div class="grid grid-cols-2 text-center pt-6 text-[10px] text-slate-500">
            <div>
              <p class="font-bold">Người nhận thuốc</p>
              <p class="text-[9px] mt-0.5">(Ký và ghi rõ họ tên)</p>
              <div class="h-12"></div>
              <p class="text-slate-750 font-bold">{{ receiptPrescription.patientName }}</p>
            </div>
            <div>
              <p class="font-bold">Dược sĩ bàn giao</p>
              <p class="text-[9px] mt-0.5">(Ký và ghi rõ họ tên)</p>
              <div class="h-12 font-signature text-blue-600 flex items-center justify-center italic font-bold">
                {{ currentUser.fullName.split(' ').pop() }}
              </div>
              <p class="text-slate-750 font-bold">{{ currentUser.fullName }}</p>
            </div>
          </div>
        </div>

        <div class="border-t border-slate-200 pt-4 flex gap-3.5">
          <button 
            type="button" 
            @click="showReceiptModal = false" 
            class="flex-1 py-3 border border-slate-200 rounded-xl font-bold text-slate-600 hover:bg-slate-50"
          >
            Đóng cửa sổ
          </button>
          <button 
            type="button" 
            @click="window.print()" 
            class="flex-1 py-3 bg-blue-600 hover:bg-blue-755 text-white font-bold rounded-xl shadow-lg transition-colors flex items-center justify-center gap-1.5"
          >
            <i class="bi bi-printer-fill"></i> In hóa đơn
          </button>
        </div>

      </div>
    </div>

    <!-- ==================== DIALOG MODAL 3: MEDICINE POCKET LABELS PRINT ==================== -->
    <div v-if="showPocketLabelsModal && receiptPrescription" class="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in text-xs">
      <div class="bg-white text-slate-900 rounded-3xl shadow-2xl max-w-lg w-full p-6 md:p-8 animate-fade-in-up space-y-6 flex flex-col justify-between max-h-[90vh]">
        
        <div class="overflow-y-auto pr-1 space-y-6 flex-1">
          <div class="text-center space-y-1 border-b border-slate-200 pb-3">
            <h2 class="text-sm font-black text-slate-800 uppercase">NHÃN DÁN HƯỚNG DẪN DÙNG THUỐC</h2>
            <p class="text-[10px] text-slate-500">In dán lên từng bao bì/túi đựng thuốc cho người bệnh</p>
          </div>

          <!-- Print layout for each medicine -->
          <div class="space-y-4">
            <!-- Normal Prescription medicines -->
            <div 
              v-for="m in receiptPrescription.medicines" 
              :key="m.name"
              class="border-2 border-dashed border-slate-350 p-4 rounded-xl space-y-2 relative bg-slate-50/50"
            >
              <div class="flex justify-between items-center border-b border-slate-200 pb-1">
                <span class="text-[9px] font-black text-blue-700">METRO HOSPITAL PHARMACY</span>
                <span class="text-[8px] font-mono text-slate-500">{{ receiptPrescription.code }}</span>
              </div>
              <div class="text-xs text-slate-800 space-y-1">
                <p>Bệnh nhân: <strong class="text-slate-900">{{ receiptPrescription.patientName }}</strong></p>
                <p>Tên thuốc: <strong class="text-blue-700 text-sm block mt-0.5">{{ m.name }}</strong></p>
                <p>Số lượng cấp: <strong class="font-mono text-slate-900">{{ m.qty }} {{ m.unit }}</strong></p>
                <div class="bg-white p-2 border border-slate-200 rounded-lg text-xs mt-1.5 font-semibold text-slate-900">
                  Cách dùng: {{ m.use }}
                </div>
              </div>
              <p class="text-[8px] text-slate-500 text-right italic font-medium mt-1">Chúc quý khách mau chóng hồi phục sức khỏe!</p>
            </div>

            <!-- Extra medicines bought -->
            <div 
              v-for="em in receiptPrescription.extraMedicines" 
              :key="em.name"
              class="border-2 border-dashed border-emerald-350 p-4 rounded-xl space-y-2 relative bg-emerald-50/15"
            >
              <div class="flex justify-between items-center border-b border-slate-200 pb-1">
                <span class="text-[9px] font-black text-emerald-700">METRO HOSPITAL PHARMACY (MUA THÊM)</span>
                <span class="text-[8px] font-mono text-slate-500">{{ receiptPrescription.code }}</span>
              </div>
              <div class="text-xs text-slate-800 space-y-1">
                <p>Bệnh nhân: <strong class="text-slate-900">{{ receiptPrescription.patientName }}</strong></p>
                <p>Tên thuốc: <strong class="text-emerald-700 text-sm block mt-0.5">{{ em.name }}</strong></p>
                <p>Số lượng cấp: <strong class="font-mono text-slate-900">{{ em.qty }} {{ em.unit }}</strong></p>
                <div class="bg-white p-2 border border-slate-200 rounded-lg text-xs mt-1.5 font-semibold text-slate-900">
                  Cách dùng: {{ em.use }}
                </div>
              </div>
              <p class="text-[8px] text-slate-500 text-right italic font-medium mt-1">Chúc quý khách mau chóng hồi phục sức khỏe!</p>
            </div>
          </div>
        </div>

        <div class="border-t border-slate-200 pt-4 flex gap-3.5">
          <button 
            type="button" 
            @click="showPocketLabelsModal = false" 
            class="flex-1 py-3 border border-slate-200 rounded-xl font-bold text-slate-650 hover:bg-slate-50"
          >
            Đóng cửa sổ
          </button>
          <button 
            type="button" 
            @click="window.print()" 
            class="flex-1 py-3 bg-blue-600 hover:bg-blue-755 text-white font-bold rounded-xl shadow-lg transition-colors flex items-center justify-center gap-1.5"
          >
            <i class="bi bi-printer-fill"></i> In nhãn dán
          </button>
        </div>

      </div>
    </div>

  </div>
</template>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.25s ease-out forwards;
}
.animate-fade-in-up {
  animation: fadeInUp 0.3s ease-out forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.98); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
