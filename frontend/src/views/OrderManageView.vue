<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-start gap-4">
      <div class="min-w-0 flex-1">
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주 관리</h1>
      </div>
      <div class="flex gap-2">
        <button @click="showSettings = true"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2">
          <Settings class="w-4 h-4" /> 이상 발주 기준 설정
        </button>
        <button @click="setOrderViewTab('manual')"
          class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded hover:bg-[#e0661d] transition-colors flex items-center gap-2">
          <Plus class="w-4 h-4" /> 수동 발주 생성
        </button>
      </div>
    </div>

    <!-- Tabs (underline style) -->
    <div class="flex border-b border-gray-200">
      <button v-for="tab in tabs" :key="tab.id"
        @click="setOrderViewTab(tab.id)"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors"
        :class="activeTab === tab.id
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
        <span v-if="tab.badge"
          class="ml-1.5 text-xs font-bold px-1.5 py-0.5 rounded"
          :class="activeTab === tab.id ? 'bg-orange-100 text-[#F37321]' : 'bg-gray-100 text-gray-500'">
          {{ tab.badge }}
        </span>
      </button>
    </div>

    <!-- Tab: 자동 발주 제안 (이상 발주 미처리 건 포함, 상단 필터로 구분 조회) -->
    <div v-if="activeTab === 'auto'" class="space-y-4">
      <div class="bg-amber-50 px-4 py-3 flex items-start gap-2.5 rounded-md">
        <AlertTriangle class="w-4 h-4 text-amber-500 shrink-0 mt-0.5" />
        <p class="text-sm text-amber-700">
          재고 기준 이하로 산출된 <strong class="font-semibold">자동 발주 제안</strong>과, 과거 평균 대비 비정상적으로 큰 수량이 감지된
          <strong class="font-semibold">이상 발주</strong>가 함께 표시됩니다. 아래 필터로 유형별로 나누어 볼 수 있습니다.
        </p>
      </div>
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-xs font-semibold text-gray-500">표시</span>
        <button
          v-for="btn in proposalFilterButtons"
          :key="btn.id"
          type="button"
          @click="setProposalListFilter(btn.id)"
          class="text-xs px-3 py-1.5 rounded-md border font-semibold transition-colors"
          :class="proposalListFilter === btn.id
            ? 'bg-[#F37321] text-white border-[#F37321]'
            : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50'"
        >
          {{ btn.label }}
        </button>
        <span class="text-[11px] text-gray-400 ml-auto hidden sm:inline">URL에 <code class="text-gray-500">?tab=auto&proposal=abnormal</code> 로 이상 발주만 열 수 있습니다.</span>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">대상 가맹점</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재재고</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제안수량</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">거래처 / 비고</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">처리</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="row in filteredAutoProposalRows"
              :key="row.key"
              class="hover:bg-gray-50/50 transition-colors"
              :class="row.kind === 'abnormal' ? 'bg-rose-50/35' : ''"
            >
              <td class="px-4 py-3.5">
                <span
                  class="text-[10px] font-bold px-2 py-0.5 rounded border"
                  :class="row.kind === 'auto'
                    ? 'bg-blue-50 text-blue-700 border-blue-200'
                    : 'bg-rose-100 text-rose-800 border-rose-200'"
                >
                  {{ row.kind === 'auto' ? '자동' : '이상' }}
                </span>
              </td>
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ row.id }}</td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ row.store }}</td>
              <td class="px-5 py-3.5 text-gray-700">{{ row.product }}</td>
              <td class="px-5 py-3.5 font-bold" :class="row.kind === 'auto' ? 'text-red-500' : 'text-gray-400 font-medium'">{{ row.currentStock }}</td>
              <td class="px-5 py-3.5 text-gray-500">{{ row.minStock }}</td>
              <td class="px-5 py-3.5 font-bold text-[#F37321]">{{ row.kind === 'abnormal' ? row.suggestedQty.toLocaleString() : row.suggestedQty }}</td>
              <td class="px-5 py-3.5 text-gray-600 text-xs leading-snug">{{ row.supplier }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="statusClass(row.status)">{{ row.status }}</span>
              </td>
              <td class="px-5 py-3.5">
                <div v-if="row.kind === 'auto' && row.status === '제안중'" class="flex justify-center gap-1.5">
                  <button @click="confirmOrder(row.auto)"
                    class="px-2.5 py-1 bg-[#F37321] text-white text-xs font-semibold hover:bg-[#e0661d]">확정</button>
                  <button @click="rejectOrder(row.auto)"
                    class="px-2.5 py-1 border border-gray-200 text-gray-600 text-xs font-medium hover:bg-gray-50">거절</button>
                </div>
                <div v-else-if="row.kind === 'abnormal' && row.status === '검토필요'" class="flex justify-center gap-1.5">
                  <button @click="approveAbnormal(row.abnormal)"
                    class="px-2.5 py-1 bg-[#F37321] text-white text-xs font-semibold hover:bg-[#e0661d] rounded">승인</button>
                  <button @click="rejectAbnormal(row.abnormal)"
                    class="px-2.5 py-1 border border-gray-200 text-gray-600 text-xs font-medium hover:bg-gray-50 rounded">반려</button>
                </div>
                <span v-else class="text-xs text-gray-400 block text-center">—</span>
              </td>
            </tr>
            <tr v-if="filteredAutoProposalRows.length === 0">
              <td colspan="10" class="px-5 py-10 text-center text-sm text-gray-400">
                표시할 항목이 없습니다. 필터를 바꾸거나 이상 발주 탭에서 처리 완료된 건을 확인하세요.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: 수동 발주 -->
    <div v-if="activeTab === 'manual'" class="max-w-2xl space-y-3">
      <div class="bg-white border border-gray-200 rounded-lg p-6 space-y-4">
        <h3 class="font-bold text-gray-900 text-sm">수동 발주 생성</h3>
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-1.5">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">대상 가맹점</label>
            <select v-model="manualForm.store"
              class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
              <option value="">선택</option>
              <option>한화빌딩점</option>
              <option>여의도역점</option>
              <option>판교테크노밸리점</option>
              <option>부산센텀점</option>
            </select>
          </div>
          <div class="space-y-1.5">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">거래처</label>
            <select v-model="manualForm.supplier"
              class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
              <option value="">선택</option>
              <option>서울우유</option>
              <option>동서식품</option>
              <option>한국포장</option>
            </select>
          </div>
        </div>
        <div class="space-y-2">
          <div class="flex justify-between items-center">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">발주 품목</label>
            <button @click="addManualItem" class="text-xs text-[#F37321] font-semibold hover:underline flex items-center gap-1">
              <Plus class="w-3 h-3" /> 품목 추가
            </button>
          </div>
          <div v-for="(item, idx) in manualForm.items" :key="idx" class="flex gap-2 items-center">
            <select v-model="item.product"
              class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
              <option value="">품목 선택</option>
              <option>프리미엄 원두</option>
              <option>에스프레소 원두</option>
              <option>우유(1L)</option>
              <option>두유(1L)</option>
              <option>바닐라 시럽</option>
              <option>카라멜 시럽</option>
              <option>종이컵(M)</option>
              <option>종이컵(L)</option>
            </select>
            <input v-model.number="item.qty" type="number" min="1" placeholder="수량"
              class="w-24 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            <button @click="manualForm.items.splice(idx, 1)" class="text-gray-300 hover:text-red-500 transition-colors shrink-0">
              <X class="w-4 h-4" />
            </button>
          </div>
          <div v-if="manualForm.items.length === 0"
            class="text-sm text-gray-400 text-center py-4 bg-gray-50 border border-gray-100">
            품목 추가 버튼을 눌러 발주 품목을 입력하세요.
          </div>
        </div>
        <div class="space-y-1.5">
          <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">비고</label>
          <textarea v-model="manualForm.note" rows="2" placeholder="특이사항 입력 (선택)"
            class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none resize-none"></textarea>
        </div>
        <button @click="submitManualOrder"
          class="w-full rounded bg-[#F37321] text-white py-3 font-bold hover:bg-[#e0661d] text-sm">
          발주 생성
        </button>
      </div>
    </div>

    <!-- Tab: 이상 발주 (ORDER_007) -->
    <div v-if="activeTab === 'abnormal'" class="space-y-4">
      <div class="bg-red-50 px-4 py-3 flex items-start gap-2.5 rounded-md">
        <AlertTriangle class="w-4 h-4 text-red-500 shrink-0 mt-0.5" />
        <p class="text-sm text-red-700">
          평균 발주량 대비 <strong>{{ abnormalThreshold }}% 이상</strong> 초과한 발주건입니다. 승인 또는 반려 처리가 필요합니다.
        </p>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주수량</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">평균수량</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">초과율</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">처리</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="o in abnormalOrders" :key="o.id"
              class="hover:bg-gray-50/50 transition-colors"
              :class="o.processed ? 'opacity-50' : ''">
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ o.id }}</td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ o.store }}</td>
              <td class="px-5 py-3.5 text-gray-700">{{ o.product }}</td>
              <td class="px-5 py-3.5 font-bold text-red-600">{{ o.qty.toLocaleString() }}</td>
              <td class="px-5 py-3.5 text-gray-500">{{ o.avgQty.toLocaleString() }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-black px-2 py-0.5 rounded bg-red-50 text-red-600 border border-red-200">
                  +{{ o.ratio }}%
                </span>
              </td>
              <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ o.date }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="o.processed
                    ? 'bg-gray-100 text-gray-400 border border-gray-200'
                    : 'bg-red-50 text-red-600 border border-red-200'">
                  {{ o.processed ? '처리완료' : 'DANGER' }}
                </span>
              </td>
              <td class="px-5 py-3.5">
                <div v-if="!o.processed" class="flex justify-center gap-1.5">
                  <button @click="approveAbnormal(o)"
                    class="px-2.5 py-1 bg-[#F37321] text-white text-xs font-semibold hover:bg-[#e0661d] rounded">승인</button>
                  <button @click="rejectAbnormal(o)"
                    class="px-2.5 py-1 border border-gray-200 text-gray-600 text-xs font-medium hover:bg-gray-50 rounded">반려</button>
                </div>
                <span v-else class="text-xs text-gray-400 block text-center">—</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: 발주 이력 (ORDER_006) -->
    <div v-if="activeTab === 'history'" class="space-y-3">
      <div class="bg-white border border-gray-200 rounded-lg p-4 flex flex-wrap gap-3 items-end">
        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 uppercase">유형</label>
          <select v-model="historyFilterType"
            class="px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-[#F37321]">
            <option value="">전체</option>
            <option value="자동">자동</option>
            <option value="수동">수동</option>
          </select>
        </div>
        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 uppercase">기간 시작</label>
          <input v-model="historyDateFrom" type="date" class="px-3 py-2 rounded border border-gray-200 text-sm outline-none" />
        </div>
        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 uppercase">기간 종료</label>
          <input v-model="historyDateTo" type="date" class="px-3 py-2 rounded border border-gray-200 text-sm outline-none" />
        </div>
        <div class="space-y-1 flex-1 min-w-[10rem]">
          <label class="text-[10px] font-bold text-gray-400 uppercase">검색</label>
          <input v-model="historySearch" type="search" placeholder="발주번호·가맹점·품목"
            class="w-full px-3 py-2 rounded border border-gray-200 text-sm outline-none" />
        </div>
        <button type="button" class="text-xs font-semibold text-gray-500 border border-gray-200 px-3 py-2 rounded hover:bg-gray-50"
          @click="historyFilterType = ''; historyDateFrom = ''; historyDateTo = ''; historySearch = ''">
          초기화
        </button>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">거래처</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="h in filteredOrderHistory" :key="h.id" class="hover:bg-gray-50/50 transition-colors">
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ h.id }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="h.type === '자동' ? 'bg-blue-50 text-blue-600 border border-blue-200' : 'bg-purple-50 text-purple-600 border border-purple-200'">
                  {{ h.type }}
                </span>
              </td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ h.store }}</td>
              <td class="px-5 py-3.5 text-gray-700">{{ h.product }}</td>
              <td class="px-5 py-3.5 font-medium text-gray-900">{{ h.qty }}</td>
              <td class="px-5 py-3.5 text-gray-600">{{ h.supplier }}</td>
              <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ h.date }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-bold px-2 py-0.5 rounded"
                  :class="statusClass(h.status)">{{ h.status }}</span>
              </td>
            </tr>
            <tr v-if="filteredOrderHistory.length === 0">
              <td colspan="8" class="px-5 py-10 text-center text-sm text-gray-400">조건에 맞는 발주 이력이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <!-- 이상 발주 기준 설정 Modal (ORDER_008) -->
    <div v-if="showSettings" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showSettings = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-sm border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">이상 발주 기준 설정</h3>
          <button @click="showSettings = false" class="text-gray-400 hover:text-gray-600">✕</button>
        </div>
        <div class="p-6 space-y-5">
          <div class="space-y-3">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">이상 발주 감지 기준</label>
            <div class="space-y-3">
              <div>
                <p class="text-xs text-gray-500 mb-1.5 font-medium">초과 비율 기준</p>
                <div class="flex items-center gap-3">
                  <input v-model.number="abnormalThreshold" type="number" min="100" max="1000" step="10"
                    class="w-28 px-3 py-2 rounded border border-gray-200 text-sm font-bold focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
                  <span class="text-sm text-gray-600">% 초과 시 이상 감지</span>
                </div>
              </div>
              <div>
                <p class="text-xs text-gray-500 mb-1.5 font-medium">분석 기준 기간</p>
                <div class="flex items-center gap-3">
                  <input v-model.number="abnormalMonths" type="number" min="1" max="24"
                    class="w-28 px-3 py-2 rounded border border-gray-200 text-sm font-bold focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
                  <span class="text-sm text-gray-600">개월 과거 데이터 기준</span>
                </div>
              </div>
            </div>
            <p class="text-xs text-gray-400 bg-gray-50 px-3 py-2 rounded border border-gray-100">
              현재 설정: 최근 <strong class="text-gray-700">{{ abnormalMonths }}개월</strong> 평균 대비
              <strong class="text-red-600">{{ abnormalThreshold }}%</strong> 이상 초과 발주 감지
            </p>
          </div>
          <div class="flex gap-3 pt-1">
            <button @click="showSettings = false"
              class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">취소</button>
            <button @click="showSettings = false"
              class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d]">저장</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus, X, AlertTriangle, Settings } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const abnormalCount = computed(() => abnormalOrders.value.filter(o => !o.processed).length)

