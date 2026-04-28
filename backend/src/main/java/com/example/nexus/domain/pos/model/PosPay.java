package com.example.nexus.domain.pos.model;

import com.example.nexus.common.enums.PosPayMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "pos_pay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosPay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_pay_idx")
    private Long idx;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PosPayMethod method;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "pay_amount", nullable = false)
    private Long payAmount;

    @Column(name = "store_idx", nullable = false)
    private Long storeIdx;
}
