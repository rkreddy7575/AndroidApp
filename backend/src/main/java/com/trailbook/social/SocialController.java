package com.trailbook.social;

import com.trailbook.auth.SecurityUtils;
import com.trailbook.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experiences/{experienceId}")
public class SocialController {

    private final LikeService likeService;
    private final BookmarkService bookmarkService;
    private final CommentService commentService;

    public SocialController(LikeService likeService, BookmarkService bookmarkService, CommentService commentService) {
        this.likeService = likeService;
        this.bookmarkService = bookmarkService;
        this.commentService = commentService;
    }

    @PostMapping("/like")
    public ApiResponse<Void> like(@PathVariable UUID experienceId) {
        likeService.like(SecurityUtils.currentUserId(), experienceId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/like")
    public ApiResponse<Void> unlike(@PathVariable UUID experienceId) {
        likeService.unlike(SecurityUtils.currentUserId(), experienceId);
        return ApiResponse.ok();
    }

    @PostMapping("/bookmark")
    public ApiResponse<Void> bookmark(@PathVariable UUID experienceId) {
        bookmarkService.bookmark(SecurityUtils.currentUserId(), experienceId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/bookmark")
    public ApiResponse<Void> removeBookmark(@PathVariable UUID experienceId) {
        bookmarkService.removeBookmark(SecurityUtils.currentUserId(), experienceId);
        return ApiResponse.ok();
    }

    @GetMapping("/comments")
    public ApiResponse<Page<CommentResponse>> getComments(
            @PathVariable UUID experienceId,
            Pageable pageable
    ) {
        return ApiResponse.ok(commentService.getComments(experienceId, pageable));
    }

    @PostMapping("/comments")
    public ApiResponse<CommentResponse> addComment(
            @PathVariable UUID experienceId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ApiResponse.ok(
                commentService.addComment(SecurityUtils.currentUserId(), experienceId, request.content())
        );
    }
}
