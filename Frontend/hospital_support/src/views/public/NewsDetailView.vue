<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const fontSizeLevel = ref(0) 

const fontSizeClass = computed(() => {
  if (fontSizeLevel.value === 1) return 'text-xl md:text-2xl leading-relaxed'
  if (fontSizeLevel.value === 2) return 'text-2xl md:text-3xl leading-loose'
  return 'text-lg md:text-xl leading-relaxed'
})

const articles = ref([])

onMounted(() => {
  loadArticles()
})

function loadArticles() {
  const data = localStorage.getItem('hospitalNews')
  if (data) {
    articles.value = JSON.parse(data)
  } else {
    const defaultNews = [
      {
        id: 1,
        image: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
        title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
        category: 'Sự kiện',
        author: 'Phòng Truyền Thông',
        date: '10/06/2026',
        content: `<h3>Chương trình ý nghĩa vì cộng đồng</h3><p>Nhằm hưởng ứng tháng hành động vì người cao tuổi Việt Nam và mong muốn đồng hành chăm sóc sức khỏe cộng đồng, Bệnh viện Đa Khoa thông báo triển khai chiến dịch chăm sóc sức khỏe cộng đồng đặc biệt với các nội dung thiết thực:</p><ul><li>Đo đường huyết mao mạch phòng ngừa đái tháo đường.</li><li>Đo điện tâm đồ (ECG) tầm soát bệnh lý tim mạch, huyết áp.</li><li>Khám chuyên khoa Cơ Xương Khớp miễn phí.</li></ul>`
      },
      {
        id: 2,
        image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=600&q=80',
        title: 'Ứng dụng kỹ thuật phẫu thuật nội soi tiên tiến trong điều trị bệnh tim',
        category: 'Y khoa',
        author: 'TS.BS Nguyễn Văn An',
        date: '05/06/2026',
        content: `<h3>Kỷ nguyên mới trong phẫu thuật tim mạch</h3><p>Đội ngũ chuyên khoa Ngoại tim mạch của Bệnh viện đã thực hiện thành công ca phẫu thuật thay van động mạch chủ bằng kỹ thuật phẫu thuật nội soi robot ít xâm lấn. Phương pháp này mang lại nhiều ưu điểm vượt trội như vết mổ nhỏ, hồi phục nhanh và hạn chế biến chứng...</p>`
      },
      {
        id: 3,
        image: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=600&q=80',
        title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
        category: 'Hướng dẫn',
        author: 'Tổ Chăm Sóc Khách Hàng',
        date: '01/06/2026',
        content: `<h3>Các bước đặt lịch khám trực tuyến dễ dàng</h3><p>Bệnh nhân chỉ cần truy cập website, chọn Khoa khám, chọn Khung giờ mong muốn và mô tả triệu chứng để được xếp lịch và nhận số thứ tự ưu tiên khám bệnh mà không cần xếp hàng tại quầy lễ tân...</p>`
      }
    ]
    articles.value = defaultNews
    localStorage.setItem('hospitalNews', JSON.stringify(defaultNews))
  }
}

const currentId = computed(() => parseInt(route.params.id) || 1)
const article = computed(() => {
  return articles.value.find(a => a.id === currentId.value) || articles.value[0] || {}
})

const relatedArticles = computed(() => {
  return articles.value.filter(a => a.id !== currentId.value).slice(0, 2)
})
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <div class="max-w-4xl mx-auto px-4 py-8 animate-fade-in">
      <!-- Back Link -->
      <RouterLink to="/tin-tuc" class="inline-flex items-center gap-2 text-primary-700 font-bold text-lg hover:underline mb-6">
        <i class="bi bi-arrow-left text-xl"></i> Quay lại danh sách tin tức
      </RouterLink>

      <!-- Article Header -->
      <div class="bg-white rounded-3xl p-6 md:p-8 border border-gray-100 shadow-sm space-y-6">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <span class="px-3.5 py-1 bg-primary-100 text-primary-700 font-bold rounded-full text-base">
            {{ article.category }}
          </span>
          
          <!-- Font Size Adjuster Panel (Elderly helper) -->
          <div class="flex items-center gap-2 bg-gray-100 p-1 rounded-xl">
            <span class="text-sm text-gray-500 font-bold px-2">Cỡ chữ:</span>
            <button
              @click="fontSizeLevel = 0"
              class="w-10 h-10 rounded-lg font-bold text-sm transition-all"
              :class="fontSizeLevel === 0 ? 'bg-white text-primary-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
            >
              A
            </button>
            <button
              @click="fontSizeLevel = 1"
              class="w-10 h-10 rounded-lg font-bold text-lg transition-all"
              :class="fontSizeLevel === 1 ? 'bg-white text-primary-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
            >
              A+
            </button>
            <button
              @click="fontSizeLevel = 2"
              class="w-10 h-10 rounded-lg font-bold text-xl transition-all"
              :class="fontSizeLevel === 2 ? 'bg-white text-primary-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
            >
              A++
            </button>
          </div>
        </div>

        <h1 class="text-3xl md:text-4xl font-extrabold text-gray-800 leading-tight">
          {{ article.title }}
        </h1>

        <div class="flex items-center gap-6 text-sm text-gray-400 font-semibold border-b border-gray-100 pb-4">
          <span><i class="bi bi-calendar3 text-primary-600"></i> Ngày: {{ article.date }}</span>
        </div>

        <!-- Article Image -->
        <div class="rounded-2xl overflow-hidden h-[300px] md:h-[450px]">
          <img
            :src="article.image"
            :alt="article.title"
            class="w-full h-full object-cover"
          />
        </div>

        <!-- Article content with dynamic font-size -->
        <div
          class="text-gray-700 space-y-4 pt-4 text-justify ProseMirror"
          :class="fontSizeClass"
          v-html="article.content"
        >
        </div>
      </div>



      <!-- ==================== RELATED ARTICLES ==================== -->
      <div class="mt-12 space-y-6">
        <h3 class="text-2xl font-bold text-gray-800"><i class="bi bi-newspaper text-primary-600 mr-2"></i> Bài viết liên quan:</h3>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <RouterLink
            v-for="item in relatedArticles"
            :key="item.id"
            :to="`/tin-tuc/${item.id}`"
            class="bg-white border border-gray-100 rounded-3xl p-5 shadow-sm hover:shadow-lg transition-all flex gap-4 group"
          >
            <!-- Thumbnail -->
            <div class="w-24 h-24 rounded-2xl bg-gradient-to-br from-primary-100 to-primary-200 overflow-hidden flex-shrink-0">
              <img
                :src="item.image"
                :alt="item.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </div>
            <div class="flex flex-col justify-between">
              <div>
                <span class="text-xs text-primary-700 font-bold">{{ item.category }}</span>
                <h4 class="text-base font-bold text-gray-800 mt-1 leading-snug line-clamp-2 group-hover:text-primary-700 transition-colors">
                  {{ item.title }}
                </h4>
              </div>
              <span class="text-xs text-gray-400 font-semibold">{{ item.date }}</span>
            </div>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
