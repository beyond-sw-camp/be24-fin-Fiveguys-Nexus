<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-5">
      <h1 class="text-xl font-bold text-gray-900">알림</h1>
      <button
        v-if="unreadCount > 0"
        @click="store.markAllRead('STORE_OWNER')"
        class="text-sm text-blue-600 hover:underline font-medium"
      >
        전체 읽음 처리
      </button>
    </div>

    <!-- Filter tabs -->
    <div class="flex gap-1 mb-4 border-b border-gray-200">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="activeTab = tab.value"
        class="px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px"
        :class="activeTab === tab.value
          ? 'border-blue-500 text-blue-600'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- List -->
    <div v-if="filtered.length > 0" class="flex flex-col gap-2">
      <div
        v-for="n in filtered"
        :key="n.id"
        @click="store.markRead(n.id)"
        class="flex gap-3 p-4 rounded-lg border cursor-pointer transition-colors"
        :class="n.read
          ? 'bg-white border-gray-100 hover:bg-gray-50'
          : 'bg-blue-50/40 border-blue-100 hover:bg-blue-50/70'"
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
            <span v-if="!n.read" class="w-1.5 h-1.5 rounded-full bg-blue-500 shrink-0"></span>
          </div>
          <p class="text-sm text-gray-600 leading-snug">{{ n.body }}</p>
          <p class="text-xs text-gray-400 mt-1">{{ store.relativeTime(n.time) }}</p>
        </div>
      </div>
    </div>

    <!-- Empty -->
    <div v-else class="flex flex-col items-center justify-center py-20 text-gray-400">
      <Bell class="w-10 h-10 mb-3 opacity-30" />
      <p class="text-sm">알림이 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Bell } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/notification'

const store = useNotificationStore()

const tabs = [
  { label: '전체',    value: 'all' },
  { label: '발주',    value: 'order' },
  { label: '배송',    value: 'delivery' },
  { label: '자동발주', value: 'auto' },
  { label: '정산',    value: 'settlement' },
]

const activeTab = ref('all')

const storeNotifications = computed(() => store.forRole('STORE_OWNER'))
const unreadCount = computed(() => store.unreadCount('STORE_OWNER'))

const tabTypeMap = {
  order:      ['order_approved', 'order_rejected'],
  delivery:   ['delivery_update'],
  auto:       ['auto_order'],
  settlement: ['settlement'],
}

const filtered = computed(() => {
  if (activeTab.value === 'all') return storeNotifications.value
  const types = tabTypeMap[activeTab.value] ?? []
  return storeNotifications.value.filter(n => types.includes(n.type))
})

function typeConfig(type) {
  return store.typeConfig[type] ?? { label: type, color: 'text-gray-600', bg: 'bg-gray-50', border: 'border-gray-200' }
}
</script>
