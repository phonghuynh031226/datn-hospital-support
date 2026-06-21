<script setup>
import { ref, computed, onMounted } from 'vue'

const searchQuery = ref('')
const selectedCategory = ref('ALL')

const categories = ['ALL', 'Sự kiện', 'Y khoa', 'Hướng dẫn', 'Dinh dưỡng']

const newsArticles = ref([])

onMounted(() => {
  loadNews()
})

function loadNews() {
  const data = localStorage.getItem('hospitalNews')
  if (data) {
    newsArticles.value = JSON.parse(data)
  } else {
    const defaultNews = [
      {
        id: 1,
        image: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
        title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
        summary: 'Nhằm hưởng ứng tháng hành động vì người cao tuổi, bệnh viện tổ chức chương trình khám miễn phí cho bệnh nhân trên 60 tuổi, bao gồm đo đường huyết, điện tim và tư vấn dinh dưỡng xương khớp.',
        date: '10/06/2026',
        category: 'Sự kiện',
        author: 'Phòng Truyền Thông'
      },
      {
        id: 2,
        image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=600&q=80',
        title: 'Ứng dụng kỹ thuật phẫu thuật nội soi tiên tiến trong điều trị bệnh tim',
        summary: 'Lần đầu tiên tại Việt Nam, bệnh viện áp dụng thành công kỹ thuật phẫu thuật nội soi robot trong lĩnh vực tim mạch cho bệnh nhân cao tuổi bị hẹp van động mạch chủ.',
        date: '05/06/2026',
        category: 'Y khoa',
        author: 'TS.BS Nguyễn Văn An'
      },
      {
        id: 3,
        image: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=600&q=80',
        title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
        summary: 'Hệ thống đặt lịch khám trực tuyến giúp bệnh nhân tiết kiệm thời gian chờ đợi, chủ động lựa chọn bác sĩ và khung giờ phù hợp ngay tại nhà thông qua máy tính hoặc điện thoại.',
        date: '01/06/2026',
        category: 'Hướng dẫn',
        author: 'Tổ Chăm Sóc Khách Hàng'
      },
      {
        id: 4,
        image: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
        title: 'Chế độ dinh dưỡng phòng ngừa loãng xương cho người trên 60 tuổi',
        summary: 'Những lời khuyên hữu ích từ các chuyên gia dinh dưỡng về thực đơn hằng ngày, lượng canxi cần thiết và các bài tập vận động nhẹ nhàng giúp người cao tuổi có hệ xương chắc khỏe.',
        date: '28/05/2026',
        category: 'Dinh dưỡng',
        author: 'ThS.BS Trần Văn Bình'
      },
      {
        id: 5,
        image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=600&q=80',
        title: 'Thông báo về việc tăng cường đo huyết áp miễn phí tại Quầy Tiếp Đón',
        summary: 'Để giúp người dân phòng ngừa đột quỵ do tăng huyết áp đột ngột trong những ngày nắng nóng, bệnh viện tổ chức đo huyết áp miễn phí cho tất cả khách đến thăm và khám bệnh.',
        date: '20/05/2026',
        category: 'Sự kiện',
        author: 'Phòng Hành Chính'
      }
    ]
    newsArticles.value = defaultNews
    localStorage.setItem('hospitalNews', JSON.stringify(defaultNews))
  }
}

const filteredArticles = computed(() => {
  return newsArticles.value.filter(item => {
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

    <!-- ==================== MAIN SECTION ==================== -->
    <div class="max-w-7xl mx-auto px-4 mt-8">
      <div class="grid grid-cols-12 gap-6 md:gap-8">
        
        <!-- Left Sidebar: Category Menu (2 columns on large screen) -->
        <div class="col-span-12 lg:col-span-2">
          <div class="bg-white p-4 rounded-3xl shadow-sm border border-gray-100 sticky top-28">
            <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-4 px-2">Danh mục</h3>
            <div class="flex flex-row lg:flex-col gap-1.5 overflow-x-auto lg:overflow-x-visible pb-2 lg:pb-0">
              <button
                v-for="cat in categories"
                :key="cat"
                @click="selectedCategory = cat"
                class="px-4 py-2.5 rounded-xl text-sm font-bold transition-all text-left flex-shrink-0 flex items-center justify-between"
                :class="selectedCategory === cat
                  ? 'bg-primary-50 text-primary-700 shadow-sm border border-primary-100 font-extrabold'
                  : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900 border border-transparent'"
              >
                <span>{{ cat === 'ALL' ? 'Tất cả' : cat }}</span>
                <i v-if="selectedCategory === cat" class="bi bi-chevron-right text-primary-600 hidden lg:inline-block"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- Right Content: Search & News Articles Grid (10 columns on large screen) -->
        <div class="col-span-12 lg:col-span-10 space-y-6">
          
          <!-- Search box -->
          <div class="bg-white p-4 rounded-3xl shadow-sm border border-gray-100 flex items-center animate-fade-in">
            <div class="relative w-full max-w-xl">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Tìm kiếm bài viết..."
                class="w-full pl-12 pr-4 py-3 text-base rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
              />
              <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-lg text-gray-400"></i>
            </div>
          </div>

          <!-- Articles grid -->
          <div v-if="filteredArticles.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 animate-fade-in-up">
            <div
              v-for="item in filteredArticles"
              :key="item.id"
              class="bg-white border border-gray-100 rounded-3xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col justify-between group"
            >
              <div>
                <div class="relative h-48 bg-gradient-to-br from-primary-100 to-primary-200 overflow-hidden">
                  <img
                    :src="item.image"
                    :alt="item.title"
                    class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <span class="absolute top-4 left-4 px-3 py-0.5 bg-primary-700 text-white text-xs font-bold rounded-full shadow-md">
                    {{ item.category }}
                  </span>
                </div>
                
                <div class="p-5">
                  <div class="flex items-center gap-3 text-[11px] text-gray-400 mb-2 font-semibold">
                    <span><i class="bi bi-calendar3 text-primary-600"></i> {{ item.date }}</span>
                    <span><i class="bi bi-person text-primary-600"></i> {{ item.author }}</span>
                  </div>
                  <h3 class="text-base font-bold text-gray-800 leading-snug mb-2 group-hover:text-primary-700 transition-colors line-clamp-2">
                    {{ item.title }}
                  </h3>
                  <p class="text-xs text-gray-500 leading-relaxed line-clamp-3">
                    {{ item.summary }}
                  </p>
                </div>
              </div>

              <div class="px-5 pb-5 pt-2">
                <RouterLink
                  :to="`/tin-tuc/${item.id}`"
                  class="w-full py-2.5 border-2 border-primary-700 text-primary-700 font-bold text-sm rounded-xl hover:bg-primary-700 hover:text-white transition-all flex items-center justify-center gap-2"
                >
                  Đọc bài viết <i class="bi bi-arrow-right"></i>
                </RouterLink>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-16 text-gray-400 text-lg bg-white rounded-3xl border border-gray-100 shadow-sm w-full">
            Không tìm thấy bài viết nào phù hợp.
          </div>

        </div>

      </div>
    </div>
  </div>
</template>
