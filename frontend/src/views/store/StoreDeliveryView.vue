<template>
  <div class="p-5 space-y-4 w-full">
    <div class="space-y-4">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">배송 현황</h1>
      </div>

      <div class="space-y-3">
        <div class="relative w-full sm:w-80">
          <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
            <svg class="w-4 h-4 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <input
            type="text"
            v-model="searchQuery"
            class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 outline-none block w-full pl-10 p-2.5 transition-colors shadow-sm"
            placeholder="발주 번호로 검색..."
          >
        </div>

        <div class="flex flex-wrap gap-2">
          <div class="relative w-32">
            <select v-model="selectedYear" class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 outline-none block w-full p-2.5 cursor-pointer shadow-sm">
              <option value="전체">연도 전체</option>
              <option v-for="y in uniqueYears" :key="y" :value="y">{{ y }}년</option>
            </select>
          </div>
          <div class="relative w-28">
            <select v-model="selectedMonth" class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 outline-none block w-full p-2.5 cursor-pointer shadow-sm">
              <option value="전체">월 전체</option>
              <option v-for="m in uniqueMonths" :key="m" :value="m">{{ m }}월</option>
            </select>
          </div>
          <div class="relative w-28">
            <select v-model="selectedDay" class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 outline-none block w-full p-2.5 cursor-pointer shadow-sm">
              <option value="전체">일 전체</option>
              <option v-for="d in uniqueDays" :key="d" :value="d">{{ d }}일</option>
            </select>
          </div>
        </div>
      </div>

      <div class="flex gap-2 flex-wrap pt-1">
        <button v-for="f in filterOptions" :key="f"
                @click="currentFilter = f"
                class="px-3.5 py-1.5 text-sm font-semibold border rounded-md transition-colors cursor-pointer"
                :class="currentFilter === f
            ? 'bg-blue-500 text-white border-blue-500 shadow-sm'
            : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50'">
          {{ f }}
          <span class="ml-1 text-xs font-bold opacity-80">({{ getFilteredCount(f) }})</span>
        </button>
      </div>
    </div>

    <div v-if="filteredDeliveries.length > 0" class="space-y-3">
      <div v-for="d in filteredDeliveries" :key="d.id"
           @click="d.status === '지연' ? openModal(d) : null"
           class="bg-white border overflow-hidden rounded-lg transition-all duration-200"
           :class="[
          d.status === '지연'
            ? 'border-red-300 cursor-pointer hover:border-red-400 hover:shadow-md'
            : 'border-gray-200 shadow-sm'
        ]">
        <div class="px-5 py-3 border-b flex justify-between items-center"
             :class="d.status === '지연' ? 'bg-red-50/60 border-red-200' : 'bg-gray-50/60 border-gray-100'">
          <div class="flex items-center gap-4">
            <div class="flex flex-col">
              <span class="text-[10px] font-bold text-blue-500 mb-0.5">{{ d.date }}</span>
              <span class="text-xs font-mono text-gray-400">{{ d.id }}</span>
            </div>
            <span class="font-bold text-gray-900 text-sm">{{ d.items.join(' · ') }}</span>
          </div>
          <div class="flex items-center gap-4">
            <div class="flex items-center">
              <span v-if="d.driver !== '미배정'" class="text-xs font-medium text-gray-500 px-2 py-1 bg-gray-100 rounded-md">
                기사: {{ d.driver }}
              </span>
              <span v-else class="text-xs text-gray-400">기사: 미배정</span>
            </div>
            <span class="text-xs font-bold px-2 py-0.5 rounded whitespace-nowrap"
                  :class="statusClass(d.status)">{{ d.status }}</span>
          </div>
        </div>

        <div class="px-5 py-4 flex items-center gap-0 overflow-x-auto hide-scrollbar">
          <div v-for="(step, idx) in d.timeline" :key="idx" class="flex items-center">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 border-2 border-white rounded-full shadow-sm"
                   :class="step.done ? 'bg-blue-400' : step.current ? 'bg-blue-600 ring-2 ring-blue-500/20' : 'bg-gray-200'">
              </div>
              <p class="text-xs font-medium mt-1.5 text-center w-20 sm:w-24 truncate"
                 :class="step.done ? 'text-gray-800' : step.current ? 'text-blue-600' : 'text-gray-400'">
                {{ step.label }}
              </p>
              <p class="text-[10px] text-gray-400 mt-0.5">{{ step.time }}</p>
            </div>
            <div v-if="idx < d.timeline.length - 1"
                 class="h-px w-8 sm:w-16 mx-1 sm:mx-2 mb-6"
                 :class="step.done ? 'bg-blue-300' : 'bg-gray-200'">
            </div>
          </div>
        </div>

        <div v-if="d.status === '지연'" class="px-5 py-2.5 bg-red-50/30 border-t border-red-100 flex items-center justify-between">
          <span class="text-xs text-red-500 font-medium flex items-center gap-1.5">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            배송 지연 상세 사유를 확인하려면 클릭하세요.
          </span>
        </div>
      </div>
    </div>

    <div v-else class="bg-white border border-gray-200 py-16 text-center rounded-lg shadow-sm">
      <p class="text-sm text-gray-400 font-medium">조건에 맞는 배송 내역이 없습니다.</p>
    </div>

    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/40 backdrop-blur-sm" @click="closeModal">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-sm overflow-hidden" @click.stop>
        <div class="px-5 py-4 border-b border-red-100 bg-red-50 flex justify-between items-center">
          <h3 class="text-base font-bold text-red-700">배송 지연 안내</h3>
          <button @click="closeModal" class="text-red-400 hover:text-red-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div class="flex flex-col gap-1">
            <span class="text-xs font-semibold text-gray-500">주문 번호</span>
            <span class="text-sm font-mono text-gray-900 bg-gray-50 px-2 py-1.5 rounded border border-gray-100 w-fit">{{ selectedDelivery?.id }}</span>
          </div>
          <div class="flex flex-col gap-1">
            <span class="text-xs font-semibold text-gray-500">배송 물품</span>
            <span class="text-sm text-gray-900 font-medium">{{ selectedDelivery?.items.join(', ') }}</span>
          </div>
          <div class="flex flex-col gap-2 mt-2">
            <span class="text-xs font-semibold text-red-600">지연 사유</span>
            <div class="bg-red-50/50 border border-red-100 text-gray-800 text-sm p-4 rounded-lg leading-relaxed">
              {{ selectedDelivery?.delayReason || '등록된 상세 사유가 없습니다.' }}
            </div>
          </div>
        </div>
        <div class="px-6 py-4 bg-gray-50 flex justify-end border-t border-gray-100">
          <button @click="closeModal" class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600 rounded transition-colors shadow-sm">
            확인
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const isModalOpen = ref(false)
const selectedDelivery = ref(null)

