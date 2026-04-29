<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, FileText, ChevronDown } from 'lucide-vue-next'
import { getStoreList , getStoreDetailList} from '@/api/store/index.js'

const searchQuery = ref('')
const filterStatus = ref('전체')
const activeDropdown = ref(null)

const statusOptions = ['전체', '입점', '폐점']

const storesList = reactive([])


const storeListRes = async ()=>{
  const res = await getStoreList()
  storesList.push(...res.data.result)

}
const filteredStores = computed(() => {
  let list = [...storesList];

  list = list.filter(s => {
    if (filterStatus.value === '폐점') {
      return s.status === '폐점'; // '폐점' 필터일 때는 폐점인 것만 반환
    } else if (filterStatus.value === '입점') {
      return s.status === '입점'; // '입점' 필터일 때는 폐점이 아닌 것(운영중)만 반환
    }
    return true; // '전체'일 때는 모두 반환
  })
  console.log(list)

// 2. 검색어 필터링 (storeName, ownerName, address 기준)
  const q = searchQuery.value.trim().toLowerCase()
  if (q) {
    list = list.filter(s =>
      (s.storeName?.toLowerCase().includes(q)) ||
      (s.ownerName?.toLowerCase().includes(q)) ||
      (s.address?.toLowerCase().includes(q))
    )
  }

  // 3. 정렬 (입점 중인 매장 우선)
  return list.sort((a, b) => {
    // a가 폐점이고 b가 입점이면, a를 뒤로 보냄 (1)
    if (a.status === "폐점" && b.status === "입점") return 1
    // a가 입점이고 b가 폐점이면, a를 앞으로 보냄 (-1)
    if (a.status === "입점" && b.status === "폐점") return -1
    // 상태가 같으면 순서를 유지함
    return 0
  })
})

const showModal = ref(false)
const showDetailModal = ref(false)
const editTarget = ref(null)
const detailTarget = ref(null)
const form = ref({ storeName: '', ownerName: '', address: '', business: '', ownerEmail: '', openedAt: '', status: '입점', bizPdfName: ''})

function toggleDropdown(type) {
  activeDropdown.value = activeDropdown.value === type ? null : type
}

function selectFilter(type, value) {
  if (type === 'status') filterStatus.value = value
  activeDropdown.value = null
}

function handleSearch() {}

// 상세 모달
async function openDetail(idx) {
  const res = await getStoreDetailList(idx)
  console.log(res.data.result)
  detailTarget.value = res.data.result
  showDetailModal.value = true
}

function openModal(store) {
  editTarget.value = store
  form.value = store
    ? { ...store, bizPdfName: '' }
    : { name: '', owner: '', details: '', bizNumber: '', email: '', openDate: '', closeDate: '', bizPdfName: '' }
  showModal.value = true
}

function handleFileChange(e) {
  const file = e.target.files[0]
  if (file) form.value.bizPdfName = file.name
}

// 저장 로직 (백엔드 연동 전 임시 처리)
function saveStore() {
  if (editTarget.value) {
    Object.assign(editTarget.value, form.value)
  } else {
    storesList.push({ idx: Date.now(), ...form.value })
  }
  showModal.value = false
}

function downloadPdf() {
  alert('사업자 등록증 PDF를 다운로드합니다.')
}

onMounted(() => {
  storeListRes()
})
</script>


