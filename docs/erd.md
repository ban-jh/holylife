# Faith Pathway (Holylife) ERD

> 현재 데이터베이스 스키마를 기반으로 작성된 ERD
> (Spring Batch 메타테이블 8개는 제외, 비즈니스 테이블 21개 포함)

## ERD 다이어그램

```
┌──────────────────────────────┐         ┌──────────────────────────┐
│           USER               │         │       COMMON_CODE        │
│──────────────────────────────│         │──────────────────────────│
│ PK  user_id        BIGINT    │         │ PK  code_id      BIGINT  │
│     email          VARCHAR   │         │     group_code   VARCHAR │
│     password       VARCHAR   │         │     code         VARCHAR │
│     name           VARCHAR   │         │     code_name    VARCHAR │
│     nickname       VARCHAR   │         │     description  TEXT    │
│     bible_version  VARCHAR   │         │     sort_order   INT     │
│     qt_source      VARCHAR   │         │     use_yn       BOOLEAN │
│     status         VARCHAR   │         │     created_at   DATETIME│
│     created_at     DATETIME  │         │     updated_at   DATETIME│
│     updated_at     DATETIME  │         └──────────────────────────┘
└────────────┬─────────────────┘
             │ 1
             │
             ├──────────────────────────────────┐
             │                                  │
             │ N (작성자)                        │
             ▼                                  ▼
┌──────────────────────────┐         ┌──────────────────────────┐
│      SERMON              │         │     GROUP_CODE           │
│──────────────────────────│         │──────────────────────────│
│ PK  sermon_id    BIGINT  │         │ PK  group_code   VARCHAR │
│ FK  user_id      BIGINT  │         │     group_name   VARCHAR │
│     title        VARCHAR │         │     modified_date DATETIME│
│     sermon_date  DATE    │         └──────────────────────────┘
│     preacher     VARCHAR │
│     bible_ref    VARCHAR │         ┌──────────────────────────┐
│     bible_text   TEXT    │         │          MENU            │
│     content      TEXT    │         │──────────────────────────│
│     status       VARCHAR │         │ PK  menu_id      BIGINT  │
│     created_at   DATETIME│         │ FK  parent_id    BIGINT  │
│     updated_at   DATETIME│         │     menu_name    VARCHAR │
└────────────┬─────────────┘         │     menu_url     VARCHAR │
             │ N                     │     sort_order   INT     │
             ▼                       │     icon        VARCHAR │
┌──────────────────────────┐         │     use_yn       BOOLEAN │
│     SERMON_TAG           │         │     created_at   DATETIME│
│──────────────────────────│         │     updated_at   DATETIME│
│ PK  sermon_tag_id BIGINT │         └──────────────┬───────────┘
│ FK  sermon_id    BIGINT  │                        │
│ FK  tag_id       BIGINT  │         ┌──────────────┴───────────┐
└──────┬───────────────────┘         │    PERMISSION            │
       │                             │──────────────────────────│
       │ N                           │ PK  permission_id BIGINT │
       ▼                             │ FK  role_id      BIGINT  │
┌──────────────────────────┐         │ FK  menu_id      BIGINT  │
│         TAG              │         │     can_read    BOOLEAN │
│──────────────────────────│         │     can_write   BOOLEAN │
│ PK  tag_id       BIGINT  │         │     can_delete  BOOLEAN │
│     tag_name     VARCHAR │         │     created_at   DATETIME│
│     created_at   DATETIME│         └──────────────────────────┘
└──────────────────────────┘
                                     ┌──────────────────────────┐
┌──────────────────────────┐         │         ROLE             │
│         PBS              │         │──────────────────────────│
│──────────────────────────│         │ PK  role_id      BIGINT  │
│ PK  pbs_id       BIGINT  │         │     group_code   VARCHAR │
│ FK  user_id      BIGINT  │         │     group_name   VARCHAR │
│     title        VARCHAR │         │     group_desc   VARCHAR │
│     pbs_date     DATE    │         │     use_yn       BOOLEAN │
│     book_from    VARCHAR │         │     created_at   DATETIME│
│     book_to      VARCHAR │         └──────────────┬───────────┘
│     bible_text   TEXT    │                        │
│     content      TEXT    │                        │ 1
│     status       VARCHAR │                        ▼
│     created_at   DATETIME│         ┌──────────────────────────┐
│     updated_at   DATETIME│         │    USER_ROLE             │
└────────────┬─────────────┘         │──────────────────────────│
             │ N                     │ PK  user_role_id  BIGINT │
             ▼                       │ FK  user_id      BIGINT  │
┌──────────────────────────┐         │ FK  role_id      BIGINT  │
│     PBS_TAG              │         └──────────────────────────┘
│──────────────────────────│
│ PK  pbs_tag_id   BIGINT  │
│ FK  pbs_id       BIGINT  │
│ FK  tag_id       BIGINT  │
└──────────────────────────┘


┌──────────────────────────┐         ┌──────────────────────────┐
│          QT              │         │     READING_NOTE         │
│──────────────────────────│         │──────────────────────────│
│ PK  qt_id        BIGINT  │         │ PK  reading_id   BIGINT  │
│ FK  user_id      BIGINT  │         │ FK  user_id      BIGINT  │
│     title        VARCHAR │         │     book_title   VARCHAR │
│     qt_date      DATE    │         │     read_date    DATE    │
│     bible_ref    VARCHAR │         │     author      VARCHAR │
│     bible_text   TEXT    │         │     translator   VARCHAR │
│     content      TEXT    │         │     toc         TEXT    │
│     visibility   VARCHAR │         │     cover_image  VARCHAR │
│     created_at   DATETIME│         │     summary     TEXT    │
│     updated_at   DATETIME│         │     impression  TEXT    │
└────────────┬─────────────┘         │     created_at   DATETIME│
             │ N                     │     updated_at   DATETIME│
             ▼                       └──────────────────────────┘
┌──────────────────────────┐
│     QT_TAG               │
│──────────────────────────│
│ PK  qt_tag_id    BIGINT  │
│ FK  qt_id        BIGINT  │
│ FK  tag_id       BIGINT  │
└──────────────────────────┘


┌──────────────────────────────┐
│   REFERENCE_RESOURCE         │
│──────────────────────────────│
│ PK  ref_id         BIGINT    │
│     ref_type       VARCHAR   │
│     target_id      BIGINT    │
│     ref_category   VARCHAR   │
│     url            VARCHAR   │
│     file_path      VARCHAR   │
│     file_name      VARCHAR   │
│     file_type      VARCHAR   │
│     file_size      VARCHAR   │
│     description    TEXT      │
│     sort_order     INT       │
│     created_at     DATETIME  │
└──────────────────────────────┘


┌──────────────────────────┐         ┌──────────────────────────────┐
│     BIBLE_VERSION        │         │     BIBLE_BOOKS              │
│──────────────────────────│         │──────────────────────────────│
│ PK  version_id   BIGINT  │         │ PK  book_id        BIGINT    │
│     version_name VARCHAR │◄────────│ FK  version_id     BIGINT    │
│     language    VARCHAR │         │     book_code      VARCHAR   │
│     use_yn      BOOLEAN │         │     book_name_kr   VARCHAR   │
└──────────────────────────┘         │     book_name_en   VARCHAR   │
                                     │     testament      VARCHAR   │
┌──────────────────────────┐         │     chapter        INT      │
│     QT_DAILY             │         │     verse          INT      │
│──────────────────────────│         │     content        TEXT     │
│ PK  qt_id        BIGINT  │         │     created_at     DATETIME  │
│     qt_date      DATE    │         └──────────────────────────────┘
│     bible_ref    VARCHAR │
│     title        VARCHAR │
│     hymn_info    VARCHAR │
│     hymn_text    TEXT    │
│ bible_section_title VARCHAR│
│     bible_text   TEXT    │
│     prayer       TEXT    │
│     source       VARCHAR │
│     created_at   DATETIME│
└──────────────────────────┘
```

