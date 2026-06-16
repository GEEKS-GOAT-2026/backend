package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.ClubManager;
import lombok.Getter;

@Getter
public class ManagedClubResponse {

    private final Long clubId;
    private final String clubName;
    private final String role;

    private ManagedClubResponse(Long clubId, String clubName, String role) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.role = role;
    }

    public static ManagedClubResponse from(ClubManager manager) {
        return new ManagedClubResponse(
                manager.getClub().getId(),
                manager.getClub().getName(),
                manager.getRole()
        );
    }
}
