<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeTab = ref('intro') // Default to general introduction
const hospitalIntro = ref(`<h2 style="text-align: center; color: #1e3a8a;"><strong>HỆ THỐNG Y TẾ BỆNH VIỆN ĐA KHOA QUỐC TẾ</strong></h2>` +
  `<p style="text-align: center; font-style: italic; color: #4b5563;">Hệ thống y tế hiện đại, uy tín hàng đầu — Nơi sức khỏe của bạn là ưu tiên số một</p>` +
  `<hr />` +
  `<p>Bệnh viện Đa khoa Quốc tế tự hào là một trong những cơ sở y tế hàng đầu Việt Nam cung cấp dịch vụ khám chữa bệnh chất lượng cao theo tiêu chuẩn quốc tế. Với triết lý <strong>"Tận tâm vì sức khỏe người bệnh"</strong>, chúng tôi không ngừng cải tiến quy trình, đầu tư trang thiết bị công nghệ hiện đại và quy tụ đội ngũ chuyên gia, y bác sĩ đầu ngành nhằm đem lại trải nghiệm y tế hoàn hảo nhất.</p>` +
  `<p style="text-align: center;">` +
  `  <img src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&amp;fit=crop&amp;w=800&amp;q=80" alt="Bệnh viện đa khoa quốc tế" style="border-radius: 8px; max-width: 100%; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);" />` +
  `</p>` +
  `<h3 style="color: #0f766e;"><strong><u>Tầm Nhìn &amp; Sứ Mệnh Phát Triển</u></strong></h3>` +
  `<p>Chúng tôi hướng tới mục tiêu trở thành một tổ chức y tế chuẩn quốc tế hàng đầu, nơi người bệnh gửi trọn niềm tin bằng dịch vụ chuyên nghiệp và chi phí hợp lý nhất. Sứ mệnh của chúng tôi là mang lại giải pháp chăm sóc sức khỏe toàn diện, an toàn và hiệu quả cao.</p>` +
  `<h3 style="color: #0f766e;"><strong><u>Những Lý Do Lựa Chọn Dịch Vụ Của Chúng Tôi</u></strong></h3>` +
  `<ul>` +
  `  <li><strong>Đội ngũ chuyên gia hàng đầu:</strong> Các Giáo sư, Tiến sĩ, Bác sĩ chuyên khoa giàu kinh nghiệm, tận tụy phục vụ người bệnh.</li>` +
  `  <li><strong>Trang thiết bị tiên tiến nhất:</strong> Hệ thống máy chụp MRI, CT Scanner 128 dãy, hệ thống nội soi thế hệ mới giúp phát hiện sớm mọi dấu hiệu bất thường.</li>` +
  `  <li><strong>Không gian khám chữa bệnh cao cấp:</strong> Môi trường bệnh viện xanh, sạch, an toàn, thiết kế theo mô hình khách sạn bệnh viện tiện nghi.</li>` +
  `  <li><strong>Dịch vụ hỗ trợ người bệnh 24/7:</strong> Hỗ trợ tận tâm từ lúc đón tiếp, khám bệnh cho đến khi xuất viện và chăm sóc tại nhà.</li>` +
  `</ul>` +
  `<hr />` +
  `<h3 style="color: #0f766e;"><strong><u>Thông Tin Liên Hệ &amp; Địa Chỉ Bản Đồ</u></strong></h3>` +
  `<p>Quý khách hàng có nhu cầu tư vấn hoặc đặt lịch khám vui lòng liên hệ với chúng tôi qua các kênh thông tin chính thức dưới đây:</p>` +
  `<ul>` +
  `  <li><strong>Địa chỉ chính thức:</strong> <a href="https://maps.google.com/?q=Bệnh+viện+Đa+khoa+Quốc+tế" target="_blank" rel="noopener noreferrer"><u>Bản đồ hướng dẫn đường đi đến Bệnh viện</u></a></li>` +
  `  <li><strong>Hotline Đặt lịch:</strong> <a href="tel:19001234" style="color: #2563eb; font-weight: bold;"><u>1900 1234</u></a> (Hỗ trợ 24/7)</li>` +
  `  <li><strong>Email phản hồi &amp; Hỗ trợ:</strong> <a href="mailto:contact@hospital.com" style="color: #2563eb; font-weight: bold;"><u>contact@hospital.com</u></a></li>` +
  `</ul>`)

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  loadIntro()
  loadDoctors()
})

function loadIntro() {
  const configJson = localStorage.getItem('hospitalConfig')
  if (configJson) {
    const config = JSON.parse(configJson)
    let intro = config.introText
    if (!intro || intro.length < 300 || intro.includes('nơi sức khỏe của bạn là ưu tiên số một. Chúng tôi cam kết đem lại dịch vụ khám chữa bệnh tốt nhất')) {
      intro = hospitalIntro.value
      // Upgrade local storage too if it exists
      config.introText = intro
      localStorage.setItem('hospitalConfig', JSON.stringify(config))
    }
    hospitalIntro.value = intro
  }
}

function switchTab(tab) {
  activeTab.value = tab
  router.push({ query: { tab } })
}

/* ==================== 1. HOSPITAL MAP DATA & LOGIC ==================== */
const searchMapQuery = ref('')
const selectedFloor = ref('G') // 'G', '1', '2'

