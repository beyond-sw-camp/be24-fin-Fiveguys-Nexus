<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">운영 현황</h1>
      </div>
      <div class="flex items-center gap-2">
        <button v-for="tab in periodTabs" :key="tab" class="text-xs px-3 py-1.5 rounded-md border transition-colors cursor-pointer"
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
            <p class="text-[30px] leading-none font-extrabold text-gray-900 tracking-tight">{{ kpi.value }}</p>
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

    <!-- 중간 행: 라인차트 + 진행중 배송 -->
    <div class="grid grid-cols-3 gap-4">

      <!-- 발주 통계 라인차트 -->
      <div class="col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
        style="min-height:280px">
        <div class="flex items-center justify-between mb-5 shrink-0">
          <div>
            <h2 class="font-bold text-gray-900">주간 발주 통계</h2>
            <p class="text-xs text-gray-400 mt-0.5">이번 주 vs 지난 주 (단위: 건)</p>
          </div>
          <div class="flex items-center gap-4 text-xs text-gray-500">
            <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-orange-500 inline-block rounded"></span>이번
              주</span>
            <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-gray-300 inline-block rounded"></span>지난
              주</span>
          </div>
        </div>
        <div class="flex-1 min-h-0 relative" style="height:200px">
          <canvas ref="lineCanvas" style="display:block; width:100%; height:100%;"></canvas>
        </div>
      </div>

      <!-- 진행중 배송 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col">
        <div class="px-5 pt-5 pb-4 border-b border-gray-100">
          <h2 class="font-bold text-gray-900">진행중 배송</h2>
        </div>
        <div class="flex-1 divide-y divide-gray-50 overflow-y-auto">
          <div v-for="d in ongoingDeliveries" :key="d.id"
            class="px-5 py-4 hover:bg-gray-50 transition-colors cursor-pointer"
            role="button" tabindex="0"
            @click="router.push('/delivery')"
            @keydown.enter="router.push('/delivery')"
            @keydown.space.prevent="router.push('/delivery')">
            <div class="flex items-start gap-3">
              <div class="flex-1 min-w-0">
                <p class="text-xs text-gray-400">발주 {{ d.num }}</p>
                <p class="text-sm font-bold text-gray-900 font-mono">{{ d.id }}</p>
                <div class="flex items-center gap-1.5 mt-1.5 text-xs text-gray-500">
                  <span class="flex items-center gap-1">
                    <span class="w-1.5 h-1.5 rounded-full bg-blue-400 inline-block"></span>{{ d.from }}
                  </span>
                  <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                  <span class="flex items-center gap-1">
                    <span class="w-1.5 h-1.5 rounded-full bg-green-400 inline-block"></span>{{ d.to }}
                  </span>
                </div>
              </div>
              <span class="text-xs px-2 py-0.5 rounded-full font-medium shrink-0" :class="deliveryCls(d.status)">{{
                d.status }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 행: 이상발주 바그래프 + 배송비율 도넛/재고위험 -->
    <div class="grid grid-cols-3 gap-4">

      <!-- 이상 발주 통계 바그래프 -->
      <div class="col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
        style="min-height:280px">
        <div class="flex items-center justify-between mb-5 shrink-0">
          <div>
            <h2 class="font-bold text-gray-900">이상 발주 통계</h2>
            <p class="text-xs text-gray-400 mt-0.5">최근 6개월 이상 발주 현황 (단위: 건)</p>
          </div>
          <div class="flex items-center gap-4 text-xs text-gray-500">
            <span class="flex items-center gap-1.5"><span
                class="w-2.5 h-2.5 rounded-sm bg-red-400 inline-block"></span>승인 대기</span>
            <span class="flex items-center gap-1.5"><span
                class="w-2.5 h-2.5 rounded-sm bg-green-400 inline-block"></span>승인</span>
            <span class="flex items-center gap-1.5"><span
                class="w-2.5 h-2.5 rounded-sm bg-gray-300 inline-block"></span>반려</span>
          </div>
        </div>
        <div class="flex-1 min-h-0 relative" style="height:200px">
          <canvas ref="barCanvas" style="display:block; width:100%; height:100%;"></canvas>
        </div>
      </div>

      <!-- 오른쪽: 배송 비율 도넛 + 재고 위험 -->
      <div class="space-y-4">

        <!-- 배송 비율 도넛 -->
        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5">
          <div class="flex items-center justify-between mb-4">
            <h2 class="font-bold text-gray-900">배송 비율</h2>
            <span class="text-xs text-gray-400">오늘 기준</span>
          </div>
          <div class="relative mx-auto" style="width:140px;height:140px">
            <canvas ref="donutCanvas"></canvas>
            <div class="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <p class="text-2xl font-bold text-gray-900">{{ deliveryRate }}%</p>
              <p class="text-xs text-gray-400 mt-0.5">배송완료율</p>
            </div>
          </div>
          <div class="mt-4 space-y-2">
            <div v-for="item in donutLegend" :key="item.label" class="flex items-center gap-2">
              <span class="w-2 h-2 rounded-full shrink-0" :style="{ background: item.color }"></span>
              <span class="text-xs text-gray-500 flex-1">{{ item.label }}</span>
              <div class="w-16 h-1 rounded-full bg-gray-100 overflow-hidden">
                <div class="h-full rounded-full" :style="{ width: item.pct + '%', background: item.color }"></div>
              </div>
              <span class="text-xs font-semibold text-gray-700 w-7 text-right">{{ item.pct }}%</span>
            </div>
          </div>
        </div>

        <!-- 재고 위험 경고 -->
        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5">
          <div class="mb-3">
            <h2 class="font-bold text-gray-900">재고 위험</h2>
          </div>
          <div class="space-y-2">
            <div v-for="w in warnings" :key="w.store + w.product"
              class="flex items-center justify-between py-1.5 cursor-pointer hover:bg-gray-50 rounded px-1 transition-colors"
              role="button" tabindex="0"
              @click="router.push('/inventory/franchise')"
              @keydown.enter="router.push('/inventory/franchise')"
              @keydown.space.prevent="router.push('/inventory/franchise')">
              <div class="flex items-center gap-2">
                <span class="w-1.5 h-1.5 rounded-full bg-red-500"></span>
                <p class="text-sm text-gray-800 truncate max-w-28">{{ w.product }}</p>
              </div>
              <span class="text-xs px-2 py-0.5 rounded-full font-medium bg-red-100 text-red-600">
                {{ w.current }}/{{ w.min }}
              </span>
            </div>
          </div>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Chart } from 'chart.js/auto'
