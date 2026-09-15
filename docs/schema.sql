-- ============================================================
-- Faith Pathway (Holylife) 테이블 생성 스크립트
-- ERD 기반: 17 엔티티
-- ============================================================

-- 1. USER (사용자)
CREATE TABLE IF NOT EXISTS users (
    user_id              BIGINT       NOT NULL AUTO_INCREMENT,
    email                VARCHAR(255) NOT NULL,
    password             VARCHAR(255) NOT NULL,
    name                 VARCHAR(100) NOT NULL,
    nickname             VARCHAR(100) NOT NULL,
    phone                VARCHAR(20)  NULL,
    role_group           VARCHAR(50)  NOT NULL DEFAULT 'USER',
    account_status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    memo                 VARCHAR(500) NULL,
    email_notification   BOOLEAN      NOT NULL DEFAULT FALSE,
    two_factor_auth      BOOLEAN      NOT NULL DEFAULT FALSE,
    session_auto_expiry  BOOLEAN      NOT NULL DEFAULT FALSE,
    activity_log         BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login           DATETIME     NULL,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. ROLE (역할 / 권한 그룹)
CREATE TABLE IF NOT EXISTS roles (
    role_id              BIGINT       NOT NULL AUTO_INCREMENT,
    group_code           VARCHAR(20)  NOT NULL,
    group_name           VARCHAR(50)  NOT NULL,
    group_desc           VARCHAR(200) NULL,
    use_yn               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_roles_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. USER_ROLE (사용자-역할 매핑)
CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id              BIGINT       NOT NULL,
    role_id              BIGINT       NOT NULL,
    PRIMARY KEY (user_role_id),
    UNIQUE KEY uk_user_roles (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. MENU (메뉴)
CREATE TABLE IF NOT EXISTS menus (
    menu_id              BIGINT       NOT NULL AUTO_INCREMENT,
    parent_id            BIGINT       NULL,
    parent_name          VARCHAR(100) NULL,
    menu_name            VARCHAR(100) NOT NULL,
    menu_code            VARCHAR(50)  NOT NULL,
    menu_url             VARCHAR(255) NOT NULL,
    depth                INT          NOT NULL DEFAULT 1,
    sort_order           INT          NOT NULL DEFAULT 0,
    icon                 VARCHAR(100) NULL,
    use_yn               BOOLEAN      NOT NULL DEFAULT TRUE,
    display_yn           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (menu_id),
    CONSTRAINT fk_menus_parent FOREIGN KEY (parent_id) REFERENCES menus (menu_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. PERMISSION (권한)
CREATE TABLE IF NOT EXISTS permissions (
    permission_id        BIGINT       NOT NULL AUTO_INCREMENT,
    role_id              BIGINT       NOT NULL,
    menu_id              BIGINT       NOT NULL,
    can_read             BOOLEAN      NOT NULL DEFAULT FALSE,
    can_write            BOOLEAN      NOT NULL DEFAULT FALSE,
    can_delete           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (permission_id),
    UNIQUE KEY uk_permissions (role_id, menu_id),
    CONSTRAINT fk_permissions_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    CONSTRAINT fk_permissions_menu FOREIGN KEY (menu_id) REFERENCES menus (menu_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6-1. GROUP_CODE (그룹 코드)
CREATE TABLE IF NOT EXISTS group_codes (
    group_code           VARCHAR(50)  NOT NULL,
    group_name           VARCHAR(100) NOT NULL,
    modified_date        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6-2. COMMON_CODE (상세 코드)
CREATE TABLE IF NOT EXISTS common_codes (
    code_id              BIGINT       NOT NULL AUTO_INCREMENT,
    group_code           VARCHAR(50)  NOT NULL,
    code                 VARCHAR(50)  NOT NULL,
    code_name            VARCHAR(100) NOT NULL,
    sort_order           INT          NOT NULL DEFAULT 0,
    use_yn               BOOLEAN      NOT NULL DEFAULT TRUE,
    modified_date        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (code_id),
    UNIQUE KEY uk_common_codes (group_code, code),
    CONSTRAINT fk_common_codes_group FOREIGN KEY (group_code) REFERENCES group_codes (group_code) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. TAG (태그)
CREATE TABLE IF NOT EXISTS tags (
    tag_id               BIGINT       NOT NULL AUTO_INCREMENT,
    tag_name             VARCHAR(50)  NOT NULL,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tag_id),
    UNIQUE KEY uk_tags_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. SERMON (설교)
CREATE TABLE IF NOT EXISTS sermons (
    sermon_id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id              BIGINT       NOT NULL,
    title                VARCHAR(200) NOT NULL,
    sermon_date          DATE         NOT NULL,
    preacher             VARCHAR(100) NOT NULL,
    bible_ref            VARCHAR(100) NULL,
    bible_text           TEXT         NULL,
    content              TEXT         NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sermon_id),
    CONSTRAINT fk_sermons_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. SERMON_TAG (설교-태그 매핑)
CREATE TABLE IF NOT EXISTS sermon_tags (
    sermon_tag_id        BIGINT       NOT NULL AUTO_INCREMENT,
    sermon_id            BIGINT       NOT NULL,
    tag_id               BIGINT       NOT NULL,
    PRIMARY KEY (sermon_tag_id),
    UNIQUE KEY uk_sermon_tags (sermon_id, tag_id),
    CONSTRAINT fk_sermon_tags_sermon FOREIGN KEY (sermon_id) REFERENCES sermons (sermon_id) ON DELETE CASCADE,
    CONSTRAINT fk_sermon_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. PBS (성경공부)
CREATE TABLE IF NOT EXISTS pbs (
    pbs_id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_id              BIGINT       NOT NULL,
    title                VARCHAR(200) NOT NULL,
    pbs_date             DATE         NOT NULL,
    book_from            VARCHAR(50)  NULL,
    book_to              VARCHAR(50)  NULL,
    bible_text           TEXT         NULL,
    content              TEXT         NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (pbs_id),
    CONSTRAINT fk_pbs_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. PBS_TAG (성경공부-태그 매핑)
CREATE TABLE IF NOT EXISTS pbs_tags (
    pbs_tag_id           BIGINT       NOT NULL AUTO_INCREMENT,
    pbs_id               BIGINT       NOT NULL,
    tag_id               BIGINT       NOT NULL,
    PRIMARY KEY (pbs_tag_id),
    UNIQUE KEY uk_pbs_tags (pbs_id, tag_id),
    CONSTRAINT fk_pbs_tags_pbs FOREIGN KEY (pbs_id) REFERENCES pbs (pbs_id) ON DELETE CASCADE,
    CONSTRAINT fk_pbs_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. QT (큐티)
CREATE TABLE IF NOT EXISTS qts (
    qt_id                BIGINT       NOT NULL AUTO_INCREMENT,
    user_id              BIGINT       NOT NULL,
    title                VARCHAR(200) NOT NULL,
    qt_date              DATE         NOT NULL,
    bible_ref            VARCHAR(100) NULL,
    bible_text           TEXT         NULL,
    content              TEXT         NULL,
    visibility           VARCHAR(20)  NOT NULL DEFAULT 'PUBLIC',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (qt_id),
    CONSTRAINT fk_qts_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. QT_TAG (큐티-태그 매핑)
CREATE TABLE IF NOT EXISTS qt_tags (
    qt_tag_id            BIGINT       NOT NULL AUTO_INCREMENT,
    qt_id                BIGINT       NOT NULL,
    tag_id               BIGINT       NOT NULL,
    PRIMARY KEY (qt_tag_id),
    UNIQUE KEY uk_qt_tags (qt_id, tag_id),
    CONSTRAINT fk_qt_tags_qt FOREIGN KEY (qt_id) REFERENCES qts (qt_id) ON DELETE CASCADE,
    CONSTRAINT fk_qt_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. READING_NOTE (독서 노트)
CREATE TABLE IF NOT EXISTS reading_notes (
    reading_id           BIGINT       NOT NULL AUTO_INCREMENT,
    user_id              BIGINT       NOT NULL,
    book_title           VARCHAR(200) NOT NULL,
    read_date            DATE         NOT NULL,
    author               VARCHAR(100) NULL,
    translator           VARCHAR(100) NULL,
    toc                  TEXT         NULL,
    cover_image          VARCHAR(500) NULL,
    summary              TEXT         NULL,
    impression           TEXT         NULL,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (reading_id),
    CONSTRAINT fk_reading_notes_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. REFERENCE_RESOURCE (참고자료 — 다형성 참조)
CREATE TABLE IF NOT EXISTS reference_resources (
    ref_id               BIGINT       NOT NULL AUTO_INCREMENT,
    ref_type             VARCHAR(30)  NOT NULL,
    target_id            BIGINT       NOT NULL,
    ref_category         VARCHAR(20)  NOT NULL,
    url                  VARCHAR(500) NULL,
    file_path            VARCHAR(500) NULL,
    file_name            VARCHAR(200) NULL,
    file_type            VARCHAR(20)  NULL,
    file_size            VARCHAR(20)  NULL,
    description          TEXT         NULL,
    sort_order           INT          NOT NULL DEFAULT 0,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ref_id),
    INDEX idx_ref_target (ref_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. BIBLE_VERSION (성경 버전)
CREATE TABLE IF NOT EXISTS bible_versions (
    version_id           BIGINT       NOT NULL AUTO_INCREMENT,
    version_name         VARCHAR(50)  NOT NULL,
    language             VARCHAR(10)  NOT NULL,
    use_yn               BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (version_id),
    UNIQUE KEY uk_bible_versions_name (version_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. BIBLE_TEXT (성경 본문)
CREATE TABLE IF NOT EXISTS bible_texts (
    bible_text_id        BIGINT       NOT NULL AUTO_INCREMENT,
    version_id           BIGINT       NOT NULL,
    book                 VARCHAR(50)  NOT NULL,
    chapter              INT          NOT NULL,
    verse                INT          NOT NULL,
    content              TEXT         NOT NULL,
    PRIMARY KEY (bible_text_id),
    UNIQUE KEY uk_bible_texts (version_id, book, chapter, verse),
    CONSTRAINT fk_bible_texts_version FOREIGN KEY (version_id) REFERENCES bible_versions (version_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;