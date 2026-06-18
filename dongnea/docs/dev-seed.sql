-- Dongne dev seed data for Supabase/PostgreSQL.
-- 실행 위치: Supabase SQL Editor
-- 목적: 프론트 연동 확인용 테스트 데이터 삽입.
-- 주의: users 테이블은 Google OAuth 로그인 성공 시 백엔드가 생성한다.

begin;

insert into clubs (
    name,
    description,
    activity_description,
    category,
    profile_img,
    contact,
    instagram_url
) values
(
    'GDG on Campus Inha',
    '개발과 기술 공유를 중심으로 활동하는 학생 커뮤니티입니다.',
    '정기 세미나, 스터디, 해커톤, 프로젝트 발표를 진행합니다. 프론트/백엔드/AI 등 다양한 주제를 팀 프로젝트로 경험합니다.',
    'IT/개발',
    'https://placehold.co/400x400?text=GDG',
    'gdg.inha@example.com',
    'https://instagram.com/gdg.inha'
),
(
    '인하 밴드',
    '합주와 공연을 즐기는 밴드 동아리입니다.',
    '보컬, 기타, 베이스, 드럼, 키보드 파트가 정기 합주와 교내 공연을 준비합니다.',
    '공연',
    'https://placehold.co/400x400?text=BAND',
    'band@example.com',
    'https://instagram.com/inha.band'
),
(
    '봉사단 하랑',
    '지역사회와 함께하는 봉사 동아리입니다.',
    '교육 봉사, 환경 캠페인, 지역 행사 지원 활동을 꾸준히 진행합니다.',
    '봉사',
    'https://placehold.co/400x400?text=HARANG',
    'harang@example.com',
    'https://instagram.com/harang'
),
(
    '인하 축구회',
    '축구를 좋아하는 학생들이 모여 운동하는 체육 동아리입니다.',
    '주 1회 정기 운동과 교내외 친선 경기를 진행합니다.',
    '체육',
    'https://placehold.co/400x400?text=SOCCER',
    'soccer@example.com',
    'https://instagram.com/inha.soccer'
),
(
    '사진연구회',
    '사진 촬영과 편집을 함께 배우는 예술 동아리입니다.',
    '정기 출사, 사진전, 보정 스터디를 통해 결과물을 공유합니다.',
    '예술',
    'https://placehold.co/400x400?text=PHOTO',
    'photo@example.com',
    'https://instagram.com/inha.photo'
)
on conflict (name) do update set
    description = excluded.description,
    activity_description = excluded.activity_description,
    category = excluded.category,
    profile_img = excluded.profile_img,
    contact = excluded.contact,
    instagram_url = excluded.instagram_url;

insert into recruitments (
    club_id,
    title,
    summary,
    start_date,
    end_date,
    is_always_open,
    form_schema,
    is_active
)
select
    c.id,
    c.name || ' 신입 부원 모집',
    '함께 활동할 신입 부원을 모집합니다.',
    null,
    null,
    true,
    '{
      "questions": [
        {"id": "name", "label": "이름", "type": "text", "required": true},
        {"id": "studentNumber", "label": "학번", "type": "text", "required": true},
        {"id": "department", "label": "학과", "type": "text", "required": true},
        {"id": "phone", "label": "전화번호", "type": "text", "required": false},
        {"id": "motivation", "label": "지원 동기", "type": "textarea", "required": true}
      ]
    }'::jsonb,
    true
from clubs c
where c.name in ('GDG on Campus Inha', '봉사단 하랑', '사진연구회')
  and not exists (
      select 1
      from recruitments r
      where r.club_id = c.id
        and r.title = c.name || ' 신입 부원 모집'
  );

