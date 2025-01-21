<script setup lang="ts">
import {computed} from "vue";

const props = defineProps<{
  mode?: 'horizontal' | 'vertical'
}>()

const defaultMenuMode = 'vertical'

const menuMode = computed(() => {
  if (props.mode !== null) return props.mode
  return defaultMenuMode
})

</script>

<template>
  <el-menu
      router
      :mode="menuMode"
  >
    <el-sub-menu index="1">
      <template #title>
        <span>舞萌DX</span>
      </template>
      <el-menu-item index="/maimai/icon">头像</el-menu-item>
      <el-menu-item index="/maimai/plate">姓名框</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>