<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">운영 현황</h1>
        <p class="page-spec-hint">
          <code>DASH_001~006</code>가맹점·금일 발주·배송·재고 위험·비정상 발주·자동 발주 제안을 한 화면에서 확인합니다. 수치 없음은 0건으로 처리합니다.
        </p>
      </div>
      <div class="flex items-center gap-2">
        <button
          v-for="tab in periodTabs"
          :key="tab"
          class="text-xs px-3 py-1.5 rounded-md border transition-colors"
          :class="activePeriod === tab ? 'bg-gray-900 text-white border-gray-900' : 'bg-white text-gray-500 border-gray-200 hover:bg-gray-50'"
          @click="activePeriod = tab"
        >
          {{ tab }}
        </button>
        <span class="text-xs text-gray-400 border border-gray-200 px-3 py-1.5 rounded-md bg-white">{{ today }}</span>
      </div>
    </div>

    <div class="grid grid-cols-4 gap-4">
      <RouterLink
        v-for="kpi in kpis"
        :key="kpi.title"
        :to="kpi.to"
        class="block rounded-2xl border border-gray-200 bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.03)] transition-all hover:-translate-y-0.5 hover:shadow-[0_8px_20px_rgba(15,23,42,0.08)]"
      >
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

    <div class="grid grid-cols-12 gap-4">
      <section
        class="col-span-8 rounded-2xl border border-gray-200 bg-white p-4 cursor-pointer transition-shadow hover:shadow-[0_8px_20px_rgba(15,23,42,0.06)]"
        @click="goTo('/delivery')"
      >
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-base font-bold text-gray-900">주간 배송/발주 추이</h3>
          </div>
        </div>
        <div class="mt-3 rounded-xl border border-gray-100 bg-gray-50/60 p-3">
          <svg viewBox="0 0 640 220" class="w-full h-44">
            <g stroke="#e5e7eb" stroke-width="1">
              <line v-for="y in [20, 60, 100, 140, 180]" :key="`grid-${y}`" x1="20" :y1="y" x2="620" :y2="y" />
            </g>
            <polyline points="20,160 110,152 200,148 290,120 380,112 470,98 560,106 620,92" fill="none" stroke="#f97316" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" />
            <polyline points="20,172 110,166 200,158 290,150 380,136 470,124 560,126 620,118" fill="none" stroke="#64748b" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="6 7" />
            <circle class="chart-point" cx="470" cy="98" r="6.5" fill="#f97316" />
            <g class="chart-tooltip-group">
              <rect x="410" y="32" width="150" height="56" rx="10" fill="#111827" />
              <text x="428" y="56" fill="#d1d5db" font-size="11">목요일</text>
              <text x="428" y="76" fill="#ffffff" font-size="14" font-weight="700">배송 완료 128건</text>
            </g>
            <g fill="#9ca3af" font-size="11">
              <text x="18" y="210">월</text><text x="106" y="210">화</text><text x="197" y="210">수</text><text x="286" y="210">목</text><text x="375" y="210">금</text><text x="463" y="210">토</text><text x="553" y="210">일</text>
            </g>
          </svg>
        </div>
        <div class="mt-3 flex items-center gap-6 text-xs text-gray-500">
          <div class="flex items-center gap-2"><span class="w-2 h-2 rounded-full bg-orange-500"></span>배송 완료</div>
          <div class="flex items-center gap-2"><span class="w-2 h-2 rounded-full bg-slate-500"></span>제안 발주</div>
        </div>
      </section>

      <section
        class="col-span-4 rounded-2xl border border-gray-200 bg-white p-4 cursor-pointer transition-shadow hover:shadow-[0_8px_20px_rgba(15,23,42,0.06)]"
        @click="goTo('/delivery')"
      >
        <h3 class="text-base font-bold text-gray-900">배송 성공률</h3>
        <div class="mt-3 flex items-center justify-center">
          <div class="relative w-32 h-32 rounded-full chart-ring">
            <div class="absolute inset-3 rounded-full bg-white flex items-center justify-center">
              <div class="text-center">
                <p class="text-[24px] leading-none font-extrabold text-gray-900">{{ deliveryRate }}%</p>
              </div>
            </div>
          </div>
        </div>
        <div class="mt-4 space-y-2.5">
          <div v-for="status in deliveryBreakdown" :key="status.label" class="flex items-center justify-between text-sm">
            <div class="flex items-center gap-2"><span class="w-2.5 h-2.5 rounded-full" :style="{ backgroundColor: status.color }"></span><span class="text-gray-500">{{ status.label }}</span></div>
            <span class="font-semibold text-gray-900">{{ status.count }}건</span>
          </div>
        </div>
      </section>
    </div>

    <div class="grid grid-cols-2 gap-4">
      <section
        class="rounded-2xl border border-gray-200 bg-white overflow-hidden cursor-pointer transition-shadow hover:shadow-[0_8px_20px_rgba(15,23,42,0.06)]"
        @click="goTo('/inventory')"
      >
        <div class="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-sm font-bold text-gray-900">재고 위험 경고</h3>
        </div>
        <table class="w-full text-sm">
          <thead class="bg-gray-50/70">
            <tr>
              <th class="px-5 py-2.5 text-left text-[11px] font-semibold text-gray-400">매장</th>
              <th class="px-5 py-2.5 text-left text-[11px] font-semibold text-gray-400">상품</th>
              <th class="px-5 py-2.5 text-right text-[11px] font-semibold text-gray-400">현재 / 최소</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="w in warnings" :key="w.store + w.product" class="hover:bg-gray-50/70">
              <td class="px-5 py-2.5 text-xs text-gray-500">{{ w.store }}</td>
              <td class="px-5 py-2.5 text-sm font-semibold text-gray-800">{{ w.product }}</td>
              <td class="px-5 py-2.5 text-right"><span class="font-bold text-red-600">{{ w.current }}</span><span class="text-xs text-gray-400"> / {{ w.min }}</span></td>
            </tr>
          </tbody>
        </table>
      </section>

      <section
        class="rounded-2xl border border-gray-200 bg-white overflow-hidden transition-shadow hover:shadow-[0_8px_20px_rgba(15,23,42,0.06)]"
      >
        <div class="px-4 py-3 border-b border-gray-100 flex flex-wrap items-center justify-between gap-2">
          <h3 class="text-sm font-bold text-gray-900">오늘의 자동 발주 제안</h3>
          <div class="flex flex-wrap items-center gap-2" @click.stop>
            <div class="flex rounded-lg border border-gray-200 bg-gray-50/90 p-0.5">
              <button
                v-for="f in dashboardProposalFilters"
                :key="f.id"
                type="button"
                class="text-[11px] px-2.5 py-1 rounded-md font-semibold transition-colors"
                :class="dashboardProposalFilter === f.id ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
                @click="dashboardProposalFilter = f.id"
              >
                {{ f.label }}
              </button>
            </div>
            <RouterLink
              :to="{ path: '/order', query: { tab: 'auto', proposal: dashboardProposalFilter } }"
              class="text-xs font-semibold text-[#F37321] hover:underline shrink-0"
            >
              발주 관리에서 보기
            </RouterLink>
          </div>
        </div>
        <table class="w-full text-sm">
          <thead class="bg-gray-50/70">
            <tr>
              <th class="px-4 py-2.5 text-left text-[11px] font-semibold text-gray-400 w-[72px]">구분</th>
              <th class="px-5 py-2.5 text-left text-[11px] font-semibold text-gray-400">발주 지점</th>
              <th class="px-5 py-2.5 text-left text-[11px] font-semibold text-gray-400">제안 요약</th>
              <th class="px-5 py-2.5 text-right text-[11px] font-semibold text-gray-400">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="o in dashboardProposalsDisplayed"
              :key="o.kind + '-' + o.id"
              class="hover:bg-gray-50/70"
              :class="o.kind === 'abnormal' ? 'bg-rose-50/40' : ''"
            >
              <td class="px-4 py-2.5">
                <span
                  class="text-[10px] font-bold px-1.5 py-0.5 rounded border"
                  :class="o.kind === 'auto' ? 'bg-blue-50 text-blue-700 border-blue-200' : 'bg-rose-100 text-rose-800 border-rose-200'"
                >
                  {{ o.kind === 'auto' ? '자동' : '이상' }}
                </span>
              </td>
              <td class="px-5 py-2.5 text-sm font-semibold text-gray-900">{{ o.store }}</td>
              <td class="px-5 py-2.5 text-xs text-gray-500">
                <template v-if="o.kind === 'auto'">
                  <span class="font-mono text-gray-700">{{ o.proposalId }}</span>
                  <span class="text-gray-400"> · 품목 {{ o.itemCount }}건</span>
                </template>
                <template v-else>
                  <span class="font-mono text-gray-700">{{ o.proposalId }}</span>
                  <span class="text-rose-700 font-medium"> · {{ o.lineSummary }}</span>
                </template>
              </td>
              <td class="px-5 py-2.5 text-right">
                <span
                  class="text-xs px-2.5 py-1 rounded-full font-semibold"
                  :class="o.status === '제안중'
                    ? 'bg-blue-50 text-blue-600'
                    : o.status === '검토필요'
                      ? 'bg-rose-50 text-rose-700'
                      : 'bg-emerald-50 text-emerald-600'"
                >
                  {{ o.status }}
                </span>
              </td>
            </tr>
            <tr v-if="dashboardProposalsDisplayed.length === 0">
              <td colspan="4" class="px-5 py-10 text-center text-sm text-gray-400">데이터가 없습니다</td>
            </tr>
          </tbody>
        </table>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

