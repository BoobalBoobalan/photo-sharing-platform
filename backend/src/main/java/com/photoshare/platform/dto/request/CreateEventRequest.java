package com.photoshare.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public class CreateEventRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    private Set<Long> assignedUserIds;

    public CreateEventRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public Set<Long> getAssignedUserIds() { return assignedUserIds; }
    public void setAssignedUserIds(Set<Long> assignedUserIds) { this.assignedUserIds = assignedUserIds; }
}