## 엔티티 상세 정의

### 1. USER (사용자)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| user_id | BIGINT PK | 사용자 ID |
| email | VARCHAR(255) | 이메일 (로그인 ID, 필수) |
| password | VARCHAR(255) | 비밀번호 (암호화, 최소 8자) |
| name | VARCHAR(100) | 이름 (필수) |
| nickname | VARCHAR(100) | 닉네임 (필수) |
| phone | VARCHAR(20) | 연락처 (010-0000-0000 형식) |
| role_group | VARCHAR(50) | 권한 그룹 (Super Admin / Admin / Editor / Viewer) |
| account_status | VARCHAR(20) | 계정 상태 (ACTIVE / INACTIVE / PENDING) |
| bible_version | VARCHAR(20) | 기본 성경 버전 (사용자 선호 설정) |
| qt_source | VARCHAR(20) | 기본 QT 출처 (사용자 선호 설정) |
| memo | VARCHAR(500) | 메모/설명 |
| email_notification | BOOLEAN | 이메일 알림 수신 여부 |
| two_factor_auth | BOOLEAN | 2단계 인증(2FA) 사용 여부 |
| session_auto_expiry | BOOLEAN | 세션 자동 만료 여부 |
| activity_log | BOOLEAN | 활동 로그 기록 여부 |
| last_login | DATETIME | 최근 접속 일시 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

