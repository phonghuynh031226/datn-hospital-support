<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

/* ========== CAROUSEL ========== */
const currentSlide = ref(0)
const slides = ref([
  {
    image: 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&fit=crop&w=1200&q=80',
    title: 'Chào Mừng Đến Với Bệnh Viện Đa Khoa',
    subtitle: 'Hệ thống y tế hiện đại, uy tín hàng đầu — nơi sức khỏe của bạn là ưu tiên số một',
  },
  {
    image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80',
    title: 'Đội Ngũ Bác Sĩ Giỏi & Tận Tâm',
    subtitle: 'Hơn 50 chuyên gia đầu ngành, giàu kinh nghiệm, luôn đồng hành cùng bạn',
  },
  {
    image: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=1200&q=80',
    title: 'Trang Thiết Bị Hiện Đại',
    subtitle: 'Hệ thống máy móc tiên tiến nhất, đảm bảo chẩn đoán chính xác và điều trị hiệu quả',
  },
])
let slideInterval = null

function nextSlide() {
  currentSlide.value = (currentSlide.value + 1) % slides.value.length
}
function prevSlide() {
  currentSlide.value = (currentSlide.value - 1 + slides.value.length) % slides.value.length
}
function goToSlide(index) {
  currentSlide.value = index
}

onMounted(() => {
  slideInterval = setInterval(nextSlide, 5000)
  loadSystemConfig()
  loadNews()
  loadDoctors()
  updateDisplayCount()
  window.addEventListener('resize', updateDisplayCount)
  doctorInterval = setInterval(nextDoctor, 4000)
})

onUnmounted(() => {
  if (slideInterval) clearInterval(slideInterval)
  if (doctorInterval) clearInterval(doctorInterval)
  window.removeEventListener('resize', updateDisplayCount)
})

function loadSystemConfig() {
  const data = localStorage.getItem('hospitalConfig')
  if (data) {
    const config = JSON.parse(data)
    if (config.banner1) slides.value[0].image = config.banner1
    if (config.banner2) slides.value[1].image = config.banner2
    if (config.banner3) slides.value[2].image = config.banner3
    if (config.whyChooseUsTitle) whyChooseUsTitle.value = config.whyChooseUsTitle
    if (config.whyChooseUsSubtitle) whyChooseUsSubtitle.value = config.whyChooseUsSubtitle
    if (config.reasons && Array.isArray(config.reasons)) reasons.value = config.reasons
  }
}

function loadNews() {
  const data = localStorage.getItem('hospitalNews')
  const defaultNews = [
    {
      id: 1,
      image: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
      title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
      summary: 'Nhằm hưởng ứng tháng hành động vì người cao tuổi, bệnh viện tổ chức chương trình khám miễn phí cho bệnh nhân trên 60 tuổi, bao gồm đo đường huyết, điện tim và tư vấn dinh dưỡng xương khớp.',
      date: '10/06/2026',
      category: 'Sự kiện'
    },
    {
      id: 2,
      image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=600&q=80',
      title: 'Ứng dụng kỹ thuật phẫu thuật nội soi tiên tiến trong điều trị bệnh tim',
      summary: 'Lần đầu tiên tại Việt Nam, bệnh viện áp dụng thành công kỹ thuật phẫu thuật nội soi robot trong lĩnh vực tim mạch cho bệnh nhân cao tuổi bị hẹp van động mạch chủ.',
      date: '05/06/2026',
      category: 'Y khoa'
    },
    {
      id: 3,
      image: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=600&q=80',
      title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
      summary: 'Hệ thống đặt lịch khám trực tuyến giúp bệnh nhân tiết kiệm thời gian chờ đợi, chủ động lựa chọn bác sĩ và khung giờ phù hợp ngay tại nhà thông qua máy tính hoặc điện thoại.',
      date: '01/06/2026',
      category: 'Hướng dẫn'
    },
    {
      id: 4,
      image: 'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=600&q=80',
      title: 'Chế độ dinh dưỡng khoa học dành cho người bệnh đái tháo đường',
      summary: 'Chế độ ăn uống hợp lý đóng vai trò quyết định giúp kiểm soát tốt chỉ số đường huyết, duy trì năng lượng và hạn chế tối đa các biến chứng nguy hiểm của bệnh tiểu đường.',
      date: '28/05/2026',
      category: 'Dinh dưỡng'
    },
    {
      id: 5,
      image: 'https://images.unsplash.com/photo-1579684389782-64d84b5e901a?auto=format&fit=crop&w=600&q=80',
      title: 'Tầm soát ung thư sớm: Chìa khóa vàng bảo vệ sức khỏe gia đình',
      summary: 'Việc thực hiện kiểm tra sức khỏe và tầm soát ung thư định kỳ giúp phát hiện mầm mống tế bào ác tính ở giai đoạn đầu, nâng cao đáng kể tỷ lệ điều trị thành công lên tới 90%.',
      date: '25/05/2026',
      category: 'Y khoa'
    },
    {
      id: 6,
      image: 'https://images.unsplash.com/photo-1512864084360-7c0c4d0a0845?auto=format&fit=crop&w=600&q=80',
      title: 'Bí quyết bảo vệ đôi mắt khỏe mạnh trong kỷ nguyên số',
      summary: 'Tiếp xúc liên tục với thiết bị điện tử khiến mắt dễ bị khô, mỏi và giảm thị lực. Bác sĩ chuyên khoa khuyên bạn nên áp dụng quy tắc 20-20-20 để bảo vệ mắt.',
      date: '20/05/2026',
      category: 'Y khoa'
    }
  ]
  if (data) {
    news.value = JSON.parse(data).slice(0, 6)
  } else {
    news.value = defaultNews
    localStorage.setItem('hospitalNews', JSON.stringify(defaultNews))
  }
}

