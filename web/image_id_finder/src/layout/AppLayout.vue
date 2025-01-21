<script setup lang="ts">
import AppMenu from '@/components/AppMenu.vue';
import TopBar from "@/components/TopBar.vue";
</script>

<template>
  <TopBar class="fixed w-full h-16 z-2 border-solid b-0 b-b-1 border-[var(--el-border-color)]" />
  <AppMenu class="fixed w-48 h-full mt-16 hidden md:block" />

  <el-scrollbar
    class="fixed z-1 bottom-0 right-0 w-full h-100vh md:pl-48"
  >
    <div class="h-16 block">&nbsp;</div>
    <router-view />
  </el-scrollbar>
</template>

<style>
.el-image-viewer__close {
  top: calc(40px + 4rem);
}
</style>
