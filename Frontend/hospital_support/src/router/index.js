import { createRouter, createWebHistory } from 'vue-router'

/* ====== Public Views ====== */
import HomeView from '../views/public/TrangChuView.vue'

const routes = [
  /* ---------- Public ---------- */
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/gioi-thieu',
    name: 'about',
    component: () => import('../views/public/GioiThieuView.vue'),
  },
  {
    path: '/tin-tuc',
    name: 'news',
    component: () => import('../views/public/DanhSachTinTucView.vue'),
  },
  {
    path: '/tin-tuc/:id',
    name: 'news-detail',
    component: () => import('../views/public/ChiTietTinTucView.vue'),
  },
  {
    path: '/lien-he',
    name: 'contact',
    component: () => import('../views/public/LienHeView.vue'),
  },
  {
    path: '/huong-dan',
    name: 'guide',
    component: () => import('../views/public/HuongDanView.vue'),
  },
  {
    path: '/bac-si',
    name: 'doctors',
    component: () => import('../views/public/DanhSachBacSiView.vue'),
  },

  /* ---------- Auth ---------- */
  {
    path: '/dang-nhap',
    name: 'login',
    component: () => import('../views/auth/DangNhapView.vue'),
  },
  {
    path: '/dang-ky',
    name: 'register',
    component: () => import('../views/auth/DangKyView.vue'),
  },
  {
    path: '/quen-mat-khau',
    name: 'forgot-password',
    component: () => import('../views/auth/QuenMatKhauView.vue'),
  },
  {
    path: '/dat-lai-mat-khau',
    name: 'reset-password',
    component: () => import('../views/auth/DatLaiMatKhauView.vue'),
  },

  /* ---------- Patient ---------- */
  {
    path: '/dat-lich',
    name: 'booking',
    component: () => import('../views/patient/DatLichView.vue'),
  },
  {
    path: '/ho-so',
    name: 'patient-profile',
    component: () => import('../views/patient/HoSoBenhNhanView.vue'),
  },
  {
    path: '/lich-su-kham',
    name: 'medical-history',
    component: () => import('../views/patient/LichSuKhamView.vue'),
  },
  {
    path: '/don-thuoc',
    name: 'prescriptions',
    component: () => import('../views/patient/DonThuocView.vue'),
  },
  {
    path: '/so-do',
    name: 'hospital-map',
    component: () => import('../views/patient/BanDoView.vue'),
  },

  /* ---------- Nurse ---------- */
  {
    path: '/dieu-duong',
    name: 'nurse-dashboard',
    component: () => import('../views/nurse/DieuDuongDashboard.vue'),
  },

  /* ---------- Doctor ---------- */
  {
    path: '/bac-si-dashboard',
    name: 'doctor-dashboard', 
    component: () => import('../views/doctor/BacSiDashboard.vue'),
  },
  {
    path: '/ho-so-nhan-vien',
    name: 'staff-profile',
    component: () => import('../views/doctor/HoSoNhanVienView.vue'),
  },

  /* ---------- Pharmacist ---------- */
  {
    path: '/duoc-si',
    name: 'pharmacist-dashboard',
    component: () => import('../views/pharmacist/DuocSiDashboard.vue'),
  },

  /* ---------- Warehouse ---------- */
  {
    path: '/kho-thuoc',
    name: 'warehouse-dashboard',
    component: () => import('../views/warehouse/KhoThuocDashboard.vue'),
  },

  /* ---------- Director ---------- */
  {
    path: '/giam-doc',
    name: 'director-dashboard',
    component: () => import('../views/director/GiamDocDashboard.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash) return { el: to.hash, behavior: 'smooth' }
    return { top: 0, behavior: 'smooth' }
  },
})

router.beforeEach((to, from, next) => {
  const userJson = localStorage.getItem('currentUser')
  const currentUser = userJson ? JSON.parse(userJson) : null
  const userRole = currentUser ? currentUser.role : null

  // 1. Danh sách đường dẫn cho từng vai trò
  const patientPaths = ['/dat-lich', '/ho-so', '/lich-su-kham', '/don-thuoc', '/so-do']
  const nursePaths = ['/dieu-duong']
  const doctorPaths = ['/bac-si', '/ho-so-nhan-vien']
  const pharmacistPaths = ['/duoc-si']
  const warehousePaths = ['/kho-thuoc']
  const directorPaths = ['/giam-doc']
  const authPaths = ['/dang-nhap', '/dang-ky', '/quen-mat-khau', '/dat-lai-mat-khau']

  // Nếu người dùng đã đăng nhập mà cố truy cập các trang đăng nhập/đăng ký/quên mật khẩu
  if (currentUser && authPaths.includes(to.path)) {
    if (userRole === 'patient') return next('/dat-lich')
    if (userRole === 'nurse') return next('/dieu-duong')
    if (userRole === 'doctor') return next('/bac-si')
    if (userRole === 'pharmacist') return next('/duoc-si')
    if (userRole === 'warehouse') return next('/kho-thuoc') 
    if (userRole === 'director') return next('/giam-doc')
  }

  // Kiểm tra quyền hạn truy cập của người dùng
  if (patientPaths.includes(to.path)) {
    if (!currentUser) return next('/dang-nhap')
    if (userRole !== 'patient') return next('/')
  }
  
  if (nursePaths.includes(to.path)) {
    if (!currentUser) return next('/dang-nhap')
    if (userRole !== 'nurse') return next('/')
  }

  if (doctorPaths.includes(to.path)) {
    if (!currentUser) return next('/dang-nhap')
    if (userRole !== 'doctor') return next('/')
  }

  if (pharmacistPaths.includes(to.path)) {
    if (!currentUser) return next('/dang-nhap')
    if (userRole !== 'pharmacist') return next('/')
  }

  if (warehousePaths.includes(to.path)) {
    if (!currentUser) return next('/dang-nhap')
    if (userRole !== 'warehouse') return next('/')
  }

  if (directorPaths.includes(to.path)) {
    if (!currentUser) return next('/dang-nhap')
    if (userRole !== 'director') return next('/')
  }

  next()
})

export default router