const router = useRouter()

const periodTabs = ['일간', '주간', '월간']
const activePeriod = ref('주간')

const today = computed(() => new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))

const kpis = ref([
  { title: '총 매출', value: '89.2', unit: '백만', sub: '전일 대비', delta: 12.4, to: '/settlement' },
  { title: '운영 가맹점', value: '142', unit: '개', sub: '활성 매장 비율 96%', delta: 2.1, to: '/store' },
  { title: '금일 자동 발주', value: '85', unit: '건', sub: '확정 62건', delta: 8.7, to: '/order' },
  { title: '배송 지연', value: '4', unit: '건', sub: '즉시 확인 필요', delta: -1.8, to: '/delivery' },
])

const warnings = ref([
  { store: '여의도역점', product: '우유(1L)', current: 85, min: 120 },
  { store: '판교테크노밸리점', product: '에스프레소 원두', current: 12, min: 80 },
  { store: '한화빌딩점', product: '바닐라 시럽', current: 5, min: 30 },
  { store: '부산센텀점', product: '종이컵(M)', current: 300, min: 500 },
])

const dashboardProposalFilter = ref('all')
const dashboardProposalFilters = [
  { id: 'all', label: '전체' },
  { id: 'auto', label: '자동' },
  { id: 'abnormal', label: '이상' },
]

