import api from '@/plugins/axiosinterceptor'

// 본사 전체 배송 현황 조회 (매장명, 상태, 날짜 필터 포함)
export const getAllDeliveries = (params) =>
  api.get('/delivery/head', { params })

// 본사 배송 지연 사유 등록 및 수정
export const updateDelayReason = (deliveryIdx, delayReason) =>
  api.patch(`/delivery/head/${deliveryIdx}/delayreason`, { delayReason })

// 가맹점 배송 현황 조회
export const getMyStoreDeliveries = (params) =>
  api.get('/delivery/store', { params })
