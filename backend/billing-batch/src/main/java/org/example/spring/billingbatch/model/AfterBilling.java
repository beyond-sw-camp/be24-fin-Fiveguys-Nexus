package org.example.spring.billingbatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Entity
public class AfterBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Long storeIdx;

    private Integer totalPayAmount;

    private Integer payedMonth;

    private Boolean isPaid;


}
