<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <!-- Header -->
    <div>
      <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">ESG 대시보드</h1>
      <p class="text-sm text-gray-500 mt-1">폐기 최소화 및 재고 효율 지표를 확인합니다.</p>
    </div>

    <!-- 탭 -->
    <div class="flex gap-1 border-b border-gray-200">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        type="button"
        @click="activeTab = tab.value"
        class="px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px cursor-pointer"
        :class="activeTab === tab.value
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 탭 1: 폐기 최소화 -->
    <template v-if="activeTab === 'waste'">
      <!-- 요약 카드 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div
          v-for="card in wasteCards"
          :key="card.title"
          class="rounded-2xl border border-gray-200 bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.03)]"
        >
          <p class="text-[11px] font-semibold text-gray-400 uppercase tracking-[0.12em]">{{ card.title }}</p>
          <div class="flex items-end gap-1 mt-2.5">
            <p class="text-[26px] leading-none font-extrabold tracking-tight" :class="card.valueClass ?? 'text-gray-900'">
              {{ card.value }}
            </p>
            <span v-if="card.unit" class="text-sm text-gray-400 mb-0.5">{{ card.unit }}</span>
          </div>
          <p class="text-xs text-gray-500 mt-3">{{ card.sub }}</p>
        </div>
      </div>

      <!-- 차트 영역 -->
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-4">
        <!-- 월별 폐기량 추이 -->
        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height: 300px">
          <div class="mb-4 shrink-0">
            <h2 class="font-bold text-gray-900">월별 폐기량 추이</h2>
            <p class="text-xs text-gray-400 mt-0.5">최근 6개월 폐기 수량 (kg)</p>
          </div>
          <div class="flex-1 min-h-0 relative" style="height: 220px">
            <canvas ref="wasteMonthlyCanvas" class="block w-full h-full"></canvas>
          </div>
        </div>

        <!-- 품목별 폐기 비중 -->
        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height: 300px">
          <div class="mb-4 shrink-0">
            <h2 class="font-bold text-gray-900">품목별 폐기 비중</h2>
            <p class="text-xs text-gray-400 mt-0.5">이번달 카테고리별 폐기 비중</p>
          </div>
          <div class="flex-1 min-h-0 relative flex items-center justify-center" style="height: 220px">
            <canvas ref="wasteCategoryCanvas" class="max-w-full" style="max-height: 200px"></canvas>
          </div>
        </div>
      </div>

      <!-- 유통기한 경고 후 소진 현황 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5">
        <div class="mb-4">
          <h2 class="font-bold text-gray-900">유통기한 경고 후 소진 현황</h2>
          <p class="text-xs text-gray-400 mt-0.5">경고 발생 후 소진 성공 / 실패 가맹점 현황</p>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b border-gray-100 bg-gray-50/70">
                <th class="text-left px-4 py-3 font-semibold text-gray-600">가맹점</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">경고 횟수</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">소진 성공</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">소진 실패</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">성공률</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr
                v-for="row in expiryRows"
                :key="row.store"
                class="hover:bg-gray-50/50 transition-colors"
              >
                <td class="px-4 py-3 font-medium text-gray-800">{{ row.store }}</td>
                <td class="px-4 py-3 text-center text-gray-600">{{ row.warned }}</td>
                <td class="px-4 py-3 text-center text-green-600 font-semibold">{{ row.success }}</td>
                <td class="px-4 py-3 text-center text-red-500 font-semibold">{{ row.fail }}</td>
                <td class="px-4 py-3 text-center">
                  <span
                    class="inline-block px-2 py-0.5 rounded-full text-xs font-bold"
                    :class="row.rate >= 80 ? 'bg-green-100 text-green-700' : row.rate >= 60 ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-600'"
                  >
                    {{ row.rate }}%
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <!-- 탭 2: 재고 효율 -->
    <template v-if="activeTab === 'inventory'">
      <!-- 요약 카드 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div
          v-for="card in inventoryCards"
          :key="card.title"
          class="rounded-2xl border border-gray-200 bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.03)]"
        >
          <p class="text-[11px] font-semibold text-gray-400 uppercase tracking-[0.12em]">{{ card.title }}</p>
          <div class="flex items-end gap-1 mt-2.5">
            <p class="text-[26px] leading-none font-extrabold tracking-tight" :class="card.valueClass ?? 'text-gray-900'">
              {{ card.value }}
            </p>
            <span v-if="card.unit" class="text-sm text-gray-400 mb-0.5">{{ card.unit }}</span>
          </div>
          <p class="text-xs text-gray-500 mt-3">{{ card.sub }}</p>
        </div>
      </div>

      <!-- 차트 영역 -->
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-4">
        <!-- 가맹점별 재고 회전율 -->
        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height: 300px">
          <div class="mb-4 shrink-0">
            <h2 class="font-bold text-gray-900">가맹점별 재고 회전율</h2>
            <p class="text-xs text-gray-400 mt-0.5">이번달 기준 (회/월)</p>
          </div>
          <div class="flex-1 min-h-0 relative" style="height: 220px">
            <canvas ref="turnoverCanvas" class="block w-full h-full"></canvas>
          </div>
        </div>

        <!-- 과잉 재고 발생 횟수 -->
        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col" style="min-height: 300px">
          <div class="mb-4 shrink-0">
            <h2 class="font-bold text-gray-900">과잉 재고 발생 횟수</h2>
            <p class="text-xs text-gray-400 mt-0.5">최근 6개월 가맹점별 과잉 재고 발생 건수</p>
          </div>
          <div class="flex-1 min-h-0 relative" style="height: 220px">
            <canvas ref="overStockCanvas" class="block w-full h-full"></canvas>
          </div>
        </div>
      </div>

      <!-- 가맹점별 적정 재고 유지 현황 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5">
        <div class="mb-4">
          <h2 class="font-bold text-gray-900">가맹점별 적정 재고 유지 현황</h2>
          <p class="text-xs text-gray-400 mt-0.5">이번달 기준 적정 재고 유지율 및 자동발주 정확도</p>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b border-gray-100 bg-gray-50/70">
                <th class="text-left px-4 py-3 font-semibold text-gray-600">가맹점</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">적정 재고 유지율</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">재고 회전율</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">과잉 재고 건수</th>
                <th class="text-center px-4 py-3 font-semibold text-gray-600">자동발주 정확도</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr
                v-for="row in inventoryRows"
                :key="row.store"
                class="hover:bg-gray-50/50 transition-colors"
              >
                <td class="px-4 py-3 font-medium text-gray-800">{{ row.store }}</td>
                <td class="px-4 py-3 text-center">
                  <span
                    class="inline-block px-2 py-0.5 rounded-full text-xs font-bold"
                    :class="row.stockRate >= 80 ? 'bg-green-100 text-green-700' : row.stockRate >= 60 ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-600'"
                  >
                    {{ row.stockRate }}%
                  </span>
                </td>
                <td class="px-4 py-3 text-center text-gray-600">{{ row.turnover }}회</td>
                <td class="px-4 py-3 text-center text-gray-600">{{ row.overStock }}건</td>
                <td class="px-4 py-3 text-center">
                  <span
                    class="inline-block px-2 py-0.5 rounded-full text-xs font-bold"
                    :class="row.autoOrderAcc >= 90 ? 'bg-green-100 text-green-700' : row.autoOrderAcc >= 75 ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-600'"
                  >
                    {{ row.autoOrderAcc }}%
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Chart } from 'chart.js/auto'

