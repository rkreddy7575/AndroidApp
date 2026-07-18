package com.trailbook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trailbook.jwt")
public record JwtProperties(
        String secret,
        String refreshSecret,
        long accessExpirationMs,
        long refreshExpirationMs
) {}
