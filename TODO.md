# Dongne v1.1 TODO

## 현재 제품 우선순위

PM 기준 최우선 기능은 **로그인한 인하대 사용자가 전체 동아리 목록을 무한 스크롤로 탐색하고 상세 정보를 확인하는 흐름**이다.

현재 MVP 핵심 흐름:

```text
Google OAuth 로그인
-> 인하대 계정 검증
-> users 저장/조회
-> /api/users/me로 현재 유저 확인
-> /api/clubs 페이지네이션 목록 조회
-> /api/clubs/{clubId} 상세 조회
```

동아리 생성/수정 기능은 초기 사용자 기능이 아니며, 당분간 팀에서 seed 또는 DB로 직접 관리한다.
회장 편집 기능은 이후 단계로 분리하고, 현재는 조회/탐색 중심 MVP에 집중한다.

## 완료된 작업

- Google OAuth 로그인
- `@inha.edu`, `@inha.ac.kr` 계정 제한
- 로그인 성공 시 `users` 테이블 저장/업데이트
- 백엔드 JWT 발급
- JWT 인증 필터 연결
- OAuth 실패 핸들러 추가
  - 실패 시 프론트 redirect URL로 `error`, `reason`, `message` 전달
- `/api/users/me` 구현
  - `id`, `email`, `name` 반환
- 현재 로그인 유저 조회 공통 서비스 추가
  - `CurrentUserService`
  - JWT/OAuth principal에서 email 추출
  - DB에서 현재 `User` 조회
- 클라이언트가 현재 로그인한 `userId`를 직접 보내지 않도록 방향 정리
- 지원서 제출 테스트 API에서 `userId` 파라미터 제거
- 동아리 목록 페이지네이션 API 구현
  - `GET /api/clubs?page=0&size=20`
- 동아리 상세 조회 API 구현
  - `GET /api/clubs/{clubId}`
- 클럽 상세용 필드 보강
  - `category`
  - `activityDescription`
  - `contact`
  - `instagramUrl`
- 모집 정보 필드 보강
  - `summary`
  - `startDate`
  - `endDate`
  - `isAlwaysOpen`
- 로컬 테스트용 더미 동아리 데이터 30개
- 로컬 테스트용 더미 행사 데이터
  - `GET /api/events`
  - `GET /api/events/recent`
- OAuth/동아리 목록 테스트용 단일 Node 테스트 페이지
- Cloudtype 테스트 서버 배포
- Swagger 문서 접근 public 허용
- 프론트 조회 확인용 public GET API 허용
  - `GET /api/clubs/**`
  - `GET /api/events/**`

## 진행 중

### 1. 프론트 더미 데이터 백엔드 이전

- 프론트 `main`, `clubs`, `events` 화면의 동아리/행사 더미 데이터를 백엔드 seed/API 기준으로 전환
- 동아리 목록은 기존 `GET /api/clubs` 페이지네이션 응답 사용
  - 프론트 카드 렌더링 기준 필드: `id`, `name`, `description`, `category`, `profileImg`, `activeRecruitment`
  - 필터 파라미터: `category`, `keyword`, `hasActiveRecruitment`
- 메인 최근 행사는 `GET /api/events/recent` 사용
- 전체 행사 목록은 `GET /api/events` 사용
  - 프론트 카드 렌더링 기준 필드: `id`, `clubId`, `clubName`, `title`, `description`, `eventDate`, `location`, `imageUrl`
  - 필터 파라미터: `keyword`, `clubId`, `fromDate`, `toDate`
- 회장 페이지의 회원/신청자 더미는 지원서/관리자 권한 API 실제화 단계에서 분리 처리
  - 1차로 `club_members` 테이블과 `GET /api/clubs/{clubId}/members` 연결
  - 프론트 회원/신청자 카드 기준 필드: `id`, `name`, `major`, `email`, `birth`, `phone`, `image`, `status`
  - 필터 파라미터: `status`, `keyword`
  - 수락/거절 API: `PATCH /api/clubs/{clubId}/members/{memberId}/accept`, `DELETE /api/clubs/{clubId}/members/{memberId}`

## 지금 프론트와 합의해야 할 것

- 로그인 시작 URL
  - 현재: `GET /oauth2/authorization/google`
- 로그인 성공 redirect URL
  - 현재 로컬: `http://localhost:3000/oauth2/redirect?token=<JWT>`
- 로그인 실패 redirect URL
  - 현재 로컬: `http://localhost:3000/oauth2/redirect?error=...&message=...`
