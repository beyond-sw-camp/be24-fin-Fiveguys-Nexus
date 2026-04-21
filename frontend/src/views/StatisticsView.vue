<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">발주 통계</h1>
        <p class="text-sm text-gray-500 mt-1">
          치킨 프랜차이즈 본사·가맹 발주 기준 데모입니다. 기간·카테고리에 따라 트렌드·비중·이상 발주를 확인할 수 있습니다.
        </p>
      </div>
      <div class="flex flex-wrap items-center gap-2">
        <div class="flex rounded-lg border border-gray-200 bg-white p-0.5">
          <button
            v-for="p in periodOptions"
            :key="p.value"
            type="button"
            class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
            :class="period === p.value ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-50'"
            @click="period = p.value"
          >
            {{ p.label }}
          </button>
        </div>
        <select
          v-model="category"
          class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
        >
          <option value="">전체 카테고리</option>
          <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
        </select>
        <button
          type="button"
          class="text-xs px-3 py-2 rounded-lg border border-gray-200 bg-white font-semibold text-gray-600 hover:bg-gray-50"
          @click="downloadCsv"
        >
          CSV 다운로드
        </button>
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
          <p class="text-[26px] leading-none font-extrabold text-gray-900 tracking-tight">{{ card.value }}</p>
          <span v-if="card.unit" class="text-sm text-gray-400 mb-0.5">{{ card.unit }}</span>
        </div>
        <p class="text-xs text-gray-500 mt-3">{{ card.sub }}</p>
      </div>
    </div>

    <div class="grid grid-cols-1 xl:grid-cols-3 gap-4">
      <!-- 발주 트렌드 -->
      <div class="xl:col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col min-h-[320px]">
        <div class="flex items-center justify-between mb-4 shrink-0">
          <div>
            <h2 class="font-bold text-gray-900">발주 트렌드</h2>
            <p class="text-xs text-gray-400 mt-0.5">버킷별 발주 금액 및 수량</p>
          </div>
        </div>
        <div class="flex-1 min-h-0 relative" style="height: 260px">
          <canvas ref="trendCanvas" class="block w-full h-full"></canvas>
        </div>
      </div>

      <!-- 카테고리 비중 -->
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col min-h-[320px]">
        <div class="mb-4 shrink-0">
          <h2 class="font-bold text-gray-900">카테고리별 비중</h2>
          <p class="text-xs text-gray-400 mt-0.5">선택 구간 내 발주 금액 기준</p>
        </div>
        <div class="flex-1 min-h-[200px] relative flex items-center justify-center">
          <canvas ref="shareCanvas" class="max-w-full" style="max-height: 220px"></canvas>
        </div>
      </div>
    </div>

    <!-- 이상 발주 품목 순위 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col min-h-[300px]">
      <div class="flex items-center justify-between mb-4 shrink-0">
        <div>
          <h2 class="font-bold text-gray-900">이상 발주 품목 순위</h2>
          <p class="text-xs text-gray-400 mt-0.5">이상 발주 발생 건수가 많은 품목 상위</p>
        </div>
      </div>
      <div class="flex-1 min-h-0 relative" style="height: 260px">
        <canvas ref="abnormalCanvas" class="block w-full h-full"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Chart } from 'chart.js/auto'

/** 재현 가능한 난수 (차트·데이터 안정) */
function mulberry32(seed) {
  return function () {
    let t = (seed += 0x6d2b79f5)
    t = Math.imul(t ^ (t >>> 15), t | 1)
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61)
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296
  }
}

/** 치킨집(프랜차이즈) SCM 카테고리 */
const categories = [
  '신선육',
  '튀김·배합',
  '양념·소스',
  '사이드·토핑',
  '포장·소모품',
  '음료',
  '장비·세척',
]

const productsByCategory = {
  신선육: ['닭다리살 20kg', '닭봉 10kg', '순살 다짐육 5kg', '가슴살 필렛 2kg'],
  '튀김·배합': ['후라이드 믹스 10kg', '순살 전용믹스 8kg', '양념치킨 코팅가루', '패티믹스 5kg'],
  '양념·소스': ['간장 양념소스 2kg', '양념치킨 소스 2kg', '마늘간장 베이스', '핫스파이시 소스'],
  '사이드·토핑': ['감자튀김 2kg', '치즈볼 1kg', '콜팝 닭껍질', '떡사리 500g'],
  '포장·소모품': ['치킨박스 L', '치킨봉투 대', '장갑 100매', '키친타월 4롤'],
  음료: ['콜라 500ml(24)', '사이다 500ml(24)', '생수 500ml(20)', '에이드 베이스 1L'],
  '장비·세척': ['튀김기름 18L', '그릴 클리너', '필터지', '살균세제 4L'],
}

