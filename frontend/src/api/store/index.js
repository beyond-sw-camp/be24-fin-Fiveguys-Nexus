import api from '@/plugins/axiosinterceptor'

export const getStoreList = (searchReq, page, size) => {
  return api.get('/store/list', {
    params: {
      keyword: searchReq.keyword,
      status: searchReq.status,
      page: page,
      size: size
    }
  });
}

export const getStoreTotal = () =>
  api.get('/store/totalCount/list')

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
