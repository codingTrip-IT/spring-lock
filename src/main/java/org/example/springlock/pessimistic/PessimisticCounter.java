package org.example.springlock.pessimistic;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PessimisticCounter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int count;
}
