<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const isLoggedIn = ref(false)
const currentUser = ref(null)

const profileForm = ref({
  fullName: '',
  gender: 'Nam',
  phone: '',
  email: '',
  address: '',
  ngaySinh: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

onMounted(() => {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
    
    // Fill form
    profileForm.value.fullName = currentUser.value.fullName || ''
    profileForm.value.gender = currentUser.value.gender || 'Nam'
    profileForm.value.phone = currentUser.value.phone || ''
    profileForm.value.email = currentUser.value.email || ''
    profileForm.value.address = currentUser.value.address || ''
    profileForm.value.ngaySinh = currentUser.value.ngaySinh || ''
  }
})

async function handleUpdateProfile() {
  if (!profileForm.value.fullName || !profileForm.value.phone) {
    alert('Họ tên và Số điện thoại là bắt buộc!')
    return
  }
  
  try {
    const res = await axios.put(`http://localhost:8080/api/auth/update-profile/${currentUser.value.id}`, {
      fullName: profileForm.value.fullName,
      gender: profileForm.value.gender,
      phone: profileForm.value.phone,
      email: profileForm.value.email || null,
      address: profileForm.value.address || null,
      ngaySinh: profileForm.value.ngaySinh || null,
      password: 'dummy'
    })
    
    const user = res.data
    const roleMap = {
      'BENH_NHAN': 'patient',
      'DIEU_DUONG': 'nurse',
      'BAC_SI': 'doctor',
      'DUOC_SI': 'pharmacist',
      'THU_KHO': 'warehouse'
    }
    const updatedUser = {
      id: user.id,
      fullName: user.fullName,
      gender: user.gender,
      phone: user.phone,
      email: user.email,
      address: user.address,
      ngaySinh: user.ngaySinh,
      role: roleMap[user.role] || 'patient'
    }
    
    currentUser.value = updatedUser
    localStorage.setItem('currentUser', JSON.stringify(updatedUser))
    alert('Cập nhật thông tin cá nhân thành công!')
  } catch (err) {
    alert(err.response?.data?.message || 'Cập nhật thông tin cá nhân thất bại!')
  }
}

function handleChangePassword() {
  const p = passwordForm.value
  if (!p.oldPassword || !p.newPassword || !p.confirmPassword) {
    alert('Vui lòng điền đầy đủ thông tin đổi mật khẩu!')
    return
  }
  
  if (p.newPassword !== p.confirmPassword) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  
  // Check in registeredUsers
  let registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
  let index = registeredUsers.findIndex(u => u.phone === currentUser.value.phone || u.email === currentUser.value.email)
  
  if (index !== -1) {
    if (registeredUsers[index].password !== p.oldPassword) {
      alert('Mật khẩu cũ không chính xác!')
      return
    }
    
    registeredUsers[index].password = p.newPassword
    localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers))
  } else {
    // If logging in with the default mock 'patient'
    if (p.oldPassword !== '123456') {
      alert('Mật khẩu cũ không chính xác! (Mật khẩu mặc định là: 123456)')
      return
    }
  }
  
  alert('Đổi mật khẩu thành công!')
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

