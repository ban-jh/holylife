# 운영 매뉴얼 (Operations)

> 최종 갱신: 2026-11-11
> 대상: 서버 운영 담당자

---

## 1. 시스템 구성

```
[사용자 브라우저]
      │ HTTP :38080
      ▼
[Nginx]  root /opt/holylife/holylifeView
      │  /api, /uploads, /swagger-ui → proxy_pass
      ▼ HTTP :38081
[Spring Boot 4.0]  (systemd: holylife)
      │  JDBC
      ▼
[MariaDB 10.11]  :3306  (DB: holylife)
```

| 항목 | 값 |
|------|-----|
| 서버 OS | Ubuntu 24.04 LTS |
| 내부 IP | 192.168.2.15 |
| 웹 포트 | **38080** (Nginx) |
| 백엔드 포트 | **38081** (Spring Boot) |
| DB | MariaDB `holylife` (3306) |

---

## 2. 배포 경로

| 구분 | 경로 |
|------|------|
| 애플리케이션 JAR | `/opt/holylife/holylife-0.0.1.jar` |
| 운영 설정 | `/opt/holylife/config/application-prod.yml` |
| 프론트 정적 파일 | `/opt/holylife/holylifeView/` |
| 업로드 파일 | `/opt/holylife/uploads/pbs/` |
| 로그 | `/opt/holylife/logs/holylife.log` |
| PID | `/opt/holylife/logs/holylife.pid` |
| 운영 스크립트 | `/opt/holylife/sh/` |

**스크립트**
| 파일 | 용도 |
|------|------|
| `start.sh` | 기동 (dev 프로파일, config 추가 로드) |
| `stop.sh` | PID 기반 종료 |
| `restart.sh` | 재시작 |
| `watchdog.sh` | 1분 주기 생존 확인·자동 재기동 (crontab) |
| `deploy-backend.sh` | JAR 빌드/복사/재시작 |
| `deploy-frontend.sh` | 프론트 빌드/복사/reload |

**소스 경로**
- 백엔드: `/home/jhban/holylife/holylife` (branch `dev`)
- 프론트: `/home/jhban/holylife/holylifeView` (branch `main`)

---

## 3. 서비스 관리

```bash
# 상태
sudo systemctl status holylife
ps -eo pid,args | grep '[h]olylife-0.0.1.jar'

# 시작 / 중지 / 재시작
sudo systemctl start holylife
sudo systemctl stop holylife
sudo systemctl restart holylife

# 스크립트 직접 사용
sudo /opt/holylife/sh/start.sh
sudo /opt/holylife/sh/stop.sh
```

> ⚠️ `Type=forking` + `Restart=no` 구성입니다.
> `systemctl is-active`가 `failed`로 보여도 실제 프로세스가 살아 있을 수 있으므로 **프로세스와 포트를 함께 확인**하세요.

### 헬스 체크
```bash
curl -o /dev/null -w "%{http_code}\n" http://localhost:38081/api/pbs/folders   # 200
curl -o /dev/null -w "%{http_code}\n" http://localhost:38080/dashboard          # 200
ss -ltnp | grep -E '38080|38081'
```

---

## 4. 배포 절차

### 4.1 백엔드
```bash
cd /home/jhban/holylife/holylife
./mvnw clean package -DskipTests

# 1) 반드시 먼저 종료 (실행 중 JAR 덮어쓰기 금지)
sudo /opt/holylife/sh/stop.sh
for i in $(seq 1 20); do ss -ltn | grep -q 38081 || break; sleep 1; done

# 2) 교체 및 기동
sudo cp target/holylife-0.0.1-SNAPSHOT.jar /opt/holylife/holylife-0.0.1.jar
sudo rm -f /opt/holylife/logs/holylife.pid
sudo /opt/holylife/sh/start.sh

# 3) 확인
sleep 8; curl -o /dev/null -w "%{http_code}\n" http://localhost:38081/api/pbs/folders
```

> 실행 중인 JAR을 덮어쓰면 `ClassNotFoundException`(logback 등)으로 기동 실패합니다.
> 이 경우 프로세스를 완전히 종료한 뒤 다시 복사하세요.

### 4.2 프론트엔드
```bash
cd /home/jhban/holylife/holylifeView
npm run build
sudo rm -rf /opt/holylife/holylifeView/*
sudo cp -r dist/* /opt/holylife/holylifeView/
sudo nginx -s reload
curl -o /dev/null -w "%{http_code}\n" http://localhost:38080/dashboard
```
- `index.html`은 no-cache 설정 → 배포 즉시 반영

---

## 5. QT 자동 수집 배치

| Job | 대상 | 스케줄 |
|-----|------|--------|
| `qtCrawlJob` | 생명의삶(duranno) | 매일 04:00 |
| `qtSumCrawlJob` | SUM | 매일 04:05 |

### 수동 실행
```bash
TOKEN=$(curl -s -X POST http://localhost:38081/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@holylife.local","password":"<비밀번호>"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

curl -X POST "http://localhost:38081/api/qt/crawl?source=all&date=$(date +%F)" \
  -H "Authorization: Bearer $TOKEN"
```

