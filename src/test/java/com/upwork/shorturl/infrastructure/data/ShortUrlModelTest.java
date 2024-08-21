package com.upwork.shorturl.infrastructure.data;

import com.upwork.shorturl.domain.entity.ShortUrl;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortUrlModelTest {

    @Test
    void shouldConvertToDomain() {
        Instant now = Instant.now();
        ShortUrlModel model = ShortUrlModel.builder()
                .id("1")
                .originalUrl("http://example.com")
                .slug("exampleSlug")
                .createdAt(now.minusSeconds(10))
                .expiresAt(now.plusSeconds(10))
                .build();

        ShortUrl domain = model.toDomain();

        assertEquals(model.getId(), domain.id());
        assertEquals(model.getOriginalUrl(), domain.originalUrl());
        assertEquals(model.getSlug(), domain.slug());
        assertEquals(model.getCreatedAt(), domain.createdAt());
        assertEquals(model.getExpiresAt(), domain.expiresAt());
    }

    @Test
    void shouldConvertFromDomain() {
        Instant now = Instant.now();
        ShortUrl domain = ShortUrl.builder()
                .id("1")
                .originalUrl("http://example.com")
                .slug("exampleSlug")
                .createdAt(now.minusSeconds(10))
                .expiresAt(now.plusSeconds(10))
                .build();

        ShortUrlModel model = ShortUrlModel.fromDomain(domain);

        assertEquals(domain.id(), model.getId());
        assertEquals(domain.originalUrl(), model.getOriginalUrl());
        assertEquals(domain.slug(), model.getSlug());
        assertEquals(domain.createdAt(), model.getCreatedAt());
        assertEquals(domain.expiresAt(), model.getExpiresAt());
    }
}
