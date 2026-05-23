<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { getMonthlySales } from '@/api/statistics'

const currentYear = new Date().getFullYear()
const currentMonth = new Date().getMonth() + 1

const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)
const monthlyData = ref([])  // [{month, total}, ...]

const yearOptions = [2025, 2026]
const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1)

const fetchData = async () => {
  try {
    const { data } = await getMonthlySales(selectedYear.value)
    monthlyData.value = data.result.monthlyData
  } catch (e) {
    console.error('월별 매출 조회 실패', e)
    monthlyData.value = []
  }
}

const selectedTotal = computed(() => {
  const found = monthlyData.value.find((d) => d.month === selectedMonth.value)
  return found ? found.total : 0
})

const formattedTotal = computed(() => {
  return `₩ ${selectedTotal.value.toLocaleString('ko-KR')}`
})

onMounted(fetchData)
watch(() => selectedYear.value, fetchData)
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
    style="min-height:200px">
    <div class="flex items-start justify-between mb-4">
      <div>
        <h2 class="font-bold text-gray-900">월별 매출</h2>
        <p class="text-xs text-gray-400 mt-0.5">선택한 달의 총 매출</p>
      </div>
      <div class="flex gap-2">
        <select v-model.number="selectedYear"
          class="border border-gray-200 rounded-md px-3 py-1.5 text-sm bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-300">
          <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
        </select>
        <select v-model.number="selectedMonth"
          class="border border-gray-200 rounded-md px-3 py-1.5 text-sm bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-300">
          <option v-for="m in monthOptions" :key="m" :value="m">{{ m }}월</option>
        </select>
      </div>
    </div>

    <div class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-xs text-gray-400 mb-2">
          {{ selectedYear }}년 {{ selectedMonth }}월 총 매출
        </p>
        <p class="text-4xl font-extrabold text-orange-600 tracking-tight">
          {{ formattedTotal }}
        </p>
      </div>
    </div>
  </div>
</template>
