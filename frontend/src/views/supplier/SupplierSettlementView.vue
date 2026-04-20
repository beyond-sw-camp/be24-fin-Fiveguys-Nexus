<template>
  <div class="p-5 space-y-4">
    <div class="flex justify-between items-end">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">매출 정산</h1>
        <p class="page-spec-hint">협력사 포털 시연 — 납품 기준 정산·명세(본사 INCOME 도메인과 대응).</p>
      </div>
      <select v-model="selectedMonth"
        class="px-3 py-2 rounded-lg border border-gray-200 text-sm focus:ring-2 focus:ring-emerald-200 focus:border-emerald-400 outline-none bg-white">
        <option>2026-04</option>
        <option>2026-03</option>
        <option>2026-02</option>
      </select>
    </div>

    <!-- Summary -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
        <p class="text-sm text-gray-500">{{ selectedMonth }} 총 납품액</p>
        <p class="text-2xl font-bold text-gray-900 mt-1">₩{{ totalAmount.toLocaleString() }}</p>
      </div>
      <div class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
        <p class="text-sm text-gray-500">정산 완료</p>
        <p class="text-2xl font-bold text-emerald-600 mt-1">₩{{ paidAmount.toLocaleString() }}</p>
      </div>
      <div class="bg-white rounded-xl p-5 border border-gray-100 shadow-sm">
        <p class="text-sm text-gray-500">정산 대기</p>
        <p class="text-2xl font-bold mt-1"
          :class="pendingAmount > 0 ? 'text-yellow-600' : 'text-gray-400'">
          ₩{{ pendingAmount.toLocaleString() }}
        </p>
      </div>
    </div>

    <!-- Settlement table -->
    <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead class="bg-gray-50 text-gray-500 uppercase text-xs">
          <tr>
            <th class="px-6 py-4">납품 건</th>
            <th class="px-6 py-4">납품처</th>
            <th class="px-6 py-4">납품 기간</th>
            <th class="px-6 py-4">납품액</th>
            <th class="px-6 py-4">세금계산서</th>
            <th class="px-6 py-4">정산 상태</th>
            <th class="px-6 py-4 text-center">명세서</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="s in settlements" :key="s.id" class="hover:bg-gray-50/50">
            <td class="px-6 py-4 font-mono text-gray-400 text-xs">{{ s.id }}</td>
            <td class="px-6 py-4 font-semibold">{{ s.buyer }}</td>
            <td class="px-6 py-4 text-gray-500 text-xs">{{ s.period }}</td>
            <td class="px-6 py-4 font-bold">₩{{ s.amount.toLocaleString() }}</td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded text-xs font-medium"
                :class="s.taxInvoice ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-400'">
                {{ s.taxInvoice ? '발행완료' : '미발행' }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded-full text-xs font-bold"
                :class="s.status === '정산완료' ? 'bg-green-50 text-green-600' : 'bg-yellow-50 text-yellow-600'">
                {{ s.status }}
              </span>
            </td>
            <td class="px-6 py-4 text-center">
              <button class="text-gray-400 hover:text-gray-800">
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
  { id: 'STLM-2604-001', buyer: '한화 Nexus 본사', period: '2026-04-01 ~ 04-07', amount: 1200000, taxInvoice: true,  status: '정산완료' },
  { id: 'STLM-2604-002', buyer: '한화 Nexus 본사', period: '2026-04-08 ~ 04-13', amount: 2300000, taxInvoice: true,  status: '정산완료' },
  { id: 'STLM-2604-003', buyer: '한화 Nexus 본사', period: '2026-04-14 ~ 04-20', amount: 1800000, taxInvoice: false, status: '대기'     },
  { id: 'STLM-2604-004', buyer: '한화 Nexus 본사', period: '2026-04-21 ~ 04-30', amount: 2200000, taxInvoice: false, status: '대기'     },
])

const totalAmount   = computed(() => settlements.value.reduce((s, v) => s + v.amount, 0))
const paidAmount    = computed(() => settlements.value.filter(s => s.status === '정산완료').reduce((s, v) => s + v.amount, 0))
const pendingAmount = computed(() => totalAmount.value - paidAmount.value)
</script>