const periodOptions = [
  { value: 'day', label: '일간' },
  { value: 'week', label: '주간' },
  { value: 'month', label: '월간' },
]

const period = ref('week')
const category = ref('')

const categoryWeights = [
  ['신선육', 26],
  ['튀김·배합', 20],
  ['양념·소스', 16],
  ['사이드·토핑', 12],
  ['포장·소모품', 10],
  ['음료', 8],
  ['장비·세척', 8],
]
const weightTotal = categoryWeights.reduce((s, [, w]) => s + w, 0)

function pickCategory(rand) {
  let r = rand() * weightTotal
  for (const [c, w] of categoryWeights) {
    if (r < w) return c
    r -= w
  }
  return categoryWeights[categoryWeights.length - 1][0]
}

function qtyForCategory(cat, rand) {
  if (cat === '포장·소모품') return 120 + Math.floor(rand() * 900)
  if (cat === '음료') return 12 + Math.floor(rand() * 96)
  if (cat === '신선육') return 10 + Math.floor(rand() * 55)
  if (cat === '튀김·배합') return 4 + Math.floor(rand() * 22)
  if (cat === '양념·소스') return 6 + Math.floor(rand() * 36)
  if (cat === '사이드·토핑') return 8 + Math.floor(rand() * 40)
  return 2 + Math.floor(rand() * 14)
}

function unitPriceFor(cat, rand) {
  if (cat === '신선육') return 5200 + Math.floor(rand() * 3800)
  if (cat === '튀김·배합') return 11000 + Math.floor(rand() * 9500)
  if (cat === '양념·소스') return 6500 + Math.floor(rand() * 4500)
  if (cat === '사이드·토핑') return 4800 + Math.floor(rand() * 5200)
  if (cat === '포장·소모품') return 35 + Math.floor(rand() * 120)
  if (cat === '음료') return 780 + Math.floor(rand() * 420)
  return 8000 + Math.floor(rand() * 12000)
}

function buildRawOrders() {
  const rand = mulberry32(20260422)
  const list = []
  const anchor = new Date('2026-04-21T12:00:00')
  for (let day = 0; day < 200; day++) {
    const d = new Date(anchor)
    d.setDate(anchor.getDate() - day)
    const dateStr = d.toISOString().slice(0, 10)
    const dow = d.getDay()
    const isPeak = dow === 5 || dow === 6 || dow === 0
    const isHolidayLike = day % 21 === 0
    let linesPerDay = 10 + Math.floor(rand() * 14)
    if (isPeak) linesPerDay += 8 + Math.floor(rand() * 16)
    if (isHolidayLike) linesPerDay += 12 + Math.floor(rand() * 20)
    linesPerDay = Math.min(linesPerDay, 52)

    for (let i = 0; i < linesPerDay; i++) {
      const cat = pickCategory(rand)
      const names = productsByCategory[cat]
      const product = names[Math.floor(rand() * names.length)]
      let qty = qtyForCategory(cat, rand)
      if (isPeak && (cat === '신선육' || cat === '튀김·배합')) {
        qty += Math.floor(rand() * 18)
      }
      const unitPrice = unitPriceFor(cat, rand)
      const amount = qty * unitPrice

      let abnormalProb = 0.048
      if (cat === '신선육') abnormalProb += 0.055
      if (cat === '튀김·배합' && qty > 18) abnormalProb += 0.028
      if (qty > 200) abnormalProb += 0.035
      if (isHolidayLike && rand() < 0.15) abnormalProb += 0.04
      const abnormal = rand() < abnormalProb ? 1 : 0

      list.push({ date: dateStr, category: cat, product, amount, qty, abnormal })
    }
  }
  return list
}

/** 위 헬퍼·가중치 정의 이후에만 생성 (초기화 순서 오류 방지) */
const rawOrders = buildRawOrders()

const filteredOrders = computed(() => {
  if (!category.value) return rawOrders
  return rawOrders.filter((o) => o.category === category.value)
})

/** 현재 기준 구간 길이(일) */
const windowDays = computed(() => {
  if (period.value === 'day') return 21
  if (period.value === 'week') return 7 * 12
  return 30 * 8
})

function parseYmd(s) {
  return new Date(`${s}T12:00:00`)
}

function addDays(d, n) {
  const x = new Date(d)
  x.setDate(x.getDate() + n)
  return x
}

function startOfIsoWeek(d) {
  const x = new Date(d)
  const day = x.getDay()
  const diff = (day + 6) % 7
  x.setDate(x.getDate() - diff)
  return x
}

/** 주간 버킷 정렬용 키 (해당 주 월요일 YYYY-MM-DD) */
function weekBucketKey(dateStr) {
  const w = startOfIsoWeek(parseYmd(dateStr))
  return w.toISOString().slice(0, 10)
}

