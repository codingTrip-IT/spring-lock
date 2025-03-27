package org.example.springlock.reservation.optimistic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptimisticReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int availableSpots;

    @Version // 낙관적 락을 위한 버전 관리
    private int version;

    public OptimisticReservation(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    public void decreaseAvailableSpots() {
        if (availableSpots > 0) {
            availableSpots--;
        } else {
            throw new RuntimeException("예약이 마감되었습니다");
        }
    }
}