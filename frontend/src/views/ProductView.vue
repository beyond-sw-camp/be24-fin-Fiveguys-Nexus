<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-bold text-gray-900 tracking-tight">제품 목록 관리</h1>
      <div class="flex gap-2">
        <button @click="showCategoryModal = true"
                class="px-4 py-2 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 flex items-center gap-2 cursor-pointer">
          <Tag class="w-4 h-4" /> 카테고리 관리
        </button>
        <button @click="openAddModal"
                class="px-4 py-2 bg-[#F37321] text-white rounded-lg text-sm font-bold hover:bg-[#e0661d] flex items-center gap-2 cursor-pointer transition-colors">
          <Plus class="w-4 h-4" /> 제품 등록
        </button>
      </div>
    </div>

    <div>
      <div class="flex gap-3 items-center flex-wrap mb-4">
        <div class="relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input v-model="searchQuery" type="text" placeholder="제품명 또는 매장명 검색..."
                 class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-64 bg-white shadow-sm
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
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">입점 매장</th>
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
          <tr v-for="p in filteredProducts" :key="p.code + '_' + p.storeId" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ p.code }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ p.name }}</td>
            <td class="px-5 py-3.5">
                <span class="text-xs font-semibold px-2 py-0.5 rounded bg-blue-50 text-blue-500 border border-blue-100">
                  {{ p.storeName }}
                </span>
            </td>
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
            <td colspan="10" class="px-5 py-12 text-center text-gray-400 text-sm">검색 결과가 없습니다.</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-lg border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">{{ form.code ? '제품 정보 수정' : '신규 제품 등록' }}</h3>
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
import { Trash2, Search, Tag, Plus } from 'lucide-vue-next'

// --- 데이터 및 상태 관리 ---
// 카테고리: 더벤티 메뉴 및 운영 분류
const categories = ref(['커피/원두', '유제품/리얼라떼', '에이드/티', '프라페/버블티', '베이커리', '더벤티 소모품'])
const selectedCategory = ref('전체')
const searchQuery = ref('')
const newCategory = ref('')

// 제품 목록: 더벤티 전용 레시피와 대용량 부자재 중심
const products = ref([
  { code: 'V100', name: '더벤티 뉴-에스프레소 블렌드', category: '커피/원두',    unit: 'kg',  baseStock: 150, minStock: 30, price: 28000, expiryDays: 90 },
  { code: 'V101', name: '디카페인 과테말라 블렌드',   category: '커피/원두',    unit: 'kg',  baseStock: 40,  minStock: 10, price: 32000, expiryDays: 90 },
  { code: 'V200', name: '서울우유 바리스타즈 (1L)',  category: '유제품/리얼라떼', unit: '팩',  baseStock: 300, minStock: 60, price: 2750,  expiryDays: 7  },
  { code: 'V201', name: '연유 베이스 (시그니처)',     category: '유제품/리얼라떼', unit: 'kg',  baseStock: 80,  minStock: 20, price: 12000, expiryDays: 30 },
  { code: 'V300', name: '자몽 농축액',              category: '에이드/티',    unit: 'kg',  baseStock: 50,  minStock: 15, price: 18500, expiryDays: 180 },
  { code: 'V301', name: '청포도 베이스',            category: '에이드/티',    unit: 'kg',  baseStock: 50,  minStock: 15, price: 16000, expiryDays: 180 },
  { code: 'V400', name: '자바칩 토핑',              category: '프라페/버블티', unit: 'kg',  baseStock: 60,  minStock: 12, price: 22000, expiryDays: null },
  { code: 'V401', name: '타피오카 펄 (냉동)',        category: '프라페/버블티', unit: 'kg',  baseStock: 100, minStock: 25, price: 14000, expiryDays: 365 },
  { code: 'V402', name: '쿠키앤크림 파우더',         category: '프라페/버블티', unit: 'kg',  baseStock: 40,  minStock: 10, price: 15500, expiryDays: null },
  { code: 'V500', name: '감자빵/고구마빵 (냉동)',    category: '베이커리',     unit: '박스', baseStock: 30,  minStock: 5,  price: 45000, expiryDays: 180 },
  { code: 'V501', name: '미니 붕어빵 (팥/슈크림)',    category: '베이커리',     unit: 'kg',  baseStock: 40,  minStock: 8,  price: 13000, expiryDays: 180 },
  { code: 'V600', name: 'Venti 아이스컵 (20oz)',    category: '더벤티 소모품', unit: '박스', baseStock: 50,  minStock: 15, price: 72000, expiryDays: null },
  { code: 'V601', name: 'Venti 핫컵 (20oz)',      category: '더벤티 소모품', unit: '박스', baseStock: 30,  minStock: 10, price: 68000, expiryDays: null },
  { code: 'V602', name: '보라색 빨대 (개별포장)',     category: '더벤티 소모품', unit: '박스', baseStock: 20,  minStock: 5,  price: 35000, expiryDays: null },
  { code: 'V603', name: '퍼플 로고 컵홀더',          category: '더벤티 소모품', unit: '박스', baseStock: 40,  minStock: 12, price: 42000, expiryDays: null },
])

