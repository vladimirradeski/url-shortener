package com.upwork.shorturl.api;

import com.upwork.shorturl.domain.usecase.GetOriginalUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

@ExtendWith(MockitoExtension.class)
class ExpandUrlControllerTest {

    @Mock
    private GetOriginalUrl getOriginalUrl;

    @InjectMocks
    private ExpandUrlController expandUrlController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = bindToController(expandUrlController).build();
    }

    @Test
    void shouldRedirectToOriginalUrl() {
        String slug = "abc123";
        String originalUrl = "http://example.com";

        when(getOriginalUrl.execute(slug)).thenReturn(Mono.just(originalUrl));

        webTestClient.get()
                .uri("/{slug}", slug)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.PERMANENT_REDIRECT)
                .expectHeader().location(originalUrl)
                .expectBody().isEmpty();

        verify(getOriginalUrl, times(1)).execute(slug);
    }

    @Test
    void shouldReturnNotFoundForNonExistentSlug() {
        String slug = "nonexistent";

        when(getOriginalUrl.execute(slug)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/{slug}", slug)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody().isEmpty();

        verify(getOriginalUrl, times(1)).execute(slug);
    }
}
