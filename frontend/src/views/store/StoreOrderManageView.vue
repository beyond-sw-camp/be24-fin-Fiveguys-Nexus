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
      <div v-for="order in pendingOrders" :key="order.idx" class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="px-5 py-3.5 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <div>
            <span class="text-xs font-mono text-gray-400">No.{{ order.idx }}</span>
            <p class="font-bold text-gray-900 mt-0.5 text-sm">자동 발주 제안</p>
            <p class="text-[11px] text-gray-400 mt-0.5">생성일시: {{ order.createdAt }}</p>
          </div>
          <div class="flex gap-2">
            <button @click="openPaymentModal(order)"
              class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600 rounded-lg transition-colors cursor-pointer">
              전체 확정
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

        <!-- AI 추천 이유 배너 -->
        <div v-if="order.aiReason" class="border-b border-purple-100 bg-purple-50/60">
          <button
            class="w-full px-5 py-3 flex items-center justify-between cursor-pointer hover:bg-purple-50 transition-colors"
            @click="toggleAiReason(order.id)">
            <div class="flex items-center gap-2.5 min-w-0">
              <span class="flex items-center justify-center w-6 h-6 rounded-full bg-purple-100 shrink-0">
                <Sparkles class="w-3.5 h-3.5 text-purple-600" />
              </span>
              <span class="text-xs font-bold text-purple-700 shrink-0">AI 추천 이유</span>
              <span class="text-xs text-purple-600/80 truncate">{{ order.aiReason.summary }}</span>
            </div>
            <div class="flex items-center gap-2 shrink-0 ml-2">
              <div class="flex gap-1.5">
                <span
                  v-for="(tag, idx) in order.aiReason.contexts"
                  :key="`${order.id}-tag-${idx}`"
                  class="hidden sm:inline text-[10px] font-semibold px-2 py-0.5 rounded-full bg-purple-100 text-purple-600 border border-purple-200">
                  {{ tag }}
                </span>
              </div>
              <ChevronDown
                class="w-4 h-4 text-purple-400 transition-transform duration-200 shrink-0"
                :class="expandedAiReasons.has(order.id) ? 'rotate-180' : ''" />
            </div>
          </button>

          <div v-if="expandedAiReasons.has(order.id)" class="px-5 pb-4 space-y-3">
            <div class="sm:hidden flex gap-1.5 flex-wrap">
              <span
                v-for="(tag, idx) in order.aiReason.contexts"
                :key="`${order.id}-tag-mobile-${idx}`"
                class="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-purple-100 text-purple-600 border border-purple-200">
                {{ tag }}
              </span>
            </div>
            <p class="text-xs text-gray-600 leading-relaxed bg-white border border-purple-100 rounded-lg px-4 py-3">
              {{ order.aiReason.detail }}
            </p>
            <div class="grid grid-cols-2 gap-3">
              <div class="bg-white border border-purple-100 rounded-lg px-4 py-2.5 text-center">
                <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1">최소 발주량</p>
                <p class="text-sm font-bold text-gray-700">{{ minQtyLabel(order) }}</p>
              </div>
              <div class="bg-purple-600 rounded-lg px-4 py-2.5 text-center">
                <p class="text-[10px] font-bold text-purple-200 uppercase tracking-wider mb-1">AI 추천 수량</p>
                <p class="text-sm font-bold text-white">{{ recommendedQtyLabel(order) }}</p>
              </div>
            </div>
          </div>
        </div>
        <table class="w-full text-sm">
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
          <h2 class="text-lg font-bold text-gray-900">결제 및 승인</h2>
          <button @click="isModalOpen = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <div class="p-6 space-y-6">
          <section>
            <label class="text-[11px] font-bold text-gray-400 uppercase block mb-3">결제 수단</label>
            <div class="flex gap-3">
              <div class="flex-1 border-2 border-blue-500 rounded-lg p-3 bg-blue-50 flex items-center gap-3">
                <div class="w-10 h-6 bg-slate-800 rounded flex items-center justify-center">
                   <div class="w-2 h-2 bg-amber-400 rounded-full"></div>
                </div>
                <div>
                  <p class="text-[11px] font-bold text-blue-600 leading-none">등록된 카드</p>
                  <p class="text-[10px] text-gray-500 mt-1">현대카드 (****-1234)</p>
                </div>
              </div>
              <button class="flex-1 border border-dashed border-gray-300 rounded-lg p-3 text-gray-400 text-xs font-bold hover:bg-gray-50 cursor-pointer">+ 신규 등록</button>
            </div>
          </section>

          <section class="bg-gray-50 rounded-lg p-4">
            <div class="space-y-2 pb-3 border-b border-gray-200">
              <div v-for="item in selectedOrder?.items" :key="item.product" class="flex justify-between text-xs">
                <span class="text-gray-500">{{ item.product }} ({{ item.adjusted }}개 × {{ (PRODUCT_PRICES[item.product] ?? 0).toLocaleString() }}원)</span>
                <span class="font-medium text-gray-900">₩ {{ ((item.adjusted || 0) * (PRODUCT_PRICES[item.product] ?? 0)).toLocaleString() }}</span>
              </div>
            </div>
            <div class="flex justify-between items-center pt-3">
              <span class="text-sm font-bold text-gray-900">총 결제 금액</span>
              <span class="text-lg font-black text-blue-600">₩ {{totalPrice.toLocaleString() }}</span>
            </div>
          </section>

          <section>
            <label class="text-[11px] font-bold text-gray-400 uppercase block mb-2">승인 요청 메시지</label>
            <textarea v-model="approvalMessage" placeholder="메모를 입력하세요."
              class="w-full border border-gray-200 rounded-lg p-3 text-sm focus:ring-2 focus:ring-blue-100 outline-none resize-none h-20"></textarea>
          </section>
        </div>

        <div class="px-6 py-4 bg-gray-50 flex gap-2">
          <button @click="isModalOpen = false" class="flex-1 py-3 bg-white border border-gray-200 text-gray-600 font-bold rounded-lg text-sm cursor-pointer">취소</button>
          <button @click="processPayment" class="flex-2 py-3 bg-blue-600 text-white font-bold rounded-lg text-sm flex items-center justify-center gap-2 cursor-pointer">
            <CreditCard class="w-4 h-4"/> 결제
          </button>
        </div>
      </div>
    </div>

    <StoreManualOrderModal :visible="showManualForm" @close="showManualForm = false" @submit="submitManualOrder" />
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ClipboardList, CreditCard, Sparkles, ChevronDown, Plus } from 'lucide-vue-next'
import StoreManualOrderModal from '@/components/orders/StoreManualOrderModal.vue'
import StoreOrderHistoryTable from '@/components/orders/StoreOrderHistoryTable.vue'
import StoreOrderDetailModal from '@/components/orders/StoreOrderDetailModal.vue'
import ordersApi from '@/api/orders'

