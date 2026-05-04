<template>
  <KpiCard title="배송 지연" :value="value" unit="건" :sub="sub" :delta="delta" to="/delivery" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from './KpiCard.vue'

const value = ref('4')
const sub = ref('즉시 확인 필요')
const delta = ref(-1.8)
</script>
