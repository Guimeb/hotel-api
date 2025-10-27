package com.sishotel.hotel_api.controller;

import com.sishotel.hotel_api.dto.RoomRequestDTO;
import com.sishotel.hotel_api.dto.RoomResponseDTO;
import com.sishotel.hotel_api.service.RoomService;
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
@RequestMapping("/api/rooms")
@Tag(name = "Quartos", description = "Endpoints para gerenciamento de Quartos")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    @Operation(summary = "Listar todos os quartos")
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAllRooms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar quarto por ID")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable String id) {
        return ResponseEntity.ok(roomService.findRoomById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo quarto")
    public ResponseEntity<RoomResponseDTO> createRoom(@Valid @RequestBody RoomRequestDTO roomDTO) {
        RoomResponseDTO newRoom = roomService.createRoom(roomDTO);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(newRoom.getId()).toUri();
                
        return ResponseEntity.created(location).body(newRoom);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um quarto existente")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable String id, @Valid @RequestBody RoomRequestDTO roomDTO) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar um quarto (não exclui fisicamente)")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}