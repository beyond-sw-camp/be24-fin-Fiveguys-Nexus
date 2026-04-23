<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-gray-900">지역 뉴스</h1>
      </div>
    </div>

    <p class="text-xs text-gray-400 mb-3">매일 오전 9시 AI가 푸드코트 및 외식 업계 관련 정보를 수집·요약합니다.</p>

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
    <Teleport to="body">
    <div v-if="selectedNews" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="selectedNews = null"></div>
      <div class="relative bg-white rounded-xl border border-gray-200 shadow-xl w-full max-w-2xl max-h-[85vh] flex flex-col overflow-hidden">

        <!-- 모달 헤더 -->
        <div class="shrink-0 flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <div class="flex items-center gap-2">
            <Newspaper class="w-4 h-4 text-blue-500" />
            <span class="font-bold text-gray-900 text-sm">{{ selectedNews.summary_title }}</span>
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
        <div class="shrink-0 px-6 py-4 border-t border-gray-100 flex justify-end">
          <button @click="selectedNews = null"
            class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer">닫기</button>
        </div>
      </div>
    </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Newspaper, Lightbulb, ExternalLink } from 'lucide-vue-next'

const selectedNews = ref(null)

const categoryMap = {
  LOCAL_EVENT: { label: '지역 행사', class: 'text-purple-600 bg-purple-50 border-purple-200' },
  HYGIENE:     { label: '위생/점검', class: 'text-red-600 bg-red-50 border-red-200' },
  POLICY:      { label: '규제/정책', class: 'text-amber-600 bg-amber-50 border-amber-200' },
  TREND:       { label: '상권 트렌드', class: 'text-blue-600 bg-blue-50 border-blue-200' },
}

function categoryStyle(category) {
  return categoryMap[category] ?? { label: category, class: 'text-gray-600 bg-gray-50 border-gray-200' }
}

function firstLine(content) {
  return content?.split('\n')?.[0] ?? ''
}

const newsList = ref([
  {
    idx: 4,
    category: 'LOCAL_EVENT',
    summary_title: '2026-04-21 강남구 지역 행사 안내',
    summary_date: '2026-04-21 09:00',
    summary_content: '1. 오는 04-26(토) 강남구 코엑스 일대에서 "서울 푸드 페스티벌"이 개최됩니다. 예상 방문객 5만 명 이상으로 주변 상권 유동인구 급증이 예상됩니다.\n2. 04-27(일) 삼성동 일대 마라톤 대회가 열려 오전 시간대 주변 유동인구가 크게 늘어날 전망입니다.',
    advice: '행사 기간 유동인구 증가에 대비해 재료 및 포장재 재고를 미리 확보하고, 배달·홀 주문 급증에 대한 인력 운용 계획을 점검하세요.',
    urls: ['https://example.com/event/1', 'https://example.com/event/2'],
  },
  {
    idx: 3,
    category: 'HYGIENE',
    summary_title: '2026-04-21 강남구 식품 위생 점검 예고',
    summary_date: '2026-04-21 09:00',
    summary_content: '1. 강남구 보건소가 04-28~05-02 기간 중 관내 음식점 일제 위생 점검을 실시할 예정입니다.\n2. 닭고기 냉장 보관 온도(4℃ 이하)와 완전 조리 온도(75℃ 이상) 준수 여부가 중점 점검 항목입니다.\n3. 주방 내 교차 오염 방지 및 직원 개인 위생 상태도 확인 대상입니다.',
    advice: '점검 전 냉장고 온도 설정과 조리 기구 소독 상태를 재확인하고, 직원 위생 교육(손 씻기, 위생모 착용 등)을 사전에 실시하세요.',
    urls: ['https://example.com/hygiene/1'],
  },
  {
    idx: 2,
    category: 'POLICY',
    summary_title: '2026-04-20 배달 플랫폼 수수료 정책 변경 안내',
    summary_date: '2026-04-20 09:00',
    summary_content: '1. 주요 배달 플랫폼이 05-01부터 중개 수수료를 기존 6.8%에서 7.5%로 인상한다고 공지했습니다.\n2. 광고비 정책도 일부 변경되어 노출 순위 기준이 리뷰 수 중심에서 응답률·완료율 중심으로 조정됩니다.',
    advice: '수수료 인상분을 반영한 메뉴 가격 조정 여부를 검토하시고, 플랫폼 노출 순위 유지를 위해 주문 응답 속도와 완료율 관리에 신경 쓰세요.',
    urls: ['https://example.com/policy/1', 'https://example.com/policy/2'],
  },
  {
    idx: 1,
    category: 'TREND',
    summary_title: '2026-04-19 갤러리아 백화점 상권 동향',
    summary_date: '2026-04-19 09:00',
    summary_content: '1. 갤러리아 백화점 인근 고급 레스토랑 2곳이 이달 중 오픈 예정으로 푸드코트 방문객 증가가 기대됩니다.\n2. 최근 백화점 푸드코트에서 프리미엄 식사 수요가 전월 대비 18% 증가한 것으로 나타났습니다.\n3. 주말 점심 시간대(12:00~14:00) 대기 고객이 늘며 회전율 관리가 중요해지고 있습니다.',
    advice: '주말 피크 시간대 회전율을 높이기 위한 운영 인력 배치를 점검하고, 프리미엄 메뉴 구성 강화로 객단가 향상을 검토하세요.',
    urls: ['https://example.com/trend/1'],
  },
])
</script>
