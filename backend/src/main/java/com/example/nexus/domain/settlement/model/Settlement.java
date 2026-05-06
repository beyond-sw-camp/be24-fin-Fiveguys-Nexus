package com.example.nexus.domain.settlement.model;

import com.example.nexus.domain.orders.model.Orders;
import com.example.nexus.domain.store.model.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settlement")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_idx")
    private Long idx;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "pg_payment_id")
    private String pgPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_idx", nullable = false)
    private Orders orders;
}
