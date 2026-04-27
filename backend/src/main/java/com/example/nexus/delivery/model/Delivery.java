package com.example.nexus.delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_idx", nullable = false)
    private Long idx;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "delay_reason", nullable = true)
    private String delayReason;

    @Column(name = "dep_date", nullable = false)
    private LocalDateTime departureDate;

    @Column(name = "est_des_date", nullable = false)
    private LocalDateTime estimatedArrivalAt;

    @Column(name = "deliveryed_date", nullable = false)
    private LocalDateTime deliveryedDate;
}
