# spring-boot-monolithic-starter
스프링부트 모놀리식 스타터 
> DDD 기반 스프링부트 **모놀리식 애플리케이션**을 일관성 있게 설계하기 위한 구조 및 규칙 가이드입니다.
>

---

## 📂 Package Structure

> 도메인 중심 구조 + 계층 책임 분리
>

```
com.example.app
│
├── global/                         # 전역 공통 요소
│   ├── config/                     # 전역 설정 (JPA, Security, Web, Kafka 등)
│   ├── response/                   # API 응답 표준 (ApiResponse, ErrorResponse)
│   ├── exception/                  # 전역 예외 처리 (GlobalExceptionHandler, ErrorCode)
│   └── util/                       # 유틸리티 클래스
│
└── domains/                        # 도메인별 모듈
    ├── common/                     # 도메인 공통 인프라
    │   ├── outbox/                 # Transaction Outbox 패턴 (선택)
    │   └── saga/                   # Saga 공통 인프라 (선택)
    │
    └── {domain-name}/              # 예: order, product, customer
        ├── controller/             # REST API 컨트롤러
        ├── service/
        │   ├── application/        # Application Service (유즈케이스 조율)
        │   └── domain/             # Domain Service (도메인 규칙)
        ├── repository/             # Repository 인터페이스
        ├── model/                  # Entity, Value Object, Enum
        ├── dto/
        │   ├── request/            # 요청 DTO
        │   └── response/           # 응답 DTO
        ├── exception/              # 도메인 예외
        ├── event/                  # 도메인 이벤트 (선택)
        ├── saga/                   # Saga 구현 (선택)
        └── kafka/                  # Kafka Producer/Consumer (선택)

```

---

## 🧱 Layer Responsibilities

### Controller

- HTTP 요청/응답 처리
- 요청 검증(@Valid)
- **Application Service만 호출**
- 비즈니스 로직 ❌

### Application Service

- 유즈케이스 흐름 관리
- 트랜잭션 경계 관리
- 여러 Repository / Domain Service 조합
- **다른 도메인의 Application Service 호출 ❌**

### Domain Service

- 하나의 Entity에 넣기 애매한 비즈니스 규칙을 담당
- Stateless (상태와 관련된 값을 맴버 변수로 가질 수 없음) ❌
- **자기 도메인만 사용**
- 트랜잭션 관리 ❌

### Model (Entity / Value Object)

- 핵심 비즈니스 규칙 보유
- Entity
    - 기본 생성자는 `protected`
    - 객체 생성은 정적 팩토리 메서드 사용
    - Getter만 제공, Setter 금지
    - 비즈니스 의미를 가진 메서드로 상태 변경
    - `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- Value Object
    - 정적 팩토리 메서드로 생성
    - 생성 시 검증 수행
    - 불변 객체 (final 필드)
    - `equals/hashCode` 오버라이드
    - `@Embeddable` 어노테이션 사용

### Repository

- JPA 기반 데이터 접근 인터페이스

### DTO

- API 전송 전용 객체
- Request / Response 분리
- Response는 `from()` 팩토리 메서드 사용

### Exception

- 도메인별 비즈니스 예외 정의

---

## 🔗 의존성 규칙

| 계층 | 참조 가능 | 참조 금지 |
| --- | --- | --- |
| **Controller** | Application Service | Domain Service, Repository, Model |
| **Application Service** | 자기/다른 도메인의 Repository, Domain Service | 다른 도메인의 Application Service |
| **Domain Service** | 자기 도메인의 Repository, Model | 다른 도메인의 Domain Service |
| **Model** | Value Object, Enum | Repository, Service |
- Controller → Application Service
- Application Service → Repository / Domain Service
- Domain Service → Model
- Model → 다른 계층 의존 ❌

---

## 🏷 Naming Convention

### 클래스명

| 구분 | 규칙 | 예시 |
| --- | --- | --- |
| Controller | `{Domain}Controller` | OrderController |
| Application Service | `{Domain}ApplicationService` | OrderApplicationService |
| Domain Service | `{비즈니스개념}Service` | OrderPricingService, StockService |
| Entity | 명사 | Order, Product |
| Value Object | 명사 | Money, Address |
| Repository | `{Entity}Repository` | OrderRepository |
| Request DTO | `{동사}{Domain}Request` | CreateOrderRequest |
| Response DTO | `{Domain}Response` | OrderResponse |
| Exception | `{Domain}{이유}Exception` | OrderNotFoundException |

### 메서드명

| 구분 | 규칙 | 예시 |
| --- | --- | --- |
| Application Service | 유즈케이스 동사 | createOrder, cancelOrder |
| Domain Service | 도메인 규칙 동사 | calculateTotalPrice, reserveStock |
| Entity | 비즈니스 동작 동사 | cancel, confirm, ship |
| Repository | find, save, delete | findByCustomerId, save |

---