package org.example.springlock.reservation.distributed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedReservationService {

    private final DistributedReservationProvider distributedReservationProvider;
    private final DistributedReservationLockManager distributedReservationLockManager;

    public void decrementAvailableSpotsWithDistributedLock(Long reservationId) {
        try {
            distributedReservationLockManager.executeWithLock(reservationId, () ->
                    distributedReservationProvider.decrementAvailableSpots(reservationId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트 발생: " + reservationId, e);
        }
    }
}