package geeks.dongnea.domain.club.controller;

import geeks.dongnea.domain.club.dto.RecruitmentCreateRequest;
import geeks.dongnea.domain.club.dto.RecruitmentSummaryResponse;
import geeks.dongnea.domain.club.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitment API", description = "모집 공고 관련 API")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @PostMapping
    @Operation(summary = "모집 공고 생성", description = "동아리 관리자가 동적 질문 폼을 포함한 모집 공고를 생성합니다.")
    public ResponseEntity<RecruitmentSummaryResponse> createRecruitment(
            @RequestParam Long clubId,
            @RequestBody RecruitmentCreateRequest request
    ) {
        return ResponseEntity.ok(recruitmentService.createRecruitment(clubId, request));
    }

    @PatchMapping("/{recruitmentId}/close")
    @Operation(summary = "모집 공고 마감", description = "동아리 관리자가 모집 공고를 비활성화합니다.")
    public ResponseEntity<Void> closeRecruitment(@PathVariable Long recruitmentId) {
        recruitmentService.closeRecruitment(recruitmentId);
        return ResponseEntity.noContent().build();
    }
}
