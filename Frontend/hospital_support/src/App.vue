<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import HeaderChung from './components/HeaderChung.vue'
import FooterChung from './components/FooterChung.vue'
import HeaderNhanVien from './components/HeaderNhanVien.vue'
import FooterNhanVien from './components/FooterNhanVien.vue'

const route = useRoute()
const isStaffRoute = computed(() => {
  const staffPaths = ['/dieu-duong', '/bac-si-dashboard', '/duoc-si', '/kho-thuoc', '/giam-doc']
  return staffPaths.some(path => route.path === path || route.path.startsWith(path + '/'))
})
</script>

<template>
  <div class="min-h-screen flex flex-col bg-white">
    <HeaderChung v-if="!isStaffRoute" />
    <HeaderNhanVien v-else />
    
    <main class="flex-1">
      <RouterView />
    </main>
    
    <FooterChung v-if="!isStaffRoute" />
    <FooterNhanVien v-else />
  </div>
</template>


