<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 제품 관리</h1>
        <p class="text-xs text-gray-500 mt-1">이 매장에서 사용하는 식자재·소모품을 등록합니다. POS·재고·메뉴(레시피)와 연동됩니다.</p>
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
          class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-52
             bg-white shadow-sm
             focus:border-[#2563eb] focus:ring-1 focus:ring-[#2563eb]
             outline-none transition-colors"
        />
      </div>
      <div class="flex gap-1.5 flex-wrap">
        <button
          v-for="cat in ['전체', ...categories]"
          :key="cat"
          @click="selectedCategory = cat"
          class="px-3 py-1.5 text-sm font-semibold border rounded-lg
                transition-colors shadow-sm cursor-pointer"
          :class="selectedCategory === cat
            ? 'bg-[#2563eb] text-white border-[#2563eb]'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
          {{ cat }}
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
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
        <tr v-for="p in filteredProducts" :key="p.code" class="hover:bg-gray-50/50 transition-colors">
          <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ p.code }}</td>
          <td class="px-5 py-3.5 font-semibold text-gray-900">{{ p.name }}</td>
          <td class="px-5 py-3.5">
            <span class="text-xs font-semibold px-2 py-0.5 bg-gray-100 text-gray-600 border border-gray-200 rounded">{{ p.category }}</span>
          </td>
          <td class="px-5 py-3.5 text-gray-600">{{ p.unit }}</td>
          <td class="px-5 py-3.5 font-medium text-gray-900">{{ p.baseStock.toLocaleString() }}</td>
          <td class="px-5 py-3.5 font-semibold text-[#2563eb]">{{ p.minStock.toLocaleString() }}</td>
          <td class="px-5 py-3.5 font-medium text-gray-900">₩ {{ p.price.toLocaleString() }}</td>
          <td class="px-5 py-3.5 text-xs font-mono"
              :class="p.expiryDays ? 'text-amber-600 font-semibold' : 'text-gray-400'">
            {{ p.expiryDays ? `D-${p.expiryDays}` : '-' }}
          </td>
          <td class="px-5 py-3.5">
            <div class="flex justify-center gap-2">
              <button @click="openModal(p)" class="px-3 py-1.5 text-xs font-semibold text-[#2563eb] border border-[#2563eb] rounded hover:bg-blue-50 transition-colors cursor-pointer">
                수정
              </button>
              <button @click="deleteProduct(p.code)" class="px-3 py-1.5 text-xs font-semibold text-red-500 border border-red-400 rounded hover:bg-red-50 transition-colors cursor-pointer">
                삭제
              </button>
            </div>
          </td>
        </tr>
        <tr v-if="filteredProducts.length === 0">
          <td colspan="9" class="px-5 py-12 text-center text-gray-400 text-sm">검색 결과가 없습니다.</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Search } from 'lucide-vue-next'

const categories = ref(['육류', '어류', '채소', '소스류', '오일/유제품', '음료'])
const selectedCategory = ref('전체')
const searchQuery = ref('')

const products = ref([
  { code: 'P100', name: '한우 등심',  category: '육류',        unit: 'kg',   baseStock: 50,  minStock: 10, price: 85000, expiryDays: 5  },
  { code: 'P101', name: '한우 안심',  category: '육류',        unit: 'kg',   baseStock: 30,  minStock: 8,  price: 95000, expiryDays: 5  },
  { code: 'P102', name: '한우 채끝',  category: '육류',        unit: 'kg',   baseStock: 20,  minStock: 5,  price: 78000, expiryDays: 5  },

  { code: 'P200', name: '연어 필렛',  category: '어류',        unit: 'kg',   baseStock: 30,  minStock: 8,  price: 32000, expiryDays: 2  },
  { code: 'P201', name: '참치 블록',  category: '어류',        unit: 'kg',   baseStock: 20,  minStock: 5,  price: 45000, expiryDays: 2  },
  { code: 'P202', name: '새우',       category: '어류',        unit: 'kg',   baseStock: 20,  minStock: 5,  price: 28000, expiryDays: 3  },

  { code: 'P300', name: '양파',       category: '채소',        unit: 'kg',   baseStock: 100, minStock: 20, price: 1500,  expiryDays: 14 },
  { code: 'P301', name: '마늘',       category: '채소',        unit: 'kg',   baseStock: 50,  minStock: 10, price: 8000,  expiryDays: 14 },
  { code: 'P302', name: '대파',       category: '채소',        unit: 'kg',   baseStock: 30,  minStock: 8,  price: 3000,  expiryDays: 7  },

  { code: 'P400', name: '간장',       category: '소스류',      unit: 'L',    baseStock: 30,  minStock: 8,  price: 4000,  expiryDays: null },
  { code: 'P401', name: '고추장',     category: '소스류',      unit: 'kg',   baseStock: 20,  minStock: 5,  price: 5500,  expiryDays: null },

  { code: 'P500', name: '올리브오일', category: '오일/유제품', unit: 'L',    baseStock: 20,  minStock: 5,  price: 12000, expiryDays: null },
  { code: 'P501', name: '버터',       category: '오일/유제품', unit: 'kg',   baseStock: 20,  minStock: 5,  price: 9000,  expiryDays: 14 },
  { code: 'P502', name: '생크림',     category: '오일/유제품', unit: 'L',    baseStock: 20,  minStock: 5,  price: 7000,  expiryDays: 7  },

  { code: 'P600', name: '콜라',       category: '음료',        unit: '박스', baseStock: 100, minStock: 20, price: 15000, expiryDays: null },
  { code: 'P601', name: '생수',       category: '음료',        unit: '박스', baseStock: 80,  minStock: 20, price: 8000,  expiryDays: null },
])

const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchCat = selectedCategory.value === '전체' || p.category === selectedCategory.value
    const matchSearch = !searchQuery.value || p.name.includes(searchQuery.value) || p.code.includes(searchQuery.value)
    return matchCat && matchSearch
  })
})

// 제품 CRUD
const showModal = ref(false)
const editTarget = ref(null)
const form = ref({ name: '', category: '원두', unit: '', baseStock: 0, minStock: 0, price: 0, expiryDays: null })

function openModal(product) {
  editTarget.value = product
  form.value = product
    ? { ...product }
    : { name: '', category: categories.value[0] || '', unit: '', baseStock: 0, minStock: 0, price: 0, expiryDays: null }
  showModal.value = true
}

function saveProduct() {
  if (editTarget.value) {
    Object.assign(editTarget.value, form.value)
  } else {
    const newCode = 'P' + String((products.value.length + 1) * 100)
    products.value.push({ code: newCode, ...form.value })
  }
  showModal.value = false
}

function deleteProduct(code) {
  if (confirm('제품을 삭제하시겠습니까?')) {
    products.value = products.value.filter(p => p.code !== code)
  }
}
</script>
