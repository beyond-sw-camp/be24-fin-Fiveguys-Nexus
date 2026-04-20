<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">본사 재고 현황</h1>
        <p class="text-xs text-gray-500 mt-1">물류센터·본사 보관 재고 기준입니다.</p>
        <p class="page-spec-hint mt-1">
          <code>STOCK_001</code>본사 소유 재고, 창고 단위, 최소재고·유통기한·상태(정상·임박·부족), 유통기한 순 정렬.
        </p>
      </div>
      <div class="flex gap-2">
        <RouterLink to="/inventory/history"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2">
          <History class="w-4 h-4" /> 입출고 이력
        </RouterLink>
        <button class="px-4 py-2 border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">재고 동기화</button>
      </div>
    </div>

    <div class="flex gap-3 items-center flex-wrap">
      <select v-model="filterWarehouse"
        class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white">
        <option value="">전체 창고</option>
        <option>수도권 물류센터</option>
        <option>부산 RDC</option>
        <option>본사 직영 창고</option>
      </select>
      <div class="flex gap-1.5">
        <button v-for="f in statusFilters" :key="f.value"
          type="button"
          @click="filterStatus = f.value"
          class="px-3 py-2 text-sm font-semibold border transition-colors"
          :class="filterStatus === f.value
            ? 'bg-[#F37321] text-white border-[#F37321]'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
          {{ f.label }}
        </button>
      </div>
    </div>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">창고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유통기한</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in filteredItems" :key="item.code + item.warehouse" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ item.warehouse }}</td>
            <td class="px-5 py-3.5 font-bold"
              :class="item.stock < item.safe ? 'text-red-600' : 'text-gray-900'">
              {{ item.stock.toLocaleString() }}
            </td>
            <td class="px-5 py-3.5 text-gray-500">{{ item.safe.toLocaleString() }}</td>
            <td class="px-5 py-3.5 text-xs font-mono"
              :class="isExpiringSoon(item.expiry) ? 'text-orange-500 font-semibold' : 'text-gray-400'">
              {{ item.expiry || '-' }}
            </td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="getStatusClass(item)">
                {{ getStatus(item) }}
              </span>
            </td>
          </tr>
          <tr v-if="filteredItems.length === 0">
            <td colspan="7" class="px-5 py-12 text-center text-gray-400 text-sm">조회된 재고가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { History } from 'lucide-vue-next'

const filterWarehouse = ref('')
const filterStatus = ref('all')

const statusFilters = [
  { value: 'all',      label: '전체' },
  { value: 'danger',   label: '재고부족' },
  { value: 'expiring', label: '유통기한 임박' },
  { value: 'normal',   label: '정상' },
]

const items = ref([
  { code: 'P100', name: '프리미엄 원두',   warehouse: '수도권 물류센터', stock: 4200, safe: 2000, expiry: '2026-06-01' },
  { code: 'P101', name: '에스프레소 원두', warehouse: '수도권 물류센터', stock: 1800, safe: 1500, expiry: '2026-05-20' },
  { code: 'P200', name: '우유(1L)',         warehouse: '수도권 물류센터', stock: 9600, safe: 8000, expiry: '2026-04-19' },
  { code: 'P200', name: '우유(1L)',         warehouse: '부산 RDC',       stock: 3200, safe: 2500, expiry: '2026-04-21' },
  { code: 'P300', name: '바닐라 시럽',     warehouse: '본사 직영 창고', stock: 880,  safe: 400,  expiry: '2026-08-10' },
  { code: 'P400', name: '종이컵(M)',        warehouse: '수도권 물류센터', stock: 45000, safe: 30000, expiry: null },
  { code: 'P401', name: '종이컵(L)',        warehouse: '부산 RDC',       stock: 12000, safe: 8000, expiry: null },
])

function sortByExpiryAsc(list) {
  return [...list].sort((a, b) => {
    if (!a.expiry && !b.expiry) return 0
    if (!a.expiry) return 1
    if (!b.expiry) return -1
    return new Date(a.expiry) - new Date(b.expiry)
  })
}

/** STOCK_001: 유통기한 순 정렬 */
const filteredItems = computed(() => {
  const rows = items.value.filter((item) => {
    if (filterWarehouse.value && item.warehouse !== filterWarehouse.value) return false
    if (filterStatus.value === 'danger' && item.stock >= item.safe) return false
    if (filterStatus.value === 'normal' && (item.stock < item.safe || isExpiringSoon(item.expiry))) return false
    if (filterStatus.value === 'expiring' && !isExpiringSoon(item.expiry)) return false
    return true
  })
  return sortByExpiryAsc(rows)
})

function isExpiringSoon(expiry) {
  if (!expiry) return false
  const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
  return diff >= 0 && diff <= 7
}

function getStatus(item) {
  if (item.stock < item.safe) return '재고부족'
  if (isExpiringSoon(item.expiry)) return '유통기한 임박'
  return '정상'
}

function getStatusClass(item) {
  if (item.stock < item.safe) return 'bg-red-50 text-red-600 border border-red-200'
  if (isExpiringSoon(item.expiry)) return 'bg-orange-50 text-orange-500 border border-orange-200'
  return 'bg-blue-50 text-blue-600 border border-blue-200'
}
</script>
