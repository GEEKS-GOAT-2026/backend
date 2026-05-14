package geeks.dongnea.domain.club.service;

import geeks.dongnea.domain.club.dto.ClubDetailResponse;
import geeks.dongnea.domain.club.dto.ClubListResponse;
import geeks.dongnea.domain.club.dto.ClubPageResponse;
import geeks.dongnea.domain.club.dto.RecruitmentSummaryResponse;
import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubManager;
import geeks.dongnea.domain.club.repository.ClubManagerRepository;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubManagerRepository clubManagerRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;

    public ClubPageResponse getClubs(Pageable pageable, String category, String keyword, Boolean hasActiveRecruitment) {
        Page<ClubListResponse> clubs = clubRepository.findClubsForList(
                        normalize(category),
                        normalize(keyword),
                        hasActiveRecruitment,
                        pageable)
                .map(ClubListResponse::from);

        return ClubPageResponse.from(clubs);
    }

    public ClubDetailResponse getClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리가 없습니다."));

        List<RecruitmentSummaryResponse> recruitments = recruitmentRepository.findByClubAndIsActiveTrue(club)
                .stream()
                .map(RecruitmentSummaryResponse::from)
                .toList();

        return ClubDetailResponse.of(club, recruitments);
    }

    /**
     * 유저를 동아리 관리자로 추가
     */
    @Transactional
    public void addManager(Long userId, Long clubId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리가 없습니다."));

        ClubManager manager = ClubManager.builder()
                .user(user)
                .club(club)
                .role(role)
                .build();

        clubManagerRepository.save(manager);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