const searchQuery = ref('')
const selectedYear = ref('전체')
const selectedMonth = ref('전체')
const selectedDay = ref('전체')
const currentFilter = ref('전체')
const filterOptions = ['전체', '출고대기', '배송중', '지연', '배송 완료']

const deliveries = ref([
  {
    id: 'DLV-20260413-001', date: '2026-04-13', status: '배송중', driver: '홍길동',
    items: ['염지닭(10호) 100수', '치킨 파우더 40kg'],
    timeline: [
      { label: '발주 확정',    time: '08:00', done: true },
      { label: '출고 완료',    time: '08:30', done: true },
      { label: '배송 중',      time: '10:15', done: false, current: true },
      { label: '배송 완료',    time: '예정 14:30', done: false },
    ],
  },
  {
    id: 'DLV-20260413-003', date: '2026-04-13', status: '지연', driver: '최동욱',
    items: ['치킨 상자(L) 1000개', '콜라(1.5L) 10박스'],
    delayReason: '강우로 인한 고속도로 정체로 배송이 약 2시간 지연되고 있습니다.',
    timeline: [
      { label: '발주 확정',    time: '06:00', done: true },
      { label: '출고 완료',    time: '07:00', done: true },
      { label: '배송 지연',    time: '10:00', done: false, current: true },
      { label: '배송 완료',    time: '미정', done: false },
    ],
  },
  {
    id: 'DLV-20260412-099', date: '2026-04-12', status: '배송 완료', driver: '김배송',
    items: ['치킨무 500팩', '포장 비닐 2000매'],
    timeline: [
      { label: '발주 확정',    time: '07:00', done: true },
      { label: '출고 완료',    time: '07:45', done: true },
      { label: '배송 완료',    time: '11:20', done: true },
    ],
  }
])

const uniqueYears = computed(() => [...new Set(deliveries.value.map(d => d.date.split('-')[0]))].sort().reverse())
const uniqueMonths = computed(() => [...new Set(deliveries.value.map(d => d.date.split('-')[1]))].sort())
const uniqueDays = computed(() => [...new Set(deliveries.value.map(d => d.date.split('-')[2]))].sort())

const filteredDeliveries = computed(() => {
  return deliveries.value
    .filter(d => {
      const [year, month, day] = d.date.split('-')
      const matchesSearch = d.id.includes(searchQuery.value) || d.items.join('').includes(searchQuery.value)
      const matchesYear = selectedYear.value === '전체' || year === selectedYear.value
      const matchesMonth = selectedMonth.value === '전체' || month === selectedMonth.value
      const matchesDay = selectedDay.value === '전체' || day === selectedDay.value
      const matchesStatus = currentFilter.value === '전체' || d.status === currentFilter.value
      return matchesSearch && matchesYear && matchesMonth && matchesDay && matchesStatus
    })
    .sort((a, b) => b.id.localeCompare(a.id))
})

function getFilteredCount(status) {
  return deliveries.value.filter(d => {
    const [year, month, day] = d.date.split('-')
    const matchesYear = selectedYear.value === '전체' || year === selectedYear.value
    const matchesMonth = selectedMonth.value === '전체' || month === selectedMonth.value
    const matchesDay = selectedDay.value === '전체' || day === selectedDay.value
    const matchesStatus = status === '전체' || d.status === status
    return matchesYear && matchesMonth && matchesDay && matchesStatus
  }).length
}

function statusClass(status) {
  const map = {
    '출고대기': 'bg-gray-100 text-gray-600 border border-gray-200',
    '배송중':   'bg-blue-50 text-blue-600 border border-blue-200',
    '배송 완료': 'bg-green-50 text-green-600 border border-green-200',
    '지연':     'bg-red-50 text-red-600 border border-red-200',
  }
  return map[status] || 'bg-gray-100 text-gray-500 border border-gray-200'
}

function openModal(delivery) {
  selectedDelivery.value = delivery
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
  setTimeout(() => { selectedDelivery.value = null }, 200)
}
</script>

<style scoped>
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
