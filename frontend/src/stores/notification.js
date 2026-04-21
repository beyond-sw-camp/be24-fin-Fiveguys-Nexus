import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([
    // ADMIN 알림
    {
      id: 1,
      type: 'order_new',
      title: '새 발주 요청',
      body: '판교테크노밸리점에서 생닭(10kg) 외 3종 발주를 요청했습니다.',
      time: '2026-04-15T09:12:00',
      read: false,
      role: 'ADMIN',
    },
    {
      id: 2,
      type: 'order_new',
      title: '새 발주 요청',
      body: '부산센텀점에서 튀김유(18L) 외 2종 발주를 요청했습니다.',
      time: '2026-04-15T08:45:00',
      read: false,
      role: 'ADMIN',
    },
    {
      id: 3,
      type: 'low_stock',
      title: '재고 부족 경고',
      body: '치킨파우더(P300) 재고가 최소 기준(10통) 이하입니다. 현재 재고: 6통.',
      time: '2026-04-15T07:30:00',
      read: false,
      role: 'ADMIN',
    },
    {
      id: 4,
      type: 'auto_order',
      title: '자동발주 제안',
      body: '생닭(P101) 재고가 기준치 이하입니다. 자동발주 승인이 필요합니다.',
      time: '2026-04-14T17:00:00',
      read: true,
      role: 'ADMIN',
    },
    {
      id: 5,
      type: 'settlement',
      title: '정산 마감 임박',
      body: '2026-04 월 가맹점 정산 마감일이 3일 남았습니다. 미완료 2건을 확인하세요.',
      time: '2026-04-14T09:00:00',
      read: true,
      role: 'ADMIN',
    },
    {
      id: 6,
      type: 'abnormal_order',
      title: '비정상 발주 감지',
      body: '여의도역점의 발주량이 평균 대비 230% 초과입니다. 검토가 필요합니다.',
      time: '2026-04-13T14:22:00',
      read: true,
      role: 'ADMIN',
    },
    // STORE_OWNER 알림
    {
      id: 7,
      type: 'order_approved',
      title: '발주 승인',
      body: '요청하신 발주(ORD-042)가 본사에서 승인되었습니다. 생닭(10kg) 외 2종, 납품 예정: 04-17.',
      time: '2026-04-15T10:05:00',
      read: false,
      role: 'STORE_OWNER',
    },
    {
      id: 8,
      type: 'delivery_update',
      title: '배송 상태 변경',
      body: '발주 ORD-040(치킨파우더 외 1종) 배송이 출발했습니다. 예상 도착: 오늘 오후 3시.',
      time: '2026-04-15T09:30:00',
      read: false,
      role: 'STORE_OWNER',
    },
    {
      id: 9,
      type: 'order_rejected',
      title: '발주 반려',
      body: '요청하신 발주(ORD-041)가 반려되었습니다. 사유: 튀김유 재고 충분. 본사에 문의하세요.',
      time: '2026-04-14T16:10:00',
      read: false,
      role: 'STORE_OWNER',
    },
    {
      id: 10,
      type: 'auto_order',
      title: '자동발주 제안',
      body: '비닐장갑 재고가 부족합니다. 자동발주 500개를 신청하시겠습니까?',
      time: '2026-04-14T08:00:00',
      read: true,
      role: 'STORE_OWNER',
    },
    {
      id: 11,
      type: 'settlement',
      title: '정산 완료',
      body: '2026-04 월 정산이 완료되었습니다. 청구 금액: ₩8,750,000.',
      time: '2026-04-13T11:00:00',
      read: true,
      role: 'STORE_OWNER',
    },
  ])

  // 현재 역할에 맞는 알림 필터링
  function forRole(role) {
    return notifications.value.filter(n => n.role === role)
  }

  function unreadCount(role) {
    return forRole(role).filter(n => !n.read).length
  }

  function markRead(id) {
    const n = notifications.value.find(n => n.id === id)
    if (n) n.read = true
  }

  function markAllRead(role) {
    forRole(role).forEach(n => { n.read = true })
  }

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

  // 알림 타입별 스타일
  const typeConfig = {
    order_new:       { label: '발주 요청',  color: 'text-[#F37321]',  bg: 'bg-orange-50',  border: 'border-orange-200' },
    order_approved:  { label: '승인',       color: 'text-green-600',  bg: 'bg-green-50',   border: 'border-green-200'  },
    order_rejected:  { label: '반려',       color: 'text-red-600',    bg: 'bg-red-50',     border: 'border-red-200'    },
    delivery_update: { label: '배송',       color: 'text-blue-600',   bg: 'bg-blue-50',    border: 'border-blue-200'   },
    low_stock:       { label: '재고 경고',  color: 'text-amber-600',  bg: 'bg-amber-50',   border: 'border-amber-200'  },
    auto_order:      { label: '자동발주',   color: 'text-purple-600', bg: 'bg-purple-50',  border: 'border-purple-200' },
    settlement:      { label: '정산',       color: 'text-gray-600',   bg: 'bg-gray-50',    border: 'border-gray-200'   },
    abnormal_order:  { label: '비정상 감지', color: 'text-red-600',   bg: 'bg-red-50',     border: 'border-red-200'    },
  }

  return { notifications, forRole, unreadCount, markRead, markAllRead, relativeTime, typeConfig }
})
