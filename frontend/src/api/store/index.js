import api from '@/plugins/axiosinterceptor'

const DEFAULT_STORE_LIST_FILTER = {
  keyword: '',
  status: undefined,
}

const normalizeStoreListFilter = (searchReq = {}) => {
  const keyword = typeof searchReq.keyword === 'string' ? searchReq.keyword.trim() : DEFAULT_STORE_LIST_FILTER.keyword
  const status = typeof searchReq.status === 'string' && searchReq.status.trim() ? searchReq.status.trim() : DEFAULT_STORE_LIST_FILTER.status
  return { keyword, status }
}

export const getStoreList = (searchReq = DEFAULT_STORE_LIST_FILTER, page = 0, size = 10) => {
  const filter = normalizeStoreListFilter(searchReq)
  return api.get('/store/list', {
    params: {
      keyword: filter.keyword,
      status: filter.status,
      page: page,
      size: size
    }
  });
}
export const getStoreDetailList = (storeIdx) => api.get(`/store/detail/list/${storeIdx}`)

export const searchStoreList = (keyword = '') =>
  api.get('/store/search', { params: { keyword } })

export const getStoreInventoryByStore = (storeIdx, page = 0, size = 10) =>
  api.get(`/store/inventory/list/${storeIdx}`, { params: { page, size } })

export const getPresignedUrl = (fileName) =>
  api.get(`/store/presignedUrl/${fileName}`)

export const postNewRegister = (storeRegDto)=>
  api.post('/store/new/register',storeRegDto)

export const putStoreUpdate = (storeIdx,updateData) =>
  api.put(`/store/update/${storeIdx}`,updateData)
