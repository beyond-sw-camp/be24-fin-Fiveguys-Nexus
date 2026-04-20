<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <!-- Header -->
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">매장 현황</h1>
      </div>
      <div class="flex items-center gap-2">
        <button v-for="tab in periodTabs" :key="tab" class="text-xs px-3 py-1.5 rounded-md border transition-colors"
          :class="activePeriod === tab ? 'bg-gray-900 text-white border-gray-900' : 'bg-white text-gray-500 border-gray-200 hover:bg-gray-50'"
          @click="activePeriod = tab">
          {{ tab }}
        </button>
        <span class="text-xs text-gray-400 border border-gray-200 px-3 py-1.5 rounded-md bg-white">{{ today }}</span>
      </div>
    </div>

    <!-- KPI 카드 4개 -->
    <div class="grid grid-cols-4 gap-4">
      <RouterLink v-for="kpi in kpis" :key="kpi.title" :to="kpi.to"
        class="block rounded-2xl border border-gray-200 bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.03)] transition-all hover:-translate-y-0.5 hover:shadow-[0_8px_20px_rgba(15,23,42,0.08)]">
        <div>
          <p class="text-[11px] font-semibold text-gray-400 uppercase tracking-[0.14em]">{{ kpi.title }}</p>
          <div class="flex items-end gap-1 mt-2.5">
            <p class="text-[30px] leading-none font-extrabold tracking-tight"
              :class="kpi.alert ? 'text-red-600' : 'text-gray-900'">{{ kpi.value }}</p>
            <span class="text-sm text-gray-400 mb-1">{{ kpi.unit }}</span>
          </div>
        </div>
        <div class="mt-4 flex items-center justify-between">
          <span class="text-xs text-gray-500">{{ kpi.sub }}</span>
          <span class="text-xs font-semibold" :class="kpi.delta > 0 ? 'text-emerald-600' : 'text-red-500'">
            {{ kpi.delta > 0 ? '+' : '' }}{{ kpi.delta }}%
          </span>
        </div>
      </RouterLink>
    </div>

    <!-- 중간 행: 라인차트 + 배송 현황 -->
    <div class="grid grid-cols-3 gap-4">

      <!-- 주간 발주 현황 라인차트 -->
      <div class="col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
        style="min-height:280px">
        <div class="flex items-center justify-between mb-5 shrink-0">
          <div>
            <h2 class="font-bold text-gray-900">주간 발주 현황</h2>
            <p class="text-xs text-gray-400 mt-0.5">이번 주 vs 지난 주 (단위: 건)</p>
          </div>
          <div class="flex items-center gap-4 text-xs text-gray-500">
            <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-orange-500 inline-block rounded"></span>이번 주</span>
            <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-gray-300 inline-block rounded"></span>지난 주</span>
          </div>
        </div>
        <div class="flex-1 min-h-0 relative" style="height:200px">
          <canvas ref="lineCanvas" style="display:block; width:100%; height:100%;"></canvas>
        </div>
      </div>

      <!-- 나의 배송 현황 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col">
        <div class="flex items-center justify-between px-5 pt-5 pb-4 border-b border-gray-100">
          <h2 class="font-bold text-gray-900">나의 배송 현황</h2>
          <RouterLink to="/store-delivery" class="text-xs text-orange-500 font-medium hover:text-orange-600">전체보기</RouterLink>
        </div>
        <div class="flex-1 divide-y divide-gray-50 overflow-y-auto">
          <div v-for="d in ongoingDeliveries" :key="d.id"
            class="px-5 py-4 hover:bg-gray-50 transition-colors cursor-pointer">
            <div class="flex items-start gap-3">
              <div class="w-9 h-9 rounded-xl bg-orange-50 flex items-center justify-center shrink-0 mt-0.5">
                <svg class="w-4 h-4 text-orange-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-xs text-gray-400">발주 {{ d.num }}</p>
                <p class="text-sm font-bold text-gray-900 font-mono">{{ d.id }}</p>
                <p class="text-xs text-gray-500 mt-0.5 truncate">{{ d.items }}</p>
              </div>
              <span class="text-xs px-2 py-0.5 rounded-full font-medium shrink-0" :class="deliveryCls(d.status)">{{ d.status }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Chart } from 'chart.js/auto'

