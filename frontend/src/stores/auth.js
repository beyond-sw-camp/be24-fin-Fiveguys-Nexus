import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('nexus_user') || 'null'))

  const isLoggedIn = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isStoreOwner = computed(() => user.value?.role === 'STORE_OWNER')

  const MOCK_USERS = {
    'admin@theventi.co.kr': {
      id: 'admin@theventi.co.kr', name: '이재혁', role: 'ADMIN',
      dept: 'SCM 통제센터', email: 'jh.lee@hanwha-nexus.com', avatar: 'HQ',
    },
    'store01@theventi.co.kr': {
      id: 'store01@theventi.co.kr', name: '김동현', role: 'STORE_OWNER',
      storeName: '한화빌딩점', storeId: 'S001', email: 'dh.kim@store.com', avatar: 'KD',
    },
  }

  function login(userId, password) {
    if (password !== 'password123') return false
    const found = MOCK_USERS[userId]
    if (!found) return false
    user.value = found
    localStorage.setItem('nexus_user', JSON.stringify(found))
    return true
  }

  function logout() {
    user.value = null
    localStorage.removeItem('nexus_user')
  }

  return { user, isLoggedIn, isAdmin, isStoreOwner, login, logout }
})
