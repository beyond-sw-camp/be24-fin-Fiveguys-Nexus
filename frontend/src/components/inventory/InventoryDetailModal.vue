<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    default: null,
  },
  detailTitleId: {
    type: String,
    required: true,
  },
  totalStock: {
    type: Function,
    required: true,
  },
  fifoLots: {
    type: Function,
    required: true,
  },
  isExpiringSoon: {
    type: Function,
    required: true,
  },
  getSubtitle: {
    type: Function,
    required: true,
  },
})

defineEmits(['close'])

const sortedLots = computed(() => {
  if (!props.item) return []
  return props.fifoLots(props.item)
})
</script>

<template>
  <Teleport to="body">
    <div
      v-if="item"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="detailTitleId"
      @click.self="$emit('close')"
    >
      <div class="relative bg-white rounded-xl shadow-xl max-w-lg w-full max-h-[85vh] overflow-hidden flex flex-col border border-gray-200">
        <div class="flex items-start justify-between gap-3 px-6 py-4 border-b border-gray-200">
          <div class="min-w-0">
            <p :id="detailTitleId" class="font-bold text-gray-900 truncate">{{ item.name }}</p>
            <p class="text-xs font-mono text-gray-500 mt-0.5">{{ getSubtitle(item) }}</p>
            <p class="text-xs text-gray-500 mt-2">
              합계 <span class="font-semibold text-gray-800">{{ totalStock(item).toLocaleString() }}</span>
            </p>
          </div>
          <button
            type="button"
            class="text-gray-400 hover:text-gray-600 cursor-pointer"
            aria-label="닫기"
            @click="$emit('close')"
          >
            ✕
          </button>
        </div>
        <div class="overflow-y-auto flex-1">
          <table class="w-full text-sm text-left">
            <thead>
              <tr class="border-b border-gray-100 bg-white sticky top-0">
                <th class="px-5 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">순번</th>
                <th class="px-5 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">유통기한</th>
                <th class="px-5 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-right">수량</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              <tr v-for="(row, idx) in sortedLots" :key="row.id ?? `${row.expiry ?? 'none'}-${row.qty}-${idx}`">
                <td class="px-5 py-3 text-gray-500">{{ idx + 1 }}</td>
                <td class="px-5 py-3 text-xs font-mono" :class="isExpiringSoon(row.expiry) ? 'text-orange-600 font-semibold' : 'text-gray-700'">
                  {{ row.expiry ?? '—' }}
                </td>
                <td class="px-5 py-3 text-right font-semibold text-gray-900">{{ row.qty.toLocaleString() }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end">
          <button
            type="button"
            class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer"
            @click="$emit('close')"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>
