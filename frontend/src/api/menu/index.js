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
