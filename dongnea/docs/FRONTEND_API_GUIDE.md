# 백엔드 API 연동 가이드

프론트엔드 연동 기준으로 정리한 백엔드 API 문서입니다.

## 핵심 요약

- 회장 여부는 `club_managers` 기준입니다.
- 회원/신청자는 `club_members.status` 기준이며, 현재 사용하는 값은 `member`, `applicant`만입니다.
- 프론트는 `/api/users/me` 응답의 `manager`, `managedClubs`를 보고 일반 유저/회장 유저 화면을 분기하면 됩니다.
- 자유 게시판은 현재 구현하지 않습니다.
- 회장 계정 글쓰기 범위는 동아리 소개 수정, 공지 작성, 행사 작성입니다.
- 이미지 업로드 연동은 `docs/IMAGE_UPLOAD_FRONTEND_GUIDE.md`를 참고합니다.
- 가입 거절, 회원 삭제, 유저 탈퇴는 `club_members` row를 실제 삭제합니다.
- `club_members.birth`, `rejected`, `left`는 더 이상 사용하지 않습니다.

## 최근 변경사항

- 신청자 거절 API가 변경되었습니다.
  - 이전: `DELETE /api/clubs/{clubId}/members/{memberId}`로 거절 처리
  - 현재: `PATCH /api/clubs/{clubId}/members/{memberId}/reject`로 거절 처리
- `DELETE /api/clubs/{clubId}/members/{memberId}`는 이제 거절이 아니라 실제 회원/신청자 삭제입니다.
- `DELETE /api/users/me/clubs/{clubId}`는 내 동아리 탈퇴이며, `club_members` row를 실제 삭제합니다.
- 지원서 상태를 `REJECTED`로 바꾸면 연결된 `club_members` 신청자 row도 삭제됩니다.
- 지원서 상태를 `ACCEPTED`로 바꾸면 연결된 `club_members.status`가 `member`로 변경됩니다.
- 회장(`PRESIDENT`)은 바로 탈퇴/삭제할 수 없고, 먼저 회장 권한을 양도해야 합니다.

## 1. 인증

로그인 후 프론트는 백엔드 JWT를 `localStorage.accessToken`에 저장하고, 이후 API 요청에 아래 헤더를 붙입니다.

```http
Authorization: Bearer {accessToken}
```

### Google 로그인 시작

```http
GET /oauth2/authorization/google
```

로그인 성공 후 백엔드는 프론트 로그인 페이지로 토큰을 전달합니다.

```text
{frontend}/login?token={BACKEND_JWT}
```

## 2. 회장/일반 유저 구분

### 내 정보 조회

```http
GET /api/users/me
Authorization: Bearer {accessToken}
```

응답 예시:

```json
{
  "id": 4,
  "email": "hhjjpp03@inha.edu",
  "name": "박현진",
  "manager": true,
  "managedClubs": [
    {
      "clubId": 1,
      "clubName": "GDG on Campus Inha",
      "role": "PRESIDENT"
    }
  ]
}
```

프론트 처리 기준:

```ts
if (user.manager) {
  // 회장/관리자 화면 진입 가능
}

const managedClubId = user.managedClubs[0]?.clubId;
```

`managedClubs`가 비어 있으면 일반 유저입니다.

## 3. DB 역할 구분

### club_managers

회장/관리자 권한을 저장합니다.

```text
user_id = 유저 ID
club_id = 관리하는 동아리 ID
role = PRESIDENT
```

회장 여부는 반드시 `club_managers` 또는 `/api/users/me`의 `manager`, `managedClubs`로 판단합니다.

### club_members

현재 회원/신청자 목록에 보여줄 사람만 저장합니다.

```text
status = member     -> 가입된 회원
status = applicant  -> 가입 신청자
```

`rejected`, `left` 상태는 사용하지 않습니다. 가입 거절, 탈퇴, 강제 삭제는 `club_members` row를 삭제합니다.

현재 프론트에서 사용하는 회원 정보 필드는 아래 기준으로 통일합니다.

```text
name          -> 이름
studentNumber -> 학번 8자리
department    -> 학과
phone         -> 전화번호
email         -> 이메일
image         -> 프로필 이미지 URL 또는 빈 문자열
status        -> member 또는 applicant
```

`birth` 컬럼/필드는 더 이상 사용하지 않습니다.

회장도 특정 동아리의 회원일 수 있지만, 회장 여부 판단은 `club_members`가 아니라 `club_managers` 기준입니다.

## 4. 동아리 목록/상세

### 동아리 목록

```http
GET /api/clubs?page=0&size=20&category=IT/개발&keyword=gdg&hasActiveRecruitment=true
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
      "profileImg": "https://placehold.co/400x400?text=gdg-inha",
      "activeRecruitment": true,
      "recruitmentDisplayText": "상시모집"
    }
  ],
  "page": 0,
  "size": 20,
  "hasNext": false
}
```

