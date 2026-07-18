package com.trailbook.auth;

import com.trailbook.user.UserResponse;

public record AuthResponse(String accessToken, String refreshToken, UserResponse user) {}
