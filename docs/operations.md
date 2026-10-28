# Faith Pathway (Holylife) 운영 설명서

## 1. 시스템 구성

```
외부 사용자 (브라우저)
    │
    ▼ HTTP :80
┌──────────────────────────────────────┐
│         Nginx (리버스 프록시)          │
│  /               → Vue.js 정적 파일    │
│  /api/*          → Spring Boot :8080  │
│  /swagger-ui/*   → Spring Boot :8080  │
│  /v3/api-docs    → Spring Boot :8080  │
└──────────┬───────────────────────────┘
           │
           ▼ HTTP :8080
┌──────────────────────────────────────┐
│    Spring Boot (JAR)                  │
│    RESTful API + Swagger + MyBatis    │
│    Spring Security + JWT 인증          │
└──────────┬───────────────────────────┘
           │
           ▼ TCP :3306
┌──────────────────────────────────────┐
│       MariaDB (bind 0.0.0.0:3306)    │
└──────────────────────────────────────┘
```

### 1.1 인증 구성

| 항목 | 내용 |
|---|---|
| 인증 프레임워크 | Spring Security + JWT (jjwt 0.12.6) |
| 토큰 유효기간 | 24시간 |
| API 접근 제어 | RoleAuthorizationFilter (역할 기반) |
| 세션 관리 | localStorage + 날짜 변경 감지 + 1시간 타임아웃 |
| 기본 관리자 | admin@holylife.local / admin123 |

## 2. 배포 경로

```
/opt/holylife/
├── holylife-0.0.1.jar              ← Spring Boot JAR 실행 파일
├── holylife.conf                   ← Nginx 사이트 설정
├── holylife.service                ← systemd 서비스 파일
├── config/
│   └── application-prod.yml        ← 운영 설정 (DB, MyBatis, Swagger, 로그)
├── logs/
│   ├── holylife.log                ← 애플리케이션 로그
│   └── holylife.pid                ← PID 파일
├── sh/
│   ├── start.sh                    ← 서비스 시작
│   ├── stop.sh                     ← 서비스 중지
│   ├── restart.sh                  ← 서비스 재시작
│   ├── deploy-backend.sh           ← 백엔드 배포 스크립트
│   └── deploy-frontend.sh          ← 프론트엔드 배포 스크립트
└── holylifeView/                   ← Vue.js 빌드 결과 (Nginx 서빙)
    ├── index.html
    └── assets/
```

## 3. 소스 코드 경로

```
/home/jhban/holylife/
├── holylife/                       ← Spring Boot 백엔드 (GitHub: ban-jh/holylife, branch: dev)
│   ├── src/main/java/org/shaloman/pj/
│   │   ├── config/                 ← SecurityConfig, JwtUtil, JwtAuthenticationFilter,
│   │   │                              RoleAuthorizationFilter, MyBatisConfig, SwaggerConfig,
│   │   │                              QtScheduler
│   │   ├── common/fileupload/      ← controller, service
│   │   ├── content/
│   │   │   ├── bible/              ← controller, domain, mapper
│   │   │   ├── sermon/             ← controller, domain, dto, mapper, service
│   │   │   ├── qt/                 ← controller, domain, dto, mapper, service
│   │   │   ├── pbs/                ← controller, domain, mapper, service
│   │   │   └── reading/            ← controller, domain, mapper, service
│   │   ├── system/
│   │   │   ├── user/               ← controller (AuthController 포함), domain, dto, mapper, service
│   │   │   ├── role/               ← controller, domain, dto, mapper, service
│   │   │   ├── permission/         ← controller, domain, dto, mapper, service
│   │   │   ├── menu/               ← controller, domain, dto, mapper, service
│   │   │   └── code/               ← controller, domain, dto, mapper, service
│   │   └── nlp/date/               ← NaturalDateParser
│   ├── src/main/resources/
│   │   ├── application.yml         ← 개발용 설정
│   │   └── mapper/                 ← MyBatis XML 매퍼
│   ├── pom.xml
│   ├── Dockerfile                  ← 백엔드 컨테이너 이미지
│   └── docker-compose.yml          ← 전체 서비스 구성 (DB + 백엔드 + 프론트엔드)
│
└── holylifeView/                   ← Vue.js 프론트엔드 (GitHub: ban-jh/holylifeView, branch: main)
    ├── src/
    │   ├── api/index.ts            ← Axios API 클라이언트
    │   ├── router/index.ts         ← Vue Router (22개 라우트)
    │   ├── views/                  ← 17개 화면 컴포넌트
    │   │   ├── DashboardView.vue
    │   │   ├── LoginView.vue
    │   │   ├── SignupView.vue
    │   │   ├── SermonListView.vue
    │   │   ├── SermonDetailView.vue
    │   │   ├── QtListView.vue
    │   │   ├── QtDetailView.vue
    │   │   ├── PbsListView.vue
    │   │   ├── PbsDetailView.vue
    │   │   ├── ReadingListView.vue
    │   │   ├── ReadingDetailView.vue
    │   │   ├── MyPageView.vue
    │   │   └── system/
    │   │       ├── UserListView.vue
    │   │       ├── UserCreateView.vue
    │   │       ├── MenuListView.vue
    │   │       ├── PermissionListView.vue
    │   │       └── CommonCodeView.vue
    │   └── assets/main.css         ← Warm 톤 전역 스타일
    ├── Dockerfile                  ← 프론트엔드 컨테이너 이미지
    ├── vite.config.ts              ← Vite 설정 (개발 프록시 :8080)
    └── package.json
```

