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
# **Testing Guide**

> DDD 기반 스프링부트 애플리케이션을 위한 테스트 전략 가이드입니다.
>

---

## **테스트 피라미드**

```
        /\
       /  \        E2E 테스트 (선택)
      /----\
     /      \      통합 테스트 (Controller, Repository)
    /--------\
   /          \    단위 테스트 (Model, Service)
  --------------

```

- **단위 테스트**: 가장 많이 작성, 빠른 피드백
- **통합 테스트**: 컴포넌트 간 상호작용 검증
- **E2E 테스트**: 전체 흐름 검증 (선택적)

---

## **레이어별 테스트 전략**

| **레이어** | **테스트 종류** | **어노테이션** | **DB** | **Spring Context** |
| --- | --- | --- | --- | --- |
| Model (Entity, VO) | 단위 테스트 | 없음 | X | X |
| Domain Service | 단위 테스트 | 없음 | X | X |
| Application Service | 단위 테스트 | `@ExtendWith(MockitoExtension.class)` | X | X |
| Repository | 슬라이스 테스트 | `@DataJpaTest` | H2 | 부분 |
| Controller | 슬라이스 테스트 | `@WebMvcTest` | X | 부분 |

---

## **1. Model 테스트 (Entity, Value Object) (✅ 반드시 테스트)**

### **테스트 포인트**

- 생성 규칙 (유효성 검증)
- 상태 변경 메서드
- 비즈니스 규칙 위반 시 예외
- 동등성 비교 (Value Object)

### **특징**

- 순수 Java 단위 테스트
- Spring Context 불필요 → **가장 빠름**
- 비즈니스 규칙 검증에 집중

### **Value Object 테스트 예시**

```java
@DisplayName("Money 값 객체")
class MoneyTest {

    @Nested
    @DisplayName("생성")
    class Creation {

        @Test
        @DisplayName("양수 금액으로 생성할 수 있다")
        void createWithPositiveAmount() {
            Money money = Money.of(BigDecimal.valueOf(1000));

            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("음수 금액으로 생성하면 예외가 발생한다")
        void createWithNegativeAmountThrowsException() {
            assertThatThrownBy(() -> Money.of(BigDecimal.valueOf(-1000)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
```

### **Entity 테스트 예시**

```java
@DisplayName("Product 엔티티")
class ProductTest {

    @Nested
    @DisplayName("재고 관리")
    class StockManagement {

        @Test
        @DisplayName("재고를 추가할 수 있다")
        void addStock() {
            Product product = createProduct(100);

            product.addStock(50);

            assertThat(product.getStockQuantity()).isEqualTo(150);
        }

        @Test
        @DisplayName("재고보다 많은 수량을 차감하면 예외가 발생한다")
        void removeStockExceedingQuantityThrowsException() {
            Product product = createProduct(100);

            assertThatThrownBy(() -> product.removeStock(150))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }
    }

    private Product createProduct(int stockQuantity) {
        return Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), stockQuantity);
    }
}
```

---

## **2. Domain Service 테스트**

### 테스트 포인트

- 비즈니스 규칙 검증 (calculateTotalPrice, reserveStock 등)
- 도메인 규칙 위반 시 예외 발생
- 여러 Entity를 사용하는 복잡한 로직

### **특징**

- 순수 Java 단위 테스트
- 의존성이 있어도 실제 객체 사용 가능 (가벼운 경우)

### **예시**

```java
@DisplayName("StockService 도메인 서비스")
class StockServiceTest {

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService();
    }

    @Nested
    @DisplayName("재고 예약")
    class ReserveStock {

        @Test
        @DisplayName("충분한 재고가 있으면 예약할 수 있다")
        void reserveStockSuccessfully() {
            Product product = createProduct(100);

            stockService.reserveStock(product, 30);

            assertThat(product.getStockQuantity()).isEqualTo(70);
        }

        @Test
        @DisplayName("재고가 부족하면 예외가 발생한다")
        void reserveStockWithInsufficientStock() {
            Product product = createProduct(10);

            assertThatThrownBy(() -> stockService.reserveStock(product, 20))
                    .isInstanceOf(ProductOutOfStockException.class);
        }
    }
}
```

---

## **3. Application Service 테스트 (⚠️ 선택적으로 테스트)**

### 테스트 포인트

- Repository는 Mock 처리
- 중요한 비즈니스 플로우만 테스트
- 트랜잭션 동작 검증

### **특징**

- Mockito를 사용한 단위 테스트
- Repository, Domain Service를 Mock 처리
- 유즈케이스 흐름 검증

### **예시**

```java
@DisplayName("ProductApplicationService")
@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @InjectMocks
    private ProductApplicationService productApplicationService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockService stockService;

    @Test
    @DisplayName("상품을 생성할 수 있다")
    void createProduct() {
        // given
        CreateProductRequest request = new CreateProductRequest(
                "새 상품", "설명", BigDecimal.valueOf(10000), 100
        );
        Product savedProduct = Product.create(
                request.name(), request.description(), request.price(), request.stockQuantity()
        );
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        // when
        ProductResponse response = productApplicationService.createProduct(request);

        // then
        assertThat(response.name()).isEqualTo("새 상품");
        then(productRepository).should().save(any(Product.class));
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
    void getProductNotFound() {
        // given
        Long productId = 999L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productApplicationService.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
```