insert into recruitments (
    club_id,
    title,
    summary,
    start_date,
    end_date,
    is_always_open,
    form_schema,
    is_active
)
select
    c.id,
    c.name || ' 기간 모집',
    '정해진 기간 동안 신입 부원을 모집합니다.',
    current_date - interval '3 days',
    current_date + interval '21 days',
    false,
    '{
      "questions": [
        {"id": "name", "label": "이름", "type": "text", "required": true},
        {"id": "studentNumber", "label": "학번", "type": "text", "required": true},
        {"id": "department", "label": "학과", "type": "text", "required": true},
        {"id": "motivation", "label": "지원 동기", "type": "textarea", "required": true}
      ]
    }'::jsonb,
    true
from clubs c
where c.name in ('인하 밴드', '인하 축구회')
  and not exists (
      select 1
      from recruitments r
      where r.club_id = c.id
        and r.title = c.name || ' 기간 모집'
  );

insert into events (
    club_id,
    title,
    description,
    event_date,
    location,
    image_url,
    published
)
select
    c.id,
    c.name || ' 공개 행사',
    c.name || '에서 준비한 공개 활동 및 교류 행사입니다.',
    current_date + interval '7 days',
    '인하대학교 학생회관',
    c.profile_img,
    true
from clubs c
where c.name in ('GDG on Campus Inha', '인하 밴드', '봉사단 하랑')
  and not exists (
      select 1
      from events e
      where e.club_id = c.id
        and e.title = c.name || ' 공개 행사'
  );

insert into club_activities (
    club_id,
    title,
    description,
    start_date,
    end_date,
    image_url
)
select
    c.id,
    '정기 활동 기록',
    c.name || '의 최근 정기 활동 기록입니다.',
    current_date - interval '14 days',
    current_date - interval '14 days',
    c.profile_img
from clubs c
where c.name in ('GDG on Campus Inha', '인하 밴드', '봉사단 하랑')
  and not exists (
      select 1
      from club_activities a
      where a.club_id = c.id
        and a.title = '정기 활동 기록'
  );

insert into club_notices (
    club_id,
    title,
    content,
    notice_date,
    badge,
    pinned
)
select
    c.id,
    '신입 부원 안내',
    c.name || ' 신입 부원 안내 공지입니다. 지원 전 모집 요강을 확인해주세요.',
    current_date,
    '필독',
    true
from clubs c
where c.name in ('GDG on Campus Inha', '인하 밴드', '봉사단 하랑')
  and not exists (
      select 1
      from club_notices n
      where n.club_id = c.id
        and n.title = '신입 부원 안내'
  );

insert into club_members (
    club_id,
    name,
    student_number,
    department,
    major,
    email,
    birth,
    phone,
    image,
    status
)
select
    c.id,
    '강민규',
    '12240001',
    '컴퓨터공학과',
    '컴퓨터공학과',
    'member1@example.com',
    null,
    '010-1234-5678',
    c.profile_img,
    'member'
from clubs c
where c.name = 'GDG on Campus Inha'
  and not exists (
      select 1 from club_members m
      where m.club_id = c.id
        and m.email = 'member1@example.com'
  );

insert into club_members (
    club_id,
    name,
    student_number,
    department,
    major,
    email,
    birth,
    phone,
    image,
    status
)
select
    c.id,
    '홍길동',
    '12240002',
    '컴퓨터공학과',
    '컴퓨터공학과',
    'applicant1@example.com',
    null,
    '010-0000-0001',
    '',
    'applicant'
from clubs c
where c.name = 'GDG on Campus Inha'
  and not exists (
      select 1 from club_members m
      where m.club_id = c.id
        and m.email = 'applicant1@example.com'
  );

-- 회장 권한 연결:
-- 1. 먼저 Google OAuth로 한 번 로그인해서 users 테이블에 본인 계정이 생성되게 한다.
-- 2. 아래 email 값을 실제 인하대 이메일로 바꾸고 실행한다.
--
-- insert into club_managers (user_id, club_id, role)
-- select u.id, c.id, 'PRESIDENT'
-- from users u
-- join clubs c on c.name = 'GDG on Campus Inha'
-- where u.email = 'YOUR_INHA_EMAIL@inha.edu'
-- on conflict (user_id, club_id) do update set role = excluded.role;

commit;
