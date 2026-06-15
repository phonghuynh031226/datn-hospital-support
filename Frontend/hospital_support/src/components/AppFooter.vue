<script setup>
const currentYear = new Date().getFullYear()

const quickLinks = [
  { name: 'Trang chủ', to: '/' },
  { name: 'Giới thiệu', to: '/gioi-thieu' },
  { name: 'Đội ngũ bác sĩ', to: '/bac-si' },
  { name: 'Tin tức', to: '/tin-tuc' },
  { name: 'Liên hệ', to: '/lien-he' },
]

const departments = [
  'Tim mạch',
  'Nội tổng quát',
  'Ngoại khoa',
  'Sản phụ khoa',
  'Nhi khoa',
  'Mắt',
]
</script>

<template>
  <footer class="bg-gray-900 text-gray-300">
    <!-- Main Footer -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 lg:py-16">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-10 lg:gap-8">
        <!-- Hospital Info -->
        <div class="lg:col-span-1">
          <div class="flex items-center gap-3 mb-5">
            <div class="w-11 h-11 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl flex items-center justify-center">
              <i class="bi bi-hospital text-white text-xl"></i>
            </div>
            <div>
              <h3 class="text-white font-bold text-lg leading-tight">Bệnh Viện Đa Khoa</h3>
              <p class="text-xs text-gray-500">Hospital Support System</p>
            </div>
          </div>
          <p class="text-sm leading-relaxed text-gray-400 mb-5">
            Hệ thống y tế hiện đại, uy tín hàng đầu. Chăm sóc sức khỏe toàn diện cho mọi gia đình với đội ngũ bác sĩ giỏi và trang thiết bị tiên tiến.
          </p>
          <!-- Social -->
          <div class="flex gap-3">
            <a href="#" class="w-9 h-9 rounded-lg bg-gray-800 hover:bg-primary-700 flex items-center justify-center transition-colors duration-300" aria-label="Facebook">
              <i class="bi bi-facebook text-sm"></i>
            </a>
            <a href="#" class="w-9 h-9 rounded-lg bg-gray-800 hover:bg-primary-700 flex items-center justify-center transition-colors duration-300" aria-label="YouTube">
              <i class="bi bi-youtube text-sm"></i>
            </a>
            <a href="#" class="w-9 h-9 rounded-lg bg-gray-800 hover:bg-primary-700 flex items-center justify-center transition-colors duration-300" aria-label="Zalo">
              <i class="bi bi-chat-dots text-sm"></i>
            </a>
            <a href="#" class="w-9 h-9 rounded-lg bg-gray-800 hover:bg-primary-700 flex items-center justify-center transition-colors duration-300" aria-label="Phone">
              <i class="bi bi-telephone text-sm"></i>
            </a>
          </div>
        </div>

        <!-- Quick Links -->
        <div>
          <h4 class="text-white font-semibold text-base mb-5 relative pl-3 before:absolute before:left-0 before:top-0 before:bottom-0 before:w-1 before:bg-primary-500 before:rounded-full">
            Liên kết nhanh
          </h4>
          <ul class="space-y-3">
            <li v-for="link in quickLinks" :key="link.to">
              <RouterLink
                :to="link.to"
                class="text-sm text-gray-400 hover:text-primary-400 hover:pl-1 transition-all duration-200 flex items-center gap-2"
              >
                <i class="bi bi-chevron-right text-xs text-primary-500"></i>
                {{ link.name }}
              </RouterLink>
            </li>
            <li>
              <RouterLink
                to="/dat-lich"
                class="text-sm text-gray-400 hover:text-primary-400 hover:pl-1 transition-all duration-200 flex items-center gap-2"
              >
                <i class="bi bi-chevron-right text-xs text-primary-500"></i>
                Đặt lịch khám
              </RouterLink>
            </li>
          </ul>
        </div>

        <!-- Departments -->
        <div>
          <h4 class="text-white font-semibold text-base mb-5 relative pl-3 before:absolute before:left-0 before:top-0 before:bottom-0 before:w-1 before:bg-primary-500 before:rounded-full">
            Chuyên khoa
          </h4>
          <ul class="space-y-3">
            <li v-for="dept in departments" :key="dept">
              <span class="text-sm text-gray-400 flex items-center gap-2">
                <i class="bi bi-chevron-right text-xs text-primary-500"></i>
                {{ dept }}
              </span>
            </li>
          </ul>
        </div>

        <!-- Contact Info -->
        <div>
          <h4 class="text-white font-semibold text-base mb-5 relative pl-3 before:absolute before:left-0 before:top-0 before:bottom-0 before:w-1 before:bg-primary-500 before:rounded-full">
            Thông tin liên hệ
          </h4>
          <ul class="space-y-4">
            <li class="flex items-start gap-3">
              <div class="w-8 h-8 rounded-lg bg-gray-800 flex items-center justify-center flex-shrink-0 mt-0.5">
                <i class="bi bi-geo-alt text-primary-400 text-sm"></i>
              </div>
              <span class="text-sm text-gray-400">
                123 Nguyễn Văn Linh, Quận 7,<br>TP. Hồ Chí Minh
              </span>
            </li>
            <li class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-gray-800 flex items-center justify-center flex-shrink-0">
                <i class="bi bi-telephone text-primary-400 text-sm"></i>
              </div>
              <div>
                <a href="tel:19001234" class="text-sm text-gray-400 hover:text-primary-400 transition-colors">
                  Hotline: 1900 1234
                </a>
              </div>
            </li>
            <li class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-gray-800 flex items-center justify-center flex-shrink-0">
                <i class="bi bi-envelope text-primary-400 text-sm"></i>
              </div>
              <a href="mailto:info@benhvien.vn" class="text-sm text-gray-400 hover:text-primary-400 transition-colors">
                info@benhvien.vn
              </a>
            </li>
            <li class="flex items-start gap-3">
              <div class="w-8 h-8 rounded-lg bg-gray-800 flex items-center justify-center flex-shrink-0 mt-0.5">
                <i class="bi bi-clock text-primary-400 text-sm"></i>
              </div>
              <div class="text-sm text-gray-400">
                <p>Thứ 2 - Thứ 6: 7:00 - 17:00</p>
                <p>Thứ 7: 7:00 - 12:00</p>
                <p>Chủ nhật: Nghỉ</p>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Bottom Bar -->
    <div class="border-t border-gray-800">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-5">
        <div class="flex flex-col sm:flex-row items-center justify-between gap-3">
          <p class="text-sm text-gray-500 text-center sm:text-left">
            &copy; {{ currentYear }} Bệnh Viện Đa Khoa. All rights reserved.
          </p>
          <div class="flex gap-4 text-sm text-gray-500">
            <a href="#" class="hover:text-primary-400 transition-colors">Chính sách bảo mật</a>
            <a href="#" class="hover:text-primary-400 transition-colors">Điều khoản sử dụng</a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>
