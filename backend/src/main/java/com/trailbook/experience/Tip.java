package com.trailbook.experience;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tips")
public class Tip {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "experience_id", nullable = false) private Experience experience;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Experience getExperience() { return experience; }
    public void setExperience(Experience experience) { this.experience = experience; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
