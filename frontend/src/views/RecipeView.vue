<template>
  <div class="p-5 space-y-4">

    <!-- ── 페이지 헤더 ── -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">메뉴 관리</h1>
      </div>
    </div>

    <!-- 텍스트 검색 및 매장 드롭다운 -->
    <div class="flex items-center gap-3 mb-4 flex-wrap">
      <div class="relative">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="메뉴명 또는 매장명 검색..."
          class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-64
             bg-white shadow-sm
             focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]
             outline-none transition-colors"
        />
      </div>

      <div class="relative w-52">
        <select
          v-model="selectedStoreId"
          class="w-full pl-4 pr-10 py-2 bg-white border border-gray-200 rounded-lg text-sm appearance-none outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] transition-colors shadow-sm cursor-pointer text-gray-600"
        >
          <option value="">전체 매장</option>
          <option v-for="store in stores" :key="store.id" :value="store.id">
            {{ store.name }}
          </option>
        </select>
        <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-gray-400">
          <ChevronDown class="w-4 h-4" />
        </div>
      </div>
    </div>

    <!-- ── 메뉴 목록 테이블 ── -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">메뉴번호</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">매장명</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">메뉴명</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가격</th>
            <th class="px-4 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">재료 수</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
          <tr v-for="menu in filteredMenus" :key="menu.id"
              @click="openIngredientModal(menu)"
              class="hover:bg-gray-50/50 transition-colors cursor-pointer group">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ menu.id }}</td>
            <td class="px-5 py-3.5 text-gray-600 font-medium">{{ getStoreName(menu.storeId) }}</td>
            <td class="px-5 py-3.5 font-bold text-gray-900 group-hover:text-[#F97316] transition-colors">{{ menu.name }}</td>
            <td class="px-5 py-3.5 text-gray-700 font-semibold">{{ formatPrice(menu.price) }}</td>
            <td class="px-5 py-3.5 text-center">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full bg-orange-50 text-orange-600">
                  {{ menu.ingredients.length }}종
                </span>
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
         재료 목록 상세 모달
    ══════════════════════════════════════════ -->
    <div v-if="showIngredientModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40" @click="showIngredientModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-lg border border-gray-200 shadow-xl overflow-hidden max-h-[85vh] flex flex-col">

        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <div>
            <p class="text-xs text-gray-400 font-medium mb-0.5">{{ getStoreName(selectedMenu?.storeId) }}</p>
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
        <div class="shrink-0 px-6 py-4 border-t border-gray-100 flex justify-end">
          <button @click="showIngredientModal = false"
                  class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
            닫기
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, ChevronDown } from 'lucide-vue-next'

// ─────────────────────────────────────────────
//  매장 목록
// ─────────────────────────────────────────────
const stores = ref([
  { id: 'S001', name: '한우 오마카세' },
  { id: 'S002', name: '이탈리안 키친' },
  { id: 'S003', name: '일식 스시바' },
  { id: 'S004', name: '차이나 가든' },
  { id: 'S005', name: '프렌치 비스트로' },
])

// ─────────────────────────────────────────────
//  제품 목록
// ─────────────────────────────────────────────
const products = ref([
  { id: 'P-001', name: '한우 등심' },
  { id: 'P-002', name: '한우 안심' },
  { id: 'P-003', name: '돼지 삼겹살' },
  { id: 'P-004', name: '닭 가슴살' },
  { id: 'P-005', name: '연어' },
  { id: 'P-006', name: '참치' },
  { id: 'P-007', name: '새우' },
  { id: 'P-008', name: '양파' },
  { id: 'P-009', name: '마늘' },
  { id: 'P-010', name: '대파' },
  { id: 'P-011', name: '파프리카' },
  { id: 'P-012', name: '간장' },
  { id: 'P-013', name: '고추장' },
  { id: 'P-014', name: '된장' },
  { id: 'P-015', name: '올리브오일' },
  { id: 'P-016', name: '버터' },
  { id: 'P-017', name: '생크림' },
  { id: 'P-018', name: '콜라' },
  { id: 'P-019', name: '생수' },
])

