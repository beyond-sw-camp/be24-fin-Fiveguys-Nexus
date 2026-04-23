<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">제품 목록 관리</h1>
      <button @click="showCategoryModal = true"
              class="px-4 py-2 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2 cursor-pointer">
        <Tag class="w-4 h-4" /> 카테고리 관리
      </button>
    </div>

    <!-- Tabs -->
    <div class="flex border-b border-gray-200">
      <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
        class="px-5 py-2.5 text-sm font-semibold border-b-2 -mb-px transition-colors cursor-pointer"
        :class="activeTab === tab.id
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ── 전체 제품 목록 탭 ── -->
    <div v-if="activeTab === 'all'">
      <div class="flex gap-3 items-center flex-wrap mb-4">
        <div class="relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input v-model="searchQuery" type="text" placeholder="제품명 검색..."
            class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-52 bg-white shadow-sm
                   focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321] outline-none transition-colors" />
        </div>
        <div class="flex gap-1.5 flex-wrap">
          <button v-for="cat in ['전체', ...categories]" :key="cat" @click="selectedCategory = cat"
            class="px-3 py-1.5 text-sm font-semibold border rounded-lg transition-colors shadow-sm cursor-pointer"
            :class="selectedCategory === cat
              ? 'bg-[#F37321] text-white border-[#F37321]'
              : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
            {{ cat }}
          </button>
        </div>
      </div>

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
              <td class="px-5 py-3.5 font-semibold text-[#F37321]">{{ p.minStock.toLocaleString() }}</td>
              <td class="px-5 py-3.5 font-medium text-gray-900">₩ {{ p.price.toLocaleString() }}</td>
              <td class="px-5 py-3.5 text-xs font-mono"
                  :class="p.expiryDays ? 'text-amber-600 font-semibold' : 'text-gray-400'">
                {{ p.expiryDays ? `D-${p.expiryDays}` : '-' }}
              </td>
              <td class="px-5 py-3.5">
                <div class="flex justify-center gap-2">
                  <button @click="openModal(p)" class="px-3 py-1.5 text-xs font-semibold text-[#F37321] border border-[#F37321] rounded hover:bg-orange-50 transition-colors cursor-pointer">수정</button>
                  <button @click="deleteProduct(p.code)" class="px-3 py-1.5 text-xs font-semibold text-red-500 border border-red-400 rounded hover:bg-red-50 transition-colors cursor-pointer">삭제</button>
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

    <!-- ── 매장별 제품 목록 탭 ── -->
    <div v-if="activeTab === 'store'">
      <div class="flex gap-3 items-center mb-4">
        <div class="relative w-64">
          <select v-model="selectedStoreId"
            class="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg text-sm outline-none
                   focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321] transition-colors shadow-sm cursor-pointer text-gray-700">
            <option value="">매장을 선택하세요</option>
            <option v-for="s in stores" :key="s.id" :value="s.id">{{ s.name }}</option>
          </select>
        </div>
      </div>

      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div v-if="!selectedStoreId" class="py-16 text-center text-gray-400 text-sm">
          <p>조회할 매장을 선택해주세요.</p>
        </div>
        <table v-else class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품코드</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품명</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">카테고리</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">단위</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">단가</th>
              <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="p in storeProducts" :key="p.code" class="hover:bg-gray-50/50 transition-colors">
              <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ p.code }}</td>
              <td class="px-5 py-3.5 font-semibold text-gray-900">{{ p.name }}</td>
              <td class="px-5 py-3.5">
                <span class="text-xs font-semibold px-2 py-0.5 bg-gray-100 text-gray-600 border border-gray-200 rounded">{{ p.category }}</span>
              </td>
              <td class="px-5 py-3.5 text-gray-600">{{ p.unit }}</td>
              <td class="px-5 py-3.5 font-medium text-gray-900">₩ {{ p.price.toLocaleString() }}</td>
              <td class="px-5 py-3.5">
                <div class="flex justify-center gap-2">
                  <button @click="openModal(p)" class="px-3 py-1.5 text-xs font-semibold text-[#F37321] border border-[#F37321] rounded hover:bg-orange-50 transition-colors cursor-pointer">수정</button>
                  <button @click="deleteProduct(p.code)" class="px-3 py-1.5 text-xs font-semibold text-red-500 border border-red-400 rounded hover:bg-red-50 transition-colors cursor-pointer">삭제</button>
                </div>
              </td>
            </tr>
            <tr v-if="storeProducts.length === 0">
              <td colspan="6" class="px-5 py-12 text-center text-gray-400 text-sm">해당 매장에 등록된 제품이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 제품 수정 모달 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-lg border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">제품 정보 수정</h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
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
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">최대재고</label>
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
                    class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer">취소</button>
            <button type="submit"
                    class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] cursor-pointer">저장</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 카테고리 관리 모달 -->
    <div v-if="showCategoryModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showCategoryModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-md border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">카테고리 관리</h3>
          <button @click="showCategoryModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>
        <div class="p-6 space-y-4">
          <div class="flex gap-2">
            <input v-model="newCategory" type="text" placeholder="새 카테고리명 입력"
                   @keyup.enter="addCategory"
                   class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            <button @click="addCategory"
                    class="px-4 py-2 bg-[#F37321] text-white text-sm font-semibold rounded hover:bg-[#e0661d] cursor-pointer">추가</button>
          </div>
          <div class="border border-gray-200 rounded-lg overflow-hidden">
            <div class="px-4 py-2.5 bg-gray-50 border-b border-gray-100">
              <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">카테고리 목록 ({{ categories.length }}개)</p>
            </div>
            <div class="divide-y divide-gray-100 max-h-64 overflow-y-auto">
              <div v-for="cat in categories" :key="cat"
                   class="flex items-center justify-between px-4 py-2.5 hover:bg-gray-50">
                <span class="text-sm font-medium text-gray-700">{{ cat }}</span>
                <button @click="deleteCategory(cat)" class="text-gray-300 hover:text-red-500 transition-colors cursor-pointer">
                  <Trash2 class="w-3.5 h-3.5" />
                </button>
              </div>
              <div v-if="categories.length === 0" class="px-4 py-6 text-center text-gray-400 text-sm">
                등록된 카테고리가 없습니다.
              </div>
            </div>
          </div>
          <button @click="showCategoryModal = false"
                  class="w-full py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Trash2, Search, Tag } from 'lucide-vue-next'

