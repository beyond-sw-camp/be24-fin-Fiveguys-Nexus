<template>
  <KpiCard title="금일 자동 발주" :value="value" unit="건" :sub="sub" :badge="badge" to="/order" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import KpiCard from './KpiCard.vue'
import { getOrdersKpi } from '@/api/dashboard'

const value = ref('-')
const sub = ref('확정 -건 · 수동 -건')
const badge = ref(null)

onMounted(async () => {
  try {
    const { data } = await getOrdersKpi()
    const result = data.result

    value.value = result.todayAutoCount.toLocaleString()
    sub.value = `확정 ${result.confirmedCount}건 · 수동 ${result.todayManualCount}건`
    badge.value = result.dangerCount > 0 ? `이상 ${result.dangerCount}건` : null
  } catch (e) {
    console.error('발주 KPI 조회 실패', e)
  }
})
</script>
