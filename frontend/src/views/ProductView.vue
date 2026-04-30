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
          <input v-model="searchQuery" type="text" placeholder="제품명 검색..."
                 @input="handleSearch"
                 class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-64 bg-white shadow-sm
                   focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321] outline-none transition-colors" />
        </div>
        <div class="flex gap-1.5 flex-wrap">
          <button @click="selectedCategory = '전체'"
                  class="px-3 py-1.5 text-sm font-semibold border rounded-lg transition-colors shadow-sm cursor-pointer"
                  :class="selectedCategory === '전체'
              ? 'bg-[#F37321] text-white border-[#F37321]'
              : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
            전체
          </button>
          <button v-for="cat in categories" :key="cat.idx" @click="selectedCategory = cat.categoryName"
                  class="px-3 py-1.5 text-sm font-semibold border rounded-lg transition-colors shadow-sm cursor-pointer"
                  :class="selectedCategory === cat.categoryName
              ? 'bg-[#F37321] text-white border-[#F37321]'
              : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'">
            {{ cat.categoryName }}
          </button>
        </div>
      </div>

      <!-- 제품 테이블 -->
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <table class="w-full text-sm text-left">
          <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품 번호(ID)</th>
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
          <tr v-for="p in filteredProducts" :key="p.idx" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ p.idx }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ p.productName }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-semibold px-2 py-0.5 bg-gray-100 text-gray-600 border border-gray-200 rounded">{{ p.categoryName }}</span>
            </td>
            <td class="px-5 py-3.5 text-gray-600">{{ p.productUnit }}</td>
            <td class="px-5 py-3.5 font-medium text-gray-900">{{ p.maxStock?.toLocaleString() }}</td>
            <td class="px-5 py-3.5 font-semibold text-[#F37321]">{{ p.minStock?.toLocaleString() }}</td>
            <td class="px-5 py-3.5 font-medium text-gray-900">₩ {{ p.unitPrice?.toLocaleString() }}</td>
            <td class="px-5 py-3.5 text-xs font-mono"
                :class="p.dangerDays ? 'text-amber-600 font-semibold' : 'text-gray-400'">
              {{ p.dangerDays ? `D-${p.dangerDays}` : '-' }}
            </td>
            <td class="px-5 py-3.5">
              <div class="flex justify-center gap-2">
                <button @click="openEditModal(p)" class="px-3 py-1.5 text-xs font-semibold text-[#F37321] border border-[#F37321] rounded hover:bg-orange-50 transition-colors cursor-pointer">수정</button>
                <button @click="handleDeleteProduct(p.idx)" class="px-3 py-1.5 text-xs font-semibold text-red-500 border border-red-400 rounded hover:bg-red-50 transition-colors cursor-pointer">삭제</button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredProducts.length === 0">
            <td colspan="9" class="px-5 py-12 text-center text-gray-400 text-sm">등록된 제품이 없거나 검색 결과가 없습니다.</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 제품 등록/수정 모달 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-lg border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">{{ form.idx ? '제품 정보 수정' : '신규 제품 등록' }}</h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>
        <form @submit.prevent="handleSaveProduct" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">제품명</label>
              <input v-model="form.productName" required type="text"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">카테고리</label>
              <select v-model="form.categoryIdx" required
                      class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
                <option v-for="c in categories" :key="c.idx" :value="c.idx">{{ c.categoryName }}</option>
              </select>
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">단위</label>
              <input v-model="form.productUnit" required type="text" placeholder="ex) kg, 개"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">최대재고</label>
              <input v-model.number="form.maxStock" required type="number" min="0"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">최소재고</label>
              <input v-model.number="form.minStock" required type="number" min="0"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">단가 (원)</label>
              <input v-model.number="form.unitPrice" required type="number" min="0"
                     class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">위험 유통기한 (일)</label>
              <input v-model="form.dangerDays" type="text" placeholder="예) 7"
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
            <input v-model="newCategoryInput" type="text" placeholder="새 카테고리명 입력"
                   @keyup.enter="addCategoryAction"
                   class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            <button @click="addCategoryAction"
                    class="px-4 py-2 bg-[#F37321] text-white text-sm font-semibold rounded hover:bg-[#e0661d] cursor-pointer">추가</button>
          </div>
          <div class="border border-gray-200 rounded-lg overflow-hidden">
            <div class="px-4 py-2.5 bg-gray-50 border-b border-gray-100">
              <p class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">카테고리 목록 ({{ categories.length }}개)</p>
            </div>
            <div class="divide-y divide-gray-100 max-h-64 overflow-y-auto">
              <div v-for="cat in categories" :key="cat.idx"
                   class="flex items-center justify-between px-4 py-2.5 hover:bg-gray-50">
                <span class="text-sm font-medium text-gray-700">{{ cat.categoryName }}</span>
                <button @click="deleteCategoryAction(cat.idx, cat.categoryName)" class="text-gray-300 hover:text-red-500 transition-colors cursor-pointer">
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
import { ref, computed, onMounted } from 'vue'
import { Trash2, Search, Tag, Plus } from 'lucide-vue-next'
import { createCategory, readCategoryList, deleteCategory } from '@/api/category'
import { getProductList, addNewProduct, updateProduct, deleteProduct, searchProduct } from '@/api/product'

