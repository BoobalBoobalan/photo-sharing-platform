package com.photoshare.platform.dto.response;

import com.photoshare.platform.entity.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private UserDto createdBy;
    private Set<UserDto> assignedTeam;
    private GalleryDto gallery;
    private long totalPhotosCount;
    private long selectedPhotosCount;
    private LocalDateTime createdAt;

    public EventDto() {}

    public EventDto(Long id, String title, String description, LocalDate eventDate, UserDto createdBy, Set<UserDto> assignedTeam, GalleryDto gallery, long totalPhotosCount, long selectedPhotosCount, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.createdBy = createdBy;
        this.assignedTeam = assignedTeam;
        this.gallery = gallery;
        this.totalPhotosCount = totalPhotosCount;
        this.selectedPhotosCount = selectedPhotosCount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public UserDto getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserDto createdBy) { this.createdBy = createdBy; }

    public Set<UserDto> getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(Set<UserDto> assignedTeam) { this.assignedTeam = assignedTeam; }

    public GalleryDto getGallery() { return gallery; }
    public void setGallery(GalleryDto gallery) { this.gallery = gallery; }

    public long getTotalPhotosCount() { return totalPhotosCount; }
    public void setTotalPhotosCount(long totalPhotosCount) { this.totalPhotosCount = totalPhotosCount; }

    public long getSelectedPhotosCount() { return selectedPhotosCount; }
    public void setSelectedPhotosCount(long selectedPhotosCount) { this.selectedPhotosCount = selectedPhotosCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static EventDto fromEntity(Event event, long totalPhotos, long selectedPhotos, GalleryDto galleryDto) {
        if (event == null) return null;
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                UserDto.fromEntity(event.getCreatedBy()),
                event.getAssignedTeam() != null ? event.getAssignedTeam().stream().map(UserDto::fromEntity).collect(Collectors.toSet()) : null,
                galleryDto,
                totalPhotos,
                selectedPhotos,
                event.getCreatedAt()
        );
    }

    public static EventDtoBuilder builder() { return new EventDtoBuilder(); }

    public static class EventDtoBuilder {
        private Long id;
        private String title;
        private String description;
        private LocalDate eventDate;
        private UserDto createdBy;
        private Set<UserDto> assignedTeam;
        private GalleryDto gallery;
        private long totalPhotosCount;
        private long selectedPhotosCount;
        private LocalDateTime createdAt;

        public EventDtoBuilder id(Long id) { this.id = id; return this; }
        public EventDtoBuilder title(String title) { this.title = title; return this; }
        public EventDtoBuilder description(String description) { this.description = description; return this; }
        public EventDtoBuilder eventDate(LocalDate eventDate) { this.eventDate = eventDate; return this; }
        public EventDtoBuilder createdBy(UserDto createdBy) { this.createdBy = createdBy; return this; }
        public EventDtoBuilder assignedTeam(Set<UserDto> assignedTeam) { this.assignedTeam = assignedTeam; return this; }
        public EventDtoBuilder gallery(GalleryDto gallery) { this.gallery = gallery; return this; }
        public EventDtoBuilder totalPhotosCount(long totalPhotosCount) { this.totalPhotosCount = totalPhotosCount; return this; }
        public EventDtoBuilder selectedPhotosCount(long selectedPhotosCount) { this.selectedPhotosCount = selectedPhotosCount; return this; }
        public EventDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EventDto build() {
            return new EventDto(id, title, description, eventDate, createdBy, assignedTeam, gallery, totalPhotosCount, selectedPhotosCount, createdAt);
        }
    }
}
