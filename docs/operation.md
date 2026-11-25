# HolyLife Docker 배포 안내서

이 문서는 Ubuntu 서버에 HolyLife 백엔드와 프론트엔드를 Docker Compose로 배포하는 절차를 설명합니다. 서버의 MariaDB는 기존 별도 DB를 사용하며, 이 안내서에서 MariaDB 컨테이너를 새로 만들지는 않습니다.

> **포트 구성:** 외부 TCP `38080` → 프론트엔드 Nginx 컨테이너 `8080` → Spring Boot 백엔드 컨테이너 `8081`. 호스트에서 공개하는 서비스 포트는 `38080`뿐이며, 8080/8081은 Docker 내부에서 사용합니다.

> **브랜치 정책:** 개발은 각 저장소의 `dev` 브랜치에서 진행합니다. 서버 배포는 `dev`를 `main`에 병합하고 원격 `main`에 푸시한 다음, 서버에서 `main`을 받아 실행합니다.

## 1. 배포 구조와 서버 경로

### 간략 설명

Git 소스와 운영 데이터의 위치를 분리합니다. Docker Compose 명령은 백엔드 디렉터리에서 실행하며, 프론트엔드 빌드 경로는 그 옆의 `frontend` 디렉터리입니다.

### 명령어

```text
/opt/holylife/
├── backend/          백엔드 Git 저장소와 docker-compose.yml
├── frontend/         프론트엔드 Git 저장소
├── config/.env       DB 접속 정보와 JWT 키 (Git에 올리지 않음)
├── uploads/          업로드 파일 보관 (컨테이너 교체 후에도 유지)
└── logs/              백엔드 로그 보관
```

### 자세한 설명

<details>
<summary>각 경로가 사용되는 이유</summary>

- `/opt/holylife/backend`: Spring Boot 소스, 백엔드 Dockerfile, Compose 파일을 둡니다.
- `/opt/holylife/frontend`: Vue 소스, 프론트엔드 Dockerfile, `docker/nginx.conf`를 둡니다.
- `/opt/holylife/config/.env`: DB 주소/계정/비밀번호와 JWT 서명 키를 둡니다. 저장소 안의 `.env`와 분리해 운영 비밀값이 Git에 올라가지 않게 합니다.
- `/opt/holylife/uploads`: 사용자가 업로드한 이미지 파일을 둡니다. 백엔드 컨테이너의 `/opt/holylife/uploads`와 연결합니다.
- `/opt/holylife/logs`: 백엔드 로그를 둡니다. 백엔드 컨테이너의 `/opt/holylife/logs`와 연결합니다.

프론트엔드 컨테이너는 빌드된 웹 파일을 `/usr/share/nginx/html`에서 제공합니다. 백엔드 컨테이너는 JAR을 `/app/app.jar`로 실행합니다. 두 경로는 컨테이너 안의 경로이며 서버의 소스 디렉터리와 다릅니다.
</details>

## 2. holylife 계정 생성

### 간략 설명

배포 전용 Linux 계정 `holylife`를 만듭니다. 이 계정은 외부 SSH 로그인을 허용하지 않습니다. 관리자는 기존 SSH 계정으로 서버에 접속한 뒤 서버 안에서 `holylife`로 전환해 작업합니다.

### 명령어

기존 관리자 계정으로 서버에 접속해 실행합니다.

```bash
sudo adduser holylife
sudo usermod -aG sudo holylife
```

SSH 접속을 막고 설정 문법을 확인한 뒤 SSH 서비스를 다시 읽습니다.

```bash
echo "DenyUsers holylife" | sudo tee /etc/ssh/sshd_config.d/00-deny-holylife.conf
sudo sshd -t
sudo systemctl reload ssh
```

그 다음 관리자 계정의 현재 SSH 세션에서 `holylife`로 전환합니다.

```bash
sudo -iu holylife
whoami
```

### 자세한 설명

<details>
<summary>계정과 SSH 접속 설명</summary>

