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

  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'

const periodTabs = ['일간', '주간', '월간']
const activePeriod = ref('주간')

const today = computed(() => new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))

const kpis = ref([
  { title: '금일 매출',       value: '84.5', unit: '만원', sub: '전일 대비',         delta: 5.2,  alert: false, to: '/store-settlement' },
  { title: '미확정 발주',     value: '3',    unit: '건',   sub: '자동 발주 승인 대기', delta: 0,    alert: false, to: '/store-order' },
  { title: '재고 위험 품목',  value: '2',    unit: '종',   sub: '최소재고 이하 품목', delta: -1,   alert: true,  to: '/store-inventory' },
  { title: '이번달 정산 예정', value: '12.5', unit: '만원', sub: '2026-04 납부 예정',  delta: -3.1, alert: false, to: '/store-settlement' },
])
</script>
