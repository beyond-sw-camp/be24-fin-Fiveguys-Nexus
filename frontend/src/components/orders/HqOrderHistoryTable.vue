<template>
  <div class="space-y-3">
    <div class="bg-white border border-gray-200 rounded-lg px-5 py-4 flex flex-wrap gap-5 items-end">
      <label class="flex flex-col gap-2">
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</span>
        <select v-model="filterType"
          class="w-24 px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-[#F37321]">
          <option value="">전체</option>
          <option value="자동">자동</option>
          <option value="수동">수동</option>
        </select>
      </label>
      <label class="flex flex-col gap-2">
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">기간 시작</span>
        <input v-model="dateFrom" type="date" class="px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-[#F37321]" />
      </label>
      <label class="flex flex-col gap-2">
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">기간 종료</span>
        <input v-model="dateTo" type="date" class="px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-[#F37321]" />
      </label>
      <div class="flex flex-col gap-2 flex-1 min-w-40">
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">검색</span>
        <div class="flex gap-2 items-center">
          <input v-model="search" type="search" placeholder="발주번호·가맹점·품목"
            class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm outline-none focus:border-[#F37321]" />
          <button type="button" class="text-xs font-semibold text-gray-500 border border-gray-200 px-4 py-2 rounded hover:bg-gray-50 shrink-0 cursor-pointer"
            @click="filterType = ''; dateFrom = ''; dateTo = ''; search = ''">
            초기화
          </button>
        </div>
      </div>
    </div>
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">입점 매장</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">총 금액</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="h in filteredHistory" :key="h.id" class="hover:bg-gray-50/50 transition-colors cursor-pointer"
            @click="$emit('open-detail', { id: h.id, type: h.type, store: h.store, status: h.status, date: h.date, items: h.items })">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ h.id }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="h.type === '자동' ? 'bg-blue-50 text-blue-600 border border-blue-200' : 'bg-purple-50 text-purple-600 border border-purple-200'">
                {{ h.type }}
              </span>
            </td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ h.store }}</td>
            <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ h.date }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-700">{{ formatPrice((h.items ?? []).reduce((s, i) => s + i.unitPrice * i.qty, 0)) }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded" :class="statusClass(h.status)">{{ h.status }}</span>
            </td>
          </tr>
          <tr v-if="filteredHistory.length === 0">
            <td colspan="6" class="px-5 py-10 text-center text-sm text-gray-400">조건에 맞는 발주 이력이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { statusClass, formatPrice } from './orderUtils'

const props = defineProps({
  orders: { type: Array, required: true },
})

defineEmits(['open-detail'])

const filterType = ref('')
const dateFrom = ref('')
const dateTo = ref('')
const search = ref('')

const filteredHistory = computed(() =>
  props.orders.filter((h) => {
    if (filterType.value && h.type !== filterType.value) return false
    const day = h.date.slice(0, 10)
    if (dateFrom.value && day < dateFrom.value) return false
    if (dateTo.value && day > dateTo.value) return false
    const q = search.value.trim()
    if (q && !h.id.includes(q) && !h.store.includes(q) && !h.items.some(i => i.product.includes(q))) return false
    return true
  }),
)
</script>