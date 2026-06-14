<script setup>
import { ref, computed } from 'vue'

const searchDocQuery = ref('')
const selectedSpecialty = ref('ALL')
const selectedTitle = ref('ALL')
const selectedDoctor = ref(null) // Detail modal

const doctors = [
  { name: 'PGS.TS Nguyễn Văn An', title: 'PGS.TS', specialty: 'Tim mạch', experience: '25 năm kinh nghiệm', image: '/images/doctors/doctor1.png', dept: 'Khoa Tim Mạch', bio: 'Nguyên Phó Giám đốc Bệnh viện Tim, Giáo sư thỉnh giảng Đại học Y Hà Nội. Chuyên sâu về can thiệp tim mạch, điều trị suy tim và tăng huyết áp.', schedule: 'Thứ 2, Thứ 4, Thứ 6 (Sáng: 08:00 - 11:30)' },
  { name: 'TS.BS Trần Thị Mai', title: 'TS.BS', specialty: 'Sản phụ khoa', experience: '20 năm kinh nghiệm', image: '/images/doctors/doctor2.png', dept: 'Khoa Sản Phụ Khoa', bio: 'Tốt nghiệp Tiến sĩ tại Pháp. Hơn 20 năm kinh nghiệm trong chẩn đoán trước sinh và phẫu thuật phụ khoa nội soi phức tạp.', schedule: 'Thứ 3, Thứ 5 (Cả ngày: 08:00 - 16:30)' },
  { name: 'GS.TS Lê Hoàng Minh', title: 'GS.TS', specialty: 'Ngoại khoa', experience: '30 năm kinh nghiệm', image: '/images/doctors/doctor3.png', dept: 'Khoa Ngoại Tổng Quát', bio: 'Giáo sư đầu ngành ngoại khoa Việt Nam. Đã trực tiếp phẫu thuật thành công hàng ngàn ca ngoại tiêu hóa và chấn thương nặng.', schedule: 'Thứ 2, Thứ 5 (Sáng: 08:00 - 12:00)' },
  { name: 'ThS.BS Phạm Thùy Linh', title: 'ThS.BS', specialty: 'Nhi khoa', experience: '15 năm kinh nghiệm', image: '/images/doctors/doctor1.png', dept: 'Khoa Nhi Khoa', bio: 'Thạc sĩ Nhi khoa Đại học Y Hà Nội. Yêu trẻ em, có kinh nghiệm chuyên sâu về dinh dưỡng, hô hấp và tiêm chủng trẻ em.', schedule: 'Hằng ngày (Trừ Chủ nhật)' },
  { name: 'TS.BS Lê Thị Lan', title: 'TS.BS', specialty: 'Mắt', experience: '18 năm kinh nghiệm', image: '/images/doctors/doctor2.png', dept: 'Khoa Mắt', bio: 'Chuyên gia hàng đầu về phẫu thuật Phaco điều trị đục thủy tinh thể (cườm khô) ở người cao tuổi. Thành viên Hội Nhãn khoa Việt Nam.', schedule: 'Thứ 3, Thứ 6 (Chiều: 13:30 - 16:30)' },
  { name: 'ThS.BS Trần Văn Bình', title: 'ThS.BS', specialty: 'Nội tổng quát', experience: '12 năm kinh nghiệm', image: '/images/doctors/doctor3.png', dept: 'Khoa Nội Tổng Quát', bio: 'Chuyên gia khám điều trị các bệnh lão khoa, xương khớp, tiểu đường và các bệnh chuyển hóa mãn tính ở người lớn tuổi.', schedule: 'Thứ 2 đến Thứ 7 (Hằng ngày)' }
]

const specialties = ['ALL', 'Tim mạch', 'Nội tổng quát', 'Ngoại khoa', 'Sản phụ khoa', 'Nhi khoa', 'Mắt']
const titles = ['ALL', 'GS.TS', 'PGS.TS', 'TS.BS', 'ThS.BS']

const filteredDoctors = computed(() => {
  return doctors.filter(d => {
    const matchesSearch = d.name.toLowerCase().includes(searchDocQuery.value.toLowerCase()) || d.bio.toLowerCase().includes(searchDocQuery.value.toLowerCase())
    const matchesSpecialty = selectedSpecialty.value === 'ALL' || d.specialty === selectedSpecialty.value
    const matchesTitle = selectedTitle.value === 'ALL' || d.title === selectedTitle.value
    return matchesSearch && matchesSpecialty && matchesTitle
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
              <option value="ALL">-- Tất cả chuyên khoa --</option>
              <option v-for="sp in specialties.slice(1)" :key="sp" :value="sp">{{ sp }}</option>
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
          
          <div class="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-between items-center text-sm font-semibold text-primary-700">
            <span class="text-xs text-gray-400">Lịch khám: {{ doc.schedule.split('(')[0] }}</span>
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

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h5 class="font-bold text-gray-700 mb-1"><i class="bi bi-award text-primary-600 mr-1.5"></i> Kinh nghiệm lâm sàng</h5>
              <p class="text-base text-gray-700">{{ selectedDoctor.experience }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <h5 class="font-bold text-gray-700 mb-1"><i class="bi bi-calendar-event text-primary-600 mr-1.5"></i> Lịch khám hành chính</h5>
              <p class="text-base text-gray-700 font-semibold text-primary-800">{{ selectedDoctor.schedule }}</p>
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
