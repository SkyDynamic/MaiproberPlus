<script setup lang="ts">
import {useDark} from "@vueuse/core";
import {Moon, Sunny} from "@element-plus/icons-vue";
import AppMenu from "@/components/AppMenu.vue";

const isDark = useDark()
</script>

<template>
  <el-page-header
    icon=""
    class="of-hidden"
  >
    <template #title>
      <img src="@/assets/logo.png" alt="logo" class="w-16 p-2">
    </template>
    <template #content>
      资源ID寻找器
    </template>
    <template #extra>
      <el-row
        align="middle"
        justify="end"
      >
        <AppMenu mode="horizontal" />
        <el-switch
            v-model="isDark"
            style="
              --el-switch-on-color: #7766ff;
              --el-switch-off-color: #ff7744;
            "
            inline-prompt
            :active-icon="Moon"
            :inactive-icon="Sunny"
            class="p-4"
        />
      </el-row>
    </template>
  </el-page-header>
</template>

<style scoped>

</style>