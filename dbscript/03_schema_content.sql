-- ============================================================
-- 03. 콘텐츠 테이블 생성
-- sermons, sermon_tags, qts, qt_tags, qt_daily, pbs, pbs_tags,
-- reading_notes, tags, reference_resources
-- ============================================================

-- ---------- tags (태그 정보, 공통) ----------
CREATE TABLE tags (
    tag_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '태그 ID (PK)',
    tag_name             VARCHAR(50)  NOT NULL                COMMENT '태그명',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (tag_id),
    UNIQUE KEY uk_tags_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='태그 정보 (공통)';

-- ---------- sermons (설교 정보) ----------
CREATE TABLE sermons (
    sermon_id            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '설교 ID (PK)',
    user_id              BIGINT       NOT NULL                COMMENT '작성자 ID (FK)',
    title               VARCHAR(200) NOT NULL                COMMENT '설교 제목',
    sermon_date          DATE         NOT NULL                COMMENT '설교 날짜',
    preacher            VARCHAR(100) NOT NULL                COMMENT '설교자',
    bible_ref           VARCHAR(100) NULL                    COMMENT '성경 구절 참조',
    bible_text          TEXT         NULL                    COMMENT '성경 본문',
    content             TEXT         NULL                    COMMENT '설교 내용',
    status              VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '상태 (DRAFT/PUBLISHED)',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '생성일시',
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (sermon_id),
    KEY fk_sermons_user (user_id),
    CONSTRAINT fk_sermons_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='설교 정보';

-- ---------- sermon_tags (설교-태그 매핑) ----------
CREATE TABLE sermon_tags (
    sermon_tag_id        BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '매핑 ID (PK)',
    sermon_id            BIGINT       NOT NULL                COMMENT '설교 ID (FK)',
    tag_id              BIGINT       NOT NULL                COMMENT '태그 ID (FK)',
    PRIMARY KEY (sermon_tag_id),
    UNIQUE KEY uk_sermon_tags (sermon_id, tag_id),
    CONSTRAINT fk_sermon_tags_sermon FOREIGN KEY (sermon_id) REFERENCES sermons (sermon_id) ON DELETE CASCADE,
    CONSTRAINT fk_sermon_tags_tag    FOREIGN KEY (tag_id)    REFERENCES tags (tag_id)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='설교-태그 매핑';

-- ---------- qts (큐티 정보) ----------
CREATE TABLE qts (
    qt_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT 'QT ID (PK)',
    user_id              BIGINT       NOT NULL                COMMENT '작성자 ID (FK)',
    title               VARCHAR(200) NOT NULL                COMMENT 'QT 제목',
    qt_date             DATE         NOT NULL                COMMENT 'QT 날짜',
    bible_ref           VARCHAR(100) NULL                    COMMENT '성경 구절 참조',
    bible_text          TEXT         NULL                    COMMENT '성경 본문',
    content             TEXT         NULL                    COMMENT 'QT 내용 (묵상/적용)',
    visibility          VARCHAR(20)  NOT NULL DEFAULT 'PRIVATE' COMMENT '공개 범위 (PUBLIC/PRIVATE)',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '생성일시',
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (qt_id),
    KEY fk_qts_user (user_id),
    CONSTRAINT fk_qts_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='큐티(QT) 정보';

-- ---------- qt_tags (QT-태그 매핑) ----------
CREATE TABLE qt_tags (
    qt_tag_id           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '매핑 ID (PK)',
    qt_id               BIGINT       NOT NULL                COMMENT 'QT ID (FK)',
    tag_id              BIGINT       NOT NULL                COMMENT '태그 ID (FK)',
    PRIMARY KEY (qt_tag_id),
    UNIQUE KEY uk_qt_tags (qt_id, tag_id),
    CONSTRAINT fk_qt_tags_qt  FOREIGN KEY (qt_id)  REFERENCES qts (qt_id)  ON DELETE CASCADE,
    CONSTRAINT fk_qt_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='QT-태그 매핑';

-- ---------- qt_daily (매일 QT 자동 수집 데이터) ----------
CREATE TABLE qt_daily (
    qt_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT 'QT ID (PK)',
    qt_date             DATE         NOT NULL                COMMENT 'QT 날짜',
    bible_ref           VARCHAR(100) NOT NULL                COMMENT '성경 구절 참조',
    title               VARCHAR(200) NOT NULL                COMMENT 'QT 제목',
    hymn_info           VARCHAR(200) NULL                    COMMENT '찬송가 정보',
    hymn_text           TEXT         NULL                    COMMENT '찬송가 가사',
    bible_section_title  VARCHAR(200) NULL                    COMMENT '성경 구절 제목',
    bible_text          TEXT         NULL                    COMMENT '성경 본문',
    prayer              TEXT         NULL                    COMMENT '기도문',
    source              VARCHAR(50)  NULL                    COMMENT '출처 (duranno/sum)',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (qt_id),
    UNIQUE KEY uk_qt_date_source (qt_date, source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='매일 QT (자동 수집 데이터)';

-- ---------- pbs_folder (P.B.S 폴더 트리) ----------
CREATE TABLE pbs_folder (
    folder_id            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '폴더 ID (PK)',
    parent_id            BIGINT       NULL                    COMMENT '부모 폴더 ID (FK, NULL=루트)',
    folder_name          VARCHAR(100) NOT NULL                COMMENT '폴더명 (예: 창세기)',
    sort_order           INT          NOT NULL DEFAULT 0      COMMENT '정렬 순서',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (folder_id),
    KEY idx_folder_parent (parent_id),
    CONSTRAINT fk_folder_parent FOREIGN KEY (parent_id) REFERENCES pbs_folder (folder_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='P.B.S 폴더 (트리)';

-- ---------- pbs_page (P.B.S 페이지 — Confluence 스타일) ----------
CREATE TABLE pbs_page (
    page_id              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '페이지 ID (PK)',
    folder_id            BIGINT       NOT NULL                COMMENT '폴더 ID (FK)',
    user_id              BIGINT       NOT NULL                COMMENT '작성자 ID (FK)',
    title                VARCHAR(200) NOT NULL DEFAULT '새 페이지' COMMENT '페이지 제목',
    content              LONGTEXT     NULL                    COMMENT '페이지 내용 (자동 저장)',
    status               VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '상태 (DRAFT/PUBLISHED)',
    testament            VARCHAR(10)  NULL                    COMMENT '신구약 (OLD/NEW)',
    book_code            VARCHAR(20)  NULL                    COMMENT '성경책 코드 (예: Gen)',
    chapter_from         INT          NULL                    COMMENT '시작 장',
    verse_from           INT          NULL                    COMMENT '시작 절',
    chapter_to           INT          NULL                    COMMENT '종료 장 (NULL=단일 장)',
    verse_to             INT          NULL                    COMMENT '종료 절 (NULL=장 전체)',
    sort_order           INT          NOT NULL DEFAULT 0      COMMENT '폴더 내 정렬 순서',
    published_at         DATETIME     NULL                    COMMENT '배포 일시',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (page_id),
    KEY idx_page_folder (folder_id),
    KEY idx_page_user (user_id),
    KEY idx_page_status (status),
    KEY idx_page_book (book_code, chapter_from, verse_from),
    CONSTRAINT fk_page_folder FOREIGN KEY (folder_id) REFERENCES pbs_folder (folder_id) ON DELETE CASCADE,
    CONSTRAINT fk_page_user   FOREIGN KEY (user_id)   REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='P.B.S 페이지';

-- ---------- pbs_page_section (영역별 내용 — 관찰/해석/적용) ----------
CREATE TABLE pbs_page_section (
    section_id           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '영역 ID (PK)',
    page_id              BIGINT       NOT NULL                COMMENT '페이지 ID (FK)',
    section_title        VARCHAR(100) NOT NULL                COMMENT '영역 제목',
    content              LONGTEXT     NULL                    COMMENT '영역 내용',
    sort_order           INT          NOT NULL DEFAULT 0      COMMENT '정렬 순서',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (section_id),
    KEY idx_section_page (page_id),
    CONSTRAINT fk_section_page FOREIGN KEY (page_id) REFERENCES pbs_page (page_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='P.B.S 영역별 내용';

-- ---------- pbs_reference (참고자료) ----------
CREATE TABLE pbs_reference (
    ref_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '참고자료 ID (PK)',
    page_id              BIGINT       NOT NULL                COMMENT '페이지 ID (FK)',
    ref_type             VARCHAR(20)  NOT NULL                COMMENT '유형 (URL/IMAGE/DOCUMENT)',
    ref_name             VARCHAR(200) NOT NULL                COMMENT '자료명',
    ref_url              VARCHAR(500) NULL                    COMMENT 'URL',
    file_path            VARCHAR(500) NULL                    COMMENT '파일 경로',
    file_size            BIGINT       NULL                    COMMENT '파일 크기 (bytes)',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (ref_id),
    KEY idx_ref_page (page_id),
    CONSTRAINT fk_ref_page FOREIGN KEY (page_id) REFERENCES pbs_page (page_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='P.B.S 참고자료';

-- ---------- pbs_tag_map (P.B.S-태그 매핑) ----------
CREATE TABLE pbs_tag_map (
    map_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '매핑 ID (PK)',
    page_id              BIGINT       NOT NULL                COMMENT '페이지 ID (FK)',
    tag_id               BIGINT       NOT NULL                COMMENT '태그 ID (FK)',
    PRIMARY KEY (map_id),
    UNIQUE KEY uk_pbs_tag (page_id, tag_id),
    CONSTRAINT fk_pbs_tag_page FOREIGN KEY (page_id) REFERENCES pbs_page (page_id) ON DELETE CASCADE,
    CONSTRAINT fk_pbs_tag_tag  FOREIGN KEY (tag_id)  REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='P.B.S-태그 매핑';

-- ---------- reading_notes (독서 노트 정보) ----------
CREATE TABLE reading_notes (
    reading_id           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '독서 노트 ID (PK)',
    user_id              BIGINT       NOT NULL                COMMENT '작성자 ID (FK)',
    book_title           VARCHAR(200) NOT NULL                COMMENT '도서 제목',
    read_date            DATE         NOT NULL                COMMENT '독서 날짜',
    author              VARCHAR(100) NULL                    COMMENT '저자',
    translator          VARCHAR(100) NULL                    COMMENT '번역자',
    toc                 TEXT         NULL                    COMMENT '목차',
    cover_image         VARCHAR(500) NULL                    COMMENT '책 표지 이미지 경로',
    summary             TEXT         NULL                    COMMENT '요약',
    impression          TEXT         NULL                    COMMENT '감상/소감',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '생성일시',
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (reading_id),
    KEY fk_reading_notes_user (user_id),
    CONSTRAINT fk_reading_notes_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='독서 노트 정보';

-- ---------- reference_resources (참고 자료 정보) ----------
CREATE TABLE reference_resources (
    ref_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '참고자료 ID (PK)',
    ref_type             VARCHAR(30)  NOT NULL                COMMENT '참조 대상 타입 (SERMON/QT/PBS/READING)',
    target_id            BIGINT       NOT NULL                COMMENT '참조 대상 ID',
    ref_category         VARCHAR(20)  NOT NULL                COMMENT '자료 카테고리 (URL/FILE)',
    url                 VARCHAR(500) NULL                    COMMENT 'URL',
    file_path           VARCHAR(500) NULL                    COMMENT '파일 경로',
    file_name           VARCHAR(200) NULL                    COMMENT '파일명',
    file_type           VARCHAR(20)  NULL                    COMMENT '파일 타입',
    file_size           VARCHAR(20)  NULL                    COMMENT '파일 크기',
    description         TEXT         NULL                    COMMENT '설명',
    sort_order           INT          NOT NULL DEFAULT 0        COMMENT '정렬 순서',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (ref_id),
    KEY idx_ref_target (ref_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='참고 자료 정보 (URL/파일)';