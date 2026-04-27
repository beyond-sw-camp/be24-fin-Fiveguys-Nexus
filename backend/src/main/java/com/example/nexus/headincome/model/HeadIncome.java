package com.example.nexus.headincome.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "head_income")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HeadIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "head_income_idx")
    private Long idx;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "status", nullable = false)
    private Boolean status;

}