const autoTabPendingTotal = computed(
  () => autoOrders.value.filter((o) => o.status === '제안중').length + abnormalOrders.value.filter((o) => !o.processed).length,
)

const tabs = computed(() => [
  { id: 'auto',     label: '자동 발주 제안', badge: autoTabPendingTotal.value || null },
  { id: 'manual',   label: '수동 발주' },
  { id: 'history',  label: '발주 이력' },
  { id: 'abnormal', label: '이상 발주', badge: abnormalCount.value || null },
])
const activeTab = ref('auto')

/** 자동 탭 내 표시: 전체 / 자동 제안만 / 이상 발주만 (URL ?tab=auto&proposal= ) */
const proposalListFilter = ref('all')

const autoOrders = ref([
  { id: 'AUTO-20260413-001', store: '여의도역점',      product: '우유(1L)',        currentStock: 85,  minStock: 120, suggestedQty: 200, supplier: '서울우유',  status: '제안중' },
  { id: 'AUTO-20260413-002', store: '판교테크노밸리점', product: '에스프레소 원두', currentStock: 12,  minStock: 80,  suggestedQty: 150, supplier: '동서식품', status: '제안중' },
  { id: 'AUTO-20260413-003', store: '한화빌딩점',      product: '바닐라 시럽',     currentStock: 5,   minStock: 30,  suggestedQty: 60,  supplier: '청정원F&B', status: '제안중' },
  { id: 'AUTO-20260412-004', store: '부산센텀점',      product: '종이컵(M)',       currentStock: 300, minStock: 500, suggestedQty: 1000, supplier: '한국포장', status: '확정'   },
  { id: 'AUTO-20260412-005', store: '한화빌딩점',      product: '두유(1L)',        currentStock: 40,  minStock: 60,  suggestedQty: 100, supplier: '서울우유',  status: '거절'   },
])

