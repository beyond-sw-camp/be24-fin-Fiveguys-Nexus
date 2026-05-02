import { ref } from 'vue'
import ordersApi from '@/api/orders'

export function useAddOrderItem(productList, fetchPendingOrders) {
  const addItemForm = ref(null)

  function openAddItemForm(order) {
    addItemForm.value = { ordersIdx: order.idx, productIdx: null, productName: '', keyword: '', showDropdown: false, count: 1 }
  }

  function filteredProducts(order) {
    const existing = new Set(order.ordersItemList.map(i => i.productName))
    const keyword = addItemForm.value?.keyword?.trim() || ''
    return productList.value.filter(p => !existing.has(p.productName) && (keyword === '' || p.productName.includes(keyword)))
  }

  function selectAddItemProduct(product) {
    addItemForm.value.productIdx = product.idx
    addItemForm.value.productName = product.productName
    addItemForm.value.showDropdown = false
  }

  function clearAddItemProduct() {
    addItemForm.value.productIdx = null
    addItemForm.value.productName = ''
    addItemForm.value.keyword = ''
  }

  async function submitAddItem(order) {
    const form = addItemForm.value
    if (!form.productIdx) { alert('품목을 선택해주세요.'); return }
    if (!form.count || form.count < 1) { alert('수량을 입력해주세요.'); return }
    try {
      await ordersApi.addStoreOrderItem(order.idx, {
        productIdx: form.productIdx,
        count: form.count,
      })
      addItemForm.value = null
      fetchPendingOrders()
    } catch (e) {
      console.error('품목 추가 실패', e)
      alert('품목 추가에 실패했습니다.')
    }
  }

  return {
    addItemForm,
    openAddItemForm,
    filteredProducts,
    selectAddItemProduct,
    clearAddItemProduct,
    submitAddItem,
  }
}
