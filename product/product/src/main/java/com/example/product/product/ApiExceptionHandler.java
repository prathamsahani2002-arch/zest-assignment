package com.example.product.product;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(ProductNotFoundException ex) { return response(HttpStatus.NOT_FOUND, ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>(response(HttpStatus.BAD_REQUEST, "Validation failed").getBody());
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> badRequest(Exception ex) { return response(HttpStatus.BAD_REQUEST, ex.getMessage()); }
    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) { return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now(), "status", status.value(), "error", status.getReasonPhrase(), "message", message == null ? "Request failed" : message)); }
}