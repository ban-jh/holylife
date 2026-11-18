-- ============================================================
-- 07. 추가 인덱스 (선택)
--
-- 현재 운영 DB에는 아래 인덱스가 적용되어 있지 않습니다.
-- (테이블 DDL에 포함된 PK / UNIQUE / FK 인덱스만 존재)
--
-- 따라서 기본 설치 시 이 파일은 아무 것도 변경하지 않습니다.
-- 조회 성능이 필요할 때 주석을 해제하여 수동 적용하세요.
-- 적용하면 운영 DB와 스키마가 달라지므로 ERD/문서에도 반영해야 합니다.
-- ============================================================

-- users
-- CREATE INDEX idx_users_role_group ON users (role_group);
-- CREATE INDEX idx_users_status     ON users (account_status);

-- sermons
-- CREATE INDEX idx_sermons_date     ON sermons (sermon_date);
-- CREATE INDEX idx_sermons_status   ON sermons (status);

-- qts
-- CREATE INDEX idx_qts_date         ON qts (qt_date);
-- CREATE INDEX idx_qts_visibility   ON qts (visibility);

-- reading_notes
-- CREATE INDEX idx_reading_date     ON reading_notes (read_date);

-- menus
-- CREATE INDEX idx_menus_depth_sort ON menus (depth, sort_order);
-- CREATE INDEX idx_menus_use        ON menus (use_yn, display_yn);

-- common_codes
-- CREATE INDEX idx_common_codes_sort ON common_codes (group_code, sort_order);

-- permissions
-- CREATE INDEX idx_permissions_role ON permissions (role_id);

-- pbs_page
-- CREATE INDEX idx_pbs_page_updated ON pbs_page (updated_at);

-- qt_daily: uk_qt_date_source(qt_date, source) — 테이블 DDL에 포함
-- bible_books: idx_bible_lookup / idx_bible_book — 테이블 DDL에 포함
-- tags: uk_tags_name — 테이블 DDL에 포함