const tabs = [
  { value: 'waste',     label: '폐기 최소화' },
  { value: 'inventory', label: '재고 효율' },
]
const activeTab = ref('waste')

// ── 폐기 최소화 ───────────────────────────────────────────

const wasteCards = [
  {
    title: '이번달 폐기량',
    value: '142',
    unit: 'kg',
    sub: '전월 대비 -18kg 감소',
  },
  {
    title: '지난달 대비 감소율',
    value: '-11.2',
    unit: '%',
    sub: '전월 160kg → 이번달 142kg',
    valueClass: 'text-green-600',
  },
  {
    title: '폐기 절감 금액',
    value: '₩324,000',
    unit: '',
    sub: '전월 대비 절감 추정액',
    valueClass: 'text-[#F37321]',
  },
  {
    title: '소진 성공률',
    value: '76.4',
    unit: '%',
    sub: '유통기한 경고 후 소진 성공 비율',
  },
]

const wasteMonthlyData = {
  labels: ['11월', '12월', '1월', '2월', '3월', '4월'],
  data: [198, 185, 173, 168, 160, 142],
}

const wasteCategoryData = {
  labels: ['신선육', '사이드·토핑', '양념·소스', '음료', '기타'],
  data: [52, 34, 28, 16, 12],
}

const expiryRows = [
  { store: 'BBQ 강남역점',         warned: 12, success: 10, fail: 2, rate: 83 },
  { store: 'BBQ 홍대입구점',       warned: 9,  success: 7,  fail: 2, rate: 78 },
  { store: 'BBQ 여의도역점',       warned: 7,  success: 6,  fail: 1, rate: 86 },
  { store: 'BBQ 판교테크노밸리점', warned: 11, success: 8,  fail: 3, rate: 73 },
  { store: 'BBQ 잠실새내점',       warned: 6,  success: 3,  fail: 3, rate: 50 },
  { store: 'BBQ 부산서면점',       warned: 8,  success: 6,  fail: 2, rate: 75 },
  { store: 'BBQ 대전둔산점',       warned: 5,  success: 4,  fail: 1, rate: 80 },
  { store: 'BBQ 수원광교점',       warned: 10, success: 9,  fail: 1, rate: 90 },
]

