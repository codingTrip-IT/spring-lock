package org.example.springlock.optimistic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptimisticCounter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int count;
    @Version
    private int version;

    public void addCount(int count) {
        this.count = count;
    }
}
