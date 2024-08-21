package com.upwork.shorturl.domain.usecase;

import com.upwork.shorturl.domain.entity.ShortUrl;
import com.upwork.shorturl.domain.entity.exception.ShortenUrlException;
import com.upwork.shorturl.domain.gateway.GenerateSlugProvider;
import com.upwork.shorturl.domain.repository.ShortUrlRepository;
import com.upwork.shorturl.domain.usecase.properties.ShortUrlProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortenUrlTest {

    @Mock
    private ShortUrlProperties shortUrlProperties;

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private GenerateSlugProvider generateSlugProvider;

    @Mock
    private ValidateAndSanitizeUrl validateAndSanitizeUrl;

    @InjectMocks
    private ShortenUrl shortenUrl;

    @Test
    void shouldShortenUrlSuccessfully() {
        String url = "http://example.com";
        String slug = "short";
        int expirationDays = 365;
        int maxAttempts = 3;
        Instant expirationDate = Instant.now().plus(expirationDays, ChronoUnit.DAYS);
        ShortUrl shortUrl = ShortUrl.builder()
                .slug(slug)
                .originalUrl(url)
                .expiresAt(expirationDate)
                .createdAt(Instant.now())
                .build();

        when(shortUrlProperties.getExpirationDays()).thenReturn(expirationDays);
        when(shortUrlProperties.getSlugCollisionMaxAttempts()).thenReturn(maxAttempts);
        when(validateAndSanitizeUrl.execute(url)).thenReturn(Mono.just(url));
        when(generateSlugProvider.generate(url)).thenReturn(Mono.just(slug));
        when(shortUrlRepository.findBySlug(slug)).thenReturn(Mono.empty());
        when(shortUrlRepository.save(ArgumentMatchers.any(ShortUrl.class))).thenReturn(Mono.just(shortUrl));

        StepVerifier.create(shortenUrl.execute(url))
                .expectNext(slug)
                .verifyComplete();

        verify(validateAndSanitizeUrl).execute(url);
        verify(generateSlugProvider, times(1)).generate(url);
        verify(shortUrlRepository).findBySlug(slug);
        verify(shortUrlRepository).save(argThat(input -> {
                    assertThat(input.expiresAt()).isCloseTo(expirationDate, within(5, ChronoUnit.SECONDS));
                    return slug.equals(input.slug()) &&
                            url.equals(input.originalUrl()) && input.expiresAt().truncatedTo(ChronoUnit.SECONDS).equals(expirationDate.truncatedTo(ChronoUnit.SECONDS));
                }

        ));
    }

    @Test
    void shouldHandleSlugCollisionWithRetry() {
        String url = "http://example.com";
        String slug = "short";
        int expirationDays = 365;
        int maxAttempts = 3;
        ShortUrl shortUrl = ShortUrl.builder()
                .slug(slug)
                .originalUrl(url)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();

        when(shortUrlProperties.getExpirationDays()).thenReturn(expirationDays);
        when(shortUrlProperties.getSlugCollisionMaxAttempts()).thenReturn(maxAttempts);
        when(validateAndSanitizeUrl.execute(url)).thenReturn(Mono.just(url));
        when(generateSlugProvider.generate(url)).thenReturn(Mono.just(slug));
        when(shortUrlRepository.findBySlug(slug))
                .thenReturn(Mono.just(shortUrl))
                .thenReturn(Mono.empty());
        when(shortUrlRepository.save(ArgumentMatchers.any(ShortUrl.class))).thenReturn(Mono.just(shortUrl));

        StepVerifier.create(shortenUrl.execute(url))
                .expectNext(slug)
                .verifyComplete();

        verify(validateAndSanitizeUrl).execute(url);
        verify(generateSlugProvider, times(2)).generate(url);
        verify(shortUrlRepository, times(2)).findBySlug(slug);
        verify(shortUrlRepository, times(1)).save(argThat(input ->
                slug.equals(input.slug()) &&
                        url.equals(input.originalUrl())
        ));
    }

    @Test
    void shouldHandleExhaustMaxRetries() {
        String url = "http://example.com";
        String slug = "short";
        int maxAttempts = 3;
        ShortUrl shortUrl = ShortUrl.builder()
                .slug(slug)
                .originalUrl(url)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();

        when(shortUrlProperties.getSlugCollisionMaxAttempts()).thenReturn(maxAttempts);
        when(validateAndSanitizeUrl.execute(url)).thenReturn(Mono.just(url));
        when(generateSlugProvider.generate(url)).thenReturn(Mono.just(slug));
        when(shortUrlRepository.findBySlug(slug))
                .thenReturn(Mono.just(shortUrl));

        StepVerifier.create(shortenUrl.execute(url))
                .expectError(ShortenUrlException.class)
                .verify();

        verify(validateAndSanitizeUrl).execute(url);
        verify(generateSlugProvider, times(maxAttempts + 1)).generate(url);
        verify(shortUrlRepository, times(maxAttempts + 1)).findBySlug(slug);
        verify(shortUrlRepository, times(0)).save(any());
    }

    @Test
    void shouldNotShortenUrlIfValidationFails() {
        String url = "http://example.com";

        when(validateAndSanitizeUrl.execute(url)).thenReturn(Mono.error(new ShortenUrlException("Invalid URL")));

        StepVerifier.create(shortenUrl.execute(url))
                .expectErrorMessage("Invalid URL")
                .verify();

        verify(validateAndSanitizeUrl).execute(url);
        verify(generateSlugProvider, times(0)).generate(any());
        verify(shortUrlRepository, times(0)).findBySlug(any());
        verify(shortUrlRepository, times(0)).save(any());
    }

}