# HolyLife 문서 인덱스

> 최소 문서 세트 (6종) + 참고 문서
> 최종 갱신: 2026-11-11

---

## 최소 세트 (필수 6종)

| # | 문서 | 경로 | 내용 |
|---|------|------|------|
| 1 | **프로젝트 개요서** | [`00-overview.md`](./00-overview.md) | 목적·범위·역할·구성도·성공 기준·제약 |
| 2 | **화면 목록표** | [`02-screen-list.md`](./02-screen-list.md) | 18개 라우트·16개 화면, 접근 권한, 화면-API 매핑 |
| 3 | **ERD + 데이터 사전** | [`erd.md`](./erd.md) | 22개 비즈니스 테이블 + Batch 6개, Mermaid 다이어그램 |
| 4 | **API 명세서** | [`06-api-spec.md`](./06-api-spec.md) | 84개 엔드포인트, 요청/응답, 권한, 에러 규격 |
| 5 | **운영 매뉴얼** | [`10-operations.md`](./10-operations.md) | 배포·서비스 관리·배치·백업·장애 대응 |
| 6 | **README** | [`../README.md`](../README.md) | 기술 스택·구조·빌드/실행·배포 요약 |

---

## 보조 문서

| 문서 | 경로 | 내용 |
|------|------|------|
| DB 스키마 스크립트 | [`../dbscript/`](../dbscript/) | **DDL 단일 소스** — 01~07 순차 생성, 시드, 인덱스, Batch/PBS 스키마 |
| 대시보드 기획서 | [`../dashboard-planning.md`](../dashboard-planning.md) | 화면 구성·데이터 연동 정의 |
| P.B.S 재설계안 | [`../design/성경공부/pbs_redesign_plan.md`](../design/성경공부/pbs_redesign_plan.md) | 목록/등록 → Confluence형 전환 근거 |
| 화면 시안(HTML) | [`../design/`](../design/) | 로그인·시스템 관리·독서·성경공부 시안 |

---

## 문서 갱신 규칙

1. **DB 변경 시**: `dbscript/` 수정 → 신규 DB에서 01~07 실행 검증 → `erd.md` 재생성
2. **API 변경 시**: 컨트롤러 수정과 같은 커밋에서 `06-api-spec.md` 갱신
3. **화면/라우트 변경 시**: `02-screen-list.md`와 `holylifeView/src/router/index.ts`를 함께 수정
4. **배포/포트 변경 시**: `10-operations.md`와 `README.md`를 함께 수정
5. ERD는 `information_schema`에서 추출, API는 Swagger(`/v3/api-docs`)와 대조하여 검증
