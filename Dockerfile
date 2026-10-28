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

# DB 덤프 복사 (초기화용)
COPY docker/init-db.sql /docker-entrypoint-initdb.d/init-db.sql

# application-prod.yml 복사
COPY src/main/resources/application.yml /app/config/application.yml

EXPOSE 8080

# Spring Boot 실행 — Docker 환경에 맞게 DB 호스트를 mariadb로 설정
ENTRYPOINT ["java", "-jar", "/app/app.jar", \
  "--spring.profiles.active=prod", \
  "--spring.config.additional-location=file:/app/config/", \
  "--spring.datasource.url=jdbc:mariadb://mariadb:3306/holylife", \
  "--spring.datasource.username=shaloman", \
  "--spring.datasource.password=123321"]