package org.example.springlock.none;

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
public class Counter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int count;

    public void addCount(int count) {
        this.count = count;
    }
}
