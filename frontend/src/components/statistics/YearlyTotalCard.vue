<script setup>
import { ref, onMounted, computed, watch, onUnmounted, nextTick } from 'vue'
import { Chart } from 'chart.js/auto'
import { getYearlySales } from '@/api/statistics'
import { formatCurrency, calculateChange } from '@/utils/formatCurrency'

const allYears = ref([])      // [{year, total}, ...]
const selectedYear = ref(null)
const sparklineCanvas = ref(null)
let chartInstance = null

const fetchData = async () => {
  try {
    const { data } = await getYearlySales()
    allYears.value = data.result.yearlyData
    // 기본값: 가장 최신 연도
    if (allYears.value.length > 0 && selectedYear.value == null) {
      selectedYear.value = allYears.value[allYears.value.length - 1].year
    }
    await nextTick()
    renderSparkline()
  } catch (e) {
    console.error('연도별 매출 조회 실패', e)
  }
}

const sortedYears = computed(() =>
  [...allYears.value].sort((a, b) => a.year - b.year),
)

const selectedTotal = computed(() => {
  const found = allYears.value.find((d) => d.year === selectedYear.value)
  return found ? found.total : 0
})

// YoY (전년 대비)
const yoy = computed(() => {
  const sorted = sortedYears.value
  const idx = sorted.findIndex((d) => d.year === selectedYear.value)
  if (idx <= 0) return null
  return calculateChange(sorted[idx].total, sorted[idx - 1].total)
})

const formattedTotal = computed(() => formatCurrency(selectedTotal.value))
const yearOptions = computed(() => sortedYears.value.map((d) => d.year))

const renderSparkline = () => {
  if (!sparklineCanvas.value || sortedYears.value.length === 0) return

  const labels = sortedYears.value.map((d) => `${d.year}`)
  const totals = sortedYears.value.map((d) => d.total)

  // 선택된 연도만 진하게, 나머지는 연하게
  const backgroundColors = sortedYears.value.map((d) =>
    d.year === selectedYear.value ? 'rgba(249,115,22,0.9)' : 'rgba(249,115,22,0.3)',
  )

  if (chartInstance) chartInstance.destroy()

  chartInstance = new Chart(sparklineCanvas.value, {
    type: 'bar',
    data: {
      labels,
      datasets: [{ data: totals, backgroundColor: backgroundColors, borderRadius: 3, barPercentage: 0.6 }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1f2937',
          titleColor: '#f9fafb',
          bodyColor: '#d1d5db',
          padding: 8,
          cornerRadius: 8,
          callbacks: { label: (ctx) => ` ${formatCurrency(ctx.parsed.y)}` },
        },
      },
      scales: {
        x: { display: false },
        y: { display: false, beginAtZero: true },
      },
    },
  })
}

onMounted(fetchData)
watch(selectedYear, () => renderSparkline())
onUnmounted(() => {
  if (chartInstance) chartInstance.destroy()
})
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height:200px">
    <div class="flex items-start justify-between mb-4">
      <div>
        <h2 class="font-bold text-gray-900">연도별 매출</h2>
        <p class="text-xs text-gray-400 mt-0.5">선택 연도의 총 매출</p>
      </div>
      <select v-model.number="selectedYear"
        class="border border-gray-200 rounded-md px-3 py-1.5 text-sm bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-300">
        <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
      </select>
    </div>

    <div class="flex-1 flex items-center gap-6">
      <!-- 큰 숫자 + YoY -->
      <div class="flex-shrink-0 min-w-[140px]">
        <p class="text-3xl font-extrabold text-orange-600 tracking-tight">
          {{ formattedTotal }}
        </p>
        <p v-if="yoy" class="text-xs mt-1.5 font-semibold"
          :class="yoy.isPositive ? 'text-emerald-600' : 'text-red-500'">
          {{ yoy.text }}
          <span class="text-gray-400 font-normal">vs 전년</span>
        </p>
        <p v-else class="text-xs mt-1.5 text-gray-400">전년 데이터 없음</p>
      </div>

      <!-- Sparkline (선택 연도 강조) -->
      <div class="flex-1 relative" style="height: 70px; min-width: 100px;">
        <canvas ref="sparklineCanvas" style="display:block; width:100%; height:100%;"></canvas>
      </div>
    </div>
  </div>
</template>
