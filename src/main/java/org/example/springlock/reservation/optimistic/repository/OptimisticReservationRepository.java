package org.example.springlock.reservation.optimistic.repository;

import org.example.springlock.reservation.optimistic.entity.OptimisticReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimisticReservationRepository extends JpaRepository<OptimisticReservation, Long> {
}