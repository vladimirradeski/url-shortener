package com.upwork.shorturl.infrastructure.data;

import com.upwork.shorturl.domain.entity.ShortUrl;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Data
@ToString
@Document(collection = "shortUrls")
public class ShortUrlModel {
    @Id
    private String id;
    private String originalUrl;
    @Indexed(unique = true)
    private String slug;
    private Instant createdAt;
    private Instant expiresAt;

    public ShortUrl toDomain() {
        return ShortUrl.builder()
                .id(this.id)
                .slug(this.slug)
                .originalUrl(this.originalUrl)
                .expiresAt(this.expiresAt)
                .createdAt(this.createdAt)
                .build();
    }

    public static ShortUrlModel fromDomain(ShortUrl domain) {
        return ShortUrlModel.builder()
                .id(domain.id())
                .slug(domain.slug())
                .originalUrl(domain.originalUrl())
                .expiresAt(domain.expiresAt())
                .createdAt(domain.createdAt())
                .build();
    }

}
