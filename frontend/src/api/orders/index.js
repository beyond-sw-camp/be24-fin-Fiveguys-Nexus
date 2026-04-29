import api from '@/plugins/axiosinterceptor'

const getAutoOrders = () => {
  return api.get('/orders/list/auto')
}

const getOrderDetail = (ordersIdx) => {
  return api.get(`/orders/${ordersIdx}`)
}

export default {
  getAutoOrders,
  getOrderDetail,
}
