<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">배송 관리</h1>
    </div>

    <!-- 검색 & 날짜 필터 -->
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
          @input="onSearchInput"
          class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-[#F37321] focus:border-[#F37321] outline-none block w-full pl-10 p-2.5 transition-colors shadow-sm"
          placeholder="매장명 검색..."
        >
      </div>

      <div class="flex flex-wrap gap-2">
        <div class="relative w-32">
          <select
            v-model="selectedYear"
            @change="fetchDeliveries"
            class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-[#F37321] focus:border-[#F37321] outline-none block w-full p-2.5 cursor-pointer shadow-sm">
            <option value="">연도 전체</option>
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
          </select>
        </div>
        <div class="relative w-28">
          <select
            v-model="selectedMonth"
            @change="fetchDeliveries"
            class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-[#F37321] focus:border-[#F37321] outline-none block w-full p-2.5 cursor-pointer shadow-sm">
            <option value="">월 전체</option>
            <option v-for="m in 12" :key="m" :value="m">{{ m }}월</option>
          </select>
        </div>
        <div class="relative w-28">
          <select
            v-model="selectedDay"
            @change="fetchDeliveries"
            class="bg-white border border-gray-200 text-gray-900 text-sm rounded-lg focus:ring-1 focus:ring-[#F37321] focus:border-[#F37321] outline-none block w-full p-2.5 cursor-pointer shadow-sm">
            <option value="">일 전체</option>
            <option v-for="d in 31" :key="d" :value="d">{{ d }}일</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 상태 필터 버튼 -->
    <div class="flex gap-2 flex-wrap">
      <button
        v-for="f in statusFilters"
        :key="f.value"
        @click="onStatusFilter(f.value)"
        class="px-3.5 py-1.5 text-sm font-semibold border rounded-md transition-colors cursor-pointer"
        :class="filterStatus === f.value
          ? 'bg-[#F37321] text-white border-[#F37321]'
          : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50'">
        {{ f.label }}
        <span class="ml-1 text-xs font-bold opacity-80">({{ countByStatus(f.value) }})</span>
      </button>
    </div>

    <!-- 로딩 -->
    <div v-if="isLoading" class="bg-white border border-gray-200 py-16 text-center rounded-xl shadow-sm">
      <div class="flex flex-col items-center gap-3">
        <svg class="w-6 h-6 text-[#F37321] animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
        </svg>
        <p class="text-gray-400 text-sm">배송 정보를 불러오는 중...</p>
      </div>
    </div>

    <!-- 에러 -->
    <div v-else-if="fetchError" class="bg-red-50 border border-red-200 py-12 text-center rounded-xl">
      <p class="text-red-500 text-sm font-medium">데이터를 불러오는 데 실패했습니다.</p>
      <button
        @click="fetchDeliveries"
        class="mt-3 px-4 py-1.5 text-xs font-semibold bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors cursor-pointer">
        다시 시도
      </button>
    </div>

    <!-- 배송 카드 목록 -->
    <div v-else-if="deliveries.length > 0" class="space-y-3">
      <div
        v-for="d in deliveries"
        :key="d.deliveryIdx"
        @click="d.statusEnum === 'DELAY' ? openModal(d) : null"
        class="bg-white border rounded-xl overflow-hidden shadow-[0_2px_10px_rgba(15,23,42,0.03)] transition-all duration-200"
        :class="[
          d.statusEnum === 'DELAY'
            ? 'border-red-300 cursor-pointer hover:border-red-400 hover:shadow-md'
            : 'border-gray-200'
        ]">

        <!-- 카드 헤더 -->
        <div
          class="px-5 py-3 border-b flex justify-between items-center"
          :class="d.statusEnum === 'DELAY' ? 'bg-red-50/60 border-red-200' : 'bg-gray-50/60 border-gray-100'">
          <div class="flex items-center gap-3">
            <span class="text-xs font-mono text-gray-400"># {{ d.orderIdx }}</span>
            <span class="font-bold text-gray-900 text-sm">{{ d.storeName }}</span>
          </div>
          <span
            class="text-xs font-bold px-2 py-0.5 rounded whitespace-nowrap"
            :class="statusClass(d.statusEnum)">
            {{ d.statusLabel }}
          </span>
        </div>

        <!-- 타임라인 -->
        <div class="px-5 py-4 flex items-start overflow-x-auto hide-scrollbar">
          <div v-for="(step, idx) in d.timeline" :key="idx" class="flex items-center">
            <div class="flex flex-col items-center">
              <div
                class="w-3 h-3 rounded-full border-2 border-white shadow-sm"
                :class="step.done
                  ? 'bg-[#F37321]'
                  : step.current
                    ? 'bg-blue-500 ring-2 ring-blue-100'
                    : 'bg-gray-200'">
              </div>
              <p
                class="text-xs font-medium mt-1.5 text-center w-[150px] truncate"
                :title="step.label"
                :class="step.done
                  ? 'text-gray-800'
                  : step.current
                    ? 'text-blue-600'
                    : 'text-gray-400'">
                {{ step.label }}
              </p>
              <p class="text-[10px] text-gray-400 mt-0.5">{{ step.time }}</p>
            </div>
            <div
              v-if="idx < d.timeline.length - 1"
              class="h-px w-20 sm:w-24 mx-1 sm:mx-2 mt-1 shrink-0"
              :class="step.done ? 'bg-[#F37321]' : 'bg-gray-200'">
            </div>
          </div>
        </div>

        <!-- 지연 사유 영역 -->
        <div v-if="d.statusEnum === 'DELAY'" class="px-5 pb-4 pt-1">
          <div
            v-if="d.delayReason"
            class="bg-red-50/80 text-red-700 text-xs p-3 rounded-md border border-red-100 flex items-start gap-2">
            <svg class="w-4 h-4 shrink-0 mt-0.5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
            </svg>
            <div>
              <span class="font-bold">지연 사유:</span>
              <p class="mt-0.5 whitespace-pre-wrap">{{ d.delayReason }}</p>
            </div>
          </div>
          <div
            v-else
            class="text-[11px] text-red-400 bg-red-50/50 p-2 rounded-md border border-red-100 border-dashed text-center">
            지연 사유를 입력해 주세요.
          </div>
        </div>

      </div>
    </div>

    <!-- 빈 결과 -->
    <div v-else class="bg-white border border-gray-200 py-16 text-center rounded-xl shadow-sm">
      <p class="text-gray-400 text-sm">해당 조건에 일치하는 배송 건이 없습니다.</p>
    </div>

    <!-- 지연 사유 입력 모달 -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/40 backdrop-blur-sm">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden" @click.stop>
        <div class="px-6 py-4 border-b border-gray-100 bg-gray-50/50 flex justify-between items-center">
          <h3 class="text-lg font-bold text-gray-900 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-red-500"></span>
            배송 지연 사유 입력
          </h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors cursor-pointer">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <div class="p-6">
          <div class="mb-5 text-sm text-gray-600 bg-gray-50 p-3.5 rounded-lg border border-gray-100">
            <div class="flex justify-between mb-1">
              <span class="font-medium text-gray-500">발주 번호</span>
              <span class="font-mono text-gray-900"># {{ selectedDelivery?.orderIdx }}</span>
            </div>
            <div class="flex justify-between">
              <span class="font-medium text-gray-500">입점 매장</span>
              <span class="font-bold text-gray-900">{{ selectedDelivery?.storeName }}</span>
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

        <div class="px-6 py-4 bg-gray-50 flex justify-end gap-2 border-t border-gray-100">
          <button
            @click="closeModal"
            :disabled="isSaving"
            class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 text-sm font-semibold transition-colors cursor-pointer disabled:opacity-50">
            취소
          </button>
          <button
            @click="saveDelayReason"
            :disabled="isSaving || !delayReasonText.trim()"
            class="px-5 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 text-sm font-semibold transition-colors shadow-sm cursor-pointer disabled:opacity-50 flex items-center gap-2">
            <svg
              v-if="isSaving"
              class="w-4 h-4 animate-spin"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
            {{ isSaving ? '저장 중...' : '사유 저장' }}
          </button>
        </div>
      </div>
    </div>
    <Toast :show="toast.show" :message="toast.message" :type="toast.type" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAllDeliveries, updateDelayReason } from '@/api/delivery'
import Toast from '@/components/common/Toast.vue'
import { useToast } from '@/composables/useToast'

const { toast, showToast } = useToast()

// 상태
const deliveries   = ref([])
const isLoading    = ref(false)
const fetchError   = ref(false)
const filterStatus = ref('')
const searchQuery  = ref('')
const selectedYear  = ref('')
const selectedMonth = ref('')
const selectedDay   = ref('')

const isModalOpen      = ref(false)
const selectedDelivery = ref(null)
const delayReasonText  = ref('')
const isSaving         = ref(false)

let searchDebounceTimer = null

// DeliveryStatus enum 매핑, 백엔드 DeliveryStatus.name() 기준
const STATUS_LABEL_MAP = {
  READY:       '출고대기',
  START:       '출고중',
  DELIVERYING: '배송중',
  DELIVERED:   '입고완료',
  DELAY:       '지연',
}

// 상태 필터 버튼 목록
const statusFilters = [
  { value: '',           label: '전체'    },
  { value: 'READY',      label: '출고 대기' },
  { value: 'START',      label: '출고 중'  },
  { value: 'DELIVERYING',label: '배송 중'  },
  { value: 'DELIVERED',  label: '입고 완료' },
  { value: 'DELAY',      label: '지연'     },
]

// 연도 옵션 (현재 연도 기준 최근 3년)
const yearOptions = computed(() => {
  const cur = new Date().getFullYear()
  return [cur, cur - 1, cur - 2]
})

// LocalDateTime 문자열 포맷, 백엔드 응답: "2026-04-13T08:30:00" -> "2026-04-13 08:30"
function formatDatetime(val) {
  if (!val) return null
  return String(val).replace('T', ' ').substring(0, 16)
}

// DeliveryDto -> 프론트엔드 포맷 매핑
function mapDelivery(dto) {
  const statusEnum  = dto.deliveryStatus
  const statusLabel = STATUS_LABEL_MAP[statusEnum] || statusEnum

  const departureFormatted      = formatDatetime(dto.departureDate)
  const estimatedFormatted      = formatDatetime(dto.estimatedArrivalAt)
  const deliveredFormatted      = formatDatetime(dto.deliveredDate)

  // 타임라인 스텝별 완료/현재 여부
  // 순서: READY -> START -> DELIVERYING -> DELIVERED
  // DELAY는 DELIVERYING 단계에서 발생한 것으로 처리
  const ORDER = ['READY', 'START', 'DELIVERYING', 'DELIVERED']
  const currentIdx = statusEnum === 'DELAY'
    ? 2  // DELIVERYING 위치에서 지연
    : ORDER.indexOf(statusEnum)

  const timeline = [
    {
      label:   '출고 대기',
      time:    departureFormatted ? `${departureFormatted} 이전` : '-',
      done:    currentIdx > 0,
      current: currentIdx === 0,
    },
    {
      label:   '출고 완료',
      time:    departureFormatted || '-',
      done:    currentIdx > 1,
      current: currentIdx === 1,
    },
    {
      label:   statusEnum === 'DELAY' ? '배송 지연' : '배송 중',
      time:    statusEnum === 'DELAY' ? '지연 발생' : (estimatedFormatted ? `도착 예정 ${estimatedFormatted}` : '-'),
      done:    currentIdx > 2,
      current: currentIdx === 2,
    },
    {
      label:   '입고 완료',
      time:    deliveredFormatted || (estimatedFormatted ? `예정 ${estimatedFormatted}` : '예정'),
      done:    statusEnum === 'DELIVERED',
      current: false,
    },
  ]

  return {
    deliveryIdx:  dto.deliveryIdx,
    orderIdx:     dto.orderIdx,
    storeName:    dto.storeName   || '-',
    statusEnum,
    statusLabel,
    delayReason:  dto.delayReason || '',
    timeline,
  }
}

// API 호출
async function fetchDeliveries() {
  isLoading.value  = true
  fetchError.value = false

  try {
    const params = {}
    if (searchQuery.value.trim()) params.storeName = searchQuery.value.trim()
    if (filterStatus.value)       params.status    = filterStatus.value
    if (selectedYear.value)       params.year      = Number(selectedYear.value)
    if (selectedMonth.value)      params.month     = Number(selectedMonth.value)
    if (selectedDay.value)        params.day       = Number(selectedDay.value)

    const { data } = await getAllDeliveries(params)
    deliveries.value = (data || []).map(mapDelivery)
  } catch (err) {
    console.error('[DeliveryView] 배송 목록 조회 실패:', err)
    fetchError.value = true
  } finally {
    isLoading.value = false
  }
}

// 검색어 디바운스
function onSearchInput() {
  clearTimeout(searchDebounceTimer)
  searchDebounceTimer = setTimeout(fetchDeliveries, 400)
}

// 상태 필터 클릭
function onStatusFilter(value) {
  filterStatus.value = value
  fetchDeliveries()
}

// 상태별 카운트 (현재 로드된 목록 기준)
function countByStatus(value) {
  if (!value) return deliveries.value.length
  return deliveries.value.filter(d => d.statusEnum === value).length
}

// 상태별 배지 스타일
function statusClass(statusEnum) {
  const map = {
    READY:       'bg-gray-100 text-gray-600 border border-gray-200',
    START:       'bg-orange-50 text-orange-600 border border-orange-200',
    DELIVERYING: 'bg-blue-50 text-blue-600 border border-blue-200',
    DELIVERED:   'bg-green-50 text-green-700 border border-green-200',
    DELAY:       'bg-red-50 text-red-600 border border-red-200',
  }
  return map[statusEnum] || 'bg-gray-100 text-gray-500 border border-gray-200'
}

// 모달 제어
function openModal(delivery) {
  selectedDelivery.value = delivery
  delayReasonText.value  = delivery.delayReason || ''
  isModalOpen.value      = true
}

function closeModal() {
  isModalOpen.value      = false
  selectedDelivery.value = null
  delayReasonText.value  = ''
}

async function saveDelayReason() {
  if (!selectedDelivery.value || !delayReasonText.value.trim()) return

  isSaving.value = true
  try {
    await updateDelayReason(
      selectedDelivery.value.deliveryIdx,
      delayReasonText.value.trim()
    )

    // 로컬 상태 즉시 반영 (재조회 없이 UX 개선)
    const target = deliveries.value.find(
      d => d.deliveryIdx === selectedDelivery.value.deliveryIdx
    )
    if (target) {
      target.delayReason = delayReasonText.value.trim()
    }

    closeModal()
  } catch (err) {
    console.error('[DeliveryView] 지연 사유 저장 실패:', err)
    showToast('지연 사유 저장에 실패했습니다. 다시 시도해 주세요.', 'error')
  } finally {
    isSaving.value = false
  }
}

// 초기 로드
onMounted(() => {
  fetchDeliveries()
})
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
