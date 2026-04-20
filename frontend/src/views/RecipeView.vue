<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">메뉴 관리</h1>
        <p class="page-spec-hint">
          <code>RECIPE_001~005</code>메뉴·재료(원자재명·소요량·단위) 등록·수정·삭제, 메뉴명·재료명 검색 및 재료 수 표시를 지원합니다.
        </p>
      </div>
      <button @click="showNewMenu = true"
        class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded hover:bg-[#e0661d] transition-colors flex items-center gap-2">
        <Plus class="w-4 h-4" /> 신규 메뉴 등록
      </button>
    </div>

    <!-- 신규 메뉴 등록 Modal (RECIPE_001) -->
    <div v-if="showNewMenu" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showNewMenu = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-md border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">신규 메뉴 등록</h3>
          <button @click="showNewMenu = false" class="text-gray-400 hover:text-gray-600">✕</button>
        </div>
        <form @submit.prevent="createMenu" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">상품명</label>
              <input v-model="newMenuForm.name" required type="text" placeholder="예) 카페모카"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">카테고리</label>
              <select v-model="newMenuForm.category"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
                <option>커피</option>
                <option>음료</option>
                <option>디저트</option>
                <option>기타</option>
              </select>
            </div>
          </div>
          <div class="space-y-1.5">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">판매 가격 (원)</label>
            <input v-model.number="newMenuForm.price" type="number" min="0" placeholder="0"
              class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showNewMenu = false"
              class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">취소</button>
            <button type="submit"
              class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d]">등록</button>
          </div>
        </form>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-5">
      <!-- Left: 상품 목록 -->
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="px-5 py-3 border-b border-gray-200 bg-gray-50/60 space-y-2">
          <p class="text-xs font-bold text-gray-500 uppercase tracking-wider">판매 상품 목록</p>
          <!-- 메뉴 검색 (RECIPE_004) -->
          <div class="relative">
            <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
            <input v-model="searchQuery" type="text" placeholder="메뉴명·재료명 검색..."
              class="w-full pl-8 pr-3 py-1.5 rounded border border-gray-200 text-xs focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]/10 outline-none" />
          </div>
        </div>
        <div class="divide-y divide-gray-100">
          <div v-for="menu in filteredMenus" :key="menu.id"
            @click="selectMenu(menu)"
            class="px-5 py-3.5 cursor-pointer transition-colors flex justify-between items-center relative"
            :class="selectedMenu?.id === menu.id
              ? 'bg-orange-50/70 text-[#F37321]'
              : 'hover:bg-gray-50 text-gray-700'">
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold truncate">{{ menu.name }}</p>
              <p class="text-xs text-gray-400 mt-0.5">{{ menu.category }}</p>
            </div>
            <div class="flex items-center gap-2 shrink-0">
              <span class="text-xs font-bold"
                :class="selectedMenu?.id === menu.id ? 'text-[#F37321]' : 'text-gray-400'">
                {{ menu.ingredients.length }}종
              </span>
              <!-- 메뉴 삭제 (RECIPE_005) -->
              <button @click.stop="deleteMenu(menu.id)"
                class="text-gray-200 hover:text-red-400 transition-colors">
                <Trash2 class="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: 메뉴 상세 -->
      <div class="col-span-2 space-y-4">
        <div v-if="!selectedMenu" class="bg-white border border-gray-200 flex items-center justify-center h-64">
          <div class="text-center text-gray-400">
            <BookOpen class="w-10 h-10 mx-auto mb-3 opacity-20" />
            <p class="text-sm">좌측에서 판매 상품을 선택하세요</p>
          </div>
        </div>

        <template v-else>
          <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
            <div class="px-5 py-3 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
              <div>
                <h3 class="font-bold text-gray-900 text-sm">{{ selectedMenu.name }}</h3>
                <p class="text-xs text-gray-400 mt-0.5">{{ selectedMenu.category }}</p>
              </div>
              <button @click="addIngredient"
                class="text-xs px-3 py-1.5 bg-orange-50 text-[#F37321] border border-orange-200 font-semibold hover:bg-orange-100 flex items-center gap-1.5 transition-colors">
                <Plus class="w-3.5 h-3.5" /> 재료 추가
              </button>
            </div>

            <table class="w-full text-sm">
              <thead>
                <tr class="border-b border-gray-100 bg-gray-50">
                  <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">원자재명</th>
                  <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">제품코드</th>
                  <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">소요량</th>
                  <th class="px-5 py-2.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">단위</th>
                  <th class="px-5 py-2.5 text-center text-[10px] font-bold text-gray-400 uppercase tracking-wider">삭제</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-for="(ing, idx) in editingIngredients" :key="idx" class="hover:bg-gray-50/50">
                  <td class="px-5 py-2.5">
                    <input v-model="ing.name" type="text"
                      class="w-full px-2 py-1 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]/10 outline-none" />
                  </td>
                  <td class="px-5 py-2.5">
                    <input v-model="ing.code" type="text"
                      class="w-24 px-2 py-1 rounded border border-gray-200 text-sm font-mono focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]/10 outline-none" />
                  </td>
                  <td class="px-5 py-2.5">
                    <input v-model.number="ing.amount" type="number" min="0" step="0.1"
                      class="w-20 px-2 py-1 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]/10 outline-none" />
                  </td>
                  <td class="px-5 py-2.5">
                    <input v-model="ing.unit" type="text"
                      class="w-16 px-2 py-1 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]/10 outline-none" />
                  </td>
                  <td class="px-5 py-2.5 text-center">
                    <button @click="removeIngredient(idx)" class="text-gray-300 hover:text-red-500 transition-colors">
                      <X class="w-4 h-4" />
                    </button>
                  </td>
                </tr>
                <tr v-if="editingIngredients.length === 0">
                  <td colspan="5" class="px-5 py-8 text-center text-gray-400 text-sm">
                    등록된 재료가 없습니다. 재료 추가 버튼을 눌러주세요.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="flex justify-end gap-3">
            <button @click="cancelEdit"
              class="px-4 py-2 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">
              취소
            </button>
            <button @click="saveRecipe"
              class="px-4 py-2 bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d]">
              메뉴 저장
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Plus, X, BookOpen, Search, Trash2 } from 'lucide-vue-next'

