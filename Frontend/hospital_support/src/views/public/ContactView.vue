<script setup>
import { ref } from 'vue'

const form = ref({
  fullName: '',
  phone: '',
  email: '',
  title: '',
  message: ''
})

const isSubmitted = ref(false)

function submitContact() {
  if (!form.value.fullName || !form.value.phone || !form.value.message) {
    alert('Vui lòng nhập các thông tin bắt buộc (*)!')
    return
  }
  
  isSubmitted.value = true
  alert('Gửi lời nhắn thành công! Đội ngũ tư vấn sẽ liên hệ lại với bạn sớm nhất qua Số điện thoại.')
  form.value = {
    fullName: '',
    phone: '',
    email: '',
    title: '',
    message: ''
  }
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12 md:py-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Liên Hệ & Góp Ý</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Đội ngũ chăm sóc khách hàng của bệnh viện luôn sẵn sàng lắng nghe và giải đáp mọi thắc mắc của quý bệnh nhân.
        </p>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 mt-10">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start">
        
        <!-- ==================== CONTACT INFO ==================== -->
        <div class="space-y-6 animate-slide-in-left">
          <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-6">
            <h2 class="text-3xl font-bold text-gray-800 border-b border-gray-100 pb-3">Thông Tin Liên Hệ</h2>
            
            <!-- Address -->
            <div class="flex gap-4 items-start">
              <div class="w-12 h-12 rounded-xl bg-primary-100 text-primary-700 flex items-center justify-center flex-shrink-0">
                <i class="bi bi-geo-alt text-2xl"></i>
              </div>
              <div>
                <h3 class="text-lg font-bold text-gray-800">Địa chỉ bệnh viện:</h3>
                <p class="text-base text-gray-600 mt-1 leading-relaxed">123 Đường Láng, phường Láng Thượng, quận Đống Đa, Hà Nội</p>
              </div>
            </div>

            <!-- Hotline -->
            <div class="flex gap-4 items-start">
              <div class="w-12 h-12 rounded-xl bg-red-100 text-red-700 flex items-center justify-center flex-shrink-0 animate-pulse">
                <i class="bi bi-telephone-fill text-2xl"></i>
              </div>
              <div>
                <h3 class="text-lg font-bold text-gray-800">Tổng đài hỗ trợ 24/7 (Bấm để gọi):</h3>
                <a href="tel:19001234" class="text-2xl md:text-3xl font-extrabold text-red-600 hover:underline inline-block mt-1">
                  1900 1234
                </a>
                <p class="text-xs text-gray-400 mt-1">(Miễn phí cước cuộc gọi cho bệnh nhân BHYT)</p>
              </div>
            </div>

            <!-- Email -->
            <div class="flex gap-4 items-start">
              <div class="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center flex-shrink-0">
                <i class="bi bi-envelope text-2xl"></i>
              </div>
              <div>
                <h3 class="text-lg font-bold text-gray-800">Hòm thư điện tử:</h3>
                <a href="mailto:lienhe@benhvien.vn" class="text-lg text-primary-700 font-semibold hover:underline inline-block mt-1">
                  lienhe@benhvien.vn
                </a>
              </div>
            </div>

            <!-- Hours -->
            <div class="flex gap-4 items-start">
              <div class="w-12 h-12 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center flex-shrink-0">
                <i class="bi bi-clock text-2xl"></i>
              </div>
              <div>
                <h3 class="text-lg font-bold text-gray-800">Thời gian làm việc:</h3>
                <p class="text-base text-gray-600 mt-1">Từ thứ Hai đến thứ Bảy: 07h30 - 17h00</p>
                <p class="text-sm text-red-600 font-semibold mt-1">Khu vực Cấp cứu hoạt động 24/24 kể cả ngày lễ Tết.</p>
              </div>
            </div>
          </div>

          <!-- Mock Map (Visual representation) -->
          <div class="bg-white p-4 rounded-3xl border border-gray-100 shadow-sm overflow-hidden h-[300px] flex flex-col justify-between relative group">
            <!-- Simulated Google Map Design -->
            <div class="absolute inset-0 bg-blue-50/50 flex flex-col items-center justify-center text-center p-6 border">
              <div class="w-16 h-16 bg-white shadow-md rounded-full flex items-center justify-center text-red-500 mb-3 text-3xl">
                <i class="bi bi-geo-alt-fill"></i>
              </div>
              <h4 class="font-bold text-gray-800 text-lg">Bản đồ chỉ đường Bệnh viện Đa Khoa</h4>
              <p class="text-sm text-gray-400 mt-1">123 Đường Láng, phường Láng Thượng, Đống Đa, Hà Nội</p>
              <a href="https://maps.google.com" target="_blank" class="mt-4 px-6 py-2.5 bg-primary-700 text-white font-bold rounded-xl text-sm shadow hover:bg-primary-800 transition-colors">
                <i class="bi bi-box-arrow-up-right"></i> Mở trên Google Maps
              </a>
            </div>
          </div>
        </div>

        <!-- ==================== CONTACT FORM ==================== -->
        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm animate-fade-in">
          <h2 class="text-3xl font-bold text-gray-800 border-b border-gray-100 pb-3 mb-6">Gửi Lời Nhắn Hỗ Trợ</h2>
          
          <form @submit.prevent="submitContact" class="space-y-5">
            <div>
              <label for="contact-name" class="block text-lg font-semibold text-gray-700 mb-2">
                Họ và tên của bạn <span class="text-red-500">*</span>
              </label>
              <input
                id="contact-name"
                v-model="form.fullName"
                type="text"
                required
                placeholder="Nhập đầy đủ họ và tên bạn..."
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="contact-phone" class="block text-lg font-semibold text-gray-700 mb-2">
                  Số điện thoại <span class="text-red-500">*</span>
                </label>
                <input
                  id="contact-phone"
                  v-model="form.phone"
                  type="tel"
                  required
                  placeholder="Nhập số điện thoại liên hệ"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
                />
              </div>

              <div>
                <label for="contact-email" class="block text-lg font-semibold text-gray-700 mb-2">
                  Địa chỉ Email (Nếu có)
                </label>
                <input
                  id="contact-email"
                  v-model="form.email"
                  type="email"
                  placeholder="VD: email@gmail.com"
                  class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
                />
              </div>
            </div>

            <div>
              <label for="contact-title" class="block text-lg font-semibold text-gray-700 mb-2">
                Chủ đề cần hỏi (Ví dụ: Đặt lịch, Chi phí khám, BHYT)
              </label>
              <input
                id="contact-title"
                v-model="form.title"
                type="text"
                placeholder="Nhập chủ đề bạn quan tâm"
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 bg-gray-50/50"
              />
            </div>

            <div>
              <label for="contact-msg" class="block text-lg font-semibold text-gray-700 mb-2">
                Nội dung câu hỏi hoặc lời nhắn góp ý <span class="text-red-500">*</span>
              </label>
              <textarea
                id="contact-msg"
                v-model="form.message"
                rows="5"
                required
                placeholder="Nhập chi tiết câu hỏi hoặc thắc mắc của bạn cần giải đáp..."
                class="w-full px-5 py-3.5 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400 resize-none bg-gray-50/50"
              ></textarea>
            </div>

            <button
              type="submit"
              class="w-full btn-primary !py-4.5 !text-xl !rounded-2xl shadow-lg mt-4"
            >
              <i class="bi bi-send-fill mr-1.5"></i> Gửi Tin Nhắn Cho Chúng Tôi
            </button>
            
            <p class="text-center text-xs text-gray-400"><i class="bi bi-shield-check"></i> Thông tin của bạn được bảo mật và phản hồi trong 24 giờ làm việc.</p>
          </form>
        </div>

      </div>
    </div>
  </div>
</template>
