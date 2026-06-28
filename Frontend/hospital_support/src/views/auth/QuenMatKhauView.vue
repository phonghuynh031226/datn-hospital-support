<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const step = ref(1) // 1: Enter Phone/Email, 2: Enter OTP, 3: Reset Password
const info = ref('')
const otp = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)

async function handleSendOTP() {
  if (!info.value) {
    alert('Vui lòng nhập số điện thoại hoặc email của bác!')
    return
  }
  try {
    await axios.post('http://localhost:8080/api/auth/forgot-password', { username: info.value })
    alert(`Mã OTP đã được gửi về email liên kết với tài khoản: ${info.value}.`)
    step.value = 2
  } catch (err) {
    alert(err.response?.data?.message || 'Không tìm thấy tài khoản hoặc lỗi gửi mã OTP!')
  }
}

function handleVerifyOTP() {
  if (!otp.value) {
    alert('Vui lòng nhập mã OTP!')
    return
  }
  step.value = 3
}

async function handleResetPassword() {
  if (!newPassword.value || !confirmPassword.value) {
    alert('Vui lòng điền mật khẩu mới!')
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }

  try {
    await axios.post('http://localhost:8080/api/auth/reset-password', {
      username: info.value,
      otp: otp.value,
      newPassword: newPassword.value
    })
    alert('Đặt lại mật khẩu thành công! Hãy đăng nhập lại bằng mật khẩu mới.')
    router.push('/dang-nhap')
  } catch (err) {
    alert(err.response?.data?.message || 'Đặt lại mật khẩu thất bại!')
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-140px)] bg-gradient-to-br from-primary-50 via-white to-primary-100 flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full bg-white rounded-3xl shadow-xl border border-gray-100 p-8 md:p-10 animate-fade-in-up">
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="bi bi-shield-lock text-3xl"></i>
        </div>
        <h2 class="text-3xl font-extrabold text-gray-800">Quên Mật Khẩu?</h2>
        <p class="text-base text-gray-500 mt-2">Chúng tôi sẽ giúp bác khôi phục lại mật khẩu chỉ với vài bước đơn giản.</p>
      </div>

      <!-- STEP 1: Enter Phone/Email -->
      <form v-if="step === 1" @submit.prevent="handleSendOTP" class="space-y-6">
        <div>
          <label for="recovery-info" class="block text-lg font-semibold text-gray-700 mb-2">
            Nhập số điện thoại hoặc email đăng ký:
          </label>
          <input
            id="recovery-info"
            v-model="info"
            type="text"
            required
            placeholder="VD: 0901234567"
            class="w-full px-5 py-4 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
          />
        </div>

        <button
          type="submit"
          class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg"
        >
          Nhận Mã Xác Thực (OTP)
        </button>

        <div class="text-center mt-4">
          <RouterLink to="/dang-nhap" class="text-base font-semibold text-primary-700 hover:underline">
            <i class="bi bi-arrow-left"></i> Quay lại Đăng nhập
          </RouterLink>
        </div>
      </form>

      <!-- STEP 2: Enter OTP -->
      <form v-if="step === 2" @submit.prevent="handleVerifyOTP" class="space-y-6">
        <div>
          <label for="recovery-otp" class="block text-lg font-semibold text-gray-700 mb-2 text-center">
            Nhập mã OTP 6 số đã được gửi:
          </label>
          <input
            id="recovery-otp"
            v-model="otp"
            type="text"
            required
            maxlength="6"
            placeholder="Nhập 123456"
            class="w-full px-5 py-4 text-xl rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all text-center tracking-widest placeholder:text-gray-400 placeholder:tracking-normal font-bold"
          />
        </div>

        <button
          type="submit"
          class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg"
        >
          Xác Thực OTP
        </button>

        <div class="text-center mt-4">
          <button
            type="button"
            @click="step = 1"
            class="text-base font-semibold text-gray-500 hover:text-gray-700"
          >
            Quay lại nhập thông tin
          </button>
        </div>
      </form>

      <!-- STEP 3: Reset Password -->
      <form v-if="step === 3" @submit.prevent="handleResetPassword" class="space-y-6">
        <div>
          <label for="new-pass" class="block text-lg font-semibold text-gray-700 mb-2">
            Mật khẩu mới:
          </label>
          <div class="relative">
            <input
              id="new-pass"
              v-model="newPassword"
              :type="showPassword ? 'text' : 'password'"
              required
              placeholder="Nhập mật khẩu mới"
              class="w-full px-5 py-4 pr-12 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-4 top-1/2 -translate-y-1/2 text-2xl text-gray-400 hover:text-gray-600"
            >
              <i :class="showPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
            </button>
          </div>
        </div>

        <div>
          <label for="confirm-pass" class="block text-lg font-semibold text-gray-700 mb-2">
            Xác nhận lại mật khẩu mới:
          </label>
          <div class="relative">
            <input
              id="confirm-pass"
              v-model="confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              required
              placeholder="Nhập lại mật khẩu mới"
              class="w-full px-5 py-4 pr-12 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
            />
            <button
              type="button"
              @click="showConfirmPassword = !showConfirmPassword"
              class="absolute right-4 top-1/2 -translate-y-1/2 text-2xl text-gray-400 hover:text-gray-600"
            >
              <i :class="showConfirmPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
            </button>
          </div>
        </div>

        <button
          type="submit"
          class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg"
        >
          Đặt Lại Mật Khẩu
        </button>
      </form>
    </div>
  </div>
</template>
