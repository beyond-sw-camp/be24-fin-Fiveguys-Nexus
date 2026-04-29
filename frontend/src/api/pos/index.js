import api from '@/plugins/axiosinterceptor'

export const getPosInventoryList = () => api.get('/pos/inventory/list')
