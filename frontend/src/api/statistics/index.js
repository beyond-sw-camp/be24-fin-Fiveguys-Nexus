import api from '@/plugins/axiosinterceptor'

export const getTodaySales = () => api.get('/statistics/sales/today')

export const getTopStores = () => api.get('/statistics/store/top')

export const getTopMenus = () => api.get('/statistics/menu/top')

export const getHourlySales = () => api.get('/statistics/sales/hourly')

export const getCategoryRatio = () => api.get('/statistics/category/ratio')

export const getPaymentRatio = () => api.get('/statistics/payment/ratio')
