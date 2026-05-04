<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">운영 현황</h1>
      </div>
      <div class="flex items-center gap-2">
        <button v-for="tab in periodTabs" :key="tab" class="text-xs px-3 py-1.5 rounded-md border transition-colors cursor-pointer"
          :class="activePeriod === tab ? 'bg-gray-900 text-white border-gray-900' : 'bg-white text-gray-500 border-gray-200 hover:bg-gray-50'"
          @click="activePeriod = tab">
          {{ tab }}
        </button>
        <span class="text-xs text-gray-400 border border-gray-200 px-3 py-1.5 rounded-md bg-white">{{ today }}</span>
      </div>
    </div>

    <!-- KPI 카드 4개 -->
    <div class="grid grid-cols-4 gap-4">
      <StoreKpiCard />
      <OrdersKpiCard />
      <InventoryKpiCard />
      <DeliveryKpiCard />
    </div>

    <!-- 중간 행: 라인차트 + 재고위험 -->
    <div class="grid grid-cols-3 gap-4">
      <WeeklyOrderChart />
      <DangerInventoryList />
    </div>

    <!-- 하단 행: 이상발주 바그래프 + 지연배송/배송비율 -->
    <div class="grid grid-cols-3 gap-4">
      <DangerOrderChart />
      <div class="space-y-4">
        <OngoingDeliveryList />
        <DeliveryRatioChart />
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import StoreKpiCard from '@/components/dashboard/StoreKpiCard.vue'
import OrdersKpiCard from '@/components/dashboard/OrdersKpiCard.vue'
import InventoryKpiCard from '@/components/dashboard/InventoryKpiCard.vue'
import DeliveryKpiCard from '@/components/dashboard/DeliveryKpiCard.vue'
import WeeklyOrderChart from '@/components/dashboard/WeeklyOrderChart.vue'
import OngoingDeliveryList from '@/components/dashboard/OngoingDeliveryList.vue'
import DangerOrderChart from '@/components/dashboard/DangerOrderChart.vue'
import DeliveryRatioChart from '@/components/dashboard/DeliveryRatioChart.vue'
import DangerInventoryList from '@/components/dashboard/DangerInventoryList.vue'

const periodTabs = ['일간', '주간', '월간']
const activePeriod = ref('주간')

const today = computed(() => new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))
</script>
