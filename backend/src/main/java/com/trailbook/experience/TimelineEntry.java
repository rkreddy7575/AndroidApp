package com.trailbook.experience;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "timeline_entries")
public class TimelineEntry {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "experience_id", nullable = false) private Experience experience;
    @Column(name = "day_number", nullable = false) private int dayNumber;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "order_index", nullable = false) private int orderIndex;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Experience getExperience() { return experience; }
    public void setExperience(Experience experience) { this.experience = experience; }
    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
