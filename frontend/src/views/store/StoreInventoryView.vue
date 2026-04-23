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
        <div class="bg-white rounded-xl shadow-xl w-full max-w-lg border border-gray-200 max-h-[85vh] flex flex-col overflow-hidden">
          <div class="shrink-0 px-6 py-4 border-b border-gray-200 flex justify-between items-center">
            <div class="min-w-0 pr-2">
              <h2 :id="detailTitleId" class="text-base font-bold text-gray-900 truncate">{{ detailItem.name }}</h2>
              <p class="text-xs font-mono text-gray-500 mt-0.5">{{ detailItem.code }}</p>
              <p class="text-xs text-gray-500 mt-1.5">
                합계 <span class="font-semibold text-gray-800">{{ totalStock(detailItem).toLocaleString() }}</span>
              </p>
            </div>
            <button type="button" class="text-gray-400 hover:text-gray-600 cursor-pointer" aria-label="닫기" @click="closeDetail">✕</button>
          </div>

          <div class="p-6 space-y-4 overflow-y-auto flex-1">
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

          <div class="shrink-0 px-6 py-4 border-t border-gray-100 flex justify-end gap-2">
            <button type="button"
              class="flex-1 py-2.5 rounded-lg text-sm font-semibold text-white bg-blue-500 hover:bg-blue-600 disabled:bg-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed"
              :disabled="!hasDraftLotChanges"
              @click="applyLotAdjustments">
              lot 보정 반영
            </button>
            <button type="button" class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50" @click="closeDetail">
              닫기
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
    code: 'C100', name: '한우 등심', min: 10,
    lots: [
      { id: 'c100-1', expiry: '2026-04-18', qty: 3 },
      { id: 'c100-2', expiry: '2026-04-22', qty: 2 },
    ],
  },
  {
    code: 'C101', name: '한우 안심', min: 8,
    lots: [
      { id: 'c101-1', expiry: '2026-04-19', qty: 4 },
      { id: 'c101-2', expiry: '2026-04-23', qty: 5 },
    ],
  },
  {
    code: 'C200', name: '연어', min: 8,
    lots: [
      { id: 'c200-1', expiry: '2026-04-16', qty: 2 },
      { id: 'c200-2', expiry: '2026-04-20', qty: 1 },
    ],
  },
  {
    code: 'C201', name: '참치', min: 6,
    lots: [{ id: 'c201-1', expiry: '2026-04-17', qty: 4 }],
  },
  {
    code: 'C300', name: '양파', min: 20,
    lots: [
      { id: 'c300-1', expiry: '2026-05-10', qty: 5 },
      { id: 'c300-2', expiry: '2026-05-20', qty: 3 },
    ],
  },
  {
    code: 'C301', name: '마늘', min: 15,
    lots: [{ id: 'c301-1', expiry: '2026-05-15', qty: 12 }],
  },
  {
    code: 'C302', name: '대파', min: 10,
    lots: [{ id: 'c302-1', expiry: '2026-04-25', qty: 8 }],
  },
  {
    code: 'C400', name: '간장', min: 8,
    lots: [{ id: 'c400-1', expiry: '2027-01-01', qty: 3 }],
  },
  {
    code: 'C401', name: '올리브오일', min: 5,
    lots: [{ id: 'c401-1', expiry: '2026-12-01', qty: 2 }],
  },
  {
    code: 'C500', name: '버터', min: 5,
    lots: [{ id: 'c500-1', expiry: '2026-06-30', qty: 2 }],
  },
  {
    code: 'C501', name: '생크림', min: 4,
    lots: [
      { id: 'c501-1', expiry: '2026-04-20', qty: 1 },
    ],
  },
  {
    code: 'C600', name: '콜라(박스)', min: 15,
    lots: [{ id: 'c600-1', expiry: '2026-12-31', qty: 18 }],
  },
  {
    code: 'C601', name: '생수(박스)', min: 20,
    lots: [{ id: 'c601-1', expiry: null, qty: 25 }],
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
