package com.sishotel.hotel_api.repository;

import com.sishotel.hotel_api.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, String> {
    
    Optional<Guest> findByDocument(String document);
    Optional<Guest> findByEmail(String email);
}