// --- 상태 관리 ---
const categories = ref([])
const products = ref([]) // 서버에서 받아올 제품 리스트
const selectedCategory = ref('전체')
const searchQuery = ref('')
const newCategoryInput = ref('')
const showCategoryModal = ref(false)
const showModal = ref(false)

// 제품 등록/수정용 폼 상태 (RegReq DTO 규격)
const form = ref({
  idx: null, // 수정 시에만 존재
  productName: '',
  categoryIdx: null,
  productUnit: '',
  maxStock: 0,
  minStock: 0,
  unitPrice: 0,
  dangerDays: ''
})

// --- 데이터 로딩 (Read) ---
const fetchCategories = async () => {
  try {
    const response = await readCategoryList()
    categories.value = response.data
  } catch (error) {
    console.error('카테고리 조회 실패:', error)
  }
}

const fetchProducts = async () => {
  try {
    const response = await getProductList()
    products.value = response.data
  } catch (error) {
    console.error('제품 목록 조회 실패:', error)
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})

// --- 검색 기능 (Search API 연동) ---
const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    await fetchProducts()
    return
  }
  try {
    const response = await searchProduct(searchQuery.value)
    products.value = response.data
  } catch (error) {
    console.error('제품 검색 실패:', error)
  }
}

// --- 카테고리 필터링 (클라이언트 사이드) ---
const filteredProducts = computed(() => {
  return products.value.filter(p => {
    return selectedCategory.value === '전체' || p.categoryName === selectedCategory.value
  })
})

// --- 제품 관리 (C.U.D) ---
function openAddModal() {
  form.value = {
    idx: null,
    productName: '',
    categoryIdx: categories.value.length > 0 ? categories.value[0].idx : null,
    productUnit: '',
    maxStock: 0,
    minStock: 0,
    unitPrice: 0,
    dangerDays: ''
  }
  showModal.value = true
}

function openEditModal(product) {
  // ListRes 데이터를 RegReq 규격으로 매핑
  // 카테고리 이름 대신 현재 등록된 카테고리 중 이름이 같은 것의 idx를 찾음
  const cat = categories.value.find(c => c.categoryName === product.categoryName)
  form.value = {
    idx: product.idx,
    productName: product.productName,
    categoryIdx: cat ? cat.idx : null,
    productUnit: product.productUnit,
    maxStock: product.maxStock,
    minStock: product.minStock,
    unitPrice: product.unitPrice,
    dangerDays: product.dangerDays
  }
  showModal.value = true
}

async function handleSaveProduct() {
  try {
    const dto = { ...form.value }
    delete dto.idx // 등록/수정 요청 바디에서는 idx 제외 (경로 변수로 처리되거나 자동생성)

    if (form.value.idx) {
      // 수정 (PUT)
      await updateProduct(form.value.idx, dto)
      alert('제품 정보가 수정되었습니다.')
    } else {
      // 신규 등록 (POST)
      await addNewProduct(dto)
      alert('새 제품이 등록되었습니다.')
    }
    showModal.value = false
    await fetchProducts() // 목록 새로고침
  } catch (error) {
    console.error('제품 저장 실패:', error)
    alert('저장 중 오류가 발생했습니다.')
  }
}

async function handleDeleteProduct(idx) {
  if (!confirm('이 제품을 삭제하시겠습니까?')) return
  try {
    await deleteProduct(idx)
    await fetchProducts()
  } catch (error) {
    console.error('삭제 실패:', error)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

// --- 카테고리 액션 (기존 코드 유지) ---
async function addCategoryAction() {
  const name = newCategoryInput.value.trim()
  if (!name) return
  try {
    await createCategory(name)
    newCategoryInput.value = ''
    await fetchCategories()
  } catch (error) {
    alert('카테고리 등록 실패')
  }
}

async function deleteCategoryAction(idx, name) {
  if (!confirm(`'${name}' 카테고리를 삭제하시겠습니까?`)) return
  try {
    await deleteCategory(idx)
    await fetchCategories()
    if (selectedCategory.value === name) selectedCategory.value = '전체'
  } catch (error) {
    alert('삭제 실패: 해당 카테고리를 사용하는 제품이 있을 수 있습니다.')
  }
}
</script>
