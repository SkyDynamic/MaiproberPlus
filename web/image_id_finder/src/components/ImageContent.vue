<script setup lang="ts">
import {CopyDocument, Picture as IconPicture} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

interface Props {
  src: string
  id: string
}

const props = defineProps<Props>()
const { src, id } = props

function copyId() {
    navigator.clipboard.writeText(id)
    ElMessage.success('已复制ID')
}
</script>

<template>
  <el-card>
    <el-image
        class="w-24 pb-4"
        :src="src"
        :fit="'fill'"
        :lazy="true"
        :preview-src-list="[src]"

    >
      <template #error>
        <div class="w-24 h-24">
          <el-icon size=64><icon-picture /></el-icon>
        </div>
      </template>
    </el-image>

    <div class="w-full">
      <el-text>ID: {{id}}</el-text>
      <el-icon class="float-right pt-1" @click="copyId">
        <CopyDocument />
      </el-icon>
    </div>

  </el-card>
</template>
