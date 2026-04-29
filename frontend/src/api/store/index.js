import api from '@/plugins/axiosinterceptor'

export const getStoreList = () => api.get('/store/list')

export const getStoreInventoryByStore = (storeIdx) =>
  api.get(`/store/inventory/list/${storeIdx}`)
