package com.photoshare.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "galleries")
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(nullable = false, unique = true)
    private String galleryToken;

    @Column(nullable = false)
    private String pin;

    private String title;

    @Column(nullable = false)
    private Boolean isPublished = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Gallery() {}

    public Gallery(Long id, Event event, String galleryToken, String pin, String title, Boolean isPublished, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.event = event;
        this.galleryToken = galleryToken;
        this.pin = pin;
        this.title = title;
        this.isPublished = isPublished != null ? isPublished : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isPublished == null) {
            this.isPublished = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

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

    public static GalleryBuilder builder() { return new GalleryBuilder(); }

    public static class GalleryBuilder {
        private Long id;
        private Event event;
        private String galleryToken;
        private String pin;
        private String title;
        private Boolean isPublished = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public GalleryBuilder id(Long id) { this.id = id; return this; }
        public GalleryBuilder event(Event event) { this.event = event; return this; }
        public GalleryBuilder galleryToken(String galleryToken) { this.galleryToken = galleryToken; return this; }
        public GalleryBuilder pin(String pin) { this.pin = pin; return this; }
        public GalleryBuilder title(String title) { this.title = title; return this; }
        public GalleryBuilder isPublished(Boolean isPublished) { this.isPublished = isPublished; return this; }
        public GalleryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public GalleryBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Gallery build() {
            return new Gallery(id, event, galleryToken, pin, title, isPublished, createdAt, updatedAt);
        }
    }
}