function formatWeekLabelFromKey(key) {
  const w = parseYmd(key)
  return `${w.getMonth() + 1}/${w.getDate()}주`
}

function formatMonthLabel(d) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

function bucketKey(dateStr) {
  const d = parseYmd(dateStr)
  if (period.value === 'day') return dateStr
  if (period.value === 'week') return weekBucketKey(dateStr)
  return formatMonthLabel(d)
}

function ordersInRange(orders, endExclusive, days) {
  const end = parseYmd(endExclusive)
  const start = addDays(end, -days)
  return orders.filter((o) => {
    const od = parseYmd(o.date)
    return od >= start && od < end
  })
}

const anchorDate = '2026-04-22'

const currentWindowOrders = computed(() =>
  ordersInRange(filteredOrders.value, anchorDate, windowDays.value)
)

const previousWindowOrders = computed(() =>
  ordersInRange(filteredOrders.value, addDays(parseYmd(anchorDate), -windowDays.value).toISOString().slice(0, 10), windowDays.value)
)

function aggregateByBucket(orders) {
  const map = new Map()
  for (const o of orders) {
    const key = bucketKey(o.date)
    if (!map.has(key)) map.set(key, { amount: 0, qty: 0, abnormal: 0 })
    const row = map.get(key)
    row.amount += o.amount
    row.qty += o.qty
    row.abnormal += o.abnormal
  }
  return map
}

const trendBuckets = computed(() => {
  const map = aggregateByBucket(currentWindowOrders.value)
  const sortedKeys = [...map.keys()].sort((a, b) => a.localeCompare(b))
  const labels = sortedKeys.map((k) =>
    period.value === 'week' ? formatWeekLabelFromKey(k) : k
  )
  const keyByIndex = sortedKeys
  const amounts = keyByIndex.map((k) => map.get(k).amount)
  const qtys = keyByIndex.map((k) => map.get(k).qty)
  return { labels, amounts, qtys, map, sortedKeys }
})

const summary = computed(() => {
  const cur = currentWindowOrders.value
  const prev = previousWindowOrders.value
  const totalAmount = cur.reduce((s, o) => s + o.amount, 0)
  const prevAmount = prev.reduce((s, o) => s + o.amount, 0)
  const momPct = prevAmount === 0 ? (totalAmount > 0 ? 100 : 0) : ((totalAmount - prevAmount) / prevAmount) * 100
  const abnormalCount = cur.reduce((s, o) => s + o.abnormal, 0)
  const byProduct = new Map()
  for (const o of cur) {
    byProduct.set(o.product, (byProduct.get(o.product) || 0) + o.amount)
  }
  let topProduct = '-'
  let topAmt = 0
  for (const [p, a] of byProduct) {
    if (a > topAmt) {
      topAmt = a
      topProduct = p
    }
  }
  return { totalAmount, momPct, abnormalCount, topProduct }
})

const summaryCards = computed(() => {
  const { totalAmount, momPct, abnormalCount, topProduct } = summary.value
  const periodLabel = periodOptions.find((p) => p.value === period.value)?.label ?? ''
  return [
    {
      title: '총 발주 금액',
      value: `₩${Math.round(totalAmount).toLocaleString()}`,
      unit: '',
      sub: `${periodLabel} · 선택 구간 합계`,
    },
    {
      title: '전 기간 대비',
      value: `${momPct >= 0 ? '+' : ''}${momPct.toFixed(1)}`,
      unit: '%',
      sub: '직전 동일 길이 구간 대비 변동률',
    },
    {
      title: '이상 발주 건수',
      value: String(abnormalCount),
      unit: '건',
      sub: '선택 구간 내 이상 플래그 합계',
    },
    {
      title: '최다 발주 품목',
      value: topProduct,
      unit: '',
      sub: '금액 기준 상위 1개 품목',
    },
  ]
})

const categoryShare = computed(() => {
  const map = new Map()
  for (const o of currentWindowOrders.value) {
    map.set(o.category, (map.get(o.category) || 0) + o.amount)
  }
  const entries = [...map.entries()].sort((a, b) => b[1] - a[1])
  return {
    labels: entries.map((e) => e[0]),
    data: entries.map((e) => e[1]),
  }
})

const abnormalRanking = computed(() => {
  const map = new Map()
  for (const o of currentWindowOrders.value) {
    if (!o.abnormal) continue
    map.set(o.product, (map.get(o.product) || 0) + 1)
  }
  const entries = [...map.entries()].sort((a, b) => b[1] - a[1]).slice(0, 10)
  return {
    labels: entries.map((e) => e[0]),
    data: entries.map((e) => e[1]),
  }
})

const trendCanvas = ref(null)
const shareCanvas = ref(null)
const abnormalCanvas = ref(null)
let trendChart
let shareChart
let abnormalChart

