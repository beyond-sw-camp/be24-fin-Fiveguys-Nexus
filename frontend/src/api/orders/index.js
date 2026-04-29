import api from '@/plugins/axiosinterceptor'

const getAutoOrders = () => {
  return api.get('/orders/list/auto')
}

export default {
  getAutoOrders,
}
