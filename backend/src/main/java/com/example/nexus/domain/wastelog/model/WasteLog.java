package com.example.nexus.domain.wastelog.model;

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

import java.time.LocalDateTime;

@Entity
@Table(name = "waste_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WasteLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waste_log_idx")
    private Long idx;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "amount_loss", nullable = false)
    private Long amountLoss;

    @Column(name = "waste_date", nullable = false)
    private LocalDateTime wasteDate;

    @Column(name = "waste_reason", nullable = false)
    private String wasteReason;

    @Column(name = "store_idx", nullable = false)
    private Long storeIdx;

    @Column(name = "product_idx", nullable = false)
    private Long productIdx;
}