const orderHistory = ref([
  { id: 'ORD-20260413-001', type: '자동', store: '부산센텀점',      product: '종이컵(M)',        qty: 1000, supplier: '한국포장', date: '2026-04-12 22:00', status: '배송중'   },
  { id: 'ORD-20260413-002', type: '수동', store: '한화빌딩점',      product: '프리미엄 원두',    qty: 50,   supplier: '동서식품', date: '2026-04-11 10:30', status: '입고완료' },
  { id: 'ORD-20260412-003', type: '자동', store: '여의도역점',      product: '우유(1L)',         qty: 200,  supplier: '서울우유', date: '2026-04-11 22:00', status: '입고완료' },
  { id: 'ORD-20260411-004', type: '수동', store: '판교테크노밸리점', product: '카라멜 시럽',     qty: 30,   supplier: '청정원F&B', date: '2026-04-10 14:15', status: '입고완료' },
])

const historyFilterType = ref('')
const historyDateFrom = ref('')
const historyDateTo = ref('')
const historySearch = ref('')

const filteredOrderHistory = computed(() =>
  orderHistory.value.filter((h) => {
    if (historyFilterType.value && h.type !== historyFilterType.value) return false
    const day = h.date.slice(0, 10)
    if (historyDateFrom.value && day < historyDateFrom.value) return false
    if (historyDateTo.value && day > historyDateTo.value) return false
    const q = historySearch.value.trim()
    if (q && !h.id.includes(q) && !h.store.includes(q) && !h.product.includes(q)) return false
    return true
  }),
)

