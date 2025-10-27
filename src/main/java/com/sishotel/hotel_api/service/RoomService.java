package com.sishotel.hotel_api.service;

import com.sishotel.hotel_api.dto.RoomRequestDTO;
import com.sishotel.hotel_api.dto.RoomResponseDTO;
import com.sishotel.hotel_api.entity.Room;
import com.sishotel.hotel_api.repository.RoomRepository;
import com.sishotel.hotel_api.service.exception.ResourceConflictException;
import com.sishotel.hotel_api.service.exception.ResourceNotFoundException;
import com.sishotel.hotel_api.repository.ReservationRepository; 
import com.sishotel.hotel_api.entity.enums.ReservationStatus;
import java.util.Arrays; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;

    // Lista todos os quartos cadastrados.
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Busca um quarto específico pelo seu ID.
    @Transactional(readOnly = true)
    public RoomResponseDTO findRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado com ID: " + id));
        return new RoomResponseDTO(room);
    }

    // Cadastra um novo quarto, validando se o número já existe.
    @Transactional
    public RoomResponseDTO createRoom(RoomRequestDTO roomDTO) {
        // Regra de Negócio: Valida se o número do quarto é único
        roomRepository.findByNumber(roomDTO.getNumber())
            .ifPresent(room -> {
                throw new ResourceConflictException("O número de quarto " + roomDTO.getNumber() + " já está cadastrado.");
            });

        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        mapDtoToEntity(roomDTO, room);

        Room savedRoom = roomRepository.save(room);
        return new RoomResponseDTO(savedRoom);
    }

    // Atualiza um quarto existente, validando a unicidade do número se alterado.
    @Transactional
    public RoomResponseDTO updateRoom(String id, RoomRequestDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado com ID: " + id));

        // Regra de Negócio: Valida número único apenas se ele foi alterado
        if (!room.getNumber().equals(roomDTO.getNumber())) {
            roomRepository.findByNumber(roomDTO.getNumber())
                .ifPresent(existingRoom -> {
                    throw new ResourceConflictException("O número de quarto " + roomDTO.getNumber() + " já está cadastrado.");
                });
        }

        mapDtoToEntity(roomDTO, room);
        Room updatedRoom = roomRepository.save(room);
        return new RoomResponseDTO(updatedRoom);
    }

    // Regra de Negócio 7: Impede a inativação se o quarto possuir reservas ativas
    @Transactional
    public void deleteRoom(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado com ID: " + id));

        // Regra de Negócio: Verifica se existem reservas CREATED ou CHECKED_IN
        List<ReservationStatus> activeStatuses = Arrays.asList(
            ReservationStatus.CREATED, 
            ReservationStatus.CHECKED_IN
        );
        
        if (reservationRepository.existsByRoomIdAndStatusIn(room.getId(), activeStatuses)) {
           throw new ResourceConflictException(
               "Quarto não pode ser inativado pois possui reservas ativas (CREATED ou CHECKED_IN)."
           );
        }

        // Altera o status para INATIVO ao invés de deletar
        room.setStatus("INATIVO");
        roomRepository.save(room);
    }

    // Método utilitário para mapear dados do DTO para a Entidade.
    private void mapDtoToEntity(RoomRequestDTO dto, Room entity) {
        entity.setNumber(dto.getNumber());
        entity.setType(dto.getType());
        entity.setCapacity(dto.getCapacity());
        entity.setPricePerNight(dto.getPricePerNight());
        entity.setStatus(dto.getStatus());
    }
}