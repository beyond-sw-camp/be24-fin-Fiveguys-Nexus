<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">배송 현황</h1>
      <p class="page-spec-hint">
        <code>DELIVERY_003</code>본인 가맹점 발주에 대한 배송 단계 및 지연 표시.
      </p>
    </div>

    <!-- Delivery list -->
    <div v-if="deliveries.length > 0" class="space-y-3">
      <div v-for="d in deliveries" :key="d.id"
        class="bg-white border overflow-hidden"
        :class="d.status === '지연' ? 'border-red-300' : 'border-gray-200'">
        <div class="px-5 py-3 border-b flex justify-between items-center"
          :class="d.status === '지연' ? 'bg-red-50/60 border-red-200' : 'bg-gray-50/60 border-gray-100'">
          <div class="flex items-center gap-4">
            <span class="text-xs font-mono text-gray-400">{{ d.id }}</span>
            <span class="font-bold text-gray-900 text-sm">{{ d.items.join(' · ') }}</span>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-xs text-gray-500">기사: {{ d.driver }}</span>
            <span class="text-xs font-bold px-2 py-0.5 rounded whitespace-nowrap"
              :class="statusClass(d.status)">{{ d.status }}</span>
          </div>
        </div>
        <div class="px-5 py-4 flex items-center gap-0">
          <div v-for="(step, idx) in d.timeline" :key="idx" class="flex items-center">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 border-2 border-white"
                :class="step.done ? 'bg-blue-500' : step.current ? 'bg-[#F37321]' : 'bg-gray-200'">
              </div>
              <p class="text-xs font-medium mt-1.5 text-center max-w-30"
                :class="step.done ? 'text-gray-800' : step.current ? 'text-[#F37321]' : 'text-gray-400'">
                {{ step.label }}
              </p>
              <p class="text-[10px] text-gray-400 mt-0.5">{{ step.time }}</p>
            </div>
            <div v-if="idx < d.timeline.length - 1"
              class="h-px w-16 mx-2 mb-6"
              :class="step.done ? 'bg-blue-400' : 'bg-gray-200'">
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="bg-white border border-gray-200 py-16 text-center text-gray-400 text-sm">
      현재 진행 중인 배송 건이 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const deliveries = ref([
  {
    id: 'DLV-20260413-001', status: '배송중', driver: '홍길동',
    items: ['우유(1L) 200팩'],
    timeline: [
      { label: '발주 확정',    time: '2026-04-13 08:00', done: true },
      { label: '출고 완료',    time: '2026-04-13 08:30', done: true },
      { label: '배송 중',      time: '2026-04-13 10:15', done: false, current: true },
      { label: '입고 예정',    time: '예정 14:30',        done: false },
    ],
  },
  {
    id: 'DLV-20260413-002', status: '출고대기', driver: '미배정',
    items: ['바닐라 시럽 60병'],
    timeline: [
      { label: '발주 확정',    time: '2026-04-13 11:00', done: true },
      { label: '출고 대기 중', time: '2026-04-13 11:00', done: false, current: true },
      { label: '배송 중',      time: '-',                done: false },
      { label: '입고 예정',    time: '미정',              done: false },
    ],
  },
])

function statusClass(status) {
  const map = {
    '출고대기': 'bg-gray-100 text-gray-600 border border-gray-200',
    '배송중':   'bg-blue-50 text-blue-600 border border-blue-200',
    '입고완료': 'bg-green-50 text-green-700 border border-green-200',
    '지연':     'bg-red-50 text-red-600 border border-red-200',
  }
  return map[status] || 'bg-gray-100 text-gray-500 border border-gray-200'
}
</script>