### **BDDMockito 패턴**

```java
// Stubbing
given(repository.findById(id)).willReturn(Optional.of(entity));

// Verification
then(repository).should().save(any(Entity.class));
then(service).should(never()).doSomething();
```

---

## **4. Repository 테스트**

### **테스트 포인트**

- CRUD 동작 확인
- 커스텀 쿼리 메서드 검증
- 연관관계 매핑 확인

### **특징**

- `@DataJpaTest` 사용 → JPA 관련 빈만 로드
- H2 인메모리 데이터베이스 사용
- 트랜잭션 자동 롤백

### **예시**

```java
@DisplayName("ProductRepository 통합 테스트")
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장하고 조회할 수 있다")
    void saveAndFind() {
        // given
        Product product = Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), 100);

        // when
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("테스트 상품");
    }

    @Test
    @DisplayName("상태별로 상품을 조회할 수 있다")
    void findByStatus() {
        // given
        Product availableProduct = Product.create("판매중 상품", "설명", BigDecimal.valueOf(10000), 100);
        Product discontinuedProduct = Product.create("판매중지 상품", "설명", BigDecimal.valueOf(20000), 50);
        discontinuedProduct.discontinue();

        productRepository.save(availableProduct);
        productRepository.save(discontinuedProduct);

        // when
        List<Product> availableProducts = productRepository.findByStatus(ProductStatus.AVAILABLE);

        // then
        assertThat(availableProducts).hasSize(1);
        assertThat(availableProducts.get(0).getName()).isEqualTo("판매중 상품");
    }
}
```

---

## **5. Controller 테스트**

### **테스트 포인트**

- HTTP 상태 코드
- 요청 유효성 검증 (`@Valid`)
- 응답 JSON 구조
- 예외 처리 (GlobalExceptionHandler)

### **특징**

- `@WebMvcTest` 사용 → Web Layer만 로드
- MockMvc로 HTTP 요청/응답 테스트
- Application Service를 Mock 처리

### **예시**

```java
@DisplayName("ProductController 통합 테스트")
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductApplicationService productApplicationService;

    @Test
    @DisplayName("상품을 생성할 수 있다")
    void createProduct() throws Exception {
        // given
        CreateProductRequest request = new CreateProductRequest(
                "새 상품", "상품 설명", BigDecimal.valueOf(10000), 100
        );
        ProductResponse response = createProductResponse(1L, "새 상품", BigDecimal.valueOf(10000), 100);
        given(productApplicationService.createProduct(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("새 상품"));
    }

    @Test
    @DisplayName("상품명이 없으면 400 에러가 발생한다")
    void createProductWithoutName() throws Exception {
        // given
        CreateProductRequest request = new CreateProductRequest(
                "", "설명", BigDecimal.valueOf(10000), 100
        );

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 404 에러가 발생한다")
    void getProductNotFound() throws Exception {
        // given
        Long productId = 999L;
        given(productApplicationService.getProduct(productId))
                .willThrow(new ProductNotFoundException(productId));

        // when & then
        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("P001"));
    }
}
```

---

## **테스트 작성 패턴**

### **1. given-when-then (BDD 스타일)**

```java
@Test
void testMethod() {
    // given - 테스트 준비
    Product product = createProduct(100);

    // when - 테스트 실행
    product.addStock(50);

    // then - 결과 검증
    assertThat(product.getStockQuantity()).isEqualTo(150);
}
```

### **2. @Nested로 테스트 그룹화**

```java
@DisplayName("Product 엔티티")
class ProductTest {

    @Nested
    @DisplayName("생성")
    class Creation { ... }

    @Nested
    @DisplayName("재고 관리")
    class StockManagement { ... }

    @Nested
    @DisplayName("상태 변경")
    class StatusChange { ... }
}
```

### **3. @DisplayName으로 한글 테스트명**

```java
@Test
@DisplayName("재고보다 많은 수량을 차감하면 예외가 발생한다")
void removeStockExceedingQuantityThrowsException() { ... }
```

### **4. 팩토리 메서드로 테스트 데이터 생성**

```java
private Product createProduct(int stockQuantity) {
    return Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), stockQuantity);
}

private ProductResponse createProductResponse(Long id, String name, BigDecimal price, Integer stockQuantity) {
    return new ProductResponse(id, name, "설명", price, stockQuantity, ProductStatus.AVAILABLE, true, LocalDateTime.now(), null);
}
```

---

## **테스트 실행 명령어**

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "*.ProductTest"

# 특정 테스트 메서드 실행
./gradlew test --tests "*.ProductTest.createProduct"

# Nested 클래스 테스트 실행
./gradlew test --tests "*.ProductTest\$Creation"

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

---

## **테스트 의존성**

```groovy
dependencies {
    // 테스트 기본
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // H2 인메모리 DB (Repository 테스트용)
    testRuntimeOnly 'com.h2database:h2'
}
```

### **spring-boot-starter-test 포함 라이브러리**

- JUnit 5
- AssertJ
- Mockito
- JSONPath
- Spring Test / Spring Boot Test

---

## **테스트 설정 파일**

### **src/test/resources/application.properties**

```properties
spring.application.name=spring-boot-monolithic-starter

# H2 Database for Testing
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```