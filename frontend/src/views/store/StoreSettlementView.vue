<template>
  <div class="p-5 space-y-8">

    <div class="flex gap-6 border-b border-gray-200 pb-2">
      <button
        @click="activeTab = 'sales'"
        class="cursor-pointer"
        :class="activeTab === 'sales'
          ? 'text-blue-600 font-bold border-b-2 border-blue-600 pb-2'
          : 'text-gray-400'"
      >
        매출 정산 내역
      </button>

      <button
        @click="activeTab = 'order'"
        class="cursor-pointer"
        :class="activeTab === 'order'
          ? 'text-blue-600 font-bold border-b-2 border-blue-600 pb-2'
          : 'text-gray-400'"
      >
        발주 정산 내역
      </button>
    </div>


    <!-- ================= 매출 정산 내역 ================= -->
    <div v-if="activeTab === 'sales'">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">매출 정산 내역</h1>

        <div class="flex gap-2 mt-3">
          <select v-model="salesYear" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">년도</option>
            <option v-for="y in years" :key="y">{{ y }}</option>
          </select>

          <select v-model="salesMonth" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">월</option>
            <option v-for="m in months" :key="m">{{ m }}</option>
          </select>

          <select v-model="salesWeek" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">주간</option>
            <option v-for="w in weeks" :key="w" :value="w">{{ w }}주차</option>
          </select>

          <select v-model="salesDay" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">일</option>
            <option v-for="d in days" :key="d">{{ d }}</option>
          </select>
        </div>

        <div class="grid grid-cols-2 gap-4 mt-4">
          <div class="bg-white border border-gray-200 rounded-lg">
            <div class="p-5">
              <p class="text-xs font-bold text-gray-400">
                {{ salesYear }}-{{ salesMonth }} 총 매출액
              </p>
              <p class="text-2xl font-black text-gray-900 mt-2">
                ₩ {{ salesTotal.toLocaleString() }}
              </p>
            </div>
          </div>

          <div class="bg-white border border-gray-200 rounded-lg">
            <div class="p-5">
              <p class="text-xs font-bold text-gray-400">계산 완료</p>
              <p class="text-2xl font-black text-green-600 mt-2">
                ₩ {{ salesPaid.toLocaleString() }}
              </p>
            </div>
          </div>
        </div>

        <div class="bg-white border border-gray-200 rounded-lg overflow-hidden mt-4">
          <table class="w-full text-sm text-left">
            <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] text-gray-400">매출 항목</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">판매 건수</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">판매 기간</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">금액</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">상태</th>
            </tr>
            </thead>

            <tbody class="divide-y divide-gray-100">
            <tr v-for="s in salesFiltered" :key="s.id" class="hover:bg-gray-50/50">
              <td class="px-5 py-3 font-semibold">{{ s.item }}</td>
              <td class="px-5 py-3 text-gray-600">{{ s.count }}건</td>
              <td class="px-5 py-3 text-xs text-gray-400">{{ s.period }}</td>
              <td class="px-5 py-3 font-bold">
                ₩ {{ s.amount.toLocaleString() }}
              </td>
              <td class="px-5 py-3">
                  <span class="text-xs px-2 py-0.5 rounded"
                        :class="s.status === '계산완료'
                      ? 'bg-green-50 text-green-700'
                      : 'bg-amber-50 text-amber-600'">
                    {{ s.status }}
                  </span>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>


    <!-- ================= 발주 정산 내역 ================= -->
    <div v-if="activeTab === 'order'">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">발주 정산 내역</h1>

        <div class="flex gap-2 mt-3">
          <select v-model="selectedYear" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">년도</option>
            <option v-for="y in years" :key="y">{{ y }}</option>
          </select>

          <select v-model="selectedMonth" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">월</option>
            <option v-for="m in months" :key="m">{{ m }}</option>
          </select>

          <select v-model="selectedWeek" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">주간</option>
            <option v-for="w in weeks" :key="w" :value="w">{{ w }}주차</option>
          </select>

          <select v-model="selectedDay" class="px-3 py-2 rounded-lg border border-gray-200 text-sm bg-white">
            <option value="">일</option>
            <option v-for="d in days" :key="d">{{ d }}</option>
          </select>
        </div>

        <div class="grid grid-cols-2 gap-4 mt-4">
          <div class="bg-white border border-gray-200 rounded-lg">
            <div class="p-5">
              <p class="text-xs font-bold text-gray-400">
                {{ selectedYear }}-{{ selectedMonth }} 총 납부액
              </p>
              <p class="text-2xl font-black text-gray-900 mt-2">
                ₩ {{ totalAmount.toLocaleString() }}
              </p>
            </div>
          </div>

          <div class="bg-white border border-gray-200 rounded-lg">
            <div class="p-5">
              <p class="text-xs font-bold text-gray-400">납부 완료</p>
              <p class="text-2xl font-black text-green-600 mt-2">
                ₩ {{ paidAmount.toLocaleString() }}
              </p>
            </div>
          </div>
        </div>

        <div class="bg-white border border-gray-200 rounded-lg overflow-hidden mt-4">
          <table class="w-full text-sm text-left">
            <thead>
            <tr class="border-b border-gray-200 bg-gray-50">
              <th class="px-5 py-3 text-[10px] text-gray-400">정산 항목</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">발주 건수</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">정산 기간</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">금액</th>
              <th class="px-5 py-3 text-[10px] text-gray-400">상태</th>
              <th class="px-5 py-3 text-[10px] text-gray-400 text-center">명세서</th>
            </tr>
            </thead>

            <tbody class="divide-y divide-gray-100">
            <tr v-for="s in filteredSettlements" :key="s.id"
                class="hover:bg-gray-50/50">
              <td class="px-5 py-3 font-semibold">{{ s.item }}</td>
              <td class="px-5 py-3 text-gray-600">{{ s.count }}건</td>
              <td class="px-5 py-3 text-xs text-gray-400">{{ s.period }}</td>
              <td class="px-5 py-3 font-bold">
                ₩ {{ s.amount.toLocaleString() }}
              </td>
              <td class="px-5 py-3">
                  <span class="text-xs px-2 py-0.5 rounded"
                        :class="s.status === '납부완료'
                      ? 'bg-green-50 text-green-700'
                      : 'bg-amber-50 text-amber-600'">
                    {{ s.status }}
                  </span>
              </td>
              <td class="px-5 py-3.5 text-center">
                <button
                  type="button"
                  class="text-gray-300 hover:text-[#F37321] transition-colors cursor-pointer"
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
    </div>
    <Toast :show="toast.show" :message="toast.message" :type="toast.type" />
  </div>
