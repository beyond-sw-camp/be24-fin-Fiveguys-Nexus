<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const orders = ref([
  { id: 1, product: '한우 등심', qty: 20, unitPrice: 85000 },
  { id: 2, product: '올리브오일', qty: 5, unitPrice: 12000 },
  { id: 3, product: '생크림', qty: 8, unitPrice: 7000 },
])
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
    <div class="px-5 py-4 border-b border-gray-100">
      <h2 class="font-bold text-gray-900">미확정 발주</h2>
    </div>
    <table class="w-full text-sm">
      <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
          <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">제안 수량</th>
          <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">금액</th>
          <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-100">
        <tr v-for="o in orders" :key="o.id"
          class="hover:bg-gray-50/50 cursor-pointer"
          role="button" tabindex="0"
          @click="router.push('/store-order')"
          @keydown.enter="router.push('/store-order')">
          <td class="px-5 py-3.5 font-semibold text-gray-800">{{ o.product }}</td>
          <td class="px-5 py-3.5 text-right text-gray-600">{{ o.qty.toLocaleString() }}개</td>
          <td class="px-5 py-3.5 text-right font-semibold text-gray-700">₩ {{ (o.qty * o.unitPrice).toLocaleString() }}</td>
          <td class="px-5 py-3.5 text-right">
            <span class="text-xs font-bold px-2 py-0.5 rounded bg-blue-50 text-blue-600 border border-blue-200">제안중</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
