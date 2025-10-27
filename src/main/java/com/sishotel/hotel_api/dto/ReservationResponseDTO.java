package com.sishotel.hotel_api.dto;

import com.sishotel.hotel_api.entity.Reservation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReservationResponseDTO {
    private String id;
    private String status;

    // Detalhes do Hóspede (simplificado)
    private GuestSummary guest;
    
    // Detalhes do Quarto (simplificado)
    private RoomSummary room;

    private LocalDate checkinExpected;
    private LocalDate checkoutExpected;
    private Instant checkinAt;
    private Instant checkoutAt;
    
    private BigDecimal estimatedAmount;
    private BigDecimal finalAmount;
    
    private Instant createdAt;
    
    // DTO aninhado para Hóspede
    @Data
    private static class GuestSummary {
        private String id;
        private String fullName;
        private String email;
        
        GuestSummary(com.sishotel.hotel_api.entity.Guest guest) {
            this.id = guest.getId();
            this.fullName = guest.getFullName();
            this.email = guest.getEmail();
        }
    }
    
    // DTO aninhado para Quarto
    @Data
    private static class RoomSummary {
        private String id;
        private Integer number;
        private String type;
        
        RoomSummary(com.sishotel.hotel_api.entity.Room room) {
            this.id = room.getId();
            this.number = room.getNumber();
            this.type = room.getType();
        }
    }

    // Construtor principal que mapeia a Entidade Reservation
    public ReservationResponseDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.status = reservation.getStatus().name();
        this.checkinExpected = reservation.getCheckinExpected();
        this.checkoutExpected = reservation.getCheckoutExpected();
        this.checkinAt = reservation.getCheckinAt();
        this.checkoutAt = reservation.getCheckoutAt();
        this.estimatedAmount = reservation.getEstimatedAmount();
        this.finalAmount = reservation.getFinalAmount();
        this.createdAt = reservation.getCreatedAt();
        
        // Mapeia os DTOs aninhados
        this.guest = new GuestSummary(reservation.getGuest());
        this.room = new RoomSummary(reservation.getRoom());
    }
}