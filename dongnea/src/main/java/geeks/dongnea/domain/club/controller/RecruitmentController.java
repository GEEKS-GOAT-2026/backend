package geeks.dongnea.domain.club.controller;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitment API", description = "모집 공고 관련 API")
public class RecruitmentController {

    private final RecruitmentRepository recruitmentRepository;
    private final ClubRepository clubRepository;

    @PostMapping
    @Operation(summary = "모집 공고 생성 (JSONB 폼 테스트)", description = "동적 질문 폼을 포함하여 모집 공고를 생성합니다.")
    public String createRecruitment(
            @RequestParam Long clubId,
            @RequestParam String title,
            @RequestBody Map<String, Object> formSchema) { // JSONB 데이터를 Map으로 받음

        Club club = clubRepository.findById(clubId).orElseThrow();

        Recruitment recruitment = Recruitment.builder()
                .club(club)
                .title(title)
                .formSchema(formSchema)
                .build();

        recruitmentRepository.save(recruitment);
        return "모집 공고가 성공적으로 생성되었습니다!";
    }
}