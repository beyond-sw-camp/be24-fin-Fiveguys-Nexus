<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { Plus, Search, FileText, ChevronDown } from 'lucide-vue-next'
import { getStoreList , getStoreDetailList, getPresignedUrl, postNewRegister} from '@/api/store/index.js'
import axios from 'axios'


const searchQuery = ref('')
const filterStatus = ref('전체')
const activeDropdown = ref(null)

const statusOptions = ['전체', '입점', '폐점']

// --- 가맹점 목록 리스트 ---
const storesList = reactive([])
const pagination = reactive({
  totalPage: 0,
  totalCount: 0,
  currentPage: 0,
  currentSize: 10
})

const storeListRes = async (page = 0)=>{
  const searchReq = {
    keyword: searchQuery.value, // ref로 선언된 검색어
    status: filterStatus.value === '입점' ? 'ACTIVE' : filterStatus.value === '폐점' ? 'CLOSED' : null
  }

  const res = await getStoreList(searchReq, page, pagination.currentSize)
  console.log(res.data.result)
  storesList.splice(0, storesList.length, ...res.data.result.storeList)


  pagination.totalPage = res.data.result.totalPage
  pagination.totalCount = res.data.result.totalCount
  pagination.currentPage = res.data.result.currentPage
}

const changePage = (page) => {
  // 0보다 작거나 마지막 페이지(totalPage - 1)보다 크면 무시[cite: 1]
  if (page < 0 || page >= pagination.totalPage) return
  storeListRes(page)
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

const formatBusinessNumber = (e) => {
  // 1. 숫자 이외의 문자(한글, 영문 등)를 모두 제거
  let val = e.target.value.replace(/[^0-9]/g, '');

  // 2. 최대 10자리까지만 남기기
  if (val.length > 10) val = val.substring(0, 10);

  // 3. 포맷팅 로직 (000-00-00000)
  let formatted = '';
  if (val.length <= 3) {
    formatted = val;
  } else if (val.length <= 5) {
    formatted = `${val.slice(0, 3)}-${val.slice(3)}`;
  } else {
    formatted = `${val.slice(0, 3)}-${val.slice(3, 5)}-${val.slice(5, 10)}`;
  }

  // [중요] input 태그의 실제 값과 Vue 상태를 강제로 일치시킴
  e.target.value = formatted;
  form.business = formatted;
};

const showModal = ref(false)
const showDetailModal = ref(false)
const editTarget = ref(null)
const detailTarget = ref(null)

// 초기화 하는 함수
const getInitialForm = () => ({
  storeName: '',
  ownerName: '',
  ownerEmail: '',
  postcode: '',
  address: '',
  addressDetail: '',
  totalAddress:'',
  business: '',
  createdAt: '',
  closedAt: '',
  filePath: '',
  fileName:''
});

const form = reactive(getInitialForm())


// 상태 토글
function toggleDropdown(type) {
  activeDropdown.value = activeDropdown.value === type ? null : type
}

// 토글 필터링 조건
function selectFilter(type, value) {
  if (type === 'status') filterStatus.value = value
  activeDropdown.value = null
}

// 가맹점 목록 클릭시 상세 모달창
async function openDetail(idx) {
  const res = await getStoreDetailList(idx)

  detailTarget.value = res.data.result
  showDetailModal.value = true
}

// 수정 등록 팝업 창 열기
async function openModal(idx =! null) {

  if (idx) {
    // [수정 모드]
    const res = await getStoreDetailList(idx);
    const detailData = res.data.result;
    Object.assign(form, getInitialForm());
    // v-model로 연결된 form에 DB에서 가져온 값을 세팅 (화면에 바로 보임)
    Object.assign(form, detailData);

    editTarget.value = detailData; // 나중에 수정 API 보낼 때 쓸 idx가 담긴 원본
  } else {
    // [등록 모드] idx가 없으면 입력 칸을 싹 비움
    Object.assign(form, getInitialForm());
    editTarget.value = null;
  }
  showModal.value = true;
}

const selectedFile = ref(null);
// 파일 선택 감지
async function handleFileChange(e) {
  const file = e.target.files[0]
  if (file) {
    selectedFile.value = file; // 선택한 파일 보관
    form.fileName = file.name; // 화면 표시용
  }
}


// [추가] S3 업로드 로직만 담당하는 공통 함수
async function uploadFileToS3() {
  // 선택된 파일이 없으면 업로드 과정을 건너뛰고 null 반환
  if (!selectedFile.value) return null;

  try {
    // 1. 백엔드에 Presigned URL 요청
    const presigned = await getPresignedUrl(selectedFile.value.name);
    const { url, fileName: s3Path } = presigned.data.result;

    // 2. S3에 실제 파일 업로드 (PUT 방식)
    await axios.put(url, selectedFile.value, {
      headers: { 'Content-Type': 'application/pdf' }
    });

    return s3Path; // 업로드된 S3 파일 경로(DB 저장용) 반환
  } catch (error) {
    console.error("S3 업로드 실패:", error);
    throw new Error("파일 업로드 중 오류가 발생했습니다.");
  }
}

async function saveStore() {
  try {
    // [핵심 추가] 파일을 선택했다면 등록/수정 전에 S3에 먼저 올립니다.
    const newFilePath = await uploadFileToS3();
    if (newFilePath) {
      form.filePath = newFilePath; // 업로드된 경로를 form에 세팅
    }

    // --- 수정 로직 ---
    if (editTarget.value) {
      // form.value가 아니라 reactive 객체인 form 자체를 비교해야 합니다.
      const isNoChange = JSON.stringify(editTarget.value) === JSON.stringify(form);

      if (isNoChange) {
        alert("수정된 내용이 없습니다.");
        showModal.value = false;
        return;
      }

      // TODO: 수정 API가 준비되면 여기에 호출 로직 추가
      alert("수정 완료 (API 연동 필요)");
    }
    // --- 등록 로직 ---
    else {
      // form 내용을 복사하여 전송용 DTO 생성
      const storeRegDto = { ...form };

      const res = await postNewRegister(storeRegDto);

      if (res.data.code === 2000) {
        alert("가맹점이 등록되었습니다.");
        storesList.length = 0;
        await storeListRes();
      }
    }

    showModal.value = false; // 성공 시에만 모달 닫기
    selectedFile.value = null; // 파일 변수 초기화

  } catch (error) {
    alert("처리 중 오류 발생: " + error.message);
  }
}

// S3 미리보기 (뷰어)
function viewPdf() {
  const filePath = detailTarget.value?.filePath;
  if (!filePath) return alert('파일이 없습니다.');

  const s3BaseUrl = 'https://nexus-store-archive.s3.ap-northeast-2.amazonaws.com/';
  window.open(s3BaseUrl + filePath, '_blank'); // 새 탭에서 열기
}

// S3 다운로드
async function downloadPdf() {
  console.log(detailTarget.value)
  const filePath = detailTarget.value?.filePath;
  if (!filePath) return alert('파일이 없습니다.');

  const s3BaseUrl = 'https://nexus-store-archive.s3.ap-northeast-2.amazonaws.com/';
  const fileUrl = s3BaseUrl + filePath;

  try {
    // 1. fetch로 파일을 가져옵니다.
    const response = await fetch(fileUrl);
    if (!response.ok) throw new Error('파일을 가져오는데 실패했습니다.');

    // 2. 파일 데이터를 Blob(Binary Large Object)으로 변환합니다.
    const blob = await response.blob();

    // 3. Blob 데이터를 위한 임시 URL을 생성합니다.
    const url = window.URL.createObjectURL(blob);

    // 4. 가상의 링크를 생성하여 클릭을 유도합니다.
    const link = document.createElement('a');
    link.href = url;
    link.download = `${detailTarget.value.storeName}_사업자등록증.pdf`; // 저장될 파일명
    document.body.appendChild(link);
    link.click();

    // 5. 사용이 끝난 임시 URL과 링크를 제거합니다.
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

  } catch (error) {
    console.error('다운로드 중 오류 발생:', error);
    // 만약 CORS 에러가 난다면, 가장 단순한 방법인 window.open을 백업으로 사용합니다.
    alert('브라우저 보안 정책으로 인해 직접 다운로드가 제한되었습니다. 새 탭에서 파일을 엽니다.');
    window.open(fileUrl, '_blank');
  }
}

// 카카오 주소 API
const sample6_execDaumPostcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      let addr = ''; // 주소 변수

      // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
      if (data.userSelectedType === 'R') { // 도로명 주소
        addr = data.roadAddress;
      } else { // 지번 주소
        addr = data.jibunAddress;
      }

      // [핵심] Vue의 reactive 객체에 직접 값을 할당합니다.
      form.address = addr;

      // 만약 우편번호도 저장하고 싶다면 form에 추가 후 할당하세요.
      form.postcode = data.zonecode;

      // 상세주소 입력창으로 포커스 이동
      nextTick(() => {
        const detailInput = document.getElementById("sample6_detailAddress");
        if (detailInput) detailInput.focus();
      });
    }
  }).open();
}

