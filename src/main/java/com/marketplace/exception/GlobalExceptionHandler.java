package com.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ElementNotFound.class)
    public ResponseEntity<ApiErrorDefault> handleElementNotFoundException(ElementNotFound e) {
        ApiErrorDefault apiError = new ApiErrorDefault();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(DataBaseErrorException.class)
    public ResponseEntity<ApiErrorDefault> handleDataBaseErrorException(DataBaseErrorException e) {
        ApiErrorDefault apiError = new ApiErrorDefault();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(apiError);
    }

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<ApiErrorDefault> handleBadCredentialException(BadCredentialException e) {
        ApiErrorDefault apiError = new ApiErrorDefault();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }
}