<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900">지역 뉴스</h1>
      </div>
    </div>

    <p class="text-xs text-gray-400 mb-3">매일 오전 9시 AI가 가맹점 지역 관련 정보를 수집·요약합니다.</p>

    <div v-if="newsList.length === 0" class="bg-white rounded-lg border border-gray-200 px-5 py-16 text-center text-gray-400 text-sm">
      수집된 뉴스가 없습니다.
    </div>

    <!-- 뉴스 목록 -->
    <div class="flex flex-col gap-3">
      <button
        v-for="news in newsList"
        :key="news.idx"
        @click="selectedNews = news"
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
            <p class="font-semibold text-gray-900 text-sm">{{ news.summary_title }}</p>
            <p class="text-xs text-gray-500 mt-1 line-clamp-1">{{ firstLine(news.summary_content) }}</p>
          </div>
        </div>
        <span class="text-xs text-gray-400 shrink-0">{{ news.summary_date }}</span>
      </button>
    </div>

    <!-- 상세 모달 -->
    <div v-if="selectedNews" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="selectedNews = null"></div>
      <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl mx-4 max-h-[80vh] flex flex-col overflow-hidden">

        <!-- 모달 헤더 -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100 shrink-0">
          <div class="flex items-center gap-2">
            <Newspaper class="w-4 h-4 text-blue-500" />
            <span class="font-bold text-gray-900 text-sm">{{ selectedNews.summary_title }}</span>
          </div>
          <button @click="selectedNews = null" class="text-gray-400 hover:text-gray-700 transition-colors">
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
            <span class="text-xs text-gray-400">{{ selectedNews.summary_date }}</span>
          </div>

          <!-- 요약 내용 -->
          <div class="bg-gray-50 rounded-lg px-4 py-3">
            <p class="text-xs font-semibold text-gray-500 mb-2 uppercase tracking-wide">요약</p>
            <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-line">{{ selectedNews.summary_content }}</p>
          </div>

          <!-- 운영 조언 -->
          <div class="flex gap-2 bg-blue-50 border border-blue-100 rounded-lg px-4 py-3">
            <Lightbulb class="w-4 h-4 text-blue-500 shrink-0 mt-0.5" />
            <div>
              <p class="text-xs font-semibold text-blue-600 mb-1">운영 조언</p>
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Newspaper, Lightbulb, ExternalLink, X } from 'lucide-vue-next'

const selectedNews = ref(null)

const categoryMap = {
  LOCAL_EVENT: { label: '지역 행사', class: 'text-purple-600 bg-purple-50 border-purple-200' },
  TRAFFIC:     { label: '교통',     class: 'text-amber-600 bg-amber-50 border-amber-200' },
  WEATHER:     { label: '날씨',     class: 'text-blue-600 bg-blue-50 border-blue-200' },
  RISK:        { label: '안전',     class: 'text-red-600 bg-red-50 border-red-200' },
}

function categoryStyle(category) {
  return categoryMap[category] ?? { label: category, class: 'text-gray-600 bg-gray-50 border-gray-200' }
}

function firstLine(content) {
  return content?.split('\n')[0] ?? ''
}

const newsList = ref([
  {
    idx: 4,
    category: 'LOCAL_EVENT',
    summary_title: '2026-04-21 강남구 지역 행사 안내',
    summary_date: '2026-04-21 09:00',
    summary_content: '1. 오는 04-26(토) 강남구 코엑스 일대에서 "서울 푸드 페스티벌"이 개최됩니다. 예상 방문객 5만 명 이상으로 주변 상권 유동인구 급증이 예상됩니다.\n2. 04-27(일) 삼성동 일대 마라톤 대회로 인해 오전 7시~12시 일부 도로가 통제됩니다.',
    advice: '행사 기간 주변 유동인구 증가에 대비해 재료 및 포장재 재고를 미리 확보하고, 배달 주문 급증에 대한 인력 운용 계획을 점검하세요.',
    urls: ['https://example.com/event/1', 'https://example.com/event/2'],
  },
  {
    idx: 3,
    category: 'TRAFFIC',
    summary_title: '2026-04-21 강남구 교통 정보',
    summary_date: '2026-04-21 09:00',
    summary_content: '1. 테헤란로 지하철 공사로 인해 강남역~선릉역 구간 차량 정체가 지속되고 있습니다. 우회로 이용이 권장됩니다.\n2. 배달 오토바이 통행 시 해당 구간 우회로 안내가 필요합니다.',
    advice: '배달 지연이 예상되므로 배달 예상 시간 안내 문구를 업데이트하고, 고객 불만 대응 준비를 하시기 바랍니다.',
    urls: ['https://example.com/traffic/1'],
  },
  {
    idx: 2,
    category: 'WEATHER',
    summary_title: '2026-04-20 주간 날씨 예보',
    summary_date: '2026-04-20 09:00',
    summary_content: '1. 이번 주 목~금요일 강한 비가 예보되어 있습니다. 강수량은 50~80mm로 예상됩니다.\n2. 주말에는 맑은 날씨로 회복될 전망입니다.',
    advice: '우천 시 배달 주문이 증가하는 경향이 있으므로 재료 재고를 넉넉히 확보하고, 배달 포장재 방수 처리 상태를 점검하세요.',
    urls: ['https://example.com/weather/1'],
  },
  {
    idx: 1,
    category: 'RISK',
    summary_title: '2026-04-19 강남구 안전 공지',
    summary_date: '2026-04-19 09:00',
    summary_content: '1. 강남구 보건소가 봄철 식중독 예방 강화 캠페인을 시작했습니다. 음식점 위생 점검이 강화될 예정입니다.\n2. 특히 닭고기 관련 식품의 냉장 보관 온도 및 조리 온도 준수가 중점 점검 대상입니다.',
    advice: '위생 점검에 대비해 냉장고 온도 설정(4℃ 이하)과 닭고기 완전 조리(75℃ 이상) 기준을 재확인하고, 직원 위생 교육을 실시하세요.',
    urls: ['https://example.com/safety/1'],
  },
])
</script>
