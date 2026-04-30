import api from '@/plugins/axiosinterceptor'

export const getPosInventoryList = () => api.get('/pos/inventory/list')

export const changePosInventoryCount = (posStoreInventoryIdx, count) =>
  api.patch(`/pos/inventory/${posStoreInventoryIdx}`, { count })
