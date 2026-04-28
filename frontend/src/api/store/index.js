import api from '@/plugins/axiosinterceptor.js'

const getStoreList = async () => {
  const res = await api.get('/store/list')
  return res.data
}

export default {
  getStoreList,
}