<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">입점 가맹점 관리</h1>
      </div>
      <button @click="openModal(null)"
              class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded-lg hover:bg-[#e0661d] transition-colors flex items-center gap-2 shadow-sm cursor-pointer">
        <Plus class="w-4 h-4" /> 신규 가맹점 등록
      </button>
    </div>

    <!-- Summary -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">전체 입점 가맹점</p>
        <p class="text-3xl font-black text-gray-900 mt-2">{{ storesList.length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">입점</p>
        <p class="text-3xl font-black text-green-600 mt-2">{{storesList.filter(s => s.status === '입점').length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">폐점 가맹점</p>
        <p class="text-3xl font-black text-red-500 mt-2">{{ storesList.filter(s => s.status === '폐점').length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
      </div>
    </div>

    <!-- Filters & Search -->
    <div class="flex items-center justify-between gap-4">
      <div class="flex items-center gap-2 flex-1 max-w-md">
        <div class="relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="가맹점명 검색"
            @keyup.enter="handleSearch"
            class="pl-10 pr-4 py-2 rounded-lg border border-gray-200 text-sm w-52
             bg-white shadow-sm
             focus:border-[#F37321] focus:ring-1 focus:ring-[#F37321]
             outline-none transition-colors"
          />
        </div>
        <button @click="handleSearch"
                class="px-4 py-2 bg-white border border-gray-300 text-gray-600 text-sm font-bold rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-colors shadow-sm whitespace-nowrap cursor-pointer">
          검색
        </button>
      </div>

      <div class="flex items-center gap-3">
        <div class="relative">
          <button @click="toggleDropdown('status')"
                  class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-lg text-sm font-bold text-gray-700 hover:border-gray-300 transition-all shadow-sm min-w-[120px] justify-between cursor-pointer">
            <span>상태: {{ filterStatus }}</span>
            <ChevronDown class="w-4 h-4 text-gray-400 transition-transform" :class="{ 'rotate-180': activeDropdown === 'status' }" />
          </button>
          <div v-if="activeDropdown === 'status'" class="absolute right-0 mt-1 w-40 bg-white border border-gray-200 rounded-lg shadow-lg z-20 py-1 animate-in fade-in zoom-in-95 duration-100">
            <button v-for="s in statusOptions" :key="s" @click="selectFilter('status', s)"
                    class="w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors cursor-pointer"
                    :class="filterStatus === s ? 'text-[#F37321] font-bold' : 'text-gray-600'">
              {{ s }}
            </button>
          </div>
        </div>
      </div>
      <div v-if="activeDropdown" class="fixed inset-0 z-10" @click="activeDropdown = null"></div>
    </div>

    <!-- Table Section -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden shadow-sm">
      <table class="w-full text-sm text-left">
        <thead>
        <tr class="border-b border-gray-200 bg-gray-50">
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점 코드</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">담당자명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">이메일</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">가맹점 위치</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">사업자번호</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">운영상태</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
        <tr v-for="store in filteredStores" :key="store.idx"
            @click="openDetail(store.idx)"
            class="hover:bg-gray-50/50 transition-colors cursor-pointer group"
            :class="{ 'bg-gray-50/40 opacity-70': store.status === '폐점' }">
          <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ store.idx }}</td>
          <td class="px-5 py-3.5 font-bold text-gray-900 group-hover:text-[#F37321] transition-colors">
            {{ store.storeName }}
          </td>
          <td class="px-5 py-3.5 text-gray-600">{{ store.ownerName }}</td>
          <td class="px-5 py-3.5 text-gray-500 text-xs truncate max-w-[150px]">{{ store.ownerEmail }}</td>
          <td class="px-5 py-3.5 text-gray-500 text-xs">{{ store.address }}</td>
          <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ store.business }}</td>
          <td class="px-5 py-3.5 text-center">
              <span class="text-[11px] font-bold px-2 py-0.5 rounded-lg"
                    :class="store.status === '입점'
                  ? 'bg-green-100 text-green-700'
                  : 'bg-red-50 text-red-600'">
                {{ store.status }}
              </span>
          </td>
          <td class="px-5 py-3.5">
            <div class="flex justify-center">
              <button
                @click.stop="openModal(store)"
                :disabled="store.status === '폐점'"
                class="px-3 py-1.5 text-xs font-semibold rounded transition-colors shadow-sm"
                :class="store.status === '입점'
                ? 'text-[#F37321] border border-[#F37321] hover:bg-orange-50 cursor-pointer'
                : 'text-gray-400 border border-gray-200 bg-gray-50 cursor-not-allowed'"
              >
                수정
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 입점 가맹점 상세 정보 모달 -->
    <div v-if="showDetailModal" class="fixed inset-0 z-[60] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40" @click="showDetailModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-lg border border-gray-200 shadow-xl overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">입점 가맹점 상세 정보</h3>
          <button @click="showDetailModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <div class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">가맹점 코드</label>
              <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm font-mono text-gray-500">
                {{ detailTarget?.idx }}
              </div>
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">가맹점명</label>
              <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm font-bold text-gray-900">
                {{ detailTarget?.storeName }}
              </div>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-5">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">담당자명</label>
              <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm text-gray-700">
                {{ detailTarget?.ownerName }}
              </div>
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">이메일 주소</label>
              <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm text-gray-700">
                {{ detailTarget?.ownerEmail }}
              </div>
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">가맹점 위치</label>
            <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm text-gray-600">
              {{ detailTarget?.address }}
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자번호</label>
            <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm font-mono text-gray-500">
              {{ detailTarget?.business }}
            </div>
          </div>

          <div class="grid grid-cols-2 gap-5 p-4 bg-gray-50 rounded-lg border border-gray-100">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest flex items-center gap-1">
                <div class="w-1 h-1 bg-green-400 rounded-full"></div> 개업일
              </label>
              <div class="text-sm font-bold text-gray-700">{{ detailTarget?.createdAt || '-' }}</div>
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest flex items-center gap-1">
                <div class="w-1 h-1 rounded-full" :class="detailTarget?.closedAt === '운영 중' ? 'bg-gray-300' : 'bg-red-400'"></div>
                폐업일
              </label>

              <div class="text-sm font-bold"
                   :class="detailTarget?.closedAt === '운영 중' ? 'text-gray-300' : 'text-gray-900'">
                {{ detailTarget?.closedAt }}
              </div>
            </div>
          </div>

          <div class="pt-2">
            <button @click="downloadPdf"
                    class="w-full flex items-center justify-center gap-2 py-3 rounded-lg bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] transition-colors shadow-sm cursor-pointer">
              <FileText class="w-4 h-4 text-white" />
              사업자 PDF 다운로드
            </button>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end">
          <button @click="showDetailModal = false"
                  class="px-4 py-2 text-sm font-semibold text-gray-600 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer">닫기</button>
        </div>
      </div>
    </div>

    <!-- 신규 가맹점 등록 / 수정 모달 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40" @click="showModal = false"></div>
      <div class="relative bg-white rounded-lg w-full max-w-lg border border-gray-200 shadow-xl overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200">
        <div class="px-8 py-5 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h3 class="font-bold text-gray-900 text-lg">{{ editTarget ? '입점 가맹점 정보 수정' : '신규 가맹점 등록' }}</h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600 font-bold text-xl cursor-pointer">✕</button>
        </div>

        <form @submit.prevent="saveStore" class="p-8 space-y-5">
          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">가맹점명</label>
            <input v-model="form.name" required type="text" placeholder="예: 한우 오마카세"
                   class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
          </div>

          <div class="grid grid-cols-2 gap-5">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">담당자명</label>
              <input v-model="form.owner" required type="text" placeholder="성함 입력"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">이메일</label>
              <input v-model="form.email" type="email" placeholder="example@email.com"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">위치</label>
            <input v-model="form.details" required type="text" placeholder="예: 갤러리아 백화점 B1F 101호"
                   class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
          </div>

          <!-- 수정 모달에서만 표시 -->
          <div v-if="editTarget" class="grid grid-cols-2 gap-5">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">개업일</label>
              <input v-model="form.openDate" type="date"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">폐업일</label>
              <input v-model="form.closeDate" type="date"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자번호</label>
            <input v-model="form.bizNumber" type="text" placeholder="000-00-00000"
                   class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자 PDF</label>
            <label class="cursor-pointer block">
              <div class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm text-gray-400 bg-gray-50 hover:bg-gray-100 transition-colors flex items-center justify-between">
                <span>{{ form.bizPdfName || '파일을 선택해주세요' }}</span>
                <FileText class="w-4 h-4" />
              </div>
              <input type="file" class="hidden" @change="handleFileChange" accept=".pdf" />
            </label>
          </div>

          <div class="flex gap-3 pt-4">
            <button type="button" @click="showModal = false"
                    class="flex-1 py-3 rounded-lg border border-gray-200 text-sm font-bold text-gray-500 hover:bg-gray-50 transition-colors cursor-pointer">취소</button>
            <button type="submit"
                    class="flex-1 py-3 rounded-lg bg-[#F37321] text-white text-sm font-bold hover:bg-[#e0661d] transition-colors shadow-sm cursor-pointer">저장</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

