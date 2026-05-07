import api from '@/plugins/axiosinterceptor'

const getMenuList = (searchReq, page, size) => {
  return api.get('/menu/list', {
    params: {
      keyword: searchReq.keyword,
      categoryIdx: searchReq.categoryIdx,
      page: page,
      size: size
    }
  });
}
