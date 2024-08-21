package com.upwork.shorturl.infrastructure.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ShortUrlReactiveMongoRepository extends ReactiveMongoRepository<ShortUrlModel, String> {

    Mono<ShortUrlModel> findBySlug(String slug);
}
