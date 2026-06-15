<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)

const navLinks = [
  { name: 'Trang chủ', to: '/' },
  { name: 'Giới thiệu', to: '/gioi-thieu' },
  { name: 'Tin tức', to: '/tin-tuc' },
  { name: 'Liên hệ', to: '/lien-he' },
  { name: 'Hướng dẫn', to: '/huong-dan' },
]

function handleScroll() {
  isScrolled.value = window.scrollY > 20
}

function toggleMobileMenu() {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

function closeMobileMenu() {
  isMobileMenuOpen.value = false
}

onMounted(() => {
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
          <div class="w-11 h-11 bg-gradient-to-br from-primary-600 to-primary-800 rounded-xl flex items-center justify-center shadow-md group-hover:shadow-lg transition-shadow duration-300">
            <i class="bi bi-hospital text-white text-xl"></i>
          </div>
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
          <div class="pt-3 space-y-2 border-t border-gray-100 mt-3">
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
          </div>
        </div>
      </div>
    </Transition>
  </header>

  <!-- Spacer to prevent content from hiding under fixed header -->
  <div class="h-[68px] sm:h-[72px]"></div>
</template>
