<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-start gap-4">
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주서 확인</h1>
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
      <div v-for="order in pendingOrders" :key="order.id" class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="px-5 py-3.5 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <div>
            <span class="text-xs font-mono text-gray-400">{{ order.id }}</span>
            <p class="font-bold text-gray-900 mt-0.5 text-sm">자동 발주 제안</p>
            <p class="text-[11px] text-gray-400 mt-0.5">생성일시: {{ order.createdAt }}</p>
          </div>
          <div class="flex gap-2">
            <button @click="openPaymentModal(order)"
              class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600 rounded transition-colors cursor-pointer">
              전체 확정
            </button>
            <button @click="openAddItemForm(order)"
              class="px-4 py-2 border border-blue-200 text-blue-500 bg-blue-50 text-sm font-semibold hover:bg-blue-100 rounded transition-colors cursor-pointer">
              + 품목 추가
            </button>
            <button @click="rejectOrder(order)"
              class="px-4 py-2 border border-gray-200 text-gray-600 text-sm font-semibold hover:bg-gray-50 rounded cursor-pointer">
              거절
            </button>
          </div>
        </div>
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재 재고</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소 재고</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">제안 수량</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">수정 수량</th>
              <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider"></th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="item in order.items" :key="item.product" class="hover:bg-gray-50/50">
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.product }}</td>
              <td class="px-5 py-3.5 font-bold text-red-500">{{ item.current }}<span class="text-xs font-normal ml-0.5">{{ PRODUCT_UNIT[item.product] ?? '' }}</span></td>
              <td class="px-5 py-3.5 text-gray-500">{{ item.min }}<span class="text-xs ml-0.5">{{ PRODUCT_UNIT[item.product] ?? '' }}</span></td>
              <td class="px-5 py-3.5 font-semibold text-blue-600">{{ item.suggested }}<span class="text-xs font-normal ml-0.5">{{ PRODUCT_UNIT[item.product] ?? '' }}</span></td>
              <td class="px-5 py-3.5">
                <div class="flex items-center gap-1.5">
                  <input v-model.number="item.adjusted" type="number" min="0"
                    class="w-20 px-2 py-1.5 rounded border border-gray-200 text-sm focus:border-blue-400 outline-none" />
                  <span class="text-xs text-gray-400 font-medium">{{ PRODUCT_UNIT[item.product] ?? '' }}</span>
                </div>
              </td>
              <td class="px-5 py-3.5">
                <button
                  @click="removeOrderItem(order, item)"
                  :disabled="order.items.length <= 1"
                  class="px-3 py-1.5 text-xs font-semibold rounded border border-red-200 text-red-500 bg-red-50 hover:bg-red-500 hover:text-white hover:cursor-pointer transition-colors disabled:opacity-30 disabled:cursor-not-allowed">
                  삭제
                </button>
              </td>
            </tr>
            <tr v-if="addItemForm?.orderId === order.id" class="bg-blue-50/50 border-t border-blue-100">
              <td class="px-5 py-3" colspan="2">
                <select v-model="addItemForm.product"
                  class="w-full px-2 py-1.5 rounded-lg border border-blue-200 text-sm outline-none focus:border-blue-400 bg-white">
                  <option value="">품목 선택</option>
                  <option v-for="p in availableProducts(order)" :key="p" :value="p">{{ p }}</option>
                </select>
              </td>
              <td class="px-5 py-3 text-xs text-gray-400">—</td>
              <td class="px-5 py-3 text-xs text-gray-400">—</td>
              <td class="px-5 py-3">
                <input v-model.number="addItemForm.qty" type="number" min="1" placeholder="수량"
                  class="w-24 px-2 py-1.5 rounded border border-blue-200 text-sm outline-none focus:border-blue-400" />
              </td>
              <td class="px-5 py-3">
                <div class="flex gap-1.5">
                  <button @click="confirmAddItem(order)"
                    class="px-3 py-1.5 text-xs font-semibold rounded border border-blue-300 text-blue-600 bg-white hover:bg-blue-500 hover:text-white hover:cursor-pointer transition-colors">
                    추가
                  </button>
                  <button @click="addItemForm = null"
                    class="px-3 py-1.5 text-xs font-semibold rounded border border-gray-200 text-gray-500 bg-white hover:bg-gray-100 hover:cursor-pointer transition-colors">
                    취소
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="pendingOrders.length === 0" class="bg-white border border-gray-200 py-12 text-center text-gray-400 rounded-lg">
        <ClipboardList class="w-10 h-10 mx-auto mb-2 opacity-20" />
        <p class="text-sm">현재 검토 대기 중인 발주서가 없습니다.</p>
        <p class="text-xs mt-1 text-gray-400">발주가 필요한 경우 본사에 문의하세요.</p>
      </div>
    </div>


    <div v-if="activeTab === 'history'">
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">총 금액</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="h in orderHistory" :key="h.id"
              class="hover:bg-gray-50/50 transition-colors cursor-pointer"
              @click="openHistoryDetail(h)">
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ h.id }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="h.type === '자동' ? 'bg-blue-50 text-blue-600 border border-blue-200' : 'bg-purple-50 text-purple-600 border border-purple-200'">
                  {{ h.type }}
                </span>
              </td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">
                {{ h.items[0].product }}
                <span v-if="h.items.length > 1" class="text-xs text-gray-400 font-normal"> 외 {{ h.items.length - 1 }}건</span>
              </td>
              <td class="px-5 py-3.5 font-semibold text-gray-700">
                {{ h.items.reduce((s, i) => s + (PRODUCT_PRICES[i.product] ?? 0) * i.qty, 0).toLocaleString() }}원
              </td>
              <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ h.date }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded" :class="historyStatusCls(h.status)">
                  {{ h.status }}
                </span>
              </td>
            </tr>
            <tr v-if="orderHistory.length === 0">
              <td colspan="7" class="px-5 py-10 text-center text-sm text-gray-400">발주 이력이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 이력 상세 모달 -->
    <div v-if="showHistoryDetail" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showHistoryDetail = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-md border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <div>
            <h3 class="font-bold text-gray-900">발주 상세</h3>
            <p class="text-xs text-gray-400 font-mono mt-0.5">{{ selectedHistory?.id }}</p>
          </div>
          <button @click="showHistoryDetail = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>
        <div v-if="selectedHistory" class="p-6 space-y-4 text-sm">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-xs text-gray-400 mb-1">유형</p>
              <span class="text-xs font-bold px-2 py-0.5 rounded border"
                :class="selectedHistory.type === '자동' ? 'bg-blue-50 text-blue-600 border-blue-200' : 'bg-purple-50 text-purple-600 border-purple-200'">
                {{ selectedHistory.type }}
              </span>
            </div>
            <div>
              <p class="text-xs text-gray-400 mb-1">상태</p>
              <span class="text-xs font-bold px-2 py-0.5 rounded" :class="historyStatusCls(selectedHistory.status)">
                {{ selectedHistory.status }}
              </span>
            </div>
            <div class="col-span-2">
              <p class="text-xs text-gray-400 mb-1">발주일시</p>
              <p class="text-gray-700 font-mono text-xs">{{ selectedHistory.date }}</p>
            </div>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-2">품목 목록</p>
            <div class="border border-gray-100 rounded-lg overflow-hidden">
              <table class="w-full text-sm">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase">품목명</th>
                    <th class="px-4 py-2.5 text-right text-[10px] font-bold text-gray-400 uppercase">수량</th>
                    <th class="px-4 py-2.5 text-right text-[10px] font-bold text-gray-400 uppercase">단가</th>
                    <th class="px-4 py-2.5 text-right text-[10px] font-bold text-gray-400 uppercase">금액</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                  <tr v-for="item in selectedHistory.items" :key="item.product">
                    <td class="px-4 py-2.5 text-gray-800 font-semibold">{{ item.product }}</td>
                    <td class="px-4 py-2.5 text-right text-gray-600">{{ item.qty.toLocaleString() }}개</td>
                    <td class="px-4 py-2.5 text-right text-xs text-gray-500">{{ (PRODUCT_PRICES[item.product] ?? 0).toLocaleString() }}원</td>
                    <td class="px-4 py-2.5 text-right font-bold text-blue-600">{{ ((PRODUCT_PRICES[item.product] ?? 0) * item.qty).toLocaleString() }}원</td>
                  </tr>
                </tbody>
                <tfoot class="bg-gray-50 border-t border-gray-200">
                  <tr>
                    <td colspan="3" class="px-4 py-2.5 text-right text-xs font-bold text-gray-500">합계</td>
                    <td class="px-4 py-2.5 text-right font-black text-blue-600">
                      {{ selectedHistory.items.reduce((s, i) => s + (PRODUCT_PRICES[i.product] ?? 0) * i.qty, 0).toLocaleString() }}원
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end">
          <button @click="showHistoryDetail = false"
            class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer">닫기</button>
        </div>
      </div>
    </div>

    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-[2px]">
      <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden animate-modal-up">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <h2 class="text-lg font-bold text-gray-900">결제 및 승인</h2>
          <button @click="isModalOpen = false" class="text-gray-400 hover:text-gray-600 cursor-pointer"><X class="w-5 h-5"/></button>
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
                <span class="font-medium text-gray-900">₩{{ ((item.adjusted * (PRODUCT_PRICES[item.product] ?? 0))).toLocaleString() }}</span>
              </div>
            </div>
            <div class="flex justify-between items-center pt-3">
              <span class="text-sm font-bold text-gray-900">총 결제 금액</span>
              <span class="text-lg font-black text-blue-600">₩{{ totalPrice.toLocaleString() }}</span>
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
            <CreditCard class="w-4 h-4"/> 결제 및 승인 요청
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { X, ClipboardList, CreditCard } from 'lucide-vue-next'

