<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

/* ========== CAROUSEL ========== */
const currentSlide = ref(0)
const slides = [
  {
    image: '/images/banners/banner1.png',
    title: 'Chào Mừng Đến Với Bệnh Viện Đa Khoa',
    subtitle: 'Hệ thống y tế hiện đại, uy tín hàng đầu — nơi sức khỏe của bạn là ưu tiên số một',
  },
  {
    image: '/images/banners/banner2.png',
    title: 'Đội Ngũ Bác Sĩ Giỏi & Tận Tâm',
    subtitle: 'Hơn 50 chuyên gia đầu ngành, giàu kinh nghiệm, luôn đồng hành cùng bạn',
  },
  {
    image: '/images/banners/banner3.png',
    title: 'Trang Thiết Bị Hiện Đại',
    subtitle: 'Hệ thống máy móc tiên tiến nhất, đảm bảo chẩn đoán chính xác và điều trị hiệu quả',
  },
]
let slideInterval = null

function nextSlide() {
  currentSlide.value = (currentSlide.value + 1) % slides.length
}
function prevSlide() {
  currentSlide.value = (currentSlide.value - 1 + slides.length) % slides.length
}
function goToSlide(index) {
  currentSlide.value = index
}

onMounted(() => {
  slideInterval = setInterval(nextSlide, 5000)
})
onUnmounted(() => {
  if (slideInterval) clearInterval(slideInterval)
})

/* ========== WHY CHOOSE US ========== */
const reasons = [
  {
    icon: 'bi-people-fill',
    title: 'Đội Ngũ Chuyên Gia',
    desc: 'Hơn 50 bác sĩ đầu ngành, giàu kinh nghiệm, được đào tạo tại các trường y khoa hàng đầu trong và ngoài nước.',
    color: 'from-blue-500 to-blue-700',
  },
  {
    icon: 'bi-gpu-card',
    title: 'Thiết Bị Hiện Đại',
    desc: 'Hệ thống trang thiết bị y tế tiên tiến, nhập khẩu từ Đức, Nhật Bản, đảm bảo chẩn đoán chính xác nhất.',
    color: 'from-emerald-500 to-emerald-700',
  },
  {
    icon: 'bi-cash-coin',
    title: 'Chi Phí Hợp Lý',
    desc: 'Cam kết chi phí minh bạch, hợp lý. Hỗ trợ bảo hiểm y tế và các chương trình ưu đãi cho bệnh nhân.',
    color: 'from-amber-500 to-amber-700',
  },
  {
    icon: 'bi-heart-pulse-fill',
    title: 'Phục Vụ Tận Tâm',
    desc: 'Dịch vụ chăm sóc chu đáo, tận tâm từ khâu tiếp đón đến điều trị. Luôn lấy bệnh nhân làm trung tâm.',
    color: 'from-rose-500 to-rose-700',
  },
]

/* ========== DOCTORS ========== */
const doctors = [
  {
    name: 'PGS.TS Nguyễn Văn An',
    specialty: 'Tim mạch',
    experience: '25 năm kinh nghiệm',
    image: '/images/doctors/doctor1.png',
    desc: 'Chuyên gia hàng đầu về tim mạch can thiệp, nguyên Phó Giám đốc Bệnh viện Tim',
  },
  {
    name: 'TS.BS Trần Thị Mai',
    specialty: 'Sản phụ khoa',
    experience: '20 năm kinh nghiệm',
    image: '/images/doctors/doctor2.png',
    desc: 'Chuyên gia sản phụ khoa, từng tu nghiệp tại Pháp. Nhiều công trình nghiên cứu quốc tế',
  },
  {
    name: 'GS.TS Lê Hoàng Minh',
    specialty: 'Ngoại tổng quát',
    experience: '30 năm kinh nghiệm',
    image: '/images/doctors/doctor3.png',
    desc: 'Giáo sư đầu ngành ngoại khoa. Đã thực hiện hàng nghìn ca phẫu thuật thành công',
  },
  {
    name: 'ThS.BS Phạm Thùy Linh',
    specialty: 'Nhi khoa',
    experience: '15 năm kinh nghiệm',
    image: '/images/doctors/doctor1.png',
    desc: 'Chuyên gia Nhi khoa, tốt nghiệp Đại học Y Hà Nội. Yêu thương và tận tâm với bệnh nhi',
  },
]

