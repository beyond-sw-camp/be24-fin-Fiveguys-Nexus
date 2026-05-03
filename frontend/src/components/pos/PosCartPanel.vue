<template>
  <aside class="w-[360px] bg-white shadow-[-4px_0_15px_rgba(0,0,0,0.03)] flex flex-col border-l border-gray-200 shrink-0">
    <div class="h-14 px-5 border-b border-gray-100 flex justify-between items-center shrink-0">
      <h2 class="text-sm font-bold text-gray-900 flex items-center gap-2">
        <ShoppingCart class="w-4 h-4 text-gray-500" /> 현재 주문 내역
      </h2>
      <button
        type="button"
        @click="$emit('clear-cart')"
        class="text-xs text-gray-500 hover:text-red-500 border border-gray-200 px-3 py-1.5 rounded-md hover:bg-red-50 hover:border-red-100 transition-colors cursor-pointer">
        전체 삭제
      </button>
    </div>

    <div class="flex-1 overflow-y-auto p-4 flex flex-col gap-3 bg-[#F8F9FA]">
      <div v-if="cart.length === 0" class="h-full flex flex-col items-center justify-center text-gray-400">
        <div class="w-14 h-14 bg-gray-100 rounded-full flex items-center justify-center mb-3">
          <ShoppingBag class="w-6 h-6 text-gray-300" />
        </div>
        <p class="text-sm font-medium text-gray-500">상품을 담아주세요</p>
      </div>

      <div v-for="item in cart" :key="item.menuIdx" class="bg-white border border-gray-200 p-4">
        <div class="flex justify-between items-start mb-3">
          <span class="font-semibold text-gray-900 text-sm leading-tight">{{ item.name }}</span>
          <button type="button" @click="$emit('remove-item', item.menuIdx)" class="text-gray-300 hover:text-red-500 transition-colors ml-2 shrink-0 cursor-pointer">
            <X class="w-4 h-4" />
          </button>
        </div>
        <div class="flex justify-between items-center">
          <span class="font-bold text-gray-900">{{ formatPrice(item.price * item.quantity) }}</span>
          <div class="flex items-center border border-gray-200 rounded-lg overflow-hidden">
            <button type="button" @click="$emit('decrease-qty', item)" class="w-8 h-7 flex items-center justify-center bg-gray-50 text-gray-600 hover:bg-gray-100 hover:text-blue-500 transition-colors cursor-pointer">
              <Minus class="w-3 h-3" />
            </button>
            <span class="w-9 h-7 flex items-center justify-center text-xs font-bold text-gray-800 border-x border-gray-200">{{ item.quantity }}</span>
            <button type="button" @click="$emit('increase-qty', item)" class="w-8 h-7 flex items-center justify-center bg-gray-50 text-gray-600 hover:bg-gray-100 hover:text-blue-500 transition-colors cursor-pointer">
              <Plus class="w-3 h-3" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="p-5 border-t border-gray-200 bg-white shrink-0">
      <div class="flex justify-between text-gray-500 mb-1.5 text-sm font-medium">
        <span>총 수량</span>
        <span class="text-gray-900">{{ totalQuantity }} 개</span>
      </div>
      <div class="flex justify-between items-end mb-5">
        <span class="text-gray-500 font-medium text-sm">총 결제금액</span>
        <span class="text-2xl font-bold text-blue-600 tracking-tight">{{ formatPrice(totalPrice) }}</span>
      </div>
      <button
        type="button"
        @click="$emit('checkout')"
        :disabled="cart.length === 0 || isClosed"
        :class="['w-full py-3.5 rounded-xl font-bold text-sm flex items-center justify-center gap-2 transition-all text-white shadow-sm',
          (cart.length === 0 || isClosed) ? 'bg-gray-200 text-gray-400 cursor-not-allowed shadow-none' : 'bg-blue-500 hover:bg-blue-600 active:scale-[0.98] cursor-pointer']">
        {{ isClosed ? '마감됨 — 주문 불가' : '결제 진행하기' }}
      </button>
    </div>
  </aside>
</template>

<script setup>
import { ShoppingCart, ShoppingBag, X, Minus, Plus } from 'lucide-vue-next'
import { formatPrice } from '@/utils/formatPrice.js'

defineProps({
  cart: { type: Array, default: () => [] },
  totalPrice: { type: Number, default: 0 },
  totalQuantity: { type: Number, default: 0 },
  isClosed: { type: Boolean, default: false },
})

defineEmits(['clear-cart', 'remove-item', 'increase-qty', 'decrease-qty', 'checkout'])
</script>
