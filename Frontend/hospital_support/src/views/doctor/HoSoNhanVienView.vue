<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const currentUser = ref(null)
const isLoggedIn = ref(false)
const loading = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

// Bio form - chỉ dành cho bác sĩ
const bioForm = ref({
  chuyenKhoa: '',
  hocVi: '',
  kinhNghiem: '',
  moTa: ''
})
const editBio = ref(false)

const roleLabel = computed(() => {
  const map = {
    BENH_NHAN: 'Bệnh nhân',
    DIEU_DUONG: 'Điều dưỡng',
    BAC_SI: 'Bác sĩ',
    DUOC_SI: 'Dược sĩ',
    THU_KHO: 'Thủ kho'
  }
  return map[currentUser.value?.vaiTro || currentUser.value?.role?.toUpperCase()] || currentUser.value?.role || 'Nhân viên'
})

const roleIcon = computed(() => {
  const map = {
    BENH_NHAN: '🏥',
    DIEU_DUONG: '💉',
    BAC_SI: '🩺',
    DUOC_SI: '💊',
    THU_KHO: '📦'
  }
  const r = currentUser.value?.vaiTro || currentUser.value?.role?.toUpperCase() || ''
  return map[r] || '👤'
})

const roleColor = computed(() => {
  const r = currentUser.value?.vaiTro || currentUser.value?.role?.toUpperCase() || ''
  const map = {
    BENH_NHAN: { bg: 'from-blue-500 to-blue-700', badge: 'bg-blue-100 text-blue-700', accent: '#3b82f6' },
    DIEU_DUONG: { bg: 'from-pink-500 to-rose-600', badge: 'bg-pink-100 text-pink-700', accent: '#ec4899' },
    BAC_SI: { bg: 'from-emerald-500 to-teal-700', badge: 'bg-emerald-100 text-emerald-700', accent: '#10b981' },
    DUOC_SI: { bg: 'from-violet-500 to-purple-700', badge: 'bg-violet-100 text-violet-700', accent: '#8b5cf6' },
    THU_KHO: { bg: 'from-amber-500 to-orange-600', badge: 'bg-amber-100 text-amber-700', accent: '#f59e0b' },
  }
  return map[r] || { bg: 'from-slate-500 to-slate-700', badge: 'bg-slate-100 text-slate-700', accent: '#64748b' }
})

const isDoctor = computed(() => {
  const r = currentUser.value?.vaiTro || currentUser.value?.role?.toUpperCase() || ''
  return r === 'BAC_SI'
})

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  if (isNaN(d)) return dateStr
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const calcAge = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d)) return ''
  const age = new Date().getFullYear() - d.getFullYear()
  return `(${age} tuổi)`
}

onMounted(() => {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
    // Load saved bio from localStorage if doctor
    if (isDoctor.value) {
      const savedBio = localStorage.getItem(`bio_${currentUser.value.id}`)
      if (savedBio) {
        bioForm.value = JSON.parse(savedBio)
      }
    }
  }
})

