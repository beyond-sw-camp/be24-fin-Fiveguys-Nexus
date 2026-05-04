<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5">
    <div class="mb-3">
      <h2 class="font-bold text-gray-900">재고 위험</h2>
    </div>
    <div class="space-y-2">
      <div v-for="w in warnings" :key="w.store + w.product"
        class="flex items-center justify-between py-1.5 cursor-pointer hover:bg-gray-50 rounded px-1 transition-colors"
        role="button" tabindex="0"
        @click="router.push('/inventory/franchise')"
        @keydown.enter="router.push('/inventory/franchise')"
        @keydown.space.prevent="router.push('/inventory/franchise')">
        <div class="flex items-center gap-2">
          <span class="w-1.5 h-1.5 rounded-full bg-red-500"></span>
          <p class="text-sm text-gray-800 truncate max-w-28">{{ w.product }}</p>
        </div>
        <span class="text-xs px-2 py-0.5 rounded-full font-medium bg-red-100 text-red-600">
          {{ w.current }}/{{ w.min }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const warnings = ref([
  { store: '이탈리안 키친',  product: '한우 등심',  current: 30,  min: 50   },
  { store: '일식 스시바',    product: '올리브오일', current: 2,   min: 5    },
  { store: '한우 오마카세',  product: '버터',       current: 8,   min: 20   },
  { store: '차이나 가든',    product: '생수',       current: 200, min: 500  },
])
</script>