**출처:** login.html, signup.html, WarmAdminUserList_nickname.html, WarmAdminDashboard_nickname.html

---

### 2. ROLE (역할 / 권한 그룹)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| role_id | BIGINT PK | 역할 ID |
| group_code | VARCHAR(20) | 권한 그룹 코드 (ADMIN / MANAGER / EDITOR / USER / GUEST) |
| group_name | VARCHAR(50) | 권한 그룹명 (관리자 / 운영 관리자 / 콘텐츠 관리자 / 일반 사용자 / 게스트) |
| group_desc | VARCHAR(200) | 그룹 설명 |
| use_yn | BOOLEAN | 사용 여부 |
| created_at | DATETIME | 생성일시 |

**출처:** WarmAdminPermissionMenuManagement.html, WarmAdminUserList_nickname.html (role_group: Super Admin, Admin, Editor, Viewer)

---

### 3. USER_ROLE (사용자-역할 매핑)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| user_role_id | BIGINT PK | 매핑 ID |
| user_id | BIGINT FK → USER | 사용자 ID |
| role_id | BIGINT FK → ROLE | 역할 ID |

**출처:** WarmAdminUserList_nickname.html, WarmAdminPermissionMenuManagement.html

---

### 4. MENU (메뉴)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| menu_id | BIGINT PK | 메뉴 ID |
| parent_id | BIGINT FK → MENU | 상위 메뉴 ID (자기참조, NULL이면 최상위) |
| parent_name | VARCHAR(100) | 상위메뉴명 (1Depth는 "-") |
| menu_name | VARCHAR(100) | 메뉴명 (P.B.S, Q.T, 설교, 독서, 시스템 등) |
| menu_code | VARCHAR(50) | 메뉴 코드 (CONTENT, BIBLE_CONTENT 등) |
| menu_url | VARCHAR(255) | 메뉴 URL 경로 |
| depth | INT | 메뉴 깊이 (1=1Depth, 2=2Depth) |
| sort_order | INT | 정렬 순서 |
| icon | VARCHAR(100) | 아이콘 (SVG 경로 또는 이름) |
| use_yn | BOOLEAN | 사용 여부 |
| display_yn | BOOLEAN | 노출 여부 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

**출처:** WarmAdminMenuManagement_UserFormat.html, 모든 화면의 사이드바

---

