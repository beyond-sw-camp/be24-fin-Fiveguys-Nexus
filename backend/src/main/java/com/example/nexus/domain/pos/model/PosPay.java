package com.example.nexus.domain.pos.model;

import com.example.nexus.common.enums.PosPayMethod;
import com.example.nexus.domain.store.model.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "posPay", fetch = FetchType.LAZY)
    private List<PosOrdersItem> posOrdersItems = new ArrayList<>();
}
