<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const deliveries = ref([
  { id: 'ORD-2604-008', items: '한우 등심(kg) 외 1건', status: '배송중' },
  { id: 'ORD-2604-011', items: '올리브오일(L) 외 1건', status: '출고대기' },
  { id: 'ORD-2604-015', items: '버터 외 2건', status: '배송지연' },
  { id: 'ORD-2604-003', items: '생수 외 1건', status: '출고완료' },
])

const DELIVERY_STATUS_CLS = {
  '배송중': 'bg-blue-50 text-blue-600 border-blue-200',
  '출고대기': 'bg-gray-100 text-gray-600 border-gray-200',
  '출고완료': 'bg-green-50 text-green-700 border-green-200',
  '배송완료': 'bg-green-50 text-green-700 border-green-200',
  '배송지연': 'bg-red-50 text-red-600 border-red-200',
}
const deliveryCls = s => DELIVERY_STATUS_CLS[s] ?? 'bg-gray-100 text-gray-500 border-gray-200'
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col">
    <div class="px-5 pt-5 pb-4 border-b border-gray-100">
      <h2 class="font-bold text-gray-900">나의 배송 현황</h2>
    </div>
    <div class="flex-1 divide-y divide-gray-50 overflow-y-auto">
      <div v-for="d in deliveries" :key="d.id"
        class="px-5 py-4 hover:bg-gray-50 transition-colors cursor-pointer"
        role="button" tabindex="0"
        @click="router.push('/store-delivery')"
        @keydown.enter="router.push('/store-delivery')">
        <div class="flex items-center justify-between gap-3">
          <div class="flex-1 min-w-0">
            <p class="text-sm font-bold text-gray-900 font-mono">{{ d.id }}</p>
            <p class="text-xs text-gray-500 mt-0.5 truncate">{{ d.items }}</p>
          </div>
          <span class="text-xs px-2 py-0.5 rounded-full font-medium shrink-0 border"
            :class="deliveryCls(d.status)">{{ d.status }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
