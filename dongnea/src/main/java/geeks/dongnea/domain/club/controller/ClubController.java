package geeks.dongnea.domain.club.controller;

import geeks.dongnea.domain.club.dto.ClubDetailResponse;
import geeks.dongnea.domain.club.dto.ClubPageResponse;
import geeks.dongnea.domain.club.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "Club API", description = "동아리 및 관리자 관련 API")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    @Operation(summary = "전체 동아리 목록 조회", description = "로그인한 사용자가 동아리 목록을 페이지 단위로 조회합니다.")
    public ResponseEntity<ClubPageResponse> getClubs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(clubService.getClubs(pageRequest));
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "동아리 상세 조회", description = "동아리 소개, 자세한 활동, 활성 모집 정보를 조회합니다.")
    public ResponseEntity<ClubDetailResponse> getClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClub(clubId));
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
