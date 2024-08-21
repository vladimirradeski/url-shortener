package com.upwork.shorturl.domain.usecase.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ShortUrlProperties {

    @Value("${shorturl.expiration.days}")
    private int expirationDays;

    @Value("${shorturl.slug.collision.max.attempts}")
    private int slugCollisionMaxAttempts;
}
