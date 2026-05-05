<template>
  <KpiCard title="재고 위험 품목" :value="value" unit="종" :sub="sub" :badge-danger="true" to="/store-inventory" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from '../KpiCard.vue'

const value = ref('2')
const sub = ref('최소재고 이하 품목')
</script>
