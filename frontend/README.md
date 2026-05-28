# Nexus Frontend

> Vue 3 + Vite 기반 본사 운영 + 가맹점 POS 통합 웹 클라이언트

---

## 기술 스택

- Vue 3.5
- Vite 8.0
- Vue Router
- Pinia 3 (상태 관리)
- Axios 1.15 (HTTP, JWT interceptor)
- Tailwind CSS 4.2
- Chart.js 4.5 (차트)
- Lucide Vue Next (아이콘)
- PortOne Browser SDK (결제)
- ESLint + Oxlint

---

## 디렉토리 구조

```
frontend/
├── public/                       # 정적 파일
├── nginx/                        # 운영 배포용 nginx 설정
├── src/
│   ├── api/                      # 도메인별 API 모듈 (axios)
│   │   ├── category/  dashboard/  delivery/  headincome/
│   │   ├── headinventory/  inventory/  menu/  news/
│   │   ├── notification/  orders/  pos/  product/
│   │   ├── report/  settlement/  statistics/  store/
│   │   ├── store-dashboard/  user/  wastelog/
│   ├── assets/                   # 이미지 / 글로벌 스타일
│   ├── components/               # 재사용 컴포넌트
│   ├── composables/              # 재사용 로직 (use*)
│   ├── constants/                # 상수
│   ├── plugins/
│   │   └── axiosinterceptor.js   # JWT 자동 첨부 + 401 처리
│   ├── router/
│   │   └── index.js              # Vue Router 라우팅
│   ├── stores/                   # Pinia
│   │   ├── auth.js               # 로그인 / 사용자 상태
│   │   └── notification.js       # 알림 SSE 구독
│   ├── utils/                    # 유틸 함수
│   ├── views/                    # 페이지 컴포넌트
│   │   ├── LoginView.vue
│   │   ├── ProfileView.vue
│   │   ├── hq/                   # 본사 페이지 (16개)
│   │   └── store/                # 가맹점 페이지 (11개)
│   ├── App.vue                   # 루트 컴포넌트
│   └── main.js                   # 앱 진입점
├── vite.config.js
├── tailwind.config.js
├── eslint.config.js
└── package.json
```

---

## 페이지 (Views)

### 본사 (`views/hq/`)

| 파일 | 페이지 |
|---|---|
| DashboardView | 본사 대시보드 (KPI + 차트) |
| HqOrderManageView | 발주 관리 (자동 / 확정 / 이력 / 이상) |
| DeliveryView | 배송 관리 |
| InventoryHeadOfficeView | 본사 재고 |
| InventoryHistoryView | 재고 이력 |
| InventoryView | 재고 관리 |
| StoreView | 가맹점 관리 |
| ProductView | 상품 (원자재) |
| RecipeView | 메뉴 레시피 |
| StatisticsView | 실시간 통계 |
| LongTermStatisticsView | 장기 통계 (월 / 분기 / 연도) |
| SettlementView | 정산 |
| ReportView | 리포트 |
| EsgDashboardView | ESG 대시보드 |
| HqNotificationView | 알림 |
| AdminAccountCreateView | 본사 계정 생성 |

### 가맹점 (`views/store/`)

| 파일 | 페이지 |
|---|---|
| PosView | POS 결제 화면 |
| PaymentView | 결제 |
| StoreDashboardView | 가맹점 대시보드 |
| StoreInventoryView | 매장 재고 |
| StoreOrderManageView | 발주 관리 (수동 / AI 추천 확인) |
| StoreDeliveryView | 배송 현황 |
| StoreProductView | 상품 |
| StoreRecipeView | 레시피 |
| StoreSettlementView | 정산 내역 |
| StoreNewsView | 소식 |
| StoreNotificationView | 알림 |

---

## 실행

### 사전 요구사항
- Node.js 20.19+ 또는 22.12+
- 백엔드 (Gateway / 모놀리식 / POS MSA / 통계 MSA) 가 떠 있어야 정상 동작

### 설치 + 개발 서버
```bash
npm install
npm run dev
```
- http://localhost:5173 접속

### 빌드
```bash
npm run build
```
- 결과 디렉토리: `dist/`

### Lint
```bash
npm run lint
```
- oxlint + eslint 자동 fix

---

## API 라우팅 (Vite Proxy)

`vite.config.js` 의 proxy 규칙 — 로컬 dev 환경에서 백엔드 호출 분기:

| 프론트 경로 | 대상 | 비고 |
|---|---|---|
| `/api/pos/**` | `http://localhost:8080` | 로컬에서 Gateway 없을 때 모놀리식 직접 (Gateway 띄울 시 `:8088`) |
| `/api/statistics/**` | `http://localhost:8081` | 통계 MSA 직접 (또는 Gateway) |
| `/api/**` | `http://localhost:8080` | 모놀리식 |

운영 환경에서는 nginx 가 라우팅 (아래 운영 배포 참조).

---

## API 호출 컨벤션

### Axios Interceptor (`plugins/axiosinterceptor.js`)
- 모든 요청에 JWT 자동 첨부 (`Authorization: Bearer <token>`)
- 401 응답 시 → 로그아웃 + 로그인 페이지 리다이렉트
- BaseResponse 구조 (`{ success, code, message, result }`) 처리

### API 모듈 패턴 (`api/<domain>/index.js`)
```javascript
import api from '@/plugins/axiosinterceptor'

export const getXxxList = (params) => api.get('/xxx/list', { params })
export const postXxx = (body) => api.post('/xxx', body)
export const putXxx = (idx, body) => api.put(`/xxx/${idx}`, body)
export const deleteXxx = (idx) => api.delete(`/xxx/${idx}`)
```

---

## 상태 관리 (Pinia)

### `stores/auth.js`
- 로그인 사용자 정보 (idx, email, role: ADMIN / STORE)
- JWT 토큰 관리 (localStorage 영속)
- 로그인 / 로그아웃 액션

### `stores/notification.js`
- SSE 알림 구독 (본사 / 가맹점)
- 미읽음 카운트
- 알림 리스트

---

## Vue SFC 컨벤션
- 순서: `<script setup>` → `<template>` → `<style>`
- 자세한 규칙: [.claude/frontend/VUE_STRUCTURE.md](../.claude/frontend/VUE_STRUCTURE.md)

---

## 운영 배포

### nginx (TLS termination + reverse proxy)
- `nginx/default.conf` — HTTP → HTTPS redirect + API 분기
- 분기 규칙:
  - `/api/auth` → monolith
  - `/api/(stat|pos)/**` → gateway
  - `/api/**` → monolith
- SSE buffering off (실시간 알림용)

### Dockerfile
- `docker/frontend/Dockerfile`
- Multi-stage build (Node 빌드 → Nginx 서빙)

### Kubernetes
- `k8s/frontend/` — Deployment (Blue/Green) + LoadBalancer Service + nexus-tls Secret
- 443 포트 외부 노출 (HTTPS 진입점)

---

## 로그인 (dev seed)
- 본사: `admin@theventi.co.kr` / `password123`
- 가맹점: `store0001@theventi.co.kr` ~ `store0100@theventi.co.kr` / `password123`
