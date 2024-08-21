package com.upwork.shorturl.infrastructure.data;

import com.upwork.shorturl.domain.entity.ShortUrl;
import com.upwork.shorturl.domain.repository.ShortUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ShortUrlReactiveRepositoryAdapter implements ShortUrlRepository {

    private final ShortUrlReactiveMongoRepository reactiveMongoRepository;

    public ShortUrlReactiveRepositoryAdapter(ShortUrlReactiveMongoRepository reactiveMongoRepository) {
        this.reactiveMongoRepository = reactiveMongoRepository;
    }

    @Override
    public Mono<ShortUrl> save(ShortUrl shortUrl) {
        return Mono.just(shortUrl)
                .doOnNext(r -> log.info("SAVING_SHORT_URL | data: {}", r))
                .map(ShortUrlModel::fromDomain)
                .flatMap(reactiveMongoRepository::save)
                .doOnSuccess(r -> log.info("SAVED_SHORT_URL | data: {}", r))
                .doOnError(e -> log.error("ERROR_SAVING_SHORT_URL | error: {}", e.getMessage()))
                .map(ShortUrlModel::toDomain);
    }

    @Override
    public Mono<ShortUrl> findBySlug(String slug) {
        return Mono.just(slug)
                .doOnNext(r -> log.info("RETRIEVE_SHORT_URL_BY_SLUG | originalUrl: {}", r))
                .flatMap(reactiveMongoRepository::findBySlug)
                .doOnSuccess(r -> log.info("RETRIEVED_SHORT_URL_BY_SLUG | data: {}", r))
                .doOnError(e -> log.info("ERROR_RETRIEVING_SHORT_URL_BY_SLUG | error: {}", e.getMessage()))
                .map(ShortUrlModel::toDomain);
    }
}
