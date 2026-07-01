<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentUser = ref(null)
const currentTime = ref('')

function checkUser() {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
  } else {
    currentUser.value = null
  }
}

function handleLogout() {
  localStorage.removeItem('currentUser')
  alert('Đã đăng xuất khỏi tài khoản nhân viên!')
  router.push('/')
}

let timer = null
function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' }) + ' - ' + now.toLocaleDateString('vi-VN')
}

onMounted(() => {
  checkUser()
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <header class="h-16 bg-gray-900 border-b border-gray-800 text-white flex items-center justify-between px-6 z-40 relative shadow-md">
    <!-- Brand / Title -->
    <div class="flex items-center gap-3">
      <img 
        src="https://i.ibb.co/4gXZ9qs4/z7939748517288-6fea6e7a88629fd0c0ef6ce9326fd25f.png" 
        alt="Logo" 
        class="w-9 h-9 object-contain bg-white rounded-lg p-0.5"
      />
      <div>
        <h1 class="text-sm font-bold tracking-wider text-purple-400">HỆ THỐNG NỘI BỘ BỆNH VIỆN</h1>
        <p class="text-[10px] text-gray-400 -mt-0.5">Dành cho Cán bộ & Nhân viên Y tế</p>
      </div>
    </div>

    <!-- Clock & Greetings -->
    <div class="hidden md:flex items-center gap-2 text-xs font-semibold text-gray-400">
      <i class="bi bi-clock"></i>
      <span>{{ currentTime }}</span>
    </div>

    <!-- User & Controls -->
    <div class="flex items-center gap-4">
      <div v-if="currentUser" class="flex items-center gap-2.5 text-right">
        <div>
          <h4 class="text-xs font-bold text-gray-200">{{ currentUser.name }}</h4>
          <span class="text-[10px] px-1.5 py-0.5 rounded bg-purple-900/50 text-purple-300 font-bold uppercase">
            {{ currentUser.role === 'director' ? 'Giám Đốc' : currentUser.role === 'doctor' ? 'Bác Sĩ' : currentUser.role === 'nurse' ? 'Điều Dưỡng' : currentUser.role === 'pharmacist' ? 'Dược Sĩ' : 'Thủ Kho' }}
          </span>
        </div>
        <div class="w-8 h-8 rounded-lg bg-purple-700 text-white flex items-center justify-center font-bold text-sm">
          {{ currentUser.name ? currentUser.name.charAt(0) : 'E' }}
        </div>
      </div>

      <!-- Action buttons -->
      <div class="h-8 w-px bg-gray-800"></div>

      <div class="flex items-center gap-2">
        <RouterLink 
          to="/" 
          class="text-xs font-semibold px-3 py-1.5 rounded-lg border border-gray-700 hover:bg-gray-800 text-gray-300 transition-colors flex items-center gap-1"
          title="Về trang chủ công cộng"
        >
          <i class="bi bi-house-door"></i>
          <span class="hidden sm:inline">Trang chủ</span>
        </RouterLink>

        <button 
          @click="handleLogout" 
          class="text-xs font-bold px-3 py-1.5 rounded-lg bg-red-600 hover:bg-red-750 text-white transition-colors flex items-center gap-1"
          title="Đăng xuất khỏi hệ thống"
        >
          <i class="bi bi-box-arrow-right"></i>
          <span>Đăng xuất</span>
        </button>
      </div>
    </div>
  </header>
</template>
