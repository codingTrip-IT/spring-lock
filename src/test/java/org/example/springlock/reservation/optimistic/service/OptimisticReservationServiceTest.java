package org.example.springlock.reservation.optimistic.service;

import org.example.springlock.reservation.optimistic.entity.OptimisticReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OptimisticReservationServiceTest {

    @Autowired
    private OptimisticReservationService optimisticReservationService;

    private Long optimisticReservationId;

    @BeforeEach
    public void setup() {
        OptimisticReservation optimisticReservation = new OptimisticReservation(100);
        OptimisticReservation saveOptimisticReservation = optimisticReservationService.saveReservation(optimisticReservation);
        optimisticReservationId = saveOptimisticReservation.getId();
    }

    @Test
    public void testOptimisticLock() throws InterruptedException {
        int testCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCount);

        // 예외 발생 횟수를 추적하기 위한 변수
        AtomicInteger optimisticLockExceptionCount = new AtomicInteger(0);
        AtomicInteger successfulReservations = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticReservationService.decrementAvailableSpots(optimisticReservationId);
                    successfulReservations.incrementAndGet(); //예약 성공 횟수 증가
                } catch (OptimisticLockingFailureException e) {
                    // 낙관적 락 충돌 발생 시 처리 및 예외 카운트 증가
                    optimisticLockExceptionCount.incrementAndGet();
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

        assertTrue(optimisticLockExceptionCount.get() > 0);

        // 예약 가능한 인원은 정확히 0이어야 함
        int finalCount = optimisticReservationService.getReservationById(optimisticReservationId).getAvailableSpots();
        assertEquals(0, finalCount);

        System.out.println("발생한 예외 수: " + optimisticLockExceptionCount.get());
        System.out.println("성공한 예약 수: " + successfulReservations.get());
        System.out.println("남은 예약 가능인원: " + finalCount);
        System.out.println("테스트 실행 시간: " + durationInSeconds + "초");
    }

}
