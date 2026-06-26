<script setup>
import { ref, computed, onMounted } from 'vue'

const activeMenu = ref('catalog') // catalog (xem ton kho), import (nhap lo), classification (phan loai), expiry (bao cao het han)
const stock = ref([])

// Mock suppliers list for selection during import
const suppliers = ref([
  { id: 1, name: 'Dược phẩm TW1 (Pharmedic)', contact: 'Bà Nguyễn Thị Hoa', phone: '028 3829 1234', address: 'Quận 1, TP.HCM' },
  { id: 2, name: 'Công ty cổ phần Traphaco', contact: 'Ông Trần Văn Nam', phone: '024 3766 2288', address: 'Quận Hoàng Mai, Hà Nội' },
  { id: 3, name: 'Dược Hậu Giang (DHG Pharma)', contact: 'Bà Lê Thu Trang', phone: '0292 3891 433', address: 'Bình Thủy, Cần Thơ' }
])

// Stats variables
const stats = ref({
  totalItems: 0,
  totalQty: 0,
  nearExpiry: 0,
  expiredCount: 0
})

// Search & Filter state
const searchQuery = ref('')
const selectedCategoryFilter = ref('Tất cả')

// Forms
const importForm = ref({
  name: '',
  qty: 50,
  unit: 'Viên',
  expiryDate: '2027-12-31',
  minStock: 20,
  supplier: 'Dược phẩm TW1 (Pharmedic)',
  category: 'Giảm đau & Hạ sốt',
  price: 5000
})

const classMedName = ref('')
const classCategory = ref('')

onMounted(() => {
  loadStock()
})

