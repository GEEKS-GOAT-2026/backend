package geeks.dongnea.domain.club.service;

import geeks.dongnea.domain.application.repository.ApplicationRepository;
import geeks.dongnea.domain.club.dto.ClubActivityRequest;
import geeks.dongnea.domain.club.dto.ClubActivityResponse;
import geeks.dongnea.domain.club.dto.ClubDetailResponse;
import geeks.dongnea.domain.club.dto.ClubListResponse;
import geeks.dongnea.domain.club.dto.ClubMemberResponse;
import geeks.dongnea.domain.club.dto.ClubNoticeRequest;
import geeks.dongnea.domain.club.dto.ClubNoticeResponse;
import geeks.dongnea.domain.club.dto.ClubPageResponse;
import geeks.dongnea.domain.club.dto.ClubProfileUpdateRequest;
import geeks.dongnea.domain.club.dto.ManagedClubResponse;
import geeks.dongnea.domain.club.dto.ManagerTransferRequest;
import geeks.dongnea.domain.club.dto.RecruitmentSummaryResponse;
import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubActivity;
import geeks.dongnea.domain.club.entity.ClubMember;
import geeks.dongnea.domain.club.entity.ClubManager;
import geeks.dongnea.domain.club.entity.ClubNotice;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.ClubActivityRepository;
import geeks.dongnea.domain.club.repository.ClubManagerRepository;
import geeks.dongnea.domain.club.repository.ClubMemberRepository;
import geeks.dongnea.domain.club.repository.ClubNoticeRepository;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubActivityRepository clubActivityRepository;
    private final ClubNoticeRepository clubNoticeRepository;
    private final ClubManagerRepository clubManagerRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;
    private final ClubAuthorizationService clubAuthorizationService;

    public ClubPageResponse getClubs(Pageable pageable, String category, String keyword, Boolean hasActiveRecruitment) {
        LocalDate today = LocalDate.now();

        Page<ClubListResponse> clubs = clubRepository.findClubsForList(
                        normalize(category),
                        normalize(keyword),
                        hasActiveRecruitment,
                        today,
                        pageable)
                .map(club -> ClubListResponse.of(club, getOpenRecruitment(club, today)));

        return ClubPageResponse.from(clubs);
    }

    public ClubDetailResponse getClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리가 없습니다."));

        return toClubDetailResponse(club);
    }

    public List<ClubMemberResponse> getClubMembers(Long clubId, String status, String keyword) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);

        return clubMemberRepository.findMembers(
                        club,
                        normalize(status),
                        normalize(keyword)
                )
                .stream()
                .map(ClubMemberResponse::from)
                .toList();
    }

    public List<ClubListResponse> getJoinedClubs(User user) {
        LocalDate today = LocalDate.now();

        return clubMemberRepository.findByEmailAndStatus(user.getEmail(), "member")
                .stream()
                .map(ClubMember::getClub)
                .distinct()
                .map(club -> ClubListResponse.of(club, getOpenRecruitment(club, today)))
                .toList();
    }

    @Transactional
    public void leaveClub(User user, Long clubId) {
        Club club = getClubEntity(clubId);
        ClubMember member = clubMemberRepository.findByClubAndEmail(club, user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입된 동아리가 아닙니다."));

        if (!"member".equals(member.getStatus())) {
            throw new IllegalArgumentException("현재 가입 상태인 동아리만 탈퇴할 수 있습니다.");
        }

        removeManagerRoleIfPossible(club, user);
        clubMemberRepository.delete(member);
    }

    public List<ClubActivityResponse> getClubActivities(Long clubId) {
        Club club = getClubEntity(clubId);

        return clubActivityRepository.findByClubOrderByStartDateDescIdDesc(club)
                .stream()
                .map(ClubActivityResponse::from)
                .toList();
    }

    @Transactional
    public ClubActivityResponse createClubActivity(Long clubId, ClubActivityRequest request) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);
        validateActivityRequest(request.getTitle(), request.getStartDate());

        ClubActivity activity = ClubActivity.builder()
                .club(club)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .imageUrl(request.getImageUrl())
                .build();

        return ClubActivityResponse.from(clubActivityRepository.save(activity));
    }

    @Transactional
    public ClubActivityResponse updateClubActivity(Long clubId, Long activityId, ClubActivityRequest request) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubActivity activity = getClubActivityForUpdate(clubId, activityId);

        activity.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getImageUrl()
        );

        return ClubActivityResponse.from(activity);
    }

    @Transactional
    public void deleteClubActivity(Long clubId, Long activityId) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubActivity activity = getClubActivityForUpdate(clubId, activityId);
        clubActivityRepository.delete(activity);
    }

    public List<ClubNoticeResponse> getClubNotices(Long clubId) {
        Club club = getClubEntity(clubId);

        return clubNoticeRepository.findByClubOrderByPinnedDescNoticeDateDescIdDesc(club)
                .stream()
                .map(ClubNoticeResponse::from)
                .toList();
    }

    @Transactional
    public ClubDetailResponse updateClubProfile(Long clubId, ClubProfileUpdateRequest request) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);

        club.updateProfile(
                request.getDescription(),
                request.getActivityDescription(),
                request.getCategory(),
                request.getProfileImg(),
                request.getContact(),
                request.getInstagramUrl()
        );

        return toClubDetailResponse(club);
    }

    @Transactional
    public ClubNoticeResponse createClubNotice(Long clubId, ClubNoticeRequest request) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);
        validateNoticeRequest(request.getTitle(), request.getNoticeDate());

        ClubNotice notice = ClubNotice.builder()
                .club(club)
                .title(request.getTitle())
                .content(request.getContent())
                .noticeDate(request.getNoticeDate())
                .badge(request.getBadge())
                .pinned(request.getPinned())
                .imageUrl(request.getImageUrl())
                .build();

        return ClubNoticeResponse.from(clubNoticeRepository.save(notice));
    }

    @Transactional
    public ClubNoticeResponse updateClubNotice(Long clubId, Long noticeId, ClubNoticeRequest request) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubNotice notice = getClubNoticeForUpdate(clubId, noticeId);

        notice.update(
                request.getTitle(),
                request.getContent(),
                request.getNoticeDate(),
                request.getBadge(),
                request.getPinned(),
                request.getImageUrl()
        );

        return ClubNoticeResponse.from(notice);
    }

    @Transactional
    public void deleteClubNotice(Long clubId, Long noticeId) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubNotice notice = getClubNoticeForUpdate(clubId, noticeId);
        clubNoticeRepository.delete(notice);
    }

    @Transactional
    public ClubMemberResponse acceptMember(Long clubId, Long memberId) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubMember member = getClubMemberForUpdate(clubId, memberId);
        member.accept();
        syncLatestApplicationStatus(member, "ACCEPTED");
        return ClubMemberResponse.from(member);
    }

    @Transactional
    public void rejectMember(Long clubId, Long memberId) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubMember member = getClubMemberForUpdate(clubId, memberId);
        syncLatestApplicationStatus(member, "REJECTED");
        clubMemberRepository.delete(member);
    }

    @Transactional
    public void removeMember(Long clubId, Long memberId) {
        clubAuthorizationService.requireManagedClub(clubId);
        ClubMember member = getClubMemberForUpdate(clubId, memberId);

        userRepository.findByEmail(member.getEmail())
                .ifPresent(user -> removeManagerRoleIfPossible(member.getClub(), user));

        clubMemberRepository.delete(member);
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

    @Transactional
    public ManagedClubResponse transferPresident(Long clubId, ManagerTransferRequest request) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);
        String targetEmail = normalize(request.getTargetEmail());

        if (targetEmail == null) {
            throw new IllegalArgumentException("양도할 유저 이메일은 필수입니다.");
        }

        User targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("양도 대상 유저가 없습니다."));
        ClubMember targetMember = clubMemberRepository.findByClubAndEmail(club, targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("동아리 회원에게만 회장 권한을 양도할 수 있습니다."));

        if (!"member".equals(targetMember.getStatus())) {
            throw new IllegalArgumentException("가입 승인된 회원에게만 회장 권한을 양도할 수 있습니다.");
        }

        List<ClubManager> previousPresidents = clubManagerRepository.findByClubAndRole(club, "PRESIDENT");
        ClubManager targetManager = clubManagerRepository.findByUserAndClub(targetUser, club)
                .orElseGet(() -> ClubManager.builder()
                        .user(targetUser)
                        .club(club)
                        .role("PRESIDENT")
                        .build());

        targetManager.updateRole("PRESIDENT");
        previousPresidents.stream()
                .filter(manager -> !manager.getUser().getId().equals(targetUser.getId()))
                .forEach(clubManagerRepository::delete);

        return ManagedClubResponse.from(clubManagerRepository.save(targetManager));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private Recruitment getOpenRecruitment(Club club, LocalDate today) {
        return recruitmentRepository.findOpenRecruitmentsByClub(club, today)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private ClubDetailResponse toClubDetailResponse(Club club) {
        LocalDate today = LocalDate.now();

        List<RecruitmentSummaryResponse> recruitments = recruitmentRepository.findByClubAndIsActiveTrue(club)
                .stream()
                .map(recruitment -> RecruitmentSummaryResponse.from(recruitment, today))
                .toList();

        return ClubDetailResponse.of(club, recruitments);
    }

    private Club getClubEntity(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리가 없습니다."));
    }

    private ClubMember getClubMemberForUpdate(Long clubId, Long memberId) {
        Club club = getClubEntity(clubId);

        ClubMember member = clubMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 또는 신청자가 없습니다."));

        if (!member.getClub().getId().equals(club.getId())) {
            throw new IllegalArgumentException("해당 동아리의 회원 또는 신청자가 아닙니다.");
        }

        return member;
    }

    private void syncLatestApplicationStatus(ClubMember member, String applicationStatus) {
        userRepository.findByEmail(member.getEmail())
                .flatMap(user -> applicationRepository.findFirstByRecruitmentClubAndUserOrderByIdDesc(
                        member.getClub(),
                        user
                ))
                .ifPresent(application -> application.updateStatus(applicationStatus));
    }

    private void removeManagerRoleIfPossible(Club club, User user) {
        clubManagerRepository.findByUserAndClub(user, club)
                .ifPresent(manager -> {
                    if ("PRESIDENT".equals(manager.getRole())) {
                        throw new IllegalArgumentException("회장은 권한 양도 후 탈퇴 또는 삭제할 수 있습니다.");
                    }

                    clubManagerRepository.delete(manager);
                });
    }

    private ClubActivity getClubActivityForUpdate(Long clubId, Long activityId) {
        ClubActivity activity = clubActivityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 활동기록이 없습니다."));

        if (!activity.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 활동기록이 아닙니다.");
        }

        return activity;
    }

    private ClubNotice getClubNoticeForUpdate(Long clubId, Long noticeId) {
        ClubNotice notice = clubNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지가 없습니다."));

        if (!notice.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 공지가 아닙니다.");
        }

        return notice;
    }

    private void validateActivityRequest(String title, LocalDate startDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("활동기록 제목은 필수입니다.");
        }

        if (startDate == null) {
            throw new IllegalArgumentException("활동 시작일은 필수입니다.");
        }
    }

    private void validateNoticeRequest(String title, LocalDate noticeDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("공지 제목은 필수입니다.");
        }

        if (noticeDate == null) {
            throw new IllegalArgumentException("공지 날짜는 필수입니다.");
        }
    }
}
