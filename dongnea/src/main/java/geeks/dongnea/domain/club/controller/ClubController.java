package geeks.dongnea.domain.club.controller;

import geeks.dongnea.domain.application.dto.ApplicationResponse;
import geeks.dongnea.domain.application.service.ApplicationService;
import geeks.dongnea.domain.club.dto.ClubActivityRequest;
import geeks.dongnea.domain.club.dto.ClubActivityResponse;
import geeks.dongnea.domain.club.dto.ClubDetailResponse;
import geeks.dongnea.domain.club.dto.ClubMemberResponse;
import geeks.dongnea.domain.club.dto.ClubNoticeRequest;
import geeks.dongnea.domain.club.dto.ClubNoticeResponse;
import geeks.dongnea.domain.club.dto.ClubPageResponse;
import geeks.dongnea.domain.club.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "Club API", description = "동아리 및 관리자 관련 API")
public class ClubController {

    private final ClubService clubService;
    private final ApplicationService applicationService;

    @GetMapping
    @Operation(summary = "전체 동아리 목록 조회", description = "로그인한 사용자가 동아리 목록을 페이지 단위로 조회하고, 카테고리/키워드/활성 모집 여부로 필터링합니다.")
    public ResponseEntity<ClubPageResponse> getClubs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean hasActiveRecruitment
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(clubService.getClubs(pageRequest, category, keyword, hasActiveRecruitment));
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "동아리 상세 조회", description = "동아리 소개, 자세한 활동, 활성 모집 정보를 조회합니다.")
    public ResponseEntity<ClubDetailResponse> getClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClub(clubId));
    }

    @GetMapping("/{clubId}/members")
    @Operation(summary = "동아리 회원/신청자 목록 조회", description = "회장 페이지에서 사용할 회원 또는 신청자 목록을 조회합니다.")
    public ResponseEntity<List<ClubMemberResponse>> getClubMembers(
            @PathVariable Long clubId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(clubService.getClubMembers(clubId, status, keyword));
    }

    @GetMapping("/{clubId}/applications")
    @Operation(summary = "동아리 지원서 목록 조회", description = "동아리 관리자가 해당 동아리의 지원서 목록을 조회합니다.")
    public ResponseEntity<List<ApplicationResponse>> getClubApplications(@PathVariable Long clubId) {
        return ResponseEntity.ok(applicationService.getClubApplications(clubId));
    }

    @GetMapping("/{clubId}/activities")
    @Operation(summary = "동아리 활동기록 조회", description = "동아리 상세 화면에서 사용할 활동기록 목록을 조회합니다.")
    public ResponseEntity<List<ClubActivityResponse>> getClubActivities(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClubActivities(clubId));
    }

    @PostMapping("/{clubId}/activities")
    @Operation(summary = "동아리 활동기록 작성", description = "동아리 관리자가 활동기록을 작성합니다.")
    public ResponseEntity<ClubActivityResponse> createClubActivity(
            @PathVariable Long clubId,
            @RequestBody ClubActivityRequest request
    ) {
        return ResponseEntity.ok(clubService.createClubActivity(clubId, request));
    }

    @PatchMapping("/{clubId}/activities/{activityId}")
    @Operation(summary = "동아리 활동기록 수정", description = "동아리 관리자가 활동기록을 수정합니다.")
    public ResponseEntity<ClubActivityResponse> updateClubActivity(
            @PathVariable Long clubId,
            @PathVariable Long activityId,
            @RequestBody ClubActivityRequest request
    ) {
        return ResponseEntity.ok(clubService.updateClubActivity(clubId, activityId, request));
    }

    @DeleteMapping("/{clubId}/activities/{activityId}")
    @Operation(summary = "동아리 활동기록 삭제", description = "동아리 관리자가 활동기록을 삭제합니다.")
    public ResponseEntity<Void> deleteClubActivity(
            @PathVariable Long clubId,
            @PathVariable Long activityId
    ) {
        clubService.deleteClubActivity(clubId, activityId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clubId}/notices")
    @Operation(summary = "동아리 공지 조회", description = "동아리 상세 화면에서 사용할 공지 목록을 조회합니다.")
    public ResponseEntity<List<ClubNoticeResponse>> getClubNotices(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClubNotices(clubId));
    }

    @PostMapping("/{clubId}/notices")
    @Operation(summary = "동아리 공지 작성", description = "동아리 관리자가 공지를 작성합니다.")
    public ResponseEntity<ClubNoticeResponse> createClubNotice(
            @PathVariable Long clubId,
            @RequestBody ClubNoticeRequest request
    ) {
        return ResponseEntity.ok(clubService.createClubNotice(clubId, request));
    }

    @PatchMapping("/{clubId}/notices/{noticeId}")
    @Operation(summary = "동아리 공지 수정", description = "동아리 관리자가 공지를 수정합니다.")
    public ResponseEntity<ClubNoticeResponse> updateClubNotice(
            @PathVariable Long clubId,
            @PathVariable Long noticeId,
            @RequestBody ClubNoticeRequest request
    ) {
        return ResponseEntity.ok(clubService.updateClubNotice(clubId, noticeId, request));
    }

    @DeleteMapping("/{clubId}/notices/{noticeId}")
    @Operation(summary = "동아리 공지 삭제", description = "동아리 관리자가 공지를 삭제합니다.")
    public ResponseEntity<Void> deleteClubNotice(
            @PathVariable Long clubId,
            @PathVariable Long noticeId
    ) {
        clubService.deleteClubNotice(clubId, noticeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{clubId}/members/{memberId}/accept")
    @Operation(summary = "동아리 신청자 수락", description = "신청자 상태를 재원(member)으로 변경합니다.")
    public ResponseEntity<ClubMemberResponse> acceptMember(
            @PathVariable Long clubId,
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(clubService.acceptMember(clubId, memberId));
    }

    @DeleteMapping("/{clubId}/members/{memberId}")
    @Operation(summary = "동아리 신청자 거절", description = "신청자 상태를 rejected로 변경합니다.")
    public ResponseEntity<Void> rejectMember(
            @PathVariable Long clubId,
            @PathVariable Long memberId
    ) {
        clubService.rejectMember(clubId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{clubId}/managers")
    @Operation(summary = "동아리 운영진 추가", description = "특정 유저를 동아리의 관리자(PRESIDENT 등)로 임명합니다.")
    public String addManager(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @RequestParam String role) {

        clubService.addManager(userId, clubId, role);
        return "유저(ID: " + userId + ")가 동아리(ID: " + clubId + ")의 " + role + "로 임명되었습니다!";
    }
}
