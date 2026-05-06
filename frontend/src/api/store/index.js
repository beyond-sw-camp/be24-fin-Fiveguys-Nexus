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
export const getStoreDetailList = (storeIdx) => api.get(`/store/detail/list/${storeIdx}`)

export const searchStoreList = (keyword = '') =>
  api.get('/store/search', { params: { keyword } })

export const getStoreInventoryByStore = (storeIdx) =>
  api.get(`/store/inventory/list/${storeIdx}`)

export const getPresignedUrl = (fileName) =>
  api.get(`/store/presignedUrl/${fileName}`)

export const postNewRegister = (storeRegDto)=>
  api.post('/store/new/register',storeRegDto)

export const putStoreUpdate = (storeIdx,updateData) =>
  api.put(`/store/update/${storeIdx}`,updateData)
