import api from '@/plugins/axiosinterceptor'

/**
 * 정산 준비 - 지난 달 미결제 발주서 조회 및 정산 금액 계산
 * @returns {Promise} 정산 정보 (settlementIdx, paymentPrice, headIncomeIdxList 등)
 */
const prepareSettlement = () => {
  return api.post('/pay')
}

/**
 * 정산 검증 - 결제 완료 후 정산 처리
 * @param {Object} verifyData - 결제 검증 데이터 (paymentId, settlementIdx)
 * @returns {Promise} 정산 완료 응답
 */
const verifySettlement = (verifyData) => {
  return api.post('/verify', verifyData)
}

/**
 * 빌링키 발급 요청 (카드 등록)
 * @param {Object} data - authKey, customerKey
 * @returns {Promise} 빌링키 발급 결과
 */
const issueBillingKey = (data) => {
  return api.post('/api/billing/issue', data)
}

/**
 * 등록된 빌링 정보(카드) 리스트 조회
 * @returns {Promise} 빌링 정보 리스트
 */
const getBillingList = () => {
  return api.get('/api/billing/list')
}

/**
 * 빌링 정보(카드) 삭제
 * @param {number} storeIdx - 가맹점 번호
 * @returns {Promise} 삭제 결과
 */
const deleteBillingInfo = (storeIdx) => {
  return api.delete(`/api/billing/delete/${storeIdx}`)
}

export default {
  prepareSettlement,
  verifySettlement,
  issueBillingKey,
  getBillingList,
  deleteBillingInfo,
}
