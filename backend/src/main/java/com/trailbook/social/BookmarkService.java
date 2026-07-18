package com.trailbook.social;

import com.trailbook.common.ResourceNotFoundException;
import com.trailbook.experience.Experience;
import com.trailbook.experience.ExperienceRepository;
import com.trailbook.experience.ExperienceSummaryResponse;
import com.trailbook.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ExperienceRepository experienceRepository;
    private final LikeRepository likeRepository;

    public BookmarkService(
            BookmarkRepository bookmarkRepository,
            ExperienceRepository experienceRepository,
            LikeRepository likeRepository
    ) {
        this.bookmarkRepository = bookmarkRepository;
        this.experienceRepository = experienceRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public void bookmark(UUID userId, UUID experienceId) {
        if (!experienceRepository.existsById(experienceId)) {
            throw new ResourceNotFoundException("Experience not found");
        }
        if (bookmarkRepository.existsByUserIdAndExperienceId(userId, experienceId)) {
            return;
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setExperienceId(experienceId);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(UUID userId, UUID experienceId) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setExperienceId(experienceId);
        bookmarkRepository.delete(bookmark);
    }

    public Page<ExperienceSummaryResponse> getBookmarkedExperiences(UUID userId, Pageable pageable) {
        Page<UUID> ids = bookmarkRepository.findExperienceIdsByUserId(userId, pageable);
        List<ExperienceSummaryResponse> content = ids.getContent().stream()
                .map(experienceRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(e -> new ExperienceSummaryResponse(
                        e.getId(),
                        e.getTitle(),
                        e.getOverview(),
                        e.getDestination(),
                        e.getCoverImageUrl(),
                        e.getStatus(),
                        e.getLikeCount(),
                        e.getCommentCount(),
                        UserResponse.from(e.getAuthor()),
                        e.getCreatedAt(),
                        likeRepository.existsByUserIdAndExperienceId(userId, e.getId()),
                        true
                ))
                .toList();
        return new PageImpl<>(content, pageable, ids.getTotalElements());
    }
}
