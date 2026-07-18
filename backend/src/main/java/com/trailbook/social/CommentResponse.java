package com.trailbook.social;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID experienceId,
        UUID userId,
        String userName,
        String userAvatarUrl,
        String content,
        Instant createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getExperienceId(),
                c.getUser().getId(),
                c.getUser().getDisplayName(),
                c.getUser().getAvatarUrl(),
                c.getContent(),
                c.getCreatedAt()
        );
    }
}
