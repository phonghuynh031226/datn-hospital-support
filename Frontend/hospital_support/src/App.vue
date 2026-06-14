<script setup>
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col bg-white">
    <AppHeader />
    <main class="flex-1">
      <RouterView />
    </main>
    <AppFooter />
  </div>
</template>