const floors = {
  'G': {
    name: 'Tầng trệt (G) - Cấp cứu & Tiếp đón',
    rooms: [
      { id: 'ER', name: 'Phòng Cấp Cứu', code: 'P.101', desc: 'Mở cửa 24/7 cho các trường hợp khẩn cấp', x: 1, y: 1, type: 'emergency' },
      { id: 'REC', name: 'Quầy Tiếp Đón & Đăng Ký Khám', code: 'Q.102', desc: 'Đăng ký thông tin, mua sổ khám bệnh', x: 2, y: 1, type: 'reception' },
      { id: 'PHAR', name: 'Nhà Thuốc Bệnh Viện', code: 'N.103', desc: 'Cấp phát thuốc theo đơn bảo hiểm & dịch vụ', x: 3, y: 1, type: 'pharmacy' },
      { id: 'XRAY', name: 'Phòng Chụp X-Quang & Siêu Âm', code: 'P.104', desc: 'Chẩn đoán hình ảnh kỹ thuật số', x: 1, y: 2, type: 'diagnostic' },
      { id: 'LAB', name: 'Phòng Xét Nghiệm Máu', code: 'P.105', desc: 'Lấy mẫu xét nghiệm sinh hóa, huyết học', x: 2, y: 2, type: 'diagnostic' },
      { id: 'CARD', name: 'Khoa Tim Mạch', code: 'P.106', desc: 'Khám và tư vấn bệnh lý tim mạch, huyết áp', x: 3, y: 2, type: 'clinical' }
    ]
  },
  '1': {
    name: 'Tầng 1 - Khoa Khám Bệnh Ngoại Trú',
    rooms: [
      { id: 'GP', name: 'Phòng Nội Tổng Quát', code: 'P.201', desc: 'Khám lâm sàng nội khoa tổng hợp', x: 1, y: 1, type: 'clinical' },
      { id: 'SURG', name: 'Khoa Ngoại Khoa', code: 'P.202', desc: 'Khám ngoại khoa, xử lý vết thương, tiểu phẫu', x: 2, y: 1, type: 'clinical' },
      { id: 'OBGYN', name: 'Khoa Sản Phụ Khoa', code: 'P.203', desc: 'Khám thai, tư vấn chăm sóc sức khỏe phụ nữ', x: 3, y: 1, type: 'clinical' },
      { id: 'PED', name: 'Khoa Nhi Khoa', code: 'P.204', desc: 'Khám và điều trị cho trẻ em dưới 15 tuổi', x: 1, y: 2, type: 'clinical' },
      { id: 'EYE', name: 'Khoa Mắt', code: 'P.205', desc: 'Đo thị lực, khám điều trị bệnh lý về mắt', x: 2, y: 2, type: 'clinical' },
      { id: 'ENT', name: 'Phòng Tai Mũi Họng', code: 'P.206', desc: 'Khám nội soi tai mũi họng', x: 3, y: 2, type: 'clinical' }
    ]
  },
  '2': {
    name: 'Tầng 2 - Khu Điều Trị Nội Trú',
    rooms: [
      { id: 'WARD_A', name: 'Phòng Bệnh Nội Trú A (Nam)', code: 'P.301', desc: 'Khu phòng bệnh lưu trú cho nam giới', x: 1, y: 1, type: 'ward' },
      { id: 'WARD_B', name: 'Phòng Bệnh Nội Trú B (Nữ)', code: 'P.302', desc: 'Khu phòng bệnh lưu trú cho nữ giới', x: 2, y: 1, type: 'ward' },
      { id: 'ICU', name: 'Phòng Chăm Sóc Đặc Biệt (ICU)', code: 'P.303', desc: 'Điều trị tích cực cho bệnh nhân nặng', x: 3, y: 1, type: 'emergency' },
      { id: 'DOC_OFF', name: 'Văn Phòng Bác Sĩ', code: 'P.304', desc: 'Nơi làm việc, họp giao ban của y bác sĩ', x: 1, y: 2, type: 'admin' },
      { id: 'MEET', name: 'Hội Trường Hội Chẩn', code: 'P.305', desc: 'Phòng họp chuyên môn, hội chẩn bệnh án', x: 2, y: 2, type: 'admin' },
      { id: 'DIAL', name: 'Khu Lọc Máu - Thận Nhân Tạo', code: 'P.306', desc: 'Chạy thận chu kỳ cho bệnh nhân suy thận', x: 3, y: 2, type: 'clinical' }
    ]
  }
}

const mapSearchResult = computed(() => {
  if (!searchMapQuery.value.trim()) return []
  const query = searchMapQuery.value.toLowerCase()
  const results = []
  Object.keys(floors).forEach(floorKey => {
    floors[floorKey].rooms.forEach(room => {
      if (room.name.toLowerCase().includes(query) || room.code.toLowerCase().includes(query) || room.desc.toLowerCase().includes(query)) {
        results.push({ floorKey, floorName: floors[floorKey].name, ...room })
      }
    })
  })
  return results
})

function selectResult(result) {
  selectedFloor.value = result.floorKey
  // Highlight flash can be added, but for now we focus on switching the tab
  searchMapQuery.value = ''
}

const currentRooms = computed(() => floors[selectedFloor.value].rooms)

