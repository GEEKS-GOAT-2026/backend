# Dongne Backend

인하대학교 동아리 통합 플랫폼 **동네(Dongne)** 백엔드 API 서버입니다.

현재 MVP 우선순위는 다음 사용자 흐름입니다.

```text
Google 로그인 -> 인하대 계정 검증 -> users 저장/조회 -> 동아리 목록 무한 스크롤 -> 동아리 필터링 -> 동아리 상세 확인
```

## 현재 구현 상태

프로토타입 확정 흐름 기준 API 계약과 Supabase 테스트 데이터 SQL은 아래 문서를 기준으로 관리합니다.

- `dongnea/docs/API_CONTRACT.md`
- `dongnea/docs/dev-seed.sql`

### 완료

- Google OAuth2 로그인
- `@inha.edu`, `@inha.ac.kr` 학교 계정 검증
- 로그인 성공 시 `users` 테이블에 유저 저장 또는 업데이트
- 백엔드 JWT 발급
- `Authorization: Bearer <token>` 기반 인증 필터
- `GET /api/users/me` 내 정보 조회
- `GET /api/clubs?page=0&size=20` 동아리 목록 페이지네이션
- `GET /api/clubs/{clubId}` 동아리 상세 조회
- 동아리 목록 카테고리/키워드/모집 상태 필터
- 모집 상태 계산
  - `isActive=true`
  - 상시모집이거나 현재 날짜가 `startDate ~ endDate` 사이일 때 가입 가능
- 동아리 목록 응답에 `activeRecruitment`, `recruitmentDisplayText` 제공
- 로컬 테스트용 동아리 더미데이터 60개
  - 모집중 50개
  - 모집마감 10개
  - 상시모집/기간모집/기간만료 케이스 포함
- 행사 도메인 1차 구현
  - `events` 테이블
  - `GET /api/events`
  - `GET /api/events/recent`
- 회장 페이지용 회원/신청자 더미 도메인 1차 구현
  - `club_members` 테이블
  - `GET /api/clubs/{clubId}/members`
  - 신청자 수락/거절 API
- 회장 권한 기반 글쓰기 API
  - 동아리 소개 수정: `PATCH /api/clubs/{clubId}/profile`
  - 공지 작성/수정/삭제: `/api/clubs/{clubId}/notices`
  - 행사 작성/수정/삭제: `/api/events`
- 프로토타입 상세 화면용 데이터 구조
  - 활동기록: `club_activities`
  - 공지: `club_notices`
  - 모집 폼: `recruitments.form_schema`
- local 프로필 전용 테스트 토큰 발급 API
  - `GET /api/local/auth-token`

### 아직 실험/초안 상태

- 모집 공고 생성 API
- 지원서 제출 API
- 동아리 관리자 권한 검증
- refresh token / logout
- 운영 배포 설정

## Tech Stack

- Java 21
- Spring Boot 3.5.14
- Spring Security
- Spring OAuth2 Client
- JWT: `jjwt 0.12.5`
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Swagger / Springdoc OpenAPI

## 프로젝트 구조

```text
dongnea
    ├── build.gradle
    ├── docker-compose.yml
└── src/main
    ├── java/geeks/dongnea
    │   ├── domain
    │   │   ├── user
    │   │   ├── club
    │   │   ├── event
    │   │   └── application
    │   └── global
    │       ├── config
    │       └── security
    └── resources
        ├── application.yml
        └── application-secret.yml
```

## 로컬 실행 가이드

아래 명령은 `backend_repo/dongnea` 디렉터리에서 실행합니다.

```bash
cd dongnea
```

### 1. PostgreSQL 실행

```bash
docker-compose up -d
```

현재 로컬 DB 기본값:

```text
host: localhost
port: 5432
database: dongnea_db
username: admin
password: 1234
```

### 2. Secret 설정

`src/main/resources/application-secret.yml` 파일을 생성합니다.

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            scope:
              - email
              - profile

jwt:
  secret: YOUR_LONG_JWT_SECRET_AT_LEAST_32_BYTES
