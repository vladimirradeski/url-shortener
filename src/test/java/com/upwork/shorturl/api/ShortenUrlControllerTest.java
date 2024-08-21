package com.upwork.shorturl.api;

import com.upwork.shorturl.api.dto.ShortenUrlRequest;
import com.upwork.shorturl.domain.entity.exception.ShortenUrlException;
import com.upwork.shorturl.domain.usecase.ShortenUrl;
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

@ExtendWith(MockitoExtension.class)
class ShortenUrlControllerTest {

    @Mock
    private ShortenUrl shortenUrl;

    @InjectMocks
    private ShortenUrlController shortenUrlController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(shortenUrlController).build();
    }

    @Test
    void shouldReturnShortenedUrl() {
        String originalUrl = "http://example.com";
        String shortenedUrl = "http://short.url/abc123";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl(originalUrl);

        when(shortenUrl.execute(originalUrl)).thenReturn(Mono.just(shortenedUrl));

        webTestClient.post()
                .uri("/shorten")
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(String.class).isEqualTo(shortenedUrl);

        verify(shortenUrl, times(1)).execute(originalUrl);
    }

    @Test
    void shouldReturnBadRequestOnError() {
        String originalUrl = "http://example.com";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl(originalUrl);
        String errorMessage = "Shortening failed";

        when(shortenUrl.execute(originalUrl)).thenReturn(Mono.error(new ShortenUrlException(errorMessage)));

        webTestClient.post()
                .uri("/shorten")
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class).isEqualTo(errorMessage);

        verify(shortenUrl, times(1)).execute(originalUrl);
    }
}
