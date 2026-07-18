package com.trailbook.experience;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "videos")
public class Video {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "experience_id", nullable = false) private Experience experience;
    @Column(name = "video_url", nullable = false, length = 500) private String videoUrl;
    @Column(name = "thumbnail_url", length = 500) private String thumbnailUrl;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Experience getExperience() { return experience; }
    public void setExperience(Experience experience) { this.experience = experience; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}
