package com.sishotel.hotel_api.service.exception;

// Para a Regra de Negócio 1 (Datas inválidas)
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}