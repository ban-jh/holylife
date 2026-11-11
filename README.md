# HolyLife (Faith Pathway) — Backend

설교 · QT(큐티) · P.B.S(개인성경공부) · 독서노트를 기록하고 관리하는 기독교 콘텐츠 플랫폼의 **Spring Boot 백엔드**입니다.
성경 본문 전체(개역개정/새번역/NIV)를 DB에 보관하고, QT는 Spring Batch로 매일 자동 수집합니다.

- 저장소: https://github.com/ban-jh/holylife (branch: `dev`)
- 프론트엔드: https://github.com/ban-jh/holylifeView (branch: `main`)

---

## 1. 주요 기능

| 영역 | 기능 |
|------|------|
| **대시보드** | 설교/QT/P.B.S/독서 건수, 이번 달 작성 통계, 오늘의 QT, 최근 등록 콘텐츠(탭) |
| **설교** | 목록/등록/수정/삭제, 설교자 선택(공통코드), 성경 본문 자동 조회, 공개 범위 |
| **QT** | 목록/등록/수정/삭제, 크롤링 본문 불러오기, 3개 성경역 탭, 공개 여부, 필기(손글씨) |
| **P.B.S** | Confluence형 **폴더 트리 + 페이지** 구조, 3분할 화면(트리 / 성경말씀 / 제목+내용), 자동저장, 배포(DRAFT→PUBLISHED), 드래그&드롭 이동/정렬, 참고자료 |
| **독서노트** | 목록/등록/수정/삭제, 목차·요약·느낀점 리치 에디터 |
| **성경** | 66권 전체 본문 조회(장/절 범위), 역본별 조회 |
| **시스템 관리** | 사용자, 권한 그룹(role), 메뉴(트리·드래그&드롭·아이콘), 공통코드/그룹코드 |
| **마이페이지** | 프로필, 기본 성경역본/기본 QT 출처 설정, 비밀번호 변경 |
| **배치** | QT 매일 자동 수집(생명의삶 04:00 / SUM 04:05), 수동 실행 API, 실행 이력 DB 기록 |

---

## 2. 기술 스택

| 구성 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot **4.0.0** (Web MVC, Validation, Security, Batch, JDBC) |
| Persistence | MyBatis **4.0.1** (`mybatis-spring-boot-starter`), XML Mapper |
| Database | MariaDB **10.11** |
| 인증 | Spring Security + JWT (jjwt **0.12.6**), BCrypt |
| 배치 | Spring Batch 6 (JDBC JobRepository, 스케줄러) |
| API 문서 | springdoc-openapi **2.7.0** (Swagger UI) |
| 빌드 | Maven Wrapper (`./mvnw`) |
| 웹 서버 | Nginx (프론트 정적 파일 + `/api` 리버스 프록시) |
| 운영 | systemd + watchdog (cron) |

---

## 3. 패키지 구조

```
org.shaloman.pj
├── HolylifeApplication.java
├── config/
│   ├── SecurityConfig.java              (CORS, 접근 제어, JWT 필터 체인)
│   ├── JwtUtil.java / JwtAuthenticationFilter.java
│   ├── RoleAuthorizationFilter.java     (역할별 API 접근 제어)
│   ├── MyBatisConfig.java / SwaggerConfig.java
│   ├── BatchConfig.java                 (qtCrawlJob, qtSumCrawlJob, JDBC JobRepository)
│   └── QtScheduler.java                 (매일 04:00 / 04:05)
├── common/fileupload/                   (이미지/파일 업로드)
├── content/
│   ├── bible/                           (성경 본문·책 목록 조회)
│   ├── sermon/                          (설교)
│   ├── qt/                              (QT + qt_daily 크롤링 + 배치 실행 API)
│   ├── pbs/                             (P.B.S — folder / page / section / reference)
│   └── reading/                         (독서노트)
├── system/
│   ├── user/                            (사용자 + AuthController 로그인)
│   ├── role/  permission/  menu/  code/ (권한·메뉴·공통코드)
└── nlp/date/                            (자연어 날짜 파서)
```

