package com.upwork.shorturl.domain.usecase;

import com.upwork.shorturl.domain.entity.ShortUrl;
import com.upwork.shorturl.domain.entity.exception.ShortenUrlException;
import com.upwork.shorturl.domain.gateway.GenerateSlugProvider;
import com.upwork.shorturl.domain.repository.ShortUrlRepository;
import com.upwork.shorturl.domain.usecase.properties.ShortUrlProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class ShortenUrl {

    private final ShortUrlProperties shortUrlProperties;
    private final ShortUrlRepository shortUrlRepository;
    private final GenerateSlugProvider generateSlugProvider;
    private final ValidateAndSanitizeUrl validateAndSanitizeUrl;

    public ShortenUrl(ShortUrlProperties shortUrlProperties, ShortUrlRepository shortUrlRepository, GenerateSlugProvider generateSlugProvider, ValidateAndSanitizeUrl validateAndSanitizeUrl) {
        this.shortUrlProperties = shortUrlProperties;
        this.shortUrlRepository = shortUrlRepository;
        this.generateSlugProvider = generateSlugProvider;
        this.validateAndSanitizeUrl = validateAndSanitizeUrl;
    }

    public Mono<String> execute(String url) {
        return validateAndSanitizeUrl.execute(url)
                .flatMap(this::generateSlugWithRetry)
                .map(ShortUrl::slug);
    }

    private Mono<ShortUrl> generateSlugWithRetry(String url) {
        return generateSlug(url)
                .retryWhen(createRetrySpec(url));
    }

    private Retry createRetrySpec(String url) {
        return Retry.max(shortUrlProperties.getSlugCollisionMaxAttempts())
                .onRetryExhaustedThrow(
                        (retryBackoffSpec, retrySignal) -> {
                            log.error("Shorten url failed due to handling slug collision retry exhausted | url: {}", url);
                            throw new ShortenUrlException("Url shortening failed please try again");
                        }
                );
    }

    private Mono<ShortUrl> generateSlug(String url) {
        return Mono.just(url)
                .flatMap(generateSlugProvider::generate)
                .flatMap(slug -> checkUniqueSlug(url, slug));
    }

    private Mono<ShortUrl> checkUniqueSlug(String url, String slug) {
        return shortUrlRepository.findBySlug(slug)
                .flatMap(existingShortUrl -> handleSlugCollision(url, slug))
                .switchIfEmpty(saveShortUrl(url, slug));
    }

    private Mono<ShortUrl> handleSlugCollision(String url, String slug) {
        log.error("Slug collision occurred | slug: {}, url: {}", slug, url);
        return Mono.error(new ShortenUrlException("Slug collision occurred: " + slug));
    }

    private Mono<ShortUrl> saveShortUrl(String url, String slug) {
        return Mono.defer(() -> Mono.just(toShortUrl(url, slug))
                .flatMap(shortUrlRepository::save)
        );
    }

    private ShortUrl toShortUrl(String originalUrl, String slug) {
        return ShortUrl.builder()
                .slug(slug)
                .originalUrl(originalUrl)
                .expiresAt(Instant.now().plus(shortUrlProperties.getExpirationDays(), ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();
    }
}
