package com.upwork.shorturl.infrastructure.data;

import com.upwork.shorturl.domain.entity.ShortUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@DataMongoTest
@AutoConfigureWebFlux
@Import(
        value = {
                ShortUrlReactiveRepositoryAdapter.class
        }
)
@ActiveProfiles("test")
class ShortUrlReactiveRepositoryAdapterTest {

    @Autowired
    private ShortUrlReactiveMongoRepository reactiveMongoRepository;

    @Autowired
    private ShortUrlReactiveRepositoryAdapter adapter;


    @Test
    public void testSaveAndFindBySlug() {
        String url = "http://example.com";
        String slug = "short";
        int expirationDays = 365;
        Instant expirationDate = Instant.now().plus(expirationDays, ChronoUnit.DAYS);
        ShortUrl shortUrl = ShortUrl.builder()
                .slug(slug)
                .originalUrl(url)
                .expiresAt(expirationDate)
                .createdAt(Instant.now())
                .build();

        adapter.save(shortUrl).block();

        StepVerifier.create(adapter.findBySlug(slug))
                .expectNextMatches(result -> result.slug().equals(slug))
                .verifyComplete();
    }

}