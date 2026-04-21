<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">판매/매출 분석</h1>
        <p class="text-sm text-gray-500 mt-1">
          가맹점·제품·카테고리 기준 매출 현황을 분석합니다.
        </p>
      </div>
      <div class="flex flex-wrap items-center gap-2">
        <select
          v-model="selectedYear"
          class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
        >
          <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
        </select>
        <select
          v-model="selectedMonth"
          class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
        >
          <option :value="0">전체 월</option>
          <option v-for="m in 12" :key="m" :value="m">{{ m }}월</option>
        </select>
      </div>
    </div>

    <!-- 요약 카드 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div
        v-for="card in summaryCards"
        :key="card.title"
        class="rounded-2xl border border-gray-200 bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.03)]"
      >
        <p class="text-[11px] font-semibold text-gray-400 uppercase tracking-[0.12em]">{{ card.title }}</p>
        <div class="flex items-end gap-1 mt-2.5">
          <p class="text-[22px] leading-none font-extrabold text-gray-900 tracking-tight">{{ card.value }}</p>
          <span v-if="card.unit" class="text-sm text-gray-400 mb-0.5">{{ card.unit }}</span>
        </div>
        <p class="text-xs text-gray-500 mt-3">{{ card.sub }}</p>
      </div>
    </div>

    <!-- 핵심 분석 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5">
      <div class="flex items-center justify-between mb-4">
        <div class="flex gap-1 rounded-lg border border-gray-200 bg-gray-50 p-0.5">
          <button
            v-for="mode in analysisModes"
            :key="mode.value"
            type="button"
            @click="analysisMode = mode.value"
            class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
            :class="analysisMode === mode.value ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
          >
            {{ mode.label }}
          </button>
        </div>
        <button
          type="button"
          @click="showFullList = true"
          class="text-xs px-3 py-1.5 rounded-lg border border-gray-200 font-semibold text-gray-600 hover:bg-gray-50 transition-colors"
        >
          전체 목록 보기
        </button>
      </div>

      <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <!-- 상위 5 -->
        <div class="flex flex-col gap-3">
          <h3 class="text-sm font-bold text-gray-700 flex items-center gap-1.5">
            <span class="w-2 h-2 rounded-full bg-[#F37321] inline-block"></span>
            상위 5
          </h3>
          <div
            v-for="(item, i) in top5"
            :key="item.label"
            class="flex items-center gap-3 p-3 rounded-xl bg-orange-50/40 border border-orange-100/60"
          >
            <span class="w-6 h-6 rounded-full bg-[#F37321] text-white text-xs font-bold flex items-center justify-center shrink-0">
              {{ i + 1 }}
            </span>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold text-gray-800 truncate">{{ item.label }}</p>
              <div class="mt-1.5 h-1.5 rounded-full bg-orange-100 overflow-hidden">
                <div class="h-full rounded-full bg-[#F37321] transition-all duration-500" :style="{ width: `${item.pct}%` }"></div>
              </div>
            </div>
            <span class="text-sm font-bold text-gray-900 shrink-0">₩{{ item.amount.toLocaleString() }}</span>
          </div>
          <p v-if="top5.length === 0" class="text-sm text-gray-400 py-4 text-center">데이터 없음</p>
        </div>

        <!-- 하위 5 -->
        <div class="flex flex-col gap-3">
          <h3 class="text-sm font-bold text-gray-700 flex items-center gap-1.5">
            <span class="w-2 h-2 rounded-full bg-gray-400 inline-block"></span>
            하위 5
          </h3>
          <div
            v-for="(item, i) in bottom5"
            :key="item.label"
            class="flex items-center gap-3 p-3 rounded-xl bg-gray-50 border border-gray-100"
          >
            <span class="w-6 h-6 rounded-full bg-gray-400 text-white text-xs font-bold flex items-center justify-center shrink-0">
              {{ rankingData.length - bottom5.length + i + 1 }}
            </span>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold text-gray-800 truncate">{{ item.label }}</p>
              <div class="mt-1.5 h-1.5 rounded-full bg-gray-200 overflow-hidden">
                <div class="h-full rounded-full bg-gray-400 transition-all duration-500" :style="{ width: `${item.pct}%` }"></div>
              </div>
            </div>
            <span class="text-sm font-bold text-gray-900 shrink-0">₩{{ item.amount.toLocaleString() }}</span>
          </div>
          <p v-if="bottom5.length === 0" class="text-sm text-gray-400 py-4 text-center">항목이 부족합니다</p>
        </div>
      </div>
    </div>

    <!-- 월별 매출 추이 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height: 320px">
      <div class="mb-4 shrink-0">
        <h2 class="font-bold text-gray-900">월별 매출 추이</h2>
        <p class="text-xs text-gray-400 mt-0.5">{{ selectedYear }}년 기준 월별 총 매출 합계</p>
      </div>
      <div class="flex-1 min-h-0 relative" style="height: 240px">
        <canvas ref="trendCanvas" class="block w-full h-full"></canvas>
      </div>
    </div>

    <!-- 전체 목록 모달 -->
    <Teleport to="body">
      <div v-if="showFullList" class="fixed inset-0 z-50 flex items-center justify-center">
        <div class="absolute inset-0 bg-black/40" @click="showFullList = false"></div>
        <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl mx-4 max-h-[80vh] flex flex-col overflow-hidden">
          <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100 shrink-0">
            <span class="font-bold text-gray-900 text-sm">{{ currentModeLabel }} 전체 매출 순위</span>
            <button @click="showFullList = false" class="text-gray-400 hover:text-gray-700 transition-colors">
              <X class="w-5 h-5" />
            </button>
          </div>
          <div class="overflow-y-auto flex-1">
            <table class="w-full text-sm">
              <thead class="sticky top-0 bg-white border-b border-gray-100">
                <tr>
                  <th class="text-left px-5 py-3 font-semibold text-gray-500 w-12">순위</th>
                  <th class="text-left px-5 py-3 font-semibold text-gray-500">{{ currentModeLabel }}</th>
                  <th class="text-right px-5 py-3 font-semibold text-gray-500">매출액</th>
                  <th class="text-right px-5 py-3 font-semibold text-gray-500 w-20">비중</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <tr
                  v-for="(item, i) in rankingData"
                  :key="item.label"
                  class="hover:bg-gray-50/50 transition-colors"
                >
                  <td class="px-5 py-3 text-gray-400">{{ i + 1 }}</td>
                  <td class="px-5 py-3 font-medium text-gray-800">{{ item.label }}</td>
                  <td class="px-5 py-3 text-right text-gray-800">₩{{ item.amount.toLocaleString() }}</td>
                  <td class="px-5 py-3 text-right text-gray-500">{{ item.pct.toFixed(1) }}%</td>
                </tr>
                <tr v-if="rankingData.length === 0">
                  <td colspan="4" class="px-5 py-12 text-center text-gray-400">데이터 없음</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { X } from 'lucide-vue-next'
