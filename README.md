# spring-boot-monolithic-starter (README)

> 도메인 중심으로 설계된 Spring Boot 모놀리식 애플리케이션 템플릿 프로젝트입니다.
> 

## 기술 스택

- Java 21
- Spring Boot 3.5.x (Web, JPA, Validation, Actuator 포함)
- MySQL (로컬 개발용 spring-boot-docker-compose를 통한 Docker Compose)
- Lombok
- SpringDoc OpenAPI (Swagger UI)

## 가이드 문서

### 📚 아키텍처 & 설계

> DDD 기반 스프링부트 애플리케이션을 위한 아키텍처 컨벤션 전략 가이드입니다.
> 

자세한 내용은 [ARCHITECTURE](docs/ARCHITECTURE.md) 문서를 참고해 주세요.

- 패키지 구조 및 도메인 중심 조직
- 레이어 책임 및 설계 원칙
- Entity, Value Object, Repository 패턴
- DTO 및 예외 처리
- 명명 규칙 (클래스 및 메서드)
- 각 레이어별 코드 예제

**주요 내용:**

| 레이어 | 책임 | 참조 가능 |
| --- | --- | --- |
| **Controller** | HTTP 처리, 검증 | Application Service만 |
| **Application Service** | 사용 사례 조율 | Repository, Domain Service |
| **Domain Service** | 비즈니스 규칙 | Repository, Model |
| **Model** (Entity/VO) | 핵심 비즈니스 로직 | Value Objects, Enums |

### 🧪 테스팅

> DDD 기반 스프링부트 애플리케이션을 위한 테스트 전략 가이드입니다.
> 

자세한 내용은 [COMMITS](docs/COMMITS.md) 문서를 참고해 주세요.

- 테스트 피라미드 및 레이어별 테스팅 접근 방식
- 단위 테스트: Model, Domain Service, Application Service
- 통합 테스트: Repository, Controller
- 테스트 패턴: BDD 스타일, @Nested, @DisplayName
- 각 테스트 유형별 완전한 코드 예제
- 특정 테스트 실행 및 리포트 보기

### 📝 커밋 & 버전 관리

> Conventional Commits을 기반으로한 커밋 컨벤션 전략 가이드입니다.
> 

자세한 내용은 [COMMITS](docs/COMMITS.md) 문서를 참고해 주세요.

- 커밋 메시지 구조 및 유형 (feat, fix, docs, refactor 등)
- 범위 및 주요 변경 사항
- 다양한 커밋 시나리오 예제
- 버전 관리 및 변경 로그 생성과의 통합

**커밋 형식:**

```
<type>(<scope>): <description>

[선택적 본문]

[선택적 바닥글]
```

예제:

- `feat(auth): JWT 토큰 만료 처리`
- `fix(api): 검색 결과가 없을 때 404 처리`
- `docs: README 설치 가이드 업데이트`
