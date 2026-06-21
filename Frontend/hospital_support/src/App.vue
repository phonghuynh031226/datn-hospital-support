<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import StaffHeader from './components/StaffHeader.vue'
import StaffFooter from './components/StaffFooter.vue'

const route = useRoute()
const isStaffRoute = computed(() => {
  const staffPaths = ['/dieu-duong', '/bac-si-dashboard', '/duoc-si', '/kho-thuoc', '/giam-doc']
  return staffPaths.some(path => route.path === path || route.path.startsWith(path + '/'))
})
</script>

<template>
  <div class="min-h-screen flex flex-col bg-white">
    <AppHeader v-if="!isStaffRoute" />
    <StaffHeader v-else />
    
    <main class="flex-1">
      <RouterView />
    </main>
    
    <AppFooter v-if="!isStaffRoute" />
    <StaffFooter v-else />
  </div>
</template>


