package geeks.dongnea.domain.club.dto;

import lombok.Getter;

@Getter
public class ClubImageUploadResponse {

    private final String imageUrl;
    private final String path;

    public ClubImageUploadResponse(String imageUrl, String path) {
        this.imageUrl = imageUrl;
        this.path = path;
    }
}
