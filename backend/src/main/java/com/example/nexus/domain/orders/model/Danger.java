package com.example.nexus.domain.orders.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "danger")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Danger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "danger_idx")
    private Long idx;

    @Column(name = "ratio", nullable = false)
    private Integer ratio;

    @Column(name = "period", nullable = false)
    private Integer period;

}