</template>

<script setup>
import { Download } from 'lucide-vue-next'
import { ref, computed } from 'vue'
import Toast from '@/components/common/Toast.vue'
import { useToast } from '@/composables/useToast'

const { toast, showToast } = useToast()

const activeTab = ref('sales')

const years = ['2026', '2025']
const months = ['01','02','03','04']
const weeks = [1,2,3,4,5]
const days = Array.from({ length: 31 }, (_, i) => String(i+1).padStart(2,'0'))

const getWeek = (day) => Math.ceil(Number(day)/7)

/* 매출 */
const salesYear = ref('2026')
const salesMonth = ref('04')
const salesWeek = ref('')
const salesDay = ref('')

const salesData = ref([
  { id:1, year:'2026', month:'04', day:'01', item:'한우 등심 오마카세 + 생수', count:1, period:'04-01', amount:184000, status:'계산완료' },
  { id:2, year:'2026', month:'04', day:'01', item:'한우 안심 스테이크 + 콜라', count:1, period:'04-01', amount:125000, status:'계산완료' },

  { id:3, year:'2026', month:'04', day:'02', item:'오마카세 런치 세트 외 1건', count:1, period:'04-02', amount:156000, status:'계산완료' },
  { id:4, year:'2026', month:'04', day:'02', item:'한우 육회 + 생수', count:1, period:'04-02', amount:49000, status:'계산완료' },

  { id:5, year:'2026', month:'04', day:'03', item:'한우 2인 코스', count:1, period:'04-03', amount:220000, status:'계산완료' },
  { id:6, year:'2026', month:'04', day:'03', item:'비즈니스 런치 + 유자차', count:1, period:'04-03', amount:91000, status:'계산완료' },

  { id:7, year:'2026', month:'04', day:'04', item:'한우 등심 오마카세 외 2건', count:1, period:'04-04', amount:210000, status:'계산완료' },
  { id:8, year:'2026', month:'04', day:'04', item:'계절 채소 샐러드 + 된장국', count:1, period:'04-04', amount:30000, status:'계산완료' },

  { id:9, year:'2026', month:'04', day:'05', item:'한우 안심 스테이크 + 콜라 + 공기밥', count:1, period:'04-05', amount:128000, status:'계산완료' },

  { id:10, year:'2026', month:'04', day:'06', item:'오마카세 런치 세트', count:1, period:'04-06', amount:150000, status:'계산완료' },

  { id:11, year:'2026', month:'04', day:'07', item:'한우 2인 코스 + 유자차 2잔', count:1, period:'04-07', amount:232000, status:'계산완료' },

  { id:12, year:'2026', month:'04', day:'08', item:'한우 육회 + 된장국 + 콜라', count:1, period:'04-08', amount:62000, status:'계산완료' },

  { id:13, year:'2026', month:'03', day:'15', item:'비즈니스 런치 + 생수', count:1, period:'03-15', amount:89000, status:'계산완료' },
  { id:14, year:'2026', month:'03', day:'20', item:'한우 등심 오마카세 + 유자차', count:1, period:'03-20', amount:186000, status:'계산완료' },

  { id:15, year:'2025', month:'04', day:'05', item:'한우 2인 코스 + 콜라 2잔', count:1, period:'04-05', amount:230000, status:'계산완료' },
  { id:16, year:'2025', month:'04', day:'08', item:'오마카세 런치 세트 + 생수', count:1, period:'04-08', amount:154000, status:'계산완료' }
])

