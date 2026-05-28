![header](https://capsule-render.vercel.app/api?type=Venom&color=4F46E5&height=300&section=header&text=Nexus&desc=%EB%8D%94%EB%B2%A4%ED%8B%B0%20%ED%94%84%EB%9E%9C%EC%B0%A8%EC%9D%B4%EC%A6%88%20%ED%86%B5%ED%95%A9%20%EA%B4%80%EB%A6%AC%20%EC%8B%9C%EC%8A%A4%ED%85%9C&descSize=20&descAlign=50&descAlignY=70&fontSize=100&animation=fadeIn&fontColor=ffffff)

### :sunny: **[플레이 데이터] 한화시스템 BEYOND SW캠프** :sunny:

<br>

## 🤼‍♂️ 팀원 소개

<br>

| 👤 | 이름 | 담당 |
|---|---|---|
| 🐻 | [TBD](https://github.com/) | TBD |
| 🦁 | [TBD](https://github.com/) | TBD |
| 🐶 | [TBD](https://github.com/) | TBD |
| 🐯 | [TBD](https://github.com/) | TBD |
| 🐻‍❄️ | [정동현](https://github.com/) | 발주 / 알림 / 대시보드 / 통계 MSA / 인프라 일부 |

<br>

## ✨ 프로젝트 기본 소개

#### 프로젝트 배경
- 더벤티 본사가 100여 개 가맹점의 발주·재고·배송·정산·매출을 통합 관리할 수 있는 플랫폼이 필요하다.
- 가맹점주는 POS 결제 + 매장 재고 + AI 추천 자동 발주를 한 화면에서 운영할 수 있어야 한다.
- 본사가 실시간/장기 매출 통계로 운영 의사결정을 내릴 수 있어야 한다.

#### 프로젝트 목표
- **본사** — 가맹점 등록/관리, 발주 자동/확정/이상, 배송, 본사 재고, 매출/재고/배송 대시보드, 장기 통계, SSE 알림
- **가맹점** — POS 결제 + 영업 마감 → AI 자동 발주서 생성, 매장 재고, 발주 (수동/제안), 정산
- **통계** — 결제 발생 → Kafka → Redis 사전 집계 (실시간 O(1) 조회), 매일 새벽 MariaDB dump (장기 보존)
- **발주 일괄 승인** — Spring Batch 로 본사 확정 발주를 product 별 파티션 병렬 처리

<br>

### :rainbow: 시연 사이트 바로가기
#### :arrow_right: [TBD](#)

---

## 📌 기술 스택

#### :door: Frontend
<div align="center">
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

#### :computer: Backend
<div align="center">
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

#### :floppy_disk: Data
<div align="center">
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"/>
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
<img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white"/>
</div>

#### :loudspeaker: DevOps / Infra
<div align="center">
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

## :one: &nbsp; 프로젝트 자료

| 자료 | 링크 |
|---|---|
| :large_orange_diamond: 화면 설계 (Figma) | [TBD](#) |
| :large_orange_diamond: 요구사항 정의서 | [TBD](#) |
| :large_orange_diamond: WBS | [TBD](#) |
| :large_orange_diamond: API 명세서 (Notion / Swagger) | [TBD](#) |
| :large_orange_diamond: ERD | 아래 펼침 |
| :large_orange_diamond: 시스템 아키텍처 | 아래 펼침 |

<br>

## :two: &nbsp; ERD
<details>
<summary><b>ERD 보기</b></summary>
<br>
<img src="docs/img/erd.png"/>
</details>

<br>

## :three: &nbsp; 시스템 아키텍처
<details>
<summary><b>시스템 아키텍처 보기</b></summary>
<br>

```
                       [ Frontend : Vue 3 + Vite ]
                                 ↓ HTTPS
                  [ Nginx (TLS termination + reverse proxy) ]
                                 ↓
                  [ Gateway : Spring Cloud Gateway :8088 ]
                                 ↓
┌──────────────────┬──────────────────┬──────────────────┐
│   Monolith       │   POS MSA        │   Statistics MSA │
│   :8080          │   :8082          │   :8081          │
│                  │                  │                  │
│  - 발주          │  - 결제          │  - 실시간 통계   │
│  - 알림 (SSE)    │  - 매장 재고     │  - 장기 통계     │
│  - 대시보드      │  - POS 메뉴      │  - 일별 dump     │
│  - 정산          │  - 영업 마감     │                  │
│  - 본사 재고     │                  │                  │
└────┬─────────────┴──────┬───────────┴──────┬───────────┘
     │                    │                  │
     │       [ Kafka (KRaft) :9092 ]         │
     │  - store / menu / product 이벤트      │
     │  - pos.payment.created                │
     │  - pos.close.completed                │
     │                                       │
     ↓                    ↓                  ↓
[ MariaDB :3306 ]   [ MariaDB :3308 ]  [ MariaDB :3307 ]
                                       [ Redis :6379 ]
                                          (사전 집계)

         [ Batch :8090 ]            [ Billing Batch ]
         - 발주 일괄 승인           - 정산 자동 처리
         (Spring Batch + Kafka)     (Spring Batch)

                 [ Discovery (Eureka) :8761 ]
```

(이미지 버전: `docs/img/architecture.png`)
</details>

<br>

---

## :four: &nbsp; 주요 기능

### 👉 본사 (Head)
<details>
<summary><b>가맹점 관리 / 발주 / 배송 / 재고 / 대시보드 / 통계 / 정산 / 알림</b></summary>

- **가맹점 관리** — 등록 / 정보 수정, 100여 매장 운영
- **발주 관리** — 자동 / 확정 / 이력 / 이상 발주 + Spring Batch 로 일괄 승인
- **배송 관리** — READY / DELIVERYING / DELIVERED / DELAY 상태 추적
- **본사 재고** — `head_inventory` 출고 / 입고 / 위험도(NORMAL/LOW/CRITICAL)
- **대시보드** — 가맹점 KPI / 발주 KPI / 재고 위험 / 배송 현황 / 주간 발주 통계 / 위험 재고 목록 / 이상 발주 통계 / 지연 배송 목록 / 배송 비율
- **장기 통계** — 연도 / 분기 / 월별 매출 + 매장 / 카테고리 / 메뉴 랭킹
- **알림** — SSE 실시간 푸시
- **정산** — Billing Batch 로 반월 자동 정산
</details>

### 👉 가맹점 (Store)
<details>
<summary><b>POS / 매장 재고 / 발주 / 대시보드 / 정산 / 알림</b></summary>

- **POS** — 메뉴 결제 (현금/카드) + 영업 마감 → AI 자동 발주서 생성
- **매장 재고** — `pos_store_inventory` FIFO 차감
- **발주** — 수동 발주 + AI 추천 제안 발주서 확정 / 항목 수정 / 거절
- **가맹점 대시보드** — 매출 KPI + 제안 발주서 + 재고 위험 + 정산 + 일별 매출 추이 + 배송 현황
- **정산 내역** — 반월 단위 정산 + 매출 채권
- **알림** — SSE 실시간 푸시
</details>

---

## :five: &nbsp; 빠른 시작 (로컬 dev)

<details>
<summary><b>사전 요구사항 + 실행 단계</b></summary>

### 사전 요구사항
- JDK 17
- Node.js 20+
- Docker Desktop
- IntelliJ IDEA / VSCode (선택)

### 1. 인프라 컨테이너
```bash
# Kafka + Kafka UI (KRaft 모드 단일 노드)
docker compose -f docker-compose.local.yml up -d

# MariaDB / Redis 컨테이너는 backend/README.md 참조
```

### 2. 백엔드 모듈 (권장 순서)
1. Discovery (Eureka, :8761)
2. Gateway (:8088)
3. Monolith (:8080) / POS (:8082) / Statistics (:8081)
4. Batch (:8090)

자세한 환경변수 / 실행: [backend/README.md](backend/README.md)

### 3. 프론트엔드
```bash
cd frontend
npm install
npm run dev
```
- http://localhost:5173

자세한 내용: [frontend/README.md](frontend/README.md)

### 4. 시드 데이터
```bash
docker exec -i <monolith-mariadb> mariadb -uroot -pqwer1234 nexus < backend/monolith/src/main/resources/seed-dev.sql
docker exec -i <stats-mariadb> mariadb -uroot -pqwer1234 statistics < backend/statistics/src/main/resources/seed-dev.sql
REDIS_CLI="docker exec -i <redis-container> redis-cli" bash backend/statistics/src/main/resources/seed-redis-dev.sh
```

### 5. 로그인 계정 (dev seed)
- 본사: `admin@theventi.co.kr` / `password123`
- 가맹점: `store0001@theventi.co.kr` ~ `store0100@theventi.co.kr` / `password123`

### 6. Swagger UI
| 모듈 | URL |
|---|---|
| Monolith | http://localhost:8080/swagger-ui/index.html |
| POS MSA | http://localhost:8082/swagger-ui/index.html |
| Statistics MSA | http://localhost:8081/swagger-ui/index.html |
| Batch | http://localhost:8090/swagger-ui/index.html |
</details>

---

## :six: &nbsp; 디렉토리 구조

<details>
<summary><b>전체 구조 보기</b></summary>

```
Nexus/
├── backend/                  # 백엔드 모듈 (자세한 내용: backend/README.md)
│   ├── monolith/             # 본사 + 일부 공통 도메인
│   ├── pos/                  # POS MSA (결제, 매장 재고)
│   ├── statistics/           # 통계 MSA (Redis + MariaDB)
│   ├── batch/                # 발주 일괄 승인 배치
│   ├── billing-batch/        # 정산 자동 배치
│   ├── gateway/              # Spring Cloud Gateway
│   └── discovery/            # Eureka 서비스 디스커버리
├── frontend/                 # Vue 3 + Vite (자세한 내용: frontend/README.md)
├── docker/                   # 서비스별 Dockerfile
├── k8s/                      # Kubernetes manifests
├── jenkins/                  # Jenkinsfile 모듈별
├── docs/                     # 트러블슈팅 / 설계 문서
└── docker-compose.local.yml  # 로컬 dev Kafka (gitignore)
```
</details>

---

## :seven: &nbsp; 기타 설명

### 👉 Frontend
- LoadBalancer 타입 Service 로 외부 노출, Nginx 가 진입점
- Nginx Reverse Proxy 로 `/api/*` 를 백엔드 (모놀리식 / Gateway) 로 분기
- SSE 알림 위해 HTTP/1.1 유지 + buffering off
- Deployment + Blue/Green 무중단 배포

#### 🤔 [ Frontend 설명 더보기 ](frontend/README.md)

### 👉 Backend
- 모놀리식 + 3 MSA (POS / 통계 / 배치) + Gateway + Eureka 구조
- Kafka 로 모듈 간 이벤트 발행 (store / menu / product / payment / close)
- 통계 MSA 는 Redis 사전 집계 (실시간) + MariaDB dump (장기)
- 발주 일괄 승인은 Spring Batch (4 Step + product 별 파티션 병렬)
- 모든 백엔드 Cluster IP 통신, 외부엔 Gateway 만 노출

#### 🤔 [ Backend 설명 더보기 ](backend/README.md)

### 👉 CI / CD
- 개발자 push → Webhook → Jenkins 파이프라인
- Gradle build → Docker image build/push → K8s rollout (Blue/Green)
- 무중단 배포 + 모니터링

---

## 📜 컨벤션

| 종류 | 파일 |
|---|---|
| 커밋 | [.claude/convention/COMMIT_CONVENTION.md](.claude/convention/COMMIT_CONVENTION.md) |
| 이슈 | [.claude/convention/ISSUE_CONVENTION.md](.claude/convention/ISSUE_CONVENTION.md) |
| Pull Request | [.claude/convention/PULL_REQUEST_CONVENTION.md](.claude/convention/PULL_REQUEST_CONVENTION.md) |
| 작업 흐름 | [.claude/convention/WORKFLOW_CONVENTION.md](.claude/convention/WORKFLOW_CONVENTION.md) |
| Backend Javadoc | [.claude/convention/JAVADOC_CONVENTION.md](.claude/convention/JAVADOC_CONVENTION.md) |

---

![footer](https://capsule-render.vercel.app/api?type=Venom&color=4F46E5&height=120&section=footer&fontColor=ffffff)
