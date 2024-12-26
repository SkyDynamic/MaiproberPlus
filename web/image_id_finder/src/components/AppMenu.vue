<template>
  <el-menu
      background-color="#ebf1f5"
      text-color="#606266"
      active-text-color="#2F74FF"
      router
      class="h-full w-48 fixed top-10 left-0 overflow-y-auto overflow-x-hidden hidden  sm:block  "
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