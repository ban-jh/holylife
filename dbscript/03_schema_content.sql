-- ============================================================
-- 03. 콘텐츠 테이블
--   sermons, sermon_tags, qts, qt_tags, qt_daily, tags, reference_resources, reading_notes
--   P.B.S: pbs_folder, pbs_page, pbs_page_section, pbs_reference, pbs_tag_map
--   (현재 운영 DB의 SHOW CREATE TABLE 결과와 동일)
-- ============================================================


-- ---------- tags ----------
CREATE TABLE `tags` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '태그 ID (PK)',
  `tag_name` varchar(50) NOT NULL COMMENT '태그명',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tags_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='태그 정보 (공통)';


-- ---------- sermons ----------
CREATE TABLE `sermons` (
  `sermon_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '설교 ID (PK)',
  `user_id` bigint(20) NOT NULL COMMENT '작성자 ID (FK)',
  `title` varchar(200) NOT NULL COMMENT '설교 제목',
  `sermon_date` date NOT NULL COMMENT '설교 날짜',
  `preacher` varchar(100) NOT NULL COMMENT '설교자',
  `bible_ref` varchar(100) DEFAULT NULL COMMENT '성경 구절 참조',
  `bible_text` text DEFAULT NULL COMMENT '성경 본문',
  `content` text DEFAULT NULL COMMENT '설교 내용',
  `status` varchar(20) NOT NULL COMMENT '상태 (DRAFT/PUBLISHED)',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`sermon_id`),
  KEY `fk_sermons_user` (`user_id`),
  CONSTRAINT `fk_sermons_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='설교 정보';


-- ---------- sermon_tags ----------
CREATE TABLE `sermon_tags` (
  `sermon_tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '매핑 ID (PK)',
  `sermon_id` bigint(20) NOT NULL COMMENT '설교 ID (FK)',
  `tag_id` bigint(20) NOT NULL COMMENT '태그 ID (FK)',
  PRIMARY KEY (`sermon_tag_id`),
  UNIQUE KEY `uk_sermon_tags` (`sermon_id`,`tag_id`),
  KEY `fk_sermon_tags_tag` (`tag_id`),
  CONSTRAINT `fk_sermon_tags_sermon` FOREIGN KEY (`sermon_id`) REFERENCES `sermons` (`sermon_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sermon_tags_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='설교-태그 매핑';


-- ---------- qts ----------
CREATE TABLE `qts` (
  `qt_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'QT ID (PK)',
  `user_id` bigint(20) NOT NULL COMMENT '작성자 ID (FK)',
  `title` varchar(200) NOT NULL COMMENT 'QT 제목',
  `qt_date` date NOT NULL COMMENT 'QT 날짜',
  `bible_ref` varchar(100) DEFAULT NULL COMMENT '성경 구절 참조',
  `bible_text` text DEFAULT NULL COMMENT '성경 본문',
  `content` text DEFAULT NULL COMMENT 'QT 내용 (묵상/적용)',
  `visibility` varchar(20) NOT NULL COMMENT '공개 범위 (PUBLIC/PRIVATE)',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`qt_id`),
  KEY `fk_qts_user` (`user_id`),
  CONSTRAINT `fk_qts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='큐티(QT) 정보';


-- ---------- qt_tags ----------
CREATE TABLE `qt_tags` (
  `qt_tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '매핑 ID (PK)',
  `qt_id` bigint(20) NOT NULL COMMENT 'QT ID (FK)',
  `tag_id` bigint(20) NOT NULL COMMENT '태그 ID (FK)',
  PRIMARY KEY (`qt_tag_id`),
  UNIQUE KEY `uk_qt_tags` (`qt_id`,`tag_id`),
  KEY `fk_qt_tags_tag` (`tag_id`),
  CONSTRAINT `fk_qt_tags_qt` FOREIGN KEY (`qt_id`) REFERENCES `qts` (`qt_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_qt_tags_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='QT-태그 매핑';


-- ---------- qt_daily ----------
CREATE TABLE `qt_daily` (
  `qt_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'QT ID (PK)',
  `qt_date` date NOT NULL COMMENT 'QT 날짜',
  `bible_ref` varchar(100) NOT NULL COMMENT '성경 구절 참조',
  `title` varchar(200) NOT NULL COMMENT 'QT 제목',
  `hymn_info` varchar(200) DEFAULT NULL COMMENT '찬송가 정보',
  `hymn_text` text DEFAULT NULL COMMENT '찬송가 가사',
  `bible_section_title` varchar(200) DEFAULT NULL COMMENT '성경 구절 제목',
  `bible_text` text DEFAULT NULL COMMENT '성경 본문',
  `prayer` text DEFAULT NULL COMMENT '기도문',
  `source` varchar(50) DEFAULT NULL COMMENT '출처',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  PRIMARY KEY (`qt_id`),
  UNIQUE KEY `uk_qt_date_source` (`qt_date`,`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='매일 QT (자동 수집 데이터)';


-- ---------- reading_notes ----------
CREATE TABLE `reading_notes` (
  `reading_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '독서 노트 ID (PK)',
  `user_id` bigint(20) NOT NULL COMMENT '작성자 ID (FK)',
  `book_title` varchar(200) NOT NULL COMMENT '도서 제목',
  `read_date` date NOT NULL COMMENT '독서 날짜',
  `author` varchar(100) DEFAULT NULL COMMENT '저자',
  `translator` varchar(100) DEFAULT NULL COMMENT '번역자',
  `toc` text DEFAULT NULL COMMENT '목차',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '책 표지 이미지 경로',
  `summary` text DEFAULT NULL COMMENT '요약',
  `impression` text DEFAULT NULL COMMENT '감상/소감',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`reading_id`),
  KEY `fk_reading_notes_user` (`user_id`),
  CONSTRAINT `fk_reading_notes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='독서 노트 정보';


-- ---------- reference_resources ----------
CREATE TABLE `reference_resources` (
  `ref_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '참고자료 ID (PK)',
  `ref_type` varchar(30) NOT NULL COMMENT '참조 대상 타입 (SERMON/QT/PBS/READING)',
  `target_id` bigint(20) NOT NULL COMMENT '참조 대상 ID',
  `ref_category` varchar(20) NOT NULL COMMENT '자료 카테고리 (URL/FILE)',
  `url` varchar(500) DEFAULT NULL COMMENT 'URL',
  `file_path` varchar(500) DEFAULT NULL COMMENT '파일 경로',
  `file_name` varchar(200) DEFAULT NULL COMMENT '파일명',
  `file_type` varchar(20) DEFAULT NULL COMMENT '파일 타입',
  `file_size` varchar(20) DEFAULT NULL COMMENT '파일 크기',
  `description` text DEFAULT NULL COMMENT '설명',
  `sort_order` int(11) NOT NULL COMMENT '정렬 순서',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  PRIMARY KEY (`ref_id`),
  KEY `idx_ref_target` (`ref_type`,`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='참고 자료 정보 (URL/파일)';


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
