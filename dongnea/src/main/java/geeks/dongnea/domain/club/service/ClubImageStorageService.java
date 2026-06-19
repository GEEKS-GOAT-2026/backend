package geeks.dongnea.domain.club.service;

import geeks.dongnea.domain.club.dto.ClubImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
public class ClubImageStorageService {

    private static final Map<String, String> EXTENSIONS = Map.of(
            MediaType.IMAGE_JPEG_VALUE, "jpg",
            MediaType.IMAGE_PNG_VALUE, "png",
            "image/webp", "webp"
    );

    private final ClubAuthorizationService clubAuthorizationService;
    private final RestClient restClient;
    private final String supabaseUrl;
    private final String secretKey;
    private final String bucket;
    private final long maxFileSize;

    public ClubImageStorageService(
            ClubAuthorizationService clubAuthorizationService,
            RestClient.Builder restClientBuilder,
            @Value("${app.storage.supabase-url:}") String supabaseUrl,
            @Value("${app.storage.secret-key:}") String secretKey,
            @Value("${app.storage.bucket:club-images}") String bucket,
            @Value("${app.storage.max-file-size:5242880}") long maxFileSize
    ) {
        this.clubAuthorizationService = clubAuthorizationService;
        this.restClient = restClientBuilder.build();
        this.supabaseUrl = removeTrailingSlash(supabaseUrl);
        this.secretKey = secretKey;
        this.bucket = bucket;
        this.maxFileSize = maxFileSize;
    }

    public ClubImageUploadResponse upload(Long clubId, String typeValue, MultipartFile file) {
        clubAuthorizationService.requireManagedClub(clubId);
        validateConfiguration();

        ClubImageType type = ClubImageType.from(typeValue);
        String contentType = validateFile(file);
        String extension = EXTENSIONS.get(contentType);
        String objectPath = "clubs/%d/%s/%s.%s".formatted(
                clubId,
                type.getDirectory(),
                UUID.randomUUID(),
                extension
        );

        uploadObject(objectPath, contentType, file);

        String publicUrl = "%s/storage/v1/object/public/%s/%s".formatted(
                supabaseUrl,
                bucket,
                objectPath
        );
        return new ClubImageUploadResponse(publicUrl, objectPath);
    }

    private void uploadObject(String objectPath, String contentType, MultipartFile file) {
        String uploadUrl = "%s/storage/v1/object/%s/%s".formatted(supabaseUrl, bucket, objectPath);

        try {
            RestClient.RequestBodySpec request = restClient.post()
                    .uri(URI.create(uploadUrl))
                    .header("apikey", secretKey)
                    .header("x-upsert", "false")
                    .contentType(MediaType.parseMediaType(contentType));

            if (!secretKey.startsWith("sb_secret_")) {
                request.header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey);
            }

            request.body(file.getBytes())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(
                    "이미지 저장소 업로드에 실패했습니다. status=" + exception.getStatusCode().value()
            );
        } catch (IOException exception) {
            throw new IllegalStateException("이미지 파일을 읽을 수 없습니다.", exception);
        }
    }

    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지 파일이 필요합니다.");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("이미지 파일은 5MB 이하여야 합니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !EXTENSIONS.containsKey(contentType)) {
            throw new IllegalArgumentException("JPG, PNG, WebP 이미지만 업로드할 수 있습니다.");
        }
        return contentType;
    }

    private void validateConfiguration() {
        if (supabaseUrl.isBlank() || secretKey.isBlank() || bucket.isBlank()) {
            throw new IllegalStateException("Supabase Storage 환경변수가 설정되지 않았습니다.");
        }
    }

    private static String removeTrailingSlash(String value) {
        if (value == null) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
