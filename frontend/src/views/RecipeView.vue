<template>
  <div class="p-6 space-y-4 font-sans text-gray-900">

    <!-- ── 페이지 헤더 ── -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-black text-gray-900">메뉴 관리</h1>
        <p class="text-xs text-gray-400 mt-1">메뉴 및 레시피를 관리합니다</p>
      </div>
      <button @click="openNewMenuModal"
              class="flex items-center gap-2 px-4 py-2 bg-[#F97316] text-white text-sm font-bold rounded-lg hover:bg-[#EA6700] transition-colors shadow-sm">
        <Plus class="w-4 h-4" /> 신규 메뉴 등록
      </button>
    </div>

    <!-- ── 검색 ── -->
    <div class="bg-white border border-gray-200 rounded-xl shadow-sm p-5">
      <div class="flex items-center gap-2 mb-4">
        <div class="relative flex-1 max-w-xs">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-300" />
          <input v-model="searchQuery" type="text" placeholder="메뉴명 검색..."
                 class="w-full pl-9 pr-4 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#F97316] focus:ring-4 focus:ring-[#F97316]/5 transition-all placeholder:text-gray-300" />
        </div>
      </div>

      <!-- ── 메뉴 테이블 ── -->
      <div class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead>
          <tr class="border-b border-gray-100 bg-gray-50/60">
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">메뉴번호</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">메뉴명</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가격</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">재료 수</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
          <tr v-for="menu in filteredMenus" :key="menu.id"
              @click="openIngredientModal(menu)"
              class="hover:bg-gray-50/80 transition-colors cursor-pointer group">
            <td class="px-4 py-4 font-mono text-xs text-gray-400">{{ menu.id }}</td>
            <td class="px-4 py-4 font-bold text-gray-900 group-hover:text-[#F97316] transition-colors">{{ menu.name }}</td>
            <td class="px-4 py-4 text-gray-700 font-semibold">{{ formatPrice(menu.price) }}</td>
            <td class="px-4 py-4 text-center">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full bg-orange-50 text-orange-600">
                  {{ menu.ingredients.length }}종
                </span>
            </td>
            <td class="px-4 py-4" @click.stop>
              <div class="flex justify-center gap-2">
                <button @click="openEditMenuModal(menu)"
                        class="text-gray-300 hover:text-[#F97316] transition-colors" title="수정">
                  <Pencil class="w-4 h-4" />
                </button>
                <button @click="openDeleteConfirm(menu)"
                        class="text-gray-300 hover:text-red-500 transition-colors" title="삭제">
                  <Trash2 class="w-4 h-4" />
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredMenus.length === 0">
            <td colspan="5" class="px-4 py-12 text-center text-gray-300 text-sm">검색 결과가 없습니다</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ══════════════════════════════════════════
         신규 메뉴 등록 / 수정 모달
    ══════════════════════════════════════════ -->
    <div v-if="showMenuModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showMenuModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-xl border border-gray-200 shadow-xl overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200">

        <!-- 모달 헤더 -->
        <div class="px-7 py-5 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h3 class="font-bold text-gray-900 text-lg">{{ editTarget ? '메뉴 수정' : '신규 메뉴 등록' }}</h3>
          <button @click="showMenuModal = false" class="text-gray-400 hover:text-gray-600 font-bold text-xl">✕</button>
        </div>

        <form @submit.prevent="saveMenu" class="p-7 space-y-5">

          <!-- 메뉴명 / 가격 -->
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">메뉴명</label>
              <input v-model="menuForm.name" required type="text" placeholder="예: 삼겹살 세트"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#F97316] focus:ring-4 focus:ring-[#F97316]/5 transition-all" />
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">가격 (원)</label>
              <!-- input 요소를 직접 참조하기 위해 v-on:input 수정 -->
              <input :value="formattedPriceInput"
                     @input="onPriceInput"
                     required
                     type="text"
                     placeholder="0"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#F97316] focus:ring-4 focus:ring-[#F97316]/5 transition-all text-right" />
            </div>
          </div>

          <!-- 이미지 업로드 -->
          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">메뉴 이미지</label>
            <label class="cursor-pointer block">
              <div class="w-full px-4 py-3 rounded-lg border-2 border-dashed border-gray-200 text-sm text-gray-400 bg-gray-50 hover:bg-gray-100 hover:border-[#F97316]/40 transition-all flex items-center justify-between">
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
                      class="flex items-center gap-1 text-xs font-bold text-[#F97316] hover:text-[#EA6700] transition-colors">
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
                        class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#F97316] focus:ring-4 focus:ring-[#F97316]/5 transition-all">
                  <option value="">상품 선택</option>
                  <option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}</option>
                </select>
                <input v-model.number="item.amount" type="number" placeholder="0" min="0"
                       class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#F97316] focus:ring-4 focus:ring-[#F97316]/5 transition-all" />
                <select v-model="item.unit"
                        class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm outline-none focus:border-[#F97316] focus:ring-4 focus:ring-[#F97316]/5 transition-all">
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
                        class="w-8 h-8 flex items-center justify-center rounded-lg bg-red-50 text-red-400 hover:bg-red-100 hover:text-red-600 transition-colors text-sm font-bold">
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
                    class="flex-1 py-3 rounded-lg border border-gray-200 text-sm font-bold text-gray-500 hover:bg-gray-50 transition-colors">
              취소
            </button>
            <button type="submit"
                    class="flex-1 py-3 rounded-lg bg-[#F97316] text-white text-sm font-bold hover:bg-[#EA6700] transition-colors shadow-sm">
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
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showIngredientModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-lg border border-gray-200 shadow-xl overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200">

        <div class="px-7 py-5 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <div>
            <h3 class="font-bold text-gray-900 text-lg">{{ selectedMenu?.name }}</h3>
            <p class="text-xs text-gray-400 mt-0.5">재료 목록</p>
          </div>
          <button @click="showIngredientModal = false" class="text-gray-400 hover:text-gray-600 font-bold text-xl">✕</button>
        </div>

        <div class="p-7">
          <table class="w-full text-sm text-left">
            <thead>
            <tr class="border-b border-gray-100 bg-gray-50/60">
              <th class="px-3 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">재료번호</th>
              <th class="px-3 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품명</th>
              <th class="px-3 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-right">소요량</th>
              <th class="px-3 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">단위</th>
              <th class="px-3 py-2.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
            <tr v-for="(item, idx) in selectedMenu?.ingredients" :key="idx"
                class="hover:bg-gray-50/80 transition-colors">
              <td class="px-3 py-3 font-mono text-xs text-gray-400">R-{{ String(idx + 1).padStart(3, '0') }}</td>
              <td class="px-3 py-3 font-semibold text-gray-800">{{ getProductName(item.productId) }}</td>
              <td class="px-3 py-3 text-right text-gray-700 font-mono">{{ item.amount }}</td>
              <td class="px-3 py-3 text-gray-500">{{ item.unit }}</td>
              <td class="px-3 py-3">
                <div class="flex justify-center gap-2">
                  <button @click="editIngredient(idx)"
                          class="text-xs font-bold px-2 py-1 rounded border border-gray-200 text-gray-500 hover:border-[#F97316] hover:text-[#F97316] transition-colors">
                    수정
                  </button>
                  <button @click="deleteIngredient(idx)"
                          class="text-xs font-bold px-2 py-1 rounded border border-red-100 text-red-400 hover:bg-red-50 transition-colors">
                    삭제
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!selectedMenu?.ingredients?.length">
              <td colspan="5" class="px-3 py-8 text-center text-gray-300 text-sm">등록된 재료가 없습니다</td>
            </tr>
            </tbody>
          </table>

          <div class="mt-5 flex justify-end">
            <button @click="showIngredientModal = false"
                    class="px-5 py-2.5 rounded-lg bg-gray-100 text-gray-600 text-sm font-bold hover:bg-gray-200 transition-colors">
              닫기
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ══════════════════════════════════════════
         삭제 확인 모달
    ══════════════════════════════════════════ -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showDeleteConfirm = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-sm border border-gray-200 shadow-xl p-8 text-center animate-in fade-in zoom-in-95 duration-200">
        <div class="text-4xl mb-4">🗑️</div>
        <h3 class="font-bold text-gray-900 text-base mb-2">메뉴를 삭제하시겠습니까?</h3>
        <p class="text-xs text-gray-400 mb-6">이 작업은 되돌릴 수 없습니다.</p>
        <div class="flex gap-3">
          <button @click="showDeleteConfirm = false"
                  class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-bold text-gray-500 hover:bg-gray-50 transition-colors">
            취소
          </button>
          <button @click="confirmDelete"
                  class="flex-1 py-2.5 rounded-lg bg-red-500 text-white text-sm font-bold hover:bg-red-600 transition-colors">
            삭제
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Pencil, Trash2, Search, Image as ImageIcon } from 'lucide-vue-next'

