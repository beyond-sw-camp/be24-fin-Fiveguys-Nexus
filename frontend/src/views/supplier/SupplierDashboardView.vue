<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">대시보드</h1>
      <p class="page-spec-hint">협력사 포털 시연 — 본사 발주·정산과 연동되는 납품 KPI 요약.</p>
    </div>

    <!-- KPI -->
    <div class="grid grid-cols-4 gap-4">
      <div v-for="kpi in kpis" :key="kpi.title"
        class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-sm font-medium text-gray-500">{{ kpi.title }}</p>
            <div class="flex items-end gap-1 mt-2">
              <h3 class="text-3xl font-bold text-gray-900">{{ kpi.value }}</h3>
              <span class="text-base font-medium text-gray-400 mb-1">{{ kpi.unit }}</span>
            </div>
          </div>
          <div class="w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center">
            <component :is="kpi.icon" class="w-5 h-5 text-emerald-500" />
          </div>
        </div>
        <p class="text-xs text-gray-400 mt-3">{{ kpi.sub }}</p>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-6">
      <!-- 처리 대기 주문 -->
      <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <h3 class="font-semibold text-gray-900 flex items-center gap-2">
            <ShoppingCart class="w-4 h-4 text-emerald-500" />
            처리 대기 주문
          </h3>
          <RouterLink to="/supplier-order" class="text-xs text-emerald-600 hover:underline">주문 확인 →</RouterLink>
        </div>
        <div class="divide-y divide-gray-50">
          <div v-for="o in pendingOrders" :key="o.id" class="px-6 py-3.5 flex justify-between items-center">
            <div>
              <p class="text-sm font-semibold text-gray-800">{{ o.destination }}</p>
              <p class="text-xs text-gray-400 mt-0.5">{{ o.items }}</p>
            </div>
            <span class="px-2 py-1 bg-yellow-50 text-yellow-600 rounded-full text-xs font-bold">출고대기</span>
          </div>
        </div>
      </div>

      <!-- 출고 현황 -->
      <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <h3 class="font-semibold text-gray-900 flex items-center gap-2">
            <Truck class="w-4 h-4 text-blue-500" />
            오늘 출고 현황
          </h3>
          <RouterLink to="/supplier-shipment" class="text-xs text-emerald-600 hover:underline">출고 처리 →</RouterLink>
        </div>
        <div class="grid grid-cols-3 divide-x divide-gray-100">
          <div v-for="stat in shipStats" :key="stat.label" class="py-5 text-center">
            <p class="text-2xl font-bold" :class="stat.color">{{ stat.count }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ stat.label }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { ShoppingCart, Truck, PackageCheck, Receipt } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const kpis = ref([
  { title: '오늘 신규 주문',  value: '3',  unit: '건', icon: ShoppingCart, sub: '확정된 발주서 기준' },
  { title: '출고 대기',       value: '2',  unit: '건', icon: Truck,         sub: '즉시 출고 처리 필요' },
  { title: '오늘 출고 완료',  value: '1',  unit: '건', icon: PackageCheck,  sub: '오늘 출고 처리 건' },
  { title: '이번 달 매출',    value: '450', unit: '만원', icon: Receipt,   sub: '2026-04 기준' },
])

const pendingOrders = ref([
  { id: 1, destination: '여의도역점',       items: '우유(1L) 200팩, 두유(1L) 80팩' },
  { id: 2, destination: '부산센텀점',       items: '우유(1L) 150팩' },
])

const shipStats = ref([
  { label: '출고 대기', count: 2, color: 'text-yellow-500' },
  { label: '배송 중',   count: 1, color: 'text-blue-600'   },
  { label: '완료',      count: 1, color: 'text-green-600'  },
])
</script>