### 3.1 백엔드 패키지 구성 (총 82개 Java 파일)

| 패키지 | 구성 요소 |
|---|---|
| `org.shaloman.pj.config` | SecurityConfig, JwtUtil, JwtAuthenticationFilter, RoleAuthorizationFilter, MyBatisConfig, SwaggerConfig, QtScheduler |
| `org.shaloman.pj.common.fileupload` | controller, service |
| `org.shaloman.pj.content.bible` | controller, domain, mapper |
| `org.shaloman.pj.content.sermon` | controller, domain, dto, mapper, service |
| `org.shaloman.pj.content.qt` | controller, domain, dto, mapper, service |
| `org.shaloman.pj.content.pbs` | controller, domain, mapper, service |
| `org.shaloman.pj.content.reading` | controller, domain, mapper, service |
| `org.shaloman.pj.system.user` | controller (AuthController 포함), domain, dto, mapper, service |
| `org.shaloman.pj.system.role` | controller, domain, dto, mapper, service |
| `org.shaloman.pj.system.permission` | controller, domain, dto, mapper, service |
| `org.shaloman.pj.system.menu` | controller, domain, dto, mapper, service |
| `org.shaloman.pj.system.code` | controller, domain, dto, mapper, service |
| `org.shaloman.pj.nlp.date` | NaturalDateParser |

## 4. 시스템 설정 파일

| 파일 | 위치 | 설명 |
|---|---|---|
| systemd 서비스 | `/etc/systemd/system/holylife.service` | 서비스 등록 (부팅 시 자동 시작) |
| Nginx 사이트 | `/etc/nginx/sites-available/holylife` | 리버스 프록시 설정 |
| Nginx 심볼릭링크 | `/etc/nginx/sites-enabled/holylife` | 사이트 활성화 |
| Docker Compose | `/home/jhban/holylife/holylife/docker-compose.yml` | 컨테이너 서비스 구성 |

## 5. 서비스 관리

### 5.1 서비스 시작

```bash
# systemd로 시작
sudo systemctl start holylife

# 스크립트로 시작
/opt/holylife/sh/start.sh
```

### 5.2 서비스 중지

```bash
# systemd로 중지
sudo systemctl stop holylife

# 스크립트로 중지
/opt/holylife/sh/stop.sh
```

### 5.3 서비스 재시작

```bash
# systemd로 재시작
sudo systemctl restart holylife

# 스크립트로 재시작
/opt/holylife/sh/restart.sh
```

### 5.4 서비스 상태 확인

```bash
# 서비스 상태
sudo systemctl status holylife

# 활성 여부만 확인
systemctl is-active holylife

# 부팅 시 자동 시작 여부
systemctl is-enabled holylife
```

### 5.5 Nginx 관리

```bash
# Nginx 시작 / 중지 / 재시작
sudo systemctl start nginx
sudo systemctl stop nginx
sudo systemctl restart nginx

# Nginx 설정 문법 검사
sudo nginx -t

# Nginx 상태 확인
sudo systemctl status nginx
```

### 5.6 Docker 서비스 관리

```bash
# 컨테이너 전체 기동
cd /home/jhban/holylife/holylife
docker compose up -d

# 컨테이너 상태 확인
docker compose ps

# 컨테이너 중지
docker compose down

# 로그 확인
docker compose logs -f
```

### 5.7 로그 확인

```bash
# 애플리케이션 로그 (실시간)
tail -f /opt/holylife/logs/holylife.log

# 애플리케이션 로그 (최근 50줄)
tail -50 /opt/holylife/logs/holylife.log

# Nginx 접근 로그
sudo tail -f /var/log/nginx/access.log

# Nginx 에러 로그
sudo tail -f /var/log/nginx/error.log

# MariaDB 로그
sudo tail -f /var/log/mysql/error.log
```

## 6. 배포 방법

### 6.1 백엔드 배포 (Spring Boot)

```bash
/opt/holylife/sh/deploy-backend.sh
```

