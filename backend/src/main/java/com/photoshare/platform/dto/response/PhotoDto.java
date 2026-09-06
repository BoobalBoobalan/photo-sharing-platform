package com.photoshare.platform.dto.response;

import com.photoshare.platform.entity.Photo;
import java.time.LocalDateTime;

public class PhotoDto {
    private Long id;
    private Long eventId;
    private Long uploadedById;
    private String uploadedByName;
    private String filename;
    private String storageLocation;
    private Long fileSize;
    private String contentType;
    private Boolean isSelected;
    private LocalDateTime createdAt;

    public PhotoDto() {}

    public PhotoDto(Long id, Long eventId, Long uploadedById, String uploadedByName, String filename, String storageLocation, Long fileSize, String contentType, Boolean isSelected, LocalDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.uploadedById = uploadedById;
        this.uploadedByName = uploadedByName;
        this.filename = filename;
        this.storageLocation = storageLocation;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.isSelected = isSelected;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUploadedById() { return uploadedById; }
    public void setUploadedById(Long uploadedById) { this.uploadedById = uploadedById; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

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

    public static PhotoDto fromEntity(Photo photo) {
        if (photo == null) return null;
        return new PhotoDto(
                photo.getId(),
                photo.getEvent() != null ? photo.getEvent().getId() : null,
                photo.getUploadedBy() != null ? photo.getUploadedBy().getId() : null,
                photo.getUploadedBy() != null ? photo.getUploadedBy().getFullName() : null,
                photo.getFilename(),
                photo.getStorageLocation(),
                photo.getFileSize(),
                photo.getContentType(),
                photo.getIsSelected(),
                photo.getCreatedAt()
        );
    }

    public static PhotoDtoBuilder builder() { return new PhotoDtoBuilder(); }

    public static class PhotoDtoBuilder {
        private Long id;
        private Long eventId;
        private Long uploadedById;
        private String uploadedByName;
        private String filename;
        private String storageLocation;
        private Long fileSize;
        private String contentType;
        private Boolean isSelected;
        private LocalDateTime createdAt;

        public PhotoDtoBuilder id(Long id) { this.id = id; return this; }
        public PhotoDtoBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public PhotoDtoBuilder uploadedById(Long uploadedById) { this.uploadedById = uploadedById; return this; }
        public PhotoDtoBuilder uploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; return this; }
        public PhotoDtoBuilder filename(String filename) { this.filename = filename; return this; }
        public PhotoDtoBuilder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }
        public PhotoDtoBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public PhotoDtoBuilder contentType(String contentType) { this.contentType = contentType; return this; }
        public PhotoDtoBuilder isSelected(Boolean isSelected) { this.isSelected = isSelected; return this; }
        public PhotoDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PhotoDto build() {
            return new PhotoDto(id, eventId, uploadedById, uploadedByName, filename, storageLocation, fileSize, contentType, isSelected, createdAt);
        }
    }
}
