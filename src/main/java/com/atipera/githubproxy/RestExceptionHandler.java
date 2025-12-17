package com.atipera.githubproxy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
class RestExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubUserNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleUserNotFound(GithubUserNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 404);
        body.put("message", ex.getMessage());
        return ResponseEntity.status(404).body(body);
    }
}