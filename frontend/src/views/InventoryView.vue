<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">입점 매장 재고 현황</h1>
      </div>
      <div class="flex gap-2">
        <RouterLink to="/inventory/history"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2">
          <History class="w-4 h-4" /> 입출고 이력
        </RouterLink>
        <button class="px-4 py-2 border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer">재고 동기화</button>
      </div>
    </div>

    <div class="flex gap-3 items-center flex-wrap">
      <select v-model="filterRegion"
        class="px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white">
        <option value="">전체 지역</option>
        <option value="서울">서울</option>
        <option value="경기">경기</option>
        <option value="부산">부산</option>
      </select>
      <input
        v-model.trim="storeSearch"
        type="search"
        placeholder="매장명 검색"
        autocomplete="off"
        class="min-w-[10rem] flex-1 max-w-xs px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white"
      />
      <select
        v-model="selectedStoreIdx"
        class="min-w-[12rem] px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white"
        @change="onStoreSelectChange"
      >
        <option value="">매장 선택</option>
        <option v-for="s in visibleStores" :key="s.idx" :value="String(s.idx)">
          {{ s.storeName }}
        </option>
      </select>
      <button
        type="button"
        class="px-3 py-2 rounded-lg border border-[#F37321] text-sm font-semibold text-[#F37321] hover:bg-[#F37321]/5 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="!selectedStoreIdx || listLoading"
        @click="fetchStoreInventory"
      >
        {{ listLoading ? '조회 중…' : '재고 조회' }}
      </button>
      <div class="flex gap-1.5">
        <button v-for="f in statusFilters" :key="f.value"
          type="button"
          @click="filterStatus = f.value"
          class="px-3 py-1.5 text-sm font-semibold border rounded-lg transition-colors cursor-pointer"
          :class="filterStatus === f.value
            ? 'bg-[#F37321] text-white border-[#F37321]'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
          {{ f.label }}
        </button>
      </div>
    </div>

    <p v-if="listError" class="text-sm text-red-600">{{ listError }}</p>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">입점 매장</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">평균재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in filteredItems" :key="item.idx ?? (item.code + String(item.manufacturedDate ?? ''))"
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
            <td class="px-5 py-3.5 text-gray-500">{{ formatAverageStock(item.avgStock) }}</td>
            <td class="px-5 py-3.5 text-gray-500">{{ item.safe.toLocaleString() }}</td>
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

    <Teleport to="body">
      <div v-if="detailItem"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="detailTitleId"
        @click.self="closeDetail">
        <div class="relative bg-white rounded-xl shadow-xl max-w-lg w-full max-h-[85vh] overflow-hidden flex flex-col border border-gray-200">
          <div class="flex items-start justify-between gap-3 px-6 py-4 border-b border-gray-200">
            <div class="min-w-0">
              <p :id="detailTitleId" class="font-bold text-gray-900 truncate">{{ detailItem.name }}</p>
              <p class="text-xs font-mono text-gray-500 mt-0.5">{{ detailItem.code }} · {{ detailItem.store }}</p>
              <p class="text-xs text-gray-500 mt-2">
                합계 <span class="font-semibold text-gray-800">{{ totalStock(detailItem).toLocaleString() }}</span>
              </p>
            </div>
            <button type="button"
              class="text-gray-400 hover:text-gray-600 cursor-pointer"
              aria-label="닫기"
              @click="closeDetail">
              ✕
            </button>
          </div>
          <div class="overflow-y-auto flex-1">
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
          <div class="px-6 py-4 border-t border-gray-100 flex justify-end">
            <button type="button" @click="closeDetail"
              class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer">닫기</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'
import { getStoreList, getStoreInventoryByStore } from '@/api/store'

const filterRegion = ref('')
const storeSearch = ref('')
const selectedStoreIdx = ref('')
const filterStatus = ref('all')
const detailItem = ref(null)
const detailTitleId = 'inv-store-detail-title'
const storeList = ref([])
const items = ref([])
const listLoading = ref(false)
const listError = ref('')

const statusFilters = [
  { value: 'all', label: '전체' },
  { value: 'danger', label: '재고부족' },
  { value: 'expiring', label: '유통기한 임박' },
  { value: 'normal', label: '정상' },
]

