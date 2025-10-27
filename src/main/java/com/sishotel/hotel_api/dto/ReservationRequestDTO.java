package com.sishotel.hotel_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequestDTO {

    @NotBlank(message = "O ID do hóspede (guestId) é obrigatório")
    private String guestId;

    @NotBlank(message = "O ID do quarto (roomId) é obrigatório")
    private String roomId;

    @NotNull(message = "A data de check-in é obrigatória")
    @FutureOrPresent(message = "A data de check-in deve ser hoje ou no futuro")
    private LocalDate checkinExpected;

    @NotNull(message = "A data de check-out é obrigatória")
    @FutureOrPresent(message = "A data de check-out deve ser hoje ou no futuro")
    private LocalDate checkoutExpected;

    @NotNull(message = "O número de hóspedes é obrigatório")
    @Min(value = 1, message = "Deve haver pelo menos 1 hóspede")
    private Integer guestCount; // Usado para a Regra de Capacidade
}