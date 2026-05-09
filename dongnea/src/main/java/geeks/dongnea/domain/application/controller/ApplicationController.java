package geeks.dongnea.domain.application.controller;

import geeks.dongnea.domain.application.entity.Application;
import geeks.dongnea.domain.application.repository.ApplicationRepository;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.global.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Application API", description = "지원서 관련 API")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CurrentUserService currentUserService;

    @PostMapping
    @Operation(summary = "지원서 제출 (JSONB 답변 테스트)", description = "학생이 작성한 지원서 답변을 제출합니다.")
    public String submitApplication(
            @RequestParam Long recruitmentId,
            @RequestBody Map<String, Object> answers) { // JSONB 데이터를 Map으로 받음

        User user = currentUserService.getCurrentUser();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow();

        Application application = Application.builder()
                .user(user)
                .recruitment(recruitment)
                .answers(answers)
                .status("PENDING")
                .build();

        applicationRepository.save(application);
        return "지원서가 성공적으로 제출되었습니다!";
    }
}