/* ========== WHY CHOOSE US ========== */
const whyChooseUsTitle = ref('Vì Sao Chọn Bệnh Viện Đa Khoa?')
const whyChooseUsSubtitle = ref('Chúng tôi cam kết mang đến dịch vụ y tế chất lượng cao nhất, vì sức khỏe của bạn và gia đình')
const reasons = ref([
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
])

/* ========== DOCTORS CAROUSEL ========== */
const doctors = ref([])
const currentDoctorIndex = ref(0)
const displayCount = ref(4)
let doctorInterval = null

function updateDisplayCount() {
  if (window.innerWidth >= 1024) {
    displayCount.value = 4
  } else if (window.innerWidth >= 640) {
    displayCount.value = 2
  } else {
    displayCount.value = 1
  }
}

function loadDoctors() {
  const data = localStorage.getItem('hospitalStaff')
  const defaultDoctors = [
    {
      name: 'PGS.TS Nguyễn Văn An',
      specialty: 'Tim mạch',
      experience: '25 năm kinh nghiệm',
      image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80',
      desc: 'Chuyên gia hàng đầu về tim mạch can thiệp, nguyên Phó Giám đốc Bệnh viện Tim',
    },
    {
      name: 'TS.BS Trần Thị Mai',
      specialty: 'Sản phụ khoa',
      experience: '20 năm kinh nghiệm',
      image: 'https://images.unsplash.com/photo-1594824813573-246434de83fb?auto=format&fit=crop&w=600&q=80',
      desc: 'Chuyên gia sản phụ khoa, từng tu nghiệp tại Pháp. Nhiều công trình nghiên cứu quốc tế',
    },
    {
      name: 'GS.TS Lê Hoàng Minh',
      specialty: 'Ngoại tổng quát',
      experience: '30 năm kinh nghiệm',
      image: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=600&q=80',
      desc: 'Giáo sư đầu ngành ngoại khoa. Đã thực hiện hàng nghìn ca phẫu thuật thành công',
    },
    {
      name: 'ThS.BS Phạm Thùy Linh',
      specialty: 'Nhi khoa',
      experience: '15 năm kinh nghiệm',
      image: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80',
      desc: 'Chuyên gia Nhi khoa, tốt nghiệp Đại học Y Hà Nội. Yêu thương và tận tâm với bệnh nhi',
    }
  ]

  if (data) {
    const staffList = JSON.parse(data)
    const filtered = staffList.filter(s => s.role.includes('Bác sĩ') || s.role === 'doctor')
    if (filtered.length > 0) {
      doctors.value = filtered.map((s, index) => {
        const defaultImages = [
          'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80',
          'https://images.unsplash.com/photo-1594824813573-246434de83fb?auto=format&fit=crop&w=600&q=80',
          'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=600&q=80',
          'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80',
        ]
        return {
          name: s.name,
          specialty: s.specialty || 'Chuyên khoa',
          experience: s.schedule ? s.schedule.split(' ')[0] + ' ' + (s.schedule.includes('Ca') ? s.schedule.split(' ').slice(-3).join(' ') : 'Ca trực') : 'Có lịch hẹn',
          image: s.image || defaultImages[index % defaultImages.length],
          desc: s.bio || 'Bác sĩ chuyên khoa giỏi và tận tâm.'
        }
      })
      return
    }
  }
  doctors.value = defaultDoctors
}

function nextDoctor() {
  if (doctors.value.length <= displayCount.value) return
  const maxIndex = doctors.value.length - displayCount.value
  if (currentDoctorIndex.value >= maxIndex) {
    currentDoctorIndex.value = 0
  } else {
    currentDoctorIndex.value++
  }
  resetDoctorAutoplay()
}

function prevDoctor() {
  if (doctors.value.length <= displayCount.value) return
  const maxIndex = doctors.value.length - displayCount.value
  if (currentDoctorIndex.value <= 0) {
    currentDoctorIndex.value = maxIndex
  } else {
    currentDoctorIndex.value--
  }
  resetDoctorAutoplay()
}

