<template>
  <div class="space-y-4">
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주번호</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">대상 매장</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주일시</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">총 수량</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">총 금액</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="o in orders"
            :key="o.id"
            class="hover:bg-gray-50/50 transition-colors cursor-pointer"
            @click="$emit('open-detail', { id: o.id, type: '자동', store: o.store, status: o.status, date: o.date, items: o.items })"
          >
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ o.id }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ o.store }}</td>
            <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ o.date ?? '-' }}</td>
            <td class="px-5 py-3.5 font-bold text-[#F37321]">{{ (o.items ?? []).reduce((s, i) => s + i.qty, 0).toLocaleString() }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-700">{{ formatPrice((o.items ?? []).reduce((s, i) => s + i.unitPrice * i.qty, 0)) }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded" :class="statusClass(o.status)">{{ o.status }}</span>
            </td>
          </tr>
          <tr v-if="orders.length === 0">
            <td colspan="6" class="px-5 py-10 text-center text-sm text-gray-400">자동 발주 제안이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { statusClass, formatPrice } from './orderUtils'

defineProps({
  orders: { type: Array, required: true },
})

defineEmits(['open-detail'])
</script>