- 프론트 token 저장 위치
- `/api/users/me` 응답 사용 방식
  - 프론트는 여기서 받은 `id`를 화면 상태에 저장할 수 있음
  - 단, API 요청에서 현재 로그인한 `userId`를 다시 보내면 안 됨
- 동아리 목록 page size
- 클럽 카드에 필요한 필드
- 상세 페이지에서 보여줄 모집 정보 표현
  - 기간 모집
  - 상시 모집
- 행사 카드에 필요한 필드
  - 현재 1차 계약: `id`, `clubId`, `clubName`, `title`, `description`, `eventDate`, `location`, `imageUrl`
- 회장 페이지 회원/신청자 목록의 실제 출처
  - 현재 프론트 카드 형태 기준으로 `club_members` 1차 테이블을 추가
  - 이후 실제 지원서 제출 플로우와 연결할 때 `applications -> club_members(applicant)` 생성 흐름 결정 필요

## 다음 백엔드 우선순위

### 0. 팀 테스트 서버 안정화

- Supabase SQL seed 확정
  - `clubs`, `recruitments`, `events` 우선
  - `users`, `applications`, `club_members`는 로그인/지원 플로우 확정 후 추가
- Swagger에서 인증 API 테스트 방법 정리
- 무료 Cloudtype sleep 이후 재시작 체크리스트 정리
- 프론트 팀 공유용 연결 정보 최신화
  - API base URL
  - Google login URL
  - Swagger URL

### 1. 설정값 분리

- OAuth 성공 redirect URL 설정화
- OAuth 실패 redirect URL 설정화
- CORS 허용 origin 설정화
- 로컬/배포 환경 profile 정리

### 2. 에러 응답 통일

- `IllegalArgumentException`을 그대로 노출하지 않도록 전역 예외 처리 추가
- 401 인증 실패 응답 통일
- 403 권한 없음 응답 통일
- OAuth 실패 메시지 코드화

### 3. 동아리 목록 API 안정화

- 필터 API 1차 반영 완료 후 세부 조정
- 필요하면 검색 범위/정렬/응답 필드 추가 논의
- 이미지 URL 실제 저장소 정책 결정
- 프론트 화면 기준 응답 필드 최종 확정
  - 목록 카드
  - 상세 페이지
  - 모집 상태 표시

### 3-1. 행사 API 안정화

- `events` 테이블 1차 추가
- 메인/행사 페이지용 조회 API 추가
- 이후 회장 권한 기반 행사 생성/수정/삭제 API 추가 논의
- 날짜 정렬, 지난 행사 포함 여부, 동아리별 행사 필터 확정

### 4. 모집 공고 API 실제화

- 현재 `RecruitmentController`는 테스트 성격이 강함
- Controller -> Service -> Repository 구조로 정리
- 관리자 권한 확인 후 모집 공고 생성
- 활성 모집 공고 조회
- 상시모집/기간모집 validation 추가

### 5. 지원서 API 실제화

- 현재 로그인 유저 기준으로 지원서 제출
- 중복 지원 예외 처리
- 내 지원 현황 조회
- 동아리 관리자용 지원자 목록 조회
- 지원 상태 변경

### 6. 관리자 권한

- `club_managers` 기반 권한 확인 서비스 추가
- 특정 동아리 관리자 여부 확인
- 내 관리 동아리 목록 조회
  - `GET /api/users/me/managed-clubs`

## DB/배포 계획

- 로컬 개발은 Docker PostgreSQL 사용
- 팀 공유 개발 DB는 Supabase PostgreSQL 우선 검토
- Supabase 연결 전 로컬에서 아래 흐름을 먼저 안정화
  - 로그인
  - `/api/users/me`
  - 동아리 목록
  - 동아리 상세
  - 동아리 목록 필터링
- 이후 Supabase dev DB 연결

## 팀 테스트용 무료 배포 TODO

목표는 운영 배포가 아니라 **팀 테스트용 dev 서버**다. 무료 사용을 우선하므로 Cloudtype 프리티어와 Supabase Free Plan을 기준으로 준비한다.

### 1. 무료 서비스 등록

- Supabase
  - GitHub 계정으로 가입
  - New Project 생성
  - Project name 예: `dongnea-dev`
  - Database password 생성 후 개인 비밀 저장소에 보관
  - Region은 한국/일본/싱가포르 등 가까운 곳 우선
  - Free Plan 선택
  - Free Plan 제한 확인
    - DB 500MB
    - active project 2개
    - 1주 미사용 시 pause 가능
