import api from '@/plugins/axiosinterceptor'

const getAutoOrders = (params = {}) => {
  return api.get('/orders/list/auto', { params })
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

const getOrderHistory = (params = {}) => {
  return api.get('/orders/list/history', { params })
}

const getConfirmedOrders = (params = {}) => {
  return api.get('/orders/list/confirmed', { params })

}

const approveAllConfirmed = () => {
  return api.put('/orders/confirmed/approve')
}

export default {
  getAutoOrders,
  getOrderDetail,
  getOrderHistory,
  getDangerSettings,
  updateDangerSettings,
  createStoreManualOrder,
  cancelOrder,
  getConfirmedOrders,
  approveAllConfirmed,
}
