<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col">
    <div class="px-5 pt-5 pb-4 border-b border-gray-100">
      <h2 class="font-bold text-gray-900">진행중 배송</h2>
    </div>
    <div class="flex-1 divide-y divide-gray-50 overflow-y-auto">
      <div v-for="d in ongoingDeliveries" :key="d.id"
        class="px-5 py-4 hover:bg-gray-50 transition-colors cursor-pointer"
        role="button" tabindex="0"
        @click="router.push('/delivery')"
        @keydown.enter="router.push('/delivery')"
        @keydown.space.prevent="router.push('/delivery')">
        <div class="flex items-start gap-3">
          <div class="flex-1 min-w-0">
            <p class="text-xs text-gray-400">발주 {{ d.num }}</p>
            <p class="text-sm font-bold text-gray-900 font-mono">{{ d.id }}</p>
            <div class="flex items-center gap-1.5 mt-1.5 text-xs text-gray-500">
              <span class="flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-blue-400 inline-block"></span>{{ d.from }}
              </span>
              <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
              <span class="flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-green-400 inline-block"></span>{{ d.to }}
              </span>
            </div>
          </div>
          <span class="text-xs px-2 py-0.5 rounded-full font-medium shrink-0" :class="deliveryCls(d.status)">{{
            d.status }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const ongoingDeliveries = [
  { num: 1, id: 'ORD-2604-001', from: '본사 창고', to: '이탈리안 키친',  status: '배송중' },
  { num: 2, id: 'ORD-2604-002', from: '본사 창고', to: '한우 오마카세',  status: '출고대기' },
  { num: 3, id: 'ORD-2604-004', from: '본사 창고', to: '프렌치 비스트로', status: '배송지연' },
  { num: 4, id: 'ORD-2604-005', from: '본사 창고', to: '일식 스시바',    status: '출고완료' },
]

const deliveryCls = s => ({
  '배송중': 'bg-blue-50 text-blue-600 border border-blue-200',
  '출고대기': 'bg-gray-100 text-gray-600 border border-gray-200',
  '출고완료': 'bg-green-50 text-green-700 border border-green-200',
  '배송완료': 'bg-green-50 text-green-700 border border-green-200',
  '배송지연': 'bg-red-50 text-red-600 border border-red-200',
}[s] || 'bg-gray-100 text-gray-500 border border-gray-200')
</script>
