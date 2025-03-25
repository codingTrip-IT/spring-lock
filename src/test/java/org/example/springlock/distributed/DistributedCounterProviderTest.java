package org.example.springlock.distributed;

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
public class DistributedCounterProviderTest {

    @Autowired
    private DistributedCounterProvider distributedCounterProvider;

    @Autowired
    private DistributedCounterService distributedCounterService;

    private Long counterId;

    @BeforeEach
    public void setup() {
        DistributedCounter counter = new DistributedCounter();
        counter.setCount(0);
        distributedCounterProvider.save(counter);
        counterId = counter.getId();
    }

    @Test
    public void testDistributedLock() throws InterruptedException {
        int testCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCount);
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    distributedCounterService.incrementCounterWithDistributedLock(counterId);
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

        DistributedCounter finalCounter = distributedCounterProvider.getCounter(counterId);
        System.out.println("성공한 업데이트 수: " + successfulUpdates.get());
        System.out.println("최종 count: " + finalCounter.getCount());

        assertEquals(successfulUpdates.get(), finalCounter.getCount());
    }
}
