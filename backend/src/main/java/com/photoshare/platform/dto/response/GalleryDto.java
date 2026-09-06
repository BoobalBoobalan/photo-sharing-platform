package com.photoshare.platform.dto.response;

import com.photoshare.platform.entity.Gallery;
import java.time.LocalDateTime;

public class GalleryDto {
    private Long id;
    private Long eventId;
    private String galleryToken;
    private String pin;
    private String title;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GalleryDto() {}

    public GalleryDto(Long id, Long eventId, String galleryToken, String pin, String title, Boolean isPublished, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.eventId = eventId;
        this.galleryToken = galleryToken;
        this.pin = pin;
        this.title = title;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getGalleryToken() { return galleryToken; }
    public void setGalleryToken(String galleryToken) { this.galleryToken = galleryToken; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean published) { isPublished = published; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static GalleryDto fromEntity(Gallery gallery) {
        if (gallery == null) return null;
        return new GalleryDto(
                gallery.getId(),
                gallery.getEvent() != null ? gallery.getEvent().getId() : null,
                gallery.getGalleryToken(),
                gallery.getPin(),
                gallery.getTitle(),
                gallery.getIsPublished(),
                gallery.getCreatedAt(),
                gallery.getUpdatedAt()
        );
    }

    public static GalleryDtoBuilder builder() { return new GalleryDtoBuilder(); }

    public static class GalleryDtoBuilder {
        private Long id;
        private Long eventId;
        private String galleryToken;
        private String pin;
        private String title;
        private Boolean isPublished;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public GalleryDtoBuilder id(Long id) { this.id = id; return this; }
        public GalleryDtoBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public GalleryDtoBuilder galleryToken(String galleryToken) { this.galleryToken = galleryToken; return this; }
        public GalleryDtoBuilder pin(String pin) { this.pin = pin; return this; }
        public GalleryDtoBuilder title(String title) { this.title = title; return this; }
        public GalleryDtoBuilder isPublished(Boolean isPublished) { this.isPublished = isPublished; return this; }
        public GalleryDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public GalleryDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public GalleryDto build() {
            return new GalleryDto(id, eventId, galleryToken, pin, title, isPublished, createdAt, updatedAt);
        }
    }
}
