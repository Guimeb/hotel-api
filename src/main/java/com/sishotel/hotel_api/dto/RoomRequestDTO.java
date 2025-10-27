package com.sishotel.hotel_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequestDTO {

    @NotNull(message = "O número do quarto é obrigatório")
    @Min(value = 1, message = "O número do quarto deve ser positivo")
    private Integer number;

    @NotBlank(message = "O tipo é obrigatório (ex: STANDARD, DELUXE, SUITE)")
    private String type;

    @NotNull(message = "A capacidade é obrigatória")
    @Min(value = 1, message = "A capacidade deve ser de no mínimo 1")
    private Integer capacity;

    @NotNull(message = "O preço por noite é obrigatório")
    @Positive(message = "O preço deve ser positivo")
    private BigDecimal pricePerNight;

    @NotBlank(message = "O status é obrigatório (ex: ATIVO, INATIVO)")
    private String status;
}