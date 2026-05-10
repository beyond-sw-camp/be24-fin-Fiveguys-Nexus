<template>
  <div class="p-5 space-y-8">
    <div class="flex gap-6 border-b border-gray-200 pb-2">
      <button
        @click="activeTab = 'sales'"
        class="cursor-pointer transition-colors"
        :class="activeTab === 'sales'
          ? 'text-blue-600 font-bold border-b-2 border-blue-600 pb-2'
          : 'text-gray-400 hover:text-gray-600'"
      >
        매출 정산 내역
      </button>

      <button
        @click="activeTab = 'order'"
        class="cursor-pointer transition-colors"
        :class="activeTab === 'order'
          ? 'text-blue-600 font-bold border-b-2 border-blue-600 pb-2'
          : 'text-gray-400 hover:text-gray-600'"
      >
        발주 정산 내역
      </button>
    </div>

    <div v-if="activeTab === 'sales'" class="space-y-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매출 정산 내역</h1>

        <div class="flex gap-2 mt-3">
          <select v-model="salesYear" @change="fetchSalesData" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white outline-none focus:border-blue-500">
            <option value="2026">2026년</option>
            <option value="2025">2025년</option>
          </select>
          <select v-model="salesMonth" @change="fetchSalesData" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white outline-none focus:border-blue-500">
            <option v-for="m in 12" :key="m" :value="String(m).padStart(2, '0')">{{ m }}월</option>
          </select>
        </div>

        <div class="mt-6">
          <div class="bg-gray-50 p-6 rounded-2xl border border-gray-200 w-full shadow-sm text-left">
            <p class="text-sm font-medium text-gray-500 mb-1">{{ salesYear }}-{{ salesMonth }} 총 매출액</p>
            <p class="text-3xl font-bold text-blue-600">₩ {{ totalSalesAmount.toLocaleString() }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm">
        <table class="w-full text-sm text-left">
          <thead class="bg-gray-50 border-b border-gray-200 text-gray-500 font-bold">
          <tr>
            <th class="px-6 py-4">결제일</th>
            <th class="px-6 py-4">판매 항목</th>
            <th class="px-6 py-4">수량</th>
            <th class="px-6 py-4">결제 금액</th>
            <th class="px-6 py-4">결제 상태</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
          <tr v-for="item in salesData" :key="item.posPayIdx" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-6 py-4 text-gray-600">{{ item.paidDate }}</td>
            <td class="px-6 py-4 font-semibold text-gray-900">{{ item.menuNames }}</td>
            <td class="px-6 py-4 text-gray-600">{{ item.payCount }}건</td>
            <td class="px-6 py-4 font-bold text-gray-900">₩ {{ item.payAmount?.toLocaleString() }}</td>
            <td class="px-6 py-4">
                <span class="px-2.5 py-1 rounded-full text-xs font-bold bg-blue-50 text-blue-600 border border-blue-100">
                  결제완료
                </span>
            </td>
          </tr>
          <tr v-if="salesData.length === 0">
            <td colspan="5" class="px-6 py-10 text-center text-gray-400">해당 기간의 매출 내역이 없습니다.</td>
          </tr>
          </tbody>
        </table>

        <div v-if="salesTotalPages > 1" class="flex justify-center items-center gap-2 py-4 border-t border-gray-100">
          <button
            @click="changeSalesPage(salesCurrentPage - 1)"
            :disabled="salesCurrentPage === 0"
            class="p-2 border rounded-lg hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M15 19l-7-7 7-7" stroke-width="2"/></svg>
          </button>
          <div class="flex gap-1">
            <button
              v-for="p in salesTotalPages"
              :key="p"
              @click="changeSalesPage(p - 1)"
              class="w-9 h-9 text-sm font-semibold rounded-lg transition-colors"
              :class="salesCurrentPage === p - 1 ? 'bg-blue-600 text-white' : 'bg-white border border-gray-200 text-gray-600 hover:bg-gray-50'">
              {{ p }}
            </button>
          </div>
          <button
            @click="changeSalesPage(salesCurrentPage + 1)"
            :disabled="salesCurrentPage === salesTotalPages - 1"
            class="p-2 border rounded-lg hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="2"/></svg>
          </button>
        </div>
      </div>
    </div>

    <div v-if="activeTab === 'order'" class="space-y-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주 정산 내역</h1>

        <div class="flex gap-2 mt-3">
          <select v-model="orderYear" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white outline-none focus:border-blue-500">
            <option value="2026">2026년</option>
            <option value="2025">2025년</option>
          </select>
          <select v-model="orderMonth" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white outline-none focus:border-blue-500">
            <option v-for="m in 12" :key="m" :value="String(m).padStart(2, '0')">{{ m }}월</option>
          </select>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">
          <div class="bg-orange-50 p-6 rounded-2xl border border-orange-100 shadow-sm">
            <p class="text-sm font-medium text-orange-600 mb-1">총 납부액</p>
            <p class="text-2xl font-bold text-gray-900">₩ {{ totalOrderAmount.toLocaleString() }}</p>
          </div>
          <div class="bg-green-50 p-6 rounded-2xl border border-green-100 shadow-sm">
            <p class="text-sm font-medium text-red-600 mb-1">미납금</p>
            <p class="text-2xl font-bold text-gray-900">₩ 0</p>
          </div>
        </div>
      </div>

      <div class="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm">
        <table class="w-full text-sm text-left">
          <thead class="bg-gray-50 border-b border-gray-200 text-gray-500 font-bold">
          <tr>
            <th class="px-6 py-4">날짜</th>
            <th class="px-6 py-4">발주 제품</th>
            <th class="px-6 py-4">수량</th>
            <th class="px-6 py-4">정산 금액</th>
            <th class="px-6 py-4">납부 상태</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
          <tr v-for="item in filteredOrders" :key="item.id" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-6 py-4 text-gray-600">{{ item.period }}</td>
            <td class="px-6 py-4 font-semibold text-gray-900">{{ item.item }}</td>
            <td class="px-6 py-4 text-gray-600">{{ item.count }}개</td>
            <td class="px-6 py-4 font-bold text-gray-900">₩ {{ item.amount.toLocaleString() }}</td>
            <td class="px-6 py-4">
                <span class="px-2.5 py-1 rounded-full text-xs font-bold bg-green-50 text-green-600 border border-green-100">
                  납부완료
                </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getSettlement } from '@/api/store' // api/index.js 경로에 맞게 수정 필요

