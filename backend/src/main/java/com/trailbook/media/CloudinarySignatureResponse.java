package com.trailbook.media;

public record CloudinarySignatureResponse(
        String cloudName,
        String apiKey,
        long timestamp,
        String signature
) {}
