package geeks.dongnea.domain.club.service;

import geeks.dongnea.domain.club.dto.RecruitmentCreateRequest;
import geeks.dongnea.domain.club.dto.RecruitmentSummaryResponse;
import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final ClubAuthorizationService clubAuthorizationService;

    @Transactional
    public RecruitmentSummaryResponse createRecruitment(Long clubId, RecruitmentCreateRequest request) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);
        validateCreateRequest(request);

        Recruitment recruitment = Recruitment.builder()
                .club(club)
                .title(request.getTitle())
                .summary(request.getSummary())
                .startDate(Boolean.TRUE.equals(request.getAlwaysOpen()) ? null : request.getStartDate())
                .endDate(Boolean.TRUE.equals(request.getAlwaysOpen()) ? null : request.getEndDate())
                .isAlwaysOpen(request.getAlwaysOpen())
                .formSchema(request.getFormSchema())
                .isActive(true)
                .build();

        return RecruitmentSummaryResponse.from(recruitmentRepository.save(recruitment), LocalDate.now());
    }

    @Transactional
    public void closeRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집 공고가 없습니다."));
        clubAuthorizationService.requireManagedClub(recruitment.getClub().getId());
        recruitment.close();
    }

    private void validateCreateRequest(RecruitmentCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("모집 공고 제목은 필수입니다.");
        }

        if (!Boolean.TRUE.equals(request.getAlwaysOpen())) {
            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new IllegalArgumentException("기간 모집은 시작일과 종료일이 필요합니다.");
            }

            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new IllegalArgumentException("모집 종료일은 시작일보다 빠를 수 없습니다.");
            }
        }
    }
}
