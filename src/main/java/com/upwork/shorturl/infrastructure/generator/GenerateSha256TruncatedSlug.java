package com.upwork.shorturl.infrastructure.generator;

import com.upwork.shorturl.domain.gateway.GenerateSlugProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class GenerateSha256TruncatedSlug implements GenerateSlugProvider {

    private final static int SLUG_LENGTH = 8;

    @Override
    public Mono<String> generate(String originalUrl) {
        return Mono.fromCallable(() -> {
            String uuid = UUID.randomUUID().toString();
            String urlWithUUID = originalUrl + ":" + uuid;
            byte[] hash = DigestUtils.sha256(urlWithUUID.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, SLUG_LENGTH);
        });
    }
}