const periodTabs = ['일간', '주간', '월간']
const activePeriod = ref('주간')

const today = computed(() => new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))

const kpis = ref([
  { title: '금일 매출',       value: '84.5', unit: '만원', sub: '전일 대비',          delta: 5.2,  alert: false, to: '/store-settlement' },
  { title: '미확정 발주',     value: '3',    unit: '건',   sub: '자동 발주 승인 대기', delta: 0,    alert: false, to: '/store-order' },
  { title: '재고 위험 품목',  value: '2',    unit: '종',   sub: '최소재고 이하 품목',  delta: -1,   alert: true,  to: '/store-inventory' },
  { title: '이번달 정산 예정', value: '12.5', unit: '만원', sub: '2026-04 납부 예정',  delta: -3.1, alert: false, to: '/store-settlement' },
])

const lineCanvas = ref(null)

const thisWeek = [2, 3, 1, 4, 2, 3, 2]
const lastWeek = [1, 2, 3, 2, 3, 2, 1]
const weekLabels = ['일', '월', '화', '수', '목', '금', '토']

const ongoingDeliveries = ref([
  { num: 1, id: 'ORD-2604-008', items: '우유(1L) 외 2건',    status: '배송중'   },
  { num: 2, id: 'ORD-2604-011', items: '에스프레소 원두 외 1건', status: '출고대기' },
  { num: 3, id: 'ORD-2604-015', items: '바닐라 시럽 외 3건', status: '배송지연' },
  { num: 4, id: 'ORD-2604-003', items: '종이컵(M) 외 1건',   status: '출고완료' },
])

const deliveryCls = s => ({
  '배송중':   'bg-blue-50 text-blue-600 border border-blue-200',
  '출고대기': 'bg-gray-100 text-gray-600 border border-gray-200',
  '출고완료': 'bg-green-50 text-green-700 border border-green-200',
  '배송완료': 'bg-green-50 text-green-700 border border-green-200',
  '배송지연': 'bg-red-50 text-red-600 border border-red-200',
}[s] || 'bg-gray-100 text-gray-500 border border-gray-200')

onMounted(() => {
  new Chart(lineCanvas.value, {
    type: 'line',
    data: {
      labels: weekLabels,
      datasets: [
        {
          label: '이번 주',
          data: thisWeek,
          borderColor: '#f97316',
          backgroundColor: (ctx) => {
            const g = ctx.chart.ctx.createLinearGradient(0, 0, 0, ctx.chart.height)
            g.addColorStop(0, 'rgba(249,115,22,0.18)')
            g.addColorStop(1, 'rgba(249,115,22,0)')
            return g
          },
          fill: true,
          tension: 0.45,
          borderWidth: 2.5,
          pointRadius: (ctx) => ctx.dataIndex === thisWeek.indexOf(Math.max(...thisWeek)) ? 5 : 0,
          pointHoverRadius: 6,
          pointBackgroundColor: '#f97316',
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
        },
        {
          label: '지난 주',
          data: lastWeek,
          borderColor: '#d1d5db',
          backgroundColor: 'transparent',
          fill: false,
          tension: 0.45,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          pointBackgroundColor: '#d1d5db',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1f2937',
          titleColor: '#f9fafb',
          bodyColor: '#d1d5db',
          padding: 10,
          cornerRadius: 10,
          callbacks: { label: (ctx) => ` ${ctx.dataset.label}: ${ctx.parsed.y}건` },
        },
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { color: '#9ca3af', font: { size: 12 } },
          border: { display: false },
        },
        y: {
          min: 0, max: 6,
          grid: { color: '#f3f4f6' },
          ticks: { color: '#9ca3af', font: { size: 11 }, stepSize: 2, precision: 0 },
          border: { display: false },
        },
      },
    },
  })
})
</script>