function loadStock() {
  const mockStock = [
    { name: 'Panadol Extra 500mg', qty: 120, heldQty: 0, unit: 'Viên', expiryDate: '2027-06-30', minStock: 20, price: 2000, category: 'Giảm đau & Hạ sốt' },
    { name: 'Concor 2.5mg (Bisoprolol fumarate)', qty: 85, heldQty: 0, unit: 'Viên', expiryDate: '2026-09-15', minStock: 25, price: 8000, category: 'Tim mạch' },
    { name: 'Nexium mups 20mg', qty: 15, heldQty: 0, unit: 'Viên', expiryDate: '2027-11-20', minStock: 30, price: 15000, category: 'Dạ dày & Tiêu hóa' },
    { name: 'Phosphalugel (Thuốc chữ P)', qty: 60, heldQty: 0, unit: 'Gói', expiryDate: '2026-08-01', minStock: 15, price: 6000, category: 'Dạ dày & Tiêu hóa' },
    { name: 'Pharmaton Essential', qty: 45, heldQty: 0, unit: 'Viên', expiryDate: '2027-04-10', minStock: 20, price: 5000, category: 'Vitamin & Bổ dưỡng' },
    { name: 'Amoxicillin 500mg', qty: 9, heldQty: 0, unit: 'Viên', expiryDate: '2027-09-30', minStock: 25, price: 3000, category: 'Kháng sinh' },
    { name: 'Siro ho Prospan (Chai)', qty: 30, heldQty: 0, unit: 'Chai', expiryDate: '2027-10-15', minStock: 5, price: 75000, category: 'Hô hấp' }
  ]
  const data = localStorage.getItem('warehouseStock')
  if (data) {
    const parsed = JSON.parse(data)
    stock.value = parsed.map(item => {
      const match = mockStock.find(m => m.name.toLowerCase() === item.name.toLowerCase())
      return {
        heldQty: 0,
        price: 5000,
        category: 'Khác',
        ...match,
        ...item
      }
    })
    if (!stock.value.some(s => s.name.toLowerCase().includes('siro ho'))) {
      const siro = mockStock.find(m => m.name.includes('Siro ho'))
      if (siro) {
        stock.value.push(siro)
        localStorage.setItem('warehouseStock', JSON.stringify(stock.value))
      }
    }
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
  
  const today = new Date()
  const limit = new Date()
  limit.setMonth(today.getMonth() + 6) // within 6 months

  stats.value.nearExpiry = list.filter(item => {
    const exp = new Date(item.expiryDate)
    return exp > today && exp <= limit
  }).length

  stats.value.expiredCount = list.filter(item => {
    const exp = new Date(item.expiryDate)
    return exp <= today
  }).length
}

const categories = computed(() => {
  const cats = new Set(stock.value.map(s => s.category || 'Khác'))
  return ['Tất cả', ...Array.from(cats)]
})

const categoriesWithoutAll = computed(() => {
  const cats = new Set(stock.value.map(s => s.category || 'Khác'))
  return Array.from(cats)
})

const filteredStock = computed(() => {
  return stock.value.filter(s => {
    const matchSearch = s.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchCat = selectedCategoryFilter.value === 'Tất cả' || (s.category || 'Khác') === selectedCategoryFilter.value
    return matchSearch && matchCat
  })
})

const expiredList = computed(() => {
  const today = new Date()
  return stock.value.filter(item => {
    const exp = new Date(item.expiryDate)
    return exp <= today
  })
})

const nearExpiryList = computed(() => {
  const today = new Date()
  const limit = new Date()
  limit.setMonth(today.getMonth() + 6)
  return stock.value.filter(item => {
    const exp = new Date(item.expiryDate)
    return exp > today && exp <= limit
  })
})

function submitImport() {
  const f = importForm.value
  if (!f.name.trim()) {
    alert('Vui lòng nhập tên dược phẩm!')
    return
  }
  if (f.qty <= 0) {
    alert('Số lượng nhập lô phải lớn hơn 0!')
    return
  }
  const list = [...stock.value]
  const idx = list.findIndex(item => item.name.toLowerCase() === f.name.trim().toLowerCase())

  if (idx !== -1) {
    list[idx].qty += f.qty
    list[idx].expiryDate = f.expiryDate
    if (f.category) list[idx].category = f.category
    if (f.price) list[idx].price = f.price
  } else {
    list.push({
      name: f.name.trim(),
      qty: f.qty,
      heldQty: 0,
      unit: f.unit,
      expiryDate: f.expiryDate,
      minStock: f.minStock,
      price: f.price || 5000,
      category: f.category || 'Khác'
    })
  }

  localStorage.setItem('warehouseStock', JSON.stringify(list))
  stock.value = list
  calculateStats()
  
  // Reset form
  importForm.value = {
    name: '',
    qty: 50,
    unit: 'Viên',
    expiryDate: '2027-12-31',
    minStock: 20,
    supplier: 'Dược phẩm TW1 (Pharmedic)',
    category: 'Giảm đau & Hạ sốt',
    price: 5000
  }
  alert('🎉 Đã xác nhận nhập kho lô thuốc thành công!')
}

function updateClassification() {
  if (!classMedName.value) {
    alert('Vui lòng chọn thuốc cần phân loại!')
    return
  }
  if (!classCategory.value.trim()) {
    alert('Vui lòng điền tên phân loại/nhóm thuốc!')
    return
  }
  
  const list = [...stock.value]
  const idx = list.findIndex(s => s.name === classMedName.value)
  if (idx !== -1) {
    list[idx].category = classCategory.value.trim()
    stock.value = list
    localStorage.setItem('warehouseStock', JSON.stringify(list))
    alert(`🏷️ Đã phân loại thành công thuốc "${classMedName.value}" vào nhóm "${classCategory.value.trim()}".`)
    classMedName.value = ''
    classCategory.value = ''
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
    alert(`⚡ Nhập nhanh bổ sung 100 đơn vị cho thuốc ${name}!`)
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
            :class="activeMenu === 'catalog' ? 'bg-amber-50 text-amber-850 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-box-seam text-lg"></i>
            Xem tồn kho thuốc
          </button>
          
          <button 
            @click="activeMenu = 'import'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'import' ? 'bg-amber-50 text-amber-850 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-file-earmark-plus text-lg"></i>
            Nhập lô thuốc
          </button>

          <button 
            @click="activeMenu = 'classification'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'classification' ? 'bg-amber-50 text-amber-850 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-tags text-lg"></i>
            Phân loại thuốc
          </button>

          <button 
            @click="activeMenu = 'expiry'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'expiry' ? 'bg-amber-50 text-amber-850 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-calendar-x-fill text-lg text-rose-600"></i>
            Báo cáo thuốc hết hạn
          </button>
        </nav>
      </div>

      <div class="p-2 border-t text-xs text-gray-400 text-center">
        Hospital Inventory v3.0
      </div>
    </aside>

    <!-- Main Content Area -->
    <main class="flex-1 p-6 md:p-8 overflow-y-auto">
      
      <!-- Statistics summary boxes customized for the warehouse keeper roles -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Tổng Số Mặt Hàng</span>
            <h4 class="text-2xl font-black text-amber-700 mt-1">{{ stats.totalItems }}</h4>
          </div>
          <span class="text-3xl">💊</span>
        </div>
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Tổng Tồn Kho</span>
            <h4 class="text-2xl font-black text-gray-800 mt-1">{{ stats.totalQty }}</h4>
          </div>
          <span class="text-3xl">📦</span>
        </div>
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between"
             :class="stats.expiredCount > 0 ? 'bg-rose-50 border-rose-200' : ''">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Đã Hết Hạn</span>
            <h4 class="text-2xl font-black text-rose-600 mt-1">{{ stats.expiredCount }}</h4>
          </div>
          <span class="text-3xl">🚨</span>
        </div>
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between"
             :class="stats.nearExpiry > 0 ? 'bg-amber-50 border-amber-200' : ''">
          <div>
            <span class="text-xs text-gray-400 font-bold uppercase">Cận Hạn (6T)</span>
            <h4 class="text-2xl font-black text-amber-600 mt-1">{{ stats.nearExpiry }}</h4>
          </div>
          <span class="text-3xl">⏳</span>
        </div>
      </div>

      <!-- ==================== MENU 1: VIEW INVENTORY (XEM TỒN KHO) ==================== -->
      <div v-if="activeMenu === 'catalog'" class="space-y-6 animate-fade-in">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Tồn kho dược phẩm thực tế</h2>
            <p class="text-sm text-gray-400">Xem tồn kho vật lý, hàng giữ chỗ khả dụng và hạn dùng.</p>
          </div>
          <!-- Search & Filter Controls -->
          <div class="flex items-center gap-2 flex-wrap">
            <div class="relative w-64">
              <input 
                v-model="searchQuery" 
                type="text" 
                placeholder="Tìm tên thuốc..." 
                class="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200" 
              />
              <i class="bi bi-search absolute left-3 top-2.5 text-gray-400 text-xs"></i>
            </div>
            <select 
              v-model="selectedCategoryFilter" 
              class="px-3 py-2 text-xs rounded-xl border border-gray-200 focus:outline-none bg-white"
            >
              <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
            </select>
          </div>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Tên thuốc</th>
                  <th class="py-3.5 px-6">Nhóm phân loại</th>
                  <th class="py-3.5 px-6 text-center">Tồn kho vật lý</th>
                  <th class="py-3.5 px-6 text-center">Khả dụng (Để kê)</th>
                  <th class="py-3.5 px-6">Đơn vị</th>
                  <th class="py-3.5 px-6">Hạn dùng (HSD)</th>
                  <th class="py-3.5 px-6 text-center">Bổ sung nhanh</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="item in filteredStock" :key="item.name" class="hover:bg-gray-50/30">
                  <td class="py-4 px-6 font-bold text-gray-850">{{ item.name }}</td>
                  <td class="py-4 px-6">
                    <span class="px-2.5 py-1 bg-gray-100 text-gray-700 text-xs font-semibold rounded-lg">
                      {{ item.category || 'Khác' }}
                    </span>
                  </td>
                  <td class="py-4 px-6 text-center">
                    <span class="font-bold text-gray-800">{{ item.qty }}</span>
                    <span class="text-[10px] text-gray-400 block font-normal mt-0.5">(Giữ chỗ: {{ item.heldQty || 0 }})</span>
                  </td>
                  <td class="py-4 px-6 text-center text-amber-800 font-black bg-amber-50/20">
                    {{ item.qty - (item.heldQty || 0) }}
                  </td>
                  <td class="py-4 px-6 text-gray-500 font-semibold">{{ item.unit }}</td>
                  <td class="py-4 px-6 font-mono text-xs">{{ item.expiryDate }}</td>
                  <td class="py-4 px-6 text-center">
                    <button @click="quickRestock(item.name)" class="py-1.5 px-3 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-200 rounded-xl text-xs font-bold transition-all">
                      +100 bổ sung
                    </button>
                  </td>
                </tr>
                <tr v-if="filteredStock.length === 0">
                  <td colspan="7" class="py-8 text-center text-gray-400 text-xs">Không tìm thấy thuốc nào khớp bộ lọc</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 2: IMPORT BATCH (NHẬP LÔ THUỐC) ==================== -->
      <div v-else-if="activeMenu === 'import'" class="max-w-xl mx-auto animate-fade-in">
        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-4">
          <h3 class="text-xl font-bold text-gray-850 flex items-center gap-2 border-b pb-3">
            <i class="bi bi-box-arrow-in-down text-amber-700 text-2xl"></i> Phiếu Nhập Lô Thuốc Mới
          </h3>
          <p class="text-xs text-gray-400">Ghi nhận thông tin nhập kho lô thuốc, HSD và phân nhóm ban đầu.</p>
          
          <form @submit.prevent="submitImport" class="space-y-4 text-sm">
            <div>
              <label for="med-name-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Tên thuốc nhập kho</label>
              <input id="med-name-in" type="text" v-model="importForm.name" required class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl" placeholder="Ví dụ: Panadol Extra 500mg, Amoxicillin..." />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label for="med-qty-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Số lượng nhập</label>
                <input id="med-qty-in" type="number" min="1" v-model="importForm.qty" required class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl text-center font-bold" />
              </div>
              <div>
                <label for="med-unit-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Đơn vị tính</label>
                <select id="med-unit-in" v-model="importForm.unit" class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl bg-white">
                  <option value="Viên">Viên</option>
                  <option value="Gói">Gói</option>
                  <option value="Chai">Chai</option>
                  <option value="Tuýp">Tuýp</option>
                </select>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label for="med-price-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Giá bán / Đơn vị (đ)</label>
                <input id="med-price-in" type="number" min="500" step="500" v-model="importForm.price" required class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl text-center" />
              </div>
              <div>
                <label for="med-min-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Tồn tối thiểu an toàn</label>
                <input id="med-min-in" type="number" min="1" v-model="importForm.minStock" required class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl text-center" />
              </div>
            </div>

            <div>
              <label for="med-exp-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Hạn sử dụng (HSD)</label>
              <input id="med-exp-in" type="date" v-model="importForm.expiryDate" required class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl" />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label for="med-cat-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Nhóm phân loại</label>
                <select id="med-cat-in" v-model="importForm.category" class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl bg-white">
                  <option value="Giảm đau & Hạ sốt">Giảm đau & Hạ sốt</option>
                  <option value="Kháng sinh">Kháng sinh</option>
                  <option value="Tim mạch">Tim mạch</option>
                  <option value="Dạ dày & Tiêu hóa">Dạ dày & Tiêu hóa</option>
                  <option value="Hô hấp">Hô hấp</option>
                  <option value="Vitamin & Bổ dưỡng">Vitamin & Bổ dưỡng</option>
                  <option value="Khác">Khác</option>
                </select>
              </div>
              <div>
                <label for="med-sup-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Nhà cung cấp dược</label>
                <select id="med-sup-in" v-model="importForm.supplier" class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl bg-white">
                  <option v-for="s in suppliers" :key="s.id" :value="s.name">{{ s.name }}</option>
                </select>
              </div>
            </div>

            <button type="submit" class="w-full py-3 bg-amber-700 hover:bg-amber-800 text-white font-bold rounded-xl shadow mt-2 transition-all">
              Nhập kho lô thuốc
            </button>
          </form>
        </div>
      </div>

      <!-- ==================== MENU 3: DRUG CLASSIFICATION (PHÂN LOẠI THUỐC) ==================== -->
      <div v-else-if="activeMenu === 'classification'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Phân loại & Gán nhóm dược phẩm</h2>
          <p class="text-sm text-gray-400">Xếp các biệt dược vào nhóm lâm sàng tương ứng (Kháng sinh, Tim mạch, Giảm đau...).</p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
          
          <!-- Category list columns (Takes 2/3 width) -->
          <div class="lg:col-span-2 space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div 
                v-for="cat in categoriesWithoutAll" 
                :key="cat" 
                class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm space-y-3"
              >
                <h3 class="font-black text-sm text-amber-800 border-b pb-2 flex items-center gap-1.5">
                  <i class="bi bi-tag-fill text-amber-600"></i>
                  {{ cat }}
                  <span class="text-xs text-gray-450 font-normal ml-auto">({{ stock.filter(s => s.category === cat).length }} mặt hàng)</span>
                </h3>
                <ul class="space-y-1.5 text-xs text-gray-700">
                  <li 
                    v-for="med in stock.filter(s => s.category === cat)" 
                    :key="med.name"
                    class="flex justify-between items-center py-1 bg-gray-50 px-2 rounded-lg"
                  >
                    <span class="font-semibold text-gray-850">{{ med.name }}</span>
                    <span class="text-gray-400 font-mono">{{ med.qty }} {{ med.unit }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Adjust category form (Takes 1/3 width) -->
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-4">
            <h3 class="text-base font-bold text-gray-800 border-b pb-2 flex items-center gap-1.5">
              <i class="bi bi-pencil-square text-amber-600"></i> Cập nhật phân loại
            </h3>
            <form @submit.prevent="updateClassification" class="space-y-4 text-xs">
              <div>
                <label for="class-med" class="block text-xs font-bold text-gray-400 uppercase mb-1.5">Chọn biệt dược</label>
                <select 
                  id="class-med" 
                  v-model="classMedName" 
                  class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl bg-white"
                >
                  <option value="">-- Chọn thuốc cần đổi nhóm --</option>
                  <option v-for="item in stock" :key="item.name" :value="item.name">{{ item.name }} ({{ item.category }})</option>
                </select>
              </div>

              <div>
                <label for="class-cat" class="block text-xs font-bold text-gray-400 uppercase mb-1.5">Gán vào nhóm / Phân loại</label>
                <input 
                  id="class-cat" 
                  type="text" 
                  v-model="classCategory" 
                  placeholder="Gõ nhóm mới hoặc nhóm sẵn có..." 
                  list="category-suggestions"
                  class="w-full px-3 py-2.5 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-200 rounded-xl"
                />
                <datalist id="category-suggestions">
                  <option v-for="cat in categoriesWithoutAll" :key="cat" :value="cat" />
                </datalist>
              </div>

              <button 
                type="submit" 
                class="w-full py-2.5 bg-amber-700 hover:bg-amber-800 text-white font-bold rounded-xl transition-all shadow"
              >
                Xác nhận đổi nhóm
              </button>
            </form>
          </div>

        </div>
      </div>

      <!-- ==================== MENU 4: EXPIRED MEDICINE REPORT (BÁO CÁO THUỐC HẾT HẠN) ==================== -->
      <div v-else-if="activeMenu === 'expiry'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-rose-800">Báo cáo thuốc hết hạn & Cận hạn sử dụng</h2>
          <p class="text-sm text-gray-400">Danh mục thuốc đã quá hạn sử dụng cần hủy bỏ và thuốc cận hạn dưới 6 tháng cần ưu tiên phân phối.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          
          <!-- Hết hạn sử dụng -->
          <div class="bg-white p-6 rounded-3xl border border-red-100 shadow-sm space-y-4">
            <h3 class="text-base font-bold text-red-700 border-b pb-2 flex items-center justify-between">
              <span>🚨 ĐÃ HẾT HẠN SỬ DỤNG</span>
              <span class="text-xs font-black px-2 py-0.5 bg-red-100 rounded text-red-800">Cần xử lý hủy</span>
            </h3>
            
            <div class="divide-y divide-gray-50">
              <div v-for="med in expiredList" :key="med.name" class="py-3 flex justify-between items-center text-xs">
                <div>
                  <h4 class="font-bold text-gray-850">{{ med.name }}</h4>
                  <p class="text-[10px] text-gray-450">Nhóm: {{ med.category }}</p>
                </div>
                <div class="text-right">
                  <span class="font-mono font-bold text-red-655 block">HSD: {{ med.expiryDate }}</span>
                  <span class="text-[10px] text-red-500 font-bold">Số lượng tồn kho: {{ med.qty }} {{ med.unit }}</span>
                </div>
              </div>

              <div v-if="expiredList.length === 0" class="py-8 text-center text-gray-400 text-xs italic">
                Tuyệt vời! Hiện tại không có loại thuốc nào bị hết hạn sử dụng.
              </div>
            </div>
          </div>

          <!-- Cận hạn sử dụng -->
          <div class="bg-white p-6 rounded-3xl border border-amber-100 shadow-sm space-y-4">
            <h3 class="text-base font-bold text-amber-700 border-b pb-2 flex items-center justify-between">
              <span>⏳ CẬN HẠN SỬ DỤNG (DƯỚI 6 THÁNG)</span>
              <span class="text-xs font-black px-2 py-0.5 bg-amber-100 rounded text-amber-800">Ưu tiên xuất kê</span>
            </h3>
            
            <div class="divide-y divide-gray-50">
              <div v-for="med in nearExpiryList" :key="med.name" class="py-3 flex justify-between items-center text-xs">
                <div>
                  <h4 class="font-bold text-gray-850">{{ med.name }}</h4>
                  <p class="text-[10px] text-gray-450">Nhóm: {{ med.category }}</p>
                </div>
                <div class="text-right">
                  <span class="font-mono font-bold text-amber-800 block">HSD: {{ med.expiryDate }}</span>
                  <span class="text-[10px] text-gray-500">Số lượng tồn kho: {{ med.qty }} {{ med.unit }}</span>
                </div>
              </div>

              <div v-if="nearExpiryList.length === 0" class="py-8 text-center text-gray-400 text-xs italic">
                Không có thuốc cận hạn sử dụng nào dưới 6 tháng.
              </div>
            </div>
          </div>

        </div>
      </div>

    </main>
  </div>
</template>
