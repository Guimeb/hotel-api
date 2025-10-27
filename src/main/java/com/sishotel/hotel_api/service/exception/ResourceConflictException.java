package com.sishotel.hotel_api.service.exception;

// Esta exceção será usada para conflitos (ex: número de quarto duplicado)
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}