### 5. PERMISSION (권한)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| permission_id | BIGINT PK | 권한 ID |
| role_id | BIGINT FK → ROLE | 역할 ID |
| menu_id | BIGINT FK → MENU | 메뉴 ID |
| can_read | BOOLEAN | 조회 권한 |
| can_write | BOOLEAN | 작성 권한 |
| can_delete | BOOLEAN | 삭제 권한 |
| created_at | DATETIME | 생성일시 |

**출처:** WarmAdminPermissionMenuManagement.html

---

### 6. COMMON_CODE (공통 코드)

공통 코드는 그룹 코드와 상세 코드의 2단계 구조로 관리됩니다.

**6-1. GROUP_CODE (그룹 코드)**

| 컬럼 | 타입 | 설명 |
|---|---|---|
| group_code | VARCHAR(50) PK | 그룹 코드 (STATUS, BIBLE_BOOK, TAG_CATEGORY 등) |
| group_name | VARCHAR(100) | 그룹코드명 (필수) |
| modified_date | DATETIME | 등록/최종수정일자 |

**6-2. COMMON_CODE (상세 코드)**

| 컬럼 | 타입 | 설명 |
|---|---|---|
| code_id | BIGINT PK | 코드 ID |
| group_code | VARCHAR(50) FK → GROUP_CODE | 소속 그룹코드 |
| code | VARCHAR(50) | 코드값 (필수) |
| code_name | VARCHAR(100) | 코드명 (필수) |
| sort_order | INT | 정렬 순서 |
| use_yn | BOOLEAN | 사용 여부 |
| modified_date | DATETIME | 등록/최종수정일자 |

**출처:** WarmAdminCommonCodeManagement_v2.html

---

### 7. SERMON (설교)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| sermon_id | BIGINT PK | 설교 ID |
| user_id | BIGINT FK → USER | 작성자 ID |
| title | VARCHAR(200) | 설교 제목 |
| sermon_date | DATE | 설교 날짜 |
| preacher | VARCHAR(100) | 설교자 |
| bible_ref | VARCHAR(100) | 성경 구절 (예: 고린도전서 1:1-4) |
| bible_text | TEXT | 성경 본문 (JSON 또는 버전별 저장) |
| content | TEXT | 설교 내용 |
| status | VARCHAR(20) | 상태 (PUBLISHED / DRAFT / ARCHIVED) |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

**출처:** sermon_list.html, sermon_content.html

---

### 8. PBS (성경공부)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| pbs_id | BIGINT PK | 성경공부 ID |
| user_id | BIGINT FK → USER | 작성자 ID |
| title | VARCHAR(200) | 콘텐츠 제목 |
| pbs_date | DATE | 작성일 |
| book_from | VARCHAR(50) | 시작 성경책 (예: 창세기) |
| book_to | VARCHAR(50) | 종료 성경책 |
| bible_text | TEXT | 성경 본문 (버전별 JSON) |
| content | TEXT | 묵상 및 설명 내용 |
| status | VARCHAR(20) | 상태 (PUBLISHED / DRAFT / ARCHIVED) |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

**출처:** pbs_list.html, pbs_content.html

---

### 9. QT (큐티)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| qt_id | BIGINT PK | QT ID |
| user_id | BIGINT FK → USER | 작성자 ID |
| title | VARCHAR(200) | QT 제목 |
| qt_date | DATE | QT 날짜 |
| bible_ref | VARCHAR(100) | 말씀 (예: 시편 23:1-6) |
| bible_text | TEXT | 성경 본문 (버전별 JSON) |
| content | TEXT | 묵상 내용 |
| visibility | VARCHAR(20) | 공개여부 (PUBLIC / PRIVATE) |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

**출처:** qt_list.html, qt_content.html

---

