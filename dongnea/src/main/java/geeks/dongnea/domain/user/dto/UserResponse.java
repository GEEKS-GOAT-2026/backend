package geeks.dongnea.domain.user.dto;

import geeks.dongnea.domain.club.dto.ManagedClubResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private boolean manager;
    private List<ManagedClubResponse> managedClubs;
}
