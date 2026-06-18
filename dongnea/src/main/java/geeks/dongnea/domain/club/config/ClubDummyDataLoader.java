package geeks.dongnea.domain.club.config;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubActivity;
import geeks.dongnea.domain.club.entity.ClubMember;
import geeks.dongnea.domain.club.entity.ClubNotice;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.ClubActivityRepository;
import geeks.dongnea.domain.club.repository.ClubMemberRepository;
import geeks.dongnea.domain.club.repository.ClubNoticeRepository;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import geeks.dongnea.domain.event.entity.Event;
import geeks.dongnea.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@Profile("local")
@RequiredArgsConstructor
public class ClubDummyDataLoader implements CommandLineRunner {

    private final ClubRepository clubRepository;
    private final ClubActivityRepository clubActivityRepository;
    private final ClubNoticeRepository clubNoticeRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (clubRepository.count() == 0) {
            List<Club> clubs = List.of(
                    club("GDG on Campus Inha", "개발과 기술 공유를 중심으로 활동하는 학생 커뮤니티입니다.", "정기 세미나, 스터디, 해커톤, 프로젝트 발표를 진행합니다.", "IT/개발"),
                    club("인하 밴드", "합주와 공연을 즐기는 밴드 동아리입니다.", "보컬, 기타, 베이스, 드럼, 키보드 파트가 함께 정기 합주와 교내 공연을 준비합니다.", "공연"),
                    club("인하 축구회", "축구를 좋아하는 학생들이 모여 운동하는 체육 동아리입니다.", "주 1회 정기 운동과 교내외 친선 경기를 진행합니다.", "체육"),
                    club("사진연구회", "사진 촬영과 편집을 함께 배우는 동아리입니다.", "출사, 사진전, 보정 스터디를 통해 결과물을 공유합니다.", "예술"),
                    club("창업동아리 루트", "아이디어를 서비스로 만드는 창업 동아리입니다.", "아이디어 검증, MVP 제작, 피칭 연습, 공모전 참가를 진행합니다.", "창업"),
                    club("봉사단 하랑", "지역사회와 함께하는 봉사 동아리입니다.", "교육 봉사, 환경 캠페인, 지역 행사 지원 활동을 합니다.", "봉사"),
                    club("토론동아리 다온", "사회 이슈를 주제로 토론하는 학술 동아리입니다.", "정기 토론, 스피치 연습, 찬반 토론 대회를 운영합니다.", "학술"),
                    club("러닝크루 인하런", "함께 뛰며 건강한 루틴을 만드는 러닝 동아리입니다.", "캠퍼스 러닝, 기록 측정, 마라톤 참가를 함께 준비합니다.", "체육"),
                    club("영화감상회 프레임", "영화를 보고 이야기하는 문화 동아리입니다.", "정기 상영회와 영화 리뷰 모임을 진행합니다.", "문화"),
                    club("댄스동아리 무브", "다양한 장르의 춤을 연습하고 공연하는 동아리입니다.", "K-POP, 힙합, 코레오그래피 연습과 축제 공연을 준비합니다.", "공연"),
                    club("디자인랩", "UX/UI와 그래픽 디자인을 함께 배우는 동아리입니다.", "디자인 툴 스터디, 포트폴리오 리뷰, 협업 프로젝트를 진행합니다.", "디자인"),
                    club("인하 농구회", "농구를 즐기는 학생들의 체육 동아리입니다.", "정기 운동과 3대3, 5대5 게임을 운영합니다.", "체육"),
                    club("문예창작회", "글쓰기를 좋아하는 학생들의 창작 동아리입니다.", "시, 소설, 에세이 합평과 소규모 작품집 제작을 합니다.", "문화"),
                    club("보드게임 동아리", "보드게임을 통해 친목을 다지는 동아리입니다.", "전략 게임, 파티 게임, 자체 리그를 운영합니다.", "취미"),
                    club("AI 스터디", "인공지능 기초와 프로젝트를 공부하는 동아리입니다.", "논문 읽기, 모델 구현, Kaggle 스터디를 진행합니다.", "IT/개발"),
                    club("클래식 기타회", "클래식 기타 연주를 배우고 공연하는 동아리입니다.", "기초 레슨, 합주, 정기 연주회를 준비합니다.", "공연"),
                    club("등산동아리 산길", "주말 산행과 아웃도어 활동을 즐기는 동아리입니다.", "근교 산행, 트레킹, 안전 교육을 진행합니다.", "체육"),
                    club("마케팅 학회", "브랜드와 소비자 경험을 공부하는 학술 동아리입니다.", "케이스 스터디, 공모전, 캠페인 기획을 진행합니다.", "학술"),
                    club("외국어 교류회", "언어 교환과 문화 교류를 하는 동아리입니다.", "영어 회화, 일본어/중국어 스터디, 문화 교류 모임을 운영합니다.", "어학"),
                    club("게임제작회", "게임을 직접 만들고 플레이테스트하는 동아리입니다.", "Unity 스터디, 기획 회의, 팀 프로젝트를 진행합니다.", "IT/개발"),
                    club("요리동아리 한끼", "함께 요리하고 레시피를 공유하는 동아리입니다.", "정기 요리 모임, 레시피 연구, 소규모 팝업 활동을 합니다.", "취미"),
                    club("캠퍼스 기자단", "학교 소식과 학생 이야기를 기록하는 동아리입니다.", "취재, 기사 작성, 카드뉴스 제작을 진행합니다.", "미디어"),
                    club("환경동아리 그린", "지속가능한 캠퍼스를 고민하는 동아리입니다.", "분리배출 캠페인, 플로깅, 환경 세미나를 진행합니다.", "봉사"),
                    club("재즈연구회", "재즈 음악을 감상하고 연주하는 동아리입니다.", "스탠다드 곡 스터디, 즉흥 연주, 소규모 공연을 진행합니다.", "공연"),
                    club("독서모임 페이지", "책을 읽고 생각을 나누는 동아리입니다.", "월별 선정 도서 토론과 독후감 공유를 진행합니다.", "문화"),
                    club("금융투자학회", "금융 시장과 투자 전략을 공부하는 학회입니다.", "기업 분석, 경제 뉴스 리뷰, 모의투자 대회를 운영합니다.", "학술"),
                    club("로봇제작회", "하드웨어와 소프트웨어를 결합해 로봇을 만드는 동아리입니다.", "아두이노, 임베디드, 센서 제어 프로젝트를 진행합니다.", "공학"),
                    club("캘리그래피 모임", "손글씨와 레터링을 배우는 예술 동아리입니다.", "기초 획 연습, 작품 제작, 전시 활동을 합니다.", "예술"),
                    club("인하 테니스회", "테니스를 배우고 경기하는 체육 동아리입니다.", "초보 레슨, 정기 랠리, 친선전을 진행합니다.", "체육"),
                    club("서비스기획회", "사용자 문제를 정의하고 서비스를 설계하는 동아리입니다.", "리서치, 와이어프레임, 정책 설계, 프로토타입 리뷰를 진행합니다.", "기획")
            );

            clubRepository.saveAll(clubs);
            clubs.forEach(this::saveRecruitment);
        }

