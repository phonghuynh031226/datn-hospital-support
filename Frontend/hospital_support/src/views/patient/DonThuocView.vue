<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const prescriptions = ref([])
const activePrescription = ref(null)
const isLoggedIn = ref(false)
const currentUser = ref(null)

onMounted(async () => {
  const userJson = localStorage.getItem('currentUser')
  if (userJson) {
    currentUser.value = JSON.parse(userJson)
    isLoggedIn.value = true
    await loadPrescriptions()
  }
})

async function loadPrescriptions() {
  try {
    const res = await axios.get(`http://localhost:8080/api/don-thuoc/patient/${currentUser.value.id}`)
    prescriptions.value = res.data.map(dt => ({
      id: dt.id,
      code: 'DT-' + dt.id,
      date: dt.ngayTao ? new Date(dt.ngayTao).toLocaleDateString('vi-VN') : '',
      doctor: dt.bacSiName,
      diagnose: 'Chi tiết xem trong lịch sử khám',
      status: dt.trangThai === 'DA_PHAT_THUOC' ? 'Đã nhận thuốc' : 'Chờ nhận thuốc',
      medicines: dt.details.map(m => ({
        name: m.tenThuoc,
        qty: m.soLuongThucPhat || m.soLuong,
        unit: m.donViTinh,
        use: m.huongDanSuDung || 'Uống theo chỉ dẫn bác sĩ'
      }))
    }))
    if (prescriptions.value.length > 0) {
      activePrescription.value = prescriptions.value[0]
    }
  } catch (err) {
    console.error('Không tải được đơn thuốc', err)
  }
}

function selectPrescription(p) {
  activePrescription.value = p
}

function downloadPrescriptionPdf(p) {
  window.open(`http://localhost:8080/api/don-thuoc/${p.id}/pdf`, '_blank')
}
</script>

