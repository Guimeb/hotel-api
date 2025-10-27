package com.sishotel.hotel_api.controller.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record ApiError(
    Instant timestamp,
    Integer status,
    String error,
    String message,
    String path,
    List<String> validationErrors
) {
    // Construtor para erros gerais
    public ApiError(Integer status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path, Collections.emptyList());
    }
    
    // Construtor para erros de validação
    public ApiError(Integer status, String error, String message, String path, List<String> validationErrors) {
        this(Instant.now(), status, error, message, path, validationErrors);
    }
}