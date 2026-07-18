package com.trailbook.experience;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateExperienceRequest(
        @NotBlank @Size(max = 200) String title,
        String overview,
        String destination,
        String coverImageUrl,
        List<TimelineEntryRequest> timeline,
        List<BudgetItemRequest> budget,
        List<AccommodationRequest> accommodation,
        List<FoodSpotRequest> food,
        List<TransportationRequest> transportation,
        List<GalleryItemRequest> gallery,
        List<VideoRequest> videos,
        List<TipRequest> tips,
        List<PackingItemRequest> packing
) {
    public record TimelineEntryRequest(Integer dayNumber, String title, String description, Integer orderIndex) {}
    public record BudgetItemRequest(String category, BigDecimal amount, String currency, String notes) {}
    public record AccommodationRequest(String name, String location, BigDecimal cost, String notes) {}
    public record FoodSpotRequest(String name, String cuisine, BigDecimal cost, String notes) {}
    public record TransportationRequest(String mode, String details, BigDecimal cost) {}
    public record GalleryItemRequest(String imageUrl, String caption, Integer orderIndex) {}
    public record VideoRequest(String videoUrl, String thumbnailUrl) {}
    public record TipRequest(String content) {}
    public record PackingItemRequest(String item, Boolean checked) {}
}
