<template>
  <div class="p-5 space-y-4">

    <!-- ── 페이지 헤더 ── -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매장 메뉴 관리</h1>
        <p class="text-xs text-gray-500 mt-1">이 매장의 메뉴와 재료(BOM)를 구성합니다. 재료는 매장 제품 관리에 등록한 품목에서 선택합니다.</p>
      </div>
      <button @click="openNewMenuModal"
              class="flex items-center gap-2 px-4 py-2 bg-[#2563eb] text-white text-sm font-bold rounded-lg hover:bg-[#1d4ed8] transition-colors shadow-sm cursor-pointer">
        <Plus class="w-4 h-4" /> 신규 메뉴 등록
      </button>
    </div>

    <!-- 텍스트 검색 및 드롭다운 -->
    <div class="flex items-center gap-3 mb-4 flex-wrap">
      <div class="relative">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="메뉴명 검색..."
          class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-52
             bg-white shadow-sm
             focus:border-[#2563eb] focus:ring-1 focus:ring-[#2563eb]
             outline-none transition-colors"
        />
      </div>

      <!-- 수정된 부분: 재료(제품명) 선택 드롭다운 -->
      <div class="relative w-64">
        <select
          v-model="selectedProductId"
          class="w-full pl-4 pr-10 py-2 bg-white border border-gray-200 rounded-lg text-sm appearance-none outline-none focus:border-[#2563eb] focus:ring-1 focus:ring-[#2563eb] transition-colors shadow-sm cursor-pointer text-gray-600"
        >
          <option value="">전체 제품 보기</option>
          <option v-for="product in products" :key="product.id" :value="product.id">
            {{ product.name }}
          </option>
        </select>
        <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-gray-400">
          <ChevronDown class="w-4 h-4" />
        </div>
      </div>
    </div>

    <!-- ── 검색 ── -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">메뉴번호</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">메뉴명</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가격</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">재료 수</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
          <tr v-for="menu in filteredMenus" :key="menu.id"
              @click="openIngredientModal(menu)"
              class="hover:bg-gray-50/50 transition-colors cursor-pointer group">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ menu.id }}</td>
            <td class="px-5 py-3.5 font-bold text-gray-900 group-hover:text-[#2563eb] transition-colors">{{ menu.name }}</td>
            <td class="px-5 py-3.5 text-gray-700 font-semibold">{{ formatPrice(menu.price) }}</td>
            <td class="px-5 py-3.5 text-center">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full bg-blue-50 text-blue-600">
                  {{ menu.ingredients.length }}종
                </span>
            </td>
            <td class="px-5 py-3.5" @click.stop>
              <div class="flex justify-center gap-2">
                <button @click="openEditMenuModal(menu)"
                        class="px-3 py-1.5 text-xs font-semibold text-[#2563eb] border border-[#2563eb] rounded hover:bg-blue-50 transition-colors cursor-pointer">
                  수정
                </button>
                <button @click="openDeleteConfirm(menu)"
                        class="px-3 py-1.5 text-xs font-semibold text-red-500 border border-red-400 rounded hover:bg-red-50 transition-colors cursor-pointer">
                  삭제
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredMenus.length === 0">
            <td colspan="5" class="px-5 py-12 text-center text-gray-400 text-sm">검색 결과가 없습니다.</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ══════════════════════════════════════════
         신규 메뉴 등록 / 수정 모달
    ══════════════════════════════════════════ -->
    <div v-if="showMenuModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 " @click="showMenuModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-xl border border-gray-200 shadow-xl overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200">

        <!-- 모달 헤더 -->
        <div class="px-7 py-5 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h3 class="font-bold text-gray-900 text-lg">{{ editTarget ? '메뉴 수정' : '신규 메뉴 등록' }}</h3>
          <button @click="showMenuModal = false" class="text-gray-400 hover:text-gray-600 font-bold text-xl cursor-pointer">✕</button>
        </div>

        <form @submit.prevent="saveMenu" class="p-7 space-y-5">

          <!-- 메뉴명 / 가격 -->
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">메뉴명</label>
              <input v-model="menuForm.name" required type="text" placeholder="예: 삼겹살 세트"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb]/5 transition-all" />
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">가격 (원)</label>
              <input :value="formattedPriceInput"
                     @input="onPriceInput"
                     required
                     type="text"
                     placeholder="0"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb]/5 transition-all text-right" />
            </div>
          </div>

          <!-- 카테고리 -->
          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">카테고리</label>
            <div class="flex items-center gap-2 flex-wrap">
              <button v-for="cat in categories.filter(c => c !== '전체')" :key="cat"
                      type="button"
                      @click="menuForm.category = cat"
                      :class="menuForm.category === cat
                        ? 'bg-[#2563eb] text-white border-[#2563eb]'
                        : 'bg-white text-gray-500 border-gray-200 hover:border-gray-300 hover:bg-gray-50'"
                      class="px-3 py-1.5 rounded-lg border text-xs font-semibold transition-all cursor-pointer">
                {{ cat }}
              </button>
            </div>
          </div>

          <!-- 이미지 업로드 -->
          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">메뉴 이미지</label>
            <label class="cursor-pointer block">
              <div class="w-full px-4 py-3 rounded-lg border-2 border-dashed border-gray-200 text-sm text-gray-400 bg-gray-50 hover:bg-gray-100 hover:border-[#2563eb]/40 transition-all flex items-center justify-between">
                <span>{{ menuForm.imageName || '이미지를 업로드하세요' }}</span>
                <ImageIcon class="w-4 h-4 flex-shrink-0" />
              </div>
              <input type="file" class="hidden" @change="handleImageChange" accept="image/*" />
            </label>
          </div>

          <div class="border-t border-gray-100 pt-5">
            <!-- 재료 헤더 -->
            <div class="flex items-center justify-between mb-3">
              <span class="text-sm font-bold text-gray-700">재료 등록</span>
              <button type="button" @click="addIngredientRow"
                      class="flex items-center gap-1 text-xs font-bold text-[#2563eb] hover:text-[#1d4ed8] transition-colors cursor-pointer">
                <Plus class="w-3.5 h-3.5" /> 재료 추가
              </button>
            </div>

            <!-- 재료 컬럼 레이블 -->
            <div class="grid grid-cols-[2fr_1fr_1fr_32px] gap-2 mb-2 px-0.5">
              <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">상품명</span>
              <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">소요량</span>
              <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">단위</span>
              <span></span>
            </div>

            <!-- 재료 행 목록 -->
            <div class="space-y-2">
              <div v-for="(item, idx) in menuForm.ingredients" :key="idx"
                   class="grid grid-cols-[2fr_1fr_1fr_32px] gap-2 items-center">
                <select v-model="item.productId"
                        class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb]/5 transition-all">
                  <option value="">상품 선택</option>
                  <option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}</option>
                </select>
                <input v-model.number="item.amount" type="number" placeholder="0" min="0" step="any"
                       class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb]/5 transition-all" />
                <select v-model="item.unit"
                        class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb]/5 transition-all">
                  <option value="">단위 선택</option>
                  <option>kg</option>
                  <option>g</option>
                  <option>L</option>
                  <option>ml</option>
                  <option>개</option>
                  <option>봉</option>
                  <option>박스</option>
                  <option>묶음</option>
                </select>
                <button type="button" @click="removeIngredientRow(idx)"
                        class="w-8 h-8 flex items-center justify-center rounded-lg bg-red-50 text-red-400 hover:bg-red-100 hover:text-red-600 transition-colors text-sm font-bold cursor-pointer">
                  ✕
                </button>
              </div>
              <p v-if="menuForm.ingredients.length === 0" class="text-xs text-gray-300 py-2 text-center">
                재료를 추가해주세요
              </p>
            </div>
          </div>

          <!-- 버튼 -->
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showMenuModal = false"
                    class="flex-1 py-3 rounded-lg border border-gray-200 text-sm font-bold text-gray-500 hover:bg-gray-50 transition-colors cursor-pointer">
              취소
            </button>
            <button type="submit"
                    class="flex-1 py-3 rounded-lg bg-[#2563eb] text-white text-sm font-bold hover:bg-[#1d4ed8] transition-colors shadow-sm cursor-pointer">
              {{ editTarget ? '수정 저장' : '등록하기' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ══════════════════════════════════════════
         재료 목록 상세 모달
    ══════════════════════════════════════════ -->
    <div v-if="showIngredientModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 " @click="showIngredientModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-lg border border-gray-200 shadow-xl overflow-hidden max-h-[85vh] flex flex-col animate-in fade-in slide-in-from-bottom-4 duration-200">

        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <div>
            <h3 class="font-bold text-gray-900">{{ selectedMenu?.name }}</h3>
            <p class="text-xs text-gray-400 font-mono mt-0.5">{{ selectedMenu?.id }}</p>
          </div>
          <button @click="showIngredientModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <div class="p-6 overflow-y-auto flex-1">
          <table class="w-full text-sm text-left">
            <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-4 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">재료번호</th>
              <th class="px-4 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품명</th>
              <th class="px-4 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-right">소요량</th>
              <th class="px-4 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">단위</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
            <tr v-for="(item, idx) in selectedMenu?.ingredients" :key="idx"
                class="hover:bg-gray-50/50 transition-colors">
              <td class="px-4 py-3 font-mono text-xs text-gray-400">R-{{ String(idx + 1).padStart(3, '0') }}</td>
              <td class="px-4 py-3 font-semibold text-gray-800">{{ getProductName(item.productId) }}</td>
              <td class="px-4 py-3 text-right text-gray-700 font-mono">{{ item.amount }}</td>
              <td class="px-4 py-3 text-gray-500">{{ item.unit }}</td>
            </tr>
            <tr v-if="!selectedMenu?.ingredients?.length">
              <td colspan="4" class="px-4 py-8 text-center text-gray-400 text-sm">등록된 재료가 없습니다.</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end">
          <button @click="showIngredientModal = false"
                  class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer">
            닫기
          </button>
        </div>
      </div>
    </div>

    <!-- ══════════════════════════════════════════
         삭제 확인 모달
    ══════════════════════════════════════════ -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 " @click="showDeleteConfirm = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-sm border border-gray-200 shadow-xl p-8 text-center animate-in fade-in zoom-in-95 duration-200">
        <div class="text-4xl mb-4">🗑️</div>
        <h3 class="font-bold text-gray-900 text-base mb-2">메뉴를 삭제하시겠습니까?</h3>
        <p class="text-xs text-gray-400 mb-6">이 작업은 되돌릴 수 없습니다.</p>
        <div class="flex gap-3">
          <button @click="showDeleteConfirm = false"
                  class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-bold text-gray-500 hover:bg-gray-50 transition-colors cursor-pointer">
            취소
          </button>
          <button @click="confirmDelete"
                  class="flex-1 py-2.5 rounded-lg bg-red-500 text-white text-sm font-bold hover:bg-red-600 transition-colors cursor-pointer">
            삭제
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Trash2, Search, Image as ImageIcon, ChevronDown } from 'lucide-vue-next'

// ─────────────────────────────────────────────
//  상품 목록 (매장 제품 관리의 제품코드와 동일하게 맞춤)
// ─────────────────────────────────────────────
const products = ref([
  { id: 'P100', name: '한우 등심' },
  { id: 'P101', name: '한우 안심' },
  { id: 'P102', name: '연어' },
  { id: 'P200', name: '간장' },
  { id: 'P201', name: '고추장' },
  { id: 'P202', name: '올리브오일' },
  { id: 'P300', name: '양파' },
  { id: 'P301', name: '마늘' },
  { id: 'P400', name: '버터' },
  { id: 'P401', name: '생크림' },
])

// ─────────────────────────────────────────────
//  메뉴 데이터
// ─────────────────────────────────────────────
const menus = ref([
  {
    id: 'M-001', name: '한우 등심 오마카세 코스', price: 180000, imageName: '', category: '한우',
    ingredients: [
      { productId: 'P100', amount: 0.3, unit: 'kg' },
      { productId: 'P301', amount: 20, unit: 'g' },
      { productId: 'P200', amount: 50, unit: 'ml' },
      { productId: 'P202', amount: 30, unit: 'ml' },
    ]
  },
  {
    id: 'M-002', name: '한우 안심 스테이크', price: 120000, imageName: '', category: '한우',
    ingredients: [
      { productId: 'P101', amount: 0.25, unit: 'kg' },
      { productId: 'P400', amount: 30, unit: 'g' },
      { productId: 'P300', amount: 50, unit: 'g' },
    ]
  },
  {
    id: 'M-003', name: '연어 스시 플래터', price: 65000, imageName: '', category: '해산물',
    ingredients: [
      { productId: 'P102', amount: 0.2, unit: 'kg' },
      { productId: 'P200', amount: 30, unit: 'ml' },
      { productId: 'P301', amount: 5, unit: 'g' },
    ]
  },
])

// ─────────────────────────────────────────────
//  검색 및 필터링
// ─────────────────────────────────────────────
const searchQuery = ref('')
const selectedProductId = ref('') // 제품 드롭다운 필터용 상태
const categories = ['전체', '육류', '음료', '채소', '소스', '기타']

const filteredMenus = computed(() => {
  let list = menus.value

  // 제품 드롭다운 필터: 선택한 제품이 재료(ingredients)에 포함되어 있는지 확인
  if (selectedProductId.value) {
    list = list.filter(m => m.ingredients.some(ing => ing.productId === selectedProductId.value))
  }

  // 텍스트 검색 (메뉴명)
  const q = searchQuery.value.trim().toLowerCase()
  if (q) {
    list = list.filter(m => m.name.toLowerCase().includes(q))
  }
  return list
})

// ─────────────────────────────────────────────
//  메뉴 등록 / 수정 모달
// ─────────────────────────────────────────────
const showMenuModal = ref(false)
const editTarget = ref(null)
const menuForm = ref({ name: '', price: 0, imageName: '', category: '전체', ingredients: [] })
const formattedPriceInput = ref('')

function openNewMenuModal() {
  editTarget.value = null
  menuForm.value = { name: '', price: 0, imageName: '', category: '육류', ingredients: [] }
  formattedPriceInput.value = ''
  showMenuModal.value = true
}

function openEditMenuModal(menu) {
  editTarget.value = menu
  menuForm.value = {
    name: menu.name,
    price: menu.price,
    imageName: menu.imageName,
    category: menu.category,
    ingredients: menu.ingredients.map(i => ({ ...i })),
  }
  formattedPriceInput.value = menu.price.toLocaleString('ko-KR')
  showMenuModal.value = true
}

const onPriceInput = (e) => {
  const val = e.target.value.replace(/[^0-9]/g, '');
  menuForm.value.price = val ? parseInt(val, 10) : 0;
  e.target.value = val ? menuForm.value.price.toLocaleString('ko-KR') : '';
};

function addIngredientRow() {
  menuForm.value.ingredients.push({ productId: '', amount: 0, unit: '' })
}

function removeIngredientRow(idx) {
  menuForm.value.ingredients.splice(idx, 1)
}

function handleImageChange(e) {
  const file = e.target.files[0]
  if (file) menuForm.value.imageName = file.name
}

function saveMenu() {
  if (editTarget.value) {
    // 수정
    Object.assign(editTarget.value, {
      name: menuForm.value.name,
      price: menuForm.value.price,
      imageName: menuForm.value.imageName,
      category: menuForm.value.category,
      ingredients: menuForm.value.ingredients.map(i => ({ ...i })),
    })
  } else {
    // 가장 큰 숫자 ID를 찾아 +1 하기 (ID 충돌 방지)
    const maxIdNum = menus.value.reduce((max, menu) => {
      const idNum = parseInt(menu.id.split('-')[1]);
      return idNum > max ? idNum : max;
    }, 0);
    const newId = `M-${String(maxIdNum + 1).padStart(3, '0')}`;
    menus.value.push({
      id: newId,
      name: menuForm.value.name,
      price: menuForm.value.price,
      imageName: menuForm.value.imageName,
      category: menuForm.value.category,
      ingredients: menuForm.value.ingredients.map(i => ({ ...i })),
    })
  }
  showMenuModal.value = false
}

// ─────────────────────────────────────────────
//  재료 목록 모달
// ─────────────────────────────────────────────
const showIngredientModal = ref(false)
const selectedMenu = ref(null)

function openIngredientModal(menu) {
  selectedMenu.value = menu
  showIngredientModal.value = true
}

function getProductName(productId) {
  return products.value.find(p => p.id === productId)?.name ?? productId
}

function editIngredient(idx) {
  // 재료 상세 모달을 닫고 수정 모달로 이동
  showIngredientModal.value = false
  openEditMenuModal(selectedMenu.value)
}

function deleteIngredient(idx) {
  if (selectedMenu.value) {
    selectedMenu.value.ingredients.splice(idx, 1)
  }
}

// ─────────────────────────────────────────────
//  삭제 확인 모달
// ─────────────────────────────────────────────
const showDeleteConfirm = ref(false)
const deleteTarget = ref(null)

function openDeleteConfirm(menu) {
  deleteTarget.value = menu
  showDeleteConfirm.value = true
}

function confirmDelete() {
  if (deleteTarget.value) {
    menus.value = menus.value.filter(m => m.id !== deleteTarget.value.id)
  }
  showDeleteConfirm.value = false
  deleteTarget.value = null
}

// ─────────────────────────────────────────────
//  유틸
// ─────────────────────────────────────────────
function formatPrice(price) {
  return '₩ ' + price.toLocaleString('ko-KR')
}
</script>