// ─────────────────────────────────────────────
//  상품 목록 (제품 관리에서 연동 가정)
// ─────────────────────────────────────────────
const products = ref([
  { id: 'P-001', name: '삼겹살 1kg' },
  { id: 'P-002', name: '소불고기 500g' },
  { id: 'P-003', name: '음료수 500ml' },
  { id: 'P-004', name: '쌈장 1kg' },
  { id: 'P-005', name: '상추 1봉' },
])

// ─────────────────────────────────────────────
//  메뉴 데이터
// ─────────────────────────────────────────────
const menus = ref([
  {
    id: 'M-001', name: '삼겹살 세트', price: 19900, imageName: '',
    ingredients: [
      { productId: 'P-001', amount: 300, unit: 'g' },
      { productId: 'P-005', amount: 0.5, unit: '봉' },
      { productId: 'P-004', amount: 50, unit: 'g' },
      { productId: 'P-003', amount: 2, unit: '개' },
    ]
  },
  {
    id: 'M-002', name: '불고기 정식', price: 14900, imageName: '',
    ingredients: [
      { productId: 'P-002', amount: 200, unit: 'g' },
      { productId: 'P-005', amount: 0.3, unit: '봉' },
      { productId: 'P-004', amount: 30, unit: 'g' },
      { productId: 'P-003', amount: 1, unit: '개' },
    ]
  },
  {
    id: 'M-003', name: '냉면 세트', price: 12900, imageName: '',
    ingredients: [
      { productId: 'P-003', amount: 2, unit: '개' },
      { productId: 'P-005', amount: 0.2, unit: '봉' },
    ]
  },
])