const PRODUCT_UNIT = {
  '생닭(1kg)':      'kg',
  '튀김유(18L)':    '통',
  '치킨파우더':     'kg',
  '황금올리브소스': '병',
  '양념소스':       '병',
  '간장소스':       '병',
  '치킨박스(중)':   '개',
  '치킨박스(대)':   '개',
  '비닐장갑':       '개',
  '냅킨':           '개',
}

const PRODUCT_STOCK = {
  '생닭(1kg)':      { current: 30,  min: 50   },
  '튀김유(18L)':    { current: 3,   min: 5    },
  '치킨파우더':     { current: 8,   min: 20   },
  '황금올리브소스': { current: 12,  min: 20   },
  '양념소스':       { current: 8,   min: 20   },
  '간장소스':       { current: 15,  min: 20   },
  '치킨박스(중)':   { current: 200, min: 500  },
  '치킨박스(대)':   { current: 120, min: 300  },
  '비닐장갑':       { current: 500, min: 1000 },
  '냅킨':           { current: 800, min: 2000 },
}

const PRODUCT_PRICES = {
  '생닭(1kg)':      5000,
  '튀김유(18L)':    35000,
  '치킨파우더':     8000,
  '황금올리브소스': 12000,
  '양념소스':       10000,
  '간장소스':       10000,
  '치킨박스(중)':   150,
  '치킨박스(대)':   200,
  '비닐장갑':       20,
  '냅킨':           10,
}

