<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 제품 관리</h1>
      </div>
    </div>

    <!-- Search + Category filter -->
    <div class="flex gap-3 items-center flex-wrap">
      <div class="relative">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="제품명 검색..."
          @input="handleSearch"
          class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-52
             bg-white shadow-sm
             focus:border-[#2563eb] focus:ring-1 focus:ring-[#2563eb]
             outline-none transition-colors"
        />
      </div>
      <div class="flex gap-1.5 flex-wrap">
        <button
          @click="selectedCategory = '전체'"
          class="px-3 py-1.5 text-sm font-semibold border rounded-lg
                transition-colors shadow-sm cursor-pointer"
          :class="selectedCategory === '전체'
            ? 'bg-[#2563eb] text-white border-[#2563eb]'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
          전체
        </button>
        <button
          v-for="cat in categories"
          :key="cat.idx"
          @click="selectedCategory = cat.categoryName"
          class="px-3 py-1.5 text-sm font-semibold border rounded-lg
                transition-colors shadow-sm cursor-pointer"
          :class="selectedCategory === cat.categoryName
            ? 'bg-[#2563eb] text-white border-[#2563eb]'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
          {{ cat.categoryName }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품코드</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">카테고리</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">단위</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최대재고</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">최소재고</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">단가</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">위험 유통기한</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
        <tr v-for="p in filteredProducts" :key="p.idx" class="hover:bg-gray-50/50 transition-colors">
          <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ p.idx }}</td>
          <td class="px-5 py-3.5 font-semibold text-gray-900">{{ p.productName }}</td>
          <td class="px-5 py-3.5">
            <span class="text-xs font-semibold px-2 py-0.5 bg-gray-100 text-gray-600 border border-gray-200 rounded">{{ p.categoryName }}</span>
          </td>
          <td class="px-5 py-3.5 text-gray-600">{{ p.productUnit }}</td>
          <td class="px-5 py-3.5 font-medium text-gray-900">{{ p.maxStock?.toLocaleString() }}</td>
          <td class="px-5 py-3.5 font-semibold text-[#2563eb]">{{ p.minStock?.toLocaleString() }}</td>
          <td class="px-5 py-3.5 font-medium text-gray-900">₩ {{ p.unitPrice?.toLocaleString() }}</td>
          <td class="px-5 py-3.5 text-xs font-mono"
              :class="p.dangerDays ? 'text-amber-600 font-semibold' : 'text-gray-400'">
            {{ p.dangerDays ? `D-${p.dangerDays}` : '-' }}
          </td>
        </tr>
        <tr v-if="filteredProducts.length === 0">
          <td colspan="8" class="px-5 py-12 text-center text-gray-400 text-sm">검색 결과가 없습니다.</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from 'lucide-vue-next'
import { getStoreProductList, searchProduct } from '@/api/product'
import { readCategoryList } from '@/api/category'


const categories = ref([])
const products = ref([])
const selectedCategory = ref('전체')
const searchQuery = ref('')

// 데이터 로드: 페이지가 열릴 때 해당 가맹점의 제품 목록을 가져옵니다.
const loadData = async () => {
  try {
    // 카테고리 목록과 가맹점 전용 제품 목록을 병렬로 가져옴
    const [catRes, prodRes] = await Promise.all([
      readCategoryList(),
      getStoreProductList()
    ])
    categories.value = catRes.data
    products.value = prodRes.data
  } catch (error) {
    console.error('데이터 호출 중 오류 발생:', error)
  }
}

// 검색 핸들러
const handleSearch = async () => {
  // 검색어가 없으면 다시 가맹점별 목록 로드
  if (!searchQuery.value.trim()) {
    const response = await getStoreProductList(storeIdx.value)
    products.value = response.data
    return
  }

  try {
    // 백엔드의 검색 API가 전체 검색인지 매장 내 검색인지에 따라 결과가 달라질 수 있습니다.
    const response = await searchProduct(searchQuery.value)
    products.value = response.data
  } catch (error) {
    console.error('검색 실패:', error)
  }
}

onMounted(() => {
  // 컴포넌트 마운트 시 데이터 로드
  loadData()
})

// 카테고리 필터링 (클라이언트 사이드)
const filteredProducts = computed(() => {
  return products.value.filter(p => {
    return selectedCategory.value === '전체' || p.categoryName === selectedCategory.value
  })
})
</script>
