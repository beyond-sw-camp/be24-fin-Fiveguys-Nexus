<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">가맹점 재고 현황</h1>
        <p class="text-xs text-gray-500 mt-1">가맹점별 보유 수량 기준입니다.</p>
        <p class="page-spec-hint mt-1">
          <code>STOCK_002·003</code>지역·가맹점 필터, 상품코드·품목·현재/최소재고·상태. 유통기한·선입선출은 품목 클릭 시 상세에서 확인.
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
          type="button"
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
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in filteredItems" :key="item.code + item.store"
            role="button"
            tabindex="0"
            class="hover:bg-gray-50/50 transition-colors cursor-pointer"
            @click="openDetail(item)"
            @keydown.enter.prevent="openDetail(item)"
            @keydown.space.prevent="openDetail(item)">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ item.store }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="totalStock(item) < item.safe ? 'text-red-600' : 'text-gray-900'">
              {{ totalStock(item).toLocaleString() }}
            </td>
            <td class="px-5 py-3.5 text-gray-500">{{ item.safe.toLocaleString() }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="getStatusClass(item)">
                {{ getStatus(item) }}
              </span>
            </td>
          </tr>
          <tr v-if="filteredItems.length === 0">
            <td colspan="6" class="px-5 py-12 text-center text-gray-400 text-sm">조회된 재고가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <Teleport to="body">
      <div v-if="detailItem"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="detailTitleId"
        @click.self="closeDetail">
        <div class="bg-white rounded-lg shadow-xl max-w-lg w-full max-h-[85vh] overflow-hidden flex flex-col border border-gray-200">
          <div class="flex items-start justify-between gap-3 px-5 py-4 border-b border-gray-200 bg-gray-50">
            <div class="min-w-0">
              <p :id="detailTitleId" class="text-lg font-bold text-gray-900 truncate">{{ detailItem.name }}</p>
              <p class="text-xs font-mono text-gray-500 mt-0.5">{{ detailItem.code }} · {{ detailItem.store }}</p>
              <p class="text-xs text-gray-500 mt-2">
                합계 <span class="font-semibold text-gray-800">{{ totalStock(detailItem).toLocaleString() }}</span>
                (유통기한 오름차순 · 선입선출)
              </p>
            </div>
            <button type="button"
              class="shrink-0 p-1.5 rounded border border-gray-200 text-gray-500 hover:bg-white hover:text-gray-800"
              aria-label="닫기"
              @click="closeDetail">
              ✕
            </button>
          </div>
          <div class="overflow-y-auto">
            <table class="w-full text-sm text-left">
              <thead>
                <tr class="border-b border-gray-100 bg-white sticky top-0">
                  <th class="px-5 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">순번</th>
                  <th class="px-5 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유통기한</th>
                  <th class="px-5 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-right">수량</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-for="(row, idx) in fifoLots(detailItem)" :key="idx + (row.expiry ?? 'none')">
                  <td class="px-5 py-3 text-gray-500">{{ idx + 1 }}</td>
                  <td class="px-5 py-3 text-xs font-mono"
                    :class="isExpiringSoon(row.expiry) ? 'text-orange-600 font-semibold' : 'text-gray-700'">
                    {{ row.expiry ?? '—' }}
                  </td>
                  <td class="px-5 py-3 text-right font-semibold text-gray-900">{{ row.qty.toLocaleString() }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'

const filterRegion = ref('')
const filterStore  = ref('')
const filterStatus = ref('all')
const detailItem = ref(null)
const detailTitleId = 'inv-store-detail-title'

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

/** lots 합이 현재재고 */
const items = ref([
  {
    code: 'C100', name: '닭고기(생닭)', store: '한화빌딩점', safe: 60,
    lots: [
      { expiry: '2026-04-22', qty: 38 },
      { expiry: '2026-04-25', qty: 50 },
    ],
  },
  {
    code: 'C110', name: '순살 정육', store: '한화빌딩점', safe: 40,
    lots: [
      { expiry: '2026-04-18', qty: 12 },
      { expiry: '2026-04-20', qty: 22 },
    ],
  },
  {
    code: 'C100', name: '닭고기(생닭)', store: '여의도역점', safe: 65,
    lots: [
      { expiry: '2026-04-19', qty: 20 },
      { expiry: '2026-04-21', qty: 32 },
    ],
  },
  {
    code: 'C200', name: '튀김가루', store: '판교테크노밸리점', safe: 25,
    lots: [{ expiry: '2026-07-10', qty: 26 }],
  },
  {
    code: 'C210', name: '양념소스', store: '한화빌딩점', safe: 20,
    lots: [{ expiry: '2026-05-02', qty: 17 }],
  },
  {
    code: 'C220', name: '핫양념소스', store: '여의도역점', safe: 12,
    lots: [
      { expiry: '2026-04-19', qty: 5 },
      { expiry: '2026-04-22', qty: 3 },
    ],
  },
  {
    code: 'C300', name: '치즈볼(냉동)', store: '부산센텀점', safe: 70,
    lots: [
      { expiry: '2026-06-05', qty: 40 },
      { expiry: '2026-06-10', qty: 56 },
    ],
  },
  {
    code: 'C310', name: '감자튀김(냉동)', store: '판교테크노밸리점', safe: 50,
    lots: [{ expiry: '2026-06-18', qty: 41 }],
  },
  {
    code: 'C500', name: '치킨 박스(대)', store: '한화빌딩점', safe: 300,
    lots: [{ expiry: null, qty: 420 }],
  },
  {
    code: 'C510', name: '소스컵/뚜껑 세트', store: '부산센텀점', safe: 400,
    lots: [
      { expiry: null, qty: 200 },
      { expiry: null, qty: 310 },
    ],
  },
])

function totalStock(item) {
  return (item.lots ?? []).reduce((s, l) => s + l.qty, 0)
}

function fifoLots(item) {
  const lots = [...(item.lots ?? [])]
  return lots.sort((a, b) => {
    if (!a.expiry && !b.expiry) return 0
    if (!a.expiry) return 1
    if (!b.expiry) return -1
    return new Date(a.expiry) - new Date(b.expiry)
  })
}

function hasExpiringSoonLot(item) {
  return (item.lots ?? []).some((l) => isExpiringSoon(l.expiry))
}

function sortKey(a) {
  return `${a.code}\u0000${a.store}`
}

function sortRows(list) {
  return [...list].sort((a, b) => sortKey(a).localeCompare(sortKey(b), undefined, { numeric: true }))
}

const filteredItems = computed(() => {
  const rows = items.value.filter((item) => {
    const stock = totalStock(item)
    if (filterRegion.value && storeRegion[item.store] !== filterRegion.value) return false
    if (filterStore.value && item.store !== filterStore.value) return false
    if (filterStatus.value === 'danger' && stock >= item.safe) return false
    if (filterStatus.value === 'normal' && (stock < item.safe || hasExpiringSoonLot(item))) return false
    if (filterStatus.value === 'expiring' && !hasExpiringSoonLot(item)) return false
    return true
  })
  return sortRows(rows)
})

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  const stock = totalStock(item)
  if (stock < item.safe) return '재고부족'
  if (hasExpiringSoonLot(item)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  const stock = totalStock(item)
  if (stock < item.safe) return 'bg-red-50 text-red-600 border border-red-200'
  if (hasExpiringSoonLot(item)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}

function openDetail(item) {
  detailItem.value = item
}

function closeDetail() {
  detailItem.value = null
}
</script>
