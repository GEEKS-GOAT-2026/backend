# Dongne Backend

인하대학교 동아리 통합 플랫폼 **동네(Dongne)** 백엔드 API 서버입니다.

현재 MVP 우선순위는 다음 사용자 흐름입니다.

```text
Google 로그인 -> 인하대 계정 검증 -> users 저장/조회 -> 동아리 목록 무한 스크롤 -> 동아리 필터링 -> 동아리 상세 확인
```

## 현재 구현 상태

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

### 4. Swagger 확인

```text
http://localhost:8080/swagger
```

Swagger에서 JWT가 필요한 API는 우측 상단 Authorize에 아래 형식으로 입력합니다.

```text
Bearer <token>
```

## 로그인 및 동아리 목록 테스트

현재 백엔드는 Google 로그인 성공 후 아래 주소로 이동합니다.

```text
http://localhost:3000/login?token=<JWT>
```

그래서 프론트가 아직 없을 때는 테스트용 단일 파일 서버를 실행하면 됩니다.

> 개발 초기에는 별도의 테스트용 정적 페이지가 제공되었으나, 현재는 실제 프론트(또는 통합 테스트 환경)를 사용해 연동하도록 변경되었습니다.

프론트와의 인증 연동 방법:

1. 백엔드에서 OAuth2 로그인을 시작합니다:

```text
http://localhost:8080/oauth2/authorization/google
```

2. 로그인 성공 시 백엔드는 `app.frontend.redirect-uri`로 설정된 프론트 주소로 리다이렉트하며, 개발 설정에서는 다음과 같이 JWT를 쿼리 파라미터로 전달합니다:

```text
http://{FRONTEND_HOST}/login?token=<JWT>
```

3. 프론트는 `token`을 받아 저장한 뒤 (`localStorage` 또는 메모리), 이후 API 호출에 `Authorization: Bearer <token>` 헤더를 포함시켜 백엔드에 요청합니다.

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
