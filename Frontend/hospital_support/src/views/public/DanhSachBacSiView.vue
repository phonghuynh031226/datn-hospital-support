<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

const searchDocQuery = ref('')
const selectedSpecialty = ref('ALL')
const selectedTitle = ref('ALL')
const selectedDoctor = ref(null) // Detail modal

const doctors = ref([])
const specialtiesList = ref([{ id: 'ALL', tenKhoa: 'Tất cả chuyên khoa' }])

function extractTitle(name) {
  const match = name.match(/^(GS\.TS|PGS\.TS|TS\.BS|ThS\.BS|BS\.CK[12]?|BS)/)
  return match ? match[1] : 'BS'
}

async function loadSpecialties() {
  try {
    const res = await axios.get('http://localhost:8080/api/khoa')
    specialtiesList.value = [{ id: 'ALL', tenKhoa: 'Tất cả chuyên khoa' }, ...res.data]
  } catch (err) {
    console.error(err)
  }
}

async function loadDoctors() {
  try {
    let url = 'http://localhost:8080/api/bac-si?'
    if (selectedSpecialty.value !== 'ALL') {
      url += `departmentId=${selectedSpecialty.value}&`
    }
    if (searchDocQuery.value) {
      url += `search=${encodeURIComponent(searchDocQuery.value)}&`
    }
    
    const res = await axios.get(url)
    doctors.value = res.data.map((s, index) => {
      const defaultImages = [
        'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80',
        'https://images.unsplash.com/photo-1594824813573-246434de83fb?auto=format&fit=crop&w=600&q=80',
        'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=600&q=80',
        'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80',
      ]
      return {
        id: s.id,
        name: s.hoTen,
        title: extractTitle(s.hoTen),
        specialty: 'Bác sĩ chuyên khoa',
        experience: 'Bác sĩ điều trị',
        image: defaultImages[index % defaultImages.length],
        dept: 'Bệnh viện',
        bio: 'Bác sĩ điều trị tận tâm.',
        schedule: 'Liên hệ để đặt lịch khám',
        room: 'Phòng khám bệnh'
      }
    })
  } catch (err) {
    console.error(err)
  }
}

async function viewDoctorDetail(docId) {
  try {
    const res = await axios.get(`http://localhost:8080/api/bac-si/${docId}`)
    const item = res.data.nhanVien
    const bio = res.data.bio
    selectedDoctor.value = {
      id: item.id,
      name: item.hoTen,
      title: extractTitle(item.hoTen),
      specialty: 'Bác sĩ chuyên khoa',
      experience: 'Bác sĩ điều trị',
      image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80',
      dept: 'Bệnh viện',
      bio: bio || 'Chưa cập nhật tiểu sử.',
      schedule: 'Theo lịch phân công bệnh viện',
      room: 'Phòng khám chuyên khoa'
    }
  } catch (err) {
    console.error(err)
  }
}

onMounted(async () => {
  await loadSpecialties()
  await loadDoctors()
})

watch([searchDocQuery, selectedSpecialty], () => {
  loadDoctors()
})

const specialties = ['ALL', 'Tim mạch', 'Nội tổng quát', 'Ngoại khoa', 'Sản phụ khoa', 'Nhi khoa', 'Mắt']
const titles = ['ALL', 'GS.TS', 'PGS.TS', 'TS.BS', 'ThS.BS']

const filteredDoctors = computed(() => {
  return doctors.value.filter(d => {
    const matchesTitle = selectedTitle.value === 'ALL' || d.title === selectedTitle.value
    return matchesTitle
  })
})
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12 md:py-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Đội Ngũ Bác Sĩ Đầu Ngành</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Tìm kiếm thông tin bác sĩ giỏi, giàu kinh nghiệm và lựa chọn lịch hẹn trực tuyến thuận tiện nhất.
        </p>
      </div>
    </div>

    <!-- ==================== SEARCH & FILTERS ==================== -->
    <div class="max-w-7xl mx-auto px-4 mt-8">
      <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 space-y-4 animate-fade-in">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- Search input -->
          <div class="relative">
            <label for="search-doc-main" class="sr-only">Tìm bác sĩ theo tên</label>
            <input
              id="search-doc-main"
              v-model="searchDocQuery"
              type="text"
              placeholder="Tìm theo tên hoặc chuyên khoa..."
              class="w-full pl-12 pr-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 placeholder:text-gray-400"
            />
            <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-lg text-gray-400"></i>
          </div>
          
          <!-- Specialty Select -->
          <div>
            <label for="select-spec-main" class="sr-only">Chuyên khoa</label>
            <select
              id="select-spec-main"
              v-model="selectedSpecialty"
              class="w-full px-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 appearance-none bg-no-repeat bg-right"
              style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-position: right 16px center;"
            >
              <option v-for="sp in specialtiesList" :key="sp.id" :value="sp.id">{{ sp.tenKhoa }}</option>
            </select>
          </div>

          <!-- Title Select -->
          <div>
            <label for="select-title-main" class="sr-only">Chức danh học vị</label>
            <select
              id="select-title-main"
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

      <!-- ==================== DOCTORS GRID ==================== -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-8 animate-fade-in-up">
        <div
          v-for="doc in filteredDoctors"
          :key="doc.name"
          @click="viewDoctorDetail(doc.id)"
          class="bg-white border border-gray-100 rounded-3xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 cursor-pointer flex flex-col justify-between group"
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

    <!-- ==================== DOCTOR DETAIL MODAL ==================== -->
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
