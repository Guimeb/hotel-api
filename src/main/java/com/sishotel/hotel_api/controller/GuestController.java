package com.sishotel.hotel_api.controller;

import com.sishotel.hotel_api.dto.GuestRequestDTO;
import com.sishotel.hotel_api.dto.GuestResponseDTO;
import com.sishotel.hotel_api.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
@Tag(name = "Hóspedes", description = "Endpoints para gerenciamento de Hóspedes")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping
    @Operation(summary = "Listar todos os hóspedes")
    public ResponseEntity<List<GuestResponseDTO>> getAllGuests() {
        return ResponseEntity.ok(guestService.findAllGuests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar hóspede por ID")
    public ResponseEntity<GuestResponseDTO> getGuestById(@PathVariable String id) {
        return ResponseEntity.ok(guestService.findGuestById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo hóspede")
    public ResponseEntity<GuestResponseDTO> createGuest(@Valid @RequestBody GuestRequestDTO guestDTO) {
        GuestResponseDTO newGuest = guestService.createGuest(guestDTO);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(newGuest.getId()).toUri();
                
        return ResponseEntity.created(location).body(newGuest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um hóspede existente")
    public ResponseEntity<GuestResponseDTO> updateGuest(@PathVariable String id, @Valid @RequestBody GuestRequestDTO guestDTO) {
        return ResponseEntity.ok(guestService.updateGuest(id, guestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um hóspede")
    public ResponseEntity<Void> deleteGuest(@PathVariable String id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}