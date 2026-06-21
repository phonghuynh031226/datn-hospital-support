<script setup>
import { ref, onMounted } from 'vue'

const activeMenu = ref('catalog') // catalog, actions, warnings, suppliers, reports
const stock = ref([])
const showImportModal = ref(false)
const showExportModal = ref(false)

// Mock suppliers list
const suppliers = ref([
  { id: 1, name: 'Dược phẩm TW1 (Pharmedic)', contact: 'Bà Nguyễn Thị Hoa', phone: '028 3829 1234', address: 'Quận 1, TP.HCM' },
  { id: 2, name: 'Công ty cổ phần Traphaco', contact: 'Ông Trần Văn Nam', phone: '024 3766 2288', address: 'Quận Hoàng Mai, Hà Nội' },
  { id: 3, name: 'Dược Hậu Giang (DHG Pharma)', contact: 'Bà Lê Thu Trang', phone: '0292 3891 433', address: 'Bình Thủy, Cần Thơ' }
])

// Stats
const stats = ref({
  totalItems: 0,
  totalQty: 0,
  nearExpiry: 0,
  lowStock: 0
})

// Forms
const importForm = ref({ name: '', qty: 50, unit: 'Viên', expiryDate: '2027-12-31', minStock: 20, supplier: 'Dược phẩm TW1 (Pharmedic)' })
const exportForm = ref({ name: 'Panadol Extra 500mg', qty: 20, reason: 'Cấp phát cho quầy Dược sĩ' })
const newSupplierForm = ref({ name: '', contact: '', phone: '', address: '' })
const showAddSupplierModal = ref(false)

onMounted(() => {
  loadStock()
})

function loadStock() {
  const mockStock = [
    { name: 'Panadol Extra 500mg', qty: 120, unit: 'Viên', expiryDate: '2027-06-30', minStock: 20 },
    { name: 'Concor 2.5mg (Bisoprolol fumarate)', qty: 85, unit: 'Viên', expiryDate: '2026-09-15', minStock: 25 },
    { name: 'Nexium mups 20mg', qty: 15, unit: 'Viên', expiryDate: '2027-11-20', minStock: 30 },
    { name: 'Phosphalugel (Thuốc chữ P)', qty: 60, unit: 'Gói', expiryDate: '2026-08-01', minStock: 15 },
    { name: 'Pharmaton Essential', qty: 45, unit: 'Viên', expiryDate: '2027-04-10', minStock: 20 },
    { name: 'Amoxicillin 500mg', qty: 9, unit: 'Viên', expiryDate: '2027-09-30', minStock: 25 }
  ]
  const data = localStorage.getItem('warehouseStock')
  if (data) {
    stock.value = JSON.parse(data)
  } else {
    stock.value = mockStock
    localStorage.setItem('warehouseStock', JSON.stringify(mockStock))
  }
  calculateStats()
}

function calculateStats() {
  const list = stock.value
  stats.value.totalItems = list.length
  stats.value.totalQty = list.reduce((sum, item) => sum + item.qty, 0)
  
  // Expiry rule: expiration before 2026-10-01
  stats.value.nearExpiry = list.filter(item => {
    const exp = new Date(item.expiryDate)
    const limit = new Date('2026-10-01')
    return exp <= limit
  }).length

  stats.value.lowStock = list.filter(item => item.qty <= item.minStock).length
}

function submitImport() {
  const f = importForm.value
  if (!f.name) {
    alert('Vui lòng nhập tên thuốc!')
    return
  }
  const list = [...stock.value]
  const idx = list.findIndex(item => item.name.toLowerCase() === f.name.toLowerCase())

  if (idx !== -1) {
    list[idx].qty += f.qty
    list[idx].expiryDate = f.expiryDate
  } else {
    list.push({
      name: f.name,
      qty: f.qty,
      unit: f.unit,
      expiryDate: f.expiryDate,
      minStock: f.minStock
    })
  }

  localStorage.setItem('warehouseStock', JSON.stringify(list))
  stock.value = list
  calculateStats()
  
  importForm.value = { name: '', qty: 50, unit: 'Viên', expiryDate: '2027-12-31', minStock: 20, supplier: 'Dược phẩm TW1 (Pharmedic)' }
  showImportModal.value = false
  alert('Nhập kho thuốc thành công!')
}

