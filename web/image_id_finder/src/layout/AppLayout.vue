<template>
  <div class="common-layout">
    <div class="logo">
      <el-text size="large" tag="bold">
        资源ID寻找器
      </el-text>
    </div>
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
  .logo{
    height: 50px;
    background: #e1eaf4;
    display: flex;
    justify-content: center;
    align-items: center;
    position: sticky;
    top: 0;
    z-index: 100;
  }
  .aside {
    width: 280px;
    position: fixed;
  }
  .main {
    margin-left: 280px;
    margin-top: 50px;
  }
</style>