<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    required: true,
  },
})

const badgeClass = computed(() => {
  const map = {
    입고: 'bg-slate-100 text-slate-700',
    출고: 'bg-orange-50/90 text-orange-950',
    보정: 'bg-stone-100 text-stone-700',
  }
  return map[props.type] || 'bg-stone-50 text-stone-600'
})
</script>

<template>
  <span
    class="inline-flex items-center rounded-md px-2 py-1 text-[11px] font-medium leading-none tracking-tight"
    :class="badgeClass"
  >
    {{ type }}
  </span>
</template>