`adduser`가 비밀번호와 기본 정보를 묻습니다. 이름 등 부가 정보는 비워 두어도 됩니다. 비밀번호는 `holylife` 셸에서 `sudo`를 사용할 때 필요합니다. SSH 설정의 `DenyUsers holylife`가 이 계정의 외부 SSH 로그인을 차단하므로, `authorized_keys`를 만들거나 `holylife`로 직접 SSH 접속하지 마세요.

`sudo -iu holylife`는 현재 접속한 관리자 계정 안에서 사용자와 홈 디렉터리를 `holylife`로 바꿉니다. `whoami` 결과가 `holylife`인지 확인한 뒤 아래 배포 단계를 실행하세요. 문서의 Docker 명령은 `sudo docker ...` 형식으로 실행합니다. `docker` 그룹에는 계정을 추가하지 않습니다.
</details>

## 3. 서버 사전 준비

### 간략 설명

Ubuntu 서버에 Docker와 Docker Compose를 설치합니다. 클라우드 VM을 사용한다면 클라우드 방화벽에서도 웹 접속 포트 38080을 허용해야 합니다.

### 명령어

```bash
sudo apt update
sudo apt install -y ca-certificates curl git
curl -fsSL https://get.docker.com | sudo sh
sudo apt install -y docker-compose-plugin
sudo systemctl enable --now docker
sudo docker version
sudo docker compose version
```

### 자세한 설명

<details>
<summary>명령어와 네트워크 설정 설명</summary>

위 명령은 Ubuntu/Debian 계열 서버 기준입니다. 버전 확인 명령에서 Docker Engine과 Compose 버전이 나오면 설치가 끝난 것입니다. 서버 재부팅 뒤에도 Docker가 자동으로 시작되도록 설정합니다.

클라우드 콘솔의 방화벽/보안 규칙과 서버 방화벽에서 외부 접속용 TCP 38080번 포트를 허용하세요. HTTPS를 설정할 계획이라면 443번도 열어 둡니다. 웹 컨테이너 포트 8080, 백엔드 포트 8081, DB 포트 3306은 인터넷 전체에 공개하지 마세요. DB 서버는 Docker 서버에서만 접속할 수 있게 DB 서버 방화벽과 계정 권한을 제한해야 합니다.
</details>

## 4. 저장소 내려받기와 데이터 디렉터리 만들기

### 간략 설명

두 저장소를 `/opt/holylife` 아래 형제 디렉터리로 내려받고, 업로드/로그 디렉터리를 만듭니다.

### 명령어

```bash
sudo mkdir -p /opt/holylife
sudo chown holylife:holylife /opt/holylife
cd /opt/holylife

git clone -b main https://github.com/ban-jh/holylife.git backend
git clone -b main https://github.com/ban-jh/holylifeView.git frontend

mkdir -p /opt/holylife/config /opt/holylife/uploads /opt/holylife/logs
```

### 자세한 설명

<details>
<summary>서버에 이미 저장소가 있는 경우</summary>

`git clone`은 저장소 디렉터리가 이미 있으면 실패합니다. 이미 내려받은 저장소가 있다면 다시 clone하지 말고 해당 디렉터리에서 `git status`와 `git pull`로 상태를 확인하세요. 운영 중인 서버라면 로컬 변경사항이 있는지 확인한 뒤 업데이트해야 합니다.

백엔드와 프론트엔드는 모두 `main` 브랜치를 기준으로 안내합니다. 실제로 운영할 코드가 다른 브랜치라면 배포 전에 브랜치를 확인하세요.
</details>

## 5. Compose와 Nginx 설정 맞추기

### 간략 설명

Compose와 Nginx는 아래 포트 체인을 사용합니다. 저장소의 설정 파일은 이미 이 구성으로 맞춰져 있으므로 서버에서 `main` 브랜치를 최신 상태로 가져왔는지 확인하세요.

### 명령어

백엔드 Compose 파일을 엽니다.

```bash
nano /opt/holylife/backend/docker-compose.yml
```

파일 내용을 다음 구성으로 바꿉니다.

