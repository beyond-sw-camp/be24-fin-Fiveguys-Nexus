<script setup>
import { ClipboardList } from 'lucide-vue-next'
import { useAddOrderItem } from '@/composables/useAddOrderItem'
import ordersApi from '@/api/orders'

defineProps({
  orders: { type: Array, required: true },
})

const emit = defineEmits(['confirm', 'reject', 'refresh', 'delete-item'])

const { addItemForm, openAddItemForm, filteredProducts, selectAddItemProduct, clearAddItemProduct, submitAddItem } = useAddOrderItem(() => emit('refresh'))

let debounceTimer = null

function onCountChange(item) {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(async () => {
    if (item.count < 1) { item.count = 1 }
    try {
      await ordersApi.updateStoreItemCount(item.idx, { count: item.count })
    } catch (e) {
      console.error('수량 수정 실패', e)
      alert('수량 수정에 실패했습니다.')
      emit('refresh')
    }
  }, 500)
}
</script>

<template>
  <div class="space-y-4">
    <div v-for="order in orders" :key="order.idx" class="bg-white border border-gray-200 rounded-lg">
      <div class="px-5 py-3.5 border-b border-gray-100 flex justify-between items-center bg-gray-50/60">
        <div>
          <span class="text-xs font-mono text-gray-400">No.{{ order.idx }}</span>
          <p class="font-bold text-gray-900 mt-0.5 text-sm">자동 발주 제안</p>
          <p class="text-[11px] text-gray-400 mt-0.5">생성일시: {{ order.createdAt }}</p>
          <p v-if="order.reason" class="text-xs text-blue-600 mt-1 font-medium">AI 추천 사유: {{ order.reason }}</p>
        </div>
        <div class="flex gap-2">
          <button @click="emit('confirm', order)"
            class="px-4 py-2 bg-blue-500 text-white text-sm font-bold hover:bg-blue-600 rounded-lg transition-colors cursor-pointer">
            확정
          </button>
          <button @click="openAddItemForm(order)"
            class="px-4 py-2 border border-blue-200 text-blue-500 bg-blue-50 text-sm font-semibold hover:bg-blue-100 rounded-lg transition-colors cursor-pointer">
            + 품목 추가
          </button>
          <button @click="emit('reject', order)"
            class="px-4 py-2 border border-red-200 text-red-500 bg-red-50 text-sm font-semibold hover:bg-red-100 rounded-lg cursor-pointer">
            거절
          </button>
        </div>
      </div>
      <table class="w-full text-sm table-fixed">
        <colgroup>
          <col class="w-[35%]" />
          <col class="w-[12%]" />
          <col class="w-[18%]" />
          <col class="w-[22%]" />
          <col class="w-[13%]" />
        </colgroup>
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">품목명</th>
            <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">수량</th>
            <th class="px-5 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-wider">단가</th>
            <th class="px-5 py-3 text-right text-[10px] font-bold text-gray-400 uppercase tracking-wider">금액</th>
            <th class="px-3 py-3"></th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="item in order.ordersItemList" :key="item.idx" class="hover:bg-gray-50/50">
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ item.productName }}</td>
            <td class="px-5 py-3.5 font-semibold text-blue-600">
              <input v-model.number="item.count" type="number" min="1"
                @input="onCountChange(item)"
                class="w-16 px-2 py-1 rounded-lg border border-gray-200 text-sm outline-none focus:border-blue-400" />
            </td>
            <td class="px-5 py-3.5 text-gray-500">₩ {{ (item.unitPrice ?? 0).toLocaleString() }}</td>
            <td class="px-5 py-3.5 text-right font-semibold text-gray-700">
              ₩ {{ ((item.count || 0) * (item.unitPrice ?? 0)).toLocaleString() }}
            </td>
            <td class="px-3 py-3.5 text-center">
              <button @click="emit('delete-item', order, item)"
                class="px-2 py-1 text-xs font-semibold text-red-500 border border-red-200 bg-red-50 rounded-lg hover:bg-red-100 cursor-pointer">
                삭제
              </button>
            </td>
          </tr>
          <tr v-if="addItemForm?.ordersIdx === order.idx" class="bg-blue-50/50 border-t border-blue-100">
            <td class="px-5 py-3">
              <div class="relative">
                <div v-if="addItemForm.productIdx" class="flex items-center justify-between px-2 py-1.5 rounded-lg border border-blue-400 bg-blue-50 text-sm">
                  <span class="font-medium text-gray-900">{{ addItemForm.productName }}</span>
                  <button @click="clearAddItemProduct" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
                </div>
                <input v-else v-model="addItemForm.keyword" @input="addItemForm.showDropdown = true" @focus="addItemForm.showDropdown = true" placeholder="상품명 검색"
                  class="w-full px-2 py-1.5 rounded-lg border border-blue-200 text-sm outline-none focus:border-blue-400" />
                <ul v-if="!addItemForm.productIdx && addItemForm.showDropdown && filteredProducts(order).length > 0"
                  class="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                  <li v-for="p in filteredProducts(order)" :key="p.idx" @click="selectAddItemProduct(p)"
                    class="px-3 py-2 text-sm hover:bg-blue-50 cursor-pointer">
                    {{ p.productName }} <span class="text-gray-400 text-xs">{{ p.productUnit }}</span>
                  </li>
                </ul>
              </div>
            </td>
            <td class="px-5 py-3">
              <input v-model.number="addItemForm.count" type="number" min="1" placeholder="수량"
                class="w-20 px-2 py-1.5 rounded-lg border border-blue-200 text-sm outline-none focus:border-blue-400" />
            </td>
            <td class="px-5 py-3 text-xs text-gray-400">—</td>
            <td class="px-5 py-3">
              <div class="flex justify-end gap-1.5">
                <button @click="submitAddItem(order)"
                  class="px-3 py-1.5 text-xs font-semibold rounded-lg border border-blue-300 text-blue-600 bg-white hover:bg-blue-500 hover:text-white hover:cursor-pointer transition-colors">
                  추가
                </button>
                <button @click="addItemForm = null"
                  class="px-3 py-1.5 text-xs font-semibold rounded-lg border border-gray-200 text-gray-500 bg-white hover:bg-gray-100 hover:cursor-pointer transition-colors">
                  취소
                </button>
              </div>
            </td>
            <td></td>
          </tr>
        </tbody>
        <tfoot class="border-t border-gray-200 bg-gray-50/60">
          <tr>
            <td class="px-5 py-3 text-left text-xs font-bold text-gray-500">합계</td>
            <td colspan="2"></td>
            <td class="px-5 py-3 text-right font-black text-blue-600">
              ₩ {{ (order.price ?? 0).toLocaleString() }}
            </td>
            <td></td>
          </tr>
        </tfoot>
      </table>
    </div>

    <div v-if="orders.length === 0" class="bg-white border border-gray-200 py-12 text-center text-gray-400 rounded-lg">
      <ClipboardList class="w-10 h-10 mx-auto mb-2 opacity-20" />
      <p class="text-sm">현재 검토 대기 중인 발주서가 없습니다.</p>
      <p class="text-xs mt-1 text-gray-400">발주가 필요한 경우 본사에 문의하세요.</p>
    </div>
  </div>
</template>
