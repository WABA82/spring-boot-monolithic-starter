# spring-boot-monolithic-starter

> **스프링부트 모놀리식 스타터**: 도메인 중심으로 설계된 Spring Boot 모놀리식 애플리케이션 예제

## **빌드 및 실행 명령어**

```bash
# 빌드
./gradlew build

# 애플리케이션 실행 (Docker Compose를 통해 MySQL 자동 실행)
./gradlew bootRun

# 전체 테스트 실행
./gradlewtest

# 단일 테스트 클래스 실행
./gradlewtest --tests"com.examples.springbootmonolithicstarter.SomeTestClass"

# 단일 테스트 메서드 실행
./gradlewtest --tests"com.examples.springbootmonolithicstarter.SomeTestClass.testMethodName"

# 클린 빌드
./gradlew clean build
```

## **기술 스택**

- Java 21
- Spring Boot 3.5.x (Web, JPA, Validation, Actuator 포함)
- MySQL

  (로컬 개발 환경에서는 spring-boot-docker-compose를 사용한 Docker Compose 기반)

- Lombok
- SpringDoc OpenAPI (Swagger UI)

## **아키텍처 개요**

이 프로젝트는 **DDD(Domain-Driven Design)** 기반의 모놀리식 Spring Boot 애플리케이션으로, 도메인 중심 패키지 구조와 계층별 책임 분리를 따릅니다.

자세한 가이드는 [ARCHITECTURE.md](docs/ARCHITECTURE.md)를 참고하세요.

### **패키지 구조**

```
com.examples.springbootmonolithicstarter
├──global/                        # 공통(횡단) 관심사
│   ├── config/                   # 전역 설정 (JPA, Security, Web, Kafka)
│   ├── response/                 # API 응답 표준 (ApiResponse, ErrorResponse)
│   ├── exception/                # 전역 예외 처리 (GlobalExceptionHandler, ErrorCode)
│   └── util/                     # 유틸리티
│
└── domains/                      # 도메인 모듈
    ├── common/                   # 공통 도메인 인프라 (outbox, saga)
    └── {domain-name}/            # 예: order, product, customer
        ├── controller/           # REST 컨트롤러
        ├── service/
        │   ├── application/      # 애플리케이션 서비스 (유스케이스 오케스트레이션)
        │   └── domain/           # 도메인 서비스 (비즈니스 규칙)
        ├── repository/           # JPA 레포지토리
        ├── model/                # 엔티티, 값 객체, Enum
        ├── dto/
        │   ├── request/          # 요청 DTO
        │   └── response/         # 응답 DTO
        ├── exception/            # 도메인 예외
        ├──event/                 # 도메인 이벤트 (선택)
        ├── saga/                 # Saga 구현 (선택)
        └── kafka/                # Kafka 프로듀서/컨슈머 (선택)
```

### **계층 규칙 (Layer Rules)**

| **계층** | **참조 가능** | **참조 불가** |
| --- | --- | --- |
| Controller | Application Service만 | Domain Service, Repository, Model |
| Application Service | 자신의/다른 도메인의 Repository, Domain Service | 다른 도메인의 Application Service |
| Domain Service | 자신의 도메인 Repository, Model | 다른 도메인의 Domain Service |
| Model | Value Object, Enum | Repository, Service |

### **핵심 설계 원칙**

**Controller**

- HTTP 요청/응답 처리
- 요청 검증(@Valid)
- Application Service만 호출
- 비즈니스 로직 금지

**Application Service**

- 유스케이스 흐름 관리
- 트랜잭션 경계 설정
- Repository 및 Domain Service 오케스트레이션

**Domain Service**

- 단일 Entity에 속하지 않는 비즈니스 규칙
- Stateless
- 자신의 도메인만 처리
- 트랜잭션 관리 없음

**Entity**

- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 사용하는 `protected` 기본 생성자
- 객체 생성을 위한 static factory method 사용
- Getter만 제공, Setter 금지
- 상태 변경은 비즈니스 메서드로 표현

  (예: `cancel()`, `confirm()`, `ship()`)


**Value Object**

- static factory method 사용, 생성 시 유효성 검증
- 불변 객체(final 필드), `equals/hashCode` 오버라이드
- `@Embeddable` 사용

**DTO**

- Request / Response 분리
- Response는 `from()` 팩토리 메서드 사용

### **네이밍 컨벤션**

### **클래스 이름**

| **유형** | **패턴** | **예시** |
| --- | --- | --- |
| Controller | `{Domain}Controller` | OrderController |
| Application Service | `{Domain}ApplicationService` | OrderApplicationService |
| Domain Service | `{Concept}Service` | OrderPricingService, StockService |
| Entity | 명사 | Order, Product |
| Value Object | 명사 | Money, Address |
| Repository | `{Entity}Repository` | OrderRepository |
| Request DTO | `{Verb}{Domain}Request` | CreateOrderRequest |
| Response DTO | `{Domain}Response` | OrderResponse |
| Exception | `{Domain}{Reason}Exception` | OrderNotFoundException |

### **메서드 이름**

| **유형** | **패턴** | **예시** |
| --- | --- | --- |
| Application Service | 유스케이스 동사 | createOrder, cancelOrder |
| Domain Service | 도메인 규칙 동사 | calculateTotalPrice, reserveStock |
| Entity | 비즈니스 행위 동사 | cancel, confirm, ship |
| Repository | find, save, delete | findByCustomerId, save |

## **테스팅 전략**

자세한 테스트 가이드는 [TESTING.md](docs/TESTING.md)를 참고하세요.

| **계층** | **테스트 유형** | **어노테이션** | **속도** |
| --- | --- | --- | --- |
| Model (Entity, VO) | 단위 테스트 | 없음 | 빠름 |
| Domain Service | 단위 테스트 | 없음 | 빠름 |
| Application Service | 단위 테스트 | `@ExtendWith(MockitoExtension.class)` | 빠름 |
| Repository | 슬라이스 테스트 | `@DataJpaTest` | 중간 |
| Controller | 슬라이스 테스트 | `@WebMvcTest` | 중간 |

### **테스트 패턴**

- **BDD 스타일**: given-when-then 구조
- **@Nested**: 관련 테스트 그룹화
- **@DisplayName**: 가독성을 위한 한글 테스트 이름
- **팩토리 메서드**: 재사용 가능한 테스트 데이터 생성

---