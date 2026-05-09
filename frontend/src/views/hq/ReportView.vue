<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900">AI 보고서</h1>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex gap-1 mb-4 border-b border-gray-200">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="activeTab = tab.value"
        class="px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px cursor-pointer"
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
                class="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-[#F37321] border border-[#F37321] rounded hover:bg-orange-50 transition-colors cursor-pointer"
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
      <p class="text-xs text-gray-400">매일 오전 9시 AI가 푸드코트 업계 관련 뉴스를 수집·요약합니다.</p>

      <div v-if="newsSummaries.length === 0" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
        수집된 뉴스가 없습니다.
      </div>

      <!-- 뉴스 목록 (간략 표시) -->
      <button
        v-for="news in newsSummaries"
        :key="news.idx"
        @click="selectedNews = news"
        class="w-full bg-white rounded-lg border border-gray-200 p-4 flex items-start justify-between gap-4 hover:border-[#F37321] hover:bg-orange-50/20 transition-colors text-left cursor-pointer"
      >
        <div class="flex items-start gap-3 min-w-0">
          <Newspaper class="w-4 h-4 text-[#F37321] shrink-0 mt-0.5" />
          <div class="min-w-0">
            <p class="font-semibold text-gray-900 text-sm">{{ news.summary_title }}</p>
            <p class="text-xs text-gray-500 mt-1 line-clamp-1">{{ firstLine(news.summary_content) }}</p>
          </div>
        </div>
        <span class="text-xs text-gray-400 shrink-0">{{ news.summary_date }}</span>
      </button>
    </div>

    <!-- 뉴스 상세 모달 -->
    <div v-if="selectedNews" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="selectedNews = null"></div>
      <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl mx-4 max-h-[80vh] flex flex-col overflow-hidden">

        <!-- 모달 헤더 -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100 shrink-0">
          <div class="flex items-center gap-2">
            <Newspaper class="w-4 h-4 text-[#F37321]" />
            <span class="font-bold text-gray-900 text-sm">{{ selectedNews.summary_title }}</span>
          </div>
          <button @click="selectedNews = null" class="text-gray-400 hover:text-gray-700 transition-colors cursor-pointer">
            <X class="w-5 h-5" />
          </button>
        </div>

        <!-- 모달 내용 -->
        <div class="overflow-y-auto px-6 py-5 flex flex-col gap-4">
          <!-- 날짜 -->
          <p class="text-xs text-gray-400">{{ selectedNews.summary_date }}</p>

          <!-- 요약 내용 -->
          <div class="bg-gray-50 rounded-lg px-4 py-3">
            <p class="text-xs font-semibold text-gray-500 mb-2 uppercase tracking-wide">요약</p>
            <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-line">{{ selectedNews.summary_content }}</p>
          </div>

          <!-- 운영 조언 -->
          <div class="flex gap-2 bg-orange-50 border border-orange-100 rounded-lg px-4 py-3">
            <Lightbulb class="w-4 h-4 text-[#F37321] shrink-0 mt-0.5" />
            <div>
              <p class="text-xs font-semibold text-[#F37321] mb-1">운영 조언</p>
              <p class="text-sm text-gray-700 leading-relaxed">{{ selectedNews.advice }}</p>
            </div>
          </div>

          <!-- 원문 링크 -->
          <div v-if="selectedNews.urls?.length">
            <p class="text-xs font-semibold text-gray-500 mb-2">원문 링크</p>
            <div class="flex flex-wrap gap-2">
              <a
                v-for="(url, i) in selectedNews.urls"
                :key="i"
                :href="url"
                target="_blank"
                rel="noopener noreferrer"
                class="inline-flex items-center gap-1 text-xs text-blue-500 hover:underline"
              >
                <ExternalLink class="w-3 h-3" />
                원문 {{ i + 1 }}
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Toast :show="toast.show" :message="toast.message" :type="toast.type" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { FileText, Download, Newspaper, Lightbulb, ExternalLink, X } from 'lucide-vue-next'
import Toast from '@/components/common/Toast.vue'
import { useToast } from '@/composables/useToast'

