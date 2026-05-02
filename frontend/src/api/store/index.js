import api from '@/plugins/axiosinterceptor'

export const getStoreList = () => api.get('/store/list')

export const getStoreDetailList = (storeIdx) => api.get(`/store/detail/list/${storeIdx}`)

export const searchStoreList = (keyword = '') =>
  api.get('/store/search', { params: { keyword } })

export const getStoreInventoryByStore = (storeIdx) =>
  api.get(`/store/inventory/list/${storeIdx}`)

export const getPresignedUrl = (fileName) =>
  api.get(`/store/presignedUrl/${fileName}`)

export const postNewRegister = (storeRegDto)=>
  api.post('/store/new/register',storeRegDto)

export const readProductList = () => api.get('/product/list')
