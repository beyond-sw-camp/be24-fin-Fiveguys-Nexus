import api from '@/plugins/axiosinterceptor'

const getHeadInventoryList = () => {
  return api.get('/head/inventory/list')
}

export default {
  getHeadInventoryList,
}
