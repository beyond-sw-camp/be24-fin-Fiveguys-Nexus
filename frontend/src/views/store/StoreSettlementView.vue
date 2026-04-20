<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">정산 내역</h1>
        <p class="page-spec-hint">
          <code>INCOME_004·005</code>본인 가맹점 정산(일·주·월), 거래 명세서 PDF 다운로드.
        </p>
      </div>
      <select v-model="selectedMonth"
        class="px-3 py-2 rounded border border-gray-200 text-sm focus:border-blue-400 focus:ring-2 focus:ring-blue-100 outline-none bg-white">
        <option>2026-04</option>
        <option>2026-03</option>
        <option>2026-02</option>
      </select>
    </div>

    <!-- Summary -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ selectedMonth }} 총 납부액</p>
          <p class="text-2xl font-black text-gray-900 mt-2">₩{{ totalAmount.toLocaleString() }}</p>
        </div>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">정산 완료</p>
          <p class="text-2xl font-black text-green-600 mt-2">₩{{ paidAmount.toLocaleString() }}</p>
        </div>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">미납 잔액</p>
          <p class="text-2xl font-black mt-2"
            :class="(totalAmount - paidAmount) > 0 ? 'text-red-600' : 'text-gray-400'">
            ₩{{ (totalAmount - paidAmount).toLocaleString() }}
          </p>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">정산 항목</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">발주 건수</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">정산 기간</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">금액</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">명세서</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="s in settlements" :key="s.item" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ s.item }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ s.count }}건</td>
            <td class="px-5 py-3.5 text-xs text-gray-400 font-mono">{{ s.period }}</td>
            <td class="px-5 py-3.5 font-bold text-gray-900">₩{{ s.amount.toLocaleString() }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="s.status === '납부완료'
                  ? 'bg-green-50 text-green-700 border border-green-200'
                  : 'bg-amber-50 text-amber-600 border border-amber-200'">
                {{ s.status }}
              </span>
            </td>
            <td class="px-5 py-3.5 text-center">
              <button
                type="button"
                class="text-gray-300 hover:text-blue-600 transition-colors"
                title="거래 명세서 다운로드"
                @click="downloadStatement(s)"
              >
                <Download class="w-4 h-4 mx-auto" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Download } from 'lucide-vue-next'

const selectedMonth = ref('2026-04')

const settlements = ref([
  { item: '유제품 (서울우유)',  count: 6,  period: '2026-04-01 ~ 04-13', amount: 3200000, status: '납부완료' },
  { item: '원두 (동서식품)',    count: 3,  period: '2026-04-01 ~ 04-13', amount: 4800000, status: '미납'     },
  { item: '시럽·소스 (청정원)', count: 2,  period: '2026-04-01 ~ 04-13', amount: 980000,  status: '미납'     },
  { item: '포장재 (한국포장)',  count: 4,  period: '2026-04-01 ~ 04-13', amount: 540000,  status: '납부완료' },
])

const totalAmount = computed(() => settlements.value.reduce((s, v) => s + v.amount, 0))
const paidAmount  = computed(() => settlements.value.filter(s => s.status === '납부완료').reduce((s, v) => s + v.amount, 0))

function downloadStatement(row) {
  alert(`[INCOME_005] ${row.item} 거래 명세서(PDF) 다운로드 시뮬레이션\n${row.period}`)
}
</script>
