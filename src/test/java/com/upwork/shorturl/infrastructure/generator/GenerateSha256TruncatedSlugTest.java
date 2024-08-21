package com.upwork.shorturl.infrastructure.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GenerateSha256TruncatedSlugTest {

    private final GenerateSha256TruncatedSlug generator = new GenerateSha256TruncatedSlug();

    @Test
    public void shouldGenerateSlug() {
        String originalUrl = "http://example.com";

        Mono<String> slugMono = generator.generate(originalUrl);

        StepVerifier.create(slugMono)
                .assertNext(slug -> {
                    assertEquals(8, slug.length());
                    assertTrue(Pattern.matches("^[a-zA-Z0-9_-]*$", slug));
                })
                .verifyComplete();
    }
}