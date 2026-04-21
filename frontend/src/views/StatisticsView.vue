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

    <!-- 판매/매출 중심 분석 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-6 space-y-5">
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 class="text-xl font-bold text-gray-900">판매/매출 핵심 분석</h2>
          <p class="text-sm text-gray-500 mt-1">기간을 직접 선택하거나 월/년 기준으로 비교할 수 있습니다.</p>
        </div>
        <div class="flex items-center gap-2 rounded-lg border border-gray-200 p-1">
          <button
            type="button"
            class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
            :class="analysisMode === 'month' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-50'"
            @click="analysisMode = 'month'"
          >
            월 비교
          </button>
          <button
            type="button"
            class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
            :class="analysisMode === 'year' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-50'"
            @click="analysisMode = 'year'"
          >
            년 비교
          </button>
          <button
            type="button"
            class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
            :class="analysisMode === 'custom' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-50'"
            @click="analysisMode = 'custom'"
          >
            직접 선택
          </button>
        </div>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <template v-if="analysisMode !== 'custom'">
          <select
            v-model="selectedYear"
            class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
          >
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
          </select>
          <select
            v-if="analysisMode === 'month'"
            v-model="selectedMonth"
            class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
          >
            <option v-for="m in monthOptions" :key="m" :value="m">{{ m }}월</option>
          </select>
        </template>
        <template v-else>
          <input
            v-model="customStartDate"
            type="date"
            class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
          />
          <span class="text-sm text-gray-400">~</span>
          <input
            v-model="customEndDate"
            type="date"
            class="text-sm px-3 py-2 rounded-lg border border-gray-200 bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
          />
        </template>
      </div>

      <div class="text-xs text-gray-500">
        기준 기간: <span class="font-semibold text-gray-700">{{ analysisLabel }}</span> /
        비교 기간: <span class="font-semibold text-gray-700">{{ compareLabel }}</span>
      </div>

      <div class="rounded-2xl border border-gray-200 p-5 space-y-4">
        <div class="flex items-center justify-between gap-3">
          <div class="flex items-center gap-2 rounded-lg border border-gray-200 p-1">
            <button
              type="button"
              class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
              :class="analysisTab === 'sales' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-50'"
              @click="analysisTab = 'sales'"
            >
              판매 분석
            </button>
            <button
              type="button"
              class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
              :class="analysisTab === 'revenue' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-50'"
              @click="analysisTab = 'revenue'"
            >
              매출 분석
            </button>
          </div>
          <p class="text-xs text-gray-400">{{ analysisTab === 'sales' ? '메뉴 판매수량 기준' : '매장 매출금액 기준' }}</p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-3">
          <div class="rounded-xl border border-gray-200 p-4">
            <p class="text-xs font-semibold text-gray-400 uppercase tracking-[0.12em]">분석 대상 수</p>
            <p class="text-2xl font-extrabold text-gray-900 mt-2">{{ analysisSummary.totalCount }}</p>
            <p class="text-xs text-gray-500 mt-2">총 집계 항목</p>
          </div>
          <div class="rounded-xl border border-gray-200 p-4">
            <p class="text-xs font-semibold text-gray-400 uppercase tracking-[0.12em]">상위 1위 값</p>
            <p class="text-xl font-extrabold text-gray-900 mt-2 break-keep">{{ analysisSummary.topName }}</p>
            <p class="text-xs text-gray-500 mt-2">{{ analysisSummary.topValue }}</p>
          </div>
          <div class="rounded-xl border border-gray-200 p-4">
            <p class="text-xs font-semibold text-gray-400 uppercase tracking-[0.12em]">중앙 구간 값</p>
            <p class="text-xl font-extrabold text-gray-900 mt-2">{{ analysisSummary.medianValue }}</p>
            <p class="text-xs text-gray-500 mt-2">중간 순위 기준</p>
          </div>
          <div class="rounded-xl border border-gray-200 p-4">
            <p class="text-xs font-semibold text-gray-400 uppercase tracking-[0.12em]">상·하위 격차</p>
            <p class="text-xl font-extrabold text-gray-900 mt-2">{{ analysisSummary.gapValue }}</p>
            <p class="text-xs text-gray-500 mt-2">상위 1위 - 하위 1위</p>
          </div>
        </div>

        <div class="flex items-center justify-end">
          <button
            type="button"
            class="text-[11px] px-2.5 py-1 rounded border border-gray-200 text-gray-600 font-semibold hover:bg-gray-50"
            @click="openFullListModal"
          >
            전체 순위 보기
          </button>
        </div>

        <div class="grid grid-cols-1 xl:grid-cols-2 gap-3">
          <div class="rounded-xl border border-gray-200 p-4">
            <p class="text-xs font-semibold text-gray-400 uppercase tracking-[0.12em]">상위 5항목</p>
            <div class="mt-3 space-y-2">
              <div
                v-for="(item, idx) in analysisTab === 'sales' ? salesTop5 : revenueTop5"
                :key="`${analysisTab}-top-${item.name}`"
                class="flex items-center justify-between rounded-lg border border-gray-100 px-3 py-2"
              >
                <p class="text-sm text-gray-900 font-medium">{{ idx + 1 }}. {{ item.name }}</p>
                <p class="text-xs text-gray-500">{{ item.display }}</p>
              </div>
            </div>
          </div>
          <div class="rounded-xl border border-gray-200 p-4">
            <p class="text-xs font-semibold text-gray-400 uppercase tracking-[0.12em]">하위 5항목</p>
            <div class="mt-3 space-y-2">
              <div
                v-for="(item, idx) in analysisTab === 'sales' ? salesBottom5 : revenueBottom5"
                :key="`${analysisTab}-bottom-${item.name}`"
                class="flex items-center justify-between rounded-lg border border-rose-200 bg-rose-50/40 px-3 py-2"
              >
                <p class="text-sm text-gray-900 font-medium">
                  <span class="inline-flex items-center justify-center w-5 h-5 mr-1 rounded-full bg-rose-100 text-rose-600 text-[10px] font-bold">▼</span>
                  {{ (analysisTab === 'sales' ? salesRanking.length : revenueRanking.length) - idx }}. {{ item.name }}
                </p>
                <p class="text-xs text-rose-600 font-semibold">{{ item.display }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 판매/매출 전체 리스트 모달 -->
    <div v-if="isFullListModalOpen" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="closeFullListModal"></div>
      <div class="relative w-full max-w-3xl bg-white rounded-xl border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <div>
            <h3 class="font-bold text-gray-900">
              {{ analysisTab === 'sales' ? '판매 분석' : '매출 분석' }} · 전체 순위
            </h3>
            <p class="text-xs text-gray-400 mt-1">{{ analysisLabel }}</p>
          </div>
          <button @click="closeFullListModal" class="text-gray-400 hover:text-gray-600">✕</button>
        </div>

        <div class="p-5 max-h-[65vh] overflow-auto">
          <div class="space-y-2">
            <div
              v-for="(item, idx) in fullListItems"
              :key="`modal-${item.name}-${idx}`"
              class="flex items-center justify-between rounded-lg border px-3 py-2"
              :class="idx >= fullListItems.length - 5 ? 'border-rose-200 bg-rose-50/40' : 'border-gray-100'"
            >
              <p class="text-sm text-gray-900 font-medium">
                {{ idx + 1 }}. {{ item.name }}
              </p>
              <p class="text-xs font-semibold" :class="idx >= fullListItems.length - 5 ? 'text-rose-600' : 'text-gray-600'">
                {{ item.display }}
              </p>
            </div>
          </div>
        </div>
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

const stores = [
  'BBQ 강남역점',
  'BBQ 홍대입구점',
  'BBQ 여의도역점',
  'BBQ 판교테크노밸리점',
  'BBQ 잠실새내점',
  'BBQ 부산서면점',
  'BBQ 대전둔산점',
  'BBQ 수원광교점',
]

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
const storeWeights = [17, 16, 14, 13, 12, 11, 9, 8]
const storeWeightTotal = storeWeights.reduce((s, w) => s + w, 0)

function pickCategory(rand) {
  let r = rand() * weightTotal
  for (const [c, w] of categoryWeights) {
    if (r < w) return c
    r -= w
  }
  return categoryWeights[categoryWeights.length - 1][0]
}

function pickStore(rand) {
  let r = rand() * storeWeightTotal
  for (let i = 0; i < stores.length; i++) {
    if (r < storeWeights[i]) return stores[i]
    r -= storeWeights[i]
  }
  return stores[stores.length - 1]
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
      const store = pickStore(rand)
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

      list.push({ date: dateStr, category: cat, product, store, amount, qty, abnormal })
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

function getYear(dateStr) {
  return Number(dateStr.slice(0, 4))
}

function getMonth(dateStr) {
  return Number(dateStr.slice(5, 7))
}

const yearOptions = computed(() => {
  const years = [...new Set(rawOrders.map((o) => getYear(o.date)))].sort((a, b) => b - a)
  return years
})

const monthOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
const analysisMode = ref('month')
const analysisTab = ref('sales')
const isFullListModalOpen = ref(false)
const selectedYear = ref(getYear(anchorDate))
const selectedMonth = ref(getMonth(anchorDate))
const customStartDate = ref('2026-03-01')
const customEndDate = ref('2026-04-21')

function getDaysInMonth(year, month) {
  return new Date(year, month, 0).getDate()
}

function ymToDateStr(year, month, day = 1) {
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
}

function filterByDateRange(orders, startStr, endStr) {
  return orders.filter((o) => o.date >= startStr && o.date <= endStr)
}

const analysisRange = computed(() => {
  if (analysisMode.value === 'year') {
    const y = selectedYear.value
    return {
      start: ymToDateStr(y, 1, 1),
      end: ymToDateStr(y, 12, 31),
      label: `${y}년`,
    }
  }

  if (analysisMode.value === 'custom') {
    const start = customStartDate.value || customEndDate.value
    const end = customEndDate.value || customStartDate.value
    const safeStart = start <= end ? start : end
    const safeEnd = start <= end ? end : start
    return {
      start: safeStart,
      end: safeEnd,
      label: `${safeStart} ~ ${safeEnd}`,
    }
  }

  const y = selectedYear.value
  const m = Number(selectedMonth.value)
  return {
    start: ymToDateStr(y, m, 1),
    end: ymToDateStr(y, m, getDaysInMonth(y, m)),
    label: `${y}년 ${m}월`,
  }
})

const compareRange = computed(() => {
  if (analysisMode.value === 'year') {
    const y = selectedYear.value - 1
    return {
      start: ymToDateStr(y, 1, 1),
      end: ymToDateStr(y, 12, 31),
      label: `${y}년`,
    }
  }

  if (analysisMode.value === 'custom') {
    const start = new Date(`${analysisRange.value.start}T00:00:00`)
    const end = new Date(`${analysisRange.value.end}T00:00:00`)
    const days = Math.max(1, Math.floor((end - start) / 86400000) + 1)
    const prevEnd = new Date(start)
    prevEnd.setDate(prevEnd.getDate() - 1)
    const prevStart = new Date(prevEnd)
    prevStart.setDate(prevStart.getDate() - (days - 1))
    const startStr = prevStart.toISOString().slice(0, 10)
    const endStr = prevEnd.toISOString().slice(0, 10)
    return {
      start: startStr,
      end: endStr,
      label: `${startStr} ~ ${endStr}`,
    }
  }

  let y = selectedYear.value
  let m = Number(selectedMonth.value) - 1
  if (m === 0) {
    y -= 1
    m = 12
  }
  return {
    start: ymToDateStr(y, m, 1),
    end: ymToDateStr(y, m, getDaysInMonth(y, m)),
    label: `${y}년 ${m}월`,
  }
})

const analysisOrders = computed(() =>
  filterByDateRange(rawOrders, analysisRange.value.start, analysisRange.value.end)
)

const compareOrders = computed(() =>
  filterByDateRange(rawOrders, compareRange.value.start, compareRange.value.end)
)

const analysisLabel = computed(() => analysisRange.value.label)
const compareLabel = computed(() => compareRange.value.label)

function rankTopBy(orders, key, metric = 'qty') {
  const map = new Map()
  for (const o of orders) {
    map.set(o[key], (map.get(o[key]) || 0) + (metric === 'amount' ? o.amount : o.qty))
  }
  const rows = [...map.entries()].sort((a, b) => b[1] - a[1])
  if (!rows.length) return { name: '-', value: 0 }
  return { name: rows[0][0], value: rows[0][1] }
}

function rankLowBy(orders, key, metric = 'amount') {
  const map = new Map()
  for (const o of orders) {
    map.set(o[key], (map.get(o[key]) || 0) + (metric === 'amount' ? o.amount : o.qty))
  }
  const rows = [...map.entries()].sort((a, b) => a[1] - b[1])
  if (!rows.length) return { name: '-', value: 0 }
  return { name: rows[0][0], value: rows[0][1] }
}

function rankListBy(orders, key, metric = 'amount') {
  const map = new Map()
  for (const o of orders) {
    map.set(o[key], (map.get(o[key]) || 0) + (metric === 'amount' ? o.amount : o.qty))
  }
  const rows = [...map.entries()].map(([name, value]) => ({ name, value }))
  rows.sort((a, b) => b.value - a.value)
  return rows
}

const topMenuCurrent = computed(() => {
  const top = rankTopBy(analysisOrders.value, 'product', 'qty')
  return {
    name: top.name,
    meta: `${analysisLabel.value} · 판매수량 ${Number(top.value).toLocaleString()}개`,
  }
})

const topMenuCompare = computed(() => {
  const top = rankTopBy(compareOrders.value, 'product', 'qty')
  return {
    name: top.name,
    meta: `${compareLabel.value} · 판매수량 ${Number(top.value).toLocaleString()}개`,
  }
})

const topStore = computed(() => {
  const top = rankTopBy(analysisOrders.value, 'store', 'amount')
  return {
    name: top.name,
    meta: `${analysisLabel.value} · 매출 ₩${Number(top.value).toLocaleString()}`,
  }
})

const lowStore = computed(() => {
  const low = rankLowBy(analysisOrders.value, 'store', 'amount')
  return {
    name: low.name,
    meta: `${analysisLabel.value} · 매출 ₩${Number(low.value).toLocaleString()}`,
  }
})

const salesRanking = computed(() => rankListBy(analysisOrders.value, 'product', 'qty'))
const revenueRanking = computed(() => rankListBy(analysisOrders.value, 'store', 'amount'))

function toDisplay(item, metric) {
  if (!item) return '-'
  if (metric === 'qty') return `${Number(item.value).toLocaleString()}개`
  return `₩${Number(item.value).toLocaleString()}`
}

const salesTop5 = computed(() =>
  salesRanking.value.slice(0, 5).map((item) => ({ ...item, display: toDisplay(item, 'qty') }))
)
const salesBottom5 = computed(() =>
  [...salesRanking.value]
    .reverse()
    .slice(0, 5)
    .map((item) => ({ ...item, display: toDisplay(item, 'qty') }))
)
const revenueTop5 = computed(() =>
  revenueRanking.value.slice(0, 5).map((item) => ({ ...item, display: toDisplay(item, 'amount') }))
)
const revenueBottom5 = computed(() =>
  [...revenueRanking.value]
    .reverse()
    .slice(0, 5)
    .map((item) => ({ ...item, display: toDisplay(item, 'amount') }))
)

const fullListItems = computed(() => {
  const base = analysisTab.value === 'sales' ? salesRanking.value : revenueRanking.value
  const metric = analysisTab.value === 'sales' ? 'qty' : 'amount'
  return base.map((item) => ({ ...item, display: toDisplay(item, metric) }))
})

function openFullListModal() {
  isFullListModalOpen.value = true
}

function closeFullListModal() {
  isFullListModalOpen.value = false
}

const analysisSummary = computed(() => {
  const list = analysisTab.value === 'sales' ? salesRanking.value : revenueRanking.value
  const metric = analysisTab.value === 'sales' ? 'qty' : 'amount'
  if (!list.length) {
    return {
      totalCount: 0,
      topName: '-',
      topValue: '-',
      medianValue: '-',
      gapValue: '-',
    }
  }
  const top = list[0]
  const bottom = list[list.length - 1]
  const median = list[Math.floor((list.length - 1) / 2)]
  const gap = top.value - bottom.value
  return {
    totalCount: list.length,
    topName: top.name,
    topValue: toDisplay(top, metric),
    medianValue: toDisplay(median, metric),
    gapValue: metric === 'qty' ? `${Number(gap).toLocaleString()}개` : `₩${Number(gap).toLocaleString()}`,
  }
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
