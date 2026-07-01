package com.guvaren.securityjwt.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class BaseController<T> {
    private ResponseEntity<Response> buildResponse(HttpStatus status, Object data) {
        return ResponseEntity.status(status).body(
                Response.builder()
                        .status(status.value())
                        .message(status.getReasonPhrase())
                        .data(data)
                        .build()
        );
    }

    public ResponseEntity<Response> getResponse(List<T> result) {
        return buildResponse(HttpStatus.OK, result);
    }

    public ResponseEntity<Response> getResponse(Optional<T> result) {
        return result.map(t -> buildResponse(HttpStatus.OK, t))
                .orElseGet(() -> buildResponse(HttpStatus.NOT_FOUND, null));
    }

    public ResponseEntity<Response> getResponse(T result) {
        if (result != null) {
            return buildResponse(HttpStatus.OK, result);
        }
        return buildResponse(HttpStatus.BAD_REQUEST, null);
    }
}