import { getStoreKpi } from '@/api/dashboard'

const periodTabs = ['일간', '주간', '월간']
const router = useRouter()
const activePeriod = ref('주간')

const today = computed(() => new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))

const lineCanvas = ref(null)
const barCanvas = ref(null)
const donutCanvas = ref(null)

const thisWeek = [32, 48, 38, 65, 52, 78, 58]
const lastWeek = [22, 38, 42, 48, 58, 52, 48]
const weekLabels = ['일', '월', '화', '수', '목', '금', '토']

const abnormalLabels = ['11월', '12월', '1월', '2월', '3월', '4월']
const abnormalDanger = [3, 5, 2, 7, 4, 2]
const abnormalApprove = [2, 4, 2, 5, 3, 1]
const abnormalReject = [1, 1, 0, 2, 1, 1]

const donutLegend = [
  { label: '배송완료', pct: 65, color: '#f97316' },
  { label: '배송중', pct: 20, color: '#60a5fa' },
  { label: '배송지연', pct: 10, color: '#f87171' },
  { label: '출고대기', pct: 5, color: '#a78bfa' },
]

const deliveryRate = computed(() => donutLegend[0].pct)

onMounted(async () => {
  // 가맹점 현황 KPI
  try {
    const { data } = await getStoreKpi()
    const store = data.result
    kpis.value[1].value = store.totalStoreCount.toLocaleString()
    kpis.value[1].sub = `이번 달 신규 ${store.newStoreCountThisMonth}개`
    kpis.value[1].delta = store.deltaPercent
  } catch (e) {
    console.error('가맹점 KPI 조회 실패', e)
  }

  // 라인차트
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
          min: 10, max: 90,
          grid: { color: '#f3f4f6' },
          ticks: { color: '#9ca3af', font: { size: 11 }, stepSize: 20 },
          border: { display: false },
        },
      },
    },
  })

  // 도넛차트
  new Chart(donutCanvas.value, {
    type: 'doughnut',
    data: {
      labels: donutLegend.map(d => d.label),
      datasets: [{
        data: donutLegend.map(d => d.pct),
        backgroundColor: donutLegend.map(d => d.color),
        borderWidth: 3,
        borderColor: '#ffffff',
        hoverBorderWidth: 3,
        hoverOffset: 6,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      cutout: '72%',
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1f2937',
          titleColor: '#f9fafb',
          bodyColor: '#d1d5db',
          padding: 10,
          cornerRadius: 10,
          callbacks: { label: (ctx) => ` ${ctx.label}: ${ctx.parsed}%` },
        },
      },
    },
  })

  // 바차트
  new Chart(barCanvas.value, {
    type: 'bar',
    data: {
      labels: abnormalLabels,
      datasets: [
        {
          label: '승인 대기',
          data: abnormalDanger,
          backgroundColor: '#f87171',
          borderRadius: 4,
          borderSkipped: false,
        },
        {
          label: '승인',
          data: abnormalApprove,
          backgroundColor: '#4ade80',
          borderRadius: 4,
          borderSkipped: false,
        },
        {
          label: '반려',
          data: abnormalReject,
          backgroundColor: '#d1d5db',
          borderRadius: 4,
          borderSkipped: false,
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
          min: 0,
          grid: { color: '#f3f4f6' },
          ticks: { color: '#9ca3af', font: { size: 11 }, stepSize: 2, precision: 0 },
          border: { display: false },
        },
      },
    },
  })
})