const salesFiltered = computed(() =>
  salesData.value.filter(s =>
    s.year === salesYear.value &&
    s.month === salesMonth.value &&
    (!salesWeek.value || getWeek(s.day) === Number(salesWeek.value)) &&
    (!salesDay.value || s.day === salesDay.value)
  )
)

const salesTotal = computed(() =>
  salesFiltered.value.reduce((s,v)=>s+v.amount,0)
)

const salesPaid = computed(() =>
  salesFiltered.value.filter(s => s.status === '계산완료').reduce((s, v) => s + v.amount, 0)
)

/* 발주 */
const selectedYear = ref('2026')
const selectedMonth = ref('04')
const selectedWeek = ref('')
const selectedDay = ref('')


const settlements = ref([
  { id:1, year:'2026', month:'04', day:'01', item:'한우 등심', count:20, period:'04-01', amount:1700000, status:'납부완료' },
  { id:2, year:'2026', month:'04', day:'02', item:'올리브오일', count:8, period:'04-02', amount:96000, status:'납부완료' },
  { id:3, year:'2026', month:'04', day:'03', item:'버터', count:15, period:'04-03', amount:135000, status:'납부완료' },
  { id:4, year:'2026', month:'04', day:'04', item:'간장', count:12, period:'04-04', amount:48000, status:'납부완료' },
  { id:5, year:'2026', month:'04', day:'05', item:'생크림', count:20, period:'04-05', amount:140000, status:'납부완료' },

  { id:6, year:'2026', month:'04', day:'06', item:'양파', count:25, period:'04-06', amount:37500, status:'납부완료' },
  { id:7, year:'2026', month:'04', day:'07', item:'마늘', count:18, period:'04-07', amount:144000, status:'납부완료' },
  { id:8, year:'2026', month:'04', day:'08', item:'콜라(박스)', count:10, period:'04-08', amount:150000, status:'납부완료' },
  { id:9, year:'2026', month:'04', day:'09', item:'생수(박스)', count:9, period:'04-09', amount:72000, status:'납부완료' },

  { id:10, year:'2026', month:'04', day:'10', item:'한우 등심', count:25, period:'04-10', amount:2125000, status:'납부완료' },
  { id:11, year:'2026', month:'04', day:'11', item:'한우 안심', count:10, period:'04-11', amount:950000, status:'납부완료' },
  { id:12, year:'2026', month:'04', day:'12', item:'올리브오일', count:10, period:'04-12', amount:120000, status:'납부완료' },

  { id:13, year:'2026', month:'03', day:'15', item:'생크림', count:22, period:'03-15', amount:154000, status:'납부완료' },
  { id:14, year:'2026', month:'03', day:'20', item:'양파', count:30, period:'03-20', amount:45000, status:'납부완료' },

  { id:15, year:'2025', month:'04', day:'05', item:'한우 등심', count:18, period:'04-05', amount:1530000, status:'납부완료' },
  { id:16, year:'2025', month:'04', day:'08', item:'버터', count:25, period:'04-08', amount:225000, status:'납부완료' }
])

const filteredSettlements = computed(() =>
  settlements.value.filter(s =>
    s.year === selectedYear.value &&
    s.month === selectedMonth.value &&
    (!selectedWeek.value || getWeek(s.day) === Number(selectedWeek.value)) &&
    (!selectedDay.value || s.day === selectedDay.value)
  )
)

const totalAmount = computed(() =>
  filteredSettlements.value.reduce((s,v)=>s+v.amount,0)
)

const paidAmount = computed(() =>
  filteredSettlements.value.filter(s => s.status === '납부완료').reduce((s, v) => s + v.amount, 0)
)

function downloadStatement(row){
  showToast(`${row.item} 다운로드`)
}
</script>
