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
    <div class="image-container pl-4">
        <el-image
            class="plateImage w-full pr-4"
            :src="src"
            :fit="'fill'"
            :lazy="true"
            :preview-src-list="[src]"
        >
          <template #error>
            <div class="image-slot">
              <el-icon size=86><icon-picture /></el-icon>
            </div>
          </template>
        </el-image>
        <el-row :gutter="15" class="mt-2 bg-gray-200 w-full">
          <el-col :span="24" class="flex justify-center w-full">
            <span class="text-center flex justify-center items-center">{{ name }}</span>
          </el-col>
          <el-col :span="18" class="flex flex-col">
            <el-text>ID: {{ id }}</el-text>
          </el-col>
          <el-col :span="6" class="">
            <el-icon class="copyButton cursor-pointer float-right hover:text-blue-500 mt-1" @click="copyId"><CopyDocument /></el-icon>
          </el-col>
        </el-row>

    </div>
</template>
