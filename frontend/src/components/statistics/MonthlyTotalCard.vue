<script setup>
import { ref, onMounted, watch, computed, onUnmounted, nextTick } from 'vue'
import { Chart } from 'chart.js/auto'
import { getMonthlySales } from '@/api/statistics'
import { formatCurrency, calculateChange } from '@/utils/formatCurrency'

const currentYear = new Date().getFullYear()
const currentMonth = new Date().getMonth() + 1

const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)

const currentYearData = ref([])    // 선택 연도 monthly
const previousYearData = ref([])   // 작년 monthly (YoY 계산용)

const yearOptions = [2025, 2026]
const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1)

const sparklineCanvas = ref(null)
let chartInstance = null

const fetchData = async () => {
  try {
    const [curr, prev] = await Promise.all([
      getMonthlySales(selectedYear.value),
      getMonthlySales(selectedYear.value - 1),
    ])
    currentYearData.value = curr.data.result.monthlyData
    previousYearData.value = prev.data.result.monthlyData
    await nextTick()
    renderSparkline()
  } catch (e) {
    console.error('월별 매출 조회 실패', e)
  }
}

const selectedTotal = computed(() => {
  const found = currentYearData.value.find((d) => d.month === selectedMonth.value)
  return found ? found.total : 0
})

// YoY (작년 동월 대비)
const yoy = computed(() => {
  const prev = previousYearData.value.find((d) => d.month === selectedMonth.value)?.total
  if (prev == null) return null
  return calculateChange(selectedTotal.value, prev)
})

const formattedTotal = computed(() => formatCurrency(selectedTotal.value))

const renderSparkline = () => {
  if (!sparklineCanvas.value) return

  const labels = monthOptions.map((m) => `${m}월`)
  const totals = new Array(12).fill(0)
  currentYearData.value.forEach((d) => {
    totals[d.month - 1] = d.total
  })

  // 선택 월 포인트만 강조
  const pointRadii = monthOptions.map((m) => (m === selectedMonth.value ? 5 : 0))
  const pointColors = monthOptions.map((m) =>
    m === selectedMonth.value ? '#f97316' : 'rgba(0,0,0,0)',
  )

  if (chartInstance) chartInstance.destroy()

  chartInstance = new Chart(sparklineCanvas.value, {
    type: 'line',
    data: {
      labels,
      datasets: [{
        data: totals,
        borderColor: '#f97316',
        backgroundColor: 'rgba(249,115,22,0.15)',
        fill: true,
        tension: 0.3,
        pointRadius: pointRadii,
        pointBackgroundColor: pointColors,
        pointBorderColor: pointColors,
        borderWidth: 2,
      }],
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
watch(() => selectedYear.value, fetchData)
watch(() => selectedMonth.value, () => renderSparkline())
onUnmounted(() => {
  if (chartInstance) chartInstance.destroy()
})
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height:200px">
    <div class="flex items-start justify-between mb-4">
      <div>
        <h2 class="font-bold text-gray-900">월별 매출</h2>
        <p class="text-xs text-gray-400 mt-0.5">선택 달의 총 매출</p>
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

    <div class="flex-1 flex items-center gap-6">
      <!-- 큰 숫자 + YoY -->
      <div class="flex-shrink-0 min-w-[140px]">
        <p class="text-3xl font-extrabold text-orange-600 tracking-tight">
          {{ formattedTotal }}
        </p>
        <p v-if="yoy" class="text-xs mt-1.5 font-semibold"
          :class="yoy.isPositive ? 'text-emerald-600' : 'text-red-500'">
          {{ yoy.text }}
          <span class="text-gray-400 font-normal">vs 작년 동월</span>
        </p>
        <p v-else class="text-xs mt-1.5 text-gray-400">작년 동월 데이터 없음</p>
      </div>

      <!-- Sparkline (선택 월 강조) -->
      <div class="flex-1 relative" style="height: 70px; min-width: 100px;">
        <canvas ref="sparklineCanvas" style="display:block; width:100%; height:100%;"></canvas>
      </div>
    </div>
  </div>
</template>
