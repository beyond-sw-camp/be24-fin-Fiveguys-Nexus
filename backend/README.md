# Nexus Backend

> Spring Boot 3.4 + Java 17 기반 7개 모듈로 구성된 백엔드. 모놀리식 + 3개 도메인 MSA + 2개 배치 + 게이트웨이/디스커버리.

---

## 모듈 구성

| 모듈 | 포트 | 책임 | DB | Kafka |
|---|---|---|---|---|
| **discovery** | 8761 | Eureka 서비스 디스커버리 | — | — |
| **gateway** | 8088 | Spring Cloud Gateway (라우팅) | — | — |
| **monolith** | 8080 | 본사 + 공통 도메인 (발주/알림/대시보드/정산/본사 재고) | MariaDB :3306 | Producer + Consumer |
| **pos** | 8082 | POS 결제, 매장 재고, POS 메뉴 | MariaDB :3308 | Producer + Consumer |
| **statistics** | 8081 | 실시간 / 장기 통계 | MariaDB :3307 + Redis :6379 | Consumer |
| **batch** | 8090 | 발주 일괄 승인 (Spring Batch + REST 트리거) | 모놀리식 DB 공유 (`order_batch.orders_item_staging`) | — |
| **billing-batch** | 8080* | 정산 자동 배치 (Spring Batch) | 모놀리식 DB 공유 | — |

(*) billing-batch 는 K8s 환경에서 별도 컨테이너로 격리되어 8080 사용. 로컬 동시 실행 시 포트 충돌 주의.

---

## 모듈 상세

### discovery (Eureka)
- 모든 백엔드 모듈의 서비스 인스턴스 등록 / 헬스 체크
- 다른 모듈은 `eureka.client.service-url.defaultZone` 환경변수로 등록

### gateway
- 외부 / 프론트엔드의 진입점 (REST 라우팅)
- 인증 토큰 검증, CORS, 경로 기반 라우팅
- 라우팅 규칙:
  - `/api/pos/**` → POS MSA
  - `/api/statistics/**` → Statistics MSA
  - `/api/**` → Monolith

### monolith
- **책임:** 본사 운영 도메인 + 공통 도메인
- **도메인:**
  - 가맹점 (store) — CRUD, 정산
  - 발주 (orders) — 자동 / 확정 / 이력 / 이상
  - 배송 (delivery)
  - 본사 재고 (head_inventory)
  - 알림 (head_notification, store_notification, SSE)
  - 대시보드 (head + store)
  - 정산 (head_income, billing)
  - 통계 (잔존 — 신규는 Statistics MSA 사용)
  - 인증 (JWT, Security)
- **의존:** MariaDB, Kafka (Producer + Consumer)

### pos (POS MSA)
- **책임:** 가맹점 POS 시스템
- **도메인:**
  - 결제 (pos_pay)
  - 매장 재고 (pos_store_inventory)
  - POS 메뉴 (모놀리식 menu 의 read-model)
  - 영업 마감 → AI 자동 발주서 생성 (모놀리식으로 이벤트 발행)
- **의존:** MariaDB (별도 :3308), Kafka

### statistics (통계 MSA)
- **책임:** 실시간 + 장기 통계
- **도메인:**
  - 실시간 — Redis 사전 집계 (`sales:today`, `sales:store:ranking`, `sales:menu:ranking` 등) O(1) 조회
  - 장기 — `daily_*_sales` 테이블 (연 / 분기 / 월별, 매장 / 카테고리 / 메뉴 랭킹)
  - 일별 dump — Redis → MariaDB (매일 새벽, ShedLock 분산 락)
- **의존:** MariaDB (별도 :3307), Redis Cluster, Kafka Consumer

### batch
- **책임:** 본사 확정 발주 일괄 승인 (REST 트리거)
- **흐름:** `POST /batch/jobs/approve` → JobLauncher → 4 Step
  1. `prepareOrdersStagingStep` — CONFIRMED 발주 스냅샷을 staging 테이블로
  2. `productProcessPartitionStep` — product 별 파티션 병렬 재고 차감
  3. `rejectInsufficientOrdersStep` — 재고 부족 발주 REJECT 처리
  4. `orderApproveStep` — 최종 APPROVE 전환
- **의존:** 모놀리식 DB 공유 (`order_batch` 별도 스키마)

### billing-batch
- **책임:** 정산 자동 배치 (반월 단위 정산 / 매출 채권 결산)
- **의존:** 모놀리식 DB 공유

---

## 로컬 실행 환경

### 인프라 컨테이너

```bash
# Monolith MariaDB (3306)
docker run -d --name nexus-monolith-db -p 3306:3306 \
  -e MARIADB_ROOT_PASSWORD=qwer1234 -e MARIADB_DATABASE=nexus \
  -v nexus-monolith-db-data:/var/lib/mysql \
  mariadb:11 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# Statistics MariaDB (3307)
docker run -d --name nexus-stats-db -p 3307:3306 \
  -e MARIADB_ROOT_PASSWORD=qwer1234 -e MARIADB_DATABASE=statistics \
  -v nexus-stats-db-data:/var/lib/mysql \
  mariadb:11 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# POS MariaDB (3308)
docker run -d --name nexus-pos-db -p 3308:3306 \
  -e MARIADB_ROOT_PASSWORD=qwer1234 -e MARIADB_DATABASE=pos \
  -v nexus-pos-db-data:/var/lib/mysql \
  mariadb:11 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# Redis (6379)
docker run -d --name nexus-redis -p 6379:6379 \
  -v nexus-redis-data:/data \
  redis:7-alpine

# Kafka (9092) + Kafka UI (8090)
docker compose -f ../docker-compose.local.yml up -d
```

