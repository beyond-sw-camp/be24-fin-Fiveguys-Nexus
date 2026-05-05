import api from '@/plugins/axiosinterceptor'

export const getSalesKpi = () => api.get('/store/dashboard/sales/kpi')
