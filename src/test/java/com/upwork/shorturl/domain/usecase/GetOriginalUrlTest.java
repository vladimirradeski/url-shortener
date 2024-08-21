package com.upwork.shorturl.domain.usecase;

import com.upwork.shorturl.domain.entity.ShortUrl;
import com.upwork.shorturl.domain.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOriginalUrlTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private GetOriginalUrl getOriginalUrl;

    @Test
    void shouldReturnOriginalUrlWhenNotExpired() {
        String slug = "slug";
        String originalUrl = "http://example.com";
        Instant now = Instant.now();
        ShortUrl shortUrl = ShortUrl.builder()
                .id("1")
                .originalUrl(originalUrl)
                .slug(slug)
                .createdAt(now.minusSeconds(20))
                .expiresAt(now.plusSeconds(20))
                .build();

        when(shortUrlRepository.findBySlug(slug)).thenReturn(Mono.just(shortUrl));

        StepVerifier.create(getOriginalUrl.execute(slug))
                .expectNext(originalUrl)
                .expectComplete()
                .verify();

        verify(shortUrlRepository, times(1)).findBySlug(slug);
    }

    @Test
    void shouldReturnEmptyWhenExpired() {
        String slug = "expiredSlug";
        Instant now = Instant.now();
        ShortUrl shortUrl = ShortUrl.builder()
                .id("1")
                .originalUrl("http://example.com")
                .slug(slug)
                .createdAt(now.minusSeconds(20))
                .expiresAt(now.minusSeconds(10))
                .build();

        when(shortUrlRepository.findBySlug(slug)).thenReturn(Mono.just(shortUrl));

        StepVerifier.create(getOriginalUrl.execute(slug))
                .expectComplete()
                .verify();

        verify(shortUrlRepository, times(1)).findBySlug(slug);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        String slug = "nonexistentSlug";

        when(shortUrlRepository.findBySlug(slug)).thenReturn(Mono.empty());

        StepVerifier.create(getOriginalUrl.execute(slug))
                .expectComplete()
                .verify();

        verify(shortUrlRepository, times(1)).findBySlug(slug);
    }
}