```yaml
services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: holylife-backend
    restart: unless-stopped
    expose:
      - "8081"
    volumes:
      - ../uploads:/opt/holylife/uploads
      - ../logs:/opt/holylife/logs
    extra_hosts:
      - "host.docker.internal:host-gateway"
    networks:
      - holylife-net
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SERVER_PORT: "8081"
      SPRING_DATASOURCE_URL: jdbc:mariadb://${DB_HOST:?DB_HOST를 설정하세요}:${DB_PORT:-3306}/${DB_NAME:?DB_NAME을 설정하세요}
      SPRING_DATASOURCE_USERNAME: ${DB_USERNAME:?DB_USERNAME을 설정하세요}
      DB_PASSWORD: ${DB_PASSWORD:?DB_PASSWORD를 설정하세요}
      JWT_SECRET: ${JWT_SECRET:?JWT_SECRET을 설정하세요}

  frontend:
    build:
      context: ../frontend
      dockerfile: Dockerfile
    container_name: holylife-frontend
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "38080:8080"
    networks:
      - holylife-net

networks:
  holylife-net:
    driver: bridge
```

백엔드는 Docker 네트워크 안에서만 `8081` 포트를 사용합니다. 프론트엔드 Nginx 설정은 컨테이너에서 `8080` 포트를 듣고 외부 호스트 포트 `38080`에 연결합니다. Nginx 설정 파일은 `/opt/holylife/frontend/docker/nginx.conf`입니다.

```bash
nano /opt/holylife/frontend/docker/nginx.conf
```

API, Swagger, OpenAPI, 업로드 위치의 `proxy_pass`가 모두 다음 주소를 가리키는지 확인합니다.

```nginx
proxy_pass http://backend:8081;
```

### 자세한 설명

<details>
<summary>설정을 맞춰야 하는 이유와 저장 방법</summary>

Compose의 `SERVER_PORT=8081` 설정이 Spring Boot 포트를 8081로 지정합니다. Nginx는 Docker Compose 서비스 이름 `backend`와 내부 포트 8081로 API, Swagger, 업로드 요청을 전달합니다. Nginx 자체는 8080에서 대기합니다.

외부 포트는 `38080:8080`으로 연결된 프론트엔드 하나뿐입니다. 백엔드는 `expose: 8081`로 Docker 네트워크에서만 연결할 수 있고, 서버의 공인 IP에서 직접 접근할 수 없습니다.

브라우저에서 로그인할 때 백엔드는 브라우저의 `Origin`도 검사합니다. 현재 운영 주소가 `http://61.77.22.117:38080`이면 백엔드 `SecurityConfig`의 CORS 허용 목록에도 같은 주소가 있어야 합니다. 공인 IP, 도메인 또는 포트가 바뀌면 이 목록을 수정하고 백엔드 이미지를 다시 빌드하세요. 허용 목록에서 빠지면 화면은 실제 CORS 403 오류를 비밀번호 오류로 표시할 수 있습니다.

`nano`에서는 `Ctrl+O`, Enter로 저장하고 `Ctrl+X`로 종료합니다. Compose의 상대 볼륨 경로는 Compose 파일 위치(`/opt/holylife/backend`) 기준이므로 `../uploads`와 `../logs`는 `/opt/holylife/uploads`, `/opt/holylife/logs`에 연결됩니다.
</details>

## 6. DB와 운영 비밀값 설정

### 간략 설명

DB 접속 정보와 JWT 서명 키를 서버의 `.env` 파일에 저장합니다. 이 프로젝트는 별도 MariaDB를 사용하므로 그 DB가 먼저 준비되어야 합니다.

### 명령어

```bash
nano /opt/holylife/config/.env
```

다음 형식으로 입력하고 실제 값으로 바꿉니다.

```dotenv
DB_HOST=DB서버의_접속가능한_주소
DB_PORT=3306
DB_NAME=holylife
DB_USERNAME=DB사용자
DB_PASSWORD=DB비밀번호
JWT_SECRET=충분히_길고_임의적인_문자열
```

```bash
chmod 600 /opt/holylife/config/.env
```

### 자세한 설명

<details>
<summary>DB 주소, 비밀번호, JWT 키 설명</summary>