<template>
  <div class="bg-gray-50 min-h-[calc(100vh-140px)] pb-16">
    <!-- ==================== BANNER ==================== -->
    <div class="bg-gradient-to-r from-primary-700 via-primary-600 to-primary-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">Danh Sách Đơn Thuốc</h1>
        <p class="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
          Theo dõi đơn thuốc đã được kê bởi bác sĩ và trạng thái nhận thuốc tại nhà thuốc bệnh viện.
        </p>
      </div>
    </div>

    <div class="max-w-6xl mx-auto px-4 mt-8">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
        
        <!-- List of prescriptions -->
        <div class="lg:col-span-5 bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
          <h2 class="text-xl font-bold text-gray-800 border-b pb-2">
            <i class="bi bi-receipt-cutoff text-primary-700 mr-2"></i>Đơn thuốc của bạn
          </h2>

          <div class="space-y-3 max-h-[500px] overflow-y-auto pr-1">
            <div 
              v-for="p in prescriptions" 
              :key="p.id"
              @click="selectPrescription(p)"
              class="p-4 rounded-2xl border transition-all cursor-pointer hover:border-primary-300"
              :class="activePrescription?.id === p.id 
                ? 'bg-primary-50/50 border-primary-500 shadow-sm' 
                : 'border-gray-100 bg-white'"
            >
              <div class="flex justify-between items-start mb-2">
                <span class="font-bold text-base text-gray-800">{{ p.code }}</span>
                <span 
                  class="text-xs px-2.5 py-0.5 rounded-full font-bold"
                  :class="p.status === 'Đã nhận thuốc' 
                    ? 'bg-emerald-100 text-emerald-800' 
                    : 'bg-amber-100 text-amber-800 animate-pulse'"
                >
                  {{ p.status }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mb-1">
                <strong>Chẩn đoán:</strong> {{ p.diagnose }}
              </p>
              <div class="flex justify-between text-xs text-gray-400 mt-2 pt-2 border-t border-gray-50">
                <span>BS: {{ p.doctor }}</span>
                <span>{{ p.date }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Prescription Detail -->
        <div class="lg:col-span-7 bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm min-h-[400px] flex flex-col">
          <div v-if="activePrescription" class="flex-1 flex flex-col space-y-6">
            <!-- Detail Header -->
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center border-b pb-4 gap-2">
              <div>
                <h3 class="text-2xl font-black text-gray-800 flex items-center gap-2">
                  <span>Mã đơn:</span>
                  <span class="text-primary-700 font-mono">{{ activePrescription.code }}</span>
                </h3>
                <p class="text-xs text-gray-400 mt-1">Ngày kê đơn: {{ activePrescription.date }} - Bác sĩ: {{ activePrescription.doctor }}</p>
              </div>
              
              <div class="flex items-center gap-2">
                <button 
                  @click="downloadPrescriptionPdf(activePrescription)"
                  class="px-4 py-1.5 bg-primary-705 bg-primary-700 text-white rounded-xl text-sm font-bold shadow hover:bg-primary-800 transition-all flex items-center gap-1.5 mr-2"
                >
                  <i class="bi bi-download"></i> Tải PDF
                </button>
                <span class="text-sm text-gray-500">Trạng thái:</span>
                <span 
                  class="text-sm px-3.5 py-1 rounded-full font-bold shadow-sm"
                  :class="activePrescription.status === 'Đã nhận thuốc' 
                    ? 'bg-emerald-500 text-white' 
                    : 'bg-amber-500 text-white'"
                >
                  {{ activePrescription.status }}
                </span>
              </div>
            </div>

            <!-- Diagnose Box -->
            <div class="bg-gray-50 p-5 rounded-2xl border border-gray-100 text-base">
              <h4 class="text-sm text-gray-400 font-bold uppercase tracking-wider mb-1">Chẩn đoán từ bác sĩ</h4>
              <p class="text-gray-800 font-semibold text-lg">{{ activePrescription.diagnose }}</p>
            </div>

            <!-- Medicines Table -->
            <div class="flex-1">
              <h4 class="text-lg font-bold text-gray-800 mb-3"><i class="bi bi-capsule-capsule text-primary-700 mr-2"></i>Danh mục thuốc uống</h4>
              
              <div class="border rounded-2xl overflow-hidden shadow-sm">
                <table class="w-full text-left border-collapse">
                  <thead>
                    <tr class="bg-gray-50 border-b border-gray-100 text-sm font-bold text-gray-500">
                      <th class="py-3 px-4">Tên thuốc</th>
                      <th class="py-3 px-4 text-center">SL</th>
                      <th class="py-3 px-4">Đơn vị</th>
                      <th class="py-3 px-4">Cách dùng</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-100 text-base text-gray-700">
                    <tr v-for="m in activePrescription.medicines" :key="m.name" class="hover:bg-gray-50/50">
                      <td class="py-3.5 px-4 font-bold text-gray-800">{{ m.name }}</td>
                      <td class="py-3.5 px-4 text-center font-bold text-primary-700">{{ m.qty }}</td>
                      <td class="py-3.5 px-4 text-gray-500">{{ m.unit }}</td>
                      <td class="py-3.5 px-4 text-sm font-medium text-gray-600 leading-relaxed">{{ m.use }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Notes footer -->
            <div class="bg-primary-50/50 border border-primary-200/50 p-4 rounded-2xl flex items-start gap-3 text-sm text-primary-850">
              <div class="text-2xl">💡</div>
              <div>
                <p class="font-bold">Lưu ý cho bệnh nhân:</p>
                <p class="leading-relaxed mt-0.5">Bạn mang theo mã đơn thuốc này cùng Căn cước công dân đến **Quầy cấp phát thuốc** để đối chiếu và nhận thuốc trực tiếp. Uống thuốc đúng liều và thời gian theo hướng dẫn của bác sĩ.</p>
              </div>
            </div>
          </div>

          <div v-else class="flex-1 flex flex-col items-center justify-center text-center text-gray-400 p-8">
            <span class="text-6xl mb-4">💊</span>
            <h3 class="text-xl font-bold text-gray-700">Bạn chưa chọn đơn thuốc</h3>
            <p class="text-base mt-1 max-w-xs">Nhấp chọn một đơn thuốc ở danh sách bên trái để xem chi tiết hướng dẫn uống thuốc.</p>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>
