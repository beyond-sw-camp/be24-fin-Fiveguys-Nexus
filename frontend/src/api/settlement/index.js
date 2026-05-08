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

export default {
  prepareSettlement,
  verifySettlement,
}
