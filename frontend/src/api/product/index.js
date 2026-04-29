import api from '@/plugins/axiosinterceptor'

export const getProductList = () => api.get('/product/list')
