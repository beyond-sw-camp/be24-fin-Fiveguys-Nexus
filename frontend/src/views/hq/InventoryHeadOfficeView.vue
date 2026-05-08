<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'
import api from '@/api/headinventory'
import InventoryStatusFilters from '@/components/inventory/InventoryStatusFilters.vue'
import InventoryHeadTable from '@/components/inventory/InventoryHeadTable.vue'
import InventoryDetailModal from '@/components/inventory/InventoryDetailModal.vue'
import { useInventoryCommon } from '@/composables/useInventoryCommon'

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
const { totalStock, fifoLots, isExpiringSoon, hasExpiringSoonLot } = useInventoryCommon()

const filteredItems = computed(() => {
  const rows = items.value.filter((item) => {
    if (filterStatus.value === 'danger' && !['LOW', 'CRITICAL'].includes(item.status)) return false
    if (filterStatus.value === 'normal' && item.status !== 'NORMAL') return false
    if (filterStatus.value === 'expiring' && !hasExpiringSoonLot(item)) return false
    return true
  })
  return sortByCodeAsc(rows)
})

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
    const inventoryList = response?.data?.result
    items.value = Array.isArray(inventoryList) ? inventoryList.map(toItem) : []
  } catch (error) {
    console.error('Failed to fetch inventory list:', error)
    items.value = []
  }
}

function sortByCodeAsc(list) {
  return [...list].sort((a, b) => a.code.localeCompare(b.code, undefined, { numeric: true }))
}

const getSubtitle = (item) => `${item.code} · ${item.warehouse}`

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

onMounted(() => {
  fetchInventoryList()
})
</script>

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

    <InventoryStatusFilters v-model="filterStatus" :status-filters="statusFilters" />

    <InventoryHeadTable
      :items="filteredItems"
      :total-stock="totalStock"
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