// 매장 목록: 더벤티 주요 지점 컨셉
const stores = ref([
  { id: 'VT01', name: '더벤티 서울강남역점' },
  { id: 'VT02', name: '더벤티 부산서면본점' },
  { id: 'VT03', name: '더벤티 대구동성로점' },
  { id: 'VT04', name: '더벤티 광주상무점' },
  { id: 'VT05', name: '더벤티 인천구월점' },
])

// 매장-제품 매핑 (더벤티 전용 코드로 동기화)
const storeProductMap = {
  VT01: ['V100', 'V101', 'V200', 'V201', 'V300', 'V400', 'V500', 'V600', 'V602', 'V603'],
  VT02: ['V100', 'V101', 'V200', 'V201', 'V301', 'V401', 'V501', 'V600', 'V601', 'V602', 'V603'],
  VT03: ['V100', 'V200', 'V300', 'V301', 'V400', 'V402', 'V600', 'V602'],
  VT04: ['V100', 'V101', 'V200', 'V201', 'V401', 'V500', 'V600', 'V602', 'V603'],
  VT05: ['V100', 'V200', 'V300', 'V400', 'V401', 'V501', 'V600', 'V602'],
}

// --- 계산된 속성 (Computed) ---
const expandedProducts = computed(() => {
  const productMap = new Map(products.value.map(p => [p.code, p]))
  const storeMap = new Map(stores.value.map(s => [s.id, s.name]))
  const result = []
  for (const [storeId, codes] of Object.entries(storeProductMap)) {
    const storeName = storeMap.get(storeId) ?? storeId
    for (const code of codes) {
      const product = productMap.get(code)
      if (product) result.push({ ...product, storeId, storeName })
    }
  }
  return result
})

const filteredProducts = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  return expandedProducts.value.filter(p => {
    const matchCat = selectedCategory.value === '전체' || p.category === selectedCategory.value
    const matchSearch = !q || p.name.toLowerCase().includes(q) || p.storeName.toLowerCase().includes(q)
    return matchCat && matchSearch
  })
})

// --- 카테고리 로직 ---
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

// --- 제품 관리 로직 ---
const showModal = ref(false)
const form = ref({ code: '', name: '', category: '', unit: '', baseStock: 0, minStock: 0, price: 0, expiryDays: null })

function openAddModal() {
  form.value = { code: '', name: '', category: categories.value[0], unit: '', baseStock: 0, minStock: 0, price: 0, expiryDays: null }
  showModal.value = true
}

function openModal(product) {
  const { storeId, storeName, ...fields } = product
  form.value = { ...fields }
  showModal.value = true
}

function saveProduct() {
  const original = products.value.find(p => p.code === form.value.code)
  if (original) {
    Object.assign(original, form.value)
  } else {
    products.value.push({ ...form.value })
  }
  showModal.value = false
}

function deleteProduct(code) {
  if (confirm('제품을 삭제하시겠습니까?')) {
    products.value = products.value.filter(p => p.code !== code)
  }
}
</script>
