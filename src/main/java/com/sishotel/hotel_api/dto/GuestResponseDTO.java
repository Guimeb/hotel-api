package com.sishotel.hotel_api.dto;

import com.sishotel.hotel_api.entity.Guest;
import lombok.Data;
import java.time.Instant;

@Data
public class GuestResponseDTO {
    private String id;
    private String fullName;
    private String document;
    private String email;
    private String phone;
    private Instant createdAt;

    public GuestResponseDTO(Guest guest) {
        this.id = guest.getId();
        this.fullName = guest.getFullName();
        this.document = guest.getDocument();
        this.email = guest.getEmail();
        this.phone = guest.getPhone();
        this.createdAt = guest.getCreatedAt();
    }
}