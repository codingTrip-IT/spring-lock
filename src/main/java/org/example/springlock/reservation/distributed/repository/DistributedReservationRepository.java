package org.example.springlock.reservation.distributed.repository;

import org.example.springlock.reservation.distributed.entity.DistributedReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributedReservationRepository extends JpaRepository<DistributedReservation, Long> {
}