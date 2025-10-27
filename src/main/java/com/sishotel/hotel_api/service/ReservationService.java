package com.sishotel.hotel_api.service;

import com.sishotel.hotel_api.dto.ReservationRequestDTO;
import com.sishotel.hotel_api.dto.ReservationResponseDTO;
import com.sishotel.hotel_api.entity.Guest;
import com.sishotel.hotel_api.entity.Reservation;
import com.sishotel.hotel_api.entity.Room;
import com.sishotel.hotel_api.entity.enums.ReservationStatus;
import com.sishotel.hotel_api.repository.GuestRepository;
import com.sishotel.hotel_api.repository.ReservationRepository;
import com.sishotel.hotel_api.repository.RoomRepository;
import com.sishotel.hotel_api.service.exception.CapacityExceededException;
import com.sishotel.hotel_api.service.exception.InvalidCheckinDateException;
import com.sishotel.hotel_api.service.exception.InvalidDateRangeException;
import com.sishotel.hotel_api.service.exception.InvalidReservationStateException;
import com.sishotel.hotel_api.service.exception.ResourceConflictException;
import com.sishotel.hotel_api.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Cria uma nova reserva, validando Regras 1, 2, 3 e 6.
    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO dto) {
        
        // Busca as entidades (Guest e Room).
        Guest guest = guestRepository.findById(dto.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Hóspede não encontrado com ID: " + dto.getGuestId()));
        
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado com ID: " + dto.getRoomId()));
        
        // Valida se o quarto está ATIVO.
        if (!"ATIVO".equalsIgnoreCase(room.getStatus())) {
            throw new ResourceConflictException("Quarto " + room.getNumber() + " está INATIVO e não pode ser reservado.");
        }

        // Valida Regras (data, capacidade, disponibilidade).
        validateReservationRules(room, dto.getCheckinExpected(), dto.getCheckoutExpected(), dto.getGuestCount());
        
        // Regra de Negócio: Calcula o valor estimado (Regra 6).
        BigDecimal estimatedAmount = calculateEstimatedAmount(
                room.getPricePerNight(), 
                dto.getCheckinExpected(), 
                dto.getCheckoutExpected()
        );

        // Cria a entidade Reserva.
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setStatus(ReservationStatus.CREATED); // Regra de Negócio: Estado inicial (Regra 4)
        
        reservation.setCheckinExpected(dto.getCheckinExpected());
        reservation.setCheckoutExpected(dto.getCheckoutExpected());
        reservation.setEstimatedAmount(estimatedAmount);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        return new ReservationResponseDTO(savedReservation);
    }
    
    // Lista todas as reservas.
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> findAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::loadLazyAndMapToDTO)
                .collect(Collectors.toList());
    }

    // Busca uma reserva por ID.
    @Transactional(readOnly = true)
    public ReservationResponseDTO findReservationById(String id) {
        Reservation reservation = findReservationByIdInternal(id);
        return loadLazyAndMapToDTO(reservation);
    }
    
    // Regra de Negócio 4 e 5: Realiza o check-in (Fluxo 3).
    @Transactional
    public ReservationResponseDTO performCheckIn(String id) {
        Reservation reservation = findReservationByIdInternal(id);

        // Regra de Negócio: Só pode fazer check-in se estiver CREATED (Regra 4 - FSM).
        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Check-in não permitido. Status da reserva: " + reservation.getStatus()
            );
        }

        // Regra de Negócio: Check-in apenas no dia esperado (Regra 5).
        LocalDate today = LocalDate.now();
        if (!today.equals(reservation.getCheckinExpected())) {
            throw new InvalidCheckinDateException(
                    "Check-in só é permitido na data esperada (" + reservation.getCheckinExpected() + ")"
            );
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckinAt(Instant.now());
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        return loadLazyAndMapToDTO(updatedReservation);
    }

    // Regra de Negócio 4 e 6: Realiza o check-out (Fluxo 4).
    @Transactional
    public ReservationResponseDTO performCheckOut(String id) {
        Reservation reservation = findReservationByIdInternal(id);

        // Regra de Negócio: Só pode fazer check-out se estiver CHECKED_IN (Regra 4 - FSM).
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStateException(
                    "Check-out não permitido. Status da reserva: " + reservation.getStatus()
            );
        }

        Instant checkinEfetivo = reservation.getCheckinAt();
        Instant checkoutEfetivo = Instant.now();

        // Regra de Negócio: Calcula o valor final (Regra 6).
        BigDecimal finalAmount = calculateFinalAmount(
                reservation.getRoom().getPricePerNight(),
                checkinEfetivo,
                checkoutEfetivo
        );

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckoutAt(checkoutEfetivo);
        reservation.setFinalAmount(finalAmount);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        return loadLazyAndMapToDTO(updatedReservation);
    }

    // Regra de Negócio 4: Cancela uma reserva (Fluxo 5).
    @Transactional
    public void cancelReservation(String id) {
        Reservation reservation = findReservationByIdInternal(id);

        // Regra de Negócio: Só pode cancelar se estiver CREATED (Regra 4 - FSM).
        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Cancelamento não permitido. Status da reserva: " + reservation.getStatus()
            );
        }

        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }


    // --- Métodos Privados de Suporte ---

    // Valida Regras 1, 2 e 3 (data, capacidade, disponibilidade).
    private void validateReservationRules(Room room, LocalDate checkin, LocalDate checkout, int guestCount) {
        // Regra de Negócio: Datas Válidas (Regra 1).
        if (!checkout.isAfter(checkin)) {
            throw new InvalidDateRangeException("A data de check-out deve ser posterior à data de check-in.");
        }

        // Regra de Negócio: Capacidade (Regra 3).
        if (guestCount > room.getCapacity()) {
            throw new CapacityExceededException(
                    "O número de hóspedes (" + guestCount + 
                    ") excede a capacidade do quarto (" + room.getCapacity() + ")."
            );
        }

        // Regra de Negócio: Disponibilidade (Regra 2).
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                room.getId(),
                checkin,
                checkout,
                ReservationStatus.CANCELED
        );

        if (!overlapping.isEmpty()) {
            throw new ResourceConflictException("O quarto " + room.getNumber() + " não está disponível para o período solicitado.");
        }
    }
    
    // Regra de Negócio: Calcula o valor estimado (Regra 6).
    private BigDecimal calculateEstimatedAmount(BigDecimal pricePerNight, LocalDate checkin, LocalDate checkout) {
        long numberOfNights = ChronoUnit.DAYS.between(checkin, checkout);
        
        // Regra: min(1)
        if (numberOfNights == 0) {
             numberOfNights = 1;
        }

        return pricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
    }
    
    // Regra de Negócio: Calcula o valor final (Regra 6).
    private BigDecimal calculateFinalAmount(BigDecimal pricePerNight, Instant checkinEfetivo, Instant checkoutEfetivo) {
        // Regra: max(1, diasEntre(checkin, checkout))
        long totalHoras = ChronoUnit.HOURS.between(checkinEfetivo, checkoutEfetivo);
        
        // Se 0 horas ou menos, cobra 1 diária.
        if (totalHoras <= 0) {
            return pricePerNight;
        }
        
        // Converte horas para diárias, arredondando para CIMA.
        long numberOfNights = (totalHoras + 23) / 24;
        
        // Garante o mínimo de 1 diária.
        numberOfNights = Math.max(1, numberOfNights); 

        return pricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
    }

    // Método utilitário para carregar dados LAZY (Guest/Room) e mapear para DTO.
    private ReservationResponseDTO loadLazyAndMapToDTO(Reservation reservation) {
        reservation.getGuest().getFullName(); 
        reservation.getRoom().getNumber();
        return new ReservationResponseDTO(reservation);
    }
    
    // Método utilitário para buscar a reserva ou lança 404.
    private Reservation findReservationByIdInternal(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
    }
}