package com.upwork.shorturl.api;

import com.upwork.shorturl.api.dto.ShortenUrlRequest;
import com.upwork.shorturl.domain.usecase.ShortenUrl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ShortenUrlController {

    private final ShortenUrl shortenUrl;

    public ShortenUrlController(ShortenUrl shortenUrl) {
        this.shortenUrl = shortenUrl;
    }

    @PostMapping("/shorten")
    public Mono<ResponseEntity<String>> shortenUrl(@RequestBody ShortenUrlRequest request) {
        return shortenUrl.execute(request.getUrl())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .onErrorResume(
                        ex -> Mono.just(new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST))
                );
    }
}
