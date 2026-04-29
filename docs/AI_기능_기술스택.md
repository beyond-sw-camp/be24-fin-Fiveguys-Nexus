# AI 기능 상세 기술 스택

## 프로젝트 기본 스택

| 구분 | 기술 |
|------|------|
| Frontend | Vue 3 + Vite + Tailwind CSS |
| Backend | Spring Boot 3.4 + Java 17 |
| DB | MariaDB (JPA) |
| 인증 | Spring Security + JWT |
| 인프라 | Docker + Jenkins + K8s |

---

## 개발 일정

| 단계 | 기간 | 목표 |
|------|------|------|
| **1단계** | ~ 5/8 (금) | 기능 구현 (MariaDB + Spring AI로 동작하게 만들기) |
| **2단계** | 5/8 ~ 6/8 | 성능 개선 (NoSQL 도입, MSA 전환) |

---

# 1단계 - 기능 구현 (~ 5/8)

> MariaDB + Spring AI + OpenAI만으로 기능 완성

## AI 스택

| 구분 | 기술 | 비고 |
|------|------|------|
| AI 프레임워크 | **Spring AI** | Spring Boot 내에서 OpenAI 연동 (Python 불필요) |
| LLM | **OpenAI GPT-4o** | 학원 제공 API Key 사용 |
| 크롤링 | **Jsoup** | Java HTML 파서 (Python 불필요) |

### Spring AI 선택 이유
- Python 별도 서비스 없이 **Spring Boot 안에서** LLM 호출 가능
- 기존 Spring 생태계(JPA, Security 등)와 자연스럽게 통합
- 팀 학습 비용 최소화 (단일 언어)

---

## 기능 1: 자동 발주서 생성

### 아키텍처 (1단계)

```
재고 임계치 도달 (MariaDB 모니터링)
  │
  ▼
Spring Scheduler 트리거
  │
  ▼
데이터 조회 (MariaDB)
  - 현재 재고 수량
  - 과거 발주 이력 (SQL 조회)
  - 공급업체 정보
  │
  ▼
OpenAI GPT-4o 호출 (Spring AI)
  - 조회된 데이터를 프롬프트에 직접 전달
  │
  ▼
발주서 초안 생성 → MariaDB 저장
  │
  ▼
관리자 승인 → 최종 발주서
```

### 기술 스택 (1단계)

| 레이어 | 기술 | 역할 |
|--------|------|------|
| 트리거 | Spring Scheduler (@Scheduled) | 주기적 재고 확인 |
| 데이터 조회 | JPA + MariaDB | 재고, 공급업체, 과거 발주 이력 |
| 문서 생성 | OpenAI GPT-4o (Spring AI) | 발주서 초안 작성 |
| 저장 | MariaDB | 발주서 초안 + 최종 저장 |

---

## 기능 2: 뉴스/법안/행사/날씨 요약

### 아키텍처 (1단계)

```
매일 AM 7:00 (Spring Scheduler + Cron)
  │
  ▼
데이터 수집 (병렬)
  ├─ 뉴스 크롤링 ──► Jsoup (Java HTML 파서)
  ├─ 법안 API ──► 국회 열린데이터 API
  ├─ 지역 행사 ──► 공공데이터포털 API
  └─ 날씨 ──► 기상청 API
  │
  ▼
수집 데이터 저장 ──► MariaDB
  │
  ▼
OpenAI GPT-4o 호출 (Spring AI)
  - 수집된 데이터를 프롬프트에 직접 전달
  - 카테고리별 요약 생성
  │
  ▼
요약본 저장 ──► MariaDB
  │
  ▼
Vue 대시보드에서 조회
```

### 기술 스택 (1단계)

| 레이어 | 기술 | 역할 |
|--------|------|------|
| 스케줄러 | Spring Scheduler + Cron | 매일 아침 자동 실행 |
| 크롤링 | Jsoup | HTML 파싱 |
| 외부 API | RestTemplate / WebClient | 공공 API 호출 (법안, 행사, 날씨) |
| 요약 생성 | OpenAI GPT-4o (Spring AI) | 카테고리별 요약본 생성 |
| 저장 | MariaDB | 크롤링 원본 + 요약본 저장 |
| 화면 | Vue 3 대시보드 | 요약본 카드형 UI 표시 |

---

## 1단계 핵심 의존성 (build.gradle)

```groovy
// Spring AI - OpenAI 연동
implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'

// 크롤링
implementation 'org.jsoup:jsoup:1.18.1'
```

---

# 2단계 - 성능 개선 + MSA (5/8 ~ 6/8)

> NoSQL 도입, RAG 고도화, MSA 전환

## 변경 사항 요약

| 개선 영역 | 1단계 (AS-IS) | 2단계 (TO-BE) |
|-----------|---------------|---------------|
| 유사 이력 검색 | SQL 쿼리 (MariaDB) | **Elasticsearch kNN 벡터 검색 (RAG)** |
| 임베딩 | 미사용 | **OpenAI text-embedding-3-small** |
| 요약본 응답 | MariaDB 직접 조회 | **Redis 캐싱 (TTL 24h)** |
| 크롤링 원본 저장 | MariaDB (정형화 필요) | **MongoDB (비정형 그대로 저장)** |
| 아키텍처 | 모놀리식 | **MSA (AI 서비스 분리)** |