function submitExport() {
  const f = exportForm.value
  const list = [...stock.value]
  const idx = list.findIndex(item => item.name === f.name)
  if (idx !== -1) {
    if (list[idx].qty < f.qty) {
      alert('Số lượng tồn kho không đủ để xuất!')
      return
    }
    list[idx].qty -= f.qty
    localStorage.setItem('warehouseStock', JSON.stringify(list))
    stock.value = list
    calculateStats()
    showExportModal.value = false
    alert(`Đã xuất kho thành công ${f.qty} ${list[idx].unit} thuốc ${f.name} cấp cho Quầy dược sĩ!`)
  }
}

function addSupplier() {
  const f = newSupplierForm.value
  if (!f.name) {
    alert('Vui lòng nhập tên nhà cung cấp!')
    return
  }
  suppliers.value.push({
    id: Date.now(),
    name: f.name,
    contact: f.contact || 'Chưa cập nhật',
    phone: f.phone || 'Chưa cập nhật',
    address: f.address || 'Chưa cập nhật'
  })
  newSupplierForm.value = { name: '', contact: '', phone: '', address: '' }
  showAddSupplierModal.value = false
  alert('Đã thêm nhà cung cấp dược phẩm mới!')
}

function removeSupplier(id) {
  if (confirm('Xóa nhà cung cấp này khỏi danh sách?')) {
    suppliers.value = suppliers.value.filter(s => s.id !== id)
  }
}