```

JWT secret은 최소 32바이트 이상으로 설정해야 합니다.

Google Cloud Console OAuth 설정에는 로컬 테스트 기준으로 아래 redirect URI를 등록합니다.

```text
http://localhost:8080/login/oauth2/code/google
```

### 3. 백엔드 실행

더미 동아리 데이터를 자동으로 넣고 테스트하려면 `local` 프로필로 실행합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

`local` 프로필에서는 앱 시작 시 테스트 데이터를 자동으로 생성합니다.

- 동아리 더미데이터 총 60개
- 모집 공고 더미데이터
  - 상시모집
  - 현재 모집 기간
  - 기간 만료
- 행사 더미데이터
- 회장 페이지 회원/신청자 더미데이터

기존 30개 동아리는 `clubs` 테이블이 비어 있을 때만 생성됩니다. 추가 30개 동아리는 이름 기준으로 존재 여부를 확인한 뒤 없을 때만 생성됩니다.

## 팀 테스트용 무료 배포 가이드

운영 배포가 아니라 팀 테스트용 dev 서버 기준입니다. 무료 사용을 우선하므로 Cloudtype 프리티어와 Supabase Free Plan을 사용합니다.

### 현재 테스트 서버

Cloudtype 테스트 서버:

```text
https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app
```

무료 계정 특성상 서버가 하루에 한 번 정도 중지될 수 있습니다. 연결이 안 되면 Cloudtype에서 서비스를 다시 시작한 뒤 확인합니다.

프론트 로컬 연동 시 환경변수 예시:

```text
NEXT_PUBLIC_API_BASE_URL=https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app
NEXT_PUBLIC_BACKEND_LOGIN_URL=https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app/oauth2/authorization/google
```

백엔드 로컬 실행 없이 프론트가 위 배포 서버를 바라보면 됩니다.

### 1. Supabase 등록 및 DB 생성

1. `https://supabase.com` 접속
2. GitHub 계정으로 가입
3. New Project 생성
4. Project name 예: `dongnea-dev`
5. Database password 생성 후 안전한 곳에 보관
6. Region은 한국/일본/싱가포르 등 가까운 곳 우선 선택
7. Free Plan 선택

Supabase Free Plan은 무료로 시작할 수 있지만 DB 용량, active project 수, 미사용 pause 제한이 있습니다. 팀 테스트용으로는 충분하지만 운영 DB처럼 가정하면 안 됩니다.

### 2. Supabase 연결 정보 확인

Supabase Dashboard에서 `Connect` 또는 Database connection 메뉴를 열고 PostgreSQL connection string을 확인합니다.

Spring Boot에는 Cloudtype 환경변수로 아래 값을 등록합니다.

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://{supabase-host}:{port}/postgres
SPRING_DATASOURCE_USERNAME={supabase-user}
SPRING_DATASOURCE_PASSWORD={supabase-db-password}
```

비밀번호가 포함된 connection string은 Git, README, 공개 문서, 메신저 공개 채널에 붙이지 않습니다.

### 3. Cloudtype 등록 및 앱 생성

1. `https://cloudtype.io` 또는 `https://www.cloudtype.dev` 접속
2. GitHub 계정 연동
3. 프리티어 사용
4. 카드 등록 요구 여부 확인
5. GitHub repository 접근 권한 부여
6. Spring Boot 또는 Java 템플릿으로 앱 생성

Cloudtype 앱 설정:

```text
Root directory: backend_repo/dongnea
Build command: ./gradlew clean build -x test
Start command: java -jar build/libs/dongnea-0.0.1-SNAPSHOT.jar
Port: 8080
```

Java version은 가능하면 21을 사용합니다. 선택지가 제한되면 Spring Boot 3.x가 요구하는 Java 17 이상에서 먼저 빌드 가능 여부를 확인합니다.

### 4. Cloudtype 환경변수

필수:

```text
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://{supabase-host}:{port}/postgres
SPRING_DATASOURCE_USERNAME={supabase-user}
SPRING_DATASOURCE_PASSWORD={supabase-db-password}
JWT_SECRET={긴 랜덤 문자열}
APP_FRONTEND_REDIRECT_URI={프론트 주소}/login
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID={google-client-id}
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET={google-client-secret}
```

권장:

```text
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=INFO
LOGGING_LEVEL_GEEKS_DONGNEA=INFO
```

`application-secret.yml`은 배포 서버에 파일로 올리지 않습니다. 로컬에서는 파일을 사용할 수 있고, 배포에서는 환경변수로 민감값을 주입합니다.

현재 `application.yml`은 `optional:classpath:application-secret.yml`로 secret 파일을 선택 import합니다. 따라서 로컬에서는 `application-secret.yml`을 사용할 수 있고, 배포에서는 파일 없이 Cloudtype 환경변수만으로 실행할 수 있습니다.

### 5. Google OAuth 설정

Google Cloud Console의 OAuth redirect URI에 Cloudtype 백엔드 주소를 추가합니다.