function destroyCharts() {
  trendChart?.destroy()
  shareChart?.destroy()
  abnormalChart?.destroy()
  trendChart = shareChart = abnormalChart = undefined
}

function renderCharts() {
  destroyCharts()
  const { labels, amounts, qtys } = trendBuckets.value

  if (trendCanvas.value && labels.length) {
    trendChart = new Chart(trendCanvas.value, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: '발주 금액 (원)',
            data: amounts,
            borderColor: '#f97316',
            backgroundColor: 'rgba(249,115,22,0.12)',
            fill: true,
            tension: 0.35,
            yAxisID: 'y',
          },
          {
            label: '수량',
            data: qtys,
            borderColor: '#64748b',
            backgroundColor: 'transparent',
            fill: false,
            tension: 0.35,
            yAxisID: 'y1',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: { legend: { position: 'top' } },
        scales: {
          y: {
            type: 'linear',
            position: 'left',
            ticks: {
              callback: (v) => `₩${Number(v).toLocaleString()}`,
            },
            grid: { color: '#f3f4f6' },
          },
          y1: {
            type: 'linear',
            position: 'right',
            grid: { drawOnChartArea: false },
          },
          x: { grid: { display: false } },
        },
      },
    })
  }

  const share = categoryShare.value
  const colors = [
    '#f97316',
    '#60a5fa',
    '#a78bfa',
    '#4ade80',
    '#fbbf24',
    '#94a3b8',
    '#fb7185',
    '#2dd4bf',
  ]
  if (shareCanvas.value && share.labels.length && share.data.some((n) => n > 0)) {
    shareChart = new Chart(shareCanvas.value, {
      type: 'doughnut',
      data: {
        labels: share.labels,
        datasets: [
          {
            data: share.data,
            backgroundColor: share.labels.map((_, i) => colors[i % colors.length]),
            borderWidth: 2,
            borderColor: '#fff',
          },
        ],
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
                return ` ${ctx.label}: ₩${Number(ctx.parsed).toLocaleString()} (${pct}%)`
              },
            },
          },
        },
      },
    })
  }

  const ab = abnormalRanking.value
  if (abnormalCanvas.value) {
    abnormalChart = new Chart(abnormalCanvas.value, {
      type: 'bar',
      data: {
        labels: ab.labels.length ? ab.labels : ['데이터 없음'],
        datasets: [
          {
            label: '이상 발주 건수',
            data: ab.labels.length ? ab.data : [0],
            backgroundColor: ab.labels.length ? '#f87171' : '#e5e7eb',
            borderRadius: 6,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: { beginAtZero: true, ticks: { precision: 0 } },
          y: { grid: { display: false } },
        },
      },
    })
  }
}

watch([period, category], () => {
  nextTick(renderCharts)
})

onMounted(() => {
  nextTick(renderCharts)
})

onBeforeUnmount(() => {
  destroyCharts()
})

function downloadCsv() {
  const rows = []
  rows.push(['구간', periodOptions.find((p) => p.value === period.value)?.label ?? period.value])
  rows.push(['카테고리 필터', category.value || '전체'])
  rows.push([])
  rows.push(['요약', '값'])
  rows.push(['총 발주 금액', summary.value.totalAmount])
  rows.push(['전 기간 대비(%)', summary.value.momPct.toFixed(2)])
  rows.push(['이상 발주 건수', summary.value.abnormalCount])
  rows.push(['최다 발주 품목', summary.value.topProduct])
  rows.push([])
  rows.push(['버킷', '발주금액', '수량', '이상건수(버킷합)'])
  const { labels, map, sortedKeys } = trendBuckets.value
  for (let i = 0; i < sortedKeys.length; i++) {
    const k = sortedKeys[i]
    const r = map.get(k)
    rows.push([labels[i], r.amount, r.qty, r.abnormal])
  }
  rows.push([])
  rows.push(['카테고리', '발주금액'])
  for (let i = 0; i < categoryShare.value.labels.length; i++) {
    rows.push([categoryShare.value.labels[i], categoryShare.value.data[i]])
  }
  rows.push([])
  rows.push(['품목', '이상발주건수'])
  for (let i = 0; i < abnormalRanking.value.labels.length; i++) {
    rows.push([abnormalRanking.value.labels[i], abnormalRanking.value.data[i]])
  }

  const body = rows.map((r) => r.map(csvEscape).join(',')).join('\r\n')
  const bom = '\uFEFF'
  const blob = new Blob([bom + body], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `chicken-franchise-order-statistics-${anchorDate}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

function csvEscape(cell) {
  const s = String(cell ?? '')
  if (/[",\r\n]/.test(s)) return `"${s.replace(/"/g, '""')}"`
  return s
}
</script>
