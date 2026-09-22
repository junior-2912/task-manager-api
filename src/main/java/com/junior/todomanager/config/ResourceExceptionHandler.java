package com.junior.todomanager.config;

import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        StandardError error = new StandardError();

        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError("Not found");
        error.setMessage(e.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
