<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const address = '121/4a Trung mỹ tây Quận 12, TP. Hồ Chí Minh'
const searchFrom = ref('')
const routesCalculated = ref(false)
const activeTab = ref('map') // map, levels

const navigationSteps = [
  { step: 1, text: 'Đi thẳng từ vị trí của bạn hướng về đường Nguyễn Ảnh Thủ Quận 12.' },
  { step: 2, text: 'Rẽ trái vào đường Trung Mỹ Tây (gần ngã tư Tô Ký).' },
  { step: 3, text: 'Đi khoảng 300m, tìm ngõ 121 rồi rẽ vào.' },
  { step: 4, text: 'Điểm đến số nhà 121/4a nằm bên tay phải của bác.' }
]

const floors = ref([])

async function loadFloorLayouts() {
  try {
    const sodoRes = await axios.get('http://localhost:8080/api/so-do-benh-vien')
    const vitriRes = await axios.get('http://localhost:8080/api/vi-tri-phong')
    
    const rooms = vitriRes.data
    floors.value = sodoRes.data.map(f => ({
      id: f.id,
      name: f.tenTang,
      rooms: rooms.filter(r => r.maSoDo === f.id || (r.soDo && r.soDo.id === f.id))
    }))
  } catch (err) {
    console.error('Không tải được sơ đồ phòng khám', err)
  }
}

onMounted(() => {
  loadFloorLayouts()
})

