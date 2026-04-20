<template>
  <div class="p-5 space-y-4">
    <div>
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">대시보드</h1>
      <p class="page-spec-hint">
        <code>DASH_007~010</code>금일 발주·위험 재고·정산 누적·배송 단계(출고대기~배송완료·지연) 요약입니다.
      </p>
    </div>

    <!-- KPI -->
    <div class="grid grid-cols-3 gap-4">
      <RouterLink
        v-for="kpi in kpis"
        :key="kpi.title"
        :to="kpi.to"
        class="block bg-white border overflow-hidden transition-all hover:-translate-y-0.5 hover:shadow-[0_8px_20px_rgba(15,23,42,0.08)]"
        :class="kpi.alert ? 'border-red-300' : 'border-gray-200'"
      >
        <div class="p-5">
          <div class="flex justify-between items-start">
            <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ kpi.title }}</p>
            <component :is="kpi.icon" class="w-4 h-4" :class="kpi.alert ? 'text-red-400' : 'text-gray-300'" />
          </div>
          <div class="flex items-end gap-1 mt-3">
            <h3 class="text-3xl font-black tracking-tight" :class="kpi.alert ? 'text-red-600' : 'text-gray-900'">{{ kpi.value }}</h3>
            <span class="text-sm text-gray-400 mb-0.5">{{ kpi.unit }}</span>
          </div>
          <p class="text-xs text-gray-400 mt-2 pt-2 border-t border-gray-100">{{ kpi.sub }}</p>
        </div>
      </RouterLink>
    </div>

    <div class="grid grid-cols-2 gap-5">
      <!-- 미확정 발주서 -->
      <div
        class="bg-white border border-gray-200 rounded-lg overflow-hidden cursor-pointer transition-shadow hover:shadow-[0_8px_20px_rgba(15,23,42,0.06)]"
        @click="goTo('/store-order')"
      >
        <div class="px-5 py-3 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <h3 class="text-sm font-bold text-gray-800 flex items-center gap-2">
            <ClipboardList class="w-4 h-4 text-blue-500" />
            미확정 발주서
          </h3>
        </div>
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-100 bg-gray-50">
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-2.5 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">제안 수량</th>
              <th class="px-5 py-2.5 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="o in pendingOrders" :key="o.id" class="hover:bg-gray-50/50">
              <td class="px-5 py-3 font-semibold text-gray-800">{{ o.product }}</td>
              <td class="px-5 py-3 text-right text-gray-600">{{ o.qty }}개</td>
              <td class="px-5 py-3 text-right">
                <span class="text-xs font-bold px-2 py-0.5 rounded bg-blue-50 text-blue-600 border border-blue-200">제안중</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 재고 위험 품목 -->
      <div
        class="bg-white border border-gray-200 rounded-lg overflow-hidden cursor-pointer transition-shadow hover:shadow-[0_8px_20px_rgba(15,23,42,0.06)]"
        @click="goTo('/store-inventory')"
      >
        <div class="px-5 py-3 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
          <h3 class="text-sm font-bold text-gray-800 flex items-center gap-2">
            <AlertTriangle class="w-4 h-4 text-red-500" />
            재고 위험 품목
          </h3>
        </div>
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-100 bg-gray-50">
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목</th>
              <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">사유</th>
              <th class="px-5 py-2.5 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">현재 재고</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="w in warnings" :key="w.product" class="hover:bg-gray-50/50">
              <td class="px-5 py-3 font-semibold text-gray-800">{{ w.product }}</td>
              <td class="px-5 py-3 text-xs text-gray-400">{{ w.reason }}</td>
              <td class="px-5 py-3 text-right font-bold text-red-600">{{ w.current }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { ClipboardList, AlertTriangle, Receipt } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const kpis = ref([
  { title: '미확정 발주서',    value: '3',    unit: '건',  alert: false, icon: ClipboardList, sub: '시스템 자동 발주 제안 대기중', to: '/store-order' },
  { title: '재고 위험 품목',   value: '2',    unit: '종',  alert: true,  icon: AlertTriangle,  sub: '최소재고 이하 즉시 확인 필요', to: '/store-inventory' },
  { title: '이번 달 정산 예정', value: '12.5', unit: '만원', alert: false, icon: Receipt,       sub: '2026-04 납부 예정', to: '/store-settlement' },
])

const pendingOrders = ref([
  { id: 1, product: '우유(1L)',    qty: 200 },
  { id: 2, product: '바닐라 시럽', qty: 60  },
  { id: 3, product: '종이컵(M)',   qty: 500 },
])

const warnings = ref([
  { product: '우유(1L)',    current: '148팩',  reason: '최소재고 120팩에 근접' },
  { product: '바닐라 시럽', current: '5병',    reason: '최소재고 30병 이하 위험' },
])

function goTo(path) {
  router.push(path)
}
</script>
