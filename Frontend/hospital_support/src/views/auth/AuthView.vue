<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  activeTab: {
    type: String,
    default: 'login'
  }
})

const router = useRouter()
const currentTab = ref(props.activeTab)

watch(() => props.activeTab, (newVal) => {
  currentTab.value = newVal
})

// Form state
const loginForm = ref({
  username: '',
  password: '',
  phone: '',
  otp: '',
  usePhone: false
})

const registerForm = ref({
  fullName: '',
  gender: 'Nam',
  phone: '',
  email: '',
  address: '',
  password: '',
  confirmPassword: ''
})

// UI state
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const otpSent = ref(false)
const otpCountdown = ref(0)
let timer = null

function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function toggleConfirmPasswordVisibility() {
  showConfirmPassword.value = !showConfirmPassword.value
}

function switchTab(tab) {
  currentTab.value = tab
  router.push(tab === 'login' ? '/dang-nhap' : '/dang-ky')
}

function sendOTP() {
  if (!loginForm.value.phone) {
    alert('Vui lòng nhập số điện thoại để nhận mã OTP!')
    return
  }
  otpSent.value = true
  alert('Hệ thống đã gửi mã OTP (mặc định: 123456) về số điện thoại của bạn.')
  otpCountdown.value = 60
  timer = setInterval(() => {
    if (otpCountdown.value > 0) {
      otpCountdown.value--
    } else {
      clearInterval(timer)
    }
  }, 1000)
}

function handleLogin() {
  if (loginForm.value.usePhone) {
    if (!loginForm.value.phone || !loginForm.value.otp) {
      alert('Vui lòng điền đầy đủ số điện thoại và mã OTP!')
      return
    }
    if (loginForm.value.otp !== '123456') {
      alert('Mã OTP không đúng! (Mã mặc định là: 123456)')
      return
    }
    
    // Success login by phone
    const user = {
      fullName: 'Bác Nguyễn Văn Hùng',
      gender: 'Nam',
      phone: loginForm.value.phone,
      email: 'hung.nguyen@gmail.com',
      address: '123 Đường Láng, Đống Đa, Hà Nội',
      role: 'patient'
    }
    localStorage.setItem('currentUser', JSON.stringify(user))
    alert('Đăng nhập thành công bằng số điện thoại!')
    router.push('/')
  } else {
    if (!loginForm.value.username || !loginForm.value.password) {
      alert('Vui lòng nhập tài khoản và mật khẩu!')
      return
    }
    
    // Simple mock database check
    let registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
    let matchedUser = registeredUsers.find(u => u.phone === loginForm.value.username || u.email === loginForm.value.username)
    
    if (matchedUser) {
      if (matchedUser.password === loginForm.value.password) {
        localStorage.setItem('currentUser', JSON.stringify(matchedUser))
        alert(`Chào mừng ${matchedUser.fullName} quay trở lại!`)
        router.push('/')
        return
      } else {
        alert('Mật khẩu không chính xác!')
        return
      }
    }
    
    // Default accounts if no match in storage
    const username = loginForm.value.username.toLowerCase()
    const password = loginForm.value.password

    if (password === '123456') {
      let matchedUser = null
      if (username === 'patient') {
        matchedUser = {
          fullName: 'Nguyễn Văn A',
          gender: 'Nam',
          phone: '0901234567',
          email: 'patient@hospital.com',
          address: 'Quận 1, TP. Hồ Chí Minh',
          role: 'patient'
        }
      } else if (username === 'nurse') {
        matchedUser = {
          fullName: 'Điều dưỡng Nguyễn Thị Mai',
          gender: 'Nữ',
          phone: '0907654321',
          email: 'nurse.mai@hospital.com',
          role: 'nurse'
        }
      } else if (username === 'doctor') {
        matchedUser = {
          fullName: 'PGS.TS Nguyễn Văn An',
          gender: 'Nam',
          phone: '0909998888',
          email: 'doctor.an@hospital.com',
          role: 'doctor',
          specialty: 'Tim mạch'
        }
      } else if (username === 'pharmacist') {
        matchedUser = {
          fullName: 'Dược sĩ Trần Thanh Thủy',
          gender: 'Nữ',
          phone: '0908887777',
          email: 'pharmacy.thuy@hospital.com',
          role: 'pharmacist'
        }
      } else if (username === 'warehouse') {
        matchedUser = {
          fullName: 'Thủ kho Phạm Minh Tuấn',
          gender: 'Nam',
          phone: '0907776666',
          email: 'warehouse.tuan@hospital.com',
          role: 'warehouse'
        }
      } else if (username === 'director') {
        matchedUser = {
          fullName: 'Giám đốc Lê Hoàng Minh',
          gender: 'Nam',
          phone: '0906665555',
          email: 'director.minh@hospital.com',
          role: 'director'
        }
      }

      if (matchedUser) {
        localStorage.setItem('currentUser', JSON.stringify(matchedUser))
        alert(`Đăng nhập thành công với tài khoản ${matchedUser.fullName} (${matchedUser.role})!`)
        // Redirect accordingly
        if (matchedUser.role === 'patient') router.push('/dat-lich')
        else if (matchedUser.role === 'nurse') router.push('/dieu-duong')
        else if (matchedUser.role === 'doctor') router.push('/bac-si-dashboard')
        else if (matchedUser.role === 'pharmacist') router.push('/duoc-si')
        else if (matchedUser.role === 'warehouse') router.push('/kho-thuoc')
        else if (matchedUser.role === 'director') router.push('/giam-doc')
        return
      }
    }
    
    alert('Tài khoản hoặc mật khẩu không chính xác! Hãy nhập tài khoản mẫu: patient, nurse, doctor, pharmacist, warehouse, hoặc director với mật khẩu là 123456.')
  }
}

