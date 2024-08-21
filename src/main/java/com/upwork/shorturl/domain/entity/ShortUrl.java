package com.upwork.shorturl.domain.entity;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ShortUrl(
        String id,
        String originalUrl,
        String slug,
        Instant createdAt,
        Instant expiresAt) {

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
