-- ═══════════════════════════════════════════════════════════════
-- P.B.S (성경공부) 테이블 — 참고용 단일 파일
-- 실제 설치 순서는 03_schema_content.sql 을 사용하세요 (동일한 DDL).
-- 현재 운영 DB의 SHOW CREATE TABLE 결과와 동일합니다.
-- ═══════════════════════════════════════════════════════════════


-- ---------- pbs_folder ----------
CREATE TABLE `pbs_folder` (
  `folder_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '폴더 ID (PK)',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '부모 폴더 ID (FK, NULL=루트)',
  `folder_name` varchar(100) NOT NULL COMMENT '폴더명 (예: 창세기, 출애굽기)',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '정렬 순서',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`folder_id`),
  KEY `idx_folder_parent` (`parent_id`),
  CONSTRAINT `fk_folder_parent` FOREIGN KEY (`parent_id`) REFERENCES `pbs_folder` (`folder_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 폴더 (성경책 카테고리 트리)';


-- ---------- pbs_page ----------
CREATE TABLE `pbs_page` (
  `page_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '페이지 ID (PK)',
  `folder_id` bigint(20) NOT NULL COMMENT '폴더 ID (FK)',
  `user_id` bigint(20) NOT NULL COMMENT '작성자 ID (FK)',
  `title` varchar(200) NOT NULL DEFAULT '새 페이지' COMMENT '페이지 제목 (생성시 임시 제목)',
  `content` longtext DEFAULT NULL COMMENT '페이지 내용 (자동 저장)',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '상태 (DRAFT=임시저장, PUBLISHED=배포됨)',
  `testament` varchar(10) DEFAULT NULL COMMENT '신구약 (OLD/NEW)',
  `book_code` varchar(20) DEFAULT NULL COMMENT '성경책 코드 (예: Gen, Exod, Ps, John)',
  `chapter_from` int(11) DEFAULT NULL COMMENT '시작 장',
  `verse_from` int(11) DEFAULT NULL COMMENT '시작 절',
  `chapter_to` int(11) DEFAULT NULL COMMENT '종료 장 (NULL=단일 장)',
  `verse_to` int(11) DEFAULT NULL COMMENT '종료 절 (NULL=해당 장 전체)',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '폴더 내 정렬 순서',
  `published_at` datetime DEFAULT NULL COMMENT '배포 일시 (status=PUBLISHED 시 설정)',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`page_id`),
  KEY `idx_page_folder` (`folder_id`),
  KEY `idx_page_user` (`user_id`),
  KEY `idx_page_status` (`status`),
  KEY `idx_page_book` (`book_code`,`chapter_from`,`verse_from`),
  CONSTRAINT `fk_page_folder` FOREIGN KEY (`folder_id`) REFERENCES `pbs_folder` (`folder_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_page_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 페이지 (Confluence 스타일)';


-- ---------- pbs_page_section ----------
CREATE TABLE `pbs_page_section` (
  `section_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '섹션 ID (PK)',
  `page_id` bigint(20) NOT NULL COMMENT '페이지 ID (FK)',
  `section_title` varchar(100) NOT NULL COMMENT '영역 제목 (예: 관찰, 해석, 적용)',
  `content` longtext DEFAULT NULL COMMENT '영역 내용',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '정렬 순서',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`section_id`),
  KEY `idx_section_page` (`page_id`),
  CONSTRAINT `fk_section_page` FOREIGN KEY (`page_id`) REFERENCES `pbs_page` (`page_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 페이지 영역 (관찰/해석/적용 등)';


-- ---------- pbs_reference ----------
CREATE TABLE `pbs_reference` (
  `ref_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '참고자료 ID (PK)',
  `page_id` bigint(20) NOT NULL COMMENT '페이지 ID (FK)',
  `ref_type` varchar(20) NOT NULL COMMENT '자료 유형 (URL/IMAGE/DOCUMENT)',
  `ref_name` varchar(200) NOT NULL COMMENT '자료명',
  `ref_url` varchar(500) DEFAULT NULL COMMENT 'URL (ref_type=URL)',
  `file_path` varchar(500) DEFAULT NULL COMMENT '파일 경로 (ref_type=IMAGE/DOCUMENT)',
  `file_size` bigint(20) DEFAULT NULL COMMENT '파일 크기 (bytes)',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  PRIMARY KEY (`ref_id`),
  KEY `idx_ref_page` (`page_id`),
  CONSTRAINT `fk_ref_page` FOREIGN KEY (`page_id`) REFERENCES `pbs_page` (`page_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS 참고자료';


-- ---------- pbs_tag_map ----------
CREATE TABLE `pbs_tag_map` (
  `map_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '매핑 ID (PK)',
  `page_id` bigint(20) NOT NULL COMMENT '페이지 ID (FK)',
  `tag_id` bigint(20) NOT NULL COMMENT '태그 ID (FK)',
  PRIMARY KEY (`map_id`),
  UNIQUE KEY `uk_pbs_tag` (`page_id`,`tag_id`),
  KEY `fk_pbs_tag_tag` (`tag_id`),
  CONSTRAINT `fk_pbs_tag_page` FOREIGN KEY (`page_id`) REFERENCES `pbs_page` (`page_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pbs_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PBS-태그 매핑';
