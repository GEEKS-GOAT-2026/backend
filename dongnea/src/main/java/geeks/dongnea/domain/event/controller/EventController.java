package geeks.dongnea.domain.event.controller;

import geeks.dongnea.domain.event.dto.EventCreateRequest;
import geeks.dongnea.domain.event.dto.EventListResponse;
import geeks.dongnea.domain.event.dto.EventPageResponse;
import geeks.dongnea.domain.event.dto.EventUpdateRequest;
import geeks.dongnea.domain.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "동아리 행사 API")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "행사 목록 조회", description = "게시된 동아리 행사를 페이지 단위로 조회합니다.")
    public ResponseEntity<EventPageResponse> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        return ResponseEntity.ok(eventService.getEvents(pageRequest, keyword, clubId, fromDate, toDate));
    }

    @GetMapping("/recent")
    @Operation(summary = "최근/예정 행사 조회", description = "메인 화면에서 사용할 행사 목록을 조회합니다.")
    public ResponseEntity<List<EventListResponse>> getRecentEvents(
            @RequestParam(defaultValue = "3") int size
    ) {
        return ResponseEntity.ok(eventService.getRecentEvents(size));
    }

    @PostMapping
    @Operation(summary = "행사 생성", description = "동아리 관리자가 새 행사를 작성합니다.")
    public ResponseEntity<EventListResponse> createEvent(
            @RequestParam Long clubId,
            @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.ok(eventService.createEvent(clubId, request));
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "행사 수정", description = "동아리 관리자가 기존 행사를 수정합니다.")
    public ResponseEntity<EventListResponse> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequest request
    ) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "행사 삭제", description = "동아리 관리자가 기존 행사를 삭제합니다.")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
