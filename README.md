# HolyLife

Spring Boot 4.0.0 / Java 17 기반 프로젝트입니다.

## 패키지 구조

```
org.shaloman.pj
├── HolylifeApplication.java   (메인 클래스)
├── controller
├── service
├── repository
├── domain
└── dto
```

## 기술 스택

- Java 17
- Spring Boot 4.0.0
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Lombok
- Spring DevTools

## 빌드 및 실행

```bash
./mvnw clean compile        # 컴파일
./mvnw spring-boot:run      # 실행
./mvnw test                 # 테스트
```

## 요구사항

- Java 17 이상
- Maven (프로젝트에 포함된 Maven Wrapper 사용 가능)