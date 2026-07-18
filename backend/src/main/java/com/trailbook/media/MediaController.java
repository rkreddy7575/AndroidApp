package com.trailbook.media;

import com.trailbook.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    public MediaController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/cloudinary-signature")
    public ApiResponse<CloudinarySignatureResponse> getSignature() {
        return ApiResponse.ok(cloudinaryService.generateSignature());
    }
}
