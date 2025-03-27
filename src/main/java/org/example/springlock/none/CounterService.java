package org.example.springlock.none;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;

    @Transactional
    public Counter saveCounter(Counter counter) {
        return counterRepository.save(counter);
    }

    @Transactional
    public void incrementCount(Long counterId) {
        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
        counter.addCount(counter.getCount() + 1);
        counterRepository.save(counter);
    }

    @Transactional(readOnly = true)
    public Counter getCounterById(Long counterId) {
        return counterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
    }
}
