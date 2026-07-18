package com.trailbook.experience;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    Page<Experience> findByStatusOrderByCreatedAtDesc(ExperienceStatus status, Pageable pageable);

    Page<Experience> findByAuthorIdAndStatusOrderByCreatedAtDesc(UUID authorId, ExperienceStatus status, Pageable pageable);

    @Query("""
            SELECT e FROM Experience e
            WHERE e.status = 'PUBLISHED'
            AND (:q IS NULL OR :q = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(e.destination) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:destination IS NULL OR :destination = '' OR LOWER(e.destination) LIKE LOWER(CONCAT('%', :destination, '%')))
            """)
    Page<Experience> search(
            @Param("q") String q,
            @Param("destination") String destination,
            Pageable pageable
    );
}
