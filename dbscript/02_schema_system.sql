-- ============================================================
-- 02. 시스템 테이블 생성
-- users, roles, user_roles, menus, permissions, group_codes, common_codes
-- ============================================================

-- ---------- users (사용자 정보) ----------
CREATE TABLE users (
    user_id              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '사용자 ID (PK)',
    email                VARCHAR(255) NOT NULL                COMMENT '이메일 주소 (로그인 ID)',
    password             VARCHAR(255) NOT NULL                COMMENT '비밀번호 (BCrypt 암호화)',
    name                 VARCHAR(100) NOT NULL                COMMENT '이름',
    nickname             VARCHAR(100) NOT NULL                COMMENT '닉네임',
    phone                VARCHAR(20)  NULL                    COMMENT '전화번호',
    role_group            VARCHAR(50)  NOT NULL               COMMENT '권한 그룹 코드',
    account_status        VARCHAR(20)  NOT NULL DEFAULT 'A'    COMMENT '계정 상태 (A=활성, I=비활성, W=탈퇴)',
    bible_version         VARCHAR(20)  NULL DEFAULT 'gae'      COMMENT '선호 성경 버전',
    qt_source            VARCHAR(20)  NULL DEFAULT 'duranno'   COMMENT 'QT 출처',
    memo                 VARCHAR(500) NULL                    COMMENT '메모',
    email_notification   TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '이메일 알림 수신 여부',
    two_factor_auth       TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '2단계 인증 사용 여부',
    session_auto_expiry   TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '세션 자동 만료 여부',
    activity_log          TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '활동 로그 기록 여부',
    last_login            DATETIME     NULL                    COMMENT '최종 로그인 일시',
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '생성일시',
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 정보';

-- ---------- roles (권한 그룹 정보) ----------
CREATE TABLE roles (
    role_id              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '권한 ID (PK)',
    group_code           VARCHAR(20)  NOT NULL                COMMENT '그룹 코드',
    group_name            VARCHAR(50)  NOT NULL                COMMENT '그룹명',
    group_desc            VARCHAR(200) NULL                    COMMENT '그룹 설명',
    use_yn               TINYINT(1)   NOT NULL DEFAULT 1       COMMENT '사용 여부',
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_roles_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권한 그룹 정보';

-- ---------- user_roles (사용자-권한 매핑) ----------
CREATE TABLE user_roles (
    user_role_id          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '매핑 ID (PK)',
    user_id               BIGINT       NOT NULL                COMMENT '사용자 ID (FK)',
    role_id               BIGINT       NOT NULL                COMMENT '권한 ID (FK)',
    PRIMARY KEY (user_role_id),
    UNIQUE KEY uk_user_roles (user_id, role_id),
    CONSTRAINT fk_user_roles_user  FOREIGN KEY (user_id)  REFERENCES users (user_id)  ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role  FOREIGN KEY (role_id)  REFERENCES roles (role_id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자-권한 그룹 매핑';

-- ---------- menus (시스템 메뉴 정보, 계층형) ----------
CREATE TABLE menus (
    menu_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '메뉴 ID (PK)',
    parent_id             BIGINT       NULL                    COMMENT '상위 메뉴 ID (self-reference, NULL=1Depth)',
    parent_name           VARCHAR(100) NULL                    COMMENT '상위 메뉴명 (표시용)',
    menu_name              VARCHAR(100) NOT NULL                COMMENT '메뉴명',
    menu_code             VARCHAR(50)  NOT NULL                COMMENT '메뉴 코드',
    menu_url              VARCHAR(255) NOT NULL                COMMENT '메뉴 URL',
    depth                 INT          NOT NULL                 COMMENT '메뉴 깊이 (1=1Depth, 2=2Depth)',
    sort_order            INT          NOT NULL DEFAULT 0        COMMENT '정렬 순서',
    icon                 VARCHAR(100) NULL                    COMMENT '메뉴 아이콘',
    use_yn               TINYINT(1)   NOT NULL DEFAULT 1       COMMENT '사용 여부',
    display_yn           TINYINT(1)   NOT NULL DEFAULT 1       COMMENT '노출 여부',
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '생성일시',
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (menu_id),
    KEY fk_menus_parent (parent_id),
    CONSTRAINT fk_menus_parent FOREIGN KEY (parent_id) REFERENCES menus (menu_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시스템 메뉴 정보 (계층형)';

-- ---------- permissions (권한 그룹-메뉴 접근 권한 매핑) ----------
CREATE TABLE permissions (
    permission_id          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '권한 매핑 ID (PK)',
    role_id               BIGINT       NOT NULL                COMMENT '권한 그룹 ID (FK)',
    menu_id               BIGINT       NOT NULL                COMMENT '메뉴 ID (FK)',
    can_read              TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '읽기 권한',
    can_write             TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '쓰기 권한',
    can_delete            TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '삭제 권한',
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (permission_id),
    UNIQUE KEY uk_permissions (role_id, menu_id),
    CONSTRAINT fk_permissions_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    CONSTRAINT fk_permissions_menu FOREIGN KEY (menu_id) REFERENCES menus (menu_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권한 그룹-메뉴 접근 권한 매핑';

-- ---------- group_codes (그룹 코드 정보) ----------
CREATE TABLE group_codes (
    group_code            VARCHAR(50)  NOT NULL                COMMENT '그룹 코드 (PK)',
    group_name            VARCHAR(100) NOT NULL                COMMENT '그룹 코드명',
    modified_date         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시',
    PRIMARY KEY (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='그룹 코드 정보';

-- ---------- common_codes (공통 코드 정보) ----------
CREATE TABLE common_codes (
    code_id               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '코드 ID (PK)',
    group_code            VARCHAR(50)  NOT NULL                COMMENT '그룹 코드 (FK)',
    code                 VARCHAR(50)  NOT NULL                COMMENT '코드값',
    code_name            VARCHAR(100) NOT NULL                COMMENT '코드명',
    sort_order            INT          NOT NULL DEFAULT 0        COMMENT '정렬 순서',
    use_yn               TINYINT(1)   NOT NULL DEFAULT 1       COMMENT '사용 여부',
    modified_date         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시',
    PRIMARY KEY (code_id),
    UNIQUE KEY uk_common_codes (group_code, code),
    CONSTRAINT fk_common_codes_group FOREIGN KEY (group_code) REFERENCES group_codes (group_code) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공통 코드 정보';