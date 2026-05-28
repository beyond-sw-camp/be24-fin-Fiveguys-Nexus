![header](https://capsule-render.vercel.app/api?type=Venom&color=4F46E5&height=300&section=header&text=Nexus&desc=%EB%8D%94%EB%B2%A4%ED%8B%B0%20%ED%94%84%EB%9E%9C%EC%B0%A8%EC%9D%B4%EC%A6%88%20%ED%86%B5%ED%95%A9%20%EA%B4%80%EB%A6%AC%20%EC%8B%9C%EC%8A%A4%ED%85%9C&descSize=20&descAlign=50&descAlignY=70&fontSize=100&animation=fadeIn&fontColor=ffffff)

---

<br>

## 🤼‍♂️ 팀원 소개

<br>

| 권민석 | 노승찬 | 이재혁 | 이지희 | 정동현 |
| :---: | :---: | :---: | :---: | :---: |
| <img src="https://github.com/RIMIN0650.png" width="96" alt="권민석"/> | <img src="https://github.com/seungchan-0629.png" width="96" alt="노승찬"/> | <img src="https://github.com/hijaehyuk.png" width="96" alt="이재혁"/> | <img src="https://github.com/dwg0245.png" width="96" alt="이지희"/> | <img src="https://github.com/DongHyunj.png" width="96" alt="정동현"/> |
| [@RIMIN0650](https://github.com/RIMIN0650) | [@seungchan-0629](https://github.com/seungchan-0629) | [@hijaehyuk](https://github.com/hijaehyuk) | [@dwg0245](https://github.com/dwg0245) | [@DongHyunj](https://github.com/DongHyunj) |

<br>

---

## ✨ 프로젝트 기본 소개

#### 프로젝트 배경
- 더벤티 본사가 100여 개 가맹점의 발주·재고·배송·정산·매출을 통합 관리할 수 있는 플랫폼이 필요하다.
- 가맹점주는 POS 결제 + 매장 재고 + AI 추천 자동 발주를 한 화면에서 운영할 수 있어야 한다.
- 본사가 실시간/장기 매출 통계로 운영 의사결정을 내릴 수 있어야 한다.

#### 프로젝트 목표
- **본사** : 가맹점 등록/관리, 발주 자동/확정/이상, 배송, 본사 재고, 매출/재고/배송 대시보드, 장기 통계, SSE 알림
- **가맹점** : POS 결제 + 영업 마감 → AI 자동 발주서 생성, 매장 재고, 발주 (수동/제안), 정산
- **통계** : 결제 발생 → Kafka → Redis 사전 집계 (실시간 O(1) 조회), 매일 새벽 MariaDB dump (장기 보존)
- **발주 일괄 승인** : Spring Batch 로 본사 확정 발주를 product 별 파티션 병렬 처리

<br>

---

<div align="center">

### 🌐 NEXUS 사이트 바로가기

<a href="https://www.nexussystem.kro.kr">
  <img src="https://img.shields.io/badge/🚀_Live_Demo_바로가기-4F46E5?style=for-the-badge&logoColor=white" height="50"/>
</a>

<br><br>

**🔐 테스트 계정 (dev seed)**

| 본사 (ADMIN) |                가맹점 (STORE)                 |
|:---:|:------------------------------------------:|
| `admin@theventi.co.kr` / `password123` | `store0001@theventi.co.kr` / `password123` |

</div>

---

## 📌 기술 스택

### Frontend
<div>
<img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=Vue.js&logoColor=white"/>
<img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white"/>
<img src="https://img.shields.io/badge/Pinia-FFD43B?style=for-the-badge&logo=Pinia&logoColor=black"/>
<img src="https://img.shields.io/badge/Vue_Router-4FC08D?style=for-the-badge&logo=Vue.js&logoColor=white"/>
<img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white"/>
<img src="https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white"/>
<img src="https://img.shields.io/badge/Chart.js-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
<img src="https://img.shields.io/badge/ESLint-4B32C3?style=for-the-badge&logo=eslint&logoColor=white"/>
</div>

### Backend
<div>
<img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Cloud_Gateway-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Kafka-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
<img src="https://img.shields.io/badge/Eureka-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
</div>

### Data
<div>
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"/>
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
<img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white"/>
</div>

### DevOps / Infra
<div>
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white"/>
<img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white"/>
<img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"/>
<img src="https://img.shields.io/badge/Istio-466BB0?style=for-the-badge&logo=istio&logoColor=white"/>
<img src="https://img.shields.io/badge/MetalLB-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white"/>
<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/>
</div>

---

## 🏗️ 시스템 아키텍처
<img src="docs/nexus%20시스템%20아키텍처.png"/>

<br>

---

## 📋 프로젝트 자료

| 자료 | 링크 |
|---|---|
| 🔶 화면 설계 | [<img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/>](https://www.figma.com/design/TcBBP86iuyJzGHxAsnc02O/NEXUS_%ED%99%94%EB%A9%B4-%EC%84%A4%EA%B3%84%EC%84%9C?node-id=0-1&t=VxcAFb2JxYOcoaNQ-1) |
| 🔶 요구사항 정의서 | [<img src="https://img.shields.io/badge/Google_Sheets-34A853?style=for-the-badge&logo=googlesheets&logoColor=white"/>](https://docs.google.com/spreadsheets/d/1Bxc2MNPWQzHHAP5Aa_0OKPYWM9hLNG4egndek0bf_ZE/edit?usp=sharing) |
| 🔶 WBS | [<img src="https://img.shields.io/badge/Google_Sheets-34A853?style=for-the-badge&logo=googlesheets&logoColor=white"/>](https://docs.google.com/spreadsheets/d/1v3hQE4EiuSdz8o5kB0BRFktiQvOigztILSRQ2ttobhw/edit?usp=sharing) |

<br>

### 📘 Swagger UI (모듈별 API 명세서)

| 모듈 | 바로가기 |
|:---:|:---:|
| **Monolith** | [<img src="https://img.shields.io/badge/Swagger_%3A8080-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>](http://localhost:8080/swagger-ui/index.html) |
| **POS MSA** | [<img src="https://img.shields.io/badge/Swagger_%3A8082-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>](http://localhost:8082/swagger-ui/index.html) |
| **Statistics MSA** | [<img src="https://img.shields.io/badge/Swagger_%3A8081-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>](http://localhost:8081/swagger-ui/index.html) |
| **Batch** | [<img src="https://img.shields.io/badge/Swagger_%3A8090-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>](http://localhost:8090/swagger-ui/index.html) |

<br>

## 🗂️ ERD

<details>
<summary><b>모놀리식 MariaDB ERD</b></summary>
<br>
<img src="docs/%EB%AA%A8%EB%86%80%EB%A6%AC%EC%8B%9D%20Mariadb%20ERD.png"/>
</details>

<details>
<summary><b>POS MSA MariaDB ERD</b></summary>
<br>
<img src="docs/POS%20MSA%20MariaDB%20ERD.png"/>
</details>

<details>
<summary><b>통계 MSA MariaDB ERD</b></summary>
<br>
<img src="docs/%ED%86%B5%EA%B3%84%20MSA%20MariaDB%20ERD.png"/>
</details>

<details>
<summary><b>Billing Batch MariaDB ERD</b></summary>
<br>
<img src="docs/billing_batch%20MariaDB%20ERD.png"/>
</details>

<br>

---

## ✨ 주요 기능

### 👉 본사 (Head)
<details>
<summary><b>인증 / 가맹점 / 메뉴·상품 / 재고 / 발주 / 배송 / 정산 / 대시보드 / 통계 / ESG / 알림 / 뉴스 / AI / 배치</b></summary>

- **🔐 인증 / 회원** — 로그인 / 회원가입 / 본사 계정 생성 / JWT 인증
- **🏪 가맹점 관리** — 100여 매장 등록 / 정보 수정 / 검색
- **☕ 메뉴 관리** — 메뉴 CRUD + 메뉴 카테고리 + 레시피 (menu_item)
- **📦 상품 / 카테고리** — 원자재 상품 CRUD + 카테고리 관리
- **🗃️ 본사 재고 / 입출고** — `head_inventory` 출고 / 입고 / 위험도 (NORMAL / LOW / CRITICAL) + 이력
- **📋 발주 관리** — 자동 / 확정 / 이력 / 이상 발주 (수량 평균 대비 ratio)
- **⚙️ 발주 일괄 승인 (Spring Batch)** — product 별 파티션 병렬 처리 → APPROVE 전환 + 재고 차감 + 배송 생성
- **🚚 배송 관리** — READY / START / DELIVERYING / DELIVERED / DELAY 상태 추적
- **💰 정산** — 반월 단위 정산 + Billing Batch 자동 처리 + 결제수단 관리
- **📊 본사 대시보드** — 가맹점 / 발주 / 재고 / 배송 KPI + 주간 발주 통계 + 위험 재고 목록 + 이상 발주 통계 + 지연 배송 목록 + 배송 비율
- **📈 장기 통계** — 연도 / 분기 / 월별 매출 + 매장 / 카테고리 / 메뉴 랭킹
- **🌱 ESG 대시보드** — 폐기물 로그 등 ESG 지표
- **🔔 알림 (SSE)** — 본사 실시간 푸시 (재고 부족 / 유통기한 임박 / 이상 발주 / 배송 지연)
- **📰 뉴스 요약** — AI 기반 외부 뉴스 자동 요약
- **🤖 AI 챗봇** — 본사 운영 지원
- **🏦 결제 배치 (Billing Batch)** — 정기 결제 자동 처리
</details>

### 👉 가맹점 (Store)
<details>
<summary><b>인증 / POS / 매장 재고 / 발주 / 배송 / 정산 / 대시보드 / 알림 / 뉴스 / AI</b></summary>

- **🔐 인증 / 회원** — 로그인 / 가맹점 정보 수정
- **💳 POS 결제** — 메뉴 결제 (현금 / 카드, PortOne 연동) + 결제 내역
- **🌙 영업 마감** — 일일 마감 → AI 자동 발주서 생성 (Kafka 이벤트로 본사 통보)
- **🗃️ 매장 재고** — `pos_store_inventory` FIFO 차감 + 입출고 이력 + 위험도
- **📋 발주** — 수동 발주 + AI 추천 자동 발주서 확정 / 항목 수정 / 거절
- **🚚 배송 현황** — 본사 → 매장 배송 진행 상황 추적
- **💰 정산 내역** — 반월 단위 정산 + 매출 채권
- **📊 가맹점 대시보드** — 매출 KPI + 제안 발주서 + 재고 위험 + 정산 + 일별 매출 추이 + 배송 현황
- **🔔 알림 (SSE)** — 가맹점 실시간 푸시 (배송 / 발주 / 재고)
- **📰 뉴스 / 소식** — 본사 공지 + 뉴스 피드
- **🤖 AI 챗봇** — 가맹점 운영 지원
</details>

---

## 🔄 Service Flow

> 핵심 시나리오를 **Mermaid 다이어그램**으로 시각화 (GitHub native 렌더링)

### 🛒 시나리오 1 — POS 결제 → 실시간 통계 사전 집계

```mermaid
sequenceDiagram
    autonumber
    actor Customer as 👥 고객
    participant Owner as 🏪 점주
    participant POS as 💳 POS MSA
    participant Kafka as 📡 Kafka
    participant Stats as 📊 Statistics MSA
    participant Redis as 💾 Redis

    Customer->>Owner: 메뉴 주문
    Owner->>POS: 결제 (현금 / 카드)
    POS->>POS: pos_pay 저장 + 매장 재고 FIFO 차감
    POS->>Kafka: pos.payment.created
    Kafka->>Stats: 이벤트 전달
    Stats->>Redis: INCRBY sales:today<br/>ZINCRBY sales:store:ranking<br/>HINCRBY sales:hourly
    Note over Redis: O(1) 실시간 조회 가능
```

### 🌙 시나리오 2 — 영업 마감 → AI 자동 발주서 생성

```mermaid
sequenceDiagram
    autonumber
    participant Owner as 🏪 점주
    participant POS as 💳 POS MSA
    participant Kafka as 📡 Kafka
    participant Mono as 🖥️ Monolith
    participant SSE as 🔔 SSE

    Owner->>POS: 영업 마감 요청
    POS->>POS: 매장 재고 + 평균 소비량 분석
    POS->>POS: 🤖 AI 자동 발주서 생성
    POS->>Kafka: pos.close.completed
    Kafka->>Mono: 이벤트 수신
    Mono->>Mono: WAITING 상태 발주 저장
    Mono->>SSE: 점주에게 푸시
    SSE-->>Owner: 제안 발주서 알림
    Owner->>Mono: 항목 수정 / 확정 (CONFIRMED)
```

### ⚙️ 시나리오 3 — 발주 일괄 승인 (Spring Batch)

```mermaid
sequenceDiagram
    autonumber
    actor Admin as 🏢 본사
    participant Mono as 🖥️ Monolith
    participant Batch as ⚙️ Spring Batch
    participant DB as 💾 MariaDB

    Admin->>Mono: 일괄 승인 트리거<br/>(PUT /confirmed/approve)
    Mono->>Batch: POST /batch/jobs/approve
    Batch->>DB: Step0 - CONFIRMED 스냅샷 → staging
    Batch->>DB: Step1 - product 별 파티션 병렬 차감 (5 threads)
    Batch->>DB: Step1.5 - 부족 발주 REJECT
    Batch->>DB: Step2 - APPROVE 전환 + 배송 생성 + HeadIncome 적재
    Batch-->>Mono: 200 OK
    Mono-->>Admin: 결과 응답
```

### 🧭 전체 모듈 흐름도

```mermaid
flowchart LR
    Vue[🎨 Frontend<br/>Vue 3 + Vite] --> GWN[🚪 Spring Cloud<br/>Gateway]
    GWN --> Mono[🖥️ Monolith]
    GWN --> POS[💳 POS MSA]
    GWN --> Stats[📊 Statistics MSA]

    Mono <-->|store/menu/product| Kafka{{📡 Kafka}}
    POS <-->|payment/close| Kafka
    Kafka --> Stats

    Mono --> DB[(💾 MariaDB ×3)]
    POS --> DB
    Stats --> DB
    Stats --> Redis[(💾 Redis)]
    Mono -.->|REST 트리거| Batch[⚙️ Batch]
    Batch --> DB
```

<br>

<details>
<summary><b>📋 전체 흐름 — 텍스트 상세 (24개 항목 펼쳐 보기)</b></summary>

#### 👤 사용자 및 권한 (Authentication & RBAC)
1. **회원가입 및 로그인** — 본사 관리자는 본사 계정 생성 후 사용. 가맹점주는 신규 가입 후 본사 승인 절차를 거쳐 매장 계정 사용.
2. **JWT 인증** — 로그인 성공 시 Access Token 발급, axios interceptor 가 모든 API 요청에 자동 첨부.
3. **역할 기반 접근 제어 (RBAC)** — `ADMIN` (본사), `STORE` (가맹점) 권한에 따라 API endpoint 접근 범위를 Spring Security 로 분리.

#### 🏪 가맹점주 (Store)
4. **POS 결제** — 메뉴 선택 → 현금 / 카드 결제 (PortOne 연동) → `pos_pay` 저장 + Kafka `pos.payment.created` 발행 (통계 MSA 실시간 집계).
5. **영업 마감** — 일일 영업 종료 → 매장 재고 + 평균 소비량 기반 **AI 자동 발주서 생성** → Kafka `pos.close.completed` → 본사 통보.
6. **매장 재고 관리** — `pos_store_inventory` FIFO 차감 + 입출고 이력 + 위험도 (NORMAL / LOW / CRITICAL) 알림.
7. **자동 발주 제안 확인** — 본사가 제안한 AI 발주서 확인 → 항목 수정 / 확정 (CONFIRMED) / 거절.
8. **수동 발주** — 점주 직접 작성 → 본사 승인 대기열로 진입.
9. **가맹점 대시보드** — 매출 / 재고 위험 / 제안 발주서 / 정산 / 배송 현황 KPI + 일별 매출 추이.

#### 🏢 본사 관리자 (HQ / ADMIN)
10. **기준정보 관리** — 가맹점 / 메뉴 / 메뉴 카테고리 / 상품 / 상품 카테고리 / 레시피 CRUD → Kafka 이벤트로 POS MSA 자동 동기화.
11. **발주 관리** — 자동 / 확정 / 이력 / 이상 발주 조회. 이상 발주는 매장 평균 발주 수량 대비 ratio 초과 시 자동 판정 (`is_danger=true`).
12. **발주 일괄 승인 (Spring Batch)** — 확정 발주 일괄 승인 트리거 → 4 Step (스테이징 → product 별 파티션 병렬 차감 → 부족 reject → APPROVE 전환) → 배송 생성 + 매출 채권 적재.
13. **본사 재고 / 입출고** — `head_inventory` 출고 / 입고 / 위험도 + 이력 추적.
14. **배송 관리** — READY / START / DELIVERYING / DELIVERED / DELAY 상태 전이.
15. **정산 / 결제수단** — 반월 단위 정산 + Billing Batch 자동 처리 + 결제수단 관리.
16. **본사 대시보드** — 가맹점 / 발주 / 재고 위험 / 배송 KPI + 주간 발주 통계 + 이상 발주 / 지연 배송 목록.

#### 📊 통계 MSA (Real-time & Long-term)
17. **실시간 통계 (Redis 사전 집계)** — POS 결제 Kafka 수신 → Redis 키 누적 (`INCRBY` / `HINCRBY` / `ZINCRBY`) → 오늘 매출 / TOP 5 / 시간대별 / 카테고리별 / 결제수단별 **O(1) 조회**.
18. **장기 통계 dump (ShedLock)** — 매일 새벽 5시 분산 락으로 Redis → `daily_*_sales` 테이블 dump → 연 / 분기 / 월 / 매장 / 카테고리 / 메뉴 랭킹 조회.

#### 🤖 자동화 · AI · 알림 (Automation & Intelligence)
19. **AI 자동 발주서 생성** — 매장 영업 마감 후 매장 재고 + 평균 소비량 분석 → 발주 항목 / 수량 자동 추천.
20. **AI 챗봇** — 본사 / 가맹점 운영 지원 (질의응답).
21. **SSE 실시간 알림** — 재고 부족 / 유통기한 임박 / 이상 발주 / 배송 지연 → 본사 / 가맹점에 실시간 푸시.
22. **뉴스 요약** — AI 기반 외부 뉴스 자동 요약.
23. **결제 배치 (Billing Batch)** — 반월 단위 정기 결제 자동 처리.
24. **ESG 데이터 관리** — 폐기물 로그 등 ESG 지표 수집 / 시각화.
</details>

---

## 🚀 빠른 시작

> 사전 요구사항: **JDK 17 / Node.js 20+ / Docker Desktop**

| 항목 | 명령 / 안내 |
|---|---|
| 🐳 인프라 (Kafka + Kafka UI) | `docker compose -f docker-compose.local.yml up -d` |
| 🖥️ Backend 실행 | [backend/README.md](backend/README.md) — 모듈 7개 실행 순서 / DB 컨테이너 / 환경변수 |
| 🎨 Frontend 실행 | [frontend/README.md](frontend/README.md) — `npm install && npm run dev` → http://localhost:5173 |

---

## 🔄 CI / CD

### 🚀 배포 파이프라인
- 개발자 `git push` → GitHub Webhook → Jenkins 자동 트리거
- **Build** : Gradle 빌드 → 단위 테스트 → 정적 분석
- **Image** : Docker 이미지 build / Docker Hub push
- **Deploy** : K8s manifest 갱신 → kubectl rollout → **Blue / Green** 전환
- **Monitor** : 배포 후 헬스 체크 + 모니터링

### 🧩 핵심 의사결정 (왜 이 구조를?)
| 항목 | 채택 사유                                                                                                                                                   |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| 🌐 **Nginx Reverse Proxy + TLS** | 단일 진입점에서 HTTPS 종료 + API 경로별 분기 (`/api/pos` → POS MSA, `/api/statistics` → 통계 MSA, `/api/*` → 모놀리식). <br> SSE 알림 위해 HTTP/1.1 유지 + `proxy_buffering off`. |
| 🚪 **LoadBalancer Service** | Nginx 가 직접 진입점 역할.                                                                                             |
| 🟦 **Blue / Green 배포** | 기존 Pod 유지 + 새 버전 Pod 띄운 후 Service selector 전환 → **다운타임 0**. 문제 발생 시 즉시 롤백 가능.                                                                           |
| ⚖️ **MetalLB LoadBalancer** | 베어메탈 K8s 환경에서 외부 IP 자동 할당 (클라우드 LB 미사용).                                                                                                                |
| 🔄 **Spring Cloud Gateway + Eureka** | 모듈 간 동적 라우팅 + 서비스 디스커버리. 신규 MSA 추가 시 코드 수정 없이 자동 발견.                                                                                                    |

### 📚 더 자세히 보기 (Wiki)
> CI / CD 의 상세 설계 / 빌드·배포 시나리오 / 무중단 배포 결과 화면은 Wiki 에 정리되어 있습니다.

* 🏗️ [**시스템 아키텍처 & 빌드 / 배포 계획**](https://github.com/beyond-sw-camp/<repo>/wiki/CICD-Architecture) — Ingress / Nginx 리버스 프록시 / Blue·Green 등 의사결정 배경
* 📸 [**무중단 배포 결과 화면**](https://github.com/beyond-sw-camp/<repo>/wiki/Zero-Downtime-Deploy) — Blue / Green 전환 시연 캡처

---

## 📚 Documents & Wiki

> **프로젝트의 상세한 내용은 아래 문서에서 확인하실 수 있습니다.**

### 🔧 모듈별 상세 README
* 🖥️ [**Backend 자세히 보기**](backend/README.md) — 7개 모듈 (monolith / pos / statistics / batch / billing-batch / gateway / discovery) + 로컬 실행 + Kafka 토픽 매트릭스 + 에러 코드
* 🎨 [**Frontend 자세히 보기**](frontend/README.md) — Vue 3 + Vite 구조 + 페이지 매트릭스 + API 라우팅 + 운영 배포

### 📜 컨벤션 (Wiki)
* 📝 [**Commit Convention**](https://github.com/beyond-sw-camp/<repo>/wiki/Commit-Convention) — 커밋 메시지 규칙
* 🐛 [**Issue Convention**](https://github.com/beyond-sw-camp/<repo>/wiki/Issue-Convention) — 이슈 작성 템플릿
* 🔀 [**Pull Request Convention**](https://github.com/beyond-sw-camp/<repo>/wiki/Pull-Request-Convention) — PR 작성 규칙
* 🔄 [**Workflow Convention**](https://github.com/beyond-sw-camp/<repo>/wiki/Workflow-Convention) — 단계별 작업 흐름
* 📘 [**Backend Javadoc Convention**](https://github.com/beyond-sw-camp/<repo>/wiki/Javadoc-Convention) — Controller 주석 규칙

---

![footer](https://capsule-render.vercel.app/api?type=Venom&color=4F46E5&height=120&section=footer&fontColor=ffffff)
