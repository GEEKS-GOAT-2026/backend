# 이미지 업로드 프론트 연동 가이드

동아리 소개, 행사, 활동기록, 공지에 사용할 이미지를 Supabase Storage에 업로드하는 API입니다.

## 중요 보안 안내

- 프론트에서는 Supabase Secret key를 사용하지 않습니다.
- `SUPABASE_SECRET_KEY`는 Cloudtype 백엔드 환경변수에만 저장합니다.
- 프론트는 기존 백엔드 JWT(`localStorage.accessToken`)만 사용합니다.
- 채팅이나 문서에 노출된 Secret key는 폐기하고 새 키를 발급해야 합니다.

## 백엔드 환경변수

Cloudtype 서비스 환경변수에 아래 값을 등록합니다.

```text
SUPABASE_URL=https://{project-ref}.supabase.co
SUPABASE_SECRET_KEY={새로 발급한 sb_secret 키}
SUPABASE_STORAGE_BUCKET=club-images
IMAGE_MAX_SIZE=5242880
```

`sb_publishable_...` 키는 이번 방식에서 사용하지 않습니다.

Supabase Storage에는 다음 조건으로 버킷을 생성합니다.

```text
Bucket name: club-images
Public bucket: ON
File size limit: 5MB
Allowed MIME types: image/jpeg, image/png, image/webp
```

## 이미지 업로드 API

```http
POST /api/clubs/{clubId}/images
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

폼 데이터:

```text
file: 이미지 파일
type: PROFILE | EVENT | ACTIVITY | NOTICE
```

권한:

- 로그인 필요
- 해당 `clubId`의 `club_managers` 권한 필요
- JPG, PNG, WebP만 가능
- 최대 5MB

성공 응답 예시:

```json
{
  "imageUrl": "https://project.supabase.co/storage/v1/object/public/club-images/clubs/1/events/uuid.jpg",
  "path": "clubs/1/events/uuid.jpg"
}
```

## 프론트 호출 예시

```ts
type ClubImageType = "PROFILE" | "EVENT" | "ACTIVITY" | "NOTICE";

async function uploadClubImage(
  clubId: number,
  file: File,
  type: ClubImageType,
) {
  const accessToken = localStorage.getItem("accessToken");
  const formData = new FormData();

  formData.append("file", file);
  formData.append("type", type);

  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/clubs/${clubId}/images`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      body: formData,
    },
  );

  if (!response.ok) {
    throw new Error("이미지 업로드에 실패했습니다.");
  }

  return response.json() as Promise<{
    imageUrl: string;
    path: string;
  }>;
}
```

주의:

- `FormData` 요청에서는 `Content-Type`을 직접 지정하지 않습니다.
- 브라우저가 multipart boundary를 포함한 헤더를 자동 생성합니다.
- 글 저장 전에 업로드 API를 먼저 호출해야 합니다.

## 글쓰기 연결 순서

```text
1. 사용자가 이미지 선택
2. uploadClubImage 호출
3. 응답의 imageUrl 저장
4. 기존 글쓰기 JSON의 imageUrl 또는 profileImg에 전달
```

### 동아리 소개 이미지

업로드 시:

```text
type=PROFILE
```

소개 수정 요청:

```http
PATCH /api/clubs/{clubId}/profile
Content-Type: application/json
```

```json
{
  "description": "동아리 소개",
  "activityDescription": "상세 활동 소개",
  "category": "IT/개발",
  "profileImg": "업로드 API에서 받은 imageUrl",
  "contact": "contact@example.com",
  "instagramUrl": "https://instagram.com/example"
}
```

### 행사 이미지

업로드 시:

```text
type=EVENT
```

행사 작성 요청의 `imageUrl`에 전달합니다.

```json
{
  "title": "정기 세미나",
  "description": "행사 설명",
  "eventDate": "2026-06-30",
  "location": "학생회관",
  "imageUrl": "업로드 API에서 받은 imageUrl",
  "published": true
}
```

### 활동기록 이미지

업로드 시:

```text
type=ACTIVITY
```

활동기록 작성 요청의 `imageUrl`에 전달합니다.

```json
{
  "title": "프로젝트 발표회",
  "description": "활동 내용",
  "startDate": "2026-06-01",
  "endDate": "2026-06-01",
  "imageUrl": "업로드 API에서 받은 imageUrl"
}
```

### 공지 이미지

업로드 시:

```text
type=NOTICE
```

공지 작성/수정 요청에도 `imageUrl` 필드가 추가되었습니다.

```json
{
  "title": "신입 부원 OT 안내",
  "content": "공지 내용",
  "noticeDate": "2026-06-30",
  "badge": "필독",
  "pinned": true,
  "imageUrl": "업로드 API에서 받은 imageUrl"
}
```

공지 조회 응답에도 `imageUrl`이 포함됩니다.

## 이미지 없이 작성

이미지는 필수가 아닙니다. 이미지가 없으면 업로드 API를 호출하지 않고 다음처럼 보냅니다.

```json
{
  "imageUrl": null
}
```

프로필은 `profileImg`, 나머지는 `imageUrl` 필드를 사용합니다.

## 프론트 에러 처리

다음 경우 업로드가 실패할 수 있습니다.

- JWT가 없거나 만료됨
- 해당 동아리 관리 권한 없음
- 파일이 5MB 초과
- JPG, PNG, WebP가 아닌 파일
- Supabase Storage 환경변수 또는 버킷 설정 오류

업로드 실패 시 글 작성 API를 호출하지 않고 사용자에게 오류를 표시합니다.

## 배포 후 확인

1. Cloudtype 환경변수 등록
2. 백엔드 재배포
3. Swagger에서 `Club Image API` 확인
4. 회장 JWT로 이미지 업로드
5. 응답의 `imageUrl`을 브라우저에서 열어 이미지 확인
6. 해당 URL로 소개/행사/활동기록/공지 작성
