<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Plus } from 'lucide-vue-next'
import { formatPrice } from './orderUtils'
import { searchStoreList } from '@/api/store'
import { getProductList } from '@/api/product'

defineProps({
  visible: { type: Boolean, required: true },
})

const emit = defineEmits(['close', 'submit'])

const stores = ref([])
const storeKeyword = ref('')
const selectedStore = ref(null)
const showStoreDropdown = ref(false)
const products = ref([])
const form = reactive({ store: '', items: [] })

async function searchStore() {
  if (storeKeyword.value.trim().length === 0) {
    stores.value = []
    showStoreDropdown.value = false
    return
  }
  try {
    const res = await searchStoreList(storeKeyword.value)
    stores.value = res.data.result
    showStoreDropdown.value = stores.value.length > 0
  } catch (e) {
    console.error('매장 검색 실패', e)
  }
}

function selectStore(store) {
  selectedStore.value = store
  form.store = store.idx
  storeKeyword.value = store.storeName
  showStoreDropdown.value = false
}

function clearStore() {
  selectedStore.value = null
  form.store = ''
  storeKeyword.value = ''
  stores.value = []
}

onMounted(async () => {
  try {
    const productRes = await getProductList()
    products.value = productRes.data
  } catch (e) {
    console.error('상품 목록 조회 실패', e)
  }
})

function addItem() {
  form.items.push({ product: '', qty: 1, unitPrice: 0 })
}

function submit() {
  if (!form.store || form.items.length === 0) {
    alert('매장과 품목을 입력해주세요.')
    return
  }
  const validItems = form.items.filter(i => i.product)
  if (validItems.length === 0) {
    alert('품목을 선택해주세요.')
    return
  }
  emit('submit', {
    store: form.store,
    items: validItems.map(i => ({ product: i.product, qty: i.qty, unitPrice: productPrices[i.product] ?? 0 })),
  })
  form.store = ''
  form.items = []
}
</script>

<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="absolute inset-0 bg-black/40" @click="$emit('close')"></div>
    <div class="relative bg-white rounded-xl w-full max-w-lg border border-gray-200 shadow-xl">
      <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h3 class="font-bold text-gray-900">수동 발주 생성</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
      </div>
      <div class="p-6 space-y-4">
        <div class="space-y-1.5">
          <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">대상 매장</label>
          <div class="relative">
            <div v-if="selectedStore" class="flex items-center justify-between w-full px-3 py-2 rounded border border-[#F37321] bg-orange-50 text-sm">
              <span class="font-medium text-gray-900">{{ selectedStore.storeName }}</span>
              <button @click="clearStore" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
            </div>
            <input v-else v-model="storeKeyword" @input="searchStore" placeholder="매장명을 검색하세요"
              class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            <ul v-if="showStoreDropdown" class="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded shadow-lg max-h-40 overflow-y-auto">
              <li v-for="store in stores" :key="store.idx" @click="selectStore(store)"
                class="px-3 py-2 text-sm hover:bg-orange-50 cursor-pointer">
                {{ store.storeName }} <span class="text-gray-400 text-xs">{{ store.address }}</span>
              </li>
            </ul>
          </div>
        </div>
        <div class="space-y-2">
          <div class="flex justify-between items-center">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">발주 품목</label>
            <button @click="addItem" class="text-xs text-[#F37321] font-semibold hover:underline flex items-center gap-1 cursor-pointer">
              <Plus class="w-3 h-3" /> 품목 추가
            </button>
          </div>
          <div v-for="(item, idx) in form.items" :key="idx" class="flex gap-2 items-center">
            <select v-model="item.product" @change="item.unitPrice = productPrices[item.product] ?? 0"
              class="flex-1 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
              <option value="">품목 선택</option>
              <option>한우 등심</option>
              <option>올리브오일</option>
              <option>버터</option>
              <option>생크림</option>
              <option>간장</option>
              <option>양파</option>
              <option>생수</option>
            </select>
            <div class="flex items-center gap-1.5">
              <input v-model.number="item.qty" type="number" min="1" placeholder="수량"
                class="w-20 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
              <span class="text-xs text-gray-400 font-medium w-6">{{ PRODUCT_UNIT[item.product] ?? '' }}</span>
            </div>
            <div class="w-28 px-3 py-2 rounded border border-gray-100 bg-gray-50 text-sm text-gray-500 text-right shrink-0">
              {{ item.product ? formatPrice(productPrices[item.product] ?? 0) : '-' }}
            </div>
            <button @click="form.items.splice(idx, 1)"
              class="px-3 py-2 text-xs font-semibold rounded border border-red-200 text-red-500 bg-red-50 hover:bg-red-500 hover:text-white hover:cursor-pointer transition-colors shrink-0">
              삭제
            </button>
          </div>
          <div v-if="form.items.length === 0"
            class="text-sm text-gray-400 text-center py-4 bg-gray-50 border border-gray-100 rounded">
            품목 추가 버튼을 눌러 발주 품목을 입력하세요.
          </div>
          <div v-if="form.items.length > 0" class="text-right text-sm font-bold text-[#F37321]">
            합계: {{ formatPrice(form.items.reduce((s, i) => s + (i.unitPrice || 0) * (i.qty || 0), 0)) }}
          </div>
        </div>
      </div>
      <div class="px-6 py-4 border-t border-gray-100 flex gap-3">
        <button @click="$emit('close')"
          class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer">취소</button>
        <button @click="submit"
          class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] cursor-pointer">발주 생성</button>
      </div>
    </div>
  </div>
</template>
