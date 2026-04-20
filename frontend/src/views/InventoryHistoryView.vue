<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">입출고 이력</h1>
        <p class="page-spec-hint">
          <code>INVENTORY_001</code>기간·유형·가맹점 필터, 출고 행 강조, 일시 내림차순, 상단 재고 현황으로 복귀.
        </p>
      </div>
      <RouterLink to="/inventory/head-office"
        class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2">
        <ArrowLeft class="w-4 h-4" /> 재고 현황
      </RouterLink>
    </div>

    <!-- Filters -->
    <div class="bg-white border border-gray-200 rounded-lg p-5 flex flex-wrap gap-4 items-end">
      <div class="space-y-1.5">
        <label class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</label>
        <select v-model="filterType"
          class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
          <option value="">전체</option>
          <option value="입고">입고</option>
          <option value="출고">출고</option>
          <option value="보정">보정</option>
        </select>
      </div>
      <div class="space-y-1.5">
        <label class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점</label>
        <select v-model="filterStore"
          class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
          <option value="">전체</option>
          <option>한화빌딩점</option>
          <option>여의도역점</option>
          <option>판교테크노밸리점</option>
          <option>부산센텀점</option>
        </select>
      </div>
      <div class="space-y-1.5">
        <label class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">기간 (시작)</label>
        <input v-model="filterFrom" type="date"
          class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
      </div>
      <div class="space-y-1.5">
        <label class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">기간 (종료)</label>
        <input v-model="filterTo" type="date"
          class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
      </div>
      <button @click="resetFilter"
        class="px-3 py-2 border border-gray-200 text-sm font-semibold text-gray-500 hover:bg-gray-50">
        초기화
      </button>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <div class="px-5 py-3 border-b border-gray-100 bg-gray-50/60 text-xs text-gray-500">
        총 <span class="font-bold text-gray-900">{{ filtered.length }}</span>건
      </div>
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">일시</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">처리자</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">비고</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="h in filtered"
            :key="h.id"
            class="hover:bg-gray-50/50 transition-colors"
            :class="h.type === '출고' ? 'bg-red-50/20' : ''"
          >
            <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ h.datetime }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded" :class="typeClass(h.type)">
                {{ h.type }}
              </span>
            </td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ h.product }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ h.store }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="h.type === '입고' ? 'text-blue-600' : h.type === '출고' ? 'text-red-500' : 'text-gray-600'">
              {{ h.type === '입고' ? '+' : h.type === '출고' ? '-' : '±' }}{{ h.qty }}
            </td>
            <td class="px-5 py-3.5 text-gray-600 text-xs">{{ h.handler }}</td>
            <td class="px-5 py-3.5 text-gray-400 text-xs">{{ h.note }}</td>
          </tr>
          <tr v-if="filtered.length === 0">
            <td colspan="7" class="px-5 py-12 text-center text-gray-400 text-sm">조회된 이력이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ArrowLeft } from 'lucide-vue-next'
import { RouterLink } from 'vue-router'

const filterType  = ref('')
const filterStore = ref('')
const filterFrom  = ref('')
const filterTo    = ref('')

const history = ref([
  { id: 1,  datetime: '2026-04-13 14:23', type: '입고', product: '우유(1L)',        store: '여의도역점',      qty: 200,  handler: '시스템(자동)', note: '자동발주 ORD-001' },
  { id: 2,  datetime: '2026-04-13 13:10', type: '출고', product: '에스프레소 원두', store: '판교테크노밸리점', qty: 18,   handler: '시스템(POS)',  note: 'POS 마감 차감'  },
  { id: 3,  datetime: '2026-04-13 12:45', type: '출고', product: '우유(1L)',        store: '한화빌딩점',      qty: 46,   handler: '시스템(POS)',  note: 'POS 마감 차감'  },
  { id: 4,  datetime: '2026-04-13 11:00', type: '입고', product: '바닐라 시럽',     store: '한화빌딩점',      qty: 60,   handler: '이재혁(운영자)', note: '수동발주 입고'  },
  { id: 5,  datetime: '2026-04-13 09:30', type: '보정', product: '종이컵(M)',       store: '부산센텀점',      qty: 50,   handler: '김동현(점주)', note: '실재고 보정'    },
  { id: 6,  datetime: '2026-04-12 22:05', type: '출고', product: '우유(1L)',        store: '여의도역점',      qty: 38,   handler: '시스템(POS)',  note: 'POS 마감 차감'  },
  { id: 7,  datetime: '2026-04-12 22:05', type: '출고', product: '에스프레소 원두', store: '여의도역점',      qty: 12,   handler: '시스템(POS)',  note: 'POS 마감 차감'  },
  { id: 8,  datetime: '2026-04-12 17:00', type: '입고', product: '프리미엄 원두',   store: '한화빌딩점',      qty: 50,   handler: '시스템(자동)', note: '자동발주 입고'  },
  { id: 9,  datetime: '2026-04-12 10:20', type: '출고', product: '종이컵(L)',       store: '부산센텀점',      qty: 120,  handler: '시스템(POS)',  note: 'POS 마감 차감'  },
  { id: 10, datetime: '2026-04-11 22:00', type: '출고', product: '카라멜 시럽',     store: '판교테크노밸리점', qty: 8,    handler: '시스템(POS)',  note: 'POS 마감 차감'  },
  { id: 11, datetime: '2026-04-11 16:30', type: '입고', product: '두유(1L)',        store: '여의도역점',      qty: 100,  handler: '이재혁(운영자)', note: '수동발주 입고'  },
  { id: 12, datetime: '2026-04-11 14:00', type: '보정', product: '바닐라 시럽',     store: '여의도역점',      qty: 3,    handler: '이재혁(운영자)', note: '유통기한 폐기'  },
])

function parseHistoryDate(s) {
  return new Date(s.replace(' ', 'T'))
}

const filtered = computed(() => {
  const rows = history.value.filter((h) => {
    if (filterType.value && h.type !== filterType.value) return false
    if (filterStore.value && h.store !== filterStore.value) return false
    if (filterFrom.value && h.datetime.slice(0, 10) < filterFrom.value) return false
    if (filterTo.value && h.datetime.slice(0, 10) > filterTo.value) return false
    return true
  })
  return [...rows].sort((a, b) => parseHistoryDate(b.datetime) - parseHistoryDate(a.datetime))
})

function resetFilter() {
  filterType.value = ''
  filterStore.value = ''
  filterFrom.value = ''
  filterTo.value = ''
}

function typeClass(type) {
  const map = {
    '입고': 'bg-blue-50 text-blue-600 border border-blue-200',
    '출고': 'bg-red-50 text-red-500 border border-red-200',
    '보정': 'bg-amber-50 text-amber-600 border border-amber-200',
  }
  return map[type] || 'bg-gray-100 text-gray-500 border border-gray-200'
}
</script>
