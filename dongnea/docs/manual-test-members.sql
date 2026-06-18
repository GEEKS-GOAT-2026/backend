-- Dongne manual test data for member/manager flows.
-- 실행 위치: Supabase SQL Editor
-- 목적:
--   1. 회장 권한 양도 대상 회원 생성
--   2. 가입 승인 대상 신청자 생성
--   3. 탈퇴 화면 확인용 일반 회원 생성
--
-- 주의:
--   - 이 파일은 자동 seed가 아닙니다. 필요할 때 Supabase SQL Editor에서 직접 실행합니다.
--   - 서버 재시작으로 데이터가 초기화되지는 않습니다.
--   - club_members.student_number / club_members.department 컬럼이 있어야 합니다.
--   - 탈퇴 API는 "현재 로그인한 유저" 기준입니다. 실제 API 탈퇴 테스트는 로그인 가능한 계정으로 해야 합니다.

begin;

-- 회장 권한 양도 대상: GDG 일반 회원
insert into users (email, name, picture)
values ('transfer.gdg.member@inha.edu', '이서연', null)
on conflict (email) do update set
    name = excluded.name,
    picture = excluded.picture;

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
    '이서연',
    '12240011',
    '컴퓨터공학과',
    '컴퓨터공학과',
    'transfer.gdg.member@inha.edu',
    null,
    '010-1111-1111',
    coalesce(c.profile_img, ''),
    'member'
from clubs c
where c.name = 'GDG on Campus Inha'
  and not exists (
      select 1
      from club_members m
      where m.club_id = c.id
        and m.email = 'transfer.gdg.member@inha.edu'
  );

-- 가입 승인 테스트 대상: GDG 신청자
insert into users (email, name, picture)
values ('applicant.gdg.test@inha.edu', '한유진', null)
on conflict (email) do update set
    name = excluded.name,
    picture = excluded.picture;

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
    '한유진',
    '12250012',
    '정보통신공학과',
    '정보통신공학과',
    'applicant.gdg.test@inha.edu',
    null,
    '010-2222-2222',
    '',
    'applicant'
from clubs c
where c.name = 'GDG on Campus Inha'
  and not exists (
      select 1
      from club_members m
      where m.club_id = c.id
        and m.email = 'applicant.gdg.test@inha.edu'
  );

-- 탈퇴 화면 확인용 일반 회원.
-- 실제 DELETE /api/users/me/clubs/{clubId} 호출 테스트는 이 이메일로 로그인할 수 있어야 합니다.
insert into users (email, name, picture)
values ('leave.gdg.member@inha.edu', '최민준', null)
on conflict (email) do update set
    name = excluded.name,
    picture = excluded.picture;

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
    '최민준',
    '12230013',
    '소프트웨어융합공학과',
    '소프트웨어융합공학과',
    'leave.gdg.member@inha.edu',
    null,
    '010-3333-3333',
    coalesce(c.profile_img, ''),
    'member'
from clubs c
where c.name = 'GDG on Campus Inha'
  and not exists (
      select 1
      from club_members m
      where m.club_id = c.id
        and m.email = 'leave.gdg.member@inha.edu'
  );

-- 다른 동아리 회장 페이지도 확인할 수 있도록 인하 밴드 일반 회원/신청자 추가
insert into users (email, name, picture)
values
    ('band.member.test@inha.edu', '정다은', null),
    ('band.applicant.test@inha.edu', '윤태호', null)
on conflict (email) do update set
    name = excluded.name,
    picture = excluded.picture;

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
    '정다은',
    '12240014',
    '문화콘텐츠학과',
    '문화콘텐츠학과',
    'band.member.test@inha.edu',
    null,
    '010-4444-4444',
    coalesce(c.profile_img, ''),
    'member'
from clubs c
where c.name = '인하 밴드'
  and not exists (
      select 1
      from club_members m
      where m.club_id = c.id
        and m.email = 'band.member.test@inha.edu'
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
    '윤태호',
    '12250015',
    '경영학과',
    '경영학과',
    'band.applicant.test@inha.edu',
    null,
    '010-5555-5555',
    '',
    'applicant'
from clubs c
where c.name = '인하 밴드'
  and not exists (
      select 1
      from club_members m
      where m.club_id = c.id
        and m.email = 'band.applicant.test@inha.edu'
  );

commit;

-- 확인 쿼리
-- select c.name as club_name, m.name, m.student_number, m.department, m.email, m.status
-- from club_members m
-- join clubs c on c.id = m.club_id
-- where m.email like '%.test@inha.edu'
--    or m.email in ('transfer.gdg.member@inha.edu', 'leave.gdg.member@inha.edu')
-- order by c.name, m.status, m.name;
