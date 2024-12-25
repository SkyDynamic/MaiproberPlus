<script setup lang="ts">
  import ImageContent from "@/components/ImageContent.vue";
  import {onMounted, ref} from "vue";

  const plateList = ref<{ id: number, name: string, description: string, genre: string }[]>([]);

  async function fetchPlateList() {
    const response = await fetch('https://maimai.lxns.net/api/v0/maimai/plate/list', {
      method: 'GET',
    });
    const data = await response.json()
    return data.plates;
  }

  onMounted(() => {
    fetchPlateList().then(data => {
      plateList.value = data;
    }).catch(error => {
      console.error('Error fetching plate list:', error);
    });
  })
</script>

<template>
  <div class="container">
    <div v-for="plate in plateList" :key="plate.id">
      <ImageContent
          width="480px"
          :src="`https://assets2.lxns.net/maimai/plate/${plate.id}.png`"
          :id="plate.id.toString()"
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