const tabs = [
  { id: 'all',   label: '전체 제품 목록' },
  { id: 'store', label: '매장별 제품 목록' },
]
const activeTab = ref('all')

const categories = ref(['육류', '어류', '채소', '소스류', '오일/유제품', '음료', '기타'])
const selectedCategory = ref('전체')
const searchQuery = ref('')
const newCategory = ref('')

const products = ref([
  { code: 'P100', name: '한우 등심',       category: '육류',       unit: 'kg',  baseStock: 50,  minStock: 10, price: 85000, expiryDays: 5  },
  { code: 'P101', name: '한우 안심',       category: '육류',       unit: 'kg',  baseStock: 30,  minStock: 8,  price: 95000, expiryDays: 5  },
  { code: 'P102', name: '돼지 삼겹살',     category: '육류',       unit: 'kg',  baseStock: 80,  minStock: 20, price: 18000, expiryDays: 5  },
  { code: 'P103', name: '닭 가슴살',       category: '육류',       unit: 'kg',  baseStock: 60,  minStock: 15, price: 8000,  expiryDays: 4  },
  { code: 'P200', name: '연어 (생선)',     category: '어류',       unit: 'kg',  baseStock: 40,  minStock: 10, price: 32000, expiryDays: 3  },
  { code: 'P201', name: '참치 (생선)',     category: '어류',       unit: 'kg',  baseStock: 30,  minStock: 8,  price: 45000, expiryDays: 3  },
  { code: 'P202', name: '새우 (냉동)',     category: '어류',       unit: 'kg',  baseStock: 50,  minStock: 12, price: 22000, expiryDays: 60 },
  { code: 'P300', name: '양파',           category: '채소',       unit: 'kg',  baseStock: 100, minStock: 30, price: 1500,  expiryDays: 14 },
  { code: 'P301', name: '마늘',           category: '채소',       unit: 'kg',  baseStock: 50,  minStock: 15, price: 8000,  expiryDays: 30 },
  { code: 'P302', name: '대파',           category: '채소',       unit: '단',  baseStock: 80,  minStock: 20, price: 2000,  expiryDays: 7  },
  { code: 'P303', name: '파프리카',        category: '채소',       unit: 'kg',  baseStock: 40,  minStock: 10, price: 5000,  expiryDays: 7  },
  { code: 'P400', name: '간장',           category: '소스류',     unit: 'L',   baseStock: 30,  minStock: 8,  price: 4000,  expiryDays: null },
  { code: 'P401', name: '고추장',         category: '소스류',     unit: 'kg',  baseStock: 20,  minStock: 5,  price: 5500,  expiryDays: null },
  { code: 'P402', name: '된장',           category: '소스류',     unit: 'kg',  baseStock: 20,  minStock: 5,  price: 4500,  expiryDays: null },
  { code: 'P500', name: '올리브오일',      category: '오일/유제품', unit: 'L',   baseStock: 25,  minStock: 6,  price: 12000, expiryDays: null },
  { code: 'P501', name: '버터',           category: '오일/유제품', unit: 'kg',  baseStock: 20,  minStock: 5,  price: 9000,  expiryDays: 30 },
  { code: 'P502', name: '생크림',         category: '오일/유제품', unit: 'L',   baseStock: 15,  minStock: 4,  price: 7000,  expiryDays: 10 },
  { code: 'P600', name: '콜라',           category: '음료',       unit: '박스', baseStock: 50,  minStock: 15, price: 15000, expiryDays: null },
  { code: 'P601', name: '생수 (2L)',      category: '음료',       unit: '박스', baseStock: 80,  minStock: 20, price: 8000,  expiryDays: null },
])

