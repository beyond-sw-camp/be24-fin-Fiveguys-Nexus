<template>
  <KpiCard title="이번달 정산 예정" :value="value" unit="만원" :sub="sub" :delta="delta" to="/store-settlement" />
</template>

<script setup>
import { ref } from 'vue'
import KpiCard from '../KpiCard.vue'

const value = ref('12.5')
const sub = ref('2026-04 납부 예정')
const delta = ref(-3.1)
</script>
