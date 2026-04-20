<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">제품 목록 관리</h1>
        <p class="page-spec-hint">
          <code>PRODUCT_001~005 · CATEGORY_001~003</code>제품코드·명·카테고리·단위·기준/최소재고·단가·위험 유통기한 조회·등록·수정·삭제 및 카테고리 관리입니다.
        </p>
      </div>
      <div class="flex gap-2">
        <button @click="showCategoryModal = true"
          class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2">
          <Tag class="w-4 h-4" /> 카테고리 관리
        </button>
        <button @click="openModal(null)"
          class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded hover:bg-[#e0661d] transition-colors flex items-center gap-2">
          <Plus class="w-4 h-4" /> 제품 등록
        </button>
      </div>
    </div>

    <!-- Search + Category filter -->
    <div class="flex gap-3 items-center flex-wrap">
      <div class="relative">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input v-model="searchQuery" type="text" placeholder="제품명 검색..."
          class="pl-9 pr-4 py-2 rounded border border-gray-200 text-sm w-52 focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
      </div>
      <div class="flex gap-1.5 flex-wrap">
        <button v-for="cat in ['전체', ...categories]" :key="cat"
          @click="selectedCategory = cat"
          class="px-3 py-1.5 text-sm font-semibold border transition-colors rounded"
          :class="selectedCategory === cat
            ? 'bg-[#F37321] text-white border-[#F37321]'
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
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">기준재고</th>
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
            <td class="px-5 py-3.5 font-semibold text-[#F37321]">{{ p.minStock.toLocaleString() }}</td>
            <td class="px-5 py-3.5 font-medium text-gray-900">₩{{ p.price.toLocaleString() }}</td>
            <td class="px-5 py-3.5 text-xs font-mono"
              :class="p.expiryDays ? 'text-amber-600 font-semibold' : 'text-gray-400'">
              {{ p.expiryDays ? `D-${p.expiryDays}` : '-' }}
            </td>
            <td class="px-5 py-3.5">
              <div class="flex justify-center gap-3">
                <button @click="openModal(p)" class="text-gray-300 hover:text-[#F37321] transition-colors">
                  <Pencil class="w-4 h-4" />
                </button>
                <button @click="deleteProduct(p.code)" class="text-gray-300 hover:text-red-500 transition-colors">
                  <Trash2 class="w-4 h-4" />
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
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600">✕</button>
        </div>
        <form @submit.prevent="saveProduct" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">제품명</label>
              <input v-model="form.name" required type="text"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">카테고리</label>
              <select v-model="form.category"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
                <option v-for="c in categories" :key="c">{{ c }}</option>
              </select>
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">단위</label>
              <input v-model="form.unit" type="text" placeholder="ex) kg, 개, L"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">기준재고</label>
              <input v-model.number="form.baseStock" type="number" min="0"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">최소재고</label>
              <input v-model.number="form.minStock" type="number" min="0"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">단가 (원)</label>
              <input v-model.number="form.price" type="number" min="0"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">위험 유통기한 (일)</label>
              <input v-model.number="form.expiryDays" type="number" min="0" placeholder="예) 7"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showModal = false"
              class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">취소</button>
            <button type="submit"
              class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d]">저장</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Category Management Modal (CATEGORY_001~003) -->
    <div v-if="showCategoryModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showCategoryModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-md border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">카테고리 관리</h3>
          <button @click="showCategoryModal = false" class="text-gray-400 hover:text-gray-600">✕</button>
        </div>
        <div class="p-6 space-y-4">
          <!-- 등록 폼 -->
          <div class="flex gap-2">
            <input v-model="newCategory" type="text" placeholder="새 카테고리명 입력"
              @keyup.enter="addCategory"
              class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            <button @click="addCategory"
              class="px-4 py-2 bg-[#F37321] text-white text-sm font-semibold rounded hover:bg-[#e0661d]">
              추가
            </button>
          </div>
          <!-- 목록 -->
          <div class="border border-gray-200 rounded-lg overflow-hidden">
            <div class="px-4 py-2.5 bg-gray-50 border-b border-gray-100">
              <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">카테고리 목록 ({{ categories.length }}개)</p>
            </div>
            <div class="divide-y divide-gray-100 max-h-64 overflow-y-auto">
              <div v-for="cat in categories" :key="cat"
                class="flex items-center justify-between px-4 py-2.5 hover:bg-gray-50">
                <span class="text-sm font-medium text-gray-700">{{ cat }}</span>
                <button @click="deleteCategory(cat)"
                  class="text-gray-300 hover:text-red-500 transition-colors">
                  <Trash2 class="w-3.5 h-3.5" />
                </button>
              </div>
              <div v-if="categories.length === 0"
                class="px-4 py-6 text-center text-gray-400 text-sm">
                등록된 카테고리가 없습니다.
              </div>
            </div>
          </div>
          <button @click="showCategoryModal = false"
            class="w-full py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">
            닫기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Pencil, Trash2, Search, Tag } from 'lucide-vue-next'

const categories = ref(['원두', '유제품', '시럽·소스', '포장재', '기타'])
const selectedCategory = ref('전체')
const searchQuery = ref('')
const newCategory = ref('')

const products = ref([
  { code: 'P100', name: '프리미엄 원두',   category: '원두',      unit: 'kg',  baseStock: 500,  minStock: 100, price: 18000, expiryDays: 180 },
  { code: 'P101', name: '에스프레소 원두', category: '원두',      unit: 'kg',  baseStock: 300,  minStock: 80,  price: 22000, expiryDays: 180 },
  { code: 'P200', name: '우유(1L)',         category: '유제품',   unit: '팩',  baseStock: 400,  minStock: 120, price: 1800,  expiryDays: 7   },
  { code: 'P201', name: '두유(1L)',         category: '유제품',   unit: '팩',  baseStock: 200,  minStock: 60,  price: 2100,  expiryDays: 7   },
  { code: 'P300', name: '바닐라 시럽',     category: '시럽·소스', unit: '병', baseStock: 150,  minStock: 30,  price: 8500,  expiryDays: 30  },
  { code: 'P301', name: '카라멜 시럽',     category: '시럽·소스', unit: '병', baseStock: 150,  minStock: 30,  price: 8500,  expiryDays: 30  },
  { code: 'P400', name: '종이컵(M)',        category: '포장재',   unit: '개',  baseStock: 2000, minStock: 500, price: 45,    expiryDays: null },
  { code: 'P401', name: '종이컵(L)',        category: '포장재',   unit: '개',  baseStock: 2000, minStock: 500, price: 55,    expiryDays: null },
])

const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchCat = selectedCategory.value === '전체' || p.category === selectedCategory.value
    const matchSearch = !searchQuery.value || p.name.includes(searchQuery.value) || p.code.includes(searchQuery.value)
    return matchCat && matchSearch
  })
})

// 카테고리 CRUD
const showCategoryModal = ref(false)

function addCategory() {
  const name = newCategory.value.trim()
  if (!name) return
  if (categories.value.includes(name)) { alert('이미 존재하는 카테고리입니다.'); return }
  categories.value.push(name)
  newCategory.value = ''
}

function deleteCategory(cat) {
  const used = products.value.some(p => p.category === cat)
  if (used && !confirm(`'${cat}' 카테고리를 사용 중인 제품이 있습니다. 삭제하시겠습니까?`)) return
  categories.value = categories.value.filter(c => c !== cat)
  if (selectedCategory.value === cat) selectedCategory.value = '전체'
}

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