const activeTab = ref('sales')

// --- 매출 상태 관리 ---
const salesYear = ref('2026')
const salesMonth = ref('04')
const salesData = ref([])
const totalSalesAmount = ref(0)
const salesCurrentPage = ref(0)
const salesTotalPages = ref(1)
const salesPageSize = 10

// 매출 API 호출
const fetchSalesData = async () => {
  try {
    const response = await getSettlement(salesYear.value, salesMonth.value, salesCurrentPage.value, salesPageSize)
    const result = response.data.result // BaseResponse의 result 매핑

    if (result) {
      totalSalesAmount.value = result.monthlyTotalAmount || 0
      if (result.payHistory) {
        salesData.value = result.payHistory.content || []
        salesTotalPages.value = result.payHistory.totalPages || 1
      }
    }
  } catch (error) {
    console.error('매출 데이터를 불러오는데 실패했습니다:', error)
  }
}

const changeSalesPage = (page) => {
  salesCurrentPage.value = page
  fetchSalesData()
}

// 필터 변경 시 1페이지부터 다시 불러오기
watch([salesYear, salesMonth], () => {
  salesCurrentPage.value = 0
})


// --- 발주 데이터 (Mock 유지) ---
const orderYear = ref('2026')
const orderMonth = ref('04')

const orderData = ref([
  { id: 10, year: '2026', month: '04', day: '10', item: '한우 등심', count: 25, period: '04-10', amount: 2125000 },
  { id: 11, year: '2026', month: '04', day: '11', item: '한우 안심', count: 10, period: '04-11', amount: 950000 },
  { id: 12, year: '2026', month: '04', day: '12', item: '올리브오일', count: 10, period: '04-12', amount: 120000 },
  { id: 13, year: '2026', month: '03', day: '15', item: '생크림', count: 22, period: '03-15', amount: 154000 },
])

const filteredOrders = computed(() => {
  return orderData.value.filter(d => d.year === orderYear.value && d.month === orderMonth.value)
})

const totalOrderAmount = computed(() => {
  return filteredOrders.value.reduce((acc, cur) => acc + cur.amount, 0)
})

// --- 탭 전환 시 조회 ---
watch(activeTab, (newTab) => {
  if (newTab === 'sales' && salesData.value.length === 0) {
    fetchSalesData()
  }
})

onMounted(() => {
  fetchSalesData()
})
</script>
