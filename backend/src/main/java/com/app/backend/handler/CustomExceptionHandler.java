package com.app.backend.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, String> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Forbidden");
        return errorResponse;
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, String> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Unauthorized");
        return errorResponse;
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, String> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Not Found");
        return errorResponse;
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Bad Request");
        return errorResponse;
    }

    // Ajoutez ici d'autres méthodes pour gérer d'autres types d'exceptions

}