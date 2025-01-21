package com.narwal.assignment;

import com.narwal.assignment.dto.ApiResponse;
import com.narwal.assignment.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleRuntimeException_ShouldReturn500() {
        RuntimeException ex = new RuntimeException("Test runtime exception");
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleRuntimeException(ex);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody().getMessage());
    }

    @Test
    void handleGeneralException_ShouldReturn500() {
        Exception ex = new Exception("Test general exception");
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleGeneralException(ex);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody().getMessage());
    }
}