const menuItems = ref([
  {
    id: 'M001', name: '아메리카노', category: '커피',
    ingredients: [
      { name: '에스프레소 원두', code: 'P101', amount: 0.018, unit: 'kg' },
      { name: '종이컵(M)',       code: 'P400', amount: 1,     unit: '개' },
    ],
  },
  {
    id: 'M002', name: '카페라떼', category: '커피',
    ingredients: [
      { name: '에스프레소 원두', code: 'P101', amount: 0.018, unit: 'kg' },
      { name: '우유(1L)',        code: 'P200', amount: 0.2,   unit: 'L'  },
      { name: '종이컵(L)',       code: 'P401', amount: 1,     unit: '개' },
    ],
  },
  {
    id: 'M003', name: '바닐라 라떼', category: '커피',
    ingredients: [
      { name: '에스프레소 원두', code: 'P101', amount: 0.018, unit: 'kg' },
      { name: '우유(1L)',        code: 'P200', amount: 0.2,   unit: 'L'  },
      { name: '바닐라 시럽',    code: 'P300', amount: 0.03,  unit: 'L'  },
      { name: '종이컵(L)',       code: 'P401', amount: 1,     unit: '개' },
    ],
  },
  {
    id: 'M004', name: '드립 커피', category: '커피',
    ingredients: [
      { name: '프리미엄 원두', code: 'P100', amount: 0.015, unit: 'kg' },
      { name: '종이컵(M)',     code: 'P400', amount: 1,     unit: '개' },
    ],
  },
])

// 신규 메뉴 등록 (RECIPE_001)
const showNewMenu = ref(false)
const newMenuForm = ref({ name: '', category: '커피', price: 0 })

function createMenu() {
  const id = 'M' + String(menuItems.value.length + 1).padStart(3, '0')
  menuItems.value.push({ id, name: newMenuForm.value.name, category: newMenuForm.value.category, price: newMenuForm.value.price, ingredients: [] })
  newMenuForm.value = { name: '', category: '커피', price: 0 }
  showNewMenu.value = false
}

const searchQuery = ref('')

const filteredMenus = computed(() => {
  const q = searchQuery.value.trim()
  if (!q) return menuItems.value
  return menuItems.value.filter(
    (m) =>
      m.name.includes(q) ||
      m.category.includes(q) ||
      m.ingredients.some((ing) => ing.name.includes(q) || (ing.code && ing.code.includes(q))),
  )
})

const selectedMenu = ref(null)
const editingIngredients = ref([])

function selectMenu(menu) {
  selectedMenu.value = menu
  editingIngredients.value = menu.ingredients.map(i => ({ ...i }))
}

function addIngredient() {
  editingIngredients.value.push({ name: '', code: '', amount: 0, unit: '' })
}

function removeIngredient(idx) {
  editingIngredients.value.splice(idx, 1)
}

function saveRecipe() {
  selectedMenu.value.ingredients = editingIngredients.value.map(i => ({ ...i }))
  alert(`'${selectedMenu.value.name}' 메뉴가 저장되었습니다.`)
}

function cancelEdit() {
  if (selectedMenu.value) {
    editingIngredients.value = selectedMenu.value.ingredients.map(i => ({ ...i }))
  }
}

function deleteMenu(id) {
  if (!confirm('해당 상품의 메뉴를 삭제하시겠습니까?')) return
  menuItems.value = menuItems.value.filter(m => m.id !== id)
  if (selectedMenu.value?.id === id) {
    selectedMenu.value = null
    editingIngredients.value = []
  }
}
</script>
