<script setup>
import { ref, computed } from 'vue'

const searchQuery = ref('')
const selectedCategory = ref('ALL')

const categories = ['ALL', 'Sự kiện', 'Y khoa', 'Hướng dẫn', 'Dinh dưỡng']

const newsArticles = [
  {
    id: 1,
    image: '/images/banners/banner1.png',
    title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
    summary: 'Nhằm hưởng ứng tháng hành động vì người cao tuổi, bệnh viện tổ chức chương trình khám miễn phí cho bệnh nhân trên 60 tuổi, bao gồm đo đường huyết, điện tim và tư vấn dinh dưỡng xương khớp.',
    date: '10/06/2026',
    category: 'Sự kiện',
    author: 'Phòng Truyền Thông'
  },
  {
    id: 2,
    image: '/images/banners/banner2.png',
    title: 'Ứng dụng kỹ thuật phẫu thuật nội sơ tiên tiến trong điều trị bệnh tim',
    summary: 'Lần đầu tiên tại Việt Nam, bệnh viện áp dụng thành công kỹ thuật phẫu thuật nội soi robot trong lĩnh vực tim mạch cho bệnh nhân cao tuổi bị hẹp van động mạch chủ.',
    date: '05/06/2026',
    category: 'Y khoa',
    author: 'TS.BS Nguyễn Văn An'
  },
  {
    id: 3,
    image: '/images/banners/banner3.png',
    title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
    summary: 'Hệ thống đặt lịch khám trực tuyến giúp bệnh nhân tiết kiệm thời gian chờ đợi, chủ động lựa chọn bác sĩ và khung giờ phù hợp ngay tại nhà thông qua máy tính hoặc điện thoại.',
    date: '01/06/2026',
    category: 'Hướng dẫn',
    author: 'Tổ Chăm Sóc Khách Hàng'
  },
  {
    id: 4,
    image: '/images/banners/banner1.png',
    title: 'Chế độ dinh dưỡng phòng ngừa loãng xương cho người trên 60 tuổi',
    summary: 'Những lời khuyên hữu ích từ các chuyên gia dinh dưỡng về thực đơn hằng ngày, lượng canxi cần thiết và các bài tập vận động nhẹ nhàng giúp người cao tuổi có hệ xương chắc khỏe.',
    date: '28/05/2026',
    category: 'Dinh dưỡng',
    author: 'ThS.BS Trần Văn Bình'
  },
  {
    id: 5,
    image: '/images/banners/banner2.png',
    title: 'Thông báo về việc tăng cường đo huyết áp miễn phí tại Quầy Tiếp Đón',
    summary: 'Để giúp người dân phòng ngừa đột quỵ do tăng huyết áp đột ngột trong những ngày nắng nóng, bệnh viện tổ chức đo huyết áp miễn phí cho tất cả khách đến thăm và khám bệnh.',
    date: '20/05/2026',
    category: 'Sự kiện',
    author: 'Phòng Hành Chính'
  }
]

const filteredArticles = computed(() => {
  return newsArticles.filter(item => {
    const matchesSearch = item.title.toLowerCase().includes(searchQuery.value.toLowerCase()) || item.summary.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesCategory = selectedCategory.value === 'ALL' || item.category === selectedCategory.value
    return matchesSearch && matchesCategory
  })
})
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12 md:py-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Tin Tức & Hoạt Động Y Khoa</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Cập nhật các chương trình khám miễn phí, kiến thức sức khỏe bổ ích và các hướng dẫn điều trị y tế mới nhất.
        </p>
      </div>
    </div>

    <!-- ==================== CONTROLS ==================== -->
    <div class="max-w-7xl mx-auto px-4 mt-8">
      <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 flex flex-col md:flex-row justify-between items-center gap-4 animate-fade-in">
        <!-- Search -->
        <div class="relative w-full md:max-w-md">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm bài viết..."
            class="w-full pl-12 pr-4 py-3 text-lg rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 placeholder:text-gray-400"
          />
          <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-lg text-gray-400"></i>
        </div>

        <!-- Category Filters -->
        <div class="flex gap-2 overflow-x-auto w-full md:w-auto pb-1 md:pb-0">
          <button
            v-for="cat in categories"
            :key="cat"
            @click="selectedCategory = cat"
            class="px-5 py-2.5 rounded-xl text-base font-bold border transition-all flex-shrink-0"
            :class="selectedCategory === cat
              ? 'bg-primary-700 text-white border-primary-700 shadow-sm'
              : 'bg-white text-gray-600 border-gray-200 hover:border-primary-400 hover:text-primary-700'"
          >
            {{ cat === 'ALL' ? 'Tất cả' : cat }}
          </button>
        </div>
      </div>

      <!-- ==================== ARTICLES LIST ==================== -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mt-8 animate-fade-in-up">
        <div
          v-for="item in filteredArticles"
          :key="item.id"
          class="bg-white border border-gray-100 rounded-3xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col justify-between group"
        >
          <div>
            <div class="relative h-56 bg-gradient-to-br from-primary-100 to-primary-200 overflow-hidden">
              <img
                :src="item.image"
                :alt="item.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
              />
              <span class="absolute top-4 left-4 px-3.5 py-1 bg-primary-700 text-white text-sm font-bold rounded-full shadow-md">
                {{ item.category }}
              </span>
            </div>
            
            <div class="p-6">
              <div class="flex items-center gap-4 text-xs text-gray-400 mb-3 font-semibold">
                <span><i class="bi bi-calendar3 text-primary-600"></i> {{ item.date }}</span>
                <span><i class="bi bi-person text-primary-600"></i> Đăng bởi: {{ item.author }}</span>
              </div>
              <h3 class="text-xl font-bold text-gray-800 leading-snug mb-3 group-hover:text-primary-700 transition-colors line-clamp-2">
                {{ item.title }}
              </h3>
              <p class="text-base text-gray-500 leading-relaxed line-clamp-3">
                {{ item.summary }}
              </p>
            </div>
          </div>

          <div class="px-6 pb-6 pt-2">
            <RouterLink
              :to="`/tin-tuc/${item.id}`"
              class="w-full py-3 border-2 border-primary-700 text-primary-700 font-bold text-base rounded-xl hover:bg-primary-700 hover:text-white transition-all flex items-center justify-center gap-2"
            >
              Đọc bài viết <i class="bi bi-arrow-right"></i>
            </RouterLink>
          </div>
        </div>
      </div>

      <div v-if="filteredArticles.length === 0" class="text-center py-16 text-gray-400 text-lg">
        Không tìm thấy bài viết nào phù hợp.
      </div>
    </div>
  </div>
</template>
