-- ============================================================
-- 07. 인덱스 생성
-- 조회 성능 향상을 위한 추가 인덱스
-- (CREATE TABLE에 이미 포함된 인덱스는 제외)
-- ============================================================

-- users: 권한 그룹별 조회
CREATE INDEX idx_users_role_group   ON users (role_group);
CREATE INDEX idx_users_status     ON users (account_status);

-- sermons: 날짜별 조회, 상태별 조회
CREATE INDEX idx_sermons_date     ON sermons (sermon_date);
CREATE INDEX idx_sermons_status   ON sermons (status);

-- qts: 날짜별 조회, 공개 범위별 조회
CREATE INDEX idx_qts_date         ON qts (qt_date);
CREATE INDEX idx_qts_visibility   ON qts (visibility);

-- qt_daily: 날짜별 조회 (UNIQUE KEY로 이미覆盖, 참고용)
-- uk_qt_date_source (qt_date, source) — 이미 CREATE TABLE에 있음

-- pbs: 날짜별 조회, 상태별 조회
CREATE INDEX idx_pbs_date          ON pbs (pbs_date);
CREATE INDEX idx_pbs_status        ON pbs (status);

-- reading_notes: 날짜별 조회
CREATE INDEX idx_reading_date     ON reading_notes (read_date);

-- bible_books: 버전별 권 조회 (CREATE TABLE에 이미 포함)
-- idx_bible_lookup (version_id, book_code, chapter, verse) — 이미 있음
-- idx_bible_book (book_code, chapter, verse) — 이미 있음

-- menus: depth별 조회, 정렬 순서
CREATE INDEX idx_menus_depth_sort  ON menus (depth, sort_order);
CREATE INDEX idx_menus_use        ON menus (use_yn, display_yn);

-- common_codes: 그룹별 정렬 조회
CREATE INDEX idx_common_codes_sort ON common_codes (group_code, sort_order);

-- permissions: 권한별 메뉴 조회
CREATE INDEX idx_permissions_role ON permissions (role_id);

-- tags: 태그명 검색
-- uk_tags_name (tag_name) — 이미 CREATE TABLE에 있음

-- reference_resources: 참조 대상별 조회
-- idx_ref_target (ref_type, target_id) — 이미 CREATE TABLE에 있음