// ─────────────────────────────────────────────
//  메뉴 데이터
// ─────────────────────────────────────────────
const menus = ref([
  {
    id: 'M-001', storeId: 'S001', name: '한우 등심 오마카세 코스', price: 180000,
    ingredients: [
      { productId: 'P-001', amount: 0.3, unit: 'kg' },
      { productId: 'P-009', amount: 20, unit: 'g' },
      { productId: 'P-012', amount: 50, unit: 'ml' },
      { productId: 'P-015', amount: 30, unit: 'ml' },
    ],
  },
  {
    id: 'M-002', storeId: 'S001', name: '한우 안심 스테이크', price: 120000,
    ingredients: [
      { productId: 'P-002', amount: 0.25, unit: 'kg' },
      { productId: 'P-016', amount: 30, unit: 'g' },
      { productId: 'P-008', amount: 50, unit: 'g' },
      { productId: 'P-011', amount: 40, unit: 'g' },
    ],
  },
  {
    id: 'M-003', storeId: 'S002', name: '뇨끼 크림 파스타', price: 28000,
    ingredients: [
      { productId: 'P-017', amount: 150, unit: 'ml' },
      { productId: 'P-016', amount: 20, unit: 'g' },
      { productId: 'P-008', amount: 30, unit: 'g' },
      { productId: 'P-009', amount: 10, unit: 'g' },
    ],
  },
  {
    id: 'M-004', storeId: 'S002', name: '해산물 리조또', price: 32000,
    ingredients: [
      { productId: 'P-005', amount: 80, unit: 'g' },
      { productId: 'P-007', amount: 60, unit: 'g' },
      { productId: 'P-017', amount: 100, unit: 'ml' },
      { productId: 'P-015', amount: 20, unit: 'ml' },
      { productId: 'P-008', amount: 40, unit: 'g' },
    ],
  },
  {
    id: 'M-005', storeId: 'S003', name: '연어 스시 플래터', price: 65000,
    ingredients: [
      { productId: 'P-005', amount: 0.2, unit: 'kg' },
      { productId: 'P-012', amount: 30, unit: 'ml' },
      { productId: 'P-009', amount: 5, unit: 'g' },
    ],
  },
  {
    id: 'M-006', storeId: 'S003', name: '참치 사시미', price: 72000,
    ingredients: [
      { productId: 'P-006', amount: 0.18, unit: 'kg' },
      { productId: 'P-012', amount: 30, unit: 'ml' },
      { productId: 'P-010', amount: 10, unit: 'g' },
    ],
  },
  {
    id: 'M-007', storeId: 'S004', name: '새우 볶음밥', price: 22000,
    ingredients: [
      { productId: 'P-007', amount: 100, unit: 'g' },
      { productId: 'P-008', amount: 60, unit: 'g' },
      { productId: 'P-010', amount: 20, unit: 'g' },
      { productId: 'P-012', amount: 20, unit: 'ml' },
    ],
  },
  {
    id: 'M-008', storeId: 'S004', name: '마파두부', price: 18000,
    ingredients: [
      { productId: 'P-003', amount: 80, unit: 'g' },
      { productId: 'P-013', amount: 30, unit: 'g' },
      { productId: 'P-009', amount: 15, unit: 'g' },
      { productId: 'P-010', amount: 10, unit: 'g' },
    ],
  },
  {
    id: 'M-009', storeId: 'S005', name: '크로크무슈', price: 24000,
    ingredients: [
      { productId: 'P-016', amount: 25, unit: 'g' },
      { productId: 'P-017', amount: 80, unit: 'ml' },
      { productId: 'P-008', amount: 30, unit: 'g' },
    ],
  },
  {
    id: 'M-010', storeId: 'S005', name: '크렘브륄레', price: 16000,
    ingredients: [
      { productId: 'P-017', amount: 200, unit: 'ml' },
      { productId: 'P-016', amount: 15, unit: 'g' },
    ],
  },
])

// ─────────────────────────────────────────────
//  검색 및 필터링
// ─────────────────────────────────────────────
const searchQuery = ref('')
const selectedStoreId = ref('')

const filteredMenus = computed(() => {
  let list = menus.value

  if (selectedStoreId.value) {
    list = list.filter(m => m.storeId === selectedStoreId.value)
  }

  const q = searchQuery.value.trim().toLowerCase()
  if (q) {
    list = list.filter(m =>
      m.name.toLowerCase().includes(q) ||
      getStoreName(m.storeId).toLowerCase().includes(q)
    )
  }
  return list
})

// ─────────────────────────────────────────────
//  재료 목록 모달
// ─────────────────────────────────────────────
const showIngredientModal = ref(false)
const selectedMenu = ref(null)

function openIngredientModal(menu) {
  selectedMenu.value = menu
  showIngredientModal.value = true
}

// ─────────────────────────────────────────────
//  유틸
// ─────────────────────────────────────────────
function getStoreName(storeId) {
  return stores.value.find(s => s.id === storeId)?.name ?? storeId
}

function getProductName(productId) {
  return products.value.find(p => p.id === productId)?.name ?? productId
}

function formatPrice(price) {
  return '₩ ' + price.toLocaleString('ko-KR')
}
</script>
