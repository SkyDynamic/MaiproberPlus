<template>
  <div class="logo">
    <el-text size="large" tag="bold">
      资源ID寻找器
    </el-text>
  </div>
  <el-menu
      default-active="2"
      :collapse="props.isCollapse"
      :collapse-transition="false"
      @open="handleOpen"
      @close="handleClose"
      background-color="#ebf1f5"
      text-color="#606266"
      active-text-color="#2F74FF"
      class="menu"
      router
  >
    <el-sub-menu index="1">
      <template #title>
        <span>舞萌DX</span>
      </template>
      <el-menu-item index="maimai-icon">头像</el-menu-item>
      <el-menu-item index="maimai-plate">姓名框</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script lang="ts" setup>
const props = defineProps(['isCollapse'])
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style>
  .logo{
    height: 50px;
    background: #e1eaf4;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .menu{
    min-height: calc(100vh - 50px);
  }
</style>