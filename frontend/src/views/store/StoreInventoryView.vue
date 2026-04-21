<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 재고 관리</h1>
      </div>
      <div class="text-right">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">마지막 POS 동기화</p>
        <p class="text-sm font-semibold text-gray-700 mt-0.5">2026-04-13 13:00</p>
      </div>
    </div>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">전산 재고</th>
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
              :class="totalStock(item) < item.min ? 'text-red-600' : 'text-gray-900'">
              {{ totalStock(item).toLocaleString() }}
            </td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="getStatusClass(item)">{{ getStatus(item) }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Teleport to="body">
      <div
        v-if="detailItem"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/40 backdrop-blur-sm"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="detailTitleId"
        @click.self="closeDetail">
        <div class="bg-white rounded-xl shadow-xl w-full max-w-lg border border-gray-100 max-h-[90vh] flex flex-col overflow-hidden">
          <div class="px-5 py-4 border-b border-gray-100 flex justify-between items-center shrink-0 bg-gray-50/80">
            <div class="min-w-0 pr-2">
              <h2 :id="detailTitleId" class="text-base font-bold text-gray-900 truncate">{{ detailItem.name }}</h2>
              <p class="text-xs font-mono text-gray-500 mt-0.5">{{ detailItem.code }}</p>
              <p class="text-xs text-gray-500 mt-1.5">
                합계 <span class="font-semibold text-gray-800">{{ totalStock(detailItem).toLocaleString() }}</span>
              </p>
            </div>
            <button type="button" class="text-gray-400 hover:text-gray-600 p-1 rounded-md hover:bg-white shrink-0" aria-label="닫기" @click="closeDetail">
              <X class="w-5 h-5" />
            </button>
          </div>

          <div class="p-5 space-y-4 overflow-y-auto flex-1">
            <div>
              <p class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">최소 재고</p>
              <p class="text-sm text-gray-700">{{ detailItem.min.toLocaleString() }}</p>
            </div>

            <div class="border-t border-gray-100 pt-4">
              <p class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">유통기한별 lot</p>
              <div class="rounded-lg border border-gray-200 overflow-hidden">
                <table class="w-full text-sm text-left">
                  <thead>
                    <tr class="bg-gray-50 border-b border-gray-200">
                      <th class="px-3 py-2 text-[10px] font-bold text-gray-400 uppercase">#</th>
                      <th class="px-3 py-2 text-[10px] font-bold text-gray-400 uppercase">유통기한</th>
                      <th class="px-3 py-2 text-[10px] font-bold text-gray-400 uppercase text-right">전산</th>
                      <th class="px-3 py-2 text-[10px] font-bold text-gray-400 uppercase text-right min-w-[7rem]">보정 후</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-100">
                    <tr v-for="(row, idx) in draftLots" :key="row.id">
                      <td class="px-3 py-2.5 text-gray-500">{{ idx + 1 }}</td>
                      <td class="px-3 py-2.5 text-xs font-mono"
                        :class="isExpiringSoon(row.expiry) ? 'text-orange-600 font-semibold' : 'text-gray-700'">
                        {{ row.expiry ?? '—' }}
                      </td>
                      <td class="px-3 py-2.5 text-right font-medium text-gray-800">{{ row.qty.toLocaleString() }}</td>
                      <td class="px-3 py-2.5 text-right">
                        <input
                          v-model.number="row.adjustTo"
                          type="number"
                          min="0"
                          step="1"
                          class="w-full max-w-[6.5rem] ml-auto px-2 py-1.5 rounded-md border border-gray-200 text-sm text-right focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none" />
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div class="flex gap-2 p-5 pt-0 shrink-0">
            <button type="button" class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50" @click="closeDetail">
              닫기
            </button>
            <button type="button"
              class="flex-1 py-2.5 rounded-lg text-sm font-semibold text-white bg-blue-500 hover:bg-blue-600 disabled:bg-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed"
              :disabled="!hasDraftLotChanges"
              @click="applyLotAdjustments">
              lot 보정 반영
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { X } from 'lucide-vue-next'

const detailTitleId = 'store-inv-detail-title'
const detailItem = ref(null)
const draftLots = ref([])

const inventory = ref([
  {
    code: 'C100', name: '닭고기(생닭)', min: 60,
    lots: [
      { id: 'c100-1', expiry: '2026-04-20', qty: 38 },
      { id: 'c100-2', expiry: '2026-04-22', qty: 50 },
    ],
  },
  {
    code: 'C110', name: '순살 정육', min: 40,
    lots: [
      { id: 'c110-1', expiry: '2026-04-18', qty: 12 },
      { id: 'c110-2', expiry: '2026-04-20', qty: 22 },
    ],
  },
  {
    code: 'C200', name: '튀김가루', min: 30,
    lots: [{ id: 'c200-1', expiry: '2026-07-15', qty: 52 }],
  },
  {
    code: 'C210', name: '양념소스', min: 20,
    lots: [{ id: 'c210-1', expiry: '2026-05-02', qty: 17 }],
  },
  {
    code: 'C220', name: '핫양념소스', min: 12,
    lots: [
      { id: 'c220-1', expiry: '2026-05-01', qty: 6 },
      { id: 'c220-2', expiry: '2026-05-08', qty: 8 },
    ],
  },
  {
    code: 'C300', name: '치즈볼(냉동)', min: 70,
    lots: [
      { id: 'c300-1', expiry: '2026-06-05', qty: 40 },
      { id: 'c300-2', expiry: '2026-06-10', qty: 56 },
    ],
  },
  {
    code: 'C310', name: '감자튀김(냉동)', min: 50,
    lots: [{ id: 'c310-1', expiry: '2026-06-18', qty: 74 }],
  },
  {
    code: 'C400', name: '콜라 1.25L', min: 30,
    lots: [{ id: 'c400-1', expiry: '2026-09-01', qty: 41 }],
  },
  {
    code: 'C410', name: '사이다 1.25L', min: 30,
    lots: [{ id: 'c410-1', expiry: '2026-08-24', qty: 28 }],
  },
  {
    code: 'C500', name: '치킨 박스(대)', min: 300,
    lots: [{ id: 'c500-1', expiry: null, qty: 420 }],
  },
  {
    code: 'C510', name: '소스컵/뚜껑 세트', min: 400,
    lots: [
      { id: 'c510-1', expiry: null, qty: 200 },
      { id: 'c510-2', expiry: null, qty: 310 },
    ],
  },
  {
    code: 'C520', name: '비닐봉투(중)', min: 300,
    lots: [{ id: 'c520-1', expiry: null, qty: 390 }],
  },
])

function totalStock(item) {
  return (item.lots ?? []).reduce((s, l) => s + l.qty, 0)
}

function fifoLots(item) {
  const lots = [...(item.lots ?? [])]
  return lots.sort((a, b) => {
    if (!a.expiry && !b.expiry) return 0
    if (!a.expiry) return 1
    if (!b.expiry) return -1
    return new Date(a.expiry) - new Date(b.expiry)
  })
}

function hasExpiringSoonLot(item) {
  return (item.lots ?? []).some((l) => isExpiringSoon(l.expiry))
}

function parseAdjustTo(row) {
  const v = row.adjustTo
  if (v === '' || v === null || v === undefined) return row.qty
  const n = Math.floor(Number(v))
  return Number.isFinite(n) ? Math.max(0, n) : row.qty
}

const hasDraftLotChanges = computed(() => {
  return draftLots.value.some((row) => parseAdjustTo(row) !== row.qty)
})

function openDetail(item) {
  detailItem.value = item
  draftLots.value = fifoLots(item).map((l) => ({
    id: l.id,
    expiry: l.expiry,
    qty: l.qty,
    adjustTo: l.qty,
  }))
}

function closeDetail() {
  detailItem.value = null
  draftLots.value = []
}

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  const stock = totalStock(item)
  if (stock < item.min) return '부족'
  if (hasExpiringSoonLot(item)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  const stock = totalStock(item)
  if (stock < item.min) return 'bg-red-50 text-red-600 border border-red-200'
  if (hasExpiringSoonLot(item)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}

function applyLotAdjustments() {
  const item = detailItem.value
  if (!item) return

  const lines = draftLots.value.map((row) => ({
    row,
    next: parseAdjustTo(row),
    prev: row.qty,
  }))
  const changes = lines.filter((l) => l.next !== l.prev)
  if (!changes.length) {
    alert('변경된 lot 수량이 없습니다.')
    return
  }

  const desc = changes
    .map(({ row, prev, next }) => `${row.expiry ?? '무기한'} ${prev} → ${next}`)
    .join('\n')

  if (!confirm(`다음 lot 보정을 반영할까요?\n\n${desc}`)) return

  for (const { row, next } of changes) {
    const lot = item.lots.find((l) => l.id === row.id)
    if (lot) lot.qty = next
  }

  draftLots.value = fifoLots(item).map((l) => ({
    id: l.id,
    expiry: l.expiry,
    qty: l.qty,
    adjustTo: l.qty,
  }))

  alert('lot 재고 보정이 완료되었습니다.')
  closeDetail()
}
</script>
