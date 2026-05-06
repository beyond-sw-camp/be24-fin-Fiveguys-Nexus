import api from '@/plugins/axiosinterceptor'

// 제품 목록 조회
export const getProductList = () =>
  api.get('/product/list')

// 신규 제품 등록
export const addNewProduct = (dto) =>
  api.post('/product/reg', dto)

// 기존 제품 수정
export const updateProduct = (idx, dto) =>
  api.put(`/product/update/${idx}`, dto)

// 제품 삭제
export const deleteProduct = (idx) =>
  api.delete(`/product/delete/${idx}`)

// 제품 검색
export const searchProduct = (productName) =>
  api.get('/product/search', { params: { productName } })

// 가맹점별 제품 목록 조회
export const getStoreProductList = () =>
  api.get(`/product/store`)
