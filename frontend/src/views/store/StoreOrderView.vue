<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주서 확인</h1>
      <p class="page-spec-hint">
        <code>ORDER_008~013</code>발주 이력·상세, 제안 승인/거절·수정·정산(결제) 흐름을 지원합니다.
      </p>
    </div>

    <!-- Tabs (underline style) -->
    <div class="flex border-b border-gray-200">
      <button v-for="tab in tabs" :key="tab.id"
        @click="activeTab = tab.id"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors"
        :class="activeTab === tab.id
          ? 'border-blue-500 text-blue-600'
          : 'border-transparent text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
        <span v-if="tab.count > 0"
          class="ml-1.5 text-xs font-bold px-1.5 py-0.5 rounded"
          :class="activeTab === tab.id ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500'">
          {{ tab.count }}
        </span>
      </button>
    </div>

    <!-- 제안 발주서 -->
    <div v-if="activeTab === 'pending'" class="space-y-4">
      <div class="bg-blue-50 px-4 py-3 text-sm text-blue-700 rounded-md">
        재고 데이터를 기반으로 시스템이 자동 산출한 발주서입니다. 수량을 조정하거나 그대로 확정할 수 있습니다.
      </div>

      <div v-for="order in pendingOrders" :key="order.id"
        class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="px-5 py-3 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <div>
            <span class="text-xs font-mono text-gray-400">{{ order.id }}</span>
            <p class="font-bold text-gray-900 mt-0.5 text-sm">{{ order.supplier }} 발주 제안</p>
            <p class="text-xs text-gray-400 mt-0.5">생성일시: {{ order.createdAt }}</p>
          </div>
          <div class="flex gap-2">
            <button @click="confirmOrder(order)"
              class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600">
              전체 확정
            </button>
            <button @click="rejectOrder(order)"
              class="px-4 py-2 border border-gray-200 text-gray-600 text-sm font-semibold hover:bg-gray-50">
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
                  class="w-24 px-2 py-1.5 rounded border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="pendingOrders.length === 0"
        class="bg-white border border-gray-200 py-12 text-center text-gray-400">
        <ClipboardList class="w-10 h-10 mx-auto mb-2 opacity-20" />
        <p class="text-sm">현재 검토 대기 중인 발주서가 없습니다.</p>
      </div>
    </div>

    <!-- 직접 발주 -->
    <div v-if="activeTab === 'manual'" class="max-w-2xl">
      <div class="bg-white border border-gray-200 rounded-lg p-6 space-y-4">
        <h3 class="font-bold text-gray-900 text-sm">직접 발주 요청</h3>
        <div class="space-y-2">
          <div class="flex justify-between items-center">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">발주 품목</label>
            <button @click="manualItems.push({ product: '', qty: 1 })"
              class="text-xs text-blue-500 font-semibold hover:underline flex items-center gap-1">
              <Plus class="w-3 h-3" /> 품목 추가
            </button>
          </div>
          <div v-for="(item, idx) in manualItems" :key="idx" class="flex gap-2 items-center">
            <select v-model="item.product"
              class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none">
              <option value="">품목 선택</option>
              <option>우유(1L)</option>
              <option>두유(1L)</option>
              <option>프리미엄 원두</option>
              <option>에스프레소 원두</option>
              <option>바닐라 시럽</option>
              <option>카라멜 시럽</option>
              <option>종이컵(M)</option>
              <option>종이컵(L)</option>
            </select>
            <input v-model.number="item.qty" type="number" min="1" placeholder="수량"
              class="w-24 px-3 py-2 rounded border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none" />
            <button @click="manualItems.splice(idx, 1)" class="text-gray-300 hover:text-red-500 transition-colors">
              <X class="w-4 h-4" />
            </button>
          </div>
          <div v-if="manualItems.length === 0"
            class="py-6 text-center text-sm text-gray-400 bg-gray-50 border border-gray-100">
            품목 추가 버튼으로 발주 품목을 추가하세요.
          </div>
        </div>
        <textarea v-model="manualNote" rows="2" placeholder="요청 사항 (선택)"
          class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none resize-none"></textarea>
        <button @click="submitManual"
          class="w-full py-3 bg-blue-500 text-white font-bold hover:bg-blue-600 text-sm">
          발주 요청
        </button>
      </div>
    </div>

    <!-- 발주 이력 -->
    <div v-if="activeTab === 'history'">
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">거래처</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="h in orderHistory" :key="h.id" class="hover:bg-gray-50/50 transition-colors">
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ h.id }}</td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ h.product }}</td>
              <td class="px-5 py-3.5 text-gray-600">{{ h.qty }}</td>
              <td class="px-5 py-3.5 text-gray-600">{{ h.supplier }}</td>
              <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ h.date }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="h.status === '입고완료'
                    ? 'bg-green-50 text-green-700 border border-green-200'
                    : 'bg-blue-50 text-blue-600 border border-blue-200'">
                  {{ h.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, X, ClipboardList } from 'lucide-vue-next'

const activeTab = ref('pending')

const pendingOrders = ref([
  {
    id: 'AUTO-20260413-001', supplier: '서울우유', createdAt: '2026-04-13 08:00',
    items: [
      { product: '우유(1L)',  current: 85,  min: 120, suggested: 200, adjusted: 200 },
      { product: '두유(1L)',  current: 55,  min: 60,  suggested: 80,  adjusted: 80  },
    ],
  },
  {
    id: 'AUTO-20260413-002', supplier: '청정원F&B', createdAt: '2026-04-13 08:00',
    items: [
      { product: '바닐라 시럽', current: 5, min: 30, suggested: 60, adjusted: 60 },
    ],
  },
])

const orderHistory = ref([
  { id: 'ORD-20260412-001', product: '종이컵(M)',     qty: 500, supplier: '한국포장',  date: '2026-04-12 22:00', status: '배송중'   },
  { id: 'ORD-20260411-002', product: '프리미엄 원두', qty: 30,  supplier: '동서식품',  date: '2026-04-11 10:30', status: '입고완료' },
  { id: 'ORD-20260410-003', product: '우유(1L)',       qty: 200, supplier: '서울우유',  date: '2026-04-10 22:00', status: '입고완료' },
])

const tabs = computed(() => [
  { id: 'pending', label: '제안 발주서', count: pendingOrders.value.length },
  { id: 'manual',  label: '직접 발주',  count: 0 },
  { id: 'history', label: '발주 이력',  count: 0 },
])

const manualItems = ref([])
const manualNote  = ref('')

function confirmOrder(order) {
  const idx = pendingOrders.value.indexOf(order)
  if (idx > -1) {
    order.items.forEach(item => {
      orderHistory.value.unshift({
        id: order.id.replace('AUTO', 'ORD'),
        product: item.product,
        qty: item.adjusted,
        supplier: order.supplier,
        date: new Date().toISOString().slice(0, 16).replace('T', ' '),
        status: '배송중',
      })
    })
    pendingOrders.value.splice(idx, 1)
    alert('발주서가 확정되었습니다.')
  }
}

function rejectOrder(order) {
  const idx = pendingOrders.value.indexOf(order)
  if (idx > -1 && confirm('발주서를 거절하시겠습니까?')) {
    pendingOrders.value.splice(idx, 1)
  }
}

function submitManual() {
  if (manualItems.value.length === 0) { alert('발주 품목을 추가해주세요.'); return }
  manualItems.value.forEach(item => {
    if (item.product) {
      orderHistory.value.unshift({
        id: `ORD-${Date.now()}`,
        product: item.product,
        qty: item.qty,
        supplier: '본사 요청',
        date: new Date().toISOString().slice(0, 16).replace('T', ' '),
        status: '배송중',
      })
    }
  })
  alert('발주 요청이 접수되었습니다.')
  manualItems.value = []
  manualNote.value = ''
  activeTab.value = 'history'
}
</script>
