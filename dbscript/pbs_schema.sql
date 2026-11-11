-- ═══════════════════════════════════════════════════════════════
-- PBS (성경공부) — Confluence 스타일 페이지 기반 구조 DB 스키마
-- 프로젝트: holylife
-- 생성일: 2026-11-09
-- ═══════════════════════════════════════════════════════════════

-- 기존 테이블 삭제 (데이터 없음)
DROP TABLE IF EXISTS pbs_tag_map;
DROP TABLE IF EXISTS pbs_reference;
DROP TABLE IF EXISTS pbs_page;
DROP TABLE IF EXISTS pbs_folder;
DROP TABLE IF EXISTS pbs;
DROP TABLE IF EXISTS pbs_tags;

-- ═══════════════════════════════════════════════════════════════
-- 1. pbs_folder — 폴더 (성경책 카테고리 트리)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE pbs_folder (
  folder_id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '폴더 ID (PK)',
  parent_id    BIGINT       DEFAULT NULL            COMMENT '부모 폴더 ID (FK, NULL=루트)',
  folder_name  VARCHAR(100) NOT NULL                COMMENT '폴더명 (예: 창세기, 출애굽기)',
  sort_order   INT          NOT NULL DEFAULT 0      COMMENT '정렬 순서',
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (folder_id),
  KEY idx_folder_parent (parent_id),
  CONSTRAINT fk_folder_parent FOREIGN KEY (parent_id) REFERENCES pbs_folder (folder_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 폴더 (성경책 카테고리 트리)';

-- ═══════════════════════════════════════════════════════════════
-- 2. pbs_page — 페이지 (Confluence 스타일)
--    - 생성시 임시 제목('새 페이지')으로 생성
--    - 내용 입력 즉시 자동 저장 (content 컬럼)
--    - 배포 버튼 클릭 시 status → PUBLISHED, published_at 설정
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE pbs_page (
  page_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '페이지 ID (PK)',
  folder_id    BIGINT       NOT NULL                COMMENT '폴더 ID (FK)',
  user_id      BIGINT       NOT NULL                COMMENT '작성자 ID (FK)',
  title        VARCHAR(200) NOT NULL DEFAULT '새 페이지' COMMENT '페이지 제목 (생성시 임시 제목)',
  content      LONGTEXT     DEFAULT NULL            COMMENT '페이지 내용 (자동 저장)',
  status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '상태 (DRAFT=임시저장, PUBLISHED=배포됨)',

  -- 성경 구절 선택
  testament    VARCHAR(10)  DEFAULT NULL            COMMENT '신구약 (OLD/NEW)',
  book_code    VARCHAR(20)  DEFAULT NULL            COMMENT '성경책 코드 (예: Gen, Exod, Ps, John)',
  chapter_from INT          DEFAULT NULL            COMMENT '시작 장',
  verse_from   INT          DEFAULT NULL            COMMENT '시작 절',
  chapter_to   INT          DEFAULT NULL            COMMENT '종료 장 (NULL=단일 장)',
  verse_to     INT          DEFAULT NULL            COMMENT '종료 절 (NULL=해당 장 전체)',

  sort_order   INT          NOT NULL DEFAULT 0      COMMENT '폴더 내 정렬 순서',
  published_at DATETIME     DEFAULT NULL            COMMENT '배포 일시 (status=PUBLISHED 시 설정)',
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (page_id),
  KEY idx_page_folder (folder_id),
  KEY idx_page_user (user_id),
  KEY idx_page_status (status),
  KEY idx_page_book (book_code, chapter_from, verse_from),
  CONSTRAINT fk_page_folder FOREIGN KEY (folder_id) REFERENCES pbs_folder (folder_id) ON DELETE CASCADE,
  CONSTRAINT fk_page_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 페이지 (Confluence 스타일)';

-- ═══════════════════════════════════════════════════════════════
-- 3. pbs_page_section — 영역별 내용 (관찰/해석/적용)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE pbs_page_section (
  section_id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '영역 ID (PK)',
  page_id       BIGINT       NOT NULL                COMMENT '페이지 ID (FK)',
  section_title VARCHAR(100) NOT NULL                COMMENT '영역 제목',
  content       LONGTEXT     DEFAULT NULL            COMMENT '영역 내용',
  sort_order    INT          NOT NULL DEFAULT 0      COMMENT '정렬 순서',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (section_id),
  KEY idx_section_page (page_id),
  CONSTRAINT fk_section_page FOREIGN KEY (page_id) REFERENCES pbs_page (page_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 영역별 내용 (관찰/해석/적용)';

-- ═══════════════════════════════════════════════════════════════
-- 4. pbs_reference — 참고자료 (URL, 이미지, 문서 첨부)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE pbs_reference (
  ref_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '참고자료 ID (PK)',
  page_id      BIGINT       NOT NULL                COMMENT '페이지 ID (FK)',
  ref_type     VARCHAR(20)  NOT NULL                COMMENT '자료 유형 (URL/IMAGE/DOCUMENT)',
  ref_name     VARCHAR(200) NOT NULL                COMMENT '자료명',
  ref_url      VARCHAR(500) DEFAULT NULL            COMMENT 'URL (ref_type=URL)',
  file_path    VARCHAR(500) DEFAULT NULL            COMMENT '파일 경로 (ref_type=IMAGE/DOCUMENT)',
  file_size    BIGINT       DEFAULT NULL            COMMENT '파일 크기 (bytes)',
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  PRIMARY KEY (ref_id),
  KEY idx_ref_page (page_id),
  CONSTRAINT fk_ref_page FOREIGN KEY (page_id) REFERENCES pbs_page (page_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 참고자료';

-- ═══════════════════════════════════════════════════════════════
-- 5. pbs_tag_map — PBS-태그 매핑
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE pbs_tag_map (
  map_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '매핑 ID (PK)',
  page_id      BIGINT       NOT NULL                COMMENT '페이지 ID (FK)',
  tag_id       BIGINT       NOT NULL                COMMENT '태그 ID (FK)',
  PRIMARY KEY (map_id),
  UNIQUE KEY uk_pbs_tag (page_id, tag_id),
  KEY fk_pbs_tag_tag (tag_id),
  CONSTRAINT fk_pbs_tag_page FOREIGN KEY (page_id) REFERENCES pbs_page (page_id) ON DELETE CASCADE,
  CONSTRAINT fk_pbs_tag_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS-태그 매핑';

-- ═══════════════════════════════════════════════════════════════
-- 샘플 데이터
-- ═══════════════════════════════════════════════════════════════

-- 폴더
INSERT INTO pbs_folder (folder_id, parent_id, folder_name, sort_order) VALUES
  (1, NULL, '창세기', 1),
  (2, NULL, '출애굽기', 2),
  (3, NULL, '시편', 3),
  (4, NULL, '요한복음', 4);

-- 페이지
INSERT INTO pbs_page (page_id, folder_id, user_id, title, content, status, testament, book_code, chapter_from, verse_from, chapter_to, verse_to, sort_order, published_at) VALUES
  (1, 1, 1, '창세기 1장 — 하나님이 세상을 창조하셨다',
   '<p><strong>[관찰]</strong> 태초에 하나님이 말씀으로 천지를 창조하셨다. 첫째 날에 빛과 어둠이 구분되었고, 낮과 밤이 생겼다.</p><p><strong>[해석]</strong> 하나님의 말씀은 창조의 도구이다. "빛이 생겨라"라는 말씀으로 빛이 존재하게 되었다.</p><p><strong>[적용]</strong> 하나님의 말씀에 순종하며 살아가자.</p>',
   'PUBLISHED', 'OLD', 'Gen', 1, 1, 1, 3, 1, NOW()),
  (2, 1, 1, '창세기 3장', NULL, 'DRAFT', 'OLD', 'Gen', 3, 1, 3, 7, 2, NULL),
  (3, 2, 1, '출애굽기 3장', NULL, 'DRAFT', 'OLD', 'Exod', 3, 1, 3, 5, 1, NULL),
  (4, 3, 1, '시편 23편', NULL, 'DRAFT', 'OLD', 'Ps', 23, 1, 23, 6, 1, NULL),
  (5, 4, 1, '요한복음 1장', NULL, 'DRAFT', 'NEW', 'John', 1, 1, 1, 14, 1, NULL);

-- 참고자료 (창세기 1장 페이지)
INSERT INTO pbs_reference (page_id, ref_type, ref_name, ref_url) VALUES
  (1, 'URL', '창세기 1장 주해 자료', 'https://biblestudy.example.com/genesis-1');

INSERT INTO pbs_reference (page_id, ref_type, ref_name, file_path, file_size) VALUES
  (1, 'DOCUMENT', '창세기_연구자료.pdf', '/uploads/pbs/창세기_연구자료.pdf', 2516582),
  (1, 'IMAGE', '창조_일러스트.jpg', '/uploads/pbs/창조_일러스트.jpg', 1153433);

-- Auto increment
ALTER TABLE pbs_folder AUTO_INCREMENT = 5;
ALTER TABLE pbs_page AUTO_INCREMENT = 6;
ALTER TABLE pbs_reference AUTO_INCREMENT = 4;
