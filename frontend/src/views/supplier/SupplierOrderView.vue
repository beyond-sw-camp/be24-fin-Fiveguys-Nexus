<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">주문 확인</h1>
      <p class="page-spec-hint">협력사 포털 시연 — 본사·가맹점 발주 수신 및 상태 처리.</p>
    </div>

    <!-- Status filter -->
    <div class="flex gap-2">
      <button v-for="f in statusFilters" :key="f"
        @click="filterStatus = f"
        class="px-3 py-2 rounded-lg text-sm font-medium border transition-all"
        :class="filterStatus === f
          ? 'bg-emerald-500 text-white border-emerald-500'
          : 'bg-white text-gray-600 border-gray-200 hover:border-gray-300'">
        {{ f }}
      </button>
    </div>

    <!-- Order list -->
    <div class="space-y-4">
      <div v-for="order in filteredOrders" :key="order.id"
        class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <div>
            <span class="text-xs font-mono text-gray-400">{{ order.id }}</span>
            <p class="font-semibold text-gray-900 mt-0.5">{{ order.destination }}</p>
            <p class="text-xs text-gray-500 mt-0.5">확정일시: {{ order.confirmedAt }}</p>
          </div>
          <div class="flex items-center gap-3">
            <span class="px-2.5 py-1 rounded-full text-xs font-bold"
              :class="statusClass(order.status)">{{ order.status }}</span>
            <button v-if="order.status === '출고대기'"
              @click="startShipment(order)"
              class="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-bold hover:bg-emerald-600">
              출고 처리
            </button>
          </div>
        </div>
        <table class="w-full text-sm">
          <thead class="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th class="px-6 py-3 text-left">품목명</th>
              <th class="px-6 py-3 text-left">수량</th>
              <th class="px-6 py-3 text-left">단위</th>
              <th class="px-6 py-3 text-left">비고</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="item in order.items" :key="item.product">
              <td class="px-6 py-3 font-semibold">{{ item.product }}</td>
              <td class="px-6 py-3 font-bold text-emerald-600">{{ item.qty }}</td>
              <td class="px-6 py-3 text-gray-500">{{ item.unit }}</td>
              <td class="px-6 py-3 text-gray-400 text-xs">{{ item.note || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="filteredOrders.length === 0"
        class="bg-white rounded-xl border border-gray-100 shadow-sm py-12 text-center text-gray-400">
        <p class="text-sm">해당 상태의 주문이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const statusFilters = ['전체', '출고대기', '출고완료']
const filterStatus  = ref('전체')

const orders = ref([
  {
    id: 'ORD-20260413-001', destination: '여의도역점', confirmedAt: '2026-04-13 08:30', status: '출고대기',
    items: [
      { product: '우유(1L)',  qty: 200, unit: '팩', note: '냉장 보관 필수' },
      { product: '두유(1L)',  qty: 80,  unit: '팩', note: '' },
    ],
  },
  {
    id: 'ORD-20260413-002', destination: '부산센텀점', confirmedAt: '2026-04-13 09:00', status: '출고대기',
    items: [
      { product: '우유(1L)',  qty: 150, unit: '팩', note: '냉장 보관 필수' },
    ],
  },
  {
    id: 'ORD-20260412-003', destination: '한화빌딩점', confirmedAt: '2026-04-12 14:00', status: '출고완료',
    items: [
      { product: '두유(1L)',  qty: 100, unit: '팩', note: '' },
    ],
  },
])

const filteredOrders = computed(() =>
  filterStatus.value === '전체'
    ? orders.value
    : orders.value.filter(o => o.status === filterStatus.value)
)

function startShipment(order) {
  if (confirm(`'${order.destination}' 주문을 출고 처리하시겠습니까?`)) {
    order.status = '출고완료'
    alert('출고 처리가 완료되었습니다.')
    router.push('/supplier-shipment')
  }
}

function statusClass(status) {
  return status === '출고대기' ? 'bg-yellow-50 text-yellow-600' : 'bg-green-50 text-green-600'
}
</script>
