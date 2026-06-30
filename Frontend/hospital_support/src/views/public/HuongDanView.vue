<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

const searchQuery = ref('')
const expandedGuide = ref(null)

const guides = ref([])

async function loadGuides() {
  try {
    const res = await axios.get(`http://localhost:8080/api/huong-dan?search=${encodeURIComponent(searchQuery.value)}`)
    guides.value = res.data.map(item => ({
      id: item.id,
      category: item.danhMuc ? item.danhMuc.tenDanhMuc : 'Hướng dẫn',
      icon: 'bi-calendar-check',
      title: item.tieuDe,
      desc: item.tieuDe,
      steps: [
        {
          step: 'Chi tiết',
          title: 'Nội dung hướng dẫn',
          content: item.noiDung
        }
      ]
    }))
  } catch (err) {
    console.error(err)
  }
}

onMounted(() => {
  loadGuides()
})

watch(searchQuery, () => {
  loadGuides()
})

const filteredGuides = computed(() => {
  return guides.value
})

function toggleGuide(id) {
  if (expandedGuide.value === id) {
    expandedGuide.value = null
  } else {
    expandedGuide.value = id
  }
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12 md:py-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Hướng Dẫn Cho Bệnh Nhân</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Giải đáp các câu hỏi thường gặp và hướng dẫn quy trình đặt khám, thanh toán viện phí từng bước dễ hiểu.
        </p>
      </div>
    </div>

    <!-- ==================== SEARCH BAR ==================== -->
    <div class="max-w-3xl mx-auto px-4 mt-8 animate-fade-in">
      <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100">
        <label for="search-guide-input" class="block text-xl font-bold text-gray-700 mb-2">Bác cần hỗ trợ tìm kiếm hướng dẫn gì?</label>
        <div class="relative">
          <input
            id="search-guide-input"
            v-model="searchQuery"
            type="text"
            placeholder="Nhập từ khóa tìm kiếm (Ví dụ: đặt lịch, bảo hiểm y tế, thanh toán...)"
            class="w-full pl-12 pr-4 py-4 text-lg rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-600 transition-all placeholder:text-gray-400"
          />
          <i class="bi bi-search absolute left-4 top-1/2 -translate-y-1/2 text-xl text-gray-400"></i>
        </div>
      </div>
    </div>

    <!-- ==================== GUIDES LIST (ACCORDION) ==================== -->
    <div class="max-w-4xl mx-auto px-4 mt-8 space-y-4 animate-fade-in-up">
      <div
        v-for="guide in filteredGuides"
        :key="guide.id"
        class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden transition-all duration-300"
      >
        <!-- Header -->
        <button
          @click="toggleGuide(guide.id)"
          class="w-full p-6 text-left flex items-center justify-between hover:bg-primary-50/30 transition-colors"
        >
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-2xl bg-primary-100 text-primary-700 flex items-center justify-center flex-shrink-0">
              <i :class="'bi ' + guide.icon" class="text-2xl"></i>
            </div>
            <div>
              <span class="text-xs font-bold text-primary-700 uppercase tracking-wider bg-primary-50 px-2.5 py-0.5 rounded-full">{{ guide.category }}</span>
              <h3 class="text-xl font-bold text-gray-800 mt-1 leading-snug">{{ guide.title }}</h3>
            </div>
          </div>
          <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-xl text-gray-500 transition-transform duration-300"
               :class="expandedGuide === guide.id ? 'rotate-180 bg-primary-100 text-primary-700' : ''">
            <i class="bi bi-chevron-down"></i>
          </div>
        </button>

        <!-- Expanded steps -->
        <div
          v-show="expandedGuide === guide.id"
          class="px-6 pb-8 border-t border-gray-50 bg-gray-50/30 space-y-6 pt-6 animate-fade-in"
        >
          <p class="text-lg text-gray-500 border-l-4 border-primary-500 pl-4 italic mb-6">
            {{ guide.desc }}
          </p>

          <div class="relative pl-6 border-l-2 border-primary-100 space-y-6 ml-4">
            <div
              v-for="(step, idx) in guide.steps"
              :key="idx"
              class="relative"
            >
              <!-- Step Bullet -->
              <div class="absolute -left-[35px] top-1.5 w-7 h-7 rounded-full bg-primary-700 text-white font-bold text-xs flex items-center justify-center border-4 border-white shadow">
                {{ idx + 1 }}
              </div>
              <div>
                <h4 class="text-lg font-bold text-gray-800 flex items-center gap-2">
                  <span class="text-sm bg-primary-100 text-primary-700 px-2 py-0.5 rounded font-bold">{{ step.step }}</span>
                  {{ step.title }}
                </h4>
                <p class="text-base text-gray-600 mt-1 leading-relaxed">{{ step.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="filteredGuides.length === 0" class="text-center py-12 text-gray-400 text-lg">
        Không tìm thấy hướng dẫn nào phù hợp. Bác thử tìm với từ khóa khác nhé.
      </div>
    </div>
  </div>
</template>