`DB_HOST`에는 백엔드 컨테이너에서 접근할 수 있는 DB 서버 주소를 적습니다. MariaDB가 Docker 호스트(Ubuntu)에 설치되어 있으면 `.env`의 `DB_HOST`를 `host.docker.internal`로 설정합니다. Compose가 이 이름을 호스트 게이트웨이에 연결합니다. `127.0.0.1`은 백엔드 컨테이너 자신을 가리키므로 호스트에 설치된 DB 접속 주소로 사용할 수 없습니다. 별도 DB 서버라면 그 서버의 사설 IP 또는 DNS 이름을 사용하고, DB 방화벽은 애플리케이션 서버에서 오는 3306 접속만 허용하세요.

`JWT_SECRET`은 로그인 토큰을 서명하는 비밀 키입니다. HS256 기준 최소 32바이트 이상이어야 하며, 다음 명령은 32바이트를 무작위로 생성해 64자리 16진수로 보여 줍니다.

```bash
openssl rand -hex 32
```

운영 환경에서는 저장소의 개발 기본값을 쓰지 말고 생성한 키를 `/opt/holylife/config/.env`에만 저장합니다. 비밀값을 Git에 커밋하거나 공개 채팅에 붙여 넣지 마세요. 키를 교체하면 기존 로그인 토큰은 모두 무효화됩니다. `chmod 600`은 파일 소유자만 읽고 쓸 수 있게 합니다.

Compose 명령에는 `--env-file /opt/holylife/config/.env`를 붙입니다. 그래야 Compose가 이 파일을 읽어 DB 주소와 비밀값을 컨테이너에 전달합니다.
</details>

## 7. 데이터베이스 스키마 준비

### 간략 설명

백엔드 시작 전에 MariaDB에 `holylife` 스키마와 테이블이 준비되어 있어야 합니다. 새 DB를 만들 때만 초기 스크립트를 실행합니다.

### 명령어

새 DB에 초기화할 때 DB 서버 또는 DB에 접속 가능한 관리 PC에서 실행합니다.

```bash
cd /opt/holylife/backend/dbscript

for f in 01_schema_drop.sql 02_schema_system.sql 03_schema_content.sql \
         04_schema_bible.sql 05_schema_batch.sql 06_seed_data.sql 07_indexes.sql
do
  mysql --default-character-set=utf8mb4 -h DB서버주소 -u DB관리자 -p holylife < "$f"
done
```

### 자세한 설명

<details>
<summary>운영 데이터가 있는 경우 주의사항</summary>

위 스크립트 중 `01_schema_drop.sql`은 기존 테이블을 삭제하는 스크립트입니다. **데이터가 있는 운영 DB에 실행하지 마세요.** 처음 설치하는 빈 DB에서만 실행하고, 기존 DB라면 백업을 만든 뒤 필요한 변경 스크립트만 DBA와 확인해야 합니다.

`-p` 뒤에 비밀번호를 직접 적지 마세요. 명령 실행 후 표시되는 프롬프트에 입력하면 터미널 기록에 비밀번호가 남는 것을 줄일 수 있습니다. DB 사용자에게는 필요한 스키마에 대한 권한이 있어야 합니다. UTF-8 문자와 이모지 저장을 위해 `--default-character-set=utf8mb4`를 유지합니다.
</details>

## 8. 이미지 빌드와 첫 실행

### 간략 설명

Compose 설정과 비밀값을 확인한 다음 프론트엔드와 백엔드 이미지를 빌드하고 서비스를 실행합니다.

### 명령어

```bash
cd /opt/holylife/backend

sudo docker compose --env-file /opt/holylife/config/.env config
sudo docker compose --env-file /opt/holylife/config/.env up -d --build
sudo docker compose ps
```

### 자세한 설명

<details>
<summary>처음 실행할 때 확인할 내용</summary>

`config` 명령은 Compose 설정을 검사합니다. `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`이 없다는 오류가 나오면 `.env` 파일을 확인하세요. 이 출력에 비밀값이 포함될 수 있으므로 전체 결과를 외부에 공유하지 마세요.