async function saveBio() {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    // Save bio to localStorage (backend bio endpoint can be added later)
    localStorage.setItem(`bio_${currentUser.value.id}`, JSON.stringify(bioForm.value))
    successMsg.value = 'Cập nhật hồ sơ bác sĩ thành công!'
    editBio.value = false
    setTimeout(() => successMsg.value = '', 3000)
  } catch (e) {
    errorMsg.value = 'Có lỗi xảy ra, vui lòng thử lại!'
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  localStorage.removeItem('currentUser')
  window.location.href = '/'
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-100 via-gray-50 to-slate-200 pb-16">

    <!-- ===== NOT LOGGED IN ===== -->
    <div v-if="!isLoggedIn" class="flex items-center justify-center min-h-screen">
      <div class="bg-white rounded-3xl shadow-xl p-12 text-center max-w-md mx-4">
        <div class="text-6xl mb-4">🔒</div>
        <h2 class="text-2xl font-bold text-gray-800 mb-2">Yêu Cầu Đăng Nhập</h2>
        <p class="text-gray-500 mb-6">Bạn cần đăng nhập để xem thẻ nhân viên.</p>
        <RouterLink to="/dang-nhap"
          class="inline-block px-8 py-3 bg-emerald-600 text-white rounded-2xl font-semibold hover:bg-emerald-700 transition-all">
          Đến Trang Đăng Nhập
        </RouterLink>
      </div>
    </div>

    <!-- ===== LOGGED IN ===== -->
    <div v-else class="max-w-5xl mx-auto px-4 py-10">

      <!-- Success / Error Toast -->
      <Transition name="fade">
        <div v-if="successMsg"
          class="fixed top-6 right-6 z-50 bg-emerald-500 text-white px-6 py-3 rounded-2xl shadow-xl font-semibold flex items-center gap-2">
          <i class="bi bi-check-circle-fill"></i> {{ successMsg }}
        </div>
      </Transition>
      <Transition name="fade">
        <div v-if="errorMsg"
          class="fixed top-6 right-6 z-50 bg-red-500 text-white px-6 py-3 rounded-2xl shadow-xl font-semibold flex items-center gap-2">
          <i class="bi bi-x-circle-fill"></i> {{ errorMsg }}
        </div>
      </Transition>

      <!-- ===== EMPLOYEE CARD ===== -->
      <div class="bg-white rounded-[2rem] shadow-2xl overflow-hidden mb-8">
        <!-- Card Header / Banner -->
        <div :class="`bg-gradient-to-r ${roleColor.bg} relative h-48`">
          <!-- Decorative circles -->
          <div class="absolute top-[-40px] right-[-40px] w-56 h-56 rounded-full bg-white/10"></div>
          <div class="absolute bottom-[-30px] left-[-20px] w-40 h-40 rounded-full bg-white/10"></div>
          <div class="absolute top-4 left-6 flex items-center gap-3">
            <span class="bg-white/20 backdrop-blur text-white text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wider">
              Bệnh Viện Đa Khoa
            </span>
          </div>
          <!-- Role badge top right -->
          <div class="absolute top-4 right-6 bg-white/20 backdrop-blur text-white text-sm font-semibold px-4 py-1.5 rounded-full flex items-center gap-2">
            <span>{{ roleIcon }}</span>
            <span>{{ roleLabel }}</span>
          </div>
        </div>

        <!-- Avatar + Name overlapping the banner -->
        <div class="px-8 pb-8">
          <div class="flex flex-col md:flex-row items-start md:items-end gap-6 -mt-16 mb-6">
            <!-- Avatar -->
            <div class="relative shrink-0">
              <div :class="`w-32 h-32 rounded-3xl bg-gradient-to-br ${roleColor.bg} border-4 border-white shadow-xl flex items-center justify-center text-5xl`">
                {{ roleIcon }}
              </div>
              <div class="absolute -bottom-1 -right-1 w-7 h-7 bg-emerald-400 border-2 border-white rounded-full flex items-center justify-center">
                <i class="bi bi-check2 text-white text-xs font-bold"></i>
              </div>
            </div>

            <!-- Name + Info -->
            <div class="flex-1 mt-16 md:mt-0 pb-1">
              <h1 class="text-3xl font-extrabold text-gray-900 leading-tight">
                {{ currentUser.fullName || currentUser.hoTen || 'Chưa cập nhật' }}
              </h1>
              <div class="flex flex-wrap items-center gap-3 mt-2">
                <span :class="`${roleColor.badge} text-sm font-semibold px-3 py-1 rounded-full`">
                  {{ roleIcon }} {{ roleLabel }}
                </span>
                <span v-if="currentUser.ngayLam || currentUser.startDate" class="text-sm text-gray-500 flex items-center gap-1">
                  <i class="bi bi-calendar2-check"></i>
                  Vào làm: {{ formatDate(currentUser.ngayLam || currentUser.startDate) }}
                </span>
              </div>
            </div>

            <!-- Logout button -->
            <button @click="handleLogout"
              class="shrink-0 flex items-center gap-2 px-4 py-2 text-sm text-red-500 border border-red-200 rounded-xl hover:bg-red-50 transition-all font-semibold">
              <i class="bi bi-box-arrow-right"></i> Đăng xuất
            </button>
          </div>

          <!-- ===== INFO GRID ===== -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">

            <!-- Phone -->
            <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <div class="w-11 h-11 rounded-xl bg-blue-100 flex items-center justify-center shrink-0">
                <i class="bi bi-telephone-fill text-blue-600 text-lg"></i>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-medium uppercase tracking-wide">Số điện thoại</p>
                <p class="text-base font-bold text-gray-800">{{ currentUser.phone || currentUser.soDienThoai || '—' }}</p>
              </div>
            </div>

            <!-- Email -->
            <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <div class="w-11 h-11 rounded-xl bg-violet-100 flex items-center justify-center shrink-0">
                <i class="bi bi-envelope-fill text-violet-600 text-lg"></i>
              </div>
              <div class="min-w-0">
                <p class="text-xs text-gray-400 font-medium uppercase tracking-wide">Email</p>
                <p class="text-base font-bold text-gray-800 truncate">{{ currentUser.email || '—' }}</p>
              </div>
            </div>

            <!-- Gender -->
            <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <div class="w-11 h-11 rounded-xl bg-pink-100 flex items-center justify-center shrink-0">
                <i class="bi bi-gender-ambiguous text-pink-600 text-lg"></i>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-medium uppercase tracking-wide">Giới tính</p>
                <p class="text-base font-bold text-gray-800">{{ currentUser.gender || currentUser.gioiTinh || '—' }}</p>
              </div>
            </div>

            <!-- Date of birth -->
            <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100">
              <div class="w-11 h-11 rounded-xl bg-amber-100 flex items-center justify-center shrink-0">
                <i class="bi bi-cake2-fill text-amber-600 text-lg"></i>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-medium uppercase tracking-wide">Ngày sinh</p>
                <p class="text-base font-bold text-gray-800">
                  {{ formatDate(currentUser.ngaySinh) }}
                  <span class="text-sm text-gray-400 font-normal ml-1">{{ calcAge(currentUser.ngaySinh) }}</span>
                </p>
              </div>
            </div>

            <!-- Address -->
            <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100 md:col-span-2">
              <div class="w-11 h-11 rounded-xl bg-emerald-100 flex items-center justify-center shrink-0">
                <i class="bi bi-geo-alt-fill text-emerald-600 text-lg"></i>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-medium uppercase tracking-wide">Địa chỉ</p>
                <p class="text-base font-bold text-gray-800">{{ currentUser.address || currentUser.diaChi || '—' }}</p>
              </div>
            </div>

          </div>

          <!-- ===== EMPLOYEE ID FOOTER ===== -->
          <div class="mt-6 flex flex-col md:flex-row items-center justify-between gap-4 p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-2xl border border-gray-200">
            <div class="flex items-center gap-3">
              <i class="bi bi-person-badge-fill text-2xl text-gray-500"></i>
              <div>
                <p class="text-xs text-gray-400 font-medium">Mã nhân viên</p>
                <p class="font-mono font-bold text-gray-700 text-lg">#{{ String(currentUser.id || '000').padStart(5, '0') }}</p>
              </div>
            </div>
            <div class="text-center">
              <p class="text-xs text-gray-400">Trạng thái tài khoản</p>
              <span class="inline-flex items-center gap-2 bg-emerald-100 text-emerald-700 text-sm font-bold px-4 py-1.5 rounded-full mt-1">
                <span class="w-2 h-2 bg-emerald-500 rounded-full animate-pulse"></span>
                Đang hoạt động
              </span>
            </div>
            <div class="text-center">
              <p class="text-xs text-gray-400">Ngày tạo tài khoản</p>
              <p class="font-semibold text-gray-700 text-sm">{{ formatDate(currentUser.ngayTao || currentUser.createdAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ===== BIO SECTION - CHỈ BÁC SĨ ===== -->
      <div v-if="isDoctor" class="bg-white rounded-[2rem] shadow-2xl overflow-hidden">
        <div class="p-8">
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center gap-3">
              <div class="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center">
                <i class="bi bi-file-person-fill text-emerald-600 text-2xl"></i>
              </div>
              <div>
                <h2 class="text-2xl font-extrabold text-gray-900">Hồ Sơ Bác Sĩ</h2>
                <p class="text-sm text-gray-400">Thông tin chuyên môn & kinh nghiệm</p>
              </div>
            </div>
            <button v-if="!editBio" @click="editBio = true"
              class="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-all font-semibold text-sm shadow">
              <i class="bi bi-pencil-fill"></i> Chỉnh sửa Bio
            </button>
            <div v-else class="flex gap-2">
              <button @click="editBio = false"
                class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-all font-semibold text-sm">
                Hủy
              </button>
              <button @click="saveBio" :disabled="loading"
                class="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-all font-semibold text-sm shadow disabled:opacity-60">
                <i class="bi bi-check-lg"></i> {{ loading ? 'Đang lưu...' : 'Lưu Bio' }}
              </button>
            </div>
          </div>

          <!-- View Mode -->
          <div v-if="!editBio" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="p-5 bg-emerald-50 rounded-2xl border border-emerald-100">
              <p class="text-xs text-emerald-500 font-semibold uppercase tracking-wider mb-1">
                <i class="bi bi-mortarboard-fill mr-1"></i> Học vị / Chứng chỉ
              </p>
              <p class="text-base font-bold text-gray-800">{{ bioForm.hocVi || '—' }}</p>
            </div>
            <div class="p-5 bg-teal-50 rounded-2xl border border-teal-100">
              <p class="text-xs text-teal-500 font-semibold uppercase tracking-wider mb-1">
                <i class="bi bi-heart-pulse-fill mr-1"></i> Chuyên khoa
              </p>
              <p class="text-base font-bold text-gray-800">{{ bioForm.chuyenKhoa || '—' }}</p>
            </div>
            <div class="p-5 bg-cyan-50 rounded-2xl border border-cyan-100">
              <p class="text-xs text-cyan-500 font-semibold uppercase tracking-wider mb-1">
                <i class="bi bi-briefcase-fill mr-1"></i> Kinh nghiệm làm việc
              </p>
              <p class="text-base font-bold text-gray-800">{{ bioForm.kinhNghiem || '—' }}</p>
            </div>
            <div class="p-5 bg-slate-50 rounded-2xl border border-slate-100 md:row-span-1">
              <p class="text-xs text-slate-500 font-semibold uppercase tracking-wider mb-2">
                <i class="bi bi-card-text mr-1"></i> Giới thiệu bản thân
              </p>
              <p class="text-sm text-gray-700 leading-relaxed">{{ bioForm.moTa || 'Chưa có thông tin giới thiệu.' }}</p>
            </div>
          </div>

          <!-- Edit Mode -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1.5">
                <i class="bi bi-mortarboard-fill text-emerald-500 mr-1"></i> Học vị / Chứng chỉ
              </label>
              <input v-model="bioForm.hocVi" type="text" placeholder="VD: PGS.TS, Thạc sĩ Y khoa..."
                class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-300 bg-gray-50 text-sm font-medium" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1.5">
                <i class="bi bi-heart-pulse-fill text-teal-500 mr-1"></i> Chuyên khoa
              </label>
              <input v-model="bioForm.chuyenKhoa" type="text" placeholder="VD: Tim mạch, Nội tổng quát..."
                class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-300 bg-gray-50 text-sm font-medium" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1.5">
                <i class="bi bi-briefcase-fill text-cyan-500 mr-1"></i> Kinh nghiệm làm việc
              </label>
              <input v-model="bioForm.kinhNghiem" type="text" placeholder="VD: 10 năm kinh nghiệm..."
                class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-300 bg-gray-50 text-sm font-medium" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1.5">
                <i class="bi bi-card-text text-slate-500 mr-1"></i> Giới thiệu bản thân
              </label>
              <textarea v-model="bioForm.moTa" rows="4" placeholder="Viết đôi dòng giới thiệu về bản thân..."
                class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-300 bg-gray-50 text-sm font-medium resize-none"></textarea>
            </div>
          </div>

        </div>
      </div>

      <!-- ===== READ-ONLY NOTICE for non-doctors ===== -->
      <div v-if="!isDoctor" class="mt-4 flex items-center gap-3 p-4 bg-amber-50 border border-amber-200 rounded-2xl text-amber-700">
        <i class="bi bi-info-circle-fill text-xl shrink-0"></i>
        <p class="text-sm font-medium">
          Thông tin nhân viên do bộ phận nhân sự quản lý. Nếu cần chỉnh sửa, vui lòng liên hệ quản trị viên.
        </p>
      </div>

    </div>
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
