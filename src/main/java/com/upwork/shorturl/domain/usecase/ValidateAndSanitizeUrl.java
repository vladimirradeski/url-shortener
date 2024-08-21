package com.upwork.shorturl.domain.usecase;

import com.upwork.shorturl.domain.entity.exception.ShortenUrlException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ValidateAndSanitizeUrl {

    private static final int MAX_URL_LENGTH = 2048;

    public Mono<String> execute(String url) {
        return Mono.just(url)
                .map(String::trim)
                .map(this::processUrl);
    }

    private String processUrl(String url) {
        if (url.length() > MAX_URL_LENGTH) {
            throw new ShortenUrlException("URL exceeds maximum length");
        }

        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if ((!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new ShortenUrlException("Invalid URL scheme");
            }

            URI normalizedUri = uri.normalize();
            return normalizedUri.toString();
        } catch (URISyntaxException e) {
            throw new ShortenUrlException("Invalid URL", e);
        }
    }
}
