<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

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
  confirmPassword: '',
  ngaySinh: ''
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

function mapRole(backendRole) {
  const roleMap = {
    'BENH_NHAN': 'patient',
    'DIEU_DUONG': 'nurse',
    'BAC_SI': 'doctor',
    'DUOC_SI': 'pharmacist',
    'THU_KHO': 'warehouse',
    'GIAM_DOC': 'director'
  }
  return roleMap[backendRole] || 'patient'
}

async function handleLogin() {
  if (loginForm.value.usePhone) {
    if (!loginForm.value.phone || !loginForm.value.otp) {
      alert('Vui lòng điền đầy đủ số điện thoại và mã OTP!')
      return
    }
    if (loginForm.value.otp !== '123456') {
      alert('Mã OTP không đúng! (Mã mặc định là: 123456)')
      return
    }
    
    try {
      const res = await axios.post('http://localhost:8080/api/auth/login', {
        username: loginForm.value.phone,
        password: '123456'
      })
      const user = res.data
      const mappedUser = {
        id: user.id,
        fullName: user.fullName,
        gender: user.gender,
        phone: user.phone,
        email: user.email,
        address: user.address,
        role: mapRole(user.role),
        ngaySinh: user.ngaySinh
      }
      localStorage.setItem('currentUser', JSON.stringify(mappedUser))
      alert('Đăng nhập thành công bằng số điện thoại!')
      router.push('/')
    } catch (err) {
      alert(err.response?.data?.message || 'Không tìm thấy tài khoản tương ứng với số điện thoại này!')
    }
  } else {
    if (!loginForm.value.username || !loginForm.value.password) {
      alert('Vui lòng nhập tài khoản và mật khẩu!')
      return
    }
    
    try {
      const res = await axios.post('http://localhost:8080/api/auth/login', {
        username: loginForm.value.username,
        password: loginForm.value.password
      })
      const user = res.data
      const mappedUser = {
        id: user.id,
        fullName: user.fullName,
        gender: user.gender,
        phone: user.phone,
        email: user.email,
        address: user.address,
        role: mapRole(user.role),
        ngaySinh: user.ngaySinh
      }
      localStorage.setItem('currentUser', JSON.stringify(mappedUser))
      alert(`Chào mừng ${mappedUser.fullName} quay trở lại!`)

      // Redirect accordingly
      if (mappedUser.role === 'patient') router.push('/dat-lich')
      else if (mappedUser.role === 'nurse') router.push('/dieu-duong')
      else if (mappedUser.role === 'doctor') router.push('/bac-si')
      else if (mappedUser.role === 'pharmacist') router.push('/duoc-si')
      else if (mappedUser.role === 'warehouse') router.push('/kho-thuoc')
      else if (mappedUser.role === 'director') router.push('/giam-doc')
      else router.push('/')
    } catch (err) {
      alert(err.response?.data?.message || 'Tài khoản hoặc mật khẩu không chính xác!')
    }
  }
}

async function handleRegister() {
  const f = registerForm.value
  if (!f.fullName || !f.phone || !f.password || !f.confirmPassword) {
    alert('Vui lòng nhập đầy đủ các trường bắt buộc (*)!')
    return
  }
  if (f.password !== f.confirmPassword) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  
  try {
    await axios.post('http://localhost:8080/api/auth/register', {
      fullName: f.fullName,
      gender: f.gender,
      phone: f.phone,
      email: f.email || null,
      address: f.address || null,
      password: f.password,
      ngaySinh: f.ngaySinh || null
    })
    alert('Đăng ký tài khoản thành công! Bây giờ bạn có thể đăng nhập.')
    switchTab('login')
  } catch (err) {
    alert(err.response?.data?.message || 'Đăng ký tài khoản thất bại!')
  }
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

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
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

              <div>
                <label for="reg-dob" class="block text-lg font-semibold text-gray-700 mb-2">
                  <i class="bi bi-calendar-event text-primary-700 mr-1.5"></i>
                  Ngày sinh <span class="text-red-500">*</span>
                </label>
                <input
                  id="reg-dob"
                  v-model="registerForm.ngaySinh"
                  type="date"
                  required
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all text-gray-700"
                />
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
