package com.photoshare.platform.dto.response;

import java.time.LocalDate;
import java.util.List;

public class PublicGalleryResponse {
    private String galleryToken;
    private String eventTitle;
    private String eventDescription;
    private LocalDate eventDate;
    private Boolean isPinRequired;
    private Boolean isPinVerified;
    private Integer totalPhotosCount;
    private List<PhotoDto> photos;

    public PublicGalleryResponse() {}

    public PublicGalleryResponse(String galleryToken, String eventTitle, String eventDescription, LocalDate eventDate, Boolean isPinRequired, Boolean isPinVerified, Integer totalPhotosCount, List<PhotoDto> photos) {
        this.galleryToken = galleryToken;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.isPinRequired = isPinRequired;
        this.isPinVerified = isPinVerified;
        this.totalPhotosCount = totalPhotosCount;
        this.photos = photos;
    }

    public String getGalleryToken() { return galleryToken; }
    public void setGalleryToken(String galleryToken) { this.galleryToken = galleryToken; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public Boolean getIsPinRequired() { return isPinRequired; }
    public void setIsPinRequired(Boolean pinRequired) { isPinRequired = pinRequired; }

    public Boolean getIsPinVerified() { return isPinVerified; }
    public void setIsPinVerified(Boolean pinVerified) { isPinVerified = pinVerified; }

    public Integer getTotalPhotosCount() { return totalPhotosCount; }
    public void setTotalPhotosCount(Integer totalPhotosCount) { this.totalPhotosCount = totalPhotosCount; }

    public List<PhotoDto> getPhotos() { return photos; }
    public void setPhotos(List<PhotoDto> photos) { this.photos = photos; }

    public static PublicGalleryResponseBuilder builder() { return new PublicGalleryResponseBuilder(); }

    public static class PublicGalleryResponseBuilder {
        private String galleryToken;
        private String eventTitle;
        private String eventDescription;
        private LocalDate eventDate;
        private Boolean isPinRequired;
        private Boolean isPinVerified;
        private Integer totalPhotosCount;
        private List<PhotoDto> photos;

        public PublicGalleryResponseBuilder galleryToken(String galleryToken) { this.galleryToken = galleryToken; return this; }
        public PublicGalleryResponseBuilder eventTitle(String eventTitle) { this.eventTitle = eventTitle; return this; }
        public PublicGalleryResponseBuilder eventDescription(String eventDescription) { this.eventDescription = eventDescription; return this; }
        public PublicGalleryResponseBuilder eventDate(LocalDate eventDate) { this.eventDate = eventDate; return this; }
        public PublicGalleryResponseBuilder isPinRequired(Boolean isPinRequired) { this.isPinRequired = isPinRequired; return this; }
        public PublicGalleryResponseBuilder isPinVerified(Boolean isPinVerified) { this.isPinVerified = isPinVerified; return this; }
        public PublicGalleryResponseBuilder totalPhotosCount(Integer totalPhotosCount) { this.totalPhotosCount = totalPhotosCount; return this; }
        public PublicGalleryResponseBuilder photos(List<PhotoDto> photos) { this.photos = photos; return this; }

        public PublicGalleryResponse build() {
            return new PublicGalleryResponse(galleryToken, eventTitle, eventDescription, eventDate, isPinRequired, isPinVerified, totalPhotosCount, photos);
        }
    }
}
