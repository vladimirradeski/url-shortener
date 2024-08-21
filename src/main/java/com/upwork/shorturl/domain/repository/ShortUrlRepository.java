package com.upwork.shorturl.domain.repository;

import com.upwork.shorturl.domain.entity.ShortUrl;
import reactor.core.publisher.Mono;

public interface ShortUrlRepository {
    Mono<ShortUrl> save(ShortUrl shortUrl);

    Mono<ShortUrl> findBySlug(String slug);
}
