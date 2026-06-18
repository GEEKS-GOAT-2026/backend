# Dongne API Contract

이 문서는 현재 확정된 앱 워크프레임 기준으로 프론트가 호출할 백엔드 API 계약을 정리한다.

## Current Membership Policy

- `club_members`는 현재 화면에 표시할 회원/신청자만 저장한다.
- `club_members.status`는 `member`, `applicant`만 사용한다.
- `rejected`, `left`, `birth`는 사용하지 않는다.
- 가입 거절, 회원 삭제, 유저 탈퇴는 `club_members` row를 실제 삭제한다.
- 지원서 기록은 `applications.status`에 `PENDING`, `ACCEPTED`, `REJECTED`로 남긴다.
- 회장(`PRESIDENT`)은 바로 삭제/탈퇴할 수 없고, 먼저 회장 권한을 양도해야 한다.

## Base URL

```text
https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app
```

로컬 프론트에서는 `.env.local`에 아래처럼 둔다.

```text
NEXT_PUBLIC_API_BASE_URL=https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app
NEXT_PUBLIC_BACKEND_LOGIN_URL=https://port-0-dongnea-mhfzs5l502d0035e.sel3.cloudtype.app/oauth2/authorization/google
```

## Auth

로그인 후 프론트는 백엔드 JWT를 `localStorage.accessToken`에 저장하고 API 요청에 붙인다.

```http
Authorization: Bearer {accessToken}
```

### Login

```http
GET /oauth2/authorization/google
```

Google OAuth 성공 후 백엔드는 `APP_FRONTEND_REDIRECT_URI`로 아래 형태로 이동시킨다.

```text
{frontend}/login?token={BACKEND_JWT}
```

### My Info

```http
GET /api/users/me
```

응답:

