<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 재고 관리</h1>
        <p class="page-spec-hint">
          <code>STOCK_004·005</code>목록은 상품코드·품목명·전산 재고·유통기한·상태만 표시. 행 클릭 시 상세 모달에서 최소 재고·보정 수량·보정 처리.
        </p>
      </div>
      <div class="text-right">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">마지막 POS 동기화</p>
        <p class="text-sm font-semibold text-gray-700 mt-0.5">2026-04-13 13:00</p>
      </div>
    </div>

    <!-- Info banner -->
    <div class="bg-blue-50 px-4 py-3 flex items-start gap-2.5 rounded-md">
      <Info class="w-4 h-4 text-blue-500 shrink-0 mt-0.5" />
      <span class="text-sm text-blue-700">
        행을 클릭하면 상세 창이 열립니다. 전산 재고와 실제 재고가 다를 경우 <strong>보정 수량</strong>을 입력 후 보정을 진행해 주세요.
        이력은 본사 입출고 이력에 자동으로 기록됩니다.
      </span>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">전산 재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유통기한</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="item in inventory"
            :key="item.code"
            role="button"
            tabindex="0"
            @click="openDetail(item)"
            @keydown.enter.prevent="openDetail(item)"
            @keydown.space.prevent="openDetail(item)"
            class="hover:bg-blue-50/40 transition-colors cursor-pointer">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="item.stock < item.min ? 'text-red-600' : 'text-gray-900'">
              {{ item.stock }}
            </td>
            <td class="px-5 py-3.5 text-xs font-mono"
              :class="isExpiringSoon(item.expiry) ? 'text-orange-500 font-semibold' : 'text-gray-400'">
              {{ item.expiry || '-' }}
            </td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="getStatusClass(item)">{{ getStatus(item) }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 상세 모달 -->
    <Teleport to="body">
      <div
        v-if="detailItem"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/40 backdrop-blur-sm"
        @click.self="closeDetail">
        <div class="bg-white rounded-xl shadow-xl w-full max-w-md border border-gray-100 max-h-[90vh] overflow-y-auto">
          <div class="px-5 py-4 border-b border-gray-100 flex justify-between items-center shrink-0">
            <h2 class="text-base font-bold text-gray-900">재고 상세</h2>
            <button type="button" class="text-gray-400 hover:text-gray-600 p-1 rounded-md hover:bg-gray-50" aria-label="닫기" @click="closeDetail">
              <X class="w-5 h-5" />
            </button>
          </div>

          <div class="p-5 space-y-5">
            <dl class="grid grid-cols-[7rem_1fr] gap-x-3 gap-y-2 text-sm">
              <dt class="text-gray-500">상품코드</dt>
              <dd class="font-mono text-gray-800">{{ detailItem.code }}</dd>
              <dt class="text-gray-500">품목명</dt>
              <dd class="font-semibold text-gray-900">{{ detailItem.name }}</dd>
              <dt class="text-gray-500">전산 재고</dt>
              <dd class="font-bold" :class="detailItem.stock < detailItem.min ? 'text-red-600' : 'text-gray-900'">
                {{ detailItem.stock }}
              </dd>
              <dt class="text-gray-500">유통기한</dt>
              <dd class="font-mono text-xs" :class="isExpiringSoon(detailItem.expiry) ? 'text-orange-600 font-semibold' : 'text-gray-600'">
                {{ detailItem.expiry || '-' }}
              </dd>
              <dt class="text-gray-500">상태</dt>
              <dd>
                <span class="text-xs font-bold px-2 py-0.5 rounded inline-block"
                  :class="getStatusClass(detailItem)">{{ getStatus(detailItem) }}</span>
              </dd>
            </dl>

            <div class="border-t border-gray-100 pt-4 space-y-4">
              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">최소 재고</label>
                <p class="text-sm text-gray-700">{{ detailItem.min }} <span class="text-gray-400 font-normal">(본사 기준)</span></p>
              </div>
              <div>
                <label for="adjust-to" class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">보정 수량</label>
                <input
                  id="adjust-to"
                  v-model.number="detailItem.adjustTo"
                  type="number"
                  min="0"
                  placeholder="실제 수량 입력"
                  class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none" />
              </div>
            </div>

            <div class="flex gap-2 pt-1">
              <button type="button" class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50" @click="closeDetail">
                닫기
              </button>
              <button type="button"
                :disabled="detailItem.adjustTo === null || detailItem.adjustTo === undefined"
                class="flex-1 py-2.5 rounded-lg text-sm font-semibold text-white transition-all"
                :class="(detailItem.adjustTo !== null && detailItem.adjustTo !== undefined)
                  ? 'bg-blue-500 hover:bg-blue-600'
                  : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
                @click="adjustStock(detailItem)">
                재고 보정
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Info, X } from 'lucide-vue-next'

const inventory = ref([
  { code: 'C100', name: '닭고기(생닭)',        stock: 88,  min: 60,  expiry: '2026-04-22', adjustTo: null },
  { code: 'C110', name: '순살 정육',           stock: 34,  min: 40,  expiry: '2026-04-20', adjustTo: null },
  { code: 'C200', name: '튀김가루',            stock: 52,  min: 30,  expiry: '2026-07-15', adjustTo: null },
  { code: 'C210', name: '양념소스',            stock: 17,  min: 20,  expiry: '2026-05-02', adjustTo: null },
  { code: 'C220', name: '핫양념소스',          stock: 14,  min: 12,  expiry: '2026-05-08', adjustTo: null },
  { code: 'C300', name: '치즈볼(냉동)',        stock: 96,  min: 70,  expiry: '2026-06-10', adjustTo: null },
  { code: 'C310', name: '감자튀김(냉동)',      stock: 74,  min: 50,  expiry: '2026-06-18', adjustTo: null },
  { code: 'C400', name: '콜라 1.25L',          stock: 41,  min: 30,  expiry: '2026-09-01', adjustTo: null },
  { code: 'C410', name: '사이다 1.25L',        stock: 28,  min: 30,  expiry: '2026-08-24', adjustTo: null },
  { code: 'C500', name: '치킨 박스(대)',       stock: 420, min: 300, expiry: null,          adjustTo: null },
  { code: 'C510', name: '소스컵/뚜껑 세트',    stock: 510, min: 400, expiry: null,          adjustTo: null },
  { code: 'C520', name: '비닐봉투(중)',        stock: 390, min: 300, expiry: null,          adjustTo: null },
])

const detailItem = ref(null)

function openDetail(item) {
  detailItem.value = item
}

function closeDetail() {
  detailItem.value = null
}

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  if (item.stock < item.min)       return '부족'
  if (isExpiringSoon(item.expiry)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.stock < item.min)       return 'bg-red-50 text-red-600 border border-red-200'
  if (isExpiringSoon(item.expiry)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}

function adjustStock(item) {
  if (item.adjustTo === null || item.adjustTo === undefined) return
  const diff = item.adjustTo - item.stock
  const sign = diff > 0 ? '+' : ''
  if (confirm(`'${item.name}' 재고를 ${item.stock} → ${item.adjustTo} (${sign}${diff})로 보정하시겠습니까?`)) {
    item.stock = item.adjustTo
    item.adjustTo = null
    alert('재고 보정이 완료되었습니다.')
    closeDetail()
  }
}
</script>
