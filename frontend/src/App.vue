<template>
  <!-- 로그인 페이지: 사이드바 없이 전체 화면 -->
  <RouterView v-if="route.name === 'login'" />

  <!-- 메인 레이아웃 -->
  <div v-else class="flex h-screen bg-[#F5F5F5] text-gray-800 overflow-hidden">

    <!-- Sidebar -->
    <aside class="w-[18rem] bg-white border-r border-gray-200 flex flex-col shrink-0 z-20">
      <!-- Logo -->
      <div class="h-14 flex items-center px-4 bg-white border-b-2 shrink-0" :class="logoBorderClass">
        <img :src="nexusLogo" alt="Nexus logo" class="h-6 w-auto object-contain" />
      </div>

      <!-- Navigation -->
      <nav class="py-3 flex-1 overflow-y-auto">
        <p class="px-5 text-[10px] font-bold text-gray-400 mb-1.5 mt-1 uppercase tracking-widest">Menu</p>

        <template v-for="menu in currentMenus" :key="menu.path">

          <!-- 드롭다운 그룹 (children 있는 항목) -->
          <div v-if="menu.children">
            <button @click="handleGroupClick(menu)"
              class="w-full flex items-center gap-3 px-5 py-2.5 text-sm font-medium cursor-pointer transition-all relative select-none"
              :class="isGroupActive(menu) ? activeMenuClass : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
              <component :is="menu.icon" class="w-4 h-4 shrink-0"
                :class="isGroupActive(menu) ? activeIconClass : 'text-gray-400'" />
              <span class="flex-1 text-left">{{ menu.name }}</span>
              <ChevronDown class="w-3.5 h-3.5 shrink-0 transition-transform duration-200"
                :class="[isGroupOpen(menu) ? 'rotate-180' : '', isGroupActive(menu) ? activeIconClass : 'text-gray-400']" />
            </button>

            <!-- 하위 항목 (슬라이드 다운) -->
            <div v-if="isGroupOpen(menu)" class="ml-9 border-l border-gray-100">
              <RouterLink v-for="child in menu.children" :key="child.path" :to="child.path"
                class="flex items-center gap-2.5 pl-3.5 pr-5 py-2 text-sm transition-colors font-medium relative"
                :class="isActive(child)
                  ? (auth.isAdmin ? 'text-[#F37321] font-semibold bg-orange-50/60' : 'text-blue-600 font-semibold bg-blue-50/60')
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'">
                <span class="w-1.5 h-1.5 rounded-full shrink-0"
                  :class="isActive(child)
                    ? (auth.isAdmin ? 'bg-[#F37321]' : 'bg-blue-500')
                    : 'bg-gray-300'"></span>
                {{ child.name }}
              </RouterLink>
            </div>
          </div>

          <!-- 일반 단일 항목 -->
          <RouterLink v-else :to="menu.path"
            class="w-full flex items-center gap-3 px-5 py-2.5 text-sm font-medium transition-all relative"
            :class="isActive(menu) ? activeMenuClass : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'">
            <component :is="menu.icon" class="w-4 h-4 shrink-0"
              :class="isActive(menu) ? activeIconClass : 'text-gray-400'" />
            {{ menu.name }}
          </RouterLink>

        </template>
      </nav>

      <!-- Sidebar footer -->
      <div class="px-5 py-4 border-t border-gray-100 shrink-0">
        <div class="w-full flex items-center justify-center gap-1.5 text-xs font-medium text-gray-400">
          <span class="w-1.5 h-1.5 rounded-full" :class="footerRoleDotClass"></span>
          <span :class="footerRoleTextClass">
            {{ footerRoleLabel }}
          </span>
        </div>
      </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 flex flex-col overflow-hidden">

      <!-- Header -->
      <header class="h-14 bg-white border-b border-gray-200 flex items-center justify-end px-7 shrink-0 z-30">
        <div class="flex items-center gap-2">
          <!-- 알림 벨 -->
          <NotificationBell />

          <div class="relative">
            <div v-if="isUserMenuOpen" @click="isUserMenuOpen = false" class="fixed inset-0 z-40"></div>

            <button @click="isUserMenuOpen = !isUserMenuOpen"
              class="flex items-center gap-2.5 hover:bg-gray-50 px-3 py-2 rounded transition-colors relative z-50">
              <div class="w-8 h-8 rounded flex items-center justify-center text-xs font-bold border border-gray-200 bg-gray-50"
                :class="avatarColorClass">
                {{ auth.user?.avatar }}
              </div>
              <div class="flex flex-col text-left">
                <span class="text-sm font-bold text-gray-900 leading-tight">{{ auth.user?.name }}</span>
                <span class="text-[11px] text-gray-400 leading-tight">{{ roleLabelShort }}</span>
              </div>
              <ChevronDown class="w-3.5 h-3.5 text-gray-400 transition-transform duration-200"
                :class="{ 'rotate-180': isUserMenuOpen }" />
            </button>

            <transition enter-active-class="transition ease-out duration-100" enter-from-class="opacity-0 translate-y-1"
              enter-to-class="opacity-100 translate-y-0" leave-active-class="transition ease-in duration-75"
              leave-from-class="opacity-100 translate-y-0" leave-to-class="opacity-0 translate-y-1">
              <div v-if="isUserMenuOpen"
                class="absolute right-0 mt-1 w-44 bg-white border border-gray-200 rounded shadow-md z-50 py-1 overflow-hidden">
                <RouterLink to="/profile" @click="isUserMenuOpen = false"
                  class="flex items-center gap-2.5 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors">
                  <User class="w-4 h-4 text-gray-400" />
                  내 정보 관리
                </RouterLink>
                <div class="h-px bg-gray-100 mx-1"></div>
                <button @click="handleLogout"
                  class="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-red-500 hover:bg-red-50 transition-colors text-left">
                  <LogOut class="w-4 h-4" />
                  로그아웃
                </button>
              </div>
            </transition>
          </div>
        </div>
      </header>

      <div :class="route.name === 'storePos' ? 'flex-1 overflow-hidden' : 'flex-1 overflow-auto bg-[#F5F5F5]'">
        <RouterView />
      </div>
    </main>

    <!-- AI 챗봇 (관리자 전용) -->
    <AiChatbot v-if="auth.isAdmin" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import {
  LayoutDashboard, Store,
  Calculator, Box, ClipboardList,
  PackageSearch, Truck,
  Warehouse, Receipt, Tablet,
  ChevronDown, User, LogOut, UserPlus, BarChart3, Bell, FileText, Newspaper,
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import NotificationBell from '@/components/NotificationBell.vue'
import AiChatbot from '@/components/AiChatbot.vue'
import nexusLogo from '@/assets/nexus-logo.png'

const route  = useRoute()
const router = useRouter()
const auth   = useAuthStore()

const isUserMenuOpen = ref(false)
const openGroups     = ref([])
/** 현재 라우트가 그룹 자식이어도, 헤더 클릭으로 접어 둔 그룹 (path 키) */
const collapsedGroups = ref([])

// ── 역할별 메뉴 (children → 드롭다운 그룹) ───────────────
const adminMenus = [
  { path: '/dashboard', name: '대시보드',  icon: LayoutDashboard },
  { path: '/store',     name: '가맹점 관리', icon: Store },
  {
    path: '/product', name: '제품 관리', icon: PackageSearch,
    children: [
      { path: '/product', name: '제품 목록' },
      { path: '/recipe',  name: '메뉴 관리' },
    ],
  },
  { path: '/order', name: '발주 관리', icon: ClipboardList },
  {
    path: '/inventory/head-office', name: '재고 관리', icon: Box,
    children: [
      { path: '/inventory/head-office', name: '본사 재고 현황' },
      { path: '/inventory/franchise', name: '가맹점 재고 현황' },
    ],
  },
  { path: '/delivery',   name: '배송 관리', icon: Truck },
  { path: '/settlement', name: '정산 관리', icon: Calculator },
  { path: '/admin-account', name: '계정 관리', icon: UserPlus },
  {
    path: '/statistics', name: '통계', icon: BarChart3,
    children: [
      { path: '/statistics', name: '발주 통계' },
      { path: '/statistics/sales', name: '판매/매출 분석' },
      { path: '/statistics/esg', name: 'ESG 대시보드' },
    ],
  },
  { path: '/report', name: '보고서', icon: FileText },
  { path: '/notification', name: '알림', icon: Bell },
]

const storeMenus = [
  { path: '/store-dashboard', name: '대시보드',  icon: LayoutDashboard },
  { path: '/store-pos',       name: 'POS',        icon: Tablet },
  { path: '/store-order',     name: '발주서',     icon: ClipboardList },
  { path: '/store-inventory', name: '매장 재고',  icon: Warehouse },
  { path: '/store-delivery',  name: '배송 현황',  icon: Truck },
  { path: '/store-settlement',name: '정산 내역',  icon: Receipt },
  { path: '/store-news',       name: '지역 뉴스', icon: Newspaper },
  { path: '/store-notification', name: '알림', icon: Bell },
]

const currentMenus = computed(() => {
  if (auth.isAdmin)      return adminMenus
  if (auth.isStoreOwner) return storeMenus
  return []
})

// ── 현재 라우트 변경 시 해당 그룹 자동 오픈 ────────────────
watch(() => route.path, () => {
  currentMenus.value.forEach(menu => {
    if (!menu.children) return
    if (isGroupActive(menu) && !openGroups.value.includes(menu.path)) {
      openGroups.value.push(menu.path)
    }
    // 이 그룹 자식 라우트가 아니면 접기 상태 초기화
    if (!isGroupActive(menu)) {
      collapsedGroups.value = collapsedGroups.value.filter(p => p !== menu.path)
    }
  })
}, { immediate: true })

// ── 네비게이션 함수 ──────────────────────────────────────
function handleGroupClick(menu) {
  const expanded = isGroupOpen(menu)
  if (expanded) {
    if (!collapsedGroups.value.includes(menu.path)) collapsedGroups.value.push(menu.path)
    openGroups.value = openGroups.value.filter(p => p !== menu.path)
  } else {
    collapsedGroups.value = collapsedGroups.value.filter(p => p !== menu.path)
    if (!openGroups.value.includes(menu.path)) openGroups.value.push(menu.path)
  }
  router.push(menu.path)
}

// ── Active 판정 ─────────────────────────────────────────
function isActive(menu) {
  return route.path === menu.path
}

function isGroupActive(menu) {
  return menu.children?.some(child =>
    route.path === child.path || route.path.startsWith(child.path + '/')
  ) ?? false
}

function isGroupOpen(menu) {
  if (collapsedGroups.value.includes(menu.path)) return false
  return openGroups.value.includes(menu.path) || isGroupActive(menu)
}

// ── 역할별 스타일 ─────────────────────────────────────────
const footerRoleLabel = computed(() => {
  if (auth.isAdmin)      return '본사 관리자'
  if (auth.isStoreOwner) return auth.user?.storeName ? `${auth.user.storeName} 점주` : '가맹점 점주'
  return '사용자'
})

const roleLabelShort = computed(() => {
  if (auth.isAdmin)      return '본사 관리자'
  if (auth.isStoreOwner) return auth.user?.storeName
  return ''
})

const footerRoleTextClass = computed(() => {
  if (auth.isAdmin)      return 'text-[#F37321]'
  if (auth.isStoreOwner) return 'text-blue-600'
  return 'text-gray-500'
})

const footerRoleDotClass = computed(() => {
  if (auth.isAdmin)      return 'bg-[#F37321]'
  if (auth.isStoreOwner) return 'bg-blue-500'
  return 'bg-gray-400'
})

const logoBorderClass = computed(() => {
  if (auth.isAdmin)      return 'border-[#F37321]'
  if (auth.isStoreOwner) return 'border-blue-500'
  return 'border-gray-200'
})

const activeMenuClass = computed(() => {
  if (auth.isAdmin)      return 'text-[#F37321] bg-orange-50/70 font-semibold'
  if (auth.isStoreOwner) return 'text-blue-600 bg-blue-50/70 font-semibold'
  return 'text-gray-900 bg-gray-100 font-semibold'
})

const activeBorderClass = computed(() => {
  if (auth.isAdmin)      return 'bg-[#F37321]'
  if (auth.isStoreOwner) return 'bg-blue-500'
  return 'bg-gray-700'
})

const activeIconClass = computed(() => {
  if (auth.isAdmin)      return 'text-[#F37321]'
  if (auth.isStoreOwner) return 'text-blue-500'
  return 'text-gray-700'
})

const avatarColorClass = computed(() => {
  if (auth.isAdmin)      return 'text-[#F37321]'
  if (auth.isStoreOwner) return 'text-blue-500'
  return 'text-gray-500'
})

function handleLogout() {
  auth.logout()
  isUserMenuOpen.value = false
  router.push('/login')
}
</script>