const kpis = ref([
  { title: '총 매출', value: '89.2', unit: '백만', sub: '전일 대비', delta: 12.4, to: '/settlement' },
  { title: '입점 매장', value: '-', unit: '개', sub: '이번 달 신규 -개', delta: 0, to: '/store' },
  { title: '금일 자동 발주', value: '85', unit: '건', sub: '확정 62건', delta: 8.7, to: '/order' },
  { title: '배송 지연', value: '4', unit: '건', sub: '즉시 확인 필요', delta: -1.8, to: '/delivery' },
])

const warnings = ref([
  { store: '이탈리안 키친',  product: '한우 등심',  current: 30,  min: 50   },
  { store: '일식 스시바',    product: '올리브오일', current: 2,   min: 5    },
  { store: '한우 오마카세',  product: '버터',       current: 8,   min: 20   },
  { store: '차이나 가든',    product: '생수',       current: 200, min: 500  },
])

const ongoingDeliveries = [
  { num: 1, id: 'ORD-2604-001', from: '본사 창고', to: '이탈리안 키친',  status: '배송중' },
  { num: 2, id: 'ORD-2604-002', from: '본사 창고', to: '한우 오마카세',  status: '출고대기' },
  { num: 3, id: 'ORD-2604-004', from: '본사 창고', to: '프렌치 비스트로', status: '배송지연' },
  { num: 4, id: 'ORD-2604-005', from: '본사 창고', to: '일식 스시바',    status: '출고완료' },
]

const deliveryCls = s => ({
  '배송중': 'bg-blue-50 text-blue-600 border border-blue-200',
  '출고대기': 'bg-gray-100 text-gray-600 border border-gray-200',
  '출고완료': 'bg-green-50 text-green-700 border border-green-200',
  '배송완료': 'bg-green-50 text-green-700 border border-green-200',
  '배송지연': 'bg-red-50 text-red-600 border border-red-200',
}[s] || 'bg-gray-100 text-gray-500 border border-gray-200')

</script>
