import { createRouter, createWebHistory } from 'vue-router'

/* ====== Public Views ====== */
import HomeView from '../views/public/HomeView.vue'

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
    component: () => import('../views/public/AboutView.vue'),
  },
  {
    path: '/tin-tuc',
    name: 'news',
    component: () => import('../views/public/NewsListView.vue'),
  },
  {
    path: '/tin-tuc/:id',
    name: 'news-detail',
    component: () => import('../views/public/NewsDetailView.vue'),
  },
  {
    path: '/lien-he',
    name: 'contact',
    component: () => import('../views/public/ContactView.vue'),
  },
  {
    path: '/huong-dan',
    name: 'guide',
    component: () => import('../views/public/GuideView.vue'),
  },
  {
    path: '/bac-si',
    name: 'doctors',
    component: () => import('../views/public/DoctorsView.vue'),
  },

  /* ---------- Auth ---------- */
  {
    path: '/dang-nhap',
    name: 'login',
    component: () => import('../views/auth/LoginView.vue'),
  },
  {
    path: '/dang-ky',
    name: 'register',
    component: () => import('../views/auth/RegisterView.vue'),
  },
  {
    path: '/quen-mat-khau',
    name: 'forgot-password',
    component: () => import('../views/auth/ForgotPasswordView.vue'),
  },
  {
    path: '/dat-lai-mat-khau',
    name: 'reset-password',
    component: () => import('../views/auth/ResetPasswordView.vue'),
  },

  /* ---------- Patient ---------- */
  {
    path: '/dat-lich',
    name: 'booking',
    component: () => import('../views/patient/BookingView.vue'),
  },
  {
    path: '/ho-so',
    name: 'patient-profile',
    component: () => import('../views/patient/PatientProfileView.vue'),
  },
  {
    path: '/lich-su-kham',
    name: 'medical-history',
    component: () => import('../views/patient/MedicalHistoryView.vue'),
  },
  /* ---------- Doctor ---------- */
  {
    path: '/thong-tin-bac-si',
    name: 'doctor-thong-tin-bac-si',
    component: () => import('../views/doctor/DoctorProfileView.vue'),
  }
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

export default router
