package org.example.springlock.reservation.pessimistic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PessimisticReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int availableSpots;

    public PessimisticReservation(int availableSpots) {
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