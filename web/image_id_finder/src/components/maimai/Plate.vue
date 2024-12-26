<script setup lang="ts">
  import PlateSets from "@/components/Plate-sets.vue";
  import {onMounted, ref, computed} from "vue";
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


  <div class="sets-container">
      <div class="w-full mb-4 flex justify-center">

        <el-pagination size="small" layout="prev, pager, next" :total="filteredPlateList.length" @current-change="handlePageChange" />
      </div>
      <div class="w-full mb-4 flex justify-center">
      <el-input
        class="w-2/4"
        v-model="searchQuery"
        placeholder="请输入查询内容"
        clearable
        @input="handleSearch"
      />
  </div>



    
    </div>
  <div class="img-sets flex flex-wrap justify-center gap-4">
    <div v-for="plate in resultVal" :key="plate.id" >
      <PlateSets
          :name="plate.name"
          :src="`https://assets2.lxns.net/maimai/plate/${plate.id}.png`"
          :id="plate.id.toString()"
        />
      </div>
    </div>

</template>