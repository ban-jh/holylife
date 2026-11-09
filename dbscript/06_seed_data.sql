-- ============================================================
-- 06. 초기 데이터 (Seed Data)
-- 관리자 계정, 권한, 메뉴, 공통코드, 성경 버전, 설교자
-- ============================================================

-- ---------- roles (권한 그룹) ----------
INSERT INTO roles (role_id, group_code, group_name, group_desc, use_yn) VALUES
(1, 'AGENT_DEV', 'Agent 개발자', '콘텐츠 개발 및 관리', 1),
(2, 'SYS_ADMIN', '시스템 관리자', '전체 시스템 관리', 1);

-- ---------- users (관리자 계정) ----------
-- 비밀번호: test12345 (BCrypt 해시)
INSERT INTO users (user_id, email, password, name, nickname, role_group, account_status, bible_version, qt_source) VALUES
(1, 'admin@holylife.local', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MrqJ3k0z4Z4Z4Z4Z4Z4Z4Z4Z4Z4Z4Z4', '관리자', '관리자', 'SYS_ADMIN', 'A', 'gae', 'duranno');

-- ---------- user_roles (사용자-권한 매핑) ----------
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2);

-- ---------- group_codes (그룹 코드) ----------
INSERT INTO group_codes (group_code, group_name) VALUES
('user_role',   '사용자 권한'),
('user_status', '계정 상태'),
('preacher',    '설교자');

-- ---------- common_codes (공통 코드) ----------
INSERT INTO common_codes (group_code, code, code_name, sort_order, use_yn) VALUES
('user_role',   'AGENT_DEV', 'Agent 개발자',   1, 1),
('user_role',   'SYS_ADMIN', '시스템 관리자', 2, 1),
('user_status', 'A',        '활성',           1, 1),
('user_status', 'I',        '비활성',         2, 1),
('user_status', 'W',        '탈퇴',           3, 1),
('preacher',    '권대선',   '권대선 담임 목사', 1, 1),
('preacher',    '최세광',   '최세광 목사',     2, 1),
('preacher',    '박명선',   '박명선 목사',     3, 1);

-- ---------- bible_versions (성경 버전) ----------
INSERT INTO bible_versions (version_id, version_name, language, use_yn) VALUES
(1, '개역한글',   'ko', 0),
(2, '개역개정',   'ko', 1),
(3, '새번역',     'ko', 1),
(4, '현대인성경', 'ko', 0),
(5, 'KJV',       'en', 0),
(6, 'NIV',       'en', 1);

-- ---------- menus (시스템 메뉴) ----------
INSERT INTO menus (menu_id, parent_id, parent_name, menu_name, menu_code, menu_url, depth, sort_order, icon, use_yn, display_yn) VALUES
(1,  NULL, NULL,    '시스템 관리',   'system',    '/system',         1, 1,  '⚙️',  1, 1),
(2,  1,    '시스템 관리', '사용자 관리',   'users',     '/users',          2, 1,  '👥',  1, 1),
(3,  1,    '시스템 관리', '권한 관리',     'roles',     '/roles',          2, 2,  '🛡️', 1, 1),
(4,  1,    '시스템 관리', '메뉴 관리',     'menus',     '/menus',          2, 3,  '📋',  1, 1),
(5,  1,    '시스템 관리', '공통코드',     'codes',     '/codes',          2, 4,  '🏷️', 1, 1),
(6,  NULL, NULL,    '콘텐츠 관리',   'content',   '/content',        1, 2,  '📖',  1, 1),
(7,  6,    '콘텐츠 관리', '설교',         'sermons',   '/sermons',        2, 1,  '⛪',  1, 1),
(8,  6,    '콘텐츠 관리', '큐티',         'qts',      '/qts',           2, 2,  '✨',  1, 1),
(9,  6,    '콘텐츠 관리', 'P.B.S',        'pbs',      '/pbs',           2, 3,  '📝',  1, 1),
(10, 6,    '콘텐츠 관리', '독서노트',     'readings',  '/readings',       2, 4,  '📚',  1, 1),
(11, NULL, NULL,    '마이페이지',     'mypage',    '/mypage',         1, 3,  '👤',  1, 1);