package com.trailbook.experience;

import com.trailbook.auth.SecurityUtils;
import com.trailbook.common.BadRequestException;
import com.trailbook.common.ResourceNotFoundException;
import com.trailbook.social.BookmarkRepository;
import com.trailbook.social.LikeRepository;
import com.trailbook.user.User;
import com.trailbook.user.UserRepository;
import com.trailbook.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    public ExperienceService(
            ExperienceRepository experienceRepository,
            UserRepository userRepository,
            LikeRepository likeRepository,
            BookmarkRepository bookmarkRepository
    ) {
        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    public Page<ExperienceSummaryResponse> getFeed(Pageable pageable) {
        return experienceRepository.findByStatusOrderByCreatedAtDesc(ExperienceStatus.PUBLISHED, pageable)
                .map(this::toSummary);
    }

    public Page<ExperienceSummaryResponse> search(String q, String destination, String sort, Pageable pageable) {
        Sort effectiveSort = "popular".equals(sort)
                ? Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))
                : Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
        return experienceRepository.search(q, destination, sortedPageable)
                .map(this::toSummary);
    }

    public Page<ExperienceSummaryResponse> getPublishedByAuthor(UUID authorId, Pageable pageable) {
        return experienceRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(
                authorId, ExperienceStatus.PUBLISHED, pageable
        ).map(this::toSummary);
    }

    public ExperienceDetailResponse getById(UUID id) {
        Experience experience = findExperience(id);
        if (experience.getStatus() != ExperienceStatus.PUBLISHED) {
            UUID currentUserId = SecurityUtils.currentUserIdOrNull();
            if (currentUserId == null || !experience.getAuthor().getId().equals(currentUserId)) {
                throw new ResourceNotFoundException("Experience not found");
            }
        }
        return toDetail(experience);
    }

    @Transactional
    public ExperienceDetailResponse create(UUID authorId, CreateExperienceRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Experience experience = new Experience();
        experience.setAuthor(author);
        applyRequest(experience, request);
        experience.setStatus(ExperienceStatus.DRAFT);
        return toDetail(experienceRepository.save(experience));
    }

    @Transactional
    public ExperienceDetailResponse update(UUID id, UUID userId, CreateExperienceRequest request) {
        Experience experience = findOwnedExperience(id, userId);
        applyRequest(experience, request);
        return toDetail(experienceRepository.save(experience));
    }

    @Transactional
    public ExperienceDetailResponse publish(UUID id, UUID userId) {
        Experience experience = findOwnedExperience(id, userId);
        if (experience.getTitle() == null || experience.getTitle().isBlank()) {
            throw new BadRequestException("Title is required to publish");
        }
        experience.setStatus(ExperienceStatus.PUBLISHED);
        return toDetail(experienceRepository.save(experience));
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        Experience experience = findOwnedExperience(id, userId);
        experienceRepository.delete(experience);
    }

    private Experience findExperience(UUID id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
    }

    private Experience findOwnedExperience(UUID id, UUID userId) {
        Experience experience = findExperience(id);
        if (!experience.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("Not authorized to modify this experience");
        }
        return experience;
    }

    private void applyRequest(Experience experience, CreateExperienceRequest request) {
        experience.setTitle(request.title());
        experience.setOverview(request.overview());
        experience.setDestination(request.destination());
        experience.setCoverImageUrl(request.coverImageUrl());

        experience.getTimeline().clear();
        if (request.timeline() != null) {
            int idx = 0;
            for (var t : request.timeline()) {
                TimelineEntry entry = new TimelineEntry();
                entry.setExperience(experience);
                entry.setDayNumber(t.dayNumber() != null ? t.dayNumber() : 1);
                entry.setTitle(t.title());
                entry.setDescription(t.description());
                entry.setOrderIndex(t.orderIndex() != null ? t.orderIndex() : idx++);
                experience.getTimeline().add(entry);
            }
        }

        experience.getBudgetItems().clear();
        if (request.budget() != null) {
            for (var b : request.budget()) {
                BudgetItem item = new BudgetItem();
                item.setExperience(experience);
                item.setCategory(b.category());
                item.setAmount(b.amount());
                item.setCurrency(b.currency() != null ? b.currency() : "USD");
                item.setNotes(b.notes());
                experience.getBudgetItems().add(item);
            }
        }

        experience.getAccommodations().clear();
        if (request.accommodation() != null) {
            for (var a : request.accommodation()) {
                Accommodation acc = new Accommodation();
                acc.setExperience(experience);
                acc.setName(a.name());
                acc.setLocation(a.location());
                acc.setCost(a.cost());
                acc.setNotes(a.notes());
                experience.getAccommodations().add(acc);
            }
        }

        experience.getFoodSpots().clear();
        if (request.food() != null) {
            for (var f : request.food()) {
                FoodSpot spot = new FoodSpot();
                spot.setExperience(experience);
                spot.setName(f.name());
                spot.setCuisine(f.cuisine());
                spot.setCost(f.cost());
                spot.setNotes(f.notes());
                experience.getFoodSpots().add(spot);
            }
        }

        experience.getTransportations().clear();
        if (request.transportation() != null) {
            for (var t : request.transportation()) {
                Transportation tr = new Transportation();
                tr.setExperience(experience);
                tr.setMode(t.mode());
                tr.setDetails(t.details());
                tr.setCost(t.cost());
                experience.getTransportations().add(tr);
            }
        }

        experience.getGallery().clear();
        if (request.gallery() != null) {
            int idx = 0;
            for (var g : request.gallery()) {
                GalleryItem item = new GalleryItem();
                item.setExperience(experience);
                item.setImageUrl(g.imageUrl());
                item.setCaption(g.caption());
                item.setOrderIndex(g.orderIndex() != null ? g.orderIndex() : idx++);
                experience.getGallery().add(item);
            }
        }

        experience.getVideos().clear();
        if (request.videos() != null) {
            for (var v : request.videos()) {
                Video video = new Video();
                video.setExperience(experience);
                video.setVideoUrl(v.videoUrl());
                video.setThumbnailUrl(v.thumbnailUrl());
                experience.getVideos().add(video);
            }
        }

        experience.getTips().clear();
        if (request.tips() != null) {
            for (var t : request.tips()) {
                Tip tip = new Tip();
                tip.setExperience(experience);
                tip.setContent(t.content());
                experience.getTips().add(tip);
            }
        }

        experience.getPackingItems().clear();
        if (request.packing() != null) {
            for (var p : request.packing()) {
                PackingItem item = new PackingItem();
                item.setExperience(experience);
                item.setItem(p.item());
                item.setChecked(p.checked() != null && p.checked());
                experience.getPackingItems().add(item);
            }
        }
    }

    private ExperienceSummaryResponse toSummary(Experience e) {
        UUID currentUserId = SecurityUtils.currentUserIdOrNull();
        boolean liked = currentUserId != null && likeRepository.existsByUserIdAndExperienceId(currentUserId, e.getId());
        boolean bookmarked = currentUserId != null && bookmarkRepository.existsByUserIdAndExperienceId(currentUserId, e.getId());
        return new ExperienceSummaryResponse(
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
                liked,
                bookmarked
        );
    }

    private ExperienceDetailResponse toDetail(Experience e) {
        UUID currentUserId = SecurityUtils.currentUserIdOrNull();
        boolean liked = currentUserId != null && likeRepository.existsByUserIdAndExperienceId(currentUserId, e.getId());
        boolean bookmarked = currentUserId != null && bookmarkRepository.existsByUserIdAndExperienceId(currentUserId, e.getId());

        return new ExperienceDetailResponse(
                e.getId(),
                e.getTitle(),
                e.getOverview(),
                e.getDestination(),
                e.getCoverImageUrl(),
                e.getStatus(),
                e.getLikeCount(),
                e.getCommentCount(),
                e.getAuthor().getId(),
                e.getAuthor().getDisplayName(),
                e.getAuthor().getAvatarUrl(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                liked,
                bookmarked,
                e.getTimeline().stream().map(t -> new ExperienceDetailResponse.TimelineEntryDto(
                        t.getId(), t.getDayNumber(), t.getTitle(), t.getDescription(), t.getOrderIndex()
                )).toList(),
                e.getBudgetItems().stream().map(b -> new ExperienceDetailResponse.BudgetItemDto(
                        b.getId(), b.getCategory(), b.getAmount(), b.getCurrency(), b.getNotes()
                )).toList(),
                e.getAccommodations().stream().map(a -> new ExperienceDetailResponse.AccommodationDto(
                        a.getId(), a.getName(), a.getLocation(), a.getCost(), a.getNotes()
                )).toList(),
                e.getFoodSpots().stream().map(f -> new ExperienceDetailResponse.FoodSpotDto(
                        f.getId(), f.getName(), f.getCuisine(), f.getCost(), f.getNotes()
                )).toList(),
                e.getTransportations().stream().map(t -> new ExperienceDetailResponse.TransportationDto(
                        t.getId(), t.getMode(), t.getDetails(), t.getCost()
                )).toList(),
                e.getGallery().stream().map(g -> new ExperienceDetailResponse.GalleryItemDto(
                        g.getId(), g.getImageUrl(), g.getCaption(), g.getOrderIndex()
                )).toList(),
                e.getVideos().stream().map(v -> new ExperienceDetailResponse.VideoDto(
                        v.getId(), v.getVideoUrl(), v.getThumbnailUrl()
                )).toList(),
                e.getTips().stream().map(t -> new ExperienceDetailResponse.TipDto(
                        t.getId(), t.getContent()
                )).toList(),
                e.getPackingItems().stream().map(p -> new ExperienceDetailResponse.PackingItemDto(
                        p.getId(), p.getItem(), p.isChecked()
                )).toList()
        );
    }
}