### 동아리 상세

```http
GET /api/clubs/{clubId}
```

응답 예시:

```json
{
  "id": 1,
  "name": "GDG on Campus Inha",
  "description": "짧은 소개",
  "activityDescription": "상세 소개글",
  "category": "IT/개발",
  "profileImg": "https://placehold.co/400x400?text=gdg-inha",
  "contact": "gdg@example.com",
  "instagramUrl": "https://instagram.com/gdg",
  "recruitments": [
    {
      "id": 1,
      "title": "신입 부원 모집",
      "summary": "함께 활동할 신입 부원을 모집합니다.",
      "startDate": null,
      "endDate": null,
      "alwaysOpen": true,
      "active": true,
      "formSchema": {
        "questions": [
          {
            "id": "motivation",
            "label": "지원 동기",
            "type": "textarea",
            "required": true
          }
        ]
      }
    }
  ]
}
```

## 5. 내가 가입한 동아리

```http
GET /api/users/me/clubs
Authorization: Bearer {accessToken}
```

응답 예시:

```json
[
  {
    "id": 1,
    "name": "GDG on Campus Inha",
    "description": "개발과 기술 공유를 중심으로 활동하는 학생 커뮤니티입니다.",
    "category": "IT/개발",
    "profileImg": "https://placehold.co/400x400?text=gdg-inha",
    "activeRecruitment": true,
    "recruitmentDisplayText": "상시모집"
  }
]
```

## 6. 지원서 제출

동아리 상세 응답의 `recruitments` 중 `active: true`인 모집 공고의 `id`를 사용합니다.

```http
POST /api/applications
Authorization: Bearer {accessToken}
Content-Type: application/json
```

요청 예시:

```json
{
  "recruitmentId": 1,
  "answers": {
    "name": "김정우",
    "studentNumber": "12241234",
    "department": "인공지능공학과",
    "phone": "010-0000-0000",
    "motivation": "프로젝트 활동에 참여하고 싶습니다."
  }
}
```

지원서 제출 시 백엔드는 다음을 처리합니다.

- `applications`에 지원서 저장
- `club_members`에 `status = applicant`로 신청자 추가

## 7. 회장 페이지 회원/신청자 목록

회장 페이지에서는 `/api/users/me`의 `managedClubs[0].clubId`를 사용해서 호출하면 됩니다.

### 회원 목록

```http
GET /api/clubs/{clubId}/members?status=member
Authorization: Bearer {accessToken}
```

### 신청자 목록

```http
GET /api/clubs/{clubId}/members?status=applicant
Authorization: Bearer {accessToken}
```

검색이 필요하면 `keyword`를 붙입니다.

```http
GET /api/clubs/{clubId}/members?status=applicant&keyword=김정우
```

응답 예시:

```json
[
  {
    "id": 4,
    "name": "김정우",
    "studentNumber": "12241234",
    "department": "인공지능공학과",
    "phone": "010-0000-0000",
    "email": "26_ai_jwk@inha.edu",
    "image": "",
    "status": "applicant"
  }
]
```

## 8. 신청자 수락/거절

### 수락

```http
PATCH /api/clubs/{clubId}/members/{memberId}/accept
Authorization: Bearer {accessToken}
```

수락하면 해당 `club_members` row의 `status`가 `member`로 변경됩니다.

처리 결과:

```text
club_members.status = member
latest applications.status = ACCEPTED
```

### 거절

```http
PATCH /api/clubs/{clubId}/members/{memberId}/reject
Authorization: Bearer {accessToken}
```

거절하면 해당 `club_members` row는 실제 삭제됩니다. 지원서 기록은 최신 지원서 기준으로 `REJECTED`가 남습니다.

처리 결과:

```text
delete from club_members where id = {memberId}
latest applications.status = REJECTED
```

프론트 처리 기준:

- 성공 응답은 `204 No Content`
- 성공 후 신청자 목록을 다시 조회하거나, 화면 상태에서 해당 `memberId`를 제거하면 됩니다.

### 회원/신청자 삭제

거절과 별개로 회장이 회원 또는 신청자를 목록에서 실제 삭제할 때 사용합니다.

```http
DELETE /api/clubs/{clubId}/members/{memberId}
Authorization: Bearer {accessToken}
```

처리 결과:

```text
delete from club_members where id = {memberId}
```

주의:

- 이 API는 `applications.status`를 변경하지 않습니다.
- 회장(`PRESIDENT`)은 이 API로 삭제할 수 없습니다.
- 회장을 삭제해야 하면 먼저 회장 권한 양도 API를 호출해야 합니다.

## 9. 회장 권한 양도 / 탈퇴

### 회장 권한 양도

현재 회장이 가입 승인된 회원에게 `PRESIDENT` 권한을 넘깁니다.

