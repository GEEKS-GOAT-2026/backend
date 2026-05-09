package geeks.dongnea.domain.club.config;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
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
    private final RecruitmentRepository recruitmentRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (clubRepository.count() > 0) {
            return;
        }

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
}
