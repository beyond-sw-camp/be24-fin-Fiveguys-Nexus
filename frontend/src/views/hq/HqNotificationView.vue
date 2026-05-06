<script setup>
import { ref } from 'vue'
import { Bell } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/notification'

const store = useNotificationStore()

const tabs = [
  { label: '전체',        value: null },
  { label: '재고 부족',   value: 'LOW_STOCK' },
  { label: '유통기한',    value: 'EXPIRY' },
  { label: '비정상 감지', value: 'ABNORMAL_ORDER' },
  { label: '배송 지연',   value: 'DELIVERY_DELAY' },
]

const activeTab = ref(null)


async function switchTab(type) {
  activeTab.value = type
  await store.fetchNotifications(type)
}

async function handleMarkAllRead() {
  await store.markAllRead()
}

async function handleClick(n) {
  if (!n.isRead) await store.markRead(n.idx)
}

function typeConfig(type) {
  return store.typeConfig[type] ?? { label: type, color: 'text-gray-600', bg: 'bg-gray-50', border: 'border-gray-200' }
}
</script>

<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-5">
      <h1 class="text-xl font-bold text-gray-900">알림</h1>
      <button
        v-if="store.unreadCount > 0"
        @click="handleMarkAllRead"
        class="text-sm text-[#F37321] hover:underline font-medium cursor-pointer"
      >
        전체 읽음 처리
      </button>
    </div>

    <!-- Filter tabs -->
    <div class="flex gap-1 mb-4 border-b border-gray-200">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="switchTab(tab.value)"
        class="px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px cursor-pointer"
        :class="activeTab === tab.value
          ? 'border-[#F37321] text-[#F37321]'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- List -->
    <div v-if="store.notifications.length > 0" class="flex flex-col gap-2">
      <button
        v-for="n in store.notifications"
        :key="n.idx"
        @click="handleClick(n)"
        class="w-full flex gap-3 p-4 rounded-lg border transition-colors text-left"
        :class="n.isRead
          ? 'bg-white border-gray-100 hover:bg-gray-50'
          : 'bg-orange-50/40 border-orange-100 hover:bg-orange-50/70'"
      >
        <!-- Type badge -->
        <div class="shrink-0 pt-0.5">
          <span
            class="inline-block text-[11px] font-semibold px-2 py-0.5 rounded-full border"
            :class="[typeConfig(n.type).color, typeConfig(n.type).bg, typeConfig(n.type).border]"
          >
            {{ typeConfig(n.type).label }}
          </span>
        </div>

        <!-- Content -->
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 mb-0.5">
            <span class="text-sm font-semibold text-gray-900">{{ n.title }}</span>
            <span v-if="!n.isRead" class="w-1.5 h-1.5 rounded-full bg-[#F37321] shrink-0"></span>
          </div>
          <p class="text-sm text-gray-600 leading-snug">{{ n.content }}</p>
          <p class="text-xs text-gray-400 mt-1">{{ store.relativeTime(n.createdAt) }}</p>
        </div>
      </button>

      <!-- 더 보기 -->
      <button
        v-if="store.hasNext"
        @click="store.loadMore()"
        class="w-full py-3 text-sm text-gray-500 hover:text-gray-700 font-medium"
      >
        더 보기
      </button>
    </div>

    <!-- Empty -->
    <div v-else class="flex flex-col items-center justify-center py-20 text-gray-400">
      <Bell class="w-10 h-10 mb-3 opacity-30" />
      <p class="text-sm">알림이 없습니다.</p>
    </div>
  </div>
</template>
