package com.hotelBooking.airBnB.handler;

import com.hotelBooking.airBnB.dto.ApiError;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        ApiError apiError = ApiError.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = ApiError.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {

        System.out.println("Exception got raised::: "+ ex.getStackTrace());

        ApiError apiError = ApiError.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
