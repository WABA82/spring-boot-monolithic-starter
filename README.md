# spring-boot-monolithic-starter

![Java](https://img.shields.io/badge/Java-21-blue?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-green?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.4-orange?style=flat-square)
![Redis](https://img.shields.io/badge/Redis-9.0.1-red?style=flat-square)

**DDD 기반 모놀리식 Spring Boot 스타터 프로젝트**입니다.

개발 경험이 쌓이면서 단순히 기능을 구현하는 것보다, **표준과 정책을 일관되게 지키며 유지보수 가능한 코드를 작성하는 것**이 중요하다고 느끼게 되었습니다.

이를 계기로 **기본 패키지 구조**, **Spring Boot 컴포넌트 간 의존성 규칙**, **클래스 네이밍 컨벤션**, **커밋 컨벤션** 등 개인적으로 추구하는 개발 규칙과 프로젝트 표준을 정리하고, 이를 실제 코드 구조로 구현한 프로젝트입니다.

CQRS나 헥사고날 아키텍처처럼 복잡한 구조를 무리하게 도입하기보다는, **직관적이고 이해하기 쉬우면서도 실무적으로 활용 가능한 아키텍처**를 목표로 설계하고 있습니다.

현재는 아키텍처 구조, 테스트 전략, 커밋 가이드 등이 문서화되어 있으며, 앞으로도 개인적인 프레임워크 형태로 지속적으로 개선 및 확장해나갈 예정입니다.

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
