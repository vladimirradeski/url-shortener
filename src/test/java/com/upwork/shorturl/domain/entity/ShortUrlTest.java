package com.upwork.shorturl.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortUrlTest {

    @Test
    void shouldReturnTrueIfExpired() {
        Instant now = Instant.now();
        Instant expiredAt = now.minusSeconds(10);
        ShortUrl shortUrl = ShortUrl.builder()
                .id("1")
                .originalUrl("http://example.com")
                .slug("short")
                .createdAt(now.minusSeconds(20))
                .expiresAt(expiredAt)
                .build();


        boolean isExpired = shortUrl.isExpired();

        assertTrue(isExpired, "Expected the URL to be expired");
    }

    @Test
    void shouldReturnFalseIfNotExpired() {
        Instant now = Instant.now();
        Instant notExpiredAt = now.plusSeconds(10);
        ShortUrl shortUrl = ShortUrl.builder()
                .id("1")
                .originalUrl("http://example.com")
                .slug("short")
                .createdAt(now.minusSeconds(20))
                .expiresAt(notExpiredAt)
                .build();

        boolean isExpired = shortUrl.isExpired();

        assertFalse(isExpired, "Expected the URL to not be expired");
    }
}