// ── 재고 효율 ────────────────────────────────────────────

const inventoryCards = [
  {
    title: '평균 재고 회전율',
    value: '4.2',
    unit: '회/월',
    sub: '전체 가맹점 평균',
  },
  {
    title: '적정 재고 유지율',
    value: '78.5',
    unit: '%',
    sub: '기준 재고 범위 내 유지 비율',
  },
  {
    title: '과잉 재고 발생',
    value: '37',
    unit: '건',
    sub: '이번달 전체 가맹점 합계',
    valueClass: 'text-amber-500',
  },
  {
    title: '자동발주 정확도',
    value: '88.3',
    unit: '%',
    sub: '자동발주 수량 적중률 평균',
  },
]

const turnoverData = {
  labels: ['강남역', '홍대입구', '여의도역', '판교', '잠실새내', '부산서면', '대전둔산', '수원광교'],
  data: [5.1, 4.8, 4.3, 3.9, 4.6, 3.7, 4.1, 4.5],
}

const overStockMonthlyData = {
  labels: ['11월', '12월', '1월', '2월', '3월', '4월'],
  datasets: [
    { label: '강남역',   data: [8, 7, 6, 5, 5, 4] },
    { label: '홍대입구', data: [5, 6, 4, 5, 4, 3] },
    { label: '판교',     data: [6, 5, 7, 6, 5, 6] },
  ],
}

const inventoryRows = [
  { store: 'BBQ 강남역점',         stockRate: 85, turnover: 5.1, overStock: 4,  autoOrderAcc: 92 },
  { store: 'BBQ 홍대입구점',       stockRate: 80, turnover: 4.8, overStock: 3,  autoOrderAcc: 89 },
  { store: 'BBQ 여의도역점',       stockRate: 78, turnover: 4.3, overStock: 5,  autoOrderAcc: 88 },
  { store: 'BBQ 판교테크노밸리점', stockRate: 65, turnover: 3.9, overStock: 6,  autoOrderAcc: 82 },
  { store: 'BBQ 잠실새내점',       stockRate: 82, turnover: 4.6, overStock: 3,  autoOrderAcc: 91 },
  { store: 'BBQ 부산서면점',       stockRate: 60, turnover: 3.7, overStock: 8,  autoOrderAcc: 75 },
  { store: 'BBQ 대전둔산점',       stockRate: 74, turnover: 4.1, overStock: 5,  autoOrderAcc: 86 },
  { store: 'BBQ 수원광교점',       stockRate: 88, turnover: 4.5, overStock: 3,  autoOrderAcc: 93 },
]

