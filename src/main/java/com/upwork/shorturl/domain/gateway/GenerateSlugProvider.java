package com.upwork.shorturl.domain.gateway;

import reactor.core.publisher.Mono;

public interface GenerateSlugProvider {

    Mono<String> generate(String originalUrl);
}
