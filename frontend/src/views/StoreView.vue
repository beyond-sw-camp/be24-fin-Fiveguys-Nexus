<template>
  <div class="p-5 space-y-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-xl font-bold text-gray-900 tracking-tight">가맹점 관리</h1>

      </div>
      <button @click="openModal(null)"
              class="bg-[#F37321] text-white px-4 py-2 text-sm font-semibold rounded-lg hover:bg-[#e0661d] transition-colors flex items-center gap-2 shadow-sm cursor-pointer">
        <Plus class="w-4 h-4" /> 신규 가맹점 등록
      </button>
    </div>

    <!-- Summary -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">전체 가맹점</p>
        <p class="text-3xl font-black text-gray-900 mt-2">{{ stores.length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">정상</p>
        <p class="text-3xl font-black text-green-600 mt-2">{{ stores.filter(s => !s.closeDate).length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
      </div>
      <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">폐업 가맹점</p>
        <p class="text-3xl font-black text-red-500 mt-2">{{ stores.filter(s => s.closeDate).length }}<span class="text-sm font-normal text-gray-400 ml-1">개</span></p>
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
          <button @click="toggleDropdown('region')"
                  class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-lg text-sm font-bold text-gray-700 hover:border-gray-300 transition-all shadow-sm min-w-[120px] justify-between cursor-pointer">
            <span>지역: {{ filterRegion }}</span>
            <ChevronDown class="w-4 h-4 text-gray-400 transition-transform" :class="{ 'rotate-180': activeDropdown === 'region' }" />
          </button>
          <div v-if="activeDropdown === 'region'" class="absolute right-0 mt-1 w-40 bg-white border border-gray-200 rounded-lg shadow-lg z-20 py-1 animate-in fade-in zoom-in-95 duration-100">
            <button v-for="r in regionChips" :key="r" @click="selectFilter('region', r)"
                    class="w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors cursor-pointer"
                    :class="filterRegion === r ? 'text-[#F37321] font-bold' : 'text-gray-600'">
              {{ r }}
            </button>
          </div>
        </div>

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
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">지점명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">점주명</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">이메일</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">소재지</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider">사업자번호</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">운영상태</th>
          <th class="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider text-center">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
        <tr v-for="(store, index) in filteredStores" :key="store.id"
            @click="openDetail(store)"
            class="hover:bg-gray-50/50 transition-colors cursor-pointer group"
            :class="{ 'bg-gray-50/40 opacity-70': store.closeDate }">
          <td class="px-5 py-3.5 font-bold text-gray-900 group-hover:text-[#F37321] transition-colors">
            <div class="flex items-center gap-2">
              {{ store.name }}
              <span v-if="store.closeDate" class="text-[9px] font-normal text-red-400 border border-red-200 px-1 rounded-lg italic">CLOSED</span>
            </div>
          </td>
          <td class="px-5 py-3.5 text-gray-600">{{ store.owner }}</td>
          <td class="px-5 py-3.5 text-gray-500 text-xs truncate max-w-[150px]">{{ store.email }}</td>
          <td class="px-5 py-3.5 text-gray-500 text-xs">{{ store.region }}</td>
          <td class="px-5 py-3.5 font-mono text-xs text-gray-400">{{ store.bizNumber }}</td>
          <td class="px-5 py-3.5 text-center">
              <span class="text-[11px] font-bold px-2 py-0.5 rounded-lg"
                    :class="!store.closeDate
                  ? 'bg-green-100 text-green-700'
                  : 'bg-red-50 text-red-600'">
                {{ store.closeDate ? '폐업' : '정상' }}
              </span>
          </td>
          <td class="px-5 py-3.5">
            <div class="flex justify-center">
              <button @click.stop="openModal(store)"
                      class="px-3 py-1.5 text-xs font-semibold text-[#F37321] border border-[#F37321] rounded hover:bg-orange-50 transition-colors cursor-pointer">
                수정
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 가맹점 상세 페이지 모달 -->
    <div v-if="showDetailModal" class="fixed inset-0 z-[60] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 " @click="showDetailModal = false"></div>
      <div class="relative bg-white rounded-xl w-full max-w-lg border border-gray-200 shadow-xl overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="font-bold text-gray-900">가맹점 상세 정보</h3>
          <button @click="showDetailModal = false" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <div class="p-6 space-y-4">
          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">지점명</label>
            <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm font-bold text-gray-900">
              {{ detailTarget?.name }}
            </div>
          </div>



          <div class="grid grid-cols-2 gap-5">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">점주명</label>
              <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm text-gray-700">
                {{ detailTarget?.owner }}
              </div>
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">이메일 주소</label>
              <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm text-gray-700">
                {{ detailTarget?.email }}
              </div>
            </div>
          </div>



          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">소재지</label>
            <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm text-gray-600">
              {{ detailTarget?.region }}
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자번호</label>
            <div class="w-full px-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm font-mono text-gray-500">
              {{ detailTarget?.bizNumber }}
            </div>
          </div>

          <!-- 개업일 및 폐업일 표시 섹션 -->
          <div class="grid grid-cols-2 gap-5 p-4 bg-gray-50 rounded-lg border border-gray-100">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest flex items-center gap-1">
                <div class="w-1 h-1 bg-green-400 rounded-full"></div> 개업일
              </label>
              <div class="text-sm font-bold text-gray-700">
                {{ detailTarget?.openDate || '-' }}
              </div>
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest flex items-center gap-1">
                <div class="w-1 h-1 bg-red-400 rounded-lg"></div> 폐업일
              </label>
              <div class="text-sm font-bold" :class="detailTarget?.closeDate ? 'text-red-500' : 'text-gray-300'">
                {{ detailTarget?.closeDate || '운영 중' }}
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
          <h3 class="font-bold text-gray-900 text-lg">{{ editTarget ? '가맹점 정보 수정' : '신규 가맹점 등록' }}</h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600 font-bold text-xl cursor-pointer">✕</button>
        </div>

        <form @submit.prevent="saveStore" class="p-8 space-y-5">
          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">지점명</label>
            <input v-model="form.name" required type="text" placeholder="예: 한화빌딩점"
                   class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
          </div>

          <div class="grid grid-cols-2 gap-5">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">점주명</label>
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
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest ml-1">소재지 (서울 자치구)</label>
            <div class="relative">
              <!-- v-model이 비었을 때(placeholder) text-gray-400 적용 -->
              <select
                v-model="form.region"
                :class="[
                    'w-full px-4 py-2.5 bg-white rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all appearance-none cursor-pointer',
                    !form.region ? 'text-gray-400' : 'text-gray-900'
                  ]"
              >
                <option value="" disabled selected>자치구를 선택해주세요</option>
                <option v-for="region in regionChips.slice(1)" :key="region" :value="region" class="text-gray-900">
                  {{ region }}
                </option>
              </select>
              <ChevronDown class="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
            </div>
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
            <div class="flex items-center gap-2">
              <label class="flex-1 cursor-pointer">
                <div class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm text-gray-400 bg-gray-50 hover:bg-gray-100 transition-colors flex items-center justify-between">
                  <span>{{ form.bizPdfName || '파일을 선택해주세요' }}</span>
                  <FileText class="w-4 h-4" />
                </div>
                <input type="file" class="hidden" @change="handleFileChange" accept=".pdf" />
              </label>
            </div>
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

<script setup>
import { ref, computed } from 'vue'
import { Plus, Search, FileText, ChevronDown } from 'lucide-vue-next'

const searchQuery = ref('')
const filterRegion = ref('전체')
const filterStatus = ref('전체')
const activeDropdown = ref(null)

const regionChips = [
  '전체', '종로구', '중구', '용산구', '성동구', '광진구',
  '동대문구', '중랑구', '성북구', '강북구', '도봉구',
  '노원구', '은평구', '서대문구', '마포구', '양천구',
  '강서구', '구로구', '금천구', '영등포구', '동작구',
  '관악구', '서초구', '강남구', '송파구', '강동구'
]
const statusOptions = ['전체', '정상', '폐업']

// 초기 데이터 구조 변경: status 필드 대신 closeDate 필드 활용
const stores = ref([
  { id: 'BBQ-S001', name: 'BBQ 서울강남역점', owner: '김동현', region: '강남구', bizNumber: '101-12-34567', email: 'dh.kim@hanwha.com', openDate: '2022-03-15', closeDate: '' },
  { id: 'BBQ-S002', name: 'BBQ 서울종로본점', owner: '이재혁', region: '종로구', bizNumber: '201-45-67890', email: 'jh.lee@hanwha.com', openDate: '2022-07-20', closeDate: '' },
  { id: 'BBQ-S003', name: 'BBQ 서울동작점', owner: '박민수', region: '동작구', bizNumber: '301-78-90123', email: 'ms.park@hanwha.com', openDate: '2023-01-10', closeDate: '' },
  { id: 'BBQ-S004', name: 'BBQ 서울관악점', owner: '정수진', region: '관악구', bizNumber: '401-23-45678', email: 'sj.jung@hanwha.com', openDate: '2023-05-30', closeDate: '2024-01-15' },
  { id: 'BBQ-S005', name: 'BBQ 서울마포구점', owner: '한소희', region: '마포구', bizNumber: '107-82-99887', email: 'yeouido@bbq.com', openDate: '2023-12-20', closeDate: '2025-05-04' },
])

const filteredStores = computed(() => {
  let list = [...stores.value]

  if (filterRegion.value !== '전체') {
    list = list.filter((s) => s.region.includes(filterRegion.value))
  }

  if (filterStatus.value !== '전체') {
    // 폐업일 유무로 필터링
    if (filterStatus.value === '폐업') list = list.filter((s) => !!s.closeDate)
    if (filterStatus.value === '정상') list = list.filter((s) => !s.closeDate)
  }

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    list = list.filter(
      (s) => s.name.toLowerCase().includes(q) ||
        s.region.toLowerCase().includes(q) ||
        s.owner.toLowerCase().includes(q)
    )
  }

  // 폐업 가맹점(closeDate가 있는 항목)을 아래로 정렬
  return list.sort((a, b) => {
    if (!!a.closeDate && !b.closeDate) return 1
    if (!a.closeDate && !!b.closeDate) return -1
    return 0
  })
})

const showModal = ref(false)
const showDetailModal = ref(false)
const editTarget = ref(null)
const detailTarget = ref(null)
const form = ref({ name: '', owner: '', region: '', bizNumber: '', email: '', openDate: '', closeDate: '', bizPdfName: '' })

function toggleDropdown(type) {
  activeDropdown.value = activeDropdown.value === type ? null : type
}

function selectFilter(type, value) {
  if (type === 'region') filterRegion.value = value
  if (type === 'status') filterStatus.value = value
  activeDropdown.value = null
}

function handleSearch() {
  console.log('Searching:', searchQuery.value)
}

function openDetail(store) {
  detailTarget.value = store
  showDetailModal.value = true
}

function openModal(store) {
  editTarget.value = store
  form.value = store
    ? { ...store, bizPdfName: '' }
    : { name: '', owner: '', region: '', bizNumber: '', email: '', openDate: '', closeDate: '', bizPdfName: '' }
  showModal.value = true
}

function handleFileChange(e) {
  const file = e.target.files[0]
  if (file) form.value.bizPdfName = file.name
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


function downloadPdf() {
  alert('사업자 등록증 PDF를 다운로드합니다.')
}
</script>
