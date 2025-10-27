package com.sishotel.hotel_api.service;

import com.sishotel.hotel_api.dto.GuestRequestDTO;
import com.sishotel.hotel_api.dto.GuestResponseDTO;
import com.sishotel.hotel_api.entity.Guest;
import com.sishotel.hotel_api.repository.GuestRepository;
import com.sishotel.hotel_api.service.exception.ResourceNotFoundException;
import com.sishotel.hotel_api.service.exception.ResourceConflictException; 
// Imports para a Regra 7
import com.sishotel.hotel_api.repository.ReservationRepository; 
import com.sishotel.hotel_api.entity.enums.ReservationStatus;
import java.util.Arrays; 
// ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    // Injeção para a Regra 7
    @Autowired
    private ReservationRepository reservationRepository;

    // Lista todos os hóspedes cadastrados.
    @Transactional(readOnly = true)
    public List<GuestResponseDTO> findAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        
        return guests.stream()
                .map(GuestResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Busca um hóspede específico pelo seu ID.
    @Transactional(readOnly = true)
    public GuestResponseDTO findGuestById(String id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóspede não encontrado com ID: " + id));
        return new GuestResponseDTO(guest);
    }

    // Cadastra um novo hóspede, validando documento e e-mail.
    @Transactional
    public GuestResponseDTO createGuest(GuestRequestDTO guestDTO) {
        
        // Regra de Negócio: Valida se o documento é único
        guestRepository.findByDocument(guestDTO.getDocument())
            .ifPresent(g -> {
                throw new ResourceConflictException("Documento " + guestDTO.getDocument() + " já cadastrado.");
            });
        
        // Regra de Negócio: Valida se o e-mail é único
        guestRepository.findByEmail(guestDTO.getEmail())
            .ifPresent(g -> {
                throw new ResourceConflictException("E-mail " + guestDTO.getEmail() + " já cadastrado.");
            });

        Guest guest = new Guest();
        guest.setId(UUID.randomUUID().toString()); 
        
        guest.setFullName(guestDTO.getFullName());
        guest.setDocument(guestDTO.getDocument());
        guest.setEmail(guestDTO.getEmail());
        guest.setPhone(guestDTO.getPhone());
        
        Guest savedGuest = guestRepository.save(guest);
        
        return new GuestResponseDTO(savedGuest);
    }

    // Atualiza um hóspede existente, validando documento e e-mail se alterados.
    @Transactional
    public GuestResponseDTO updateGuest(String id, GuestRequestDTO guestDTO) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóspede não encontrado com ID: " + id));

        // Regra de Negócio: Valida documento único apenas se ele foi alterado
        if (!guest.getDocument().equals(guestDTO.getDocument())) {
            guestRepository.findByDocument(guestDTO.getDocument())
                .ifPresent(g -> {
                    throw new ResourceConflictException("Documento " + guestDTO.getDocument() + " já cadastrado.");
                });
        }
        
        // Regra de Negócio: Valida e-mail único apenas se ele foi alterado
        if (!guest.getEmail().equals(guestDTO.getEmail())) {
            guestRepository.findByEmail(guestDTO.getEmail())
                .ifPresent(g -> {
                    throw new ResourceConflictException("E-mail " + guestDTO.getEmail() + " já cadastrado.");
                });
        }

        guest.setFullName(guestDTO.getFullName());
        guest.setDocument(guestDTO.getDocument());
        guest.setEmail(guestDTO.getEmail());
        guest.setPhone(guestDTO.getPhone());
        
        Guest updatedGuest = guestRepository.save(guest);
        
        return new GuestResponseDTO(updatedGuest);
    }

    // Regra de Negócio 7: Exclui um hóspede, se não houver reservas ativas.
    @Transactional
    public void deleteGuest(String id) {
        // Primeiro, verifica se o hóspede existe
        if (!guestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hóspede não encontrado com ID: " + id);
        }

        // Regra de Negócio: Verifica se existem reservas CREATED ou CHECKED_IN
        List<ReservationStatus> activeStatuses = Arrays.asList(
            ReservationStatus.CREATED, 
            ReservationStatus.CHECKED_IN
        );

        if (reservationRepository.existsByGuestIdAndStatusIn(id, activeStatuses)) {
           throw new ResourceConflictException(
               "Hóspede não pode ser excluído pois possui reservas ativas (CREATED ou CHECKED_IN)."
           );
        }
        
        guestRepository.deleteById(id);
    }
}