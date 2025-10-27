package com.sishotel.hotel_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true)
    private Integer number; // Número do quarto (ex: 101, 202)

    @Column(nullable = false, length = 20)
    private String type; // STANDARD, DELUXE, SUITE

    @Column(nullable = false)
    private Integer capacity; // Nº de hóspedes

    @Column(nullable = false, name = "price_per_night", precision = 10, scale = 2)
    private BigDecimal pricePerNight; // Preço da diária

    @Column(nullable = false, length = 20)
    private String status; // ATIVO, INATIVO
}