```text
https://{cloudtype-backend-domain}/login/oauth2/code/google
```

백엔드 로그인 성공 후 이동할 프론트 주소는 Cloudtype 환경변수로 설정합니다.

```text
APP_FRONTEND_REDIRECT_URI=https://{frontend-domain}/login
```

프론트가 아직 로컬이면 임시로 아래 값을 사용할 수 있지만, 팀원별 테스트가 불편하므로 프론트도 임시 배포 URL을 두는 편이 좋습니다.

```text
APP_FRONTEND_REDIRECT_URI=http://localhost:3000/login
```

### 6. 배포 프로필과 seed 정책

- `local`
  - Docker PostgreSQL 기준 로컬 개발
  - `ClubDummyDataLoader` 자동 실행
  - `/api/local/auth-token` 사용 가능
- `dev`
  - Cloudtype + Supabase 팀 테스트 서버
  - local seed 자동 실행 안 함
  - `/api/local/auth-token` 비활성
  - 초기 데이터는 수동 SQL, Supabase import, 또는 별도 1회 seed 방식으로 관리

### 7. 배포 후 확인

```bash
curl https://{backend-domain}/api-docs
```

브라우저:

```text
https://{backend-domain}/swagger
https://{backend-domain}/oauth2/authorization/google
```

로그인 후 JWT로:

```bash
curl -H "Authorization: Bearer {JWT}" \
  "https://{backend-domain}/api/clubs?page=0&size=5"
```

실패하면 Cloudtype logs, Java version, Root directory, build/start command, Supabase 연결 정보, OAuth redirect URI, `APP_FRONTEND_REDIRECT_URI`를 우선 확인합니다.

### 8. Swagger 확인

```text
http://localhost:8080/swagger
```

배포 서버:

```text
https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app/swagger
```

Swagger에서 JWT가 필요한 API는 우측 상단 Authorize에 아래 형식으로 입력합니다.

```text
Bearer <token>
```

현재 팀 테스트 편의를 위해 아래 조회 API는 토큰 없이 접근 가능합니다.

```text
GET /api/clubs
GET /api/clubs/{clubId}
GET /api/events
GET /api/events/recent
```

그 외 사용자 정보, 지원서 제출, 회원 수락/거절 등 쓰기/개인화 API는 JWT가 필요합니다.

## 로그인 및 동아리 목록 테스트

현재 백엔드는 Google 로그인 성공 후 아래 주소로 이동합니다.

```text
http://localhost:3000/login?token=<JWT>
```

> 개발 초기에는 별도의 테스트용 정적 페이지가 제공되었으나, 현재는 실제 프론트(또는 통합 테스트 환경)를 사용해 연동하도록 변경되었습니다.

프론트와의 인증 연동 방법:

1. 백엔드에서 OAuth2 로그인을 시작합니다:

```text
http://localhost:8080/oauth2/authorization/google
```

배포 서버:

```text
https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app/oauth2/authorization/google
```

2. 로그인 성공 시 백엔드는 `app.frontend.redirect-uri`로 설정된 프론트 주소로 리다이렉트하며, 개발 설정에서는 다음과 같이 JWT를 쿼리 파라미터로 전달합니다:

```text
http://{FRONTEND_HOST}/login?token=<JWT>
```

3. 프론트는 `token`을 받아 저장한 뒤 (`localStorage` 또는 메모리), 이후 API 호출에 `Authorization: Bearer <token>` 헤더를 포함시켜 백엔드에 요청합니다.

로그인 실패 시에도 백엔드는 프론트 redirect URL로 이동하며, 쿼리 파라미터에 실패 정보를 전달합니다.

```text
http://{FRONTEND_HOST}/login?error=oauth_login_failed&reason=<REASON>&message=<MESSAGE>
```

예를 들어 학교 계정이 아닌 경우 `reason=invalid_school_email`이 전달될 수 있습니다.

테스트용 정적 페이지는 더 이상 프로젝트에서 권장되지 않으므로, 프론트가 준비되기 전에는 간단한 클라이언트(예: Postman 또는 임시 SPA)를 사용해 연동을 확인하세요.

## 로컬 테스트용 토큰

`local` 프로필에서만 아래 API가 열립니다. OAuth 흐름 없이 백엔드 JWT가 필요한 API를 확인할 때 사용합니다.

```http
GET /api/local/auth-token
```

응답 예시:

```json
{
  "accessToken": "BACKEND_JWT"
}
```

사용 예시:

```bash
TOKEN=$(curl -s "http://localhost:8080/api/local/auth-token" \
  | node -pe "JSON.parse(require('fs').readFileSync(0,'utf8')).accessToken")

curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/clubs?page=0&size=5"
```

주의: 이 API는 `@Profile("local")` 전용입니다. 운영/배포 프로필에서는 컨트롤러가 등록되지 않습니다.

## 주요 API

### 로그인 시작

```http
GET /oauth2/authorization/google
```

브라우저에서 직접 이동하는 URL입니다.

```text
http://localhost:8080/oauth2/authorization/google
```

### 내 정보 조회

```http
GET /api/users/me
Authorization: Bearer <token>
```

응답 예시:

```json
{
  "id": 1,
  "email": "student@inha.edu",
  "name": "홍길동"
}
```

### 동아리 목록 조회

```http
GET /api/clubs?page=0&size=20
Authorization: Bearer <token>
```

필터 파라미터를 함께 보낼 수 있습니다.

```http
GET /api/clubs?page=0&size=20&category=IT/개발&keyword=AI&hasActiveRecruitment=true
Authorization: Bearer <token>
```

응답 예시:

```json
{
  "content": [
    {
      "id": 1,
      "name": "GDG on Campus Inha",
      "description": "개발과 기술 공유를 중심으로 활동하는 학생 커뮤니티입니다.",
      "category": "IT/개발",
      "profileImg": "https://placehold.co/400x400?text=gdg-on-campus-inha",
      "activeRecruitment": true,
      "recruitmentDisplayText": "상시모집"
    }
  ],
  "page": 0,
  "size": 20,
  "hasNext": true
}
```

모집 상태 필터는 기존처럼 boolean으로 유지합니다.

```http
GET /api/clubs?hasActiveRecruitment=true
GET /api/clubs?hasActiveRecruitment=false
```

`activeRecruitment=true` 판정 기준:

- 모집 공고의 `isActive=true`
- 그리고 `isAlwaysOpen=true` 이거나
- 현재 날짜가 `startDate` 이상이고 `endDate` 이하

`recruitmentDisplayText`는 프론트 표시용 필드입니다.

- 상시모집: `"상시모집"`
- 기간모집: `"2026-05-16 ~ 2026-06-06"`
- 모집마감: `"모집마감"`

### 동아리 상세 조회

```http
GET /api/clubs/{clubId}
Authorization: Bearer <token>
```

응답에는 동아리 기본 정보, 자세한 활동 설명, 연락처, 인스타그램 URL, 활성 모집 공고가 포함됩니다.

### 행사 목록 조회

```http
GET /api/events?page=0&size=20
Authorization: Bearer <token>
```

필터 파라미터:

```http
GET /api/events?page=0&size=20&keyword=정기&clubId=1&fromDate=2026-05-01&toDate=2026-06-30
Authorization: Bearer <token>
```

응답 예시:

```json
{
  "content": [
    {
      "id": 1,
      "clubId": 1,
      "clubName": "GDG on Campus Inha",
      "title": "GDG on Campus Inha 정기 활동",
      "description": "GDG on Campus Inha에서 준비한 공개 활동 및 교류 행사입니다.",
      "eventDate": "2026-05-22",
      "location": "인하대학교 학생회관",
      "imageUrl": "https://placehold.co/400x400?text=gdg-on-campus-inha"
    }
  ],
  "page": 0,
  "size": 20,
  "hasNext": true
}
```

### 메인 최근/예정 행사 조회

```http
GET /api/events/recent?size=3
Authorization: Bearer <token>
```

응답 예시:

```json
[
  {
    "id": 1,
    "clubId": 1,
    "clubName": "GDG on Campus Inha",
    "title": "GDG on Campus Inha 정기 활동",
    "description": "GDG on Campus Inha에서 준비한 공개 활동 및 교류 행사입니다.",
    "eventDate": "2026-05-22",
    "location": "인하대학교 학생회관",
    "imageUrl": "https://placehold.co/400x400?text=gdg-on-campus-inha"
  }
]
```

### 동아리 회원/신청자 목록 조회

회장 페이지 1차 연동용 API입니다. 현재는 `club_members` 테이블의 local 더미 데이터를 기준으로 동작합니다.

```http
GET /api/clubs/{clubId}/members?status=applicant&keyword=홍길동
Authorization: Bearer <token>
```

응답 예시:

