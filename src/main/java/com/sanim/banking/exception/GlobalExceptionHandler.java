package com.sanim.banking.exception;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import com.sanim.banking.exception.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// A component whose @ExceptionHandler methods apply to every controller on the app, all end points covered
@RestControllerAdvice
public class GlobalExceptionHandler {
    // when any controller throws something of type ForbiddenException, call this method."
    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Forbidden", e.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Account not found", e.getMessage()));
    }
}