### 10. READING_NOTE (독서 노트)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| reading_id | BIGINT PK | 독서 노트 ID |
| user_id | BIGINT FK → USER | 작성자 ID |
| book_title | VARCHAR(200) | 책 제목 |
| read_date | DATE | 읽은 날짜 |
| author | VARCHAR(100) | 저자 |
| translator | VARCHAR(100) | 옮긴이 |
| toc | TEXT | 목차 (여러 줄 입력 가능) |
| cover_image | VARCHAR(500) | 책 표지 이미지 경로 |
| summary | TEXT | 요약 |
| impression | TEXT | 느낀점 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

**출처:** reading_list.html, reading_content.html

---

### 11. TAG (태그)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| tag_id | BIGINT PK | 태그 ID |
| tag_name | VARCHAR(50) | 태그명 (은혜, 회개, 믿음, 소망, 사랑 등) |
| created_at | DATETIME | 생성일시 |

**출처:** sermon_list.html, sermon_content.html, qt_list.html, qt_content.html

---

### 12. SERMON_TAG (설교-태그 매핑)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| sermon_tag_id | BIGINT PK | 매핑 ID |
| sermon_id | BIGINT FK → SERMON | 설교 ID |
| tag_id | BIGINT FK → TAG | 태그 ID |

---

### 13. PBS_TAG (성경공부-태그 매핑)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| pbs_tag_id | BIGINT PK | 매핑 ID |
| pbs_id | BIGINT FK → PBS | 성경공부 ID |
| tag_id | BIGINT FK → TAG | 태그 ID |

---

### 14. QT_TAG (큐티-태그 매핑)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| qt_tag_id | BIGINT PK | 매핑 ID |
| qt_id | BIGINT FK → QT | QT ID |
| tag_id | BIGINT FK → TAG | 태그 ID |

---

### 15. REFERENCE_RESOURCE (참고자료)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| ref_id | BIGINT PK | 참고자료 ID |
| ref_type | VARCHAR(30) | 대상 타입 (PBS / SERMON / QT) |
| target_id | BIGINT | 대상 ID (다형성 참조) |
| ref_category | VARCHAR(20) | 자료 유형 (URL / IMAGE / DOCUMENT / MAP) |
| url | VARCHAR(500) | URL (ref_category=URL인 경우) |
| file_path | VARCHAR(500) | 파일 경로 (IMAGE, DOCUMENT인 경우) |
| file_name | VARCHAR(200) | 파일명 |
| file_type | VARCHAR(20) | 파일 유형 (확장자) |
| file_size | VARCHAR(20) | 파일 크기 |
| description | TEXT | 설명 |
| sort_order | INT | 정렬 순서 |
| created_at | DATETIME | 생성일시 |

**출처:** pbs_content.html의 참고자료 탭 (URL, 이미지, 문서, 지도)

---

### 16. BIBLE_VERSION (성경 버전)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| version_id | BIGINT PK | 버전 ID |
| version_name | VARCHAR(50) | 버전명 (새번역, NIV, 현대인) |
| language | VARCHAR(10) | 언어 (KO, EN) |
| use_yn | BOOLEAN | 사용 여부 |

**출처:** pbs_content.html, sermon_content.html, qt_content.html의 성경 말씀 탭

---

### 17. BIBLE_BOOKS (성경책별 본문)

> 이전 `BIBLE_TEXT` 테이블을 대체한 테이블입니다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| book_id | BIGINT PK | 본문 ID |
| version_id | BIGINT FK → BIBLE_VERSION | 버전 ID |
| book_code | VARCHAR(10) | 성경책 코드 (예: GEN, MAT) |
| book_name_kr | VARCHAR(20) | 성경책명 (한글, 예: 창세기) |
| book_name_en | VARCHAR(50) | 성경책명 (영문, 예: Genesis) |
| testament | VARCHAR(5) | 구약/신약 구분 (OT / NT) |
| chapter | INT | 장 |
| verse | INT | 절 |
| content | TEXT | 본문 내용 |
| created_at | DATETIME | 생성일시 |

**출처:** pbs_content.html, sermon_content.html, qt_content.html의 성경 말씀 표시, bible_versions setup

---

