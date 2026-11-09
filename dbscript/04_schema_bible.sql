-- ============================================================
-- 04. 성경 테이블 생성
-- bible_versions, bible_books
-- ============================================================

-- ---------- bible_versions (성경 버전 정보) ----------
CREATE TABLE bible_versions (
    version_id           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '버전 ID (PK)',
    version_name          VARCHAR(50)  NOT NULL                COMMENT '버전명',
    language             VARCHAR(10)  NOT NULL                COMMENT '언어',
    use_yn               TINYINT(1)   NOT NULL DEFAULT 1       COMMENT '사용 여부',
    PRIMARY KEY (version_id),
    UNIQUE KEY uk_bible_versions_name (version_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='성경 버전 정보';

-- ---------- bible_books (성경 본문 데이터) ----------
CREATE TABLE bible_books (
    book_id              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '본문 ID (PK)',
    version_id           BIGINT       NOT NULL                COMMENT '버전 ID (FK)',
    book_code            VARCHAR(10)  NOT NULL                COMMENT '권 코드 (예: GEN, EXO)',
    book_name_kr          VARCHAR(20)  NOT NULL                COMMENT '권명 (한글)',
    book_name_en          VARCHAR(50)  NOT NULL                COMMENT '권명 (영문)',
    testament            VARCHAR(5)   NOT NULL                COMMENT '구분 (OT=구약, NT=신약)',
    chapter             INT          NOT NULL                COMMENT '장 번호',
    verse              INT          NOT NULL                COMMENT '절 번호',
    content             TEXT         NOT NULL                COMMENT '성경 본문 내용',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (book_id),
    KEY idx_bible_lookup (version_id, book_code, chapter, verse),
    KEY idx_bible_book (book_code, chapter, verse),
    CONSTRAINT fk_bible_books_version FOREIGN KEY (version_id) REFERENCES bible_versions (version_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='성경 본문 데이터';