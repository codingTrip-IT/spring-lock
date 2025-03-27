package org.example.springlock.reservation.optimistic.service;

import lombok.RequiredArgsConstructor;
import org.example.springlock.reservation.optimistic.entity.OptimisticReservation;
import org.example.springlock.reservation.optimistic.repository.OptimisticReservationRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticReservationService {

    private final OptimisticReservationRepository optimisticReservationRepository;

    @Transactional
    public OptimisticReservation saveReservation(OptimisticReservation optimisticReservation) {
        return optimisticReservationRepository.save(optimisticReservation);
    }

    @Transactional
    public void decrementAvailableSpots(Long reservationId) {
        for (int i = 0; i < 3; i++) {
            try {
                OptimisticReservation optimisticReservation = optimisticReservationRepository.findById(reservationId)
                        .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
                optimisticReservation.decreaseAvailableSpots();
                optimisticReservationRepository.save(optimisticReservation);
                return;
            } catch (OptimisticLockingFailureException e) {
                if (i == 2) {
                    throw e;
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public OptimisticReservation getReservationById(Long reservationId) {
        return optimisticReservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
    }
}
