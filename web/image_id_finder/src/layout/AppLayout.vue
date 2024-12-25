<template>
  <div class="common-layout">
    <el-container class="container">
      <el-aside class="aside">
        <AppMenu/>
      </el-aside>
      <el-container>
        <el-main class="main"><router-view/></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup >
  import AppMenu from '@/components/AppMenu.vue';
</script>

<style scoped lang="scss">
  .aside {
    width: 280px;
    position: fixed;
  }
  .main {
    margin-left: 280px;
  }
</style>