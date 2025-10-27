package com.sishotel.hotel_api.dto;

import com.sishotel.hotel_api.entity.Room;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponseDTO {
    private String id;
    private Integer number;
    private String type;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private String status;

    // Construtor para mapear da Entidade para o DTO
    public RoomResponseDTO(Room room) {
        this.id = room.getId();
        this.number = room.getNumber();
        this.type = room.getType();
        this.capacity = room.getCapacity();
        this.pricePerNight = room.getPricePerNight();
        this.status = room.getStatus();
    }
}