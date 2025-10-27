package com.sishotel.hotel_api.service.exception;

// Para a Regra de Negócio 3 (Capacidade)
public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(String message) {
        super(message);
    }
}