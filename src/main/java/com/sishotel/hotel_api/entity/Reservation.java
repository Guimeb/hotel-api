package com.sishotel.hotel_api.entity;

import com.sishotel.hotel_api.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @Column(length = 36)
    private String id;

    // Relacionamento: Muitas reservas para UM hóspede
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = só carregar do banco quando for usado
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    // Relacionamento: Muitas reservas para UM quarto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, name = "checkin_expected")
    private LocalDate checkinExpected;

    @Column(nullable = false, name = "checkout_expected")
    private LocalDate checkoutExpected;

    @Column(name = "checkin_at")
    private Instant checkinAt; // TIMESTAMP

    @Column(name = "checkout_at")
    private Instant checkoutAt; // TIMESTAMP

    @Enumerated(EnumType.STRING) // Grava "CREATED", "CHECKED_IN", etc. no VARCHAR(20)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "estimated_amount", precision = 10, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(name = "final_amount", precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}