<p align="center">
  <img src="../docs/nexus%20%EB%8C%80%ED%91%9C%20%EC%9D%B4%EB%AF%B8%EC%A7%80.png" alt="Nexus — 더벤티 프랜차이즈 통합 관리 시스템" width="100%"/>
</p>

<h3 align="center">🎨 Frontend — Vue 3 + Vite 기반 통합 웹 클라이언트</h3>

### ☀️ **[플레이 데이터] 한화시스템 BEYOND SW캠프** ☀️

<br>

## 🤼‍♂️ 팀원 소개

<br>

| 권민석 | 노승찬 | 이재혁 | 이지희 | 정동현 |
| :---: | :---: | :---: | :---: | :---: |
| <img src="https://github.com/RIMIN0650.png" width="96" alt="권민석"/> | <img src="https://github.com/seungchan-0629.png" width="96" alt="노승찬"/> | <img src="https://github.com/hijaehyuk.png" width="96" alt="이재혁"/> | <img src="https://github.com/dwg0245.png" width="96" alt="이지희"/> | <img src="https://github.com/DongHyunj.png" width="96" alt="정동현"/> |
| [@RIMIN0650](https://github.com/RIMIN0650) | [@seungchan-0629](https://github.com/seungchan-0629) | [@hijaehyuk](https://github.com/hijaehyuk) | [@dwg0245](https://github.com/dwg0245) | [@DongHyunj](https://github.com/DongHyunj) |
| 로그인 / 회원<br/>ESG 대시보드<br/>결제 BATCH | 제품 / 카테고리<br/>배송 / 정산<br/>결제수단 | 재고 / 입출고<br/>주문 / 마감<br/>뉴스 요약<br/>승인 처리 BATCH | 가맹점 / 메뉴<br/>AI 챗봇<br/>POS MSA | 발주 / 알림<br/>대시보드<br/>통계 MSA<br/>인프라 일부 |

<br>

## 📌 기술 스택

<div align="center">
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"/>
<img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"/>
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=black"/>
<img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=Vue.js&logoColor=white"/>
<img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white"/>
<img src="https://img.shields.io/badge/Vue_Router-4FC08D?style=for-the-badge&logo=Vue.js&logoColor=white"/>
<img src="https://img.shields.io/badge/Pinia-FFD43B?style=for-the-badge&logo=Pinia&logoColor=black"/>
<img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white"/>
<img src="https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white"/>
<img src="https://img.shields.io/badge/Chart.js-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
<img src="https://img.shields.io/badge/PortOne-3182F6?style=for-the-badge&logo=&logoColor=white"/>
<img src="https://img.shields.io/badge/ESLint-4B32C3?style=for-the-badge&logo=eslint&logoColor=white"/>
<img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"/>
</div>

<br>

<details>
<summary><b>상세 기술 스택</b></summary>

- Vue 3.5
- Vite 8.0
- Vue Router 4.x
- Pinia 3.0
- Axios 1.15 (JWT interceptor)
- Tailwind CSS 4.2
- Chart.js 4.5
- Lucide Vue Next (아이콘)
- PortOne Browser SDK 0.1 (결제 게이트)
- ESLint + Oxlint
- Nginx 1.25 (운영 배포)
</details>

---

## 📁 &nbsp; 디렉토리 구조

<details>
<summary><b>전체 구조 보기</b></summary>

```
frontend/
├── public/                       # 정적 파일
├── nginx/                        # 운영 배포용 nginx 설정
├── src/
│   ├── api/                      # 도메인별 API 모듈 (axios) - 19개
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
</details>

---

## 🪟 &nbsp; 페이지

### 👉 본사 (`views/hq/`)

<details>
<summary><b>본사 페이지 16개</b></summary>

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
</details>

### 👉 가맹점 (`views/store/`)

<details>
<summary><b>가맹점 페이지 11개</b></summary>

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
</details>

---

## 기능 테스트 &nbsp; 
상세 기능 (펼쳐서 확인해주세요)

<details>
<summary><b>로그인 / 회원가입</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/login.gif"/></p>
</details>

<details>
<summary><b>POS 결제</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/pos-payment.gif"/></p>
</details>

<details>
<summary><b>영업 마감 → AI 자동 발주서 생성</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/pos-close.gif"/></p>
</details>

<details>
<summary><b>본사 대시보드</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/hq-dashboard.gif"/></p>
</details>

<details>
<summary><b>발주 관리 (본사 일괄 승인)</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/hq-order-approve.gif"/></p>
</details>

<details>
<summary><b>가맹점 발주 (수동 + 제안 발주서 확정)</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/store-order.gif"/></p>
</details>

<details>
<summary><b>실시간 알림 (SSE)</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/notification.gif"/></p>
</details>

<details>
<summary><b>장기 통계 차트</b></summary>
<br>
<p align="center"><img width="80%" src="../docs/img/gif/long-term-statistics.gif"/></p>
</details>

> GIF 파일은 `docs/img/gif/` 에 추가하면 자동 표시.

---

## 🚀 &nbsp; 실행

<details>
<summary><b>1. 사전 요구사항</b></summary>

- Node.js 20.19+ 또는 22.12+
- 백엔드 (Gateway / 모놀리식 / POS MSA / 통계 MSA) 가 떠 있어야 정상 동작
</details>

<details>
<summary><b>2. 설치 + 개발 서버</b></summary>

```bash
npm install
npm run dev
```
- http://localhost:5173 접속
</details>

<details>
<summary><b>3. 빌드</b></summary>

```bash
npm run build
```
- 결과 디렉토리: `dist/`
</details>

<details>
<summary><b>4. Lint</b></summary>

```bash
npm run lint
```
- oxlint + eslint 자동 fix
</details>

---

## 🛣️ &nbsp; API 라우팅 (Vite Proxy)

<details>
<summary><b>로컬 dev proxy 규칙</b></summary>

`vite.config.js` 의 proxy 규칙 — 로컬 dev 환경에서 백엔드 호출 분기:

| 프론트 경로 | 대상 | 비고 |
|---|---|---|
| `/api/pos/**` | `http://localhost:8080` | 로컬에서 Gateway 없을 때 모놀리식 직접 (Gateway 띄울 시 `:8088`) |
| `/api/statistics/**` | `http://localhost:8081` | 통계 MSA 직접 (또는 Gateway) |
| `/api/**` | `http://localhost:8080` | 모놀리식 |

운영 환경에서는 nginx 가 라우팅 (아래 운영 배포 참조).
</details>

---

## 📡 &nbsp; API 호출 컨벤션

<details>
<summary><b>Axios Interceptor (plugins/axiosinterceptor.js)</b></summary>

- 모든 요청에 JWT 자동 첨부 (`Authorization: Bearer <token>`)
- 401 응답 시 → 로그아웃 + 로그인 페이지 리다이렉트
- BaseResponse 구조 (`{ success, code, message, result }`) 처리
</details>

<details>
<summary><b>API 모듈 패턴 (api/&lt;domain&gt;/index.js)</b></summary>

```javascript
import api from '@/plugins/axiosinterceptor'

export const getXxxList = (params) => api.get('/xxx/list', { params })
export const postXxx = (body) => api.post('/xxx', body)
export const putXxx = (idx, body) => api.put(`/xxx/${idx}`, body)
export const deleteXxx = (idx) => api.delete(`/xxx/${idx}`)
```
</details>

---

## 💾 &nbsp; 상태 관리 (Pinia)

<details>
<summary><b>stores/auth.js</b></summary>

- 로그인 사용자 정보 (idx, email, role: `ADMIN` / `STORE`)
- JWT 토큰 관리 (localStorage 영속)
- 로그인 / 로그아웃 액션
</details>

<details>
<summary><b>stores/notification.js</b></summary>

- SSE 알림 구독 (본사 / 가맹점)
- 미읽음 카운트
- 알림 리스트
</details>

---

## 🎨 &nbsp; Vue SFC 컨벤션

- 순서: `<script setup>` → `<template>` → `<style>`
- 자세한 규칙: [.claude/frontend/VUE_STRUCTURE.md](../.claude/frontend/VUE_STRUCTURE.md)

---

## 🚢 &nbsp; 운영 배포

<details>
<summary><b>Nginx (TLS termination + reverse proxy)</b></summary>

- `nginx/default.conf` — HTTP → HTTPS redirect + API 분기
- 분기 규칙:
  - `/api/auth` → monolith
  - `/api/(stat|pos)/**` → gateway
  - `/api/**` → monolith
- SSE buffering off (실시간 알림용)
</details>

<details>
<summary><b>Dockerfile</b></summary>

- `docker/frontend/Dockerfile`
- Multi-stage build (Node 빌드 → Nginx 서빙)
</details>

<details>
<summary><b>Kubernetes</b></summary>

- `k8s/frontend/` — Deployment (Blue/Green) + LoadBalancer Service + nexus-tls Secret
- 443 포트 외부 노출 (HTTPS 진입점)
</details>

---

## 🔑 로그인 (dev seed)
- 본사: `admin@theventi.co.kr` / `password123`
- 가맹점: `store0001@theventi.co.kr` / `password123`

---

![footer](https://capsule-render.vercel.app/api?type=Venom&color=4F46E5&height=120&section=footer&fontColor=ffffff)