---

## NoSQL 도입 계획

### 1. Redis - 캐싱 / 성능 최적화

| 적용 위치 | 용도 |
|-----------|------|
| **AI 요약 캐시** | 매일 1회 생성된 요약본 캐싱 → DB 조회 없이 즉시 응답 (TTL: 24h) |
| **발주서 생성 중복 방지** | 동일 상품에 대한 발주서 중복 생성 요청을 락(Lock)으로 방지 |
| **LLM 응답 캐시** | 동일/유사 요청에 대한 LLM 응답 캐싱 → API 호출 비용 절감 |

### 2. Elasticsearch - 벡터 검색 + 전문 검색

| 적용 위치 | 용도 |
|-----------|------|
| **뉴스/법안 전문 검색** | 크롤링된 문서의 키워드 검색 (한국어 형태소 분석 nori 플러그인) |
| **벡터 스토어 (RAG)** | ES 8.x kNN 벡터 검색으로 임베딩 저장 + 유사도 검색 |
| **발주 이력 검색** | 과거 발주 패턴을 의미 기반으로 검색 |

> ES 하나로 **전문 검색 + 벡터 검색** 동시 처리 → 별도 벡터 DB 불필요

### 3. MongoDB - 비정형 데이터 저장

| 적용 위치 | 용도 |
|-----------|------|
| **크롤링 원본 데이터** | 뉴스, 법안, 행사 등 출처마다 구조가 다른 원본을 유연하게 저장 |
| **AI 생성 문서** | LLM이 생성한 발주서 초안, 요약본 등 JSON 문서 저장 |
| **AI 요청/응답 로그** | 프롬프트, 응답, 토큰 사용량 등 이력 관리 |

---

## 2단계 아키텍처 (고도화)

### 자동 발주서 - RAG 적용

```
재고 임계치 도달
  │
  ▼
과거 유사 발주 이력 검색 ──► Elasticsearch (벡터 검색, RAG)
  │
  ▼
컨텍스트 조합
  - 현재 재고 수량 (MariaDB)
  - 유사 발주 이력 (Elasticsearch)
  - 공급업체 정보 (MariaDB)
  │
  ▼
OpenAI GPT-4o 호출 (Spring AI)
  │
  ▼
발주서 초안 저장 ──► MongoDB
중복 방지 ──► Redis Lock
  │
  ▼
관리자 승인 → 최종 발주서 (MariaDB)
```

### 뉴스 요약 - RAG + 캐싱 적용

```
매일 AM 7:00 크론 실행
  │
  ▼
데이터 수집 (Jsoup + 공공 API)
  │
  ▼
원본 저장 ──► MongoDB
  │
  ▼
임베딩 변환 ──► OpenAI text-embedding-3-small
  │
  ▼
벡터 저장 + 관련도 필터링 ──► Elasticsearch (kNN)
  │
  ▼
GPT-4o 요약 생성 (Spring AI)
  │
  ▼
요약본 캐싱 ──► Redis (TTL 24h)
  │
  ▼
Vue 대시보드 조회 (Redis 캐시 히트 → 즉시 응답)
```

---

## DB별 역할 정리 (2단계 최종)

```
┌─────────────────────────────────────────────────────┐
│                    Spring Boot (MSA)                  │
│                                                      │
│  MariaDB          Redis          Elasticsearch       │
│  ─────────        ──────         ──────────────      │
│  비즈니스 데이터    캐싱            전문 검색           │
│  - 회원/상품       - 요약본 캐시    - 뉴스/법안 검색    │
│  - 재고/발주       - LLM 응답 캐시  - 벡터 검색 (RAG)  │
│  - 주문/결제       - 분산 락       - 발주 이력 검색     │
│                                                      │
│  MongoDB                                             │
│  ────────                                            │
│  비정형 데이터                                         │
│  - 크롤링 원본                                        │
│  - AI 생성 문서                                       │
│  - AI 로그                                           │
└─────────────────────────────────────────────────────┘
```

---

## 2단계 추가 의존성 (build.gradle)

```groovy
// 임베딩 (RAG용)
implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'

// Elasticsearch
implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'

// Redis
implementation 'org.springframework.boot:spring-boot-starter-data-redis'

// MongoDB
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```

---

## 기술 선택 요약

| 고민 | 결정 | 이유 |
|------|------|------|
| Python vs Java | **Java (Spring AI)** | 팀 학습 비용 최소화, 단일 언어로 통일 |
| 별도 벡터 DB vs ES | **Elasticsearch** | 전문 검색 + 벡터 검색 동시 해결 |
| 캐싱 전략 | **Redis** | 하루 1회 생성 요약본 캐싱에 최적 |
| 크롤링 원본 저장 | **MongoDB** | 출처별 스키마가 다른 비정형 데이터 |
| 크롤러 | **Jsoup** | Java 네이티브, Python 서비스 분리 불필요 |