---

## 4. API 요약

모든 API는 `/api` 하위입니다. **`/api/auth/login`, `/api/pbs/**`, `/uploads/**`, Swagger를 제외하면 JWT Bearer 토큰이 필요**합니다.
(회원가입 `POST /api/users`는 공개가 아니라 **관리자 전용**입니다.)

### 인증 / 사용자
| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/auth/login` | 로그인 → JWT 발급 |
| GET | `/api/auth/me` | 내 정보 |
| GET/POST | `/api/users` | 목록 / 등록(회원가입) |
| GET/PUT/DELETE | `/api/users/{userId}` | 조회 / 수정 / 삭제 |
| GET/PUT | `/api/users/{userId}/settings` | 마이페이지 설정(기본 역본, QT 출처) |
| PUT | `/api/users/{userId}/password` | 비밀번호 변경 |

### 콘텐츠
| Method | Path | 설명 |
|--------|------|------|
| GET/POST | `/api/sermons` | 설교 목록 / 등록 |
| GET/PUT/DELETE | `/api/sermons/{sermonId}` | 설교 조회 / 수정 / 삭제 |
| GET/POST | `/api/qts` | QT 목록 / 등록 |
| GET/PUT/DELETE | `/api/qts/{qtId}` | QT 조회 / 수정 / 삭제 |
| GET/POST | `/api/readings` | 독서노트 목록 / 등록 |
| GET/PUT/DELETE | `/api/readings/{readingId}` | 독서노트 조회 / 수정 / 삭제 |

### 성경
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/bible/books?versionId=2` | 66권 책 목록 |
| GET | `/api/bible/verses` | 본문 조회 (`bookCode`, `chapterFrom/To`, `verseFrom/To`, `versionId`) |

### QT 자동 수집 (qt_daily)
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/qt-daily/today?source=duranno` | 오늘의 QT (기본: 생명의삶) |
| GET | `/api/qt-daily/by-date?date=&source=` | 특정일 QT |
| GET | `/api/qt-daily` | QT 목록 |
| POST · GET | `/api/qt/crawl?source=all&date=YYYY-MM-DD` | **배치 수동 실행** (duranno / sum / all) |

### P.B.S (폴더 · 페이지)
| Method | Path | 설명 |
|--------|------|------|
| GET/POST | `/api/pbs/folders` | 폴더 트리 / 생성 |
| PUT/DELETE | `/api/pbs/folders/{folderId}` | 이름 수정 / 삭제 |
| PUT | `/api/pbs/folders/reorder` | 폴더 순서 변경 |
| POST | `/api/pbs/pages` | 페이지 생성(임시 제목) |
| GET/PUT/DELETE | `/api/pbs/pages/{pageId}` | 페이지 조회 / 저장 / 삭제 |
| PUT | `/api/pbs/pages/{pageId}/content` | 제목·본문 자동저장 |
| PUT | `/api/pbs/pages/{pageId}/publish` | 배포(읽기 모드 전환) |
| PUT | `/api/pbs/pages/reorder` | 페이지 순서·폴더 이동 |
| GET/POST | `/api/pbs/pages/{pageId}/sections` | 영역별 내용(관찰/해석/적용) |
| PUT/DELETE | `/api/pbs/sections/{sectionId}` | 영역 수정 / 삭제 |

### 시스템
| Method | Path | 설명 |
|--------|------|------|
| GET/POST | `/api/roles` · `/api/permissions` | 권한 그룹 / 권한 |
| GET | `/api/permissions/role/{roleId}/menus` | 역할별 접근 메뉴 |
| GET/POST | `/api/menus` | 메뉴 (tree, order, move 지원) |
| GET/POST | `/api/group-codes` · `/api/common-codes` | 그룹코드 / 공통코드 |

### 파일
| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/files/upload?category=pbs` | 단일 업로드 (이미지) |
| POST | `/api/files/uploadMultiple` | 다중 업로드 |
| DELETE | `/api/files/delete` | 파일 삭제 |