### 실행 이력 확인
```sql
SELECT JOB_INSTANCE_ID, JOB_NAME, JOB_KEY FROM BATCH_JOB_INSTANCE;
SELECT JOB_EXECUTION_ID, STATUS, EXIT_CODE, CREATE_TIME, END_TIME FROM BATCH_JOB_EXECUTION ORDER BY 1 DESC;
SELECT STEP_NAME, STATUS, READ_COUNT, WRITE_COUNT FROM BATCH_STEP_EXECUTION ORDER BY 1 DESC;
SELECT qt_date, source, LEFT(title,40) FROM qt_daily ORDER BY qt_date DESC LIMIT 10;
```

### 장애 시 확인 순서
1. `qt_daily`에 오늘 데이터가 있는가
2. `BATCH_JOB_EXECUTION`에 오늘 실행 기록이 있는가
   - 기록 없음 → 스케줄러 미실행/앱 재시작 상태
   - `FAILED` → `BATCH_STEP_EXECUTION.EXIT_MESSAGE` 확인
3. 로그: `grep -i "qt\|batch" /opt/holylife/logs/holylife.log | tail -50`
4. 수동 실행으로 재현 → 사이트 구조 변경(셀렉터) 여부 확인

> Spring Batch 6는 네이티브 `SEQUENCE`를 사용합니다. `BATCH_JOB_SEQ` 같은 테이블 시퀀스를 만들면 동작하지 않습니다.

---

## 6. 로그

| 로그 | 경로 |
|------|------|
| 애플리케이션 | `/opt/holylife/logs/holylife.log` |
| Nginx access | `/var/log/nginx/access.log` |
| Nginx error | `/var/log/nginx/error.log` |

```bash
tail -f /opt/holylife/logs/holylife.log
grep -i "error\|exception" /opt/holylife/logs/holylife.log | tail -30
```

---

## 7. DB 운영

### 접속
```bash
mysql -u shaloman -p holylife
```

### 백업
```bash
mkdir -p /home/jhban/backup
mysqldump -u shaloman -p --default-character-set=utf8mb4 \
  --single-transaction --routines holylife \
  | gzip > /home/jhban/backup/holylife_$(date +%Y%m%d_%H%M).sql.gz
```

### 복구
```bash
gunzip -c holylife_YYYYMMDD_HHMM.sql.gz | mysql -u shaloman -p holylife
```

### 주요 확인 쿼리
```sql
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM bible_books;                    -- 약 9만 절
SELECT source, COUNT(*), MAX(qt_date) FROM qt_daily GROUP BY source;
SELECT status, COUNT(*) FROM pbs_page GROUP BY status;
```

---

## 8. 장애 대응 (Troubleshooting)

| 증상 | 원인 | 조치 |
|------|------|------|
| 기동 실패 `ClassNotFoundException` | 실행 중 JAR 덮어씀 | 프로세스 완전 종료 후 JAR 재복사 |
| `Port 38081 was already in use` | 이전 프로세스 잔존 | `ss -ltnp \| grep 38081` → PID kill → PID 파일 삭제 후 기동 |
| 프론트에서 403 (CORS) | 허용 origin 누락 | `SecurityConfig`의 `setAllowedOriginPatterns` 확인 |
| 401 반복 | JWT 만료(24h) | 재로그인 |
| 500 `/api/...` | 대상 테이블/컬럼 불일치 | 로그의 SQL 예외 확인, ERD와 대조 |
| 대시보드 전체 빈 화면 | 한 API 500 → `Promise.all` 실패 | 네트워크 탭에서 실패 API 확인 |
| 배치 미실행 | 앱 미기동/스케줄 누락 | 5장 절차로 확인 |
| 이미지 업로드 실패 | MIME/용량 제한 | 10MB·허용 형식 확인 |

---

## 9. 보안 점검

- [ ] `JWT_SECRET`이 코드에 하드코딩되지 않았는가 (운영은 환경변수)
- [ ] DB 계정이 `0.0.0.0` 바인딩이면 외부 노출 여부 확인 → 내부망이면 `127.0.0.1` 권장
- [ ] 방화벽(ufw) 정책 확인 — 현재 비활성 상태
- [ ] CORS 허용 origin이 운영 도메인만인가 (앱 사용 시 `https://localhost` 포함)
- [ ] 앱 WebView에서 HTTP API 호출 시 Mixed Content 차단 여부 확인 (`androidScheme`)
- [ ] 업로드 디렉터리 실행 권한 없음
- [ ] 관리자 계정 비밀번호 정기 변경
- [ ] Swagger 운영 노출 여부 결정 (`springdoc.swagger-ui.enabled`)

---

## 10. 정기 점검

| 주기 | 항목 |
|------|------|
| 일 | QT 배치 수집 결과 (`qt_daily`) |
| 주 | 디스크 사용량, 로그 크기, 업로드 용량 |
| 월 | DB 백업 파일 검증, 계정/권한 점검, 보안 패치 |