// 이상 발주 데이터 (ORDER_007)
const abnormalOrders = ref([
  { id: 'ORD-20260414-ABN1', store: '여의도역점',       product: '에스프레소 원두', qty: 850, avgQty: 150, ratio: 567, date: '2026-04-14 11:22', processed: false },
  { id: 'ORD-20260413-ABN2', store: '판교테크노밸리점', product: '우유(1L)',         qty: 1200, avgQty: 300, ratio: 400, date: '2026-04-13 09:15', processed: false },
  { id: 'ORD-20260412-ABN3', store: '부산센텀점',        product: '바닐라 시럽',     qty: 500,  avgQty: 60,  ratio: 833, date: '2026-04-12 16:40', processed: true  },
])

const proposalFilterButtons = [
  { id: 'all', label: '전체' },
  { id: 'auto', label: '자동 제안만' },
  { id: 'abnormal', label: '이상 발주만' },
]

const combinedAutoProposalRows = computed(() => {
  const abnormal = abnormalOrders.value
    .filter((o) => !o.processed)
    .map((o) => ({
      kind: 'abnormal',
      key: `abn-${o.id}`,
      id: o.id,
      store: o.store,
      product: o.product,
      currentStock: '—',
      minStock: '—',
      suggestedQty: o.qty,
      supplier: `과거 평균 ${o.avgQty.toLocaleString()} · +${o.ratio}%`,
      status: '검토필요',
      abnormal: o,
    }))
  const auto = autoOrders.value.map((o) => ({
    kind: 'auto',
    key: `auto-${o.id}`,
    id: o.id,
    store: o.store,
    product: o.product,
    currentStock: o.currentStock,
    minStock: o.minStock,
    suggestedQty: o.suggestedQty,
    supplier: o.supplier,
    status: o.status,
    auto: o,
  }))
  return [...abnormal, ...auto]
})

