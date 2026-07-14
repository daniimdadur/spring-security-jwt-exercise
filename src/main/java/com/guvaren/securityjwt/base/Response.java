package com.guvaren.securityjwt.base;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record Response<T>(int status, String message, T data) {
    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> Response<T> success(T data, String message) {
        return Response.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> created(T data) {
        return Response.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message("Created successfully")
                .data(data)
                .build();
    }

    public static <T> Response<T> created(T data, String message) {
        return Response.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> ok(String message) {
        return Response.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(null)
                .build();
    }
}
