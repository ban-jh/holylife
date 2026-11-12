# API 명세서

> 최종 갱신: 2026-11-11 · 백엔드 `holylife` (Spring Boot 4.0) 기준
> 실시간 문서: `http://<host>:38081/swagger-ui.html`

---

## 0. 공통 규칙

### Base URL
```
개발: http://localhost:38081/api
운영: http://<host>:38080/api      (Nginx 프록시)
```

### 인증
- `Authorization: Bearer <JWT>` (로그인 응답의 `token`)
- JWT 만료: **24시간** (`jwt.expiration=86400000`)
- 면제 경로: `/api/auth/login`, `/api/pbs/**`, `/uploads/**`, Swagger

### CORS (허용 출처)
| 구분 | Origin |
|------|--------|
| 웹 | `http://localhost(:38080)`, `http://192.168.2.15(:38080)`, `http://123.143.232.180(:38080)` |
| 모바일 앱 (Capacitor) | `https://localhost`, `capacitor://localhost`, `ionic://localhost` |

- `OPTIONS` preflight는 **인증 없이 통과** (`HttpMethod.OPTIONS → permitAll`)
- 허용 메서드: GET, POST, PUT, DELETE, PATCH, **OPTIONS**
- 허용 헤더: `*` (요청 헤더를 그대로 echo), 노출 헤더: `Authorization`, `Content-Disposition`
- `allowCredentials: true` + `allowedOriginPatterns` 사용 → 허용 목록 이외 출처는 **403**

> ⚠️ **WebView 혼합 콘텐츠(Mixed Content)**
> Capacitor 6+의 기본 `androidScheme`는 `https`라 앱 Origin이 `https://localhost`가 됩니다.
> 이 상태에서 `http://192.168.2.15:38081` API를 호출하면 CORS가 통과해도 **WebView가 Mixed Content로 차단**합니다.
> 개발용 HTTP 서버라면 `capacitor.config.ts`에서 `server.androidScheme: 'http'`(Origin → `http://localhost`)로 두거나
> `android.allowMixedContent: true` / `CapacitorHttp` 플러그인을 사용해야 합니다.

### 응답 형식
성공: HTTP 200/201 + JSON 본문 (도메인 DTO)
오류:
```json
{ "timestamp": "2026-11-11T11:23:26", "status": 400, "error": "Bad Request", "message": "이메일 형식이 올바르지 않습니다." }
```

| 코드 | 의미 |
|------|------|
| 400 | 입력값 검증 실패 / 잘못된 요청 |
| 401 | 토큰 없음·만료 |
| 403 | 권한 없음 (시스템 관리 API는 `SYS_ADMIN` 전용) |
| 404 | 대상 없음 |
| 409 | 중복·상태 충돌 (`IllegalStateException`) |
| 429 | 로그인 시도 초과 (rate limit) |
| 500 | 서버 오류 |

### 권한 규칙
- `/api/users`, `/api/roles`, `/api/permissions`, `/api/menus`(조회 외), `/api/common-codes`, `/api/group-codes` → **SYS_ADMIN**
- `/api/roles` GET, `/api/menus/tree` → 일반 사용자 허용 (사이드바 구성용)

---

## 1. 인증 (Auth)

| Method | Path | 설명 | 권한 |
|--------|------|------|------|
| POST | `/api/auth/login` | 로그인 | 공개 |
| GET | `/api/auth/me` | 내 정보 조회 | 로그인 |

**POST /api/auth/login**
```json
// 요청
{ "email": "admin@holylife.local", "password": "********" }

// 응답
{ "token": "eyJhbGciOi...", "userId": 1, "email": "...", "name": "...", "nickname": "...", "roleGroup": "SYS_ADMIN" }
```
- 실패: `400` (불일치), `429` (반복 실패)

---

## 2. 사용자 (Users)

| Method | Path | 설명 | 권한 |
|--------|------|------|------|
| GET | `/api/users` | 목록 | SYS_ADMIN |
| GET | `/api/users/{userId}` | 상세 | SYS_ADMIN |
| POST | `/api/users` | 등록 (관리자) | SYS_ADMIN |
| PUT | `/api/users/{userId}` | 수정 | SYS_ADMIN |
| DELETE | `/api/users/{userId}` | 삭제 | SYS_ADMIN |
| GET | `/api/users/{userId}/settings` | 마이페이지 설정 조회 | 로그인 |
| PUT | `/api/users/{userId}/settings` | 기본 역본 / QT 출처 변경 | 로그인 |
| PUT | `/api/users/{userId}/password` | 비밀번호 변경 | 로그인 |

**요청 필드 (`UserRequestDto`)**
`email`, `password`(등록 시 필수), `name`, `nickname`, `phone`, `roleGroup`, `accountStatus`, `memo`, `bibleVersion`, `qtSource`

**응답 필드 (`UserResponseDto`)**
`userId`, `email`, `name`, `nickname`, `phone`, `roleGroup`, `accountStatus`, `lastLogin`, `createdAt`, `bibleVersion`, `qtSource`

