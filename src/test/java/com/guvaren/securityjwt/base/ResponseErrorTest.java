package com.guvaren.securityjwt.base;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ResponseErrorTest {

    @Test
    void of_shouldCreateResponseErrorWithCorrectFields() {
        ResponseError error = ResponseError.of(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid input");

        assertEquals(400, error.status());
        assertEquals("Bad Request", error.message());
        assertEquals("Invalid input", error.error());
        assertNotNull(error.timestamp());
    }

    @Test
    void of_shouldHandleNullError() {
        ResponseError error = ResponseError.of(HttpStatus.NOT_FOUND, "Not Found", null);

        assertEquals(404, error.status());
        assertEquals("Not Found", error.message());
        assertNull(error.error());
    }

    @Test
    void constructor_shouldSetTimestampToNow() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        ResponseError error = ResponseError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error", "details");
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(error.timestamp());
        assertFalse(error.timestamp().isBefore(before), "Timestamp should not be before test start");
        assertFalse(error.timestamp().isAfter(after), "Timestamp should not be after test end");
    }

    @Test
    void of_shouldHandleListErrors() {
        var errors = java.util.List.of("error1", "error2");
        ResponseError responseError = ResponseError.of(HttpStatus.BAD_REQUEST, "Validation Failed", errors);

        assertEquals(400, responseError.status());
        assertNotNull(responseError.error());
    }

    @Test
    void of_shouldHandleAllHttpStatusCodes() {
        ResponseError error401 = ResponseError.of(HttpStatus.UNAUTHORIZED, "Unauthorized", "msg");
        assertEquals(401, error401.status());

        ResponseError error403 = ResponseError.of(HttpStatus.FORBIDDEN, "Forbidden", "msg");
        assertEquals(403, error403.status());

        ResponseError error500 = ResponseError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error", "msg");
        assertEquals(500, error500.status());
    }
}
