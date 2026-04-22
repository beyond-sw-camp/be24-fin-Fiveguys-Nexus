<template>
  <div class="p-5 space-y-4">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">계정 관리</h1>
      </div>
      <button
        @click="openCreateModal"
        class="inline-flex items-center gap-2 px-4 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] cursor-pointer"
      >
        + 계정 생성
      </button>
    </div>

    <section class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <div class="px-5 py-3 border-b border-gray-100 flex items-center justify-between">
        <h2 class="text-sm font-bold text-gray-800">계정 리스트</h2>
        <span class="text-xs text-gray-400">총 {{ accounts.length }}명</span>
      </div>

      <table class="w-full text-sm text-left">
        <thead>
          <tr class="bg-gray-50 border-b border-gray-200">
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">ID</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">이름</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">부서</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">이메일</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">아바타</th>
            <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="account in accounts" :key="account.id" class="hover:bg-gray-50/60">
            <td class="px-5 py-3.5 text-xs font-mono text-gray-500">{{ account.id }}</td>
            <td class="px-5 py-3.5 font-semibold text-gray-900">{{ account.name }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ account.dept }}</td>
            <td class="px-5 py-3.5 text-gray-600">{{ account.email }}</td>
            <td class="px-5 py-3.5">
              <span class="inline-flex items-center justify-center w-7 h-7 rounded border border-gray-200 bg-gray-50 text-xs font-bold text-[#F37321]">
                {{ account.avatar }}
              </span>
            </td>
            <td class="px-5 py-3.5">
              <div class="flex items-center justify-center gap-2">
                <button
                  @click="openEditModal(account)"
                  class="px-2.5 py-1.5 rounded border border-[#F37321] text-xs font-semibold text-[#F37321] hover:bg-orange-50 cursor-pointer"
                >
                  수정
                </button>
                <button
                  @click="openDeleteModal(account)"
                  class="px-2.5 py-1.5 rounded border border-red-400 text-xs font-semibold text-red-500 hover:bg-red-50 cursor-pointer"
                >
                  삭제
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="accounts.length === 0">
            <td colspan="6" class="px-5 py-12 text-center text-gray-400 text-sm">등록된 계정이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </section>

    <div v-if="isFormModalOpen" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="closeFormModal"></div>
      <div class="relative w-full max-w-lg bg-white rounded-lg border border-gray-200 shadow-xl">
        <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h3 class="font-bold text-gray-900">{{ formMode === 'create' ? '계정 생성' : '계정 수정' }}</h3>
          <button @click="closeFormModal" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <form class="p-6 space-y-4" @submit.prevent="submitForm">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">사번 (ID)</label>
              <input
                v-model.trim="form.id"
                type="text"
                :readonly="formMode === 'edit'"
                placeholder="예: A002"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none read-only:bg-gray-100 read-only:text-gray-500"
                required
              />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">이름</label>
              <input
                v-model.trim="form.name"
                type="text"
                placeholder="예: 홍길동"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
                required
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">부서</label>
              <input
                v-model.trim="form.dept"
                type="text"
                placeholder="예: 운영지원팀"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
                required
              />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">이메일</label>
              <input
                v-model.trim="form.email"
                type="email"
                placeholder="예: user@hanwha-nexus.com"
                class="w-full px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none"
                required
              />
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-xs font-bold text-gray-500 uppercase tracking-wider">아바타 이니셜</label>
            <input
              v-model.trim="form.avatar"
              type="text"
              maxlength="2"
              placeholder="예: HK"
              class="w-full max-w-24 px-3 py-2 rounded border border-gray-200 text-sm focus:border-[#F37321] focus:ring-2 focus:ring-[#F37321]/10 outline-none uppercase"
              required
            />
          </div>

          <div class="flex gap-3 pt-2">
            <button
              type="button"
              @click="closeFormModal"
              class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer"
            >
              취소
            </button>
            <button
              type="submit"
              class="flex-1 py-2.5 rounded bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] cursor-pointer"
            >
              {{ formMode === 'create' ? '생성' : '수정 저장' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="isDeleteModalOpen" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/40" @click="closeDeleteModal"></div>
      <div class="relative w-full max-w-sm bg-white rounded-lg border border-gray-200 shadow-xl p-6">
        <h3 class="text-base font-bold text-gray-900">계정 삭제</h3>
        <p class="text-sm text-gray-600 mt-2">
          <span class="font-semibold">{{ selectedAccount?.name }}</span> 계정을 삭제하시겠습니까?
        </p>
        <p class="text-xs text-gray-400 mt-1">삭제된 계정은 복구할 수 없습니다.</p>
        <div class="flex gap-3 mt-5">
          <button
            type="button"
            @click="closeDeleteModal"
            class="flex-1 py-2.5 rounded border border-gray-200 text-sm font-semibold text-gray-600 hover:bg-gray-50 cursor-pointer"
          >
            취소
          </button>
          <button
            type="button"
            @click="confirmDelete"
            class="flex-1 py-2.5 rounded bg-red-500 text-white text-sm font-bold hover:bg-red-600 cursor-pointer"
          >
            삭제
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const STORAGE_KEY = 'nexus_admin_accounts'

const defaultAccounts = [
  {
    id: 'A001',
    name: '이재혁',
    role: 'ADMIN',
    dept: 'SCM 통제센터',
    email: 'jh.lee@hanwha-nexus.com',
    avatar: 'HQ',
  },
]

const accounts = ref(loadAccounts())
const isFormModalOpen = ref(false)
const isDeleteModalOpen = ref(false)
const formMode = ref('create')
const selectedAccount = ref(null)

const form = reactive({
  id: '',
  name: '',
  dept: '',
  email: '',
  avatar: '',
})

function loadAccounts() {
  try {
    const parsed = JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
    if (!Array.isArray(parsed) || parsed.length === 0) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(defaultAccounts))
      return [...defaultAccounts]
    }
    return parsed
  } catch {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(defaultAccounts))
    return [...defaultAccounts]
  }
}

