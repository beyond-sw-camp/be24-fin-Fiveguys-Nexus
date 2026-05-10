<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900">지역 뉴스</h1>
      </div>
    </div>

    <p class="text-xs text-gray-400 mb-3">매일 오전 8시 AI가 매장 지역 관련 정보를 수집·요약합니다.</p>

    <div v-if="isLoading" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
      불러오는 중...
    </div>

    <div v-else-if="newsList.length === 0" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
      수집된 뉴스가 없습니다.
    </div>

    <!-- 뉴스 목록 -->
    <div v-else class="flex flex-col gap-3">
      <button
        v-for="news in newsList"
        :key="news.idx"
        @click="openDetail(news)"
        class="w-full bg-white rounded-lg border border-gray-200 p-4 flex items-start justify-between gap-4 hover:border-blue-400 hover:bg-blue-50/20 transition-colors text-left"
      >
        <div class="flex items-start gap-3 min-w-0">
          <Newspaper class="w-4 h-4 text-blue-500 shrink-0 mt-0.5" />
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

    <!-- 상세 모달 -->
    <Teleport to="body">
      <div v-if="selectedNews" class="fixed inset-0 z-50 flex items-center justify-center">
        <div class="absolute inset-0 bg-black/40" @click="selectedNews = null"></div>
        <div class="relative bg-white rounded-xl border border-gray-200 shadow-xl w-full max-w-2xl max-h-[85vh] flex flex-col overflow-hidden">

          <!-- 모달 헤더 -->
          <div class="shrink-0 flex items-center justify-between px-6 py-4 border-b border-gray-200">
            <div class="flex items-center gap-2">
              <Newspaper class="w-4 h-4 text-blue-500" />
              <span class="font-bold text-gray-900 text-sm">{{ selectedNews.summaryTitle }}</span>
            </div>
            <button @click="selectedNews = null" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
          </div>

          <!-- 모달 내용 -->
          <div class="flex-1 overflow-y-auto px-6 py-5 flex flex-col gap-4">
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

          <div class="shrink-0 px-6 py-4 border-t border-gray-100 flex justify-end">
            <button
              @click="selectedNews = null"
              class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer"
            >닫기</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Newspaper, ExternalLink } from 'lucide-vue-next'
import { getMyStoreNews, getNewsDetail } from '@/api/news'

const newsList = ref([])
const isLoading = ref(false)
const selectedNews = ref(null)
const detailContents = ref('')
const detailLoading = ref(false)

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

async function fetchNews() {
  isLoading.value = true
  try {
    const today = new Date().toISOString().slice(0, 10)
    const { data } = await getMyStoreNews(today)
    newsList.value = data.result ?? []
  } catch (e) {
    console.error('[StoreNewsView] 뉴스 조회 실패:', e)
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchNews)
</script>