**비밀번호 변경**
```json
{ "currentPassword": "...", "newPassword": "..." }
```

---

## 3. 설교 (Sermons)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/sermons` | 목록 |
| GET | `/api/sermons/{sermonId}` | 상세 |
| POST | `/api/sermons` | 등록 |
| PUT | `/api/sermons/{sermonId}` | 수정 |
| DELETE | `/api/sermons/{sermonId}` | 삭제 |

**주요 필드**: `sermonId`, `userId`, `title`, `sermonDate`, `preacher`, `bibleRef`, `bibleText`, `content`, `status`

---

## 4. QT (Quiet Time)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/qts` | 목록 (사용자 작성 QT) |
| GET | `/api/qts/{qtId}` | 상세 |
| POST | `/api/qts` | 등록 |
| PUT | `/api/qts/{qtId}` | 수정 |
| DELETE | `/api/qts/{qtId}` | 삭제 |

**주요 필드**: `qtId`, `userId`, `title`, `qtDate`, `bibleRef`, `bibleText`, `content`, `visibility`(`PUBLIC`/`PRIVATE`)

---

## 5. QT 자동 수집 (qt_daily)

| Method | Path | 설명 | 권한 |
|--------|------|------|------|
| GET | `/api/qt-daily/today?source=duranno` | 오늘의 QT | 로그인 |
| GET | `/api/qt-daily` | 오늘 기준 전체 출처 | 로그인 |
| GET | `/api/qt-daily/by-date?date=YYYY-MM-DD&source=sum` | 특정일 QT | 로그인 |
| POST · GET | `/api/qt/crawl?source=all&date=YYYY-MM-DD` | **배치 수동 실행** | 로그인 |

**응답 필드**: `qtId`, `qtDate`, `bibleRef`, `title`, `hymnInfo`, `hymnText`, `bibleSectionTitle`, `bibleText`, `prayer`, `source`

**배치 실행 응답**
```json
{ "date": "2026-11-11", "source": "all",
  "duranno": "COMPLETED", "durannoExecutionId": 1,
  "sum": "COMPLETED", "sumExecutionId": 2 }
```

---

## 6. 독서노트 (Readings)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/readings` | 목록 |
| GET | `/api/readings/{readingId}` | 상세 |
| POST | `/api/readings` | 등록 |
| PUT | `/api/readings/{readingId}` | 수정 |
| DELETE | `/api/readings/{readingId}` | 삭제 |

**주요 필드**: `readingId`, `userId`, `bookTitle`, `readDate`, `author`, `translator`, `toc`, `coverImage`, `summary`, `impression`

---

## 7. 성경 (Bible)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/bible/books?versionId=2` | 66권 목록 (bookCode, bookNameKr, testament) |
| GET | `/api/bible/verses` | 본문 조회 |

**/api/bible/verses 파라미터**

| 이름 | 필수 | 설명 |
|------|------|------|
| `bookCode` | Y | 성경 코드 (`Gen`, `Exod`, `Ps`, `John` …) |
| `chapterFrom` | Y | 시작 장 |
| `chapterTo` | N | 종료 장 (없으면 단일 장) |
| `verseFrom` | N | 시작 절 |
| `verseTo` | N | 종료 절 |
| `versionId` | N | 2=개역개정(기본), 3=새번역, 6=NIV |

```bash
GET /api/bible/verses?bookCode=Gen&chapterFrom=1&chapterTo=2&verseFrom=20&verseTo=5&versionId=2
```

---

## 8. P.B.S (폴더 · 페이지 · 영역 · 참고자료)

> `/api/pbs/**`는 **공개 경로**입니다 (읽기 전용 공유 목적).

### 8.1 폴더
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/pbs/folders` | 폴더 트리 (페이지 포함) |
| POST | `/api/pbs/folders` | 생성 — `{ "folderName": "창세기", "parentId": null }` |
| PUT | `/api/pbs/folders/{folderId}` | 이름/순서 수정 |
| PUT | `/api/pbs/folders/reorder` | 순서 변경 — `[{ "folderId": 1, "sortOrder": 0 }]` |
| DELETE | `/api/pbs/folders/{folderId}` | 삭제 (하위 페이지 CASCADE) |

### 8.2 페이지
| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/pbs/pages` | 생성 — `{ "folderId": 1 }` (제목 "새 페이지") |
| GET | `/api/pbs/pages/{pageId}` | 상세 |
| PUT | `/api/pbs/pages/{pageId}` | 제목·본문·성경구절 저장 |
| PUT | `/api/pbs/pages/{pageId}/content` | 자동저장 — `{ "title": "...", "content": "<p>...</p>" }` |
| PUT | `/api/pbs/pages/{pageId}/publish` | 배포 (DRAFT → PUBLISHED) |
| PUT | `/api/pbs/pages/reorder` | 순서·폴더 이동 — `[{ "pageId": 1, "sortOrder": 0, "folderId": 4 }]` |
| DELETE | `/api/pbs/pages/{pageId}` | 삭제 |

