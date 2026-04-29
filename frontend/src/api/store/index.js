import api from '@/plugins/axiosinterceptor'

export const getStoreList = () => api.get('/store/list')

export const getStoreDetailList = (storeIdx) => api.get(`/store/detail/list/${storeIdx}`)

export const getStoreInventoryByStore = (storeIdx) =>
  api.get(`/store/inventory/list/${storeIdx}`)
