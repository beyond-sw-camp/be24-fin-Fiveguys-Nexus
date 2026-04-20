<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">가맹점 재고 현황</h1>
        <p class="text-xs text-gray-500 mt-1">가맹점별 보유 수량 기준입니다.</p>
        <p class="page-spec-hint mt-1">
          <code>STOCK_002·003</code>지역·가맹점 필터, 상품코드·품목·현재/최소재고·유통기한·상태, 유통기한 순 정렬.
        </p>
      </div>
      <div class="flex gap-2">
        <RouterLink to="/inventory/history"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2">
          <History class="w-4 h-4" /> 입출고 이력
        </RouterLink>
        <button class="px-4 py-2 border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">재고 동기화</button>
      </div>
    </div>

    <!-- Filters (STOCK_002 지역별 · 가맹점별, STOCK_003 선택값에 따른 목록) -->
    <div class="flex gap-3 items-center flex-wrap">
      <select v-model="filterRegion"
        class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white">
        <option value="">전체 지역</option>
        <option value="서울">서울</option>
        <option value="경기">경기</option>
        <option value="부산">부산</option>
      </select>
      <select v-model="filterStore"
        class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white">
        <option value="">전체 가맹점</option>
        <option>한화빌딩점</option>
        <option>여의도역점</option>
        <option>판교테크노밸리점</option>
        <option>부산센텀점</option>
      </select>
      <div class="flex gap-1.5">
        <button v-for="f in statusFilters" :key="f.value"
          @click="filterStatus = f.value"
          class="px-3 py-2 text-sm font-semibold border transition-colors"
          :class="filterStatus === f.value
            ? 'bg-[#F37321] text-white border-[#F37321]'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
          {{ f.label }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유통기한</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in filteredItems" :key="item.code + item.store" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ item.store }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="item.stock < item.safe ? 'text-red-600' : 'text-gray-900'">
              {{ item.stock.toLocaleString() }}
            </td>
            <td class="px-5 py-3.5 text-gray-500">{{ item.safe.toLocaleString() }}</td>
            <td class="px-5 py-3.5 text-xs font-mono"
              :class="isExpiringSoon(item.expiry) ? 'text-orange-500 font-semibold' : 'text-gray-400'">
              {{ item.expiry || '-' }}
            </td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="getStatusClass(item)">
                {{ getStatus(item) }}
              </span>
            </td>
          </tr>
          <tr v-if="filteredItems.length === 0">
            <td colspan="7" class="px-5 py-12 text-center text-gray-400 text-sm">조회된 재고가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'

const filterRegion = ref('')
const filterStore  = ref('')
const filterStatus = ref('all')

const storeRegion = {
  한화빌딩점: '서울',
  여의도역점: '서울',
  판교테크노밸리점: '경기',
  부산센텀점: '부산',
}

watch(filterRegion, (region) => {
  if (!region || !filterStore.value) return
  if (storeRegion[filterStore.value] !== region) {
    filterStore.value = ''
  }
})

const statusFilters = [
  { value: 'all',      label: '전체' },
  { value: 'danger',   label: '재고부족' },
  { value: 'expiring', label: '유통기한 임박' },
  { value: 'normal',   label: '정상' },
]

const items = ref([
  { code: 'P100', name: '프리미엄 원두',   store: '한화빌딩점',      stock: 320,  safe: 100, expiry: '2026-05-30' },
  { code: 'P200', name: '우유(1L)',         store: '한화빌딩점',      stock: 148,  safe: 120, expiry: '2026-04-20' },
  { code: 'P200', name: '우유(1L)',         store: '여의도역점',      stock: 85,   safe: 120, expiry: '2026-04-18' },
  { code: 'P101', name: '에스프레소 원두', store: '판교테크노밸리점', stock: 12,   safe: 80,  expiry: '2026-06-15' },
  { code: 'P300', name: '바닐라 시럽',     store: '한화빌딩점',      stock: 5,    safe: 30,  expiry: '2026-08-01' },
  { code: 'P400', name: '종이컵(M)',        store: '부산센텀점',      stock: 300,  safe: 500, expiry: null },
  { code: 'P401', name: '종이컵(L)',        store: '한화빌딩점',      stock: 800,  safe: 500, expiry: null },
  { code: 'P201', name: '두유(1L)',         store: '여의도역점',      stock: 55,   safe: 60,  expiry: '2026-04-16' },
  { code: 'P301', name: '카라멜 시럽',     store: '판교테크노밸리점', stock: 42,   safe: 30,  expiry: '2026-09-01' },
])

function sortByExpiryAsc(list) {
  return [...list].sort((a, b) => {
    if (!a.expiry && !b.expiry) return 0
    if (!a.expiry) return 1
    if (!b.expiry) return -1
    return new Date(a.expiry) - new Date(b.expiry)
  })
}

/** STOCK_003 · STOCK_001과 동일 컬럼, 유통기한 순 나열 */
const filteredItems = computed(() => {
  const rows = items.value.filter((item) => {
    if (filterRegion.value && storeRegion[item.store] !== filterRegion.value) return false
    if (filterStore.value && item.store !== filterStore.value) return false
    if (filterStatus.value === 'danger' && item.stock >= item.safe) return false
    if (filterStatus.value === 'normal' && (item.stock < item.safe || isExpiringSoon(item.expiry))) return false
    if (filterStatus.value === 'expiring' && !isExpiringSoon(item.expiry)) return false
    return true
  })
  return sortByExpiryAsc(rows)
})

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  if (item.stock < item.safe)       return '재고부족'
  if (isExpiringSoon(item.expiry))  return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.stock < item.safe)      return 'bg-red-50 text-red-600 border border-red-200'
  if (isExpiringSoon(item.expiry)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}
</script>
