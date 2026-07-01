<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)
const isDropdownOpen = ref(false)

const isLoggedIn = ref(false)
const currentUser = ref(null)

function toggleDropdown() {
  isDropdownOpen.value = !isDropdownOpen.value
}

function closeDropdown() {
  isDropdownOpen.value = false
}

const navLinks = [
  { name: 'Trang chủ', to: '/' },
  { name: 'Giới thiệu', to: '/gioi-thieu' },
  { name: 'Tin tức', to: '/tin-tuc' },
  { name: 'Liên hệ', to: '/lien-he' },
  { name: 'Hướng dẫn', to: '/huong-dan' },
]

function checkUser() {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
  } else {
    currentUser.value = null
    isLoggedIn.value = false
  }
}

watch(() => route.path, () => {
  checkUser()
  closeDropdown()
})

function handleScroll() {
  isScrolled.value = window.scrollY > 20
}

function toggleMobileMenu() {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

function closeMobileMenu() {
  isMobileMenuOpen.value = false
}

function handleLogout() {
  localStorage.removeItem('currentUser')
  checkUser()
  alert('Đã đăng xuất tài khoản!')
  router.push('/')
  closeMobileMenu()
}

onMounted(() => {
  checkUser()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <header
    id="app-header"
    class="fixed top-0 left-0 right-0 z-50 transition-all duration-300"
    :class="isScrolled
      ? 'bg-white/95 backdrop-blur-md shadow-lg py-2'
      : 'bg-white py-3'"
  >
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between">
        <!-- Logo -->
        <RouterLink to="/" class="flex items-center gap-3 group" @click="closeMobileMenu">
          <img 
            src="https://i.ibb.co/4gXZ9qs4/z7939748517288-6fea6e7a88629fd0c0ef6ce9326fd25f.png" 
            alt="Logo" 
            class="w-11 h-11 object-contain rounded-xl shadow-md group-hover:shadow-lg transition-shadow duration-300 bg-white p-0.5" 
          />
          <div class="hidden sm:block">
            <h1 class="text-lg font-bold text-primary-800 leading-tight">Bệnh Viện Đa Khoa</h1>
            <p class="text-xs text-gray-400 -mt-0.5">Hospital Support System</p>
          </div>
        </RouterLink>

        <!-- Desktop Navigation -->
        <nav class="hidden lg:flex items-center gap-1">
          <RouterLink
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            class="px-4 py-2 rounded-lg text-[15px] font-medium text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-all duration-200 relative"
            active-class="!text-primary-700 !bg-primary-50"
            @click="closeMobileMenu"
          >
            {{ link.name }}
          </RouterLink>
          

        </nav>

        <!-- CTA Buttons -->
        <div class="hidden lg:flex items-center gap-3">
          <template v-if="!isLoggedIn">
            <RouterLink
              to="/dat-lich"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg"
            >
              <i class="bi bi-calendar-check"></i>
              Đặt lịch khám
            </RouterLink>
            <RouterLink
              to="/dang-nhap"
              class="btn-outline !py-2.5 !px-5 !text-sm !rounded-lg"
            >
              <i class="bi bi-person"></i>
              Đăng nhập
            </RouterLink>
          </template>
          
          <template v-else>
            <!-- Dashboard link based on role -->
            <RouterLink
              v-if="currentUser.role === 'patient'"
              to="/dat-lich"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg bg-emerald-600 hover:bg-emerald-700"
            >
              <i class="bi bi-calendar2-check"></i> Đặt khám ngay
            </RouterLink>
            
            <RouterLink
              v-else-if="currentUser.role === 'nurse'"
              to="/dieu-duong"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg bg-indigo-600 hover:bg-indigo-700"
            >
              <i class="bi bi-speedometer2"></i> Đội ngũ Điều dưỡng
            </RouterLink>
            
            <RouterLink
              v-else-if="currentUser.role === 'doctor'"
              to="/bac-si-dashboard"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg bg-sky-600 hover:bg-sky-700"
            >
              <i class="bi bi-heart-pulse"></i> Phòng Khám Bác sĩ
            </RouterLink>

            <RouterLink
              v-else-if="currentUser.role === 'pharmacist'"
              to="/duoc-si"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg bg-teal-600 hover:bg-teal-700"
            >
              <i class="bi bi-capsule"></i> Quầy Dược sĩ
            </RouterLink>

            <RouterLink
              v-else-if="currentUser.role === 'warehouse'"
              to="/kho-thuoc"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg bg-amber-600 hover:bg-amber-700"
            >
              <i class="bi bi-box-seam"></i> Kho Dược phẩm
            </RouterLink>

            <RouterLink
              v-else-if="currentUser.role === 'director'"
              to="/giam-doc"
              class="btn-primary !py-2.5 !px-5 !text-sm !rounded-lg bg-purple-600 hover:bg-purple-700"
            >
              <i class="bi bi-buildings"></i> Giám đốc Bệnh viện
            </RouterLink>

            <!-- User profile dropdown -->
            <div class="relative">
              <!-- Overlay to close dropdown when clicking outside -->
              <div 
                v-if="isDropdownOpen" 
                class="fixed inset-0 z-40 bg-transparent" 
                @click="closeDropdown"
              ></div>

              <div class="relative z-50 flex items-center gap-2 border-l pl-3 border-gray-200">
                <button 
                  @click="toggleDropdown"
                  class="flex items-center gap-1.5 py-1 px-2.5 rounded-xl hover:bg-gray-50 transition-colors text-gray-700 focus:outline-none"
                >
                  <div class="w-8 h-8 rounded-full bg-primary-100 text-primary-700 flex items-center justify-center font-bold text-sm">
                    {{ currentUser.fullName ? currentUser.fullName.charAt(0).toUpperCase() : 'U' }}
                  </div>
                  <span class="text-sm font-semibold max-w-[120px] truncate" :title="currentUser.fullName">
                    {{ currentUser.fullName || 'Nhân viên y tế' }}
                  </span>
                  <i class="bi bi-chevron-down text-xs text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': isDropdownOpen }"></i>
                </button>
              </div>

              <!-- Dropdown Menu -->
              <div 
                v-if="isDropdownOpen"
                class="absolute right-0 mt-2 w-56 bg-white border border-gray-100 rounded-2xl shadow-xl py-2 z-50 animate-fade-in origin-top-right"
              >
                <div class="px-4 py-2 border-b border-gray-50">
                  <p class="text-xs text-gray-400 font-medium">Đăng nhập với vai trò</p>
                  <p class="text-sm font-bold text-gray-800 truncate">
                    {{ currentUser.role === 'patient' ? 'Bệnh nhân' : 
                       currentUser.role === 'nurse' ? 'Điều dưỡng' : 
                       currentUser.role === 'doctor' ? 'Bác sĩ' : 
                       currentUser.role === 'pharmacist' ? 'Dược sĩ' : 
                       currentUser.role === 'warehouse' ? 'Quản kho' : 
                       currentUser.role === 'director' ? 'Giám đốc' : 'Nhân viên' }}
                  </p>
                </div>

                <!-- Patient-specific list -->
                <div v-if="currentUser.role === 'patient'" class="py-1">
                  <RouterLink
                    to="/ho-so"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-person-bounding-box text-gray-450"></i>
                    Hồ sơ của tôi
                  </RouterLink>
                  <RouterLink
                    to="/lich-su-kham"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-clock-history text-gray-450"></i>
                    Lịch sử khám
                  </RouterLink>
                  <RouterLink
                    to="/don-thuoc"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-capsule text-gray-450"></i>
                    Đơn thuốc của tôi
                  </RouterLink>
                </div>

                <!-- General Dashboard Links for Staff -->
                <div v-else class="py-1">
                  <RouterLink
                    v-if="currentUser.role === 'nurse'"
                    to="/dieu-duong"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-speedometer2 text-gray-450"></i>
                    Bảng điều khiển
                  </RouterLink>
                  <RouterLink
                    v-else-if="currentUser.role === 'doctor'"
                    to="/bac-si-dashboard"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-speedometer2 text-gray-450"></i>
                    Bảng điều khiển
                  </RouterLink>
                  <RouterLink
                    v-else-if="currentUser.role === 'pharmacist'"
                    to="/duoc-si"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-speedometer2 text-gray-450"></i>
                    Bảng điều khiển
                  </RouterLink>
                  <RouterLink
                    v-else-if="currentUser.role === 'warehouse'"
                    to="/kho-thuoc"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-speedometer2 text-gray-450"></i>
                    Bảng điều khiển
                  </RouterLink>
                  <RouterLink
                    v-else-if="currentUser.role === 'director'"
                    to="/giam-doc"
                    class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-colors"
                    @click="closeDropdown"
                  >
                    <i class="bi bi-speedometer2 text-gray-450"></i>
                    Bảng điều khiển
                  </RouterLink>
                </div>

                <div class="border-t border-gray-50 mt-1 pt-1">
                  <button 
                    @click="handleLogout" 
                    class="w-full flex items-center gap-2.5 px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors text-left"
                  >
                    <i class="bi bi-box-arrow-right text-red-450"></i>
                    Đăng xuất
                  </button>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- Mobile Menu Toggle -->
        <button
          class="lg:hidden p-2 rounded-lg text-gray-600 hover:bg-primary-50 hover:text-primary-700 transition-colors"
          @click="toggleMobileMenu"
          :aria-label="isMobileMenuOpen ? 'Đóng menu' : 'Mở menu'"
        >
          <i class="bi text-2xl" :class="isMobileMenuOpen ? 'bi-x-lg' : 'bi-list'"></i>
        </button>
      </div>
    </div>

    <!-- Mobile Menu -->
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 -translate-y-2 max-h-0"
      enter-to-class="opacity-100 translate-y-0 max-h-[500px]"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 translate-y-0 max-h-[500px]"
      leave-to-class="opacity-0 -translate-y-2 max-h-0"
    >
      <div v-if="isMobileMenuOpen" class="lg:hidden overflow-hidden border-t border-gray-100 bg-white">
        <div class="px-4 py-4 space-y-1">
          <RouterLink
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            class="block px-4 py-3 rounded-lg text-base font-medium text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-all"
            active-class="!text-primary-700 !bg-primary-50"
            @click="closeMobileMenu"
          >
            {{ link.name }}
          </RouterLink>
          
          <template v-if="isLoggedIn && currentUser?.role === 'patient'">
            <RouterLink
              to="/don-thuoc"
              class="block px-4 py-3 rounded-lg text-base font-medium text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-all"
              active-class="!text-primary-700 !bg-primary-50"
              @click="closeMobileMenu"
            >
              Đơn thuốc
            </RouterLink>

            <RouterLink
              to="/lich-su-kham"
              class="block px-4 py-3 rounded-lg text-base font-medium text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-all"
              active-class="!text-primary-700 !bg-primary-50"
              @click="closeMobileMenu"
            >
              Lịch sử khám
            </RouterLink>
            <RouterLink
              to="/ho-so"
              class="block px-4 py-3 rounded-lg text-base font-medium text-gray-600 hover:text-primary-700 hover:bg-primary-50 transition-all"
              active-class="!text-primary-700 !bg-primary-50"
              @click="closeMobileMenu"
            >
              Hồ sơ của tôi
            </RouterLink>
          </template>

          <div class="pt-3 space-y-2 border-t border-gray-100 mt-3">
            <template v-if="!isLoggedIn">
              <RouterLink
                to="/dat-lich"
                class="btn-primary w-full !text-sm"
                @click="closeMobileMenu"
              >
                <i class="bi bi-calendar-check"></i>
                Đặt lịch khám
              </RouterLink>
              <RouterLink
                to="/dang-nhap"
                class="btn-outline w-full !text-sm"
                @click="closeMobileMenu"
              >
                <i class="bi bi-person"></i>
                Đăng nhập
              </RouterLink>
            </template>
            
            <template v-else>
              <!-- Dashboards -->
              <RouterLink
                v-if="currentUser.role === 'patient'"
                to="/dat-lich"
                class="btn-primary w-full !text-sm bg-emerald-600"
                @click="closeMobileMenu"
              >
                Đặt khám ngay
              </RouterLink>
              
              <RouterLink
                v-else-if="currentUser.role === 'nurse'"
                to="/dieu-duong"
                class="btn-primary w-full !text-sm bg-indigo-600"
                @click="closeMobileMenu"
              >
                Đội ngũ Điều dưỡng
              </RouterLink>
              
              <RouterLink
                v-else-if="currentUser.role === 'doctor'"
                to="/bac-si-dashboard"
                class="btn-primary w-full !text-sm bg-sky-600"
                @click="closeMobileMenu"
              >
                Phòng Khám Bác sĩ
              </RouterLink>

              <RouterLink
                v-else-if="currentUser.role === 'pharmacist'"
                to="/duoc-si"
                class="btn-primary w-full !text-sm bg-teal-600"
                @click="closeMobileMenu"
              >
                Quầy Dược sĩ
              </RouterLink>

              <RouterLink
                v-else-if="currentUser.role === 'warehouse'"
                to="/kho-thuoc"
                class="btn-primary w-full !text-sm bg-amber-600"
                @click="closeMobileMenu"
              >
                Kho Dược phẩm
              </RouterLink>

              <RouterLink
                v-else-if="currentUser.role === 'director'"
                to="/giam-doc"
                class="btn-primary w-full !text-sm bg-purple-600"
                @click="closeMobileMenu"
              >
                Giám đốc Bệnh viện
              </RouterLink>

              <div class="flex items-center justify-between px-4 py-2 border rounded-xl bg-gray-50">
                <span class="text-sm font-semibold text-gray-700 truncate">
                  {{ currentUser.fullName || 'Nhân viên y tế' }}
                </span>
                <button 
                  @click="handleLogout" 
                  class="text-red-500 font-bold text-sm hover:underline"
                >
                  Đăng xuất
                </button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </Transition>
  </header>

  <!-- Spacer to prevent content from hiding under fixed header -->
  <div class="h-[68px] sm:h-[72px]"></div>
</template>