Swagger UI: `http://<host>:38081/swagger-ui.html`

---

## 5. 데이터베이스

MariaDB `holylife` 스키마. 스키마 스크립트는 `dbscript/` 참고.

| 파일 | 내용 |
|------|------|
| `00_README.sql` | 스키마 개요 |
| `01_schema_drop.sql` | 테이블 삭제 (선택) |
| `02_schema_system.sql` | 시스템 (users, roles, menus, permissions, codes) |
| `03_schema_content.sql` | 콘텐츠 (sermons, qts, qt_daily, reading_notes, pbs_*) |
| `04_schema_bible.sql` | 성경 (bible_versions, bible_books) |
| `05_schema_batch.sql` | Spring Batch 메타 |
| `06_seed_data.sql` | 초기 데이터 |
| `07_indexes.sql` | 인덱스 |
| `batch_schema.sql` | Batch 6 공식 스키마 (네이티브 SEQUENCE) |
| `pbs_schema.sql` | P.B.S 폴더/페이지/참고자료 |

DB 초기 구성 (신규 환경):

```bash
cd dbscript
for f in 01_schema_drop.sql 02_schema_system.sql 03_schema_content.sql \
         04_schema_bible.sql 05_schema_batch.sql 06_seed_data.sql 07_indexes.sql; do
  mysql --default-character-set=utf8mb4 -u shaloman -p holylife < "$f"
done
```

> ⚠️ `mysql` 클라이언트는 `--default-character-set=utf8mb4`를 반드시 지정해야 합니다. (미지정 시 이모지 아이콘 INSERT에서 `Incorrect string value` 오류)
> 위 7개 스크립트는 신규 DB에서 순차 실행 검증 완료 (테이블 28개 + 시퀀스 3개 생성)

**콘텐츠**
- `users`, `roles`, `user_roles`, `permissions`, `menus`, `group_codes`, `common_codes`
- `sermons`, `sermon_tags`, `qts`, `qt_tags`, `qt_daily`, `reading_notes`, `tags`, `reference_resources`
- `pbs_folder`, `pbs_page`, `pbs_page_section`, `pbs_reference`, `pbs_tag_map`
- `bible_versions`, `bible_books`

**성경 본문 (`bible_books`)**

| version_id | 역본 | 절 수 |
|-----------|------|-------|
| 2 | 개역개정 | 29,888 |
| 3 | 새번역 | 29,887 |
| 6 | NIV | 31,103 |

66권 전체 수록 (book_code, chapter, verse 단위).

**Spring Batch 메타 (9개)**
`BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, `BATCH_JOB_EXECUTION_PARAMS`, `BATCH_JOB_EXECUTION_CONTEXT`, `BATCH_STEP_EXECUTION`, `BATCH_STEP_EXECUTION_CONTEXT` + 네이티브 시퀀스 3개
(`BATCH_JOB_INSTANCE_SEQ`, `BATCH_JOB_EXECUTION_SEQ`, `BATCH_STEP_EXECUTION_SEQ`)

> ⚠️ **Spring Batch 6 주의**
> - 기본 JobRepository가 인메모리(Resourceless)이므로 `BatchConfig`에서 **JDBC JobRepository를 `@Primary`로 명시**해야 실행 이력이 남습니다.
> - 시퀀스는 **테이블이 아니라 네이티브 `SEQUENCE` 객체**입니다. (`dbscript/batch_schema.sql`)

---

## 6. QT 자동 수집 배치

| Job | 대상 | 스케줄 | Step |
|-----|------|--------|------|
| `qtCrawlJob` | 생명의삶 (duranno) | 매일 **04:00** | `qtCrawlStep` |
| `qtSumCrawlJob` | SUM | 매일 **04:05** | `qtSumCrawlStep` |

- JobParameters: `job.type`, `job.date`, **`run.id`(매 실행 고유값)** → 일자별 JobInstance 충돌 방지
- 결과는 `qt_daily`에 `(qt_date, source)` UNIQUE로 업서트
- 수동 실행:
```bash
curl -X POST "http://localhost:38081/api/qt/crawl?source=all&date=2026-11-11" \
     -H "Authorization: Bearer <JWT>"
