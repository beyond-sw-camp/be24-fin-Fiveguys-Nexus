<script setup>
import InventoryMovementTypeBadge from '@/components/inventory/InventoryMovementTypeBadge.vue'

defineProps({
  rows: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

function qtyPrefix(type) {
  if (type === '입고') return '+'
  if (type === '출고') return '-'
  return '±'
}

function qtyClass(type) {
  if (type === '입고') return 'text-slate-700'
  if (type === '출고') return 'text-orange-950'
  return 'text-stone-600'
}
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
    <div class="px-5 py-3 border-b border-gray-100 bg-gray-50/60 text-xs text-gray-500">
      <span v-if="loading">불러오는 중…</span>
      <template v-else>
        총 <span class="font-bold text-gray-900">{{ rows.length }}</span>건
      </template>
    </div>
    <table class="w-full text-sm text-left">
      <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">일시</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유형</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">처리자</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">비고</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-100">
        <tr v-for="h in rows" :key="h.id" class="hover:bg-gray-50/80 transition-colors">
          <td class="px-5 py-3.5 text-xs text-gray-500 tabular-nums">{{ h.datetime }}</td>
          <td class="px-5 py-3.5">
            <InventoryMovementTypeBadge :type="h.type" />
          </td>
          <td class="px-5 py-3.5 font-medium text-gray-900">{{ h.product }}</td>
          <td class="px-5 py-3.5 text-gray-600">{{ h.store }}</td>
          <td class="px-5 py-3.5 tabular-nums font-semibold" :class="qtyClass(h.type)">
            {{ qtyPrefix(h.type) }}{{ h.qty }}
          </td>
          <td class="px-5 py-3.5 text-gray-600 text-xs">{{ h.handler }}</td>
          <td class="px-5 py-3.5 text-gray-400 text-xs">{{ h.note }}</td>
        </tr>
        <tr v-if="rows.length === 0 && !loading">
          <td colspan="7" class="px-5 py-12 text-center text-gray-400 text-sm">조회된 이력이 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