function calculateRoute() {
  if (!searchFrom.value.trim()) {
    alert('Bác vui lòng nhập vị trí xuất phát của mình!')
    return
  }
  routesCalculated.value = true
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Sơ Đồ Bệnh Viện & Chỉ Đường</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Tìm kiếm đường đi nhanh nhất tới bệnh viện tại địa chỉ Trung Mỹ Tây Quận 12 và xem sơ đồ các phòng khám.
        </p>
      </div>
    </div>

    <div class="max-w-6xl mx-auto px-4 mt-8">
      <!-- Tab Toggles -->
      <div class="flex border-b border-gray-200 mb-6 bg-white p-1 rounded-2xl shadow-sm max-w-md">
        <button 
          @click="activeTab = 'map'"
          class="flex-1 py-3 text-base font-bold rounded-xl transition-all flex items-center justify-center gap-1.5"
          :class="activeTab === 'map' ? 'bg-primary-700 text-white shadow-md' : 'text-gray-500 hover:text-gray-700'"
        >
          <i class="bi bi-map"></i> Bản đồ chỉ đường
        </button>
        <button 
          @click="activeTab = 'levels'"
          class="flex-1 py-3 text-base font-bold rounded-xl transition-all flex items-center justify-center gap-1.5"
          :class="activeTab === 'levels' ? 'bg-primary-700 text-white shadow-md' : 'text-gray-500 hover:text-gray-700'"
        >
          <i class="bi bi-layers"></i> Sơ đồ phân tầng phòng khám
        </button>
      </div>

      <!-- ==================== TAB 1: MAP AND DIRECTIONS ==================== -->
      <div v-if="activeTab === 'map'" class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start animate-fade-in">
        <!-- Search and Navigation Instruction Panel -->
        <div class="lg:col-span-5 bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-6">
          <div>
            <h2 class="text-xl font-bold text-gray-800"><i class="bi bi-geo-alt-fill text-red-500 mr-1.5"></i>Vị Trí Bệnh Viện</h2>
            <p class="text-base text-gray-600 mt-2 font-medium bg-gray-50 p-3.5 rounded-2xl border border-gray-100">
              📍 {{ address }}
            </p>
          </div>

          <div class="border-t pt-4">
            <h3 class="text-lg font-bold text-gray-800 mb-3"><i class="bi bi-arrow-up-right-circle text-primary-700 mr-1.5"></i>Chỉ đường đi</h3>
            
            <form @submit.prevent="calculateRoute" class="space-y-4">
              <div>
                <label for="route-start" class="block text-sm font-semibold text-gray-500 mb-1.5">Điểm xuất phát của bác</label>
                <div class="relative">
                  <input 
                    id="route-start"
                    v-model="searchFrom"
                    type="text" 
                    placeholder="Nhập vị trí hiện tại (Ví dụ: Chợ Bến Thành, Quận 1...)"
                    class="w-full pl-10 pr-3 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 text-sm"
                  />
                  <i class="bi bi-search absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"></i>
                </div>
              </div>
              
              <button 
                type="submit" 
                class="w-full btn-primary !py-3 !text-sm !rounded-xl"
              >
                <i class="bi bi-signpost-split"></i> Tìm tuyến đường tối ưu
              </button>
            </form>
          </div>

          <!-- Step-by-step navigation instructions -->
          <div v-if="routesCalculated" class="border-t pt-4 space-y-4 animate-fade-in">
            <h4 class="text-sm text-gray-400 font-bold uppercase tracking-wider">Chi tiết lộ trình đi xe máy/ô tô</h4>
            <div class="relative pl-6 border-l-2 border-primary-100 space-y-4 ml-2">
              <div 
                v-for="step in navigationSteps" 
                :key="step.step"
                class="relative text-sm text-gray-700 leading-relaxed"
              >
                <div class="absolute -left-[32px] top-0.5 w-5 h-5 rounded-full bg-primary-700 text-white font-bold text-[10px] flex items-center justify-center border-2 border-white shadow">
                  {{ step.step }}
                </div>
                {{ step.text }}
              </div>
            </div>
            <p class="text-xs text-gray-400 italic mt-2"><i class="bi bi-clock mr-1"></i> Ước tính khoảng cách: ~1.5km từ Quốc lộ 22.</p>
          </div>
        </div>

        <!-- Interactive Map Iframe Viewport -->
        <div class="lg:col-span-7 bg-white p-4 rounded-3xl border border-gray-100 shadow-sm relative overflow-hidden">
          <div class="w-full h-[450px] rounded-2xl overflow-hidden border border-gray-100 relative">
            <!-- OpenStreetMap embed utilizing specific address coordinates (near Trung My Tay, District 12) -->
            <iframe 
              src="https://www.openstreetmap.org/export/embed.html?bbox=106.613%2C10.840%2C106.633%2C10.860&amp;layer=mapnik&amp;marker=10.8499%2C106.6231" 
              class="w-full h-full border-none"
              title="Bản đồ Bệnh viện"
            ></iframe>
            
            <div class="absolute bottom-4 left-4 bg-white/90 backdrop-blur-sm px-4 py-2.5 rounded-xl shadow-md border border-gray-100 text-xs text-gray-700 font-semibold pointer-events-none">
              <p class="text-primary-850 font-bold">121/4a Trung mỹ tây Quận 12</p>
              <p class="text-gray-400 font-medium -mt-0.5">District 12, Ho Chi Minh City</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== TAB 2: HOSPITAL FLOOR LAYOUT ==================== -->
      <div v-else class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm animate-fade-in space-y-8">
        <div>
          <h2 class="text-2xl font-bold text-gray-800 mb-1"><i class="bi bi-buildings-fill text-primary-700 mr-2"></i>Cơ Cấu Sảnh & Các Phòng Ban</h2>
          <p class="text-sm text-gray-400">Xem sơ đồ phân bố phòng chuyên khoa để dễ dàng di chuyển khi tới bệnh viện khám.</p>
        </div>

        <div class="space-y-4">
          <div v-for="floor in floors" :key="floor.id" class="flex flex-col md:flex-row items-stretch border border-indigo-100 rounded-2xl overflow-hidden hover:shadow-md transition-all">
            <div class="w-full md:w-32 bg-indigo-50/50 text-indigo-700 border-r border-indigo-100 flex flex-col justify-center items-center p-4 text-center">
              <span class="text-xs font-bold uppercase tracking-wider">Tầng</span>
              <span class="text-2xl font-black mt-1 uppercase">{{ floor.name }}</span>
            </div>
            <div class="flex-1 p-5 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
              <div v-for="room in floor.rooms" :key="room.id" class="bg-indigo-50/30 p-3 rounded-xl border border-indigo-50 flex items-center gap-3">
                <span class="text-xl">🩺</span>
                <div>
                  <h4 class="text-sm font-bold text-gray-800">{{ room.soPhong ? room.soPhong + ' - ' : '' }}{{ room.tenPhong }}</h4>
                  <p class="text-xs text-gray-400">{{ room.moTa || 'Phòng khám chuyên khoa' }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