import { Chart } from 'chart.js/auto'
import { rawOrders } from '@/utils/orderData'

const selectedYear = ref(2026)
const selectedMonth = ref(0)
const analysisMode = ref('store')
const showFullList = ref(false)

const yearOptions = [2026, 2025]

const analysisModes = [
  { value: 'store',    label: '가맹점별' },
  { value: 'product',  label: '제품별' },
  { value: 'category', label: '카테고리별' },
]

const currentModeLabel = computed(() =>
  analysisModes.find(m => m.value === analysisMode.value)?.label ?? ''
)

const filteredOrders = computed(() =>
  rawOrders.filter(o => {
    const d = new Date(o.date)
    if (d.getFullYear() !== selectedYear.value) return false
    if (selectedMonth.value !== 0 && d.getMonth() + 1 !== selectedMonth.value) return false
    return true
  })
)

const rankingData = computed(() => {
  const map = new Map()
  for (const o of filteredOrders.value) {
    const key = analysisMode.value === 'store' ? o.store
      : analysisMode.value === 'product' ? o.product
      : o.category
    map.set(key, (map.get(key) || 0) + o.amount)
  }
  const total = [...map.values()].reduce((s, v) => s + v, 0)
  return [...map.entries()]
    .sort((a, b) => b[1] - a[1])
    .map(([label, amount]) => ({ label, amount, pct: total ? (amount / total) * 100 : 0 }))
})

