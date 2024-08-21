package com.upwork.shorturl.domain.entity.exception;

public class ShortenUrlException extends RuntimeException {
    public ShortenUrlException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public ShortenUrlException(String errorMessage) {
        super(errorMessage);
    }
}
