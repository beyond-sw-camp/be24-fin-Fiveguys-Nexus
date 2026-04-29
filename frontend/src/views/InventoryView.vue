<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'
import { getStoreList, getStoreInventoryByStore } from '@/api/store'
import { useInventoryCommon } from '@/composables/useInventoryCommon'
import InventoryStatusFilters from '@/components/inventory/InventoryStatusFilters.vue'
import InventoryDetailModal from '@/components/inventory/InventoryDetailModal.vue'
import InventoryFranchiseToolbar from '@/components/inventory/InventoryFranchiseToolbar.vue'
import InventoryFranchiseTable from '@/components/inventory/InventoryFranchiseTable.vue'

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

const { totalStock, fifoLots, isExpiringSoon, hasExpiringSoonLot } = useInventoryCommon()

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

function formatDate(value) {
  if (!value) return null
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return null
  return date.toISOString().slice(0, 10)
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
        expiry: formatDate(row.manufacturedDate),
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

function formatAverageStock(value) {
  if (value === null || value === undefined || value === '') return '-'
  const numeric = Number(value)
  if (!Number.isFinite(numeric)) return '-'
  return Math.round(numeric).toLocaleString()
}

function sortRows(list) {
  return [...list].sort((a, b) => `${a.code}\u0000${a.idx ?? ''}`.localeCompare(`${b.code}\u0000${b.idx ?? ''}`, undefined, { numeric: true }))
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

function getStatus(item) {
  if (item.apiStatus === 'CRITICAL') return '재고위험'
  if (item.apiStatus === 'LOW') return '재고부족'
  if (totalStock(item) < item.safe) return '재고부족'
  if (hasExpiringSoonLot(item)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.apiStatus === 'CRITICAL') return 'bg-red-50 text-red-700 border border-red-200'
  if (item.apiStatus === 'LOW') return 'bg-red-50 text-red-600 border border-red-200'
  if (totalStock(item) < item.safe) return 'bg-red-50 text-red-600 border border-red-200'
  if (hasExpiringSoonLot(item)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}

const getSubtitle = (item) => `${item.code} · ${item.store}`

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

<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">입점 매장 재고 현황</h1>
      </div>
      <div class="flex gap-2">
        <RouterLink
          to="/inventory/history"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2"
        >
          <History class="w-4 h-4" /> 입출고 이력
        </RouterLink>
        <button class="px-4 py-2 border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer">재고 동기화</button>
      </div>
    </div>

    <InventoryFranchiseToolbar
      v-model:filter-region="filterRegion"
      v-model:store-search="storeSearch"
      v-model:selected-store-idx="selectedStoreIdx"
      :visible-stores="visibleStores"
      :list-loading="listLoading"
      @fetch-store-inventory="fetchStoreInventory"
    />

    <InventoryStatusFilters v-model="filterStatus" :status-filters="statusFilters" />

    <p v-if="listError" class="text-sm text-red-600">{{ listError }}</p>

    <InventoryFranchiseTable
      :items="filteredItems"
      :total-stock="totalStock"
      :format-average-stock="formatAverageStock"
      :get-status="getStatus"
      :get-status-class="getStatusClass"
      @open-detail="openDetail"
    />

    <InventoryDetailModal
      :item="detailItem"
      :detail-title-id="detailTitleId"
      :total-stock="totalStock"
      :fifo-lots="fifoLots"
      :is-expiring-soon="isExpiringSoon"
      :get-subtitle="getSubtitle"
      @close="closeDetail"
    />
  </div>
</template>