function resetDoctorAutoplay() {
  if (doctorInterval) {
    clearInterval(doctorInterval)
    doctorInterval = setInterval(nextDoctor, 4000)
  }
}

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
const news = ref([])
// Booking form states removed as booking form has its own dedicated page at /dat-lich

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
                    <RouterLink to="/dat-lich" class="btn-primary !bg-white !text-primary-700 hover:!bg-blue-50 !shadow-lg">
                      <i class="bi bi-calendar-check"></i>
                      Đặt lịch khám ngay
                    </RouterLink>
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
      class="pt-16 pb-8 md:pt-20 md:pb-10 bg-white"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-14">
          <span class="inline-block px-4 py-1.5 bg-primary-50 text-primary-700 text-sm font-semibold rounded-full mb-4">
            <i class="bi bi-star-fill mr-1"></i> Vì sao chọn chúng tôi
          </span>
          <h2 class="section-title">{{ whyChooseUsTitle }}</h2>
          <p class="section-subtitle">
            {{ whyChooseUsSubtitle }}
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
      class="pt-8 pb-8 md:pt-10 md:pb-10 bg-gradient-to-b from-primary-50/50 to-white"
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

        <div class="relative px-4 md:px-12" :class="visibleSections.has('doctors-section') ? 'animate-fade-in-up' : 'opacity-0'">
          <!-- Navigation buttons -->
          <button
            @click="prevDoctor"
            class="absolute left-0 top-1/2 -translate-y-1/2 z-10 w-10 h-10 rounded-full bg-white shadow-lg border border-gray-100 text-primary-700 hover:bg-primary-50 transition-all duration-300 flex items-center justify-center disabled:opacity-30 disabled:pointer-events-none"
            :disabled="doctors.length <= displayCount"
            aria-label="Bác sĩ trước"
          >
            <i class="bi bi-chevron-left text-lg"></i>
          </button>
          <button
            @click="nextDoctor"
            class="absolute right-0 top-1/2 -translate-y-1/2 z-10 w-10 h-10 rounded-full bg-white shadow-lg border border-gray-100 text-primary-700 hover:bg-primary-50 transition-all duration-300 flex items-center justify-center disabled:opacity-30 disabled:pointer-events-none"
            :disabled="doctors.length <= displayCount"
            aria-label="Bác sĩ tiếp"
          >
            <i class="bi bi-chevron-right text-lg"></i>
          </button>

          <!-- Carousel Window -->
          <div class="overflow-hidden">
            <div
              class="flex transition-transform duration-500 ease-in-out"
              :style="{ transform: `translateX(-${currentDoctorIndex * (100 / displayCount)}%)` }"
            >
              <div
                v-for="(doctor, index) in doctors"
                :key="index"
                class="w-full sm:w-1/2 lg:w-1/4 p-3 flex-shrink-0 animate-fade-in"
              >
                <div class="card-hover group bg-white rounded-2xl overflow-hidden border border-gray-100 h-full flex flex-col justify-between shadow-sm hover:shadow-md transition-all duration-300">
                  <!-- Doctor Image -->
                  <div class="relative overflow-hidden h-64 flex-shrink-0">
                    <img
                      :src="doctor.image"
                      :alt="doctor.name"
                      class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    />
                    <div class="absolute inset-0 bg-gradient-to-t from-primary-900/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-end p-4">
                      <p class="text-white text-sm leading-relaxed line-clamp-4">{{ doctor.desc }}</p>
                    </div>
                    <!-- Specialty Badge -->
                    <div class="absolute top-3 right-3 px-3 py-1 bg-white/90 backdrop-blur-sm rounded-full text-xs font-semibold text-primary-700">
                      {{ doctor.specialty }}
                    </div>
                  </div>
                  <!-- Doctor Info -->
                  <div class="p-5 flex-grow flex flex-col justify-between">
                    <div>
                      <h3 class="text-base font-bold text-gray-800 mb-1 line-clamp-1">{{ doctor.name }}</h3>
                      <p class="text-sm text-primary-600 font-medium mb-1 line-clamp-1">
                        <i class="bi bi-briefcase mr-1"></i>{{ doctor.specialty }}
                      </p>
                    </div>
                    <p class="text-xs text-gray-400 mt-2 border-t pt-2 border-gray-50 flex items-center gap-1">
                      <i class="bi bi-clock text-primary-500"></i>{{ doctor.experience }}
                    </p>
                  </div>
                </div>
              </div>
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
      class="pt-8 pb-8 md:pt-10 md:pb-10 bg-white"
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
      class="pt-8 pb-8 md:pt-10 md:pb-10 bg-gradient-to-b from-gray-50 to-white"
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

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 lg:gap-8">
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
            <RouterLink
              to="/dat-lich"
              class="inline-flex items-center gap-2 border-2 border-white text-white font-semibold px-7 py-3.5 rounded-xl hover:bg-white/15 transition-all duration-300 text-base"
            >
              <i class="bi bi-calendar-check"></i>
              Đặt lịch khám
            </RouterLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
