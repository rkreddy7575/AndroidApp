package com.trailbook.experience;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExperienceDetailResponse(
        UUID id,
        String title,
        String overview,
        String destination,
        String coverImageUrl,
        ExperienceStatus status,
        int likeCount,
        int commentCount,
        UUID authorId,
        String authorName,
        String authorAvatarUrl,
        Instant createdAt,
        Instant updatedAt,
        boolean likedByCurrentUser,
        boolean bookmarkedByCurrentUser,
        List<TimelineEntryDto> timeline,
        List<BudgetItemDto> budget,
        List<AccommodationDto> accommodation,
        List<FoodSpotDto> food,
        List<TransportationDto> transportation,
        List<GalleryItemDto> gallery,
        List<VideoDto> videos,
        List<TipDto> tips,
        List<PackingItemDto> packing
) {
    public record TimelineEntryDto(UUID id, int dayNumber, String title, String description, int orderIndex) {}
    public record BudgetItemDto(UUID id, String category, BigDecimal amount, String currency, String notes) {}
    public record AccommodationDto(UUID id, String name, String location, BigDecimal cost, String notes) {}
    public record FoodSpotDto(UUID id, String name, String cuisine, BigDecimal cost, String notes) {}
    public record TransportationDto(UUID id, String mode, String details, BigDecimal cost) {}
    public record GalleryItemDto(UUID id, String imageUrl, String caption, int orderIndex) {}
    public record VideoDto(UUID id, String videoUrl, String thumbnailUrl) {}
    public record TipDto(UUID id, String content) {}
    public record PackingItemDto(UUID id, String item, boolean checked) {}
}
