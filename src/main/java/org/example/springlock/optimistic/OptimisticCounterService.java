package org.example.springlock.optimistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticCounterService {

    private final OptimisticCounterRepository optimisticCounterRepository;

    @Transactional
    public OptimisticCounter saveCounter(OptimisticCounter optimisticCounter) {
        return optimisticCounterRepository.save(optimisticCounter);
    }

    @Transactional
    public void incrementCount(Long counterId) {
        OptimisticCounter optimisticCounter = optimisticCounterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
        optimisticCounter.addCount(optimisticCounter.getCount() + 1);
        optimisticCounterRepository.save(optimisticCounter);
    }

    @Transactional(readOnly = true)
    public OptimisticCounter getCounterById(Long counterId) {
        return optimisticCounterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
    }
}
