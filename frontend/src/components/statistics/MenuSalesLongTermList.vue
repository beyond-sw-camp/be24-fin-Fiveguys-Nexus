<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { getMenuSalesLongTerm } from '@/api/statistics'

const currentYear = new Date().getFullYear()

const selectedYear = ref(currentYear)
const selectedMonth = ref(null)  // null = 연 전체

const allMenus = ref([])
const visibleCount = ref(15)
const PAGE_SIZE = 15

const yearOptions = [2025, 2026]
const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1)

const scrollContainer = ref(null)

const fetchData = async () => {
  try {
    const { data } = await getMenuSalesLongTerm(selectedYear.value, selectedMonth.value)
    allMenus.value = data.result.menus
    visibleCount.value = PAGE_SIZE
  } catch (e) {
    console.error('메뉴별 판매량 조회 실패', e)
    allMenus.value = []
  }
}

const visibleMenus = computed(() => allMenus.value.slice(0, visibleCount.value))
const hasMore = computed(() => visibleCount.value < allMenus.value.length)

const periodLabel = computed(() => {
  return selectedMonth.value
    ? `${selectedYear.value}년 ${selectedMonth.value}월`
    : `${selectedYear.value}년 전체`
})

const handleScroll = () => {
  const el = scrollContainer.value
  if (!el || !hasMore.value) return
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 80) {
    visibleCount.value = Math.min(visibleCount.value + PAGE_SIZE, allMenus.value.length)
  }
}

onMounted(() => {
  fetchData()
  scrollContainer.value?.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  scrollContainer.value?.removeEventListener('scroll', handleScroll)
})

watch([selectedYear, selectedMonth], fetchData)
</script>

<template>
  <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col">
    <div class="flex items-start justify-between mb-4 shrink-0">
      <div>
        <h2 class="font-bold text-gray-900">메뉴 판매량 순위</h2>
        <p class="text-xs text-gray-400 mt-0.5">
          {{ periodLabel }} · {{ visibleMenus.length }} / {{ allMenus.length }} 메뉴
        </p>
      </div>
      <div class="flex gap-2">
        <select v-model.number="selectedYear"
          class="border border-gray-200 rounded-md px-3 py-1.5 text-sm bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-300">
          <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}년</option>
        </select>
        <select v-model="selectedMonth"
          class="border border-gray-200 rounded-md px-3 py-1.5 text-sm bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-300">
          <option :value="null">연 전체</option>
          <option v-for="m in monthOptions" :key="m" :value="m">{{ m }}월</option>
        </select>
      </div>
    </div>

    <ul ref="scrollContainer" class="space-y-2 overflow-auto pr-1" style="height: 432px;">
      <li v-for="(item, i) in visibleMenus" :key="item.menuIdx"
        class="flex items-center justify-between py-1.5 px-2 rounded hover:bg-gray-50">
        <div class="flex items-center gap-3">
          <span class="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold"
            :class="i < 3 ? 'bg-orange-100 text-orange-600' : 'bg-gray-100 text-gray-500'">
            {{ i + 1 }}
          </span>
          <span class="text-sm font-medium text-gray-800">{{ item.menuName }}</span>
        </div>
        <span class="text-sm font-semibold text-gray-700">
          {{ item.totalQuantity.toLocaleString('ko-KR') }} 개
        </span>
      </li>
      <li v-if="hasMore" class="text-xs text-gray-400 text-center py-2">
        스크롤하면 더 보이기...
      </li>
      <li v-else-if="visibleMenus.length > 0" class="text-xs text-gray-400 text-center py-2">
        — 끝 —
      </li>
      <li v-if="visibleMenus.length === 0" class="text-sm text-gray-400 text-center py-4">
        데이터 없음
      </li>
    </ul>
  </div>
</template>
