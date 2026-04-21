<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900">AI 보고서</h1>
        <p class="text-sm text-gray-500 mt-0.5">AI가 생성한 보고서와 뉴스 요약을 확인할 수 있습니다.</p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex gap-1 mb-4 border-b border-gray-200">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="activeTab = tab.value"
        class="px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px"
        :class="activeTab === tab.value
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- AI 보고서 탭 -->
    <div v-if="activeTab === 'report'" class="bg-white rounded-lg border border-gray-200 overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-gray-100 bg-gray-50/70">
            <th class="text-left px-5 py-3 font-semibold text-gray-600 w-20">번호</th>
            <th class="text-left px-5 py-3 font-semibold text-gray-600">보고서 제목</th>
            <th class="text-left px-5 py-3 font-semibold text-gray-600 w-40">생성일시</th>
            <th class="text-center px-5 py-3 font-semibold text-gray-600 w-28">다운로드</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="reports.length === 0">
            <td colspan="4" class="px-5 py-16 text-center text-gray-400 text-sm">
              생성된 보고서가 없습니다.
            </td>
          </tr>
          <tr
            v-for="report in reports"
            :key="report.idx"
            class="hover:bg-gray-50/50 transition-colors"
          >
            <td class="px-5 py-3.5 text-gray-400">{{ report.idx }}</td>
            <td class="px-5 py-3.5">
              <div class="flex items-center gap-2">
                <FileText class="w-4 h-4 text-[#F37321] shrink-0" />
                <span class="font-medium text-gray-800">{{ report.report_title }}</span>
              </div>
            </td>
            <td class="px-5 py-3.5 text-gray-500">{{ report.created_at }}</td>
            <td class="px-5 py-3.5 text-center">
              <button
                @click="handleDownload(report)"
                class="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-[#F37321] border border-[#F37321] rounded hover:bg-orange-50 transition-colors"
              >
                <Download class="w-3.5 h-3.5" />
                PDF
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 뉴스 요약 탭 -->
    <div v-if="activeTab === 'news'" class="flex flex-col gap-3">
      <p class="text-xs text-gray-400">매일 오전 9시 AI가 치킨 업계 관련 뉴스를 수집·요약합니다.</p>
      <div v-if="newsSummaries.length === 0" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
        수집된 뉴스가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { FileText, Download } from 'lucide-vue-next'

const tabs = [
  { label: 'AI 보고서', value: 'report' },
  { label: '뉴스 요약', value: 'news' },
]

const activeTab = ref('report')

const reports = ref([
  {
    idx: 5,
    report_title: '2026년 4월 가맹점별 매출 비교 보고서',
    created_at: '2026-04-20 14:32',
    file_path: '/reports/report_5.pdf',
  },
  {
    idx: 4,
    report_title: '3월 vs 4월 총 매출 비교 보고서',
    created_at: '2026-04-18 09:15',
    file_path: '/reports/report_4.pdf',
  },
  {
    idx: 3,
    report_title: '2026년 1분기 발주 품목 통계 보고서',
    created_at: '2026-04-10 16:48',
    file_path: '/reports/report_3.pdf',
  },
  {
    idx: 2,
    report_title: '판교테크노밸리점 월별 정산 분석 보고서',
    created_at: '2026-04-05 11:20',
    file_path: '/reports/report_2.pdf',
  },
  {
    idx: 1,
    report_title: '2026년 3월 전체 가맹점 재고 현황 보고서',
    created_at: '2026-04-01 10:00',
    file_path: '/reports/report_1.pdf',
  },
])

const newsSummaries = ref([])

function handleDownload(report) {
  alert(`${report.report_title} 다운로드 준비 중입니다.`)
}
</script>
