package com.photoshare.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public class PublishGalleryRequest {
    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^[0-9]{4,8}$", message = "PIN must be between 4 and 8 digits")
    private String pin;

    private String title;

    private List<Long> selectedPhotoIds;

    public PublishGalleryRequest() {}

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Long> getSelectedPhotoIds() { return selectedPhotoIds; }
    public void setSelectedPhotoIds(List<Long> selectedPhotoIds) { this.selectedPhotoIds = selectedPhotoIds; }
}
