package com.example.nexus.orderitem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_idx")
    private Long idx;

    @Column(name = "count", nullable = false)
    private Integer count;
}
