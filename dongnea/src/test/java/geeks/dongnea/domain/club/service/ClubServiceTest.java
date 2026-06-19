package geeks.dongnea.domain.club.service;

import geeks.dongnea.domain.application.repository.ApplicationRepository;
import geeks.dongnea.domain.club.dto.ClubPageResponse;
import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.repository.ClubActivityRepository;
import geeks.dongnea.domain.club.repository.ClubManagerRepository;
import geeks.dongnea.domain.club.repository.ClubMemberRepository;
import geeks.dongnea.domain.club.repository.ClubNoticeRepository;
import geeks.dongnea.domain.club.repository.ClubRepository;
import geeks.dongnea.domain.club.repository.RecruitmentRepository;
import geeks.dongnea.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubActivityRepository clubActivityRepository;

    @Mock
    private ClubNoticeRepository clubNoticeRepository;

    @Mock
    private ClubManagerRepository clubManagerRepository;

    @Mock
    private ClubMemberRepository clubMemberRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClubAuthorizationService clubAuthorizationService;

    private ClubService clubService;

    @BeforeEach
    void setUp() {
        clubService = new ClubService(
                clubRepository,
                clubActivityRepository,
                clubNoticeRepository,
                clubManagerRepository,
                clubMemberRepository,
                applicationRepository,
                recruitmentRepository,
                userRepository,
                clubAuthorizationService
        );
    }

    @Test
    void getClubs_shouldNormalizeBlankFiltersBeforeQuery() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "name"));
        when(clubRepository.findClubsForList(isNull(), isNull(), eq(true), any(LocalDate.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        ClubPageResponse response = clubService.getClubs(pageable, "   ", "\t", true);

        ArgumentCaptor<String> categoryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<LocalDate> todayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(clubRepository).findClubsForList(
                categoryCaptor.capture(),
                keywordCaptor.capture(),
                activeCaptor.capture(),
                todayCaptor.capture(),
                pageableCaptor.capture()
        );

        assertThat(categoryCaptor.getValue()).isNull();
        assertThat(keywordCaptor.getValue()).isNull();
        assertThat(activeCaptor.getValue()).isTrue();
        assertThat(todayCaptor.getValue()).isEqualTo(LocalDate.now());
        assertThat(pageableCaptor.getValue()).isEqualTo(pageable);
        assertThat(response.getContent()).isEmpty();
        assertThat(response.isHasNext()).isFalse();
    }

    @Test
    void getClubs_shouldTrimFilterValuesBeforeQuery() {
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Club> clubPage = new PageImpl<>(List.of(
                Club.builder()
                        .name("AI 스터디")
                        .description("인공지능 동아리")
                        .activityDescription("모델 구현")
                        .category("IT/개발")
                        .profileImg("img")
                        .build()
        ), pageable, 1);

        when(clubRepository.findClubsForList(eq("IT/개발"), eq("AI"), isNull(), any(LocalDate.class), eq(pageable)))
                .thenReturn(clubPage);

        ClubPageResponse response = clubService.getClubs(pageable, " IT/개발 ", " AI ", null);

        verify(clubRepository).findClubsForList(eq("IT/개발"), eq("AI"), isNull(), any(LocalDate.class), eq(pageable));
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("AI 스터디");
    }
}
