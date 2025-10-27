package com.sishotel.hotel_api.service.exception;

// Esta exceção será usada quando um recurso (quarto, hóspede) não for encontrado
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}