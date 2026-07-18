package com.trailbook.social;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Like.LikeId> {
    boolean existsByUserIdAndExperienceId(UUID userId, UUID experienceId);
}
