package com.guvaren.securityjwt.base;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResponseError(LocalDateTime timestamp, int status, String message, Object error) {

    public ResponseError(int status, String message, Object error) {
        this(LocalDateTime.now(), status, message, error);
    }
}
