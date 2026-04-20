<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">출고 처리</h1>
      <p class="page-spec-hint">협력사 포털 시연 — 출고 대기·완료 및 배송 연계.</p>
    </div>

    <!-- Tabs -->
    <div class="flex gap-1 bg-gray-100 p-1 rounded-xl w-fit">
      <button v-for="tab in tabs" :key="tab.id"
        @click="activeTab = tab.id"
        class="px-4 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === tab.id ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
        <span v-if="tab.count > 0" class="ml-1.5 px-1.5 py-0.5 text-xs rounded-full"
          :class="activeTab === tab.id ? 'bg-emerald-100 text-emerald-600' : 'bg-gray-200 text-gray-500'">
          {{ tab.count }}
        </span>
      </button>
    </div>

    <!-- 출고 대기 -->
    <div v-if="activeTab === 'waiting'" class="space-y-4">
      <div v-for="s in waitingShipments" :key="s.id"
        class="bg-white rounded-xl border border-yellow-200 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <div>
            <span class="text-xs font-mono text-gray-400">{{ s.id }}</span>
            <p class="font-semibold text-gray-900 mt-0.5">{{ s.destination }}</p>
            <p class="text-xs text-gray-500 mt-0.5">발주 확정: {{ s.confirmedAt }}</p>
          </div>
          <button @click="markShipped(s)"
            class="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-bold hover:bg-emerald-600 flex items-center gap-2">
            <PackageCheck class="w-4 h-4" /> 출고 완료 처리
          </button>
        </div>
        <div class="px-6 py-4">
          <div class="flex flex-wrap gap-2">
            <span v-for="item in s.items" :key="item"
              class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm text-gray-700">
              {{ item }}
            </span>
          </div>
        </div>
      </div>
      <div v-if="waitingShipments.length === 0"
        class="bg-white rounded-xl border border-gray-100 shadow-sm py-12 text-center text-gray-400">
        <PackageCheck class="w-10 h-10 mx-auto mb-2 opacity-30" />
        <p class="text-sm">출고 대기 건이 없습니다.</p>
      </div>
    </div>

    <!-- 출고 이력 -->
    <div v-if="activeTab === 'history'">
      <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead class="bg-gray-50 text-gray-500 uppercase text-xs">
            <tr>
              <th class="px-6 py-4">출고번호</th>
              <th class="px-6 py-4">배송지</th>
              <th class="px-6 py-4">출고 품목</th>
              <th class="px-6 py-4">출고 일시</th>
              <th class="px-6 py-4">배송 상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="h in shipHistory" :key="h.id" class="hover:bg-gray-50/50">
              <td class="px-6 py-4 font-mono text-gray-400 text-xs">{{ h.id }}</td>
              <td class="px-6 py-4 font-semibold">{{ h.destination }}</td>
              <td class="px-6 py-4 text-gray-600 text-xs">{{ h.items.join(', ') }}</td>
              <td class="px-6 py-4 text-gray-500 text-xs">{{ h.shippedAt }}</td>
              <td class="px-6 py-4">
                <span class="px-2 py-1 rounded-full text-xs font-bold"
                  :class="h.deliveryStatus === '입고완료' ? 'bg-green-50 text-green-600' : 'bg-blue-50 text-blue-600'">
                  {{ h.deliveryStatus }}
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
import { PackageCheck } from 'lucide-vue-next'

const activeTab = ref('waiting')

const waitingShipments = ref([
  {
    id: 'ORD-20260413-001', destination: '여의도역점',
    confirmedAt: '2026-04-13 08:30',
    items: ['우유(1L) 200팩', '두유(1L) 80팩'],
  },
  {
    id: 'ORD-20260413-002', destination: '부산센텀점',
    confirmedAt: '2026-04-13 09:00',
    items: ['우유(1L) 150팩'],
  },
])

const shipHistory = ref([
  { id: 'ORD-20260412-003', destination: '한화빌딩점',      items: ['두유(1L) 100팩'],  shippedAt: '2026-04-12 15:30', deliveryStatus: '입고완료' },
  { id: 'ORD-20260411-004', destination: '여의도역점',      items: ['우유(1L) 200팩'],  shippedAt: '2026-04-11 14:00', deliveryStatus: '입고완료' },
  { id: 'ORD-20260410-005', destination: '판교테크노밸리점', items: ['두유(1L) 80팩'],   shippedAt: '2026-04-10 09:30', deliveryStatus: '입고완료' },
])

const tabs = computed(() => [
  { id: 'waiting', label: '출고 대기', count: waitingShipments.value.length },
  { id: 'history', label: '출고 이력', count: 0 },
])

function markShipped(shipment) {
  if (confirm(`'${shipment.destination}' 출고를 완료 처리하시겠습니까?`)) {
    const idx = waitingShipments.value.indexOf(shipment)
    if (idx > -1) {
      shipHistory.value.unshift({
        id: shipment.id,
        destination: shipment.destination,
        items: shipment.items,
        shippedAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
        deliveryStatus: '배송중',
      })
      waitingShipments.value.splice(idx, 1)
      alert('출고가 완료 처리되었습니다.')
    }
  }
}
</script>