const todayOrders = ref([
  { id: 1, kind: 'auto', store: '여의도역점', proposalId: 'AO-20260420-014', itemCount: 4, status: '제안중' },
  { id: 2, kind: 'auto', store: '판교테크노밸리점', proposalId: 'AO-20260420-021', itemCount: 2, status: '제안중' },
  { id: 3, kind: 'auto', store: '한화빌딩점', proposalId: 'AO-20260420-008', itemCount: 6, status: '제안중' },
  { id: 4, kind: 'auto', store: '부산센텀점', proposalId: 'AO-20260420-003', itemCount: 1, status: '확정' },
])

const todayAbnormalOrders = ref([
  { id: 'abn-dash-1', kind: 'abnormal', store: '여의도역점', proposalId: 'ORD-20260414-ABN1', lineSummary: '평균 대비 수량 +567%', status: '검토필요' },
  { id: 'abn-dash-2', kind: 'abnormal', store: '판교테크노밸리점', proposalId: 'ORD-20260413-ABN2', lineSummary: '평균 대비 수량 +400%', status: '검토필요' },
])

const dashboardProposalsDisplayed = computed(() => {
  const merged = [...todayAbnormalOrders.value, ...todayOrders.value]
  const f = dashboardProposalFilter.value
  if (f === 'auto') return merged.filter((r) => r.kind === 'auto')
  if (f === 'abnormal') return merged.filter((r) => r.kind === 'abnormal')
  return merged
})

const deliveryBreakdown = ref([
  { label: '배송 완료', count: 128, color: '#f97316' },
  { label: '배송 중', count: 42, color: '#fb923c' },
  { label: '지연', count: 8, color: '#fda4af' },
  { label: '반송', count: 6, color: '#e5e7eb' },
])

const deliveryRate = computed(() => {
  const total = deliveryBreakdown.value.reduce((sum, item) => sum + item.count, 0)
  const completed = deliveryBreakdown.value[0]?.count ?? 0
  return Math.round((completed / total) * 100)
})

function goTo(path) {
  router.push(path)
}
</script>

<style scoped>
.chart-ring {
  background: conic-gradient(#f97316 0deg 252deg, #fb923c 252deg 324deg, #fda4af 324deg 338deg, #e5e7eb 338deg 360deg);
}

.chart-point {
  cursor: pointer;
}

.chart-tooltip-group {
  opacity: 0;
  transition: opacity 0.15s ease;
  pointer-events: none;
}

.chart-point:hover + .chart-tooltip-group {
  opacity: 1;
}
</style>
