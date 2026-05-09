import api from '@/plugins/axiosinterceptor.js'

export const postReportGenerate = (ChatRequest)=>
  api.post('/report/generate', ChatRequest)
