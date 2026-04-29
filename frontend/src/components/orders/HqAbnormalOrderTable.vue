<script setup>
import { formatPrice } from './orderUtils'

defineProps({
  orders: { type: Array, required: true },
})

defineEmits(['open-detail', 'approve', 'reject'])
</script>

<template>
  <div class="space-y-4">
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">입점 매장</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주수량</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">평균수량</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">초과율</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">총 금액</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">처리</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="o in orders" :key="o.id"
            class="hover:bg-gray-50/50 transition-colors cursor-pointer"
            :class="o.processed ? 'opacity-50' : ''"
            @click="$emit('open-detail', { id: o.id, type: '이상', store: o.store, status: o.processed ? '처리완료' : 'DANGER', date: o.date, items: o.items, avgQty: o.avgQty, ratio: o.ratio })"
          >
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ o.id }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ o.store }}</td>
            <td class="px-5 py-3.5 font-bold text-red-600">{{ o.qty.toLocaleString() }}</td>
            <td class="px-5 py-3.5 text-gray-500">{{ o.avgQty.toLocaleString() }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-black px-2 py-0.5 rounded bg-red-50 text-red-600 border border-red-200">
                +{{ o.ratio }}%
              </span>
            </td>
            <td class="px-5 py-3.5 font-semibold text-gray-700">{{ formatPrice((o.items ?? []).reduce((s, i) => s + i.unitPrice * i.qty, 0)) }}</td>
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
                <button @click.stop="$emit('approve', o)"
                  class="px-2.5 py-1 bg-[#F37321] text-white text-xs font-semibold hover:bg-[#e0661d] rounded cursor-pointer">승인</button>
                <button @click.stop="$emit('reject', o)"
                  class="px-2.5 py-1 border border-gray-200 text-gray-600 text-xs font-medium hover:bg-gray-50 rounded cursor-pointer">반려</button>
              </div>
              <span v-else class="text-xs text-gray-400 block text-center">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
