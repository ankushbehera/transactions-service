package com.pismo.transactions.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.pismo.transactions.dto.response.Error;
import com.pismo.transactions.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        LOGGER.error("resource not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAccount(DuplicateAccountException ex) {
        LOGGER.error("duplicate account", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        "CONFLICT",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransaction(InvalidTransactionException ex) {
        LOGGER.error("bad request", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "BAD_REQUEST",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        LOGGER.error("Invalid request", ex);
        List<Error> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> Error.builder()
                        .message("VALIDATION_ERROR")
                        .details(f.getField() + " " + f.getDefaultMessage())
                        .build())
                .distinct()
                .collect(Collectors.toList());
        ErrorResponse response = errors.isEmpty()
                ? ErrorResponse.of("VALIDATION_ERROR", "Invalid request")
                : ErrorResponse.builder().errors(errors).build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJson(HttpMessageNotReadableException ex) {
        LOGGER.error("Invalid request body", ex);
        String message = "Invalid request body";

        if (ex.getCause() instanceof JsonMappingException mappingException) {
            var field = mappingException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (field != null) {
                message = MessageFormat.format( "Invalid value for field {0}", field);
            }
        }
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("BAD_REQUEST",  message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        LOGGER.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        "INTERNAL_SERVER_ERROR",
                        ex.getMessage()
                ));
    }
}
