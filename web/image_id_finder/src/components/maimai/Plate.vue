<script setup lang="ts">
import PlateSets from "@/components/PlateSets.vue";
import {computed, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";

const plateList = ref<{ id: number, name: string, description: string, genre: string }[]>([]);
  const searchQuery = ref('')
  const filteredPlateList = ref<{ id: number, name: string, description: string, genre: string }[]>([])
  const pages = ref(1)

  const SkeletonOn = ref(false)


  async function fetchPlateList() {
    const response = await fetch('https://maimai.lxns.net/api/v0/maimai/plate/list', {
      method: 'GET',
    });
    const data = await response.json()
    return data.plates;
  }


  function handleSearch() {
    if (searchQuery.value) {
      filteredPlateList.value = plateList.value.filter(plate => plate.name.includes(searchQuery.value) || plate.description.includes(searchQuery.value) || plate.genre.includes(searchQuery.value) || plate.id.toString().includes(searchQuery.value))
    } else {
      filteredPlateList.value = plateList.value
    }
  }


  const handlePageChange = (page: number) => {
    pages.value = page
  }

  const resultVal = computed(() => {
    return filteredPlateList.value.slice((pages.value - 1) * 10, pages.value * 10)
  })

  onMounted(() => {
    fetchPlateList().then(data => {
      plateList.value = data;
      filteredPlateList.value = plateList.value
    }).catch(error => {
      ElMessage.error('获取失败, 请稍后再试: ' + error.message)
    });
  })


</script>

<template>
  <div class="fixed z-10 top-18 w-full">
    <div class="md:mr-48">
      <div class="w-50% mx-a my-4 p-2 radius-10 b-solid b-1 b-[var(--el-border-color)] bg-[var(--el-bg-color)]">
        <el-input
          v-model="searchQuery"
          placeholder="请输入查询内容"
          clearable
          @input="handleSearch"
        />
      </div>
    </div>
  </div>

  <div class="fixed z-10 bottom-4 w-full">
    <div class="md:mr-48">
      <el-card class="w-fit mx-a">
        <el-pagination size="small" layout="prev, pager, next" :total="filteredPlateList.length" @current-change="handlePageChange" />
      </el-card>
    </div>
  </div>

  <div class="flex flex-wrap justify-center gap-4 mt-24">
    <div v-for="plate in resultVal" :key="plate.id" >
      <PlateSets
          :name="plate.name"
          :src="`https://assets2.lxns.net/maimai/plate/${plate.id}.png`"
          :id="plate.id.toString()"
        />
      </div>
    </div>

  <div class="h-48 w-full" />
</template>

<style scoped>
.radius-10 {
  border-radius: 10px;
}
</style>
