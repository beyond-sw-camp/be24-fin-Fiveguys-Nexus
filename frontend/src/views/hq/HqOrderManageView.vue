<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus, Settings } from 'lucide-vue-next'
import ordersApi from '@/api/orders'
import HqAutoOrderTable from '@/components/orders/HqAutoOrderTable.vue'
import HqManualOrderTable from '@/components/orders/HqManualOrderTable.vue'
import HqOrderHistoryTable from '@/components/orders/HqOrderHistoryTable.vue'
import HqAbnormalOrderTable from '@/components/orders/HqAbnormalOrderTable.vue'
import HqOrderDetailModal from '@/components/orders/HqOrderDetailModal.vue'
import HqManualOrderModal from '@/components/orders/HqManualOrderModal.vue'
import HqDangerSettingsModal from '@/components/orders/HqDangerSettingsModal.vue'

const route = useRoute()
const router = useRouter()

const abnormalCount = computed(() => abnormalOrders.value.filter(o => !o.processed).length)

const tabs = computed(() => [
  { id: 'auto',     label: '자동 발주 제안', badge: autoOrders.value.length || null },
  { id: 'manual',   label: '수동 발주 제안' },
  { id: 'history',  label: '발주 이력' },
  { id: 'abnormal', label: '이상 발주', badge: abnormalCount.value || null },
])
const activeTab = ref('auto')

const autoOrders = ref([])

async function fetchAutoOrders() {
  try {
    const res = await ordersApi.getAutoOrders()
    autoOrders.value = res.data.result.map(o => ({
      id: o.idx,
      store: o.storeName,
      date: o.createdAt?.replace('T', ' ').slice(0, 16) ?? '-',
      price: o.price,
      status: '제안중',
    }))
  } catch (e) {
    console.error('자동 발주 목록 조회 실패', e)
  }
}

const orderHistory = ref([
  { id: 'ORD-20260413-001', type: '자동', store: '차이나 가든',    date: '2026-04-12 22:00', status: '배송중',
    items: [{ product: '양파', qty: 20, unitPrice: 1500 }, { product: '생수', qty: 10, unitPrice: 8000 }] },
  { id: 'ORD-20260413-002', type: '수동', store: '한우 오마카세',  date: '2026-04-11 10:30', status: '입고완료',
    items: [{ product: '한우 등심', qty: 15, unitPrice: 85000 }, { product: '올리브오일', qty: 5, unitPrice: 12000 }] },
  { id: 'ORD-20260412-003', type: '자동', store: '이탈리안 키친',  date: '2026-04-11 22:00', status: '입고완료',
    items: [{ product: '버터', qty: 10, unitPrice: 9000 }, { product: '생크림', qty: 8, unitPrice: 7000 }, { product: '간장', qty: 5, unitPrice: 4000 }] },
  { id: 'ORD-20260411-004', type: '수동', store: '일식 스시바',    date: '2026-04-10 14:15', status: '입고완료',
    items: [{ product: '연어', qty: 10, unitPrice: 32000 }, { product: '마늘', qty: 5, unitPrice: 8000 }] },
])

const abnormalOrders = ref([
  { id: 'ORD-20260414-ABN1', store: '이탈리안 키친', qty: 85, avgQty: 15, ratio: 567, date: '2026-04-14 11:22', processed: false,
    items: [
      { product: '한우 등심',  qty: 85, unitPrice: 85000, avgQty: 15, ratio: 567, isAbnormal: true  },
      { product: '올리브오일', qty: 6,  unitPrice: 12000, avgQty: 5,  ratio: 20,  isAbnormal: false },
    ] },
  { id: 'ORD-20260413-ABN2', store: '일식 스시바',   qty: 60, avgQty: 10, ratio: 500, date: '2026-04-13 09:15', processed: false,
    items: [
      { product: '버터',   qty: 60, unitPrice: 9000, avgQty: 10, ratio: 500, isAbnormal: true  },
      { product: '생크림', qty: 12, unitPrice: 7000, avgQty: 11, ratio: 9,   isAbnormal: false },
    ] },
  { id: 'ORD-20260412-ABN3', store: '차이나 가든',   qty: 500, avgQty: 50, ratio: 900, date: '2026-04-12 16:40', processed: true,
    items: [
      { product: '양파', qty: 500, unitPrice: 1500, avgQty: 50, ratio: 900, isAbnormal: true  },
      { product: '생수', qty: 40,  unitPrice: 8000, avgQty: 38, ratio: 5,   isAbnormal: false },
    ] },
])

const pendingManualOrders = ref([
  { id: 'MAN-20260420-001', store: '이탈리안 키친', date: '2026-04-20 09:10', status: '확정',
    items: [{ product: '한우 등심', qty: 10, unitPrice: 85000 }, { product: '버터', qty: 5, unitPrice: 9000 }] },
  { id: 'MAN-20260419-002', store: '일식 스시바',   date: '2026-04-19 14:30', status: '배송중',
    items: [{ product: '올리브오일', qty: 8, unitPrice: 12000 }, { product: '간장', qty: 5, unitPrice: 4000 }, { product: '생수', qty: 10, unitPrice: 8000 }] },
])