        saveAdditionalClubs();

        if (eventRepository.count() == 0) {
            saveEvents(clubRepository.findAll());
        }

        clubRepository.findAll()
                .stream()
                .findFirst()
                .ifPresent(club -> {
                    saveClubMembers(club);
                    saveClubActivities(club);
                    saveClubNotices(club);
                });
    }

    private Club club(String name, String description, String activityDescription, String category) {
        String encodedName = name.replace(" ", "-").toLowerCase();

        return Club.builder()
                .name(name)
                .description(description)
                .activityDescription(activityDescription)
                .category(category)
                .profileImg("https://placehold.co/400x400?text=" + encodedName)
                .contact("dongne@example.com")
                .instagramUrl("https://instagram.com/dongne")
                .build();
    }

    private void saveRecruitment(Club club) {
        Recruitment recruitment = Recruitment.builder()
                .club(club)
                .title(club.getName() + " 신입 부원 모집")
                .summary("함께 활동할 신입 부원을 모집합니다.")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(21))
                .isAlwaysOpen(false)
                .formSchema(Map.of(
                        "questions", List.of(
                                Map.of("id", "motivation", "label", "지원 동기", "type", "textarea"),
                                Map.of("id", "experience", "label", "관련 경험", "type", "textarea")
                        )
                ))
                .build();

        recruitmentRepository.save(recruitment);
    }

    private void saveAdditionalClubs() {
        List<ClubSeed> seeds = List.of(
                seed("인하 코딩클럽", "알고리즘과 웹 개발을 함께 공부하는 동아리입니다.", "코딩 테스트 스터디와 서비스 개발 프로젝트를 진행합니다.", "IT/개발"),
                seed("모바일앱 연구회", "iOS와 Android 앱을 직접 만들어보는 동아리입니다.", "모바일 UI 구현, 앱 배포, 사용자 테스트를 경험합니다.", "IT/개발"),
                seed("데이터사이언스 학회", "데이터 분석과 머신러닝을 실습하는 학술 모임입니다.", "Python 분석, 시각화, 모델링 프로젝트를 진행합니다.", "학술"),
                seed("웹툰창작부", "스토리와 그림으로 웹툰을 제작하는 창작 동아리입니다.", "콘티 제작, 캐릭터 디자인, 작품 피드백을 나눕니다.", "예술"),
                seed("스트릿댄스 크루", "스트릿 장르 중심으로 퍼포먼스를 준비합니다.", "힙합, 락킹, 왁킹 기본기와 무대 연습을 진행합니다.", "공연"),
                seed("캠퍼스 오케스트라", "클래식 합주와 정기 연주회를 준비하는 동아리입니다.", "파트 연습과 전체 합주를 통해 공연을 준비합니다.", "공연"),
                seed("배드민턴 모임", "초보자도 함께 운동하는 배드민턴 동아리입니다.", "정기 랠리, 복식 경기, 자세 교정을 진행합니다.", "체육"),
                seed("수영동아리 블루", "수영을 배우고 기록을 관리하는 체육 동아리입니다.", "기초 영법, 자유 수영, 교내 수영장 활동을 합니다.", "체육"),
                seed("캠퍼스 산책회", "가볍게 걷고 기록하는 생활 운동 모임입니다.", "야간 산책, 캠퍼스 코스 발굴, 건강 루틴을 공유합니다.", "체육"),
                seed("사회문제 연구회", "지역과 사회 문제를 조사하는 학술 동아리입니다.", "자료 조사, 인터뷰, 정책 제안서를 작성합니다.", "학술"),
                seed("심리학 독서회", "심리학 도서를 읽고 토론하는 동아리입니다.", "월별 도서 선정, 발제, 토론을 진행합니다.", "학술"),
                seed("교육봉사 다리", "청소년 학습 멘토링을 진행하는 봉사 동아리입니다.", "주말 멘토링과 학습 자료 제작을 담당합니다.", "봉사"),
                seed("동물보호 서포터즈", "유기동물 보호 캠페인을 기획하는 봉사 동아리입니다.", "캠페인, 카드뉴스, 보호소 연계 활동을 합니다.", "봉사"),
                seed("업사이클링 공방", "버려지는 재료를 활용해 작품을 만드는 모임입니다.", "제작 워크숍과 전시, 환경 캠페인을 진행합니다.", "봉사"),
                seed("커피연구회", "커피 추출과 카페 문화를 배우는 취미 동아리입니다.", "핸드드립, 원두 비교, 카페 탐방을 합니다.", "취미"),
                seed("여행기록회", "여행 코스와 사진 기록을 공유하는 동아리입니다.", "국내 여행 계획, 기록집 제작, 후기 발표를 합니다.", "문화"),
                seed("연극동아리 무대", "연극 제작과 공연을 함께하는 동아리입니다.", "연기, 연출, 무대 소품 제작을 경험합니다.", "공연"),
                seed("작곡스터디", "작곡과 사운드 디자인을 공부하는 음악 동아리입니다.", "DAW 사용법, 편곡, 합작 프로젝트를 진행합니다.", "예술"),
                seed("인하 e스포츠", "게임 전략과 팀 플레이를 연구하는 동아리입니다.", "정기 스크림, 전략 분석, 교내 대회를 운영합니다.", "취미"),
                seed("드론항공회", "드론 조종과 항공 촬영을 배우는 공학 동아리입니다.", "비행 연습, 안전 교육, 촬영 프로젝트를 진행합니다.", "공학"),
                seed("3D프린팅 랩", "모델링과 출력 과정을 배우는 제작 동아리입니다.", "CAD 기초, 출력 실습, 제품 프로토타입을 만듭니다.", "공학"),
                seed("창작사진실", "인물과 풍경 사진을 깊게 다루는 사진 동아리입니다.", "출사, 조명 실습, 포트폴리오 리뷰를 합니다.", "예술"),
                seed("시사뉴스 편집부", "뉴스를 정리하고 콘텐츠로 만드는 미디어 동아리입니다.", "기사 큐레이션, 카드뉴스 제작, 인터뷰를 진행합니다.", "미디어"),
                seed("영상제작소", "촬영과 편집을 배우는 영상 동아리입니다.", "기획, 촬영, 편집, 상영회를 운영합니다.", "미디어"),
                seed("한국사 탐방회", "역사 현장을 방문하고 기록하는 문화 동아리입니다.", "답사, 자료 조사, 발표회를 진행합니다.", "문화"),
                seed("외식창업 연구회", "푸드 비즈니스와 외식 트렌드를 공부합니다.", "시장 조사, 메뉴 기획, 창업 사례 분석을 합니다.", "창업"),
                seed("브랜딩 스튜디오", "브랜드 전략과 디자인을 함께 공부합니다.", "브랜드 리서치, 로고 실습, 캠페인 기획을 합니다.", "디자인"),
                seed("서비스 QA 모임", "서비스 품질과 테스트를 배우는 IT 동아리입니다.", "테스트 케이스 작성, 버그 리포트, QA 프로세스를 연습합니다.", "IT/개발"),
                seed("인하 탁구회", "탁구를 즐기는 학생들의 체육 동아리입니다.", "초보 레슨, 랠리 연습, 친선전을 진행합니다.", "체육"),
                seed("문제해결 워크숍", "논리적 사고와 문제 해결을 훈련하는 모임입니다.", "케이스 풀이, 팀 토론, 발표를 진행합니다.", "학술")
        );

        for (int index = 0; index < seeds.size(); index++) {
            ClubSeed seed = seeds.get(index);

            if (clubRepository.existsByName(seed.name())) {
                continue;
            }

            Club club = club(seed.name(), seed.description(), seed.activityDescription(), seed.category());
            clubRepository.save(club);
            saveRecruitment(club, index);
        }
    }

    private ClubSeed seed(String name, String description, String activityDescription, String category) {
        return new ClubSeed(name, description, activityDescription, category);
    }

    private void saveRecruitment(Club club, int index) {
        int recruitmentType = index % 3;
        boolean alwaysOpen = recruitmentType == 0;
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (recruitmentType == 1) {
            startDate = LocalDate.now().minusDays(3);
            endDate = LocalDate.now().plusDays(18);
        }

        if (recruitmentType == 2) {
            startDate = LocalDate.now().minusDays(45);
            endDate = LocalDate.now().minusDays(7);
        }

        Recruitment recruitment = Recruitment.builder()
                .club(club)
                .title(club.getName() + " 신입 부원 모집")
                .summary(alwaysOpen ? "상시로 함께 활동할 부원을 모집합니다." : "정해진 기간 동안 신입 부원을 모집합니다.")
                .startDate(startDate)
                .endDate(endDate)
                .isAlwaysOpen(alwaysOpen)
                .isActive(true)
                .formSchema(Map.of(
                        "questions", List.of(
                                Map.of("id", "motivation", "label", "지원 동기", "type", "textarea"),
                                Map.of("id", "availability", "label", "활동 가능 시간", "type", "textarea")
                        )
                ))
                .build();

        recruitmentRepository.save(recruitment);
    }

    private void saveEvents(List<Club> clubs) {
        List<Club> eventClubs = clubs.stream()
                .limit(8)
                .toList();

        for (int index = 0; index < eventClubs.size(); index++) {
            Club club = eventClubs.get(index);

            Event event = Event.builder()
                    .club(club)
                    .title(club.getName() + " 정기 활동")
                    .description(club.getName() + "에서 준비한 공개 활동 및 교류 행사입니다.")
                    .eventDate(LocalDate.now().plusDays(3L + index * 4L))
                    .location("인하대학교 학생회관")
                    .imageUrl(club.getProfileImg())
                    .published(true)
                    .build();

            eventRepository.save(event);
        }
    }

    private void saveClubMembers(Club club) {
        if (clubMemberRepository.countByClub(club) > 0) {
            return;
        }

        List<ClubMember> members = List.of(
                member(club, "강민규", "12240001", "컴퓨터공학과", "asd@gmail.com", "010-1234-5678", club.getProfileImg(), "member"),
                member(club, "홍길동", "12240002", "컴퓨터공학과", "asdasd@gmail.com", "010-1234-5678", "", "applicant"),
                member(club, "아이유", "12230003", "컴퓨터공학과", "aaa@gmail.com", "010-1234-5678", "", "applicant"),
                member(club, "이찬혁", "12250004", "컴퓨터공학과", "bbb@gmail.com", "010-1234-5678", "", "member")
        );

        clubMemberRepository.saveAll(members);
    }

    private ClubMember member(
            Club club,
            String name,
            String studentNumber,
            String department,
            String email,
            String phone,
            String image,
            String status
    ) {
        return ClubMember.builder()
                .club(club)
                .name(name)
                .studentNumber(studentNumber)
                .department(department)
                .major(department)
                .email(email)
                .phone(phone)
                .image(image)
                .status(status)
                .build();
    }

    private void saveClubActivities(Club club) {
        if (clubActivityRepository.countByClub(club) > 0) {
            return;
        }

        List<ClubActivity> activities = List.of(
                ClubActivity.builder()
                        .club(club)
                        .title("정기 세미나")
                        .description("신입 부원을 위한 기술 세미나와 네트워킹을 진행했습니다.")
                        .startDate(LocalDate.now().minusDays(21))
                        .endDate(LocalDate.now().minusDays(21))
                        .imageUrl(club.getProfileImg())
                        .build(),
                ClubActivity.builder()
                        .club(club)
                        .title("프로젝트 발표회")
                        .description("팀별 프로젝트 결과물을 공유하고 피드백을 나눴습니다.")
                        .startDate(LocalDate.now().minusDays(7))
                        .endDate(LocalDate.now().minusDays(7))
                        .imageUrl(club.getProfileImg())
                        .build()
        );

        clubActivityRepository.saveAll(activities);
    }

    private void saveClubNotices(Club club) {
        if (clubNoticeRepository.countByClub(club) > 0) {
            return;
        }

        List<ClubNotice> notices = List.of(
                ClubNotice.builder()
                        .club(club)
                        .title("신입 부원 OT 안내")
                        .content("이번 학기 신입 부원 오리엔테이션 일정과 장소를 안내합니다.")
                        .noticeDate(LocalDate.now().plusDays(2))
                        .badge("필독")
                        .pinned(true)
                        .build(),
                ClubNotice.builder()
                        .club(club)
                        .title("정기 활동 일정")
                        .content("정기 활동은 매주 수요일 18시에 진행됩니다.")
                        .noticeDate(LocalDate.now())
                        .badge("일정")
                        .pinned(false)
                        .build()
        );

        clubNoticeRepository.saveAll(notices);
    }

    private record ClubSeed(String name, String description, String activityDescription, String category) {
    }
}
