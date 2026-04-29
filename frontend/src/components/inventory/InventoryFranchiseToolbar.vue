<script setup>
const props = defineProps({
  filterRegion: {
    type: String,
    required: true,
  },
  storeSearch: {
    type: String,
    required: true,
  },
  selectedStoreIdx: {
    type: String,
    required: true,
  },
  visibleStores: {
    type: Array,
    required: true,
  },
  listLoading: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits([
  'update:filterRegion',
  'update:storeSearch',
  'update:selectedStoreIdx',
  'fetch-store-inventory',
])

const onStoreChange = (event) => {
  emit('update:selectedStoreIdx', event.target.value)
  emit('fetch-store-inventory')
}
</script>

<template>
  <div class="flex gap-3 items-center flex-wrap">
    <select
      :value="props.filterRegion"
      class="px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white"
      @change="emit('update:filterRegion', $event.target.value)"
    >
      <option value="">전체 지역</option>
      <option value="서울">서울</option>
      <option value="경기">경기</option>
      <option value="부산">부산</option>
    </select>

    <input
      :value="props.storeSearch"
      type="search"
      placeholder="매장명 검색"
      autocomplete="off"
      class="min-w-[10rem] flex-1 max-w-xs px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white"
      @input="emit('update:storeSearch', $event.target.value)"
    />

    <select
      :value="props.selectedStoreIdx"
      class="min-w-[12rem] px-3 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none bg-white"
      @change="onStoreChange"
    >
      <option value="">매장 선택</option>
      <option v-for="s in props.visibleStores" :key="s.idx" :value="String(s.idx)">
        {{ s.storeName }}
      </option>
    </select>

    <button
      type="button"
      class="px-3 py-2 rounded-lg border border-[#F37321] text-sm font-semibold text-[#F37321] hover:bg-[#F37321]/5 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
      :disabled="!props.selectedStoreIdx || props.listLoading"
      @click="emit('fetch-store-inventory')"
    >
      {{ props.listLoading ? '조회 중...' : '재고 조회' }}
    </button>
  </div>
</template>
