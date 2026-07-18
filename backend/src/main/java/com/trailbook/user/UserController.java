package com.trailbook.user;

import com.trailbook.auth.SecurityUtils;
import com.trailbook.common.ApiResponse;
import com.trailbook.experience.ExperienceService;
import com.trailbook.experience.ExperienceSummaryResponse;
import com.trailbook.social.BookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ExperienceService experienceService;
    private final BookmarkService bookmarkService;

    public UserController(UserService userService, ExperienceService experienceService, BookmarkService bookmarkService) {
        this.userService = userService;
        this.experienceService = experienceService;
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe() {
        UUID userId = SecurityUtils.currentUserId();
        return ApiResponse.ok(UserResponse.from(userService.getById(userId)));
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMe(@RequestBody UpdateUserRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        return ApiResponse.ok(userService.updateProfile(userId, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID id) {
        return ApiResponse.ok(UserResponse.from(userService.getById(id)));
    }

    @GetMapping("/{id}/experiences")
    public ApiResponse<Page<ExperienceSummaryResponse>> getUserExperiences(
            @PathVariable UUID id,
            Pageable pageable
    ) {
        return ApiResponse.ok(experienceService.getPublishedByAuthor(id, pageable));
    }

    @GetMapping("/me/bookmarks")
    public ApiResponse<Page<ExperienceSummaryResponse>> getBookmarks(Pageable pageable) {
        UUID userId = SecurityUtils.currentUserId();
        return ApiResponse.ok(bookmarkService.getBookmarkedExperiences(userId, pageable));
    }
}