function getRoomColorClass(type) {
  switch (type) {
    case 'emergency': return 'bg-red-50 hover:bg-red-100 border-red-200 text-red-700'
    case 'reception': return 'bg-blue-50 hover:bg-blue-100 border-blue-200 text-blue-700'
    case 'pharmacy': return 'bg-emerald-50 hover:bg-emerald-100 border-emerald-200 text-emerald-700'
    case 'diagnostic': return 'bg-purple-50 hover:bg-purple-100 border-purple-200 text-purple-700'
    case 'clinical': return 'bg-sky-50 hover:bg-sky-100 border-sky-200 text-sky-700'
    case 'ward': return 'bg-amber-50 hover:bg-amber-100 border-amber-200 text-amber-700'
    default: return 'bg-gray-50 hover:bg-gray-100 border-gray-200 text-gray-700'
  }
}

/* ==================== 2. DEPARTMENTS LIST & DETAIL ==================== */
const searchDeptQuery = ref('')
const selectedDeptFilter = ref('ALL') // 'ALL' | 'CLINICAL' | 'DIAGNOSTIC'
const selectedDept = ref(null) // Detail modal

const departments = [
  { id: 'tim-mach', type: 'CLINICAL', icon: 'bi-heart-pulse', name: 'Khoa Tim Mạch', desc: 'Chẩn đoán & điều trị các bệnh lý tim mạch, huyết áp, rối loạn nhịp tim cho người trưởng thành và cao tuổi.', lead: 'PGS.TS Nguyễn Văn An', facilities: 'Máy siêu âm tim doppler màu 4D, máy điện tim gắng sức, hệ thống chụp mạch xóa nền (DSA) hiện đại.', hours: '7:30 - 16:30 (Thứ 2 - Thứ 7)' },
  { id: 'noi-tong-quat', type: 'CLINICAL', icon: 'bi-clipboard2-pulse', name: 'Khoa Nội Tổng Quát', desc: 'Khám lâm sàng, phát hiện sớm và điều trị các bệnh nội khoa như tiêu hóa, gan mật, hô hấp, đái tháo đường và quản lý bệnh mãn tính.', lead: 'ThS.BS Trần Văn Bình', facilities: 'Hệ thống nội soi tiêu hóa không đau, máy đo chức năng hô hấp tiên tiến.', hours: '7:30 - 17:00 (Hằng ngày)' },
  { id: 'ngoai-khoa', type: 'CLINICAL', icon: 'bi-bandaid', name: 'Khoa Ngoại Tổng Quát', desc: 'Thực hiện phẫu thuật nội soi ngoại khoa tiêu hóa, chấn thương chỉnh hình, xử lý vết thương và tiểu phẫu an toàn.', lead: 'GS.TS Lê Hoàng Minh', facilities: 'Phòng mổ vô khuẩn một chiều đạt chuẩn quốc tế, giàn máy phẫu thuật nội soi Full HD.', hours: '24/7 (Phẫu thuật cấp cứu) / Khám hành chính: Thứ 2 - Thứ 6' },
  { id: 'san-phu-khoa', type: 'CLINICAL', icon: 'bi-gender-female', name: 'Khoa Sản Phụ Khoa', desc: 'Chăm sóc sức khỏe thai kỳ toàn diện, hỗ trợ sinh nở không đau và điều trị các bệnh phụ khoa lành tính.', lead: 'TS.BS Trần Thị Mai', facilities: 'Máy siêu âm thai Voluson E10 hàng đầu thế giới, phòng sinh gia đình ấm cúng.', hours: '7:30 - 16:30 (Thứ 2 - Thứ 7)' },
  { id: 'nhi-khoa', type: 'CLINICAL', icon: 'bi-emoji-smile', name: 'Khoa Nhi Khoa', desc: 'Chăm sóc sức khỏe toàn diện cho trẻ em dưới 15 tuổi, bao gồm dinh dưỡng, tiêm chủng và điều trị các bệnh nhi khoa thường gặp.', lead: 'ThS.BS Phạm Thùy Linh', facilities: 'Khu vui chơi trẻ em nhiều màu sắc, phòng tiêm chủng an toàn thân thiện.', hours: '7:30 - 20:00 (Hằng ngày)' },
  { id: 'mat', type: 'CLINICAL', icon: 'bi-eye', name: 'Khoa Mắt', desc: 'Đo khám khúc xạ, phẫu thuật cườm khô (Phaco), điều trị đục thủy tinh thể thường gặp ở người già và các bệnh lý võng mạc.', lead: 'TS.BS Lê Thị Lan', facilities: 'Hệ thống phẫu thuật Phaco Centurion hiện đại, máy đo bản đồ giác mạc.', hours: '7:30 - 16:30 (Thứ 2 - Thứ 7)' },
  { id: 'chan-doan-hinh-anh', type: 'DIAGNOSTIC', icon: 'bi-exclude', name: 'Khoa Chẩn Đoán Hình Ảnh', desc: 'Cung cấp các kết quả chụp X-quang, Siêu âm, Chụp CT cắt lớp, Cộng hưởng từ (MRI) chính xác phục vụ công tác điều trị.', lead: 'ThS.BS Nguyễn Minh Đức', facilities: 'Hệ thống MRI 1.5 Tesla, Máy CT 128 lát cắt, Máy X-Quang kỹ thuật số công nghệ giảm liều tia xạ.', hours: '24/7 (Cho nội trú & cấp cứu)' },
  { id: 'xet-nghiem', type: 'DIAGNOSTIC', icon: 'bi-droplet-half', name: 'Khoa Xét Nghiệm', desc: 'Thực hiện xét nghiệm sinh hóa, huyết học, miễn dịch và vi sinh chất lượng cao hỗ trợ chẩn đoán chính xác bệnh.', lead: 'TS. Nguyễn Thị Vân', facilities: 'Hệ thống xét nghiệm tự động hoàn toàn (Cobas 8000), đạt tiêu chuẩn ISO 15189.', hours: '24/7' }
]

