<script setup>
import { ref, onMounted, computed } from 'vue'
import { getYearlySales } from '@/api/statistics'

const allYears = ref([])      // [{year, total}, ...]
const selectedYear = ref(null)

const fetchData = async () => {
  try {
    const { data } = await getYearlySales()
    allYears.value = data.result.yearlyData
    // 기본값: 가장 최신 연도
    if (allYears.value.length > 0 && selectedYear.value == null) {
      selectedYear.value = allYears.value[allYears.value.length - 1].year
    }
  } catch (e) {
    console.error('연도별 매출 조회 실패', e)
  }
}

const selectedTotal = computed(() => {
  const found = allYears.value.find((d) => d.year === selectedYear.value)
  return found ? found.total : 0
})

const formattedTotal = computed(() => {
  return `₩ ${selectedTotal.value.toLocaleString('ko-KR')}`
})

const yearOptions = computed(() => allYears.value.map((d) => d.year))

onMounted(fetchData)
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
    style="min-height:200px">
    <div class="flex items-start justify-between mb-4">
      <div>
        <h2 class="font-bold text-gray-900">연도별 매출</h2>
        <p class="text-xs text-gray-400 mt-0.5">선택한 연도의 총 매출</p>
      </div>
      <select v-model.number="selectedYear"
        class="border border-gray-200 rounded-md px-3 py-1.5 text-sm bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-300">
        <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
      </select>
    </div>

    <div class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-xs text-gray-400 mb-2">{{ selectedYear }}년 총 매출</p>
        <p class="text-4xl font-extrabold text-orange-600 tracking-tight">
          {{ formattedTotal }}
        </p>
      </div>
    </div>
  </div>
</template>
