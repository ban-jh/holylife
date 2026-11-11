# 화면 목록표 (Screen Inventory)

> 최종 갱신: 2026-11-11 · 프론트엔드 `holylifeView` 기준
> 라우트 18개 / 화면 파일 16개 (`/` 리다이렉트, `/notice` 인라인 포함)

---

## 1. 인증 (레이아웃: `AuthLayout` — 사이드바 없음)

| ID | 화면 | 경로 | 파일 | 접근 | 주요 기능 | 관련 API |
|----|------|------|------|------|-----------|----------|
| A-01 | 로그인 | `/login` | `LoginView.vue` | 공개 | 이메일/비밀번호, JWT 저장, 세션 검사 | `POST /api/auth/login` |
| A-02 | 회원가입 | `/signup` | `SignupView.vue` | 공개 | 입력 검증·안내 모달 (실제 등록은 관리자 화면) | — |

---

## 2. 콘텐츠 (레이아웃: `DefaultLayout` — 사이드바 + 헤더)

| ID | 화면 | 경로 | 파일 | 접근 | 주요 기능 | 관련 API |
|----|------|------|------|------|-----------|----------|
| C-00 | 대시보드 | `/dashboard` | `DashboardView.vue` | 로그인 | 통계 4종, 오늘의 QT, 월간 현황, 최근 4탭 | `/api/sermons`, `/api/qts`, `/api/pbs/folders`, `/api/readings`, `/api/qt-daily/today`, `/api/bible/books` |
| C-01 | 설교 목록 | `/sermons` | `SermonListView.vue` | 로그인 | 검색·카드 목록, 페이징 | `GET /api/sermons` |
| C-02 | 설교 등록/수정 | `/sermons/:id` | `SermonDetailView.vue` | 로그인 | 설교자 선택, 성경 3역본 조회, 에디터 | `GET/POST/PUT /api/sermons`, `GET /api/bible/verses` |
| C-03 | QT 목록 | `/qts` | `QtListView.vue` | 로그인 | 검색·목록 | `GET /api/qts` |
| C-04 | QT 등록/수정 | `/qts/:id` | `QtDetailView.vue` | 로그인 | 크롤링 본문 불러오기, 공개 여부, 에디터 | `GET/POST/PUT /api/qts`, `GET /api/qt-daily/today` |
| C-05 | P.B.S | `/pbs` | `PbsListView.vue` | 로그인 | 3분할(트리/성경/내용), 폴더·페이지 CRUD, 자동저장, 배포 | `/api/pbs/folders`, `/api/pbs/pages`, `/api/bible/verses` |
| C-06 | 독서 목록 | `/readings` | `ReadingListView.vue` | 로그인 | 검색·카드 목록 | `GET /api/readings` |
| C-07 | 독서 등록/수정 | `/readings/:id` | `ReadingDetailView.vue` | 로그인 | 목차/요약/느낀점 에디터 | `GET/POST/PUT /api/readings` |
| C-08 | 마이페이지 | `/mypage` | `MyPageView.vue` | 로그인 | 프로필, 기본 역본/QT 출처, 비밀번호 변경 | `GET/PUT /api/users/{id}/settings`, `PUT /api/users/{id}/password` |
| C-09 | 공지사항 | `/notice` | 인라인 컴포넌트 | 로그인 | "준비 중" 플레이스홀더 | — |

---

## 3. 시스템 관리 (`SYS_ADMIN` 전용)

| ID | 화면 | 경로 | 파일 | 주요 기능 | 관련 API |
|----|------|------|------|-----------|----------|
| S-01 | 사용자 관리 | `/system/users` | `system/UserListView.vue` | 목록, 행 클릭 상세 팝업, 상태 변경 | `GET/PUT/DELETE /api/users` |
| S-02 | 사용자 등록 | `/system/users/new` | `system/UserCreateView.vue` | 신규 사용자 등록 | `POST /api/users`, `GET /api/roles` |
| S-03 | 메뉴 관리 | `/system/menus` | `system/MenuListView.vue` | 트리, 드래그&드롭, 아이콘 업로드, 1Depth 이동 제한 | `/api/menus` (tree/order/move) |
| S-04 | 권한 관리 | `/system/permissions` | `system/PermissionListView.vue` | 역할 ↔ 메뉴 권한 매트릭스 | `/api/roles`, `/api/permissions/role/{roleId}/menus` |
| S-05 | 공통코드 | `/system/common-codes` | `system/CommonCodeView.vue` | 그룹코드/상세코드 관리 | `/api/group-codes`, `/api/common-codes` |

---

## 4. 공통 컴포넌트

| 컴포넌트 | 파일 | 설명 |
|----------|------|------|
| 사이드바 | `AppSidebar.vue` | DB 메뉴 기반, 권한 필터링, 접기/펼치기 |
| 헤더 | `AppHeader.vue` | 사용자 메뉴, 사이드바 토글 |
| 리치 에디터 | `RichTextEditor.vue` | Tiptap, 이미지 업로드/리사이즈/정렬, 읽기 모드 |
| 이미지 리사이즈 | `ResizableImage.ts` | Tiptap NodeView 확장 |
| 손글씨 | `HandwritingPad.vue` | Canvas + Pointer Events |
| 페이지네이션 | `Pagination.vue` | 공통 |
| 상태 배지 | `StatusBadge.vue` | DRAFT/PUBLISHED/PUBLIC 등 |
| 토스트 | `AppToast.vue` | 전역 알림 |

---

## 5. 화면 정책

- **권한**: 로그인 후 DB 메뉴 기준으로 접근 가능 경로만 허용 (`router.beforeEach` + `useAuth.isMenuAllowed`)
- **세션**: localStorage 저장, 날짜 변경 후 1시간 경과 시 만료
- **반응형**: PC(1024+) / 태블릿(~900) / 모바일(~500) 3구간
- **P.B.S**: 페이지 단위 라우트 없음 — 한 화면(`/pbs`)에서 트리 선택으로 전환