```json
[
  {
    "id": 1,
    "name": "홍길동",
    "major": "컴퓨터공학과 24학번",
    "email": "asdasd@gmail.com",
    "birth": "2001-12-12",
    "phone": "010-1234-5678",
    "image": "",
    "status": "applicant"
  }
]
```

### 동아리 신청자 수락

```http
PATCH /api/clubs/{clubId}/members/{memberId}/accept
Authorization: Bearer <token>
```

신청자 상태를 `member`로 변경합니다.

### 동아리 신청자 거절

```http
DELETE /api/clubs/{clubId}/members/{memberId}
Authorization: Bearer <token>
```

현재 구현은 row를 삭제하지 않고 상태를 `rejected`로 변경합니다.

## DB 구조 요약

현재 프론트 연동에 직접 쓰는 주요 테이블은 다음과 같습니다.

### `clubs`

동아리 기본 정보입니다.

주요 필드:

- `id`
- `name`
- `description`
- `activity_description`
- `category`
- `profile_img`
- `contact`
- `instagram_url`
- `created_at`
- `updated_at`

### `recruitments`

동아리 모집 공고입니다.

주요 필드:

- `id`
- `club_id`
- `title`
- `summary`
- `start_date`
- `end_date`
- `is_always_open`
- `is_active`
- `form_schema` JSONB
- `created_at`
- `updated_at`

현재 `GET /api/clubs`의 가입 가능/불가능 필터는 이 테이블의 `is_active`, `is_always_open`, `start_date`, `end_date`를 기준으로 계산합니다.

### `events`

동아리 행사 정보입니다.

주요 필드:

- `id`
- `club_id`
- `title`
- `description`
- `event_date`
- `location`
- `image_url`
- `published`
- `created_at`
- `updated_at`

### `club_members`

회장 페이지 1차 연동용 회원/신청자 정보입니다.

주요 필드:

- `id`
- `club_id`
- `name`
- `major`
- `email`
- `birth`
- `phone`
- `image`
- `status`

`status` 값:

- `member`
- `applicant`
- `rejected`

## curl 테스트 예시

로그인으로 받은 JWT가 있다고 가정합니다.

```bash
TOKEN="YOUR_BACKEND_JWT"
```

```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/users/me
```

```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/clubs?page=0&size=20"
```

모집중/마감 필터:

```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/clubs?page=0&size=5&hasActiveRecruitment=true"
```

```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/clubs?page=0&size=5&hasActiveRecruitment=false"
```

행사:

```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/events/recent?size=3"
```

```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/clubs/1
```

## DB 설계 현황

현재 핵심 테이블:

- `users`
- `clubs`
- `club_managers`
- `recruitments`
- `applications`

현재 MVP에서 우선 사용하는 테이블:

- `users`
- `clubs`
- `recruitments`

`clubs` 주요 필드:

- `id`
- `name`
- `description`
- `activity_description`
- `category`
- `profile_img`
- `contact`
- `instagram_url`
- `created_at`
- `updated_at`

`recruitments` 주요 필드:

- `id`
- `club_id`
- `title`
- `summary`
- `start_date`
- `end_date`
- `is_always_open`
- `is_active`
- `form_schema`
- `created_at`
- `updated_at`

## Supabase 사용 계획

로컬 개발은 Docker PostgreSQL로 진행하고, 팀 공유용 개발 DB는 Supabase PostgreSQL을 사용할 예정입니다.

권장 순서:

1. 로컬에서 로그인, 유저 조회, 동아리 목록, 동아리 상세 흐름 확인
2. Supabase에 개발 DB 생성
3. Supabase 접속 정보를 환경 변수 또는 secret yml로 분리
4. 백엔드 배포 환경에서 Supabase DB 연결
5. 팀원이 같은 dev DB로 통합 테스트

## 주의사항

- API 호출에 사용하는 토큰은 Google access token이 아니라 **백엔드가 발급한 JWT**입니다.
- 동아리 목록과 상세 조회는 현재 정책상 로그인한 사용자만 접근 가능합니다.
- `application-secret.yml`은 절대 Git에 올리지 않습니다.
- `OAuth2SuccessHandler`의 프론트 redirect URL은 현재 로컬 테스트용 `http://localhost:3000`입니다. 실제 프론트 배포 후에는 환경 변수 기반으로 변경해야 합니다.
- `local` 프로필 더미데이터는 개발 테스트용입니다. 운영 DB에서는 사용하지 않습니다.

## 테스트

```bash
./gradlew test
```

최근 확인 결과:

```text
BUILD SUCCESSFUL
```

## License

Copyright 2026 Team Dongne.