function handleLogout() {
  localStorage.removeItem('currentUser')
  alert('Đã đăng xuất tài khoản thành công.')
  window.location.href = '/'
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Hồ Sơ Cá Nhân</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Quản lý thông tin sức khỏe cá nhân, thông tin liên lạc và bảo mật tài khoản của bạn.
        </p>
      </div>
    </div>

    <!-- Check Login State -->
    <div v-if="!isLoggedIn" class="max-w-md mx-auto px-4 mt-16 text-center animate-fade-in space-y-6">
      <div class="bg-white p-8 rounded-3xl border border-gray-100 shadow-sm">
        <div class="w-16 h-16 bg-primary-50 text-primary-700 rounded-full flex items-center justify-center mx-auto mb-4 text-3xl">
          🔒
        </div>
        <h3 class="text-2xl font-bold text-gray-800">Yêu Cầu Đăng Nhập</h3>
        <p class="text-base text-gray-500 mt-2">Bạn cần đăng nhập tài khoản để xem và cập nhật hồ sơ cá nhân.</p>
        <RouterLink to="/dang-nhap" class="w-full btn-primary !py-3.5 !text-lg !rounded-2xl shadow mt-6">
          Đến Trang Đăng Nhập
        </RouterLink>
      </div>
    </div>

    <div v-else class="max-w-6xl mx-auto px-4 mt-8">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        <!-- ==================== PATIENT DETAILS FORM ==================== -->
        <div class="lg:col-span-7 bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-6 animate-slide-in-left">
          <div class="flex justify-between items-center border-b border-gray-100 pb-3">
            <h2 class="text-2xl font-bold text-gray-800"><i class="bi bi-person-circle text-primary-700 mr-2"></i>Thông Tin Bệnh Nhân</h2>
          </div>

          <form @submit.prevent="handleUpdateProfile" class="space-y-5">
            <div>
              <label for="profile-name" class="block text-lg font-semibold text-gray-700 mb-2">Họ và tên bệnh nhân <span class="text-red-500">*</span></label>
              <input
                id="profile-name"
                v-model="profileForm.fullName"
                type="text"
                required
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all bg-gray-50/50 font-bold"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <span class="block text-lg font-semibold text-gray-700 mb-2">Giới tính</span>
                <div class="flex gap-3">
                  <label class="flex-1 flex items-center justify-center gap-2 p-3 border rounded-2xl cursor-pointer text-base font-semibold transition-all"
                         :class="profileForm.gender === 'Nam' ? 'bg-primary-50 border-primary-500 text-primary-700 font-bold' : 'border-gray-200 text-gray-600'">
                    <input type="radio" value="Nam" v-model="profileForm.gender" class="sr-only" />
                    Nam
                  </label>
                  <label class="flex-1 flex items-center justify-center gap-2 p-3 border rounded-2xl cursor-pointer text-base font-semibold transition-all"
                         :class="profileForm.gender === 'Nữ' ? 'bg-pink-50 border-pink-500 text-pink-700 font-bold' : 'border-gray-200 text-gray-600'">
                    <input type="radio" value="Nữ" v-model="profileForm.gender" class="sr-only" />
                    Nữ
                  </label>
                </div>
              </div>

              <div>
                <label for="profile-phone" class="block text-lg font-semibold text-gray-700 mb-2">Số điện thoại <span class="text-red-500">*</span></label>
                <input
                  id="profile-phone"
                  v-model="profileForm.phone"
                  type="tel"
                  required
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all bg-gray-50/50"
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="profile-email" class="block text-lg font-semibold text-gray-700 mb-2">Địa chỉ Email</label>
                <input
                  id="profile-email"
                  v-model="profileForm.email"
                  type="email"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all bg-gray-50/50"
                />
              </div>

              <div>
                <label for="profile-dob" class="block text-lg font-semibold text-gray-700 mb-2">Ngày sinh</label>
                <input
                  id="profile-dob"
                  v-model="profileForm.ngaySinh"
                  type="date"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all bg-gray-50/50"
                />
              </div>
            </div>

            <div>
              <label for="profile-address" class="block text-lg font-semibold text-gray-700 mb-2">Địa chỉ thường trú</label>
              <input
                id="profile-address"
                v-model="profileForm.address"
                type="text"
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all bg-gray-50/50"
              />
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-4 !text-lg !rounded-2xl shadow mt-2"
            >
              Lưu Thay Đổi Thông Tin
            </button>
          </form>
        </div>

        <!-- ==================== SECURITY / CHANGE PASSWORD ==================== -->
        <div class="lg:col-span-5 bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-6 animate-fade-in">
          <h2 class="text-2xl font-bold text-gray-800 border-b border-gray-100 pb-3">
            <i class="bi bi-shield-lock text-primary-700 mr-2"></i>Đổi Mật Khẩu
          </h2>

          <form @submit.prevent="handleChangePassword" class="space-y-4">
            <!-- Old Password -->
            <div>
              <label for="old-pass" class="block text-base font-semibold text-gray-700 mb-1">Mật khẩu cũ:</label>
              <div class="relative">
                <input
                  id="old-pass"
                  v-model="passwordForm.oldPassword"
                  :type="showOldPassword ? 'text' : 'password'"
                  required
                  placeholder="Nhập mật khẩu đang dùng"
                  class="w-full px-4 py-3 text-base rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 placeholder:text-gray-400 bg-gray-50/50"
                />
                <button
                  type="button"
                  @click="showOldPassword = !showOldPassword"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-xl text-gray-400 hover:text-gray-600"
                >
                  <i :class="showOldPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
                </button>
              </div>
            </div>

            <!-- New Password -->
            <div>
              <label for="new-pass-profile" class="block text-base font-semibold text-gray-700 mb-1">Mật khẩu mới:</label>
              <div class="relative">
                <input
                  id="new-pass-profile"
                  v-model="passwordForm.newPassword"
                  :type="showNewPassword ? 'text' : 'password'"
                  required
                  placeholder="Nhập mật khẩu mới"
                  class="w-full px-4 py-3 text-base rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 placeholder:text-gray-400 bg-gray-50/50"
                />
                <button
                  type="button"
                  @click="showNewPassword = !showNewPassword"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-xl text-gray-400 hover:text-gray-600"
                >
                  <i :class="showNewPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
                </button>
              </div>
            </div>

            <!-- Confirm New Password -->
            <div>
              <label for="confirm-pass-profile" class="block text-base font-semibold text-gray-700 mb-1">Xác nhận mật khẩu mới:</label>
              <div class="relative">
                <input
                  id="confirm-pass-profile"
                  v-model="passwordForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  required
                  placeholder="Nhập lại mật khẩu mới"
                  class="w-full px-4 py-3 text-base rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 placeholder:text-gray-400 bg-gray-50/50"
                />
                <button
                  type="button"
                  @click="showConfirmPassword = !showConfirmPassword"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-xl text-gray-400 hover:text-gray-600"
                >
                  <i :class="showConfirmPassword ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'"></i>
                </button>
              </div>
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-3.5 !text-base !rounded-xl shadow mt-4"
            >
              Cập Nhật Mật Khẩu Mới
            </button>
          </form>
        </div>

      </div>
    </div>
  </div>
</template>
