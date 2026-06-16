package geeks.dongnea.domain.application.controller;

import geeks.dongnea.domain.application.dto.ApplicationResponse;
import geeks.dongnea.domain.application.dto.ApplicationStatusUpdateRequest;
import geeks.dongnea.domain.application.dto.ApplicationSubmitRequest;
import geeks.dongnea.domain.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Application API", description = "지원서 관련 API")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "지원서 제출", description = "로그인한 사용자가 모집 공고에 지원서를 제출합니다.")
    public ApplicationResponse submitApplication(@RequestBody ApplicationSubmitRequest request) {
        return applicationService.submitApplication(request);
    }

    @GetMapping("/me")
    @Operation(summary = "내 지원서 목록", description = "로그인한 사용자의 지원 내역을 조회합니다.")
    public List<ApplicationResponse> getMyApplications() {
        return applicationService.getMyApplications();
    }

    @PatchMapping("/{applicationId}/status")
    @Operation(summary = "지원서 상태 변경", description = "동아리 관리자가 지원서 상태를 변경합니다.")
    public ApplicationResponse updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateRequest request
    ) {
        return applicationService.updateApplicationStatus(applicationId, request);
    }
}
