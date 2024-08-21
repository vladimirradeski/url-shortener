package com.upwork.shorturl.domain.usecase;

import com.upwork.shorturl.domain.entity.exception.ShortenUrlException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ValidateAndSanitizeUrlTest {

    @InjectMocks
    private ValidateAndSanitizeUrl validateAndSanitizeUrl;


    @Test
    void shouldReturnNormalizedUrlForValidUrl() {
        String url = "http://example.com/somepath ";
        String expectedUrl = "http://example.com/somepath";

        StepVerifier.create(validateAndSanitizeUrl.execute(url))
                .expectNext(expectedUrl)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionForUrlExceedingMaxLength() {
        String longUrl = "http://" + "a".repeat(2048);

        StepVerifier.create(validateAndSanitizeUrl.execute(longUrl))
                .expectErrorMatches(e -> e instanceof ShortenUrlException &&
                        e.getMessage().equals("URL exceeds maximum length"))
                .verify();
    }

    @Test
    void shouldThrowExceptionForInvalidScheme() {
        String url = "ftp://example.com";

        StepVerifier.create(validateAndSanitizeUrl.execute(url))
                .expectErrorMatches(e -> e instanceof ShortenUrlException &&
                        e.getMessage().equals("Invalid URL scheme"))
                .verify();
    }

    @Test
    void shouldThrowExceptionForInvalidUrlSyntax() {
        String invalidUrl = "http://example.com/<>[]{}";

        StepVerifier.create(validateAndSanitizeUrl.execute(invalidUrl))
                .expectErrorMatches(e -> e instanceof ShortenUrlException &&
                        e.getMessage().equals("Invalid URL"))
                .verify();
    }
}
