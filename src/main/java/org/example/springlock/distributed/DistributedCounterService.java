package org.example.springlock.distributed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedCounterService {

    private final DistributedCounterProvider distributedCounterProvider;
    private final DistributedLockManager distributedLockManager;

    public void incrementCounterWithDistributedLock(Long counterId) {
        try {
            distributedLockManager.executeWithLock(counterId, () ->
                distributedCounterProvider.incrementCounter(counterId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트 발생: " + counterId, e);
        }
    }
}
