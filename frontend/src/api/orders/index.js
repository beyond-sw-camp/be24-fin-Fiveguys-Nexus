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

export default {
  getAutoOrders,
  getOrderDetail,
  getDangerSettings,
}