const top5 = computed(() => rankingData.value.slice(0, 5))
const bottom5 = computed(() => {
  const all = rankingData.value
  return all.length > 5 ? all.slice(-5) : []
})

const summaryCards = computed(() => {
  const orders = filteredOrders.value
  const total = orders.reduce((s, o) => s + o.amount, 0)

  const storeMap = new Map()
  for (const o of orders) storeMap.set(o.store, (storeMap.get(o.store) || 0) + o.amount)
  const storeCount = storeMap.size
  const avg = storeCount ? total / storeCount : 0
  const topStore = rankingData.value[0]?.label ?? '-'

  const prevOrders = rawOrders.filter(o => {
    const d = new Date(o.date)
    return d.getFullYear() === selectedYear.value - 1
      && (selectedMonth.value === 0 || d.getMonth() + 1 === selectedMonth.value)
  })
  const prevTotal = prevOrders.reduce((s, o) => s + o.amount, 0)
  const growth = prevTotal > 0 ? ((total - prevTotal) / prevTotal) * 100 : null

  return [
    {
      title: '총 매출',
      value: `₩${Math.round(total).toLocaleString()}`,
      unit: '',
      sub: '선택 기간 전체 매출 합계',
    },
    {
      title: '가맹점 평균',
      value: `₩${Math.round(avg).toLocaleString()}`,
      unit: '',
      sub: `활성 가맹점 ${storeCount}개 기준 평균`,
    },
    {
      title: '매출 1위',
      value: topStore,
      unit: '',
      sub: '선택 기간 매출 최상위 항목',
    },
    {
      title: '전년 동기 대비',
      value: growth !== null ? `${growth >= 0 ? '+' : ''}${growth.toFixed(1)}` : 'N/A',
      unit: growth !== null ? '%' : '',
      sub: '직전 연도 동일 기간 대비 변동률',
    },
  ]
})

const monthlyTrend = computed(() => {
  const map = new Map(Array.from({ length: 12 }, (_, i) => [i + 1, 0]))
  for (const o of rawOrders.filter(o => new Date(o.date).getFullYear() === selectedYear.value)) {
    const m = new Date(o.date).getMonth() + 1
    map.set(m, (map.get(m) || 0) + o.amount)
  }
  return {
    labels: Array.from({ length: 12 }, (_, i) => `${i + 1}월`),
    data: Array.from({ length: 12 }, (_, i) => map.get(i + 1) || 0),
  }
})

const trendCanvas = ref(null)
let trendChart

function destroyChart() {
  trendChart?.destroy()
  trendChart = undefined
}

function renderChart() {
  destroyChart()
  if (!trendCanvas.value) return
  const { labels, data } = monthlyTrend.value
  trendChart = new Chart(trendCanvas.value, {
    type: 'bar',
    data: {
      labels,
      datasets: [{
        label: '월별 매출 (원)',
        data,
        backgroundColor: '#f97316',
        borderRadius: 6,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: {
          beginAtZero: true,
          ticks: { callback: v => `₩${Number(v / 1e8).toFixed(1)}억` },
          grid: { color: '#f3f4f6' },
        },
        x: { grid: { display: false } },
      },
    },
  })
}

watch(selectedYear, () => nextTick(renderChart))
onMounted(() => nextTick(renderChart))
onBeforeUnmount(() => destroyChart())
</script>
