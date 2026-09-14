---
name: holylife-html-design
description: "Faith Pathway 웹 화면 HTML 디자인 생성 스킬."
version: 0.1.0
author: ban-jh (ban-jh), Hermes Agent
license: MIT
platforms: [linux, macos, windows]
metadata:
  hermes:
    tags: [html, design, faith-pathway, holylife, ui]
    related_skills: []
---

# Holylife HTML Design Skill

Faith Pathway(홀리라이프) 프로젝트의 웹 화면용 정적 HTML 디자인 파일을 생성합니다.
단일 HTML 파일 내에 `<style>`과 `<script>`를 인라인으로 포함하며, 외부 CSS/JS 프레임워크 의존성 없이 동작합니다.

## When to Use

- Faith Pathway 프로젝트에 새 화면(로그인, 회원가입, 성경공부 목록/상세, 관리자 대시보드 등)의 HTML 디자인이 필요할 때
- 기존 design 폴더의 디자인 스타일을 따라 새 HTML 파일을 생성할 때
- Thymeleaf 템플릿으로 변환하기 전 단계의 정적 HTML 프로토타입이 필요할 때

## Don't use for

- Spring Boot 컨트롤러/서비스/리포지토리 등 백엔드 코드 생성
- Thymeleaf 템플릿(`.html` with `th:` 속성) 직접 생성 — 본 스킬은 순수 정적 HTML만 다룹니다
- 데이터베이스 스키마 설계

## Design System

### 프로젝트 디자인 폴더 구조

```
design/
  login/           - 로그인, 회원가입, PBS 로그인 화면
  성경공부/         - 성경공부 목록, 성경 콘텐츠 상세 화면
  System_html/     - 관리자 대시보드, 사용자 관리, 메뉴 관리, 권한 관리, 공통코드 관리
```

### 두 가지 디자인 테마

프로젝트는 화면 종류에 따라 두 가지 테마를 사용합니다:

#### 1. 인증 화면 테마 (login/ 폴더)

로그인, 회원가입 등 인증 관련 화면에 사용됩니다.

- **폰트:** `'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif`
- **배경:** `#f4f6f8` + 도트 패턴 (`radial-gradient` 기반)
- **카드 컨테이너:** 흰색 배경, `max-width: 420px`(로그인) / `460px`(회원가입), `border-radius: 16px`
- **그림자:** `0 10px 25px -5px rgba(0,0,0,0.05), 0 8px 10px -6px rgba(0,0,0,0.05)`
- **브랜드 컬러:** `#2b4c7e` (딥 블루)
  - hover: `#1d355a`
- **텍스트 컬러:**
  - 제목: `#1e293b`
  - 본문: `#475569`
  - 보조: `#64748b`
- **입력 필드:** `border: 1px solid #cbd5e1`, `border-radius: 8px`, focus 시 `#2b4c7e` + `box-shadow`
- **버튼:** 브랜드 컬러 배경, 흰색 텍스트, `border-radius: 8px`, `font-weight: 600`
- **모달/팝업:** `.modal-overlay` + `.modal-card` 패턴, `backdrop-filter: blur(4px)`, `transform: scale()` 애니메이션

#### 2. 관리자/콘텐츠 화면 테마 (성경공부/, System_html/ 폴더)

관리자 대시보드, 성경공부 목록/상세 등에 사용되는 Warm 톤 테마입니다.

- **폰트:** `'Pretendard', -apple-system, 'Apple SD Gothic Neo', 'Noto Sans KR', 'Malgun Gothic', sans-serif`
- **CSS 변수 기반 디자인 토큰 (`:root`):**