const activeTab = ref('pending')
const isModalOpen = ref(false)
const selectedOrder = ref(null)
const approvalMessage = ref('')

const pendingOrders = ref([
  {
    id: 'AUTO-20260413-001', createdAt: '2026-04-13 08:00',
    items: [
      { product: '생닭(1kg)',   current: 30, min: 50, suggested: 100, adjusted: 100 },
      { product: '튀김유(18L)', current: 3,  min: 5,  suggested: 10,  adjusted: 10  },
    ],
  },
  {
    id: 'AUTO-20260413-002', createdAt: '2026-04-13 08:00',
    items: [
      { product: '황금올리브소스', current: 12, min: 20, suggested: 30, adjusted: 30 },
      { product: '양념소스',      current: 8,  min: 20, suggested: 30, adjusted: 30 },
    ],
  },
])

const orderHistory = ref([
  { id: 'ORD-20260413-001', type: '자동', date: '2026-04-13 22:00', status: '배송중',
    items: [{ product: '생닭(1kg)', qty: 100 }, { product: '튀김유(18L)', qty: 10 }] },
  { id: 'ORD-20260412-002', type: '자동', date: '2026-04-12 22:00', status: '배송중',
    items: [{ product: '치킨박스(중)', qty: 500 }, { product: '치킨박스(대)', qty: 200 }] },
  { id: 'ORD-20260411-003', type: '수동', date: '2026-04-11 10:30', status: '입고완료',
    items: [{ product: '황금올리브소스', qty: 30 }, { product: '간장소스', qty: 30 }] },
  { id: 'ORD-20260410-004', type: '자동', date: '2026-04-10 22:00', status: '입고완료',
    items: [{ product: '치킨파우더', qty: 20 }, { product: '양념소스', qty: 30 }] },
  { id: 'ORD-20260409-005', type: '수동', date: '2026-04-09 14:20', status: '입고완료',
    items: [{ product: '비닐장갑', qty: 1000 }, { product: '냅킨', qty: 2000 }, { product: '치킨박스(중)', qty: 300 }] },
])

const showHistoryDetail = ref(false)
const selectedHistory = ref(null)

function openHistoryDetail(h) {
  selectedHistory.value = h
  showHistoryDetail.value = true
}

const HISTORY_STATUS_CLS = {
  '승인대기': 'bg-gray-100 text-gray-500 border border-gray-200',
  '배송중':   'bg-blue-50 text-blue-600 border border-blue-200',
  '입고완료': 'bg-green-50 text-green-700 border border-green-200',
  '거절':     'bg-red-50 text-red-600 border border-red-200',
}
const historyStatusCls = s => HISTORY_STATUS_CLS[s] ?? 'bg-gray-100 text-gray-500 border border-gray-200'

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