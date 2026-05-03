<template>
  <div class="h-full flex flex-col overflow-hidden select-none">
    <PosHeaderBar
      :active-tab="activeTab"
      :is-closed="salesData.isClosed"
      :current-time="currentTime"
      :store-name="auth.user?.storeName"
      @select-tab="activeTab = $event" />

    <div v-if="activeTab === 'order'" class="flex-1 flex overflow-hidden">
      <PosMenuBoard
        :loading-menus="loadingMenus"
        :menus="filteredMenus"
        :categories="categories"
        :search-query="searchQuery"
        :selected-category="selectedCategory"
        @update:search-query="searchQuery = $event"
        @update:selected-category="selectedCategory = $event"
        @add-to-cart="addToCart" />
      <PosCartPanel
        :cart="cart"
        :total-price="totalPrice"
        :total-quantity="totalQuantity"
        :is-closed="salesData.isClosed"
        @clear-cart="clearCart"
        @remove-item="removeFromCart"
        @increase-qty="increaseQty"
        @decrease-qty="decreaseQty"
        @checkout="openPaymentModal" />
    </div>

    <PosSettlementPanel
      v-else-if="activeTab === 'settlement'"
      :sales="salesData"
      :payment-history="paymentHistory"
      @toggle-store-status="toggleStoreStatus" />

    <PosToast :show="toast.show" :message="toast.message" />

    <PosPaymentMethodModal
      :open="showPaymentModal"
      :formatted-total="formatPrice(totalPrice)"
      @close="showPaymentModal = false"
      @select="processPayment" />

    <PosCloseStoreModal
      :open="showCloseModal"
      @close="showCloseModal = false"
      @confirm="confirmClose" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getMenuCategoryList, getMenuListPaged } from '@/api/pos'
import { formatPrice } from '@/utils/formatPrice.js'
import PosHeaderBar from '@/components/pos/PosHeaderBar.vue'
import PosMenuBoard from '@/components/pos/PosMenuBoard.vue'
import PosCartPanel from '@/components/pos/PosCartPanel.vue'
import PosSettlementPanel from '@/components/pos/PosSettlementPanel.vue'
import PosToast from '@/components/pos/PosToast.vue'
import PosPaymentMethodModal from '@/components/pos/PosPaymentMethodModal.vue'
import PosCloseStoreModal from '@/components/pos/PosCloseStoreModal.vue'

const auth = useAuthStore()

const activeTab = ref('order')
const selectedCategory = ref('전체')
const searchQuery = ref('')
const showPaymentModal = ref(false)
const showCloseModal = ref(false)
const currentTime = ref('')
const toast = ref({ show: false, message: '' })
const loadingMenus = ref(true)

let timer = null
let toastTimer = null

const categories = ref(['전체'])
const menus = ref([])
const cart = ref([])

const salesData = ref({
  total: 124800, card: 92800, cash: 32000, count: 5, isClosed: false,
})

const paymentHistory = ref([
  { id: 5, time: '20:42:17', method: '신용카드', items: '한우 등심 오마카세 외 1건', amount: 200000 },
  { id: 4, time: '19:58:03', method: '현금', items: '한우 안심 스테이크 1인', amount: 120000 },
  { id: 3, time: '18:26:44', method: '신용카드', items: '오마카세 런치 세트 외 1건', amount: 156000 },
  { id: 2, time: '17:11:20', method: '신용카드', items: '한우 2인 코스', amount: 220000 },
  { id: 1, time: '16:40:09', method: '현금', items: '계절 채소 샐러드 외 2건', amount: 36000 },
])

const filteredMenus = computed(() => {
  let result = menus.value
  if (selectedCategory.value !== '전체') {
    result = result.filter((m) => m.menuCategory === selectedCategory.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim()
    result = result.filter((m) => m.menuName.includes(q))
  }
  return result
})

const totalPrice = computed(() => cart.value.reduce((s, i) => s + i.price * i.quantity, 0))
const totalQuantity = computed(() => cart.value.reduce((s, i) => s + i.quantity, 0))

function showToastMsg(msg) {
  toast.value = { show: true, message: msg }
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value.show = false }, 3000)
}

