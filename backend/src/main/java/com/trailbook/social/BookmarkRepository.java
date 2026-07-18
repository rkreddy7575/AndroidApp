package com.trailbook.social;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, Bookmark.BookmarkId> {
    boolean existsByUserIdAndExperienceId(UUID userId, UUID experienceId);

    @Query("SELECT b.experienceId FROM Bookmark b WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    Page<UUID> findExperienceIdsByUserId(@Param("userId") UUID userId, Pageable pageable);

    List<Bookmark> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
