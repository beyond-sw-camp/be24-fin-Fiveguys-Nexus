<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">배송 관리</h1>
      <p class="text-sm text-gray-500 mt-1">
        본사→가맹점 배송 목록. 가맹점 이름으로 검색 및 지연 건의 사유를 관리할 수 있습니다.
      </p>
    </div>

    <!-- 1. Search Bar (가맹점 이름 검색) -->
    <div class="flex flex-wrap gap-3 items-center mt-2">
      <div class="relative w-full sm:w-80">
        <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
          <svg class="w-4 h-4 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
        <input
          type="text"
          v-model="searchQuery"
          class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-[#F37321] focus:border-[#F37321] outline-none block w-full pl-10 p-2.5 transition-colors shadow-sm"
          placeholder="가맹점 이름 검색..."
        >
      </div>
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
           @click="d.status === '지연' ? openModal(d) : null"
           class="bg-white border rounded-xl overflow-hidden shadow-[0_2px_10px_rgba(15,23,42,0.03)] transition-all duration-200"
           :class="[
          d.status === '지연'
            ? 'border-red-300 cursor-pointer hover:border-red-400 hover:shadow-md'
            : 'border-gray-200'
        ]">
        <!-- Card header -->
        <div class="px-5 py-3 border-b flex justify-between items-center"
             :class="d.status === '지연' ? 'bg-red-50/60 border-red-200' : 'bg-gray-50/60 border-gray-100'">
          <div class="flex items-center gap-3">
            <span class="text-xs font-mono text-gray-400">{{ d.id }}</span>
            <span class="font-bold text-gray-900 text-sm">{{ d.destination }}</span>
            <span class="text-xs text-gray-500">{{ d.items.join(' · ') }}</span>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-xs text-gray-500 hidden sm:inline-block">거래처: {{ d.supplier }} · 기사: {{ d.driver }}</span>
            <span class="text-xs font-bold px-2 py-0.5 rounded whitespace-nowrap"
                  :class="statusClass(d.status)">{{ d.status }}</span>
          </div>
        </div>

        <!-- Timeline -->
        <div class="px-5 py-4 flex items-start gap-0 overflow-x-auto hide-scrollbar">
          <div v-for="(step, idx) in d.timeline" :key="idx" class="flex items-center">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 rounded-full border-2 border-white shadow-sm"
                   :class="step.done ? 'bg-[#F37321]' : step.current ? 'bg-blue-500 ring-2 ring-blue-100' : 'bg-gray-200'">
              </div>
              <p
                class="text-xs font-medium mt-1.5 text-center w-[160px] truncate"
                :title="step.label"
                :class="step.done ? 'text-gray-800' : step.current ? 'text-blue-600' : 'text-gray-400'">
                {{ step.label }}
              </p>
              <p class="text-[10px] text-gray-400 mt-0.5">{{ step.time }}</p>
            </div>
            <div v-if="idx < d.timeline.length - 1"
                 class="h-px w-20 sm:w-24 mx-1 sm:mx-2 mt-1"
                 :class="step.done ? 'bg-[#F37321]' : 'bg-gray-200'">
            </div>
          </div>
        </div>

        <!-- 지연 사유 표시 영역 -->
        <div v-if="d.status === '지연'" class="px-5 pb-4 pt-1">
          <div v-if="d.delayReason" class="bg-red-50/80 text-red-700 text-xs p-3 rounded-md border border-red-100 flex items-start gap-2">
            <svg class="w-4 h-4 shrink-0 mt-0.5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
            <div>
              <span class="font-bold">지연 사유:</span>
              <p class="mt-0.5 whitespace-pre-wrap">{{ d.delayReason }}</p>
            </div>
          </div>
          <div v-else class="text-[11px] text-red-400 bg-red-50/50 p-2 rounded-md border border-red-100 border-dashed text-center">
            카드를 클릭하여 지연 사유를 입력해 주세요.
          </div>
        </div>

      </div>
    </div>

    <!-- 검색 결과 없음 -->
    <div v-else class="bg-white border border-gray-200 py-16 text-center rounded-xl shadow-sm">
      <p class="text-gray-400 text-sm">해당 조건에 일치하는 배송 건이 없습니다.</p>
    </div>

    <!-- 2. 배송 지연 사유 입력 모달 -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/40 backdrop-blur-sm">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden" @click.stop>
        <!-- 모달 헤더 -->
        <div class="px-6 py-4 border-b border-gray-100 bg-gray-50/50 flex justify-between items-center">
          <h3 class="text-lg font-bold text-gray-900 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-red-500"></span>
            배송 지연 사유 입력
          </h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <!-- 모달 본문 -->
        <div class="p-6">
          <div class="mb-5 text-sm text-gray-600 bg-gray-50 p-3.5 rounded-lg border border-gray-100">
            <div class="flex justify-between mb-1">
              <span class="font-medium text-gray-500">주문번호</span>
              <span class="font-mono text-gray-900">{{ selectedDelivery?.id }}</span>
            </div>
            <div class="flex justify-between">
              <span class="font-medium text-gray-500">가맹점</span>
              <span class="font-bold text-gray-900">{{ selectedDelivery?.destination }}</span>
            </div>
          </div>

          <div class="space-y-2">
            <label class="block text-xs font-bold text-gray-700">상세 사유</label>
            <textarea
              v-model="delayReasonText"
              class="w-full border border-gray-300 rounded-lg p-3 text-sm focus:outline-none focus:border-red-400 focus:ring-4 focus:ring-red-100 min-h-[120px] resize-none transition-all"
              placeholder="배송 지연 사유를 상세히 입력해 주세요&#10;(예: 기상 악화로 인한 배송 지연, 교통 체증 등)"
            ></textarea>
          </div>
        </div>

        <!-- 모달 푸터 -->
        <div class="px-6 py-4 bg-gray-50 flex justify-end gap-2 border-t border-gray-100">
          <button @click="closeModal" class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 text-sm font-semibold transition-colors">
            취소
          </button>
          <button @click="saveDelayReason" class="px-5 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 text-sm font-semibold transition-colors shadow-sm">
            사유 저장
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const filterStatus = ref('전체')
const searchQuery = ref('') // 가맹점 검색어 상태

