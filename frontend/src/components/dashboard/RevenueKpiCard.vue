<template>
  <KpiCard title="총 매출" :value="value" unit="백만" :sub="sub" :delta="delta" to="/settlement" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import KpiCard from './KpiCard.vue'
import { getRevenueKpi } from '@/api/dashboard'

const value = ref('-')
const sub = ref('금일 매출 -')
const delta = ref(0)

onMounted(async () => {
  try {
    const { data } = await getRevenueKpi()
    const result = data.result
    const monthly = result.monthlyRevenue
    const today = result.todayRevenue

    // 백만 단위로 변환
    value.value = (monthly / 1000000).toFixed(1)
    sub.value = `금일 ${(today / 1000000).toFixed(1)}백만`

    // 금일 매출 / 월간 매출 비율로 delta 표시
    delta.value = monthly > 0 ? Math.round(today * 1000 / monthly) / 10 : 0
  } catch (e) {
    console.error('매출 KPI 조회 실패', e)
  }
})
</script>