function handleRegister() {
  const f = registerForm.value
  if (!f.fullName || !f.phone || !f.password || !f.confirmPassword) {
    alert('Vui lòng nhập đầy đủ các trường bắt buộc (*)!')
    return
  }
  if (f.password !== f.confirmPassword) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  
  // Register account
  const newUser = {
    fullName: f.fullName,
    gender: f.gender,
    phone: f.phone,
    email: f.email || `${f.phone}@gmail.com`,
    address: f.address || 'Chưa cập nhật',
    password: f.password,
    role: 'patient'
  }
  
  let registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
  // Check if exists
  if (registeredUsers.some(u => u.phone === f.phone)) {
    alert('Số điện thoại này đã được đăng ký tài khoản!')
    return
  }
  
  registeredUsers.push(newUser)
  localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers))
  alert('Đăng ký tài khoản thành công! Bây giờ bạn có thể đăng nhập.')
  switchTab('login')
}
</script>

<template>
  <div class="min-h-[calc(100vh-140px)] bg-gradient-to-br from-primary-50 via-white to-primary-100 flex items-center justify-center px-4 py-12">
    <div class="max-w-2xl w-full bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden animate-fade-in-up">
      <!-- Tabs header -->
      <div class="flex border-b border-gray-100 bg-gray-50/50">
        <button
          @click="switchTab('login')"
          class="flex-1 py-5 text-xl font-bold text-center border-b-4 transition-all duration-300 flex items-center justify-center gap-2"
          :class="currentTab === 'login'
            ? 'border-primary-700 text-primary-700 bg-white'
            : 'border-transparent text-gray-400 hover:text-gray-600'"
        >
          <i class="bi bi-box-arrow-in-right text-2xl"></i>
          Đăng Nhập
        </button>
        <button
          @click="switchTab('register')"
          class="flex-1 py-5 text-xl font-bold text-center border-b-4 transition-all duration-300 flex items-center justify-center gap-2"
          :class="currentTab === 'register'
            ? 'border-primary-700 text-primary-700 bg-white'
            : 'border-transparent text-gray-400 hover:text-gray-600'"
        >
          <i class="bi bi-person-plus text-2xl"></i>
          Đăng Ký
        </button>
      </div>

      <div class="p-8 md:p-10">
        <!-- ==================== LOGIN FORM ==================== -->
        <div v-if="currentTab === 'login'" class="animate-fade-in space-y-6">
          <div class="text-center mb-6">
            <h2 class="text-3xl font-extrabold text-gray-800">Chào Mừng Trở Lại</h2>
            <p class="text-lg text-gray-500 mt-2">Dành cho bệnh nhân và nhân viên y tế</p>
          </div>

          <!-- Switch Login Method (User / Phone OTP) -->
          <div class="flex justify-center gap-4 p-1 bg-gray-100 rounded-xl mb-4">
            <button
              @click="loginForm.usePhone = false"
              class="flex-1 py-2 rounded-lg font-bold text-base transition-all"
              :class="!loginForm.usePhone ? 'bg-white text-primary-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
            >
              Mật khẩu
            </button>
            <button
              @click="loginForm.usePhone = true"
              class="flex-1 py-2 rounded-lg font-bold text-base transition-all"
              :class="loginForm.usePhone ? 'bg-white text-primary-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
            >
              Số điện thoại (Mã OTP)
            </button>
          </div>

          <!-- LOGIN WITH USERNAME/PASSWORD -->
          <form v-if="!loginForm.usePhone" @submit.prevent="handleLogin" class="space-y-6">
            <div>
              <label for="login-username" class="block text-lg font-semibold text-gray-700 mb-2">
                <i class="bi bi-person text-primary-700 mr-1.5"></i>
                Số điện thoại hoặc Email
              </label>
              <input
                id="login-username"
                v-model="loginForm.username"
                type="text"
                required
                placeholder="Nhập số điện thoại hoặc email đã đăng ký"
                class="w-full px-5 py-4 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
              />
            </div>

            <div>
              <div class="flex justify-between items-center mb-2">
                <label for="login-password" class="block text-lg font-semibold text-gray-700">
                  <i class="bi bi-shield-lock text-primary-700 mr-1.5"></i>
                  Mật khẩu
                </label>
                <RouterLink to="/quen-mat-khau" class="text-base font-semibold text-primary-700 hover:underline">
                  Quên mật khẩu?
                </RouterLink>
              </div>
              <div class="relative">
                <input
                  id="login-password"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  placeholder="Nhập mật khẩu của bạn"
                  class="w-full px-5 py-4 pr-12 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                />
                <button
                  type="button"
                  @click="togglePasswordVisibility"
                  class="absolute right-4 top-1/2 -translate-y-1/2 text-2xl text-gray-400 hover:text-gray-600"
                  :aria-label="showPassword ? 'Ẩn mật khẩu' : 'Hiển thị mật khẩu'"
                >
                  <i :class="showPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
                </button>
              </div>
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg mt-4"
            >
              Đăng Nhập
            </button>
          </form>

          <!-- LOGIN WITH PHONE & OTP -->
          <form v-else @submit.prevent="handleLogin" class="space-y-6">
            <div>
              <label for="login-phone" class="block text-lg font-semibold text-gray-700 mb-2">
                <i class="bi bi-telephone text-primary-700 mr-1.5"></i>
                Số điện thoại
              </label>
              <div class="flex gap-3">
                <input
                  id="login-phone"
                  v-model="loginForm.phone"
                  type="tel"
                  placeholder="VD: 0901234567"
                  class="flex-1 px-5 py-4 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                />
                <button
                  type="button"
                  @click="sendOTP"
                  :disabled="otpCountdown > 0"
                  class="px-5 bg-primary-100 text-primary-800 font-bold rounded-2xl hover:bg-primary-200 transition-colors border border-primary-200 disabled:bg-gray-100 disabled:text-gray-400 disabled:border-gray-200 text-base"
                >
                  {{ otpCountdown > 0 ? `Gửi lại (${otpCountdown}s)` : 'Gửi mã OTP' }}
                </button>
              </div>
            </div>

            <div v-if="otpSent">
              <label for="login-otp" class="block text-lg font-semibold text-gray-700 mb-2">
                <i class="bi bi-chat-left-dots text-primary-700 mr-1.5"></i>
                Mã xác thực OTP (6 chữ số)
              </label>
              <input
                id="login-otp"
                v-model="loginForm.otp"
                type="text"
                required
                maxlength="6"
                placeholder="Nhập mã OTP 6 số"
                class="w-full px-5 py-4 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all text-center tracking-widest placeholder:text-gray-400 placeholder:tracking-normal font-bold"
              />
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg mt-4"
            >
              Xác Nhận Đăng Nhập
            </button>
          </form>

          <div class="mt-6 p-5 bg-gradient-to-br from-primary-50 to-blue-50 border border-primary-100 rounded-2xl text-base text-primary-850">
            <p class="font-bold mb-3 flex items-center gap-1.5 text-primary-800">
              <i class="bi bi-person-workspace text-xl"></i>
              Bảng Đăng Nhập Nhanh Cho Demo (Mật khẩu: 123456)
            </p>
            <div class="grid grid-cols-2 sm:grid-cols-3 gap-2.5">
              <button 
                type="button"
                @click="loginForm.username = 'patient'; loginForm.password = '123456'; loginForm.usePhone = false"
                class="py-2 px-3 bg-white hover:bg-emerald-50 border hover:border-emerald-300 rounded-xl text-sm font-semibold text-emerald-700 shadow-sm transition-all text-left flex items-center gap-1.5"
              >
                <span>🩺</span> Bệnh nhân
              </button>
              <button 
                type="button"
                @click="loginForm.username = 'nurse'; loginForm.password = '123456'; loginForm.usePhone = false"
                class="py-2 px-3 bg-white hover:bg-indigo-50 border hover:border-indigo-300 rounded-xl text-sm font-semibold text-indigo-700 shadow-sm transition-all text-left flex items-center gap-1.5"
              >
                <span>👩‍⚕️</span> Điều dưỡng
              </button>
              <button 
                type="button"
                @click="loginForm.username = 'doctor'; loginForm.password = '123456'; loginForm.usePhone = false"
                class="py-2 px-3 bg-white hover:bg-sky-50 border hover:border-sky-300 rounded-xl text-sm font-semibold text-sky-700 shadow-sm transition-all text-left flex items-center gap-1.5"
              >
                <span>👨‍⚕️</span> Bác sĩ
              </button>
              <button 
                type="button"
                @click="loginForm.username = 'pharmacist'; loginForm.password = '123456'; loginForm.usePhone = false"
                class="py-2 px-3 bg-white hover:bg-teal-50 border hover:border-teal-300 rounded-xl text-sm font-semibold text-teal-700 shadow-sm transition-all text-left flex items-center gap-1.5"
              >
                <span>💊</span> Dược sĩ
              </button>
              <button 
                type="button"
                @click="loginForm.username = 'warehouse'; loginForm.password = '123456'; loginForm.usePhone = false"
                class="py-2 px-3 bg-white hover:bg-amber-50 border hover:border-amber-300 rounded-xl text-sm font-semibold text-amber-700 shadow-sm transition-all text-left flex items-center gap-1.5"
              >
                <span>📦</span> Thủ kho thuốc
              </button>
              <button 
                type="button"
                @click="loginForm.username = 'director'; loginForm.password = '123456'; loginForm.usePhone = false"
                class="py-2 px-3 bg-white hover:bg-purple-50 border hover:border-purple-300 rounded-xl text-sm font-semibold text-purple-700 shadow-sm transition-all text-left flex items-center gap-1.5"
              >
                <span>🏢</span> Giám đốc
              </button>
            </div>
          </div>
        </div>

        <!-- ==================== REGISTER FORM ==================== -->
        <div v-else class="animate-fade-in space-y-6">
          <div class="text-center mb-6">
            <h2 class="text-3xl font-extrabold text-gray-800">Tạo Tài Khoản Mới</h2>
            <p class="text-lg text-gray-500 mt-2">Đăng ký để dễ dàng đặt lịch khám và xem đơn thuốc trực tuyến</p>
          </div>

          <form @submit.prevent="handleRegister" class="space-y-5">
            <div>
              <label for="reg-name" class="block text-lg font-semibold text-gray-700 mb-2">
                <i class="bi bi-person text-primary-700 mr-1.5"></i>
                Họ và tên bệnh nhân <span class="text-red-500">*</span>
              </label>
              <input
                id="reg-name"
                v-model="registerForm.fullName"
                type="text"
                required
                placeholder="Nhập họ và tên đầy đủ của bác"
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
              />
            </div>

            <div>
              <span class="block text-lg font-semibold text-gray-700 mb-2">
                <i class="bi bi-gender-ambiguous text-primary-700 mr-1.5"></i>
                Giới tính <span class="text-red-500">*</span>
              </span>
              <div class="flex gap-4">
                <label class="flex-1 flex items-center justify-center gap-2 p-3 border rounded-xl cursor-pointer text-lg font-semibold transition-all"
                       :class="registerForm.gender === 'Nam' ? 'bg-primary-50 border-primary-500 text-primary-700 font-bold' : 'border-gray-200 text-gray-600'">
                  <input type="radio" value="Nam" v-model="registerForm.gender" class="sr-only" />
                  <i class="bi bi-gender-male text-xl"></i> Nam
                </label>
                <label class="flex-1 flex items-center justify-center gap-2 p-3 border rounded-xl cursor-pointer text-lg font-semibold transition-all"
                       :class="registerForm.gender === 'Nữ' ? 'bg-pink-50 border-pink-500 text-pink-700 font-bold' : 'border-gray-200 text-gray-600'">
                  <input type="radio" value="Nữ" v-model="registerForm.gender" class="sr-only" />
                  <i class="bi bi-gender-female text-xl"></i> Nữ
                </label>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="reg-phone" class="block text-lg font-semibold text-gray-700 mb-2">
                  <i class="bi bi-telephone text-primary-700 mr-1.5"></i>
                  Số điện thoại <span class="text-red-500">*</span>
                </label>
                <input
                  id="reg-phone"
                  v-model="registerForm.phone"
                  type="tel"
                  required
                  placeholder="Số điện thoại của bác"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                />
              </div>

              <div>
                <label for="reg-email" class="block text-lg font-semibold text-gray-700 mb-2">
                  <i class="bi bi-envelope text-primary-700 mr-1.5"></i>
                  Email (Tùy chọn)
                </label>
                <input
                  id="reg-email"
                  v-model="registerForm.email"
                  type="email"
                  placeholder="VD: hung@gmail.com"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                />
              </div>
            </div>

            <div>
              <label for="reg-address" class="block text-lg font-semibold text-gray-700 mb-2">
                <i class="bi bi-geo-alt text-primary-700 mr-1.5"></i>
                Địa chỉ cư trú
              </label>
              <input
                id="reg-address"
                v-model="registerForm.address"
                type="text"
                placeholder="Số nhà, đường, xã/phường, quận/huyện"
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="reg-password" class="block text-lg font-semibold text-gray-700 mb-2">
                  <i class="bi bi-shield-lock text-primary-700 mr-1.5"></i>
                  Mật khẩu <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <input
                    id="reg-password"
                    v-model="registerForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    required
                    placeholder="Tạo mật khẩu"
                    class="w-full px-5 py-3.5 pr-12 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                  />
                  <button
                    type="button"
                    @click="togglePasswordVisibility"
                    class="absolute right-4 top-1/2 -translate-y-1/2 text-2xl text-gray-400 hover:text-gray-600"
                  >
                    <i :class="showPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
                  </button>
                </div>
              </div>

              <div>
                <label for="reg-confirm" class="block text-lg font-semibold text-gray-700 mb-2">
                  <i class="bi bi-shield-check text-primary-700 mr-1.5"></i>
                  Xác nhận lại mật khẩu <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <input
                    id="reg-confirm"
                    v-model="registerForm.confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    required
                    placeholder="Nhập lại mật khẩu"
                    class="w-full px-5 py-3.5 pr-12 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
                  />
                  <button
                    type="button"
                    @click="toggleConfirmPasswordVisibility"
                    class="absolute right-4 top-1/2 -translate-y-1/2 text-2xl text-gray-400 hover:text-gray-600"
                  >
                    <i :class="showConfirmPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
                  </button>
                </div>
              </div>
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg mt-4"
            >
              Đăng Ký Tài Khoản
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