`up -d --build`는 Docker 이미지를 새로 만들고 컨테이너를 백그라운드에서 실행합니다. 첫 빌드는 Maven과 npm 패키지를 내려받으므로 시간이 걸릴 수 있습니다. `docker compose ps`에서 `holylife-backend`와 `holylife-frontend`가 실행 중인지 확인합니다.

웹 브라우저에서 `http://서버의_공인_IP/`에 접속합니다. 로그인 화면이 나오면 Nginx 정적 파일 서빙이 동작하는 것입니다. 로그인/API도 별도로 확인해야 합니다. DB 연결 오류는 백엔드 로그에서 확인합니다.
</details>

## 9. 서비스 상태와 오류 확인

### 간략 설명

컨테이너 상태와 로그를 확인하고, 브라우저에서 접속되지 않을 때 차례로 점검합니다.

### 명령어

```bash
cd /opt/holylife/backend
sudo docker compose --env-file /opt/holylife/config/.env ps
sudo docker compose --env-file /opt/holylife/config/.env logs --tail=100 backend
sudo docker compose --env-file /opt/holylife/config/.env logs --tail=100 frontend
curl -I http://localhost/
```

실시간 백엔드 로그를 볼 때:

```bash
sudo docker compose --env-file /opt/holylife/config/.env logs -f backend
```

### 자세한 설명

<details>
<summary>자주 확인하는 문제</summary>

- 웹 페이지 접속 불가: 클라우드 방화벽과 서버 방화벽에서 TCP 38080번이 허용되었는지, 프론트엔드 컨테이너가 실행 중인지 확인합니다.
- 백엔드가 시작되지 않음: backend 로그에서 DB 주소/계정 오류, `JWT_SECRET` 누락, DB 접근 차단 여부를 확인합니다.
- 화면은 열리지만 API가 실패: Nginx의 `proxy_pass`가 `backend:8081`인지, backend 서비스가 실행 중인지 확인합니다.
- 브라우저에서 비밀번호 오류가 표시되지만 직접 API 로그인은 됨: 브라우저 주소와 백엔드 `SecurityConfig`의 CORS 허용 origin이 정확히 같은지 확인합니다. 화면은 CORS 오류도 로그인 실패 문구로 표시할 수 있습니다.
- 업로드 이미지가 재시작 뒤 사라짐: Compose 볼륨이 `../uploads:/opt/holylife/uploads`로 지정되었는지 확인합니다.

실시간 로그는 `Ctrl+C`를 누르면 화면 출력만 종료됩니다. 컨테이너는 계속 실행됩니다.
</details>

## 10. 업데이트, 재시작, 중지

### 간략 설명

코드를 갱신한 뒤 이미지를 다시 빌드해 반영합니다. 컨테이너를 중지하거나 재시작할 때도 같은 Compose 파일과 `.env`를 사용합니다.

### 명령어

```bash
git -C /opt/holylife/backend switch main
git -C /opt/holylife/backend pull --ff-only origin main
git -C /opt/holylife/frontend switch main
git -C /opt/holylife/frontend pull --ff-only origin main
cd /opt/holylife/backend
sudo docker compose --env-file /opt/holylife/config/.env up -d --build
```

서비스 재시작/중지/시작:

```bash
sudo docker compose --env-file /opt/holylife/config/.env restart
sudo docker compose --env-file /opt/holylife/config/.env stop
sudo docker compose --env-file /opt/holylife/config/.env start
```

### 자세한 설명

<details>
<summary>업데이트와 데이터 보존 설명</summary>

서버는 두 저장소 모두 `main` 브랜치만 사용합니다. `dev`에서 작업한 코드는 운영 반영 전에 `main`에 병합하고 원격 `main`에 푸시해야 합니다. 서버는 `main`을 `--ff-only`로 갱신한 뒤 `up -d --build`를 실행합니다. Git에 서버 로컬 변경사항이 있으면 pull이 중단될 수 있으니 먼저 `git status`로 확인하세요.

`stop`은 컨테이너를 멈추고, `start`는 다시 시작합니다. `restart`는 컨테이너를 재시작합니다. 업로드와 로그는 호스트 디렉터리에 저장되어 컨테이너를 재생성해도 남습니다. 다만 DB는 별도 백업이 필요합니다.

