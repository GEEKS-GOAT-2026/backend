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
  - 실패 시 프론트 redirect URL로 `error`, `message` 전달
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
- OAuth/동아리 목록 테스트용 단일 Node 테스트 페이지

## 진행 중

### 1. 동아리 목록 API 안정화

- `GET /api/clubs`에 필터 파라미터 추가
  - `category`
  - `keyword`
  - `hasActiveRecruitment`
- 무한 스크롤 응답 유지
  - `content`, `page`, `size`, `hasNext`
- 정렬 기준은 우선 `name ASC` 유지
- 프론트와 카드/필터 UI 계약 확인

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

## 다음 백엔드 우선순위

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

## 주의사항

- API 호출에 쓰는 토큰은 Google access token이 아니라 백엔드 JWT다.
- 현재 로그인 유저 식별은 클라이언트 입력값이 아니라 서버의 `CurrentUserService` 기준으로 처리한다.
- `application-secret.yml`은 절대 Git에 올리지 않는다.
- TODO/PRD/세션 메모는 팀 공유 문서로 관리하고, 필요할 때만 별도 합의 후 Git에 포함한다.
