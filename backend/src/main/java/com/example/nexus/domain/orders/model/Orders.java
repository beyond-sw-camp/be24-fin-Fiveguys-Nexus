package com.example.nexus.domain.orders.model;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.domain.store.model.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_idx")
    private Long idx;

    @Column(name = "price", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrdersType ordersType;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrdersStatus ordersStatus;

    @Column(name = "is_danger", nullable = false)
    private Boolean isDanger;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Store> storeList;

    @OneToMany(mappedBy = "orders_item", fetch = FetchType.LAZY)
    private List<OrdersItem> ordersItemList;
}
