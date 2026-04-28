<script setup>
defineProps({
  items: {
    type: Array,
    required: true,
  },
  totalStock: {
    type: Function,
    required: true,
  },
  getStatus: {
    type: Function,
    required: true,
  },
  getStatusClass: {
    type: Function,
    required: true,
  },
})

defineEmits(['open-detail'])
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
    <table class="w-full text-sm text-left">
      <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품코드</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">창고</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재재고</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-100">
        <tr
          v-for="item in items"
          :key="item.idx"
          role="button"
          tabindex="0"
          class="hover:bg-gray-50/50 transition-colors cursor-pointer"
          @click="$emit('open-detail', item)"
          @keydown.enter.prevent="$emit('open-detail', item)"
          @keydown.space.prevent="$emit('open-detail', item)"
        >
          <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ item.code }}</td>
          <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.name }}</td>
          <td class="px-5 py-3.5 text-gray-600">{{ item.warehouse }}</td>
          <td class="px-5 py-3.5 font-bold" :class="item.status === 'CRITICAL' ? 'text-red-600' : 'text-gray-900'">
            {{ totalStock(item).toLocaleString() }}
          </td>
          <td class="px-5 py-3.5 text-gray-500">-</td>
          <td class="px-5 py-3.5">
            <span class="text-xs font-bold px-2 py-0.5 rounded" :class="getStatusClass(item)">
              {{ getStatus(item) }}
            </span>
          </td>
        </tr>
        <tr v-if="items.length === 0">
          <td colspan="6" class="px-5 py-12 text-center text-gray-400 text-sm">조회된 재고가 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
