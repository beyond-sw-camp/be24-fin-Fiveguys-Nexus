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

    <div v-if="activeTab === 'pending'" class="space-y-4">
      <div v-for="order in pendingOrders" :key="order.idx" class="bg-white border border-gray-200 rounded-lg">
        <div class="px-5 py-3.5 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <div>
            <span class="text-xs font-mono text-gray-400">No.{{ order.idx }}</span>
            <p class="font-bold text-gray-900 mt-0.5 text-sm">자동 발주 제안</p>
            <p class="text-[11px] text-gray-400 mt-0.5">생성일시: {{ order.createdAt }}</p>
          </div>
          <div class="flex gap-2">
            <button @click="openConfirmModal(order)"
              class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600 rounded-lg transition-colors cursor-pointer">
              확정
            </button>
            <button @click="openAddItemForm(order)"
              class="px-4 py-2 border border-blue-200 text-blue-500 bg-blue-50 text-sm font-semibold hover:bg-blue-100 rounded-lg transition-colors cursor-pointer">
              + 품목 추가
            </button>
            <button @click="rejectOrder(order)"
              class="px-4 py-2 border border-gray-200 text-gray-600 text-sm font-semibold hover:bg-gray-50 rounded-lg cursor-pointer">
              거절
            </button>
          </div>
        </div>
        <table class="w-full text-sm table-fixed">
          <colgroup>
            <col class="w-[40%]" />
            <col class="w-[15%]" />
            <col class="w-[20%]" />
            <col class="w-[25%]" />
          </colgroup>
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">단가</th>
              <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">금액</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="item in order.ordersItemList" :key="item.idx" class="hover:bg-gray-50/50">
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.productName }}</td>
              <td class="px-5 py-3.5 font-semibold text-blue-600">{{ item.count }}</td>
              <td class="px-5 py-3.5 text-gray-500">₩ {{ (item.unitPrice ?? 0).toLocaleString() }}</td>
              <td class="px-5 py-3.5 text-right font-semibold text-gray-700">
                ₩ {{ ((item.count || 0) * (item.unitPrice ?? 0)).toLocaleString() }}
              </td>
            </tr>
            <tr v-if="addItemForm?.ordersIdx === order.idx" class="bg-blue-50/50 border-t border-blue-100">
              <td class="px-5 py-3">
                <div class="relative">
                  <div v-if="addItemForm.productIdx" class="flex items-center justify-between px-2 py-1.5 rounded-lg border border-blue-400 bg-blue-50 text-sm">
                    <span class="font-medium text-gray-900">{{ addItemForm.productName }}</span>
                    <button @click="clearAddItemProduct" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
                  </div>
                  <input v-else v-model="addItemForm.keyword" @input="addItemForm.showDropdown = true" @focus="addItemForm.showDropdown = true" placeholder="상품명 검색"
                    class="w-full px-2 py-1.5 rounded-lg border border-blue-200 text-sm outline-none focus:border-blue-400" />
                  <ul v-if="!addItemForm.productIdx && addItemForm.showDropdown && filteredProducts(order).length > 0"
                    class="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                    <li v-for="p in filteredProducts(order)" :key="p.idx" @click="selectAddItemProduct(p)"
                      class="px-3 py-2 text-sm hover:bg-blue-50 cursor-pointer">
                      {{ p.productName }} <span class="text-gray-400 text-xs">{{ p.productUnit }}</span>
                    </li>
                  </ul>
                </div>
              </td>
              <td class="px-5 py-3">
                <input v-model.number="addItemForm.count" type="number" min="1" placeholder="수량"
                  class="w-20 px-2 py-1.5 rounded-lg border border-blue-200 text-sm outline-none focus:border-blue-400" />
              </td>
              <td class="px-5 py-3 text-xs text-gray-400">—</td>
              <td class="px-5 py-3">
                <div class="flex justify-end gap-1.5">
                  <button @click="submitAddItem(order)"
                    class="px-3 py-1.5 text-xs font-semibold rounded-lg border border-blue-300 text-blue-600 bg-white hover:bg-blue-500 hover:text-white hover:cursor-pointer transition-colors">
                    추가
                  </button>
                  <button @click="addItemForm = null"
                    class="px-3 py-1.5 text-xs font-semibold rounded-lg border border-gray-200 text-gray-500 bg-white hover:bg-gray-100 hover:cursor-pointer transition-colors">
                    취소
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
          <tfoot class="border-t border-gray-200 bg-gray-50/60">
            <tr>
              <td class="px-5 py-3 text-left text-xs font-bold text-gray-500">합계</td>
              <td colspan="2"></td>
              <td class="px-5 py-3 text-right font-black text-blue-600">
                ₩ {{ (order.price ?? 0).toLocaleString() }}
              </td>
            </tr>
          </tfoot>
        </table>
      </div>

      <div v-if="pendingOrders.length === 0" class="bg-white border border-gray-200 py-12 text-center text-gray-400 rounded-lg">
        <ClipboardList class="w-10 h-10 mx-auto mb-2 opacity-20" />
        <p class="text-sm">현재 검토 대기 중인 발주서가 없습니다.</p>
        <p class="text-xs mt-1 text-gray-400">발주가 필요한 경우 본사에 문의하세요.</p>
      </div>
    </div>


    <StoreOrderHistoryTable v-if="activeTab === 'history'" :orders="orderHistory"
      @open-detail="openHistoryDetail" @cancel="cancelOrder" />

    <StoreOrderDetailModal :order="selectedHistory" @close="selectedHistory = null" />

    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-[2px]">
      <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden animate-modal-up">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <h2 class="text-lg font-bold text-gray-900">발주서 확정</h2>
          <button @click="isModalOpen = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <div class="p-6 space-y-4">
          <p class="text-sm text-gray-500">아래 품목으로 발주서를 확정하시겠습니까?</p>

          <div class="bg-gray-50 rounded-lg p-4">
            <div class="space-y-2 pb-3 border-b border-gray-200">
              <div v-for="item in selectedOrder?.ordersItemList" :key="item.idx" class="flex justify-between text-xs">
                <span class="text-gray-500">{{ item.productName }} ({{ item.count }}개 × ₩{{ (item.unitPrice ?? 0).toLocaleString() }})</span>
                <span class="font-medium text-gray-900">₩ {{ ((item.count || 0) * (item.unitPrice ?? 0)).toLocaleString() }}</span>
              </div>
            </div>
            <div class="flex justify-between items-center pt-3">
              <span class="text-sm font-bold text-gray-900">합계</span>
              <span class="text-lg font-black text-blue-600">₩ {{ (selectedOrder?.price ?? 0).toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <div class="px-6 py-4 bg-gray-50 flex gap-2">
          <button @click="isModalOpen = false" class="flex-1 py-3 bg-white border border-gray-200 text-gray-600 font-bold rounded-lg text-sm cursor-pointer">취소</button>
          <button @click="confirmOrder" class="flex-1 py-3 bg-blue-600 text-white font-bold rounded-lg text-sm cursor-pointer">확정</button>
        </div>
      </div>
    </div>

    <StoreManualOrderModal :visible="showManualForm" @close="showManualForm = false" @submit="submitManualOrder" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ClipboardList, Plus } from 'lucide-vue-next'
import StoreManualOrderModal from '@/components/orders/StoreManualOrderModal.vue'
import StoreOrderHistoryTable from '@/components/orders/StoreOrderHistoryTable.vue'
import StoreOrderDetailModal from '@/components/orders/StoreOrderDetailModal.vue'
import ordersApi from '@/api/orders'
import { useAddOrderItem } from '@/composables/useAddOrderItem'

const activeTab = ref('pending')
const isModalOpen = ref(false)
const selectedOrder = ref(null)

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
  fetchOrderHistory()
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
  isModalOpen.value = true
}

async function confirmOrder() {
  const order = selectedOrder.value
  try {
    await ordersApi.confirmStoreOrder(order.idx)
    const idx = pendingOrders.value.indexOf(order)
    if (idx > -1) pendingOrders.value.splice(idx, 1)
    isModalOpen.value = false
    alert('발주서가 확정되었습니다.')
    fetchOrderHistory()
  } catch (e) {
    console.error('발주서 확정 실패', e)
    alert('발주서 확정에 실패했습니다.')
  }
}

const { addItemForm, openAddItemForm, filteredProducts, selectAddItemProduct, clearAddItemProduct, submitAddItem } = useAddOrderItem(fetchPendingOrders)

function rejectOrder(order) {
  if (confirm('발주서를 거절하시겠습니까?')) {
    const idx = pendingOrders.value.indexOf(order)
    pendingOrders.value.splice(idx, 1)
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

<style scoped>
.animate-modal-up {
  animation: modalUp 0.3s ease-out;
}
@keyframes modalUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
