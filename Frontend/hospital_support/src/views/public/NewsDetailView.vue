<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// Font size scaling (rem-based or inline tailwind styling)
// FontSizeLevel: 0 (Normal - text-lg), 1 (Large - text-xl), 2 (Extra Large - text-2xl)
const fontSizeLevel = ref(0) 

const fontSizeClass = computed(() => {
  if (fontSizeLevel.value === 1) return 'text-xl md:text-2xl leading-relaxed'
  if (fontSizeLevel.value === 2) return 'text-2xl md:text-3xl leading-loose'
  return 'text-lg md:text-xl leading-relaxed'
})

const articles = [
  {
    id: 1,
    image: '/images/banners/banner1.png',
    title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
    date: '10/06/2026',
    category: 'Sự kiện',
    author: 'Phòng Truyền Thông',
    content: `Nhằm hưởng ứng tháng hành động vì người cao tuổi và mong muốn đồng hành chăm sóc sức khỏe cộng đồng, Bệnh viện Đa Khoa thông báo triển khai chương trình: **"Sống vui sống khỏe cùng Người cao tuổi"** với nhiều ưu đãi đặc biệt và hoạt động khám chữa bệnh miễn phí 100%.

### 1. Nội dung chương trình khám miễn phí:
- **Đo đường huyết mao mạch**: Phát hiện sớm bệnh lý đái tháo đường (tiểu đường) thường gặp ở người già.
- **Đo huyết áp và ghi Điện tâm đồ (ECG)**: Tầm soát các bệnh lý tim mạch, rối loạn nhịp tim, phòng ngừa nguy cơ đột quỵ đột ngột.
- **Khám và tư vấn bệnh lý cơ xương khớp**: Khám thoái hóa khớp gối, loãng xương, cột sống cổ - thắt lưng.
- **Tư vấn chế độ dinh dưỡng**: Hướng dẫn xây dựng thực đơn ăn uống khoa học, tập luyện thể thao lành mạnh phù hợp với từng thể trạng.

### 2. Đối tượng áp dụng:
Tất cả công dân Việt Nam từ **60 tuổi trở lên** (sinh năm 1966 trở về trước). Khi đi khám, các bác vui lòng mang theo thẻ Căn cước công dân để nhân viên hỗ trợ đối chiếu thông tin nhanh chóng.

### 3. Thời gian và địa điểm:
- **Thời gian**: Từ 07h30 đến 11h30 các ngày từ **15/06/2026 đến hết ngày 20/06/2026** (từ thứ Hai đến thứ Bảy).
- **Địa điểm**: Sảnh Tiếp đón Tầng Trệt (G), Bệnh viện Đa khoa.

Bệnh viện khuyến khích các bác đặt lịch hẹn trước thông qua số hotline **1900 1234** hoặc đăng ký trực tuyến tại mục **Đặt lịch khám** để nhận mã số khám ưu tiên, tránh phải xếp hàng chờ đợi lâu.`
  },
  {
    id: 2,
    image: '/images/banners/banner2.png',
    title: 'Ứng dụng kỹ thuật phẫu thuật nội soi tiên tiến trong điều trị bệnh tim',
    date: '05/06/2026',
    category: 'Y khoa',
    author: 'TS.BS Nguyễn Văn An',
    content: `Ngày 04/06/2026 vừa qua, Đội ngũ chuyên khoa Ngoại tim mạch của Bệnh viện đã thực hiện thành công ca phẫu thuật thay van động mạch chủ bằng kỹ thuật **nội soi robot ít xâm lấn** cho cụ ông N.V.T (78 tuổi, trú tại Hà Nội) bị hẹp khít van tim mức độ nặng kèm theo bệnh lý nền tăng huyết áp.

Thông thường, đối với bệnh nhân lớn tuổi, phẫu thuật mổ hở lồng ngực truyền thống ẩn chứa nhiều nguy cơ tai biến y khoa nguy hiểm và thời gian phục hồi kéo dài. Việc ứng dụng công nghệ phẫu thuật nội soi mới này giúp giảm thiểu tối đa đau đớn cho bệnh nhân sau mổ:
- **Đường mổ cực nhỏ**: Chỉ từ 2 - 3 cm thay vì đường mổ dài dọc xương ức.
- **Mất máu tối thiểu**: Giảm nhu cầu truyền máu ngoài, tránh nhiễm trùng.
- **Phục hồi thần tốc**: Bệnh nhân cụ ông đã có thể tự ngồi dậy sau 24h và xuất viện chỉ sau 5 ngày theo dõi.

Sự thành công của ca mổ đánh dấu bước phát triển vượt bậc của bệnh viện trong lĩnh vực y khoa kỹ thuật cao, đem lại cơ hội điều trị an toàn cho các bệnh nhân lớn tuổi mắc bệnh tim phức tạp.`
  },
  {
    id: 3,
    image: '/images/banners/banner3.png',
    title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
    date: '01/06/2026',
    category: 'Hướng dẫn',
    author: 'Tổ Chăm Sóc Khách Hàng',
    content: `Nhằm mang đến sự thuận tiện tối đa cho bệnh nhân, đặc biệt là các bác lớn tuổi không muốn phải dậy sớm xếp hàng lấy số, Bệnh viện đã nâng cấp hệ thống đặt lịch khám trực tuyến hoàn toàn mới. Dưới đây là hướng dẫn các bước chi tiết để đặt lịch nhanh chóng:

### Bước 1: Chọn Chuyên khoa và Bác sĩ
Truy cập vào trang **Đặt lịch khám**, lựa chọn chuyên khoa bác muốn khám (Ví dụ: Khoa Mắt, Khoa Tim Mạch, Khoa Nội Tổng Quát) và chọn bác sĩ mong muốn (hoặc để hệ thống chọn bác sĩ bất kỳ).

### Bước 2: Chọn Ngày và Khung giờ
Bác chọn ngày muốn đến khám và khung giờ trống phù hợp với lịch sinh hoạt cá nhân (Ví dụ: Khung giờ 08h30 - 09h00).

### Bước 3: Nhập Triệu chứng & Sử dụng AI gợi ý khoa
Nếu bác phân vân không biết mình bị đau khớp gối hay đau ngực nên khám khoa nào, chỉ cần nhập triệu chứng bệnh vào ô mô tả. Hệ thống trí tuệ nhân tạo (AI) sẽ tự động phân tích và đưa ra gợi ý khoa khám phù hợp nhất cho bác.

### Bước 4: Xác nhận và Nhận mã khám
Sau khi nhấn Đặt lịch, hệ thống sẽ hiện thông tin phiếu đặt khám chi tiết (gồm Mã khám, Số thứ tự khám, Khung giờ khám). Các bác hãy lưu giữ thông tin này để trình cho y tá tại phòng khám khi đến bệnh viện.`
  }
]

