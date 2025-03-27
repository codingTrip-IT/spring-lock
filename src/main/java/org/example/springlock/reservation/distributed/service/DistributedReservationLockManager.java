package org.example.springlock.reservation.distributed.service;

public interface DistributedReservationLockManager {
    void executeWithLock(Long key, Runnable task) throws InterruptedException;
}
