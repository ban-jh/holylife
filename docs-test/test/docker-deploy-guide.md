# holylife Docker 배포 가이드

| 항목 | 내용 |
|------|------|
| 문서 버전 | v1.0 |
| 작성일 | 2026-10-28 |
| 대상 환경 | Oracle Cloud Free Tier (ARM Ampere A1, Ubuntu 22.04) |

---

## 1. Docker 파일 구성

```
holylife/
├── Dockerfile                    # 백엔드 (Spring Boot)
├── docker-compose.yml            # 전체 서비스 orchestration
├── .dockerignore                 # 백엔드 빌드 제외 파일
└── docker/
    └── init-db.sql               # DB 초기화 (스키마 + 데이터 덤프)

holylifeView/
├── Dockerfile                    # 프론트엔드 (Vue + Nginx)
├── .dockerignore                 # 프론트엔드 빌드 제외 파일
└── docker/
    └── nginx.conf                # Nginx 설정 (정적 서빙 + API 프록시)
```

---

## 2. 아키텍처

```
                    :80 (HTTP)
                       │
              ┌────────┴────────┐
              │  Nginx (frontend) │
              │  Vue 정적 파일 서빙  │
              └────────┬────────┘
                       │ /api/* → proxy_pass
              ┌────────┴────────┐
              │  Spring Boot      │
              │  (backend :8080)  │
              └────────┬────────┘
                       │ JDBC
              ┌────────┴────────┐
              │  MariaDB          │
              │  (mariadb :3306)  │
              └───────────────────┘
```

---

## 3. Oracle Cloud VM 설정

### 3.1 VM 생성 후 초기 설정

```bash
# 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# Docker 설치
curl -fsSL https://get.docker.com | sh

# Docker Compose 설치 (v2)
sudo apt install docker-compose-plugin -y

# 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER
newgrp docker

# 방화벽 설정 (필요시)
sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 443 -j ACCEPT
sudo netfilter-persistent save
```

### 3.2 Oracle Cloud 보안 목록(Security List) 설정

OCI 콘솔 → Networking → VCN → Security Lists → Ingress Rules:
- Source: 0.0.0.0/0, Protocol: TCP, Dest Port: 80 (HTTP)
- Source: 0.0.0.0/0, Protocol: TCP, Dest Port: 443 (HTTPS)

---

## 4. 배포 절차

### 4.1 소스 코드 가져오기

```bash
# Git 클론
git clone https://github.com/ban-jh/holylife.git
cd holylife
```

### 4.2 docker-compose.yml 위치 확인

docker-compose.yml은 holylife/holylife/ 디렉토리 안에 있습니다.
frontend 빌드를 위해 holylifeView도 같은 부모 디렉토리에 있어야 합니다.

```bash
# 디렉토리 구조
holylife/           # Git 클론
├── holylife/       # 백엔드 (docker-compose.yml 위치)
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── docker/
└── holylifeView/   # 프론트엔드 (별도 클론 필요)
    ├── Dockerfile
    └── docker/
```

```bash
# 프론트엔드도 클론 (백엔드와 같은 부모 디렉토리)
cd ..
git clone https://github.com/ban-jh/holylifeView.git
```

### 4.3 서비스 시작

```bash
cd holylife/holylife

# 전체 빌드 + 시작 (백그라운드)
docker compose up -d --build

# 상태 확인
docker compose ps

# 로그 확인
docker compose logs -f backend
```

### 4.4 서비스 중지/재시작

```bash
# 중지
docker compose down

# 재시작
docker compose restart backend

# 전체 재빌드
docker compose up -d --build
```

---

## 5. 확인

### 5.1 서비스 상태

```bash
docker compose ps
```

| 컨테이너 | 포트 | 상태 |
|----------|------|------|
| holylife-db | 3306 | healthy |
| holylife-backend | 8080 | running |
| holylife-frontend | 80 | running |

### 5.2 API 테스트

```bash
# 로그인
curl -s -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@holylife.local","password":"admin123"}'

# 메뉴 트리
curl -s http://localhost/api/menus/tree
```

### 5.3 웹 접속

브라우저에서 `http://(VM 공인 IP)` 접속 → 로그인 화면 → 대시보드

---

## 6. SSL 설정 (Let's Encrypt)

```bash
# Certbot 설치
sudo apt install certbot python3-certbot-nginx -y

# 인증서 발급 (도메인 필요)
sudo certbot --nginx -d yourdomain.com

# 자동 갱신 확인
sudo certbot renew --dry-run
```

---

## 7. 백업

### 7.1 DB 백업

```bash
docker exec holylife-db mysqldump -u shaloman -p123321 holylife > backup_$(date +%Y%m%d).sql
```

### 7.2 DB 복원

```bash
docker exec -i holylife-db mysql -u shaloman -p123321 holylife < backup_20261028.sql
```

---

## 8. 문제 해결

| 문제 | 해결 방법 |
|------|----------|
| VM 생성 시 "Out of capacity" | 시간을 두고 재시도, 또는 춘천/일본 리전 선택 |
| Docker 빌드 실패 | `docker compose build --no-cache` 로 재빌드 |
| 백엔드 연결 실패 | `docker compose logs backend` 로 로그 확인 |
| DB 연결 실패 | MariaDB 컨테이너 healthy 상태 확인 |
| 프론트엔드 안 됨 | `docker compose logs frontend` 로 Nginx 로그 확인 |
| 포트 충돌 | `docker compose down` 후 `docker compose up -d` |
