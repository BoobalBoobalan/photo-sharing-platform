package com.photoshare.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String storageLocation;

    @Column(nullable = false)
    private Long fileSize;

    private String contentType;

    @Column(nullable = false)
    private Boolean isSelected = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Photo() {}

    public Photo(Long id, Event event, User uploadedBy, String filename, String storageLocation, Long fileSize, String contentType, Boolean isSelected, LocalDateTime createdAt) {
        this.id = id;
        this.event = event;
        this.uploadedBy = uploadedBy;
        this.filename = filename;
        this.storageLocation = storageLocation;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.isSelected = isSelected != null ? isSelected : false;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isSelected == null) {
            this.isSelected = false;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Boolean getIsSelected() { return isSelected; }
    public void setIsSelected(Boolean selected) { isSelected = selected; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PhotoBuilder builder() { return new PhotoBuilder(); }

    public static class PhotoBuilder {
        private Long id;
        private Event event;
        private User uploadedBy;
        private String filename;
        private String storageLocation;
        private Long fileSize;
        private String contentType;
        private Boolean isSelected = false;
        private LocalDateTime createdAt;

        public PhotoBuilder id(Long id) { this.id = id; return this; }
        public PhotoBuilder event(Event event) { this.event = event; return this; }
        public PhotoBuilder uploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; return this; }
        public PhotoBuilder filename(String filename) { this.filename = filename; return this; }
        public PhotoBuilder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }
        public PhotoBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public PhotoBuilder contentType(String contentType) { this.contentType = contentType; return this; }
        public PhotoBuilder isSelected(Boolean isSelected) { this.isSelected = isSelected; return this; }
        public PhotoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Photo build() {
            return new Photo(id, event, uploadedBy, filename, storageLocation, fileSize, contentType, isSelected, createdAt);
        }
    }
}
