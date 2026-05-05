# 자동 발주서 생성 (AI) - 구현 가이드

> 요구사항 ID: ORDER_001
> 브랜치: feat/backend/auto-order-ai
> 이슈: #62

---

## 단계별 구현 내역

### 1단계: 의존성 추가 (`build.gradle`)

#### 추가한 코드

```groovy
repositories {
    mavenCentral()
    maven { url 'https://repo.spring.io/milestone' }  // 추가
}

dependencies {
    // --- Spring AI (OpenAI) ---
    implementation platform('org.springframework.ai:spring-ai-bom:1.0.0-M6')
    implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'

    // --- 크롤링 ---
    implementation 'org.jsoup:jsoup:1.18.1'
}
```

#### 왜 추가했는가

| 의존성 | 역할 | 선택 이유 |
|--------|------|-----------|
| `spring-ai-bom` | Spring AI 버전 관리 BOM | Spring Boot 3.4와 호환되는 버전 통일 |
| `spring-ai-openai-spring-boot-starter` | OpenAI GPT-4o 호출 | Python 없이 Spring Boot 안에서 LLM 호출 가능. 팀 전원 Java 사용 중이라 학습 비용 최소화 |
| `jsoup` | HTML 크롤링/파싱 | 뉴스/이벤트 데이터 수집용. Java 네이티브라 별도 서비스 불필요 |
| Spring milestone repo | Spring AI가 아직 정식 릴리즈 전 | mavenCentral에 없는 M6 버전을 받기 위해 필요 |

#### 코드 설명

- **BOM (Bill of Materials)**: `platform()`으로 선언하면 Spring AI 관련 라이브러리의 버전을 일괄 관리. 개별 버전 명시 불필요.
- **spring-ai-openai-spring-boot-starter**: 이 하나로 `ChatClient`, `OpenAiChatModel` 등 OpenAI 연동에 필요한 Bean이 자동 등록됨.
- **Jsoup**: 2단계(뉴스/행사 크롤링)에서 사용 예정. HTML을 DOM처럼 파싱해서 원하는 데이터만 추출.

---

### 2단계: OpenAI 설정 (`application-dev.yaml` + `.env`)

#### 추가한 코드

**application-dev.yaml:**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.3
```

**.env:**
```
OPENAI_API_KEY=your-openai-api-key-here
```

#### 왜 이렇게 설정했는가

| 설정 | 역할 | 이유 |
|------|------|------|
| `api-key: ${OPENAI_API_KEY}` | 환경변수로 API Key 주입 | 코드에 키를 직접 넣지 않음 (보안). 기존 `.env` 패턴 동일하게 사용 |
| `model: gpt-4o` | 사용할 LLM 모델 지정 | 학원 제공 API Key가 GPT-4o 지원 |
| `temperature: 0.3` | 응답의 창의성 정도 (0~1) | 발주서는 정확한 수치가 중요하므로 낮게 설정. 높으면 매번 다른 결과 나옴 |

#### 코드 설명

- Spring AI starter가 `spring.ai.openai.*` 프로퍼티를 자동으로 읽어서 `ChatClient` Bean을 구성함
- `.env` 파일은 `.gitignore`에 포함되어 있어 원격 저장소에 올라가지 않음
- 팀원은 각자 `.env`에 본인 API Key를 넣으면 됨

---

### 3단계: 마감 API (`POST /pos/close`)

> (구현 후 업데이트 예정)

---

### 4단계: AI 발주서 생성 Service

> (구현 후 업데이트 예정)

---

## 전체 흐름 요약

```
[점주 마감 버튼 클릭]
  → Frontend: POST /api/pos/close 호출
  → Backend:
      ① 마감 기록 저장
      ② 해당 매장 재고 현황 조회 (StoreInventory)
      ③ 재고 차감 이력 조회 (InventoryMovement)
      ④ 과거 발주 이력 조회 (Orders)
      ⑤ 요일/배송 가능일 계산
      ⑥ OpenAI GPT-4o 호출 (Spring AI) - 프롬프트에 위 데이터 전달
      ⑦ AI 응답 파싱 → Orders 테이블에 AUTO/WAITING 상태로 저장
  → 점주 대시보드에서 제안 발주서 확인
```

## 기존 코드와의 관계

| 기존 코드 | 이번 기능에서의 역할 |
|-----------|---------------------|
| `Orders` Entity (ordersType=AUTO) | AI가 생성한 발주서 저장 |
| `StoreInventory` Entity | 현재 재고 수량 조회 |
| `InventoryMovement` Entity | 과거 사용량 패턴 분석 데이터 |
| `OrdersScheduler` | Scheduler 패턴 참고 |
| `PosController` | 마감 API 추가 위치 |