const visiblePages = computed(() => {
  const range = 10; // 한 번에 보여줄 페이지 개수
  const currentGroup = Math.floor(pagination.currentPage / range);

  const start = currentGroup * range;
  const end = Math.min(start + range, pagination.totalPage);

  const pages = [];
  for (let i = start; i < end; i++) {
    pages.push(i);
  }
  return pages;
});

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
          <td class="px-5 py-3.5 text-gray-500 text-xs truncate max-w-[250px]" :title="store.address">
            {{ store.address }}
          </td>
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
                @click.stop="openModal(store.idx)"
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
              {{ detailTarget?.totalAddress }}
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자번호</label>
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

          <div class="space-y-3 pt-2">
            <div v-if="detailTarget.closedAt && detailTarget.closedAt !== '운영 중'"
                 class="flex flex-col items-center p-3 bg-red-50 rounded-lg border border-red-100">
              <span class="text-xs text-red-600 font-bold">* 폐업 처리된 가맹점입니다. 증빙 용도로만 사용하세요.</span>
              <span class="text-[11px] text-red-400 mt-0.5">폐업일: {{ detailTarget.closedAt }}</span>
            </div>


            <button
              @click="viewPdf"
              type="button"
              class="w-full flex items-center justify-center gap-2 py-3 rounded-lg bg-white border border-gray-200 text-gray-700 text-sm font-bold hover:bg-gray-50 transition-colors shadow-sm cursor-pointer">
              <Search class="w-4 h-4 text-gray-400" />
              사업자 PDF 미리보기
            </button>

            <button @click="downloadPdf"
                    type="button"
                    :class="[
            'w-full flex items-center justify-center gap-2 py-3 rounded-lg text-sm font-bold transition-colors shadow-sm cursor-pointer',
            detailTarget.closedAt && detailTarget.closedAt !== '운영 중'
              ? 'bg-white border border-gray-200 text-gray-700 text-sm font-bold hover:bg-gray-50' // 폐업 시: 미리보기와 유사한 연회색 톤
              : 'bg-[#F37321] text-white hover:bg-[#e0661d]' // 운영 중: 기존 주황색
          ]">
              <FileText :class="['w-4 h-4', detailTarget.closedAt && detailTarget.closedAt !== '운영 중' ? 'text-gray-400' : 'text-white']" />
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
            <input v-model="form.storeName" required type="text" placeholder="예: 한우 오마카세"
                   class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
          </div>

          <div class="grid gap-5" :class="editTarget ? 'grid-cols-2' : 'grid-cols-1'">

            <div v-if="editTarget" class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">담당자명</label>
              <input v-model="form.ownerName" required type="text" placeholder="성함 입력"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>

            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">이메일</label>
              <input v-model="form.ownerEmail" type="email" placeholder="example@email.com"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
          </div>

          <div class="space-y-3">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">위치 (주소)</label>

            <div class="flex gap-2">
              <input
                v-model="form.postcode"
                type="text"
                id="sample6_postcode"
                placeholder="우편번호"
                readonly
                class="w-32 px-4 py-2 rounded-lg border border-gray-200 text-sm outline-none bg-gray-50"
              />
              <button
                type="button"
                @click="sample6_execDaumPostcode"
                class="px-4 py-2 bg-gray-800 text-white text-sm rounded-lg hover:bg-gray-700 transition-colors cursor-pointer"
              >
                주소 검색
              </button>
            </div>

            <input
              v-model="form.address"
              type="text"
              placeholder="도로명/지번 주소"
              readonly
              class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all"
            />

            <input
              v-model="form.addressDetail"
              type="text"
              id="sample6_detailAddress"
              placeholder="상세 주소를 입력해주세요"
              class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all"
            />
          </div>
          <!-- 수정 모달에서만 표시 -->
          <div v-if="editTarget" class="grid grid-cols-2 gap-5">
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">개업일</label>
              <input v-model="form.createdAt" type="date"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
            <div class="space-y-1.5">
              <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">폐업일</label>
              <input v-model="form.closedAt" type="date"
                     class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all" />
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자번호</label>
            <input
              :value="form.business"
              @input="formatBusinessNumber"
              type="text"
              placeholder="숫자만 입력하세요"
              maxlength="12"
              class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm focus:border-[#F37321] focus:ring-4 focus:ring-[#F37321]/5 outline-none transition-all"
            />
          </div>

          <div class="space-y-1.5">
            <label class="text-[11px] font-bold text-gray-400 uppercase tracking-widest">사업자 PDF</label>
            <label class="cursor-pointer block">
              <div class="w-full px-4 py-2 rounded-lg border border-gray-200 text-sm text-gray-400 bg-gray-50 hover:bg-gray-100 transition-colors flex items-center justify-between">
                <span>{{ form.fileName || '파일을 선택해주세요' }}</span>
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
    <!-- Pagination UI -->
    <div class="flex items-center justify-center gap-3 mt-8 pb-10">
      <!-- 이전 페이지 버튼 (작게) -->
      <button
        @click="changePage(pagination.currentPage - 1)"
        :disabled="pagination.currentPage === 0"
        class="w-7 h-7 flex items-center justify-center rounded border border-gray-200 bg-white disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors cursor-pointer"
      >
        <span class="text-[10px] text-gray-500">◀</span>
      </button>

      <!-- 페이지 번호 -->
      <div class="flex items-center gap-1">
        <button
          v-for="pageIdx in visiblePages"
          :key="pageIdx"
          @click="changePage(pageIdx)"
          class="min-w-[28px] h-7 px-2 flex items-center justify-center rounded text-xs font-bold transition-all cursor-pointer"
          :class="pagination.currentPage === pageIdx
        ? 'text-[#F37321] border-b-2 border-[#F37321] rounded-none'
        : 'text-gray-400 hover:text-gray-600'"
        >
          {{ pageIdx + 1 }}
        </button>
      </div>

      <!-- 다음 페이지 버튼 (작게) -->
      <button
        @click="changePage(pagination.currentPage + 1)"
        :disabled="pagination.currentPage >= pagination.totalPage - 1"
        class="w-7 h-7 flex items-center justify-center rounded border border-gray-200 bg-white disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors cursor-pointer"
      >
        <span class="text-[10px] text-gray-500">▶</span>
      </button>
    </div>
  </div>
</template>

