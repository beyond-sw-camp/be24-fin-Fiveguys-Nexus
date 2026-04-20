<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 재고 관리</h1>
        <p class="page-spec-hint">
          <code>STOCK_004·005</code>전산 재고·최소재고·유통기한·보정 수량·보정 버튼, 유통기한 순 목록.
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
        전산 재고와 실제 재고가 다른 경우 <strong>보정 수량</strong>을 입력 후 보정 버튼을 눌러주세요.
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
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소 재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유통기한</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">보정 수량</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">보정</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in inventory" :key="item.code" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="item.stock < item.min ? 'text-red-600' : 'text-gray-900'">
              {{ item.stock }}
            </td>
            <td class="px-5 py-3.5 text-gray-500">{{ item.min }}</td>
            <td class="px-5 py-3.5 text-xs font-mono"
              :class="isExpiringSoon(item.expiry) ? 'text-orange-500 font-semibold' : 'text-gray-400'">
              {{ item.expiry || '-' }}
            </td>
            <td class="px-5 py-3.5">
              <input v-model.number="item.adjustTo" type="number" min="0"
                placeholder="실제 수량"
                class="w-28 px-2 py-1.5 rounded border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none" />
            </td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="getStatusClass(item)">{{ getStatus(item) }}</span>
            </td>
            <td class="px-5 py-3.5 text-center">
              <button @click="adjustStock(item)"
                :disabled="item.adjustTo === null || item.adjustTo === undefined"
                class="px-3 py-1.5 text-xs font-semibold transition-all"
                :class="(item.adjustTo !== null && item.adjustTo !== undefined)
                  ? 'bg-blue-500 text-white hover:bg-blue-600'
                  : 'bg-gray-100 text-gray-400 cursor-not-allowed'">
                보정
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Info } from 'lucide-vue-next'

const inventory = ref([
  { code: 'P100', name: '프리미엄 원두',   stock: 320, min: 100, expiry: '2026-05-30', adjustTo: null },
  { code: 'P200', name: '우유(1L)',         stock: 148, min: 120, expiry: '2026-04-20', adjustTo: null },
  { code: 'P201', name: '두유(1L)',         stock: 55,  min: 60,  expiry: '2026-04-18', adjustTo: null },
  { code: 'P300', name: '바닐라 시럽',     stock: 5,   min: 30,  expiry: '2026-08-01', adjustTo: null },
  { code: 'P301', name: '카라멜 시럽',     stock: 42,  min: 30,  expiry: '2026-09-01', adjustTo: null },
  { code: 'P400', name: '종이컵(M)',        stock: 650, min: 500, expiry: null,          adjustTo: null },
  { code: 'P401', name: '종이컵(L)',        stock: 800, min: 500, expiry: null,          adjustTo: null },
])

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
  }
}
</script>
