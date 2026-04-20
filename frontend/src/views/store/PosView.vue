<template>
  <div class="h-full flex flex-col overflow-hidden select-none">

    <!-- POS 내부 헤더 -->
    <div class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between shrink-0">
      <div class="flex items-center gap-4">
        <!-- 내부 탭 -->
        <div class="flex border border-gray-200">
          <button
            @click="activeTab = 'order'"
            :class="['px-4 py-2 text-sm font-semibold transition-all flex items-center gap-2',
              activeTab === 'order' ? 'bg-gray-900 text-white' : 'bg-white text-gray-500 hover:bg-gray-50']">
            <ShoppingCart class="w-4 h-4" /> 주문 및 결제
          </button>
          <button
            @click="activeTab = 'settlement'"
            :class="['px-4 py-2 text-sm font-semibold transition-all flex items-center gap-2 border-l border-gray-200',
              activeTab === 'settlement' ? 'bg-gray-900 text-white' : 'bg-white text-gray-500 hover:bg-gray-50']">
            <TrendingUp class="w-4 h-4" /> 일일 마감 (정산)
          </button>
        </div>

        <!-- 영업 상태 뱃지 -->
        <span class="text-xs px-2.5 py-1 font-bold rounded border"
          :class="salesData.isClosed ? 'bg-red-50 text-red-600 border-red-200' : 'bg-green-50 text-green-600 border-green-200'">
          {{ salesData.isClosed ? '마감됨 (주문불가)' : '영업중' }}
        </span>
      </div>

      <!-- 시간 / 매장명 -->
      <div class="text-right">
        <p class="text-sm font-bold text-blue-600">{{ currentTime }}</p>
        <p class="text-xs text-gray-400">{{ auth.user?.storeName }}</p>
      </div>
    </div>

    <p class="px-6 py-2 border-b border-gray-100 bg-gray-50/80 text-[11px] text-gray-500 leading-relaxed shrink-0">
      <code class="font-mono text-gray-400">MENU_001 · PAY_001~002 · CLOSED_001</code>
      메뉴·카테고리·검색, 장바구니 결제, 일일 마감 시 본사로 판매·재고 데이터 전송 및 전산 재고 차감에 연동됩니다.
    </p>

    <!-- ── 주문/결제 탭 ─────────────────────────────── -->
    <div v-if="activeTab === 'order'" class="flex-1 flex overflow-hidden">

      <!-- 좌측: 카테고리 + 상품 그리드 -->
      <div class="flex-1 flex flex-col overflow-hidden">

        <!-- 카테고리 필터 + 검색 -->
        <div class="bg-[#F8F9FA] px-4 pt-4 pb-3 border-b border-gray-200 shrink-0 space-y-3">
          <!-- 검색 -->
          <div class="flex items-center bg-white rounded-full px-4 py-2 border border-gray-200 focus-within:border-blue-300 focus-within:ring-2 focus-within:ring-blue-100 transition-all max-w-sm">
            <Search class="w-4 h-4 text-gray-400 mr-2 shrink-0" />
            <input
              type="text"
              v-model="searchQuery"
              placeholder="상품명 검색..."
              class="bg-transparent border-none outline-none text-sm w-full text-gray-700" />
            <button v-if="searchQuery" @click="searchQuery = ''" class="text-gray-400 hover:text-gray-600 ml-1">
              <XCircle class="w-4 h-4" />
            </button>
          </div>

          <!-- 카테고리 버튼 -->
          <div class="flex gap-2 overflow-x-auto pb-1">
            <button v-for="cat in categories" :key="cat"
              @click="selectedCategory = cat"
              :class="['px-5 py-2 rounded-full text-sm font-semibold whitespace-nowrap transition-all border shrink-0',
                selectedCategory === cat
                  ? 'bg-gray-800 text-white border-gray-800 shadow-sm'
                  : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']">
              {{ cat }}
            </button>
          </div>
        </div>

        <!-- 상품 그리드 -->
        <div class="flex-1 overflow-y-auto p-5">
          <div v-if="filteredProducts.length === 0"
            class="h-full flex flex-col items-center justify-center text-gray-400">
            <Search class="w-10 h-10 mb-3 opacity-30" />
            <p class="text-sm font-medium">검색된 상품이 없습니다.</p>
          </div>

          <div class="grid grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4">
            <div v-for="product in filteredProducts" :key="product.id"
              @click="addToCart(product)"
              class="bg-white p-3 border border-gray-200 cursor-pointer flex flex-col h-47.5
                     hover:border-blue-400 transition-all active:scale-[0.97] group relative">

              <!-- 상품 이미지 -->
              <div class="w-full h-28 rounded-lg overflow-hidden mb-3 bg-gray-50 border border-gray-50 relative">
                <img :src="product.image" :alt="product.name"
                  class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                  loading="lazy" />
                <div class="absolute inset-0 bg-black/5 group-hover:bg-transparent transition-colors"></div>
              </div>

              <!-- 상품 정보 -->
              <div class="flex-1 flex flex-col justify-between">
                <h3 class="font-medium text-gray-800 text-sm leading-tight line-clamp-1">{{ product.name }}</h3>
                <span class="text-gray-900 font-bold text-sm mt-auto pt-1">{{ formatPrice(product.price) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 우측: 장바구니 -->
      <aside class="w-[360px] bg-white shadow-[-4px_0_15px_rgba(0,0,0,0.03)] flex flex-col border-l border-gray-200 shrink-0">
        <!-- 장바구니 헤더 -->
        <div class="h-14 px-5 border-b border-gray-100 flex justify-between items-center shrink-0">
          <h2 class="text-sm font-bold text-gray-900 flex items-center gap-2">
            <ShoppingCart class="w-4 h-4 text-gray-500" /> 현재 주문 내역
          </h2>
          <button @click="clearCart"
            class="text-xs text-gray-500 hover:text-red-500 border border-gray-200 px-3 py-1.5 rounded-md hover:bg-red-50 hover:border-red-100 transition-colors">
            전체 삭제
          </button>
        </div>

        <!-- 장바구니 아이템 -->
        <div class="flex-1 overflow-y-auto p-4 flex flex-col gap-3 bg-[#F8F9FA]">
          <div v-if="cart.length === 0"
            class="h-full flex flex-col items-center justify-center text-gray-400">
            <div class="w-14 h-14 bg-gray-100 rounded-full flex items-center justify-center mb-3">
              <ShoppingBag class="w-6 h-6 text-gray-300" />
            </div>
            <p class="text-sm font-medium text-gray-500">상품을 담아주세요</p>
          </div>

          <div v-for="item in cart" :key="item.id"
            class="bg-white border border-gray-200 p-4">
            <div class="flex justify-between items-start mb-3">
              <span class="font-semibold text-gray-900 text-sm leading-tight">{{ item.name }}</span>
              <button @click="removeFromCart(item.id)" class="text-gray-300 hover:text-red-500 transition-colors ml-2 shrink-0">
                <X class="w-4 h-4" />
              </button>
            </div>
            <div class="flex justify-between items-center">
              <span class="font-bold text-gray-900">{{ formatPrice(item.price * item.quantity) }}</span>
              <!-- 수량 조절 -->
              <div class="flex items-center border border-gray-200 rounded-lg overflow-hidden">
                <button @click="decreaseQty(item)"
                  class="w-8 h-7 flex items-center justify-center bg-gray-50 text-gray-600 hover:bg-gray-100 hover:text-blue-500 transition-colors">
                  <Minus class="w-3 h-3" />
                </button>
                <span class="w-9 h-7 flex items-center justify-center text-xs font-bold text-gray-800 border-x border-gray-200">{{ item.quantity }}</span>
                <button @click="increaseQty(item)"
                  class="w-8 h-7 flex items-center justify-center bg-gray-50 text-gray-600 hover:bg-gray-100 hover:text-blue-500 transition-colors">
                  <Plus class="w-3 h-3" />
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 결제 하단 영역 -->
        <div class="p-5 border-t border-gray-200 bg-white shrink-0">
          <div class="flex justify-between text-gray-500 mb-1.5 text-sm font-medium">
            <span>총 수량</span>
            <span class="text-gray-900">{{ totalQuantity }} 개</span>
          </div>
          <div class="flex justify-between items-end mb-5">
            <span class="text-gray-500 font-medium text-sm">총 결제금액</span>
            <span class="text-2xl font-bold text-blue-600 tracking-tight">{{ formatPrice(totalPrice) }}</span>
          </div>

          <button @click="openPaymentModal"
            :disabled="cart.length === 0 || salesData.isClosed"
            :class="['w-full py-3.5 rounded-xl font-bold text-sm flex items-center justify-center gap-2 transition-all text-white shadow-sm',
              (cart.length === 0 || salesData.isClosed) ? 'bg-gray-200 text-gray-400 cursor-not-allowed shadow-none' : 'bg-blue-500 hover:bg-blue-600 active:scale-[0.98]']">
            {{ salesData.isClosed ? '마감됨 — 주문 불가' : '결제 진행하기' }}
          </button>
        </div>
      </aside>
    </div>

    <!-- ── 일일 마감(정산) 탭 ───────────────────────── -->
    <div v-if="activeTab === 'settlement'" class="flex-1 overflow-auto p-8 space-y-6">
      <div class="flex justify-between items-end">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">일일 정산</h1>
          <p class="text-sm text-gray-500 mt-1">매출 내역과 결제 이력을 확인하고 영업 마감을 관리합니다.</p>
        </div>

        <!-- 마감 / 영업 시작 버튼 -->
        <button @click="toggleStoreStatus"
          :class="['text-sm px-6 py-2.5 rounded-lg font-bold transition-all shadow-sm text-white',
            salesData.isClosed ? 'bg-blue-500 hover:bg-blue-600 ring-4 ring-blue-100' : 'bg-gray-800 hover:bg-gray-700']">
          {{ salesData.isClosed ? '새로운 영업 시작하기' : '영업 마감하기' }}
        </button>
      </div>

      <!-- KPI 카드 -->
      <div class="grid grid-cols-3 gap-4">
        <div class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
          <div>
            <p class="text-sm font-medium text-gray-500">총 매출액</p>
            <div class="flex items-end gap-1 mt-2">
              <h3 class="text-3xl font-bold text-gray-900">{{ salesData.total.toLocaleString() }}</h3>
              <span class="text-base font-medium text-gray-500 mb-1">원</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
          <div>
            <p class="text-sm font-medium text-gray-500">총 결제 건수</p>
            <div class="flex items-end gap-1 mt-2">
              <h3 class="text-3xl font-bold text-gray-900">{{ salesData.count }}</h3>
              <span class="text-base font-medium text-gray-500 mb-1">건</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
          <div>
            <p class="text-sm font-medium text-gray-500">평균 객단가</p>
            <div class="flex items-end gap-1 mt-2">
              <h3 class="text-3xl font-bold text-gray-900">
                {{ salesData.count > 0 ? Math.floor(salesData.total / salesData.count).toLocaleString() : 0 }}
              </h3>
              <span class="text-base font-medium text-gray-500 mb-1">원</span>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-6">
        <!-- 결제 수단별 매출 -->
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="px-5 py-4 border-b border-gray-100 font-semibold text-gray-900 text-sm">
            결제 수단별 매출
          </div>
          <table class="w-full text-sm">
            <thead class="bg-gray-50 text-xs text-gray-500 uppercase">
              <tr>
                <th class="px-5 py-3 text-left">수단</th>
                <th class="px-5 py-3 text-center">비율</th>
                <th class="px-5 py-3 text-right">금액</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              <tr class="hover:bg-gray-50/50">
                <td class="px-5 py-4 font-medium">신용카드</td>
                <td class="px-5 py-4 text-center text-gray-500">
                  {{ salesData.total > 0 ? Math.round((salesData.card / salesData.total) * 100) : 0 }}%
                </td>
                <td class="px-5 py-4 font-bold text-right">{{ formatPrice(salesData.card) }}</td>
              </tr>
              <tr class="hover:bg-gray-50/50">
                <td class="px-5 py-4 font-medium">현금</td>
                <td class="px-5 py-4 text-center text-gray-500">
                  {{ salesData.total > 0 ? Math.round((salesData.cash / salesData.total) * 100) : 0 }}%
                </td>
                <td class="px-5 py-4 font-bold text-right">{{ formatPrice(salesData.cash) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 실시간 결제 내역 -->
        <div class="col-span-2 bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden flex flex-col max-h-[360px]">
          <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center font-semibold text-gray-900 text-sm shrink-0">
            <span>실시간 결제 내역</span>
            <span class="text-xs text-gray-400 font-normal">최신 순</span>
          </div>
          <div class="overflow-auto flex-1">
            <table class="w-full text-sm">
              <thead class="bg-gray-50 text-xs text-gray-500 uppercase sticky top-0">
                <tr>
                  <th class="px-6 py-3 text-left">결제 시간</th>
                  <th class="px-6 py-3 text-left">주문 내역</th>
                  <th class="px-6 py-3 text-center">결제 수단</th>
                  <th class="px-6 py-3 text-right">결제 금액</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-if="paymentHistory.length === 0">
                  <td colspan="4" class="px-6 py-12 text-center text-gray-400">
                    <p class="text-sm">오늘 등록된 결제 내역이 없습니다.</p>
                  </td>
                </tr>
                <tr v-for="h in paymentHistory" :key="h.id" class="hover:bg-gray-50/50">
                  <td class="px-6 py-4 font-mono text-xs text-gray-500">{{ h.time }}</td>
                  <td class="px-6 py-4 text-gray-800 font-medium truncate max-w-[200px]" :title="h.items">{{ h.items }}</td>
                  <td class="px-6 py-4 text-center">
                    <span class="px-2.5 py-1 rounded-full text-xs font-bold"
                      :class="h.method === '신용카드' ? 'bg-blue-50 text-blue-600' : 'bg-emerald-50 text-emerald-600'">
                      {{ h.method }}
                    </span>
                  </td>
                  <td class="px-6 py-4 font-bold text-gray-900 text-right">{{ formatPrice(h.amount) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- ── Toast 알림 ─────────────────────────────────── -->
    <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 translate-y-2" enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-150" leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="toast.show"
        class="fixed bottom-8 right-8 bg-gray-800 text-white px-5 py-3 rounded-xl shadow-xl flex items-center gap-3 z-50">
        <CheckCircle class="w-5 h-5 text-green-400 shrink-0" />
        <span class="text-sm font-medium">{{ toast.message }}</span>
      </div>
    </transition>

    <!-- ── 결제 모달 ──────────────────────────────────── -->
    <div v-if="showPaymentModal"
      class="fixed inset-0 bg-gray-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md border border-gray-100">
        <div class="border-b border-gray-100 p-5 flex justify-between items-center bg-gray-50 rounded-t-2xl">
          <h2 class="text-lg font-bold text-gray-800">결제 수단 선택</h2>
          <button @click="showPaymentModal = false" class="text-gray-400 hover:text-gray-600">
            <X class="w-5 h-5" />
          </button>
        </div>
        <div class="p-8">
          <div class="text-center mb-8">
            <p class="text-sm text-gray-500 mb-2 font-medium">총 결제 금액</p>
            <p class="text-4xl font-bold text-blue-600 tracking-tight">{{ formatPrice(totalPrice) }}</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <button @click="processPayment('card')"
              class="flex flex-col items-center justify-center p-6 border border-gray-200 rounded-xl hover:border-blue-400 hover:bg-blue-50 transition-all group shadow-sm hover:shadow-md">
              <CreditCard class="w-10 h-10 text-gray-400 group-hover:text-blue-500 mb-3 transition-colors" />
              <span class="font-semibold text-gray-700 group-hover:text-blue-600">신용카드</span>
            </button>
            <button @click="processPayment('cash')"
              class="flex flex-col items-center justify-center p-6 border border-gray-200 rounded-xl hover:border-emerald-400 hover:bg-emerald-50 transition-all group shadow-sm hover:shadow-md">
              <Banknote class="w-10 h-10 text-gray-400 group-hover:text-emerald-500 mb-3 transition-colors" />
              <span class="font-semibold text-gray-700 group-hover:text-emerald-600">현금</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 마감 확인 모달 ─────────────────────────────── -->
    <div v-if="showCloseModal"
      class="fixed inset-0 bg-gray-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-sm border border-gray-100 text-center p-8">
        <div class="w-16 h-16 bg-red-50 rounded-full flex items-center justify-center mx-auto mb-5">
          <AlertCircle class="w-8 h-8 text-red-500" />
        </div>
        <h2 class="text-xl font-bold text-gray-900 mb-2">영업을 마감하시겠습니까?</h2>
        <p class="text-sm text-gray-500 mb-8 leading-relaxed">
          마감 처리 후에는 새로운 결제를<br/>진행할 수 없습니다.<br/>
          <span class="text-orange-500 font-semibold mt-2 block">
            판매 데이터가 본사로 자동 전송됩니다.
          </span>
        </p>
        <div class="flex gap-3">
          <button @click="showCloseModal = false"
            class="flex-1 py-3 bg-white border border-gray-200 text-gray-600 rounded-xl text-sm font-bold hover:bg-gray-50 transition-colors">
            취소
          </button>
          <button @click="confirmClose"
            class="flex-1 py-3 bg-red-500 text-white rounded-xl text-sm font-bold hover:bg-red-600 shadow-sm transition-colors">
            마감 승인
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  ShoppingCart, ShoppingBag,
  Search, XCircle, X, Minus, Plus,
  CreditCard, Banknote,
  CheckCircle, AlertCircle,
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

// ── 상태 ──────────────────────────────────────────────
const activeTab       = ref('order')
const selectedCategory = ref('전체')
const searchQuery     = ref('')
const showPaymentModal = ref(false)
const showCloseModal  = ref(false)
const currentTime     = ref('')
const toast           = ref({ show: false, message: '' })

let timer = null
let toastTimer = null

// ── 카테고리 ──────────────────────────────────────────
const categories = ['전체', '치킨', '사이드', '음료', '세트']

// ── 상품 데이터 ───────────────────────────────────────
const allProducts = ref([
  { id: 1,  name: '황금올리브치킨',      price: 23000, category: '치킨', image: 'https://images.unsplash.com/photo-1562967916-eb82221dfb92?auto=format&fit=crop&w=500&q=80' },
  { id: 2,  name: '황금올리브순살',      price: 24000, category: '치킨', image: 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?auto=format&fit=crop&w=500&q=80' },
  { id: 3,  name: '핫황금올리브치킨',    price: 24000, category: '치킨', image: 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?auto=format&fit=crop&w=500&q=80' },
  { id: 4,  name: '양념치킨',            price: 24500, category: '치킨', image: 'https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=500&q=80' },
  { id: 5,  name: '반반치킨',            price: 25000, category: '치킨', image: 'https://images.unsplash.com/photo-1626645738196-c2a7c87d8f5b?auto=format&fit=crop&w=500&q=80' },
  { id: 6,  name: '자메이카 통다리',     price: 25500, category: '치킨', image: 'https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?auto=format&fit=crop&w=500&q=80' },
  { id: 7,  name: '치즈볼(5개)',         price: 5500,  category: '사이드', image: 'https://images.unsplash.com/photo-1625944230945-1b7dd3b949ab?auto=format&fit=crop&w=500&q=80' },
  { id: 8,  name: '케이준 감자튀김',     price: 4500,  category: '사이드', image: 'https://images.unsplash.com/photo-1576107232684-1279f390859f?auto=format&fit=crop&w=500&q=80' },
  { id: 9,  name: '콜라 1.25L',          price: 3000,  category: '음료', image: 'https://images.unsplash.com/photo-1581636625402-29b2a704ef13?auto=format&fit=crop&w=500&q=80' },
  { id: 10, name: '사이다 1.25L',        price: 3000,  category: '음료', image: 'https://images.unsplash.com/photo-1581006852262-e4307cf6283a?auto=format&fit=crop&w=500&q=80' },
  { id: 11, name: '황금올리브+콜라세트', price: 26000, category: '세트', image: 'https://images.unsplash.com/photo-1512152272829-e3139592d56f?auto=format&fit=crop&w=500&q=80' },
  { id: 12, name: '반반+치즈볼세트',     price: 29900, category: '세트', image: 'https://images.unsplash.com/photo-1548340748-6d98d2fe7806?auto=format&fit=crop&w=500&q=80' },
])

// ── 장바구니 ──────────────────────────────────────────
const cart = ref([])

// ── 매출 데이터 ───────────────────────────────────────
const salesData = ref({
  total: 124800, card: 92800, cash: 32000, count: 5, isClosed: false,
})

const paymentHistory = ref([
  { id: 5, time: '20:42:17', method: '신용카드', items: '황금올리브치킨 외 1건',      amount: 29000 },
  { id: 4, time: '19:58:03', method: '현금',     items: '양념치킨 1마리',             amount: 24500 },
  { id: 3, time: '18:26:44', method: '신용카드', items: '반반치킨 외 2건',            amount: 36500 },
  { id: 2, time: '17:11:20', method: '신용카드', items: '자메이카 통다리 1마리',       amount: 25500 },
  { id: 1, time: '16:40:09', method: '현금',     items: '황금올리브순살 1마리',        amount: 24000 },
])

// ── Computed ──────────────────────────────────────────
const filteredProducts = computed(() => {
  let result = allProducts.value
  if (selectedCategory.value !== '전체') {
    result = result.filter(p => p.category === selectedCategory.value)
  }
  if (searchQuery.value.trim()) {
    result = result.filter(p => p.name.includes(searchQuery.value))
  }
  return result
})

const totalPrice    = computed(() => cart.value.reduce((s, i) => s + i.price * i.quantity, 0))
const totalQuantity = computed(() => cart.value.reduce((s, i) => s + i.quantity, 0))

// ── 유틸 ──────────────────────────────────────────────
function formatPrice(price) {
  return price.toLocaleString() + '원'
}

function showToastMsg(msg) {
  toast.value = { show: true, message: msg }
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value.show = false }, 3000)
}

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleDateString('ko-KR') + ' ' +
    now.toLocaleTimeString('ko-KR', { hour12: false })
}

// ── 장바구니 액션 ─────────────────────────────────────
function addToCart(product) {
  if (salesData.value.isClosed) {
    alert('영업이 마감되어 주문을 추가할 수 없습니다.')
    return
  }
  const existing = cart.value.find(i => i.id === product.id)
  if (existing) existing.quantity += 1
  else cart.value.push({ ...product, quantity: 1 })
}

function increaseQty(item) { item.quantity += 1 }
function decreaseQty(item) {
  if (item.quantity > 1) item.quantity -= 1
  else removeFromCart(item.id)
}
function removeFromCart(id) { cart.value = cart.value.filter(i => i.id !== id) }
function clearCart() {
  if (cart.value.length > 0 && confirm('장바구니를 모두 비우시겠습니까?')) cart.value = []
}

// ── 결제 ──────────────────────────────────────────────
function openPaymentModal() {
  if (cart.value.length === 0 || salesData.value.isClosed) return
  showPaymentModal.value = true
}

function processPayment(method) {
  const amount   = totalPrice.value
  const methodKr = method === 'card' ? '신용카드' : '현금'
  const itemsSummary = cart.value.length > 1
    ? `${cart.value[0].name} 외 ${totalQuantity.value - cart.value[0].quantity}건`
    : `${cart.value[0].name} ${cart.value[0].quantity}개`

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

// ── 마감 ──────────────────────────────────────────────
function toggleStoreStatus() {
  if (!salesData.value.isClosed) {
    showCloseModal.value = true
  } else {
    if (confirm('새로운 영업을 시작하시겠습니까?\n기존 매출 및 결제 내역이 모두 초기화됩니다.')) {
      Object.assign(salesData.value, { isClosed: false, total: 0, card: 0, cash: 0, count: 0 })
      paymentHistory.value = []
      showToastMsg('새로운 영업이 시작되었습니다.')
    }
  }
}

function confirmClose() {
  salesData.value.isClosed = true
  showCloseModal.value = false
  showToastMsg('성공적으로 영업이 마감되었습니다. 데이터가 본사로 전송됩니다.')
}

// ── 라이프사이클 ──────────────────────────────────────
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
  clearTimeout(toastTimer)
})
</script>
