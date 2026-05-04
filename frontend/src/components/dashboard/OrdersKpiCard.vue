<template>
  <KpiCard title="금일 자동 발주" :value="value" unit="건" :sub="sub" :delta="delta" to="/order" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from './KpiCard.vue'

const value = ref('85')
const sub = ref('확정 62건')
const delta = ref(8.7)
</script>
