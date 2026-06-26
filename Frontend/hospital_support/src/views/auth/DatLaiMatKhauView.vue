<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)

function handleReset() {
  if (!password.value || !confirmPassword.value) {
    alert('Vui lòng nhập mật khẩu mới!')
    return
  }
  if (password.value !== confirmPassword.value) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  alert('Đặt lại mật khẩu mới thành công! Đang chuyển hướng bác sang đăng nhập.')
  router.push('/dang-nhap')
}
</script>

<template>
  <div class="min-h-[calc(100vh-140px)] bg-gradient-to-br from-primary-50 via-white to-primary-100 flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full bg-white rounded-3xl shadow-xl border border-gray-100 p-8 md:p-10 animate-fade-in-up">
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="bi bi-key-fill text-3xl"></i>
        </div>
        <h2 class="text-3xl font-extrabold text-gray-800">Đặt Lại Mật Khẩu</h2>
        <p class="text-base text-gray-500 mt-2">Nhập mật khẩu mới của bác bên dưới để thay thế mật khẩu cũ.</p>
      </div>

      <form @submit.prevent="handleReset" class="space-y-6">
        <div>
          <label for="new-password" class="block text-lg font-semibold text-gray-700 mb-2">Mật khẩu mới:</label>
          <div class="relative">
            <input
              id="new-password"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              required
              placeholder="Nhập mật khẩu mới của bác"
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
          <label for="confirm-password" class="block text-lg font-semibold text-gray-700 mb-2">Nhập lại mật khẩu mới:</label>
          <div class="relative">
            <input
              id="confirm-password"
              v-model="confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              required
              placeholder="Xác nhận lại mật khẩu mới"
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
