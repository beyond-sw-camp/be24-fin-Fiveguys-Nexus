<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">배송 관리</h1>
      <p class="page-spec-hint">
        <code>DELIVERY_001~002</code>본사→가맹점 배송 목록, 가맹점별 필터. 단계: 상품출고대기 → 출고완료 → 배송중 → 배송완료, 지연 시 배송 지연 표시.
      </p>
    </div>

    <div class="flex flex-wrap gap-3 items-center">
      <label class="text-xs font-bold text-gray-400 uppercase tracking-wider shrink-0">가맹점</label>
      <select
        v-model="filterDestination"
        class="px-3 py-2 rounded border border-gray-200 text-sm bg-white focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none min-w-[11rem]"
      >
        <option value="">전체 가맹점</option>
        <option v-for="dest in destinationOptions" :key="dest" :value="dest">{{ dest }}</option>
      </select>
    </div>

    <!-- Status filter -->
    <div class="flex gap-2 flex-wrap">
      <button v-for="f in statusFilters" :key="f.value"
        @click="filterStatus = f.value"
        class="px-3.5 py-1.5 text-sm font-semibold border rounded-md transition-colors"
        :class="filterStatus === f.value
          ? 'bg-[#F37321] text-white border-[#F37321]'
          : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50'">
        {{ f.label }}
        <span class="ml-1 text-xs font-bold opacity-80">({{ countByStatus(f.value) }})</span>
      </button>
    </div>

    <!-- Delivery list (table style) -->
    <div v-if="filteredDeliveries.length > 0" class="space-y-3">
      <div v-for="d in filteredDeliveries" :key="d.id"
        class="bg-white border rounded-xl overflow-hidden shadow-[0_2px_10px_rgba(15,23,42,0.03)]"
        :class="d.status === '지연' ? 'border-red-200' : 'border-gray-200'">
        <!-- Card header -->
        <div class="px-5 py-3 border-b flex justify-between items-center"
          :class="d.status === '지연' ? 'bg-red-50/60 border-red-200' : 'bg-gray-50/60 border-gray-100'">
          <div class="flex items-center gap-3">
            <span class="text-xs font-mono text-gray-400">{{ d.id }}</span>
            <span class="font-bold text-gray-900 text-sm">{{ d.destination }}</span>
            <span class="text-xs text-gray-500">{{ d.items.join(' · ') }}</span>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-xs text-gray-500">거래처: {{ d.supplier }} · 기사: {{ d.driver }}</span>
            <span class="text-xs font-bold px-2 py-0.5 rounded whitespace-nowrap"
              :class="statusClass(d.status)">{{ d.status }}</span>
          </div>
        </div>

        <!-- Timeline -->
        <div class="px-5 py-4 flex items-start gap-0">
          <div v-for="(step, idx) in d.timeline" :key="idx" class="flex items-center">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 rounded-full border-2 border-white shadow-sm"
                :class="step.done ? 'bg-[#F37321]' : step.current ? 'bg-blue-500 ring-2 ring-blue-100' : 'bg-gray-200'">
              </div>
              <p
                class="text-xs font-medium mt-1.5 text-center w-[170px] truncate"
                :title="step.label"
                :class="step.done ? 'text-gray-800' : step.current ? 'text-blue-600' : 'text-gray-400'">
                {{ step.label }}
              </p>
              <p class="text-[10px] text-gray-400 mt-0.5">{{ step.time }}</p>
            </div>
            <div v-if="idx < d.timeline.length - 1"
              class="h-px w-24 mx-2 mt-1"
              :class="step.done ? 'bg-[#F37321]' : 'bg-gray-200'">
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="bg-white border border-gray-200 py-16 text-center text-gray-400 text-sm">
      해당 상태의 배송 건이 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const filterStatus = ref('전체')
const filterDestination = ref('')

const destinationOptions = computed(() => {
  const set = new Set(deliveries.value.map((d) => d.destination))
  return [...set].sort()
})

const statusFilters = [
  { value: '전체',    label: '전체' },
  { value: '출고대기', label: '출고 대기' },
  { value: '배송중',  label: '배송 중'  },
  { value: '입고완료', label: '입고 완료' },
  { value: '지연',    label: '지연'     },
]

const deliveries = ref([
  {
    id: 'DLV-20260413-001', status: '배송중',
    destination: '여의도역점', supplier: '서울우유', driver: '홍길동',
    items: ['우유(1L) 200팩'],
    timeline: [
      { label: '출고 완료 (용인 중앙물류센터)', time: '2026-04-13 08:30', done: true },
      { label: '배송 중',                       time: '2026-04-13 10:15', done: false, current: true },
      { label: '입고 예정 (14:30)',              time: '예정',             done: false },
    ],
  },
  {
    id: 'DLV-20260413-002', status: '배송중',
    destination: '판교테크노밸리점', supplier: '동서식품', driver: '이민수',
    items: ['에스프레소 원두 150kg'],
    timeline: [
      { label: '출고 완료 (성남 물류센터)', time: '2026-04-13 09:00', done: true },
      { label: '배송 중',                  time: '2026-04-13 11:00', done: false, current: true },
      { label: '입고 예정 (15:00)',         time: '예정',             done: false },
    ],
  },
  {
    id: 'DLV-20260413-003', status: '출고대기',
    destination: '한화빌딩점', supplier: '청정원F&B', driver: '미배정',
    items: ['바닐라 시럽 60병'],
    timeline: [
      { label: '발주 확정',    time: '2026-04-13 11:00', done: true },
      { label: '출고 대기 중', time: '2026-04-13 11:00', done: false, current: true },
      { label: '입고 예정',    time: '미정',              done: false },
    ],
  },
  {
    id: 'DLV-20260413-004', status: '입고완료',
    destination: '한화빌딩점', supplier: '동서식품', driver: '박상현',
    items: ['프리미엄 원두 50kg'],
    timeline: [
      { label: '출고 완료', time: '2026-04-12 16:00', done: true },
      { label: '배송 중',   time: '2026-04-12 17:30', done: true },
      { label: '입고 완료', time: '2026-04-12 19:45', done: true },
    ],
  },
  {
    id: 'DLV-20260413-005', status: '지연',
    destination: '부산센텀점', supplier: '한국포장', driver: '최동욱',
    items: ['종이컵(M) 1000개'],
    timeline: [
      { label: '출고 완료 (부산 물류센터)', time: '2026-04-12 20:00', done: true },
      { label: '배송 지연 (교통사고)',       time: '2026-04-13 08:00', done: false, current: true },
      { label: '입고 예정 (재조율 중)',      time: '미정',              done: false },
    ],
  },
  {
    id: 'DLV-20260413-006', status: '출고대기',
    destination: '부산센텀점', supplier: '서울우유', driver: '미배정',
    items: ['두유(1L) 100팩', '우유(1L) 150팩'],
    timeline: [
      { label: '발주 확정',    time: '2026-04-13 13:00', done: true },
      { label: '출고 대기 중', time: '2026-04-13 13:00', done: false, current: true },
      { label: '입고 예정',    time: '미정',              done: false },
    ],
  },
])

const filteredDeliveries = computed(() => {
  let list = deliveries.value
  if (filterStatus.value !== '전체') {
    list = list.filter((d) => d.status === filterStatus.value)
  }
  if (filterDestination.value) {
    list = list.filter((d) => d.destination === filterDestination.value)
  }
  return list
})

function countByStatus(status) {
  const base = filterDestination.value
    ? deliveries.value.filter((d) => d.destination === filterDestination.value)
    : deliveries.value
  if (status === '전체') return base.length
  return base.filter((d) => d.status === status).length
}

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
