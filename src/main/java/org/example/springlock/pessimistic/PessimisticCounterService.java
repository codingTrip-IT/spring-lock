package org.example.springlock.pessimistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessimisticCounterService {

    private final PessimisticCounterRepository pessimisticCounterRepository;

    @Transactional
    public PessimisticCounter saveCounter(PessimisticCounter counter) {
        return pessimisticCounterRepository.save(counter); // Counter 엔티티 저장
    }

    @Transactional
    public void lockCounterAndHold(Long counterId) throws InterruptedException {
        PessimisticCounter counter = pessimisticCounterRepository.findByIdWithPessimisticLock(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
        System.out.println("락을 획득했습니다. 5초 동안 유지합니다...");
        Thread.sleep(5000);
        counter.addCount(counter.getCount() + 1);
        pessimisticCounterRepository.save(counter);
    }

    @Transactional
    public void incrementCountWithPessimisticLock(Long counterId) {
        PessimisticCounter counter = pessimisticCounterRepository.findByIdWithPessimisticLock(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
        counter.addCount(counter.getCount() + 1);
        pessimisticCounterRepository.save(counter);
    }

    @Transactional(readOnly = true)
    public PessimisticCounter getCounterById(Long counterId) {
        return pessimisticCounterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));
    }
}
