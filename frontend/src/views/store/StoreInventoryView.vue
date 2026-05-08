<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 재고 관리</h1>
      </div>
      <div class="text-right">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">마지막 POS 동기화</p>
        <p class="text-sm font-semibold text-gray-700 mt-0.5">2026-04-13 13:00</p>
      </div>
    </div>

    <StoreInventoryTable
      :items="inventory"
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
      :editable="true"
      @apply-adjustments="applyLotAdjustments"
      @close="closeDetail"
    />
    <Toast :show="toast.show" :message="toast.message" :type="toast.type" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { changePosInventoryCount, getPosInventoryList } from '@/api/pos'
import InventoryDetailModal from '@/components/inventory/InventoryDetailModal.vue'
import StoreInventoryTable from '@/components/inventory/StoreInventoryTable.vue'
import Toast from '@/components/common/Toast.vue'
import { useToast } from '@/composables/useToast'

const { toast, showToast } = useToast()

const detailTitleId = 'store-inv-detail-title'
const detailItem = ref(null)
const inventory = ref([])

function formatDate(value) {
  if (!value) return null
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return null
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function mapInventoryRow(row) {
  return {
    idx: row.idx,
    productIdx: row.productIdx,
    code: `P${row.productIdx}`,
    name: row.productName,
    min: row.minStock ?? 0,
    apiStatus: row.status,
    lots: [
      {
        id: row.idx,
        expiry: formatDate(row.manufacturedDate),
        qty: row.count ?? 0,
      },
    ],
  }
}

function statusRank(status) {
  if (status === 'CRITICAL') return 0
  if (status === 'LOW') return 1
  return 2
}

function aggregateInventoryRows(list) {
  const grouped = new Map()
  for (const row of list) {
    if (!row || row.idx === null || row.idx === undefined) continue
    const mapped = mapInventoryRow(row)
    const key = String(mapped.productIdx)
    const existing = grouped.get(key)

    if (!existing) {
      grouped.set(key, mapped)
      continue
    }

    existing.lots.push(...mapped.lots)
    if (statusRank(mapped.apiStatus) < statusRank(existing.apiStatus)) {
      existing.apiStatus = mapped.apiStatus
    }
  }
  return Array.from(grouped.values()).sort((a, b) =>
    a.code.localeCompare(b.code, undefined, { numeric: true }),
  )
}

async function fetchInventory() {
  try {
    const { data } = await getPosInventoryList()
    const list = Array.isArray(data?.result) ? data.result : []
    inventory.value = aggregateInventoryRows(list)
  } catch (error) {
    console.error('Failed to fetch POS inventory:', error)
    inventory.value = []
  }
}

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

const getSubtitle = (item) => item.code

function openDetail(item) {
  detailItem.value = item
}

function closeDetail() {
  detailItem.value = null
}

async function applyLotAdjustments(changes) {
  if (!detailItem.value) return
  if (!Array.isArray(changes) || changes.length === 0) return

  try {
    await Promise.all(
      changes.map((row) => changePosInventoryCount(row.id, row.adjustTo)),
    )
    await fetchInventory()
    closeDetail()
    showToast('lot 재고 보정이 완료되었습니다.')
  } catch (error) {
    console.error('Failed to update POS inventory:', error)
    showToast('lot 재고 보정에 실패했습니다.', 'error')
  }
}

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  if (item.apiStatus === 'CRITICAL') return '위험'
  if (item.apiStatus === 'LOW') return '부족'
  const stock = totalStock(item)
  if (stock < item.min) return '부족'
  if (hasExpiringSoonLot(item)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.apiStatus === 'CRITICAL') return 'bg-red-50 text-red-700 border border-red-200'
  if (item.apiStatus === 'LOW') return 'bg-red-50 text-red-600 border border-red-200'
  const stock = totalStock(item)
  if (stock < item.min) return 'bg-red-50 text-red-600 border border-red-200'
  if (hasExpiringSoonLot(item)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}

onMounted(() => {
  fetchInventory()
})
</script>
