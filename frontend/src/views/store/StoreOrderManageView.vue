<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Plus } from 'lucide-vue-next'
import StoreManualOrderModal from '@/components/orders/StoreManualOrderModal.vue'
import StoreOrderHistoryTable from '@/components/orders/StoreOrderHistoryTable.vue'
import StoreOrderDetailModal from '@/components/orders/StoreOrderDetailModal.vue'
import StorePendingOrderList from '@/components/orders/StorePendingOrderList.vue'
import StoreOrderConfirmModal from '@/components/orders/StoreOrderConfirmModal.vue'
import StoreOrderRejectModal from '@/components/orders/StoreOrderRejectModal.vue'
import ordersApi from '@/api/orders'

const activeTab = ref('pending')
const selectedOrder = ref(null)
const isConfirmModalOpen = ref(false)
const isRejectModalOpen = ref(false)

const pendingOrders = ref([])
const orderHistory = ref([])

async function fetchPendingOrders() {
  try {
    const res = await ordersApi.getStorePendingOrders()
    pendingOrders.value = res.data.result || []
  } catch (e) {
    console.error('제안 발주서 조회 실패', e)
  }
}

async function fetchOrderHistory() {
  try {
    const res = await ordersApi.getStoreOrderList()
    orderHistory.value = res.data.result
  } catch (e) {
    console.error('발주 이력 조회 실패', e)
  }
}

onMounted(() => {
  fetchPendingOrders()
})

watch(activeTab, (tab) => {
  if (tab === 'history') {
    fetchOrderHistory()
  }
})

const selectedHistory = ref(null)

function openHistoryDetail(h) {
  selectedHistory.value = h
}

const tabs = computed(() => [
  { id: 'pending', label: '제안 발주서', count: pendingOrders.value.length },
  { id: 'history', label: '발주 이력', count: orderHistory.value.length },
])

function openConfirmModal(order) {
  selectedOrder.value = order
  isConfirmModalOpen.value = true
}

async function confirmOrder() {
  const order = selectedOrder.value
  try {
    await ordersApi.confirmStoreOrder(order.idx)
    const idx = pendingOrders.value.indexOf(order)
    if (idx > -1) pendingOrders.value.splice(idx, 1)
    isConfirmModalOpen.value = false
    alert('발주서가 확정되었습니다.')
    fetchOrderHistory()
  } catch (e) {
    console.error('발주서 확정 실패', e)
    alert('발주서 확정에 실패했습니다.')
  }
}

function openRejectModal(order) {
  selectedOrder.value = order
  isRejectModalOpen.value = true
}

async function rejectOrder() {
  const order = selectedOrder.value
  try {
    await ordersApi.rejectStoreOrder(order.idx)
    const idx = pendingOrders.value.indexOf(order)
    if (idx > -1) pendingOrders.value.splice(idx, 1)
    isRejectModalOpen.value = false
    alert('발주서가 거절되었습니다.')
  } catch (e) {
    console.error('발주서 거절 실패', e)
    alert('발주서 거절에 실패했습니다.')
  }
}

async function cancelOrder(order) {
  if (!confirm('발주를 취소하시겠습니까?')) return
  try {
    await ordersApi.cancelOrder(order.idx)
    order.ordersStatus = 'CANCELLED'
    alert('발주가 취소되었습니다.')
  } catch (e) {
    console.error('발주 취소 실패', e)
    alert('발주 취소에 실패했습니다.')
  }
}

const showManualForm = ref(false)

async function submitManualOrder(data) {
  try {
    await ordersApi.createStoreManualOrder(data)
    alert('발주가 생성되었습니다.')
    showManualForm.value = false
    activeTab.value = 'pending'
  } catch (e) {
    console.error('수동 발주 생성 실패', e)
    alert('발주 생성에 실패했습니다.')
  }
}
</script>

<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-start gap-4">
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주서 확인</h1>
      <button @click="showManualForm = true"
        class="bg-blue-500 text-white px-4 py-2 text-sm font-semibold rounded-lg hover:bg-blue-600 transition-colors flex items-center gap-2 cursor-pointer">
        <Plus class="w-4 h-4" /> 수동 발주 생성
      </button>
    </div>

    <div class="flex border-b border-gray-200">
      <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors cursor-pointer"
        :class="activeTab === tab.id ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
        <span v-if="tab.count > 0" class="ml-1.5 text-xs font-bold px-1.5 py-0.5 rounded"
          :class="activeTab === tab.id ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500'">
          {{ tab.count }}
        </span>
      </button>
    </div>

    <StorePendingOrderList v-if="activeTab === 'pending'" :orders="pendingOrders"
      @confirm="openConfirmModal" @reject="openRejectModal" @refresh="fetchPendingOrders" />

    <StoreOrderHistoryTable v-if="activeTab === 'history'" :orders="orderHistory"
      @open-detail="openHistoryDetail" @cancel="cancelOrder" />

    <StoreOrderDetailModal :order="selectedHistory" @close="selectedHistory = null" />

    <StoreOrderConfirmModal :order="selectedOrder" :visible="isConfirmModalOpen"
      @close="isConfirmModalOpen = false" @confirm="confirmOrder" />

    <StoreOrderRejectModal :order="selectedOrder" :visible="isRejectModalOpen"
      @close="isRejectModalOpen = false" @reject="rejectOrder" />

    <StoreManualOrderModal :visible="showManualForm" @close="showManualForm = false" @submit="submitManualOrder" />
  </div>
</template>