const currentId = computed(() => parseInt(route.params.id) || 1)
const article = computed(() => {
  return articles.find(a => a.id === currentId.value) || articles[0]
})

const relatedArticles = computed(() => {
  return articles.filter(a => a.id !== currentId.value).slice(0, 2)
})

// Comment section mock
const comments = ref([
  { name: 'Bác Trần Hữu Đức', time: '10/06/2026 14:30', content: 'Chương trình rất ý nghĩa. Tuần sau tôi và nhà tôi sẽ đăng ký qua khám thử.' },
  { name: 'Cô Lê Thị Mỹ', time: '11/06/2026 09:15', content: 'Tôi đã đặt lịch khám thành công qua mạng, giao diện chữ to dễ dùng lắm. Cảm ơn bệnh viện.' }
])

const newComment = ref({
  name: '',
  content: ''
})

function submitComment() {
  if (!newComment.value.name || !newComment.value.content) {
    alert('Vui lòng nhập họ tên và nội dung ý kiến của bác!')
    return
  }
  
  const dateStr = new Date().toLocaleString('vi-VN', { hour12: false })
  comments.value.push({
    name: newComment.value.name,
    time: dateStr,
    content: newComment.value.content
  })
  
  newComment.value = { name: '', content: '' }
  alert('Gửi ý kiến đóng góp thành công! Ý kiến sẽ được duyệt trong ít phút.')
}
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
          <span><i class="bi bi-person text-primary-600"></i> Người đăng: {{ article.author }}</span>
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
          class="text-gray-700 space-y-6 pt-4 text-justify whitespace-pre-line"
          :class="fontSizeClass"
        >
          {{ article.content }}
        </div>
      </div>

      <!-- ==================== COMMENTS SECTION ==================== -->
      <div class="bg-white rounded-3xl p-6 md:p-8 border border-gray-100 shadow-sm mt-8 space-y-6">
        <h3 class="text-2xl font-bold text-gray-800"><i class="bi bi-chat-text text-primary-600 mr-2"></i> Ý kiến đóng góp từ người đọc:</h3>
        
        <!-- List comments -->
        <div class="space-y-4 divide-y divide-gray-100">
          <div v-for="(cmt, idx) in comments" :key="idx" class="pt-4 first:pt-0">
            <div class="flex justify-between items-center mb-1">
              <span class="font-bold text-gray-800 text-lg">{{ cmt.name }}</span>
              <span class="text-xs text-gray-400 font-semibold">{{ cmt.time }}</span>
            </div>
            <p class="text-base text-gray-600 leading-relaxed">{{ cmt.content }}</p>
          </div>
        </div>

        <!-- Form Comment -->
        <form @submit.prevent="submitComment" class="bg-gray-50 p-6 rounded-2xl border border-gray-100 space-y-4">
          <h4 class="font-bold text-gray-700 text-lg">Gửi ý kiến đóng góp của bác:</h4>
          
          <div>
            <label for="comment-name" class="block text-sm font-semibold text-gray-700 mb-1">Họ tên của bác:</label>
            <input
              id="comment-name"
              v-model="newComment.name"
              type="text"
              required
              placeholder="Nhập họ và tên bác..."
              class="w-full px-4 py-2.5 text-base rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 bg-white placeholder:text-gray-400"
            />
          </div>

          <div>
            <label for="comment-content" class="block text-sm font-semibold text-gray-700 mb-1">Nội dung ý kiến:</label>
            <textarea
              id="comment-content"
              v-model="newComment.content"
              rows="3"
              required
              placeholder="Nhập ý kiến hoặc thắc mắc của bác..."
              class="w-full px-4 py-2.5 text-base rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary-300 bg-white placeholder:text-gray-400 resize-none"
            ></textarea>
          </div>

          <button type="submit" class="btn-primary !py-3 !px-6 !text-base !rounded-xl">
            <i class="bi bi-send"></i> Gửi ý kiến của bác
          </button>
        </form>
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
