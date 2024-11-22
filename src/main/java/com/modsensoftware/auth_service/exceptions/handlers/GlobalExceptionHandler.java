package com.modsensoftware.auth_service.exceptions.handlers;

import com.modsensoftware.auth_service.exceptions.responses.ExceptionResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> handleDataAccessException(DataAccessException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                "Database Error An error occurred while accessing the database: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((exception) -> {
            String fieldName = ((FieldError) exception).getField();
            String errorMessage = exception.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(RuntimeException runtimeException){
        return createResponseEntity(runtimeException);
    }

    private ResponseEntity<ExceptionResponse> createResponseEntity(RuntimeException runtimeException) {
        HttpStatus status = getResponseStatus(runtimeException);
        ExceptionResponse exceptionResponse = new ExceptionResponse(runtimeException.getMessage());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    private HttpStatus getResponseStatus(RuntimeException runtimeException) {
        ResponseStatus responseStatus = runtimeException.getClass().getAnnotation(ResponseStatus.class);
        return responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}