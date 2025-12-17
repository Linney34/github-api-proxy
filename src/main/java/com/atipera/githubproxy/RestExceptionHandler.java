package com.atipera.githubproxy;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
class RestExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubUserNotFoundException.class)
    Map<String, Object> handleUserNotFound() {
        return Map.of(
                "status", 404,
                "message", "User not found"
        );
    }
}