function saveAccounts(nextAccounts) {
  accounts.value = nextAccounts
  localStorage.setItem(STORAGE_KEY, JSON.stringify(nextAccounts))
}

function resetForm() {
  form.id = ''
  form.name = ''
  form.dept = ''
  form.email = ''
  form.avatar = ''
}

function openCreateModal() {
  formMode.value = 'create'
  selectedAccount.value = null
  resetForm()
  isFormModalOpen.value = true
}

function openEditModal(account) {
  formMode.value = 'edit'
  selectedAccount.value = account
  form.id = account.id
  form.name = account.name
  form.dept = account.dept
  form.email = account.email
  form.avatar = account.avatar
  isFormModalOpen.value = true
}

function closeFormModal() {
  isFormModalOpen.value = false
  resetForm()
}

function submitForm() {
  const normalizedId = form.id.toUpperCase()
  const normalizedAvatar = form.avatar.toUpperCase()

  if (formMode.value === 'create') {
    if (accounts.value.some((account) => account.id === normalizedId)) {
      alert('이미 존재하는 계정 ID 입니다.')
      return
    }

    saveAccounts([
      {
        id: normalizedId,
        name: form.name,
        role: 'ADMIN',
        dept: form.dept,
        email: form.email,
        avatar: normalizedAvatar,
      },
      ...accounts.value,
    ])
    alert('계정이 생성되었습니다.')
  } else {
    if (!selectedAccount.value) return
    saveAccounts(
      accounts.value.map((account) =>
        account.id === selectedAccount.value.id
          ? { ...account, name: form.name, dept: form.dept, email: form.email, avatar: normalizedAvatar }
          : account
      )
    )
    alert('계정이 수정되었습니다.')
  }

  closeFormModal()
}

function openDeleteModal(account) {
  selectedAccount.value = account
  isDeleteModalOpen.value = true
}

function closeDeleteModal() {
  isDeleteModalOpen.value = false
  selectedAccount.value = null
}

function confirmDelete() {
  if (!selectedAccount.value) return
  saveAccounts(accounts.value.filter((account) => account.id !== selectedAccount.value.id))
  closeDeleteModal()
  alert('계정이 삭제되었습니다.')
}
</script>
