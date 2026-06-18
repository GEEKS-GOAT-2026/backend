-- Club member info migration.
-- 실행 위치: Supabase SQL Editor
-- 목적:
--   club_members를 이름, 학번(8자리), 학과, 전화번호, 이메일 기준으로 맞춘다.
--
-- 실행 시점:
--   1. 새 백엔드 코드를 배포해서 JPA ddl-auto=update가 컬럼을 만든 뒤 실행하거나
--   2. 아래 alter table까지 포함해서 직접 실행한다.

begin;

alter table club_members
    add column if not exists student_number varchar(8);

alter table club_members
    add column if not exists department varchar(255);

-- 기존 major 값이 "컴퓨터공학과 24학번"처럼 되어 있으면 학과만 department에 넣는다.
update club_members
set department = coalesce(
        department,
        nullif(trim(regexp_replace(coalesce(major, ''), '\s*[0-9]{2}학번\s*$', '')), '')
    );

-- 테스트용 기존 계정에 8자리 학번을 부여한다.
update club_members
set student_number = case email
    when 'pshpite1004@inha.edu' then '12240001'
    when 'hhjjpp03@inha.edu' then '12240002'
    when '26_ai_jwk@inha.edu' then '12240003'
    when 'transfer.gdg.member@inha.edu' then '12240011'
    when 'applicant.gdg.test@inha.edu' then '12250012'
    when 'leave.gdg.member@inha.edu' then '12230013'
    when 'band.member.test@inha.edu' then '12240014'
    when 'band.applicant.test@inha.edu' then '12250015'
    else student_number
end
where email in (
    'pshpite1004@inha.edu',
    'hhjjpp03@inha.edu',
    '26_ai_jwk@inha.edu',
    'transfer.gdg.member@inha.edu',
    'applicant.gdg.test@inha.edu',
    'leave.gdg.member@inha.edu',
    'band.member.test@inha.edu',
    'band.applicant.test@inha.edu'
);

-- 생년월일은 더 이상 사용하지 않는다.
update club_members
set birth = null;

commit;

-- 확인 쿼리
-- select name, student_number, department, phone, email, status
-- from club_members
-- order by club_id, status, name;