### 환경변수 (Spring Boot Run Configuration 또는 .env)

```bash
# DB
DB_HOST=localhost
MONOLITH_DB_PORT=3306
STATS_DB_PORT=3307
POS_DB_PORT=3308
DB_USERNAME=root
DB_PASSWORD=qwer1234

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=<32+ chars>

# Eureka
EUREKA_DEFAULT_ZONE=http://localhost:8761/eureka/

# Batch service (모놀리식에서 호출)
BATCH_SERVICE_URL=http://localhost:8090

# Mail (모놀리식)
MAIL_USERNAME=...
MAIL_PASSWORD=...

# AWS S3 (선택)
AWS_S3_BUCKET=...
```

### 실행 순서

1. **discovery** (8761) — Eureka 서버 (다른 모듈이 등록되려면 먼저 떠야 함)
2. **gateway** (8088)
3. **monolith** (8080), **pos** (8082), **statistics** (8081) — 병렬 가능
4. **batch** (8090)
5. (선택) **billing-batch** — 포트 충돌 주의

### 시드 데이터 (dev)

```bash
# 모놀리식 (매장 100 + 결제 / 발주 / 알림 등)
docker exec -i nexus-monolith-db mariadb -uroot -pqwer1234 nexus \
  < monolith/src/main/resources/seed-dev.sql

# 통계 MSA
docker exec -i nexus-stats-db mariadb -uroot -pqwer1234 statistics \
  < statistics/src/main/resources/seed-dev.sql

# Redis 사전 집계
REDIS_CLI="docker exec -i nexus-redis redis-cli" bash \
  statistics/src/main/resources/seed-redis-dev.sh
```

### 로그인 (dev seed)
- 본사: `admin@theventi.co.kr` / `password123`
- 가맹점: `store0001@theventi.co.kr` ~ `store0100@theventi.co.kr` / `password123`

---

## Kafka 토픽 매트릭스

| 토픽 | Producer | Consumer | 용도 |
|---|---|---|---|
| `store.created` | monolith | pos | 매장 등록 시 POS 동기화 |
| `store.updated` | monolith | pos | 매장 정보 변경 동기화 |
| `menu.created` | monolith | pos | 신규 메뉴 POS 동기화 |
| `menu.updated` | monolith | pos | 메뉴 정보 변경 |
| `menu.deleted` | monolith | pos | 메뉴 삭제 |
| `product.created` | monolith | pos | 신규 상품 동기화 |
| `product.updated` | monolith | pos | 상품 정보 변경 |
| `product.deleted` | monolith | pos | 상품 삭제 |
| `pos.payment.created` | pos, monolith | statistics | 결제 발생 → 통계 사전 집계 |
| `pos.close.completed` | pos | monolith | 영업 마감 → AI 자동 발주서 생성 |

---

## Swagger UI

| 모듈 | URL |
|---|---|
| monolith | http://localhost:8080/swagger-ui/index.html |
| pos | http://localhost:8082/swagger-ui/index.html |
| statistics | http://localhost:8081/swagger-ui/index.html |
| batch | http://localhost:8090/swagger-ui/index.html |

---

## 응답 / 에러 컨벤션

### BaseResponse

```json
{
  "success": true,
  "code": 2000,
  "message": "요청 성공",
  "result": { ... }
}
```

### BaseResponseStatus 에러 코드 (모놀리식)

| 코드 | 이름 | 의미 |
|---|---|---|
| 2000 | SUCCESS | 성공 |
| 3105 | STORE_NOT_FOUND | 가맹점 정보 없음 |
| 3202 | NOT_FOUND_PRODUCT | 상품 없음 |
| 3204 | NOT_FOUND_MENU | 메뉴 없음 |
| 3306 | STORE_INVENTORY_INSUFFICIENT | 매장 재고 부족 |
| 3307 | POS_STORE_INVENTORY_INSUFFICIENT | POS 매장 재고 부족 |
| 4001 | REQUEST_ERROR | 요청 형식 오류 |
| 4002 | NOT_FOUND_DATA | 일반 데이터 없음 |
| 4201 | ORDERS_APPROVE_INSUFFICIENT_STOCK | 본사 재고 부족으로 승인 불가 |
| 4202 | NOT_FOUND_ORDERS | 발주 없음 |
| 4203 | NOT_FOUND_ORDERS_ITEM | 발주 항목 없음 |
| 4204 | ORDERS_NOT_AUTHORIZED | 해당 발주 권한 없음 |
| 4205 | ORDERS_INVALID_STATUS | 현재 발주 상태에서 처리 불가 |
| 4209 | ORDERS_ITEMS_EMPTY | 발주 항목 비어있음 |
| 4210 | BATCH_SERVICE_UNAVAILABLE | 배치 서비스 호출 실패 |
| 4301 | NOT_FOUND_NOTIFICATION | 알림 없음 |
| 5000 | FAIL | 일반 실패 |
| 5001 | DATABASE_ERROR | DB 처리 오류 |

전체 정의: `monolith/src/main/java/com/example/nexus/common/model/BaseResponseStatus.java`

---

## 컨벤션

- **Javadoc:** [.claude/convention/JAVADOC_CONVENTION.md](../.claude/convention/JAVADOC_CONVENTION.md)
- **커밋 / PR / 이슈:** 루트 [README.md](../README.md) 의 컨벤션 섹션 참조

---

## 트러블슈팅
- `docs/troubleshooting/` 에 작업 이슈 기록
- Kafka / Redis / MSA 관련은 반드시 문서화
