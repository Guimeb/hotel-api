package com.sishotel.hotel_api.controller;

import com.sishotel.hotel_api.dto.ReservationRequestDTO;
import com.sishotel.hotel_api.dto.ReservationResponseDTO;
import com.sishotel.hotel_api.service.ReservationService;
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
@RequestMapping("/api/reservations")
@Tag(name = "Reservas", description = "Endpoints para gerenciamento de Reservas, Check-in e Check-out")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Criar uma nova reserva",
             description = "Valida datas, capacidade e disponibilidade do quarto (Regras 1, 2, 3)")
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO reservationDTO) {
        ReservationResponseDTO newReservation = reservationService.createReservation(reservationDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(newReservation.getId()).toUri();

        return ResponseEntity.created(location).body(newReservation);
    }

    @GetMapping
    @Operation(summary = "Listar todas as reservas")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @PostMapping("/{id}/checkin")
    @Operation(summary = "Realizar Check-in (Fluxo 3)",
             description = "Altera o status para CHECKED_IN. Valida status (Regra 4) e janela de check-in (Regra 5).")
    public ResponseEntity<ReservationResponseDTO> performCheckIn(@PathVariable String id) {
        ReservationResponseDTO reservation = reservationService.performCheckIn(id);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/{id}/checkout")
    @Operation(summary = "Realizar Check-out (Fluxo 4)",
             description = "Altera status para CHECKED_OUT. Valida status (Regra 4) e calcula valor final (Regra 6).")
    public ResponseEntity<ReservationResponseDTO> performCheckOut(@PathVariable String id) {
        ReservationResponseDTO reservation = reservationService.performCheckOut(id);
        return ResponseEntity.ok(reservation);
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar uma reserva (Fluxo 5)",
             description = "Altera status para CANCELED. Permitido somente se o status for CREATED (Regra 4).")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}