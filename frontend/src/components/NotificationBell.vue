<template>
  <div class="relative">
    <!-- Backdrop -->
    <div v-if="open" @click="open = false" class="fixed inset-0 z-40"></div>

    <!-- Bell button -->
    <button @click="open = !open"
      class="relative flex items-center justify-center w-9 h-9 rounded hover:bg-gray-100 transition-colors z-50">
      <Bell class="w-4.5 h-4.5 text-gray-500" />
      <span v-if="count > 0"
        class="absolute top-1 right-1 min-w-[16px] h-4 flex items-center justify-center text-[10px] font-black bg-[#F37321] text-white rounded-full px-1 leading-none">
        {{ count > 9 ? '9+' : count }}
      </span>
    </button>

    <!-- Dropdown panel -->
    <transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="opacity-0 translate-y-1"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-1">
      <div v-if="open"
        class="absolute right-0 mt-1 w-[380px] bg-white border border-gray-200 rounded-lg shadow-lg z-50 overflow-hidden">

        <!-- Panel header -->
        <div class="px-4 py-3 border-b border-gray-100 flex items-center justify-between bg-gray-50/60">
          <div class="flex items-center gap-2">
            <span class="text-sm font-bold text-gray-900">알림</span>
            <span v-if="count > 0"
              class="text-[11px] font-bold px-1.5 py-0.5 bg-[#F37321] text-white rounded">
              {{ count }}
            </span>
          </div>
          <button v-if="count > 0" @click="markAll"
            class="text-xs text-gray-400 hover:text-gray-700 font-medium transition-colors">
            모두 읽음
          </button>
        </div>

        <!-- Notification list -->
        <div class="max-h-[420px] overflow-y-auto divide-y divide-gray-50">
          <div v-if="items.length === 0" class="px-4 py-10 text-center text-gray-400 text-sm">
            새 알림이 없습니다.
          </div>

          <div v-for="n in items" :key="n.id"
            @click="notifStore.markRead(n.id)"
            class="flex gap-3 px-4 py-3.5 cursor-pointer transition-colors"
            :class="n.read ? 'bg-white hover:bg-gray-50/50' : 'bg-orange-50/30 hover:bg-orange-50/60'">

            <!-- Type badge dot -->
            <div class="shrink-0 mt-0.5">
              <div class="w-2 h-2 rounded-full mt-1.5"
                :class="n.read ? 'bg-gray-200' : typeColor(n.type)">
              </div>
            </div>

            <!-- Content -->
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between gap-2">
                <div class="flex items-center gap-1.5 flex-wrap">
                  <span class="text-[11px] font-bold px-1.5 py-0.5 rounded border"
                    :class="[typeConfig(n.type).bg, typeConfig(n.type).color, typeConfig(n.type).border]">
                    {{ typeConfig(n.type).label }}
                  </span>
                  <span class="text-xs font-semibold text-gray-900">{{ n.title }}</span>
                </div>
                <span class="text-[11px] text-gray-400 shrink-0 mt-0.5">{{ notifStore.relativeTime(n.time) }}</span>
              </div>
              <p class="text-xs text-gray-500 mt-1 leading-relaxed line-clamp-2">{{ n.body }}</p>
            </div>

            <!-- Unread indicator -->
            <div class="shrink-0 self-center">
              <div v-if="!n.read" class="w-1.5 h-1.5 rounded-full bg-[#F37321]"></div>
            </div>
          </div>
        </div>

        <!-- Panel footer -->
        <div class="px-4 py-2.5 border-t border-gray-100 bg-gray-50/60">
          <p class="text-[11px] text-gray-400 text-center">전체 {{ items.length }}건</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Bell } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/notification'
import { useAuthStore } from '@/stores/auth'

const open = ref(false)
const notifStore = useNotificationStore()
const auth = useAuthStore()

const role = computed(() => {
  if (auth.isAdmin) return 'ADMIN'
  if (auth.isStoreOwner) return 'STORE_OWNER'
  return null
})

const items = computed(() => role.value ? notifStore.forRole(role.value) : [])
const count = computed(() => role.value ? notifStore.unreadCount(role.value) : 0)

function markAll() {
  if (role.value) notifStore.markAllRead(role.value)
}

function typeColor(type) {
  const map = {
    order_new: 'bg-[#F37321]',
    order_approved: 'bg-green-500',
    order_rejected: 'bg-red-500',
    delivery_update: 'bg-blue-500',
    low_stock: 'bg-amber-500',
    auto_order: 'bg-purple-500',
    settlement: 'bg-gray-400',
    abnormal_order: 'bg-red-500',
  }
  return map[type] ?? 'bg-gray-300'
}

function typeConfig(type) {
  return notifStore.typeConfig[type] ?? { label: '알림', color: 'text-gray-600', bg: 'bg-gray-50', border: 'border-gray-200' }
}
</script>