/* ========== DEPARTMENTS ========== */
const departments = [
  { icon: 'bi-heart-pulse', name: 'Tim mạch', desc: 'Chẩn đoán & điều trị các bệnh lý tim mạch, huyết áp, rối loạn nhịp tim', color: 'bg-red-50 text-red-600' },
  { icon: 'bi-clipboard2-pulse', name: 'Nội tổng quát', desc: 'Khám tổng quát, điều trị bệnh nội khoa, quản lý bệnh mãn tính', color: 'bg-blue-50 text-blue-600' },
  { icon: 'bi-bandaid', name: 'Ngoại khoa', desc: 'Phẫu thuật tổng quát, phẫu thuật nội soi, vi phẫu và chấn thương chỉnh hình', color: 'bg-green-50 text-green-600' },
  { icon: 'bi-gender-female', name: 'Sản phụ khoa', desc: 'Chăm sóc thai sản, sanh nở an toàn, điều trị bệnh phụ khoa', color: 'bg-pink-50 text-pink-600' },
  { icon: 'bi-emoji-smile', name: 'Nhi khoa', desc: 'Khám và điều trị bệnh cho trẻ em, tiêm chủng, dinh dưỡng trẻ', color: 'bg-yellow-50 text-yellow-600' },
  { icon: 'bi-eye', name: 'Mắt', desc: 'Khám mắt tổng quát, phẫu thuật Lasik, điều trị bệnh lý về mắt', color: 'bg-purple-50 text-purple-600' },
]

/* ========== NEWS ========== */
const news = [
  {
    id: 1,
    image: '/images/banners/banner1.png',
    title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
    summary: 'Nhằm hưởng ứng tháng hành động vì người cao tuổi, bệnh viện tổ chức chương trình khám miễn phí cho bệnh nhân trên 60 tuổi.',
    date: '10/06/2026',
    category: 'Sự kiện',
  },
  {
    id: 2,
    image: '/images/banners/banner2.png',
    title: 'Ứng dụng kỹ thuật phẫu thuật nội soi tiên tiến trong điều trị bệnh tim',
    summary: 'Lần đầu tiên tại Việt Nam, bệnh viện áp dụng thành công kỹ thuật phẫu thuật nội soi robot trong lĩnh vực tim mạch.',
    date: '05/06/2026',
    category: 'Y khoa',
  },
  {
    id: 3,
    image: '/images/banners/banner3.png',
    title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
    summary: 'Hệ thống đặt lịch khám trực tuyến giúp bệnh nhân tiết kiệm thời gian chờ đợi, chủ động lựa chọn bác sĩ và khung giờ phù hợp.',
    date: '01/06/2026',
    category: 'Hướng dẫn',
  },
]

/* ========== BOOKING FORM ========== */
const form = ref({
  fullName: '',
  phone: '',
  email: '',
  department: '',
  doctor: '',
  date: '',
  symptoms: '',
})

const departmentOptions = [
  'Tim mạch', 'Nội tổng quát', 'Ngoại khoa',
  'Sản phụ khoa', 'Nhi khoa', 'Mắt',
]

function submitBooking() {
  alert('Đặt lịch khám thành công! Chúng tôi sẽ liên hệ xác nhận với bạn sớm nhất.')
  form.value = { fullName: '', phone: '', email: '', department: '', doctor: '', date: '', symptoms: '' }
}

/* ========== SCROLL ANIMATION ========== */
const visibleSections = ref(new Set())

onMounted(() => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          visibleSections.value.add(entry.target.id)
        }
      })
    },
    { threshold: 0.1 }
  )
  document.querySelectorAll('[data-animate]').forEach((el) => observer.observe(el))
})
</script>

