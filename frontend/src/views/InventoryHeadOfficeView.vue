<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">본사 재고 현황</h1>
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
      <select v-model="filterWarehouse"
        class="px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white">
        <option value="">전체 창고</option>
        <option>본사 창고</option>
      </select>
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

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">창고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in filteredItems" :key="item.idx"
            role="button"
            tabindex="0"
            class="hover:bg-gray-50/50 transition-colors cursor-pointer"
            @click="openDetail(item)"
            @keydown.enter.prevent="openDetail(item)"
            @keydown.space.prevent="openDetail(item)">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ item.warehouse }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="item.status === 'CRITICAL' ? 'text-red-600' : 'text-gray-900'">
              {{ totalStock(item).toLocaleString() }}
            </td>
            <td class="px-5 py-3.5 text-gray-500">-</td>
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
        <div class="relative bg-white rounded-xl shadow-xl max-w-lg w-full max-h-[85vh] overflow-hidden flex flex-col border border-gray-200">
          <div class="flex items-start justify-between gap-3 px-6 py-4 border-b border-gray-200">
            <div class="min-w-0">
              <p :id="detailTitleId" class="font-bold text-gray-900 truncate">{{ detailItem.name }}</p>
              <p class="text-xs font-mono text-gray-500 mt-0.5">{{ detailItem.code }} · {{ detailItem.warehouse }}</p>
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
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'
import api from '@/api/headinventory'

const filterWarehouse = ref('')
const filterStatus = ref('all')
const detailItem = ref(null)
const detailTitleId = 'inv-ho-detail-title'

const statusFilters = [
  { value: 'all',      label: '전체' },
  { value: 'danger',   label: '재고부족' },
  { value: 'expiring', label: '유통기한 임박' },
  { value: 'normal',   label: '정상' },
]

const items = ref([])

function toItem(row) {
  return {
    idx: row.idx,
    code: `P${row.productIdx}`,
    name: row.productName,
    warehouse: '본사 창고',
    count: row.count ?? 0,
    status: row.status,
    manufacturedDate: row.manufacturedDate,
    lots: [
      {
        expiry: formatDate(row.manufacturedDate),
        qty: row.count ?? 0,
      },
    ],
  }
}

function formatDate(value) {
  if (!value) return null
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return null
  return date.toISOString().slice(0, 10)
}

async function fetchInventoryList() {
  try {
    const response = await api.getHeadInventoryList()
    const inventoryList = response?.data
    items.value = Array.isArray(inventoryList) ? inventoryList.map(toItem) : []
  } catch (error) {
    console.error('Failed to fetch inventory list:', error)
    items.value = []
  }
}

onMounted(() => {
  fetchInventoryList()
})

function totalStock(item) {
  return item.count ?? (item.lots ?? []).reduce((s, l) => s + l.qty, 0)
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

function sortByCodeAsc(list) {
  return [...list].sort((a, b) => a.code.localeCompare(b.code, undefined, { numeric: true }))
}

const filteredItems = computed(() => {
  const rows = items.value.filter((item) => {
    if (filterWarehouse.value && item.warehouse !== filterWarehouse.value) return false
    if (filterStatus.value === 'danger' && !['LOW', 'CRITICAL'].includes(item.status)) return false
    if (filterStatus.value === 'normal' && item.status !== 'NORMAL') return false
    if (filterStatus.value === 'expiring' && !hasExpiringSoonLot(item)) return false
    return true
  })
  return sortByCodeAsc(rows)
})

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  if (item.status === 'CRITICAL') return '재고위험'
  if (item.status === 'LOW') return '재고부족'
  if (hasExpiringSoonLot(item)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.status === 'CRITICAL') return 'bg-red-50 text-red-600 border border-red-200'
  if (item.status === 'LOW') return 'bg-orange-50 text-orange-500 border border-orange-200'
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