const filteredAutoProposalRows = computed(() => {
  const rows = combinedAutoProposalRows.value
  if (proposalListFilter.value === 'auto') return rows.filter((r) => r.kind === 'auto')
  if (proposalListFilter.value === 'abnormal') return rows.filter((r) => r.kind === 'abnormal')
  return rows
})

function setProposalListFilter(id) {
  proposalListFilter.value = id
  activeTab.value = 'auto'
  router.replace({ path: '/order', query: { ...route.query, tab: 'auto', proposal: id } })
}

function applyOrderRouteQuery() {
  const q = route.query
  if (q.tab === 'abnormal') {
    activeTab.value = 'abnormal'
    return
  }
  if (q.tab === 'manual') {
    activeTab.value = 'manual'
    return
  }
  if (q.tab === 'history') {
    activeTab.value = 'history'
    return
  }
  if (q.tab === 'auto') {
    activeTab.value = 'auto'
  }
  if (q.proposal === 'all' || q.proposal === 'auto' || q.proposal === 'abnormal') {
    activeTab.value = 'auto'
    proposalListFilter.value = q.proposal
  }
}

onMounted(() => {
  applyOrderRouteQuery()
})

function setOrderViewTab(id) {
  activeTab.value = id
  if (id === 'auto') {
    router.replace({ path: '/order', query: { tab: 'auto', proposal: proposalListFilter.value } })
    return
  }
  if (id === 'abnormal') {
    router.replace({ path: '/order', query: { tab: 'abnormal' } })
    return
  }
  if (id === 'manual') {
    router.replace({ path: '/order', query: { tab: 'manual' } })
    return
  }
  if (id === 'history') {
    router.replace({ path: '/order', query: { tab: 'history' } })
  }
}

