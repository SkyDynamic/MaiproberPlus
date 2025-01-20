<script setup lang="ts">
import AppMenu from '@/components/AppMenu.vue';
import TopBar from "@/components/TopBar.vue";
</script>

<template>
  <div class="w-100vw h-100vh fixed">
    <TopBar class="fixed w-full h-16 z-10 border-solid b-0 b-b-1 border-gray-600" />
    <AppMenu class="fixed w-48 h-full mt-16 hidden md:block" />
    <el-scrollbar
      class="mt-16 md:ml-48 h-full pb-16"
    >
      <router-view />
    </el-scrollbar>
  </div>
</template>

<style scoped>
</style>
