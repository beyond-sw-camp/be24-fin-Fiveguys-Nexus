import api from '@/plugins/axiosinterceptor'

const getAutoOrders = () => {
  return api.get('/orders/list/auto')
}

const getOrderDetail = (ordersIdx) => {
  return api.get(`/orders/${ordersIdx}`)
}

const getDangerSettings = () => {
  return api.get('/orders/danger')
}

const updateDangerSettings = (data) => {
  return api.put('/orders/danger', data)
}

const createStoreManualOrder = (data) => {
  return api.post('/orders/store/reg/manual', data)
}

const cancelOrder = (ordersIdx) => {
  return api.put(`/orders/${ordersIdx}/cancel`)
}

const getConfirmedOrders = () => {
  return api.get('/orders/list/confirmed')
}

const approveAllConfirmed = () => {
  return api.put('/orders/approve-all')
}

export default {
  getAutoOrders,
  getOrderDetail,
  getDangerSettings,
  updateDangerSettings,
  createStoreManualOrder,
  cancelOrder,
  getConfirmedOrders,
  approveAllConfirmed,
}
