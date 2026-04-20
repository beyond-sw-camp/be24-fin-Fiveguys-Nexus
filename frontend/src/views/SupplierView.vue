<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-end">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">거래처 관리</h1>
        <p class="page-spec-hint">
          본사 정산·발주와 연계되는 거래처 마스터(요구사항 정의서 확장 시연). 등록·조회·상태 관리.
        </p>
      </div>
      <button @click="openModal(null)"
        class="bg-[#F37321] text-white px-4 py-2 rounded-lg font-medium hover:opacity-90 flex items-center gap-2">
        <Plus class="w-4 h-4" /> 거래처 등록
      </button>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
      <table class="w-full text-sm text-left">
        <thead class="bg-gray-50 text-gray-500 uppercase text-xs">
          <tr>
            <th class="px-6 py-4">거래처 ID</th>
            <th class="px-6 py-4">거래처명</th>
            <th class="px-6 py-4">담당자</th>
            <th class="px-6 py-4">연락처</th>
            <th class="px-6 py-4">취급 품목</th>
            <th class="px-6 py-4">상태</th>
            <th class="px-6 py-4 text-center">관리</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="s in suppliers" :key="s.id" class="hover:bg-gray-50/50">
            <td class="px-6 py-4 font-mono text-gray-400">{{ s.id }}</td>
            <td class="px-6 py-4 font-semibold text-gray-900">{{ s.name }}</td>
            <td class="px-6 py-4 text-gray-600">{{ s.contact }}</td>
            <td class="px-6 py-4 text-gray-600">{{ s.phone }}</td>
            <td class="px-6 py-4">
              <span v-for="tag in s.categories" :key="tag"
                class="inline-block mr-1 mb-1 px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs">
                {{ tag }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded-full text-xs font-bold"
                :class="s.active ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
                {{ s.active ? '거래중' : '거래중단' }}
              </span>
            </td>
            <td class="px-6 py-4">
              <div class="flex justify-center gap-2">
                <button @click="openModal(s)" class="text-gray-400 hover:text-[#F37321]">
                  <Pencil class="w-4 h-4" />
                </button>
                <button @click="deleteSupplier(s.id)" class="text-gray-400 hover:text-red-500">
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
      <div class="absolute inset-0 bg-black/30" @click="showModal = false"></div>
      <div class="relative bg-white rounded-2xl p-8 w-full max-w-lg shadow-xl">
        <h3 class="text-lg font-bold text-gray-900 mb-6">
          {{ editTarget ? '거래처 정보 수정' : '신규 거래처 등록' }}
        </h3>
        <form @submit.prevent="saveSupplier" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-sm font-medium text-gray-600">거래처명</label>
              <input v-model="form.name" required type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm focus:ring-2 focus:ring-[#F37321]/20 focus:border-[#F37321] outline-none" />
            </div>
            <div class="space-y-1.5">
              <label class="text-sm font-medium text-gray-600">담당자명</label>
              <input v-model="form.contact" required type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm focus:ring-2 focus:ring-[#F37321]/20 focus:border-[#F37321] outline-none" />
            </div>
          </div>
          <div class="space-y-1.5">
            <label class="text-sm font-medium text-gray-600">연락처</label>
            <input v-model="form.phone" type="text"
              class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm focus:ring-2 focus:ring-[#F37321]/20 focus:border-[#F37321] outline-none" />
          </div>
          <div class="space-y-1.5">
            <label class="text-sm font-medium text-gray-600">취급 품목 (쉼표 구분)</label>
            <input v-model="categoryInput" type="text" placeholder="예) 유제품, 원두, 포장재"
              class="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm focus:ring-2 focus:ring-[#F37321]/20 focus:border-[#F37321] outline-none" />
          </div>
          <div class="flex items-center gap-2 pt-1">
            <input v-model="form.active" type="checkbox" id="activeCheck" class="accent-[#F37321]" />
            <label for="activeCheck" class="text-sm text-gray-600">거래중 상태</label>
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showModal = false"
              class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-medium text-gray-600 hover:bg-gray-50">취소</button>
            <button type="submit"
              class="flex-1 py-2.5 rounded-lg bg-[#F37321] text-white text-sm font-bold hover:opacity-90">저장</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'

const suppliers = ref([
  { id: 'V001', name: '서울우유', contact: '박영수', phone: '02-1234-5678', categories: ['유제품', '두유'], active: true },
  { id: 'V002', name: '동서식품', contact: '최민준', phone: '02-9876-5432', categories: ['원두', '커피'], active: true },
  { id: 'V003', name: '한국포장', contact: '정수빈', phone: '031-555-1234', categories: ['종이컵', '포장재'], active: true },
  { id: 'V004', name: '청정원F&B', contact: '이영희', phone: '02-2222-3333', categories: ['시럽', '소스'], active: false },
])

const showModal = ref(false)
const editTarget = ref(null)
const form = ref({ name: '', contact: '', phone: '', active: true })
const categoryInput = ref('')

function openModal(supplier) {
  editTarget.value = supplier
  if (supplier) {
    form.value = { name: supplier.name, contact: supplier.contact, phone: supplier.phone, active: supplier.active }
    categoryInput.value = supplier.categories.join(', ')
  } else {
    form.value = { name: '', contact: '', phone: '', active: true }
    categoryInput.value = ''
  }
  showModal.value = true
}

function saveSupplier() {
  const categories = categoryInput.value.split(',').map(s => s.trim()).filter(Boolean)
  if (editTarget.value) {
    Object.assign(editTarget.value, { ...form.value, categories })
  } else {
    const newId = 'V' + String(suppliers.value.length + 1).padStart(3, '0')
    suppliers.value.push({ id: newId, ...form.value, categories })
  }
  showModal.value = false
}

function deleteSupplier(id) {
  if (confirm('거래처를 삭제하시겠습니까?')) {
    suppliers.value = suppliers.value.filter(s => s.id !== id)
  }
}
</script>
