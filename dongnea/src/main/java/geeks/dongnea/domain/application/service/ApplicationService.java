package geeks.dongnea.domain.application.service;

import geeks.dongnea.domain.application.dto.ApplicationResponse;
import geeks.dongnea.domain.application.dto.ApplicationStatusUpdateRequest;
import geeks.dongnea.domain.application.dto.ApplicationSubmitRequest;
import geeks.dongnea.domain.application.entity.Application;
import geeks.dongnea.domain.application.repository.ApplicationRepository;
import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubMember;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.ClubMemberRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import geeks.dongnea.domain.club.service.ClubAuthorizationService;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.global.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private static final Set<String> APPLICATION_STATUSES = Set.of("PENDING", "ACCEPTED", "REJECTED");

    private final ApplicationRepository applicationRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CurrentUserService currentUserService;
    private final ClubAuthorizationService clubAuthorizationService;

    @Transactional
    public ApplicationResponse submitApplication(ApplicationSubmitRequest request) {
        User user = currentUserService.getCurrentUser();
        Recruitment recruitment = recruitmentRepository.findById(request.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));

        if (!recruitment.isOpenOn(LocalDate.now())) {
            throw new IllegalArgumentException("현재 지원 가능한 모집 공고가 아닙니다.");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("지원서 답변을 입력해야 합니다.");
        }
        if (applicationRepository.findByRecruitmentAndUser(recruitment, user).isPresent()) {
            throw new IllegalArgumentException("이미 제출한 지원서가 있습니다.");
        }

        Application application = Application.builder()
                .user(user)
                .recruitment(recruitment)
                .answers(request.getAnswers())
                .status("PENDING")
                .build();

        Application savedApplication = applicationRepository.save(application);
        createApplicantMemberIfAbsent(recruitment, user, request.getAnswers());

        return ApplicationResponse.from(savedApplication);
    }

    public List<ApplicationResponse> getMyApplications() {
        User user = currentUserService.getCurrentUser();
        return applicationRepository.findByUser(user).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    public List<ApplicationResponse> getClubApplications(Long clubId) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);
        return applicationRepository.findByRecruitmentClubOrderByIdDesc(club).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request) {
        String status = normalizeStatus(request.getStatus());
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        clubAuthorizationService.requireManagedClub(application.getRecruitment().getClub().getId());
        application.updateStatus(status);
        return ApplicationResponse.from(application);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("지원서 상태를 입력해야 합니다.");
        }

        String normalized = status.trim().toUpperCase();
        if (!APPLICATION_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("지원서 상태는 PENDING, ACCEPTED, REJECTED 중 하나여야 합니다.");
        }
        return normalized;
    }

    private void createApplicantMemberIfAbsent(
            Recruitment recruitment,
            User user,
            Map<String, Object> answers
    ) {
        if (clubMemberRepository.findByClubAndEmail(recruitment.getClub(), user.getEmail()).isPresent()) {
            return;
        }

        clubMemberRepository.save(ClubMember.builder()
                .club(recruitment.getClub())
                .name(valueOrDefault(answers, "name", user.getName()))
                .studentNumber(valueOrDefault(answers, "studentNumber", valueOrDefault(answers, "student_number", null)))
                .department(valueOrDefault(answers, "department", valueOrDefault(answers, "major", null)))
                .major(valueOrDefault(answers, "department", valueOrDefault(answers, "major", null)))
                .email(user.getEmail())
                .phone(valueOrDefault(answers, "phone", null))
                .image(valueOrDefault(answers, "image", user.getPicture()))
                .status("applicant")
                .build());
    }

    private String valueOrDefault(Map<String, Object> answers, String key, String defaultValue) {
        Object value = answers.get(key);
        if (value == null) {
            return defaultValue;
        }

        String text = String.valueOf(value).trim();
        return text.isBlank() ? defaultValue : text;
    }
}
