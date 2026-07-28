package com.guvaren.securityjwt.exception;

import com.guvaren.securityjwt.base.ResponseError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.security.sasl.AuthenticationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleBadRequestException_shouldReturn400() {
        BadRequestException ex = new BadRequestException("Bad request");
        ResponseEntity<ResponseError> response = exceptionHandler.handleBadRequestException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().message());
        assertEquals("Bad request", response.getBody().error());
    }

    @Test
    void handleNotFoundException_shouldReturn404() {
        NotFoundException ex = new NotFoundException("Resource not found");
        ResponseEntity<ResponseError> response = exceptionHandler.handleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().message());
        assertEquals("Resource not found", response.getBody().error());
    }

    @Test
    void handleDuplicateException_shouldReturn409() {
        DuplicateException ex = new DuplicateException("Duplicate entry");
        ResponseEntity<ResponseError> response = exceptionHandler.handleDuplicateException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().message());
        assertEquals("Duplicate entry", response.getBody().error());
    }

    @Test
    void handleAuthenticationException_shouldReturn401() {
        AuthenticationException ex = new AuthenticationException("Unauthorized") {};
        ResponseEntity<ResponseError> response = exceptionHandler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().message());
        assertEquals("Unauthorized", response.getBody().error());
    }

    @Test
    void handleAccessDeniedException_shouldReturn403() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");
        ResponseEntity<ResponseError> response = exceptionHandler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().status());
        assertEquals("Forbidden", response.getBody().message());
        assertEquals("Forbidden", response.getBody().error());
    }

    @Test
    void handleDataAccessException_shouldReturn500() {
        DataAccessException ex = mock(DataAccessException.class);
        when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("DB connection failed"));

        ResponseEntity<ResponseError> response = exceptionHandler.handleDataAccessException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().status());
        assertEquals("DB connection failed", response.getBody().error());
    }

    @Test
    void handleException_shouldReturn500() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<ResponseError> response = exceptionHandler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().status());
        assertEquals("Unexpected error", response.getBody().error());
    }

    @Test
    void methodArgumentNotValidException_shouldReturn400WithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("req", "email", "must not be blank");
        FieldError fieldError2 = new FieldError("req", "password", "must not be empty");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<ResponseError> response = exceptionHandler.methodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertNotNull(response.getBody().error());
    }

    @Test
    void handleBadRequestException_shouldReturnCorrectMessage() {
        BadRequestException ex = new BadRequestException("One or more specified roles do not exist");
        ResponseEntity<ResponseError> response = exceptionHandler.handleBadRequestException(ex);

        assertEquals("One or more specified roles do not exist", response.getBody().error());
    }
}
