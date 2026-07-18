package com.trailbook.experience;

import com.trailbook.user.UserResponse;

import java.time.Instant;
import java.util.UUID;

public record ExperienceSummaryResponse(
        UUID id,
        String title,
        String overview,
        String destination,
        String coverImageUrl,
        ExperienceStatus status,
        int likeCount,
        int commentCount,
        UserResponse author,
        Instant createdAt,
        boolean likedByCurrentUser,
        boolean bookmarkedByCurrentUser
) {}
