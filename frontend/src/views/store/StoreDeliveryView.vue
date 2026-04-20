<template>
  <div class="p-5 space-y-4 w-full">
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">배송 현황</h1>
      <p class="text-sm text-gray-500 mt-1">
        본인 가맹점 발주에 대한 배송 단계 및 지연 여부를 확인합니다.
      </p>
    </div>

    <!-- Delivery list -->
    <div v-if="deliveries.length > 0" class="space-y-3">
      <div v-for="d in deliveries" :key="d.id"
           @click="d.status === '지연' ? openModal(d) : null"
           class="bg-white border overflow-hidden rounded-xl transition-all duration-200"
           :class="[
          d.status === '지연'
            ? 'border-red-300 cursor-pointer hover:border-red-400 hover:shadow-md'
            : 'border-gray-200'
        ]">
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
        <div class="px-5 py-4 flex items-center gap-0 overflow-x-auto hide-scrollbar">
          <div v-for="(step, idx) in d.timeline" :key="idx" class="flex items-center">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 border-2 border-white rounded-full shadow-sm"
                   :class="step.done ? 'bg-blue-500' : step.current ? 'bg-[#F37321] ring-2 ring-[#F37321]/20' : 'bg-gray-200'">
              </div>
              <p class="text-xs font-medium mt-1.5 text-center w-20 sm:w-24 truncate"
                 :class="step.done ? 'text-gray-800' : step.current ? 'text-[#F37321]' : 'text-gray-400'">
                {{ step.label }}
              </p>
              <p class="text-[10px] text-gray-400 mt-0.5">{{ step.time }}</p>
            </div>
            <div v-if="idx < d.timeline.length - 1"
                 class="h-px w-8 sm:w-16 mx-1 sm:mx-2 mb-6"
                 :class="step.done ? 'bg-blue-400' : 'bg-gray-200'">
            </div>
          </div>
        </div>

        <!-- 지연 상태일 때 카드 하단에 클릭 유도 문구 추가 -->
        <div v-if="d.status === '지연'" class="px-5 py-2.5 bg-red-50/30 border-t border-red-100 flex items-center justify-between">
          <span class="text-xs text-red-500 font-medium flex items-center gap-1.5">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            배송이 지연되고 있습니다.
          </span>
        </div>
      </div>
    </div>

    <div v-else class="bg-white border border-gray-200 py-16 text-center text-gray-400 text-sm">
      현재 진행 중인 배송 건이 없습니다.
    </div>

    <!-- 배송 지연 사유 확인 모달 -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/40 backdrop-blur-sm" @click="closeModal">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-sm overflow-hidden" @click.stop>
        <!-- 모달 헤더 -->
        <div class="px-5 py-4 border-b border-red-100 bg-red-50 flex justify-between items-center">
          <h3 class="text-base font-bold text-red-700 flex items-center gap-2">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
            배송 지연 안내
          </h3>
          <button @click="closeModal" class="text-red-400 hover:text-red-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <!-- 모달 본문 -->
        <div class="p-6 space-y-4">
          <div class="flex flex-col gap-1">
            <span class="text-xs font-semibold text-gray-500">주문 번호</span>
            <span class="text-sm font-mono text-gray-900 bg-gray-50 px-2 py-1.5 rounded border border-gray-100 w-fit">{{ selectedDelivery?.id }}</span>
          </div>

          <div class="flex flex-col gap-1">
            <span class="text-xs font-semibold text-gray-500">배송 물품</span>
            <span class="text-sm text-gray-900">{{ selectedDelivery?.items.join(', ') }}</span>
          </div>

          <div class="flex flex-col gap-2 mt-2">
            <span class="text-xs font-semibold text-red-600">지연 사유</span>
            <div class="bg-red-50/50 border border-red-100 text-gray-800 text-sm p-4 rounded-lg leading-relaxed whitespace-pre-wrap">
              {{ selectedDelivery?.delayReason || '등록된 상세 사유가 없습니다. 본사에 문의해 주세요.' }}
            </div>
          </div>
        </div>

        <!-- 모달 푸터 -->
        <div class="px-6 py-4 bg-gray-50 flex justify-end border-t border-gray-100">
          <button @click="closeModal" class="px-5 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 text-sm font-semibold transition-colors shadow-sm">
            확인
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const isModalOpen = ref(false)
const selectedDelivery = ref(null)

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
  // 1. 배송 지연 더미데이터 추가
  {
    id: 'DLV-20260413-003', status: '지연', driver: '최동욱',
    items: ['종이컵(M) 1000개'],
    delayReason: '강우로 인한 고속도로 정체 및 연쇄 추돌 사고 여파로 배송이 2~3시간가량 지연될 예정입니다.\n빠르게 조치하여 배송해 드리겠습니다.',
    timeline: [
      { label: '발주 확정',    time: '2026-04-12 18:00', done: true },
      { label: '출고 완료',    time: '2026-04-12 20:00', done: true },
      { label: '배송 지연',    time: '2026-04-13 08:00', done: false, current: true },
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

// 모달 제어 로직
function openModal(delivery) {
  selectedDelivery.value = delivery
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
  setTimeout(() => {
    selectedDelivery.value = null
  }, 200) // 모달 닫히는 애니메이션 등을 고려한 딜레이
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