const filteredDepartments = computed(() => {
  return departments.filter(d => {
    const matchesSearch = d.name.toLowerCase().includes(searchDeptQuery.value.toLowerCase()) || d.desc.toLowerCase().includes(searchDeptQuery.value.toLowerCase())
    const matchesFilter = selectedDeptFilter.value === 'ALL' || d.type === selectedDeptFilter.value
    return matchesSearch && matchesFilter
  })
})

/* ==================== 3. DOCTORS LIST & DETAIL ==================== */
const searchDocQuery = ref('')
const selectedSpecialty = ref('ALL')
const selectedTitle = ref('ALL')
const selectedDoctor = ref(null) // Detail modal

const doctors = ref([])

const fallbackDoctors = [
  { name: 'PGS.TS Nguyễn Văn An', title: 'PGS.TS', specialty: 'Tim mạch', experience: '25 năm kinh nghiệm', image: '/images/doctors/doctor1.png', dept: 'Khoa Tim Mạch', bio: 'Nguyên Phó Giám đốc Bệnh viện Tim, Giáo sư thỉnh giảng Đại học Y Hà Nội. Chuyên sâu về can thiệp tim mạch, điều trị suy tim và tăng huyết áp.', schedule: 'Thứ 2, Thứ 4, Thứ 6 (Sáng: 08:00 - 11:30)' },
  { name: 'TS.BS Trần Thị Mai', title: 'TS.BS', specialty: 'Sản phụ khoa', experience: '20 năm kinh nghiệm', image: '/images/doctors/doctor2.png', dept: 'Khoa Sản Phụ Khoa', bio: 'Tốt nghiệp Tiến sĩ tại Pháp. Hơn 20 năm kinh nghiệm trong chẩn đoán trước sinh và phẫu thuật phụ khoa nội soi phức tạp.', schedule: 'Thứ 3, Thứ 5 (Cả ngày: 08:00 - 16:30)' },
  { name: 'GS.TS Lê Hoàng Minh', title: 'GS.TS', specialty: 'Ngoại khoa', experience: '30 năm kinh nghiệm', image: '/images/doctors/doctor3.png', dept: 'Khoa Ngoại Tổng Quát', bio: 'Giáo sư đầu ngành ngoại khoa Việt Nam. Đã trực tiếp phẫu thuật thành công hàng ngàn ca ngoại tiêu hóa và chấn thương nặng.', schedule: 'Thứ 2, Thứ 5 (Sáng: 08:00 - 12:00)' },
  { name: 'ThS.BS Phạm Thùy Linh', title: 'ThS.BS', specialty: 'Nhi khoa', experience: '15 năm kinh nghiệm', image: '/images/doctors/doctor1.png', dept: 'Khoa Nhi Khoa', bio: 'Thạc sĩ Nhi khoa Đại học Y Hà Nội. Yêu trẻ em, có kinh nghiệm chuyên sâu về dinh dưỡng, hô hấp và tiêm chủng trẻ em.', schedule: 'Hằng ngày (Trừ Chủ nhật)' },
  { name: 'TS.BS Lê Thị Lan', title: 'TS.BS', specialty: 'Mắt', experience: '18 năm kinh nghiệm', image: '/images/doctors/doctor2.png', dept: 'Khoa Mắt', bio: 'Chuyên gia hàng đầu về phẫu thuật Phaco điều trị đục thủy tinh thể (cườm khô) ở người cao tuổi. Thành viên Hội Nhãn khoa Việt Nam.', schedule: 'Thứ 3, Thứ 6 (Chiều: 13:30 - 16:30)' },
  { name: 'ThS.BS Trần Văn Bình', title: 'ThS.BS', specialty: 'Nội tổng quát', experience: '12 năm kinh nghiệm', image: '/images/doctors/doctor3.png', dept: 'Khoa Nội Tổng Quát', bio: 'Chuyên gia khám điều trị các bệnh lão khoa, xương khớp, tiểu đường và các bệnh chuyển hóa mãn tính ở người lớn tuổi.', schedule: 'Thứ 2 đến Thứ 7 (Hằng ngày)' }
]

function extractTitle(name) {
  const match = name.match(/^(GS\.TS|PGS\.TS|TS\.BS|ThS\.BS|BS\.CK[12]?|BS)/)
  return match ? match[1] : 'BS'
}

