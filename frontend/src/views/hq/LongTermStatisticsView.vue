<script setup>
import YearlyTotalCard from '@/components/statistics/YearlyTotalCard.vue'
import MonthlyTotalCard from '@/components/statistics/MonthlyTotalCard.vue'
import StoreSalesLongTermList from '@/components/statistics/StoreSalesLongTermList.vue'
import MenuSalesLongTermList from '@/components/statistics/MenuSalesLongTermList.vue'
</script>

<template>
  <div class="flex-1 overflow-auto p-5 space-y-4">
    <!-- 헤더 -->
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-[22px] font-bold text-gray-900 tracking-tight">장기 통계</h1>
        <p class="text-xs text-gray-400 mt-1">연도/월/매장/메뉴별 매출 분석</p>
      </div>
    </div>

    <!-- 연도/월별 금액 카드 -->
    <div class="grid grid-cols-2 gap-4">
      <YearlyTotalCard />
      <MonthlyTotalCard />
    </div>

    <!-- 매장/메뉴 리스트 (무한 스크롤) -->
    <div class="grid grid-cols-2 gap-4">
      <StoreSalesLongTermList />
      <MenuSalesLongTermList />
    </div>
  </div>
</template>
