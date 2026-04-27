package com.example.nexus.posorderitem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_order_item_idx", nullable = false)
    private Long idx;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "menu_idx", nullable = false)
    private Long menuIdx;

    @Column(name = "pos_pay_idx", nullable = false)
    private Long posPayIdx;
}