수행 과정:
1. Maven 빌드 (`./mvnw clean package -DskipTests`)
2. 기존 서비스 중지
3. JAR 파일 복사 (`/opt/holylife/holylife-0.0.1.jar`)
4. 서비스 시작
5. API 헬스 체크 (`http://localhost:8080/api/users`)

수동 배포 (단계별):
```bash
# 1. 빌드
cd /home/jhban/holylife/holylife
./mvnw clean package -DskipTests

# 2. 서비스 중지
sudo systemctl stop holylife

# 3. JAR 복사
cp target/holylife-0.0.1-SNAPSHOT.jar /opt/holylife/holylife-0.0.1.jar

# 4. 설정 파일 업데이트 (필요한 경우)
cp src/main/resources/application-prod.yml /opt/holylife/config/application-prod.yml

# 5. 서비스 시작
sudo systemctl start holylife

# 6. 확인
curl http://localhost:8080/api/users
```

### 6.2 프론트엔드 배포 (Vue.js)

```bash
/opt/holylife/sh/deploy-frontend.sh
```

수행 과정:
1. Vite 빌드 (`npx vite build`)
2. 기존 파일 백업 (`holylifeView.bak.YYYYMMDDHHMMSS`)
3. 빌드 결과 복사 (`/opt/holylife/holylifeView/`)
4. Nginx 재시작
5. 헬스 체크 (`http://localhost:80/`)

수동 배포 (단계별):
```bash
# 1. 빌드
cd /home/jhban/holylife/holylifeView
npx vite build

# 2. 기존 파일 백업
sudo mv /opt/holylife/holylifeView /opt/holylife/holylifeView.bak.$(date +%Y%m%d%H%M%S)

# 3. 빌드 결과 복사
sudo mkdir -p /opt/holylife/holylifeView
sudo cp -r dist/* /opt/holylife/holylifeView/
sudo chown -R jhban:jhban /opt/holylife/holylifeView

# 4. Nginx 재시작
sudo systemctl restart nginx

# 5. 확인
curl http://localhost:80/
```

### 6.3 전체 배포 (백엔드 + 프론트엔드)

```bash
# 백엔드 먼저 배포
/opt/holylife/sh/deploy-backend.sh

# 프론트엔드 배포
/opt/holylife/sh/deploy-frontend.sh
```

### 6.4 Docker 배포

```bash
# docker-compose로 전체 서비스 기동
cd /home/jhban/holylife/holylife
docker compose up -d --build
```

백엔드 Dockerfile과 프론트엔드 Dockerfile을 각각 사용하여 컨테이너 이미지를 빌드합니다.
`docker-compose.yml`은 MariaDB, 백엔드, 프론트엔드를 통합하여 관리합니다.

### 6.5 Oracle Cloud Free Tier 배포

Oracle Cloud Free Tier 인스턴스에서 Docker 기반 배포를 지원합니다.
- 인스턴스 내에서 `docker compose up -d` 실행
- 방화벽 규칙에서 80, 8080, 3306 포트 개방
- 필요에 따라 Nginx 리버스 프록시 추가 구성

## 7. 개발 환경 실행

### 7.1 백엔드 개발 서버

```bash
cd /home/jhban/holylife/holylife
./mvnw spring-boot:run
# → http://localhost:8080
# → Swagger: http://localhost:8080/swagger-ui.html
```

### 7.2 프론트엔드 개발 서버

```bash
cd /home/jhban/holylife/holylifeView
npm run dev
# → http://localhost:3000
# → API 호출은 자동으로 :8080으로 프록시 (vite.config.ts)
```

### 7.3 데이터베이스 접속

```bash
# 로컬 접속
mysql -u shaloman -p123321 holylife

# 외부 네트워크에서 접속 (bind-address 0.0.0.0)
mysql -h 192.168.2.15 -P 3306 -u shaloman -p123321 holylife
```

## 8. 접속 URL

### 로컬 (서버 내부)

| 항목 | URL |
|---|---|
| Vue.js 화면 | http://localhost/ |
| Swagger UI | http://localhost/swagger-ui.html |
| OpenAPI JSON | http://localhost/v3/api-docs |
| REST API | http://localhost/api/users |
| Spring Boot 직접 | http://localhost:8080/api/users |

### 내부 네트워크

| 항목 | URL |
|---|---|
| Vue.js 화면 | http://192.168.2.15/ |
| Swagger UI | http://192.168.2.15/swagger-ui.html |
| REST API | http://192.168.2.15/api/users |
| MariaDB (외부 접속) | 192.168.2.15:3306 |

### 외부 접속

| 항목 | URL |
|---|---|
| Vue.js 화면 | http://123.143.232.180/ (포트 포워딩 필요) |

외부 접속을 위해서는 라우터/공유기에서 80번 포트를 서버(192.168.2.15)로 포트 포워딩 설정이 필요합니다.

## 9. 데이터베이스

### 9.1 접속 정보

