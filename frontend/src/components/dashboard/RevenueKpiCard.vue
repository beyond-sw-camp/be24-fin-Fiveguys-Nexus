<template>
  <KpiCard title="총 매출" :value="value" unit="백만" :sub="sub" :delta="delta" to="/settlement" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from './KpiCard.vue'

const value = ref('89.2')
const sub = ref('전일 대비')
const delta = ref(12.4)
</script>
