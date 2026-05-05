import api from '@/plugins/axiosinterceptor'

export const getSalesKpi = () => api.get('/store/dashboard/sales/kpi')

export const getPendingOrderKpi = () => api.get('/store/dashboard/orders/pending/kpi')

export const getInventoryRiskKpi = () => api.get('/store/dashboard/inventory/risk/kpi')