// Tab routing
function applyOrderRouteQuery() {
  const q = route.query
  if (q.tab && ['auto', 'manual', 'history', 'abnormal'].includes(q.tab)) {
    activeTab.value = q.tab
  }
}

onMounted(() => {
  applyOrderRouteQuery()
  fetchAutoOrders()
})

function setOrderViewTab(id) {
  activeTab.value = id
  router.replace({ path: '/order', query: { tab: id } })
}

// Detail modal
const selectedOrder = ref(null)

async function openDetail(ordersIdx) {
  try {
    const res = await ordersApi.getOrderDetail(ordersIdx)
    const o = res.data.result
    selectedOrder.value = {
      id: o.idx,
      type: o.ordersType === 'AUTO' ? '자동' : '수동',
      store: o.storeName,
      date: o.createdAt?.replace('T', ' ').slice(0, 16) ?? '-',
      status: o.ordersStatus === 'WAITING' ? '제안중' : o.ordersStatus === 'APPROVE' ? '확정' : '거절',
      items: o.ordersItemList.map(i => ({
        product: i.productName,
        qty: i.count,
        unitPrice: i.unitPrice,
      })),
    }
  } catch (e) {
    console.error('발주 상세 조회 실패', e)
  }
}

// Settings modal
const showSettings = ref(false)
const dangerSettings = ref({ ratio: null, period: null })

async function openDangerSettings() {
  try {
    const res = await ordersApi.getDangerSettings()
    const s = res.data.result
    dangerSettings.value = { ratio: s.ratio, period: s.period }
  } catch (e) {
    console.error('이상 발주 기준 조회 실패', e)
  }
  showSettings.value = true
}

async function saveDangerSettings({ threshold, months }) {
  try {
    await ordersApi.updateDangerSettings({ ratio: threshold, period: months })
    dangerSettings.value = { ratio: threshold, period: months }
    showSettings.value = false
  } catch (e) {
    console.error('이상 발주 기준 저장 실패', e)
  }
}

// Abnormal order actions
function approveAbnormal(o) { o.processed = true; alert(`${o.store} 발주 승인 처리되었습니다.`) }
function rejectAbnormal(o)  { o.processed = true; alert(`${o.store} 발주 반려 처리되었습니다.`) }

// Manual order modal
const showManualForm = ref(false)

function submitManualOrder({ store, items }) {
  const now = new Date().toISOString().slice(0, 16).replace('T', ' ')
  orderHistory.value.unshift({
    id: `ORD-${Date.now()}`,
    type: '수동',
    store,
    date: now,
    status: '확정',
    items,
  })
  alert('발주가 생성되었습니다.')
  showManualForm.value = false
  setOrderViewTab('history')
}
</script>

<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-start gap-4">
      <div class="min-w-0 flex-1">
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주 관리</h1>
      </div>
      <div class="flex gap-2">
        <button @click="openDangerSettings"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2 cursor-pointer">
          <Settings class="w-4 h-4" /> 이상 발주 기준 설정
        </button>
        <button @click="showManualForm = true"
          class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded hover:bg-[#e0661d] transition-colors flex items-center gap-2 cursor-pointer">
          <Plus class="w-4 h-4" /> 수동 발주 생성
        </button>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex border-b border-gray-200">
      <button v-for="tab in tabs" :key="tab.id"
        @click="setOrderViewTab(tab.id)"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors cursor-pointer"
        :class="activeTab === tab.id
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
        <span v-if="tab.badge"
          class="ml-1.5 text-xs font-bold px-1.5 py-0.5 rounded"
          :class="activeTab === tab.id ? 'bg-orange-100 text-[#F37321]' : 'bg-gray-100 text-gray-500'">
          {{ tab.badge }}
        </span>
      </button>
    </div>

    <!-- Tab Contents -->
    <HqAutoOrderTable v-if="activeTab === 'auto'" :orders="autoOrders" @open-detail="openDetail" />
    <HqManualOrderTable v-if="activeTab === 'manual'" :orders="pendingManualOrders" @open-detail="openDetail" />
    <HqOrderHistoryTable v-if="activeTab === 'history'" :orders="orderHistory" @open-detail="openDetail" />
    <HqAbnormalOrderTable v-if="activeTab === 'abnormal'" :orders="abnormalOrders"
      @open-detail="openDetail" @approve="approveAbnormal" @reject="rejectAbnormal" />

    <!-- Modals -->
    <HqManualOrderModal :visible="showManualForm" @close="showManualForm = false" @submit="submitManualOrder" />
    <HqOrderDetailModal :order="selectedOrder" @close="selectedOrder = null" />
    <HqDangerSettingsModal :visible="showSettings" :init-threshold="dangerSettings.ratio" :init-months="dangerSettings.period" @close="showSettings = false" @save="saveDangerSettings" />
  </div>
</template>