DB 초기화 SQL을 일반 코드 업데이트처럼 반복 실행하지 마세요. 특히 `01_schema_drop.sql`은 데이터를 삭제할 수 있습니다. 운영 배포 전 DB 백업과 복구 절차를 준비하세요.
</details>

## 11. HTTPS와 운영 전 확인

### 간략 설명

이 문서의 기본 접속 방식은 HTTP입니다. 실제 서비스에 도메인과 HTTPS를 적용하려면 인증서와 Nginx의 TLS 설정을 추가해야 합니다.

### 명령어

```text
기본 접속: http://서버의_공인_IP:38080/
도메인/HTTPS: DNS 연결, 80·443 포트 개방, TLS 인증서 및 Nginx 설정 필요
```

### 자세한 설명

<details>
<summary>실서비스 공개 전 점검 목록</summary>

- 운영 DB에 필요한 테이블/시드 데이터가 준비되어 있는지 확인합니다.
- 업로드와 로그 볼륨이 올바른 호스트 경로에 연결되었는지 확인합니다.
- `config/.env`의 파일 권한이 제한되어 있고 실제 비밀값이 저장소에 커밋되지 않았는지 확인합니다.
- 서버 외부에는 웹 포트만 공개하고 backend 및 DB 포트는 공개하지 않습니다.
- 현재 백엔드 `SecurityConfig`에서 Swagger UI와 API 문서 접근을 허용합니다. 운영에서 Swagger를 비활성화할 필요가 있다면 별도 설정을 적용하세요.
- HTTP만 사용할 경우 로그인 토큰이 암호화되지 않은 연결을 통해 전송됩니다. 외부 서비스에는 도메인과 HTTPS를 구성한 뒤 공개하세요.
</details>

## 12. Git 브랜치 정책과 서버 반영 절차

### 간략 설명

개발자는 백엔드와 프론트엔드 각 저장소의 `dev` 브랜치에서 작업합니다. 운영 서버에 적용할 때만 `dev`를 `main`에 병합하고, 서버에서는 `main`을 받아 Docker 이미지를 다시 빌드합니다.

### 명령어

개발 작업은 각 저장소에서 진행합니다. 아래 예시는 백엔드 저장소 기준이며, 프론트엔드에서도 같은 순서로 실행합니다.

```bash
git switch dev
git pull --ff-only origin dev
# 파일 수정 후
git add 변경한_파일
git commit -m "변경 내용 요약"
git push origin dev
```

운영 반영이 승인되면 해당 저장소에서 `dev`를 `main`에 병합하고 푸시합니다.

```bash
git switch main
git pull --ff-only origin main
git merge --no-ff dev
git push origin main
```

그 다음 서버에서 [10. 업데이트, 재시작, 중지](#10-업데이트-재시작-중지)의 `main` 갱신 및 Docker 재빌드 명령을 실행합니다.

### 자세한 설명

<details>
<summary>브랜치별 역할</summary>

- `dev`: 개발과 변경사항 통합을 진행하는 브랜치입니다. 작업 커밋은 먼저 `origin/dev`에 푸시합니다.
- `main`: 운영 서버에 배포할 코드만 둡니다. 서버는 `main`을 직접 수정하거나 `dev`를 직접 가져오지 않습니다.
- 병합 전에는 두 저장소 모두 `git status`로 작업 트리가 깨끗한지 확인합니다. 충돌이 나면 내용을 정리하고 병합 커밋을 완료한 뒤 `main`을 푸시합니다.
- 프론트엔드도 동일한 브랜치 정책을 따릅니다. 원격 저장소에 `dev`가 아직 없다면 최초 한 번 `git push -u origin dev`로 브랜치를 만듭니다.
- `main`에 푸시한 뒤 서버에서 `main`을 갱신하고 `docker compose ... up -d --build`를 실행해야 실제 운영 서버에 반영됩니다. Git 푸시만으로 서버가 자동 배포되지는 않습니다.
</details>
