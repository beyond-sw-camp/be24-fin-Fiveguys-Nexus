package com.example.nexus.domain.orders.model;

import com.example.nexus.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "orders_item")
@Builder
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_idx", nullable = false)
    private Orders orders;

    @BatchSize(size = 100)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx", nullable = false)
    private Product product;

    public void updateCount(Integer count) {
        this.count = count;
    }
}