// ─────────────────────────────────────────────
//  검색
// ─────────────────────────────────────────────
const searchQuery = ref('')

const filteredMenus = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return menus.value
  return menus.value.filter(m => m.name.toLowerCase().includes(q))
})

// ─────────────────────────────────────────────
//  메뉴 등록 / 수정 모달
// ─────────────────────────────────────────────
const showMenuModal = ref(false)
const editTarget = ref(null)
const menuForm = ref({ name: '', price: 0, imageName: '', ingredients: [] })

function openNewMenuModal() {
  editTarget.value = null
  menuForm.value = { name: '', price: 0, imageName: '', ingredients: [] }
  showMenuModal.value = true
}

function openEditMenuModal(menu) {
  editTarget.value = menu
  menuForm.value = {
    name: menu.name,
    price: menu.price,
    imageName: menu.imageName,
    ingredients: menu.ingredients.map(i => ({ ...i })),
  }
  showMenuModal.value = true
}

const onPriceInput = (e) => {
  // 1. 입력된 값에서 숫자가 아닌 모든 것을 제거합니다.
  const val = e.target.value.replace(/[^0-9]/g, '');

  // 2. 숫자로 변환하여 데이터 모델을 업데이트합니다.
  menuForm.value.price = val ? parseInt(val, 10) : 0;

  // 3. (중요) 실제 input 엘리먼트의 값도 즉시 강제로 업데이트하여
  // 한글 등 숫자가 아닌 문자가 잔상으로 남지 않게 합니다.
  e.target.value = menuForm.value.price === 0 ? '' : menuForm.value.price.toLocaleString('ko-KR');
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
      ingredients: menuForm.value.ingredients.map(i => ({ ...i })),
    })
  } else {
    // 신규 등록
    const newId = 'M-' + String(menus.value.length + 1).padStart(3, '0')
    menus.value.push({
      id: newId,
      name: menuForm.value.name,
      price: menuForm.value.price,
      imageName: menuForm.value.imageName,
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
