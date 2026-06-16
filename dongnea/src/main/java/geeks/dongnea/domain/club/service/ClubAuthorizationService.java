package geeks.dongnea.domain.club.service;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.repository.ClubManagerRepository;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.global.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubAuthorizationService {

    private final CurrentUserService currentUserService;
    private final ClubRepository clubRepository;
    private final ClubManagerRepository clubManagerRepository;

    public Club requireManagedClub(Long clubId) {
        User currentUser = currentUserService.getCurrentUser();
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리가 없습니다."));

        clubManagerRepository.findByUserAndClub(currentUser, club)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리 관리 권한이 없습니다."));

        return club;
    }
}
