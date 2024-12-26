<template>
  <div class="container-main w-full">
    <div class="header-line flex justify-center items-center h-10 bg-blue-400 fixed top-0 left-0 w-full z-10">
      <span class="text-white">资源ID寻找器</span>
      <el-menu
        background-color="rgb(96,165,250)"
        text-color="#fff"
        active-text-color="#fff"
        router
        class="el-menu-demo ml-auto h-10 pr-4 sm:hidden block"
        mode="horizontal"
        :ellipsis="false"
      >
        <el-menu-item index="maimai-icon">头像</el-menu-item>
        <el-menu-item index="maimai-plate">姓名框</el-menu-item>
      </el-menu>


          </div>
      <AppMenu/>
    <div class="image-container sm:ml-48 sm:p-4 pt-10 mt-10">
      <router-view/>
      </div>

  </div>
</template>

<script setup lang="ts">
import AppMenu from '@/components/AppMenu.vue';
</script>




