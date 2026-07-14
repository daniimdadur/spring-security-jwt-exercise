package com.guvaren.securityjwt.base;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ResponseError(LocalDateTime timestamp, int status, String message, Object error) {

    public ResponseError(int status, String message, Object error) {
        this(LocalDateTime.now(), status, message, error);
    }

    public static ResponseError notFound(String message) {
        return new ResponseError(
                HttpStatus.NOT_FOUND.value(),
                message,
                null
        );
    }

    public static ResponseError badRequest(String message, Object errors) {
        return new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                message,
                errors
        );
    }

    public static ResponseError conflict(String message) {
        return new ResponseError(
                HttpStatus.CONFLICT.value(),
                message,
                null
        );
    }

    public static ResponseError internalServerError(String message) {
        return new ResponseError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                null
        );
    }

    public static ResponseError unauthorized(String message) {
        return new ResponseError(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                null
        );
    }

    public static ResponseError forbidden(String message) {
        return new ResponseError(
                HttpStatus.FORBIDDEN.value(),
                message,
                null
        );
    }

    public static ResponseError of(HttpStatus status, String message, Object errors) {
        return new ResponseError(
                status.value(),
                message,
                errors
        );
    }
}