// 배송 지연 사유 모달 관련 상태
const isModalOpen = ref(false)
const selectedDelivery = ref(null)
const delayReasonText = ref('')

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
    delayReason: '강우로 인한 고속도로 정체로 배송 지연 발생', // 예시 사유
    timeline: [
      { label: '출고 완료 (부산 물류센터)', time: '2026-04-12 20:00', done: true },
      { label: '배송 지연 (교통사고)',       time: '2026-04-13 08:00', done: false, current: true },
      { label: '입고 예정 (재조율 중)',      time: '미정',              done: false },
    ],
  },
])

// 검색어(searchQuery) 및 상태(filterStatus) 기반 필터링
const filteredDeliveries = computed(() => {
  let list = deliveries.value
  if (filterStatus.value !== '전체') {
    list = list.filter((d) => d.status === filterStatus.value)
  }
  if (searchQuery.value.trim() !== '') {
    // 가맹점 이름에 검색어가 포함되어 있는지 확인
    list = list.filter((d) => d.destination.includes(searchQuery.value.trim()))
  }
  return list
})

function countByStatus(status) {
  const base = searchQuery.value.trim() !== ''
    ? deliveries.value.filter((d) => d.destination.includes(searchQuery.value.trim()))
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

// 모달 제어 로직
function openModal(delivery) {
  selectedDelivery.value = delivery
  delayReasonText.value = delivery.delayReason || '' // 기존 사유가 있다면 텍스트 창에 불러오기
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
  selectedDelivery.value = null
  delayReasonText.value = ''
}

function saveDelayReason() {
  if (selectedDelivery.value) {
    const target = deliveries.value.find(d => d.id === selectedDelivery.value.id)
    if (target) {
      target.delayReason = delayReasonText.value // 사유 업데이트
    }
  }
  closeModal() // 완료 후 모달 닫기
}
</script>

<style scoped>
/* 모바일에서 타임라인 가로 스크롤 시 스크롤바 숨김 처리용 */
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
