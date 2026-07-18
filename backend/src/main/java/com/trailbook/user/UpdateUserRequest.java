package com.trailbook.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 100) String displayName,
        @Size(max = 500) String avatarUrl,
        @Size(max = 1000) String bio
) {}
