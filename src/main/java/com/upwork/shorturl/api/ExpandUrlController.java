package com.upwork.shorturl.api;

import com.upwork.shorturl.domain.usecase.GetOriginalUrl;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class ExpandUrlController {

    private final GetOriginalUrl getOriginalUrl;

    public ExpandUrlController(GetOriginalUrl getOriginalUrl) {
        this.getOriginalUrl = getOriginalUrl;
    }

    @GetMapping("/{slug}")
    public Mono<Void> expandUrl(ServerHttpResponse response, @PathVariable String slug) {
        return getOriginalUrl.execute(slug)
                .flatMap(originalUrl -> setRedirectResponse(response, originalUrl))
                .switchIfEmpty(setNotFoundResponse(response))
                .flatMap(ServerHttpResponse::setComplete);
    }

    private Mono<ServerHttpResponse> setRedirectResponse(ServerHttpResponse response, String originalUrl) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create(originalUrl));
        return Mono.just(response);
    }

    private Mono<ServerHttpResponse> setNotFoundResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        return Mono.just(response);
    }

}