// 이상 발주 기준 설정 (ORDER_008)
const showSettings = ref(false)
const abnormalThreshold = ref(200) // 평균 대비 %
const abnormalMonths = ref(3)      // 분석 기준 개월 수

// 이상 발주 처리
function approveAbnormal(o) { o.processed = true; alert(`${o.store} 발주 승인 처리되었습니다.`) }
function rejectAbnormal(o)  { o.processed = true; alert(`${o.store} 발주 반려 처리되었습니다.`) }

const manualForm = ref({ store: '', supplier: '', items: [], note: '' })

function addManualItem() {
  manualForm.value.items.push({ product: '', qty: 1 })
}

function submitManualOrder() {
  if (!manualForm.value.store || !manualForm.value.supplier || manualForm.value.items.length === 0) {
    alert('가맹점, 거래처, 품목을 모두 입력해주세요.')
    return
  }
  const now = new Date().toISOString().slice(0, 16).replace('T', ' ')
  manualForm.value.items.forEach(item => {
    if (item.product) {
      orderHistory.value.unshift({
        id: `ORD-${Date.now()}`,
        type: '수동',
        store: manualForm.value.store,
        product: item.product,
        qty: item.qty,
        supplier: manualForm.value.supplier,
        date: now,
        status: '확정',
      })
    }
  })
  alert('발주가 생성되었습니다.')
  manualForm.value = { store: '', supplier: '', items: [], note: '' }
  setOrderViewTab('history')
}

function confirmOrder(o) {
  o.status = '확정'
  orderHistory.value.unshift({
    id: o.id.replace('AUTO', 'ORD'),
    type: '자동',
    store: o.store,
    product: o.product,
    qty: o.suggestedQty,
    supplier: o.supplier,
    date: new Date().toISOString().slice(0, 16).replace('T', ' '),
    status: '확정',
  })
}

function rejectOrder(o) {
  o.status = '거절'
}

function statusClass(status) {
  const map = {
    '제안중':   'bg-blue-50 text-blue-600 border border-blue-200',
    '검토필요': 'bg-rose-50 text-rose-700 border border-rose-200',
    '확정':     'bg-green-50 text-green-700 border border-green-200',
    '거절':     'bg-gray-100 text-gray-400 border border-gray-200',
    '배송중':   'bg-orange-50 text-orange-600 border border-orange-200',
    '입고완료': 'bg-emerald-50 text-emerald-700 border border-emerald-200',
  }
  return map[status] || 'bg-gray-100 text-gray-500 border border-gray-200'
}
</script>
