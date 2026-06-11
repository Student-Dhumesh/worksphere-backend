package com.worksphere.backend.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    Helper Function
    private ResponseEntity<Map<String, String>> build(HttpStatus status, String code, String message) {
        return ResponseEntity
                .status(status)
                .body(
                        Map.of(
                                "code", code,
                                "message", message
                        )
                );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException exception) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExists(EmailAlreadyExistsException exception) {
        return build(HttpStatus.CONFLICT, "EMAIL_EXISTS", exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExists(AlreadyExistsException exception) {
        return build(HttpStatus.CONFLICT, "ALREADY_EXISTS", exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException exception) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException exception) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage());
    }

}
