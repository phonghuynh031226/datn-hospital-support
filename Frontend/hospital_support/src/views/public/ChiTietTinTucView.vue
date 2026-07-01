<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const fontSizeLevel = ref(0) 

const fontSizeClass = computed(() => {
  if (fontSizeLevel.value === 1) return 'text-xl md:text-2xl leading-relaxed'
  if (fontSizeLevel.value === 2) return 'text-2xl md:text-3xl leading-loose'
  return 'text-lg md:text-xl leading-relaxed'
})

const article = ref({})
const relatedArticles = ref([])

const currentId = computed(() => parseInt(route.params.id) || 1)

async function loadArticle() {
  try {
    const res = await axios.get(`http://localhost:8080/api/tin-tuc/${currentId.value}`)
    const item = res.data
    article.value = {
      id: item.id,
      image: item.anhDaiDien || 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
      title: item.tieuDe,
      category: item.danhMuc ? item.danhMuc.tenDanhMuc : 'Tin tức',
      author: 'Ban Biên Tập',
      date: item.ngayTao ? new Date(item.ngayTao).toLocaleDateString('vi-VN') : '',
      content: item.noiDung
    }
    
    // Load some related/other articles
    const relatedRes = await axios.get(`http://localhost:8080/api/tin-tuc?size=3`)
    relatedArticles.value = relatedRes.data.content
      .filter(a => a.id !== item.id)
      .slice(0, 2)
      .map(r => ({
        id: r.id,
        image: r.anhDaiDien || 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
        title: r.tieuDe,
        category: r.danhMuc ? r.danhMuc.tenDanhMuc : 'Tin tức',
        date: r.ngayTao ? new Date(r.ngayTao).toLocaleDateString('vi-VN') : ''
      }))
  } catch (err) {
    console.error('Không tải được bài viết', err)
  }
}

onMounted(() => {
  loadArticle()
})

watch(currentId, () => {
  loadArticle()
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
