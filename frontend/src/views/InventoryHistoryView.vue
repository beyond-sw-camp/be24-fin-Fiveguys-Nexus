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
        <label class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">입점 매장</label>
        <select v-model="filterStore"
          class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
          <option value="">전체</option>
          <option v-for="s in stores" :key="s.idx" :value="s.storeName">{{ s.storeName }}</option>
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

    <p v-if="loadError" class="text-sm text-red-600 bg-red-50 border border-red-100 rounded-lg px-4 py-3">
      {{ loadError }}
    </p>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <div class="px-5 py-3 border-b border-gray-100 bg-gray-50/60 text-xs text-gray-500">
        <span v-if="loading">불러오는 중…</span>
        <template v-else>
          총 <span class="font-bold text-gray-900">{{ filtered.length }}</span>건
        </template>
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
            class="hover:bg-gray-50/80 transition-colors"
          >
            <td class="px-5 py-3.5 text-xs text-gray-500 tabular-nums">{{ h.datetime }}</td>
            <td class="px-5 py-3.5">
              <span
                class="inline-flex items-center rounded-md px-2 py-1 text-[11px] font-medium leading-none tracking-tight"
                :class="typeClass(h.type)"
              >
                {{ h.type }}
              </span>
            </td>
            <td class="px-5 py-3.5 font-medium text-gray-900">{{ h.product }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ h.store }}</td>
            <td
              class="px-5 py-3.5 tabular-nums font-semibold"
              :class="qtyClass(h.type)"
            >
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
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft } from 'lucide-vue-next'
import { RouterLink } from 'vue-router'
import { getStoreList } from '@/api/store/index.js'
import { getInventoryMovements } from '@/api/inventory/index.js'

const filterType  = ref('')
const filterStore = ref('')
const filterFrom  = ref('')
const filterTo    = ref('')

const stores = ref([])
const history = ref([])
const loading = ref(false)
const loadError = ref('')

function movementTypeToLabel(movementType) {
  if (movementType === 'INBOUND') return '입고'
  if (movementType === 'TRANSFER_OUT') return '출고'
  return '기타'
}

function formatLocalDateTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${min}`
}

function mapMovementRow(m, storeNameByIdx) {
  const movementType = m.movementType
  const type = movementTypeToLabel(movementType)
  let storeLabel = '—'
  if (movementType === 'INBOUND') {
    storeLabel = '본사'
  } else if (m.toLocationType === 'STORE' && m.toRefIdx != null) {
    storeLabel = storeNameByIdx.get(Number(m.toRefIdx)) || `매장 #${m.toRefIdx}`
  }

  return {
    id: m.movementIdx,
    datetime: formatLocalDateTime(m.createdAt),
    type,
    product: m.productName || '',
    store: storeLabel,
    qty: m.quantity ?? 0,
    handler: '—',
    note: m.memo || '',
  }
}

async function loadData() {
  loadError.value = ''
  loading.value = true
  try {
    const [storesRes, movRes] = await Promise.all([getStoreList(), getInventoryMovements()])
    const storeRows = storesRes.data?.result ?? []
    stores.value = storeRows
    const storeNameByIdx = new Map(storeRows.map((s) => [Number(s.idx), s.storeName]))
    const list = movRes.data?.result ?? []
    history.value = list.map((m) => mapMovementRow(m, storeNameByIdx))
  } catch (e) {
    loadError.value =
      e?.response?.data?.message || e?.message || '입출고 이력을 불러오지 못했습니다.'
    history.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

function parseHistoryDate(s) {
  return new Date(String(s).replace(' ', 'T'))
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
    입고: 'bg-slate-100 text-slate-700',
    출고: 'bg-orange-50/90 text-orange-950',
    보정: 'bg-stone-100 text-stone-700',
  }
  return map[type] || 'bg-stone-50 text-stone-600'
}

function qtyClass(type) {
  if (type === '입고') return 'text-slate-700'
  if (type === '출고') return 'text-orange-950'
  return 'text-stone-600'
}
</script>
