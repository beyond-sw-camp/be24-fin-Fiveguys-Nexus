<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">가맹점 관리</h1>
        <p class="page-spec-hint">
          <code>STORE_001~004</code>신규 등록·목록·지점명/지역 검색·상태(정상운영·점검·휴업) 관리. 삭제 시 소프트 삭제(상태 변경) 및 재확인을 권장합니다.
        </p>
      </div>
      <button @click="openModal(null)"
        class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded hover:bg-[#e0661d] transition-colors flex items-center gap-2">
        <Plus class="w-4 h-4" /> 신규 가맹점 등록
      </button>
    </div>

    <!-- Summary -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">전체 가맹점</p>
          <p class="text-3xl font-black text-gray-900 mt-2">{{ stores.length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
        </div>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">정상운영</p>
          <p class="text-3xl font-black text-green-600 mt-2">{{ stores.filter(s => s.status === '정상운영').length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
        </div>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-5">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">점검·휴업</p>
          <p class="text-3xl font-black text-amber-500 mt-2">{{ stores.filter(s => s.status !== '정상운영').length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
        </div>
      </div>
    </div>

    <!-- Search + 지역 (STORE_004) -->
    <div class="flex flex-wrap gap-3 items-center">
      <div class="flex rounded-lg border border-gray-200 bg-gray-50/90 p-0.5">
        <button
          v-for="r in regionChips"
          :key="r"
          type="button"
          class="text-xs px-3 py-1.5 rounded-md font-semibold transition-colors"
          :class="filterRegionChip === r ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'"
          @click="filterRegionChip = r"
        >
          {{ r }}
        </button>
      </div>
      <div class="relative">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input v-model="searchQuery" type="text" placeholder="가맹점명 또는 지역 검색..."
          class="pl-9 pr-4 py-2 rounded border border-gray-200 text-sm w-64 focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">ID</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">점주명</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">지역</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">연락처</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">개설일</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">상태</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <template v-if="filteredStores.length === 0">
            <tr>
              <td colspan="8" class="px-5 py-10 text-center text-sm text-gray-400">조건에 맞는 가맹점이 없습니다.</td>
            </tr>
          </template>
          <tr v-for="store in filteredStores" :key="store.id" class="hover:bg-gray-50/50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ store.id }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ store.name }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ store.owner }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ store.region }}</td>
            <td class="px-5 py-3.5 text-gray-500 text-xs font-mono">{{ store.phone }}</td>
            <td class="px-5 py-3.5 text-gray-400 text-xs">{{ store.openDate }}</td>
            <td class="px-5 py-3.5">
              <span class="text-xs font-bold px-2 py-0.5 rounded"
                :class="store.status === '정상운영'
                  ? 'bg-green-50 text-green-700 border border-green-200'
                  : 'bg-amber-50 text-amber-600 border border-amber-200'">
                {{ store.status }}
              </span>
            </td>
            <td class="px-5 py-3.5">
              <div class="flex justify-center gap-3">
                <button @click="openModal(store)" class="text-gray-300 hover:text-[#F37321] transition-colors">
                  <Pencil class="w-4 h-4" />
                </button>
                <button @click="deleteStore(store.id)" class="text-gray-300 hover:text-red-500 transition-colors">
                  <Trash2 class="w-4 h-4" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="showModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-lg border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">{{ editTarget ? '가맹점 정보 수정' : '신규 가맹점 등록' }}</h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600">✕</button>
        </div>
        <form @submit.prevent="saveStore" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">가맹점명</label>
              <input v-model="form.name" required type="text"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">점주명</label>
              <input v-model="form.owner" required type="text"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">지역</label>
              <input v-model="form.region" type="text"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">연락처</label>
              <input v-model="form.phone" type="text"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">개설일</label>
              <input v-model="form.openDate" type="date"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">상태</label>
              <select v-model="form.status"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none">
                <option>정상운영</option>
                <option>점검중</option>
                <option>휴업</option>
              </select>
            </div>
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showModal = false"
              class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50">취소</button>
            <button type="submit"
              class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d]">저장</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Pencil, Trash2, Search } from 'lucide-vue-next'

const searchQuery = ref('')
const filterRegionChip = ref('전체')
const regionChips = ['전체', '서울', '경기', '부산']

const filteredStores = computed(() => {
  let list = stores.value
  if (filterRegionChip.value === '서울') list = list.filter((s) => s.region.includes('서울'))
  else if (filterRegionChip.value === '경기') list = list.filter((s) => s.region.includes('경기'))
  else if (filterRegionChip.value === '부산') list = list.filter((s) => s.region.includes('부산'))
  if (!searchQuery.value.trim()) return list
  const q = searchQuery.value.trim()
  return list.filter(
    (s) => s.name.includes(q) || s.region.includes(q) || s.owner.includes(q),
  )
})

const stores = ref([
  { id: 'S001', name: '한화빌딩점',       owner: '김동현', region: '서울 중구',    phone: '02-1111-2222', openDate: '2022-03-15', status: '정상운영' },
  { id: 'S002', name: '여의도역점',       owner: '이재혁', region: '서울 영등포구', phone: '02-3333-4444', openDate: '2022-07-20', status: '정상운영' },
  { id: 'S003', name: '판교테크노밸리점', owner: '박민수', region: '경기 성남',    phone: '031-555-6666', openDate: '2023-01-10', status: '정상운영' },
  { id: 'S004', name: '부산센텀점',       owner: '정수진', region: '부산 해운대구', phone: '051-777-8888', openDate: '2023-05-30', status: '점검중'   },
])

const showModal = ref(false)
const editTarget = ref(null)
const form = ref({ name: '', owner: '', region: '', phone: '', openDate: '', status: '정상운영' })

function openModal(store) {
  editTarget.value = store
  form.value = store ? { ...store } : { name: '', owner: '', region: '', phone: '', openDate: '', status: '정상운영' }
  showModal.value = true
}

function saveStore() {
  if (editTarget.value) {
    Object.assign(editTarget.value, form.value)
  } else {
    const newId = 'S' + String(stores.value.length + 1).padStart(3, '0')
    stores.value.push({ id: newId, ...form.value })
  }
  showModal.value = false
}

function deleteStore(id) {
  if (confirm('가맹점을 삭제하시겠습니까?')) {
    stores.value = stores.value.filter(s => s.id !== id)
  }
}
</script>
