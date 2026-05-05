<template>
  <KpiCard title="금일 매출" :value="value" unit="만원" :sub="sub" :delta="delta" to="/store-settlement" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from '../KpiCard.vue'

const value = ref('84.5')
const sub = ref('전일 대비')
const delta = ref(5.2)
</script>
