package org.example.springlock.distributed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DistributedCounterProvider {

    private final DistributedCounterRepository distributedCounterRepository;

    @Transactional
    public void save(DistributedCounter counter) {
        distributedCounterRepository.save(counter);
    }

    @Transactional
    public void incrementCounter(Long counterId) {
        DistributedCounter counter = distributedCounterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));

        counter.setCount(counter.getCount() + 1);
        distributedCounterRepository.save(counter);
    }

    public DistributedCounter getCounter(Long counterId) {
        return distributedCounterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
    }
}
