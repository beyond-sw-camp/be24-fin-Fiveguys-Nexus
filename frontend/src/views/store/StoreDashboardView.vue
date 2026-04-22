<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <!-- Header -->
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">매장 현황</h1>
      </div>
      <span class="text-xs text-gray-400 border border-gray-200 px-3 py-1.5 rounded-md bg-white">{{ today }}</span>
    </div>

    <!-- KPI 카드 4개 -->
    <div class="grid grid-cols-4 gap-4">
      <RouterLink v-for="kpi in kpis" :key="kpi.title" :to="kpi.to"
        class="block rounded-2xl border bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.03)] transition-all hover:-translate-y-0.5 hover:shadow-[0_8px_20px_rgba(15,23,42,0.08)]"
        :class="kpi.alert ? 'border-red-200' : 'border-gray-200'">
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
          <span v-if="kpi.delta !== null" class="text-xs font-semibold"
            :class="kpi.delta > 0 ? 'text-emerald-600' : 'text-red-500'">
            {{ kpi.delta > 0 ? '+' : '' }}{{ kpi.delta }}%
          </span>
        </div>
      </RouterLink>
    </div>

    <!-- 중간 행: 일별 매출 라인차트 + 배송 현황 -->
    <div class="grid grid-cols-3 gap-4">

      <!-- 일별 매출 라인차트 -->
      <div class="col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
        style="min-height:280px">
        <div class="flex items-center justify-between mb-5 shrink-0">
          <div>
            <h2 class="font-bold text-gray-900">일별 매출 추이</h2>
            <p class="text-xs text-gray-400 mt-0.5">이번 주 vs 지난 주 (단위: 만원)</p>
          </div>
          <div class="flex items-center gap-4 text-xs text-gray-500">
            <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-blue-500 inline-block rounded"></span>이번 주</span>
            <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-gray-300 inline-block rounded"></span>지난 주</span>
          </div>
        </div>
        <div class="flex-1 min-h-0 relative" style="height:200px">
          <canvas ref="lineCanvas" style="display:block; width:100%; height:100%;"></canvas>
        </div>
      </div>

      <!-- 나의 배송 현황 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col">
        <div class="px-5 pt-5 pb-4 border-b border-gray-100">
          <h2 class="font-bold text-gray-900">나의 배송 현황</h2>
        </div>
        <div class="flex-1 divide-y divide-gray-50 overflow-y-auto">
          <div v-for="d in ongoingDeliveries" :key="d.id"
            class="px-5 py-4 hover:bg-gray-50 transition-colors cursor-pointer"
            role="button" tabindex="0"
            @click="router.push('/store-delivery')"
            @keydown.enter="router.push('/store-delivery')">
            <div class="flex items-center justify-between gap-3">
              <div class="flex-1 min-w-0">
                <p class="text-sm font-bold text-gray-900 font-mono">{{ d.id }}</p>
                <p class="text-xs text-gray-500 mt-0.5 truncate">{{ d.items }}</p>
              </div>
              <span class="text-xs px-2 py-0.5 rounded-full font-medium shrink-0 border"
                :class="deliveryCls(d.status)">{{ d.status }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 행: 미확정 발주 목록 + 재고 위험 품목 -->
    <div class="grid grid-cols-2 gap-4">

      <!-- 미확정 발주 목록 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
        <div class="px-5 py-4 border-b border-gray-100">
          <h2 class="font-bold text-gray-900">미확정 발주</h2>
        </div>
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">제안 수량</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">금액</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="o in pendingOrders" :key="o.id"
              class="hover:bg-gray-50/50 cursor-pointer"
              role="button" tabindex="0"
              @click="router.push('/store-order')"
              @keydown.enter="router.push('/store-order')">
              <td class="px-5 py-3.5 font-semibold text-gray-800">{{ o.product }}</td>
              <td class="px-5 py-3.5 text-right text-gray-600">{{ o.qty.toLocaleString() }}개</td>
              <td class="px-5 py-3.5 text-right font-semibold text-gray-700">₩ {{ (o.qty * o.unitPrice).toLocaleString() }}</td>
              <td class="px-5 py-3.5 text-right">
                <span class="text-xs font-bold px-2 py-0.5 rounded bg-blue-50 text-blue-600 border border-blue-200">제안중</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 재고 위험 품목 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
        <div class="px-5 py-4 border-b border-gray-100">
          <h2 class="font-bold text-gray-900">재고 위험 품목</h2>
        </div>
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재 재고</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소 재고</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="w in warnings" :key="w.product"
              class="hover:bg-gray-50/50 cursor-pointer"
              role="button" tabindex="0"
              @click="router.push('/store-inventory')"
              @keydown.enter="router.push('/store-inventory')">
              <td class="px-5 py-3.5 font-semibold text-gray-800">{{ w.product }}</td>
              <td class="px-5 py-3.5 text-right font-bold text-red-600">{{ w.current }}</td>
              <td class="px-5 py-3.5 text-right text-gray-400">{{ w.min }}</td>
              <td class="px-5 py-3.5 text-right">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="w.danger ? 'bg-red-50 text-red-600 border border-red-200' : 'bg-orange-50 text-orange-600 border border-orange-200'">
                  {{ w.danger ? '위험' : '주의' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Chart } from 'chart.js/auto'

const router = useRouter()

const today = computed(() => new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))

const kpis = ref([
  { title: '금일 매출',        value: '84.5', unit: '만원', sub: '전일 대비',           delta: 5.2,  alert: false, to: '/store-settlement' },
  { title: '미확정 발주',      value: '3',    unit: '건',   sub: '자동 발주 승인 대기',  delta: null, alert: false, to: '/store-order'      },
  { title: '재고 위험 품목',   value: '2',    unit: '종',   sub: '최소재고 이하 품목',   delta: null, alert: true,  to: '/store-inventory'  },
  { title: '이번달 정산 예정', value: '12.5', unit: '만원', sub: '2026-04 납부 예정',   delta: -3.1, alert: false, to: '/store-settlement' },
])

const lineCanvas = ref(null)
let lineChart = null

const thisWeek  = [72, 85, 68, 91, 84, 96, 80]
const lastWeek  = [65, 78, 74, 82, 79, 88, 71]
const allValues = computed(() => [...thisWeek, ...lastWeek].filter(v => typeof v === 'number'))
const yMin = computed(() => allValues.value.length ? Math.floor(Math.min(...allValues.value) / 10) * 10 - 10 : 0)
const yMax = computed(() => allValues.value.length ? Math.ceil(Math.max(...allValues.value) / 10) * 10 + 10 : 100)
const weekLabels = ['일', '월', '화', '수', '목', '금', '토']

const ongoingDeliveries = ref([
  { id: 'ORD-2604-008', items: '생닭(1kg) 외 1건',      status: '배송중'   },
  { id: 'ORD-2604-011', items: '튀김유(18L) 외 1건',    status: '출고대기' },
  { id: 'ORD-2604-015', items: '황금올리브소스 외 2건', status: '배송지연' },
  { id: 'ORD-2604-003', items: '치킨박스(중) 외 1건',   status: '출고완료' },
])

const pendingOrders = ref([
  { id: 1, product: '생닭(1kg)',      qty: 100, unitPrice: 5000  },
  { id: 2, product: '황금올리브소스', qty: 30,  unitPrice: 12000 },
  { id: 3, product: '치킨박스(중)',   qty: 500, unitPrice: 150   },
])

const warnings = ref([
  { product: '튀김유(18L)', current: '3통', min: '5통',  danger: true  },
  { product: '치킨파우더',  current: '8kg', min: '20kg', danger: false },
])

const DELIVERY_STATUS_CLS = {
  '배송중':   'bg-blue-50 text-blue-600 border-blue-200',
  '출고대기': 'bg-gray-100 text-gray-600 border-gray-200',
  '출고완료': 'bg-green-50 text-green-700 border-green-200',
  '배송완료': 'bg-green-50 text-green-700 border-green-200',
  '배송지연': 'bg-red-50 text-red-600 border-red-200',
}
const deliveryCls = s => DELIVERY_STATUS_CLS[s] ?? 'bg-gray-100 text-gray-500 border-gray-200'

onUnmounted(() => {
  lineChart?.destroy()
})

onMounted(() => {
  lineChart = new Chart(lineCanvas.value, {
    type: 'line',
    data: {
      labels: weekLabels,
      datasets: [
        {
          label: '이번 주',
          data: thisWeek,
          borderColor: '#3b82f6',
          backgroundColor: (ctx) => {
            const g = ctx.chart.ctx.createLinearGradient(0, 0, 0, ctx.chart.height)
            g.addColorStop(0, 'rgba(59,130,246,0.18)')
            g.addColorStop(1, 'rgba(59,130,246,0)')
            return g
          },
          fill: true,
          tension: 0.45,
          borderWidth: 2.5,
          pointRadius: (ctx) => ctx.dataIndex === thisWeek.indexOf(Math.max(...thisWeek)) ? 5 : 0,
          pointHoverRadius: 6,
          pointBackgroundColor: '#3b82f6',
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
          callbacks: { label: (ctx) => ` ${ctx.dataset.label}: ${ctx.parsed.y}만원` },
        },
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { color: '#9ca3af', font: { size: 12 } },
          border: { display: false },
        },
        y: {
          min: yMin.value, max: yMax.value,
          grid: { color: '#f3f4f6' },
          ticks: { color: '#9ca3af', font: { size: 11 }, stepSize: 20 },
          border: { display: false },
        },
      },
    },
  })
})
</script>
