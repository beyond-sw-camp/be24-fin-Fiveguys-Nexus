<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const warnings = ref([
  { product: '한우 등심', current: '5kg', min: '10kg', danger: true },
  { product: '버터', current: '2kg', min: '5kg', danger: false },
])
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
    <div class="px-5 py-4 border-b border-gray-100">
      <h2 class="font-bold text-gray-900">재고 위험 품목</h2>
    </div>
    <table class="w-full text-sm">
      <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
          <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재 재고</th>
          <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소 재고</th>
          <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-100">
        <tr v-for="w in warnings" :key="w.product"
          class="hover:bg-gray-50/50 cursor-pointer"
          role="button" tabindex="0"
          @click="router.push('/store-inventory')"
          @keydown.enter="router.push('/store-inventory')">
          <td class="px-5 py-3.5 font-semibold text-gray-800">{{ w.product }}</td>
          <td class="px-5 py-3.5 text-right font-bold text-red-600">{{ w.current }}</td>
          <td class="px-5 py-3.5 text-right text-gray-400">{{ w.min }}</td>
          <td class="px-5 py-3.5 text-right">
            <span class="text-xs font-bold px-2 py-0.5 rounded"
              :class="w.danger ? 'bg-red-50 text-red-600 border border-red-200' : 'bg-orange-50 text-orange-600 border border-orange-200'">
              {{ w.danger ? '위험' : '주의' }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
