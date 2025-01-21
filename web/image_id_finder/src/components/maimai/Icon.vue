<script setup lang="ts">
import ImageContent from "@/components/ImageContent.vue";
import {computed, onMounted, ref} from "vue";

const iconList = ref<{ id: number, name: string, description: string, genre: string }[]>([]);
const pages = ref(1)

async function fetchIconList() {
  const response = await fetch('https://maimai.lxns.net/api/v0/maimai/icon/list', {
    method: 'GET',
  });
  const data = await response.json()
  return data.icons;
}

onMounted(() => {
  fetchIconList().then(data => {
    iconList.value = data;
  }).catch(error => {
    console.error('Error fetching plate list:', error);
  });
})

const handlePageChange = (page: number) => {
  pages.value = page
}

const resultVal = computed(() => {
  return iconList.value.slice((pages.value - 1) * 50, pages.value * 50)
})

</script>

<template>
  <div
    class="fixed z-10 bottom-4 w-full"
  >
    <div class="md:mr-48">
      <el-card class="w-fit mx-a">
        <el-pagination
            size="small"
            layout="prev, pager, next"
            :total="iconList.length"
            :default-page-size="50"
            @current-change="handlePageChange"
        />
      </el-card>
    </div>
  </div>

  <div class="flex flex-wrap justify-center gap-4 mt-4">
    <div v-for="icon in resultVal" :key="icon.id">

      <ImageContent
          width="120px"
          :src="`https://assets2.lxns.net/maimai/icon/${icon.id}.png`"
          :id="icon.id.toString()"
      />
    </div>
  </div>

  <div class="h-24 w-full" />
</template>

