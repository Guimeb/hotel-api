package com.sishotel.hotel_api.repository;

import com.sishotel.hotel_api.entity.Reservation;
import com.sishotel.hotel_api.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND r.status != :statusCanceled " +
           "AND r.checkinExpected < :newCheckout " +
           "AND r.checkoutExpected > :newCheckin")
    List<Reservation> findOverlappingReservations(
            String roomId,
            LocalDate newCheckin,
            LocalDate newCheckout,
            ReservationStatus statusCanceled
    );

    boolean existsByRoomIdAndStatusIn(String roomId, List<ReservationStatus> statuses);
    
    boolean existsByGuestIdAndStatusIn(String guestId, List<ReservationStatus> statuses);
}