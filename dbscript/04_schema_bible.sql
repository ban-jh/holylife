-- ============================================================
-- 04. 성경 테이블
--   bible_versions, bible_books (66권 3역본, 약 9만 절)
--   (현재 운영 DB의 SHOW CREATE TABLE 결과와 동일)
-- ============================================================


-- ---------- bible_versions ----------
CREATE TABLE `bible_versions` (
  `version_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '버전 ID (PK)',
  `version_name` varchar(50) NOT NULL COMMENT '버전명',
  `language` varchar(10) NOT NULL COMMENT '언어',
  `use_yn` tinyint(1) NOT NULL COMMENT '사용 여부',
  PRIMARY KEY (`version_id`),
  UNIQUE KEY `uk_bible_versions_name` (`version_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='성경 버전 정보';


-- ---------- bible_books ----------
CREATE TABLE `bible_books` (
  `book_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '본문 ID (PK)',
  `version_id` bigint(20) NOT NULL COMMENT '버전 ID (FK)',
  `book_code` varchar(10) NOT NULL COMMENT '권 코드 (예: GEN, EXO)',
  `book_name_kr` varchar(20) NOT NULL COMMENT '권명 (한글)',
  `book_name_en` varchar(50) NOT NULL COMMENT '권명 (영문)',
  `testament` varchar(5) NOT NULL COMMENT '구분 (OT=구약, NT=신약)',
  `chapter` int(11) NOT NULL COMMENT '장 번호',
  `verse` int(11) NOT NULL COMMENT '절 번호',
  `content` text NOT NULL COMMENT '성경 본문 내용',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  PRIMARY KEY (`book_id`),
  KEY `idx_bible_lookup` (`version_id`,`book_code`,`chapter`,`verse`),
  KEY `idx_bible_book` (`book_code`,`chapter`,`verse`),
  CONSTRAINT `fk_bible_books_version` FOREIGN KEY (`version_id`) REFERENCES `bible_versions` (`version_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='성경 본문 데이터';
