import api from '@/plugins/axiosinterceptor'

const getManualOrders = () => {
  return api.get('/orders/list/manual')
}

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

export default {
  getManualOrders,
  getAutoOrders,
  getOrderDetail,
  getDangerSettings,
  updateDangerSettings,
}
