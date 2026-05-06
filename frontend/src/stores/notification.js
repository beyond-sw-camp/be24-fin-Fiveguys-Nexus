import { defineStore } from 'pinia'
import { ref } from 'vue'
import notificationApi from '@/api/notification'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const hasNext = ref(false)
  const currentPage = ref(0)
  const currentType = ref(null)
  let eventSource = null

  const loading = ref(false)

  // 알림 목록 조회 (첫 페이지)
  async function fetchNotifications(type = null, size = 10) {
    currentType.value = type
    currentPage.value = 0
    const res = await notificationApi.getNotifications(type, 0, size)
    notifications.value = res.data.result.content
    hasNext.value = res.data.result.hasNext
  }

  // 알림 목록 추가 로드 (다음 페이지)
  async function loadMore(size = 10) {
    if (!hasNext.value || loading.value) return
    loading.value = true
    currentPage.value++
    const res = await notificationApi.getNotifications(currentType.value, currentPage.value, size)
    notifications.value.push(...res.data.result.content)
    hasNext.value = res.data.result.hasNext
    loading.value = false
  }

  // 미읽음 개수 조회
  async function fetchUnreadCount() {
    const res = await notificationApi.getUnreadCount()
    unreadCount.value = res.data.result.count
  }

  // 단건 읽음 처리
  async function markRead(idx) {
    await notificationApi.markAsRead(idx)
    const n = notifications.value.find(n => n.idx === idx)
    if (n) n.isRead = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }

  // 전체 읽음 처리
  async function markAllRead() {
    await notificationApi.markAllAsRead()
    notifications.value.forEach(n => { n.isRead = true })
    unreadCount.value = 0
  }

  // SSE 구독 시작
  function subscribe() {
    if (eventSource) return
    const baseUrl = import.meta.env.VITE_API_URL || '/api'
    eventSource = new EventSource(`${baseUrl}/notification/subscribe`)

    eventSource.addEventListener('notification', (event) => {
      const notification = JSON.parse(event.data)
      notifications.value.unshift(notification)
      unreadCount.value++
    })

    eventSource.onerror = () => {
      // 연결 끊기면 브라우저가 자동 재연결 시도
    }
  }

  // SSE 구독 해제
  function unsubscribe() {
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
  }

  // 상대 시간 포맷
  function relativeTime(iso) {
    const diff = Date.now() - new Date(iso).getTime()
    const m = Math.floor(diff / 60000)
    if (m < 1) return '방금 전'
    if (m < 60) return `${m}분 전`
    const h = Math.floor(m / 60)
    if (h < 24) return `${h}시간 전`
    const d = Math.floor(h / 24)
    return `${d}일 전`
  }

  // 알림 타입별 스타일 (백엔드 NotificationType enum 기준)
  const typeConfig = {
    EXPIRY:         { label: '유통기한',    color: 'text-amber-600',  bg: 'bg-amber-50',   border: 'border-amber-200'  },
    LOW_STOCK:      { label: '재고 부족',   color: 'text-amber-600',  bg: 'bg-amber-50',   border: 'border-amber-200'  },
    ABNORMAL_ORDER: { label: '비정상 감지', color: 'text-red-600',    bg: 'bg-red-50',     border: 'border-red-200'    },
    DELIVERY_DELAY: { label: '배송 지연',   color: 'text-blue-600',   bg: 'bg-blue-50',    border: 'border-blue-200'   },
  }

  return {
    notifications,
    unreadCount,
    hasNext,
    loading,
    fetchNotifications,
    loadMore,
    fetchUnreadCount,
    markRead,
    markAllRead,
    subscribe,
    unsubscribe,
    relativeTime,
    typeConfig,
  }
})
