<template>
  <KpiCard title="미확정 발주" :value="value" unit="건" :sub="sub" to="/store-order" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from '../KpiCard.vue'

const value = ref('3')
const sub = ref('자동 발주 승인 대기')
</script>