const activeTab = ref('pending')
const isModalOpen = ref(false)
const selectedOrder = ref(null)
const approvalMessage = ref('')

const expandedAiReasons = reactive(new Set())

function toggleAiReason(orderId) {
  if (expandedAiReasons.has(orderId)) {
    expandedAiReasons.delete(orderId)
  } else {
    expandedAiReasons.add(orderId)
  }
}

function minQtyLabel(order) {
  return order.items
    .map(i => `${i.product} ${i.min}${PRODUCT_UNIT[i.product] ?? ''}`)
    .join(' · ')
}

function recommendedQtyLabel(order) {
  return order.items
    .map(i => `${i.product} ${i.suggested}${PRODUCT_UNIT[i.product] ?? ''}`)
    .join(' · ')
}

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


// 모달 열기 함수
function openPaymentModal(order) {
  selectedOrder.value = order
  approvalMessage.value = ""
  isModalOpen.value = true
}

const totalPrice = computed(() => {
  if (!selectedOrder.value) return 0
  return selectedOrder.value.items.reduce((sum, item) => sum + item.adjusted * (PRODUCT_PRICES[item.product] ?? 0), 0)
})

// 결제 프로세스 완료
function processPayment() {
  const order = selectedOrder.value
  const idx = pendingOrders.value.indexOf(order)
  if (idx > -1) {
    orderHistory.value.unshift({
      id: order.id.replace('AUTO', 'ORD'),
      type: '자동',
      date: new Date().toISOString().slice(0, 16).replace('T', ' '),
      status: '승인대기',
      items: order.items.map(item => ({ product: item.product, qty: item.adjusted })),
    })
    pendingOrders.value.splice(idx, 1)
    isModalOpen.value = false
    alert('결제 및 승인 요청이 완료되었습니다.')
    activeTab.value = 'history'
  }
}

const addItemForm = ref(null)

function openAddItemForm(order) {
  addItemForm.value = { orderId: order.id, product: '', qty: 1 }
}

function availableProducts(order) {
  const existing = new Set(order.items.map(i => i.product))
  return Object.keys(PRODUCT_PRICES).filter(p => !existing.has(p))
}

function confirmAddItem(order) {
  const form = addItemForm.value
  if (!form.product) { alert('품목을 선택해주세요.'); return }
  if (!form.qty || form.qty < 1) { alert('수량을 입력해주세요.'); return }
  const stock = PRODUCT_STOCK[form.product] ?? { current: '-', min: '-' }
  order.items.push({ product: form.product, current: stock.current, min: stock.min, suggested: form.qty, adjusted: form.qty })
  addItemForm.value = null
}

function removeOrderItem(order, item) {
  const idx = order.items.indexOf(item)
  if (idx > -1) order.items.splice(idx, 1)
}

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