```css
--cream: #faf7f2;           /* 기본 배경 */
--cream-dark: #f2ece0;      /* 카드 헤더, 테이블 헤더 배경 */
--warm-white: #fffcf8;      /* 카드, 헤더 배경 */
--sand: #e8ddd0;            /* 테두리 */
--sand-dark: #d4c5b0;       /* 진한 테두리 */
--brown-light: #c8b49a;
--brown: #8c6f56;
--brown-dark: #5e4a38;      /* 사이드바 배경 */
--accent: #c97d4e;          /* 포인트 컬러 (버튼, 활성 메뉴) */
--accent-light: #f0d4ba;
--accent-hover: #b06b3c;
--text-primary: #2d2118;
--text-secondary: #7a6655;
--text-muted: #a89585;
--sidebar-width: 240px;
--header-height: 64px;
--radius-sm: 8px;
--radius-md: 12px;
--radius-lg: 18px;
--shadow-sm: 0 1px 4px rgba(100,70,40,0.07);
--shadow-md: 0 4px 16px rgba(100,70,40,0.10);
--shadow-lg: 0 8px 32px rgba(100,70,40,0.13);
--transition: 0.22s cubic-bezier(.4,0,.2,1);
```

- **레이아웃:** `.layout`(flex) > `.sidebar`(왼쪽) + `.main`(오른쪽)
- **사이드바:** `--brown-dark` 배경, 로고 + 네비게이션 메뉴 + 하단 사용자 정보
- **헤더:** `--warm-white` 배경, 브레드크럼 + 검색바 + 알림/아바타
- **콘텐츠 영역:** `--cream` 배경, `padding: 32px 36px`, 스크롤 가능
- **카드:** `--warm-white` 배경, `--sand` 테두리, `--radius-lg` 라운드, hover 시 그림자 증가
- **테이블:** `.data-table` 패턴, 헤더 `--cream-dark` 배경, 행 hover 시 배경 변화
- **상태 배지:** `.status-tag` (published: 녹색, draft: 노란색, archived: 회색)
- **버튼:** `.btn` 기본 + `.btn-cancel` / `.btn-save` 변형
- **반응형:** `@media (max-width: 900px)` 기준, 그리드 단일 컬럼으로 축소

### 공통 규칙 (두 테마 모두)

- `<!DOCTYPE html>`, `<html lang="ko">` 시작
- `<meta charset="UTF-8">`, `<meta name="viewport" content="width=device-width, initial-scale=1.0">`
- 모든 CSS는 `<style>` 태그 내 인라인 (외부 CSS 파일 없음)
- 모든 JS는 `<script>` 태그 내 인라인 (외부 JS 파일 없음)
- SVG 아이콘은 인라인 `<svg>` 사용 (외부 아이콘 라이브러리 없음)
- 한국어 UI 텍스트
- `box-sizing: border-box` 전역 리셋

## Prerequisites

- 디자인 파일은 `design/` 폴더 내 카테고리별 하위 폴더에 저장
- 화면 종류에 따라 테마 선택:
  - 인증 화면 → 인증 화면 테마 (블루 계열)
  - 관리자/콘텐츠 화면 → Warm 톤 테마 (브라운/오렌지 계열)

## Procedure

1. **화면 요구사항 확인:** 생성할 화면의 목적, 포함해야 할 요소(폼, 테이블, 탭, 모달 등) 파악
2. **테마 선택:** 화면 종류에 따라 인증 화면 테마 또는 Warm 톤 테마 결정
3. **디자인 폴더 위치 선정:** `design/` 아래 적절한 하위 폴더 선택 (없으면 생성)
4. **HTML 파일 생성:** `write_file`로 단일 HTML 파일 작성
   - `<!DOCTYPE html>`로 시작
   - `<style>`에 해당 테마의 CSS 변수/스타일 포함
   - `<body>`에 시맨틱 HTML 구조 작성
   - 필요한 경우 `<script>`에 인터랙션(탭 전환, 모달, 폼 검증 등) 추가
5. **반응형 확인:** `@media (max-width: 900px)` 브레이크포인트 적용
6. **완성된 파일 경로를 사용자에게 안내**

## HTML 파일 기본 골격

### 인증 화면 템플릿

