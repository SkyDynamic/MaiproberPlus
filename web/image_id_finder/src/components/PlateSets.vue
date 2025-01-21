<script setup lang="ts">
import {CopyDocument, Picture as IconPicture} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

interface Props {
  src: string
  id: string
  name:string
}

const props = defineProps<Props>()
const { src, id } = props

function copyId() {
    navigator.clipboard.writeText(id)
    ElMessage.success('已复制ID')
}
</script>

<template>
  <el-card class="mx-4">
    <el-image
        class="w-full"
        :src="src"
        fit="fill"
        :lazy="true"
        :preview-src-list="[src]"
    >
      <template #error>
        <div class="image-slot">
          <el-icon size=86><icon-picture /></el-icon>
        </div>
      </template>
    </el-image>

    <div class="w-fit mx-a">
      <el-text size="large">{{name}}</el-text>
    </div>

    <div class="w-full">
      <el-text>ID: {{id}}</el-text>

      <el-icon
          class="cursor-pointer float-right hover:text-blue-500 mt-1"
          @click="copyId"
      >
        <CopyDocument />
      </el-icon>
    </div>
  </el-card>
</template>
