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
      <p class="text-xs text-gray-400">매일 오전 8시 AI가 더벤티 브랜드 및 업계 관련 뉴스를 수집·요약합니다.</p>

      <div v-if="newsLoading" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
        불러오는 중...
      </div>

      <div v-else-if="newsSummaries.length === 0" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
        수집된 뉴스가 없습니다.
      </div>

      <!-- 뉴스 목록 -->
      <button
        v-for="news in newsSummaries"
        :key="news.idx"
        @click="openDetail(news)"
        class="w-full bg-white rounded-lg border border-gray-200 p-4 flex items-start justify-between gap-4 hover:border-[#F37321] hover:bg-orange-50/20 transition-colors text-left cursor-pointer"
      >
        <div class="flex items-start gap-3 min-w-0">
          <Newspaper class="w-4 h-4 text-[#F37321] shrink-0 mt-0.5" />
          <div class="min-w-0">
            <div class="flex items-center gap-2 mb-1">
              <span
                class="text-[11px] font-semibold px-2 py-0.5 rounded-full border"
                :class="categoryStyle(news.category).class"
              >
                {{ categoryStyle(news.category).label }}
              </span>
            </div>
            <p class="font-semibold text-gray-900 text-sm">{{ news.summaryTitle }}</p>
            <p class="text-xs text-gray-500 mt-1 line-clamp-1">{{ news.summaryContentsPreview }}</p>
          </div>
        </div>
        <span class="text-xs text-gray-400 shrink-0">{{ formatDate(news.summaryDate) }}</span>
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
            <span class="font-bold text-gray-900 text-sm">{{ selectedNews.summaryTitle }}</span>
          </div>
          <button @click="selectedNews = null" class="text-gray-400 hover:text-gray-700 transition-colors cursor-pointer">
            <X class="w-5 h-5" />
          </button>
        </div>

        <!-- 모달 내용 -->
        <div class="overflow-y-auto px-6 py-5 flex flex-col gap-4">
          <div class="flex items-center gap-2">
            <span
              class="text-[11px] font-semibold px-2 py-0.5 rounded-full border"
              :class="categoryStyle(selectedNews.category).class"
            >
              {{ categoryStyle(selectedNews.category).label }}
            </span>
            <span class="text-xs text-gray-400">{{ formatDate(selectedNews.summaryDate) }}</span>
          </div>

          <!-- 요약 내용 -->
          <div class="bg-gray-50 rounded-lg px-4 py-3">
            <p class="text-xs font-semibold text-gray-500 mb-2 uppercase tracking-wide">요약</p>
            <p v-if="detailLoading" class="text-sm text-gray-400">불러오는 중...</p>
            <p v-else class="text-sm text-gray-700 leading-relaxed whitespace-pre-line">{{ detailContents }}</p>
          </div>

          <!-- 원문 링크 -->
          <div v-if="selectedNews.url && selectedNews.url !== '-'">
            <p class="text-xs font-semibold text-gray-500 mb-2">원문 링크</p>
            <a
              :href="selectedNews.url"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex items-center gap-1 text-xs text-blue-500 hover:underline"
            >
              <ExternalLink class="w-3 h-3" />
              원문 보기
            </a>
          </div>
        </div>
      </div>
    </div>
    <Toast :show="toast.show" :message="toast.message" :type="toast.type" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { FileText, Download, Newspaper, ExternalLink, X } from 'lucide-vue-next'
import Toast from '@/components/common/Toast.vue'
import { useToast } from '@/composables/useToast'
import { getHqNews, getNewsDetail } from '@/api/news'

const { toast, showToast } = useToast()

const tabs = [
  { label: '보고서', value: 'report' },
  { label: '뉴스 요약', value: 'news' },
]

const activeTab = ref('report')
const selectedNews = ref(null)
const detailContents = ref('')
const detailLoading = ref(false)
const newsLoading = ref(false)

const categoryMap = {
  LOCAL_EVENT: { label: '지역 행사', class: 'text-purple-600 bg-purple-50 border-purple-200' },
  TRAFFIC:     { label: '교통',     class: 'text-blue-600 bg-blue-50 border-blue-200' },
  WEATHER:     { label: '날씨',     class: 'text-sky-600 bg-sky-50 border-sky-200' },
  RISK:        { label: '위험',     class: 'text-red-600 bg-red-50 border-red-200' },
}

function categoryStyle(category) {
  return categoryMap[category] ?? { label: category, class: 'text-gray-600 bg-gray-50 border-gray-200' }
}

function formatDate(dateTime) {
  if (!dateTime) return ''
  return dateTime.slice(0, 10)
}

async function openDetail(news) {
  selectedNews.value = news
  detailContents.value = ''
  detailLoading.value = true
  try {
    const { data } = await getNewsDetail(news.idx)
    detailContents.value = data.result?.summaryContents ?? ''
  } catch {
    detailContents.value = news.summaryContentsPreview ?? ''
  } finally {
    detailLoading.value = false
  }
}

async function fetchHqNews() {
  newsLoading.value = true
  try {
    const today = new Date().toISOString().slice(0, 10)
    const { data } = await getHqNews(today)
    newsSummaries.value = data.result ?? []
  } catch (e) {
    console.error('[ReportView] 뉴스 조회 실패:', e)
  } finally {
    newsLoading.value = false
  }
}

watch(activeTab, (val) => {
  if (val === 'news' && newsSummaries.value.length === 0) fetchHqNews()
})

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

const newsSummaries = ref([])

function handleDownload(report) {
  showToast(`${report.report_title} 다운로드 준비 중입니다.`)
}
</script>
