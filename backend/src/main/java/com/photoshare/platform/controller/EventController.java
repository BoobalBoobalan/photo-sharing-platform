package com.photoshare.platform.controller;

import com.photoshare.platform.dto.request.CreateEventRequest;
import com.photoshare.platform.dto.response.ApiResponse;
import com.photoshare.platform.dto.response.EventDto;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.security.SecurityUtils;
import com.photoshare.platform.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDto>>> getEvents() {
        User currentUser = securityUtils.getCurrentUser();
        List<EventDto> events = eventService.getEventsForUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDto>> getEventById(@PathVariable Long id) {
        User currentUser = securityUtils.getCurrentUser();
        EventDto event = eventService.getEventById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EventDto>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        User admin = securityUtils.getCurrentUser();
        EventDto created = eventService.createEvent(request, admin);
        return ResponseEntity.ok(ApiResponse.success("Event created successfully", created));
    }

    @PostMapping("/{id}/team")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EventDto>> addTeamMembers(
            @PathVariable Long id,
            @RequestBody Set<Long> userIds) {
        User admin = securityUtils.getCurrentUser();
        EventDto updated = eventService.addTeamMembersToEvent(id, userIds, admin);
        return ResponseEntity.ok(ApiResponse.success("Team members added successfully", updated));
    }
}