function quickRestock(name) {
  const list = [...stock.value]
  const idx = list.findIndex(item => item.name === name)
  if (idx !== -1) {
    list[idx].qty += 100
    localStorage.setItem('warehouseStock', JSON.stringify(list))
    stock.value = list
    calculateStats()
    alert(`Nhập bổ sung nhanh 100 đơn vị cho thuốc ${name}!`)
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-112px)] bg-gray-50 flex">
    <!-- Left Navigation Sidebar -->
    <aside class="w-64 bg-white border-r border-gray-150 flex flex-col justify-between p-4 flex-shrink-0">
      <div class="space-y-6">
        <!-- Logo and header -->
        <div class="flex items-center gap-3 p-2 pb-4 border-b border-gray-100">
          <img 
            src="https://i.ibb.co/4gXZ9qs4/z7939748517288-6fea6e7a88629fd0c0ef6ce9326fd25f.png" 
            alt="Logo" 
            class="w-10 h-10 object-contain rounded-lg bg-gray-50 p-0.5 shadow-sm"
          />
          <div>
            <h4 class="text-sm font-bold text-gray-800">Kho Dược Phẩm</h4>
            <span class="text-xs text-amber-600 font-semibold">Thủ kho Phạm M. Tuấn</span>
          </div>
        </div>

        <!-- Sidebar menu -->
        <nav class="space-y-1">
          <button 
            @click="activeMenu = 'catalog'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'catalog' ? 'bg-amber-50 text-amber-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-box text-lg"></i>
            Danh mục & Hạn dùng
          </button>
          
          <button 
            @click="activeMenu = 'actions'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'actions' ? 'bg-amber-50 text-amber-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-arrow-down-up text-lg"></i>
            Nhập / Xuất kho thuốc
          </button>

          <button 
            @click="activeMenu = 'warnings'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'warnings' ? 'bg-amber-50 text-amber-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-exclamation-triangle text-lg"></i>
            Cảnh báo an toàn kho
          </button>

          <button 
            @click="activeMenu = 'suppliers'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'suppliers' ? 'bg-amber-50 text-amber-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-truck text-lg"></i>
            Nhà cung cấp dược
          </button>

          <button 
            @click="activeMenu = 'reports'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'reports' ? 'bg-amber-50 text-amber-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-bar-chart-steps text-lg"></i>
            Báo cáo xuất nhập tồn
          </button>
        </nav>
      </div>

      <div class="p-2 border-t text-xs text-gray-400 text-center">
        Hospital Inventory v2.0
      </div>
    </aside>

    <!-- Main Content Area -->
    <main class="flex-1 p-6 md:p-8 overflow-y-auto">
      
      <!-- Statistics summary boxes (shown at the top of main content) -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Tổng Số Mặt Hàng</span>
            <h4 class="text-2xl font-black text-amber-700 mt-1">{{ stats.totalItems }}</h4>
          </div>
          <span class="text-3xl">📦</span>
        </div>
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Tổng Tồn Kho</span>
            <h4 class="text-2xl font-black text-gray-800 mt-1">{{ stats.totalQty }}</h4>
          </div>
          <span class="text-3xl">📊</span>
        </div>
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between"
             :class="stats.lowStock > 0 ? 'bg-rose-50/40 border-rose-100' : ''">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Sắp Hết Hàng</span>
            <h4 class="text-2xl font-black text-rose-600 mt-1">{{ stats.lowStock }}</h4>
          </div>
          <span class="text-3xl text-rose-500">⚠️</span>
        </div>
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between"
             :class="stats.nearExpiry > 0 ? 'bg-amber-50/40 border-amber-100' : ''">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Sắp Hết Hạn</span>
            <h4 class="text-2xl font-black text-amber-600 mt-1">{{ stats.nearExpiry }}</h4>
          </div>
          <span class="text-3xl text-amber-500">⏳</span>
        </div>
      </div>

      <!-- ==================== MENU 1: CATALOG & EXPIRY ==================== -->
      <div v-if="activeMenu === 'catalog'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Danh mục thuốc & Hạn sử dụng</h2>
          <p class="text-sm text-gray-400">Danh sách toàn bộ dược phẩm lưu trữ tại kho trung tâm.</p>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Tên dược phẩm</th>
                  <th class="py-3.5 px-6 text-center">Tồn kho</th>
                  <th class="py-3.5 px-6">Đơn vị</th>
                  <th class="py-3.5 px-6">Hạn sử dụng (HSD)</th>
                  <th class="py-3.5 px-6">Định mức an toàn</th>
                  <th class="py-3.5 px-6 text-center">Nhập bổ sung nhanh</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="item in stock" :key="item.name" class="hover:bg-gray-50/30">
                  <td class="py-4 px-6 font-bold text-gray-850">{{ item.name }}</td>
                  <td class="py-4 px-6 text-center font-bold" :class="item.qty <= item.minStock ? 'text-rose-600 font-black' : 'text-gray-800'">
                    {{ item.qty }}
                  </td>
                  <td class="py-4 px-6 text-gray-500 font-semibold">{{ item.unit }}</td>
                  <td class="py-4 px-6 font-mono text-xs">{{ item.expiryDate }}</td>
                  <td class="py-4 px-6 text-gray-400 font-medium">{{ item.minStock }}</td>
                  <td class="py-4 px-6 text-center">
                    <button @click="quickRestock(item.name)" class="py-1.5 px-3 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-200 rounded-xl text-xs font-bold transition-all">
                      +100 bổ sung
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 2: IMPORT / EXPORT ==================== -->
      <div v-else-if="activeMenu === 'actions'" class="grid grid-cols-1 md:grid-cols-2 gap-8 animate-fade-in">
        <!-- Nhập kho -->
        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-4">
          <h3 class="text-xl font-bold text-gray-850 flex items-center gap-2 border-b pb-3">
            <span class="text-2xl">📥</span> Phiếu Nhập Kho Thuốc
          </h3>
          <form @submit.prevent="submitImport" class="space-y-4 text-sm">
            <div>
              <label for="med-name-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Tên thuốc nhập</label>
              <input id="med-name-in" type="text" v-model="importForm.name" required class="w-full px-3 py-2 border rounded-xl" placeholder="Tên thuốc..." />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label for="med-qty-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Số lượng</label>
                <input id="med-qty-in" type="number" v-model="importForm.qty" required class="w-full px-3 py-2 border rounded-xl text-center" />
              </div>
              <div>
                <label for="med-unit-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Đơn vị</label>
                <select id="med-unit-in" v-model="importForm.unit" class="w-full px-3 py-2 border rounded-xl">
                  <option value="Viên">Viên</option>
                  <option value="Gói">Gói</option>
                  <option value="Chai">Chai</option>
                </select>
              </div>
            </div>
            <div>
              <label for="med-exp-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Hạn sử dụng (HSD)</label>
              <input id="med-exp-in" type="date" v-model="importForm.expiryDate" required class="w-full px-3 py-2 border rounded-xl" />
            </div>
            <div>
              <label for="med-sup-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Nhà cung cấp dược</label>
              <select id="med-sup-in" v-model="importForm.supplier" class="w-full px-3 py-2 border rounded-xl">
                <option v-for="s in suppliers" :key="s.id" :value="s.name">{{ s.name }}</option>
              </select>
            </div>
            <button type="submit" class="w-full py-3 bg-amber-700 hover:bg-amber-800 text-white font-bold rounded-xl shadow mt-2">
              Xác nhận Nhập Kho
            </button>
          </form>
        </div>

        <!-- Xuất kho -->
        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-4">
          <h3 class="text-xl font-bold text-gray-850 flex items-center gap-2 border-b pb-3">
            <span class="text-2xl">📤</span> Phiếu Xuất Cấp Phát Thuốc
          </h3>
          <form @submit.prevent="submitExport" class="space-y-4 text-sm">
            <div>
              <label for="med-select-ex" class="block text-xs text-gray-450 font-bold uppercase mb-1">Chọn dược phẩm xuất</label>
              <select id="med-select-ex" v-model="exportForm.name" class="w-full px-3 py-2 border rounded-xl">
                <option v-for="item in stock" :key="item.name" :value="item.name">
                  {{ item.name }} (Còn tồn: {{ item.qty }} {{ item.unit }})
                </option>
              </select>
            </div>
            <div>
              <label for="med-qty-ex" class="block text-xs text-gray-450 font-bold uppercase mb-1">Số lượng xuất cấp</label>
              <input id="med-qty-ex" type="number" v-model="exportForm.qty" required class="w-full px-3 py-2 border rounded-xl text-center" />
            </div>
            <div>
              <label for="med-reason-ex" class="block text-xs text-gray-450 font-bold uppercase mb-1">Lý do xuất</label>
              <input id="med-reason-ex" type="text" v-model="exportForm.reason" class="w-full px-3 py-2 border rounded-xl" />
            </div>
            <button type="submit" class="w-full py-3 bg-orange-600 hover:bg-orange-700 text-white font-bold rounded-xl shadow mt-6">
              Xác nhận Xuất Dược Phẩm
            </button>
          </form>
        </div>
      </div>

      <!-- ==================== MENU 3: WARNINGS ==================== -->
      <div v-else-if="activeMenu === 'warnings'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Cảnh báo an toàn tồn kho thuốc</h2>
          <p class="text-sm text-gray-400">Danh sách các loại thuốc dưới định mức tồn kho tối thiểu hoặc cận ngày hết hạn sử dụng.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <!-- Low Stock Warnings -->
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
            <h3 class="text-lg font-bold text-rose-600 border-b pb-2 flex items-center gap-1.5">
              <i class="bi bi-exclamation-octagon-fill"></i> Tồn kho báo động đỏ (Hết hàng/Sắp hết)
            </h3>
            <div class="divide-y">
              <div v-for="item in stock.filter(i => i.qty <= i.minStock)" :key="item.name" class="py-3 flex justify-between items-center text-sm">
                <div>
                  <h4 class="font-bold text-gray-850">{{ item.name }}</h4>
                  <p class="text-xs text-gray-400">Định mức an toàn: {{ item.minStock }} - Đơn vị: {{ item.unit }}</p>
                </div>
                <div class="text-right">
                  <span class="text-xs font-bold text-red-600 bg-red-50 px-2 py-0.5 rounded mr-3">Còn tồn: {{ item.qty }}</span>
                  <button @click="quickRestock(item.name)" class="text-xs text-amber-700 font-bold hover:underline">Bổ sung nhanh</button>
                </div>
              </div>
            </div>
          </div>

          <!-- Expiry Warnings -->
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
            <h3 class="text-lg font-bold text-amber-600 border-b pb-2 flex items-center gap-1.5">
              <i class="bi bi-clock-history"></i> Cận hạn sử dụng (Dưới 6 tháng)
            </h3>
            <div class="divide-y">
              <div v-for="item in stock.filter(i => new Date(i.expiryDate) <= new Date('2026-10-01'))" :key="item.name" class="py-3 flex justify-between items-center text-sm">
                <div>
                  <h4 class="font-bold text-gray-850">{{ item.name }}</h4>
                  <p class="text-xs text-gray-400">Hạn sử dụng: <span class="font-mono text-red-500 font-semibold">{{ item.expiryDate }}</span></p>
                </div>
                <span class="text-xs font-bold text-amber-800 bg-amber-50 px-2 py-0.5 rounded">Cận HSD</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 4: SUPPLIERS ==================== -->
      <div v-else-if="activeMenu === 'suppliers'" class="space-y-6 animate-fade-in">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Quản lý nhà cung cấp dược</h2>
            <p class="text-sm text-gray-400">Danh mục đối tác cung ứng dược phẩm và vật tư y khoa cho bệnh viện.</p>
          </div>
          <button 
            @click="showAddSupplierModal = true"
            class="py-2.5 px-4 bg-amber-700 hover:bg-amber-800 text-white font-bold rounded-xl text-xs transition-colors flex items-center gap-1"
          >
            <i class="bi bi-person-plus-fill"></i> Thêm nhà cung cấp
          </button>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Tên nhà cung cấp</th>
                  <th class="py-3.5 px-6">Người liên hệ</th>
                  <th class="py-3.5 px-6">Số điện thoại</th>
                  <th class="py-3.5 px-6">Địa chỉ trụ sở</th>
                  <th class="py-3.5 px-6 text-center">Xóa</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="s in suppliers" :key="s.id" class="hover:bg-gray-50/20">
                  <td class="py-4 px-6 font-bold text-gray-850">{{ s.name }}</td>
                  <td class="py-4 px-6 text-gray-600 font-medium">{{ s.contact }}</td>
                  <td class="py-4 px-6 font-mono text-xs">{{ s.phone }}</td>
                  <td class="py-4 px-6 text-gray-500 font-semibold">{{ s.address }}</td>
                  <td class="py-4 px-6 text-center">
                    <button @click="removeSupplier(s.id)" class="text-rose-500 hover:bg-rose-50 p-1.5 rounded-lg transition-colors">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 5: INVENTORY REPORTS ==================== -->
      <div v-else-if="activeMenu === 'reports'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Báo cáo xuất - nhập - tồn kho thuốc</h2>
          <p class="text-sm text-gray-400">Xem thống kê và giá trị tổng kho dược phẩm của bệnh viện.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
            <h3 class="text-lg font-bold text-gray-850 border-b pb-2">Ước tính giá trị tồn kho hiện tại</h3>
            <div class="space-y-4 pt-2 text-sm text-gray-700">
              <div class="flex justify-between border-b pb-2">
                <span>Số lượng mặt hàng biệt dược:</span>
                <span class="font-bold text-gray-800">{{ stats.totalItems }} loại</span>
              </div>
              <div class="flex justify-between border-b pb-2">
                <span>Tổng đơn vị tồn trữ:</span>
                <span class="font-bold text-gray-800">{{ stats.totalQty }} đơn vị</span>
              </div>
              <div class="flex justify-between pb-2">
                <span class="font-bold text-amber-800">Tổng giá trị quy đổi kho dược:</span>
                <span class="font-black text-lg text-amber-700">~{{ (stats.totalQty * 18000).toLocaleString('vi-VN') }}đ</span>
              </div>
            </div>
          </div>

          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm">
            <h3 class="text-lg font-bold text-gray-850 mb-4">Nhật ký nhập xuất tồn tuần này</h3>
            <div class="space-y-3 text-xs font-semibold">
              <div class="p-3 bg-emerald-50 text-emerald-800 rounded-xl flex justify-between items-center">
                <span>📥 Đã nhập 500 viên Amoxicillin từ Traphaco</span>
                <span>Hôm qua</span>
              </div>
              <div class="p-3 bg-amber-50 text-amber-800 rounded-xl flex justify-between items-center">
                <span>📤 Xuất 50 viên Nexium cho Quầy dược sĩ</span>
                <span>Hôm nay</span>
              </div>
              <div class="p-3 bg-emerald-50 text-emerald-800 rounded-xl flex justify-between items-center">
                <span>📥 Đã nhập 200 hộp Phosphalugel từ Pharmedic</span>
                <span>15/06/2026</span>
              </div>
            </div>
          </div>
        </div>
      </div>

    </main>

    <!-- ==================== ADD SUPPLIER MODAL ==================== -->
    <div v-if="showAddSupplierModal" class="fixed inset-0 bg-black/55 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="text-center">
          <h3 class="text-2xl font-black text-gray-800">Thêm Nhà Cung Cấp</h3>
          <p class="text-sm text-gray-500 mt-1">Điền chi tiết thông tin nhà phân phối dược</p>
        </div>

        <form @submit.prevent="addSupplier" class="space-y-4 text-sm">
          <div>
            <label for="sup-name" class="block text-sm font-semibold text-gray-700 mb-1.5">Tên doanh nghiệp cung cấp</label>
            <input id="sup-name" v-model="newSupplierForm.name" type="text" required placeholder="Tên công ty dược..." class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none" />
          </div>
          <div>
            <label for="sup-contact" class="block text-sm font-semibold text-gray-700 mb-1.5">Đại diện liên hệ</label>
            <input id="sup-contact" v-model="newSupplierForm.contact" type="text" placeholder="Tên người đại diện..." class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none" />
          </div>
          <div>
            <label for="sup-phone" class="block text-sm font-semibold text-gray-700 mb-1.5">Số điện thoại liên lạc</label>
            <input id="sup-phone" v-model="newSupplierForm.phone" type="tel" placeholder="Số điện thoại bàn/di động..." class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none" />
          </div>
          <div>
            <label for="sup-addr" class="block text-sm font-semibold text-gray-700 mb-1.5">Địa chỉ văn phòng</label>
            <input id="sup-addr" v-model="newSupplierForm.address" type="text" placeholder="Số nhà, tên đường, tỉnh thành..." class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none" />
          </div>

          <div class="flex gap-3 pt-3">
            <button type="button" @click="showAddSupplierModal = false" class="flex-1 py-3 border border-gray-200 rounded-xl font-bold text-gray-650 hover:bg-gray-50 text-sm">
              Hủy
            </button>
            <button type="submit" class="flex-1 py-3 bg-amber-700 hover:bg-amber-800 text-white font-bold rounded-xl text-sm shadow">
              Thêm nhà cung cấp
            </button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>
