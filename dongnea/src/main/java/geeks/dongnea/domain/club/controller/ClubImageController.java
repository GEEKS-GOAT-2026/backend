package geeks.dongnea.domain.club.controller;

import geeks.dongnea.domain.club.dto.ClubImageUploadResponse;
import geeks.dongnea.domain.club.service.ClubImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "Club Image API", description = "동아리 글쓰기 이미지 업로드 API")
public class ClubImageController {

    private final ClubImageStorageService clubImageStorageService;

    @PostMapping(value = "/{clubId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "동아리 이미지 업로드", description = "회장/관리자가 이미지 파일을 Supabase Storage에 업로드하고 공개 URL을 받습니다.")
    public ResponseEntity<ClubImageUploadResponse> uploadImage(
            @PathVariable Long clubId,
            @RequestParam String type,
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(clubImageStorageService.upload(clubId, type, file));
    }
}