<template>
  <div>
    <!-- ==================== HERO BANNER ==================== -->
    <section id="hero-banner" class="relative overflow-hidden">
      <div class="carousel-container">
        <div
          class="carousel-track"
          :style="{ transform: `translateX(-${currentSlide * 100}%)` }"
        >
          <div
            v-for="(slide, index) in slides"
            :key="index"
            class="carousel-slide"
          >
            <img
              :src="slide.image"
              :alt="slide.title"
              class="w-full h-[500px] md:h-[560px] object-cover"
            />
            <!-- Overlay -->
            <div class="absolute inset-0 bg-gradient-to-r from-primary-900/80 via-primary-800/50 to-transparent"></div>
            <!-- Content -->
            <div class="absolute inset-0 flex items-center">
              <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 w-full">
                <div class="max-w-2xl">
                  <h2
                    class="text-3xl md:text-5xl font-bold text-white leading-tight mb-4"
                    :class="currentSlide === index ? 'animate-fade-in-up' : ''"
                  >
                    {{ slide.title }}
                  </h2>
                  <p
                    class="text-lg md:text-xl text-blue-100 mb-8 leading-relaxed"
                    :class="currentSlide === index ? 'animate-fade-in-up delay-200' : ''"
                  >
                    {{ slide.subtitle }}
                  </p>
                  <div
                    class="flex flex-wrap gap-4"
                    :class="currentSlide === index ? 'animate-fade-in-up delay-300' : ''"
                  >
                    <a href="#booking-form" class="btn-primary !bg-white !text-primary-700 hover:!bg-blue-50 !shadow-lg">
                      <i class="bi bi-calendar-check"></i>
                      Đặt lịch khám ngay
                    </a>
                    <a href="tel:19001234" class="btn-outline !border-white !text-white hover:!bg-white/20 hover:!text-white">
                      <i class="bi bi-telephone"></i>
                      1900 1234
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Carousel Controls -->
        <button
          @click="prevSlide"
          class="absolute left-4 top-1/2 -translate-y-1/2 w-12 h-12 rounded-full bg-white/20 backdrop-blur-sm text-white hover:bg-white/40 transition-all duration-300 flex items-center justify-center"
          aria-label="Ảnh trước"
        >
          <i class="bi bi-chevron-left text-xl"></i>
        </button>
        <button
          @click="nextSlide"
          class="absolute right-4 top-1/2 -translate-y-1/2 w-12 h-12 rounded-full bg-white/20 backdrop-blur-sm text-white hover:bg-white/40 transition-all duration-300 flex items-center justify-center"
          aria-label="Ảnh tiếp"
        >
          <i class="bi bi-chevron-right text-xl"></i>
        </button>

        <!-- Dots -->
        <div class="absolute bottom-6 left-1/2 -translate-x-1/2 flex gap-2.5">
          <button
            v-for="(_, index) in slides"
            :key="index"
            @click="goToSlide(index)"
            class="h-2.5 rounded-full transition-all duration-300"
            :class="currentSlide === index
              ? 'w-8 bg-white'
              : 'w-2.5 bg-white/50 hover:bg-white/70'"
            :aria-label="`Chuyển đến ảnh ${index + 1}`"
          ></button>
        </div>
      </div>
    </section>

    <!-- ==================== WHY CHOOSE US ==================== -->
    <section
      id="why-choose-us"
      data-animate
      class="py-16 md:py-24 bg-white"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-14">
          <span class="inline-block px-4 py-1.5 bg-primary-50 text-primary-700 text-sm font-semibold rounded-full mb-4">
            <i class="bi bi-star-fill mr-1"></i> Vì sao chọn chúng tôi
          </span>
          <h2 class="section-title">Vì Sao Chọn Bệnh Viện Đa Khoa?</h2>
          <p class="section-subtitle">
            Chúng tôi cam kết mang đến dịch vụ y tế chất lượng cao nhất, vì sức khỏe của bạn và gia đình
          </p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 lg:gap-8">
          <div
            v-for="(reason, index) in reasons"
            :key="index"
            class="card-hover group bg-white rounded-2xl border border-gray-100 p-7 text-center"
            :class="visibleSections.has('why-choose-us') ? 'animate-fade-in-up' : 'opacity-0'"
            :style="{ animationDelay: `${index * 0.15}s` }"
          >
            <div
              class="w-16 h-16 mx-auto mb-5 rounded-2xl bg-gradient-to-br flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300"
              :class="reason.color"
            >
              <i :class="'bi ' + reason.icon" class="text-2xl text-white"></i>
            </div>
            <h3 class="text-lg font-bold text-gray-800 mb-3">{{ reason.title }}</h3>
            <p class="text-sm text-gray-500 leading-relaxed">{{ reason.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ==================== DOCTORS ==================== -->
    <section
      id="doctors-section"
      data-animate
      class="py-16 md:py-24 bg-gradient-to-b from-primary-50/50 to-white"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-14">
          <span class="inline-block px-4 py-1.5 bg-primary-100 text-primary-700 text-sm font-semibold rounded-full mb-4">
            <i class="bi bi-person-badge mr-1"></i> Đội ngũ bác sĩ
          </span>
          <h2 class="section-title">Bác Sĩ Đầu Ngành</h2>
          <p class="section-subtitle">
            Đội ngũ bác sĩ giỏi, giàu kinh nghiệm, luôn sẵn sàng đồng hành cùng bạn trong hành trình bảo vệ sức khỏe
          </p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 lg:gap-8">
          <div
            v-for="(doctor, index) in doctors"
            :key="index"
            class="card-hover group bg-white rounded-2xl overflow-hidden border border-gray-100"
            :class="visibleSections.has('doctors-section') ? 'animate-fade-in-up' : 'opacity-0'"
            :style="{ animationDelay: `${index * 0.15}s` }"
          >
            <!-- Doctor Image -->
            <div class="relative overflow-hidden h-64">
              <img
                :src="doctor.image"
                :alt="doctor.name"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-primary-900/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-end p-4">
                <p class="text-white text-sm leading-relaxed">{{ doctor.desc }}</p>
              </div>
              <!-- Specialty Badge -->
              <div class="absolute top-3 right-3 px-3 py-1 bg-white/90 backdrop-blur-sm rounded-full text-xs font-semibold text-primary-700">
                {{ doctor.specialty }}
              </div>
            </div>
            <!-- Doctor Info -->
            <div class="p-5">
              <h3 class="text-base font-bold text-gray-800 mb-1">{{ doctor.name }}</h3>
              <p class="text-sm text-primary-600 font-medium mb-1">
                <i class="bi bi-briefcase mr-1"></i>{{ doctor.specialty }}
              </p>
              <p class="text-xs text-gray-400">
                <i class="bi bi-clock mr-1"></i>{{ doctor.experience }}
              </p>
            </div>
          </div>
        </div>

        <div class="text-center mt-10">
          <RouterLink to="/bac-si" class="btn-outline">
            <i class="bi bi-arrow-right"></i>
            Xem tất cả bác sĩ
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ==================== DEPARTMENTS ==================== -->
    <section
      id="departments-section"
      data-animate
      class="py-16 md:py-24 bg-white"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-14">
          <span class="inline-block px-4 py-1.5 bg-primary-50 text-primary-700 text-sm font-semibold rounded-full mb-4">
            <i class="bi bi-building mr-1"></i> Chuyên khoa
          </span>
          <h2 class="section-title">Các Khoa Tiêu Biểu</h2>
          <p class="section-subtitle">
            Đa dạng chuyên khoa, đáp ứng mọi nhu cầu chăm sóc sức khỏe của bạn và gia đình
          </p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="(dept, index) in departments"
            :key="index"
            class="card-hover group flex items-start gap-5 bg-white rounded-2xl border border-gray-100 p-6"
            :class="visibleSections.has('departments-section') ? 'animate-fade-in-up' : 'opacity-0'"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div
              class="w-14 h-14 rounded-xl flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300"
              :class="dept.color"
            >
              <i :class="'bi ' + dept.icon" class="text-2xl"></i>
            </div>
            <div>
              <h3 class="text-lg font-bold text-gray-800 mb-2">{{ dept.name }}</h3>
              <p class="text-sm text-gray-500 leading-relaxed">{{ dept.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ==================== NEWS ==================== -->
    <section
      id="news-section"
      data-animate
      class="py-16 md:py-24 bg-gradient-to-b from-gray-50 to-white"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-14">
          <span class="inline-block px-4 py-1.5 bg-primary-50 text-primary-700 text-sm font-semibold rounded-full mb-4">
            <i class="bi bi-newspaper mr-1"></i> Tin tức
          </span>
          <h2 class="section-title">Tin Tức & Sự Kiện</h2>
          <p class="section-subtitle">
            Cập nhật những tin tức y khoa mới nhất, sự kiện và hoạt động của bệnh viện
          </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8">
          <div
            v-for="(item, index) in news"
            :key="item.id"
            class="card-hover group bg-white rounded-2xl overflow-hidden border border-gray-100"
            :class="visibleSections.has('news-section') ? 'animate-fade-in-up' : 'opacity-0'"
            :style="{ animationDelay: `${index * 0.15}s` }"
          >
            <div class="relative overflow-hidden h-48">
              <img
                :src="item.image"
                :alt="item.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
              />
              <div class="absolute top-3 left-3 px-3 py-1 bg-primary-700 text-white text-xs font-semibold rounded-full">
                {{ item.category }}
              </div>
            </div>
            <div class="p-5">
              <p class="text-xs text-gray-400 mb-2 flex items-center gap-1">
                <i class="bi bi-calendar3"></i> {{ item.date }}
              </p>
              <h3 class="text-base font-bold text-gray-800 mb-2 line-clamp-2 group-hover:text-primary-700 transition-colors">
                {{ item.title }}
              </h3>
              <p class="text-sm text-gray-500 leading-relaxed line-clamp-3 mb-4">{{ item.summary }}</p>
              <RouterLink
                :to="`/tin-tuc/${item.id}`"
                class="inline-flex items-center gap-1.5 text-primary-700 text-sm font-semibold hover:gap-3 transition-all duration-300"
              >
                Đọc thêm <i class="bi bi-arrow-right"></i>
              </RouterLink>
            </div>
          </div>
        </div>

        <div class="text-center mt-10">
          <RouterLink to="/tin-tuc" class="btn-outline">
            <i class="bi bi-arrow-right"></i>
            Xem tất cả tin tức
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ==================== BOOKING FORM ==================== -->
    <section
      id="booking-form"
      data-animate
      class="py-16 md:py-24 bg-gradient-to-br from-primary-50 via-blue-50 to-primary-100"
    >
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <span class="inline-block px-4 py-1.5 bg-white text-primary-700 text-sm font-semibold rounded-full mb-4 shadow-sm">
            <i class="bi bi-calendar2-check mr-1"></i> Đặt lịch khám
          </span>
          <h2 class="section-title">Đặt Lịch Khám Trực Tuyến</h2>
          <p class="section-subtitle">
            Điền thông tin bên dưới để đặt lịch khám. Chúng tôi sẽ liên hệ xác nhận trong thời gian sớm nhất.
          </p>
        </div>

        <form
          @submit.prevent="submitBooking"
          class="bg-white rounded-3xl shadow-xl p-8 md:p-10 border border-gray-100"
          :class="visibleSections.has('booking-form') ? 'animate-fade-in-up' : 'opacity-0'"
        >
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <!-- Họ tên -->
            <div>
              <label for="fullName" class="block text-sm font-semibold text-gray-700 mb-2">
                <i class="bi bi-person mr-1 text-primary-600"></i> Họ và tên <span class="text-red-500">*</span>
              </label>
              <input
                id="fullName"
                v-model="form.fullName"
                type="text"
                required
                placeholder="Nhập họ và tên đầy đủ"
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                       focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                       transition-all duration-200 placeholder:text-gray-400"
              />
            </div>

            <!-- Số điện thoại -->
            <div>
              <label for="phone" class="block text-sm font-semibold text-gray-700 mb-2">
                <i class="bi bi-telephone mr-1 text-primary-600"></i> Số điện thoại <span class="text-red-500">*</span>
              </label>
              <input
                id="phone"
                v-model="form.phone"
                type="tel"
                required
                placeholder="VD: 0901 234 567"
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                       focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                       transition-all duration-200 placeholder:text-gray-400"
              />
            </div>

            <!-- Email -->
            <div>
              <label for="email" class="block text-sm font-semibold text-gray-700 mb-2">
                <i class="bi bi-envelope mr-1 text-primary-600"></i> Email
              </label>
              <input
                id="email"
                v-model="form.email"
                type="email"
                placeholder="VD: email@example.com"
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                       focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                       transition-all duration-200 placeholder:text-gray-400"
              />
            </div>

            <!-- Chọn khoa -->
            <div>
              <label for="department" class="block text-sm font-semibold text-gray-700 mb-2">
                <i class="bi bi-building mr-1 text-primary-600"></i> Chọn chuyên khoa <span class="text-red-500">*</span>
              </label>
              <select
                id="department"
                v-model="form.department"
                required
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                       focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                       transition-all duration-200 text-gray-700 appearance-none"
                style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-repeat: no-repeat; background-position: right 16px center;"
              >
                <option value="" disabled>-- Chọn chuyên khoa --</option>
                <option v-for="d in departmentOptions" :key="d" :value="d">{{ d }}</option>
              </select>
            </div>

            <!-- Ngày khám -->
            <div>
              <label for="date" class="block text-sm font-semibold text-gray-700 mb-2">
                <i class="bi bi-calendar-date mr-1 text-primary-600"></i> Ngày khám mong muốn <span class="text-red-500">*</span>
              </label>
              <input
                id="date"
                v-model="form.date"
                type="date"
                required
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                       focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                       transition-all duration-200 text-gray-700"
              />
            </div>

            <!-- Bác sĩ -->
            <div>
              <label for="doctor" class="block text-sm font-semibold text-gray-700 mb-2">
                <i class="bi bi-person-badge mr-1 text-primary-600"></i> Chọn bác sĩ (tuỳ chọn)
              </label>
              <select
                id="doctor"
                v-model="form.doctor"
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                       focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                       transition-all duration-200 text-gray-700 appearance-none"
                style="background-image: url('data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%2712%27 height=%2712%27 viewBox=%270 0 12 12%27%3E%3Cpath fill=%27%236b7280%27 d=%27M2 4l4 4 4-4%27/%3E%3C/svg%3E'); background-repeat: no-repeat; background-position: right 16px center;"
              >
                <option value="">-- Bác sĩ bất kỳ --</option>
                <option v-for="doc in doctors" :key="doc.name" :value="doc.name">
                  {{ doc.name }} — {{ doc.specialty }}
                </option>
              </select>
            </div>
          </div>

          <!-- Triệu chứng -->
          <div class="mt-5">
            <label for="symptoms" class="block text-sm font-semibold text-gray-700 mb-2">
              <i class="bi bi-chat-text mr-1 text-primary-600"></i> Triệu chứng / Lý do khám
            </label>
            <textarea
              id="symptoms"
              v-model="form.symptoms"
              rows="4"
              placeholder="Mô tả triệu chứng hoặc lý do bạn muốn khám..."
              class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-base
                     focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-500
                     transition-all duration-200 placeholder:text-gray-400 resize-none"
            ></textarea>
          </div>

          <!-- Submit -->
          <div class="mt-8 text-center">
            <button
              type="submit"
              class="btn-primary !px-10 !py-4 !text-lg !rounded-2xl group"
            >
              <i class="bi bi-send group-hover:translate-x-1 transition-transform duration-300"></i>
              Đặt lịch khám
            </button>
            <p class="text-xs text-gray-400 mt-3">
              <i class="bi bi-shield-check mr-1"></i>
              Thông tin của bạn được bảo mật tuyệt đối
            </p>
          </div>
        </form>
      </div>
    </section>

    <!-- ==================== CTA STRIP ==================== -->
    <section class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 py-10">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col md:flex-row items-center justify-between gap-6">
          <div class="text-center md:text-left">
            <h3 class="text-2xl md:text-3xl font-bold text-white mb-2">Cần tư vấn? Liên hệ ngay!</h3>
            <p class="text-blue-100 text-base">Đội ngũ hỗ trợ sẵn sàng giải đáp mọi thắc mắc của bạn 24/7</p>
          </div>
          <div class="flex flex-wrap gap-4">
            <a
              href="tel:19001234"
              class="inline-flex items-center gap-2 bg-white text-primary-700 font-bold px-7 py-3.5 rounded-xl hover:bg-blue-50 transition-all duration-300 shadow-lg hover:shadow-xl text-base"
            >
              <i class="bi bi-telephone-fill"></i>
              1900 1234
            </a>
            <a
              href="#booking-form"
              class="inline-flex items-center gap-2 border-2 border-white text-white font-semibold px-7 py-3.5 rounded-xl hover:bg-white/15 transition-all duration-300 text-base"
            >
              <i class="bi bi-calendar-check"></i>
              Đặt lịch khám
            </a>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
