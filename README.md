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
- 로컬 테스트용 동아리 더미데이터 30개
- OAuth + 동아리 목록 테스트용 단일 Node 페이지

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
├── oauth-club-test.mjs
└── src/main
    ├── java/geeks/dongnea
    │   ├── domain
    │   │   ├── user
    │   │   ├── club
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

`local` 프로필에서는 앱 시작 시 동아리 더미데이터 30개와 각 동아리의 모집 공고 1개가 생성됩니다. 이미 `clubs` 테이블에 데이터가 있으면 더미데이터를 다시 넣지 않습니다.

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
http://localhost:3000/oauth2/redirect?token=<JWT>
```

그래서 프론트가 아직 없을 때는 테스트용 단일 파일 서버를 실행하면 됩니다.

### 1. 테스트 페이지 실행

새 터미널에서 실행합니다.

```bash
node oauth-club-test.mjs
```

브라우저에서 접속합니다.

```text
http://localhost:3000
```

### 2. 테스트 흐름

1. `Google 로그인` 버튼 클릭
2. Google 학교 계정으로 로그인
3. 로그인 성공 후 `localhost:3000`으로 복귀
4. 테스트 페이지가 URL의 `token`을 저장
5. `/api/users/me` 호출
6. `/api/clubs?page=0&size=10` 호출
7. 왼쪽 동아리 목록 스크롤
8. 동아리 클릭
9. `/api/clubs/{clubId}` 상세 정보 확인

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
      "profileImg": "https://placehold.co/400x400?text=gdg-on-campus-inha"
    }
  ],
  "page": 0,
  "size": 20,
  "hasNext": true
}
```

### 동아리 상세 조회

```http
GET /api/clubs/{clubId}
Authorization: Bearer <token>
```

응답에는 동아리 기본 정보, 자세한 활동 설명, 연락처, 인스타그램 URL, 활성 모집 공고가 포함됩니다.

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