function regionFromAddress(address) {
  if (!address) return ''
  const text = String(address)
  if (text.includes('서울')) return '서울'
  if (text.includes('경기')) return '경기'
  if (text.includes('부산')) return '부산'
  return ''
}

const storesInRegion = computed(() => {
  if (!filterRegion.value) return storeList.value
  return storeList.value.filter((s) => regionFromAddress(s.address) === filterRegion.value)
})

const visibleStores = computed(() => {
  const q = storeSearch.value.toLowerCase()
  if (!q) return storesInRegion.value
  return storesInRegion.value.filter((s) => String(s.storeName ?? '').toLowerCase().includes(q))
})

function formatDateFromApi(value) {
  if (!value) return null
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return null
  return d.toISOString().slice(0, 10)
}

function mapInventoryRow(row) {
  return {
    idx: row.idx,
    code: `P${row.productIdx}`,
    name: row.productName,
    store: row.storeName,
    safe: row.minStock ?? 0,
    avgStock: row.avgStock,
    apiStatus: row.status,
    manufacturedDate: row.manufacturedDate,
    lots: [
      {
        expiry: formatDateFromApi(row.manufacturedDate),
        qty: row.count ?? 0,
      },
    ],
  }
}

async function loadStoreList() {
  listError.value = ''
  try {
    const { data } = await getStoreList()
    const list = data?.result
    storeList.value = Array.isArray(list) ? list : []
  } catch (error) {
    console.error('Failed to fetch store list:', error)
    storeList.value = []
    listError.value = '매장 목록을 불러오지 못했습니다.'
  }
}

async function fetchStoreInventory() {
  if (!selectedStoreIdx.value) {
    items.value = []
    return
  }
  listLoading.value = true
  listError.value = ''
  try {
    const { data } = await getStoreInventoryByStore(selectedStoreIdx.value)
    const list = Array.isArray(data) ? data : []
    items.value = list.map(mapInventoryRow)
  } catch (error) {
    console.error('Failed to fetch store inventory:', error)
    items.value = []
    listError.value = '해당 매장 재고를 불러오지 못했습니다.'
  } finally {
    listLoading.value = false
  }
}

function onStoreSelectChange() {
  fetchStoreInventory()
}

function totalStock(item) {
  return (item.lots ?? []).reduce((s, l) => s + l.qty, 0)
}

function formatAverageStock(value) {
  if (value === null || value === undefined || value === '') return '-'
  const numeric = Number(value)
  if (!Number.isFinite(numeric)) return '-'
  return Math.round(numeric).toLocaleString()
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
  return `${a.code}\u0000${a.idx ?? ''}`
}

function sortRows(list) {
  return [...list].sort((a, b) => sortKey(a).localeCompare(sortKey(b), undefined, { numeric: true }))
}

const filteredItems = computed(() => {
  const rows = items.value.filter((item) => {
    const stock = totalStock(item)
    if (filterStatus.value === 'danger' && stock >= item.safe) return false
    if (filterStatus.value === 'normal' && (stock < item.safe || hasExpiringSoonLot(item))) return false
    if (filterStatus.value === 'expiring' && !hasExpiringSoonLot(item)) return false
    return true
  })
  return sortRows(rows)
})

watch([filterRegion, storeSearch], () => {
  if (!selectedStoreIdx.value) return
  const selectedExists = visibleStores.value.some((s) => String(s.idx) === selectedStoreIdx.value)
  if (!selectedExists) {
    selectedStoreIdx.value = ''
    items.value = []
  }
})

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  if (item.apiStatus === 'CRITICAL') return '재고위험'
  if (item.apiStatus === 'LOW') return '재고부족'
  const stock = totalStock(item)
  if (stock < item.safe) return '재고부족'
  if (hasExpiringSoonLot(item)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.apiStatus === 'CRITICAL') return 'bg-red-50 text-red-700 border border-red-200'
  if (item.apiStatus === 'LOW') return 'bg-red-50 text-red-600 border border-red-200'
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

onMounted(() => {
  loadStoreList()
})
</script>
