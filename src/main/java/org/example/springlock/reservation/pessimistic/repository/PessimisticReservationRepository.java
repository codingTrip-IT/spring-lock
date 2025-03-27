package org.example.springlock.reservation.pessimistic.repository;

import jakarta.persistence.LockModeType;
import org.example.springlock.pessimistic.PessimisticCounter;
import org.example.springlock.reservation.pessimistic.entity.PessimisticReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PessimisticReservationRepository extends JpaRepository<PessimisticReservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from PessimisticReservation c where c.id = :id")
    Optional<PessimisticReservation> findByIdWithPessimisticLock(Long id);
}