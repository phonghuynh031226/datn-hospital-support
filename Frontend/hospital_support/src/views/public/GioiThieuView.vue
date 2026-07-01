<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const activeTab = ref('intro')
const hospitalIntro = ref(`<h2 style="text-align: center; color: #1e3a8a;"><strong>HỆ THỐNG Y TẾ BỆNH VIỆN ĐA KHOA QUỐC TẾ</strong></h2>` +
  `<p style="text-align: center; font-style: italic; color: #4b5563;">Hệ thống y tế hiện đại, uy tín hàng đầu — Nơi sức khỏe của bạn là ưu tiên số một</p>` +
  `<hr />` +
  `<p>Bệnh viện Đa khoa Quốc tế tự hào là một trong những cơ sở y tế hàng đầu Việt Nam cung cấp dịch vụ khám chữa bệnh chất lượng cao theo tiêu chuẩn quốc tế. Với triết lý <strong>"Tận tâm vì sức khỏe người bệnh"</strong>, chúng tôi không ngừng cải tiến quy trình, đầu tư trang thiết bị công nghệ hiện đại và quy tụ đội ngũ chuyên gia, y bác sĩ đầu ngành nhằm đem lại trải nghiệm y tế hoàn hảo nhất.</p>`)

onMounted(async () => {
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  await loadIntro()
  await loadHospitalMap()
  await loadDepartments()
  await loadDoctors()
})

async function loadIntro() {
  try {
    const res = await axios.get('http://localhost:8080/api/gioi-thieu')
    if (res.data && res.data.noiDung) {
      hospitalIntro.value = res.data.noiDung
    }
  } catch (err) {
    console.error('Lỗi khi tải thông tin giới thiệu:', err)
  }
}

function switchTab(tab) {
  activeTab.value = tab
  router.push({ query: { tab } })
}

/* ==================== 1. HOSPITAL MAP DATA & LOGIC ==================== */
const searchMapQuery = ref('')
const selectedFloor = ref('G')
const floors = ref({})

async function loadHospitalMap() {
  try {
    const resFloors = await axios.get('http://localhost:8080/api/so-do-benh-vien')
    const resRooms = await axios.get('http://localhost:8080/api/vi-tri-phong')
    
    const tempFloors = {}
    resFloors.data.forEach(f => {
      let key = f.id.toString()
      if (f.tenTang.toLowerCase().includes('trệt') || f.tenTang.toLowerCase().includes('tầng g')) {
        key = 'G'
      } else {
        const match = f.tenTang.match(/\d+/)
        if (match) key = match[0]
      }
      
      tempFloors[key] = {
        id: f.id,
        name: f.tenTang,
        rooms: []
      }
    })

    resRooms.data.forEach(r => {
      const floorKey = Object.keys(tempFloors).find(k => tempFloors[k].id === r.soDo.id)
      if (floorKey) {
        let type = 'clinical'
        const lp = r.loaiPhong ? r.loaiPhong.toUpperCase() : ''
        if (lp.includes('CAP_CUU') || lp.includes('EMERGENCY')) type = 'emergency'
        else if (lp.includes('TIEP_DON') || lp.includes('RECEPTION')) type = 'reception'
        else if (lp.includes('NHA_THUOC') || lp.includes('PHARMACY')) type = 'pharmacy'
        else if (lp.includes('CHAN_DOAN') || lp.includes('DIAGNOSTIC')) type = 'diagnostic'
        else if (lp.includes('BUONG_BENH') || lp.includes('WARD')) type = 'ward'

        tempFloors[floorKey].rooms.push({
          id: r.id,
          name: r.tenPhong,
          code: r.soPhong || `P.${r.id}`,
          desc: r.moTa || 'Phòng khám bệnh viện đa khoa.',
          type: type
        })
      }
    })

    const keys = Object.keys(tempFloors)
    if (keys.length > 0) {
      floors.value = tempFloors
      if (!keys.includes(selectedFloor.value)) {
        selectedFloor.value = keys[0]
      }
    }
  } catch (err) {
    console.error('Lỗi khi tải sơ đồ bệnh viện:', err)
  }
}

const mapSearchResult = computed(() => {
  if (!searchMapQuery.value.trim()) return []
  const query = searchMapQuery.value.toLowerCase()
  const results = []
  Object.keys(floors.value).forEach(floorKey => {
    floors.value[floorKey].rooms.forEach(room => {
      if (room.name.toLowerCase().includes(query) || room.code.toLowerCase().includes(query) || room.desc.toLowerCase().includes(query)) {
        results.push({ floorKey, floorName: floors.value[floorKey].name, ...room })
      }
    })
  })
  return results
})

function selectResult(result) {
  selectedFloor.value = result.floorKey
  searchMapQuery.value = ''
}

const currentRooms = computed(() => {
  return floors.value[selectedFloor.value] ? floors.value[selectedFloor.value].rooms : []
})

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
const selectedDeptFilter = ref('ALL')
const selectedDept = ref(null)
const departments = ref([])

