package org.example.springlock.reservation.distributed.service;

import org.example.springlock.reservation.distributed.entity.DistributedReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("redis")
class DistributedReservationProviderTest {

    @Autowired
    private DistributedReservationProvider distributedReservationProvider;

    @Autowired
    private DistributedReservationService distributedReservationService;

    private Long reservationId;

    @BeforeEach
    public void setup() {
        DistributedReservation distributedReservation = new DistributedReservation(100);
        distributedReservationProvider.save(distributedReservation);
        reservationId = distributedReservation.getId();
    }

    @Test
    public void testDistributedLock() throws InterruptedException {
        int testCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCount);
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    distributedReservationService.decrementAvailableSpotsWithDistributedLock(reservationId);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("락 충돌: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long durationInMillis = endTime - startTime;
        double durationInSeconds = durationInMillis / 1000.0;

        DistributedReservation finalCounter = distributedReservationProvider.getReservationById(reservationId);
        System.out.println("성공한 예약 수: " + successfulUpdates.get());
        System.out.println("남은 예약 가능인원: " + finalCounter.getAvailableSpots());
        System.out.println("테스트 실행 시간: " + durationInSeconds + "초");

        assertEquals(0, finalCounter.getAvailableSpots());
    }
}