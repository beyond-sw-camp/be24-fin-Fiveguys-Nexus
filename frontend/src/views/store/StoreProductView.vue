<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 제품 관리</h1>
        <p class="text-xs text-gray-500 mt-1">이 매장에서 사용하는 식자재·소모품을 등록합니다. POS·재고·메뉴(레시피)와 연동됩니다.</p>
      </div>
      <div class="flex gap-2">
        <button @click="openModal(null)"
                class="bg-[#2563eb] text-white px-4 py-2 text-sm font-semibold rounded hover:bg-[#1d4ed8] transition-colors flex items-center gap-2 cursor-pointer">
          <Plus class="w-4 h-4" /> 제품 등록
        </button>
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

    <!-- Product Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-lg border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">{{ editTarget ? '제품 정보 수정' : '신규 제품 등록' }}</h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>
        <form @submit.prevent="saveProduct" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">제품명</label>
              <input v-model="form.name" required type="text"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">카테고리</label>
              <select v-model="form.category"
                      class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none">
                <option v-for="c in categories" :key="c">{{ c }}</option>
              </select>
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">단위</label>
              <input v-model="form.unit" type="text" placeholder="ex) kg, 개, L"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">최대재고</label>
              <input v-model.number="form.baseStock" type="number" min="0"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">최소재고</label>
              <input v-model.number="form.minStock" type="number" min="0"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">단가 (원)</label>
              <input v-model.number="form.price" type="number" min="0"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">위험 유통기한 (일)</label>
              <input v-model.number="form.expiryDays" type="number" min="0" placeholder="예) 7"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/10 outline-none" />
            </div>
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showModal = false"
                    class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer">취소</button>
            <button type="submit"
                    class="flex-1 py-2.5 rounded bg-[#2563eb] text-white text-sm font-bold hover:bg-[#1d4ed8] cursor-pointer">저장</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Search } from 'lucide-vue-next'

const categories = ref(['육류', '소스', '채소', '가공식품', '음료'])
const selectedCategory = ref('전체')
const searchQuery = ref('')

const products = ref([
  { code: 'P100', name: '닭(10호)', category: '육류', unit: '마리', baseStock: 120, minStock: 30, price: 7000, expiryDays: 3 },
  { code: 'P101', name: '닭(부분육-윙)', category: '육류', unit: 'kg', baseStock: 80, minStock: 20, price: 9000, expiryDays: 3 },
  { code: 'P102', name: '닭(부분육-다리)', category: '육류', unit: 'kg', baseStock: 80, minStock: 20, price: 9500, expiryDays: 3 },

  { code: 'P200', name: '양념소스', category: '소스', unit: 'kg', baseStock: 50, minStock: 10, price: 4000, expiryDays: 30 },
  { code: 'P201', name: '매운양념소스', category: '소스', unit: 'kg', baseStock: 50, minStock: 10, price: 4500, expiryDays: 30 },
  { code: 'P202', name: '파우더(튀김가루)', category: '소스', unit: 'kg', baseStock: 100, minStock: 20, price: 2000, expiryDays: 60 },

  { code: 'P300', name: '감자', category: '채소', unit: 'kg', baseStock: 150, minStock: 40, price: 1500, expiryDays: 14 },
  { code: 'P301', name: '치즈볼 원재료', category: '가공식품', unit: '팩', baseStock: 100, minStock: 30, price: 3000, expiryDays: 10 },

  { code: 'P400', name: '콜라', category: '음료', unit: '박스', baseStock: 200, minStock: 50, price: 1200, expiryDays: 60 },
  { code: 'P401', name: '사이다', category: '음료', unit: '박스', baseStock: 180, minStock: 40, price: 1200, expiryDays: 60 },
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
