import api from '@/plugins/axiosinterceptor'

export const getStoreList = () => api.get('/store/list')

export const getStoreDetailList = (storeIdx) => api.get(`/store/detail/list/${storeIdx}`)

export const searchStoreList = (keyword = '') =>
  api.get('/store/search', { params: { keyword } })

export const getStoreInventoryByStore = (storeIdx) =>
  api.get(`/store/inventory/list/${storeIdx}`)

export const getPresignedUrl = (fileName) =>
  api.get(`/store/presignedUrl/${fileName}`)

export const getNewRegister = (storeRegDto)=>
  api.get('/store/new/register',storeRegDto)
