package com.couriersync.login.login_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("valid", false);
        body.put("error", "Authorization header is required");
        return ResponseEntity.badRequest().body(body);
    }
}
