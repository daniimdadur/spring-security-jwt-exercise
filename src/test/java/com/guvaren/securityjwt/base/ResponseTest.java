package com.guvaren.securityjwt.base;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void success_shouldReturnStatus200WithMessageSuccess() {
        Response<String> response = Response.success("test-data");
        assertEquals(200, response.status());
        assertEquals("Success", response.message());
        assertEquals("test-data", response.data());
    }

    @Test
    void created_shouldReturnStatus201WithCreatedMessage() {
        Response<String> response = Response.created("test-data");
        assertEquals(201, response.status());
        assertEquals("Created", response.message());
        assertEquals("test-data", response.data());
    }

    @Test
    void updated_shouldReturnStatus200WithUpdatedMessage() {
        Response<String> response = Response.updated("test-data");
        assertEquals(200, response.status());
        assertEquals("Updated", response.message());
        assertEquals("test-data", response.data());
    }

    @Test
    void deleted_shouldReturnStatus200WithDeletedMessage() {
        Response<String> response = Response.deleted("test-data");
        assertEquals(200, response.status());
        assertEquals("Deleted", response.message());
        assertEquals("test-data", response.data());
    }

    @Test
    void custom_shouldReturnCustomStatusAndMessage() {
        Response<String> response = Response.custom(HttpStatus.ACCEPTED, "Custom Message", "data");
        assertEquals(202, response.status());
        assertEquals("Custom Message", response.message());
        assertEquals("data", response.data());
    }

    @Test
    void success_shouldHandleNullData() {
        Response<String> response = Response.success(null);
        assertEquals(200, response.status());
        assertNull(response.data());
    }

    @Test
    void success_shouldHandleIntegerData() {
        Response<Integer> response = Response.success(42);
        assertEquals(200, response.status());
        assertEquals(42, response.data());
    }
}