function loadDoctors() {
  const staffData = localStorage.getItem('hospitalStaff')
  if (staffData) {
    const staffList = JSON.parse(staffData)
    const doctorStaff = staffList.filter(s => s.role === 'Bác sĩ chuyên khoa')
    if (doctorStaff.length > 0) {
      doctors.value = doctorStaff.map(s => ({
        name: s.name,
        title: extractTitle(s.name),
        specialty: s.specialty,
        experience: s.bio ? s.bio.substring(0, 30) + '...' : '10+ năm kinh nghiệm',
        image: '/images/doctors/doctor1.png',
        dept: 'Khoa ' + s.specialty,
        bio: s.bio || 'Chưa cập nhật tiểu sử.',
        schedule: s.schedule || 'Liên hệ để biết lịch khám',
        room: s.room || ''
      }))
      return
    }
  }
  doctors.value = fallbackDoctors
}

const specialties = ['ALL', 'Tim mạch', 'Nội tổng quát', 'Ngoại khoa', 'Sản phụ khoa', 'Nhi khoa', 'Mắt']
const titles = ['ALL', 'GS.TS', 'PGS.TS', 'TS.BS', 'ThS.BS']

const filteredDoctors = computed(() => {
  return doctors.value.filter(d => {
    const matchesSearch = d.name.toLowerCase().includes(searchDocQuery.value.toLowerCase()) || d.bio.toLowerCase().includes(searchDocQuery.value.toLowerCase())
    const matchesSpecialty = selectedSpecialty.value === 'ALL' || d.specialty === selectedSpecialty.value
    const matchesTitle = selectedTitle.value === 'ALL' || d.title === selectedTitle.value
    return matchesSearch && matchesSpecialty && matchesTitle
  })
})
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">

    <!-- ==================== TAB NAVIGATION ==================== -->
    <div class="bg-white border-b border-gray-200 sticky top-[68px] sm:top-[72px] z-10 shadow-sm">
      <div class="max-w-7xl mx-auto px-4 flex justify-between md:justify-center gap-1 overflow-x-auto">
        <button
          @click="switchTab('intro')"
          class="flex-shrink-0 py-4 px-6 font-bold text-lg border-b-4 transition-all duration-200 flex items-center gap-2"
          :class="activeTab === 'intro' ? 'border-primary-700 text-primary-700 bg-primary-50/50' : 'border-transparent text-gray-500 hover:text-primary-600'"
        >
          <i class="bi bi-info-circle text-xl"></i>
          Giới Thiệu Chung
        </button>
        <button
          @click="switchTab('map')"
          class="flex-shrink-0 py-4 px-6 font-bold text-lg border-b-4 transition-all duration-200 flex items-center gap-2"
          :class="activeTab === 'map' ? 'border-primary-700 text-primary-700 bg-primary-50/50' : 'border-transparent text-gray-500 hover:text-primary-600'"
        >
          <i class="bi bi-map text-xl"></i>
          Sơ Đồ Bệnh Viện
        </button>
        <button
          @click="switchTab('departments')"
          class="flex-shrink-0 py-4 px-6 font-bold text-lg border-b-4 transition-all duration-200 flex items-center gap-2"
          :class="activeTab === 'departments' ? 'border-primary-700 text-primary-700 bg-primary-50/50' : 'border-transparent text-gray-500 hover:text-primary-600'"
        >
          <i class="bi bi-building text-xl"></i>
          Danh Sách Các Khoa
        </button>
        <button
          @click="switchTab('doctors')"
          class="flex-shrink-0 py-4 px-6 font-bold text-lg border-b-4 transition-all duration-200 flex items-center gap-2"
          :class="activeTab === 'doctors' ? 'border-primary-700 text-primary-700 bg-primary-50/50' : 'border-transparent text-gray-500 hover:text-primary-600'"
        >
          <i class="bi bi-people text-xl"></i>
          Đội Ngũ Bác Sĩ
        </button>
      </div>
    </div>

    <!-- ==================== MAIN CONTENT AREA ==================== -->
    <div class="max-w-7xl mx-auto px-4 mt-8">
      
      <!-- ==================== TAB: GIỚI THIỆU CHUNG ==================== -->
      <div v-if="activeTab === 'intro'" class="animate-fade-in bg-white p-8 md:p-10 rounded-3xl shadow-sm border border-gray-100 space-y-6">
        <div class="prose max-w-none text-gray-700 leading-relaxed ProseMirror" v-html="hospitalIntro"></div>
      </div>

      <!-- ==================== TAB: SƠ ĐỒ BỆNH VIỆN ==================== -->
      <div v-if="activeTab === 'map'" class="animate-fade-in space-y-6">
        <!-- Search and Floor Selector Row -->
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 max-w-4xl mx-auto">
          <div class="flex flex-col md:flex-row gap-4 items-end">
            <!-- Search Map -->
            <div class="flex-1 w-full">
              <label for="search-map" class="block text-base font-bold text-gray-700 mb-2">Tìm kiếm phòng/khoa khám nhanh:</label>
              <div class="relative">
                <input
                  id="search-map"
                  v-model="searchMapQuery"
                  type="text"
                  placeholder="Nhập tên phòng khám, tên khoa (Ví dụ: X-Quang, Cấp cứu, Tim mạch...)"
                  class="w-full pl-12 pr-4 py-3.5 text-base rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                />
                <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-lg text-gray-400"></i>
              </div>
            </div>

            <!-- Floor Dropdown Selector -->
            <div class="w-full md:w-72">
              <label for="floor-select" class="block text-base font-bold text-gray-700 mb-2">Chọn tầng bệnh viện:</label>
              <div class="relative">
                <select
                  id="floor-select"
                  v-model="selectedFloor"
                  class="w-full px-4 pr-10 py-3.5 text-base font-semibold text-gray-700 bg-white border border-gray-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all appearance-none cursor-pointer"
                >
                  <option v-for="(val, key) in floors" :key="key" :value="key">
                    Tầng {{ key === 'G' ? 'Trệt (G)' : key }} - {{ val.name.split(' - ')[1] || val.name }}
                  </option>
                </select>
                <i class="bi bi-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none"></i>
              </div>
            </div>
          </div>
          
          <!-- Search Results Dropdown -->
          <div v-if="mapSearchResult.length > 0" class="mt-4 border border-gray-100 rounded-2xl divide-y divide-gray-100 bg-gray-50 max-h-60 overflow-y-auto">
            <button
              v-for="res in mapSearchResult"
              :key="res.id"
              @click="selectResult(res)"
              class="w-full px-4 py-3 text-left hover:bg-primary-50 transition-colors flex items-center justify-between text-base"
            >
              <div>
                <span class="font-bold text-primary-800">{{ res.name }}</span>
                <span class="ml-2 text-xs bg-gray-200 text-gray-600 px-2 py-0.5 rounded">{{ res.code }}</span>
                <p class="text-xs text-gray-400 mt-0.5">{{ res.floorName }}</p>
              </div>
              <span class="text-sm text-primary-700 font-medium">Xem trên bản đồ <i class="bi bi-arrow-right"></i></span>
            </button>
          </div>
          <div v-else-if="searchMapQuery" class="mt-3 text-center text-gray-400 text-base">Không tìm thấy phòng/khoa phù hợp.</div>
        </div>

        <div class="text-center font-bold text-gray-800 text-xl">{{ floors[selectedFloor].name }}</div>

        <!-- Virtual Interactive Floor Plan Grid -->
        <div class="bg-white p-6 md:p-8 rounded-3xl shadow-sm border border-gray-100 max-w-4xl mx-auto">
          <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
            <div
              v-for="room in currentRooms"
              :key="room.id"
              class="p-6 border-2 rounded-2xl text-center flex flex-col justify-between min-h-48 transition-all hover:scale-[1.02] hover:shadow-lg"
              :class="getRoomColorClass(room.type)"
            >
              <div>
                <div class="flex justify-between items-start mb-2">
                  <span class="text-xs font-bold px-2.5 py-1 bg-white/80 rounded-full border shadow-sm">
                    {{ room.code }}
                  </span>
                  <i class="bi text-xl" :class="{
                    'bi-exclamation-triangle-fill text-red-600': room.type === 'emergency',
                    'bi-info-circle-fill text-blue-600': room.type === 'reception',
                    'bi-capsule text-emerald-600': room.type === 'pharmacy',
                    'bi-heart-pulse-fill text-red-500 animate-pulse': room.id === 'CARD',
                    'bi-activity text-purple-600': room.type === 'diagnostic',
                    'bi-house-heart text-amber-600': room.type === 'ward'
                  }"></i>
                </div>
                <h3 class="text-lg font-bold mb-2 leading-snug">{{ room.name }}</h3>
                <p class="text-sm text-gray-500 leading-relaxed">{{ room.desc }}</p>
              </div>
              
              <div class="mt-4 pt-3 border-t border-gray-100/50 flex justify-between items-center text-xs">
                <span class="capitalize text-gray-400">Khu: {{ room.type }}</span>
                <span class="font-semibold text-primary-700"><i class="bi bi-geo-alt"></i> Tầng {{ selectedFloor }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== TAB: DANH SÁCH CÁC KHOA ==================== -->
      <div v-if="activeTab === 'departments'" class="animate-fade-in space-y-6">
        <!-- Search & Filter Controls -->
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4 justify-between items-center">
          <div class="relative w-full md:max-w-md">
            <input
              v-model="searchDeptQuery"
              type="text"
              placeholder="Tìm khoa khám nhanh..."
              class="w-full pl-12 pr-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 placeholder:text-gray-400"
            />
            <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-lg text-gray-400"></i>
          </div>

          <div class="flex gap-2 overflow-x-auto w-full md:w-auto">
            <button
              @click="selectedDeptFilter = 'ALL'"
              class="px-4 py-2.5 rounded-xl text-base font-bold transition-all border"
              :class="selectedDeptFilter === 'ALL' ? 'bg-primary-700 text-white border-primary-700 shadow-sm' : 'bg-white text-gray-600 border-gray-200 hover:border-primary-400'"
            >
              Tất cả khoa
            </button>
            <button
              @click="selectedDeptFilter = 'CLINICAL'"
              class="px-4 py-2.5 rounded-xl text-base font-bold transition-all border"
              :class="selectedDeptFilter === 'CLINICAL' ? 'bg-primary-700 text-white border-primary-700 shadow-sm' : 'bg-white text-gray-600 border-gray-200 hover:border-primary-400'"
            >
              Lâm sàng (Khám chữa)
            </button>
            <button
              @click="selectedDeptFilter = 'DIAGNOSTIC'"
              class="px-4 py-2.5 rounded-xl text-base font-bold transition-all border"
              :class="selectedDeptFilter === 'DIAGNOSTIC' ? 'bg-primary-700 text-white border-primary-700 shadow-sm' : 'bg-white text-gray-600 border-gray-200 hover:border-primary-400'"
            >
              Cận lâm sàng (Xét nghiệm/X-Quang)
            </button>
          </div>
        </div>

        <!-- Departments Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="dept in filteredDepartments"
            :key="dept.id"
            @click="selectedDept = dept"
            class="bg-white border border-gray-100 rounded-3xl p-6 shadow-sm hover:shadow-lg transition-all duration-300 cursor-pointer flex flex-col justify-between group"
          >
            <div>
              <div class="flex items-center gap-4 mb-4">
                <div class="w-14 h-14 rounded-2xl bg-primary-100 text-primary-700 flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <i :class="'bi ' + dept.icon" class="text-2xl"></i>
                </div>
                <div>
                  <h3 class="text-xl font-bold text-gray-800 group-hover:text-primary-700 transition-colors">{{ dept.name }}</h3>
                  <span class="text-xs font-semibold px-2 py-0.5 bg-gray-100 text-gray-500 rounded-full mt-0.5 inline-block">
                    {{ dept.type === 'CLINICAL' ? 'Lâm sàng' : 'Cận lâm sàng' }}
                  </span>
                </div>
              </div>
              <p class="text-base text-gray-500 leading-relaxed line-clamp-3 mb-4">
                {{ dept.desc }}
              </p>
            </div>
            
            <div class="pt-4 border-t border-gray-50 flex justify-between items-center text-sm font-semibold text-primary-700">
              <span>Trưởng khoa: {{ dept.lead }}</span>
              <span class="group-hover:translate-x-1 transition-transform">Xem chi tiết <i class="bi bi-arrow-right"></i></span>
            </div>
          </div>
        </div>

        <div v-if="filteredDepartments.length === 0" class="text-center py-12 text-gray-400 text-lg">
          Không tìm thấy khoa phù hợp với từ khóa tìm kiếm.
        </div>
      </div>

      <!-- ==================== TAB: ĐỘI NGŨ BÁC SĨ ==================== -->
      <div v-if="activeTab === 'doctors'" class="animate-fade-in space-y-6">
        <!-- Search & Filter Controls -->
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- Search field -->
            <div class="relative md:col-span-1">
              <label for="search-doc" class="sr-only">Tìm theo tên bác sĩ</label>
              <input
                id="search-doc"
                v-model="searchDocQuery"
                type="text"
                placeholder="Tìm theo tên bác sĩ..."
                class="w-full pl-12 pr-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 placeholder:text-gray-400"
              />
              <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-lg text-gray-400"></i>
            </div>
            
            <!-- Specialty filter -->
            <div>
              <label for="filter-specialty" class="sr-only">Chuyên khoa</label>
              <select
                id="filter-specialty"
                v-model="selectedSpecialty"
                class="w-full px-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 appearance-none bg-no-repeat bg-right"
                style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-position: right 16px center;"
              >
                <option value="ALL">-- Tất cả chuyên khoa --</option>
                <option v-for="sp in specialties.slice(1)" :key="sp" :value="sp">{{ sp }}</option>
              </select>
            </div>

            <!-- Title filter -->
            <div>
              <label for="filter-title" class="sr-only">Chức danh học vị</label>
              <select
                id="filter-title"
                v-model="selectedTitle"
                class="w-full px-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 appearance-none bg-no-repeat bg-right"
                style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-position: right 16px center;"
              >
                <option value="ALL">-- Tất cả chức danh --</option>
                <option v-for="t in titles.slice(1)" :key="t" :value="t">{{ t }}</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Doctors Grid -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="doc in filteredDoctors"
            :key="doc.name"
            @click="selectedDoctor = doc"
            class="bg-white border border-gray-100 rounded-3xl overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 cursor-pointer flex flex-col justify-between group"
          >
            <div class="p-6 flex gap-4">
              <!-- Doctor Image Mock -->
              <div class="w-24 h-24 rounded-2xl bg-gradient-to-br from-primary-100 to-primary-200 flex items-center justify-center flex-shrink-0 border shadow-sm">
                <i class="bi bi-person-badge text-primary-700 text-4xl"></i>
              </div>
              <div>
                <span class="inline-block px-2.5 py-0.5 bg-primary-50 text-primary-700 font-bold text-xs rounded-full mb-1">
                  {{ doc.specialty }}
                </span>
                <h3 class="text-lg font-bold text-gray-800 group-hover:text-primary-700 transition-colors leading-snug">
                  {{ doc.name }}
                </h3>
                <p class="text-sm text-gray-500 mt-1"><i class="bi bi-briefcase mr-1 text-primary-600"></i> {{ doc.experience }}</p>
                <p class="text-xs text-gray-400 mt-0.5"><i class="bi bi-building mr-1 text-primary-600"></i> {{ doc.dept }}</p>
              </div>
            </div>
            
            <div class="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center text-sm font-semibold text-primary-700">
              <span class="group-hover:translate-x-1 transition-transform">Thông tin chi tiết <i class="bi bi-arrow-right"></i></span>
            </div>
          </div>
        </div>

        <div v-if="filteredDoctors.length === 0" class="text-center py-12 text-gray-400 text-lg">
          Không tìm thấy bác sĩ phù hợp với tiêu chí tìm kiếm.
        </div>
      </div>

    </div>

    <!-- ==================== DETAIL MODALS (POPUP) ==================== -->
    
    <!-- Department Detail Modal -->
    <div v-if="selectedDept" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-2xl w-full max-h-[85vh] overflow-y-auto p-6 md:p-8 animate-fade-in-up">
        <div class="flex justify-between items-start border-b border-gray-100 pb-4 mb-6">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-xl bg-primary-100 text-primary-700 flex items-center justify-center">
              <i :class="'bi ' + selectedDept.icon" class="text-xl"></i>
            </div>
            <div>
              <h2 class="text-2xl font-bold text-gray-800">{{ selectedDept.name }}</h2>
              <span class="text-xs font-semibold px-2.5 py-0.5 bg-primary-50 text-primary-700 rounded-full mt-0.5 inline-block">
                {{ selectedDept.type === 'CLINICAL' ? 'Lâm sàng' : 'Cận lâm sàng' }}
              </span>
            </div>
          </div>
          <button @click="selectedDept = null" class="w-10 h-10 rounded-full hover:bg-gray-100 flex items-center justify-center text-2xl text-gray-400 hover:text-gray-600 transition-colors">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="space-y-6">
          <div>
            <h4 class="text-lg font-bold text-gray-800 mb-2"><i class="bi bi-info-circle text-primary-600 mr-1.5"></i> Giới thiệu khoa:</h4>
            <p class="text-base text-gray-600 leading-relaxed">{{ selectedDept.desc }}</p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h5 class="font-bold text-gray-700 mb-1"><i class="bi bi-person-badge text-primary-600 mr-1.5"></i> Trưởng khoa</h5>
              <p class="text-base text-primary-800 font-semibold">{{ selectedDept.lead }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h5 class="font-bold text-gray-700 mb-1"><i class="bi bi-clock text-primary-600 mr-1.5"></i> Thời gian khám</h5>
              <p class="text-base text-gray-600">{{ selectedDept.hours }}</p>
            </div>
          </div>

          <div>
            <h4 class="text-lg font-bold text-gray-800 mb-2"><i class="bi bi-hospital text-primary-600 mr-1.5"></i> Trang thiết bị chuyên khoa:</h4>
            <p class="text-base text-gray-600 leading-relaxed bg-primary-50/50 p-4 rounded-2xl border border-primary-100">
              {{ selectedDept.facilities }}
            </p>
          </div>
        </div>

        <div class="mt-8 pt-4 border-t border-gray-100 flex justify-end gap-3">
          <button @click="selectedDept = null" class="px-6 py-3 border border-gray-200 rounded-xl font-bold text-gray-600 hover:bg-gray-50 text-base">Đóng</button>
          <RouterLink to="/dat-lich" @click="selectedDept = null" class="btn-primary !py-3 !px-6 !text-base !rounded-xl">
            <i class="bi bi-calendar-check"></i> Đặt lịch khám tại khoa này
          </RouterLink>
        </div>
      </div>
    </div>

    <!-- Doctor Detail Modal -->
    <div v-if="selectedDoctor" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-2xl w-full max-h-[85vh] overflow-y-auto p-6 md:p-8 animate-fade-in-up">
        <div class="flex justify-between items-start border-b border-gray-100 pb-4 mb-6">
          <div class="flex items-center gap-4">
            <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-primary-100 to-primary-200 flex items-center justify-center border shadow-sm">
              <i class="bi bi-person-badge text-primary-700 text-3xl"></i>
            </div>
            <div>
              <h2 class="text-2xl font-bold text-gray-800">{{ selectedDoctor.name }}</h2>
              <span class="text-xs font-semibold px-2.5 py-0.5 bg-primary-50 text-primary-700 rounded-full mt-0.5 inline-block">
                {{ selectedDoctor.specialty }} — {{ selectedDoctor.dept }}
              </span>
            </div>
          </div>
          <button @click="selectedDoctor = null" class="w-10 h-10 rounded-full hover:bg-gray-100 flex items-center justify-center text-2xl text-gray-400 hover:text-gray-600 transition-colors">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="space-y-6">
          <div>
            <h4 class="text-lg font-bold text-gray-800 mb-2"><i class="bi bi-book text-primary-600 mr-1.5"></i> Tiểu sử & Học hàm học vị:</h4>
            <p class="text-base text-gray-600 leading-relaxed">{{ selectedDoctor.bio }}</p>
          </div>

          <div class="grid grid-cols-1 gap-4">
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h5 class="font-bold text-gray-700 mb-1"><i class="bi bi-award text-primary-600 mr-1.5"></i> Kinh nghiệm lâm sàng</h5>
              <p class="text-base text-gray-700">{{ selectedDoctor.experience }}</p>
            </div>
          </div>
        </div>

        <div class="mt-8 pt-4 border-t border-gray-100 flex justify-end gap-3">
          <button @click="selectedDoctor = null" class="px-6 py-3 border border-gray-200 rounded-xl font-bold text-gray-600 hover:bg-gray-50 text-base">Đóng</button>
          <RouterLink to="/dat-lich" @click="selectedDoctor = null" class="btn-primary !py-3 !px-6 !text-base !rounded-xl">
            <i class="bi bi-calendar-check"></i> Đặt lịch khám với bác sĩ
          </RouterLink>
        </div>
      </div>
    </div>

  </div>
</template>
