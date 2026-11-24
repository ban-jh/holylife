# holylife Backend Dockerfile
# Spring Boot JAR 빌드 + 실행
# Multi-stage build — 빌드 단계와 실행 단계 분리

# ── Stage 1: Build ──
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# pom.xml 먼저 복사 (의존성 캐싱)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 소스 복사 및 빌드
COPY src ./src
RUN mvn package -q -DskipTests

# ── Stage 2: Runtime ──
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /build/target/holylife-*-SNAPSHOT.jar app.jar

EXPOSE 8080

# 프로파일과 DB 접속 정보는 실행 환경에서 주입
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
