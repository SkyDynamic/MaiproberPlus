<script setup lang="ts">
  import {CopyDocument, Picture as IconPicture} from '@element-plus/icons-vue'
  import {ElMessage} from "element-plus";
  import {computed} from "vue";

  interface Props {
    src: string
    id: string
    width: string
  }

  const props = defineProps<Props>()
  const { src, id, width } = props

  const style = computed(() => {
    return {
      width: width,
    }
  })

  function copyId() {
    navigator.clipboard.writeText(id)
    ElMessage.success('已复制ID')
  }
</script>

<template>
    <div class="image_container">
        <el-image
            class="plateImage"
            size=86
            :style="style"
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
        <el-row :gutter="15">
          <el-col :span="80">
            <el-text>ID: {{ id }}</el-text>
          </el-col>
          <el-col :span="6">
            <el-icon class="copyButton" @click="copyId"><CopyDocument /></el-icon>
          </el-col>
        </el-row>

    </div>
</template>

<style scoped>
  .image_container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 0.5rem;
    margin: 0.5rem;
    min-width: 120px;
    min-height: 120px;
    border: #bababa 1px solid;
    background: #e1eaf4;
    border-radius: 0.5rem;
  }

  .plateImage {
    padding-bottom: 0.5rem;
  }

  .copyButton {
    cursor: pointer;
  }

  .copyButton:hover {
    color: #409EFF;
  }
</style>