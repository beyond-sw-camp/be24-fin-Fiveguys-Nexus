import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Public
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },

    // Root redirect
    { path: '/', redirect: '/dashboard' },

    // ── ADMIN ──────────────────────────────────────────
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('@/views/StatisticsView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/statistics/sales',
      name: 'statisticsSales',
      component: () => import('@/views/hq/SalesStatisticsView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/statistics/esg',
      name: 'statisticsEsg',
      component: () => import('@/views/hq/EsgDashboardView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/store',
      name: 'store',
      component: () => import('@/views/StoreView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/product',
      name: 'product',
      component: () => import('@/views/ProductView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/recipe',
      name: 'recipe',
      component: () => import('@/views/RecipeView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/order',
      name: 'order',
      component: () => import('@/views/OrderManageView.vue'),
      meta: { role: 'ADMIN' },
    },
    { path: '/inventory', redirect: '/inventory/head-office' },
    {
      path: '/inventory/head-office',
      name: 'inventoryHeadOffice',
      component: () => import('@/views/InventoryHeadOfficeView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/inventory/franchise',
      name: 'inventoryFranchise',
      component: () => import('@/views/InventoryView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/inventory/history',
      name: 'inventoryHistory',
      component: () => import('@/views/InventoryHistoryView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/delivery',
      name: 'delivery',
      component: () => import('@/views/DeliveryView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/settlement',
      name: 'settlement',
      component: () => import('@/views/SettlementView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/admin-account',
      name: 'adminAccount',
      component: () => import('@/views/AdminAccountCreateView.vue'),
      meta: { role: 'ADMIN' },
    },

    // ── STORE OWNER ────────────────────────────────────
    {
      path: '/store-dashboard',
      name: 'storeDashboard',
      component: () => import('@/views/store/StoreDashboardView.vue'),
      meta: { role: 'STORE_OWNER' },
    },
    {
      path: '/store-pos',
      name: 'storePos',
      component: () => import('@/views/store/PosView.vue'),
      meta: { role: 'STORE_OWNER' },
    },
    {
      path: '/store-order',
      name: 'storeOrder',
      component: () => import('@/views/store/StoreOrderView.vue'),
      meta: { role: 'STORE_OWNER' },
    },
    {
      path: '/store-inventory',
      name: 'storeInventory',
      component: () => import('@/views/store/StoreInventoryView.vue'),
      meta: { role: 'STORE_OWNER' },
    },
    {
      path: '/store-delivery',
      name: 'storeDelivery',
      component: () => import('@/views/store/StoreDeliveryView.vue'),
      meta: { role: 'STORE_OWNER' },
    },
    {
      path: '/store-settlement',
      name: 'storeSettlement',
      component: () => import('@/views/store/StoreSettlementView.vue'),
      meta: { role: 'STORE_OWNER' },
    },

    {
      path: '/notification',
      name: 'notification',
      component: () => import('@/views/hq/HqNotificationView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/report',
      name: 'report',
      component: () => import('@/views/hq/ReportView.vue'),
      meta: { role: 'ADMIN' },
    },
    {
      path: '/store-notification',
      name: 'storeNotification',
      component: () => import('@/views/store/StoreNotificationView.vue'),
      meta: { role: 'STORE_OWNER' },
    },
    {
      path: '/store-news',
      name: 'storeNews',
      component: () => import('@/views/store/StoreNewsView.vue'),
      meta: { role: 'STORE_OWNER' },
    },

    // ── COMMON ─────────────────────────────────────────
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
    },

    // Catch-all
    { path: '/:pathMatch(.*)*', redirect: '/login' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  // 비로그인 → 로그인 페이지로
  if (!to.meta.public && !auth.isLoggedIn) {
    return { name: 'login' }
  }

  // 이미 로그인된 상태에서 /login 접근 → 역할별 홈으로
  if (to.path === '/login' && auth.isLoggedIn) {
    if (auth.isAdmin) return { path: '/dashboard' }
    if (auth.isStoreOwner) return { path: '/store-dashboard' }
  }

  // 역할이 맞지 않는 페이지 접근 → 역할별 홈으로
  if (to.meta.role && auth.user?.role !== to.meta.role) {
    if (auth.isAdmin) return { path: '/dashboard' }
    if (auth.isStoreOwner) return { path: '/store-dashboard' }
  }
})

export default router
