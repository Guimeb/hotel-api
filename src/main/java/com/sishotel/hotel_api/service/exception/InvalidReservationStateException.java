package com.sishotel.hotel_api.service.exception;

// Regra de Negócio 4: Violação da FSM (ex: tentar cancelar após check-in).
// O requisito pede 409 Conflict.
public class InvalidReservationStateException extends ResourceConflictException {
    public InvalidReservationStateException(String message) {
        super(message); // Reutiliza a lógica do 409 Conflict
    }
}