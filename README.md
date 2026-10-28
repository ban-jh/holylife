# HolyLife (Faith Pathway)

설교, QT, PBS, 독서 노트를 관리하는 기독교 콘텐츠 플랫폼입니다.

## 기술 스택

| 구성 | 기술 |
|------|------|
| 백엔드 | Spring Boot 4.0.0, Java 17 |
| ORM | MyBatis 4.0.1 |
| 인증 | Spring Security + JWT (jjwt 0.12.6) |
| API 문서 | springdoc-openapi 2.6.0 (Swagger UI) |
| 프론트엔드 | Vue 3 + TypeScript + Vite 6 |
| 라우팅 | Vue Router 4 |
| HTTP 클라이언트 | Axios |
| 에디터 | Tiptap (RichTextEditor) |
| 데이터베이스 | MariaDB 10.11 |
| 웹 서버 | Nginx 1.24 |
| 배포 | Docker + docker-compose (Oracle Cloud 대응) |

## 패키지 구조 (도메인 그룹화)

```
org.shaloman.pj
├── HolylifeApplication.java          (메인 클래스)
├── config/                           (SecurityConfig, JwtUtil, JwtAuthenticationFilter, RoleAuthorizationFilter, MyBatisConfig, SwaggerConfig, QtScheduler)
├── common.fileupload/                (파일 업로드)
│   ├── controller/
│   └── service/
├── content/                          (콘텐츠 도메인)
│   ├── bible/                        (성경 — controller, domain, mapper)
│   ├── sermon/                       (설교 — controller, domain, dto, mapper, service)
│   ├── qt/                           (QT — controller, domain, dto, mapper, service)
│   ├── pbs/                          (PBS — controller, domain, mapper, service)
│   └── reading/                      (독서 — controller, domain, mapper, service)
├── system/                           (시스템 도메인)
│   ├── user/                         (사용자 — controller, domain, dto, mapper, service + AuthController)
│   ├── role/                         (권한 그룹 — controller, domain, dto, mapper, service)
│   ├── permission/                   (권한 — controller, domain, dto, mapper, service)
│   ├── menu/                         (메뉴 — controller, domain, dto, mapper, service)
│   └── code/                         (공통코드 — controller, domain, dto, mapper, service)
└── nlp/date/                         (자연어 날짜 파서)
```

## 프론트엔드 구조 (holylifeView)

```
src/
├── api/index.ts                      (Axios API 클라이언트 + 인터셉터)
├── composables/useAuth.ts            (인증 composable — 세션 관리)
├── router/index.ts                   (Vue Router, 22개 라우트, 가드)
├── components/
│   ├── AppSidebar.vue                (DB 기반 사이드바, 권한 필터링)
│   ├── AppHeader.vue
│   └── RichTextEditor.vue            (Tiptap 에디터)
├── views/                            (17개 화면)
│   ├── DashboardView.vue             (대시보드)
│   ├── LoginView.vue                 (로그인)
│   ├── SignupView.vue                (회원가입)
│   ├── SermonListView.vue            (설교 목록)
│   ├── SermonDetailView.vue          (설교 상세)
│   ├── QtListView.vue                (QT 목록)
│   ├── QtDetailView.vue              (QT 상세)
│   ├── PbsListView.vue               (PBS 목록)
│   ├── PbsDetailView.vue             (PBS 상세)
│   ├── ReadingListView.vue           (독서 목록)
│   ├── ReadingDetailView.vue         (독서 상세)
│   ├── MyPageView.vue                (마이페이지)
│   └── system/
│       ├── UserListView.vue          (사용자 관리)
│       ├── UserCreateView.vue        (사용자 등록)
│       ├── MenuListView.vue          (메뉴 관리 — 트리+드래그&드롭)
│       ├── PermissionListView.vue    (권한 관리)
│       └── CommonCodeView.vue        (공통코드 관리)
└── assets/css/                       (페이지별 CSS)
```

## 빌드 및 실행

### 백엔드

```bash
cd holylife
./mvnw clean compile        # 컴파일
./mvnw package -DskipTests  # JAR 빌드
./mvnw spring-boot:run      # 개발 서버 실행 (:8080)
```

### 프론트엔드

```bash
cd holylifeView
npm install                 # 의존성 설치
npm run dev                 # 개발 서버 (:5173)
npx vite build              # 프로덕션 빌드
```

### Docker (전체 서비스)

```bash
cd holylife
docker compose up -d --build    # MariaDB + 백엔드 + 프론트엔드
docker compose down             # 전체 중지
```

## 데이터베이스

- MariaDB 10.11
- 28개 테이블 (비즈니스 19 + qt_daily + bible_books + Spring Batch 8)
- 한국어 코멘트 적용 완료

## 인증 시스템

- Spring Security + JWT (24시간 토큰)
- 역할별 API 접근 제어 (RoleAuthorizationFilter)
- SYS_ADMIN: 시스템 관리 API 접근 가능
- GENERAL: 콘텐츠 API만 접근
- 세션: localStorage, 날짜 변경 시 1시간 초과면 만료

## 요구사항

- Java 17 이상
- Node.js 22 이상
- Maven (프로젝트에 포함된 Maven Wrapper 사용 가능)
- Docker + Docker Compose (Docker 배포 시)

## GitHub 저장소

| 프로젝트 | 저장소 | 브랜치 |
|----------|--------|--------|
| holylife (백엔드) | https://github.com/ban-jh/holylife | dev |
| holylifeView (프론트엔드) | https://github.com/ban-jh/holylifeView | main |