### 18. QT_DAILY (QT 크롤링 데이터)

> 크롤링된 일일 QT 데이터를 저장하는 테이블입니다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| qt_id | BIGINT PK | QT 데이터 ID |
| qt_date | DATE | QT 날짜 |
| bible_ref | VARCHAR(100) | 성경 구절 참조 |
| title | VARCHAR(200) | QT 제목 |
| hymn_info | VARCHAR(200) | 찬송가 정보 |
| hymn_text | TEXT | 찬송가 가사 |
| bible_section_title | VARCHAR(200) | 성경 구절 소제목 |
| bible_text | TEXT | 성경 본문 |
| prayer | TEXT | 기도문 |
| source | VARCHAR(50) | 출처 (크롤링 소스) |
| created_at | DATETIME | 생성일시 |

**출처:** qt_content.html, QtScheduler (크롤링 배치)

---

## 관계 요약

| 관계 | 카디널리티 | 설명 |
|---|---|---|
| USER → USER_ROLE | 1:N | 한 사용자는 여러 역할을 가질 수 있음 |
| ROLE → USER_ROLE | 1:N | 한 역할은 여러 사용자에게 할당됨 |
| ROLE → PERMISSION | 1:N | 한 역할은 여러 메뉴 권한을 가짐 |
| MENU → PERMISSION | 1:N | 한 메뉴는 여러 역할에 대해 권한을 가짐 |
| MENU → MENU | 1:N (자기참조) | 상위 메뉴-하위 메뉴 계층 구조 |
| USER → SERMON | 1:N | 한 사용자가 여러 설교를 작성 |
| USER → PBS | 1:N | 한 사용자가 여러 성경공부를 작성 |
| USER → QT | 1:N | 한 사용자가 여러 QT를 작성 |
| USER → READING_NOTE | 1:N | 한 사용자가 여러 독서 노트를 작성 |
| SERMON ↔ TAG | N:M | 설교와 태그는 다대다 (SERMON_TAG 매핑) |
| PBS ↔ TAG | N:M | 성경공부와 태그는 다대다 (PBS_TAG 매핑) |
| QT ↔ TAG | N:M | QT와 태그는 다대다 (QT_TAG 매핑) |
| REFERENCE_RESOURCE → (PBS/SERMON/QT) | N:1 (다형성) | 참고자료는 타입에 따라 여러 콘텐츠에 연결 |
| BIBLE_VERSION → BIBLE_BOOKS | 1:N | 한 버전에 여러 성경책 본문이 속함 |
| COMMON_CODE | 독립 | 그룹 코드 기반 공통 코드 관리 |
| QT_DAILY | 독립 | 크롤링된 일일 QT 데이터 (독립 테이블) |

---

## 출처 파일 매핑

| 엔티티 | 출처 HTML 파일 |
|---|---|
| USER | login.html, signup.html, WarmAdminUserList_nickname.html |
| ROLE, USER_ROLE | WarmAdminUserList_nickname.html, WarmAdminPermissionMenuManagement.html |
| MENU | WarmAdminMenuManagement_UserFormat.html, 모든 화면 사이드바 |
| PERMISSION | WarmAdminPermissionMenuManagement.html |
| COMMON_CODE, GROUP_CODE | WarmAdminCommonCodeManagement_v2.html |
| SERMON | sermon_list.html, sermon_content.html |
| PBS | pbs_list.html, pbs_content.html |
| QT | qt_list.html, qt_content.html |
| READING_NOTE | reading_list.html, reading_content.html |
| TAG | sermon_list/content, qt_list/content |
| REFERENCE_RESOURCE | pbs_content.html (참고자료 탭) |
| BIBLE_VERSION | pbs_content, sermon_content, qt_content (성경 말씀 탭) |
| BIBLE_BOOKS | pbs_content, sermon_content, qt_content (성경 말씀 표시), bible_versions setup |
| QT_DAILY | qt_content.html, QtScheduler (크롤링 배치) |