import api from '@/plugins/axiosinterceptor'

export const getMenuList = (searchReq, page, size) => {
  return api.get('/menu/list', {
    params: {
      keyword: searchReq.keyword,
      categoryIdx: searchReq.categoryIdx,
      page: page,
      size: size
    }
  });
}

export const getProductList = () => api.get('/product/list')

export const getCategoryList = () => api.get('/menu/category/list')

export const getMenuItemList = (menuIdx) => api.get(`/menu/item/list/${menuIdx}`)

export const getPresignedUrl = (fileName) =>
  api.get(`/menu/presignedUrl/${fileName}`)

export const postNewRegister = (menuRegReq) =>
  api.post('/menu/new/register', menuRegReq)

export const putMenuUpdate = (menuIdx,menuRegReq) =>
  api.put(`/menu/update/${menuIdx}`, menuRegReq)

export const putMenuDelete  = (menuIdx) =>
  api.put(`menu/delete/${menuIdx}`)