**페이지 필드**: `pageId`, `folderId`, `userId`, `title`, `content`, `status`, `testament`, `bookCode`, `chapterFrom`, `verseFrom`, `chapterTo`, `verseTo`, `sortOrder`, `publishedAt`

### 8.3 영역(섹션) — 현재 UI 미사용 (API 잔존)
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/pbs/pages/{pageId}/sections` | 목록 |
| POST | `/api/pbs/pages/{pageId}/sections` | 생성 |
| PUT | `/api/pbs/sections/{sectionId}` | 제목 수정 |
| PUT | `/api/pbs/sections/{sectionId}/content` | 내용 수정 |
| PUT | `/api/pbs/pages/{pageId}/sections/reorder` | 순서 변경 |
| DELETE | `/api/pbs/sections/{sectionId}` | 삭제 |

---

## 9. 시스템 관리

### 9.1 역할 (Roles) — `SYS_ADMIN`
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/roles?keyword=` | 목록 |
| GET | `/api/roles/{roleId}` | 상세 |
| POST | `/api/roles` | 생성 — `groupCode`, `groupName`, `groupDesc`, `useYn` |
| PUT | `/api/roles/{roleId}` | 수정 |
| DELETE | `/api/roles/{roleId}` | 삭제 |

### 9.2 권한 (Permissions) — `SYS_ADMIN`
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/permissions?roleId=` | 목록 |
| GET | `/api/permissions/role/{roleId}/menus` | 역할별 메뉴 트리(권한 포함) |
| GET | `/api/permissions/{permissionId}` | 상세 |
| POST | `/api/permissions` | 생성 — `roleId`, `menuId`, `canRead`, `canWrite`, `canDelete` |
| PUT | `/api/permissions/{permissionId}` | 수정 |
| PUT | `/api/permissions/role/{roleId}` | 역할 권한 일괄 저장 |
| DELETE | `/api/permissions/{permissionId}` | 삭제 |

### 9.3 메뉴 (Menus)
| Method | Path | 설명 | 권한 |
|--------|------|------|------|
| GET | `/api/menus?keyword=` | 전체 목록 | SYS_ADMIN |
| GET | `/api/menus/tree` | 사이드바용 트리 | 로그인 |
| GET | `/api/menus/{menuId}/children` | 하위 메뉴 | 로그인 |
| GET | `/api/menus/{menuId}` | 상세 | SYS_ADMIN |
| POST | `/api/menus` | 생성 | SYS_ADMIN |
| PUT | `/api/menus/{menuId}` | 수정 | SYS_ADMIN |
| PUT | `/api/menus/order` · `/{menuId}/move` | 순서 변경 / 이동 | SYS_ADMIN |
| DELETE | `/api/menus/{menuId}` | 삭제 | SYS_ADMIN |

**메뉴 필드**: `menuId`, `parentId`, `parentName`, `menuName`, `menuCode`, `menuUrl`, `depth`, `sortOrder`, `icon`, `useYn`, `displayYn`, `children`

### 9.4 공통코드
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/group-codes?keyword=` | 그룹코드 목록 |
| GET | `/api/group-codes/{groupCode}` | 그룹코드 상세 |
| POST | `/api/group-codes` | 생성 — `groupCode`, `groupName` |
| PUT | `/api/group-codes/{groupCode}` | 수정 |
| DELETE | `/api/group-codes/{groupCode}` | 삭제 |
| GET | `/api/common-codes?groupCode=` | 상세코드 목록 |
| GET | `/api/common-codes/{codeId}` | 상세코드 상세 |
| POST | `/api/common-codes` | 생성 — `groupCode`, `code`, `codeName`, `sortOrder`, `useYn` |
| PUT | `/api/common-codes/{codeId}` | 수정 |
| DELETE | `/api/common-codes/{codeId}` | 삭제 |

---

## 10. 파일 업로드

| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/files/upload?category=pbs` | 단일 업로드 (multipart, `file`) |
| POST | `/api/files/uploadMultiple` | 다중 업로드 |
| DELETE | `/api/files/delete?fileUrl=` | 파일 삭제 |

**제약**
- 최대 10MB/파일, 50MB/요청
- 허용 MIME: jpeg, png, gif, svg, webp, ico
- 저장 경로: `/opt/holylife/uploads/{category}/`
- 반환 URL: `/uploads/{category}/{uuid}-{filename}`

---

## 11. 엔드포인트 요약

| 도메인 | 개수 |
|--------|-----:|
| 인증 | 2 |
| 사용자 | 8 |
| 설교 | 5 |
| QT | 5 |
| QT 자동수집 | 5 |
| 독서노트 | 5 |
| 성경 | 2 |
| P.B.S | 18 |
| 역할/권한 | 12 |
| 메뉴 | 9 |
| 공통코드 | 10 |
| 파일 | 3 |
| **합계** | **84** |