```json
{
  "id": 1,
  "email": "student@inha.edu",
  "name": "홍길동",
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

프론트는 회장 화면 진입 여부를 `manager` 또는 `managedClubs.length > 0`으로 판단한다.

## Club Read APIs

### Club List

```http
GET /api/clubs?page=0&size=20&category=IT/개발&keyword=gdg&hasActiveRecruitment=true
```

응답:

```json
{
  "content": [
    {
      "id": 1,
      "name": "GDG on Campus Inha",
      "description": "개발과 기술 공유를 중심으로 활동하는 학생 커뮤니티입니다.",
      "category": "IT/개발",
      "profileImg": "https://placehold.co/400x400?text=gdg",
      "activeRecruitment": true,
      "recruitmentDisplayText": "상시모집"
    }
  ],
  "page": 0,
  "size": 20,
  "hasNext": false
}
```

### Club Detail

```http
GET /api/clubs/{clubId}
```

응답:

```json
{
  "id": 1,
  "name": "GDG on Campus Inha",
  "description": "짧은 소개",
  "activityDescription": "상세 소개",
  "category": "IT/개발",
  "profileImg": "https://placehold.co/400x400?text=gdg",
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

## President Write APIs

회장 글쓰기 기능은 자유 게시판이 아니라 아래 세 범위만 지원한다.

- 동아리 소개 수정
- 공지 작성/수정/삭제
- 행사 작성/수정/삭제

모든 API는 JWT 인증이 필요하고, 해당 동아리의 `club_managers` 권한이 있어야 한다.

### Update Club Profile

```http
PATCH /api/clubs/{clubId}/profile
Content-Type: application/json
```

요청:

```json
{
  "description": "짧은 카드 소개",
  "activityDescription": "상세 소개글",
  "category": "IT/개발",
  "profileImg": "https://example.com/club.png",
  "contact": "gdg@example.com",
  "instagramUrl": "https://instagram.com/gdg"
}
```

응답은 `GET /api/clubs/{clubId}`와 같은 `ClubDetailResponse`다.

### Create Notice

```http
POST /api/clubs/{clubId}/notices
Content-Type: application/json
```

요청:

```json
{
  "title": "신입 부원 OT 안내",
  "content": "오리엔테이션 일정과 장소를 안내합니다.",
  "noticeDate": "2026-06-20",
  "badge": "필독",
  "pinned": true
}
```

### Notice List

```http
GET /api/clubs/{clubId}/notices
```

응답:

```json
[
  {
    "id": 1,
    "clubId": 1,
    "clubName": "GDG on Campus Inha",
    "title": "신입 부원 OT 안내",
    "content": "오리엔테이션 일정과 장소를 안내합니다.",
    "noticeDate": "2026-06-20",
    "badge": "필독",
    "pinned": true
  }
]
```

### Update/Delete Notice

```http
PATCH /api/clubs/{clubId}/notices/{noticeId}
DELETE /api/clubs/{clubId}/notices/{noticeId}
```

### Create Event

```http
POST /api/events?clubId={clubId}
Content-Type: application/json
```

요청:

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

### Event List

```http
GET /api/events?page=0&size=20&keyword=세미나&clubId=1&fromDate=2026-06-01&toDate=2026-06-30
GET /api/events/recent?size=3
```

응답 항목:

```json
{
  "id": 1,
  "clubId": 1,
  "clubName": "GDG on Campus Inha",
  "title": "정기 세미나",
  "description": "동아리 공개 세미나입니다.",
  "eventDate": "2026-06-27",
  "location": "인하대학교 학생회관",
  "imageUrl": "https://example.com/event.png"
}
```

### Update/Delete Event

```http
PATCH /api/events/{eventId}
DELETE /api/events/{eventId}
```

## Recruitment And Application

모집 공고는 동아리 상세의 `recruitments[].formSchema`로 프론트가 지원서 폼을 그린다.

### Create Recruitment

```http
POST /api/recruitments?clubId={clubId}
Content-Type: application/json
```

요청:

```json
{
  "title": "2026 신입 부원 모집",
  "summary": "함께 활동할 부원을 모집합니다.",
  "alwaysOpen": false,
  "startDate": "2026-06-01",
  "endDate": "2026-06-30",
  "formSchema": {
    "questions": [
      {
        "id": "name",
        "label": "이름",
        "type": "text",
        "required": true
      },
      {
        "id": "studentNumber",
        "label": "학번",
        "type": "text",
        "required": true
      },
      {
        "id": "department",
        "label": "학과",
        "type": "text",
        "required": true
      },
      {
        "id": "motivation",
        "label": "지원 동기",
        "type": "textarea",
        "required": true
      }
    ]
  }
}
```

### Submit Application

```http
POST /api/applications
Content-Type: application/json
```

요청:

```json
{
  "recruitmentId": 1,
  "answers": {
    "name": "홍길동",
    "studentNumber": "12241234",
    "department": "컴퓨터공학과",
    "phone": "010-1234-5678",
    "motivation": "함께 프로젝트를 하고 싶습니다."
  }
}
```

지원서 제출 시 백엔드는 `applications`에 답변을 저장하고, 회장 페이지 신청자 목록용 `club_members(status=applicant)`도 생성한다.

## President Management

### Members / Applicants

```http
GET /api/clubs/{clubId}/members?status=member&keyword=홍길동
GET /api/clubs/{clubId}/members?status=applicant
```

응답 항목:

```json
{
  "id": 1,
  "name": "홍길동",
  "studentNumber": "12241234",
  "department": "컴퓨터공학과",
  "phone": "010-1234-5678",
  "email": "student@inha.edu",
  "image": "https://example.com/profile.png",
  "status": "applicant"
}
```

### Accept/Reject Applicant

```http
PATCH /api/clubs/{clubId}/members/{memberId}/accept
PATCH /api/clubs/{clubId}/members/{memberId}/reject
```

수락 처리:

```text
club_members.status = member
latest applications.status = ACCEPTED
```

거절 처리:

```text
delete from club_members where id = {memberId}
latest applications.status = REJECTED
```

### Remove Member / Leave Club

회장이 회원 또는 신청자를 목록에서 실제 삭제한다.

```http
DELETE /api/clubs/{clubId}/members/{memberId}
```

현재 로그인한 유저가 직접 동아리에서 탈퇴한다.

```http
DELETE /api/users/me/clubs/{clubId}
```

두 API 모두 `club_members` row를 실제 삭제한다. 단, 회장(`PRESIDENT`)은 먼저 권한 양도 후 삭제/탈퇴해야 한다.

### Applications

```http
GET /api/clubs/{clubId}/applications
PATCH /api/applications/{applicationId}/status
```

상태값:

```text
PENDING
ACCEPTED
REJECTED
```

## Activity Records

프로토타입에 활동기록 화면이 있으므로 DB/API는 유지한다. 다만 현재 확정된 회장 글쓰기 범위가 소개/공지/행사라면 프론트에 작성 화면을 반드시 만들 필요는 없다.

```http
GET /api/clubs/{clubId}/activities
POST /api/clubs/{clubId}/activities
PATCH /api/clubs/{clubId}/activities/{activityId}
DELETE /api/clubs/{clubId}/activities/{activityId}
```
