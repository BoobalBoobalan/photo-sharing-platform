package com.photoshare.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_team_members",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedTeam = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Event() {}

    public Event(Long id, String title, String description, LocalDate eventDate, User createdBy, Set<User> assignedTeam, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.createdBy = createdBy;
        this.assignedTeam = assignedTeam != null ? assignedTeam : new HashSet<>();
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Set<User> getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(Set<User> assignedTeam) { this.assignedTeam = assignedTeam; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static EventBuilder builder() { return new EventBuilder(); }

    public static class EventBuilder {
        private Long id;
        private String title;
        private String description;
        private LocalDate eventDate;
        private User createdBy;
        private Set<User> assignedTeam = new HashSet<>();
        private LocalDateTime createdAt;

        public EventBuilder id(Long id) { this.id = id; return this; }
        public EventBuilder title(String title) { this.title = title; return this; }
        public EventBuilder description(String description) { this.description = description; return this; }
        public EventBuilder eventDate(LocalDate eventDate) { this.eventDate = eventDate; return this; }
        public EventBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public EventBuilder assignedTeam(Set<User> assignedTeam) { this.assignedTeam = assignedTeam; return this; }
        public EventBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Event build() {
            return new Event(id, title, description, eventDate, createdBy, assignedTeam, createdAt);
        }
    }
}
