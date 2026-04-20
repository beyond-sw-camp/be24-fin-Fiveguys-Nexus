<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-start gap-4">
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주서 확인</h1>
    </div>

    <div class="flex border-b border-gray-200">
      <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors"
        :class="activeTab === tab.id ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
        <span v-if="tab.count > 0" class="ml-1.5 text-xs font-bold px-1.5 py-0.5 rounded"
          :class="activeTab === tab.id ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500'">
          {{ tab.count }}
        </span>
      </button>
    </div>

    <div v-if="activeTab === 'pending'" class="space-y-4">
      <div class="bg-blue-50 px-4 py-3 text-sm text-blue-700 rounded-lg border border-blue-100">
        재고 데이터를 기반으로 시스템이 자동 산출한 발주서입니다. 수량을 조정하거나 그대로 확정할 수 있습니다.
      </div>

      <div v-for="order in pendingOrders" :key="order.id" class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="px-5 py-3.5 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <div>
            <span class="text-xs font-mono text-gray-400">{{ order.id }}</span>
            <p class="font-bold text-gray-900 mt-0.5 text-sm">자동 발주 제안</p>
            <p class="text-[11px] text-gray-400 mt-0.5">생성일시: {{ order.createdAt }}</p>
          </div>
          <div class="flex gap-2">
            <button @click="openPaymentModal(order)"
              class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600 rounded transition-colors">
              전체 확정
            </button>
            <button @click="rejectOrder(order)"
              class="px-4 py-2 border border-gray-200 text-gray-600 text-sm font-semibold hover:bg-gray-50 rounded">
              거절
            </button>
          </div>
        </div>
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-100 bg-gray-50">
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재 재고</th>
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소 재고</th>
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">제안 수량</th>
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">수정 수량</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="item in order.items" :key="item.product" class="hover:bg-gray-50/50">
              <td class="px-5 py-3 font-semibold text-gray-900">{{ item.product }}</td>
              <td class="px-5 py-3 font-bold text-red-500">{{ item.current }}</td>
              <td class="px-5 py-3 text-gray-500">{{ item.min }}</td>
              <td class="px-5 py-3 font-semibold text-blue-600">{{ item.suggested }}</td>
              <td class="px-5 py-3">
                <input v-model.number="item.adjusted" type="number" min="0"
                  class="w-24 px-2 py-1.5 rounded border border-gray-200 text-sm focus:border-blue-400 outline-none" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="pendingOrders.length === 0" class="bg-white border border-gray-200 py-12 text-center text-gray-400 rounded-lg">
        <ClipboardList class="w-10 h-10 mx-auto mb-2 opacity-20" />
        <p class="text-sm">현재 검토 대기 중인 발주서가 없습니다.</p>
      </div>
    </div>

    <div v-if="activeTab === 'manual'" class="max-w-2xl">
      <div class="bg-white border border-gray-200 rounded-lg p-6 space-y-4 shadow-sm">
        <h3 class="font-bold text-gray-900 text-sm">직접 발주 요청</h3>
        <div class="space-y-2">
          <div class="flex justify-between items-center">
            <label class="text-xs font-bold text-gray-500 uppercase">발주 품목</label>
            <button @click="manualItems.push({ product: '', qty: 1 })"
              class="text-xs text-blue-500 font-semibold hover:underline flex items-center gap-1">
              <Plus class="w-3 h-3" /> 품목 추가
            </button>
          </div>
          <div v-for="(item, idx) in manualItems" :key="idx" class="flex gap-2 items-center">
            <select v-model="item.product" class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-blue-400">
              <option value="">품목 선택</option>
              <option>우유(1L)</option><option>두유(1L)</option><option>에스프레소 원두</option><option>바닐라 시럽</option>
            </select>
            <input v-model.number="item.qty" type="number" min="1" class="w-24 px-3 py-2 rounded border border-gray-200 text-sm" />
            <button @click="manualItems.splice(idx, 1)" class="text-gray-300 hover:text-red-500"><X class="w-4 h-4" /></button>
          </div>
        </div>
        <textarea v-model="manualNote" rows="2" placeholder="요청 사항 (선택)"
          class="w-full px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-blue-400 resize-none"></textarea>
        <button @click="submitManual" class="w-full py-3 bg-blue-500 text-white font-bold hover:bg-blue-600 rounded text-sm">
          발주 요청
        </button>
      </div>
    </div>

    <div v-if="activeTab === 'history'">
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden shadow-sm">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="h in orderHistory" :key="h.id" class="hover:bg-gray-50/50 transition-colors">
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ h.id }}</td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ h.product }}</td>
              <td class="px-5 py-3.5 text-gray-600">{{ h.qty }}개</td>
              <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ h.date }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="h.status === '입고완료' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-blue-50 text-blue-600 border border-blue-200'">
                  {{ h.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-[2px]">
      <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden animate-modal-up">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <h2 class="text-lg font-bold text-gray-900">결제 및 승인</h2>
          <button @click="isModalOpen = false" class="text-gray-400 hover:text-gray-600"><X class="w-5 h-5"/></button>
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
              <button class="flex-1 border border-dashed border-gray-300 rounded-lg p-3 text-gray-400 text-xs font-bold hover:bg-gray-50">+ 신규 등록</button>
            </div>
          </section>

          <section class="bg-gray-50 rounded-lg p-4">
            <div class="space-y-2 pb-3 border-b border-gray-200">
              <div v-for="item in selectedOrder?.items" :key="item.product" class="flex justify-between text-xs">
                <span class="text-gray-500">{{ item.product }} ({{ item.adjusted }}개)</span>
                <span class="font-medium text-gray-900">₩{{ (item.adjusted * 6500).toLocaleString() }}</span>
              </div>
            </div>
            <div class="flex justify-between items-center pt-3">
              <span class="text-sm font-bold text-gray-900">총 결제 금액</span>
              <span class="text-lg font-black text-red-600">₩{{ totalPrice.toLocaleString() }}</span>
            </div>
          </section>

          <section>
            <label class="text-[11px] font-bold text-gray-400 uppercase block mb-2">승인 요청 메시지</label>
            <textarea v-model="approvalMessage" placeholder="메모를 입력하세요." 
              class="w-full border border-gray-200 rounded-lg p-3 text-sm focus:ring-2 focus:ring-blue-100 outline-none resize-none h-20"></textarea>
          </section>
        </div>

        <div class="px-6 py-4 bg-gray-50 flex gap-2">
          <button @click="isModalOpen = false" class="flex-1 py-3 bg-white border border-gray-200 text-gray-600 font-bold rounded-lg text-sm">취소</button>
          <button @click="processPayment" class="flex-[2] py-3 bg-blue-600 text-white font-bold rounded-lg text-sm flex items-center justify-center gap-2">
            <CreditCard class="w-4 h-4"/> 결제 및 승인 요청
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, X, ClipboardList, CreditCard } from 'lucide-vue-next'

const activeTab = ref('pending')
const isModalOpen = ref(false)
const selectedOrder = ref(null)
const approvalMessage = ref('')

const pendingOrders = ref([
  {
    id: 'AUTO-20260413-001', createdAt: '2026-04-13 08:00',
    items: [
      { product: '우유(1L)',  current: 85, min: 120, suggested: 200, adjusted: 200 },
      { product: '두유(1L)',  current: 55, min: 60,  suggested: 80,  adjusted: 80  },
    ],
  },
  {
    id: 'AUTO-20260413-002', createdAt: '2026-04-13 08:00',
    items: [
      { product: '바닐라 시럽', current: 5,  min: 30, suggested: 60,  adjusted: 60  },
      { product: '카라멜 시럽', current: 12, min: 30, suggested: 40,  adjusted: 40  },
    ],
  },
])

const orderHistory = ref([
  { id: 'ORD-20260412-001', product: '종이컵(M)',     qty: 500, date: '2026-04-12 22:00', status: '배송중'   },
  { id: 'ORD-20260411-002', product: '에스프레소 원두', qty: 150, date: '2026-04-11 22:00', status: '입고완료' },
])

const tabs = computed(() => [
  { id: 'pending', label: '제안 발주서', count: pendingOrders.value.length },
  { id: 'manual', label: '직접 발주', count: 0 },
  { id: 'history', label: '발주 이력', count: orderHistory.value.length },
])

const manualItems = ref([])
const manualNote = ref('')

// 모달 열기 함수
function openPaymentModal(order) {
  selectedOrder.value = order
  approvalMessage.value = ""
  isModalOpen.value = true
}

// 총 금액 계산 (단가 6,500원 가정)
const totalPrice = computed(() => {
  if (!selectedOrder.value) return 0
  return selectedOrder.value.items.reduce((sum, item) => sum + (item.adjusted * 6500), 0)
})

// 결제 프로세스 완료
function processPayment() {
  const order = selectedOrder.value
  const idx = pendingOrders.value.indexOf(order)
  if (idx > -1) {
    order.items.forEach(item => {
      orderHistory.value.unshift({
        id: order.id.replace('AUTO', 'ORD'),
        product: item.product,
        qty: item.adjusted,
        supplier: order.supplier,
        date: new Date().toISOString().split('T')[0],
        status: '승인대기',
      })
    })
    pendingOrders.value.splice(idx, 1)
    isModalOpen.value = false
    alert('결제 및 승인 요청이 완료되었습니다.')
    activeTab.value = 'history'
  }
}

function rejectOrder(order) {
  if (confirm('발주서를 거절하시겠습니까?')) {
    const idx = pendingOrders.value.indexOf(order)
    pendingOrders.value.splice(idx, 1)
  }
}

function submitManual() {
  if (manualItems.value.length === 0) { alert('품목을 추가해주세요.'); return }
  manualItems.value.forEach(item => {
    if (item.product) {
      orderHistory.value.unshift({
        id: `ORD-${Date.now().toString().slice(-6)}`,
        product: item.product,
        qty: item.qty,
        supplier: '본사 직접발주',
        date: new Date().toISOString().split('T')[0],
        status: '승인대기',
      })
    }
  })
  manualItems.value = []
  activeTab.value = 'history'
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