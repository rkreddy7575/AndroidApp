package com.trailbook.experience;

import com.trailbook.auth.SecurityUtils;
import com.trailbook.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experiences")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping("/feed")
    public ApiResponse<Page<ExperienceSummaryResponse>> getFeed(Pageable pageable) {
        return ApiResponse.ok(experienceService.getFeed(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<ExperienceSummaryResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            Pageable pageable
    ) {
        return ApiResponse.ok(experienceService.search(q, destination, sort, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ExperienceDetailResponse> getById(@PathVariable UUID id) {
        return ApiResponse.ok(experienceService.getById(id));
    }

    @PostMapping
    public ApiResponse<ExperienceDetailResponse> create(@Valid @RequestBody CreateExperienceRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        return ApiResponse.ok(experienceService.create(userId, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ExperienceDetailResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateExperienceRequest request
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ApiResponse.ok(experienceService.update(id, userId, request));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<ExperienceDetailResponse> publish(@PathVariable UUID id) {
        UUID userId = SecurityUtils.currentUserId();
        return ApiResponse.ok(experienceService.publish(id, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        UUID userId = SecurityUtils.currentUserId();
        experienceService.delete(id, userId);
        return ApiResponse.ok();
    }
}