```html
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Faith Pathway - [화면명]</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0;
        font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif; }
    body { min-height: 100vh; display: flex; align-items: center; justify-content: center;
           background-color: #f4f6f8;
           background-image: radial-gradient(#d1d5db 0.75px, transparent 0.75px),
                             radial-gradient(#d1d5db 0.75px, #f4f6f8 0.75px);
           background-size: 30px 30px; background-position: 0 0, 15px 15px;
           padding: 20px; }
    /* 브랜드 컬러: #2b4c7e */
    /* 카드, 폼, 버튼, 모달 스타일... */
  </style>
</head>
<body>
  <!-- 카드 컨테이너 + 헤더(로고) + 폼 + 푸터 -->
  <!-- 필요 시 모달 -->
  <script>
    /* 폼 제출, 모달 제어 등 인터랙션 */
  </script>
</body>
</html>
```

### 관리자/콘텐츠 화면 템플릿

```html
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>[화면명]</title>
<style>
  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
  :root {
    --cream: #faf7f2; --cream-dark: #f2ece0; --warm-white: #fffcf8;
    --sand: #e8ddd0; --sand-dark: #d4c5b0; --brown-dark: #5e4a38;
    --accent: #c97d4e; --accent-light: #f0d4ba; --accent-hover: #b06b3c;
    --text-primary: #2d2118; --text-secondary: #7a6655; --text-muted: #a89585;
    --sidebar-width: 240px; --header-height: 64px;
    --radius-sm: 8px; --radius-md: 12px; --radius-lg: 18px;
    --shadow-sm: 0 1px 4px rgba(100,70,40,0.07);
    --shadow-md: 0 4px 16px rgba(100,70,40,0.10);
    --transition: 0.22s cubic-bezier(.4,0,.2,1);
  }
  html, body { height: 100%; font-family: 'Pretendard', -apple-system, 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;
               background: var(--cream); color: var(--text-primary); font-size: 14px; line-height: 1.6; }
  .layout { display: flex; height: 100vh; overflow: hidden; }
  .sidebar { width: var(--sidebar-width); background: var(--brown-dark); /* ... */ }
  .main { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
  .header { height: var(--header-height); background: var(--warm-white); /* ... */ }
  .content { flex: 1; overflow-y: auto; padding: 32px 36px; }
  /* 카드, 테이블, 필터바, 버튼 등... */
  @media (max-width: 900px) { /* 반응형 규칙 */ }
</style>
</head>
<body>
<div class="layout">
  <aside class="sidebar"><!-- 로고 + 네비게이션 + 사용자 정보 --></aside>
  <div class="main">
    <header class="header"><!-- 브레드크럼 + 검색 + 알림 + 아바타 --></header>
    <main class="content"><!-- 페이지 제목 + 카드/테이블/폼 --></main>
  </div>
</div>
<script>
  /* 사이드바 메뉴 토글, 탭 전환, 모달 등 인터랙션 */
</script>
</body>
</html>
```

## Pitfalls

- 외부 CSS/JS 파일을 참조하지 마세요 — 모든 스타일과 스크립트는 단일 HTML 파일 내 인라인이어야 합니다
- 두 테마를 혼용하지 마세요 — 인증 화면은 블루 계열, 관리자/콘텐츠 화면은 Warm 톤을 유지하세요
- SVG 아이콘은 외부 라이브러리(FontAwesome 등) 대신 인라인 `<svg>`를 사용하세요
- Pretendard 폰트는 CDN 링크 없이 font-family에만 선언하세요 (실제 로딩은 Spring Boot 단에서 처리)
- 한국어 UI 텍스트의 `word-break: keep-all`을 적용하여 단어 중간 줄바꿈을 방지하세요
- 관리자 화면의 사이드바 너비(`--sidebar-width: 240px`)와 헤더 높이(`--header-height: 64px`)는 일관되게 유지하세요

## Verification

- [ ] 파일이 `design/[카테고리]/[파일명].html` 경로에 생성됨
- [ ] `<!DOCTYPE html>`과 `<html lang="ko">`로 시작
- [ ] 외부 CSS/JS 의존성 없이 단일 파일로 동작
- [ ] 해당 화면 종류에 맞는 테마 적용 (인증: 블루, 관리자/콘텐츠: Warm 톤)
- [ ] CSS 변수 기반 디자인 토큰 사용 (관리자/콘텐츠 화면)
- [ ] 반응형 `@media (max-width: 900px)` 규칙 포함
- [ ] 브라우저에서 파일 열었을 때 레이아웃이 정상 표시