| 항목 | 값 |
|---|---|
| 호스트 (로컬) | localhost |
| 호스트 (외부) | 192.168.2.15 |
| 포트 | 3306 |
| 데이터베이스 | holylife |
| 사용자 | shaloman |
| 비밀번호 | 123321 |
| bind-address | 0.0.0.0 (외부 접속 허용) |

### 9.2 테이블 목록 (총 28개)

#### 비즈니스 테이블 (19개)

```
users, roles, user_roles,
menus, permissions,
group_codes, common_codes, tags,
sermons, sermon_tags,
pbs, pbs_tags,
qts, qt_tags,
reading_notes, reference_resources,
bible_versions, bible_books
```

#### QT 일일 크롤링 테이블 (1개)

```
qt_daily
```

#### Spring Batch 메타 테이블 (8개)

```
BATCH_JOB_INSTANCE, BATCH_JOB_EXECUTION, BATCH_JOB_EXECUTION_PARAMS,
BATCH_STEP_EXECUTION, BATCH_STEP_EXECUTION_CONTEXT,
BATCH_JOB_EXECUTION_CONTEXT, BATCH_JOB_SEQ, BATCH_STEP_EXECUTION_SEQ
```

### 9.3 QT 자동 크롤링

| 항목 | 내용 |
|---|---|
| 스케줄러 | QtScheduler |
| 실행 시간 | 매일 04:00 |
| 크롤링 소스 | duranno (두란노 QT) |
| 저장 테이블 | qt_daily |

### 9.4 스키마 초기화

```bash
mysql -u shaloman -p123321 holylife < /home/jhban/holylife/holylife/docs/schema.sql
```

## 10. GitHub 저장소

| 프로젝트 | 저장소 | 브랜치 |
|---|---|---|
| holylife (백엔드) | https://github.com/ban-jh/holylife | dev |
| holylifeView (프론트엔드) | https://github.com/ban-jh/holylifeView | main |

## 11. 문제 해결

### 11.1 서비스가 시작되지 않는 경우

```bash
# 로그 확인
tail -50 /opt/holylife/logs/holylife.log

# 포트 충돌 확인
ss -tlnp | grep 8080

# 기존 프로세스 종료
kill $(cat /opt/holylife/logs/holylife.pid)
# 또는
kill $(pgrep -f holylife)
```

### 11.2 Nginx 500 에러

```bash
# Nginx 에러 로그 확인
sudo tail -20 /var/log/nginx/error.log

# 권한 문제인 경우
sudo chmod o+x /opt/holylife
sudo chmod -R o+r /opt/holylife/holylifeView
sudo chmod o+x /opt/holylife/holylifeView
```

### 11.3 API는 되는데 화면이 안 나오는 경우

```bash
# Nginx 상태 확인
sudo systemctl status nginx

# Nginx 재시작
sudo systemctl restart nginx

# Vue.js 빌드 결과 확인
ls /opt/holylife/holylifeView/index.html
```

### 11.4 화면은 되는데 API가 안 되는 경우

```bash
# Spring Boot 서비스 상태
sudo systemctl status holylife

# Spring Boot 재시작
sudo systemctl restart holylife

# MariaDB 상태
sudo systemctl status mariadb

# DB 연결 확인
mysql -u shaloman -p123321 holylife -e "SELECT 1;"
```

### 11.5 JWT 인증 관련

```bash
# 로그인 테스트
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@holylife.local","password":"admin123"}'

# 토큰과 함께 API 호출
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/users
```

### 11.6 포트 확인

```bash
# 80 (Nginx), 8080 (Spring Boot), 3306 (MariaDB)
ss -tlnp | grep -E '80|8080|3306'
```

### 11.7 Docker 컨테이너 문제

```bash
# 컨테이너 상태 확인
docker compose ps

# 컨테이너 로그 확인
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f db

# 컨테이너 재기동
docker compose restart
```

## 12. 기술 스택

| 구성 | 기술 |
|---|---|
| 백엔드 | Spring Boot 4.0.0, Java 17 |
| 인증/보안 | Spring Security, JWT (jjwt 0.12.6) |
| ORM | MyBatis 4.0.1 |
| 배치 | Spring Batch (QT 일일 크롤링) |
| API 문서 | springdoc-openapi 2.6.0 (Swagger UI) |
| 프론트엔드 | Vue 3 + TypeScript + Vite 6 |
| 라우팅 | Vue Router 4 (22개 라우트, 17개 화면) |
| HTTP 클라이언트 | Axios |
| 데이터베이스 | MariaDB 10.11 |
| 웹 서버 | Nginx 1.24 |
| 컨테이너 | Docker + Docker Compose |
| 서비스 관리 | systemd |
| 빌드 | Maven (백엔드), npm/Vite (프론트엔드) |
| 클라우드 | Oracle Cloud Free Tier 지원 |