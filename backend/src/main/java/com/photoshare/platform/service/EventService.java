package com.photoshare.platform.service;

import com.photoshare.platform.dto.request.CreateEventRequest;
import com.photoshare.platform.dto.response.EventDto;
import com.photoshare.platform.dto.response.GalleryDto;
import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.Gallery;
import com.photoshare.platform.entity.Role;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.exception.ResourceNotFoundException;
import com.photoshare.platform.exception.UnauthorizedAccessException;
import com.photoshare.platform.repository.EventRepository;
import com.photoshare.platform.repository.GalleryRepository;
import com.photoshare.platform.repository.PhotoRepository;
import com.photoshare.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final GalleryRepository galleryRepository;

    @Transactional
    public EventDto createEvent(CreateEventRequest request, User admin) {
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedAccessException("Only Admins can create events");
        }

        Set<User> teamMembers = new HashSet<>();
        if (request.getAssignedUserIds() != null && !request.getAssignedUserIds().isEmpty()) {
            teamMembers.addAll(userRepository.findAllById(request.getAssignedUserIds()));
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .createdBy(admin)
                .assignedTeam(teamMembers)
                .build();

        Event savedEvent = eventRepository.save(event);
        return mapToEventDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsForUser(User user) {
        List<Event> events;
        if (user.getRole() == Role.ROLE_ADMIN) {
            events = eventRepository.findAll();
        } else {
            events = eventRepository.findEventsAssignedToUser(user.getId());
        }

        return events.stream()
                .map(this::mapToEventDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDto getEventById(Long eventId, User currentUser) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        validateUserAccessToEvent(event, currentUser);

        return mapToEventDto(event);
    }

    @Transactional
    public EventDto addTeamMembersToEvent(Long eventId, Set<Long> userIds, User admin) {
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedAccessException("Only Admins can assign team members");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        List<User> newMembers = userRepository.findAllById(userIds);
        event.getAssignedTeam().addAll(newMembers);

        Event updated = eventRepository.save(event);
        return mapToEventDto(updated);
    }

    public void validateUserAccessToEvent(Event event, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        boolean isAssigned = event.getAssignedTeam().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!isAssigned) {
            throw new UnauthorizedAccessException("Access denied: You are not assigned to event " + event.getId());
        }
    }

    public EventDto mapToEventDto(Event event) {
        long totalPhotos = photoRepository.countByEvent(event);
        long selectedPhotos = photoRepository.countByEventAndIsSelectedTrue(event);
        Optional<Gallery> galleryOpt = galleryRepository.findByEvent(event);
        GalleryDto galleryDto = galleryOpt.map(GalleryDto::fromEntity).orElse(null);

        return EventDto.fromEntity(event, totalPhotos, selectedPhotos, galleryDto);
    }
}