// 매장 목록 (STORE)
const stores = ref([
  { id: 'S001', name: '한우 오마카세' },
  { id: 'S002', name: '이탈리안 키친' },
  { id: 'S003', name: '일식 스시바' },
  { id: 'S004', name: '차이나 가든' },
  { id: 'S005', name: '프렌치 비스트로' },
])

// 매장-제품 매핑 (STORE_PRODUCT)
const storeProductMap = {
  S001: ['P100', 'P101', 'P300', 'P301', 'P400', 'P500'],
  S002: ['P102', 'P300', 'P301', 'P302', 'P303', 'P500', 'P501', 'P502', 'P600'],
  S003: ['P200', 'P201', 'P202', 'P300', 'P301', 'P400', 'P601'],
  S004: ['P102', 'P103', 'P300', 'P301', 'P302', 'P400', 'P401', 'P600'],
  S005: ['P101', 'P103', 'P300', 'P303', 'P500', 'P501', 'P502', 'P601'],
}

const selectedStoreId = ref('')

const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchCat = selectedCategory.value === '전체' || p.category === selectedCategory.value
    const matchSearch = !searchQuery.value || p.name.includes(searchQuery.value) || p.code.includes(searchQuery.value)
    return matchCat && matchSearch
  })
})

const storeProducts = computed(() => {
  if (!selectedStoreId.value) return []
  const codes = storeProductMap[selectedStoreId.value] ?? []
  return products.value.filter(p => codes.includes(p.code))
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

// 제품 수정
const showModal = ref(false)
const editTarget = ref(null)
const form = ref({ name: '', category: '', unit: '', baseStock: 0, minStock: 0, price: 0, expiryDays: null })

function openModal(product) {
  editTarget.value = product
  form.value = { ...product }
  showModal.value = true
}

function saveProduct() {
  if (editTarget.value) {
    Object.assign(editTarget.value, form.value)
  }
  showModal.value = false
}

function deleteProduct(code) {
  if (confirm('제품을 삭제하시겠습니까?')) {
    products.value = products.value.filter(p => p.code !== code)
  }
}
</script>
