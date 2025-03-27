package org.example.springlock.pessimistic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PessimisticCounterServiceTest {

    @Autowired
    private PessimisticCounterService pessimisticCounterService;

    private Long pessimisticCounterId;

    @BeforeEach
    public void setup() {
        PessimisticCounter counter = new PessimisticCounter();
        counter.addCount(0);
        PessimisticCounter savedCounter = pessimisticCounterService.saveCounter(counter);
        pessimisticCounterId = savedCounter.getId();
        System.out.println("counterId: " + pessimisticCounterId);
    }

    @Test
    public void testPessimisticLock() throws InterruptedException {
        int testCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCount);

        AtomicInteger successfulUpdates = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticCounterService.incrementCountWithPessimisticLock(pessimisticCounterId);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("비관적 락 충돌: " + e.getMessage());
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

        int finalCount = pessimisticCounterService.getCounterById(pessimisticCounterId).getCount();
        System.out.println("최종 count: " + finalCount);
        System.out.println("성공한 업데이트 수: " + successfulUpdates.get());
        System.out.println("테스트 실행 시간: " + durationInSeconds + "초");

        assertEquals(successfulUpdates.get(), finalCount);
    }

    @Test
    public void testLockTimeoutException() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executorService.submit(() -> {
            try {
                pessimisticCounterService.lockCounterAndHold(pessimisticCounterId);
            } catch (InterruptedException e) {
                System.out.println("락 유지 중 예외 발생1: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                Thread.sleep(1000); // 1초 후 시도
                pessimisticCounterService.incrementCountWithPessimisticLock(pessimisticCounterId);
            } catch (Exception e) {
                System.out.println("발생한 예외: " + e.getClass().getName() + " - " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("원인 예외: " + e.getCause().getClass().getName() + " - " + e.getCause().getMessage());
                }
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();
    }
}