```
- 실행 이력 확인:
```sql
SELECT JOB_INSTANCE_ID, JOB_NAME, JOB_KEY FROM BATCH_JOB_INSTANCE;
SELECT JOB_EXECUTION_ID, STATUS, EXIT_CODE, CREATE_TIME FROM BATCH_JOB_EXECUTION;
```

---

## 7. 보안

- JWT 24시간 만료, `JWT_SECRET`은 환경변수 필수 (기본값 없음)
- 공개 엔드포인트: `/api/auth/login`, `/api/pbs/**`, `/uploads/**`, Swagger — 그 외 `/api/**`는 인증 필요
- 회원가입(`POST /api/users`)은 **관리자 전용** (공개 가입 불가)
- 역할: `SYS_ADMIN`(시스템 관리), `AGENT_DEV`, `GENERAL`(콘텐츠)
- `RoleAuthorizationFilter`로 `/api/users`, `/api/menus`, `/api/roles`, `/api/permissions` 등 제한
- BCrypt 비밀번호, 비밀번호 복잡도 검증, 로그인 실패 시 사용자 열거 방지 메시지
- 업로드: 파일당 10MB / 요청 50MB, MIME 화이트리스트(jpeg/png/gif/svg/webp/ico)
- CORS 허용: `http://localhost(:38080)`, `http://192.168.2.15(:38080)`, `http://123.143.232.180(:38080)`

---

## 8. 빌드 및 실행

```bash
# 개발 프로파일 (기본)
./mvnw clean package -DskipTests
java -jar target/holylife-0.0.1-SNAPSHOT.jar          # :38081

# 특정 프로파일
java -jar target/holylife-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 개발 서버
./mvnw spring-boot:run
```

- 포트: **38081** (`application.yml`)
- 프로파일 파일: `application.yml`, `application-dev.yml`, `application-local.yml`
- 운영 설정: `/opt/holylife/config/application-prod.yml`

### Docker (선택)

```bash
docker compose up -d --build
```

---

## 9. 운영 배포 (현재)

| 항목 | 경로 / 값 |
|------|-----------|
| 애플리케이션 | `/opt/holylife/holylife-0.0.1.jar` |
| 설정 | `/opt/holylife/config/application-prod.yml` |
| 로그 | `/opt/holylife/logs/holylife.log` |
| 업로드 | `/opt/holylife/uploads/pbs/` |
| 프론트 정적 | `/opt/holylife/holylifeView/` |
| 스크립트 | `/opt/holylife/sh/{start,stop,restart,watchdog,deploy-backend,deploy-frontend}.sh` |
| 서비스 | `systemd holylife` (`Type=forking`, `PIDFile`, `Restart=no`) |
| 감시 | crontab watchdog (1분 주기) |
| 웹 | Nginx **38080** → 백엔드 **38081** (`/api`, `/uploads`, `/swagger-ui` 프록시) |

```bash
sudo /opt/holylife/sh/stop.sh
sudo cp target/holylife-0.0.1-SNAPSHOT.jar /opt/holylife/holylife-0.0.1.jar
sudo /opt/holylife/sh/start.sh
```

> JAR 교체 시 반드시 **프로세스 종료를 확인한 뒤** 복사해야 합니다. 실행 중 JAR을 덮어쓰면 클래스 로딩 오류가 발생합니다.

---

## 10. 요구사항

- Java 17 이상
- MariaDB 10.11 (네이티브 SEQUENCE 지원)
- Maven Wrapper 사용 가능 (`./mvnw`)
- Nginx (운영 배포 시)
