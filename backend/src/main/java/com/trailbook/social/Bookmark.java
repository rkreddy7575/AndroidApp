package com.trailbook.social;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookmarks")
@IdClass(Bookmark.BookmarkId.class)
public class Bookmark {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "experience_id")
    private UUID experienceId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getExperienceId() { return experienceId; }
    public void setExperienceId(UUID experienceId) { this.experienceId = experienceId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class BookmarkId implements Serializable {
        private UUID userId;
        private UUID experienceId;
        public BookmarkId() {}
        public BookmarkId(UUID userId, UUID experienceId) {
            this.userId = userId;
            this.experienceId = experienceId;
        }
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public UUID getExperienceId() { return experienceId; }
        public void setExperienceId(UUID experienceId) { this.experienceId = experienceId; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BookmarkId that)) return false;
            return userId.equals(that.userId) && experienceId.equals(that.experienceId);
        }
        @Override
        public int hashCode() {
            return userId.hashCode() * 31 + experienceId.hashCode();
        }
    }
}