const { toast, showToast } = useToast()

const tabs = [
  { label: '보고서', value: 'report' },
  { label: '뉴스 요약', value: 'news' },
]

const activeTab = ref('report')
const selectedNews = ref(null)

const reports = ref([
  {
    idx: 5,
    report_title: '2026년 4월 입점 매장별 매출 비교 보고서',
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
    report_title: '일식 스시바 월별 정산 분석 보고서',
    created_at: '2026-04-05 11:20',
    file_path: '/reports/report_2.pdf',
  },
  {
    idx: 1,
    report_title: '2026년 3월 전체 입점 매장 재고 현황 보고서',
    created_at: '2026-04-01 10:00',
    file_path: '/reports/report_1.pdf',
  },
])

const newsSummaries = ref([
  {
    idx: 3,
    summary_title: '2026-04-21 푸드코트 업계 주요 이슈 요약',
    summary_date: '2026-04-21 09:00',
    summary_content: '1. 국내 한우 도매가격이 전주 대비 5% 상승하며 공급 부족 우려가 커지고 있습니다. 봄철 수요 증가와 사육 두수 감소가 주요 원인으로 분석됩니다.\n2. 공정거래위원회가 백화점 푸드코트 입점 매장의 수수료 실태 점검에 착수했습니다.\n3. 올리브오일 주요 산지인 지중해 지역의 작황 부진으로 수입 올리브오일 가격 인상 가능성이 제기되고 있습니다.',
    advice: '한우 도매가 상승 추세에 따라 단기 재고 확보를 검토하시고, 올리브오일 가격 동향을 모니터링하여 구매 시점을 조율하는 것을 권장합니다.',
    urls: ['https://example.com/news/1', 'https://example.com/news/2', 'https://example.com/news/3'],
  },
  {
    idx: 2,
    summary_title: '2026-04-20 푸드코트 업계 주요 이슈 요약',
    summary_date: '2026-04-20 09:00',
    summary_content: '1. 연어·참치 등 고급 수산물 도매가격이 환율 상승 영향으로 전월 대비 7% 올랐습니다.\n2. 식품의약품안전처가 신선 식자재 냉장 유통 기준을 강화하는 개정안을 예고했습니다. 냉장 보관 온도 기준이 기존 5℃에서 4℃로 조정될 예정입니다.\n3. 백화점 푸드코트 방문객 수가 전년 동월 대비 12% 증가하며 매출 호조세를 보이고 있습니다.',
    advice: '신선 식자재 냉장 보관 온도 기준 강화에 대비해 냉장고 온도 설정을 사전에 점검하시고, 수산물 가격 동향을 모니터링하여 발주 계획을 조율하시기 바랍니다.',
    urls: ['https://example.com/news/4', 'https://example.com/news/5'],
  },
  {
    idx: 1,
    summary_title: '2026-04-19 푸드코트 업계 주요 이슈 요약',
    summary_date: '2026-04-19 09:00',
    summary_content: '1. 황금연휴 기간 백화점 푸드코트 방문객이 평일 대비 평균 180% 증가할 것으로 전망됩니다.\n2. 버터·생크림 등 유제품 제조업체들이 원가 상승을 이유로 공급가 인상을 예고했습니다.\n3. 봄철 식재료 수급 불안정으로 일부 채소류 가격이 급등하고 있어 대체 식재료 확보가 권장됩니다.',
    advice: '연휴 수요 급증에 대비해 한우·신선 식자재·유제품 재고를 충분히 확보하시고, 채소류 가격 급등에 대비한 대체 식재료 공급망을 점검하시기 바랍니다.',
    urls: ['https://example.com/news/6', 'https://example.com/news/7', 'https://example.com/news/8'],
  },
])

function firstLine(content) {
  return content?.split('\n')[0] ?? ''
}

function handleDownload(report) {
  showToast(`${report.report_title} 다운로드 준비 중입니다.`)
}
</script>
