import api from '@/plugins/axiosinterceptor.js'

export const postReportGenerate = (chatRequest) =>
  api.post('/report/generate', chatRequest)
