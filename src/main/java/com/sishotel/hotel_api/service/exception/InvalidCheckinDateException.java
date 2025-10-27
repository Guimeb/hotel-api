package com.sishotel.hotel_api.service.exception;

// REGRA 5: Janela de check-in (fora do dia)
// O requisito pede 422 Unprocessable Entity (Opcional)
public class InvalidCheckinDateException extends RuntimeException {
    public InvalidCheckinDateException(String message) {
        super(message);
    }
}