function updateTime() {
  const now = new Date()
  currentTime.value = `${now.toLocaleDateString('ko-KR')} ${now.toLocaleTimeString('ko-KR', { hour12: false })}`
}

async function loadMenusAndCategories() {
  loadingMenus.value = true
  try {
    const [catRes, menuAcc] = await Promise.all([
      getMenuCategoryList().catch(() => ({ data: { result: [] } })),
      (async () => {
        const collected = []
        let page = 0
        let totalPages = 1
        while (page < totalPages) {
          const res = await getMenuListPaged(page, 80)
          const pack = res.data?.result
          if (!pack?.menuList) break
          collected.push(...pack.menuList)
          totalPages = pack.totalPage ?? 1
          page += 1
        }
        return collected
      })(),
    ])
    const catNames = (catRes.data?.result || []).map((c) => c.menuCategoryName).filter(Boolean)
    categories.value = ['전체', ...new Set(catNames)]
    menus.value = menuAcc
  } catch (e) {
    console.error(e)
    showToastMsg('메뉴를 불러오지 못했습니다. 로그인·API 주소를 확인해 주세요.')
    menus.value = []
  } finally {
    loadingMenus.value = false
  }
}

function addToCart(menu) {
  if (salesData.value.isClosed) {
    alert('영업이 마감되어 주문을 추가할 수 없습니다.')
    return
  }
  const existing = cart.value.find((i) => i.menuIdx === menu.idx)
  if (existing) existing.quantity += 1
  else {
    cart.value.push({
      menuIdx: menu.idx,
      name: menu.menuName,
      price: menu.price,
      quantity: 1,
    })
  }
}

function increaseQty(item) {
  item.quantity += 1
}

function decreaseQty(item) {
  if (item.quantity > 1) item.quantity -= 1
  else removeFromCart(item.menuIdx)
}

function removeFromCart(menuIdx) {
  cart.value = cart.value.filter((i) => i.menuIdx !== menuIdx)
}

function clearCart() {
  if (cart.value.length > 0 && confirm('장바구니를 모두 비우시겠습니까?')) cart.value = []
}

function openPaymentModal() {
  if (cart.value.length === 0 || salesData.value.isClosed) return
  showPaymentModal.value = true
}

function processPayment(method) {
  const amount = totalPrice.value
  const methodKr = method === 'card' ? '신용카드' : '현금'
  const totalUnits = cart.value.reduce((s, i) => s + i.quantity, 0)
  const itemsSummary = cart.value.length === 1
    ? `${cart.value[0].name} ×${cart.value[0].quantity}`
    : `${cart.value[0].name} 외 ${cart.value.length - 1}종 (${totalUnits}개)`

  salesData.value.total += amount
  salesData.value.count += 1
  if (method === 'card') salesData.value.card += amount
  else salesData.value.cash += amount

  paymentHistory.value.unshift({
    id: Date.now(),
    time: new Date().toLocaleTimeString('ko-KR', { hour12: false }),
    method: methodKr,
    items: itemsSummary,
    amount,
  })

  showPaymentModal.value = false
  cart.value = []
  showToastMsg(`${methodKr} 결제가 완료되었습니다.`)
}

function toggleStoreStatus() {
  if (!salesData.value.isClosed) {
    showCloseModal.value = true
  } else if (confirm('새로운 영업을 시작하시겠습니까?\n기존 매출 및 결제 내역이 모두 초기화됩니다.')) {
    Object.assign(salesData.value, { isClosed: false, total: 0, card: 0, cash: 0, count: 0 })
    paymentHistory.value = []
    showToastMsg('새로운 영업이 시작되었습니다.')
  }
}

function confirmClose() {
  salesData.value.isClosed = true
  showCloseModal.value = false
  showToastMsg('성공적으로 영업이 마감되었습니다. 데이터가 본사로 전송됩니다.')
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadMenusAndCategories()
})

onUnmounted(() => {
  clearInterval(timer)
  clearTimeout(toastTimer)
})
</script>