```http
PATCH /api/clubs/{clubId}/managers/president/transfer
Authorization: Bearer {accessToken}
Content-Type: application/json
```

요청 예시:

```json
{
  "targetEmail": "transfer.gdg.member@inha.edu"
}
```

주의:

- `targetEmail` 유저는 `users`에 존재해야 합니다.
- 해당 유저는 같은 동아리의 `club_members.status = member` 상태여야 합니다.
- 양도 후 기존 `PRESIDENT` 권한은 대상 유저로 교체됩니다.
- 프론트는 양도 성공 후 `/api/users/me`를 다시 호출해서 현재 유저의 `manager`, `managedClubs`를 갱신하는 것이 좋습니다.

### 내 동아리 탈퇴

현재 로그인한 유저가 가입된 동아리에서 탈퇴합니다.

```http
DELETE /api/users/me/clubs/{clubId}
Authorization: Bearer {accessToken}
```

처리 결과:

```text
delete from club_members
where club_id = {clubId}
  and email = currentUser.email
```

탈퇴 후 `/api/users/me/clubs`에서는 해당 동아리가 보이지 않습니다.

주의:

- 회장(`PRESIDENT`)은 바로 탈퇴할 수 없습니다.
- 회장 권한 양도 후 탈퇴해야 합니다.

## 10. 회장용 글쓰기 API

회장 권한이 있는 동아리에 대해서만 가능합니다.

### 동아리 소개 수정

```http
PATCH /api/clubs/{clubId}/profile
Authorization: Bearer {accessToken}
Content-Type: application/json
```

요청 예시:

```json
{
  "description": "짧은 소개",
  "activityDescription": "상세 소개글",
  "category": "IT/개발",
  "profileImg": "https://example.com/logo.png",
  "contact": "gdg@example.com",
  "instagramUrl": "https://instagram.com/gdg"
}
```

### 공지 목록

```http
GET /api/clubs/{clubId}/notices
```

### 공지 작성

```http
POST /api/clubs/{clubId}/notices
Authorization: Bearer {accessToken}
Content-Type: application/json
```

요청 예시:

```json
{
  "title": "신입 부원 OT 안내",
  "content": "오리엔테이션 일정과 장소를 안내합니다.",
  "noticeDate": "2026-06-20",
  "badge": "필독",
  "pinned": true,
  "imageUrl": "https://example.com/notice.png"
}
```

### 공지 수정/삭제

```http
PATCH  /api/clubs/{clubId}/notices/{noticeId}
DELETE /api/clubs/{clubId}/notices/{noticeId}
```

### 행사 목록

```http
GET /api/events?page=0&size=20
GET /api/events/recent?size=3
GET /api/events?clubId={clubId}
```

### 행사 작성

```http
POST /api/events?clubId={clubId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

요청 예시:

```json
{
  "title": "정기 세미나",
  "description": "동아리 공개 세미나입니다.",
  "eventDate": "2026-06-27",
  "location": "인하대학교 학생회관",
  "imageUrl": "https://example.com/event.png",
  "published": true
}
```

### 행사 수정/삭제

```http
PATCH  /api/events/{eventId}
DELETE /api/events/{eventId}
```

## 11. 활동기록

활동기록 화면용 API입니다. 현재 글쓰기 핵심 범위는 소개/공지/행사지만, 활동기록 DB/API도 준비되어 있습니다.

```http
GET    /api/clubs/{clubId}/activities
POST   /api/clubs/{clubId}/activities
PATCH  /api/clubs/{clubId}/activities/{activityId}
DELETE /api/clubs/{clubId}/activities/{activityId}
```

## 12. 프론트 구현 흐름 추천

1. 로그인 후 `/api/users/me` 호출
2. `manager === true`면 회장 페이지 접근 가능
3. 회장 페이지에서는 `managedClubs[0].clubId` 기준으로 회원/신청자/공지/행사 API 호출
4. 일반 유저는 `/api/users/me/clubs`로 가입 동아리 조회
5. 동아리 상세의 `recruitments`에서 활성 공고를 찾아 지원서 제출

## 13. 테스트 계정 기준

현재 테스트 DB 기준 예시입니다.

```text
hhjjpp03@inha.edu
- GDG on Campus Inha 회장
- GDG on Campus Inha 회원

pshpite1004@inha.edu
- 인하 밴드 회장
- GDG on Campus Inha 회원

26_ai_jwk@inha.edu
- 봉사단 하랑 회장
- 봉사단 하랑 회원
- GDG on Campus Inha 신청자
```

테스트 데이터는 Supabase DB에 직접 SQL로 삽입해서 관리합니다.

추가 테스트 회원/신청자는 아래 파일을 Supabase SQL Editor에서 직접 실행하면 됩니다.

```text
dongnea/docs/manual-test-members.sql
```