// ── 차트 ─────────────────────────────────────────────────

const wasteMonthlyCanvas  = ref(null)
const wasteCategoryCanvas = ref(null)
const turnoverCanvas      = ref(null)
const overStockCanvas     = ref(null)

let wasteMonthlyChart, wasteCategoryChart, turnoverChart, overStockChart

function destroyCharts() {
  wasteMonthlyChart?.destroy()
  wasteCategoryChart?.destroy()
  turnoverChart?.destroy()
  overStockChart?.destroy()
  wasteMonthlyChart = wasteCategoryChart = turnoverChart = overStockChart = undefined
}

const colors = ['#f97316', '#60a5fa', '#a78bfa', '#4ade80', '#fbbf24', '#94a3b8', '#fb7185', '#2dd4bf']

function renderWasteCharts() {
  wasteMonthlyChart?.destroy()
  wasteCategoryChart?.destroy()

  if (wasteMonthlyCanvas.value) {
    wasteMonthlyChart = new Chart(wasteMonthlyCanvas.value, {
      type: 'line',
      data: {
        labels: wasteMonthlyData.labels,
        datasets: [{
          label: '폐기량 (kg)',
          data: wasteMonthlyData.data,
          borderColor: '#f97316',
          backgroundColor: 'rgba(249,115,22,0.1)',
          fill: true,
          tension: 0.35,
          pointRadius: 4,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: false, grid: { color: '#f3f4f6' } },
          x: { grid: { display: false } },
        },
      },
    })
  }

  if (wasteCategoryCanvas.value) {
    wasteCategoryChart = new Chart(wasteCategoryCanvas.value, {
      type: 'doughnut',
      data: {
        labels: wasteCategoryData.labels,
        datasets: [{
          data: wasteCategoryData.data,
          backgroundColor: wasteCategoryData.labels.map((_, i) => colors[i % colors.length]),
          borderWidth: 2,
          borderColor: '#fff',
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'bottom' },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                const total = ctx.dataset.data.reduce((a, b) => a + b, 0)
                const pct = total ? ((ctx.parsed / total) * 100).toFixed(1) : '0'
                return ` ${ctx.label}: ${ctx.parsed}kg (${pct}%)`
              },
            },
          },
        },
      },
    })
  }
}

function renderInventoryCharts() {
  turnoverChart?.destroy()
  overStockChart?.destroy()

  if (turnoverCanvas.value) {
    turnoverChart = new Chart(turnoverCanvas.value, {
      type: 'bar',
      data: {
        labels: turnoverData.labels,
        datasets: [{
          label: '재고 회전율 (회/월)',
          data: turnoverData.data,
          backgroundColor: turnoverData.data.map(v => v >= 4.5 ? '#4ade80' : v >= 4.0 ? '#f97316' : '#fb7185'),
          borderRadius: 6,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: '#f3f4f6' } },
          x: { grid: { display: false } },
        },
      },
    })
  }

  if (overStockCanvas.value) {
    const dsColors = ['#f97316', '#60a5fa', '#a78bfa']
    overStockChart = new Chart(overStockCanvas.value, {
      type: 'line',
      data: {
        labels: overStockMonthlyData.labels,
        datasets: overStockMonthlyData.datasets.map((ds, i) => ({
          label: ds.label,
          data: ds.data,
          borderColor: dsColors[i % dsColors.length],
          backgroundColor: 'transparent',
          tension: 0.35,
          pointRadius: 4,
        })),
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'top' } },
        scales: {
          y: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: '#f3f4f6' } },
          x: { grid: { display: false } },
        },
      },
    })
  }
}

watch(activeTab, (tab) => {
  nextTick(() => {
    destroyCharts()
    if (tab === 'waste') renderWasteCharts()
    else renderInventoryCharts()
  })
})

onMounted(() => nextTick(renderWasteCharts))
onBeforeUnmount(() => destroyCharts())
</script>