- Cloudtype
  - GitHub 계정 연동
  - 프리티어 사용
  - 카드 등록 요구 가능성 확인
  - GitHub repository 접근 권한 부여
  - Spring Boot 또는 Java 템플릿으로 앱 생성

### 2. Supabase DB 연결

- Supabase Dashboard의 `Connect` 또는 Database connection 메뉴에서 connection string 확인
- Spring Boot에는 JDBC URL 형태로 환경변수 등록
- 비밀번호가 포함된 connection string은 Git, README, 메신저 공개 채널에 공유하지 않음
- Cloudtype 환경변수로만 저장

예시:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://{supabase-host}:{port}/postgres
SPRING_DATASOURCE_USERNAME={supabase-user}
SPRING_DATASOURCE_PASSWORD={supabase-db-password}
```

### 3. Cloudtype 앱 설정

- Root directory: `backend_repo/dongnea`
- Build command:

```bash
./gradlew clean build -x test
```

- Start command:

```bash
java -jar build/libs/dongnea-0.0.1-SNAPSHOT.jar
```

- Port: `8080`
- Java version
  - Java 21 가능하면 21 사용
  - Java 21 선택 불가 시 Java 17로 빌드 가능 여부 확인

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

### 5. 보안 파일 처리

- `application-secret.yml`은 배포 서버에 파일로 올리지 않음
- `application-secret.yml`은 Git에 올리지 않음
- 배포용 민감값은 Cloudtype 환경변수로만 관리
- `spring.config.import`는 `optional:classpath:application-secret.yml`로 유지
  - 로컬에서는 secret 파일을 사용할 수 있음
  - 배포에서는 secret 파일이 없어도 환경변수로 실행 가능
- `JWT_SECRET`은 최소 32바이트 이상으로 생성
- Supabase DB password가 포함된 URL은 문서에 평문 저장 금지

### 6. OAuth 설정

- Google Cloud Console OAuth redirect URI에 Cloudtype 백엔드 주소 추가

```text
https://{cloudtype-backend-domain}/login/oauth2/code/google
```

- 백엔드 성공 redirect는 프론트 주소로 설정

```text
APP_FRONTEND_REDIRECT_URI=https://{frontend-domain}/login
```

- 프론트가 아직 로컬이면 임시로 `http://localhost:3000/login` 사용 가능
  - 단, 팀원별 테스트가 불편하므로 프론트도 임시 배포 URL을 두는 것을 권장

### 7. Seed 정책

- `local` 프로필
  - `ClubDummyDataLoader` 자동 실행
  - `/api/local/auth-token` 사용 가능
- `dev` 프로필
  - local seed 자동 실행 안 함
  - `/api/local/auth-token` 비활성
  - 초기 데이터는 수동 SQL, Supabase import, 또는 별도 1회 seed 방식으로 관리

### 8. 배포 후 확인

- 공개 문서/API 확인

```bash
curl https://{backend-domain}/api-docs
```

- 브라우저 확인

```text
https://{backend-domain}/swagger
https://{backend-domain}/oauth2/authorization/google
```

- 로그인 후 JWT로 API 확인

```bash
curl -H "Authorization: Bearer {JWT}" \
  "https://{backend-domain}/api/clubs?page=0&size=5"
```

- 필터 확인

```bash
curl -H "Authorization: Bearer {JWT}" \
  "https://{backend-domain}/api/clubs?hasActiveRecruitment=true"
```

```bash
curl -H "Authorization: Bearer {JWT}" \
  "https://{backend-domain}/api/clubs?hasActiveRecruitment=false"
```

### 9. 실패 시 우선 확인

- Cloudtype logs
- Java version
- Root directory/build/start command
- `application-secret.yml` import 에러 여부
- Supabase DB URL/username/password
- Supabase 프로젝트 pause 여부
- Google OAuth redirect URI 불일치
- `APP_FRONTEND_REDIRECT_URI` 설정
- 프론트의 백엔드 로그인 URL 설정

## 주의사항

- API 호출에 쓰는 토큰은 Google access token이 아니라 백엔드 JWT다.
- 현재 로그인 유저 식별은 클라이언트 입력값이 아니라 서버의 `CurrentUserService` 기준으로 처리한다.
- `application-secret.yml`은 절대 Git에 올리지 않는다.
- TODO/PRD/세션 메모는 팀 공유 문서로 관리하고, 필요할 때만 별도 합의 후 Git에 포함한다.
