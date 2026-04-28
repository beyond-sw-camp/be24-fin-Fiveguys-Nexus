package com.example.nexus.domain.orders.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders_item")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_item_idx")
    private Long idx;

    @Column(name = "count", nullable = false)
    private Integer count;
}
