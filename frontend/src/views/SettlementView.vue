<template>
  <div class="p-5 space-y-4">

    <!-- Header -->
    <div class="flex flex-col gap-3">

      <!-- 타이틀 -->
      <div class="flex justify-between items-start flex-wrap gap-4">
        <div class="min-w-0">
          <h1 class="text-xl font-bold text-gray-900 tracking-tight">정산 관리</h1>
        </div>
      </div>

      <!-- 🔥 버튼 영역 (검색창 위로 이동됨) -->
      <div class="flex flex-wrap gap-2 items-center">

        <div class="flex rounded-lg border border-gray-200 bg-gray-50/90 p-0.5">
          <button
            v-for="p in periodGranularityOptions"
            :key="p"
            type="button"
            class="text-xs px-2.5 py-1.5 rounded-md font-semibold transition-colors cursor-pointer"
            :class="periodGranularity === p ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
            @click="periodGranularity = p"
          >
            {{ p }}
          </button>
        </div>

        <select
          v-model="selectedMonth"
          class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white shadow-sm
                 focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321] outline-none"
        >
          <option>2026-04</option>
          <option>2026-03</option>
          <option>2026-02</option>
        </select>

        <button class="inline-flex items-center gap-2 px-4 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] cursor-pointer">
          월별 마감 실행
        </button>
      </div>
    </div>

    <!-- 검색창 -->
    <div class="flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[12rem] max-w-md">

        <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
          <svg
            class="w-4 h-4 text-gray-400"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
        </div>

        <input
          v-model="settlementSearch"
          type="search"
          placeholder="가맹점 이름 검색…"
          class="w-full pl-10 px-3 py-2 rounded-lg border border-gray-200 text-sm
                 bg-white shadow-sm
                 focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]
                 outline-none"
        />
      </div>
    </div>

    <!-- Summary -->
    <div class="grid grid-cols-2 gap-4">
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">가맹점 청구 합계</p>
          <p class="text-2xl font-black text-gray-900 mt-2">₩{{ totalStore.toLocaleString() }}</p>
          <p class="text-xs text-gray-400 mt-2 pt-2 border-t border-gray-100">{{ selectedMonth }}</p>
        </div>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">순이익 (차액)</p>
          <p class="text-2xl font-black text-green-600 mt-2">₩{{ (totalStore - totalSupplier).toLocaleString() }}</p>
          <p class="text-xs text-gray-400 mt-2 pt-2 border-t border-gray-100">{{ selectedMonth }}</p>
        </div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex border-b border-gray-200">
      <button
        v-for="tab in ['가맹점 정산']"
        :key="tab"
        @click="activeTab = tab"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors cursor-pointer"
        :class="activeTab === tab
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        {{ tab }}
      </button>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">대상</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">정산 기간</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주/납품 건수</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">정산금액</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">명세서</th>
        </tr>
        </thead>

        <tbody class="divide-y divide-gray-100">
        <tr v-for="s in filteredSettlements" :key="s.name" class="hover:bg-gray-50/50 transition-colors">
          <td class="px-5 py-3.5 font-semibold text-gray-900">{{ s.name }}</td>
          <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ s.period }}</td>
          <td class="px-5 py-3.5 text-gray-600">{{ s.count }}건</td>
          <td class="px-5 py-3.5 font-bold text-gray-900">₩{{ s.amount.toLocaleString() }}</td>
          <td class="px-5 py-3.5">
              <span
                class="text-xs font-bold px-2 py-0.5 rounded"
                :class="s.status === '정산완료'
                  ? 'bg-green-50 text-green-700 border border-green-200'
                  : 'bg-amber-50 text-amber-600 border border-amber-200'"
              >
                {{ s.status }}
              </span>
          </td>
          <td class="px-5 py-3.5 text-center">
            <button
              type="button"
              class="text-gray-300 hover:text-[#F37321] transition-colors cursor-pointer"
              @click="downloadStatement(s)"
            >
              <Download class="w-4 h-4 mx-auto" />
            </button>
          </td>
        </tr>

        <tr v-if="filteredSettlements.length === 0">
          <td colspan="6" class="px-5 py-10 text-center text-sm text-gray-400">
            검색 결과가 없습니다.
          </td>
        </tr>
        </tbody>
      </table>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Download } from 'lucide-vue-next'

const selectedMonth = ref('2026-04')
const activeTab = ref('가맹점 정산')
const settlementSearch = ref('')
const periodGranularity = ref('월간')
const periodGranularityOptions = ['주간', '월간', '년간']

const storeSettlements = ref([
  { name: '한화빌딩점', period: '2026-04-01 ~ 04-30', count: 28, amount: 12500000, status: '정산완료' },
  { name: '여의도역점', period: '2026-04-01 ~ 04-30', count: 31, amount: 15800000, status: '정산완료' },
  { name: '판교테크노밸리점', period: '2026-04-01 ~ 04-30', count: 19, amount: 9200000, status: '대기' },
  { name: '부산센텀점', period: '2026-04-01 ~ 04-30', count: 22, amount: 11300000, status: '대기' },
])

const supplierSettlements = ref([
  { name: '서울우유', period: '2026-04-01 ~ 04-30', count: 14, amount: 4500000, status: '정산완료' },
  { name: '동서식품', period: '2026-04-01 ~ 04-30', count: 8, amount: 7200000, status: '대기' },
  { name: '한국포장', period: '2026-04-01 ~ 04-30', count: 5, amount: 1800000, status: '대기' },
  { name: '청정원F&B', period: '2026-04-01 ~ 04-30', count: 6, amount: 2100000, status: '정산완료' },
])

const currentSettlements = computed(() =>
  activeTab.value === '가맹점 정산' ? storeSettlements.value : supplierSettlements.value,
)

const filteredSettlements = computed(() => {
  const q = settlementSearch.value.trim()
  if (!q) return currentSettlements.value
  return currentSettlements.value.filter(s => s.name.includes(q))
})

function downloadStatement(row) {
  alert(`[INCOME_003] ${row.name} 거래 명세서(PDF) 다운로드`)
}

const totalStore = computed(() => storeSettlements.value.reduce((s, v) => s + v.amount, 0))
const totalSupplier = computed(() => supplierSettlements.value.reduce((s, v) => s + v.amount, 0))
</script>
