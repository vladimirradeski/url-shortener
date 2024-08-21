package com.upwork.shorturl.domain.usecase;

import com.upwork.shorturl.domain.entity.ShortUrl;
import com.upwork.shorturl.domain.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetOriginalUrl {

    private final ShortUrlRepository shortUrlRepository;

    public GetOriginalUrl(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public Mono<String> execute(String slug) {
        return shortUrlRepository.findBySlug(slug)
                .filter(shortUrl -> !shortUrl.isExpired())
                .map(ShortUrl::originalUrl);
    }
}