async function loadDepartments() {
  try {
    const res = await axios.get('http://localhost:8080/api/khoa')
    if (res.data && res.data.length > 0) {
      departments.value = res.data.map(item => {
        let type = 'CLINICAL'
        const name = item.tenKhoa.toLowerCase()
        if (name.includes('xét nghiệm') || name.includes('x-quang') || name.includes('siêu âm') || name.includes('hình ảnh') || name.includes('chẩn đoán')) {
          type = 'DIAGNOSTIC'
        }

        let icon = 'bi-building'
        const slug = item.slug.toLowerCase()
        if (slug.includes('tim-mach') || slug.includes('tim')) icon = 'bi-heart-pulse'
        else if (slug.includes('noi')) icon = 'bi-clipboard2-pulse'
        else if (slug.includes('ngoai')) icon = 'bi-bandaid'
        else if (slug.includes('san') || slug.includes('phu-khoa')) icon = 'bi-gender-female'
        else if (slug.includes('nhi')) icon = 'bi-emoji-smile'
        else if (slug.includes('mat')) icon = 'bi-eye'
        else if (slug.includes('x-quang') || slug.includes('sieu-am') || slug.includes('hinh-anh')) icon = 'bi-exclude'
        else if (slug.includes('xet-nghiem')) icon = 'bi-droplet-half'

        return {
          id: item.id,
          type: type,
          icon: icon,
          name: item.tenKhoa,
          desc: item.moTaNgan || 'Giới thiệu khoa khám chữa bệnh.',
          lead: 'Bác sĩ trưởng khoa',
          facilities: item.trangThietBi || 'Thiết bị hiện đại chuẩn quốc tế.',
          hours: '7:30 - 16:30 (Thứ 2 - Thứ 7)'
        }
      })
    }
  } catch (err) {
    console.error('Lỗi khi tải danh sách khoa:', err)
  }
}

const filteredDepartments = computed(() => {
  return departments.value.filter(d => {
    const matchesSearch = d.name.toLowerCase().includes(searchDeptQuery.value.toLowerCase()) || d.desc.toLowerCase().includes(searchDeptQuery.value.toLowerCase())
    const matchesFilter = selectedDeptFilter.value === 'ALL' || d.type === selectedDeptFilter.value
    return matchesSearch && matchesFilter
  })
})

/* ==================== 3. DOCTORS LIST & DETAIL ==================== */
const searchDocQuery = ref('')
const selectedSpecialty = ref('ALL')
const selectedTitle = ref('ALL')
const selectedDoctor = ref(null)
const doctors = ref([])

const fallbackDoctors = [
  { name: 'PGS.TS Nguyễn Văn An', title: 'PGS.TS', specialty: 'Tim mạch', experience: '25 năm kinh nghiệm', image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80', dept: 'Khoa Tim Mạch', bio: 'Can thiệp tim mạch, điều trị suy tim và tăng huyết áp.', schedule: 'Thứ 2, Thứ 4, Thứ 6 (Sáng: 08:00 - 11:30)' }
]

function extractTitle(name) {
  const match = name.match(/^(GS\.TS|PGS\.TS|TS\.BS|ThS\.BS|BS\.CK[12]?|BS)/)
  return match ? match[1] : 'BS'
}

async function loadDoctors() {
  try {
    const res = await axios.get('http://localhost:8080/api/bac-si')
    if (res.data && res.data.length > 0) {
      const defaultImages = [
        'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80',
        'https://images.unsplash.com/photo-1594824813573-246434de83fb?auto=format&fit=crop&w=600&q=80',
        'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=600&q=80',
        'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80',
      ]
      doctors.value = res.data.map((s, index) => {
        return {
          id: s.id,
          name: s.hoTen,
          title: extractTitle(s.hoTen),
          specialty: s.chuyenKhoa || 'Bác sĩ chuyên khoa',
          experience: s.chucVu || 'Bác sĩ điều trị',
          image: defaultImages[index % defaultImages.length],
          dept: s.chuyenKhoa || 'Khoa khám',
          bio: s.bio || 'Bác sĩ điều trị giỏi, tận tâm và giàu kinh nghiệm.',
          schedule: 'Theo lịch phân công phòng khám'
        }
      })
    } else {
      doctors.value = fallbackDoctors
    }
  } catch (err) {
    console.error('Lỗi khi tải danh sách bác sĩ:', err)
    doctors.value = fallbackDoctors
  }
}

async function viewDoctorDetail(doc) {
  selectedDoctor.value = doc
  try {
    const res = await axios.get(`http://localhost:8080/api/bac-si/${doc.id}`)
    if (res.data && res.data.bio) {
      selectedDoctor.value.bio = res.data.bio
    }
  } catch (err) {
    console.error('Lỗi khi tải tiểu sử bác sĩ:', err)
  }
}

const specialties = computed(() => {
  const list = new Set()
  doctors.value.forEach(d => {
    if (d.specialty) list.add(d.specialty)
  })
  return ['ALL', ...Array.from(list)]
})

const titles = computed(() => {
  const list = new Set()
  doctors.value.forEach(d => {
    if (d.title) list.add(d.title)
  })
  return ['ALL', ...Array.from(list)]
})

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

        <div class="text-center font-bold text-gray-800 text-xl">{{ floors[selectedFloor]?.name || 'Đang tải sơ đồ...' }}</div>

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
              <!-- Doctor Image -->
              <div class="w-24 h-24 rounded-2xl overflow-hidden flex-shrink-0 border shadow-sm bg-gray-50 bg-gradient-to-br from-primary-100 to-primary-200">
                <img v-if="doc.image" :src="doc.image" :alt="doc.name" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <i class="bi bi-person-badge text-primary-700 text-4xl"></i>
                </div>
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
            <div class="w-16 h-16 rounded-2xl overflow-hidden border shadow-sm bg-gray-50 flex-shrink-0 bg-gradient-to-br from-primary-100 to-primary-200">
              <img v-if="selectedDoctor.image" :src="selectedDoctor.image" :alt="selectedDoctor.name" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center">
                <i class="bi bi-person-badge text-primary-700 text-3xl"></i>
              </div>
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
