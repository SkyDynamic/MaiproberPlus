<script setup lang="ts">
import ImageContent from "@/components/ImageContent.vue";
import {onMounted, ref} from "vue";

const iconList = ref<{ id: number, name: string, description: string, genre: string }[]>([]);

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
</script>

<template>
  <div class="container">
    <div v-for="icon in iconList" :key="icon.id">
      <ImageContent
          width="120px"
          :src="`https://assets2.lxns.net/maimai/icon/${icon.id}.png`"
          :id="icon.id.toString()"
      />
    </div>
  </div>
</template>

<style scoped>
  .container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 1rem;
  }
</style>