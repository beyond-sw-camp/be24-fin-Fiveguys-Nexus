import api from '@/plugins/axiosinterceptor'

export const createPosWasteLog = (body) => api.post('/wastelog/waste/pos', body)

export const postStoreWasteDiscard = (body) => api.post('/wastelog/store/discard', body)
